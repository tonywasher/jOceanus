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
package io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateRange;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataMap;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashBucket.MoneyWiseAnalysisCashBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket.MoneyWiseAnalysisCashCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositBucket.MoneyWiseAnalysisDepositBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket.MoneyWiseAnalysisDepositCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanBucket.MoneyWiseAnalysisLoanBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket.MoneyWiseAnalysisLoanCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket.MoneyWiseAnalysisPayeeBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket.MoneyWiseAnalysisPortfolioBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket.MoneyWiseAnalysisTaxBasisBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket.MoneyWiseAnalysisTransCategoryBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransTagBucket.MoneyWiseAnalysisTransTagBucketList;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;

import java.util.HashMap;
import java.util.Map;

/**
 * Analysis manager.
 */
public class MoneyWiseAnalysisManager
        implements MetisFieldItem, MetisDataMap<OceanusDateRange, MoneyWiseAnalysis> {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseAnalysisManager.class);

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisManager> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisManager.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisManager::getAnalysis);
    }

    /**
     * The analysis map.
     */
    private final Map<OceanusDateRange, MoneyWiseAnalysis> theAnalysisMap;

    /**
     * The base analysis.
     */
    private final MoneyWiseAnalysis theAnalysis;

    /**
     * Do we have active securities?
     */
    private Boolean haveActiveSecurities = Boolean.FALSE;

    /**
     * Do we have a foreign account?
     */
    private Boolean haveForeignCurrency = Boolean.FALSE;

    /**
     * Constructor.
     *
     * @param pAnalysis the new analysis
     */
    public MoneyWiseAnalysisManager(final MoneyWiseAnalysis pAnalysis) {
        /* Store the parameters */
        theAnalysis = pAnalysis;

        /* Create the analysis map */
        theAnalysisMap = new HashMap<>();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisManager> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    @Override
    public Map<OceanusDateRange, MoneyWiseAnalysis> getUnderlyingMap() {
        return theAnalysisMap;
    }

    /**
     * Is the analysis manager idle?
     *
     * @return true/false
     */
    public boolean isIdle() {
        return theAnalysis.isIdle();
    }

    /**
     * Obtain the base analysis.
     *
     * @return the base analysis
     */
    public MoneyWiseAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Do we have a foreign currency?
     *
     * @return true/false
     */
    public Boolean haveForeignCurrency() {
        return haveForeignCurrency;
    }

    /**
     * Do we have active securities?
     *
     * @return true/false
     */
    public Boolean haveActiveSecurities() {
        return haveActiveSecurities;
    }

    /**
     * Obtain an analysis for a date.
     *
     * @param pDate the date for the analysis.
     * @return the analysis
     */
    public MoneyWiseAnalysis getDatedAnalysis(final OceanusDate pDate) {
        /* Create the new Range */
        final OceanusDateRange myRange = new OceanusDateRange(null, pDate);

        /* Look for the existing analysis */
        MoneyWiseAnalysis myAnalysis = theAnalysisMap.get(myRange);
        if (myAnalysis == null) {
            /* Create the new event analysis */
            myAnalysis = new MoneyWiseAnalysis(this, pDate);
            produceTotals(myAnalysis);

            /* Check the totals */
            checkTotals(myAnalysis);

            /* Put it into the map */
            theAnalysisMap.put(myRange, myAnalysis);
        }

        /* return the analysis */
        return myAnalysis;
    }

    /**
     * Obtain an analysis for a range.
     *
     * @param pRange the date range for the analysis.
     * @return the analysis
     */
    public MoneyWiseAnalysis getRangedAnalysis(final OceanusDateRange pRange) {
        /* Look for the existing analysis */
        MoneyWiseAnalysis myAnalysis = theAnalysisMap.get(pRange);
        if (myAnalysis == null) {
            /* Create the new event analysis */
            myAnalysis = new MoneyWiseAnalysis(this, pRange);
            produceTotals(myAnalysis);

            /* Check the totals */
            checkTotals(myAnalysis);

            /* Put it into the map */
            theAnalysisMap.put(pRange, myAnalysis);
        }

        /* return the analysis */
        return myAnalysis;
    }

    /**
     * Analyse the base analysis.
     */
    public void analyseBase() {
        /* Produce totals for the base analysis */
        produceTotals(theAnalysis);
    }

    /**
     * Produce Totals for an analysis.
     *
     * @param pAnalysis the analysis.
     */
    private void produceTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Create the market analysis */
        final MoneyWiseAnalysisMarket myMarket = new MoneyWiseAnalysisMarket(pAnalysis);

        /* Analyse the deposits */
        final MoneyWiseAnalysisDepositBucketList myDeposits = pAnalysis.getDeposits();
        final MoneyWiseAnalysisDepositCategoryBucketList myDepositCategories = pAnalysis.getDepositCategories();
        myDepositCategories.analyseDeposits(myMarket, myDeposits);
        myDepositCategories.produceTotals();
        haveForeignCurrency = myDepositCategories.haveForeignCurrency();

        /* Analyse the cash */
        final MoneyWiseAnalysisCashBucketList myCash = pAnalysis.getCash();
        final MoneyWiseAnalysisCashCategoryBucketList myCashCategories = pAnalysis.getCashCategories();
        myCashCategories.analyseCash(myMarket, myCash);
        myCashCategories.produceTotals();
        haveForeignCurrency |= myCashCategories.haveForeignCurrency();

        /* Analyse the loans */
        final MoneyWiseAnalysisLoanBucketList myLoans = pAnalysis.getLoans();
        final MoneyWiseAnalysisLoanCategoryBucketList myLoanCategories = pAnalysis.getLoanCategories();
        myLoanCategories.analyseLoans(myMarket, myLoans);
        myLoanCategories.produceTotals();
        haveForeignCurrency |= myLoanCategories.haveForeignCurrency();

        /* Analyse the securities */
        final MoneyWiseAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        myPortfolios.analyseSecurities(myMarket);
        haveForeignCurrency |= myPortfolios.haveForeignCurrency();
        haveActiveSecurities = myPortfolios.haveActiveSecurities();

        /* Propagate market totals */
        myMarket.propagateTotals();

        /* Analyse the Payees */
        final MoneyWiseAnalysisPayeeBucketList myPayees = pAnalysis.getPayees();
        myPayees.produceTotals();

        /* Analyse the TransactionCategories */
        final MoneyWiseAnalysisTransCategoryBucketList myTransCategories = pAnalysis.getTransCategories();
        myTransCategories.produceTotals();

        /* Analyse the TaxBasis */
        final MoneyWiseAnalysisTaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        myTaxBasis.produceTotals();

        /* Sort the transaction Tag list */
        final MoneyWiseAnalysisTransTagBucketList myTags = pAnalysis.getTransactionTags();
        myTags.sortBuckets();
    }

    /**
     * Check totals for an analysis.
     *
     * @param pAnalysis the analysis to check
     */
    private static void checkTotals(final MoneyWiseAnalysis pAnalysis) {
        /* Obtain Totals bucket */
        final MoneyWiseAnalysisDepositCategoryBucket myDepCat = pAnalysis.getDepositCategories().getTotals();
        final MoneyWiseAnalysisCashCategoryBucket myCashCat = pAnalysis.getCashCategories().getTotals();
        final MoneyWiseAnalysisLoanCategoryBucket myLoanCat = pAnalysis.getLoanCategories().getTotals();
        final MoneyWiseAnalysisPortfolioBucket myPort = pAnalysis.getPortfolios().getTotals();
        final MoneyWiseAnalysisPayeeBucket myPayee = pAnalysis.getPayees().getTotals();
        final MoneyWiseAnalysisTransCategoryBucket myTrans = pAnalysis.getTransCategories().getTotals();
        final MoneyWiseAnalysisTaxBasisBucket myTax = pAnalysis.getTaxBasis().getTotals();

        /* Handle null data */
        if (myDepCat == null) {
            return;
        }

        /* Access totals */
        OceanusMoney myDepTotal = myDepCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        final OceanusMoney myCashTotal = myCashCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        final OceanusMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        final OceanusMoney myPortTotal = myPort.getValues().getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        final OceanusMoney myPayTotal = myPayee.getValues().getMoneyValue(MoneyWiseAnalysisPayeeAttr.PROFIT);
        final OceanusMoney myEvtTotal = myTrans.getValues().getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT);
        final OceanusMoney myTaxTotal = myTax.getValues().getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);

        /* Create a copy */
        myDepTotal = new OceanusMoney(myDepTotal);

        /* Add sub-accounts */
        myDepTotal.addAmount(myCashTotal);
        myDepTotal.addAmount(myLoanTotal);
        myDepTotal.addAmount(myPortTotal);

        /* Check identities */
        if (!myDepTotal.equals(myPayTotal)) {
            LOGGER.error("Payee total mismatch");
        }
        if (!myDepTotal.equals(myEvtTotal)) {
            LOGGER.error("TransactionCategory total mismatch");
        }
        if (!myDepTotal.equals(myTaxTotal)) {
            LOGGER.error("TaxBasis total mismatch");
        }
    }
}
