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
package net.sourceforge.joceanus.moneywise.test.data;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalysisBuilder;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.values.MoneyWiseAnalysisTransAttr;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.moneywise.test.data.trans.MoneyWiseDataTestRunner;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.tethys.helper.TethysUIHelperFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * Test security.
 */
public class MoneyWiseDataTest {
    /**
     * Create the keySet test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> dataTests() throws OceanusException {
        /* Create the data */
        final TethysUIHelperFactory myFactory = new TethysUIHelperFactory();
        final PrometheusToolkit myToolkit = new PrometheusToolkit(myFactory);
        final MoneyWiseView myView = new MoneyWiseView(myToolkit, new MoneyWiseUKTaxYearCache());

        /* Create tests */
        Stream<DynamicNode> myStream = MoneyWiseDataTestRunner.createTests(myView);
        myStream = Stream.concat(myStream, localDataTests(myView));
        return Stream.concat(myStream, archiveDataTests(myView));
    }

    /**
     * Populate local data.
     * @param pData the dataSet to populate
     * @param pView the view
     * @throws OceanusException on error
     */
    public void initLocalData(final MoneyWiseDataSet pData,
                              final MoneyWiseView pView) throws OceanusException {
        /* Initialise the data */
        new MoneyWiseTestCategories(pData).buildBasic();
        new MoneyWiseTestAccounts(pData).createAccounts();
        new MoneyWiseTestTransactions(pData).buildTransactions();
        new MoneyWiseTestSecurity(pData).initSecurity(pView.getToolkit());
    }

    /**
     * Populate local data.
     * @param pData the dataSet to populate
     * @param pView the view
     * @throws OceanusException on error
     */
    public void checkEditSet(final MoneyWiseDataSet pData,
                             final MoneyWiseView pView) throws OceanusException {
        /* Initialise the data */
        new MoneyWiseTestEditSet(pData).checkSeparateEditSets(pView);
        new MoneyWiseTestEditSet(pData).checkCombinedEditSet(pView);
    }

    /**
     * Analyse the data.
     * @param pData the dataSet to analyse
     * @param pView the view
     * @throws OceanusException on error
     */
    public void analyseData(final MoneyWiseDataSet pData,
                            final MoneyWiseView pView) throws OceanusException {
        /* Update the maps */
        pData.updateMaps();

        /* Create the analysis */
        final OceanusProfile myTask = pView.getNewProfile("Dummy");
        pView.setData(pData);
        final PrometheusEditSet myEditSet = new PrometheusEditSet(pView);
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, myEditSet, pView.getPreferenceManager());

        /* Post process the analysis */
        myAnalyser.postProcessAnalysis();

        /* Access the analysis */
        final MoneyWiseAnalysis myAnalysis = myAnalyser.getAnalysis();
        final MoneyWiseAnalysisManager myAnalysisMgr = new MoneyWiseAnalysisManager(myAnalysis);
        myAnalysisMgr.analyseBase();

        /* Obtain deposit totals */
        final MoneyWiseAnalysisDepositCategoryBucket myDepCat = myAnalysis.getDepositCategories().getTotals();
        OceanusMoney myDepTotal = myDepCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        myDepTotal = myDepTotal == null ? new OceanusMoney() : new OceanusMoney(myDepTotal);

        /* Add in cash totals */
        final MoneyWiseAnalysisCashCategoryBucket myCashCat = myAnalysis.getCashCategories().getTotals();
        final OceanusMoney myCashTotal = myCashCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        if (myCashTotal != null)  {
            myDepTotal.addAmount(myCashTotal);
        }

        /* Add in loan totals */
        final MoneyWiseAnalysisLoanCategoryBucket myLoanCat = myAnalysis.getLoanCategories().getTotals();
        final OceanusMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        if (myLoanTotal != null) {
            myDepTotal.addAmount(myLoanTotal);
        }

        /* Add in portfolio totals */
        final MoneyWiseAnalysisPortfolioBucket myPort = myAnalysis.getPortfolios().getTotals();
        final OceanusMoney myPortTotal = myPort.getValues().getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        if (myPortTotal != null) {
            myDepTotal.addAmount(myPortTotal);
        }

        /* Validate Payee totals */
        final MoneyWiseAnalysisPayeeBucket myPayeeTotals = myAnalysis.getPayees().getTotals();
        OceanusMoney myPayTotal = myPayeeTotals.getValues().getMoneyValue(MoneyWiseAnalysisPayeeAttr.PROFIT);
        if (myPayTotal == null) {
            myPayTotal = new OceanusMoney();
        }
        Assertions.assertEquals(myDepTotal, myPayTotal, "Payee total mismatch");

        /* Validate transaction totals */
        final MoneyWiseAnalysisTransCategoryBucket myTransTotals = myAnalysis.getTransCategories().getTotals();
        OceanusMoney myEvtTotal = myTransTotals.getValues().getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT);
        if (myEvtTotal == null) {
            myEvtTotal = new OceanusMoney();
        }
        Assertions.assertEquals(myDepTotal, myEvtTotal, "Transaction total mismatch");

        /* Validate tax totals */
        final MoneyWiseAnalysisTaxBasisBucket myTaxTotals = myAnalysis.getTaxBasis().getTotals();
        final OceanusMoney myTaxTotal = myTaxTotals.getValues().getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        Assertions.assertEquals(myDepTotal, myTaxTotal, "TaxBasis total mismatch");
    }

    /**
     * Analyse the data.
     * @param pData the dataSet to analyse
     * @param pView the view
     * @throws OceanusException on error
     */
    public void analyseXData(final MoneyWiseDataSet pData,
                             final MoneyWiseView pView) throws OceanusException {
        /* Create the analyser */
        pView.getNewProfile("AnalyseXData");
        final MoneyWiseXAnalysisBuilder myBuilder = new MoneyWiseXAnalysisBuilder(pView);
        final MoneyWiseXAnalysis myAnalysis = myBuilder.analyseNewData(pData);
        new MoneyWiseTestTransactions(pData).checkAnalysis(myAnalysis);
    }

    /**
     * Create the localData tests.
     * @param pView the view
     * @return the testStream
     */
    private Stream<DynamicNode> localDataTests(final MoneyWiseView pView) {
        /* Create the stream */
        final MoneyWiseDataSet myData = pView.getNewData();
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData", () -> initLocalData(myData, pView)));
        myStream = Stream.concat(myStream, storageTests(myData, pView));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("analyseData", () -> analyseXData(myData, pView))));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("editSet", () -> checkEditSet(myData, pView))));

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer("localData", myStream));
    }

    /**
     * Create the archiveData tests.
     * @param pView the view
     * @return the testStream
     */
    private Stream<DynamicNode> archiveDataTests(final MoneyWiseView pView) {
        /* Create the stream */
        final MoneyWiseDataSet myData = pView.getNewData();
        final TethysUIThreadManager myThreadMgr = new NullThreadMgr();
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData",
                () -> new MoneyWiseTestArchiveFile(myThreadMgr).performTest(myData, pView)));
        myStream = Stream.concat(myStream, storageTests(myData, pView));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("analyseData", () -> analyseData(myData, pView))));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("editSet", () -> checkEditSet(myData, pView))));

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer("archiveData", myStream));
    }

    /**
     * Create the storage tests.
     * @param pData the dataSet to populate
     * @param pView the view
     * @return the testStream
     */
    private Stream<DynamicNode> storageTests(final MoneyWiseDataSet pData,
                                             final MoneyWiseView pView) {
        /* Return the stream */
        final TethysUIThreadManager myThreadMgr = new NullThreadMgr();
        return Stream.of(DynamicContainer.dynamicContainer("Storage Tests", Stream.of(
                DynamicTest.dynamicTest("XML File", () -> new MoneyWiseTestXMLFile(myThreadMgr).performTest(pData, pView)),
                DynamicTest.dynamicTest("ODS File", () -> new MoneyWiseTestODSFile(myThreadMgr).performTest(pData, pView)),
                DynamicTest.dynamicTest("dataBase", () -> new MoneyWiseTestDatabase(myThreadMgr).performTest(pData, pView)))));
    }

    /**
     * ThreadManager stub.
     */
    static class NullThreadMgr
            implements TethysUIThreadManager {
        /**
         * The active task.
         */
        private OceanusProfile theProfile;

        /**
         * Constructor.
         */
        NullThreadMgr() {
            setNewProfile("Dummy");
        }

        @Override
        public void setNewProfile(final String pTask) {
            theProfile = new OceanusProfile(pTask);
        }

        @Override
        public OceanusEventRegistrar<TethysUIThreadEvent> getEventRegistrar() {
            return null;
        }

        @Override
        public TethysUIThreadStatusManager getStatusManager() {
            return null;
        }

        @Override
        public String getTaskName() {
            return null;
        }

        @Override
        public boolean hasWorker() {
            return false;
        }

        @Override
        public void setReportingSteps(int pSteps) {

        }

        @Override
        public Throwable getError() {
            return null;
        }

        @Override
        public void setThreadData(Object pThreadData) {

        }

        @Override
        public Object getThreadData() {
            return null;
        }

        @Override
        public <T> void startThread(TethysUIThread<T> pThread) {

        }

        @Override
        public void shutdown() {

        }

        @Override
        public void cancelWorker() {

        }

        @Override
        public OceanusProfile getActiveProfile() {
            return null;
        }

        @Override
        public void initTask(String pTask) throws OceanusException {

        }

        @Override
        public void setNumStages(int pNumStages) throws OceanusException {

        }

        @Override
        public void setNewStage(String pStage) throws OceanusException {

        }

        @Override
        public void setNumSteps(int pNumSteps) throws OceanusException {

        }

        @Override
        public void setStepsDone(int pSteps) throws OceanusException {

        }

        @Override
        public void setNextStep() throws OceanusException {

        }

        @Override
        public void setNextStep(String pStep) throws OceanusException {

        }

        @Override
        public void setCompletion() throws OceanusException {

        }

        @Override
        public void checkForCancellation() throws OceanusException {

        }

        @Override
        public void throwCancelException() throws OceanusException {

        }

        @Override
        public OceanusProfile getActiveTask() {
            return theProfile;
        }
    }
}
