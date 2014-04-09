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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JMetisExceptionWrapper;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEventObject;

/**
 * Provides control of a set of update-able DataLists.
 * @param <E> the data type enum class
 */
public class UpdateSet<E extends Enum<E>>
        extends JEventObject
        implements JDataContents {
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
        return getDataFields().getName() + "(" + theList.size() + ")";
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
        return JDataFieldValue.UNKNOWN;
    }

    /**
     * The list.
     */
    private final List<UpdateEntry<?, E>> theList;

    /**
     * The DataControl.
     */
    private final DataControl<?, E> theControl;

    /**
     * The version.
     */
    private int theVersion = 0;

    /**
     * Constructor for an update list.
     * @param pControl the Data Control
     */
    public UpdateSet(final DataControl<?, E> pControl) {
        /* Store the Control */
        theControl = pControl;

        /* Create local fields */
        theLocalFields = new JDataFields(FIELD_DEFS.getName(), FIELD_DEFS);

        /* Create the list */
        theList = new ArrayList<UpdateEntry<?, E>>();
    }

    /**
     * Register an entry for a class.
     * @param <T> the data type
     * @param pClass the class
     * @return the list class entry
     */
    public <T extends DataItem<E> & Comparable<? super T>> UpdateEntry<T, E> registerClass(final Class<T> pClass) {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            UpdateEntry<?, E> myEntry = myIterator.next();

            /* If we have found the class */
            if (myEntry.isClass(pClass)) {
                /* Update list to null and return */
                myEntry.setDataList(null);
                return (UpdateEntry<T, E>) myEntry;
            }
        }

        /* Not found , so add it */
        UpdateEntry<T, E> myResult = new UpdateEntry<T, E>(pClass);
        theList.add(myResult);
        theLocalFields.declareLocalField(myResult.getName());
        return myResult;
    }

    /**
     * Obtain the list for a class.
     * @param <T> the data type
     * @param pClass the class
     * @return the list
     */
    public <T extends DataItem<E> & Comparable<? super T>> DataList<?, E> findClass(final Class<T> pClass) {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            UpdateEntry<?, E> myEntry = myIterator.next();

            /* If we have found the class */
            if (myEntry.isClass(pClass)) {
                /* Update list to null and return */
                return myEntry.getDataList();
            }
        }

        /* Not found , so add it */
        return null;
    }

    /**
     * Find the value for a field.
     * @param pName the name of the field
     * @return the value
     */
    private Object findEntryValue(final String pName) {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access entry */
            UpdateEntry<?, E> myEntry = myIterator.next();

            /* If we have found the entry */
            if (pName.equals(myEntry.getName())) {
                /* Return the value */
                DataList<?, ?> myList = myEntry.getDataList();
                return (myList == null)
                                       ? JDataFieldValue.SKIP
                                       : myList;
            }
        }

        /* Not found , so add it */
        return JDataFieldValue.UNKNOWN;
    }

    /**
     * Increment Version.
     */
    public void incrementVersion() {
        /* Increment the version */
        theVersion++;

        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

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
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

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
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

            /* If the list exists */
            if (myDataList != null) {
                /* determine edit state */
                myDataList.validate();
                myDataList.findEditState();
            }
        }

        /* Fire that we have rewound the updateSet */
        fireStateChanged();
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
            fireStateChanged();
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
            Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
            while (myIterator.hasNext()) {
                /* Prepare changes for the entry */
                UpdateEntry<?, E> myEntry = myIterator.next();
                myEntry.prepareChanges();
            }
            return true;
        } catch (JOceanusException e) {
            Logger myLogger = theControl.getLogger();
            myLogger.log(Level.SEVERE, "Failed to prepare changes", e);
            return false;
        }
    }

    /**
     * Commit changes in a ViewSet back into the core data.
     */
    private void commitChanges() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Commit changes for the entry */
            UpdateEntry<?, E> myEntry = myIterator.next();
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
        ListIterator<UpdateEntry<?, E>> myIterator = theList.listIterator(theList.size());
        while (myIterator.hasPrevious()) {
            /* Rollback changes for the entry */
            UpdateEntry<?, E> myEntry = myIterator.previous();
            myEntry.rollBackChanges();
        }
    }

    /**
     * Has this ViewList got updates.
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access the list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

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
        Iterator<UpdateEntry<?, E>> myIterator = theList.listIterator();
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
        /* Loop through the items in the list */
        Iterator<UpdateEntry<?, E>> myIterator = theList.listIterator();
        while (myIterator.hasNext()) {
            /* Access list */
            UpdateEntry<?, E> myEntry = myIterator.next();
            DataList<?, E> myDataList = myEntry.getDataList();

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
        Iterator<UpdateEntry<?, E>> myIterator = theList.listIterator();
        EditState myState = EditState.CLEAN;
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
        DataErrorList<JMetisExceptionWrapper> myErrors = theControl.getErrors();

        /* Show the error */
        if (!myErrors.isEmpty()) {
            pError.setErrors(myErrors);
        }
    }
}
