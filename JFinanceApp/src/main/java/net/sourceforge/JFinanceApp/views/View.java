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
package net.sourceforge.JFinanceApp.views;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.preferences.RenderPreferences;
import net.sourceforge.JDataModels.sheets.SpreadSheet;
import net.sourceforge.JDataModels.views.DataControl;
import net.sourceforge.JDateDay.JDateDayRange;
import net.sourceforge.JFieldSet.RenderManager;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.database.FinanceDatabase;
import net.sourceforge.JFinanceApp.sheets.FinanceSheet;
import net.sourceforge.JFinanceApp.ui.MainTab;
import net.sourceforge.JFinanceApp.views.DilutionEvent.DilutionEventList;
import net.sourceforge.JPreferenceSet.PreferenceSet.PreferenceManager;

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
    private JDateDayRange theRange = null;

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
     * The render manager.
     */
    private final RenderManager theRenderMgr;

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
    public JDateDayRange getRange() {
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
     * Obtain the render manager.
     * @return the render manager
     */
    public RenderManager getRenderMgr() {
        return theRenderMgr;
    }

    /**
     * Constructor.
     * @param pCtl the main window.
     * @throws JDataException on error
     */
    public View(final MainTab pCtl) throws JDataException {
        /* Store access to the main window */
        theCtl = pCtl;

        /* Store access to the Debug Manager */
        setDataMgr(pCtl.getDataMgr());

        /* Access the Render Properties */
        RenderPreferences myPreferences = PreferenceManager.getPreferenceSet(RenderPreferences.class);

        /* Allocate the RenderManager */
        theRenderMgr = new RenderManager(getDataMgr(), myPreferences.getConfiguration());

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

            /* Derive the update Set */
            deriveUpdates();

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
