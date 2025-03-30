/*******************************************************************************
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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.analyse;

import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;
import java.util.Map.Entry;

/**
 * Analysis Builder.
 */
public class MoneyWiseAnalysisBuilder {
    /**
     * The dataControl.
     */
    private final PrometheusDataControl theControl;

    /**
     * Constructor.
     * @param pControl the dataControl
     */
    public MoneyWiseAnalysisBuilder(final PrometheusDataControl pControl) {
        theControl = pControl;
    }

    /**
     * analyse new data.
     * @param pData the new data
     * @return the analysis
     * @throws OceanusException on error
     */
    public MoneyWiseAnalysis analyseNewData(final MoneyWiseDataSet pData) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("calculateAnalysis");

        /* Initialise the analysis */
        myTask.startTask("updateMaps");
        updateDataSetMaps(pData);

        /* Create a dummy editSet */
        myTask.startTask("createEditSet");
        final PrometheusEditSet myEditSet = new PrometheusEditSet(theControl, pData);

        /* Create a new analysis on the editSet */
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, myEditSet, theControl.getPreferenceManager());

        /* post-process analysis */
        myAnalyser.postProcessAnalysis();

        /* Don't create totals for the moment */

        /* Complete the task */
        myTask.end();

        /* return the analysis */
        return myAnalyser.getAnalysis();
    }

    /**
     * Update dataSet maps.
     * @param pData the data
     */
    private void updateDataSetMaps(final MoneyWiseDataSet pData) {
        /* Loop through the list types */
        final Iterator<Entry<MetisListKey, PrometheusDataList<?>>> myIterator = pData.entryIterator();
        while (myIterator.hasNext()) {
            final Entry<MetisListKey, PrometheusDataList<?>> myEntry = myIterator.next();

            /* Update the maps (ignoring cryptography tables) */
            if (!(myEntry.getKey() instanceof PrometheusCryptographyDataType)) {
                final PrometheusDataList<?> myList = myEntry.getValue();
                myList.updateMaps();
            }
        }
    }

    /**
     * analysis on editSet change.
     * @param pEditSet the editSet
     * @return the analysis
     * @throws OceanusException on error
     */
    public MoneyWiseAnalysis analyseChangedData(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("calculateAnalysis");

        /* Sort the transaction list */
        myTask.startTask("sortTransactions");
        pEditSet.getDataList(MoneyWiseBasicDataType.TRANSACTION, MoneyWiseTransactionList.class).reSort();

        /* initialise analysis */
        myTask.startTask("updateMaps");
        updateEditSetMaps(pEditSet);

        /* Create a new analysis on the editSet */
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, pEditSet, theControl.getPreferenceManager());

        /* post-process analysis */
        myAnalyser.postProcessAnalysis();

        /* Complete the task */
        myTask.end();

        /* Return analysis */
        return myAnalyser.getAnalysis();
    }

    /**
     * Update the editSet Maps.
     * @param pEditSet the editSet
     */
    private void updateEditSetMaps(final PrometheusEditSet pEditSet) {
        /* Loop through the list types */
        final Iterator<PrometheusEditEntry<?>> myIterator = pEditSet.listIterator();
        while (myIterator.hasNext()) {
            final PrometheusEditEntry<?> myEntry = myIterator.next();

            /* update the maps */
            final PrometheusDataList<?> myList = myEntry.getDataList();
            myList.updateMaps();
        }
    }
}
