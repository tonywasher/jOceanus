/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.jmetis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jprometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIFileSelector;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Thread to load changes from an encrypted backup. Once the backup is loaded, the current database
 * is loaded and the backup is re-based onto the database so that a correct list of additions,
 * changes and deletions is built. These changes remain in memory and should be committed to the
 * database later.
 * @author Tony Washer
 */
public class PrometheusThreadLoadBackup
        implements TethysUIThread<PrometheusDataSet> {
    /**
     * Select Backup Task.
     */
    private static final String TASK_SELECTFILE = PrometheusThreadResource.TASK_SELECT_BACKUP.getValue();

    /**
     * Data control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadLoadBackup(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RESTOREBACKUP.toString();
    }

    @Override
    public PrometheusDataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pManager.getThreadData();
        final MetisToolkit myToolkit = myPromToolkit.getToolkit();
        final GordianPasswordManager myPasswordMgr = myPromToolkit.getPasswordManager();

        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        final File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        final TethysUIFileSelector myDialog = myToolkit.getGuiFactory().dialogFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianUtilities.SECUREZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            pManager.throwCancelException();
        }

        /* Load workbook */
        final PrometheusSpreadSheet mySheet = theControl.getSpreadSheet();
        final PrometheusDataSet myData = theControl.getNewData();
        mySheet.loadBackup(pManager, myPasswordMgr, myData, myFile);

        /* Create interface */
        final PrometheusDataStore myDatabase = theControl.getDatabase();

        /* Load underlying database */
        final PrometheusDataSet myStore = theControl.getNewData();
        myDatabase.loadDatabase(pManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(pManager);
        if (myData.hasUpdates()) {
            /* Store any updates */
            myDatabase.updateDatabase(pManager, myData);
        }

        /* Re-base the loaded backup onto the database image */
        myData.reBase(pManager, myStore);

        /* State that we have completed */
        pManager.setCompletion();

        /* Return the Data */
        return myData;
    }

    @Override
    public void processResult(final PrometheusDataSet pResult) {
        theControl.setData(pResult);
    }
}
