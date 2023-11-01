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

import net.sourceforge.joceanus.jgordianknot.api.password.GordianDialogController;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * Test security.
 */
public class MoneyWiseTestSecurity {
    /**
     * The DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * Constructor.
     * @param pDataSet the DataSet
     */
    MoneyWiseTestSecurity(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Initialise security.
     */
    public void initSecurity() throws OceanusException {
        /* Access the Password manager and disable prompting */
        final GordianPasswordManager myManager = theDataSet.getPasswordMgr();
        myManager.setDialogController(new DialogStub());

        /* Create the cloneSet and initialise security */
        final MoneyWiseData myNullData = theDataSet.deriveCloneSet();

        /* Create the control data */
        theDataSet.getControlData().addNewControl(0);
        theDataSet.initialiseSecurity(new ReporterStub(), myNullData);
    }

    /**
     * Dialog stub.
     */
    private static class DialogStub
            implements GordianDialogController {
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
    private static class ReporterStub
        implements TethysUIThreadStatusReport {
        /**
         * The active task.
         */
        private final TethysProfile theProfile;

        /**
         * Constructor.
         */
        ReporterStub() {
            theProfile = new TethysProfile("Dummy");
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
