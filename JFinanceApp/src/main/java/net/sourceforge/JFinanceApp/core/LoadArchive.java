/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.core;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.preferences.BackupPreferences;
import net.sourceforge.JDataModels.threads.LoaderThread;
import net.sourceforge.JDataModels.threads.ThreadStatus;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.sheets.FinanceSheet;
import net.sourceforge.JFinanceApp.views.View;
import net.sourceforge.JPreferenceSet.PreferenceManager;

/**
 * LoaderThread extension to load an archive spreadsheet.
 * @author Tony
 */
public class LoadArchive extends LoaderThread<FinanceData> {
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
    private final ThreadStatus<FinanceData> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public LoadArchive(final ThreadStatus<FinanceData> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = (View) pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public FinanceData performTask() throws JDataException {
        /* Initialise the status window */
        theStatus.initTask("Loading Extract");

        /* Load workbook */
        PreferenceManager myMgr = theControl.getPreferenceMgr();
        FinanceData myData = FinanceSheet.loadArchive(theStatus,
                                                      myMgr.getPreferenceSet(BackupPreferences.class));

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        Database<FinanceData> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        FinanceData myStore = myDatabase.loadDatabase(theStatus);

        /* Check security on the database */
        myStore.checkSecurity(theStatus);

        /* Initialise the status window */
        theStatus.initTask("Applying Security");

        /* Initialise the security, either from database or with a new security control */
        myData.initialiseSecurity(theStatus, myStore);

        /* Initialise the status window */
        theStatus.initTask("Analysing Data");

        /* Analyse the Data to ensure that close dates are updated */
        theControl.analyseData(myData);

        /* Re-base the loaded spreadsheet onto the database image */
        myData.reBase(myStore);

        /* Return the loaded data */
        return myData;
    }
}
