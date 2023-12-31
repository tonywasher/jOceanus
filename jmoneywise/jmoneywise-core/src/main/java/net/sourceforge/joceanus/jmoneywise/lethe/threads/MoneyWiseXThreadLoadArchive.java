/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.lethe.threads;

import net.sourceforge.joceanus.jmoneywise.atlas.threads.MoneyWiseThreadId;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.sheets.ArchiveLoader;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseXView;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusXToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * LoaderThread extension to load an archive spreadsheet.
 */
public class MoneyWiseXThreadLoadArchive
        implements TethysUIThread<MoneyWiseData> {
    /**
     * Data Control.
     */
    private final MoneyWiseXView theView;

    /**
     * Constructor (Event Thread).
     * @param pView the view
     */
    public MoneyWiseXThreadLoadArchive(final MoneyWiseXView pView) {
        theView = pView;
    }

    @Override
    public String getTaskName() {
        return MoneyWiseThreadId.LOADARCHIVE.toString();
    }

    @Override
    public MoneyWiseData performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Load workbook */
        final PrometheusXToolkit myPromToolkit = (PrometheusXToolkit) pManager.getThreadData();
        final PrometheusPreferenceManager myMgr = myPromToolkit.getPreferenceManager();
        final ArchiveLoader myLoader = new ArchiveLoader(myPromToolkit.getToolkit().getGuiFactory());
        final MoneyWiseData myData = theView.getNewData();
        myLoader.loadArchive(pManager, myData, myMgr.getPreferenceSet(PrometheusBackupPreferences.class));

        /* Initialise the status window */
        pManager.initTask("Analysing Data");

        /* Analyse the Data to ensure that close dates are updated */
        theView.analyseData(myData);

        /* Initialise the status window */
        pManager.initTask("Accessing DataStore");

        /* Create interface */
        final PrometheusXDataStore myDatabase = theView.getDatabase();

        /* Protect against failures */
        try {
            /* Load underlying database */
            final MoneyWiseData myStore = theView.getNewData();
            myDatabase.loadDatabase(pManager, myStore);

            /* Check security on the database */
            myStore.checkSecurity(pManager);

            /* Initialise the security, either from database or with a new security control */
            myData.initialiseSecurity(pManager, myStore);

            /* Re-base the loaded spreadsheet onto the database image */
            myData.reBase(pManager, myStore);

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
    public void processResult(final MoneyWiseData pResult) {
        theView.setData(pResult);
    }
}
