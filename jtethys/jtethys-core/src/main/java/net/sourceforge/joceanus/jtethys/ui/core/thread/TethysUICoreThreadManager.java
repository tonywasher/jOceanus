/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Thread Manager.
 */
public abstract class TethysUICoreThreadManager
        implements TethysEventProvider<TethysUIThreadEvent>, TethysUIThreadManager {
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
    private final TethysEventManager<TethysUIThreadEvent> theEventManager;

    /**
     * The Toolkit.
     */
    private final TethysUICoreFactory<?> theFactory;

    /**
     * The StatusManager.
     */
    private final TethysUIThreadStatusManager theStatusManager;

    /**
     * The status data.
     */
    private final TethysUICoreThreadStatus theStatus;

    /**
     * The ThreadData.
     */
    private Object theThreadData;

    /**
     * The Active thread.
     */
    private TethysUIThread<?> theThread;

    /**
     * The Active thread.
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
    protected TethysUICoreThreadManager(final TethysUICoreFactory<?> pFactory,
                                        final boolean pSlider) {
        /* record parameters */
        theFactory = pFactory;

        /* Create the event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the executor */
        theExecutor = Executors.newSingleThreadExecutor();

        /* Create the status */
        theStatus = new TethysUICoreThreadStatus();

        /* Create the Status area */
        final TethysUICoreThreadFactory myFactory = (TethysUICoreThreadFactory) pFactory.threadFactory();
        theStatusManager = pSlider
                ? myFactory.newThreadSliderStatus(this)
                : myFactory.newThreadTextAreaStatus(this);

        /* Default ThreadData is the factory */
        theThreadData = theFactory;
    }

    @Override
    public TethysEventRegistrar<TethysUIThreadEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public TethysUIThreadStatusManager getStatusManager() {
        return theStatusManager;
    }

    /**
     * Obtain the GUI factory.
     * @return the factory
     */
    public TethysUICoreFactory<?> getGuiFactory() {
        return theFactory;
    }

    @Override
    public boolean hasWorker() {
        return theThread != null;
    }

    @Override
    public void setThreadData(final Object pThreadData) {
        theThreadData = pThreadData;
    }

    @Override
    public void setReportingSteps(final int pSteps) {
        theReportingSteps = pSteps;
    }

    @Override
    public Throwable getError() {
        return theError;
    }

    @Override
    public Object getThreadData() {
        return theThreadData;
    }

    @Override
    public <T> void startThread(final TethysUIThread<T> pThread) {
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
            theEventManager.fireEvent(TethysUIThreadEvent.THREADSTART, theThread);
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
            theThread.prepareTask(this);

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
    protected abstract <T> Runnable wrapThread(TethysUIThread<T> pThread);

    /**
     * Register thread completion.
     */
    protected void threadCompleted() {
        /* Remove reference */
        final TethysUIThread<?> myThread = theThread;
        theThread = null;

        /* Note that thread has completed */
        theEventManager.fireEvent(TethysUIThreadEvent.THREADEND, myThread);
    }

    @Override
    public void shutdown() {
        theExecutor.shutdownNow();
    }

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
        throw new TethysUIThreadCancelException("Cancelled");
    }

    /**
     * Publish the status.
     * @param pStatus the status to publish
     * @throws OceanusException on cancellation
     */
    protected abstract void publishStatus(TethysUICoreThreadStatus pStatus) throws OceanusException;

    @Override
    public void setNewProfile(final String pTask) {
        /* Create a new profile */
        theProfile = theFactory.getNewProfile(pTask);

        /* Clear errors */
        theError = null;
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
        theEventManager.fireEvent(TethysUIThreadEvent.THREADERROR, theError);
    }

    @Override
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
