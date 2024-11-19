/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.threads;

import java.io.File;

import net.sourceforge.joceanus.gordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.gordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValuesFormatter;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIFileSelector;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * LoaderThread extension to load an XML backup.
 */
public class PrometheusThreadLoadXMLFile
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
    public PrometheusThreadLoadXMLFile(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RESTOREXML.toString();
    }

    @Override
    public PrometheusDataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pManager.getThreadData();
        final GordianPasswordManager myPasswordMgr = myPromToolkit.getPasswordManager();

        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        final File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        final TethysUIFileSelector myDialog = myPromToolkit.getToolkit().getGuiFactory().dialogFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianUtilities.SECUREZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            pManager.throwCancelException();
        }

        /* Create a new formatter */
        final PrometheusDataValuesFormatter myFormatter = new PrometheusDataValuesFormatter(pManager, myPasswordMgr);

        /* Load data */
        final PrometheusDataSet myNewData = theControl.getNewData();
        myFormatter.loadZipFile(myNewData, myFile);

        /* Create interface */
        final PrometheusDataStore myDatabase = theControl.getDatabase();

        /* Load underlying database */
        final PrometheusDataSet myStore = theControl.getNewData();
        myDatabase.loadDatabase(pManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(pManager);
        if (myStore.hasUpdates()) {
            /* Store any updates */
            myDatabase.updateDatabase(pManager, myStore);
        }

        /* Initialise the security, either from database or with a new security control */
        myNewData.initialiseSecurity(pManager, myStore);

        /* Re-base the loaded backup onto the database image */
        myNewData.reBase(pManager, myStore);

        /* State that we have completed */
        pManager.setCompletion();

        /* Return the Data */
        return myNewData;
    }

    @Override
    public void processResult(final PrometheusDataSet pResult) {
        theControl.setData(pResult);
    }
}
