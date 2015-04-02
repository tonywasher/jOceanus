/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.threads.swing;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.zip.ZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.swing.FileSelector;
import net.sourceforge.joceanus.jprometheus.JPrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.preference.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.SpreadSheet;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Thread to load changes from an encrypted backup. Once the backup is loaded, the current database is loaded and the backup is re-based onto the database so
 * that a correct list of additions, changes and deletions is built. These changes remain in memory and should be committed to the database later.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public class LoadBackup<T extends DataSet<T, E>, E extends Enum<E>>
        extends LoaderThread<T, E> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Backup Restoration";

    /**
     * Data control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T, E> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public LoadBackup(final ThreadStatus<T, E> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the Status bar */
        showStatusBar();
    }

    @Override
    public T performTask() throws JOceanusException {
        /* Initialise the status window */
        theStatus.initTask("Loading Backup");

        /* Access the Sheet preferences */
        PreferenceManager myMgr = theControl.getPreferenceManager();
        BackupPreferences myProperties = myMgr.getPreferenceSet(BackupPreferences.class);

        /* Determine the archive name */
        File myBackupDir = new File(myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
        String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);

        /* Determine the name of the file to load */
        FileSelector myDialog = new FileSelector(theControl.getFrame(), "Select Backup to load", myBackupDir, myPrefix, ZipReadFile.ZIPFILE_EXT);
        myDialog.showDialog();
        File myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new JPrometheusCancelException("Operation Cancelled");
        }

        /* Load workbook */
        SpreadSheet<T> mySheet = theControl.getSpreadSheet();
        T myData = mySheet.loadBackup(theStatus, myFile);

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        Database<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        T myStore = myDatabase.loadDatabase(theStatus);

        /* Check security on the database */
        myStore.checkSecurity(theStatus);

        /* Re-base the loaded backup onto the database image */
        myData.reBase(theStatus, myStore);

        /* Return the Data */
        return myData;
    }
}
