/*******************************************************************************
 * Subversion: Java SubVersion Management
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
package net.sourceforge.JSvnManager.threads;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.threads.ThreadStatus;
import net.sourceforge.JDataModels.threads.WorkerThread;
import net.sourceforge.JDataModels.views.DataControl;
import net.sourceforge.JGordianKnot.PasswordHash;
import net.sourceforge.JGordianKnot.SecureManager;
import net.sourceforge.JPreferenceSet.PreferenceManager;
import net.sourceforge.JSvnManager.tasks.Backup;

/**
 * Thread to handle subVersion backups.
 * @author Tony Washer
 * @param <T> the dataset type
 */
public class SubversionBackup<T extends DataSet<T>> extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Subversion Backup Creation";

    /**
     * Data Control.
     */
    private final DataControl<?> theControl;

    /**
     * ThreadStatus.
     */
    private final ThreadStatus<?> theStatus;

    /**
     * The preference manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     * @param pPreferenceMgr the preference manager
     */
    public SubversionBackup(final ThreadStatus<T> pStatus,
                            final PreferenceManager pPreferenceMgr) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();
        thePreferenceMgr = pPreferenceMgr;

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JDataException {
        Backup myAccess = null;

        /* Initialise the status window */
        theStatus.initTask("Creating Subversion Backup");

        /* Create a clone of the security control */
        DataSet<?> myData = theControl.getData();
        SecureManager mySecure = myData.getSecurity();
        PasswordHash myBase = myData.getPasswordHash();

        /* Create backup */
        myAccess = new Backup(theStatus, thePreferenceMgr);
        myAccess.backUpRepositories(mySecure, myBase);

        /* Return nothing */
        return null;
    }
}
