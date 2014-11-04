/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JDataProfile;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.DilutionEvent.DilutionEventMap;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.database.MoneyWiseDatabase;
import net.sourceforge.joceanus.jmoneywise.sheets.MoneyWiseSheet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.preferences.DatabasePreferences;
import net.sourceforge.joceanus.jprometheus.sheets.SpreadSheet;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;

import org.slf4j.Logger;

/**
 * Data Control for MoneyWiseApp.
 * @author Tony Washer
 */
public class View
        extends DataControl<MoneyWiseData, MoneyWiseDataType> {
    /**
     * The DataSet.
     */
    private MoneyWiseData theData = null;

    /**
     * The Date range for the view.
     */
    private JDateDayRange theRange = null;

    /**
     * The analysis.
     */
    private TransactionAnalyser theAnalyser = null;

    /**
     * The dilution event map.
     */
    private DilutionEventMap theDilutions = null;

    /**
     * Obtain the date range.
     * @return the date range
     */
    public JDateDayRange getRange() {
        return theRange;
    }

    /**
     * Obtain the analyser.
     * @return the analyser.
     */
    public TransactionAnalyser getAnalyser() {
        return theAnalyser;
    }

    /**
     * Obtain the analysis manager.
     * @return the analyser.
     */
    public AnalysisManager getAnalysisManager() {
        return theAnalyser.getAnalysisManager();
    }

    /**
     * Obtain the dilution map.
     * @return the dilution map.
     */
    public DilutionEventMap getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor.
     * @param pProfile the startup profile
     * @param pLogger the logger.
     * @throws JOceanusException on error
     */
    public View(final JDataProfile pProfile,
                final Logger pLogger) throws JOceanusException {
        /* Call super-constructor */
        super(pProfile, pLogger);

        /* Create an empty data set */
        setData(getNewData());
    }

    /**
     * Obtain a new DataSet.
     * @return new DataSet
     */
    @Override
    public final MoneyWiseData getNewData() {
        return new MoneyWiseData(getSecurity(), getPreferenceMgr(), getFieldMgr());
    }

    @Override
    public Database<MoneyWiseData> getDatabase() throws JOceanusException {
        PreferenceManager myMgr = getPreferenceMgr();
        return new MoneyWiseDatabase(getLogger(), myMgr.getPreferenceSet(DatabasePreferences.class));
    }

    /**
     * Obtain a Database interface.
     * @return new DataSet
     */
    @Override
    public SpreadSheet<MoneyWiseData> getSpreadSheet() {
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
     * @throws JOceanusException on error
     */
    public final TransactionAnalyser analyseData(final MoneyWiseData pData) throws JOceanusException {
        /* Obtain the active profile */
        JDataProfile myTask = getActiveTask();
        myTask = myTask.startTask("analyseData");

        /* Initialise the analysis */
        myTask.startTask("Initialise");
        pData.initialiseAnalysis();

        /* Create the analysis */
        TransactionAnalyser myAnalyser = new TransactionAnalyser(myTask, pData, getPreferenceMgr());

        /* Access the top level debug entry for this analysis */
        JDataEntry mySection = getDataEntry(DataControl.DATA_ANALYSIS);
        mySection.setObject(myAnalyser);

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
        JDataProfile myTask = getActiveTask();
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
            theAnalyser = analyseData(theData);

            /* Update the Data entry */
            JDataEntry myData = getDataEntry(DATA_ANALYSIS);
            myData.setObject(theAnalyser);

            /* Access the dilutions */
            theDilutions = theAnalyser.getDilutions();

            /* Derive the update Set */
            myTask.startTask("deriveUpdates");
            deriveUpdates();

            /* Catch any exceptions */
        } catch (JOceanusException e) {
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
    public void setDefaultCurrency(final AccountCurrency pCurrency) {
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
