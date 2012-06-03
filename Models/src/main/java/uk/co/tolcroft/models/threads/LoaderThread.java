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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.ui.StatusBar;
import uk.co.tolcroft.models.views.DataControl;

/**
 * A wrapper for a worker thread that loads a DataSet.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class LoaderThread<T extends DataSet<T>> extends WorkerThread<T> {
    /**
     * Data Control.
     */
    private final DataControl<T> theControl;

    /**
     * Constructor.
     * @param pTask task name
     * @param pControl data control
     * @param pStatusBar the status bar
     */
    protected LoaderThread(final String pTask,
                           final DataControl<T> pControl,
                           final StatusBar pStatusBar) {
        /* Record the parameters */
        super(pTask, pStatusBar);
        theControl = pControl;
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
                    /* Activate the data and obtain any error */
                    theControl.setData(myData);
                    setError(theControl.getError());
                }
            }

            /* Update the Status Bar */
            completeStatusBar();
        } catch (Exception e) {
            /* Report the failure */
            setError(new JDataException(ExceptionClass.DATA, "Failed to obtain and activate new data", e));
            completeStatusBar();
        }
    }
}
