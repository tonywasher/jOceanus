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
package net.sourceforge.joceanus.jthemis.threads.swing;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.swing.MetisFileSelector;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisCancelException;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmReporter.ReportTask;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisBackup;

/**
 * Thread to handle subVersion backups.
 * @author Tony Washer
 */
public class ThemisSubversionRestore
        extends ThemisScmThread {
    /**
     * ReportTask.
     */
    private final ReportTask theStatus;

    /**
     * The preference manager.
     */
    private final MetisPreferenceManager thePreferenceMgr;

    /**
     * The secure manager.
     */
    private final GordianHashManager theSecureMgr;

    /**
     * Constructor (Event Thread).
     * @param pReport the report object
     */
    public ThemisSubversionRestore(final ReportTask pReport) {
        /* Call super-constructor */
        super(pReport);

        /* Store passed parameters */
        theStatus = pReport;
        thePreferenceMgr = pReport.getPreferenceMgr();
        theSecureMgr = pReport.getSecureMgr();
    }

    @Override
    public Void doInBackground() throws OceanusException {
        /* Access the BackUp preferences */
        ThemisSvnPreferences mySVNPreferences = thePreferenceMgr.getPreferenceSet(ThemisSvnPreferences.class);
        PrometheusBackupPreferences myBUPreferences = thePreferenceMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Access preferences */
        File myRepo = new File(mySVNPreferences.getStringValue(ThemisSvnPreferenceKey.INSTALL));
        File myBackupDir = new File(myBUPreferences.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));
        String myPrefix = mySVNPreferences.getStringValue(ThemisSvnPreferenceKey.PFIX);

        /* Determine the name of the file to load */
        MetisFileSelector myDialog = new MetisFileSelector(theStatus.getFrame(), "Select Backup to restore", myBackupDir, myPrefix, GordianZipReadFile.ZIPFILE_EXT);
        myDialog.showDialog();
        File myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new ThemisCancelException("Operation Cancelled");
        }

        /* Determine the name of the repository */
        String myName = myFile.getName();
        myName = myName.substring(myPrefix.length());
        if (myName.endsWith(GordianZipReadFile.ZIPFILE_EXT)) {
            myName = myName.substring(0, myName.length() - GordianZipReadFile.ZIPFILE_EXT.length());
        }
        myRepo = new File(myRepo.getPath(), myName);

        /* restore the backup */
        ThemisBackup myAccess = new ThemisBackup(this, thePreferenceMgr);
        myAccess.loadRepository(myRepo, theSecureMgr, myFile);

        /* Return nothing */
        return null;
    }
}
