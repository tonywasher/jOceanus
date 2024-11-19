/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data;

import org.junit.jupiter.api.Assertions;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.database.MoneyWiseDataStore;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.moneywise.test.data.MoneyWiseTestSecurity.NullThreadStatusReport;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.database.PrometheusDBConfig;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadManager;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadStatusReport;

/**
 * Test Database.
 */
public class MoneyWiseTestDatabase {
    /**
     * The Thread manager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * Constructor.
     *
     * @param pManager the thread manager
     */
    public MoneyWiseTestDatabase(final TethysUIThreadManager pManager) {
        theManager = pManager;
    }

    /**
     * Perform test.
     *
     * @param pData    the data to test with.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public void performTest(final MoneyWiseDataSet pData,
                            final PrometheusToolkit pToolkit) throws OceanusException {
        /* Create config */
        final PrometheusDBConfig myConfig = PrometheusDBConfig.h2();

        /* Access Database */
        final MoneyWiseDataStore myDatabase = new MoneyWiseDataStore("TestDB", myConfig);

        /* Create database */
        final TethysUIThreadStatusReport myReport = new NullThreadStatusReport();
        myDatabase.createTables(myReport);

        /* Update the database */
        final MoneyWiseDataSet myUpdates = pData.deriveUpdateSet();
        theManager.setNewProfile("WriteDB");
        myDatabase.updateDatabase(myReport, myUpdates);

        /* Create the new dataSet */
        final MoneyWiseDataSet myNewData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());

        /* Load the database */
        theManager.setNewProfile("LoadDB");
        myDatabase.loadDatabase(myReport, myNewData);

        /* Purge the data */
        myDatabase.purgeTables(myReport);

        /* Create a difference set between the two data copies */
        final MoneyWiseDataSet myDiff = myNewData.getDifferenceSet(theManager, pData);
        Assertions.assertTrue(myDiff.isEmpty(), "Failed to save/load database");
    }
}
