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
package net.sourceforge.joceanus.jthemis.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.swing.SecureManager;
import net.sourceforge.joceanus.jgordianknot.zip.ZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.FileSelector;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisCancelException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;
import net.sourceforge.joceanus.jthemis.svn.tasks.Backup;

/**
 * Thread to handle subVersion backups.
 * @author Tony Washer
 */
public class SubversionRestore
        extends ScmThread {
    /**
     * ReportTask.
     */
    private final ReportTask theStatus;

    /**
     * The preference manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * The secure manager.
     */
    private final SecureManager theSecureMgr;

    /**
     * Constructor (Event Thread).
     * @param pReport the report object
     */
    public SubversionRestore(final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store passed parameters */
        theStatus = pReport;
        thePreferenceMgr = pReport.getPreferenceMgr();
        theSecureMgr = pReport.getSecureMgr();
    }

    @Override
    public Void doInBackground() throws JOceanusException {
        Backup myAccess = null;

        /* Access the BackUp preferences */
        SubVersionPreferences mySVNPreferences = thePreferenceMgr.getPreferenceSet(SubVersionPreferences.class);
        BackupPreferences myBUPreferences = thePreferenceMgr.getPreferenceSet(BackupPreferences.class);

        /* Access preferences */
        File myRepo = new File(mySVNPreferences.getStringValue(SubVersionPreferences.NAME_SVN_DIR));
        File myBackupDir = new File(myBUPreferences.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
        String myPrefix = mySVNPreferences.getStringValue(SubVersionPreferences.NAME_REPO_PFIX);

        /* Determine the name of the file to load */
        FileSelector myDialog = new FileSelector(theStatus.getFrame(), "Select Backup to restore", myBackupDir, myPrefix, ZipReadFile.ZIPFILE_EXT);
        myDialog.showDialog();
        File myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new JThemisCancelException("Operation Cancelled");
        }

        /* Determine the name of the repository */
        String myName = myFile.getName();
        myName = myName.substring(myPrefix.length());
        if (myName.endsWith(ZipReadFile.ZIPFILE_EXT)) {
            myName = myName.substring(0, myName.length() - ZipReadFile.ZIPFILE_EXT.length());
        }
        myRepo = new File(myRepo.getPath(), myName);

        /* restore the backup */
        myAccess = new Backup(this, thePreferenceMgr);
        myAccess.loadRepository(myRepo, theSecureMgr, myFile);

        /* Return nothing */
        return null;
    }
}
