/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.threads.swing;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jprometheus.views.DataControl;

/**
 * A wrapper for a worker thread that loads a DataSet.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public abstract class LoaderThread<T extends DataSet<T, E>, E extends Enum<E>>
        extends WorkerThread<T> {
    /**
     * Data Control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Constructor.
     * @param pTask task name
     * @param pStatus the thread status
     */
    protected LoaderThread(final String pTask,
                           final ThreadStatus<T, E> pStatus) {
        /* Record the parameters */
        super(pTask, pStatus);
        theControl = pStatus.getControl();
    }

    @Override
    public void done() {
        /* Protect against exceptions */
        try {
            /* Get the newly loaded data */
            T myData = get();

            /* If we are not cancelled and have data */
            if (!isCancelled() && (myData != null)) {
                /* Activate the data and obtain any errors */
                theControl.setData(myData);
                addErrorList(theControl.getErrors());
            }

            /* Catch any exception to keep thread interface clean */
        } catch (InterruptedException
                | CancellationException
                | ExecutionException e) {
            /* Report the failure */
            addError(new PrometheusIOException("Failed to obtain and activate new data", e));
        }

        /* Update the Status Bar */
        completeStatusBar();
    }
}
