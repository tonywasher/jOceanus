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
package uk.co.tolcroft.subversion.threads;

import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.threads.WorkerThread;
import uk.co.tolcroft.models.views.DataControl;
import uk.co.tolcroft.subversion.tasks.Backup;

public class SubversionBackup extends WorkerThread<Void> {
    /* Task description */
    private static String theTask = "Subversion Backup Creation";

    /* Properties */
    private DataControl<?> theControl = null;
    private ThreadStatus<?> theStatus = null;

    /* Constructor (Event Thread) */
    public SubversionBackup(DataControl<?> pControl) {
        /* Call super-constructor */
        super(theTask, pControl.getStatusBar());

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = theControl.allocateThreadStatus(this);

        /* Show the status window */
        showStatusBar();
    }

    /* Background task (Worker Thread) */
    @Override
    public Void performTask() throws Exception {
        Backup myAccess = null;

        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Subversion Backup");

            /* Create a clone of the security control */
            DataSet<?> myData = theControl.getData();
            SecureManager mySecure = myData.getSecurity();
            PasswordHash myBase = myData.getPasswordHash();

            /* Create backup */
            myAccess = new Backup(theStatus);
            myAccess.backUpRepositories(mySecure, myBase);
        }

        /* Catch any exceptions */
        catch (Exception e) {
            throw e;
        }

        /* Return nothing */
        return null;
    }
}
