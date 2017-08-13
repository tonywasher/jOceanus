/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.threads;

import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.sheets.ArchiveLoader;
import net.sourceforge.joceanus.jmoneywise.lethe.views.View;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LoaderThread extension to load an archive spreadsheet.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MoneyWiseThreadLoadArchive<N, I>
        implements MetisThread<MoneyWiseData, N, I> {
    /**
     * Data Control.
     */
    private final View<N, I> theView;

    /**
     * Constructor (Event Thread).
     * @param pView the view
     */
    public MoneyWiseThreadLoadArchive(final View<N, I> pView) {
        theView = pView;
    }

    @Override
    public String getTaskName() {
        return MoneyWiseThreadId.LOADARCHIVE.toString();
    }

    @Override
    public MoneyWiseData performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        final MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();

        /* Initialise the status window */
        myManager.initTask(getTaskName());

        /* Load workbook */
        final MetisPreferenceManager myMgr = pToolkit.getPreferenceManager();
        final ArchiveLoader myLoader = new ArchiveLoader();
        final MoneyWiseData myData = theView.getNewData();
        myLoader.loadArchive(myManager, myData, myMgr.getPreferenceSet(PrometheusBackupPreferences.class));

        /* Initialise the status window */
        myManager.initTask("Analysing Data");

        /* Analyse the Data to ensure that close dates are updated */
        theView.analyseData(myData);

        /* Initialise the status window */
        myManager.initTask("Accessing DataStore");

        /* Create interface */
        final PrometheusDataStore<MoneyWiseData> myDatabase = theView.getDatabase();

        /* Protect against failures */
        try {
            /* Load underlying database */
            final MoneyWiseData myStore = theView.getNewData();
            myDatabase.loadDatabase(myManager, myStore);

            /* Check security on the database */
            myStore.checkSecurity(myManager);

            /* Initialise the security, either from database or with a new security control */
            myData.initialiseSecurity(myManager, myStore);

            /* Re-base the loaded spreadsheet onto the database image */
            myData.reBase(myManager, myStore);

            /* State that we have completed */
            myManager.setCompletion();

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
