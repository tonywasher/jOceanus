/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanInfoSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

import java.util.Currency;

/**
 * Validate LoanInfoSet.
 */
public class MoneyWiseValidateLoanInfoSet
        extends PrometheusValidateInfoSet<MoneyWiseLoanInfo> {
    @Override
    public MoneyWiseLoan getOwner() {
        return (MoneyWiseLoan) super.getOwner();
    }

    @Override
    public MoneyWiseLoanInfoSet getInfoSet() {
        return (MoneyWiseLoanInfoSet) super.getInfoSet();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Loan */
        final MoneyWiseLoan myLoan = getOwner();
        final MoneyWiseLoanCategory myCategory = myLoan.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case OPENINGBALANCE:
                return MetisFieldRequired.CANEXIST;

            /* Not allowed */
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            case AUTOEXPENSE:
            case AUTOPAYEE:
            case SYMBOL:
            case REGION:
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    @Override
    public void validateClass(final MoneyWiseLoanInfo pInfo,
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
    private void validateOpeningBalance(final MoneyWiseLoanInfo pInfo) {
        final OceanusMoney myBalance = pInfo.getValue(OceanusMoney.class);
        if (!myBalance.getCurrency().equals(getOwner().getCurrency())) {
            getOwner().addError(MoneyWiseDepositInfoSet.ERROR_CURRENCY, MoneyWiseLoanInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPENINGBALANCE));
        }
    }

    /**
     * Validate the info length.
     * @param pInfo the info
     */
    private void validateInfoLength(final MoneyWiseLoanInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        final MoneyWiseAccountInfoClass myClass = pInfo.getInfoClass();
        if (myArray.length > myClass.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseLoanInfoSet.getFieldForClass(myClass));
        }
    }

    @Override
    protected void autoCorrect(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* If the info is Opening balance */
        if (MoneyWiseAccountInfoClass.OPENINGBALANCE.equals(pClass)) {
            /* Access the value */
            final MoneyWiseLoan myOwner = getOwner();
            OceanusMoney myOpening = myOwner.getOpeningBalance();
            final MoneyWiseCurrency myAssetCurrency = myOwner.getAssetCurrency();
            final Currency myCurrency = myAssetCurrency.getCurrency();

            /* If we need to change currency */
            if (!myCurrency.equals(myOpening.getCurrency())) {
                myOpening = myOpening.changeCurrency(myCurrency);
                getInfoSet().setValue(pClass, myOpening);
            }
        }
    }
}
