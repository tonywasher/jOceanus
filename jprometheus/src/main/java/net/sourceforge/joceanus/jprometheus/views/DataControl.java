/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.views;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JMetisExceptionWrapper;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.preferences.JFieldPreferences;
import net.sourceforge.joceanus.jprometheus.preferences.SecurityPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.SpreadSheet;
import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEventObject;

/**
 * Provides top-level control of data.
 * @param <T> the DataSet type
 */
public abstract class DataControl<T extends DataSet<T, ?>>
        extends JEventObject {
    /**
     * Rewind action.
     */
    public static final String ACTION_UPDATE = "DataUpdate";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DataControl.class.getName());

    /**
     * Debug View Name.
     */
    public static final String DATA_VIEWS = NLS_BUNDLE.getString("DataDataViews");

    /**
     * Underlying Data Name.
     */
    public static final String DATA_DATASET = NLS_BUNDLE.getString("DataSet");

    /**
     * Data Updates Name.
     */
    public static final String DATA_UPDATES = NLS_BUNDLE.getString("DataUpdates");

    /**
     * Analysis Name.
     */
    public static final String DATA_ANALYSIS = NLS_BUNDLE.getString("DataAnalysis");

    /**
     * Debug Edit Name.
     */
    public static final String DATA_EDIT = NLS_BUNDLE.getString("DataEditViews");

    /**
     * Debug Maintenance Name.
     */
    public static final String DATA_MAINT = NLS_BUNDLE.getString("DataMaint");

    /**
     * Error Name.
     */
    public static final String DATA_ERROR = NLS_BUNDLE.getString("DataError");

    /**
     * The DataSet.
     */
    private T theData = null;

    /**
     * The Update DataSet.
     */
    private T theUpdates = null;

    /**
     * The Error List.
     */
    private final DataErrorList<JMetisExceptionWrapper> theErrors;

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
     * The Logger.
     */
    private final Logger theLogger;

    /**
     * The Field Manager.
     */
    private final JFieldManager theFieldMgr;

    /**
     * The Field Preferences.
     */
    private final JFieldPreferences theFieldPreferences;

    /**
     * The Render Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * The Data Entry hashMap.
     */
    private final Map<String, JDataEntry> theMap;

    /**
     * Obtain logger.
     * @return the Logger
     */
    public Logger getLogger() {
        return theLogger;
    }

    /**
     * Constructor for default control.
     * @param pLogger the logger.
     * @throws JOceanusException on error
     */
    protected DataControl(final Logger pLogger) throws JOceanusException {
        /* Create the Debug Map */
        theMap = new HashMap<String, JDataEntry>();

        /* Create the Preference manager */
        theLogger = pLogger;
        thePreferenceMgr = new PreferenceManager(theLogger);

        /* Create the data manager */
        theDataMgr = new JDataManager(theLogger);
        initDataMgr();

        /* Access the Security Preferences */
        SecurityPreferences mySecurity = thePreferenceMgr.getPreferenceSet(SecurityPreferences.class);

        /* Create the Secure Manager */
        theSecurity = mySecurity.getSecurity(theLogger);

        /* Create the error list */
        theErrors = new DataErrorList<JMetisExceptionWrapper>();

        /* Access the Field Preferences */
        theFieldPreferences = thePreferenceMgr.getPreferenceSet(JFieldPreferences.class);

        /* Allocate the FieldManager */
        theFieldMgr = new JFieldManager(theDataMgr, theFieldPreferences.getConfiguration());
        theFieldPreferences.addChangeListener(new PreferenceListener());
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

        /* Analyse the data */
        analyseData(false);

        /* Refresh the views */
        refreshViews();
    }

    /**
     * Increment data version.
     */
    public void incrementVersion() {
        /* Increment data versions */
        int myVersion = theData.getVersion();
        theData.setVersion(myVersion + 1);

        /* Alert listeners */
        fireActionPerformed(ACTION_UPDATE);
    }

    /**
     * Obtain current DataSet.
     * @return the current DataSet
     */
    public T getData() {
        return theData;
    }

    /**
     * Derive update list.
     * @throws JOceanusException on error
     */
    public void deriveUpdates() throws JOceanusException {
        /* Store the updates */
        theUpdates = theData.deriveUpdateSet();

        /* Update the Data entry */
        JDataEntry myData = getDataEntry(DATA_UPDATES);
        myData.setObject(theUpdates);
    }

    /**
     * Obtain current Updates.
     * @return the current Updates
     */
    public T getUpdates() {
        return theUpdates;
    }

    /**
     * Add new Error.
     * @param pError the new Error
     */
    public void addError(final JOceanusException pError) {
        theErrors.add(new JMetisExceptionWrapper(pError));
    }

    /**
     * Clear error list.
     */
    protected void clearErrors() {
        theErrors.clear();
    }

    /**
     * Obtain current error.
     * @return the current Error
     */
    public DataErrorList<JMetisExceptionWrapper> getErrors() {
        return theErrors;
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
     * Obtain the field manager.
     * @return the field manager
     */
    public JFieldManager getFieldMgr() {
        return theFieldMgr;
    }

    /**
     * Obtain the Data Formatter.
     * @return the data formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFieldMgr.getDataFormatter();
    }

    /**
     * Obtain the preference manager.
     * @return the preference manager
     */
    public PreferenceManager getPreferenceMgr() {
        return thePreferenceMgr;
    }

    /**
     * Initialise Data Manager.
     */
    private void initDataMgr() {
        /* Create Debug Entries */
        JDataEntry myViews = getDataEntry(DATA_VIEWS);
        JDataEntry myData = getDataEntry(DATA_DATASET);
        JDataEntry myUpdates = getDataEntry(DATA_UPDATES);
        JDataEntry myAnalysis = getDataEntry(DATA_ANALYSIS);
        JDataEntry myEdit = getDataEntry(DATA_EDIT);
        JDataEntry myMaint = getDataEntry(DATA_MAINT);
        JDataEntry myError = getDataEntry(DATA_ERROR);

        /* Create the structure */
        myViews.addAsRootChild();
        myEdit.addAsRootChild();
        myMaint.addAsRootChild();
        myError.addAsRootChild();
        myData.addAsChildOf(myViews);
        myUpdates.addAsChildOf(myViews);
        myAnalysis.addAsChildOf(myViews);

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
    public final JDataEntry getDataEntry(final String pName) {
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
     * @throws JOceanusException on error
     */
    public abstract Database<T> getDatabase() throws JOceanusException;

    /**
     * Obtain DataSet object.
     * @return dataSet object
     */
    public abstract T getNewData();

    /**
     * Analyse the data in the view.
     * @param bPreserve preserve any error
     * @return success true/false
     */
    protected abstract boolean analyseData(final boolean bPreserve);

    /**
     * refresh the data view.
     */
    protected final void refreshViews() {
        /* Refresh the Control */
        fireStateChanged();
    }

    /**
     * Undo changes in a viewSet.
     */
    public void undoLastChange() {
        /* UndoLastChange */
        theData.undoLastChange();

        /* Analyse the data */
        analyseData(false);

        /* Refresh the views */
        refreshViews();
    }

    /**
     * Reset changes in a viewSet.
     */
    public void resetChanges() {
        /* Rewind the data */
        theData.resetChanges();

        /* Analyse the data */
        analyseData(false);

        /* Refresh the views */
        refreshViews();
    }

    /**
     * Preference listener class.
     */
    private final class PreferenceListener
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent evt) {
            /* Store new configuration */
            theFieldMgr.setConfig(theFieldPreferences.getConfiguration());
        }
    }
}
