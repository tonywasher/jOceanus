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
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseLoanCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Loan Builder.
 */
public class MoneyWiseLoanBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The LoanName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private MoneyWisePayee theParent;

    /**
     * The Loan Category.
     */
    private MoneyWiseLoanCategory theCategory;

    /**
     * The Currency.
     */
    private MoneyWiseCurrency theCurrency;

    /**
     * The Opening Balance.
     */
    private TethysMoney theOpeningBalance;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseLoanBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getLoans().ensureMap();
        defaultCurrency();
    }

    /**
     * Set Name.
     * @param pName the name of the loan.
     * @return the builder
     */
    public MoneyWiseLoanBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseLoanBuilder parent(final MoneyWisePayee pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseLoanBuilder parent(final String pParent) {
        return parent(theDataSet.getPayees().findItemByName(pParent));
    }

    /**
     * Set the loanCategory.
     * @param pCategory the category of the loan.
     * @return the builder
     */
    public MoneyWiseLoanBuilder category(final MoneyWiseLoanCategory pCategory) {
        theCategory = pCategory;
        return this;
    }

    /**
     * Set the loanCategory.
     * @param pCategory the category of the loan.
     * @return the builder
     */
    public MoneyWiseLoanBuilder category(final String pCategory) {
        return category(theDataSet.getLoanCategories().findItemByName(pCategory));
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the loan.
     * @return the builder
     */
    public MoneyWiseLoanBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseLoanBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
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
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseLoanBuilder openingBalance(final TethysMoney pOpening) {
        theOpeningBalance = pOpening;
        return this;
    }

    /**
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseLoanBuilder openingBalance(final String pOpening) {
        return openingBalance(new TethysMoney(pOpening, theCurrency.getCurrency()));
    }

    /**
     * Build the loan.
     * @return the new Loan
     * @throws OceanusException on error
     */
    public MoneyWiseLoan build() throws OceanusException {
        /* Create the loan */
        final MoneyWiseLoan myLoan = theDataSet.getLoans().addNewItem();
        myLoan.setName(theName);
        myLoan.setParent(theParent);
        myLoan.setCategory(theCategory);
        myLoan.setAssetCurrency(theCurrency);
        myLoan.setOpeningBalance(theOpeningBalance);
        myLoan.setClosed(Boolean.FALSE);

        /* Check for errors */
        myLoan.adjustMapForItem();
        myLoan.validate();
        if (myLoan.hasErrors()) {
            theDataSet.getLoans().remove(myLoan);
            throw new MoneyWiseDataException(myLoan, "Failed validation");
        }

        /* Update maps to reflect the new object */
        myLoan.adjustMapForItem();

        /* Reset values */
        theName = null;
        theCategory = null;
        theParent = null;
        theOpeningBalance = null;
        defaultCurrency();

        /* Return the loan */
        return myLoan;
    }
}
