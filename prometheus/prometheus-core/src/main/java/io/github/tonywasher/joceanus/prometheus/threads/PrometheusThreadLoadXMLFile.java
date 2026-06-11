/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.threads;

import io.github.tonywasher.joceanus.gordianknot.util.GordianUtilities;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValuesFormatter;
import io.github.tonywasher.joceanus.prometheus.database.PrometheusDataStore;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import io.github.tonywasher.joceanus.prometheus.toolkit.PrometheusToolkit;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataControl;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIFileSelector;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThread;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.File;

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
     *
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
    public PrometheusDataSet performTask(final TethysUIThreadStatusReport pReport) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pReport.getThreadData();
        final PrometheusSecurityPasswordManager myPasswordMgr = myPromToolkit.getPasswordManager();

        /* Initialise the status window */
        pReport.initTask(getTaskName());

        /* Access the Sheet preferences */
        final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        final File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        final TethysUIFileSelector myDialog = myPromToolkit.getToolkit().getGuiFactory().dialogFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.addExtension(GordianUtilities.SECUREZIPFILE_EXT);
        myDialog.addExtension(GordianUtilities.ZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            pReport.throwCancelException();
        }

        /* Create a new formatter */
        final PrometheusDataValuesFormatter myFormatter = new PrometheusDataValuesFormatter(pReport, myPasswordMgr);

        /* Load data */
        final PrometheusDataSet myNewData = theControl.getNewData();
        myFormatter.loadZipFile(myNewData, myFile);

        /* Create interface */
        final PrometheusDataStore myDatabase = theControl.getDatabase();

        /* Load underlying database */
        final PrometheusDataSet myStore = theControl.getNewData();
        myDatabase.loadDatabase(pReport, myStore);

        /* Check security on the database */
        myStore.checkSecurity(pReport);
        if (myStore.hasUpdates()) {
            /* Store any updates */
            myDatabase.updateDatabase(pReport, myStore);
        }

        /* Initialise the security, either from database or with a new security control */
        myNewData.initialiseSecurity(pReport, myStore);

        /* Re-base the loaded backup onto the database image */
        myNewData.reBase(pReport, myStore);

        /* State that we have completed */
        pReport.setCompletion();

        /* Return the Data */
        return myNewData;
    }

    @Override
    public void processResult(final PrometheusDataSet pResult) {
        theControl.setData(pResult);
    }
}
