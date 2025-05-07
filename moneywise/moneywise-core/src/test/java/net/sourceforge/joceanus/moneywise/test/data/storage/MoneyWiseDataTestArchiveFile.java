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

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse.MoneyWiseXAnalysisBuilder;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.analyse.MoneyWiseAnalysisBuilder;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.sheets.MoneyWiseArchiveLoader;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.prometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.prometheus.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;

/**
 * Test archive file.
 */
public class MoneyWiseDataTestArchiveFile {
    /**
     * The Thread manager.
     */
    private final TethysUIThreadManager theManager;

    /**
     * Constructor.
     * @param pManager the thread manager
     */
    public MoneyWiseDataTestArchiveFile(final TethysUIThreadManager pManager) {
        theManager = pManager;
    }

    /**
     * Perform test.
     * @param pData    the data to test with.
     * @param pView    the view
     * @throws OceanusException on error
     */
    public void performTest(final MoneyWiseDataSet pData,
                            final MoneyWiseView pView) throws OceanusException {
        /* Create the new dataSet and access preferences */
        final PrometheusPreferenceManager myMgr = pView.getPreferenceManager();
        final PrometheusBackupPreferences myPrefs = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Access the Password manager and disable prompting */
        final MoneyWiseArchiveLoader myLoader = new MoneyWiseArchiveLoader(pData);
        myLoader.loadArchive(theManager, myPrefs);

        /* Initialise the security, from the original data */
        final MoneyWiseDataSet myNullData = pView.getNewData();
        pData.initialiseSecurity(theManager, myNullData);
        pData.reBase(theManager, myNullData);
    }

    /**
     * Obtain old style analysis on archive data.
     * @param pView    the view
     * @param pLastEvent the last event date
     * @return the analysis
     * @throws OceanusException on error
     */
    public MoneyWiseAnalysis oldAnalysis(final MoneyWiseView pView,
                                         final OceanusDate pLastEvent) throws OceanusException {
        /* Create the new dataSet and access preferences */
        final PrometheusPreferenceManager myMgr = pView.getPreferenceManager();
        final PrometheusBackupPreferences myPrefs = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);
        final MoneyWiseDataSet myDataSet = pView.getNewData();

        /* Access the Password manager and disable prompting */
        final MoneyWiseArchiveLoader myLoader = new MoneyWiseArchiveLoader(myDataSet);
        myLoader.setLastEvent(pLastEvent);
        myLoader.loadArchive(theManager, myPrefs);

        /* Initialise the security, from the original data */
        final MoneyWiseDataSet myNullData = pView.getNewData();
        myDataSet.initialiseSecurity(theManager, myNullData);
        myDataSet.reBase(theManager, myNullData);

        /* Create the analysis builder */
        final MoneyWiseAnalysisBuilder myBuilder = new MoneyWiseAnalysisBuilder(pView);
        return myBuilder.analyseNewData(myDataSet);
    }

    /**
     * Obtain new style analysis on archive data.
     * @param pView    the view
     * @param pLastEvent the last event date
     * @return the analysis
     * @throws OceanusException on error
     */
    public MoneyWiseXAnalysis newAnalysis(final MoneyWiseView pView,
                                          final OceanusDate pLastEvent) throws OceanusException {
        /* Create the new dataSet and access preferences */
        final PrometheusPreferenceManager myMgr = pView.getPreferenceManager();
        final PrometheusBackupPreferences myPrefs = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);
        final MoneyWiseDataSet myDataSet = pView.getNewData();
        myDataSet.newValidityChecks();

        /* Access the Password manager and disable prompting */
        final MoneyWiseArchiveLoader myLoader = new MoneyWiseArchiveLoader(myDataSet);
        myLoader.setLastEvent(pLastEvent);
        myLoader.loadArchive(theManager, myPrefs);

        /* Initialise the security, from the original data */
        final MoneyWiseDataSet myNullData = pView.getNewData();
        myDataSet.initialiseSecurity(theManager, myNullData);
        myDataSet.reBase(theManager, myNullData);

        /* Create the analysis builder */
        final MoneyWiseXAnalysisBuilder myBuilder = new MoneyWiseXAnalysisBuilder(pView);
        return myBuilder.analyseNewData(myDataSet);
    }
}
