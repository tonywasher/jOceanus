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
package net.sourceforge.jOceanus.jDataModels.threads;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;

/**
 * Thread to change the password. The user will be prompted for a new password and this will be used to create a new Password Hash. The controlKey will be
 * updated with this Hash and the encryption DataKeys will be updated with their new wrapped format. Since the DataKeys do not themselves change there is no
 * need to re-encrypt and data fields. Data will be left in the Updated state ready for committing the change to the database.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class UpdatePassword<T extends DataSet<T>>
        extends LoaderThread<T> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Update Password";

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
    public UpdatePassword(final ThreadStatus<T> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the Status bar */
        showStatusBar();
    }

    @Override
    public T performTask() throws JDataException {
        /* Initialise the status window */
        theStatus.initTask("Updating Password");

        /* Access Data */
        T myData = theControl.getData();
        myData = myData.deriveCloneSet();

        /* Update password */
        myData.updatePasswordHash(theStatus, "Database");

        /* Return null */
        return myData;
    }
}