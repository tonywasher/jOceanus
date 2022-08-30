/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;

/**
 * Thread Manager.
 */
public abstract class TethysThreadManager
        implements TethysEventProvider<TethysThreadEvent>, TethysThreadStatusReport {
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
    private final TethysEventManager<TethysThreadEvent> theEventManager;

    /**
     * The Factory.
     */
    private final TethysGuiFactory theFactory;

    /**
     * The StatusManager.
     */
    private final TethysThreadStatusManager theStatusManager;

    /**
     * The status data.
     */
    private final TethysThreadStatus theStatus;

    /**
     * The ThreadData.
     */
    private TethysThreadData theThreadData;

    /**
     * The Active thread.
     */
    private TethysThread<?> theThread;

    /**
     * The Active Profile.
     */
    private TethysProfile theProfile;

    /**
     * The Error.
     */
    private Throwable theError;

    /**
     * The number of reporting steps.
     */
    private int theReportingSteps = DEFAULT_REPSTEPS;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSlider use slider status
     */
    protected TethysThreadManager(final TethysGuiFactory pFactory,
                                  final boolean pSlider) {
        /* record parameters */
        theFactory = pFactory;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the executor */
        theExecutor = Executors.newSingleThreadExecutor();

        /* Create the status */
        theStatus = new TethysThreadStatus();

        /* Create the status manager */
        theStatusManager = pSlider
                ? theFactory.newThreadSliderStatus(this)
                : theFactory.newThreadTextAreaStatus(this);

        /* Default ThreadData is the factory */
        theThreadData = theFactory;
    }

    @Override
    public TethysEventRegistrar<TethysThreadEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the status manager.
     * @return the status Manager
     */
    public TethysThreadStatusManager getStatusManager() {
        return theStatusManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    public TethysGuiFactory getGuiFactory() {
        return theFactory;
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
     * Set the thread Data.
     * @param pThreadData the threadData
     */
    public void setThreadData(final TethysThreadData pThreadData) {
        theThreadData = pThreadData;
    }

    /**
     * Get the thread Data.
     * @return the threadData
     */
    protected TethysThreadData getThreadData() {
        return theThreadData;
    }

    /**
     * Set the reporting steps.
     * @param pSteps the reporting steps
     */
    public void setReportingSteps(final int pSteps) {
        theReportingSteps = pSteps;
    }

    /**
     * Get the error.
     * @return the error
     */
    public Throwable getError() {
        return theError;
    }

    /**
     * Start a thread.
     * @param <T> the thread result
     * @param pThread the thread to start
     */
    public <T> void startThread(final TethysThread<T> pThread) {
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
            theEventManager.fireEvent(TethysThreadEvent.THREADSTART, theThread);

            /* Tasks for event handler */
            //theError = null;
            //theErrorEntry.setVisible(false);
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
            theThread.prepareTask(theThreadData);

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
    protected abstract <T> Runnable wrapThread(TethysThread<T> pThread);

    /**
     * Register thread completion.
     */
    protected void threadCompleted() {
        /* Remove reference */
        final TethysThread<?> myThread = theThread;
        theThread = null;

        /* Note that thread has completed */
        theEventManager.fireEvent(TethysThreadEvent.THREADEND, myThread);

        /* Tasks for event handler */
        //theProfileEntry.setObject(theProfile);
        //theProfileEntry.setFocus();
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
        throw new TethysThreadCancelException("Cancelled");
    }

    /**
     * Publish the status.
     * @param pStatus the status to publish
     * @throws OceanusException on cancellation
     */
    protected abstract void publishStatus(TethysThreadStatus pStatus) throws OceanusException;

    /**
     * Create new profile.
     * @param pTask the name of the task
     */
    public void setNewProfile(final String pTask) {
        /* Create a new profile */
        theProfile = theFactory.getNewProfile(pTask);
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
    }

    /**
     * Set error.
     * @param pException the exception
     */
    protected void setError(final Throwable pException) {
        /* Record the error */
        theError = pException;

        /* Note that thread has an error */
        theEventManager.fireEvent(TethysThreadEvent.THREADERROR, theError);

        /* Tasks for event handler */
        //theErrorEntry.setObject(theError);
        //theErrorEntry.setVisible(true);
        //theErrorEntry.setFocus();
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public TethysProfile getActiveProfile() {
        return theProfile;
    }

    @Override
    public TethysProfile getActiveTask() {
        return theProfile == null
                ? null
                : theProfile.getActiveTask();
    }
}
