/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataErrorList;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.ui.SaveButtons;
import net.sourceforge.jOceanus.jEventManager.JEventObject;

/**
 * Provides control of a set of update-able DataLists.
 */
public class UpdateSet
        extends JEventObject
        implements JDataContents {
    /**
     * Rewind action.
     */
    public static final String ACTION_REWIND = "ReWind";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(UpdateSet.class.getName());

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    /**
     * Version field id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareEqualityField(NLS_BUNDLE.getString("DataVersion"));

    /**
     * Report fields.
     */
    private final JDataFields theLocalFields;

    @Override
    public JDataFields getDataFields() {
        return theLocalFields;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName()
               + "("
               + theList.size()
               + ")";
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_VERSION.equals(pField)) {
            return theVersion;
        }

        /* If the field is an entry handle specially */
        if (pField.getAnchor() == theLocalFields) {
            /* Obtain the entry */
            return findEntryValue(pField.getName());
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * The list.
     */
    private final List<UpdateEntry<?>> theList;

    /**
     * The DataControl.
     */
    private final DataControl<?> theControl;

    /**
     * The version.
     */
    private int theVersion = 0;

    /**
     * Constructor for an update list.
     * @param pControl the Data Control
     */
    public UpdateSet(final DataControl<?> pControl) {
        /* Store the Control */
        theControl = pControl;

        /* Create local fields */
        theLocalFields = new JDataFields(FIELD_DEFS.getName(), FIELD_DEFS);

        /* Create the list */
        theList = new ArrayList<UpdateEntry<?>>();
    }

    /**
     * Register an entry for a class.
     * @param <T> the data type
     * @param pClass the class
     * @return the list class entry
     */
    @SuppressWarnings("unchecked")
    public <T extends DataItem & Comparable<? super T>> UpdateEntry<T> registerClass(final Class<T> pClass) {
        Iterator<UpdateEntry<?>> myIterator;
        UpdateEntry<?> myEntry;
        UpdateEntry<T> myResult;

        /* Loop through the items in the list */
        myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            myEntry = myIterator.next();

            /* If we have found the class */
            if (myEntry.isClass(pClass)) {
                /* Update list to null and return */
                myEntry.setDataList(null);
                return (UpdateEntry<T>) myEntry;
            }
        }

        /* Not found , so add it */
        myResult = new UpdateEntry<T>(pClass);
        theList.add(myResult);
        theLocalFields.declareLocalField(myResult.getName());
        return myResult;
    }

    /**
     * Find the value for a field.
     * @param pName the name of the field
     * @return the value
     */
    private Object findEntryValue(final String pName) {
        Iterator<UpdateEntry<?>> myIterator;
        UpdateEntry<?> myEntry;

        /* Loop through the items in the list */
        myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            myEntry = myIterator.next();

            /* If we have found the entry */
            if (pName.equals(myEntry.getName())) {
                /* Return the value */
                DataList<?> myList = myEntry.getDataList();
                return (myList == null)
                        ? JDataFieldValue.SkipField
                        : myList;
            }
        }

        /* Not found , so add it */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Increment Version.
     */
    public void incrementVersion() {
        /* Increment the version */
        theVersion++;

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

            /* Increment the version if the list exists */
            if (myDataList != null) {
                /* Set the new version and validate the list */
                myDataList.setVersion(theVersion);
                myDataList.validate();
                myDataList.findEditState();
            }
        }
    }

    /**
     * Rewind items to the require version.
     * @param pVersion the version to rewind to
     */
    public void rewindToVersion(final int pVersion) {
        /* Record the version */
        theVersion = pVersion;

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

            /* If the list exists */
            if (myDataList != null) {
                /* Rewind the version */
                myDataList.rewindToVersion(theVersion);
            }
        }

        /* Loop through the items in the list */
        myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

            /* If the list exists */
            if (myDataList != null) {
                /* determine edit state */
                myDataList.validate();
                myDataList.findEditState();
            }
        }

        /* Fire that we have rewound the updateSet */
        fireActionPerformed(ACTION_REWIND);
    }

    /**
     * Undo changes in a viewSet.
     */
    public void undoLastChange() {
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
    public void resetChanges() {
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
    public void applyChanges() {
        /* Validate the changes */
        validate();

        /* Reject request if there are errors */
        if (hasErrors()) {
            /* Fire that we have rewound the updateSet */
            fireActionPerformed(ACTION_REWIND);
            return;
        }

        /* Apply the changes */
        boolean bSuccess = prepareChanges();

        /* analyse the data */
        if (bSuccess) {
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
            /* Rollback the changes */
            rollBackChanges();

            /* Re-analyse the data */
            theControl.analyseData(true);
        }
    }

    /**
     * Prepare changes in a ViewSet back into the core data.
     * @return success true/false
     */
    private boolean prepareChanges() {
        /* Protect against exceptions */
        try {
            /* Loop through the items in the list */
            Iterator<UpdateEntry<?>> myIterator = theList.iterator();
            while (myIterator.hasNext()) {
                /* Prepare changes for the entry */
                UpdateEntry<?> myEntry = myIterator.next();
                myEntry.prepareChanges();
            }
            return true;
        } catch (JDataException e) {
            return false;
        }
    }

    /**
     * Commit changes in a ViewSet back into the core data.
     */
    private void commitChanges() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Commit changes for the entry */
            UpdateEntry<?> myEntry = myIterator.next();
            myEntry.commitChanges();
        }

        /* Increment the version and notify listeners */
        theControl.incrementVersion();
    }

    /**
     * RollBack changes in a ViewSet back into the core data.
     */
    private void rollBackChanges() {
        /* Loop backwards through the items in the list */
        ListIterator<UpdateEntry<?>> myIterator = theList.listIterator(theList.size());
        while (myIterator.hasPrevious()) {
            /* Rollback changes for the entry */
            UpdateEntry<?> myEntry = myIterator.previous();
            myEntry.rollBackChanges();
        }
    }

    /**
     * Has this ViewList got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access the list */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

            /* Determine whether there are updates */
            if ((myDataList != null)
                && (myDataList.hasUpdates())) {
                return true;
            }
        }

        /* Return to caller */
        return false;
    }

    /**
     * Has this ViewList got errors.
     * @return true/false
     */
    public boolean hasErrors() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.listIterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

            /* Determine whether there are errors */
            if ((myDataList != null)
                && (myDataList.hasErrors())) {
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
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.listIterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

            /* Combine states if list exists */
            if (myDataList != null) {
                /* Validate and calculate edit State */
                myDataList.validate();
                myDataList.findEditState();
            }
        }
    }

    /**
     * Get the edit state of this set of tables.
     * @return the edit state
     */
    public EditState getEditState() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?>> myIterator = theList.listIterator();
        EditState myState = EditState.CLEAN;
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?> myEntry = myIterator.next();
            DataList<?> myDataList = myEntry.getDataList();

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
    public void processCommand(final String pCmd,
                               final ErrorDisplay pError) {
        /* Switch on command */
        if (SaveButtons.CMD_OK.equals(pCmd)) {
            applyChanges();
        } else if (SaveButtons.CMD_UNDO.equals(pCmd)) {
            undoLastChange();
        } else if (SaveButtons.CMD_RESET.equals(pCmd)) {
            resetChanges();
        }

        /* Access any error */
        DataErrorList<JDataException> myErrors = theControl.getErrors();

        /* Show the error */
        if (myErrors.size() > 0) {
            pError.setErrors(myErrors);
        }
    }

    /**
     * Error display interface.
     */
    public interface ErrorDisplay {
        /**
         * Set error list for window.
         * @param pExceptions the exceptions
         */
        void setErrors(final DataErrorList<JDataException> pExceptions);
    }
}
