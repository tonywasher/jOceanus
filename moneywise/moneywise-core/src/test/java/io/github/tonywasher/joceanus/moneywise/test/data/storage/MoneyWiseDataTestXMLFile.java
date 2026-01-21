/*
 * MoneyWise: Finance Application
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
package io.github.tonywasher.joceanus.moneywise.test.data.storage;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValuesFormatter;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test XML File.
 */
public class MoneyWiseDataTestXMLFile {
    /**
     * The Thread manager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * Constructor.
     *
     * @param pManager the thread manager
     */
    public MoneyWiseDataTestXMLFile(final TethysUIThreadManager pManager) {
        theManager = pManager;
    }

    /**
     * Perform test.
     *
     * @param pData the data to test with.
     * @param pView the view
     * @throws OceanusException on error
     */
    public void performTest(final MoneyWiseDataSet pData,
                            final MoneyWiseView pView) throws OceanusException {
        /* Access the Password manager and disable prompting */
        final PrometheusSecurityPasswordManager myManager = pData.getPasswordMgr();
        myManager.setDialogController(new MoneyWiseNullPasswordDialog());

        /* Create a new formatter */
        final PrometheusDataValuesFormatter myFormatter = new PrometheusDataValuesFormatter(theManager, myManager);

        /* Create the output xmlZipFile */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        theManager.setNewProfile("WriteZip");
        myFormatter.createBackup(pData, myZipStream);
        final byte[] myBytes = myZipStream.toByteArray();

        /* Create the new dataSet */
        final MoneyWiseDataSet myNewData = pView.getNewData();
        if (pData.newValidityChecks()) {
            myNewData.doNewValidityChecks();
        }

        /* Access the file */
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myBytes);
        theManager.setNewProfile("LoadZip");
        myFormatter.loadZipFile(myNewData, myInputStream, "Test");

        /* Initialise the security, from the original data */
        myNewData.initialiseSecurity(theManager, pData);

        /* Create a difference set between the two data copies */
        final MoneyWiseDataSet myDiff = myNewData.getDifferenceSet(theManager, pData);
        Assertions.assertTrue(myDiff.isEmpty(), "Failed to save/load XML File");
    }
}
