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
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Portfolio Builder.
 */
public class MoneyWisePortfolioBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The PortfolioName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private MoneyWisePayee theParent;

    /**
     * The PortfolioType.
     */
    private MoneyWisePortfolioType theType;

    /**
     * The Currency.
     */
    private MoneyWiseCurrency theCurrency;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWisePortfolioBuilder(final MoneyWiseDataSet pDataSet) {
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
    public MoneyWisePortfolioBuilder parent(final MoneyWisePayee pParent) {
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
    public MoneyWisePortfolioBuilder type(final MoneyWisePortfolioType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the portfolioType.
     * @param pType the type of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder type(final MoneyWisePortfolioClass pType) {
        return type(theDataSet.getPortfolioTypes().findItemByClass(pType));
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the loan.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
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
    private MoneyWiseCurrency lookupDefaultCurrency() {
        return theDataSet.getDefaultCurrency();
    }

    /**
     * Build the portfolio.
     * @return the new Portfolio
     * @throws OceanusException on error
     */
    public MoneyWisePortfolio build() throws OceanusException {
        /* Create the payee */
        final MoneyWisePortfolio myPortfolio = theDataSet.getPortfolios().addNewItem();
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
            throw new MoneyWiseDataException(myPortfolio, "Failed validation");
        }

        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;

        /* Return the portfolio */
        return myPortfolio;
    }
}
