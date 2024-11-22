/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.threads;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.sheets.MoneyWiseArchiveLoader;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.prometheus.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadManager;

/**
 * LoaderThread extension to load an archive spreadsheet.
 */
public class MoneyWiseThreadLoadArchive
        implements TethysUIThread<MoneyWiseDataSet> {
    /**
     * Data Control.
     */
    private final MoneyWiseView theView;

    /**
     * Constructor (Event Thread).
     * @param pView the view
     */
    public MoneyWiseThreadLoadArchive(final MoneyWiseView pView) {
        theView = pView;
    }

    @Override
    public String getTaskName() {
        return MoneyWiseThreadId.LOADARCHIVE.toString();
    }

    @Override
    public MoneyWiseDataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Load workbook */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pManager.getThreadData();
        final PrometheusPreferenceManager myMgr = myPromToolkit.getPreferenceManager();
        final MoneyWiseArchiveLoader myLoader = new MoneyWiseArchiveLoader(myPromToolkit.getToolkit().getGuiFactory());
        final MoneyWiseDataSet myData = theView.getNewData();
        myLoader.loadArchive(pManager, myData, myMgr.getPreferenceSet(PrometheusBackupPreferences.class));

        /* Initialise the status window */
        pManager.initTask("Analysing Data");

        /* Analyse the Data to ensure that close dates are updated */
        theView.analyseData(myData);

        /* Initialise the status window */
        pManager.initTask("Accessing DataStore");

        /* Create interface */
        final PrometheusDataStore myDatabase = theView.getDatabase();

        /* Protect against failures */
        try {
            /* Load underlying database */
            final MoneyWiseDataSet myStore = theView.getNewData();
            myDatabase.loadDatabase(pManager, myStore);

            /* Check security on the database */
            myStore.checkSecurity(pManager);
            if (myStore.hasUpdates()) {
                /* Store any updates */
                myDatabase.updateDatabase(pManager, myStore);
            }

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
    public void processResult(final MoneyWiseDataSet pResult) {
        theView.setData(pResult);
    }
}
