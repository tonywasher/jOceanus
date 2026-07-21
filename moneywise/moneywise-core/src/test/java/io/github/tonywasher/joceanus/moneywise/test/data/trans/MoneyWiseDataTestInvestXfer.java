/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

import io.github.tonywasher.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

public class MoneyWiseDataTestInvestXfer
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
    public MoneyWiseDataTestInvestXfer(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "InvestXfer";
    }

    @Override
    public String getTitle() {
        return "Investment Account Transfer and Income/Expense";
    }

    @Override
    public String getDesc() {
        return "Investment accounts can generally performa ll the tasks that standard deposit accounts can do.";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_PARENTS);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT,
                MoneyWiseDataTestAccounts.IDDP_STARLING_DOLLAR);
        createPortfolios(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK,
                MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_US,
                MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_ISA);
    }

    @Override
    public void defineRates() throws OceanusException {
        createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
        createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jan-2025", "0.85");
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        return MoneyWiseTransInfoClass.PARTNERAMOUNT == pInfoClass;
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple inheritance in cash */
        theTransBuilder.date("01-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).amount("6000")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .build();

        /* A simple inheritance in foreign cash */
        theTransBuilder.date("02-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_US).amount("4000")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .build();

        /* A simple transfer in */
        theTransBuilder.date("03-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1500")
                .to().partner(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK)
                .build();

        /* A simple transfer in with foreign cash */
        theTransBuilder.date("04-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_STARLING_DOLLAR).amount("500")
                .to().partner(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_US)
                .build();

        /* A simple transfer in with changing currency */
        theTransBuilder.date("05-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("200")
                .to().partner(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_US).partnerAmount("240")
                .build();

        /* A simple transfer between investment accounts */
        theTransBuilder.date("06-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).amount("20")
                .to().partner(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_ISA)
                .build();

        /* A simple transfer between different currency investment accounts */
        theTransBuilder.date("07-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).amount("30")
                .to().partner(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_US).partnerAmount("35")
                .build();

        /* A simple expense */
        theTransBuilder.date("08-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_CHG_FEES)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).amount("2.3")
                .to().partner(MoneyWiseDataTestAccounts.IDPY_INTERACTIVE_INVESTOR)
                .build();

        /* A simple interest receipt */
        theTransBuilder.date("09-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).amount("12")
                .to().partner(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).taxCredit("2")
                .build();

        /* A simple interest receipt to different account */
        theTransBuilder.date("09-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_INTEREST)
                .account(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK).amount("34")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).taxCredit("3.4")
                .build();
    }

    @Override
    public void checkErrors() {
    }

    @Override
    public void checkAnalysis() {
        //checkAccountValue(MoneyWiseDataTestAccounts.IDDP_BarclaysCurrent, "6950");
        //checkAccountValue(MoneyWiseDataTestAccounts.IDDP_NatWideFlexDirect, "12000");
        //checkAccountValue(MoneyWiseDataTestAccounts.IDDP_StarlingEuro, "5320");
        //checkAccountValue(MoneyWiseDataTestAccounts.IDDP_StarlingDollar, "4717.5");
        //checkPayeeValue(MoneyWiseDataTestAccounts.IDPY_Market, "607.5", "120");
        //checkCategoryValue(MoneyWiseDataTestCategories.IDTC_MktCurrAdjust, "607.5", "120");
        //checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "487.5");
    }
}
