/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.threads;

import net.sourceforge.joceanus.jprometheus.atlas.threads.PrometheusThreadId;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Thread to renew security in the data set. A new ControlKey will be created using the same
 * password as the existing security, together with a new set of encryption DataKeys. All encrypted
 * fields in the data set will then be re-encrypted with the new ControlKey, and finally the
 * ControlData will be updated to use the new controlKey. Data will be left in the Updated state
 * ready for committing the change to the database.
 */
public class PrometheusXThreadRenewSecurity
        implements TethysUIThread<DataSet> {
    /**
     * Data Control.
     */
    private final DataControl theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusXThreadRenewSecurity(final DataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RENEWSECURITY.toString();
    }

    @Override
    public DataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access Data */
        DataSet myData = theControl.getData();
        myData = myData.deriveCloneSet();

        /* ReNew Security */
        myData.renewSecurity(pManager);

        /* State that we have completed */
        pManager.setCompletion();

        /* Return null */
        return myData;
    }

    @Override
    public void processResult(final DataSet pResult) {
        theControl.setData(pResult);
    }
}