/*******************************************************************************
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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseLoan;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.builder.MoneyWiseTransactionBuilder;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import org.junit.jupiter.api.Assertions;

/**
 * MoneyWise data testCase
 */
public abstract class MoneyWiseDataTestCase {
    /**
     *  The accountBuilder
     */
    private final MoneyWiseDataTestAccounts theBuilder;

    /**
     * TransactionBuilder.
     */
    private final MoneyWiseTransactionBuilder theTransBuilder;

    /**
     * The analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

    /**
     * Constructor.
     */
    MoneyWiseDataTestCase(final MoneyWiseDataTestAccounts pBuilder) {
        /* Store parameters */
        theBuilder = pBuilder;

        /* Access builder */
        theTransBuilder = pBuilder.getTransBuilder();
    }

    /**
     * Obtain testCase name.
     * @return the name
     */
    abstract String getName();

    /**
     * Obtain transaction builder.
     * @return the builder
     */
    MoneyWiseTransactionBuilder getTransBuilder() {
        return theTransBuilder;
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis
     */
    void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        theAnalysis = pAnalysis;
    }

    /**
     * Setup accounts.
     * @throws OceanusException on error
     */
    void setUpAccounts() throws OceanusException {
    }

    /**
     * Define Prices.
     * @throws OceanusException on error
     */
    void definePrices() throws OceanusException {
    }

    /**
     * Define Rates.
     * @throws OceanusException on error
     */
    void defineRates() throws OceanusException {
    }

    /**
     * Define transactions.
     * @throws OceanusException on error
     */
    void defineTransactions() throws OceanusException {
    }

    /**
     * Check analysis.
     */
    void checkAnalysis() {
    }

    /**
     * Create payees.
     * @param pPayees the payees to create
     * @throws OceanusException on error
     */
    void createPayees(final String ...pPayees) throws OceanusException {
        theBuilder.createPayees(pPayees);
    }

    /**
     * Create deposits.
     * @param pDeposits the deposits to create
     * @throws OceanusException on error
     */
    void createDeposits(final String ...pDeposits) throws OceanusException {
        theBuilder.createDeposits(pDeposits);
    }

    /**
     * Create cash.
     * @param pCash the cash to create
     * @throws OceanusException on error
     */
    void createCash(final String ...pCash) throws OceanusException {
        theBuilder.createCash(pCash);
    }

    /**
     * Create loans.
     * @param pLoans the loans to create
     * @throws OceanusException on error
     */
    void createLoans(final String ...pLoans) throws OceanusException {
        theBuilder.createLoans(pLoans);
    }

    /**
     * Create portfolios.
     * @param pPortfolios the portfolios to create
     * @throws OceanusException on error
     */
    void createPortfolios(final String ...pPortfolios) throws OceanusException {
        theBuilder.createPortfolios(pPortfolios);
    }

    /**
     * Create securities.
     * @param pSecurities the securities to create
     * @throws OceanusException on error
     */
    void createSecurities(final String ...pSecurities) throws OceanusException {
        theBuilder.createSecurities(pSecurities);
    }

    /**
     * Create secPrice.
     * @param pSecurity the currency
     * @param pDate the date
     * @param pPrice the price
     * @throws OceanusException on error
     */
    void createSecPrice(final String pSecurity,
                        final String pDate,
                        final String pPrice) throws OceanusException {
        theBuilder.createSecPrice(pSecurity, pDate, pPrice);
    }

    /**
     * Create xchgRate.
     * @param pCurrency the currency
     * @param pDate the date
     * @param pRate the rate
     * @throws OceanusException on error
     */
    void createXchgRate(final MoneyWiseCurrencyClass pCurrency,
                        final String pDate,
                        final String pRate) throws OceanusException {
        theBuilder.createXchgRate(pCurrency, pDate, pRate);
    }

    /**
     * Check account valuation.
     * @param pAccount the account
     * @param pValue the expected value
     */
    void checkAccountValue(final String pAccount,
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
    void checkPayeeValue(final String pPayee,
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
    void checkCategoryValue(final String pCategory,
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
    void checkTaxBasisValue(final MoneyWiseTaxClass pTaxBasis,
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
