/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferenceKey;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadPreference.MetisThreadPreferences;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerExceptionWrapper;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;

/**
 * Thread Manager.
 */
public abstract class MetisThreadManager
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
    private final MetisToolkit theToolkit;

    /**
     * The StatusManager.
     */
    private final MetisThreadStatusManager theStatusManager;

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
    private MetisThread<?> theThread;

    /**
     * The Active Profile.
     */
    private MetisProfile theProfile;

    /**
     * The Error.
     */
    private MetisViewerExceptionWrapper theError;

    /**
     * The number of reporting steps.
     */
    private int theReportingSteps;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @param pSlider use slider status
     */
    protected MetisThreadManager(final MetisToolkit pToolkit,
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
        final MetisViewerManager myViewer = theToolkit.getViewerManager();
        theProfileEntry = myViewer.getStandardEntry(MetisViewerStandardEntry.PROFILE);
        theErrorEntry = myViewer.getStandardEntry(MetisViewerStandardEntry.ERROR);

        /* Hide the error entry */
        theErrorEntry.setVisible(false);

        /* Create the status manager */
        theStatusManager = pSlider
                                   ? theToolkit.newThreadSliderStatus(this)
                                   : theToolkit.newThreadTextAreaStatus(this);

        /* Access the threadStatus properties */
        final MetisPreferenceManager myMgr = theToolkit.getPreferenceManager();
        final MetisThreadPreferences myPreferences = myMgr.getPreferenceSet(MetisThreadPreferences.class);
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
    public MetisThreadStatusManager getStatusManager() {
        return theStatusManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    public TethysGuiFactory getGuiFactory() {
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
    public <T> void startThread(final MetisThread<T> pThread) {
        /* Set new profile */
        final String myName = pThread.getTaskName();
        setNewProfile(myName);
        theThread = pThread;

        /* Create the wrapped thread */
        final Runnable myRunnable = wrapThread(pThread);

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
        boolean myResult = true;
        try {
            /* Prepare the task and continue */
            theThread.prepareTask(theToolkit);

            /* Catch exceptions */
        } catch (OceanusException e) {
            endTask();
            setError(e);
            theStatusManager.setFailure(e);
            threadCompleted();
            myResult = false;
        }
        return myResult;
    }

    /**
     * Create thread wrapper.
     * @param <T> the thread result
     * @param pThread the thread to wrap
     * @return the runnable thread
     */
    protected abstract <T> Runnable wrapThread(MetisThread<T> pThread);

    /**
     * Register thread completion.
     */
    protected void threadCompleted() {
        /* Remove reference */
        final MetisThread<?> myThread = theThread;
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
    public void initTask(final String pTask) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* If we already have a task */
        if (theStatus.getTask() != null) {
            /* make sure that task is completed */
            theStatus.setCompletion();
        }

        /* Record task */
        theStatus.setTask(pTask);

        /* Publish status */
        publishStatus(theStatus);
    }

    @Override
    public void setNumStages(final int pNumStages) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Initialise the number of stages */
        theStatus.setNumStages(pNumStages);
    }

    @Override
    public void setNewStage(final String pStage) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Store the stage and increment stages done */
        theStatus.setStage(pStage);

        /* Publish status */
        publishStatus(theStatus);
    }

    @Override
    public void setNumSteps(final int pNumSteps) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Set number of Steps */
        theStatus.setNumSteps(pNumSteps);
    }

    @Override
    public void setStepsDone(final int pSteps) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Set Next step */
        theStatus.setStepsDone(pSteps);

        /* Publish status regardless */
        publishStatus(theStatus);
    }

    @Override
    public void setNextStep() throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Set Next step */
        theStatus.setNextStep();

        /* Determine step */
        final int myStep = theStatus.getStepsDone() + 1;

        /* If we need to report the step */
        if ((myStep % theReportingSteps) == 0) {
            /* Publish status */
            publishStatus(theStatus);
        }
    }

    @Override
    public void setNextStep(final String pStep) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Set Next step */
        theStatus.setNextStep(pStep);

        /* Publish status */
        publishStatus(theStatus);
    }

    @Override
    public void setCompletion() throws OceanusException {
        /* Set Completion */
        theStatus.setCompletion();

        /* Publish status */
        publishStatus(theStatus);
    }

    @Override
    public void throwCancelException() throws OceanusException {
        throw new MetisThreadCancelException("Cancelled");
    }

    /**
     * Publish the status.
     * @param pStatus the status to publish
     * @throws OceanusException on cancellation
     */
    protected abstract void publishStatus(MetisThreadStatus pStatus) throws OceanusException;

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
     * Start the process result task.
     */
    public void resultTask() {
        theProfile.startTask("processResult");
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
        theError = new MetisViewerExceptionWrapper(pException);

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
