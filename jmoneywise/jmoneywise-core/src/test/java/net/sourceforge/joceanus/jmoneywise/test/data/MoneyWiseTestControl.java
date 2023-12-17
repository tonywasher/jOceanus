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

import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
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
     * Create the localData tests.
     * @param pToolkit the toolkit
     * @return the testStream
     */
    private Stream<DynamicNode> localDataTests(final PrometheusToolkit pToolkit) {
        /* Create the stream */
        final MoneyWiseDataSet myData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("initData", () -> initLocalData(myData, pToolkit)));
        myStream = Stream.concat(myStream, storageTests(myData, pToolkit));

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
