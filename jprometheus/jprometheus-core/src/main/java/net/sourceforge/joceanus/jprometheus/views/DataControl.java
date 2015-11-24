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

import javax.swing.JFrame;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.data.JDataProfile;
import net.sourceforge.joceanus.jmetis.data.JMetisExceptionWrapper;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.sheets.SpreadSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Provides top-level control of data.
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 */
public abstract class DataControl<T extends DataSet<T, E>, E extends Enum<E>>
        implements TethysEventProvider {
    /**
     * Debug View Name.
     */
    public static final String DATA_UNDERLYING = PrometheusViewResource.DATAENTRY_UNDERLYING.getValue();

    /**
     * Underlying Data Name.
     */
    private static final String DATA_DATASET = PrometheusViewResource.DATAENTRY_DATASET.getValue();

    /**
     * Data Updates Name.
     */
    private static final String DATA_UPDATES = PrometheusViewResource.DATAENTRY_UPDATES.getValue();

    /**
     * Analysis Name.
     */
    public static final String DATA_ANALYSIS = PrometheusViewResource.DATAENTRY_ANALYSIS.getValue();

    /**
     * Debug Edit Name.
     */
    public static final String DATA_VIEWS = PrometheusViewResource.DATAENTRY_VIEWS.getValue();

    /**
     * Debug Maintenance Name.
     */
    public static final String DATA_MAINT = PrometheusViewResource.DATAENTRY_MAINT.getValue();

    /**
     * Error Name.
     */
    public static final String DATA_ERROR = PrometheusViewResource.DATAENTRY_ERROR.getValue();

    /**
     * Active Profile.
     */
    private static final String DATA_PROFILE = PrometheusViewResource.DATAENTRY_PROFILE.getValue();

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

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
     * The UtilitySet.
     */
    private final JOceanusUtilitySet theUtilitySet;

    /**
     * The Active Profile.
     */
    private JDataProfile theProfile;

    /**
     * The Data Entry hashMap.
     */
    private final Map<String, ViewerEntry> theMap;

    /**
     * Constructor for default control.
     * @param pUtilitySet the utility set
     * @param pProfile the startup profile
     * @throws OceanusException on error
     */
    protected DataControl(final JOceanusUtilitySet pUtilitySet,
                          final JDataProfile pProfile) throws OceanusException {
        /* Store the parameters */
        theUtilitySet = pUtilitySet;
        theProfile = pProfile;

        /* Create the Debug Map */
        theMap = new HashMap<String, ViewerEntry>();

        /* Create event manager */
        theEventManager = new TethysEventManager();

        /* initialise the data manager */
        initDataMgr();

        /* Update the Profile entry */
        ViewerEntry myData = getDataEntry(DATA_PROFILE);
        myData.setObject(theProfile);

        /* Create the error list */
        theErrors = new DataErrorList<JMetisExceptionWrapper>();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
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
        ViewerEntry myData = getDataEntry(DATA_DATASET);
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
     * @throws OceanusException on error
     */
    public void deriveUpdates() throws OceanusException {
        /* Store the updates */
        theUpdates = theData.deriveUpdateSet();

        /* Update the Data entry */
        ViewerEntry myData = getDataEntry(DATA_UPDATES);
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
    public void addError(final OceanusException pError) {
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
     * Obtain UtilitySet.
     * @return the UtilitySet
     */
    public JOceanusUtilitySet getUtilitySet() {
        return theUtilitySet;
    }

    /**
     * Obtain DataFormatter.
     * @return the DataFormatter
     */
    public JDataFormatter getDataFormatter() {
        return theUtilitySet.getDataFormatter();
    }

    /**
     * Obtain SecureManager.
     * @return the SecureManager
     */
    public GordianHashManager getSecureManager() {
        return theUtilitySet.getSecureManager();
    }

    /**
     * Obtain PreferenceManager.
     * @return the PreferenceManager
     */
    public PreferenceManager getPreferenceManager() {
        return theUtilitySet.getPreferenceManager();
    }

    /**
     * Obtain ViewerManager.
     * @return the ViewerManager
     */
    public ViewerManager getViewerManager() {
        return theUtilitySet.getViewerManager();
    }

    /**
     * Initialise Data Manager.
     */
    private void initDataMgr() {
        /* Create Debug Entries */
        ViewerEntry myUnderlying = getDataEntry(DATA_UNDERLYING);
        ViewerEntry myData = getDataEntry(DATA_DATASET);
        ViewerEntry myAnalysis = getDataEntry(DATA_ANALYSIS);
        ViewerEntry myUpdates = getDataEntry(DATA_UPDATES);
        ViewerEntry myViews = getDataEntry(DATA_VIEWS);
        ViewerEntry myMaint = getDataEntry(DATA_MAINT);
        ViewerEntry myError = getDataEntry(DATA_ERROR);
        ViewerEntry myProfile = getDataEntry(DATA_PROFILE);

        /* Create the structure */
        myProfile.addAsRootChild();
        myUnderlying.addAsRootChild();
        myViews.addAsRootChild();
        myError.addAsRootChild();
        myData.addAsChildOf(myUnderlying);
        myAnalysis.addAsChildOf(myUnderlying);
        myUpdates.addAsChildOf(myUnderlying);
        myMaint.addAsChildOf(myViews);

        /* Hide the Error Entry */
        myError.hideEntry();
    }

    /**
     * Get viewer Entry.
     * @param pName the Name of the entry
     * @return the Debug Entry
     */
    public final ViewerEntry getDataEntry(final String pName) {
        /* Access any existing entry */
        ViewerEntry myEntry = theMap.get(pName);

        /* If the entry does not exist */
        if (myEntry == null) {
            /* Build the entry and add to the map */
            myEntry = getViewerManager().newEntry(pName);
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
     * @throws OceanusException on error
     */
    public abstract Database<T> getDatabase() throws OceanusException;

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
        /* Obtain the active profile */
        JDataProfile myTask = getActiveTask();
        myTask = myTask.startTask("refreshViews");

        /* Refresh the Control */
        theEventManager.fireStateChanged();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Undo changes in a viewSet.
     */
    public void undoLastChange() {
        /* Obtain the active profile */
        JDataProfile myTask = getActiveTask();
        myTask = myTask.startTask("unDoLastChange");

        /* UndoLastChange */
        theData.undoLastChange();
        myTask.end();

        /* Analyse the data */
        analyseData(false);

        /* Refresh the views */
        refreshViews();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Reset changes in a viewSet.
     */
    public void resetChanges() {
        /* Obtain the active profile */
        JDataProfile myTask = getActiveTask();
        myTask = myTask.startTask("resetChanges");

        /* Rewind the data */
        theData.resetChanges();
        myTask.end();

        /* Analyse the data */
        analyseData(false);

        /* Refresh the views */
        refreshViews();

        /* Complete the task */
        myTask.end();
    }

    /**
     * Create new profile.
     * @param pTask the name of the task
     * @return the new profile
     */
    public JDataProfile getNewProfile(final String pTask) {
        /* Create a new profile */
        theProfile = new JDataProfile(pTask);

        /* Update the Data entry */
        ViewerEntry myData = getDataEntry(DATA_PROFILE);
        myData.setObject(theProfile);

        /* Return the new profile */
        return theProfile;
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public JDataProfile getActiveProfile() {
        /* Create a new profile */
        return theProfile;
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public JDataProfile getActiveTask() {
        /* Create a new profile */
        return theProfile == null
                                 ? null
                                 : theProfile.getActiveTask();
    }
}
