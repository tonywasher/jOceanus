/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/sheet/MetisWorkBookType.java $
 * $Revision: 655 $
 * $Author: Tony $
 * $Date: 2015-12-02 14:34:04 +0000 (Wed, 02 Dec 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.threads.swing;

import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatus;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * javaFX Thread wrapper.
 * @param <T> the thread result
 */
public class MetisSwingThread<T>
        extends SwingWorker<Void, MetisThreadStatus> {
    /**
     * The ThreadManager.
     */
    private final MetisSwingThreadManager theManager;

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
     * @param pManager the thread manager
     * @param pThread the thread to wrap
     */
    protected MetisSwingThread(final MetisSwingThreadManager pManager,
                               final MetisThread<T> pThread) {
        theManager = pManager;
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
        MetisThreadStatus myStatus = pList.get(pList.size() - 1);

        /* Pass to the status bar */
        theManager.getStatusManager().setProgress(myStatus);
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

            /* Catch other Exceptions */
        } catch (OceanusException
                | InterruptedException
                | ExecutionException e) {
            theManager.handleFailure(e);
        }
    }
}
