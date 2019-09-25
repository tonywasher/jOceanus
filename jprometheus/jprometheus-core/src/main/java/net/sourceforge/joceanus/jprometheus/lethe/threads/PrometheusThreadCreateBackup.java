/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jprometheus.service.sheet.MetisSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Thread to create an encrypted backup of a data set.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public class PrometheusThreadCreateBackup<T extends DataSet<T, E>, E extends Enum<E>>
        implements MetisThread<Void> {
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
    private final DataControl<T, E> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadCreateBackup(final DataControl<T, E> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.CREATEBACKUP.toString();
    }

    @Override
    public Void performTask(final MetisToolkit pToolkit) throws OceanusException {
        /* Access the thread manager */
        final MetisThreadManager myManager = pToolkit.getThreadManager();
        final GordianSecurityManager mySecurityMgr = pToolkit.getSecurityManager();
        boolean doDelete = false;
        File myFile = null;

        try {
            /* Initialise the status window */
            myManager.initTask(getTaskName());

            /* Access the Backup preferences */
            final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
            final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

            /* Determine the archive name */
            final String myBackupDir = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR);
            final String myPrefix = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPPFIX);
            final Boolean doTimeStamp = myProperties.getBooleanValue(PrometheusBackupPreferenceKey.BACKUPTIME);
            final MetisSheetWorkBookType myType = myProperties.getEnumValue(PrometheusBackupPreferenceKey.BACKUPTYPE, MetisSheetWorkBookType.class);

            /* Create the name of the file */
            final StringBuilder myName = new StringBuilder(BUFFER_LEN);
            myName.append(myBackupDir);
            myName.append(File.separator);
            myName.append(myPrefix);

            /* If we are doing time-stamps */
            if (doTimeStamp) {
                /* Obtain the current date/time */
                final TethysDate myNow = new TethysDate();

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
            myFile = new File(myName.toString() + GordianSecurityManager.SECUREZIPFILE_EXT);

            /* Create backup */
            final PrometheusSpreadSheet<T> mySheet = theControl.getSpreadSheet();
            final T myOldData = theControl.getData();
            mySheet.createBackup(myManager, myOldData, myFile, myType);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            myManager.initTask("Verifying Backup");

            /* Load workbook */
            final T myNewData = theControl.getNewData();
            mySheet.loadBackup(myManager, mySecurityMgr, myNewData, myFile);

            /* Create a difference set between the two data copies */
            final DataSet<T, ?> myDiff = myNewData.getDifferenceSet(myManager, myOldData);

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new PrometheusDataException(myDiff, "Backup is inconsistent");
            }

            /* OK so switch off flag */
            doDelete = false;

            /* State that we have completed */
            myManager.setCompletion();

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
