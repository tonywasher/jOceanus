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

import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Loan Builder.
 */
public class MoneyWiseLoanBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The LoanName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private Payee theParent;

    /**
     * The Loan Category.
     */
    private LoanCategory theCategory;

    /**
     * The Currency.
     */
    private AssetCurrency theCurrency;

    /**
     * The Opening Balance.
     */
    private TethysMoney theOpeningBalance;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseLoanBuilder(final MoneyWiseData pDataSet) {
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
     * Set Parent.
     * @param pParent the name of the parent.
     */
    public void parent(final String pParent) {
        theParent = lookupParent(pParent);
    }

    /**
     * Obtain the parent.
     * @param pParent the name of the parent.
     * @return the parent
     */
    private Payee lookupParent(final String pParent) {
        return theDataSet.getPayees().findItemByName(pParent);
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     */
    public void parent(final Payee pParent) {
        theParent = pParent;
    }

    /**
     * Set the loanCategory.
     * @param pCategory the category of the loan.
     */
    public void category(final LoanCategory pCategory) {
        theCategory = pCategory;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the loan.
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
     * Obtain the loan.
     * @param pLoan the name of the loan.
     * @return the loan
     */
    public Loan lookupLoan(final String pLoan) {
        return theDataSet.getLoans().findItemByName(pLoan);
    }

    /**
     * Build the loan.
     * @return the new Loan
     * @throws OceanusException on error
     */
    public Loan build() throws OceanusException {
        /* Create the loan */
        final Loan myLoan = theDataSet.getLoans().addNewItem();
        myLoan.setName(theName);
        myLoan.setParent(theParent);
        myLoan.setLoanCategory(theCategory);
        myLoan.setAssetCurrency(theCurrency);
        myLoan.setOpeningBalance(theOpeningBalance);
        myLoan.validate();

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
