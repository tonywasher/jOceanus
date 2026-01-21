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
package net.sourceforge.joceanus.prometheus.threads;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.gordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValuesFormatter;
import net.sourceforge.joceanus.prometheus.exc.PrometheusDataException;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThread;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;

import java.io.File;

/**
 * LoaderThread extension to create an XML backup.
 */
public class PrometheusThreadCreateXMLFile
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
     * File indicator.
     */
    private static final String SUFFIX_FILE = "XML";

    /**
     * Data Control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Is this a Secure backup?
     */
    private final boolean isSecure;

    /**
     * Constructor (Event Thread).
     *
     * @param pControl data control
     * @param pSecure  is this a secure backup
     */
    public PrometheusThreadCreateXMLFile(final PrometheusDataControl pControl,
                                         final boolean pSecure) {
        isSecure = pSecure;
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return isSecure
                ? PrometheusThreadId.CREATEXML.toString()
                : PrometheusThreadId.CREATEXTRACT.toString();
    }

    @Override
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pManager.getThreadData();
        final PrometheusSecurityPasswordManager myPasswordMgr = myPromToolkit.getPasswordManager();
        boolean doDelete = false;
        File myFile = null;

        /* Catch Exceptions */
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

            /* Create the name of the file */
            final StringBuilder myName = new StringBuilder(BUFFER_LEN);
            myName.append(myBackupDir);
            myName.append(File.separator);
            myName.append(myPrefix);
            myName.append(SUFFIX_FILE);

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
            myFile = new File(myName.toString() + (isSecure
                    ? GordianUtilities.SECUREZIPFILE_EXT
                    : GordianUtilities.ZIPFILE_EXT));

            /* Access the data */
            final PrometheusDataSet myOldData = theControl.getData();

            /* Create a new formatter */
            final PrometheusDataValuesFormatter myFormatter = new PrometheusDataValuesFormatter(pManager, myPasswordMgr);

            /* Create backup */
            if (isSecure) {
                myFormatter.createBackup(myOldData, myFile);
            } else {
                myFormatter.createExtract(myOldData, myFile);
            }

            /* If this is a secure backup */
            if (isSecure) {
                /* File created, so delete on error */
                doDelete = true;

                /* Check for cancellation */

                /* Initialise the status window */
                pManager.initTask("Reading Backup");

                /* Load workbook */
                final PrometheusDataSet myNewData = theControl.getNewData();
                myFormatter.loadZipFile(myNewData, myFile);

                /* Initialise the security, from the original data */
                myNewData.initialiseSecurity(pManager, myOldData);

                /* Initialise the status window */
                pManager.initTask("Verifying Backup");

                /* Create a difference set between the two data copies */
                final PrometheusDataSet myDiff = myNewData.getDifferenceSet(pManager, myOldData);

                /* If the difference set is non-empty */
                if (!myDiff.isEmpty()) {
                    /* Throw an exception */
                    throw new PrometheusDataException(myDiff, "Backup is inconsistent");
                }

                /* OK so switch off flag */
                doDelete = false;
            }

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
