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

import net.sourceforge.JDataManager.DebugManager;
import net.sourceforge.JDataManager.DebugManager.DebugEntry;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JGordianKnot.SecureManager;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.threads.WorkerThread;
import uk.co.tolcroft.models.ui.StatusBar;

/**
 * Provides top-level control of data.
 * @param <T> the DataSet type
 */
public abstract class DataControl<T extends DataSet<T>> {
    /**
     * Debug View Name.
     */
    public static final String DEBUG_VIEWS = "DataViews";

    /**
     * Underlying Data Name.
     */
    public static final String DEBUG_DATA = "UnderlyingData";

    /**
     * Data Updates Name.
     */
    public static final String DEBUG_UPDATES = "DataUpdates";

    /**
     * Analysis Name.
     */
    public static final String DEBUG_ANALYSIS = "Analysis";

    /**
     * Error Name.
     */
    public static final String DEBUG_ERROR = "Error";

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
    private ModelException theError = null;

    /**
     * The StatusBar.
     */
    private StatusBar theStatusBar = null;

    /**
     * The Frame.
     */
    private JFrame theFrame = null;

    /**
     * The Security Manager.
     */
    private final SecureManager theSecurity;

    /**
     * The Debug Manager.
     */
    private DebugManager theDebugMgr = null;

    /**
     * The Debug Entry hashMap.
     */
    private final HashMap<String, DebugEntry> theMap;

    /**
     * Constructor.
     */
    protected DataControl() {
        /* Create the Secure Manager */
        theSecurity = new SecureManager();

        /* Create the Debug Map */
        theMap = new HashMap<String, DebugEntry>();
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

        /* Update the Debug entry */
        DebugEntry myDebug = getDebugEntry(DEBUG_DATA);
        myDebug.setObject(pData);
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

        /* Update the Debug entry */
        DebugEntry myDebug = getDebugEntry(DEBUG_UPDATES);
        myDebug.setObject(pUpdates);
    }

    /**
     * Obtain current Updates.
     * @return the current Updates
     */
    public T getUpdates() {
        return theUpdates;
    }

    /**
     * Obtain a new ThreadStatus.
     * @param pThread the thread to get the status for
     * @return a new ThreadStatus
     */
    public abstract ThreadStatus<T> allocateThreadStatus(WorkerThread<?> pThread);

    /**
     * Set new Error.
     * @param pError the new Error
     */
    protected void setError(final ModelException pError) {
        theError = pError;
    }

    /**
     * Obtain current updates.
     * @return the current Updates
     */
    public ModelException getError() {
        return theError;
    }

    /**
     * Set StatusBar.
     * @param pStatusBar the StatusBar
     */
    public void setStatusBar(final StatusBar pStatusBar) {
        theStatusBar = pStatusBar;
    }

    /**
     * Obtain StatusBar.
     * @return the StatusBar
     */
    public StatusBar getStatusBar() {
        return theStatusBar;
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
     * Set Debug Manager.
     * @param pDebugMgr the Debug Manager
     */
    protected void setDebugMgr(final DebugManager pDebugMgr) {
        /* Store the Manager */
        theDebugMgr = pDebugMgr;

        /* Create Debug Entries */
        DebugEntry myViews = getDebugEntry(DEBUG_VIEWS);
        DebugEntry myData = getDebugEntry(DEBUG_DATA);
        DebugEntry myUpdates = getDebugEntry(DEBUG_UPDATES);
        DebugEntry myAnalysis = getDebugEntry(DEBUG_ANALYSIS);
        DebugEntry myError = getDebugEntry(DEBUG_ERROR);

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
     * Obtain Debug Manager.
     * @return the Debug Manager
     */
    public DebugManager getDebugMgr() {
        return theDebugMgr;
    }

    /**
     * Get Debug Entry.
     * @param pName the Name of the entry
     * @return the Debug Entry
     */
    public DebugEntry getDebugEntry(final String pName) {
        /* Access any existing entry */
        DebugEntry myEntry = theMap.get(pName);

        /* If the entry does not exist */
        if (myEntry == null) {
            /* Build the entry and add to the map */
            myEntry = theDebugMgr.new DebugEntry(pName);
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
     * @throws ModelException on error
     */
    public abstract Database<T> getDatabase() throws ModelException;

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
