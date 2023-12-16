/* *****************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.test.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jmoneywise.test.data.MoneyWiseTestSecurity.NullPasswordDialog;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Test XML File.
 */
public class MoneyWiseTestODSFile {
    /**
     * The Thread manager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * Constructor.
     * @param pManager the thread manager
     */
    public MoneyWiseTestODSFile(final TethysUIThreadManager pManager) {
        theManager = pManager;
    }

    /**
     * Perform test.
     * @param pData the data to test with.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public void performTest(final MoneyWiseDataSet pData,
                            final PrometheusToolkit pToolkit) throws OceanusException {
        /* Access the Password manager and disable prompting */
        final GordianPasswordManager myManager = pData.getPasswordMgr();
        myManager.setDialogController(new NullPasswordDialog());

        /* Create a new sheet */
        final MoneyWiseSheet mySheet = new MoneyWiseSheet(pToolkit.getToolkit().getGuiFactory());

        /* Create the output xmlZipFile */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        mySheet.createBackup(theManager, pData, myZipStream, PrometheusSheetWorkBookType.OASIS);
        final byte[] myBytes = myZipStream.toByteArray();

        /* Create the new dataSet */
        final MoneyWiseDataSet myNewData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());

        /* Access the file */
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myBytes);
        mySheet.loadBackup(theManager, myManager, myNewData, myInputStream, "Test");

        /* Create a difference set between the two data copies */
        final MoneyWiseDataSet myDiff = myNewData.getDifferenceSet(theManager, pData);
        if (!myDiff.isEmpty()) {
            int i = 0;
        }
    }
}
