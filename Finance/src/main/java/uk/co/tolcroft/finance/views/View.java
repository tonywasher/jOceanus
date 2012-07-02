/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.views;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDateDay.DateDayRange;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.database.FinanceDatabase;
import uk.co.tolcroft.finance.sheets.FinanceSheet;
import uk.co.tolcroft.finance.ui.MainTab;
import uk.co.tolcroft.finance.views.DilutionEvent.DilutionEventList;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.views.DataControl;

/**
 * Data Control for FinanceApp.
 * @author Tony Washer
 */
public class View extends DataControl<FinanceData> {
    /**
     * The DataSet.
     */
    private FinanceData theData = null;

    /**
     * The Date range for the view.
     */
    private DateDayRange theRange = null;

    /**
     * The Main window.
     */
    private final MainTab theCtl;

    /**
     * The event analysis.
     */
    private EventAnalysis theAnalysis = null;

    /**
     * The dilution event list.
     */
    private DilutionEventList theDilutions = null;

    /**
     * Obtain the main window.
     * @return the main window.
     */
    public MainTab getControl() {
        return theCtl;
    }

    /**
     * Obtain the date range.
     * @return the date range
     */
    public DateDayRange getRange() {
        return theRange;
    }

    /**
     * Obtain the analysis.
     * @return the analysis.
     */
    public EventAnalysis getAnalysis() {
        return theAnalysis;
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
     * @param pCtl the main window.
     */
    public View(final MainTab pCtl) {
        /* Store access to the main window */
        theCtl = pCtl;

        /* Store access to the Debug Manager */
        setDataMgr(pCtl.getDataMgr());

        /* Create an empty data set */
        setData(getNewData());
    }

    /**
     * Obtain a new DataSet.
     * @return new DataSet
     */
    @Override
    public final FinanceData getNewData() {
        return new FinanceData(getSecurity());
    }

    @Override
    public Database<FinanceData> getDatabase() throws JDataException {
        return new FinanceDatabase();
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
        super.setData(pData);
        theData = pData;

        /* Analyse the data */
        analyseData(false);

        /* Refresh the windows */
        refreshWindow();
    }

    /**
     * Analyse the data.
     * @param pData the data
     * @return the analysis
     * @throws JDataException on error
     */
    public final EventAnalysis analyseData(final FinanceData pData) throws JDataException {
        /* Initialise the analysis */
        pData.initialiseAnalysis();

        /* Create the analysis */
        EventAnalysis myAnalysis = new EventAnalysis(this, pData);

        /* HouseKeep the analysis */
        pData.houseKeepAnalysis();

        /* Access the most recent metaAnalysis */
        MetaAnalysis myMetaAnalysis = myAnalysis.getMetaAnalysis();

        /* Note active accounts by asset */
        if (myMetaAnalysis != null) {
            myMetaAnalysis.markActiveAccounts();
        }

        /* Complete the analysis */
        pData.completeAnalysis();

        /* Return the analysis */
        return myAnalysis;
    }

    @Override
    protected boolean analyseData(final boolean bPreserve) {
        /* Clear the error */
        if (!bPreserve) {
            setError(null);
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

            /* Adjust the updates debug view */
            setUpdates(theData.deriveUpdateSet());

            /* Catch any exceptions */
        } catch (JDataException e) {
            if (!bPreserve) {
                setError(e);
            }
        }

        /* Return whether there was success */
        return (getError() == null);
    }

    /**
     * refresh the window view.
     */
    @Override
    protected final void refreshWindow() {
        /* Protect against exceptions */
        try {
            /* Refresh the Control */
            theCtl.refreshData();

            /* Catch any exceptions */
        } catch (JDataException e) {
            setError(e);
        }
    }
}
