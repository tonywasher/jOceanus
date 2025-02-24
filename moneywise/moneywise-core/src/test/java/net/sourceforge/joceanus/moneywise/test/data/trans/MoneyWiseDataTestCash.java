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
    public MoneyWiseDataTestCash(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "Cash";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.idPY_ASDA);
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent,
                MoneyWiseDataTestAccounts.idDP_StarlingEuro);
        createCash(MoneyWiseDataTestAccounts.idCS_Cash,
                MoneyWiseDataTestAccounts.idCS_EurosCash,
                MoneyWiseDataTestAccounts.idCS_CashWallet,
                MoneyWiseDataTestAccounts.idCS_EurosWallet);
    }

    @Override
    public void defineRates() throws OceanusException {
        createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "06-Apr-1980", "0.9");
        createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jan-2025", "0.85");
        createXchgRate(MoneyWiseCurrencyClass.EUR, "01-Jan-2025", "0.95");
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple payment from one account to autoCash */
        theTransBuilder.date("01-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("50")
                .to().partner(MoneyWiseDataTestAccounts.idCS_Cash)
                .build();

        /* A simple refund to one account from autoCash */
        theTransBuilder.date("02-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idCS_Cash).amount("20")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A simple payment from autoCash */
        theTransBuilder.date("03-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_Cash).amount("12")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refund to autoCash */
        theTransBuilder.date("04-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_Cash).amount("6")
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple transfer from one account to cash */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("22")
                .to().partner(MoneyWiseDataTestAccounts.idCS_CashWallet)
                .build();

        /* A simple refund to one account from cash */
        theTransBuilder.date("06-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idCS_CashWallet).amount("7")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A simple payment from one account to autoCash in foreign currency */
        theTransBuilder.date("07-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("65")
                .to().partner(MoneyWiseDataTestAccounts.idCS_EurosCash)
                .build();

        /* A simple refund to one account from autoCash in foreign currency */
        theTransBuilder.date("08-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idCS_EurosCash).amount("14")
                .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingEuro)
                .build();

        /* A simple payment from autoCash in foreign currency */
        theTransBuilder.date("09-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_EurosCash).amount("17")
                .to().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple refund to autoCash in foreign currency */
        theTransBuilder.date("10-Jun-1987").category(MoneyWiseDataTestCategories.idTC_ShopFood)
                .account(MoneyWiseDataTestAccounts.idCS_EurosCash).amount("8")
                .from().partner(MoneyWiseDataTestAccounts.idPY_ASDA)
                .build();

        /* A simple transfer from one account to cash in foreign currency */
        theTransBuilder.date("11-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idDP_StarlingEuro).amount("13")
                .to().partner(MoneyWiseDataTestAccounts.idCS_EurosWallet)
                .build();

        /* A simple refund to one account from cash in foreign currency */
        theTransBuilder.date("12-Jun-1987").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idCS_EurosWallet).amount("8.5")
                .to().partner(MoneyWiseDataTestAccounts.idDP_StarlingEuro)
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "9955");
        checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingEuro, "4697.28");
        checkAccountValue(MoneyWiseDataTestAccounts.idCS_CashWallet, "25");
        checkAccountValue(MoneyWiseDataTestAccounts.idCS_EurosWallet, "4.28");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Market, "247.46", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_ASDA, "0", "14.1");
        checkPayeeValue(MoneyWiseDataTestAccounts.idPY_CashExpense, "0", "61.8");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_MktCurrAdjust, "247.46", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ShopFood, "0", "14.1");
        checkCategoryValue(MoneyWiseDataTestCategories.idTC_ExpCash, "0", "61.8");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "247.46");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-75.9");
    }
}
