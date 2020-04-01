/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads.swing;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Swing Thread manager.
 */
public class MetisSwingThreadManager
        extends MetisThreadManager {
    /**
     * The Toolkit.
     */
    private final MetisSwingToolkit theToolkit;

    /**
     * The Active worker.
     */
    private MetisSwingThread<?> theWorker;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @param pSlider use slider status
     */
    public MetisSwingThreadManager(final MetisSwingToolkit pToolkit,
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
        theWorker = new MetisSwingThread<>(theToolkit, getThreadData(), pThread);

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

        /* update status */
        theWorker.publishStatus(new MetisThreadStatus(pStatus));
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
        final MetisSwingThread<?> myWorker = theWorker;
        if (myWorker != null) {
            myWorker.cancel(true);
            myWorker.interruptForCancel();
        }
    }
}
