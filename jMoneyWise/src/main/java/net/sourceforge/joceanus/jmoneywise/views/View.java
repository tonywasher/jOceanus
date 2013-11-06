/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import java.util.logging.Logger;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jdatamodels.database.Database;
import net.sourceforge.joceanus.jdatamodels.preferences.DatabasePreferences;
import net.sourceforge.joceanus.jdatamodels.sheets.SpreadSheet;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jmoneywise.analysis.DataAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCurrency;
import net.sourceforge.joceanus.jmoneywise.database.FinanceDatabase;
import net.sourceforge.joceanus.jmoneywise.sheets.FinanceSheet;
import net.sourceforge.joceanus.jmoneywise.views.DilutionEvent.DilutionEventList;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;

/**
 * Data Control for FinanceApp.
 * @author Tony Washer
 */
public class View
        extends DataControl<FinanceData> {
    /**
     * The DataSet.
     */
    private FinanceData theData = null;

    /**
     * The Date range for the view.
     */
    private JDateDayRange theRange = null;

    /**
     * The data analysis.
     */
    private DataAnalysis theAnalysis = null;

    /**
     * The alternate analysis.
     */
    private DataAnalyser theAnalyser = null;

    /**
     * The dilution event list.
     */
    private DilutionEventList theDilutions = null;

    /**
     * Obtain the date range.
     * @return the date range
     */
    public JDateDayRange getRange() {
        return theRange;
    }

    /**
     * Obtain the analysis.
     * @return the analysis.
     */
    public DataAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the analyser.
     * @return the analyser.
     */
    public DataAnalyser getAnalyser() {
        return theAnalyser;
    }

    /**
     * Obtain the dilution list.
     * @return the dilution list.
     */
    public DilutionEventList getDilutions() {
        return theDilutions;
    }

    /**
     * Constructor.
     * @param pLogger the logger.
     * @throws JDataException on error
     */
    public View(final Logger pLogger) throws JDataException {
        /* Call super-constructor */
        super(pLogger);

        /* Create an empty data set */
        setData(getNewData());
    }

    /**
     * Obtain a new DataSet.
     * @return new DataSet
     */
    @Override
    public final FinanceData getNewData() {
        return new FinanceData(getSecurity(), getPreferenceMgr(), getFieldMgr());
    }

    @Override
    public Database<FinanceData> getDatabase() throws JDataException {
        PreferenceManager myMgr = getPreferenceMgr();
        return new FinanceDatabase(myMgr.getPreferenceSet(DatabasePreferences.class));
    }

    /**
     * Obtain a Database interface.
     * @return new DataSet
     */
    @Override
    public SpreadSheet<FinanceData> getSpreadSheet() {
        return new FinanceSheet();
    }

    /**
     * Update the data for a view.
     * @param pData the new data set
     */
    @Override
    public final void setData(final FinanceData pData) {
        /* Record the data */
        theData = pData;
        super.setData(pData);
    }

    /**
     * Analyse the data.
     * @param pData the data
     * @return the analysis
     * @throws JDataException on error
     */
    public final DataAnalysis analyseData(final FinanceData pData) throws JDataException {
        /* Initialise the analysis */
        pData.initialiseAnalysis();

        /* Create the analysis */
        DataAnalysis myAnalysis = new DataAnalysis(this, pData);

        /* Create the alternative analysis */
        theAnalyser = new DataAnalyser(pData);

        /* Access the top level debug entry for this analysis */
        JDataEntry mySection = getDataEntry(DataControl.DATA_ANALYSIS2);
        mySection.setObject(theAnalyser);

        /* Access the most recent metaAnalysis */
        MetaAnalysis myMetaAnalysis = myAnalysis.getMetaAnalysis();

        /* If it exists */
        if (myMetaAnalysis != null) {
            /* Mark active accounts according to the analysis */
            myMetaAnalysis.markActiveAccounts();
        }

        /* Complete the analysis */
        pData.completeAnalysis();

        /* Return the analysis */
        return myAnalysis;
    }

    @Override
    protected boolean analyseData(final boolean bPreserve) {
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
            theAnalysis = analyseData(theData);

            /* Access the dilutions */
            theDilutions = theAnalysis.getDilutions();

            /* Derive the update Set */
            deriveUpdates();

            /* Catch any exceptions */
        } catch (JDataException e) {
            if (!bPreserve) {
                addError(e);
            }
        }

        /* Return whether there was success */
        return (getErrors().size() == 0);
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
