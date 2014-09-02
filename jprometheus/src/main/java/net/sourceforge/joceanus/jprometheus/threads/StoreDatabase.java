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
package net.sourceforge.joceanus.jprometheus.threads;

import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Thread to store changes in the DataSet to a database.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public class StoreDatabase<T extends DataSet<T, E>, E extends Enum<E>>
        extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "DataBase Store";

    /**
     * Data control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T, E> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public StoreDatabase(final ThreadStatus<T, E> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JOceanusException {
        /* Initialise the status window */
        theStatus.initTask("Storing to Database");

        /* Create interface */
        Database<T> myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Store database */
            myDatabase.updateDatabase(theStatus, theControl.getUpdates());

            /* Initialise the status window */
            theStatus.initTask("Verifying Store");

            /* Load database */
            T myData = myDatabase.loadDatabase(theStatus);

            /* Create a difference set between the two data copies */
            DataSet<T, ?> myDiff = myData.getDifferenceSet(theStatus, theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new JPrometheusDataException(myDiff, "DataStore is inconsistent");
            }

            /* Derive new update list */
            theControl.deriveUpdates();

            /* Return null */
            return null;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }
}
