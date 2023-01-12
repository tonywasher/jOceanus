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
package net.sourceforge.joceanus.jprometheus.threads;

import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThread;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Thread to change the password. The user will be prompted for a new password and this will be used
 * to create a new Password Hash. The controlKey will be updated with this Hash and the encryption
 * DataKeys will be updated with their new wrapped format. Since the DataKeys do not themselves
 * change there is no need to re-encrypt and data fields. Data will be left in the Updated state
 * ready for committing the change to the database.
 * @param <T> the DataSet type
 */
public class PrometheusThreadUpdatePassword<T extends DataSet<T>>
        implements TethysUIThread<T> {
    /**
     * Data Control.
     */
    private final DataControl<T> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadUpdatePassword(final DataControl<T> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.CHANGEPASS.toString();
    }

    @Override
    public T performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access Data */
        T myData = theControl.getData();
        myData = myData.deriveCloneSet();

        /* Update password */
        myData.updatePasswordHash(pManager, "Database");

        /* State that we have completed */
        pManager.setCompletion();

        /* Return data */
        return myData;
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
