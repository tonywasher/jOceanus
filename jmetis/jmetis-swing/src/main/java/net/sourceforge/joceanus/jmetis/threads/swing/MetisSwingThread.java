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
package net.sourceforge.joceanus.jmetis.threads.swing;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusManager;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * javaFX Thread wrapper.
 * @param <T> the thread result
 */
public class MetisSwingThread<T>
        extends SwingWorker<Void, MetisThreadStatus> {
    /**
     * The Toolkit.
     */
    private final MetisSwingToolkit theToolkit;

    /**
     * The ThreadManager.
     */
    private final MetisSwingThreadManager theManager;

    /**
     * The ThreadStatusManager.
     */
    private final MetisThreadStatusManager theStatusMgr;

    /**
     * The wrapped thread.
     */
    private final MetisThread<T> theThread;

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
     * @param pToolkit the toolkit
     * @param pThread the thread to wrap
     */
    protected MetisSwingThread(final MetisSwingToolkit pToolkit,
                               final MetisThread<T> pThread) {
        theToolkit = pToolkit;
        theManager = theToolkit.getThreadManager();
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
        theResult = theThread.performTask(theToolkit);
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
    protected void publishStatus(final MetisThreadStatus pStatus) {
        publish(pStatus);
    }

    @Override
    protected void process(final List<MetisThreadStatus> pList) {
        /* Obtain the most recent status */
        final MetisThreadStatus myStatus = pList.get(pList.size() - 1);

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
