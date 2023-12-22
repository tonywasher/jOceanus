/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.test.data;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisAccountAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisCashCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisDepositCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisLoanCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisPortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisSecurityAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTaxBasisAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransAttr;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.jtethys.ui.helper.TethysUIHelperFactory;

/**
 * Test security.
 */
public class MoneyWiseTestControl {
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

        /* Create tests */
        Stream<DynamicNode> myStream = localDataTests(myToolkit);
        return Stream.concat(myStream, archiveDataTests(myToolkit));
    }

    /**
     * Populate local data.
     * @param pData the dataSet to populate
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public void initLocalData(final MoneyWiseDataSet pData,
                              final PrometheusToolkit pToolkit) throws OceanusException {
        /* Initialise the data */
        new MoneyWiseTestCategories(pData).buildBasic();
        new MoneyWiseTestAccounts(pData).createAccounts();
        new MoneyWiseTestTransactions(pData).createTransfers();
        new MoneyWiseTestSecurity(pData).initSecurity(pToolkit);
    }


    /**
     * Analyse the data.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public void analyseData(final MoneyWiseDataSet pData,
                            final PrometheusToolkit pToolkit) throws OceanusException {
        /* Initialise the analysis */
        pData.initialiseAnalysis();

        /* Create the analysis */
        final TethysProfile myTask = new TethysProfile("Dummy");
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, pData, pToolkit.getPreferenceManager());

        /* Post process the analysis */
        myAnalyser.postProcessAnalysis();

        /* Access the analysis */
        final MoneyWiseAnalysis myAnalysis = myAnalyser.getAnalysis();

        /* Obtain deposit totals */
        final MoneyWiseAnalysisDepositCategoryBucket myDepCat = myAnalysis.getDepositCategories().getTotals();
        TethysMoney myDepTotal = myDepCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        myDepTotal = myDepTotal == null ? new TethysMoney() : new TethysMoney(myDepTotal);

        /* Add in cash totals */
        final MoneyWiseAnalysisCashCategoryBucket myCashCat = myAnalysis.getCashCategories().getTotals();
        final TethysMoney myCashTotal = myCashCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        if (myCashTotal != null)  {
            myDepTotal.addAmount(myCashTotal);
        }

        /* Add in loan totals */
        final MoneyWiseAnalysisLoanCategoryBucket myLoanCat = myAnalysis.getLoanCategories().getTotals();
        final TethysMoney myLoanTotal = myLoanCat.getValues().getMoneyValue(MoneyWiseAnalysisAccountAttr.VALUEDELTA);
        if (myLoanTotal != null) {
            myDepTotal.addAmount(myLoanTotal);
        }

        /* Add in portfolio totals */
        final MoneyWiseAnalysisPortfolioBucket myPort = myAnalysis.getPortfolios().getTotals();
        final TethysMoney myPortTotal = myPort.getValues().getMoneyValue(MoneyWiseAnalysisSecurityAttr.VALUEDELTA);
        if (myPortTotal != null) {
            myDepTotal.addAmount(myPortTotal);
        }

        /* Validate Payee totals */
        final MoneyWiseAnalysisPayeeBucket myPayeeTotals = myAnalysis.getPayees().getTotals();
        TethysMoney myPayTotal = myPayeeTotals.getValues().getMoneyValue(MoneyWiseAnalysisPayeeAttr.PROFIT);
        if (myPayTotal == null) {
            myPayTotal = new TethysMoney();
        }
        Assertions.assertEquals(myDepTotal, myPayTotal, "Payee total mismatch");

        /* Validate transaction totals */
        final MoneyWiseAnalysisTransCategoryBucket myTransTotals = myAnalysis.getTransCategories().getTotals();
        TethysMoney myEvtTotal = myTransTotals.getValues().getMoneyValue(MoneyWiseAnalysisTransAttr.PROFIT);
        if (myEvtTotal == null) {
            myEvtTotal = new TethysMoney();
        }
        Assertions.assertEquals(myDepTotal, myEvtTotal, "Transaction total mismatch");

        /* Validate tax totals */
        final MoneyWiseAnalysisTaxBasisBucket myTaxTotals = myAnalysis.getTaxBasis().getTotals();
        final TethysMoney myTaxTotal = myTaxTotals.getValues().getMoneyValue(MoneyWiseAnalysisTaxBasisAttr.GROSS);
        Assertions.assertEquals(myDepTotal, myTaxTotal, "TaxBasis total mismatch");
    }

    /**
     * Create the localData tests.
     * @param pToolkit the toolkit
     * @return the testStream
     */
    private Stream<DynamicNode> localDataTests(final PrometheusToolkit pToolkit) {
        /* Create the stream */
        final MoneyWiseDataSet myData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData", () -> initLocalData(myData, pToolkit)));
        myStream = Stream.concat(myStream, storageTests(myData, pToolkit));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("analyseData", () -> analyseData(myData, pToolkit))));

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer("localData", myStream));
    }

    /**
     * Create the archiveData tests.
     * @param pToolkit the toolkit
     * @return the testStream
     */
    private Stream<DynamicNode> archiveDataTests(final PrometheusToolkit pToolkit) {
        /* Create the stream */
        final MoneyWiseDataSet myData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());
        final TethysUIThreadManager myThreadMgr = new NullThreadMgr();
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData", () -> new MoneyWiseTestArchiveFile(myThreadMgr).performTest(myData, pToolkit)));
        myStream = Stream.concat(myStream, storageTests(myData, pToolkit));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("analyseData", () -> analyseData(myData, pToolkit))));

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer("archiveData", myStream));
    }

    /**
     * Create the storage tests.
     * @param pData the dataSet to populate
     * @param pToolkit the toolkit
     * @return the testStream
     */
    private Stream<DynamicNode> storageTests(final MoneyWiseDataSet pData,
                                             final PrometheusToolkit pToolkit) {
        /* Return the stream */
        final TethysUIThreadManager myThreadMgr = new NullThreadMgr();
        return Stream.of(DynamicContainer.dynamicContainer("Storage Tests", Stream.of(
                DynamicTest.dynamicTest("XML File", () -> new MoneyWiseTestXMLFile(myThreadMgr).performTest(pData, pToolkit)),
                DynamicTest.dynamicTest("ODS File", () -> new MoneyWiseTestODSFile(myThreadMgr).performTest(pData, pToolkit)),
                DynamicTest.dynamicTest("dataBase", () -> new MoneyWiseTestDatabase(myThreadMgr).performTest(pData, pToolkit)))));
    }

    /**
     * ThreadManager stub.
     */
    static class NullThreadMgr
            implements TethysUIThreadManager {
        /**
         * The active task.
         */
        private final TethysProfile theProfile;

        /**
         * Constructor.
         */
        NullThreadMgr() {
            theProfile = new TethysProfile("Dummy");
        }

        @Override
        public TethysEventRegistrar<TethysUIThreadEvent> getEventRegistrar() {
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
        public TethysProfile getActiveProfile() {
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
        public TethysProfile getActiveTask() {
            return theProfile;
        }
    }
}
