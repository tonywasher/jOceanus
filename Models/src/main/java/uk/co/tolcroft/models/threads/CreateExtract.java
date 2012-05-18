/*******************************************************************************
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

import net.sourceforge.JDataWalker.ModelException;
import net.sourceforge.JDataWalker.ModelException.ExceptionClass;
import net.sourceforge.JPreferenceSet.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.BackupProperties;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.views.DataControl;

public class CreateExtract<T extends DataSet<T>> extends WorkerThread<Void> {
    /* Task description */
    private static String theTask = "Extract Creation";

    /* Properties */
    private DataControl<T> theControl = null;
    private ThreadStatus<T> theStatus = null;

    /* Constructor (Event Thread) */
    public CreateExtract(DataControl<T> pControl) {
        /* Call super-constructor */
        super(theTask, pControl.getStatusBar());

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = new ThreadStatus<T>(this, theControl);

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws Throwable {
        T myData = null;
        DataSet<T> myDiff = null;
        SpreadSheet<T> mySheet = null;
        boolean doDelete = false;
        File myFile = null;

        /* Catch Exceptions */
        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Extract");

            /* Access the Sheet properties */
            BackupProperties myProperties = (BackupProperties) PreferenceManager
                    .getPreferenceSet(BackupProperties.class);

            /* Determine the archive name */
            File myBackupDir = new File(myProperties.getStringValue(BackupProperties.nameBackupDir));
            String myPrefix = myProperties.getStringValue(BackupProperties.nameBackupPfix);

            /* Determine the name of the file to build */
            myFile = new File(myBackupDir.getPath() + File.separator + myPrefix + ".xls");

            /* Create backup */
            mySheet = theControl.getSpreadSheet();
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
            myDiff = myData.getDifferenceSet(theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new ModelException(ExceptionClass.DATA, myDiff, "Extract is inconsistent");
            }
        }

        /* Catch any exceptions */
        catch (Throwable e) {
            /* Delete the file */
            if (doDelete)
                myFile.delete();

            /* Report the failure */
            throw e;
        }

        /* Return nothing */
        return null;
    }
}
