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
package net.sourceforge.joceanus.jtethys.ui.swing;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysThread;
import net.sourceforge.joceanus.jtethys.ui.TethysThreadCancelException;
import net.sourceforge.joceanus.jtethys.ui.TethysThreadManager;
import net.sourceforge.joceanus.jtethys.ui.TethysThreadStatus;

/**
 * Swing Thread manager.
 */
public class TethysSwingThreadManager
        extends TethysThreadManager {
    /**
     * The Active worker.
     */
    private TethysSwingThread<?> theWorker;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pSlider use slider status
     */
    TethysSwingThreadManager(final TethysSwingGuiFactory pFactory,
                             final boolean pSlider) {
        super(pFactory, pSlider);
     }

    @Override
    public String getTaskName() {
        return theWorker == null
                ? null
                : theWorker.getTaskName();
    }

    @Override
    protected <T> Runnable wrapThread(final TethysThread<T> pThread) {
        /* Create the wrapped thread and listen to state transition */
        theWorker = new TethysSwingThread<>(this, getThreadData(), pThread);

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
            throw new TethysThreadCancelException("Cancelled");
        }
    }

    @Override
    protected void publishStatus(final TethysThreadStatus pStatus) throws OceanusException {
        /* Check for cancellation */
        checkForCancellation();

        /* update status */
        theWorker.publishStatus(new TethysThreadStatus(pStatus));
    }

    /**
     * Handle completion.
     */
    protected void handleCompletion() {
        endTask();
        getStatusManager().setCompletion();
    }

    /**
     * Handle cancellation.
     */
    protected void handleCancellation() {
        endTask();
        getStatusManager().setCancelled();
    }

    /**
     * Handle failure.
     * @param pException the exception
     */
    protected void handleFailure(final Throwable pException) {
        /* Handle cancellation exception as cancel */
        if (pException instanceof TethysThreadCancelException) {
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
        final TethysSwingThread<?> myWorker = theWorker;
        if (myWorker != null) {
            myWorker.cancel(true);
            myWorker.interruptForCancel();
        }
    }
}
