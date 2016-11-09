/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.ui.javafx;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataList;
import net.sourceforge.joceanus.jmetis.newlist.MetisEditList;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange;
import net.sourceforge.joceanus.jmetis.newlist.MetisListChange.MetisListEvent;
import net.sourceforge.joceanus.jmetis.newlist.MetisVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Table FieldSet.
 * @param <R> the item type
 */
public class MetisFXTableList<R extends MetisVersionedItem>
        implements MetisDataList<R> {
    /**
     * The underlying EditList.
     */
    private final MetisEditList<R> theEditList;

    /**
     * The observableList.
     */
    private final ObservableList<R> theList;

    /**
     * The ListFields.
     */
    private final MetisFXTableListFields<R> theListFields;

    /**
     * Constructor.
     * @param pList the list
     */
    protected MetisFXTableList(final MetisEditList<R> pList) {
        /* Store parameters */
        theEditList = pList;

        /* Create the list field manager */
        theListFields = new MetisFXTableListFields<>(theEditList);

        /* Create the list */
        theList = theListFields.hasComparisons()
                                                 ? FXCollections.observableArrayList()
                                                 : FXCollections.observableArrayList(theListFields::getComparisons);

        /* Listen to events on the editList */
        TethysEventRegistrar<MetisListEvent> myRegistrar = theEditList.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> refreshList());
        myRegistrar.addEventListener(MetisListEvent.UPDATE, this::handleEditChanges);
    }

    @Override
    public ObservableList<R> getUnderlyingList() {
        return theList;
    }

    /**
     * Obtain the list fields.
     * @return the list Fields
     */
    public MetisFXTableListFields<R> getListFields() {
        return theListFields;
    }

    /**
     * Refresh the list.
     */
    private void refreshList() {
        /* Clear the list */
        theList.clear();
        theListFields.clear();

        /* Add all the elements of the list */
        theList.addAll(theEditList.getUnderlyingList());
    }

    /**
     * Handle editChanges.
     * @param pChange the change
     */
    private void handleEditChanges(final TethysEvent<MetisListEvent> pChange) {
        /* Access the change detail */
        @SuppressWarnings("unchecked")
        MetisListChange<R> myChange = (MetisListChange<R>) pChange.getDetails(MetisListChange.class);

        /* Handle deleted items */
        handleDeletedItems(myChange.deletedIterator());

        /* Handle changed items */
        handleChangedItems(myChange.changedIterator());

        /* Handle added items */
        handleAddedItems(myChange.addedIterator());
    }

    /**
     * Handle added items in the edit list.
     * @param pIterator the iterator
     */
    private void handleAddedItems(final Iterator<R> pIterator) {
        /* Loop through the added items */
        while (pIterator.hasNext()) {
            R myCurr = pIterator.next();

            /* Add the item to the list */
            theList.add(myCurr);
        }
    }

    /**
     * Handle changed items in the edit list.
     * @param pIterator the iterator
     */
    private void handleChangedItems(final Iterator<R> pIterator) {
        /* Loop through the changed items */
        while (pIterator.hasNext()) {
            R myCurr = pIterator.next();

            /* Update properties to reflect changes */
            theListFields.updateProperties(myCurr);
        }
    }

    /**
     * Handle deleted items in the edit list.
     * @param pIterator the iterator
     */
    private void handleDeletedItems(final Iterator<Integer> pIterator) {
        /* Loop through the added items */
        while (pIterator.hasNext()) {
            Integer myId = pIterator.next();

            /* Remove the item if present */
            R myItem = theListFields.removeItem(myId);
            if (myItem != null) {
                theList.remove(myItem);
            }
        }
    }
}
