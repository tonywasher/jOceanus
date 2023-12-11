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
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDepositCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

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
    private TethysMoney theOpeningBalance;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseDepositBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getDeposits().ensureMap();
        defaultCurrency();
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
    public MoneyWiseDepositBuilder openingBalance(final TethysMoney pOpening) {
        theOpeningBalance = pOpening;
        return this;
    }

    /**
     * Set the openingBalance.
     * @param pOpening the opening Balance
     * @return the builder
     */
    public MoneyWiseDepositBuilder openingBalance(final String pOpening) {
        return openingBalance(new TethysMoney(pOpening, theCurrency.getCurrency()));
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
            theDataSet.getDeposits().remove(myDeposit);
            throw new MoneyWiseDataException(myDeposit, "Failed validation");
        }

        /* Reset values */
        theName = null;
        theCategory = null;
        theParent = null;
        theOpeningBalance = null;
        defaultCurrency();

        /* Return the deposit */
        return myDeposit;
    }
}
