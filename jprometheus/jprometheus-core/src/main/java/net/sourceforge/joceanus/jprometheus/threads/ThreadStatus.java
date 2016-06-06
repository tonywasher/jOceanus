/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.threads;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferenceKey;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferences;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.StatusData;
import net.sourceforge.joceanus.jprometheus.views.StatusDisplay;

/**
 * Thread Status.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public class ThreadStatus<T extends DataSet<T, E>, E extends Enum<E>>
        implements TaskControl<T> {
    /**
     * Default Number of steps/stages.
     */
    private static final int DEFAULT_NUMBER = 100;

    /**
     * The worker thread.
     */
    private ThreadStatusControl theThread;

    /**
     * The status data.
     */
    private final StatusData theStatus;

    /**
     * The data control.
     */
    private final DataControl<T, E, ?, ?> theControl;

    /**
     * The StatusBar.
     */
    private final StatusDisplay theStatusBar;

    /**
     * The number of reporting steps.
     */
    private int theSteps;

    /**
     * Constructor.
     * @param pControl the data control
     * @param pStatusBar the status bar
     */
    public ThreadStatus(final DataControl<T, E, ?, ?> pControl,
                        final StatusDisplay pStatusBar) {
        /* Store parameter */
        theControl = pControl;
        theStatusBar = pStatusBar;

        /* Access the threadStatus properties */
        MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        MetisThreadPreferences myPreferences = myMgr.getPreferenceSet(MetisThreadPreferences.class);
        theSteps = myPreferences.getIntegerValue(MetisThreadPreferenceKey.REPSTEPS);

        /* Create the status */
        theStatus = new StatusData();
    }

    @Override
    public int getReportingSteps() {
        return theSteps;
    }

    @Override
    public T getNewDataSet() {
        return theControl.getNewData();
    }

    @Override
    public GordianHashManager getSecurity() {
        return theControl.getSecureManager();
    }

    /**
     * Get the data control.
     * @return the data control
     */
    public DataControl<T, E, ?, ?> getControl() {
        return theControl;
    }

    @Override
    public MetisProfile getNewProfile(final String pTask) {
        return theControl.getNewProfile(pTask);
    }

    @Override
    public MetisProfile getActiveProfile() {
        return theControl.getActiveProfile();
    }

    @Override
    public MetisProfile getActiveTask() {
        return theControl.getActiveTask();
    }

    @Override
    public boolean isCancelled() {
        return theThread.isCancelled();
    }

    @Override
    public MetisDataFormatter getDataFormatter() {
        return theControl.getDataFormatter();
    }

    /**
     * Obtain StatusBar.
     * @return the StatusBar
     */
    public StatusDisplay getStatusBar() {
        return theStatusBar;
    }

    /**
     * Register thread.
     * @param pThread the thread that will use this ThreadStatus
     */
    public void registerThread(final ThreadStatusControl pThread) {
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
}
