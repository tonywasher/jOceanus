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

import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
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
     * Main entry point.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Create the data */
            final TethysUIHelperFactory myFactory = new TethysUIHelperFactory();
            final PrometheusToolkit myToolkit = new PrometheusToolkit(myFactory);
            final MoneyWiseDataSet myData = new MoneyWiseDataSet(myToolkit, new MoneyWiseUKTaxYearCache());

            /* Initialise the data */
            new MoneyWiseTestCategories(myData).buildBasic();
            new MoneyWiseTestAccounts(myData).createAccounts();
            new MoneyWiseTestSecurity(myData).initSecurity(myToolkit);

            /* Test the XML File creation */
            new MoneyWiseTestXMLFile(new ThreadMgrStub()).performTest(myData, myToolkit);

            /* Test the ODS File creation */
            new MoneyWiseTestODSFile(new ThreadMgrStub()).performTest(myData, myToolkit);

            /* Test the Archive File load */
            new MoneyWiseTestArchiveFile(new ThreadMgrStub()).performTest(myData, myToolkit);

            /* Catch exceptions */
        } catch (OceanusException e) {
            e.printStackTrace();
        }
    }

    /**
     * ThreadManager stub.
     */
    static class ThreadMgrStub
            implements TethysUIThreadManager {
        /**
         * The active task.
         */
        private final TethysProfile theProfile;

        /**
         * Constructor.
         */
        ThreadMgrStub() {
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
