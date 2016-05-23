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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.joceanus.jmetis.data.MetisExceptionWrapper;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferenceKey;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferences;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;

/**
 * Thread Manager.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisThreadManager<N, I>
        implements TethysEventProvider<MetisThreadEvent> {
    /**
     * Thread Executor.
     */
    private final ExecutorService theExecutor;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisThreadEvent> theEventManager;

    /**
     * The StatusManager.
     */
    private final MetisThreadStatusManager<N, I> theStatusManager;

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
     * The Active thread.
     */
    private MetisThread<?, N, I> theThread;

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
     * @param pToolkit the toolkit
     */
    protected MetisThreadManager(final MetisToolkit<N, I> pToolkit) {
        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the executor */
        theExecutor = Executors.newSingleThreadExecutor();

        /* Create the status */
        theStatus = new MetisThreadStatus();

        /* Create the viewer entries */
        MetisViewerManager myViewer = pToolkit.getViewerManager();
        theThreadEntry = myViewer.newEntry("Thread");
        theProfileEntry = myViewer.newEntry(theThreadEntry, "Profile");
        theErrorEntry = myViewer.newEntry(theThreadEntry, "Error");

        /* Hide the thread entry */
        theThreadEntry.setVisible(false);

        /* Create the status manager */
        theStatusManager = pToolkit.newThreadStatusManager(this);

        /* Access the threadStatus properties */
        MetisPreferenceManager myMgr = pToolkit.getPreferenceManager();
        MetisThreadPreferences myPreferences = myMgr.getPreferenceSet(MetisThreadPreferences.class);
        theReportingSteps = myPreferences.getIntegerValue(MetisThreadPreferenceKey.REPSTEPS);
    }

    @Override
    public TethysEventRegistrar<MetisThreadEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the status manager.
     * @return the status Manager
     */
    protected MetisThreadStatusManager<N, I> getStatusManager() {
        return theStatusManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    public TethysGuiFactory<N, I> getGuiFactory() {
        return theStatusManager.getGuiFactory();
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
    public <T> void startThread(final MetisThread<T, N, I> pThread) {
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
        theThreadEntry.setVisible(true);

        /* Note that thread has started */
        theEventManager.fireEvent(MetisThreadEvent.THREADSTART);
    }

    /**
     * Create thread wrapper.
     * @param <T> the thread result
     * @param pThread the thread to wrap
     * @return the runnable thread
     */
    protected abstract <T> Runnable wrapThread(final MetisThread<T, N, I> pThread);

    /**
     * Register thread completion.
     */
    protected void threadCompleted() {
        /* Remove reference */
        theThread = null;
        theThreadEntry.setVisible(false);

        /* Note that thread has completed */
        theEventManager.fireEvent(MetisThreadEvent.THREADEND);
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
        return !isCancelled;
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
        return !isCancelled;
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
        return !isCancelled;
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
        }

        /* Return to caller */
        return !isCancelled;
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
        return !isCancelled;
    }

    /**
     * Publish the status.
     * @param pStatus the status to publish
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
        theErrorEntry.setVisible(false);
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
     * @param pException the exception
     */
    protected void setError(final Throwable pException) {
        /* Record the error */
        theError = new MetisExceptionWrapper(pException);

        /* Report to manager */
        theErrorEntry.setObject(theError);
        theErrorEntry.setVisible(true);
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
