/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jprometheus.PrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * Thread to create an encrypted backup of a data set.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 * @param <N> the node type
 * @param <I> the icon type
 */
public class PrometheusThreadCreateBackup<T extends DataSet<T, E>, E extends Enum<E>, N, I>
        implements MetisThread<Void, N, I> {
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
    private final DataControl<T, E, N, I> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadCreateBackup(final DataControl<T, E, N, I> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.CREATEBACKUP.toString();
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        GordianHashManager mySecurityMgr = pToolkit.getSecurityManager();
        boolean doDelete = false;
        File myFile = null;

        try {
            /* Initialise the status window */
            myManager.initTask(getTaskName());

            /* Access the Backup preferences */
            MetisPreferenceManager myMgr = theControl.getPreferenceManager();
            PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

            /* Determine the archive name */
            String myBackupDir = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR);
            String myPrefix = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPPFIX);
            Boolean doTimeStamp = myProperties.getBooleanValue(PrometheusBackupPreferenceKey.BACKUPTIME);
            MetisWorkBookType myType = myProperties.getEnumValue(PrometheusBackupPreferenceKey.BACKUPTYPE, MetisWorkBookType.class);

            /* Create the name of the file */
            StringBuilder myName = new StringBuilder(BUFFER_LEN);
            myName.append(myBackupDir);
            myName.append(File.separator);
            myName.append(myPrefix);

            /* If we are doing time-stamps */
            if (doTimeStamp) {
                /* Obtain the current date/time */
                TethysDate myNow = new TethysDate();

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
            myFile = new File(myName.toString() + GordianZipReadFile.ZIPFILE_EXT);

            /* Create backup */
            PrometheusSpreadSheet<T> mySheet = theControl.getSpreadSheet();
            T myOldData = theControl.getData();
            mySheet.createBackup(myManager, myOldData, myFile, myType);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            myManager.initTask("Verifying Backup");

            /* Load workbook */
            T myNewData = theControl.getNewData();
            mySheet.loadBackup(myManager, mySecurityMgr, myNewData, myFile);

            /* Create a difference set between the two data copies */
            DataSet<T, ?> myDiff = myNewData.getDifferenceSet(myManager, myOldData);

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
            /* Delete the file */
            if ((doDelete) && (!myFile.delete())) {
                doDelete = false;
            }
        }

        /* Return nothing */
        return null;
    }
}
