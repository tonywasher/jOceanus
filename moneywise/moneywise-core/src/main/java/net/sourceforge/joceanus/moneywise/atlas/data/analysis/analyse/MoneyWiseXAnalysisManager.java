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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisBucketResource;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashBucket.MoneyWiseXAnalysisCashBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisCashCategoryBucket.MoneyWiseXAnalysisCashCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositBucket.MoneyWiseXAnalysisDepositBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisDepositCategoryBucket.MoneyWiseXAnalysisDepositCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanBucket.MoneyWiseXAnalysisLoanBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisLoanCategoryBucket.MoneyWiseXAnalysisLoanCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket.MoneyWiseXAnalysisPayeeBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPortfolioBucket.MoneyWiseXAnalysisPortfolioBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransTagBucket.MoneyWiseXAnalysisTransTagBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.values.MoneyWiseXAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.HashMap;
import java.util.Map;

/**
 * Analysis manager.
 */
public class MoneyWiseXAnalysisManager
        implements TethysEventProvider<PrometheusDataEvent>,  MetisFieldItem, MetisDataMap<OceanusDateRange, MoneyWiseXAnalysis> {
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
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The analysis map.
     */
    private final Map<OceanusDateRange, MoneyWiseXAnalysis> theAnalysisMap;

    /**
     * The base analysis.
     */
    private MoneyWiseXAnalysis theAnalysis;

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

        /* Create the event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the analysis map */
        theAnalysisMap = new HashMap<>();
    }

    @Override
    public MetisFieldSet<MoneyWiseXAnalysisManager> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
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
     * analysis on editSet change.
     * @param pView the view
     * @param pEditSet the editSet
     */
    private void analyseChangedData(final MoneyWiseView pView,
                                    final PrometheusEditSet pEditSet) {
        /* Obtain the active profile */
        OceanusProfile myTask = pView.getActiveTask();
        myTask = myTask.startTask("calculateAnalysis");

        /* Protect against exceptions */
        try {
            /* Sort the transaction list */
            myTask.startTask("sortTransactions");
            pEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class).reSort();

            /* TODO initialise analysis */

            /* Create a new analysis on the editSet */
            final MoneyWiseXAnalysisEventAnalyser myAnalyser = new MoneyWiseXAnalysisEventAnalyser(myTask, pEditSet, pView.getPreferenceManager());

            /* Record analysis and clear the map */
            theAnalysis = myAnalyser.getAnalysis();
            theAnalysisMap.clear();

            /* Report to listeners */
            theEventManager.fireEvent(PrometheusDataEvent.DATACHANGED);

            /* Catch exceptions */
        } catch (OceanusException e) {
            LOGGER.error("Failed to analyse changes", e);
        }
    }

    /**
     * analyse new data.
     * @param pView the view
     * @param pData the new data
     * @return the analysis
     * @throws OceanusException on error
     */
    public MoneyWiseXAnalysis analyseNewData(final MoneyWiseView pView,
                                             final MoneyWiseDataSet pData) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = pView.getActiveTask();
        myTask = myTask.startTask("calculateAnalysis");

        /* Create a dummy editSet TODO unlink from View */
        final PrometheusEditSet myEditSet = new PrometheusEditSet(pView);

        /* Initialise the analysis */
        pData.initialiseAnalysis();

        /* Create a new analysis on the editSet */
        final MoneyWiseXAnalysisEventAnalyser myAnalyser = new MoneyWiseXAnalysisEventAnalyser(myTask, myEditSet, pView.getPreferenceManager());

        /* post-process analysis */
        myAnalyser.postProcessAnalysis();

        /* return the analysis */
        return myAnalyser.getAnalysis();
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis
     */
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        /* Record analysis and clear the map */
        theAnalysis = pAnalysis;
        theAnalysisMap.clear();

        /* Report to listeners */
        theEventManager.fireEvent(PrometheusDataEvent.DATACHANGED);
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
            myAnalysis = new MoneyWiseXAnalysis(theAnalysis, pDate);
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
            final MoneyWiseXAnalysis myAnalysis = new MoneyWiseXAnalysis(theAnalysis, r);
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
