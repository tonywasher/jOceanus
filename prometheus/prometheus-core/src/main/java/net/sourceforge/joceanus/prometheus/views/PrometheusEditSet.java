/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.metis.data.MetisDataEditState;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.viewer.MetisViewerErrorList;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList.PrometheusDataListSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides control of a set of update-able DataLists.
 */
public class PrometheusEditSet
        implements MetisFieldItem, TethysEventProvider<PrometheusDataEvent>, PrometheusDataListSet {
    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private static final MetisFieldSet<PrometheusEditSet> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusEditSet.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusDataResource.DATASET_VERSION, PrometheusEditSet::getVersion);
    }

    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(PrometheusEditSet.class);

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

    /**
     * Report fields.
     */
    @SuppressWarnings("rawtypes")
    private final MetisFieldSet<PrometheusEditSet> theLocalFields;

    /**
     * The entry map.
     */
    private final Map<PrometheusListKey, PrometheusEditEntry<?>> theMap;

    /**
     * The DataControl.
     */
    private final PrometheusDataControl theControl;

    /**
     * The version.
     */
    private int theVersion;

    /**
     * Are we editing?
     */
    private Boolean itemEditing = Boolean.FALSE;

    /**
     * Constructor for an update list.
     * @param pControl the Data Control
     */
    public PrometheusEditSet(final PrometheusDataControl pControl) {
        /* Store the Control */
        theControl = pControl;

        /* Create event manager */
        theEventManager = new OceanusEventManager<>();

        /* Create local fields */
        theLocalFields = MetisFieldSet.newFieldSet(this);

        /* Create the map */
        theMap = new LinkedHashMap<>();
    }

    @SuppressWarnings("rawtypes")
    @Override
    public MetisFieldSet<PrometheusEditSet> getDataFieldSet() {
        return theLocalFields;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getDataFieldSet().getName();
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set editing flag.
     * @param pFlag the editing flag
     */
    public void setEditing(final Boolean pFlag) {
        itemEditing = pFlag;
    }

    /**
     * Is the item editing?
     * @return true/false
     */
    public Boolean isEditing() {
        return itemEditing;
    }

    /**
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the dataSet.
     * @return the dataSet
     */
    public PrometheusDataSet getDataSet() {
        return theControl.getData();
    }

    /**
     * Register an entry for a class.
     * @param <T> the object type
     * @param pDataType the data type
     * @return the list class entry
     */
    public <T extends PrometheusDataItem> PrometheusEditEntry<T> registerType(final PrometheusListKey pDataType) {
        /* Locate any existing entry */
        @SuppressWarnings("unchecked")
        PrometheusEditEntry<T> myEntry = (PrometheusEditEntry<T>) theMap.get(pDataType);
        if (myEntry == null) {
            /* Not found , so add it */
            final PrometheusEditEntry<T> myNewEntry = new PrometheusEditEntry<>(pDataType);
            theMap.put(pDataType, myNewEntry);
            theLocalFields.declareLocalField(myNewEntry.getName(), n -> myNewEntry);
            myEntry = myNewEntry;
        }

        /* Update list to null and return */
        myEntry.setDataList(null);
        return myEntry;
    }

    /**
     * Obtain the list for a class.
     * <p>
     * Will look first for the list in the updateSet and then in the underlying data.
     * @param <L> the list type
     * @param pDataType the data type
     * @param pClass the list class
     * @return the list
     */
    @Override
    public <L extends PrometheusDataList<?>> L getDataList(final PrometheusListKey pDataType,
                                                           final Class<L> pClass) {
        /* Locate an existing entry */
        final PrometheusEditEntry<?> myEntry = theMap.get(pDataType);

        /* Cast correctly */
        return myEntry != null
                ? pClass.cast(myEntry.getDataList())
                : theControl.getData().getDataList(pDataType, pClass);
    }

    @Override
    public boolean hasDataType(final PrometheusListKey pDataType) {
        return theMap.containsKey(pDataType);
    }

    /**
     * Set the editEntry for a type.
     * @param <T> the dataType
     * @param pDataType the data type
     * @param pList the list
     */
    public <T extends PrometheusDataItem> void setEditEntryList(final PrometheusListKey pDataType,
                                                                final PrometheusDataList<T> pList) {
        @SuppressWarnings("unchecked")
        final PrometheusEditEntry<T> myEntry = (PrometheusEditEntry<T>) theMap.get(pDataType);
        myEntry.setDataList(pList);
    }

    /**
     * Obtain an iterator over the listKeys.
     * @return the iterator
     */
    public Iterator<PrometheusListKey> keyIterator() {
        return theMap.keySet().iterator();
    }

    /**
     * Obtain an iterator over the listKeys.
     * @return the iterator
     */
    public Iterator<PrometheusEditEntry<?>> listIterator() {
        return theMap.values().iterator();
    }

    /**
     * Increment Version.
     */
    public void incrementVersion() {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask == null
                ? theControl.getNewProfile("incrementVersion")
                : myTask.startTask("incrementVersion");

        /* Increment the version */
        theVersion++;

        /* Loop through the items in the list */
        for (PrometheusEditEntry<?> myEntry : theMap.values()) {
            /* Access list */
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* Increment the version if the list exists */
            if (myDataList != null) {
                /* Note the new step */
                mySubTask.startTask(myDataList.listName());

                /* Set the new version */
                myDataList.setVersion(theVersion);

                /* postProcess */
                if (Boolean.FALSE.equals(itemEditing)) {
                    myDataList.postProcessOnUpdate();
                }
            }
        }

        /* Complete the task */
        mySubTask.end();
    }

    /**
     * Rewind items to the required version.
     * @param pVersion the version to rewind to
     */
    private void rewindToVersion(final int pVersion) {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        OceanusProfile mySubTask = myTask.startTask("reWindToVersion");

        /* Record the version */
        theVersion = pVersion;

        /* Loop through the items in the list */
        Iterator<PrometheusEditEntry<?>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            final PrometheusEditEntry<?> myEntry = myIterator.next();
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* If the list exists */
            if (myDataList != null) {
                /* Note the new step */
                mySubTask.startTask(myDataList.listName());

                /* Rewind the version */
                myDataList.rewindToVersion(theVersion);
            }
        }

        /* Need to validate after full reWind to avoid false errors */
        mySubTask = myTask.startTask("postProcess");
        myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            final PrometheusEditEntry<?> myEntry = myIterator.next();
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* If the list exists */
            if (myDataList != null) {
                /* Note the new step */
                mySubTask.startTask(myDataList.listName());

                /* postProcess */
                if (Boolean.FALSE.equals(itemEditing)) {
                    myDataList.postProcessOnUpdate();
                }
            }
        }

        /* Note the new step */
        mySubTask = myTask.startTask("Notify");

        /* Fire that we have rewound the updateSet */
        theEventManager.fireEvent(PrometheusDataEvent.REWINDUPDATES);

        /* Complete the task */
        mySubTask.end();
    }

    /**
     * Undo changes in a viewSet.
     */
    private void undoLastChange() {
        /* Ignore if we have no changes */
        if (theVersion == 0) {
            return;
        }

        /* Decrement version */
        theVersion--;

        /* Rewind to the version */
        rewindToVersion(theVersion);
    }

    /**
     * Reset changes in a viewSet.
     */
    private void resetChanges() {
        /* Ignore if we have no changes */
        if (theVersion == 0) {
            return;
        }

        /* Decrement version */
        theVersion = 0;

        /* Rewind to the version */
        rewindToVersion(theVersion);
    }

    /**
     * Apply changes in a ViewSet into the core data.
     */
    private void applyChanges() {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask.startTask("applyChanges");

        /* Validate the changes */
        validate();

        /* Reject request if there are errors */
        if (hasErrors()) {
            /* We have finished */
            mySubTask.startTask("Notify");

            /* Fire that we have rewound the updateSet */
            theEventManager.fireEvent(PrometheusDataEvent.REWINDUPDATES);

            /* Complete the task */
            mySubTask.end();
            return;
        }

        /* Apply the changes */
        boolean bSuccess = prepareChanges();

        /* analyse the data */
        if (bSuccess) {
            /* Analyse the applied changes */
            bSuccess = theControl.analyseData(false);
        }

        /* If we were successful */
        if (bSuccess) {
            /* Commit the changes */
            commitChanges();

            /* Refresh views */
            theControl.refreshViews();

            /* else we failed */
        } else {
            /* RollBack the changes */
            rollBackChanges();

            /* Re-analyse the data */
            theControl.analyseData(true);
        }

        /* Complete the task */
        mySubTask.end();
    }

    /**
     * Prepare changes in a ViewSet back into the core data.
     * @return success true/false
     */
    private boolean prepareChanges() {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask.startTask("prepareChanges");
        boolean bSuccess = true;

        /* Protect against exceptions */
        try {
            /* Loop through the items in the list */
            for (PrometheusEditEntry<?> myEntry : theMap.values()) {
                /* Note the new step */
                mySubTask.startTask(myEntry.getName());

                /* Prepare changes for the entry */
                myEntry.prepareChanges();
            }

        } catch (OceanusException e) {
            LOGGER.error("Failed to prepare changes", e);
            bSuccess = false;
        }

        /* Complete the task */
        mySubTask.end();
        return bSuccess;
    }

    /**
     * Commit changes in a ViewSet back into the core data.
     */
    private void commitChanges() {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask.startTask("commitChanges");

        /* Loop through the items in the list */
        for (PrometheusEditEntry<?> myEntry : theMap.values()) {
            /* Note the new step */
            mySubTask.startTask(myEntry.getName());

            /* Commit changes for the entry */
            myEntry.commitChanges();
        }

        /* Increment the version and notify listeners */
        theControl.incrementVersion();
        theVersion = 0;

        /* Complete the task */
        mySubTask.end();
    }

    /**
     * RollBack changes in a ViewSet back into the core data.
     */
    private void rollBackChanges() {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask.startTask("rollBackChanges");

        /* Loop through the items in the list */
        for (PrometheusEditEntry<?> myEntry : theMap.values()) {
            /* Note the new step */
            mySubTask.startTask(myEntry.getName());

            /* RollBack changes for the entry */
            myEntry.rollBackChanges();
        }

        /* Complete the task */
        mySubTask.end();
    }

    /**
     * Has this UpdateSet got updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* We have changes if version is non-zero */
        return theVersion != 0;
    }

    /**
     * Has this UpdateSet got errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Loop through the items in the list */
        for (PrometheusEditEntry<?> myEntry : theMap.values()) {
            /* Access entry */
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* Determine whether there are errors */
            if (myDataList != null && myDataList.hasErrors()) {
                return true;
            }
        }

        /* Return to caller */
        return false;
    }

    /**
     * Validate the updateSet.
     */
    public void validate() {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask.startTask("validate");

        /* Loop through the items in the list */
        for (PrometheusEditEntry<?> myEntry : theMap.values()) {
            /* Access list */
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* if list exists */
            if (myDataList != null) {
                /* Note the new step */
                mySubTask.startTask(myDataList.listName());

                /* Validate */
                myDataList.validate();
            }
        }

        /* Complete the task */
        mySubTask.end();
    }

    /**
     * Get the edit state of this set of tables.
     * @return the edit state
     */
    public MetisDataEditState getEditState() {
        /* Loop through the items in the list */
        final Iterator<PrometheusEditEntry<?>> myIterator = theMap.values().iterator();
        MetisDataEditState myState = MetisDataEditState.CLEAN;
        while (myIterator.hasNext()) {
            /* Access list */
            final PrometheusEditEntry<?> myEntry = myIterator.next();
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* Combine states if list exists */
            if (myDataList != null) {
                myState = myState.combineState(myDataList.getEditState());
            }
        }

        /* Return the state */
        return myState;
    }

    /**
     * Process Save command.
     * @param pCmd the command.
     * @param pError the error panel
     */
    public void processCommand(final PrometheusUIEvent pCmd,
                               final MetisErrorPanel pError) {
        /* Create a new profile */
        final OceanusProfile myTask = theControl.getNewProfile("EditCommand");

        /* Switch on command */
        switch (pCmd) {
            case OK:
                applyChanges();
                break;
            case UNDO:
                undoLastChange();
                break;
            case RESET:
                resetChanges();
                break;
            default:
                break;
        }

        /* Access any error */
        final MetisViewerErrorList myErrors = theControl.getErrors();

        /* Show the error */
        if (!myErrors.isEmpty()) {
            pError.setErrors(myErrors);
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Process Edit command.
     * @param pCmd the command.
     * @param pVersion the version
     * @param pError the error panel
     */
    public void processEditCommand(final PrometheusUIEvent pCmd,
                                   final int pVersion,
                                   final MetisErrorPanel pError) {
        /* Create a new profile */
        final OceanusProfile myTask = theControl.getNewProfile("ItemCommand");

        /* Switch on command */
        switch (pCmd) {
            case OK:
                condenseHistory(pVersion);
                break;
            case UNDO:
                undoLastChange();
                break;
            case REWIND:
                rewindToVersion(pVersion);
                break;
            default:
                break;
        }

        /* Access any error */
        final MetisViewerErrorList myErrors = theControl.getErrors();

        /* Show the error */
        if (!myErrors.isEmpty()) {
            pError.setErrors(myErrors);
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Condense history.
     * @param pNewVersion the new maximum version
     */
    private void condenseHistory(final int pNewVersion) {
        /* Obtain the active profile */
        final OceanusProfile myTask = theControl.getActiveTask();
        final OceanusProfile mySubTask = myTask.startTask("condenseHistory");

        /* Loop through the items in the list */
        for (PrometheusEditEntry<?> myEntry : theMap.values()) {
            /* Access list */
            final PrometheusDataList<?> myDataList = myEntry.getDataList();

            /* Condense history in the list */
            if (myDataList != null) {
                /* Note the new step */
                final OceanusProfile myListTask = mySubTask.startTask(myDataList.listName());
                myListTask.startTask("Condense");

                /* Condense history */
                myDataList.condenseHistory(pNewVersion);

                /* postProcess */
                if (Boolean.FALSE.equals(itemEditing)) {
                    myListTask.startTask("postProcess");
                    myDataList.postProcessOnUpdate();
                }
            }
        }

        /* Store version */
        theVersion = pNewVersion;

        /* Complete the task */
        mySubTask.end();
    }
}
