/*******************************************************************************
 * jDataModels: Data models
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jdatamodels.threads;

import java.util.List;

import javax.swing.SwingWorker;

import net.sourceforge.joceanus.jdatamodels.data.DataErrorList;
import net.sourceforge.joceanus.jdatamodels.ui.StatusBar;
import net.sourceforge.joceanus.jdatamodels.ui.StatusBar.StatusData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * A wrapper for a worker thread.
 * @author Tony Washer
 * @param <T> the result type of the thread
 */
public abstract class WorkerThread<T>
        extends SwingWorker<T, StatusData> {
    /**
     * The Status Bar.
     */
    private final StatusBar theStatusBar;

    /**
     * The description of the operation.
     */
    private final String theTask;

    /**
     * The errors for the operation.
     */
    private final DataErrorList<JOceanusException> theErrors;

    /**
     * Constructor.
     * @param pTask task name
     * @param pStatus the thread status
     */
    protected WorkerThread(final String pTask,
                           final ThreadStatus<?> pStatus) {
        /* Record the parameters */
        theTask = pTask;
        theStatusBar = pStatus.getStatusBar();
        theErrors = new DataErrorList<JOceanusException>();
    }

    /**
     * Add Error.
     * @param pError the Error for the task
     */
    protected void addError(final JOceanusException pError) {
        /* Store the error */
        theErrors.add(pError);
    }

    /**
     * Add Error List.
     * @param pErrors the Errors for the task
     */
    protected void addErrorList(final DataErrorList<JOceanusException> pErrors) {
        /* Store the errors */
        theErrors.addList(pErrors);
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
        if ((!isCancelled())
            && (theErrors.isEmpty())) {
            /* Set success */
            theStatusBar.setSuccess(theTask);

            /* Else report the cancellation/failure */
        } else {
            theStatusBar.setFailure(theTask, theErrors);
        }
    }

    /**
     * Task for worker thread.
     * @return the result
     * @throws JOceanusException on error
     */
    protected abstract T performTask() throws JOceanusException;

    @Override
    public T doInBackground() {
        T myResult;

        try {
            /* Call work function */
            myResult = performTask();

            /* Return result */
            return myResult;

            /* Catch any exception to keep thread interface clean */
        } catch (Exception e) {
            addError(new JOceanusException("Failed to perform background task", e));
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
