/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data;

import org.junit.jupiter.api.Assertions;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisAccountBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseXchgRateBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;

/**
 * Transactions builder.
 */
public class MoneyWiseTestTransactions {
    /**
     * TrasnactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * XchgRateBuilder.
     */
    private final MoneyWiseXchgRateBuilder theXchgRateBuilder;

    /**
     * The dataSet.
     */
    private final MoneyWiseDataSet theData;

    /**
     * The analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     * @throws OceanusException on error
     */
    MoneyWiseTestTransactions(final MoneyWiseDataSet pDataSet) throws OceanusException {
        /* Create the builders */
        theTransBuilder = new MoneyWiseTransactionBuilder(pDataSet);
        theXchgRateBuilder = new MoneyWiseXchgRateBuilder(pDataSet);
        theData = pDataSet;
     }

    /**
     * Create transaction test.
     * @throws OceanusException on error
     */
    public void buildTransactions() throws OceanusException {
        //createXchgRates();
        //createTransfers();
        //createExpenses();
        //createAutoExpenses();
        createIncomes();
    }

    /**
     * Check analysis.
     * @param pAnalysis the analysis
     * @throws OceanusException on error
     */
    public void checkAnalysis(final MoneyWiseXAnalysis pAnalysis) throws OceanusException {
        theAnalysis = pAnalysis;
        //checkTransferTotals();
        //checkExpenseTotals();
        //checkAutoExpenseTotals();
        //checkIncomeTotals();
    }

    /**
     * build xchgRates.
     * @throws OceanusException on error
     */
    private void createXchgRates() throws OceanusException {
        theXchgRateBuilder.currency(MoneyWiseCurrencyClass.USD).date("01-Jun-1980").rate("0.8").build();
        theXchgRateBuilder.currency(MoneyWiseCurrencyClass.EUR).date("01-Jun-1980").rate("0.9").build();
        theXchgRateBuilder.currency(MoneyWiseCurrencyClass.USD).date("01-Jun-2010").rate("0.85").build();
        theXchgRateBuilder.currency(MoneyWiseCurrencyClass.EUR).date("01-Jun-2010").rate("0.95").build();
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    private void createTransfers() throws OceanusException {
        /* A simple transfer from one account to another */
        theTransBuilder.date("01-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
                .amount("2000").build();

        /* A simple transfer from standard currency to non-standard currency */
        theTransBuilder.date("02-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_StarlingEuro)
                .amount("2000").partnerAmount("2100").build();

        /* A simple transfer from non-standard currency to standard currency */
        theTransBuilder.date("03-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("1000").partnerAmount("950").build();

        /* A simple transfer from non-standard currency to non-standard currency */
        theTransBuilder.date("04-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idDP_StarlingDollar)
                .amount("500").partnerAmount("550").build();

        /* Check for failure on a transfer with same account as debit/credit */
        Assertions.assertThrows(MoneyWiseDataException.class,
                () -> theTransBuilder.date("05-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("500").partnerAmount("550").build(),
        "Failed to reject identical Debit/Credit for transfer");

        /* Check for failure on a transfer from standard currency to non-standard currency with no partnerAmount */
        Assertions.assertThrows(MoneyWiseDataException.class,
                () -> theTransBuilder.date("06-Jun-1985").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_StarlingEuro)
                .amount("2000").build(),
        "Failed to reject missing partnerAmount when transferring between accounts with differing currencies");

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Check transfer totals.
     */
    private void checkTransferTotals() {
        checkAccountValue(MoneyWiseTestAccounts.idDP_BarclaysCurrent, "6950");
        checkAccountValue(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, "12000");
        checkAccountValue(MoneyWiseTestAccounts.idDP_StarlingEuro, "570");
        checkAccountValue(MoneyWiseTestAccounts.idDP_StarlingDollar, "467.5");
        checkPayeeValue(MoneyWiseTestAccounts.idPY_Market, "107.5", "120");
        checkCategoryValue(MoneyWiseTestCategories.idTC_MktCurrAdjust, "107.5", "120");
        checkTaxBasisValue(MoneyWiseTaxClass.MARKET, "-12.5");
    }

    /**
     * Create simple expenses.
     * @throws OceanusException on error
     */
    private void createExpenses() throws OceanusException {
        /* A simple expense */
        //theTransBuilder.date("01-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopFood)
        //        .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idPY_ASDA)
        //        .amount("21.95").build();

        /* A simple refunded expense */
        //theTransBuilder.date("02-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopFood)
        //        .pair(MoneyWiseTestAccounts.idPY_ASDA, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
        //        .amount("9.99").build();

        /* A simple expense from non-standard currency */
        //theTransBuilder.date("03-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopClothes)
        //        .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idPY_ASDA)
        //        .amount("31.2").build();

        /* A simple refunded expense from non-standard currency */
        //theTransBuilder.date("04-Jun-1986").category(MoneyWiseTestCategories.idTC_ShopClothes)
        //       .pair(MoneyWiseTestAccounts.idPY_ASDA, MoneyWiseTestAccounts.idDP_StarlingEuro)
        //        .amount("500").partnerAmount("550").build();

        /* Create a private loan */
        //theTransBuilder.date("05-Jun-1986").category(MoneyWiseTestCategories.idTC_Transfer)
        //        .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idLN_DamageLoan)
        //        .amount("1000").build();

        /* Write some of the loan off */
        //theTransBuilder.date("06-Jun-1986").category(MoneyWiseTestCategories.idTC_LoanWriteDown)
        //        .pair(MoneyWiseTestAccounts.idLN_DamageLoan, MoneyWiseTestAccounts.idLN_DamageLoan)
        //        .amount("500").build();

        /* Create a mortgage */
        //theTransBuilder.date("07-Jun-1986").category(MoneyWiseTestCategories.idTC_Transfer)
        //        .pair(MoneyWiseTestAccounts.idLN_BarclaysMortgage, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
        //        .amount("10000").build();

        /* Make a mortgage payment */
        //theTransBuilder.date("08-Jun-1986").category(MoneyWiseTestCategories.idTC_Transfer)
        //        .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idLN_BarclaysMortgage)
        //        .amount("1000").build();

        /* Handle Mortgage interest */
        //theTransBuilder.date("09-Jun-1986").category(MoneyWiseTestCategories.idTC_MortgageInterest)
        //        .pair(MoneyWiseTestAccounts.idLN_BarclaysMortgage, MoneyWiseTestAccounts.idLN_BarclaysMortgage)
        //        .amount("500").taxCredit("100").build();

        /* Create a peer2Peer portfolio */
        //theTransBuilder.date("10-Jun-1986").category(MoneyWiseTestCategories.idTC_Transfer)
        //        .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idDP_FundingCircleLoans)
        //        .amount("1000").build();

        /* Create a peer2Peer capitalGainsCharge */
        //theTransBuilder.date("11-Jun-1986").category(MoneyWiseTestCategories.idTC_ChgBadDebtCap)
        //        .pair(MoneyWiseTestAccounts.idDP_FundingCircleLoans, MoneyWiseTestAccounts.idPY_FundingCircle)
        //        .amount("50").build();

        /* Create a peer2Peer interestCharge */
        //theTransBuilder.date("12-Jun-1986").category(MoneyWiseTestCategories.idTC_ChgBadDebtCap)
        //        .pair(MoneyWiseTestAccounts.idDP_FundingCircleLoans, MoneyWiseTestAccounts.idPY_FundingCircle)
        //        .amount("100").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple transfers.
     * @throws OceanusException on error
     */
    private void createAutoExpenses() throws OceanusException {
        /* A simple expense to auto-expense */
        theTransBuilder.date("01-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idDP_BarclaysCurrent, MoneyWiseTestAccounts.idCS_Cash)
                .amount("21.95").build();

        /* A simple refunded auto-expense */
        theTransBuilder.date("02-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idCS_Cash, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("9.99").build();

        /* A simple auto-expense from non-standard currency */
        theTransBuilder.date("03-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopClothes)
                .pair(MoneyWiseTestAccounts.idDP_StarlingEuro, MoneyWiseTestAccounts.idCS_EurosCash)
                .amount("17.1").build();

        /* A simple refunded auto-expense from non-standard currency */
        theTransBuilder.date("04-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopClothes)
                .pair(MoneyWiseTestAccounts.idCS_EurosCash, MoneyWiseTestAccounts.idDP_StarlingEuro)
                .amount("500").partnerAmount("550").build();

        /* A simple transferred expense from auto-expense */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idCS_Cash, MoneyWiseTestAccounts.idPY_CoOp)
                .amount("2.95").build();

        /* A simple transferred expense to auto-expense */
        theTransBuilder.date("65-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idPY_CoOp, MoneyWiseTestAccounts.idCS_Cash)
                .amount("0.95").build();

        /* A simple transferred expense from non-standard currency auto-expense */
        theTransBuilder.date("05-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idCS_EurosCash, MoneyWiseTestAccounts.idPY_CoOp)
                .amount("2.95").build();

        /* A simple transferred expense to non-standard currency auto-expense */
        theTransBuilder.date("65-Jun-1987").category(MoneyWiseTestCategories.idTC_ShopFood)
                .pair(MoneyWiseTestAccounts.idPY_CoOp, MoneyWiseTestAccounts.idCS_EurosCash)
                .amount("0.95").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple incomes.
     * @throws OceanusException on error
     */
    private void createIncomes() throws OceanusException {
        /* A simple salary income */
        theTransBuilder.date("01-Jun-1988").category(MoneyWiseTestCategories.idTC_Salary)
                .pair(MoneyWiseTestAccounts.idPY_IBM, MoneyWiseTestAccounts.idDP_BarclaysCurrent)
                .amount("1000.23").taxCredit("10.71")
                .employeesNI("60.89").employersNI("20.56").benefit("50.83").build();

        /* A simple interest income */
        //theTransBuilder.date("02-Jun-1988").category(MoneyWiseTestCategories.idTC_Interest)
        //        .pair(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, MoneyWiseTestAccounts.idDP_NatWideLoyalty)
        //        .amount("199.99").taxCredit("20.34").withheld("10.78").build();

        /* A simple cashback income */
        //theTransBuilder.date("03-Jun-1988").category(MoneyWiseTestCategories.idTC_CashBack)
        //        .pair(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, MoneyWiseTestAccounts.idDP_NatWideLoyalty)
        //        .amount("25").build();

        /* A simple dividend income */
        //theTransBuilder.date("04-Jun-1988").category(MoneyWiseTestCategories.idTC_Dividend)
        //        .pair(MoneyWiseTestAccounts.idPY_IBM, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
        //        .amount("125").taxCredit("20").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Create simple share buy/sell.
     * @throws OceanusException on error
     */
    private void createShareBuySell() throws OceanusException {
        /* A simple inherited holding */
        theTransBuilder.date("01-Jun-1989").category(MoneyWiseTestCategories.idTC_Inheritance)
                .pair(MoneyWiseTestAccounts.idPY_Parents, MoneyWiseTestAccounts.idSH_BarclaysShares)
                .amount("1000").accountUnits("100").build();

        /* A simple share purchase */
        theTransBuilder.date("02-Jun-1989").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idDP_NatWideFlexDirect, MoneyWiseTestAccounts.idSH_BarclaysShares)
                .amount("750").partnerUnits("25").build();

        /* A simple dividend income */
        theTransBuilder.date("03-Jun-1988").category(MoneyWiseTestCategories.idTC_Dividend)
                .pair(MoneyWiseTestAccounts.idSH_BarclaysShares, MoneyWiseTestAccounts.idDP_NatWideLoyalty)
                .amount("25").taxCredit("5").build();

        /* A re-invested dividend income */
        theTransBuilder.date("04-Jun-1988").category(MoneyWiseTestCategories.idTC_Dividend)
                .pair(MoneyWiseTestAccounts.idSH_BarclaysShares, MoneyWiseTestAccounts.idSH_BarclaysShares)
                .amount("125").taxCredit("20").partnerUnits("3").build();

        /* A full transfer out */
        theTransBuilder.date("05-Jun-1988").category(MoneyWiseTestCategories.idTC_Transfer)
                .pair(MoneyWiseTestAccounts.idSH_BarclaysShares, MoneyWiseTestAccounts.idDP_NatWideFlexDirect)
                .amount("1500").accountUnits("-128").build();

        /* Resolve the transactions */
        theData.getTransactions().resolveDataSetLinks();
    }

    /**
     * Check account valuation.
     * @param pAccount the account
     * @param pValue the expected value
     */
    private void checkAccountValue(final String pAccount,
                                   final String pValue) {
        /* Obtain the value */
        final OceanusMoney myAmount = new OceanusMoney(pValue);
        final MoneyWiseAssetBase myAsset = (MoneyWiseAssetBase) theTransBuilder.resolveTransactionAsset(pAccount);
        final MoneyWiseXAnalysisAccountBucket<?> myBucket = getAccountBucket(myAsset);
        Assertions.assertEquals(myAmount, myBucket.getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUATION),
                "Bad total for " + pAccount);
    }

    /**
     * Check payee income/expense.
     * @param pPayee the payee
     * @param pIncome the expected income
     * @param pExpense the expected expense
     */
    private void checkPayeeValue(final String pPayee,
                                 final String pIncome,
                                 final String pExpense) {
        /* Obtain the value */
        final OceanusMoney myIncome = new OceanusMoney(pIncome);
        final OceanusMoney myExpense = new OceanusMoney(pExpense);
        final MoneyWiseAssetBase myAsset = (MoneyWiseAssetBase) theTransBuilder.resolveTransactionAsset(pPayee);
        final MoneyWiseXAnalysisPayeeBucket myBucket = theAnalysis.getPayees().getBucket(myAsset);
        Assertions.assertEquals(myIncome, myBucket.getValues().getMoneyValue(MoneyWiseXAnalysisPayeeAttr.INCOME),
                "Bad income for " + pPayee);
        Assertions.assertEquals(myExpense, myBucket.getValues().getMoneyValue(MoneyWiseXAnalysisPayeeAttr.EXPENSE),
                "Bad expense for " + pPayee);
    }

    /**
     * Check category income/expense.
     * @param pCategory the category
     * @param pIncome the expected income
     * @param pExpense the expected expense
     */
    private void checkCategoryValue(final String pCategory,
                                    final String pIncome,
                                    final String pExpense) {
        /* Obtain the value */
        final OceanusMoney myIncome = new OceanusMoney(pIncome);
        final OceanusMoney myExpense = new OceanusMoney(pExpense);
        final MoneyWiseTransCategory myCategory = theAnalysis.getData().getTransCategories().findItemByName(pCategory);
        final MoneyWiseXAnalysisTransCategoryBucket myBucket = theAnalysis.getTransCategories().getBucket(myCategory);
        Assertions.assertEquals(myIncome, myBucket.getValues().getMoneyValue(MoneyWiseXAnalysisTransAttr.INCOME),
                "Bad income for " + pCategory);
        Assertions.assertEquals(myExpense, myBucket.getValues().getMoneyValue(MoneyWiseXAnalysisTransAttr.EXPENSE),
                "Bad expense for " + pCategory);
    }

    /**
     * Check taxBasis value.
     * @param pTaxBasis the taxBasis
     * @param pValue the expected value
     */
    private void checkTaxBasisValue(final MoneyWiseTaxClass pTaxBasis,
                                    final String pValue) {
        /* Obtain the value */
        final OceanusMoney myAmount = new OceanusMoney(pValue);
        final MoneyWiseXAnalysisTaxBasisBucket myBucket = theAnalysis.getTaxBasis().getBucket(pTaxBasis);
        Assertions.assertEquals(myAmount, myBucket.getValues().getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.NETT),
                "Bad value for " + pTaxBasis);
    }

    /**
     * Obtain Account bucket for asset.
     * @param pAsset the asset
     * @return the bucket
     */
    private MoneyWiseXAnalysisAccountBucket<?> getAccountBucket(final MoneyWiseAssetBase pAsset) {
        switch (pAsset.getAssetType()) {
            case DEPOSIT:
                return theAnalysis.getDeposits().getBucket((MoneyWiseDeposit) pAsset);
            case CASH:
                return theAnalysis.getCash().getBucket((MoneyWiseCash) pAsset);
            case LOAN:
                return theAnalysis.getLoans().getBucket((MoneyWiseLoan) pAsset);
            case PORTFOLIO:
                return theAnalysis.getPortfolios().getCashBucket((MoneyWisePortfolio) pAsset);
            default:
                throw new IllegalArgumentException();
        }
    }
}
