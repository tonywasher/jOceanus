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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.gordianknot.util.GordianUtilities;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import io.github.tonywasher.joceanus.metis.toolkit.MetisToolkit;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.exc.PrometheusDataException;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import io.github.tonywasher.joceanus.prometheus.sheets.PrometheusSpreadSheet;
import io.github.tonywasher.joceanus.prometheus.toolkit.PrometheusToolkit;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataControl;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThread;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;

import java.io.File;

/**
 * Thread to create an encrypted backup of a data set.
 *
 * @author Tony Washer
 */
public class PrometheusThreadCreateBackup
        implements TethysUIThread<Void> {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Number 10.
     */
    private static final int TEN = 10;

    /**
     * Data Control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor (Event Thread).
     *
     * @param pControl data control
     */
    public PrometheusThreadCreateBackup(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.CREATEBACKUP.toString();
    }

    @Override
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pManager.getThreadData();
        final PrometheusSecurityPasswordManager myPasswordMgr = myPromToolkit.getPasswordManager();
        boolean doDelete = false;
        File myFile = null;

        try {
            /* Initialise the status window */
            pManager.initTask(getTaskName());

            /* Access the Backup preferences */
            final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
            final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

            /* Determine the archive name */
            final String myBackupDir = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR);
            final String myPrefix = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPPFIX);
            final boolean doTimeStamp = myProperties.getBooleanValue(PrometheusBackupPreferenceKey.BACKUPTIME);
            final PrometheusSheetWorkBookType myType = myProperties.getEnumValue(PrometheusBackupPreferenceKey.BACKUPTYPE, PrometheusSheetWorkBookType.class);

            /* Create the name of the file */
            final StringBuilder myName = new StringBuilder(BUFFER_LEN);
            myName.append(myBackupDir);
            myName.append(File.separator);
            myName.append(myPrefix);

            /* If we are doing time-stamps */
            if (doTimeStamp) {
                /* Obtain the current date/time */
                final OceanusDate myNow = new OceanusDate();

                myName.append(myNow.getYear());
                if (myNow.getMonth() < TEN) {
                    myName.append('0');
                }
                myName.append(myNow.getMonth());
                if (myNow.getDay() < TEN) {
                    myName.append('0');
                }
                myName.append(myNow.getDay());
            }

            /* Set the standard backup name */
            myFile = new File(myName.toString() + GordianUtilities.SECUREZIPFILE_EXT);

            /* Create backup */
            final PrometheusSpreadSheet mySheet = theControl.getSpreadSheet();
            final PrometheusDataSet myOldData = theControl.getData();
            mySheet.createBackup(pManager, myOldData, myFile, myType);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            pManager.initTask("Verifying Backup");

            /* Load workbook */
            final PrometheusDataSet myNewData = theControl.getNewData();
            mySheet.loadBackup(pManager, myPasswordMgr, myNewData, myFile);

            /* Create a difference set between the two data copies */
            final PrometheusDataSet myDiff = myNewData.getDifferenceSet(pManager, myOldData);

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new PrometheusDataException(myDiff, "Backup is inconsistent");
            }

            /* OK so switch off flag */
            doDelete = false;

            /* State that we have completed */
            pManager.setCompletion();

            /* Delete file on error */
        } finally {
            /* Try to delete the file if required */
            if (doDelete) {
                MetisToolkit.cleanUpFile(myFile);
            }
        }

        /* Return nothing */
        return null;
    }
}
