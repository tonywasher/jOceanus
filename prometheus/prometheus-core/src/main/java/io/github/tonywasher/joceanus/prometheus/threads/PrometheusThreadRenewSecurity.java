/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.threads;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataControl;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThread;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;

/**
 * Thread to renew security in the data set. A new ControlKey will be created using the same
 * password as the existing security, together with a new set of encryption DataKeys. All encrypted
 * fields in the data set will then be re-encrypted with the new ControlKey, and finally the
 * ControlData will be updated to use the new controlKey. Data will be left in the Updated state
 * ready for committing the change to the database.
 */
public class PrometheusThreadRenewSecurity
        implements TethysUIThread<PrometheusDataSet> {
    /**
     * Data Control.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor (Event Thread).
     *
     * @param pControl data control
     */
    public PrometheusThreadRenewSecurity(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RENEWSECURITY.toString();
    }

    @Override
    public PrometheusDataSet performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access Data */
        PrometheusDataSet myData = theControl.getData();
        myData = myData.deriveCloneSet();

        /* ReNew Security */
        myData.renewSecurity(pManager);

        /* State that we have completed */
        pManager.setCompletion();

        /* Return null */
        return myData;
    }

    @Override
    public void processResult(final PrometheusDataSet pResult) {
        theControl.setData(pResult);
    }
}
