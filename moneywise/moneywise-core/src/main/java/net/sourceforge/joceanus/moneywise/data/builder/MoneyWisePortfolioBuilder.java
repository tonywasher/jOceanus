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
package net.sourceforge.joceanus.moneywise.data.builder;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePortfolioType;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;

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
     *
     * @param pDataSet the dataSet
     */
    public MoneyWisePortfolioBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getPortfolios().ensureMap();
        reportingCurrency();
    }

    /**
     * Set Name.
     *
     * @param pName the name of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set Parent.
     *
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder parent(final MoneyWisePayee pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Set Parent.
     *
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder parent(final String pParent) {
        return parent(theDataSet.getPayees().findItemByName(pParent));
    }

    /**
     * Set the portfolioType.
     *
     * @param pType the type of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder type(final MoneyWisePortfolioType pType) {
        theType = pType;
        return this;
    }

    /**
     * Set the portfolioType.
     *
     * @param pType the type of the portfolio.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder type(final MoneyWisePortfolioClass pType) {
        return type(theDataSet.getPortfolioTypes().findItemByClass(pType));
    }

    /**
     * Set the currency.
     *
     * @param pCurrency the currency of the loan.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     *
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWisePortfolioBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
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
     *
     * @return the currency
     */
    private MoneyWiseCurrency lookupReportingCurrency() {
        return theDataSet.getReportingCurrency();
    }

    /**
     * Build the portfolio.
     *
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

        /* Reset the values */
        reset();

        /* Check for errors */
        myPortfolio.adjustMapForItem();
        myPortfolio.validate();
        if (myPortfolio.hasErrors()) {
            myPortfolio.removeItem();
            throw new MoneyWiseDataException(myPortfolio, "Failed validation");
        }

        /* Return the portfolio */
        return myPortfolio;
    }

    /**
     * Reset the builder.
     */
    public void reset() {
        /* Reset values */
        theName = null;
        theParent = null;
        theType = null;
        reportingCurrency();
    }
}
