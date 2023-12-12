/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.threads;

import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Thread to purge tables in a database that represent a data set. Existing loaded data will be
 * marked as new so that it will be written to the database via the store command.
 */
public class PrometheusThreadPurgeDatabase
        implements TethysUIThread<Void> {
    /**
     * Data Control.
     */
    private final DataControl theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadPurgeDatabase(final DataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.PURGEDB.toString();
    }

    @Override
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Create interface */
        final PrometheusXDataStore myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Purge database */
            myDatabase.purgeTables(pManager);

            /* Re-base this set on a null set */
            final DataSet myNull = theControl.getNewData();
            final DataSet myData = theControl.getData();
            myData.reBase(pManager, myNull);

            /* Derive the new set of updates */
            theControl.deriveUpdates();

            /* State that we have completed */
            pManager.setCompletion();

            /* Return null */
            return null;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }
}
