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
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.sheets.BackupPreferences;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Thread to create a extract spreadsheet of a data set.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class CreateExtract<T extends DataSet<T>> extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Extract Creation";

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
    public CreateExtract(final ThreadStatus<T> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws Exception {
        T myData = null;
        boolean doDelete = false;
        File myFile = null;

        /* Catch Exceptions */
        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Extract");

            /* Access the Sheet preferences */
            BackupPreferences myProperties = PreferenceManager.getPreferenceSet(BackupPreferences.class);

            /* Determine the archive name */
            File myBackupDir = new File(myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
            String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);

            /* Determine the name of the file to build */
            myFile = new File(myBackupDir.getPath() + File.separator + myPrefix + ".xls");

            /* Create extract */
            SpreadSheet<T> mySheet = theControl.getSpreadSheet();
            mySheet.createExtract(theStatus, theControl.getData(), myFile);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            theStatus.initTask("Reading Extract");

            /* .xls will have been added to the file */
            myFile = new File(myFile.getPath() + ".xls");

            /* Load workbook */
            myData = mySheet.loadExtract(theStatus, myFile);

            /* Initialise the status window */
            theStatus.initTask("Re-applying Security");

            /* Initialise the security, from the original data */
            myData.initialiseSecurity(theStatus, theControl.getData());

            /* Initialise the status window */
            theStatus.initTask("Verifying Extract");

            /* Analyse the Data to ensure that close dates are updated */
            myData.analyseData(theControl);

            /* Create a difference set between the two data copies */
            DataSet<T> myDiff = myData.getDifferenceSet(theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new JDataException(ExceptionClass.DATA, myDiff, "Extract is inconsistent");
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
