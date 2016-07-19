/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;
import net.sourceforge.joceanus.jthemis.ThemisCancelException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisBackup;

/**
 * Thread to handle subVersion backups.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class ThemisSubversionRestore<N, I>
        implements MetisThread<Void, N, I> {
    @Override
    public String getTaskName() {
        return ThemisThreadId.RESTORESVN.toString();
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access details from toolkit */
        MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();
        GordianHashManager mySecureMgr = pToolkit.getSecurityManager();

        /* Access the BackUp preferences */
        ThemisSvnPreferences mySVNPreferences = myPreferences.getPreferenceSet(ThemisSvnPreferences.class);
        PrometheusBackupPreferences myBUPreferences = myPreferences.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Access preferences */
        File myRepo = new File(mySVNPreferences.getStringValue(ThemisSvnPreferenceKey.INSTALL));
        File myBackupDir = new File(myBUPreferences.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));
        String myPrefix = mySVNPreferences.getStringValue(ThemisSvnPreferenceKey.PFIX);

        /* Determine the name of the file to load */
        TethysFileSelector myDialog = pToolkit.getGuiFactory().newFileSelector();
        myDialog.setTitle("Select Backup to restore");
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianZipReadFile.ZIPFILE_EXT);
        File myFile = myDialog.selectFile();

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
        ThemisBackup myAccess = new ThemisBackup(myManager, myPreferences);
        myAccess.loadRepository(myRepo, mySecureMgr, myFile);

        /* Return nothing */
        return null;
    }
}
