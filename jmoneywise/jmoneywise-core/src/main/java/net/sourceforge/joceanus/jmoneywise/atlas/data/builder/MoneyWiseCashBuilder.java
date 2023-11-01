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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Cash Builder.
 */
public class MoneyWiseCashBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The Cash Name.
     */
    private String theName;

    /**
     * The Cash Category.
     */
    private CashCategory theCategory;

    /**
     * The Currency.
     */
    private AssetCurrency theCurrency;

    /**
     * The Opening Balance.
     */
    private TethysMoney theOpeningBalance;

    /**
     * The AutoPayee.
     */
    private Payee theAutoPayee;

    /**
     * The AutoExpense.
     */
    private TransactionCategory theAutoExpense;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseCashBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getCash().ensureMap();
        defaultCurrency();
    }

    /**
     * Set Name.
     * @param pName the name of the loan.
     * @return the builder
     */
    public MoneyWiseCashBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set the cashCategory.
     * @param pCategory the category of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder category(final CashCategory pCategory) {
        theCategory = pCategory;
        return this;
    }

    /**
     * Set the cashCategory.
     * @param pCategory the category of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder category(final String pCategory) {
        return category(theDataSet.getCashCategories().findItemByName(pCategory));
    }

    /**
     * Set the autoExpense.
     * @param pCategory the category.
     * @param pPayee the payee
     * @return the builder
     */
    public MoneyWiseCashBuilder autoExpense(final TransactionCategory pCategory,
                                            final Payee pPayee) {
        theAutoExpense = pCategory;
        theAutoPayee = pPayee;
        return this;
    }

    /**
     * Set the autoExpense.
     * @param pCategory the category.
     * @param pPayee the payee
     * @return the builder
     */
    public MoneyWiseCashBuilder autoExpense(final String pCategory,
                                            final String pPayee) {
        final TransactionCategory myCat = theDataSet.getTransCategories().findItemByName(pCategory);
        final Payee myPayee = theDataSet.getPayees().findItemByName(pPayee);
        return autoExpense(myCat, myPayee);
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder currency(final AssetCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseCashBuilder currency(final AssetCurrencyClass pCurrency) {
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
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseCashBuilder openingBalance(final TethysMoney pOpening) {
        theOpeningBalance = pOpening;
        return this;
    }

    /**
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseCashBuilder openingBalance(final String pOpening) {
        return openingBalance(new TethysMoney(pOpening, theCurrency.getCurrency()));
    }

    /**
     * Build the cash.
     * @return the new cash
     * @throws OceanusException on error
     */
    public Cash build() throws OceanusException {
        /* Create the cash */
        final Cash myCash = theDataSet.getCash().addNewItem();
        myCash.setName(theName);
        myCash.setCategory(theCategory);
        myCash.setAssetCurrency(theCurrency);
        myCash.setOpeningBalance(theOpeningBalance);
        myCash.setAutoExpense(theAutoExpense);
        myCash.setAutoPayee(theAutoPayee);
        myCash.setClosed(Boolean.FALSE);

        /* Check for errors */
        myCash.adjustMapForItem();
        myCash.validate();
        if (myCash.hasErrors()) {
            theDataSet.getCash().remove(myCash);
            throw new MoneyWiseDataException("Failed validation");
        }

        /* Reset values */
        theName = null;
        theCategory = null;
        theOpeningBalance = null;
        theAutoExpense = null;
        theAutoPayee = null;
        defaultCurrency();

        /* Return the cash */
        return myCash;
    }
}
