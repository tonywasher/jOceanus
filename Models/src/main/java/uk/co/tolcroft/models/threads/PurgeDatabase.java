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

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Thread to purge tables in a database that represent a data set. Existing loaded data will be marked as new
 * so that it will be written to the database via the store command.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class PurgeDatabase<T extends DataSet<T>> extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "DataBase Purge";

    /**
     * Data Control.
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
    public PurgeDatabase(final DataControl<T> pControl) {
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
        theStatus.initTask("Purging Database");

        /* Create interface */
        Database<T> myDatabase = theControl.getDatabase();

        /* Load database */
        myDatabase.purgeTables(theStatus);

        /* Re-base this set on a null set */
        T myNull = theControl.getNewData();
        T myData = theControl.getData();
        myData.reBase(myNull);

        /* Return null */
        return null;
    }
}
