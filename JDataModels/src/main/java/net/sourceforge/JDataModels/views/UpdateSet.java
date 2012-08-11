/*******************************************************************************
 * JDataModels: Data models
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
package net.sourceforge.JDataModels.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.ui.ErrorPanel;
import net.sourceforge.JDataModels.ui.SaveButtons;
import net.sourceforge.JFieldSet.EditState;

/**
 * Provides control of a set of update-able DataLists.
 */
public class UpdateSet implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(UpdateSet.class.getSimpleName());

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
        return getDataFields().getName() + "(" + theList.size() + ")";
    }

    /**
     * Date field id.
     */
    public static final JDataField FIELD_VERSION = FIELD_DEFS.declareEqualityField("Version");

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
    private final List<UpdateEntry> theList;

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
        theList = new ArrayList<UpdateEntry>();
    }

    /**
     * Register an entry for a class.
     * @param pClass the class
     * @return the list class entry
     */
    public UpdateEntry registerClass(final Class<?> pClass) {
        Iterator<UpdateEntry> myIterator;
        UpdateEntry myList;

        /* Loop through the items in the list */
        myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            myList = myIterator.next();

            /* If we have found the class */
            if (myList.theClass == pClass) {
                /* Update list to null and return */
                myList.theDataList = null;
                return myList;
            }
        }

        /* Not found , so add it */
        myList = new UpdateEntry(pClass);
        theList.add(myList);
        theLocalFields.declareLocalField(myList.getName());
        return myList;
    }

    /**
     * Find the value for a field.
     * @param pName the name of the field
     * @return the value
     */
    private Object findEntryValue(final String pName) {
        Iterator<UpdateEntry> myIterator;
        UpdateEntry myList;

        /* Loop through the items in the list */
        myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            myList = myIterator.next();

            /* If we have found the entry */
            if (pName.equals(myList.getName())) {
                /* Return the value */
                return (myList.theDataList == null) ? JDataFieldValue.SkipField : myList.theDataList;
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
        Iterator<UpdateEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

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
        Iterator<UpdateEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

            /* If the list exists */
            if (myDataList != null) {
                /* Rewind the version and determine edit state */
                myDataList.rewindToVersion(theVersion);
                myDataList.findEditState();
            }
        }
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
            return;
        }

        /* Apply the changes */
        prepareChanges();

        /* analyse the data */
        boolean bSuccess = theControl.analyseData(false);

        /* If we were successful */
        if (bSuccess) {
            /* Commit the changes */
            commitChanges();

            /* Refresh windows */
            theControl.refreshWindow();

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
     */
    private void prepareChanges() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

            /* Prepare the changes */
            if (myDataList != null) {
                myDataList.prepareChanges();
            }
        }
    }

    /**
     * Commit changes in a ViewSet back into the core data.
     */
    private void commitChanges() {
        /* Increment the version */
        theControl.incrementVersion();

        /* Loop through the items in the list */
        Iterator<UpdateEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

            /* commit the changes */
            if (myDataList != null) {
                myDataList.commitChanges();
            }
        }
    }

    /**
     * RollBack changes in a ViewSet back into the core data.
     */
    private void rollBackChanges() {
        /* Loop backwards through the items in the list */
        ListIterator<UpdateEntry> myIterator = theList.listIterator(theList.size());
        while (myIterator.hasPrevious()) {
            /* Access list */
            UpdateEntry myList = myIterator.previous();
            DataList<?> myDataList = myList.theDataList;

            /* rollback the changes */
            if (myDataList != null) {
                myDataList.rollBackChanges();
            }
        }
    }

    /**
     * Has this ViewList got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

            /* Determine whether there are updates */
            if ((myDataList != null) && (myDataList.hasUpdates())) {
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
        Iterator<UpdateEntry> myIterator = theList.listIterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

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
        /* Loop through the items in the list */
        Iterator<UpdateEntry> myIterator = theList.listIterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

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
        Iterator<UpdateEntry> myIterator = theList.listIterator();
        EditState myState = EditState.CLEAN;
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry myList = myIterator.next();
            DataList<?> myDataList = myList.theDataList;

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
                               final ErrorPanel pError) {
        /* Switch on command */
        if (SaveButtons.CMD_OK.equals(pCmd)) {
            applyChanges();
        } else if (SaveButtons.CMD_UNDO.equals(pCmd)) {
            undoLastChange();
        } else if (SaveButtons.CMD_RESET.equals(pCmd)) {
            resetChanges();
        }

        /* Access any error */
        JDataException myError = theControl.getError();

        /* Show the error */
        if (myError != null) {
            pError.setError(myError);
        }
    }

    /**
     * Update entry items.
     */
    public static final class UpdateEntry {
        /**
         * The class.
         */
        private final Class<?> theClass;

        /**
         * The DataList.
         */
        private DataList<?> theDataList = null;

        /**
         * Obtain the name of the entry.
         * @return the name
         */
        public String getName() {
            return theClass.getSimpleName();
        }

        /**
         * Set the Data list.
         * @param pDataList the DataList
         */
        public void setDataList(final DataList<?> pDataList) {
            theDataList = pDataList;
        }

        /**
         * Constructor.
         * @param pClass the class
         */
        private UpdateEntry(final Class<?> pClass) {
            /* Store details */
            theClass = pClass;
        }
    }
}
