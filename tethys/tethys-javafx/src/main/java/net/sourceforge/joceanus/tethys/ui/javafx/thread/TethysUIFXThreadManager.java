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
package net.sourceforge.joceanus.tethys.ui.javafx.thread;

import java.util.concurrent.atomic.AtomicReference;
import javafx.concurrent.Worker.State;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadCancelException;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadManager;
import net.sourceforge.joceanus.tethys.ui.core.thread.TethysUICoreThreadStatus;

/**
 * javaFX Thread manager.
 */
public class TethysUIFXThreadManager
        extends TethysUICoreThreadManager {
    /**
     * The Active status.
     */
    private final AtomicReference<TethysUICoreThreadStatus> theActiveStatus;

    /**
     * The Active worker.
     */
    private TethysUIFXThread<?> theWorker;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSlider  use slider status
     */
    TethysUIFXThreadManager(final TethysUICoreFactory<?> pFactory,
                            final boolean pSlider) {
        super(pFactory, pSlider);
        theActiveStatus = new AtomicReference<>();
    }

    @Override
    public String getTaskName() {
        return theWorker == null
                ? null
                : theWorker.getTaskName();
    }

    @Override
    protected <T> Runnable wrapThread(final TethysUIThread<T> pThread) {
        /* Create the wrapped thread and listen to state transition */
        theWorker = new TethysUIFXThread<>(this, pThread);
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
            throw new TethysUIThreadCancelException("Cancelled");
        }
    }

    @Override
    protected void publishStatus(final TethysUICoreThreadStatus pStatus) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* Take a copy as the active status */
        theActiveStatus.set(new TethysUICoreThreadStatus(pStatus));

        /* update status */
        theWorker.publishStatus();
    }

    /**
     * Process the status.
     */
    private void processStatus() {
        /* Pass to the status bar */
        getStatusManager().setProgress(theActiveStatus.get());
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
        if (pException instanceof TethysUIThreadCancelException) {
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
        final TethysUIFXThread<?> myWorker = theWorker;
        if (myWorker != null) {
            myWorker.cancel(true);
            myWorker.interruptForCancel();
        }
    }
}
