/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.svn.threads;

import java.io.File;

import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.preferences.BackupPreferences;
import net.sourceforge.joceanus.jdatamodels.sheets.SpreadSheet;
import net.sourceforge.joceanus.jdatamodels.threads.ThreadStatus;
import net.sourceforge.joceanus.jdatamodels.threads.WorkerThread;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jpreferenceset.FileSelector;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;
import net.sourceforge.joceanus.jthemis.svn.tasks.Backup;

/**
 * Thread to handle subVersion backups.
 * @author Tony Washer
 * @param <T> the dataset type
 */
public class SubversionRestore<T extends DataSet<T, ?>>
        extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Subversion Backup Restoration";

    /**
     * Data Control.
     */
    private final DataControl<?> theControl;

    /**
     * ThreadStatus.
     */
    private final ThreadStatus<?> theStatus;

    /**
     * The preference manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     * @param pPreferenceMgr the preference manager
     */
    public SubversionRestore(final ThreadStatus<T> pStatus,
                             final PreferenceManager pPreferenceMgr) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();
        thePreferenceMgr = pPreferenceMgr;

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JOceanusException {
        Backup myAccess = null;

        /* Initialise the status window */
        theStatus.initTask("Restoring Subversion Backup");

        /* Create a clone of the security control */
        DataSet<?, ?> myData = theControl.getData();
        SecureManager mySecure = myData.getSecurity();

        /* Access the BackUp preferences */
        SubVersionPreferences mySVNPreferences = thePreferenceMgr.getPreferenceSet(SubVersionPreferences.class);
        BackupPreferences myBUPreferences = thePreferenceMgr.getPreferenceSet(BackupPreferences.class);

        /* Access preferences */
        File myRepo = new File(mySVNPreferences.getStringValue(SubVersionPreferences.NAME_SVN_DIR));
        File myBackupDir = new File(myBUPreferences.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
        String myPrefix = mySVNPreferences.getStringValue(SubVersionPreferences.NAME_REPO_PFIX);

        /* Determine the name of the file to load */
        FileSelector myDialog = new FileSelector(theControl.getFrame(), "Select Backup to restore", myBackupDir, myPrefix, SpreadSheet.ZIPFILE_EXT);
        myDialog.showDialog(theStatus.getLogger());
        File myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new JOceanusException("Operation Cancelled");
        }

        /* Determine the name of the repository */
        String myName = myFile.getName();
        myName = myName.substring(myPrefix.length());
        if (myName.endsWith(SpreadSheet.ZIPFILE_EXT)) {
            myName = myName.substring(0, myName.length()
                                         - SpreadSheet.ZIPFILE_EXT.length());
        }
        myRepo = new File(myRepo.getPath(), myName);

        /* restore the backup */
        myAccess = new Backup(theStatus, thePreferenceMgr);
        myAccess.loadRepository(myRepo, mySecure, myFile);

        /* Return nothing */
        return null;
    }
}
