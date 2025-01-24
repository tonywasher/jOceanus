/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.swing.thread;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusManager;
import net.sourceforge.joceanus.tethys.core.thread.TethysUICoreThreadStatus;

/**
 * javaFX Thread wrapper.
 * @param <T> the thread result
 */
public class TethysUISwingThread<T>
        extends SwingWorker<Void, TethysUICoreThreadStatus> {
    /**
     * The ThreadManager.
     */
    private final TethysUISwingThreadManager theManager;

    /**
     * The ThreadStatusManager.
     */
    private final TethysUIThreadStatusManager theStatusMgr;

    /**
     * The wrapped thread.
     */
    private final TethysUIThread<T> theThread;

    /**
     * The task name.
     */
    private final String theTask;

    /**
     * The result.
     */
    private T theResult;

    /**
     * Constructor.
     * @param pManager the thread manager
     * @param pThread the thread to wrap
     */
    protected TethysUISwingThread(final TethysUISwingThreadManager pManager,
                                  final TethysUIThread<T> pThread) {
        theManager = pManager;
        theStatusMgr = theManager.getStatusManager();
        theThread = pThread;
        theTask = pThread.getTaskName();
    }

    /**
     * obtain the task name.
     * @return the task name
     */
    protected String getTaskName() {
        return theTask;
    }

    @Override
    public Void doInBackground() throws OceanusException {
        theResult = theThread.performTask(theManager);
        return null;
    }

    /**
     * Interrupt for cancel.
     */
    protected void interruptForCancel() {
        theThread.interruptForCancel();
    }

    /**
     * Process the result.
     * @throws OceanusException on error
     */
    protected void processResult() throws OceanusException {
        theManager.resultTask();
        theThread.processResult(theResult);
    }

    /**
     * Publish the status.
     * @param pStatus the status
     */
    protected void publishStatus(final TethysUICoreThreadStatus pStatus) {
        publish(pStatus);
    }

    @Override
    protected void process(final List<TethysUICoreThreadStatus> pList) {
        /* Obtain the most recent status */
        final TethysUICoreThreadStatus myStatus = pList.get(pList.size() - 1);

        /* Pass to the status bar */
        theStatusMgr.setProgress(myStatus);
    }

    @Override
    public void done() {
        /* Protect against exceptions */
        try {
            /* Force out any exceptions that occurred in the thread */
            get();

            /* Handle thread completion */
            processResult();

            theManager.handleCompletion();

            /* Catch cancellation */
        } catch (CancellationException e) {
            theManager.handleCancellation();

            /* Catch execution */
        } catch (ExecutionException e) {
            theManager.handleFailure(e.getCause());

            /* Catch other Exceptions */
        } catch (InterruptedException e) {
            theManager.handleFailure(e);
            Thread.currentThread().interrupt();

            /* Catch other Exceptions */
        } catch (OceanusException e) {
            theManager.handleFailure(e);
        }
    }
}

