/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
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
    MoneyWiseCashBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        defaultCurrency();
    }

    /**
     * Set Name.
     * @param pName the name of the loan.
     */
    public void name(final String pName) {
        theName = pName;
    }

    /**
     * Set the cashCategory.
     * @param pCategory the category of the cash.
     */
    public void category(final CashCategory pCategory) {
        theCategory = pCategory;
    }

    /**
     * Set the autoExpense.
     * @param pCategory the category.
     * @param pPayee the payee
     */
    public void autoExpense(final String pCategory,
                            final String pPayee) {
        autoExpense(theDataSet.getTransCategories().findItemByName(pCategory),
                    theDataSet.getPayees().findItemByName(pPayee));
    }

    /**
     * Set the autoExpense.
     * @param pCategory the category.
     * @param pPayee the payee
     */
    public void autoExpense(final TransactionCategory pCategory,
                            final Payee pPayee) {
        theAutoExpense = pCategory;
        theAutoPayee = pPayee;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     */
    public void currency(final AssetCurrency pCurrency) {
        theCurrency = pCurrency;
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
     */
    public void openingBalance(final TethysMoney pOpening) {
        theOpeningBalance = pOpening;
    }

    /**
     * Obtain the cash.
     * @param pCash the name of the cash.
     * @return the cash
     */
    public Cash lookupCash(final String pCash) {
        return theDataSet.getCash().findItemByName(pCash);
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
        myCash.setCashCategory(theCategory);
        myCash.setAssetCurrency(theCurrency);
        myCash.setOpeningBalance(theOpeningBalance);
        myCash.setAutoExpense(theAutoExpense);
        myCash.setAutoPayee(theAutoPayee);
        myCash.validate();

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
