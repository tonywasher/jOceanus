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
package uk.co.tolcroft.models.threads;

import java.io.File;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;
import net.sourceforge.JDataManager.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.BackupPreferences;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.ui.FileSelector;
import uk.co.tolcroft.models.views.DataControl;

public class LoadBackup<T extends DataSet<T>> extends LoaderThread<T> {
    /* Task description */
    private static String theTask = "Backup Restoration";

    /* Properties */
    private DataControl<T> theControl = null;
    private ThreadStatus<T> theStatus = null;

    /* Constructor (Event Thread) */
    public LoadBackup(DataControl<T> pControl) {
        /* Call super-constructor */
        super(theTask, pControl);

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = new ThreadStatus<T>(this, theControl);

        /* Show the Status bar */
        showStatusBar();
    }

    @Override
    public T performTask() throws Exception {
        T myData = null;
        T myStore;
        Database<T> myDatabase;
        SpreadSheet<T> mySheet;
        File myFile;

        /* Initialise the status window */
        theStatus.initTask("Loading Backup");

        /* Access the Sheet preferences */
        BackupPreferences myProperties = PreferenceManager.getPreferenceSet(BackupPreferences.class);

        /* Determine the archive name */
        File myBackupDir = new File(myProperties.getStringValue(BackupPreferences.nameBackupDir));
        String myPrefix = myProperties.getStringValue(BackupPreferences.nameBackupPfix);

        /* Determine the name of the file to load */
        FileSelector myDialog = new FileSelector(theControl.getFrame(), "Select Backup to load", myBackupDir,
                myPrefix, ".zip");
        myDialog.showDialog();
        myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new ModelException(ExceptionClass.EXCEL, "Operation Cancelled");
        }

        /* Load workbook */
        mySheet = theControl.getSpreadSheet();
        myData = mySheet.loadBackup(theStatus, myFile);

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        myDatabase = theControl.getDatabase();

        /* Load underlying database */
        myStore = myDatabase.loadDatabase(theStatus);

        /* Re-base the loaded backup onto the database image */
        myData.reBase(myStore);

        /* Return the Data */
        return myData;
    }
}
