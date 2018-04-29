/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui.swing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.list.MetisListChange;
import net.sourceforge.joceanus.jmetis.list.MetisListEvent;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * Metis swing table list manager.
 * @param <R> the item type
 */
public class MetisSwingTableListManager<R extends MetisFieldTableItem> {
    /**
     * The underlying List.
     */
    private final MetisListIndexed<R> theCoreList;

    /**
     * The TableList.
     */
    private final List<R> theTableList;

    /**
     * The IdList.
     */
    private final List<Integer> theIdList;

    /**
     * The TableManager.
     */
    private final MetisSwingTableManager<R> theTable;

    /**
     * Constructor.
     * @param pTable the table manager
     * @param pList the list
     */
    protected MetisSwingTableListManager(final MetisSwingTableManager<R> pTable,
                                         final MetisListIndexed<R> pList) {
        /* Store parameters */
        theTable = pTable;
        theCoreList = pList;

        /* Create the lists */
        theTableList = new ArrayList<>();
        theIdList = new ArrayList<>();

        /* Initialise the lists */
        initialiseLists();

        /* Listen to events on the editList */
        final TethysEventRegistrar<MetisListEvent> myRegistrar = theCoreList.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> handleRefresh());
        myRegistrar.addEventListener(MetisListEvent.UPDATE, this::handleEditChanges);
        myRegistrar.addEventListener(MetisListEvent.VERSION, this::handleEditChanges);
    }

    /**
     * Obtain the table list.
     * @return the table list
     */
    public List<R> getTableList() {
        return theTableList;
    }

    /**
     * Handle refresh of list.
     */
    private void handleRefresh() {
        /* Initialise the lists */
        initialiseLists();

        /* Notify the table */
        theTable.fireTableDataChanged();
    }

    /**
     * Initialise the list.
     */
    private void initialiseLists() {
        /* Clear lists */
        theTableList.clear();
        theIdList.clear();

        /* Loop through the underlying list */
        final Iterator<R> myIterator = theCoreList.iterator();
        while (myIterator.hasNext()) {
            final R myItem = myIterator.next();

            /* Add to the lists */
            theTableList.add(myItem);
            theIdList.add(myItem.getIndexedId());
        }
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
    }

    /**
     * Handle added items in the edit list.
     * @param pIterator the iterator
     */
    private void handleAddedItems(final Iterator<R> pIterator) {
        /* Loop through the added items */
        while (pIterator.hasNext()) {
            final R myCurr = pIterator.next();
            final Integer myId = myCurr.getIndexedId();

            /* Add the item to the list */
            theTableList.add(myCurr);
            theIdList.add(myId);

            /* report the addition */
            theTable.fireTableRowAdded(theTableList.size() - 1);
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
            final Integer myId = myCurr.getIndexedId();

            /* Locate the item in the idList and report change */
            final int myLoc = theIdList.indexOf(myId);
            theTable.fireTableRowChanged(myLoc);
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

            /* Locate the item in the idList and report deletion */
            final int myLoc = theIdList.indexOf(myId);
            theTable.fireTableRowDeleted(myLoc);

            /* remove the item and id */
            theIdList.remove(myLoc);
            theTableList.remove(myLoc);
        }
    }
}
