/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

import java.util.Currency;
import java.util.Iterator;

/**
 * Validate CashInfoSet.
 */
public class MoneyWiseValidateCashInfoSet
        extends PrometheusValidateInfoSet<MoneyWiseCashInfo> {
    /**
     * ClosedPayee Error string.
     */
    private static final String ERROR_PAYEECLOSED = "AutoPayee is closed for non-closed autoCash";

    @Override
    public MoneyWiseCash getOwner() {
        return (MoneyWiseCash) super.getOwner();
    }

    @Override
    public MoneyWiseCashInfoSet getInfoSet() {
        return (MoneyWiseCashInfoSet) super.getInfoSet();
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
            case AUTOPAYEE:
                validateAutoPayee(pInfo);
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
     *
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
     *
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
     * Validate the autoPayee info.
     *
     * @param pInfo the info
     */
    private void validateAutoPayee(final MoneyWiseCashInfo pInfo) {
        final MoneyWisePayee myPayee = pInfo.getPayee();
        if (myPayee.isClosed() && !getOwner().isClosed()) {
            getOwner().addError(ERROR_PAYEECLOSED, MoneyWiseCashInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.AUTOPAYEE));
        }
    }

    /**
     * Validate the Notes info.
     *
     * @param pInfo the info
     */
    private void validateNotes(final MoneyWiseCashInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        if (myArray.length > MoneyWiseAccountInfoClass.NOTES.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseCashInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.NOTES));
        }
    }

    @Override
    protected void setDefault(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case AUTOEXPENSE:
                getInfoSet().setValue(pClass, getDefaultAutoExpense());
                break;
            case AUTOPAYEE:
                getInfoSet().setValue(pClass, getDefaultAutoPayee());
                break;
            default:
                break;
        }
    }

    /**
     * Obtain default expense for autoExpense cash.
     *
     * @return the default expense
     */
    private MoneyWiseTransCategory getDefaultAutoExpense() {
        /* Access the category list */
        final MoneyWiseTransCategoryList myCategories
                = getEditSet().getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);

        /* loop through the categories */
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* Ignore categories that are the wrong class */
            final MoneyWiseTransCategoryClass myCatClass = myCategory.getCategoryTypeClass();
            if (myCatClass.isExpense() && !myCatClass.canParentCategory()) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default payee for autoExpense cash.
     *
     * @return the default payee
     */
    private MoneyWisePayee getDefaultAutoPayee() {
        /* Access the payee list */
        final MoneyWisePayeeList myPayees
                = getEditSet().getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* loop through the payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (!myPayee.isDeleted() && Boolean.TRUE.equals(!myPayee.isClosed())) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }

    @Override
    protected void autoCorrect(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* If the info is Opening balance */
        if (MoneyWiseAccountInfoClass.OPENINGBALANCE.equals(pClass)) {
            /* Access the value */
            final MoneyWiseCash myOwner = getOwner();
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
