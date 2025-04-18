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
package net.sourceforge.joceanus.moneywise.views;

import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.moneywise.data.validate.MoneyWiseValidatorFactory;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxFactory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.database.MoneyWiseDataStore;
import net.sourceforge.joceanus.moneywise.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.database.PrometheusDBConfig;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.database.PrometheusDatabase.PrometheusDatabasePreferenceKey;
import net.sourceforge.joceanus.prometheus.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataControl;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.prometheus.views.PrometheusViewerEntryId;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;

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
     * The Maps.
     */
    private final MoneyWiseMaps theMaps;

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

        /* Create the validator factory */
        setValidatorFactory(new MoneyWiseValidatorFactory());

        /* Create an empty data set */
        setData(getNewData());

        /* Create the maps */
        theMaps = new MoneyWiseMaps(this);
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public OceanusDateRange getRange() {
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
        final MoneyWiseDataSet myDataSet = new MoneyWiseDataSet(getToolkit(), theTaxFactory);
        myDataSet.setValidatorFactory(getValidatorFactory());
        return myDataSet;
    }

    @Override
    public String getDatabaseName() {
        final PrometheusDatabasePreferences myPrefs = getPreferenceManager().getPreferenceSet(PrometheusDatabasePreferences.class);
        return myPrefs.getStringValue(PrometheusDatabasePreferenceKey.DBNAME);
    }

    @Override
    public PrometheusDataStore getDatabase() throws OceanusException {
        final PrometheusDatabasePreferences myPrefs = getPreferenceManager().getPreferenceSet(PrometheusDatabasePreferences.class);
        final PrometheusDBConfig myConfig = PrometheusDBConfig.fromPrefs(myPrefs);
        return new MoneyWiseDataStore(myPrefs.getStringValue(PrometheusDatabasePreferenceKey.DBNAME), myConfig);
    }

    @Override
    public PrometheusDataStore getNullDatabase() throws OceanusException {
        final PrometheusDatabasePreferences myPrefs = getPreferenceManager().getPreferenceSet(PrometheusDatabasePreferences.class);
        final PrometheusDBConfig myConfig = PrometheusDBConfig.fromPrefs(myPrefs);
        return new MoneyWiseDataStore(myConfig);
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

        if (theMaps != null) {
            theMaps.adjustForDataSet(pData);
        }
    }

    @Override
    public MoneyWiseDataSet getData() {
        return theData;
    }

    /**
     * Analyse the data.
     * @param pData the data
     * @return the analysis
     * @throws OceanusException on error
     */
    public final MoneyWiseAnalysisTransAnalyser analyseData(final MoneyWiseDataSet pData) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseData");

        /* Update the maps */
        myTask.startTask("updateMaps");
        pData.updateMaps();

        /* Create the analysis */
        final PrometheusEditSet myEditSet = new PrometheusEditSet(this);
        final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, myEditSet, getPreferenceManager());

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
        OceanusProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseTheData");

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
     * Set the reporting currency.
     * @param pCurrency the new reporting currency
     */
    public void setReportingCurrency(final MoneyWiseCurrency pCurrency) {
        /* Set default currency in AccountCurrencies */
        theData.getAccountCurrencies().setReportingCurrency(pCurrency);

        /* Set default currency in ExchangeRates */
        theData.getExchangeRates().setReportingCurrency(pCurrency);

        /* Register the changes */
        incrementVersion();
    }
}
