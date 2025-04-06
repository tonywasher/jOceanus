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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoanInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseRegion.MoneyWiseRegionList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfoSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

import java.util.Currency;
import java.util.Iterator;

/**
 * Validate SecurityInfoSet.
 */
public class MoneyWiseValidateSecurityInfoSet
        extends PrometheusValidateInfoSet<MoneyWiseSecurityInfo> {
    /**
     * New Symbol name.
     */
    private static final String NAME_NEWSYMBOL = "SYMBOL";

    @Override
    public MoneyWiseSecurity getOwner() {
        return (MoneyWiseSecurity) super.getOwner();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Security */
        final MoneyWiseSecurity mySec = getOwner();
        final MoneyWiseSecurityClass myType = mySec.getCategoryClass();

        /* If we have no Type, no class is allowed */
        if (myType == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
                return MetisFieldRequired.CANEXIST;

            /* Symbol */
            case SYMBOL:
                return myType.needsSymbol()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Region */
            case REGION:
                return myType.needsRegion()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Options */
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
                return myType.isOption()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Not Allowed */
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            case OPENINGBALANCE:
            case AUTOEXPENSE:
            case AUTOPAYEE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    @Override
    public void validateClass(final MoneyWiseSecurityInfo pInfo,
                              final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case NOTES:
                validateNotes(pInfo);
                break;
            case SYMBOL:
                validateSymbol(pInfo);
                break;
            case UNDERLYINGSTOCK:
                validateUnderlyingStock(pInfo);
                break;
            case OPTIONPRICE:
                validateOptionPrice(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the Notes info.
     * @param pInfo the info
     */
    private void validateNotes(final MoneyWiseSecurityInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        if (myArray.length > MoneyWiseAccountInfoClass.NOTES.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.NOTES));
        }
    }

    /**
     * Validate the Symbol info.
     * @param pInfo the info
     */
    private void validateSymbol(final MoneyWiseSecurityInfo pInfo) {
        final String mySymbol = pInfo.getValue(String.class);
        if (mySymbol.length() > MoneyWiseAccountInfoClass.SYMBOL.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.SYMBOL));
        }
    }

    /**
     * Validate the UnderlyingStock info.
     * @param pInfo the info
     */
    private void validateUnderlyingStock(final MoneyWiseSecurityInfo pInfo) {
        final MoneyWiseSecurity myStock = pInfo.getValue(MoneyWiseSecurity.class);
        if (!myStock.getCategoryClass().isShares()) {
            getOwner().addError("Invalid underlying stock", MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK));
        }
        if (!myStock.getCurrency().equals(getOwner().getCurrency())) {
            getOwner().addError(MoneyWiseDepositInfoSet.ERROR_CURRENCY, MoneyWiseLoanInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK));
        }
    }

    /**
     * Validate the OptionPrice info.
     * @param pInfo the info
     */
    private void validateOptionPrice(final MoneyWiseSecurityInfo pInfo) {
        final OceanusPrice myPrice = pInfo.getValue(OceanusPrice.class);
        if (myPrice.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        } else if (!myPrice.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseSecurityInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        }
        if (!myPrice.getCurrency().equals(getOwner().getCurrency())) {
            getOwner().addError(MoneyWiseDepositInfoSet.ERROR_CURRENCY, MoneyWiseLoanInfoSet.getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        }
    }

    @Override
    protected void setDefault(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case SYMBOL:
                getInfoSet().setValue(pClass, getUniqueSymbol());
                break;
            case REGION:
                getInfoSet().setValue(pClass, getDefaultRegion());
                break;
            case UNDERLYINGSTOCK:
                getInfoSet().setValue(pClass, getDefaultUnderlyingStock());
                break;
            case OPTIONPRICE:
                getInfoSet().setValue(pClass, getDefaultOptionPrice());
                break;
            default:
                break;
        }
    }

    /**
     * Obtain unique symbol for new tag.
     * @return The new symbol
     */
    private String getUniqueSymbol() {
        /* Access the security list */
        final MoneyWiseSecurityList mySecurities = getOwner().getList();

        /* Set up base constraints */
        final String myBase = NAME_NEWSYMBOL;
        int iNextId = 1;

        /* Loop until we found a symbol */
        String mySymbol = myBase;
        for (;;) {
            /* try out the symbol */
            if (mySecurities.findItemBySymbol(mySymbol) == null) {
                return mySymbol;
            }

            /* Build next symbol */
            mySymbol = myBase.concat(Integer.toString(iNextId++));
        }
    }

    /**
     * Obtain default region for security.
     * @return the default region
     */
    private MoneyWiseRegion getDefaultRegion() {
        /* Access the region list */
        final MoneyWiseRegionList myRegions
                = getEditSet().getDataList(MoneyWiseBasicDataType.REGION, MoneyWiseRegionList.class);

        /* loop through the regions */
        final Iterator<MoneyWiseRegion> myIterator = myRegions.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseRegion myRegion = myIterator.next();

            /* Return first non-deleted region */
            if (!myRegion.isDeleted()) {
                return myRegion;
            }
        }

        /* Return no region */
        return null;
    }

    /**
     * Obtain default underlying stock.
     * @return the default underlying stock
     */
    private MoneyWiseSecurity getDefaultUnderlyingStock() {
        /* Access the security list */
        final MoneyWiseSecurityList mySecurities = getOwner().getList();

        /* loop through the securities */
        final Iterator<MoneyWiseSecurity> myIterator = mySecurities.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurity mySecurity = myIterator.next();

            /* Ignore deleted securities */
            if (mySecurity.isDeleted()) {
                continue;
            }

            /* Ignore securities that are the wrong class */
            if (mySecurity.getCategoryClass().isShares()) {
                return mySecurity;
            }
        }

        /* Return no security */
        return null;
    }

    /**
     * Obtain default option price.
     * @return the default underlying stock
     */
    private OceanusPrice getDefaultOptionPrice() {
        /* Obtain the underlying stock */
        final MoneyWiseSecurity myUnderlying = getOwner().getUnderlyingStock();

        /* If there is no underlying stock, then there is no price */
        if (myUnderlying == null) {
            return null;
        }

        final MoneyWiseCurrency myCurrency = myUnderlying.getAssetCurrency();
        return OceanusPrice.getWholeUnits(1, myCurrency.getCurrency());
    }

    @Override
    protected void autoCorrect(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case UNDERLYINGSTOCK:
                autoCorrectUnderlyingStock();
                break;
            case OPTIONPRICE:
                autoCorrectOptionPrice();
                break;
            default:
                break;
        }
    }

    /**
     * AutoCorrect underlying stock.
     * @throws OceanusException on error
     */
    private void autoCorrectUnderlyingStock() throws OceanusException {
        /* Obtain the existing value */
        MoneyWiseSecurity myStock = getOwner().getUnderlyingStock();

        /* If the stock is not the correct currency */
        if (!myStock.getCurrency().equals(getOwner().getCurrency())) {
            /* Reset to new default */
            myStock = getDefaultUnderlyingStock();
            getInfoSet().setValue(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK, myStock);
        }
    }

    /**
     * AutoCorrect option price.
     * @throws OceanusException on error
     */
    private void autoCorrectOptionPrice() throws OceanusException {
        /* Obtain the existing value */
        OceanusPrice myPrice = getOwner().getOptionPrice();
        final MoneyWiseCurrency myAssetCurrency = getOwner().getAssetCurrency();
        final Currency myCurrency = myAssetCurrency.getCurrency();

        /* If the price is not the correct currency */
        if (!myPrice.getCurrency().equals(myCurrency)) {
            myPrice = myPrice.changeCurrency(myCurrency);
            getInfoSet().setValue(MoneyWiseAccountInfoClass.OPTIONPRICE, myPrice);
        }
    }
}
