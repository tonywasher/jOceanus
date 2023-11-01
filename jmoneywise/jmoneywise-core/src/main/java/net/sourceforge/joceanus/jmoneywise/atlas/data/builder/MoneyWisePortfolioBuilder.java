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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioTypeClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Portfolio Builder.
 */
public class MoneyWisePortfolioBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The PortfolioName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private Payee theParent;

    /**
     * The PortfolioType.
     */
    private PortfolioType theType;

    /**
     * The Currency.
     */
    private AssetCurrency theCurrency;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWisePortfolioBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getPortfolios().ensureMap();
        defaultCurrency();
    }

    /**
     * Set Name.
     * @param pName the name of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder parent(final Payee pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder parent(final String pParent) {
        return parent(theDataSet.getPayees().findItemByName(pParent));
    }

    /**
     * Set the portfolioType.
     * @param pType the type of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder type(final PortfolioType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the portfolioType.
     * @param pType the type of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder type(final PortfolioTypeClass pType) {
        return type(theDataSet.getPortfolioTypes().findItemByClass(pType));
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the loan.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder currency(final AssetCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder currency(final AssetCurrencyClass pCurrency) {
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
     * Build the portfolio.
     * @return the new Portfolio
     * @throws OceanusException on error
     */
    public Portfolio build() throws OceanusException {
        /* Create the payee */
        final Portfolio myPortfolio = theDataSet.getPortfolios().addNewItem();
        myPortfolio.setName(theName);
        myPortfolio.setParent(theParent);
        myPortfolio.setCategory(theType);
        myPortfolio.setAssetCurrency(theCurrency);
        myPortfolio.setClosed(Boolean.FALSE);

        /* Check for errors */
        myPortfolio.adjustMapForItem();
        myPortfolio.validate();
        if (myPortfolio.hasErrors()) {
            theDataSet.getPortfolios().remove(myPortfolio);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;

        /* Return the portfolio */
        return myPortfolio;
    }
}
