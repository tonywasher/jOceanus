/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package net.sourceforge.joceanus.prometheus.threads;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;

/**
 * Thread to create tables in a database to represent a data set. Existing tables will be dropped
 * and redefined. Existing loaded data will be marked as new so that it will be written to the
 * database via the store command.
 */
public class PrometheusThreadCreateTables
        implements TethysUIThread<Void> {
    /**
     * Data Control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor (Event Thread).
     *
     * @param pControl data control
     */
    public PrometheusThreadCreateTables(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.CREATEDBTABLES.toString();
    }

    @Override
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access Database */
        final PrometheusDataStore myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Create database tables */
            myDatabase.createTables(pManager);

            /* Re-base this set on a null set */
            final PrometheusDataSet myNull = theControl.getNewData();
            final PrometheusDataSet myData = theControl.getData();
            myData.reBase(pManager, myNull);

            /* Derive the new set of updates */
            theControl.deriveUpdates();

            /* State that we have completed */
            pManager.setCompletion();

            /* Return null value */
            return null;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }
}
