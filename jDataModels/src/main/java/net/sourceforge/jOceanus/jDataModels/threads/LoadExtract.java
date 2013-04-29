/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.threads;

import java.io.File;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.preferences.BackupPreferences;
import net.sourceforge.jOceanus.jDataModels.sheets.SpreadSheet;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jPreferenceSet.FileSelector;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;

/**
 * Thread to load data from a spreadsheet. Once the backup is loaded, the current database is loaded and the backup is re-based onto the database so that a
 * correct list of additions, changes and deletions is built. Where data matches data in the database, security is cloned from the database data. Where this is
 * not possible, the items are re-encrypted. These changes remain in memory and should be committed to the database later.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class LoadExtract<T extends DataSet<T>>
        extends LoaderThread<T> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Extract Load";

    /**
     * Data control.
     */
    private final DataControl<T> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public LoadExtract(final ThreadStatus<T> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws JDataException {
        /* Initialise the status window */
        theStatus.initTask("Loading Extract");

        /* Access the Sheet preferences */
        PreferenceManager myMgr = theControl.getPreferenceMgr();
        BackupPreferences myProperties = myMgr.getPreferenceSet(BackupPreferences.class);

        /* Determine the archive name */
        File myBackupDir = new File(myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
        String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);
        WorkBookType myType = myProperties.getEnumValue(BackupPreferences.NAME_BACKUP_TYPE, WorkBookType.class);

        /* Determine the name of the file to load */
        FileSelector myDialog = new FileSelector(theControl.getFrame(), "Select Extract to load", myBackupDir, myPrefix, myType.getExtension());
        myDialog.showDialog();
        File myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
        }

        /* Load workbook */
        SpreadSheet<T> mySheet = theControl.getSpreadSheet();
        T myData = mySheet.loadExtract(theStatus, myFile);

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        Database<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        T myStore = myDatabase.loadDatabase(theStatus);

        /* Initialise the status window */
        theStatus.initTask("Re-applying Security");

        /* Initialise the security, either from database or with a new security control */
        myData.initialiseSecurity(theStatus, myStore);

        /* Re-base the loaded backup onto the database image */
        myData.reBase(myStore);

        /* Return the data */
        return myData;
    }
}
