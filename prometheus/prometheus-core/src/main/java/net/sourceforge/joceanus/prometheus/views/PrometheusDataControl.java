/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.views;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.gordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.metis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerErrorList;
import net.sourceforge.joceanus.metis.viewer.MetisViewerExceptionWrapper;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.metis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.prometheus.toolkit.PrometheusToolkit;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.prometheus.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;

/**
 * Provides top-level control of data.
 */
public abstract class PrometheusDataControl
        implements TethysEventProvider<PrometheusDataEvent> {
    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The DataSet.
     */
    private PrometheusDataSet theData;

    /**
     * The Update DataSet.
     */
    private PrometheusDataSet theUpdates;

    /**
     * The Error List.
     */
    private final MetisViewerErrorList theErrors;

    /**
     * The toolkit.
     */
    private final PrometheusToolkit theToolkit;

    /**
     * The metisToolkit.
     */
    private final MetisToolkit theMetisToolkit;

    /**
     * The Viewer Entry hashMap.
     */
    private final Map<PrometheusViewerEntryId, MetisViewerEntry> theViewerMap;

    /**
     * Constructor for default control.
     * @param pToolkit the toolkit
     */
    protected PrometheusDataControl(final PrometheusToolkit pToolkit) {
        /* Store the parameters */
        theToolkit = pToolkit;
        theMetisToolkit = pToolkit.getToolkit();

        /* Create the Viewer Map and initialise it */
        theViewerMap = new EnumMap<>(PrometheusViewerEntryId.class);
        initViewerMap();

        /* Create event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create the error list */
        theErrors = new MetisViewerErrorList();
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Record new DataSet.
     * @param pData the new DataSet
     */
    public void setData(final PrometheusDataSet pData) {
        /* If we already have data */
        if (theData != null) {
            /* Bump the generation */
            pData.setGeneration(theData.getGeneration() + 1);
        }

        /* Store the data */
        theData = pData;

        /* Update the Data entry */
        final MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.DATASET);
        myData.setTreeObject(pData);

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
        final int myVersion = theData.getVersion();
        theData.setVersion(myVersion + 1);
    }

    /**
     * Obtain current DataSet.
     * @return the current DataSet
     */
    public PrometheusDataSet getData() {
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
        final MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.UPDATES);
        myData.setTreeObject(theUpdates);
    }

    /**
     * Obtain current Updates.
     * @return the current Updates
     */
    public PrometheusDataSet getUpdates() {
        return theUpdates;
    }

    /**
     * Add new Error.
     * @param pError the new Error
     */
    public void addError(final OceanusException pError) {
        theErrors.add(new MetisViewerExceptionWrapper(pError));
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
    public MetisViewerErrorList getErrors() {
        return theErrors;
    }

    /**
     * Obtain toolkit.
     * @return the toolkit
     */
    public PrometheusToolkit getToolkit() {
        return theToolkit;
    }

    /**
     * Obtain DataFormatter.
     * @return the DataFormatter
     */
    public TethysUIDataFormatter getDataFormatter() {
        return theMetisToolkit.getFormatter();
    }

    /**
     * Obtain SecurityManager.
     * @return the SecurityManager
     */
    public GordianPasswordManager getPasswordManager() {
        return theToolkit.getPasswordManager();
    }

    /**
     * Obtain PreferenceManager.
     * @return the PreferenceManager
     */
    public PrometheusPreferenceManager getPreferenceManager() {
        return theToolkit.getPreferenceManager();
    }

    /**
     * Obtain ViewerManager.
     * @return the ViewerManager
     */
    public MetisViewerManager getViewerManager() {
        return theMetisToolkit.getViewerManager();
    }

    /**
     * Obtain GuiFactory.
     * @return the GuiFactory
     */
    public TethysUIFactory<?> getGuiFactory() {
        return theMetisToolkit.getGuiFactory();
    }

    /**
     * Initialise ViewerMap.
     */
    private void initViewerMap() {
        /* Access the viewer manager */
        final MetisViewerManager myViewer = getViewerManager();

        /* Access standard entries */
        theViewerMap.put(PrometheusViewerEntryId.ERROR, myViewer.getStandardEntry(MetisViewerStandardEntry.ERROR));
        theViewerMap.put(PrometheusViewerEntryId.PROFILE, myViewer.getStandardEntry(MetisViewerStandardEntry.PROFILE));
        theViewerMap.put(PrometheusViewerEntryId.DATA, myViewer.getStandardEntry(MetisViewerStandardEntry.DATA));
        theViewerMap.put(PrometheusViewerEntryId.UPDATES, myViewer.getStandardEntry(MetisViewerStandardEntry.UPDATES));
        theViewerMap.put(PrometheusViewerEntryId.VIEW, myViewer.getStandardEntry(MetisViewerStandardEntry.VIEW));

        /* Create Data entries */

        final MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.DATA);
        theViewerMap.put(PrometheusViewerEntryId.DATASET, myViewer.newEntry(myData, PrometheusViewerEntryId.DATASET.toString()));
        theViewerMap.put(PrometheusViewerEntryId.ANALYSIS, myViewer.newEntry(myData, PrometheusViewerEntryId.ANALYSIS.toString()));

        /* Create View entries */
        final MetisViewerEntry myView = getViewerEntry(PrometheusViewerEntryId.VIEW);
        final MetisViewerEntry myMaint = myViewer.newEntry(myView, PrometheusViewerEntryId.MAINTENANCE.toString());
        theViewerMap.put(PrometheusViewerEntryId.MAINTENANCE, myMaint);
        theViewerMap.put(PrometheusViewerEntryId.STATIC, myViewer.newEntry(myMaint, PrometheusViewerEntryId.STATIC.toString()));

        /* Hide the error entry */
        final MetisViewerEntry myError = theViewerMap.get(PrometheusViewerEntryId.ERROR);
        myError.setVisible(myError.getObject() != null);
    }

    /**
     * Get viewer Entry.
     * @param pId the id of the entry
     * @return the Viewer Entry
     */
    public final MetisViewerEntry getViewerEntry(final PrometheusViewerEntryId pId) {
        return theViewerMap.get(pId);
    }

    /**
     * Obtain SpreadSheet object.
     * @return SpreadSheet object
     */
    public abstract PrometheusSpreadSheet getSpreadSheet();

    /**
     * Obtain the database name.
     * @return the name
     */
    public abstract String getDatabaseName();

    /**
     * Obtain Database object.
     * @return database object
     * @throws OceanusException on error
     */
    public abstract PrometheusDataStore getDatabase() throws OceanusException;

    /**
     * Obtain Null Database object.
     * @return database object
     * @throws OceanusException on error
     */
    public abstract PrometheusDataStore getNullDatabase() throws OceanusException;

    /**
     * Obtain DataSet object.
     * @return dataSet object
     */
    public abstract PrometheusDataSet getNewData();

    /**
     * Analyse the data in the view.
     * @param bPreserve preserve any error
     * @return success true/false
     */
    protected abstract boolean analyseData(boolean bPreserve);

    /**
     * refresh the data view.
     */
    final void refreshViews() {
        /* Obtain the active profile */
        OceanusProfile myTask = getActiveTask();
        myTask = myTask.startTask("refreshViews");

        /* Refresh the Control */
        theEventManager.fireEvent(PrometheusDataEvent.REFRESHVIEWS);

        /* Complete the task */
        myTask.end();
    }

    /**
     * Undo changes in a viewSet.
     */
    public void undoLastChange() {
        /* Obtain the active profile */
        OceanusProfile myTask = getActiveTask();
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
        OceanusProfile myTask = getActiveTask();
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
    public OceanusProfile getNewProfile(final String pTask) {
        return theMetisToolkit.getNewProfile(pTask);
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public OceanusProfile getActiveProfile() {
        return theMetisToolkit.getActiveProfile();
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public OceanusProfile getActiveTask() {
        return theMetisToolkit.getActiveTask();
    }
}
