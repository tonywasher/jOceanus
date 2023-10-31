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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Region;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Security Builder.
 */
public class MoneyWiseSecurityBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The SecurityName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private Payee theParent;

    /**
     * The SecurityType.
     */
    private SecurityType theType;

    /**
     * The Currency.
     */
    private AssetCurrency theCurrency;

    /**
     * The Symbol.
     */
    private String theSymbol;

    /**
     * The Region.
     */
    private Region theRegion;

    /**
     * The Underlying.
     */
    private Security theUnderlying;

    /**
     * The OptionPrice.
     */
    private TethysPrice theOptionPrice;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseSecurityBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        defaultCurrency();
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
    public MoneyWiseSecurityBuilder parent(final Payee pParent) {
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
    public MoneyWiseSecurityBuilder type(final SecurityType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the securityType.
     * @param pType the type of the security.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder type(final SecurityTypeClass pType) {
        return type(theDataSet.getSecurityTypes().findItemByClass(pType));
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the loan.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder currency(final AssetCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder currency(final AssetCurrencyClass pCurrency) {
        return currency(theDataSet.getAccountCurrencies().findItemByClass(pCurrency));
    }

    /**
     * Set the default currency.
     */
    private void defaultCurrency() {
        currency(lookupDefaultCurrency());
    }

    /**
     * Obtain the default currency.
     * @return the currency
     */
    private AssetCurrency lookupDefaultCurrency() {
        return theDataSet.getDefaultCurrency();
    }

    /**
     * Set Symbol.
     * @param pSymbol the symbol of the security.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder symbol(final String pSymbol) {
        theName = pSymbol;
        return this;
    }

    /**
     * Set Region.
     * @param pRegion the region.
     * @return the builder
     */
    public MoneyWiseSecurityBuilder region(final Region pRegion) {
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
    public MoneyWiseSecurityBuilder underlying(final Security pUnderlying) {
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
    public Security build() throws OceanusException {
        /* Create the security */
        final Security mySecurity = theDataSet.getSecurities().addNewItem();
        mySecurity.setName(theName);
        mySecurity.setParent(theParent);
        mySecurity.setCategory(theType);
        mySecurity.setAssetCurrency(theCurrency);
        mySecurity.setSymbol(theSymbol);
        mySecurity.setRegion(theRegion);
        mySecurity.setUnderlyingStock(theUnderlying);
        mySecurity.setOptionPrice(theOptionPrice);

        /* Check for errors */
        mySecurity.validate();
        if (mySecurity.hasErrors()) {
            theDataSet.getSecurities().remove(mySecurity);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Update maps to reflect the new object */
        mySecurity.adjustMapForItem();

        /* Reset values */
        theName = null;
        theType = null;
        theParent = null;
        theSymbol = null;
        theRegion = null;
        theUnderlying = null;
        theOptionPrice = null;
        defaultCurrency();

        /* Return the security */
        return mySecurity;
    }
}
