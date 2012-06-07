/*******************************************************************************
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
package uk.co.tolcroft.finance.core;

import net.sourceforge.JDataManager.JDataException;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.sheets.FinanceSheet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.threads.LoaderThread;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public class LoadArchive extends LoaderThread<FinanceData> {
    /* Task description */
    private static final String TASK_NAME = "Archive Load";

    /* Properties */
    private DataControl<FinanceData> theControl = null;
    private ThreadStatus<FinanceData> theStatus = null;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public LoadArchive(final ThreadStatus<FinanceData> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    /* Background task (Worker Thread) */
    @Override
    public FinanceData performTask() throws JDataException {
        FinanceData myData = null;
        FinanceData myStore;
        Database<FinanceData> myDatabase;

        /* Initialise the status window */
        theStatus.initTask("Loading Extract");

        /* Load workbook */
        myData = FinanceSheet.loadArchive(theStatus);

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        myDatabase = theControl.getDatabase();

        /* Load underlying database */
        myStore = myDatabase.loadDatabase(theStatus);

        /* Initialise the status window */
        theStatus.initTask("Applying Security");

        /* Initialise the security, either from database or with a new security control */
        myData.initialiseSecurity(theStatus, myStore);

        /* Initialise the status window */
        theStatus.initTask("Analysing Data");

        /* Analyse the Data to ensure that close dates are updated */
        myData.analyseData(theControl);

        /* Re-base the loaded spreadsheet onto the database image */
        myData.reBase(myStore);

        /* Return the loaded data */
        return myData;
    }
}
