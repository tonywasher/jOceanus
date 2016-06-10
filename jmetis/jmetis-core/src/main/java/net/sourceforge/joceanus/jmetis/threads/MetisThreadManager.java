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
import net.sourceforge.joceanus.jmetis.newviewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferenceKey;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
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
        implements TethysEventProvider<MetisThreadEvent>, MetisThreadStatusReport {
    /**
     * Thread Executor.
     */
    private final ExecutorService theExecutor;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<MetisThreadEvent> theEventManager;

    /**
     * The Toolkit.
     */
    private final MetisToolkit<N, I> theToolkit;

    /**
     * The StatusManager.
     */
    private final MetisThreadStatusManager<N> theStatusManager;

    /**
     * The status data.
     */
    private final MetisThreadStatus theStatus;

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
     * @param pSlider use slider status
     */
    protected MetisThreadManager(final MetisToolkit<N, I> pToolkit,
                                 final boolean pSlider) {
        /* record parameters */
        theToolkit = pToolkit;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the executor */
        theExecutor = Executors.newSingleThreadExecutor();

        /* Create the status */
        theStatus = new MetisThreadStatus();

        /* Create the viewer entries */
        MetisViewerManager myViewer = theToolkit.getViewerManager();
        theProfileEntry = myViewer.getStandardEntry(MetisViewerStandardEntry.PROFILE);
        theErrorEntry = myViewer.getStandardEntry(MetisViewerStandardEntry.ERROR);

        /* Hide the error entry */
        theErrorEntry.setVisible(false);

        /* Create the status manager */
        theStatusManager = pSlider
                                   ? theToolkit.newThreadSliderStatus(this)
                                   : theToolkit.newThreadTextAreaStatus(this);

        /* Access the threadStatus properties */
        MetisPreferenceManager myMgr = theToolkit.getPreferenceManager();
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
    public MetisThreadStatusManager<N> getStatusManager() {
        return theStatusManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    public TethysGuiFactory<N, I> getGuiFactory() {
        return theToolkit.getGuiFactory();
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
        theThread = pThread;

        /* Create the wrapped thread */
        Runnable myRunnable = wrapThread(pThread);

        /* If we prepared the thread OK */
        if (prepareThread()) {
            /* Initialise status window */
            theStatus.setTask(myName);
            theStatusManager.setProgress(theStatus);

            /* Create thread and record status */
            theExecutor.execute(myRunnable);

            /* Note that thread has started */
            theEventManager.fireEvent(MetisThreadEvent.THREADSTART);
        }
    }

    /**
     * Prepare task.
     * @return continue true/false
     */
    private boolean prepareThread() {
        /* Protect against exceptions */
        try {
            /* Prepare the task and continue */
            theThread.prepareTask(theToolkit);
            return true;

            /* Catch exceptions */
        } catch (OceanusException e) {
            endTask();
            setError(e);
            theStatusManager.setFailure(e);
            threadCompleted();
            return false;
        }
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
        MetisThread<?, N, I> myThread = theThread;
        theThread = null;

        /* Note that thread has completed */
        theEventManager.fireEvent(MetisThreadEvent.THREADEND, myThread);
    }

    /**
     * Shut down the thread manager.
     */
    public void shutdown() {
        theExecutor.shutdownNow();
    }

    /**
     * cancel the worker.
     */
    public abstract void cancelWorker();

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public boolean setStepsDone(final int pSteps) {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Set Next step */
            theStatus.setStepsDone(pSteps);

            /* Publish status regardless */
            publishStatus(theStatus);
        }

        /* Return to caller */
        return !isCancelled;
    }

    @Override
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

    @Override
    public boolean setNextStep(final String pStep) {
        /* Check for cancellation */
        boolean isCancelled = isCancelled();
        if (!isCancelled) {
            /* Set Next step */
            theStatus.setNextStep(pStep);

            /* Publish status */
            publishStatus(theStatus);
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
    public void setNewProfile(final String pTask) {
        /* Create a new profile */
        theProfile = theToolkit.getNewProfile(pTask);

        /* Clear errors */
        theError = null;
        theErrorEntry.setVisible(false);
    }

    /**
     * End the active task.
     */
    protected void endTask() {
        theProfile.end();
        theProfileEntry.setObject(theProfile);
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

    @Override
    public MetisProfile getActiveTask() {
        return theProfile == null
                                  ? null
                                  : theProfile.getActiveTask();
    }
}
