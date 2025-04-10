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
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Test Dividend Share transactions.
 */
public class MoneyWiseDataTestShareDividend
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     */
    public MoneyWiseDataTestShareDividend(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "ShareDividend";
    }

    @Override
    public String getTitle() {
        return "Share Dividend Transactions";
    }

    @Override
    public String getDesc() {
        return "Shares can be bought and sold and have their number of units adjusted.";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_PARENTS);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT,
                MoneyWiseDataTestAccounts.IDDP_STARLING_DOLLAR);
        createPortfolios(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK);
        createSecurities(MoneyWiseDataTestAccounts.IDSC_BARCLAYS_SHARES,
                MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES_US);
    }

    @Override
    public void defineRates() throws OceanusException {
        createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
        createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jan-2025", "0.85");
    }

    @Override
    public void definePrices() throws OceanusException {
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_BARCLAYS_SHARES, "06-Apr-1980", "2");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES_US, "06-Apr-1980", "3");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_BARCLAYS_SHARES, "06-Apr-1995", "5.50");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES_US, "06-Apr-1995", "5");
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        switch (pInfoClass) {
            case ACCOUNTDELTAUNITS:
            case PARTNERAMOUNT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* Build local share holding */
        theTransBuilder.date("01-Jul-1990").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES).amount("6000")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS).accountUnits("3000")
                .build();

        /* Build foreign shareholding */
        theTransBuilder.date("02-Jul-1990").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES_US).amount("6000")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS)
                .accountUnits("2000")
                .build();

        /* A simple dividend to deposit */
        theTransBuilder.date("03-Jul-1990").category(MoneyWiseDataTestCategories.IDTC_DIVIDEND)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES).amount("1200")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).taxCredit("200")
                .build();

        /* A simple foreign dividend to deposit */
        theTransBuilder.date("04-Jul-1990").category(MoneyWiseDataTestCategories.IDTC_DIVIDEND)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES_US).amount("200.00")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .partnerAmount("180.00").taxCredit("60.00")
                .build();

        /* A simple foreign dividend to deposit */
        //theTransBuilder.date("05-Jul-1990").category(MoneyWiseDataTestCategories.IDTC_DIVIDEND)
        //        .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES_US).amount("200.00")
        //        .to().partner(MoneyWiseDataTestAccounts.IDDP_STARLING_DOLLAR).taxCredit("60.00")
        //        .build();

        /* A simple reinvested dividend */
        theTransBuilder.date("06-Jul-1990").category(MoneyWiseDataTestCategories.IDTC_DIVIDEND)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES).amount("150.00")
                .to().partner(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES)
                .accountUnits("20").taxCredit("30.00")
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
