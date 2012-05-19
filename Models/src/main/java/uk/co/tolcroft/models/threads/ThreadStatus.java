/*******************************************************************************
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
package uk.co.tolcroft.models.threads;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.PreferenceSet;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceManager;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceSetChooser;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class ThreadStatus<T extends DataSet<T>> implements StatusControl, PreferenceSetChooser {
    private WorkerThread<?> theThread = null;
    private StatusData theStatus = null;
    private DataControl<T> theControl = null;
    private int theSteps;

    /**
     * ThreadStatus Preferences
     */
    private ThreadStatusPreferences theTPreferences = null;

    /* Access methods */
    public int getReportingSteps() {
        return theSteps;
    }

    public DataControl<T> getControl() {
        return theControl;
    }

    public boolean isCancelled() {
        return theThread.isCancelled();
    }

    /* Constructor */
    public ThreadStatus(WorkerThread<?> pThread,
                        DataControl<T> pControl) {
        /* Store parameter */
        theThread = pThread;
        theControl = pControl;

        /* Access the threadStatus properties */
        theTPreferences = (ThreadStatusPreferences) PreferenceManager.getPreferenceSet(this);
        theSteps = theTPreferences.getIntegerValue(ThreadStatusPreferences.nameRepSteps);

        /* Create the status */
        theStatus = new StatusData();
    }

    @Override
    public Class<? extends PreferenceSet> getPreferenceSetClass() {
        return ThreadStatusPreferences.class;
    }

    @Override
    public boolean initTask(String pTask) {
        StatusData myStatus;

        /* Check for cancellation */
        if (theThread.isCancelled())
            return false;

        /* Record task and stage */
        theStatus.setTask(pTask);
        theStatus.setStage("");

        /* Set number of Stages and set Stages done to -1 */
        theStatus.setNumStages(100);
        theStatus.setStagesDone(-1);

        /* Set number of Steps and set Steps done to -1 */
        theStatus.setNumSteps(100);
        theStatus.setStepsDone(-1);

        /* Create a new Status */
        myStatus = new StatusData(theStatus);

        /* Publish it */
        theThread.publish(myStatus);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setNumStages(int pNumStages) {
        /* Check for cancellation */
        if (theThread.isCancelled())
            return false;

        /* Initialise the number of stages and Stages done */
        theStatus.setNumStages(pNumStages);
        theStatus.setStagesDone(-1);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setNewStage(String pStage) {
        StatusData myStatus;

        /* Check for cancellation */
        if (theThread.isCancelled())
            return false;

        /* Store the stage and increment stages done */
        theStatus.setStage(pStage);
        theStatus.setStagesDone(theStatus.getStagesDone() + 1);
        theStatus.setNumSteps(100);
        if (theStatus.getStagesDone() < theStatus.getNumStages())
            theStatus.setStepsDone(0);
        else
            theStatus.setStepsDone(100);

        /* Create a new Status */
        myStatus = new StatusData(theStatus);

        /* Publish it */
        theThread.publish(myStatus);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setNumSteps(int pNumSteps) {
        /* Check for cancellation */
        if (theThread.isCancelled())
            return false;

        /* Set number of Steps */
        theStatus.setNumSteps(pNumSteps);

        /* Return to caller */
        return true;
    }

    @Override
    public boolean setStepsDone(int pStepsDone) {
        StatusData myStatus;

        /* Check for cancellation */
        if (theThread.isCancelled())
            return false;

        /* Set Steps done */
        theStatus.setStepsDone(pStepsDone);

        /* Create a new Status */
        myStatus = new StatusData(theStatus);

        /* Publish it */
        theThread.publish(myStatus);

        /* Return to caller */
        return true;
    }

    /**
     * ThreadStatus Preferences
     */
    public static class ThreadStatusPreferences extends PreferenceSet {
        /**
         * Registry name for Reporting Steps
         */
        protected final static String nameRepSteps = "ReportingSteps";

        /**
         * Display name for Reporting Steps
         */
        protected final static String dispRepSteps = "Reporting Steps";

        /**
         * Default Reporting Steps
         */
        private final static Integer defRepSteps = 10;

        /**
         * Constructor
         * @throws ModelException
         */
        public ThreadStatusPreferences() throws ModelException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            definePreference(nameRepSteps, PreferenceType.Integer);
        }

        @Override
        protected Object getDefaultValue(String pName) {
            /* Handle default values */
            if (pName.equals(nameRepSteps))
                return defRepSteps;
            return null;
        }

        @Override
        protected String getDisplayName(String pName) {
            /* Handle default values */
            if (pName.equals(nameRepSteps))
                return dispRepSteps;
            return null;
        }
    }
}
