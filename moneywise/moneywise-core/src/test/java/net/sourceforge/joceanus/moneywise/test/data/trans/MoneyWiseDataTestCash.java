/* *****************************************************************************
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
package net.sourceforge.joceanus.moneywise.test.data.trans;

import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Test AutoCash.
 */
public class MoneyWiseDataTestCash
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    MoneyWiseDataTestCash(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    String getName() {
        return "Cash";
    }

    @Override
    void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.idPY_ASDA);
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent,
                MoneyWiseDataTestAccounts.idDP_StarlingEuro);
        createCash(MoneyWiseDataTestAccounts.idCS_Cash,
                MoneyWiseDataTestAccounts.idCS_EurosCash,
                MoneyWiseDataTestAccounts.idCS_CashWallet,
                MoneyWiseDataTestAccounts.idCS_EurosWallet);
    }

    @Override
    void defineRates() throws OceanusException {
        createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "06-Apr-1980", "0.9");
        createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jun-2010", "0.85");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "01-Jun-2010", "0.95");
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple payment from one account to autoCash */
        theTransBuilder.date("01-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("5000")
                .to().partner(MoneyWiseDataTestAccounts.idCS_Cash)
                .build();

        /* A simple refund to one account from autoCash */
        theTransBuilder.date("02-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idCS_Cash).amount("2000")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A simple payment from autoCash */
        theTransBuilder.date("03-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_Cash).amount("1200")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refund to autoCash */
        theTransBuilder.date("04-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_Cash).amount("600")
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple transfer from one account to cash */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("2200")
                .to().partner(MoneyWiseDataTestAccounts.idCS_CashWallet)
                .build();

        /* A simple refund to one account from cash */
        theTransBuilder.date("06-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_CashWallet).amount("700")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A simple payment from one account to autoCash in foreign currency */
        theTransBuilder.date("07-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("6500")
                .to().partner(MoneyWiseDataTestAccounts.idCS_EurosCash)
                .build();

        /* A simple refund to one account from autoCash in foreign currency */
        theTransBuilder.date("08-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idCS_EurosCash).amount("1400")
                .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingEuro)
                .build();

        /* A simple payment from autoCash in foreign currency */
        theTransBuilder.date("09-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_EurosCash).amount("1700")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refund to autoCash in foreign currency */
        theTransBuilder.date("10-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_EurosWallet)
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .amount("800").build();

        /* A simple transfer from one account to cash in foreign currency */
        theTransBuilder.date("11-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("1300")
                .to().partner(MoneyWiseDataTestAccounts.idCS_EurosWallet)
                .build();

        /* A simple refund to one account from cash in foreign currency */
        theTransBuilder.date("12-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_EurosWallet).amount("8500")
                .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingEuro)
                .build();
    }

    @Override
    void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "6950");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingEuro, "5320");
        checkAccountValue(MoneyWiseDataTestAccounts.idCS_CashWallet, "4717.5");
        checkAccountValue(MoneyWiseDataTestAccounts.idCS_EurosWallet, "4717.5");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Market, "607.5", "120");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_ASDA, "607.5", "120");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_CashExpense, "607.5", "120");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_MktCurrAdjust, "607.5", "120");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ShopFood, "607.5", "120");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ExpCash, "607.5", "120");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "487.5");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "487.5");
    }
}
