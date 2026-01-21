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
package io.github.tonywasher.joceanus.moneywise.test.data.trans;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTaxClass;

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
     *
     * @param pBuilder the builder
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
    public String getTitle() {
        return "Cash and AutoCash Transactions";
    }

    @Override
    public String getDesc() {
        return "Cash accounts can be used as normal accounts or as autoCash";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_ASDA);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT,
                MoneyWiseDataTestAccounts.IDDP_STARLING_EURO);
        createCash(MoneyWiseDataTestAccounts.IDCS_CASH,
                MoneyWiseDataTestAccounts.IDCS_EUROS_CASH,
                MoneyWiseDataTestAccounts.IDCS_CASH_WALLET,
                MoneyWiseDataTestAccounts.IDCS_EUROS_WALLET);
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
     *
     * @throws OceanusException on error
     */
    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple payment from one account to autoCash */
        theTransBuilder.date("01-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("50")
                .to().partner(MoneyWiseDataTestAccounts.IDCS_CASH)
                .build();

        /* A simple refund to one account from autoCash */
        theTransBuilder.date("02-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDCS_CASH).amount("20")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A simple payment from autoCash */
        theTransBuilder.date("03-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD)
                .account(MoneyWiseDataTestAccounts.IDCS_CASH).amount("12")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_ASDA)
                .build();

        /* A simple refund to autoCash */
        theTransBuilder.date("04-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD)
                .account(MoneyWiseDataTestAccounts.IDCS_CASH).amount("6")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_ASDA)
                .build();

        /* A simple transfer from one account to cash */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("22")
                .to().partner(MoneyWiseDataTestAccounts.IDCS_CASH_WALLET)
                .build();

        /* A simple refund to one account from cash */
        theTransBuilder.date("06-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDCS_CASH_WALLET).amount("7")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A simple payment from one account to autoCash in foreign currency */
        theTransBuilder.date("07-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).amount("65")
                .to().partner(MoneyWiseDataTestAccounts.IDCS_EUROS_CASH)
                .build();

        /* A simple refund to one account from autoCash in foreign currency */
        theTransBuilder.date("08-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDCS_EUROS_CASH).amount("14")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO)
                .build();

        /* A simple payment from autoCash in foreign currency */
        theTransBuilder.date("09-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD)
                .account(MoneyWiseDataTestAccounts.IDCS_EUROS_CASH).amount("17")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_ASDA)
                .build();

        /* A simple refund to autoCash in foreign currency */
        theTransBuilder.date("10-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD)
                .account(MoneyWiseDataTestAccounts.IDCS_EUROS_CASH).amount("8")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_ASDA)
                .build();

        /* A simple transfer from one account to cash in foreign currency */
        theTransBuilder.date("11-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO).amount("13")
                .to().partner(MoneyWiseDataTestAccounts.IDCS_EUROS_WALLET)
                .build();

        /* A simple refund to one account from cash in foreign currency */
        theTransBuilder.date("12-Jun-1987").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDCS_EUROS_WALLET).amount("8.5")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO)
                .build();
    }

    @Override
    public void checkAnalysis() {
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT, "9955");
        checkAccountValue(MoneyWiseDataTestAccounts.IDDP_STARLING_EURO, "4697.28");
        checkAccountValue(MoneyWiseDataTestAccounts.IDCS_CASH_WALLET, "25");
        checkAccountValue(MoneyWiseDataTestAccounts.IDCS_EUROS_WALLET, "4.28");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_MARKET, "247.46", "0");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_ASDA, "0", "14.1");
        checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_CASH_EXPENSE, "0", "61.8");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_MKT_CURR_ADJUST, "247.46", "0");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_SHOP_FOOD, "0", "14.1");
        checkCategoryValue(MoneyWiseDataTestCategories.IDTC_EXP_CASH, "0", "61.8");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "247.46");
        checkTaxBasisValue(MoneyWiseTaxClass.EXPENSE, "-75.9");
    }
}
