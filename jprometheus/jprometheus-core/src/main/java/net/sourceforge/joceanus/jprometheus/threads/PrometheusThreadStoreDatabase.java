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

import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Thread to store changes in the DataSet to a database.
 */
public class PrometheusThreadStoreDatabase
        implements TethysUIThread<Void> {
    /**
     * Data control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadStoreDatabase(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.STOREDB.toString();
    }

    @Override
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Create interface */
        final PrometheusDataStore myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Store database */
            myDatabase.updateDatabase(pManager, theControl.getUpdates());

            /* Load database */
            final PrometheusDataSet myStore = theControl.getNewData();
            myDatabase.loadDatabase(pManager, myStore);

            /* Create a difference set between the two data copies */
            final PrometheusDataSet myData = theControl.getData();
            final PrometheusDataSet myDiff = myData.getDifferenceSet(pManager, myStore);

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new PrometheusDataException(myDiff, "DataStore is inconsistent");
            }

            /* DataSet version is now zero */
            myData.setVersion(0);

            /* Derive new update list */
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
