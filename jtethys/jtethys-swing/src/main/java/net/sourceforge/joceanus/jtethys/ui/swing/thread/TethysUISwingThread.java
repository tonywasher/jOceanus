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
package net.sourceforge.joceanus.jtethys.ui.swing.thread;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadData;
import net.sourceforge.joceanus.jtethys.ui.core.thread.TethysUICoreThreadStatus;
import net.sourceforge.joceanus.jtethys.ui.core.thread.TethysUICoreThreadStatusManager;

/**
 * javaFX Thread wrapper.
 * @param <T> the thread result
 */
public class TethysUISwingThread<T>
        extends SwingWorker<Void, TethysUICoreThreadStatus> {
    /**
     * The ThreadData.
     */
    private final TethysUIThreadData theThreadData;

    /**
     * The ThreadManager.
     */
    private final TethysUISwingThreadManager theManager;

    /**
     * The ThreadStatusManager.
     */
    private final TethysUICoreThreadStatusManager theStatusMgr;

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
     * @param pThreadData the thread data
     * @param pThread the thread to wrap
     */
    protected TethysUISwingThread(final TethysUISwingThreadManager pManager,
                                  final TethysUIThreadData pThreadData,
                                  final TethysUIThread<T> pThread) {
        theThreadData = pThreadData;
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
        theResult = theThread.performTask(theThreadData);
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
