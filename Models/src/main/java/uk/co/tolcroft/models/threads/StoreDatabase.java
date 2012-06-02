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
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Thread to store changes in the DataSet to a database.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class StoreDatabase<T extends DataSet<T>> extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "DataBase Store";

    /**
     * Data control.
     */
    private final DataControl<T> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pControl the data control
     */
    public StoreDatabase(final DataControl<T> pControl) {
        /* Call super-constructor */
        super(TASK_NAME, pControl.getStatusBar());

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = new ThreadStatus<T>(this, theControl);

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws Exception {
        /* Initialise the status window */
        theStatus.initTask("Storing to Database");

        /* Create interface */
        Database<T> myDatabase = theControl.getDatabase();

        /* Store database */
        myDatabase.updateDatabase(theStatus, theControl.getUpdates());

        /* Initialise the status window */
        theStatus.initTask("Verifying Store");

        /* Load database */
        T myData = myDatabase.loadDatabase(theStatus);

        /* Create a difference set between the two data copies */
        DataSet<T> myDiff = myData.getDifferenceSet(theControl.getData());

        /* If the difference set is non-empty */
        if (!myDiff.isEmpty()) {
            /* Throw an exception */
            throw new JDataException(ExceptionClass.DATA, myDiff, "DataStore is inconsistent");
        }

        /* Return null */
        return null;
    }
}
