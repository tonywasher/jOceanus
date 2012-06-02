/*******************************************************************************
 * JDataModel: Data models
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.models.threads;

import java.io.File;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDateDay.DateDay;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.sheets.BackupPreferences;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Thread to create an encrypted backup of a data set.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class CreateBackup<T extends DataSet<T>> extends LoaderThread<T> {
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
     * @param pControl the data control
     */
    public CreateBackup(final DataControl<T> pControl) {
        /* Call super-constructor */
        super(TASK_NAME, pControl);

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = new ThreadStatus<T>(this, theControl);

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws Exception {
        T myData = null;
        boolean doDelete = false;
        File myFile = null;

        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Backup");

            /* Access the Backup preferences */
            BackupPreferences myProperties = PreferenceManager.getPreferenceSet(BackupPreferences.class);

            /* Determine the archive name */
            File myBackupDir = new File(myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
            String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);
            Boolean doTimeStamp = myProperties.getBooleanValue(BackupPreferences.NAME_BACKUP_TIME);

            /* If we are not doing time-stamps */
            if (!doTimeStamp) {
                /* Set the standard backup name */
                myFile = new File(myBackupDir.getPath() + File.separator + myPrefix);

                /* else we need to generate a time-stamp (day only) */
            } else {
                /* Obtain the current date/time */
                DateDay myNow = new DateDay();

                /* Create the name of the file */
                StringBuilder myName = new StringBuilder(BUFFER_LEN);
                myName.append(myBackupDir.getPath());
                myName.append(File.separator);
                myName.append(myPrefix);
                myName.append(myNow.getYear());
                if (myNow.getMonth() < TEN) {
                    myName.append('0');
                }
                myName.append(myNow.getMonth());
                if (myNow.getDay() < TEN) {
                    myName.append('0');
                }
                myName.append(myNow.getDay());
                myFile = new File(myName.toString());
            }

            /* Create backup */
            SpreadSheet<T> mySheet = theControl.getSpreadSheet();
            mySheet.createBackup(theStatus, theControl.getData(), myFile);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            theStatus.initTask("Verifying Backup");

            /* As we have encrypted then .zip was added to the file */
            myFile = new File(myFile.getPath() + ".zip");

            /* Load workbook */
            myData = mySheet.loadBackup(theStatus, myFile);

            /* Create a difference set between the two data copies */
            DataSet<T> myDiff = myData.getDifferenceSet(theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new JDataException(ExceptionClass.DATA, myDiff, "Backup is inconsistent");
            }

            /* Catch any exceptions */
        } catch (Exception e) {
            /* Delete the file */
            if (doDelete) {
                myFile.delete();
            }

            /* Report the failure */
            throw e;
        }

        /* Return nothing */
        return null;
    }
}
