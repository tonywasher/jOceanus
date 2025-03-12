/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

/**
 * Validate CashInfoSet.
 */
public class MoneyWiseValidateCashInfoSet
        extends PrometheusValidateInfoSet<MoneyWiseCashInfo> {
    @Override
    public MoneyWiseCash getOwner() {
        return (MoneyWiseCash) super.getOwner();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Cash */
        final MoneyWiseCash myCash = getOwner();
        final MoneyWiseCashCategory myCategory = myCash.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
                return MetisFieldRequired.CANEXIST;

            case OPENINGBALANCE:
                return myCash.isAutoExpense()
                        ? MetisFieldRequired.NOTALLOWED
                        : MetisFieldRequired.CANEXIST;
            case AUTOPAYEE:
            case AUTOEXPENSE:
                return myCash.isAutoExpense()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Disallowed Set */
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            case SYMBOL:
            case REGION:
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    @Override
    public void validateClass(final MoneyWiseCashInfo pInfo,
                              final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case OPENINGBALANCE:
                validateOpeningBalance(pInfo);
                break;
            case AUTOEXPENSE:
                validateAutoExpense(pInfo);
                break;
            case NOTES:
                validateNotes(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the opening balance.
     * @param pInfo the info
     */
    private void validateOpeningBalance(final MoneyWiseCashInfo pInfo) {
        final OceanusMoney myBalance = pInfo.getValue(OceanusMoney.class);
        if (!myBalance.getCurrency().equals(getOwner().getCurrency())) {
            getOwner().addError(MoneyWiseDepositInfoSet.ERROR_CURRENCY, MoneyWiseCashInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPENINGBALANCE));
        }
    }

    /**
     * Validate the autoExpense info.
     * @param pInfo the info
     */
    private void validateAutoExpense(final MoneyWiseCashInfo pInfo) {
        final MoneyWiseTransCategory myExpense = pInfo.getEventCategory();
        final MoneyWiseTransCategoryClass myCatClass = myExpense.getCategoryTypeClass();
        if (!myCatClass.isExpense() || myCatClass.canParentCategory()) {
            getOwner().addError(MoneyWiseCashInfoSet.ERROR_AUTOEXP, MoneyWiseCashInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.AUTOEXPENSE));
        }
    }

    /**
     * Validate the Notes info.
     * @param pInfo the info
     */
    private void validateNotes(final MoneyWiseCashInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        if (myArray.length > MoneyWiseAccountInfoClass.NOTES.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseCashInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.NOTES));
        }
    }
}
