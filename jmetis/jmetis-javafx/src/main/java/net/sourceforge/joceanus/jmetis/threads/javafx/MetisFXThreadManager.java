/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads.javafx;

import javafx.concurrent.Worker.State;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * javaFX Thread manager.
 */
public class MetisFXThreadManager
        extends MetisThreadManager {
    /**
     * The Toolkit.
     */
    private final MetisFXToolkit theToolkit;

    /**
     * The Active worker.
     */
    private MetisFXThread<?> theWorker;

    /**
     * The Active status.
     */
    private volatile MetisThreadStatus theActiveStatus;

    /**
     * Constructor.
     *
     * @param pToolkit the toolkit
     * @param pSlider  use slider status
     */
    public MetisFXThreadManager(final MetisFXToolkit pToolkit,
                                final boolean pSlider) {
        super(pToolkit, pSlider);
        theToolkit = pToolkit;
    }

    @Override
    public String getTaskName() {
        return theWorker == null
               ? null
               : theWorker.getTaskName();
    }

    @Override
    protected <T> Runnable wrapThread(final MetisThread<T> pThread) {
        /* Create the wrapped thread and listen to state transition */
        theWorker = new MetisFXThread<>(theToolkit, pThread);
        theWorker.stateProperty().addListener((v, o, n) -> handleThreadState(n));
        theWorker.valueProperty().addListener((v, o, n) -> processStatus());

        /* Return the worker to the caller */
        return theWorker;
    }

    @Override
    protected void threadCompleted() {
        /* Remove reference */
        theWorker = null;

        /* Pass call on */
        super.threadCompleted();
    }

    @Override
    public void checkForCancellation() throws OceanusException {
        if (theWorker != null
                && theWorker.isCancelled()) {
            throw new MetisThreadCancelException("Cancelled");
        }
    }

    @Override
    protected void publishStatus(final MetisThreadStatus pStatus) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Take a copy as the active status */
        theActiveStatus = new MetisThreadStatus(pStatus);

        /* update status */
        theWorker.publishStatus();
    }

    /**
     * Process the status.
     */
    protected void processStatus() {
        /* Pass to the status bar */
        getStatusManager().setProgress(theActiveStatus);
    }

    /**
     * Handle thread state.
     *
     * @param pState the state
     */
    private void handleThreadState(final State pState) {
        /* Switch on the state */
        switch (pState) {
            case SUCCEEDED:
                handleCompletion();
                break;
            case FAILED:
                handleFailure(theWorker.getException());
                break;
            case CANCELLED:
                handleCancellation();
                break;
            default:
                break;
        }
    }

    /**
     * Handle completion.
     */
    private void handleCompletion() {
        /* Handle exceptions */
        try {
            /* Complete the thread */
            resultTask();
            theWorker.processResult();

            /* Record the completion */
            endTask();
            getStatusManager().setCompletion();

            /* Catch exceptions */
        } catch (OceanusException e) {
            /* Convert to failure */
            handleFailure(e);
        }
    }

    /**
     * Handle cancellation.
     */
    private void handleCancellation() {
        endTask();
        getStatusManager().setCancelled();
    }

    /**
     * Handle failure.
     *
     * @param pException the exception
     */
    private void handleFailure(final Throwable pException) {
        /* Handle cancellation exception as cancel */
        if (pException instanceof MetisThreadCancelException) {
            handleCancellation();

            /* handle standard exception */
        } else {
            endTask();
            setError(pException);
            getStatusManager().setFailure(pException);
        }
    }

    @Override
    public void cancelWorker() {
        /* cancel the thread */
        final MetisFXThread<?> myWorker = theWorker;
        if (myWorker != null) {
            myWorker.cancel(true);
            myWorker.interruptForCancel();
        }
    }
}
