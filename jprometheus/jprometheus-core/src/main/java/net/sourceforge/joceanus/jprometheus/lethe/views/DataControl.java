/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.views;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.atlas.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerErrorList;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerExceptionWrapper;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.atlas.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jprometheus.lethe.JOceanusUtilitySet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;

/**
 * Provides top-level control of data.
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class DataControl<T extends DataSet<T, E>, E extends Enum<E>, N, I>
        implements TethysEventProvider<PrometheusDataEvent> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The DataSet.
     */
    private T theData;

    /**
     * The Update DataSet.
     */
    private T theUpdates;

    /**
     * The Error List.
     */
    private final MetisViewerErrorList theErrors;

    /**
     * The UtilitySet.
     */
    private final JOceanusUtilitySet<N, I> theUtilitySet;

    /**
     * The Toolkit.
     */
    private final MetisToolkit<N, I> theToolkit;

    /**
     * The Viewer Entry hashMap.
     */
    private final Map<PrometheusViewerEntryId, MetisViewerEntry> theViewerMap;

    /**
     * Constructor for default control.
     * @param pUtilitySet the utility set
     */
    protected DataControl(final JOceanusUtilitySet<N, I> pUtilitySet) {
        /* Store the parameters */
        theUtilitySet = pUtilitySet;
        theToolkit = pUtilitySet.getToolkit();

        /* Create the Viewer Map and initialise it */
        theViewerMap = new EnumMap<>(PrometheusViewerEntryId.class);
        initViewerMap();

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create the error list */
        theErrors = new MetisViewerErrorList();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
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
        final MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.UPDATES);
        myData.setTreeObject(theUpdates);
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
     * Obtain UtilitySet.
     * @return the UtilitySet
     */
    public JOceanusUtilitySet<N, I> getUtilitySet() {
        return theUtilitySet;
    }

    /**
     * Obtain DataFormatter.
     * @return the DataFormatter
     */
    public MetisDataFormatter getDataFormatter() {
        return theToolkit.getFormatter();
    }

    /**
     * Obtain SecurityManager.
     * @return the SecurityManager
     */
    public GordianHashManager getSecurityManager() {
        return theToolkit.getSecurityManager();
    }

    /**
     * Obtain PreferenceManager.
     * @return the PreferenceManager
     */
    public MetisPreferenceManager getPreferenceManager() {
        return theToolkit.getPreferenceManager();
    }

    /**
     * Obtain ViewerManager.
     * @return the ViewerManager
     */
    public MetisViewerManager getViewerManager() {
        return theToolkit.getViewerManager();
    }

    /**
     * Obtain GuiFactory.
     * @return the GuiFactory
     */
    public TethysGuiFactory<N, I> getGuiFactory() {
        return theToolkit.getGuiFactory();
    }

    /**
     * Obtain Toolkit.
     * @return the Toolkit
     */
    public MetisToolkit<N, I> getToolkit() {
        return theToolkit;
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
        theViewerMap.put(PrometheusViewerEntryId.VIEW, myViewer.getStandardEntry(MetisViewerStandardEntry.VIEW));

        /* Create Data entries */
        final MetisViewerEntry myData = getViewerEntry(PrometheusViewerEntryId.DATA);
        theViewerMap.put(PrometheusViewerEntryId.DATASET, myViewer.newEntry(myData, PrometheusViewerEntryId.DATASET.toString()));
        theViewerMap.put(PrometheusViewerEntryId.UPDATES, myViewer.newEntry(myData, PrometheusViewerEntryId.UPDATES.toString()));
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
    public abstract PrometheusSpreadSheet<T> getSpreadSheet();

    /**
     * Obtain Database object.
     * @return database object
     * @throws OceanusException on error
     */
    public abstract PrometheusDataStore<T> getDatabase() throws OceanusException;

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
    protected abstract boolean analyseData(boolean bPreserve);

    /**
     * refresh the data view.
     */
    protected final void refreshViews() {
        /* Obtain the active profile */
        MetisProfile myTask = getActiveTask();
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
        MetisProfile myTask = getActiveTask();
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
        MetisProfile myTask = getActiveTask();
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
    public MetisProfile getNewProfile(final String pTask) {
        return theToolkit.getNewProfile(pTask);
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public MetisProfile getActiveProfile() {
        return theToolkit.getActiveProfile();
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public MetisProfile getActiveTask() {
        return theToolkit.getActiveTask();
    }
}
