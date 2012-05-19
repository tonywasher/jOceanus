/*******************************************************************************
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

public abstract class DataControl<T extends DataSet<T>> {
    /* Debug Names */
    public static final String DebugViews = "DataViews";
    public static final String DebugData = "UnderlyingData";
    public static final String DebugUpdates = "DataUpdates";
    public static final String DebugAnalysis = "Analysis";
    public static final String DebugError = "Error";

    /* Properties */
    private T theData = null;
    private T theUpdates = null;
    private ModelException theError = null;
    private StatusBar theStatusBar = null;
    private JFrame theFrame = null;
    private SecureManager theSecurity = null;
    private DebugManager theDebugMgr = null;

    private HashMap<String, DebugEntry> theMap = null;

    /**
     * Constructor
     */
    protected DataControl() {
        /* Create the Secure Manager */
        theSecurity = new SecureManager();

        /* Create the Debug Map */
        theMap = new HashMap<String, DebugEntry>();
    }

    /**
     * Record new DataSet
     * @param pData the new DataSet
     */
    public void setData(T pData) {
        /* If we already have data */
        if (theData != null) {
            /* Bump the generation */
            pData.setGeneration(theData.getGeneration() + 1);
        }

        /* Store the data */
        theData = pData;

        /* Update the Debug entry */
        DebugEntry myDebug = getDebugEntry(DebugData);
        myDebug.setObject(pData);
    }

    /**
     * Obtain current DataSet
     * @return the current DataSet
     */
    public T getData() {
        return theData;
    }

    /**
     * Record new Updates
     * @param pUpdates the new Updates
     */
    protected void setUpdates(T pUpdates) {
        /* Store the updates */
        theUpdates = pUpdates;

        /* Update the Debug entry */
        DebugEntry myDebug = getDebugEntry(DebugUpdates);
        myDebug.setObject(pUpdates);
    }

    /**
     * Obtain current Updates
     * @return the current Updates
     */
    public T getUpdates() {
        return theUpdates;
    }

    /**
     * Obtain a new ThreadStatus
     * @param pThread the thread to get the status for
     * @return a new ThreadStatus
     */
    public abstract ThreadStatus<T> allocateThreadStatus(WorkerThread<?> pThread);

    /**
     * Set new Error
     * @param pError the new Error
     */
    protected void setError(ModelException pError) {
        theError = pError;
    }

    /**
     * Obtain current updates
     * @return the current Updates
     */
    public ModelException getError() {
        return theError;
    }

    /**
     * Set StatusBar
     * @param pStatusBar the StatusBar
     */
    public void setStatusBar(StatusBar pStatusBar) {
        theStatusBar = pStatusBar;
    }

    /**
     * Obtain StatusBar
     * @return the StatusBar
     */
    public StatusBar getStatusBar() {
        return theStatusBar;
    }

    /**
     * Set Frame
     * @param pFrame the frame
     */
    public void setFrame(JFrame pFrame) {
        theFrame = pFrame;
    }

    /**
     * Obtain Frame
     * @return the Frame
     */
    public JFrame getFrame() {
        return theFrame;
    }

    /**
     * Obtain Secure Manager
     * @return the Secure Manager
     */
    public SecureManager getSecurity() {
        return theSecurity;
    }

    /**
     * Set Debug Manager
     * @param pDebugMgr the Debug Manager
     */
    protected void setDebugMgr(DebugManager pDebugMgr) {
        /* Store the Manager */
        theDebugMgr = pDebugMgr;

        /* Create Debug Entries */
        DebugEntry myViews = getDebugEntry(DebugViews);
        DebugEntry myData = getDebugEntry(DebugData);
        DebugEntry myUpdates = getDebugEntry(DebugUpdates);
        DebugEntry myAnalysis = getDebugEntry(DebugAnalysis);
        DebugEntry myError = getDebugEntry(DebugError);

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
     * Obtain Debug Manager
     * @return the Debug Manager
     */
    public DebugManager getDebugMgr() {
        return theDebugMgr;
    }

    /**
     * Add Debug Entry
     * @param pName the Name of the entry
     * @return the Debug Entry
     */
    public DebugEntry getDebugEntry(String pName) {
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
     * Obtain SpreadSheet object
     * @return SpreadSheet object
     */
    public abstract SpreadSheet<T> getSpreadSheet();

    /**
     * Obtain Database object
     * @return database object
     * @throws ModelException
     */
    public abstract Database<T> getDatabase() throws ModelException;

    /**
     * Obtain DataSet object
     * @return dataSet object
     */
    public abstract T getNewData();

    /**
     * Refresh the Windows
     */
    protected abstract void refreshWindow();

    /**
     * Analyse the data in the view
     * @param bPreserve preserve any error
     * @return success true/false
     */
    protected abstract boolean analyseData(boolean bPreserve);
}
