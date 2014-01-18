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

import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Thread to renew security in the data set. A new ControlKey will be created using the same password as the existing security, together with a new set of
 * encryption DataKeys. All encrypted fields in the data set will then be re-encrypted with the new ControlKey, and finally the ControlData will be updated to
 * use the new controlKey. Data will be left in the Updated state ready for committing the change to the database.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public class RenewSecurity<T extends DataSet<T, ?>>
        extends LoaderThread<T> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "ReNew Security";

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
    public RenewSecurity(final ThreadStatus<T> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws JOceanusException {
        /* Initialise the status window */
        theStatus.initTask("Renewing Security");

        /* Access Data */
        T myData = theControl.getData();
        myData = myData.deriveCloneSet();

        /* ReNew Security */
        myData.renewSecurity(theStatus);

        /* Return null */
        return myData;
    }
}
