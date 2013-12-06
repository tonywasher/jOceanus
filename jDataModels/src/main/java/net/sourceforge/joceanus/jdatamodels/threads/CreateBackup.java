/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jdatamodels.threads;

import java.io.File;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.preferences.BackupPreferences;
import net.sourceforge.joceanus.jdatamodels.sheets.SpreadSheet;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jspreadsheetmanager.WorkBookType;

/**
 * Thread to create an encrypted backup of a data set.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class CreateBackup<T extends DataSet<T>>
        extends LoaderThread<T> {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Number 10.
     */
    private static final int TEN = 10;

    /**
     * Task description.
     */
    private static final String TASK_NAME = "Backup Creation";

    /**
     * Data Control.
     */
    private final DataControl<T> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public CreateBackup(final ThreadStatus<T> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws JDataException {
        T myData = null;
        boolean doDelete = false;
        File myFile = null;

        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Backup");

            /* Access the Backup preferences */
            PreferenceManager myMgr = theControl.getPreferenceMgr();
            BackupPreferences myProperties = myMgr.getPreferenceSet(BackupPreferences.class);

            /* Determine the archive name */
            String myBackupDir = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR);
            String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);
            Boolean doTimeStamp = myProperties.getBooleanValue(BackupPreferences.NAME_BACKUP_TIME);
            WorkBookType myType = myProperties.getEnumValue(BackupPreferences.NAME_BACKUP_TYPE, WorkBookType.class);

            /* Create the name of the file */
            StringBuilder myName = new StringBuilder(BUFFER_LEN);
            myName.append(myBackupDir);
            myName.append(File.separator);
            myName.append(myPrefix);

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
            myFile = new File(myName.toString()
                              + SpreadSheet.ZIPFILE_EXT);

            /* Create backup */
            SpreadSheet<T> mySheet = theControl.getSpreadSheet();
            mySheet.createBackup(theStatus, theControl.getData(), myFile, myType);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            theStatus.initTask("Verifying Backup");

            /* Load workbook */
            myData = mySheet.loadBackup(theStatus, myFile);

            /* Create a difference set between the two data copies */
            DataSet<T> myDiff = myData.getDifferenceSet(theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new JDataException(ExceptionClass.DATA, myDiff, "Backup is inconsistent");
            }

            /* OK so switch off flag */
            doDelete = false;

            /* Delete file on error */
        } finally {
            /* Delete the file */
            if ((doDelete)
                && (!myFile.delete())) {
                doDelete = false;
            }
        }

        /* Return nothing */
        return null;
    }
}
