/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
 * Test Basic Share transactions.
 */
public class MoneyWiseDataTestShareBuySell
        extends MoneyWiseDataTestCase {
    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * Constructor.
     * @param pBuilder the builder
     */
    public MoneyWiseDataTestShareBuySell(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        super(pBuilder);
        theTransBuilder = getTransBuilder();
    }

    @Override
    public String getName() {
        return "ShareBuySell";
    }

    @Override
    public String getTitle() {
        return "Buy/Sell Share Transactions";
    }

    @Override
    public String getDesc() {
        return "Shares can be bought and sold and have their number of units adjusted.";
    }

    @Override
    public void setUpAccounts() throws OceanusException {
        createPayees(MoneyWiseDataTestAccounts.IDPY_PARENTS);
        createDeposits(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT);
        createPortfolios(MoneyWiseDataTestAccounts.IDPF_INTERACTIVE_INVESTOR_STOCK);
        createSecurities(MoneyWiseDataTestAccounts.IDSC_BARCLAYS_SHARES,
                MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES,
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
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES, "06-Apr-1980", "4");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES_US, "06-Apr-1980", "3");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_BARCLAYS_SHARES, "06-Apr-1995", "5.50");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES, "06-Apr-1995", "4.50");
        createSecPrice(MoneyWiseDataTestAccounts.IDSC_HALIFAX_SHARES_US, "06-Apr-1995", "5");
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        switch (pInfoClass) {
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
            case PRICE:
            case PARTNERAMOUNT:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple inheritance of shares */
        theTransBuilder.date("01-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_INHERITANCE)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES).amount("6000")
                .from().partner(MoneyWiseDataTestAccounts.IDPY_PARENTS).accountUnits("3000")
                .build();

        /* A simple buying of shares from a deposit account */
        theTransBuilder.date("02-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1000")
                .to().partner(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES).partnerUnits("250")
                .build();

        /* A simple buying of foreign shares from a deposit account */
        theTransBuilder.date("03-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).amount("1200")
                .to().partner(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES_US)
                .partnerUnits("500").partnerAmount("1500")
                .build();

        /* A simple stock split */
        theTransBuilder.date("04-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_SEC_STOCK_SPLIT)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES)
                .to().partner(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES)
                .accountUnits("300").price("4.5")
                .build();

        /* A simple adjustment of units */
        theTransBuilder.date("05-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_SEC_ADJUST)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES)
                .to().partner(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES).accountUnits("-6")
                .build();

        /* A simple rights issue waived */
        theTransBuilder.date("06-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_SEC_RIGHTS_ISSUE)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES).amount("785.87")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A simple rights issue taken */
        theTransBuilder.date("07-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_SEC_RIGHTS_ISSUE)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES).amount("112.89")
                .from().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).accountUnits("55")
                .build();

        /* A simple return of capital */
        theTransBuilder.date("08-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES).amount("245.93")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .build();

        /* A partial sale */
        theTransBuilder.date("09-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDSH_BARCLAYS_SHARES).amount("784.45")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT).accountUnits("-600")
                .build();

        /* A partial sale */
        theTransBuilder.date("10-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_TRANSFER)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES_US).amount("300.00")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
                .accountUnits("-100").partnerAmount("350.00")
                .build();

        /* A full sale */
        theTransBuilder.date("11-Jun-1990").category(MoneyWiseDataTestCategories.IDTC_SEC_CLOSE)
                .account(MoneyWiseDataTestAccounts.IDSH_HALIFAX_SHARES).amount("673.56")
                .to().partner(MoneyWiseDataTestAccounts.IDDP_BARCLAYS_CURRENT)
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
