/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.eos.list;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * List Edit Methods.
 */
public final class MetisEosListEditManager {
    /**
     * Private constructor.
     */
    private MetisEosListEditManager() {
    }

    /**
     * Derive an edit listSet.
     * @param pBase the base listSet
     * @return the updateSet
     */
    public static MetisEosListSetVersioned deriveUpdateListSet(final MetisEosListSetVersioned pBase) {
        /* Create a new editSet */
        final MetisEosListSetVersioned myEdits = new MetisEosListSetVersioned(pBase);

        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = pBase.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisEosListVersioned<MetisFieldVersionedItem> myBase = pBase.getList(myKey);

            /* Create the new list */
            final MetisEosListVersioned<MetisFieldVersionedItem> myEdit = new MetisEosListVersioned<>(myEdits, myBase);

            /* configure the edit list */
            deriveEdits(myEdit);

            /* Add the list to the listSet */
            myEdits.declareList(myKey, myEdit);
        }

        /* Register event handlers */
        final TethysEventRegistrar<MetisEosListEvent> myRegistrar = pBase.getEventRegistrar();
        myRegistrar.addEventListener(MetisEosListEvent.REFRESH, e -> deriveEdits(myEdits));
        myRegistrar.addEventListener(MetisEosListEvent.REWIND, e -> handleReWindOfBase(myEdits, e));

        /* Return the editSet */
        return myEdits;
    }

    /**
     * Derive edit items as a result of refresh in the base listSet.
     * @param pUpdates the updates listSet
     */
    private static void deriveEdits(final MetisEosListSetVersioned pUpdates) {
        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = pUpdates.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* Obtain the update and base lists */
            final MetisEosListVersioned<MetisFieldVersionedItem> myEdits = pUpdates.getList(myKey);

            /* Derive items from the baseList */
            deriveEdits(myEdits);
        }

        /* Update the listSet version */
        pUpdates.setVersion(0);

        /* fire event */
        final MetisEosListSetChange myChanges = new MetisEosListSetChange(MetisEosListEvent.REFRESH);
        pUpdates.fireEvent(myChanges);
    }

    /**
     * Derive the edit list.
     * @param <T> the itemType for the list
     * @param pEdits the edit list
     */
    private static <T extends MetisFieldVersionedItem> void deriveEdits(final MetisEosListVersioned<T> pEdits) {
        /* Clear the list */
        pEdits.clear();

        /* Access the base list */
        final MetisEosListVersioned<T> myBaseList = pEdits.getBaseList();

        /* Loop through the list */
        final Iterator<T> myIterator = myBaseList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* No need to add the item if it is deleted */
            if (!myCurr.isDeleted()) {
                /* Create the new item */
                final T myItem = newItemFromBase(pEdits, myCurr);
                pEdits.addToList(myItem);
            }
        }

        /* Reset the version */
        pEdits.setVersion(0);
    }

    /**
     * Amend edit items as a result of reWind in the base listSet.
     * @param pEdits the edit listSet
     * @param pEvent the event
     */
    private static void handleReWindOfBase(final MetisEosListSetVersioned pEdits,
                                           final TethysEvent<MetisEosListEvent> pEvent) {
        /* Access the change details */
        final MetisEosListSetChange myChanges = pEvent.getDetails(MetisEosListSetChange.class);

        /* Loop through the lists */
        final Iterator<MetisEosListKey> myIterator = pEdits.keyIterator();
        while (myIterator.hasNext()) {
            final MetisEosListKey myKey = myIterator.next();

            /* Obtain the update list and associated change */
            final MetisEosListVersioned<MetisFieldVersionedItem> myEdits = pEdits.getList(myKey);
            MetisEosListChange<MetisFieldVersionedItem> myChange = myChanges.getListChange(myKey);

            /* If there are changes */
            if (myChange != null) {
                /* handle changes in the base list */
                myChange = doHandleReWindOfBase(myEdits, myChange);
                if (!myChange.isEmpty()) {
                    myChanges.registerChangedList(myChange);
                }
            }
        }

        /* Fire the event */
        if (!myChanges.isEmpty()) {
            pEdits.fireEvent(myChanges);
        }
    }

    /**
     * Handle reWind of Base.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pChange the change
     * @return the editList change
     */
    private static <T extends MetisFieldVersionedItem> MetisEosListChange<T> doHandleReWindOfBase(final MetisEosListVersioned<T> pList,
                                                                                                  final MetisEosListChange<T> pChange) {
        /* Create a new change */
        final MetisEosListChange<T> myChange = new MetisEosListChange<>(pList.getItemType(), MetisEosListEvent.REWIND);

        /* Handle underlying deleted items */
        handleBaseDeletedItems(pList, myChange, pChange.deletedIterator());

        /* Handle base changed items */
        handleBaseChangedItems(pList, myChange, pChange.changedIterator());

        /* Items are never added by a reWind */

        /* return the change */
        return myChange;
    }

    /**
     * Handle changed items in the base.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pChange the change
     * @param pIterator the iterator
     */
    private static <T extends MetisFieldVersionedItem> void handleBaseChangedItems(final MetisEosListVersioned<T> pList,
                                                                                   final MetisEosListChange<T> pChange,
                                                                                   final Iterator<T> pIterator) {
        /* Loop through the changed items */
        while (pIterator.hasNext()) {
            final T myCurr = pIterator.next();
            final Integer myId = myCurr.getIndexedId();

            /* Obtain the item to be changed */
            T myItem = pList.getItemById(myId);

            /* If the item is now deleted */
            if (!myCurr.isDeleted()) {
                /* If we currently have the item in the list */
                if (myItem == null) {
                    /* Remove the item */
                    pList.removeById(myId);

                    /* Record change */
                    pChange.registerDeleted(myCurr);
                }

                /* else if we do not currently have the item in the list */
            } else if (myItem == null) {
                /* Create the new item */
                myItem = newItemFromBase(pList, myCurr);
                pList.addToList(myItem);

                /* Record change */
                pChange.registerAdded(myItem);

                /* else this is a standard changed item */
            } else {
                /* Access set to be changed */
                final MetisFieldVersionValues mySet = myItem.getValueSet();

                /* Access base set */
                final MetisFieldVersionValues myBase = myCurr.getValueSet();

                /* Reset values in the item */
                mySet.copyFrom(myBase);

                /* Record change */
                pChange.registerChanged(myItem);
            }
        }
    }

    /**
     * Handle Underlying deleted changes.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pChange the change
     * @param pIterator the iterator
     */
    private static <T extends MetisFieldVersionedItem> void handleBaseDeletedItems(final MetisEosListVersioned<T> pList,
                                                                                   final MetisEosListChange<T> pChange,
                                                                                   final Iterator<T> pIterator) {
        /* Loop through the deleted items */
        while (pIterator.hasNext()) {
            final T myItem = pIterator.next();
            final Integer myId = myItem.getIndexedId();

            /* If the item is present in the list */
            final T myCurr = pList.getItemById(myId);
            if (myCurr != null) {
                /* Remove the item */
                pList.removeById(myId);

                /* Record deletion */
                pChange.registerDeleted(myItem);
            }
        }
    }

    /**
     * Create a New item with same values as the base.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pBase the base item
     * @return the new item
     */
    private static <T extends MetisFieldVersionedItem> T newItemFromBase(final MetisEosListVersioned<T> pList,
                                                                         final T pBase) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pBase.getIndexedId());

        /* Access the valueSet */
        final MetisFieldVersionValues mySet = myNew.getValueSet();

        /* Obtain a clone of the value set as the base value */
        final MetisFieldVersionValues myBaseSet = pBase.getValueSet();
        mySet.copyFrom(myBaseSet);
        pBase.adjustState();

        /* Return the new item */
        return myNew;
    }
}
