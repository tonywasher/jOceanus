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
        createPayees(MoneyWiseDataTestAccounts.idPY_Parents);
        createDeposits(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent);
        createPortfolios(MoneyWiseDataTestAccounts.idPF_InteractiveInvestorStock);
        createSecurities(MoneyWiseDataTestAccounts.idSC_BarclaysShares,
                MoneyWiseDataTestAccounts.idSC_HalifaxShares);
    }

    //@Override
    //public void defineRates() throws OceanusException {
    //    createXchgRate(MoneyWiseCurrencyClass.USD, "06-Apr-1980", "0.8");
    //    createXchgRate(MoneyWiseCurrencyClass.EUR, "06-Apr-1980", "0.9");
    //    createXchgRate(MoneyWiseCurrencyClass.USD, "01-Jan-2025", "0.85");
    //    createXchgRate(MoneyWiseCurrencyClass.EUR, "01-Jan-2025", "0.95");
    //}

    @Override
    public void definePrices() throws OceanusException {
        createSecPrice(MoneyWiseDataTestAccounts.idSC_BarclaysShares, "06-Apr-1980", "2");
        createSecPrice(MoneyWiseDataTestAccounts.idSC_HalifaxShares, "06-Apr-1980", "4");
        createSecPrice(MoneyWiseDataTestAccounts.idSC_BarclaysShares, "06-Apr-1995", "5.50");
        createSecPrice(MoneyWiseDataTestAccounts.idSC_HalifaxShares, "06-Apr-1995", "4.50");
    }

    @Override
    public boolean useInfoClass(final MoneyWiseTransInfoClass pInfoClass) {
        switch (pInfoClass) {
            case ACCOUNTDELTAUNITS:
            //case PARTNERDELTAUNITS:
            case PRICE:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void defineTransactions() throws OceanusException {
        /* A simple inheritance of shares */
        theTransBuilder.date("01-Jun-1990").category(MoneyWiseDataTestCategories.idTC_Inheritance)
                .account(MoneyWiseDataTestAccounts.idSH_BarclaysShares).amount("6000")
                .from().partner(MoneyWiseDataTestAccounts.idPY_Parents).accountUnits("3000")
                .build();

        /* A simple buying of shares from a deposit account */
        ////theTransBuilder.date("02-Jun-1990").category(MoneyWiseDataTestCategories.idTC_Transfer)
        //        .account(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).amount("1000")
        //        .to().partner(MoneyWiseDataTestAccounts.idSH_HalifaxShares).partnerUnits("250")
        //        .build();

        /* A simple stock split */
        theTransBuilder.date("03-Jun-1990").category(MoneyWiseDataTestCategories.idTC_SecStockSplit)
                .account(MoneyWiseDataTestAccounts.idSH_BarclaysShares)
                .to().partner(MoneyWiseDataTestAccounts.idSH_BarclaysShares)
                .accountUnits("300").price("4.5")
                .build();

        /* A simple adjustment of units */
        //theTransBuilder.date("04-Jun-1990").category(MoneyWiseDataTestCategories.idTC_SecAdjust)
        //        .account(MoneyWiseDataTestAccounts.idSH_HalifaxShares)
        //        .to().partner(MoneyWiseDataTestAccounts.idSH_HalifaxShares).accountUnits("-6")
        //        .build();

        /* A simple rights issue waived */
        //theTransBuilder.date("05-Jun-1990").category(MoneyWiseDataTestCategories.idTC_SecRightsIssue)
        //        .account(MoneyWiseDataTestAccounts.idSH_HalifaxShares).amount("785.87")
        //        .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
        //        .build();

        /* A simple rights issue taken */
        //theTransBuilder.date("06-Jun-1990").category(MoneyWiseDataTestCategories.idTC_SecRightsIssue)
        //        .account(MoneyWiseDataTestAccounts.idSH_HalifaxShares).amount("112.89")
        //        .from().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).accountUnits("55")
        //        .build();

        /* A simple return of capital */
        theTransBuilder.date("07-Jun-1990").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idSH_BarclaysShares).amount("245.93")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
                .build();

        /* A partial sale */
        theTransBuilder.date("08-Jun-1990").category(MoneyWiseDataTestCategories.idTC_Transfer)
                .account(MoneyWiseDataTestAccounts.idSH_BarclaysShares).amount("784.45")
                .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent).accountUnits("-600")
                .build();

        /* A full sale */
        //theTransBuilder.date("09-Jun-1990").category(MoneyWiseDataTestCategories.idTC_SecClose)
        //        .account(MoneyWiseDataTestAccounts.idSH_HalifaxShares).amount("673.56")
        //        .to().partner(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent)
        //        .build();
    }

    @Override
    public void checkErrors() {
    }

    @Override
    public void checkAnalysis() {
        //checkAccountValue(MoneyWiseDataTestAccounts.idDP_BarclaysCurrent, "6950");
        //checkAccountValue(MoneyWiseDataTestAccounts.idDP_NatWideFlexDirect, "12000");
        //checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingEuro, "5320");
        //checkAccountValue(MoneyWiseDataTestAccounts.idDP_StarlingDollar, "4717.5");
        //checkPayeeValue(MoneyWiseDataTestAccounts.idPY_Market, "607.5", "120");
        //checkCategoryValue(MoneyWiseDataTestCategories.idTC_MktCurrAdjust, "607.5", "120");
        //checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "487.5");
    }
}
