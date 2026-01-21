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
package io.github.tonywasher.joceanus.prometheus.threads;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusDataStore;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;

/**
 * Thread to load data from the database.
 */
public class PrometheusThreadLoadDatabase
        implements TethysUIThread<PrometheusDataSet> {
    /**
     * Data control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor (Event Thread).
     *
     * @param pControl data control
     */
    public PrometheusThreadLoadDatabase(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.LOADDB.toString();
    }

    @Override
    public PrometheusDataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Access database */
        final PrometheusDataStore myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Load database */
            final PrometheusDataSet myData = theControl.getNewData();
            myDatabase.loadDatabase(pManager, myData);

            /* Check security on the database */
            myData.checkSecurity(pManager);
            if (myData.hasUpdates()) {
                /* Store any updates */
                myDatabase.updateDatabase(pManager, myData);
            }

            /* State that we have completed */
            pManager.setCompletion();

            /* Return the loaded data */
            return myData;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }

    @Override
    public void processResult(final PrometheusDataSet pResult) {
        theControl.setData(pResult);
    }
}
