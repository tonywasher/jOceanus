/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/sheet/MetisWorkBookType.java $
 * $Revision: 655 $
 * $Author: Tony $
 * $Date: 2015-12-02 14:34:04 +0000 (Wed, 02 Dec 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.joceanus.jmetis.data.MetisExceptionWrapper;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Thread Manager.
 * @param <N> the node type
 */
public abstract class MetisThreadManager<N>
        implements TethysEventProvider {
    /**
     * Thread status changed.
     */
    public static final int ACTION_THREAD = 100;

    /**
     * Default Reporting Steps.
     */
    private static final Integer DEFAULT_REPSTEPS = 10;

    /**
     * Thread Executor.
     */
    private final ExecutorService theExecutor;

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * The StatusManager.
     */
    private final MetisThreadStatusManager<N> theStatusManager;

    /**
     * The Active thread.
     */
    private MetisThread<?> theThread;

    /**
     * The status data.
     */
    private final MetisThreadStatus theStatus;

    /**
     * The Thread Viewer Entry.
     */
    private final MetisViewerEntry theThreadEntry;

    /**
     * The Profile Viewer Entry.
     */
    private final MetisViewerEntry theProfileEntry;

    /**
     * The Error Viewer Entry.
     */
    private final MetisViewerEntry theErrorEntry;

    /**
     * The Active Profile.
     */
    private MetisProfile theProfile;

    /**
     * The Error.
     */
    private MetisExceptionWrapper theError;

    /**
     * The number of reporting steps.
     */
    private int theReportingSteps;

    /**
     * Constructor.
     * @param pViewerManager the viewer manager
     * @param pStatusManager the status manager
     */
    protected MetisThreadManager(final MetisViewerManager pViewerManager,
                                 final MetisThreadStatusManager<N> pStatusManager) {
        /* Store the parameters */
        theStatusManager = pStatusManager;
        theStatusManager.setThreadManager(this);

        /* Create the event manager */
        theEventManager = new TethysEventManager();

        /* Create the executor */
        theExecutor = Executors.newSingleThreadExecutor();

        /* Create the status */
        theStatus = new MetisThreadStatus();

        /* Create the thread entry */
        theThreadEntry = pViewerManager.newEntry("Thread");
        theThreadEntry.addAsRootChild();

        /* Create the profile entry */
        theProfileEntry = pViewerManager.newEntry("Profile");
        theProfileEntry.addAsChildOf(theThreadEntry);

        /* Create the error entry */
        theErrorEntry = pViewerManager.newEntry("Error");
        theErrorEntry.addAsChildOf(theThreadEntry);

        /* Hide the thread entry */
        theThreadEntry.hideEntry();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the status manager.
     * @return the status Manager
     */
    protected MetisThreadStatusManager<N> getStatusManager() {
        return theStatusManager;
    }

    /**
     * Obtain the node.
     * @return the node
     */
    public N getNode() {
        return theStatusManager.getNode();
    }

    /**
     * obtain the task name.
     * @return the task name
     */
    public abstract String getTaskName();

    /**
     * Do we have a running thread.
     * @return true/false
     */
    public boolean hasWorker() {
        return theThread != null;
    }

    /**
     * Start a thread.
     * @param <T> the thread result
     * @param pThread the thread to start
     */
    public <T> void startThread(final MetisThread<T> pThread) {
        /* Set new profile */
        String myName = pThread.getTaskName();
        setNewProfile(myName);

        /* Create the wrapped thread */
        Runnable myRunnable = wrapThread(pThread);

        /* Initialise status window */
        theStatus.setTask(myName);
        theStatusManager.setProgress(theStatus);

        /* Create thread and record status */
        theExecutor.execute(myRunnable);
        theThread = pThread;

        /* Show the thread entry */
        theThreadEntry.showPrimeEntry();

        /* Note that thread has started */
        theEventManager.fireActionEvent(ACTION_THREAD);
    }

    /**
     * Create thread wrapper.
     * @param <T> the thread result
     * @param pThread the thread to wrap
     */
    protected abstract <T> Runnable wrapThread(final MetisThread<T> pThread);

    /**
     * Register thread completion.
     */
    protected void threadCompleted() {
        /* Remove reference */
        theThread = null;
        theThreadEntry.hideEntry();

        /* Note that thread has completed */
        theEventManager.fireActionEvent(ACTION_THREAD);
    }

    /**
     * cancel the worker.
     */
    public abstract void cancelWorker();

    /**
     * is the thread cancelled?
     * @return true/false
     */
    public abstract boolean isCancelled();

    /**
     * Initialise the task.
     * @param pTask the name of the task
     * @return continue true/false
     */
    public boolean initTask(final String pTask) {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Record task */
            theStatus.setTask(pTask);

            /* Publish status */
            publishStatus(theStatus);
        }

        /* Return to caller */
        return isCancelled;
    }

    /**
     * Set Number of stages.
     * @param pNumStages number of stages
     * @return continue true/false
     */
    public boolean setNumStages(final int pNumStages) {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Initialise the number of stages */
            theStatus.setNumStages(pNumStages);
        }

        /* Return to caller */
        return isCancelled;
    }

    /**
     * Set New stage.
     * @param pStage the stage
     * @return continue true/false
     */
    public boolean setNewStage(final String pStage) {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Store the stage and increment stages done */
            theStatus.setStage(pStage);

            /* Publish status */
            publishStatus(theStatus);
        }

        /* Return to caller */
        return isCancelled;
    }

    /**
     * Set Number of steps in this stage.
     * @param pNumSteps number of steps
     * @return continue true/false
     */
    public boolean setNumSteps(final int pNumSteps) {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Set number of Steps */
            theStatus.setNumSteps(pNumSteps);

            /* Determine reporting steps */
            theReportingSteps = DEFAULT_REPSTEPS;
        }

        /* Return to caller */
        return isCancelled;
    }

    /**
     * Set Number of steps completed.
     * @return continue true/false
     */
    public boolean setNextStep() {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Set Next step */
            theStatus.setNextStep();

            /* Determine step */
            int myStep = theStatus.getStepsDone() + 1;

            /* If we need to report the step */
            if ((myStep % theReportingSteps) == 0) {
                /* Publish status */
                publishStatus(theStatus);
            }
        }

        /* Return to caller */
        return isCancelled;
    }

    /**
     * Publish the status.
     * @param pStatus
     */
    protected abstract void publishStatus(final MetisThreadStatus pStatus);

    /**
     * Create new profile.
     * @param pTask the name of the task
     */
    private void setNewProfile(final String pTask) {
        /* Create a new profile */
        theProfile = new MetisProfile(pTask);

        /* Update the Viewer entry */
        theProfileEntry.setObject(theProfile);

        /* Clear errors */
        theError = null;
        theErrorEntry.hideEntry();
    }

    /**
     * End the active task.
     */
    protected void endTask() {
        theProfile.end();
        theProfileEntry.setFocus();
    }

    /**
     * Set error.
     */
    protected void setError(final Throwable pException) {
        /* Record the error */
        theError = new MetisExceptionWrapper(pException);

        /* Report to manager */
        theErrorEntry.setObject(theError);
        theErrorEntry.showEntry();
        theErrorEntry.setFocus();
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public MetisProfile getActiveProfile() {
        return theProfile;
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public MetisProfile getActiveTask() {
        return theProfile == null
                                  ? null
                                  : theProfile.getActiveTask();
    }
}
