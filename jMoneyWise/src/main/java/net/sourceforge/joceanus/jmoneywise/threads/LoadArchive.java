/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.threads;

import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.threads.LoaderThread;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * LoaderThread extension to load an archive spreadsheet.
 * @author Tony
 */
public class LoadArchive
        extends LoaderThread<MoneyWiseData> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Archive Load";

    /**
     * Data Control.
     */
    private final View theControl;

    /**
     * Thread status.
     */
    private final ThreadStatus<MoneyWiseData> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public LoadArchive(final ThreadStatus<MoneyWiseData> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = (View) pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public MoneyWiseData performTask() throws JOceanusException {
        /* Initialise the status window */
        theStatus.initTask("Loading Extract");

        /* Load workbook */
        PreferenceManager myMgr = theControl.getPreferenceMgr();
        MoneyWiseData myData = MoneyWiseSheet.loadArchive(theStatus, myMgr.getPreferenceSet(BackupPreferences.class));

        /* Initialise the status window */
        theStatus.initTask("Analysing Data");

        /* Analyse the Data to ensure that close dates are updated */
        theControl.analyseData(myData);

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        Database<MoneyWiseData> myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Load underlying database */
            MoneyWiseData myStore = myDatabase.loadDatabase(theStatus);

            /* Check security on the database */
            myStore.checkSecurity(theStatus);

            /* Initialise the status window */
            theStatus.initTask("Applying Security");

            /* Initialise the security, either from database or with a new security control */
            myData.initialiseSecurity(theStatus, myStore);

            /* Re-base the loaded spreadsheet onto the database image */
            myData.reBase(myStore);

            /* Return the loaded data */
            return myData;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }
}
