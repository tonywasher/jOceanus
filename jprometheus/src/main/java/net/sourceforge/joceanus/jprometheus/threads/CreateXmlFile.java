/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.zip.ZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jprometheus.JPrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;

/**
 * LoaderThread extension to create an XML backup.
 * @param <T> the DataSet type
 * @param <E> the Data list type
 */
public class CreateXmlFile<T extends DataSet<T, E>, E extends Enum<E>>
        extends LoaderThread<T, E> {
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
    protected static final String SUFFIX_FILE = "XML";

    /**
     * Task description.
     */
    private static final String TASK_NAME = "Create XML Backup";

    /**
     * Cancel error text.
     */
    private static final String ERROR_CANCEL = "Operation cancelled";

    /**
     * Data Control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T, E> theStatus;

    /**
     * Is this a Secure backup?
     */
    private final boolean isSecure;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     * @param pSecure is this a secure backup
     */
    public CreateXmlFile(final ThreadStatus<T, E> pStatus,
                         final boolean pSecure) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        isSecure = pSecure;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws JOceanusException {
        boolean doDelete = false;
        File myFile = null;

        /* Catch Exceptions */
        try {
            /* Initialise the status window */
            theStatus.initTask(TASK_NAME);

            /* Access the Backup preferences */
            PreferenceManager myMgr = theControl.getPreferenceMgr();
            BackupPreferences myProperties = myMgr.getPreferenceSet(BackupPreferences.class);

            /* Determine the archive name */
            String myBackupDir = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR);
            String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);
            Boolean doTimeStamp = myProperties.getBooleanValue(BackupPreferences.NAME_BACKUP_TIME);

            /* Create the name of the file */
            StringBuilder myName = new StringBuilder(BUFFER_LEN);
            myName.append(myBackupDir);
            myName.append(File.separator);
            myName.append(myPrefix);
            myName.append(SUFFIX_FILE);

            /* If we are doing time-stamps */
            if (doTimeStamp) {
                /* Obtain the current date/time */
                JDateDay myNow = new JDateDay();

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
            myFile = new File(myName.toString() + ZipReadFile.ZIPFILE_EXT);

            /* Access the data */
            T myOldData = theControl.getData();

            /* Create a new formatter */
            DataValuesFormatter<T, E> myFormatter = new DataValuesFormatter<T, E>(theStatus);

            /* Create backup */
            boolean bContinue = isSecure
                                        ? myFormatter.createBackup(myOldData, myFile)
                                        : myFormatter.createExtract(myOldData, myFile);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            theStatus.initTask("Reading Backup");

            /* Load workbook */
            T myNewData = theControl.getNewData();
            bContinue = myFormatter.loadZipFile(myNewData, myFile);

            /* Check for cancellation */
            if (!bContinue) {
                throw new JPrometheusCancelException(ERROR_CANCEL);
            }

            /* Initialise the status window */
            theStatus.initTask("Re-applying Security");

            /* Initialise the security, from the original data */
            myNewData.initialiseSecurity(theStatus, myOldData);

            /* Initialise the status window */
            theStatus.initTask("Verifying Backup");

            /* Create a difference set between the two data copies */
            DataSet<T, ?> myDiff = myNewData.getDifferenceSet(myOldData);

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new JPrometheusDataException(myDiff, "Backup is inconsistent");
            }

            /* OK so switch off flag */
            doDelete = false;

            /* Check for cancellation */
            if (!bContinue) {
                throw new JPrometheusCancelException(ERROR_CANCEL);
            }
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
