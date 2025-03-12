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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfoSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseDepositCategoryClass;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

/**
 * Validate DepositInfoSet.
 */
public class MoneyWiseValidateDepositInfoSet
        extends PrometheusValidateInfoSet<MoneyWiseDepositInfo> {
    @Override
    public MoneyWiseDeposit getOwner() {
        return (MoneyWiseDeposit) super.getOwner();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Deposit */
        final MoneyWiseDeposit myDeposit = getOwner();
        final MoneyWiseDepositCategory myCategory = myDeposit.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        final MoneyWiseDepositCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case OPENINGBALANCE:
                return MetisFieldRequired.CANEXIST;

            /* Handle Maturity */
            case MATURITY:
                return myClass.hasMaturity()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Not allowed */
            case AUTOEXPENSE:
            case AUTOPAYEE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case SYMBOL:
            case REGION:
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    @Override
    public void validateClass(final MoneyWiseDepositInfo pInfo,
                              final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case OPENINGBALANCE:
                validateOpeningBalance(pInfo);
                break;
            case SORTCODE:
            case ACCOUNT:
            case NOTES:
            case REFERENCE:
                validateInfoLength(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the opening balance.
     * @param pInfo the info
     */
    private void validateOpeningBalance(final MoneyWiseDepositInfo pInfo) {
        final OceanusMoney myBalance = pInfo.getValue(OceanusMoney.class);
        if (!myBalance.getCurrency().equals(getOwner().getCurrency())) {
            getOwner().addError(MoneyWiseDepositInfoSet.ERROR_CURRENCY, MoneyWiseDepositInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPENINGBALANCE));
        }
    }

    /**
     * Validate the info length.
     * @param pInfo the info
     */
    private void validateInfoLength(final MoneyWiseDepositInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        final MoneyWiseAccountInfoClass myClass = pInfo.getInfoClass();
        if (myArray.length > myClass.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseDepositInfoSet.getFieldForClass(myClass));
        }
    }
}
