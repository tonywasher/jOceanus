/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui.javafx;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.list.MetisListChange;
import net.sourceforge.joceanus.jmetis.list.MetisListEvent;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Table FieldSet.
 * @param <R> the item type
 */
public class MetisFXTableList<R extends MetisFieldTableItem>
        implements MetisDataList<R> {
    /**
     * The table.
     */
    private final MetisFXTableManager<R> theTable;

    /**
     * The core List.
     */
    private final MetisListIndexed<R> theCoreList;

    /**
     * The observableList.
     */
    private ObservableList<R> theList;

    /**
     * The ListFields.
     */
    private final MetisFXTableListFields<R> theListFields;

    /**
     * Constructor.
     * @param pTable the table
     * @param pList the list
     */
    protected MetisFXTableList(final MetisFXTableManager<R> pTable,
                               final MetisListIndexed<R> pList) {
        /* Store parameters */
        theTable = pTable;
        theCoreList = pList;

        /* Create the list field manager */
        theListFields = new MetisFXTableListFields<>(theCoreList);

        /* Create the list */
        theList = FXCollections.observableArrayList();

        /* Listen to events on the versionedList */
        final TethysEventRegistrar<MetisListEvent> myRegistrar = theCoreList.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> refreshList());
        myRegistrar.addEventListener(MetisListEvent.UPDATE, this::handleEditChanges);
        myRegistrar.addEventListener(MetisListEvent.VERSION, this::handleEditChanges);
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
        theList.addAll(theCoreList.getUnderlyingList());
    }

    /**
     * Handle editChanges.
     * @param pChange the change
     */
    private void handleEditChanges(final TethysEvent<MetisListEvent> pChange) {
        /* Access the change detail */
        @SuppressWarnings("unchecked")
        final MetisListChange<R> myChange = (MetisListChange<R>) pChange.getDetails(MetisListChange.class);

        /* Handle deleted items */
        handleDeletedItems(myChange.deletedIterator());

        /* Handle changed items */
        handleChangedItems(myChange.hiddenIterator());
        handleChangedItems(myChange.changedIterator());
        handleChangedItems(myChange.restoredIterator());

        /* Handle added items */
        handleAddedItems(myChange.addedIterator());

        /* Force a sort */
        theTable.forceSort();
    }

    /**
     * Handle added items in the edit list.
     * @param pIterator the iterator
     */
    private void handleAddedItems(final Iterator<R> pIterator) {
        /* Loop through the added items */
        while (pIterator.hasNext()) {
            final R myCurr = pIterator.next();

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
            final R myCurr = pIterator.next();

            /* Update properties to reflect changes */
            theListFields.updateProperties(myCurr);
        }
    }

    /**
     * Handle deleted items in the edit list.
     * @param pIterator the iterator
     */
    private void handleDeletedItems(final Iterator<R> pIterator) {
        /* Loop through the added items */
        while (pIterator.hasNext()) {
            final R myItem = pIterator.next();
            final Integer myId = myItem.getIndexedId();

            /* Remove the item if present */
            theListFields.removeItem(myId);
            theList.remove(myItem);
        }
    }
}
