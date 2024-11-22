/*******************************************************************************
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket.MoneyWiseXAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket.MoneyWiseXAnalysisTransTagBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Analysis manager.
 */
public class MoneyWiseXAnalysisManager
        implements MetisFieldItem, MetisDataMap<OceanusDateRange, MoneyWiseXAnalysis> {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseXAnalysisManager.class);

    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisManager> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisManager.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBucketResource.ANALYSIS_NAME, MoneyWiseXAnalysisManager::getAnalysis);
    }

    /**
     * The analysis map.
     */
    private final Map<OceanusDateRange, MoneyWiseXAnalysis> theAnalysisMap;

    /**
     * The base analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

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
     * @param pAnalysis the new analysis
     */
    public MoneyWiseXAnalysisManager(final MoneyWiseXAnalysis pAnalysis) {
        /* Store the parameters */
        theAnalysis = pAnalysis;

        /* Create the analysis map */
        theAnalysisMap = new HashMap<>();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisManager> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    @Override
    public Map<OceanusDateRange, MoneyWiseXAnalysis> getUnderlyingMap() {
        return theAnalysisMap;
    }

    /**
     * Is the analysis manager idle?
     * @return true/false
     */
    public boolean isIdle() {
        return theAnalysis.isIdle();
    }

    /**
     * Obtain the base analysis.
     * @return the base analysis
     */
    public MoneyWiseXAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Do we have a foreign currency?
     * @return true/false
     */
    public Boolean haveForeignCurrency() {
        return haveForeignCurrency;
    }

    /**
     * Do we have active securities?
     * @return true/false
     */
    public Boolean haveActiveSecurities() {
        return haveActiveSecurities;
    }

    /**
     * Obtain an analysis for a date.
     * @param pDate the date for the analysis.
     * @return the analysis
     */
    public MoneyWiseXAnalysis getDatedAnalysis(final OceanusDate pDate) {
        /* Create the new Range */
        final OceanusDateRange myRange = new OceanusDateRange(null, pDate);

        /* Look for the existing analysis */
        MoneyWiseXAnalysis myAnalysis = theAnalysisMap.get(myRange);
        if (myAnalysis == null) {
            /* Create the new event analysis */
            myAnalysis = new MoneyWiseXAnalysis(this, pDate);
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
     * @param pRange the date range for the analysis.
     * @return the analysis
     */
    public MoneyWiseXAnalysis getRangedAnalysis(final OceanusDateRange pRange) {
        /* Look for the existing analysis */
        return theAnalysisMap.computeIfAbsent(pRange, r -> {
            /* Create the new event analysis */
            final MoneyWiseXAnalysis myAnalysis = new MoneyWiseXAnalysis(this, r);
            produceTotals(myAnalysis);

            /* Check the totals */
            checkTotals(myAnalysis);

            /* Put it into the map */
            return myAnalysis;
        });
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
     * @param pAnalysis the analysis.
     */
    private void produceTotals(final MoneyWiseXAnalysis pAnalysis) {
        /* Analyse the deposits */
        final MoneyWiseXAnalysisDepositBucketList myDeposits = pAnalysis.getDeposits();
        final MoneyWiseXAnalysisDepositCategoryBucketList myDepositCategories = pAnalysis.getDepositCategories();
        myDepositCategories.analyseDeposits(myDeposits);
        myDepositCategories.produceTotals();
        haveForeignCurrency = myDepositCategories.haveForeignCurrency();

        /* Analyse the cash */
        final MoneyWiseXAnalysisCashBucketList myCash = pAnalysis.getCash();
        final MoneyWiseXAnalysisCashCategoryBucketList myCashCategories = pAnalysis.getCashCategories();
        myCashCategories.analyseCash(myCash);
        myCashCategories.produceTotals();
        haveForeignCurrency |= myCashCategories.haveForeignCurrency();

        /* Analyse the loans */
        final MoneyWiseXAnalysisLoanBucketList myLoans = pAnalysis.getLoans();
        final MoneyWiseXAnalysisLoanCategoryBucketList myLoanCategories = pAnalysis.getLoanCategories();
        myLoanCategories.analyseLoans(myLoans);
        myLoanCategories.produceTotals();
        haveForeignCurrency |= myLoanCategories.haveForeignCurrency();

        /* Analyse the securities */
        final MoneyWiseXAnalysisPortfolioBucketList myPortfolios = pAnalysis.getPortfolios();
        myPortfolios.analyseSecurities();
        haveForeignCurrency |= myPortfolios.haveForeignCurrency();
        haveActiveSecurities = myPortfolios.haveActiveSecurities();

        /* Analyse the Payees */
        final MoneyWiseXAnalysisPayeeBucketList myPayees = pAnalysis.getPayees();
        myPayees.produceTotals();

        /* Analyse the TransactionCategories */
        final MoneyWiseXAnalysisTransCategoryBucketList myTransCategories = pAnalysis.getTransCategories();
        myTransCategories.produceTotals();

        /* Analyse the TaxBasis */
        final MoneyWiseXAnalysisTaxBasisBucketList myTaxBasis = pAnalysis.getTaxBasis();
        myTaxBasis.produceTotals();

        /* Sort the transaction Tag list */
        final MoneyWiseXAnalysisTransTagBucketList myTags = pAnalysis.getTransactionTags();
        myTags.sortBuckets();
    }

    /**
     * Check totals for an analysis.
     * @param pAnalysis the analysis to check
     */
    private static void checkTotals(final MoneyWiseXAnalysis pAnalysis) {
        /* Obtain Totals bucket */
        final MoneyWiseXAnalysisDepositCategoryBucket myDepCat = pAnalysis.getDepositCategories().getTotals();
        final MoneyWiseXAnalysisCashCategoryBucket myCashCat = pAnalysis.getCashCategories().getTotals();
        final MoneyWiseXAnalysisLoanCategoryBucket myLoanCat = pAnalysis.getLoanCategories().getTotals();
        final MoneyWiseXAnalysisPortfolioBucket myPort = pAnalysis.getPortfolios().getTotals();
        final MoneyWiseXAnalysisPayeeBucket myPayee = pAnalysis.getPayees().getTotals();
        final MoneyWiseXAnalysisTransCategoryBucket myTrans = pAnalysis.getTransCategories().getTotals();
        final MoneyWiseXAnalysisTaxBasisBucket myTax = pAnalysis.getTaxBasis().getTotals();

        /* Handle null data */
        if (myDepCat == null) {
            return;
        }

        /* Access totals */
        OceanusMoney myDepTotal = myDepCat.getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA);
        final OceanusMoney myCashTotal = myCashCat.getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA);
        final OceanusMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(MoneyWiseXAnalysisAccountAttr.VALUEDELTA);
        final OceanusMoney myPortTotal = myPort.getValues().getMoneyValue(MoneyWiseXAnalysisSecurityAttr.VALUEDELTA);
        final OceanusMoney myPayTotal = myPayee.getValues().getMoneyValue(MoneyWiseXAnalysisPayeeAttr.PROFIT);
        final OceanusMoney myEvtTotal = myTrans.getValues().getMoneyValue(MoneyWiseXAnalysisTransAttr.PROFIT);
        final OceanusMoney myTaxTotal = myTax.getValues().getMoneyValue(MoneyWiseXAnalysisTaxBasisAttr.GROSS);

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
