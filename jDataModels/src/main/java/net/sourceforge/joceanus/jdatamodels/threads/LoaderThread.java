/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;

/**
 * A wrapper for a worker thread that loads a DataSet.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class LoaderThread<T extends DataSet<T>>
        extends WorkerThread<T> {
    /**
     * Data Control.
     */
    private final DataControl<T> theControl;

    /**
     * Constructor.
     * @param pTask task name
     * @param pStatus the thread status
     */
    protected LoaderThread(final String pTask,
                           final ThreadStatus<T> pStatus) {
        /* Record the parameters */
        super(pTask, pStatus);
        theControl = pStatus.getControl();
    }

    @Override
    public void done() {
        try {
            /* If we are not cancelled */
            if (!isCancelled()) {
                /* Get the newly loaded data */
                T myData = get();

                /* If we have new data */
                if (myData != null) {
                    /* Activate the data and obtain any errors */
                    theControl.setData(myData);
                    addErrorList(theControl.getErrors());
                }
            }

            /* Catch any exception to keep thread interface clean */
        } catch (Exception e) {
            /* Report the failure */
            addError(new JDataException(ExceptionClass.DATA, "Failed to obtain and activate new data", e));
        }

        /* Update the Status Bar */
        completeStatusBar();
    }
}
