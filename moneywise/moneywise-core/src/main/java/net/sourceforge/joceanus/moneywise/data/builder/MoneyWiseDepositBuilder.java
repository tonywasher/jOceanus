/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.builder;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * Deposit Builder.
 */
public class MoneyWiseDepositBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The DepositName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private MoneyWisePayee theParent;

    /**
     * The Deposit Category.
     */
    private MoneyWiseDepositCategory theCategory;

    /**
     * The Currency.
     */
    private MoneyWiseCurrency theCurrency;

    /**
     * The Opening Balance.
     */
    private OceanusMoney theOpeningBalance;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseDepositBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getDeposits().ensureMap();
        reportingCurrency();
    }

    /**
     * Set Name.
     * @param pName the name of the loan.
     * @return the builder
     */
    public MoneyWiseDepositBuilder name(final String pName) {
        theName = pName;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseDepositBuilder parent(final MoneyWisePayee pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Set Parent.
     * @param pParent the parent.
     * @return the builder
     */
    public MoneyWiseDepositBuilder parent(final String pParent) {
        return parent(theDataSet.getPayees().findItemByName(pParent));
    }

    /**
     * Set the depositCategory.
     * @param pCategory the category of the deposit.
     * @return the builder
     */
    public MoneyWiseDepositBuilder category(final MoneyWiseDepositCategory pCategory) {
        theCategory = pCategory;
        return this;
    }

    /**
     * Set the depositCategory.
     * @param pCategory the category of the deposit.
     * @return the builder
     */
    public MoneyWiseDepositBuilder category(final String pCategory) {
        return category(theDataSet.getDepositCategories().findItemByName(pCategory));
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the deposit.
     * @return the builder
     */
    public MoneyWiseDepositBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseDepositBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
        return currency(theDataSet.getAccountCurrencies().findItemByClass(pCurrency));
    }

    /**
     * Set the default currency.
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
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseDepositBuilder openingBalance(final OceanusMoney pOpening) {
        theOpeningBalance = pOpening;
        return this;
    }

    /**
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseDepositBuilder openingBalance(final String pOpening) {
        return openingBalance(new OceanusMoney(pOpening, theCurrency.getCurrency()));
    }

    /**
     * Build the deposit.
     * @return the new Deposit
     * @throws OceanusException on error
     */
    public MoneyWiseDeposit build() throws OceanusException {
        /* Create the deposit */
        final MoneyWiseDeposit myDeposit = theDataSet.getDeposits().addNewItem();
        myDeposit.setName(theName);
        myDeposit.setParent(theParent);
        myDeposit.setCategory(theCategory);
        myDeposit.setAssetCurrency(theCurrency);
        myDeposit.setOpeningBalance(theOpeningBalance);
        myDeposit.setClosed(Boolean.FALSE);

        /* Check for errors */
        myDeposit.adjustMapForItem();
        myDeposit.validate();
        if (myDeposit.hasErrors()) {
            myDeposit.removeItem();
            throw new MoneyWiseDataException(myDeposit, "Failed validation");
        }

        /* Reset values */
        theName = null;
        theCategory = null;
        theParent = null;
        theOpeningBalance = null;
        reportingCurrency();

        /* Return the deposit */
        return myDeposit;
    }
}
