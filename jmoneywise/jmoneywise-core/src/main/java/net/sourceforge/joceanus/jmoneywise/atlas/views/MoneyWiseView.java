/*******************************************************************************
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
package net.sourceforge.joceanus.jmoneywise.atlas.views;

import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.database.MoneyWiseDataStore;
import net.sourceforge.joceanus.jmoneywise.atlas.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.jprometheus.atlas.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDBConfig;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.atlas.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusDataControl;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;

/**
 * Data Control for MoneyWiseApp.
 */
public class MoneyWiseView
        extends PrometheusDataControl {
    /**
     * The TaxFactory.
     */
    private final MoneyWiseTaxFactory theTaxFactory;

    /**
     * The DataSet.
     */
    private MoneyWiseDataSet theData;

    /**
     * The analysis manager.
     */
    private MoneyWiseAnalysisManager theAnalysisMgr;

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
     * @param pTaxFactory the tax factory
     */
    public MoneyWiseView(final PrometheusToolkit pUtilitySet,
                         final MoneyWiseTaxFactory pTaxFactory) {
        /* Call super-constructor */
        super(pUtilitySet);

        /* Record the tax factory */
        theTaxFactory = pTaxFactory;

        /* Create an empty data set */
        setData(getNewData());
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public TethysDateRange getRange() {
        return theTaxFactory.getDateRange();
    }

    /**
     * Obtain the analysis manager.
     * @return the analyser.
     */
    public MoneyWiseAnalysisManager getAnalysisManager() {
        return theAnalysisMgr;
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
    public final MoneyWiseDataSet getNewData() {
        return new MoneyWiseDataSet(getToolkit(), theTaxFactory);
    }

    @Override
    public PrometheusDataStore getDatabase() throws OceanusException {
        final PrometheusDatabasePreferences myPrefs = getPreferenceManager().getPreferenceSet(PrometheusDatabasePreferences.class);
        final PrometheusDBConfig myConfig = PrometheusDBConfig.fromPrefs(myPrefs);
        return new MoneyWiseDataStore(myPrefs.getStringValue(PrometheusDatabasePreferenceKey.DBNAME), myConfig);
    }

    /**
     * Obtain a Database interface.
     * @return new DataSet
     */
    @Override
    public PrometheusSpreadSheet getSpreadSheet() {
        return new MoneyWiseSheet(getGuiFactory());
    }

    /**
     * Update the data for a view.
     * @param pData the new data set
     */
    @Override
    public final void setData(final PrometheusDataSet pData) {
        /* Record the data */
        theData = (MoneyWiseDataSet) pData;
        super.setData(pData);
    }

    /**
     * Analyse the data.
     * @param pData the data
     * @return the analysis
     * @throws OceanusException on error
     */
    public final MoneyWiseAnalysisTransAnalyser analyseData(final MoneyWiseDataSet pData) throws OceanusException {
        /* Obtain the active profile */
        TethysProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseData");

        /* Initialise the analysis */
        myTask.startTask("Initialise");
        pData.initialiseAnalysis();

        /* Create the analysis */
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, pData, getPreferenceManager());

        /* Post process the analysis */
        myAnalyser.postProcessAnalysis();

        /* Complete the task */
        myTask.end();

        /* Return the analyser */
        return myAnalyser;
    }

    @Override
    protected boolean analyseData(final boolean bPreserve) {
        /* Obtain the active profile */
        TethysProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseData");

        /* Clear the errors */
        if (!bPreserve) {
            clearErrors();
        }

        /* Protect against exceptions */
        try {
            /* Analyse the data */
            final MoneyWiseAnalysisTransAnalyser myAnalyser = analyseData(theData);
            final MoneyWiseAnalysis myAnalysis = myAnalyser.getAnalysis();
            theAnalysisMgr = new MoneyWiseAnalysisManager(myAnalysis);

            /* Analyse the basic ranged analysis */
            myTask.startTask("AnalyseBase");
            theAnalysisMgr.analyseBase();
            hasMultiCurrency = theAnalysisMgr.haveForeignCurrency();
            hasActiveSecurities = theAnalysisMgr.haveActiveSecurities();

            /* Update the Data entry */
            final MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.ANALYSIS);
            myData.setTreeObject(theAnalysisMgr);

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
    public void setDefaultCurrency(final MoneyWiseCurrency pCurrency) {
        /* Set default currency in AccountCurrencies */
        theData.getAccountCurrencies().setDefaultCurrency(pCurrency);

        /* Set default currency in ExchangeRates */
        theData.getExchangeRates().setDefaultCurrency(pCurrency);

        /* Register the changes */
        incrementVersion();
    }
}