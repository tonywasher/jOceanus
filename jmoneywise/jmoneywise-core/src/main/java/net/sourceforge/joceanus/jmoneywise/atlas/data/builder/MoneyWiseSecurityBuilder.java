/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.builder;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseRegion;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseSecurityType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Security Builder.
 */
public class MoneyWiseSecurityBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The SecurityName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private MoneyWisePayee theParent;

    /**
     * The SecurityType.
     */
    private MoneyWiseSecurityType theType;

    /**
     * The Currency.
     */
    private MoneyWiseCurrency theCurrency;

    /**
     * The Symbol.
     */
    private String theSymbol;

    /**
     * The Region.
     */
    private MoneyWiseRegion theRegion;

    /**
     * The Underlying.
     */
    private MoneyWiseSecurity theUnderlying;

    /**
     * The OptionPrice.
     */
    private TethysPrice theOptionPrice;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseSecurityBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getSecurities().ensureMap();
        reportingCurrency();
    }

    /**
     * Set Name.
     * @param pName the name of the loan.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder parent(final MoneyWisePayee pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder parent(final String pParent) {
        return parent(theDataSet.getPayees().findItemByName(pParent));
    }

    /**
     * Set the securityType.
     * @param pType the type of the security.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder type(final MoneyWiseSecurityType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the securityType.
     * @param pType the type of the security.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder type(final MoneyWiseSecurityClass pType) {
        return type(theDataSet.getSecurityTypes().findItemByClass(pType));
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the loan.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
        return currency(theDataSet.getAccountCurrencies().findItemByClass(pCurrency));
    }

    /**
     * Set the reporting currency.
     */
    private void reportingCurrency() {
        currency(lookupReportingCurrency());
    }

    /**
     * Obtain the reporting currency.
     * @return the currency
     */
    private MoneyWiseCurrency lookupReportingCurrency() {
        return theDataSet.getReportingCurrency();
    }

    /**
     * Set Symbol.
     * @param pSymbol the symbol of the security.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder symbol(final String pSymbol) {
        theSymbol = pSymbol;
        return this;
    }

    /**
     * Set Region.
     * @param pRegion the region.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder region(final MoneyWiseRegion pRegion) {
        theRegion = pRegion;
        return this;
    }

    /**
     * Set Region.
     * @param pRegion the region.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder region(final String pRegion) {
        return region(theDataSet.getRegions().findItemByName(pRegion));
    }

    /**
     * Set Underlying.
     * @param pUnderlying the underlying.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder underlying(final MoneyWiseSecurity pUnderlying) {
        theUnderlying = pUnderlying;
        return this;
    }

    /**
     * Set optionPrice.
     * @param pPrice the optionPrice of the security.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder optionPrice(final TethysPrice pPrice) {
        theOptionPrice = pPrice;
        return this;
    }

    /**
     * Build the deposit.
     * @return the new Deposit
     * @throws OceanusException on error
     */
    public MoneyWiseSecurity build() throws OceanusException {
        /* Create the security */
        final MoneyWiseSecurity mySecurity = theDataSet.getSecurities().addNewItem();
        mySecurity.setName(theName);
        mySecurity.setParent(theParent);
        mySecurity.setCategory(theType);
        mySecurity.setAssetCurrency(theCurrency);
        mySecurity.setSymbol(theSymbol);
        mySecurity.setRegion(theRegion);
        mySecurity.setUnderlyingStock(theUnderlying);
        mySecurity.setOptionPrice(theOptionPrice);
        mySecurity.setClosed(Boolean.FALSE);

        /* Check for errors */
        mySecurity.adjustMapForItem();
        mySecurity.validate();
        if (mySecurity.hasErrors()) {
            theDataSet.getSecurities().remove(mySecurity);
            throw new MoneyWiseDataException(mySecurity, "Failed validation");
        }

        /* Reset values */
        theName = null;
        theType = null;
        theParent = null;
        theSymbol = null;
        theRegion = null;
        theUnderlying = null;
        theOptionPrice = null;
        reportingCurrency();

        /* Return the security */
        return mySecurity;
    }
}
