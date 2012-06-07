/*******************************************************************************
 * JDataModel: Data models
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
package uk.co.tolcroft.models.views;

import java.util.HashMap;

import javax.swing.JFrame;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JGordianKnot.SecureManager;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.SecurityPreferences;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.SpreadSheet;

/**
 * Provides top-level control of data.
 * @param <T> the DataSet type
 */
public abstract class DataControl<T extends DataSet<T>> {
    /**
     * Debug View Name.
     */
    public static final String DATA_VIEWS = "DataViews";

    /**
     * Underlying Data Name.
     */
    public static final String DATA_DATASET = "UnderlyingData";

    /**
     * Data Updates Name.
     */
    public static final String DATA_UPDATES = "DataUpdates";

    /**
     * Analysis Name.
     */
    public static final String DATA_ANALYSIS = "Analysis";

    /**
     * Error Name.
     */
    public static final String DATA_ERROR = "Error";

    /**
     * The DataSet.
     */
    private T theData = null;

    /**
     * The Update DataSet.
     */
    private T theUpdates = null;

    /**
     * The Error.
     */
    private JDataException theError = null;

    /**
     * The Frame.
     */
    private JFrame theFrame = null;

    /**
     * The Security Manager.
     */
    private final SecureManager theSecurity;

    /**
     * The Data Manager.
     */
    private JDataManager theDataMgr = null;

    /**
     * The Data Entry hashMap.
     */
    private final HashMap<String, JDataEntry> theMap;

    /**
     * Constructor for default preferences.
     */
    protected DataControl() {
        /* Create the Secure Manager */
        theSecurity = new SecureManager();

        /* Create the Debug Map */
        theMap = new HashMap<String, JDataEntry>();
    }

    /**
     * Constructor for security preferences.
     * @param pPreferences the security preferences
     */
    protected DataControl(final SecurityPreferences pPreferences) {
        /* Create the Secure Manager */
        theSecurity = pPreferences.getSecurity();

        /* Create the Debug Map */
        theMap = new HashMap<String, JDataEntry>();
    }

    /**
     * Record new DataSet.
     * @param pData the new DataSet
     */
    public void setData(final T pData) {
        /* If we already have data */
        if (theData != null) {
            /* Bump the generation */
            pData.setGeneration(theData.getGeneration() + 1);
        }

        /* Store the data */
        theData = pData;

        /* Update the Data entry */
        JDataEntry myData = getDataEntry(DATA_DATASET);
        myData.setObject(pData);
    }

    /**
     * Obtain current DataSet.
     * @return the current DataSet
     */
    public T getData() {
        return theData;
    }

    /**
     * Record new Updates.
     * @param pUpdates the new Updates
     */
    protected void setUpdates(final T pUpdates) {
        /* Store the updates */
        theUpdates = pUpdates;

        /* Update the Data entry */
        JDataEntry myData = getDataEntry(DATA_UPDATES);
        myData.setObject(pUpdates);
    }

    /**
     * Obtain current Updates.
     * @return the current Updates
     */
    public T getUpdates() {
        return theUpdates;
    }

    /**
     * Set new Error.
     * @param pError the new Error
     */
    protected void setError(final JDataException pError) {
        theError = pError;
    }

    /**
     * Obtain current updates.
     * @return the current Updates
     */
    public JDataException getError() {
        return theError;
    }

    /**
     * Set Frame.
     * @param pFrame the frame
     */
    public void setFrame(final JFrame pFrame) {
        theFrame = pFrame;
    }

    /**
     * Obtain Frame.
     * @return the Frame
     */
    public JFrame getFrame() {
        return theFrame;
    }

    /**
     * Obtain Secure Manager.
     * @return the Secure Manager
     */
    public SecureManager getSecurity() {
        return theSecurity;
    }

    /**
     * Set Data Manager.
     * @param pDataMgr the Data Manager
     */
    protected void setDataMgr(final JDataManager pDataMgr) {
        /* Store the Manager */
        theDataMgr = pDataMgr;

        /* Create Debug Entries */
        JDataEntry myViews = getDataEntry(DATA_VIEWS);
        JDataEntry myData = getDataEntry(DATA_DATASET);
        JDataEntry myUpdates = getDataEntry(DATA_UPDATES);
        JDataEntry myAnalysis = getDataEntry(DATA_ANALYSIS);
        JDataEntry myError = getDataEntry(DATA_ERROR);

        /* Create the structure */
        myViews.addAsRootChild();
        myAnalysis.addAsRootChild();
        myData.addAsChildOf(myViews);
        myUpdates.addAsChildOf(myViews);
        myError.addAsRootChild();

        /* Hide the Error Entry */
        myError.hideEntry();
    }

    /**
     * Obtain Data Manager.
     * @return the Data Manager
     */
    public JDataManager getDataMgr() {
        return theDataMgr;
    }

    /**
     * Get Debug Entry.
     * @param pName the Name of the entry
     * @return the Debug Entry
     */
    public JDataEntry getDataEntry(final String pName) {
        /* Access any existing entry */
        JDataEntry myEntry = theMap.get(pName);

        /* If the entry does not exist */
        if (myEntry == null) {
            /* Build the entry and add to the map */
            myEntry = theDataMgr.new JDataEntry(pName);
            theMap.put(pName, myEntry);
        }

        /* Return the entry */
        return myEntry;
    }

    /**
     * Obtain SpreadSheet object.
     * @return SpreadSheet object
     */
    public abstract SpreadSheet<T> getSpreadSheet();

    /**
     * Obtain Database object.
     * @return database object
     * @throws JDataException on error
     */
    public abstract Database<T> getDatabase() throws JDataException;

    /**
     * Obtain DataSet object.
     * @return dataSet object
     */
    public abstract T getNewData();

    /**
     * Refresh the Windows.
     */
    protected abstract void refreshWindow();

    /**
     * Analyse the data in the view.
     * @param bPreserve preserve any error
     * @return success true/false
     */
    protected abstract boolean analyseData(final boolean bPreserve);
}
