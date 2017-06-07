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
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisErrorList;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisExceptionWrapper;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.lethe.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.DataListSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

/**
 * Provides control of a set of update-able DataLists.
 * @param <E> the data type enum class
 */
public class UpdateSet<E extends Enum<E>>
        implements MetisDataContents, TethysEventProvider<PrometheusDataEvent>, DataListSet<E> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(PrometheusViewResource.UPDATESET_NAME.getValue());

    /**
     * Version field id.
     */
    public static final MetisField FIELD_VERSION = FIELD_DEFS.declareEqualityField(PrometheusDataResource.DATASET_VERSION.getValue());

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateSet.class);

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * Report fields.
     */
    private final MetisFields theLocalFields;

    /**
     * The entry map.
     */
    private final Map<E, UpdateEntry<?, E>> theMap;

    /**
     * The DataControl.
     */
    private final DataControl<?, E, ?, ?> theControl;

    /**
     * The version.
     */
    private int theVersion = 0;

    /**
     * Are we editing?
     */
    private Boolean itemEditing = Boolean.FALSE;

    /**
     * Constructor for an update list.
     * @param pControl the Data Control
     * @param pClass the enum class
     */
    public UpdateSet(final DataControl<?, E, ?, ?> pControl,
                     final Class<E> pClass) {
        /* Store the Control */
        theControl = pControl;

        /* Create event manager */
        theEventManager = new TethysEventManager<>();

        /* Create local fields */
        theLocalFields = new MetisFields(FIELD_DEFS.getName(), FIELD_DEFS);

        /* Create the map */
        theMap = new EnumMap<>(pClass);
    }

    @Override
    public MetisFields getDataFields() {
        return theLocalFields;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName() + "(" + theMap.size() + ")";
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }

        /* If the field is an entry handle specially */
        if (theLocalFields.equals(pField.getAnchor())) {
            /* Obtain the entry */
            return findEntryValue(pField.getName());
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
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
     * Obtain the version.
     * @return the version
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Obtain the dataSet.
     * @param <T> the dataSet type
     * @param pClass the class of the dataSet
     * @return the dataSet
     */
    public <T extends DataSet<T, E>> T getDataSet(final Class<T> pClass) {
        return pClass.cast(theControl.getData());
    }

    /**
     * Register an entry for a class.
     * @param <T> the object type
     * @param pDataType the data type
     * @return the list class entry
     */
    public <T extends DataItem<E> & Comparable<? super T>> UpdateEntry<T, E> registerType(final E pDataType) {
        /* Locate any existing entry */
        @SuppressWarnings("unchecked")
        UpdateEntry<T, E> myEntry = (UpdateEntry<T, E>) theMap.get(pDataType);
        if (myEntry == null) {
            /* Not found , so add it */
            myEntry = new UpdateEntry<>(pDataType);
            theMap.put(pDataType, myEntry);
            theLocalFields.declareLocalField(myEntry.getName());
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
    public <L extends DataList<?, E>> L getDataList(final E pDataType,
                                                    final Class<L> pClass) {
        /* Locate an existing entry */
        UpdateEntry<?, E> myEntry = theMap.get(pDataType);

        /* Cast correctly */
        return myEntry != null
                               ? pClass.cast(myEntry.getDataList())
                               : theControl.getData().getDataList(pDataType, pClass);
    }

    /**
     * Find the value for a field.
     * @param pName the name of the field
     * @return the value
     */
    private Object findEntryValue(final String pName) {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            UpdateEntry<?, E> myEntry = myIterator.next();

            /* If we have found the entry */
            if (pName.equals(myEntry.getName())) {
                /* Return the value */
                DataList<?, ?> myList = myEntry.getDataList();
                return (myList == null)
                                        ? MetisFieldValue.SKIP
                                        : myList;
            }
        }

        /* Not found , so add it */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Increment Version.
     */
    public void incrementVersion() {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getNewProfile("incrementVersion");

        /* Increment the version */
        theVersion++;

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

            /* Increment the version if the list exists */
            if (myDataList != null) {
                /* Note the new step */
                myTask.startTask(myDataList.listName());

                /* Set the new version */
                myDataList.setVersion(theVersion);

                /* postProcess */
                if (!itemEditing) {
                    myDataList.postProcessOnUpdate();
                }
            }
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Rewind items to the require version.
     * @param pVersion the version to rewind to
     */
    private void rewindToVersion(final int pVersion) {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getActiveTask();
        MetisProfile mySubTask = myTask.startTask("reWindToVersion");

        /* Record the version */
        theVersion = pVersion;

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

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
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

            /* If the list exists */
            if (myDataList != null) {
                /* Note the new step */
                mySubTask.startTask(myDataList.listName());

                /* postProcess */
                if (!itemEditing) {
                    myDataList.postProcessOnUpdate();
                }
            }
        }

        /* Note the new step */
        myTask.startTask("Notify");

        /* Fire that we have rewound the updateSet */
        theEventManager.fireEvent(PrometheusDataEvent.REWINDUPDATES);

        /* Complete the task */
        myTask.end();
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
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("applyChanges");

        /* Validate the changes */
        validate();

        /* Reject request if there are errors */
        if (hasErrors()) {
            /* We have finished */
            myTask.startTask("Notify");

            /* Fire that we have rewound the updateSet */
            theEventManager.fireEvent(PrometheusDataEvent.REWINDUPDATES);

            /* Complete the task */
            myTask.end();
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
        myTask.end();
    }

    /**
     * Prepare changes in a ViewSet back into the core data.
     * @return success true/false
     */
    private boolean prepareChanges() {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("prepareChanges");
        boolean bSuccess = true;

        /* Protect against exceptions */
        try {
            /* Loop through the items in the list */
            Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
            while (myIterator.hasNext()) {
                UpdateEntry<?, E> myEntry = myIterator.next();

                /* Note the new step */
                myTask.startTask(myEntry.getName());

                /* Prepare changes for the entry */
                myEntry.prepareChanges();
            }

        } catch (OceanusException e) {
            LOGGER.error("Failed to prepare changes", e);
            bSuccess = false;
        }

        /* Complete the task */
        myTask.end();
        return bSuccess;
    }

    /**
     * Commit changes in a ViewSet back into the core data.
     */
    private void commitChanges() {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("commitChanges");

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            UpdateEntry<?, E> myEntry = myIterator.next();

            /* Note the new step */
            myTask.startTask(myEntry.getName());

            /* Commit changes for the entry */
            myEntry.commitChanges();
        }

        /* Increment the version and notify listeners */
        theControl.incrementVersion();
        theVersion = 0;

        /* Complete the task */
        myTask.end();
    }

    /**
     * RollBack changes in a ViewSet back into the core data.
     */
    private void rollBackChanges() {
        /* Obtain the active profile */
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("rollBackChanges");

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            UpdateEntry<?, E> myEntry = myIterator.next();

            /* Note the new step */
            myTask.startTask(myEntry.getName());

            /* RollBack changes for the entry */
            myEntry.rollBackChanges();
        }

        /* Complete the task */
        myTask.end();
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
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

            /* Determine whether there are errors */
            if ((myDataList != null) && (myDataList.hasErrors())) {
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
        MetisProfile myTask = theControl.getActiveTask();
        myTask = myTask.startTask("validate");

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

            /* if list exists */
            if (myDataList != null) {
                /* Note the new step */
                myTask.startTask(myDataList.listName());

                /* Validate */
                myDataList.validate();
            }
        }

        /* Complete the task */
        myTask.end();
    }

    /**
     * Get the edit state of this set of tables.
     * @return the edit state
     */
    public MetisEditState getEditState() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        MetisEditState myState = MetisEditState.CLEAN;
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

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
                               final MetisErrorPanel<?, ?> pError) {
        /* Create a new profile */
        MetisProfile myTask = theControl.getNewProfile("EditCommand");

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
        MetisErrorList<MetisExceptionWrapper> myErrors = theControl.getErrors();

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
                                   final MetisErrorPanel<?, ?> pError) {
        /* Create a new profile */
        MetisProfile myTask = theControl.getNewProfile("ItemCommand");

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
        MetisErrorList<MetisExceptionWrapper> myErrors = theControl.getErrors();

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
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

            /* Condense history in the list */
            if (myDataList != null) {
                myDataList.condenseHistory(pNewVersion);
            }
        }

        /* Store version */
        theVersion = pNewVersion;
    }
}