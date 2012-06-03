/*******************************************************************************
 * JDataModel: Data models
 * Copyright 2012 Tony Washer
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.threads;

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import uk.co.tolcroft.models.ui.StatusBar;
import uk.co.tolcroft.models.ui.StatusBar.StatusData;

/**
 * A wrapper for a worker thread.
 * @author Tony Washer
 * @param <T> the result type of the thread
 */
public abstract class WorkerThread<T> extends SwingWorker<T, StatusData> {
    /**
     * The Status Bar.
     */
    private final StatusBar theStatusBar;

    /**
     * The description of the operation.
     */
    private final String theTask;

    /**
     * The error for the operation.
     */
    private JDataException theError = null;

    /**
     * Constructor.
     * @param pTask task name
     * @param pStatusBar status bar
     */
    protected WorkerThread(final String pTask,
                           final StatusBar pStatusBar) {
        /* Record the parameters */
        theTask = pTask;
        theStatusBar = pStatusBar;
    }

    /**
     * Set Error.
     * @param pError the Error for the task
     */
    protected void setError(final JDataException pError) {
        /* Store the error */
        theError = pError;
    }

    /**
     * Show StatusBar.
     */
    protected void showStatusBar() {
        theStatusBar.getProgressPanel().setVisible(true);
    }

    /**
     * Complete Data Load operation.
     */
    protected void completeStatusBar() {
        /* If we are not cancelled and have no error */
        if ((!isCancelled()) && (theError == null)) {
            /* Set success */
            theStatusBar.setSuccess(theTask);

            /* Else report the cancellation/failure */
        } else {
            theStatusBar.setFailure(theTask, theError);
        }
    }

    /**
     * Task for worker thread.
     * @return the result
     * @throws Exception on error
     */
    protected abstract T performTask() throws Exception;

    @Override
    public T doInBackground() {
        T myResult;

        try {
            /* Call work function */
            myResult = performTask();

            /* Return result */
            return myResult;

            /* Catch any exceptions */
        } catch (JDataException e) {
            setError(e);
            return null;
        } catch (Exception e) {
            setError(new JDataException(ExceptionClass.DATA, "Failed " + theTask, e));
            return null;
        }
    }

    @Override
    public void done() {
        /* Update the Status Bar */
        completeStatusBar();
    }

    @Override
    protected void process(final List<StatusData> pStatus) {
        /* Access the latest status */
        StatusData myStatus = pStatus.get(pStatus.size() - 1);

        /* Update the status window */
        theStatusBar.updateStatusBar(myStatus);
    }

    /**
     * Publish status.
     * @param pStatus the Status to publish
     */
    public void publishIt(final StatusData pStatus) {
        super.publish(pStatus);
    }
}
