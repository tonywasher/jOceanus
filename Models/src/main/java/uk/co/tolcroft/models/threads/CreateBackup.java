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
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JPreferenceSet.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.sheets.BackupProperties;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.views.DataControl;

public class CreateBackup<T extends DataSet<T>> extends LoaderThread<T> {
    /* Task description */
    private static String theTask = "Backup Creation";

    /* Properties */
    private DataControl<T> theControl = null;
    private ThreadStatus<T> theStatus = null;

    /* Constructor (Event Thread) */
    public CreateBackup(DataControl<T> pControl) {
        /* Call super-constructor */
        super(theTask, pControl);

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = new ThreadStatus<T>(this, theControl);

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws Throwable {
        T myData = null;
        DataSet<T> myDiff = null;
        SpreadSheet<T> mySheet = null;
        boolean doDelete = false;
        File myFile = null;

        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Backup");

            /* Access the Backup properties */
            BackupProperties myProperties = (BackupProperties) PreferenceManager
                    .getPreferenceSet(BackupProperties.class);

            /* Determine the archive name */
            File myBackupDir = new File(myProperties.getStringValue(BackupProperties.nameBackupDir));
            String myPrefix = myProperties.getStringValue(BackupProperties.nameBackupPfix);
            Boolean doTimeStamp = myProperties.getBooleanValue(BackupProperties.nameBackupTime);

            /* If we are not doing time-stamps */
            if (!doTimeStamp) {
                /* Set the standard backup name */
                myFile = new File(myBackupDir.getPath() + File.separator + myPrefix);
            }

            /* else we need to generate a time-stamp (day only) */
            else {
                /* Obtain the current date/time */
                DateDay myNow = new DateDay();

                /* Create the name of the file */
                StringBuilder myName = new StringBuilder(100);
                myName.append(myBackupDir.getPath());
                myName.append(File.separator);
                myName.append(myPrefix);
                myName.append(myNow.getYear());
                if (myNow.getMonth() < 10)
                    myName.append('0');
                myName.append(myNow.getMonth());
                if (myNow.getDay() < 10)
                    myName.append('0');
                myName.append(myNow.getDay());
                myName.append(".zip");
                myFile = new File(myName.toString());
            }

            /* Create backup */
            mySheet = theControl.getSpreadSheet();
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
            myDiff = myData.getDifferenceSet(theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new ModelException(ExceptionClass.DATA, myDiff, "Backup is inconsistent");
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
