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
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;

/**
 * Cash Builder.
 */
public class MoneyWiseCashBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The Cash Name.
     */
    private String theName;

    /**
     * The Cash Category.
     */
    private MoneyWiseCashCategory theCategory;

    /**
     * The Currency.
     */
    private MoneyWiseCurrency theCurrency;

    /**
     * The Opening Balance.
     */
    private OceanusMoney theOpeningBalance;

    /**
     * The AutoPayee.
     */
    private MoneyWisePayee theAutoPayee;

    /**
     * The AutoExpense.
     */
    private MoneyWiseTransCategory theAutoExpense;

    /**
     * Constructor.
     *
     * @param pDataSet the dataSet
     */
    public MoneyWiseCashBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getCash().ensureMap();
        reportingCurrency();
    }

    /**
     * Set Name.
     *
     * @param pName the name of the loan.
     * @return the builder
     */
    public MoneyWiseCashBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set the cashCategory.
     *
     * @param pCategory the category of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder category(final MoneyWiseCashCategory pCategory) {
        theCategory = pCategory;
        return this;
    }

    /**
     * Set the cashCategory.
     *
     * @param pCategory the category of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder category(final String pCategory) {
        return category(theDataSet.getCashCategories().findItemByName(pCategory));
    }

    /**
     * Set the autoExpense.
     *
     * @param pCategory the category.
     * @param pPayee    the payee
     * @return the builder
     */
    public MoneyWiseCashBuilder autoExpense(final MoneyWiseTransCategory pCategory,
                                            final MoneyWisePayee pPayee) {
        theAutoExpense = pCategory;
        theAutoPayee = pPayee;
        return this;
    }

    /**
     * Set the autoExpense.
     *
     * @param pCategory the category.
     * @param pPayee    the payee
     * @return the builder
     */
    public MoneyWiseCashBuilder autoExpense(final String pCategory,
                                            final String pPayee) {
        final MoneyWiseTransCategory myCat = theDataSet.getTransCategories().findItemByName(pCategory);
        final MoneyWisePayee myPayee = theDataSet.getPayees().findItemByName(pPayee);
        return autoExpense(myCat, myPayee);
    }

    /**
     * Set the currency.
     *
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     *
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
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
     * Set the openingBalance.
     *
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseCashBuilder openingBalance(final OceanusMoney pOpening) {
        theOpeningBalance = pOpening;
        return this;
    }

    /**
     * Set the openingBalance.
     *
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseCashBuilder openingBalance(final String pOpening) {
        return openingBalance(new OceanusMoney(pOpening, theCurrency.getCurrency()));
    }

    /**
     * Build the cash.
     *
     * @return the new cash
     * @throws OceanusException on error
     */
    public MoneyWiseCash build() throws OceanusException {
        /* Create the cash */
        final MoneyWiseCash myCash = theDataSet.getCash().addNewItem();
        myCash.setName(theName);
        myCash.setCategory(theCategory);
        myCash.setAssetCurrency(theCurrency);
        myCash.setOpeningBalance(theOpeningBalance);
        myCash.setAutoExpense(theAutoExpense);
        myCash.setAutoPayee(theAutoPayee);
        myCash.setClosed(Boolean.FALSE);

        /* Reset the values */
        reset();

        /* Check for errors */
        myCash.adjustMapForItem();
        myCash.validate();
        if (myCash.hasErrors()) {
            myCash.removeItem();
            throw new MoneyWiseDataException(myCash, "Failed validation");
        }

        /* Return the cash */
        return myCash;
    }

    /**
     * Reset the builder.
     */
    public void reset() {
        /* Reset values */
        theName = null;
        theCategory = null;
        theOpeningBalance = null;
        theAutoExpense = null;
        theAutoPayee = null;
        reportingCurrency();
    }
}
