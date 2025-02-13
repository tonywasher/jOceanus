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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityDialogController;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

/**
 * Test security.
 */
public class MoneyWiseTestSecurity {
    /**
     * The DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * Constructor.
     * @param pDataSet the DataSet
     */
    MoneyWiseTestSecurity(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Initialise security.
     * @param pToolkit the toolkit
     */
    public void initSecurity(final PrometheusToolkit pToolkit) throws OceanusException {
        /* Access the Password manager and disable prompting */
        final PrometheusSecurityPasswordManager myManager = theDataSet.getPasswordMgr();
        myManager.setDialogController(new NullPasswordDialog());

        /* Create the cloneSet and initialise security */
        final MoneyWiseDataSet myNullData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());

        /* Create the control data */
        final TethysUIThreadStatusReport myReport = new NullThreadStatusReport();
        theDataSet.getControlData().addNewControl(0);
        theDataSet.initialiseSecurity(myReport, myNullData);
        theDataSet.reBase(myReport, myNullData);
    }

    /**
     * Dialog stub.
     */
    static class NullPasswordDialog
            implements PrometheusSecurityDialogController {
        @Override
        public void createTheDialog(String pTitle, boolean pNeedConfirm) {
        }

        @Override
        public boolean showTheDialog() {
            return true;
        }

        @Override
        public void releaseDialog() {
        }

        @Override
        public char[] getPassword() {
            return "DummyPassword".toCharArray();
        }

        @Override
        public void reportBadPassword() {
        }
    }

    /**
     * Report stub.
     */
    static class NullThreadStatusReport
        implements TethysUIThreadStatusReport {
        /**
         * The active task.
         */
        private final OceanusProfile theProfile;

        /**
         * Constructor.
         */
        NullThreadStatusReport() {
            theProfile = new OceanusProfile("Dummy");
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
