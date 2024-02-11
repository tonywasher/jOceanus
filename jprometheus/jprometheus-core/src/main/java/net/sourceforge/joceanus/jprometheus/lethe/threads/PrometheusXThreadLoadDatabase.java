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
package net.sourceforge.joceanus.jprometheus.lethe.threads;

import net.sourceforge.joceanus.jprometheus.atlas.threads.PrometheusThreadId;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Thread to load data from the database.
 */
public class PrometheusXThreadLoadDatabase
        implements TethysUIThread<DataSet> {
    /**
     * Data control.
     */
    private final DataControl theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusXThreadLoadDatabase(final DataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.LOADDB.toString();
    }

    @Override
    public DataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Access database */
        final PrometheusXDataStore myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Load database */
            final DataSet myData = theControl.getNewData();
            myDatabase.loadDatabase(pManager, myData);

            /* Check security on the database */
            myData.checkSecurity(pManager);

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
    public void processResult(final DataSet pResult) {
        theControl.setData(pResult);
    }
}