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

import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.views.DataControl;

public class RenewSecurity<T extends DataSet<T>> extends LoaderThread<T> {
    /* Task description */
    private static String theTask = "ReNew Security";

    /* Properties */
    private DataControl<T> theControl = null;
    private ThreadStatus<T> theStatus = null;

    /* Constructor (Event Thread) */
    public RenewSecurity(DataControl<T> pControl) {
        /* Call super-constructor */
        super(theTask, pControl);

        /* Store passed parameters */
        theControl = pControl;

        /* Create the status */
        theStatus = new ThreadStatus<T>(this, theControl);

        /* show the status window */
        showStatusBar();
    }

    @Override
    public T performTask() throws Exception {
        T myData;

        /* Initialise the status window */
        theStatus.initTask("Renewing Security");

        /* Access Data */
        myData = theControl.getData();
        myData = myData.getDeepCopy();

        /* ReNew Security */
        myData.renewSecurity(theStatus);

        /* Return null */
        return myData;
    }
}
