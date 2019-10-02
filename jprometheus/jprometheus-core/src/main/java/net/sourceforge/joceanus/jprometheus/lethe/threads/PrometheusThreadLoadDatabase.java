/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadData;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Thread to load data from the database.
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public class PrometheusThreadLoadDatabase<T extends DataSet<T, E>, E extends Enum<E>>
        implements MetisThread<T> {
    /**
     * Data control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadLoadDatabase(final DataControl<T, E> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.LOADDB.toString();
    }

    @Override
    public T performTask(final MetisThreadData pThreadData) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myToolkit = (PrometheusToolkit) pThreadData;
        final MetisThreadManager myManager = myToolkit.getThreadManager();

        /* Access database */
        final PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Protect against failures */
        try {
            /* Load database */
            final T myData = theControl.getNewData();
            myDatabase.loadDatabase(myManager, myData);

            /* Check security on the database */
            myData.checkSecurity(myManager);

            /* State that we have completed */
            myManager.setCompletion();

            /* Return the loaded data */
            return myData;

            /* Make sure that the database is closed */
        } finally {
            /* Close the database */
            myDatabase.close();
        }
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
