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
import net.sourceforge.joceanus.jmoneywise.atlas.sheets.MoneyWiseArchiveLoader;
import net.sourceforge.joceanus.jmoneywise.atlas.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKTaxYearCache;
import net.sourceforge.joceanus.jmoneywise.test.data.MoneyWiseTestControl.ThreadMgrStub;
import net.sourceforge.joceanus.jmoneywise.test.data.MoneyWiseTestSecurity.DialogStub;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Test archive file.
 */
public class MoneyWiseTestArchiveFile {
    /**
     * The Thread manager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * Constructor.
     * @param pManager the thread manager
     */
    public MoneyWiseTestArchiveFile(final TethysUIThreadManager pManager) {
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
        /* Create the new dataSet and access preferences */
        final MoneyWiseDataSet myBaseData = new MoneyWiseDataSet(pToolkit, new MoneyWiseUKTaxYearCache());
        final PrometheusPreferenceManager myMgr = pToolkit.getPreferenceManager();
        final PrometheusBackupPreferences myPrefs = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Access the Password manager and disable prompting */
        final MoneyWiseArchiveLoader myLoader = new MoneyWiseArchiveLoader(pToolkit.getToolkit().getGuiFactory());
        myLoader.loadArchive(theManager, myBaseData, myPrefs);

        /* Test the XML File creation */
        new MoneyWiseTestXMLFile(new ThreadMgrStub()).performTest(myBaseData, pToolkit);

        /* Test the ODS File creation */
        new MoneyWiseTestODSFile(new ThreadMgrStub()).performTest(myBaseData, pToolkit);
        int i = 0;
    }
}
