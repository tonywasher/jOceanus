/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jDataModels.threads;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.ui.StatusBar;
import net.sourceforge.jOceanus.jDataModels.ui.StatusBar.StatusData;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jGordianKnot.SecureManager;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceSet;

/**
 * Thread Status.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class ThreadStatus<T extends DataSet<T>> implements TaskControl<T> {
    /**
     * Default Number of steps/stages.
     */
    private static final int DEFAULT_NUMBER = 100;

    /**
     * The worker thread.
     */
    private WorkerThread<?> theThread = null;

    /**
     * The status data.
     */
    private final StatusData theStatus;

    /**
     * The data control.
     */
    private final DataControl<T> theControl;

    /**
     * The StatusBar.
     */
    private final StatusBar theStatusBar;

    /**
     * The number of reporting steps.
     */
    private int theSteps;

    @Override
    public int getReportingSteps() {
        return theSteps;
    }

    @Override
    public T getNewDataSet() {
        return theControl.getNewData();
    }

    @Override
    public SecureManager getSecurity() {
        return theControl.getSecurity();
    }

    /**
     * Get the data control.
     * @return the data control
     */
    public DataControl<T> getControl() {
        return theControl;
    }

    @Override
    public boolean isCancelled() {
        return theThread.isCancelled();
    }

    /**
     * Obtain StatusBar.
     * @return the StatusBar
     */
    public StatusBar getStatusBar() {
        return theStatusBar;
    }

    /**
     * Constructor.
     * @param pControl the data control
     * @param pStatusBar the status bar
     */
    public ThreadStatus(final DataControl<T> pControl,
                        final StatusBar pStatusBar) {
        /* Store parameter */
        theControl = pControl;
        theStatusBar = pStatusBar;

        /* Access the threadStatus properties */
        PreferenceManager myMgr = theControl.getPreferenceMgr();
        ThreadStatusPreferences myPreferences = myMgr.getPreferenceSet(ThreadStatusPreferences.class);
        theSteps = myPreferences.getIntegerValue(ThreadStatusPreferences.NAME_REPSTEPS);

        /* Create the status */
        theStatus = new StatusData();
    }

    /**
     * Register thread.
     * @param pThread the thread that will use this ThreadStatus
     */
    public void registerThread(final WorkerThread<?> pThread) {
        /* Store parameter */
        theThread = pThread;
    }

    @Override
    public boolean initTask(final String pTask) {
        StatusData myStatus;

        /* Check for cancellation */
        if (theThread.isCancelled()) {
            return false;
        }

        /* Record task and stage */
        theStatus.setTask(pTask);
        theStatus.setStage("");

        /* Set number of Stages and set Stages done to -1 */
        theStatus.setNumStages(DEFAULT_NUMBER);
        theStatus.setStagesDone(-1);

        /* Set number of Steps and set Steps done to -1 */
        theStatus.setNumSteps(DEFAULT_NUMBER);
        theStatus.setStepsDone(-1);

        /* Create a new Status */
        myStatus = new StatusData(theStatus);

        /* Publish it */
        theThread.publishIt(myStatus);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setNumStages(final int pNumStages) {
        /* Check for cancellation */
        if (theThread.isCancelled()) {
            return false;
        }

        /* Initialise the number of stages and Stages done */
        theStatus.setNumStages(pNumStages);
        theStatus.setStagesDone(-1);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setNewStage(final String pStage) {
        StatusData myStatus;

        /* Check for cancellation */
        if (theThread.isCancelled()) {
            return false;
        }

        /* Store the stage and increment stages done */
        theStatus.setStage(pStage);
        theStatus.setStagesDone(theStatus.getStagesDone() + 1);
        theStatus.setNumSteps(DEFAULT_NUMBER);
        if (theStatus.getStagesDone() < theStatus.getNumStages()) {
            theStatus.setStepsDone(0);
        } else {
            theStatus.setStepsDone(DEFAULT_NUMBER);
        }

        /* Create a new Status */
        myStatus = new StatusData(theStatus);

        /* Publish it */
        theThread.publishIt(myStatus);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setNumSteps(final int pNumSteps) {
        /* Check for cancellation */
        if (theThread.isCancelled()) {
            return false;
        }

        /* Set number of Steps */
        theStatus.setNumSteps(pNumSteps);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setStepsDone(final int pStepsDone) {
        StatusData myStatus;

        /* Check for cancellation */
        if (theThread.isCancelled()) {
            return false;
        }

        /* Set Steps done */
        theStatus.setStepsDone(pStepsDone);

        /* Create a new Status */
        myStatus = new StatusData(theStatus);

        /* Publish it */
        theThread.publishIt(myStatus);

        /* Return to caller */
        return true;
    }

    /**
     * ThreadStatus Preferences.
     */
    public static final class ThreadStatusPreferences extends PreferenceSet {
        /**
         * Registry name for Reporting Steps.
         */
        public static final String NAME_REPSTEPS = "ReportingSteps";

        /**
         * Display name for Reporting Steps.
         */
        private static final String DISPLAY_REPSTEPS = "Reporting Steps";

        /**
         * Default Reporting Steps.
         */
        private static final Integer DEFAULT_REPSTEPS = 10;

        /**
         * Constructor.
         * @throws JDataException on error
         */
        public ThreadStatusPreferences() throws JDataException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            defineIntegerPreference(NAME_REPSTEPS, DEFAULT_REPSTEPS);
        }

        @Override
        protected String getDisplayName(final String pName) {
            /* Handle default values */
            if (pName.equals(NAME_REPSTEPS)) {
                return DISPLAY_REPSTEPS;
            }
            return null;
        }
    }
}
