/*******************************************************************************
 * JDataModel: Data models
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.EditState;

/**
 * Provides control of a set of update-able DataLists.
 */
public class UpdateSet {
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
        return myList;
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
            DataList<?, ?> myDataList = myList.theDataList;

            /* Increment the version */
            if (myDataList != null) {
                myDataList.setVersion(theVersion);
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
            DataList<?, ?> myDataList = myList.theDataList;

            /* rewind to the version */
            if (myDataList != null) {
                myDataList.rewindToVersion(theVersion);
            }
        }
    }

    /**
     * Apply changes in a ViewSet into the core data.
     */
    public void applyChanges() {
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
            DataList<?, ?> myDataList = myList.theDataList;

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
            DataList<?, ?> myDataList = myList.theDataList;

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
            DataList<?, ?> myDataList = myList.theDataList;

            /* commit the changes */
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
            DataList<?, ?> myDataList = myList.theDataList;

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
            DataList<?, ?> myDataList = myList.theDataList;

            /* Determine whether there are errors */
            if ((myDataList != null) && (myDataList.hasErrors())) {
                return true;
            }
        }

        /* Return to caller */
        return false;
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
            DataList<?, ?> myDataList = myList.theDataList;

            /* Combine states if list exists */
            if (myDataList != null) {
                myState = myState.combineState(myDataList.getEditState());
            }
        }

        /* Return the state */
        return myState;
    }

    /**
     * Update entry items.
     */
    public final class UpdateEntry {
        /**
         * The class.
         */
        private final Class<?> theClass;

        /**
         * The DataList.
         */
        private DataList<?, ?> theDataList = null;

        /**
         * Set the Data list.
         * @param pDataList the DataList
         */
        public void setDataList(final DataList<?, ?> pDataList) {
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
