/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Deposit Builder.
 */
public class MoneyWiseDepositBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The DepositName.
     */
    private String theName;

    /**
     * The Parent.
     */
    private Payee theParent;

    /**
     * The Deposit Category.
     */
    private DepositCategory theCategory;

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
    MoneyWiseDepositBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
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
    public MoneyWiseDepositBuilder parent(final Payee pParent) {
        theParent = pParent;
        return this;
    }

    /**
     * Set the depositCategory.
     * @param pCategory the category of the deposit.
     * @return the builder
     */
    public MoneyWiseDepositBuilder category(final DepositCategory pCategory) {
        theCategory = pCategory;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the deposit.
     * @return the builder
     */
    public MoneyWiseDepositBuilder currency(final AssetCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
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
    public MoneyWiseDepositBuilder openingBalance(final TethysMoney pOpening) {
        theOpeningBalance = pOpening;
        return this;
    }

    /**
     * Obtain the deposit.
     * @param pDeposit the name of the deposit.
     * @return the deposit
     */
    public Deposit lookupDeposit(final String pDeposit) {
        return theDataSet.getDeposits().findItemByName(pDeposit);
    }

    /**
     * Build the deposit.
     * @return the new Deposit
     * @throws OceanusException on error
     */
    public Deposit build() throws OceanusException {
        /* Create the deposit */
        final Deposit myDeposit = theDataSet.getDeposits().addNewItem();
        myDeposit.setName(theName);
        myDeposit.setParent(theParent);
        myDeposit.setCategory(theCategory);
        myDeposit.setAssetCurrency(theCurrency);
        myDeposit.setOpeningBalance(theOpeningBalance);
        myDeposit.validate();

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
