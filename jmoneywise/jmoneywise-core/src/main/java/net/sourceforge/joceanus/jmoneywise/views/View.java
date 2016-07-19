/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.views;

import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.database.MoneyWiseDatabase;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;

/**
 * Data Control for MoneyWiseApp.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class View<N, I>
        extends DataControl<MoneyWiseData, MoneyWiseDataType, N, I> {
    /**
     * The DataSet.
     */
    private MoneyWiseData theData;

    /**
     * The Date range for the view.
     */
    private TethysDateRange theRange;

    /**
     * The analysis manager.
     */
    private AnalysisManager theAnalysisMgr;

    /**
     * The dilution event map.
     */
    private DilutionEventMap theDilutions;

    /**
     * Do we have security buckets?.
     */
    private boolean hasActiveSecurities;

    /**
     * Do we have multiple currencies?
     */
    private boolean hasMultiCurrency;

    /**
     * Constructor.
     * @param pUtilitySet the utility set
     * @param pProfile the startup profile
     * @throws OceanusException on error
     */
    public View(final JOceanusUtilitySet<N, I> pUtilitySet,
                final MetisProfile pProfile) throws OceanusException {
        /* Call super-constructor */
        super(pUtilitySet, pProfile);

        /* Create an empty data set */
        setData(getNewData());
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public TethysDateRange getRange() {
        return theRange;
    }

    /**
     * Obtain the analysis manager.
     * @return the analyser.
     */
    public AnalysisManager getAnalysisManager() {
        return theAnalysisMgr;
    }

    /**
     * Obtain the dilution map.
     * @return the dilution map.
     */
    public DilutionEventMap getDilutions() {
        return theDilutions;
    }

    /**
     * Do we have active securities?
     * @return true/false.
     */
    public boolean hasActiveSecurities() {
        return hasActiveSecurities;
    }

    /**
     * Do we have multiple currencies?
     * @return true/false.
     */
    public boolean hasMultipleCurrencies() {
        return hasMultiCurrency;
    }

    /**
     * Obtain a new DataSet.
     * @return new DataSet
     */
    @Override
    public final MoneyWiseData getNewData() {
        return new MoneyWiseData(getUtilitySet());
    }

    @Override
    public PrometheusDataStore<MoneyWiseData> getDatabase() throws OceanusException {
        return new MoneyWiseDatabase(getPreferenceManager().getPreferenceSet(PrometheusDatabasePreferences.class));
    }

    /**
     * Obtain a Database interface.
     * @return new DataSet
     */
    @Override
    public PrometheusSpreadSheet<MoneyWiseData> getSpreadSheet() {
        return new MoneyWiseSheet();
    }

    /**
     * Update the data for a view.
     * @param pData the new data set
     */
    @Override
    public final void setData(final MoneyWiseData pData) {
        /* Record the data */
        theData = pData;
        super.setData(pData);
    }

    /**
     * Analyse the data.
     * @param pData the data
     * @return the analysis
     * @throws OceanusException on error
     */
    public final TransactionAnalyser analyseData(final MoneyWiseData pData) throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseData");

        /* Initialise the analysis */
        myTask.startTask("Initialise");
        pData.initialiseAnalysis();

        /* Create the analysis */
        TransactionAnalyser myAnalyser = new TransactionAnalyser(myTask, pData, getPreferenceManager());

        /* Post process the analysis */
        myAnalyser.postProcessAnalysis();
        pData.adjustSecurityMap();

        /* Complete the task */
        myTask.end();

        /* Return the analyser */
        return myAnalyser;
    }

    @Override
    protected boolean analyseData(final boolean bPreserve) {
        /* Obtain the active profile */
        MetisProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseData");

        /* Clear the errors */
        if (!bPreserve) {
            clearErrors();
        }

        /* Calculate the Data Range */
        theData.calculateDateRange();

        /* Access the range */
        theRange = theData.getDateRange();

        /* Protect against exceptions */
        try {
            /* Analyse the data */
            TransactionAnalyser myAnalyser = analyseData(theData);
            Analysis myAnalysis = myAnalyser.getAnalysis();
            theAnalysisMgr = new AnalysisManager(myAnalysis);

            /* Analyse the basic ranged analysis */
            myTask.startTask("AnalyseBase");
            theAnalysisMgr.analyseBase();
            hasMultiCurrency = theAnalysisMgr.haveForeignCurrency();
            hasActiveSecurities = theAnalysisMgr.haveActiveSecurities();

            /* Update the Data entry */
            MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.ANALYSIS);
            myData.setTreeObject(theAnalysisMgr);

            /* Access the dilutions */
            theDilutions = myAnalyser.getDilutions();

            /* Derive the update Set */
            myTask.startTask("deriveUpdates");
            deriveUpdates();

            /* Catch any exceptions */
        } catch (OceanusException e) {
            if (!bPreserve) {
                addError(e);
            }
        }

        /* Complete the task */
        myTask.end();

        /* Return whether there was success */
        return getErrors().isEmpty();
    }

    /**
     * Set the default currency.
     * @param pCurrency the new default currency
     */
    public void setDefaultCurrency(final AssetCurrency pCurrency) {
        /* Set default currency in AccountCurrencies */
        theData.getAccountCurrencies().setDefaultCurrency(pCurrency);

        /* Set default currency in ExchangeRates */
        theData.getExchangeRates().setDefaultCurrency(pCurrency);

        /* Register the changes */
        incrementVersion();

        /* Discover data */
        theData.calculateDateRange();
    }
}
