/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;
import net.sourceforge.joceanus.jthemis.ThemisCancelException;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnBackup;

/**
 * Thread to handle subVersion backups.
 */
public class ThemisSubversionRestore
        implements MetisThread<Void> {
    @Override
    public String getTaskName() {
        return ThemisThreadId.RESTORESVN.toString();
    }

    @Override
    public Void performTask(final MetisToolkit pToolkit) throws OceanusException {
        /* Access details from toolkit */
        final MetisThreadManager myManager = pToolkit.getThreadManager();
        final MetisPreferenceManager myPreferenceMgr = pToolkit.getPreferenceManager();
        final GordianSecurityManager mySecureMgr = pToolkit.getSecurityManager();

        /* Access the BackUp preferences */
        final ThemisSvnPreferences myPreferences = myPreferenceMgr.getPreferenceSet(ThemisSvnPreferences.class);

        /* Access preferences */
        File myRepo = new File(myPreferences.getStringValue(ThemisSvnPreferenceKey.INSTALL));
        final File myBackupDir = new File(myPreferences.getStringValue(ThemisSvnPreferenceKey.BACKUP));
        final String myPrefix = myPreferences.getStringValue(ThemisSvnPreferenceKey.PFIX);

        /* Determine the name of the file to load */
        final TethysFileSelector myDialog = pToolkit.getGuiFactory().newFileSelector();
        myDialog.setTitle("Select Backup to restore");
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianSecurityManager.SECUREZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new ThemisCancelException("Operation Cancelled");
        }

        /* Determine the name of the repository */
        String myName = myFile.getName();
        myName = myName.substring(myPrefix.length());
        if (myName.endsWith(GordianSecurityManager.SECUREZIPFILE_EXT)) {
            myName = myName.substring(0, myName.length() - GordianSecurityManager.SECUREZIPFILE_EXT.length());
        }
        myRepo = new File(myRepo.getPath(), myName);

        /* restore the backup */
        final ThemisSvnBackup myAccess = new ThemisSvnBackup(myManager, myPreferenceMgr);
        myAccess.loadRepository(myRepo, mySecureMgr, myFile);

        /* Return nothing */
        return null;
    }
}
