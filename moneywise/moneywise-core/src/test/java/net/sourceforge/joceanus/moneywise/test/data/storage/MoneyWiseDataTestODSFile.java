/* *****************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.test.data.storage;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;
import org.junit.jupiter.api.Assertions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test XML File.
 */
public class MoneyWiseDataTestODSFile {
    /**
     * The Thread manager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * Constructor.
     * @param pManager the thread manager
     */
    public MoneyWiseDataTestODSFile(final TethysUIThreadManager pManager) {
        theManager = pManager;
    }

    /**
     * Perform test.
     * @param pData the data to test with.
     * @param pView the view
     * @throws OceanusException on error
     */
    public void performTest(final MoneyWiseDataSet pData,
                            final MoneyWiseView pView) throws OceanusException {
        /* Access the Password manager and disable prompting */
        final PrometheusSecurityPasswordManager myManager = pData.getPasswordMgr();
        myManager.setDialogController(new MoneyWiseNullPasswordDialog());

        /* Create a new sheet */
        final MoneyWiseSheet mySheet = new MoneyWiseSheet(pView.getGuiFactory());

        /* Create the output xmlZipFile */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        theManager.setNewProfile("WriteODS");
        mySheet.createBackup(theManager, pData, myZipStream, PrometheusSheetWorkBookType.OASIS);
        final byte[] myBytes = myZipStream.toByteArray();

        /* Create the new dataSet */
        final MoneyWiseDataSet myNewData = pView.getNewData();

        /* Access the file */
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myBytes);
        theManager.setNewProfile("LoadODS");
        mySheet.loadBackup(theManager, myManager, myNewData, myInputStream, "Test");

        /* Create a difference set between the two data copies */
        final MoneyWiseDataSet myDiff = myNewData.getDifferenceSet(theManager, pData);
        Assertions.assertTrue(myDiff.isEmpty(), "Failed to save/load ODS File");
    }
}
