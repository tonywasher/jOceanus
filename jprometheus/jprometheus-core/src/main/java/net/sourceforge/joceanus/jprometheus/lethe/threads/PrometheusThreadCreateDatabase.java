/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.threads;

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Thread to create tables in a database to represent a data set. Existing tables will be dropped
 * and redefined. Existing loaded data will be marked as new so that it will be written to the
 * database via the store command.
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 * @param <N> the node type
 * @param <I> the icon type
 */
public class PrometheusThreadCreateDatabase<T extends DataSet<T, E>, E extends Enum<E>, N, I>
        implements MetisThread<Void, N, I> {
    /**
     * Data Control.
     */
    private final DataControl<T, E, N, I> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadCreateDatabase(final DataControl<T, E, N, I> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.CREATEDB.toString();
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        final MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();

        /* Initialise the status window */
        myManager.initTask(getTaskName());

        /* Access Database */
        final PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Create database */
            myDatabase.createTables(myManager);

            /* Re-base this set on a null set */
            final T myNull = theControl.getNewData();
            final T myData = theControl.getData();
            myData.reBase(myManager, myNull);

            /* Derive the new set of updates */
            theControl.deriveUpdates();

            /* State that we have completed */
            myManager.setCompletion();

            /* Return null value */
            return null;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }
}
