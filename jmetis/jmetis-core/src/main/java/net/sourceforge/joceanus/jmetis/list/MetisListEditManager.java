/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.list;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldPairedItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.jmetis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;

/**
 * List Edit Methods.
 */
public final class MetisListEditManager {
    /**
     * Private constructor.
     */
    private MetisListEditManager() {
    }

    /**
     * Derive an edit listSet.
     * @param pBaseSet the base listSet
     * @return the updateSet
     */
    public static MetisListSetVersioned deriveEditListSet(final MetisListSetVersioned pBaseSet) {
        /* Only allowed for Base ListSets */
        if (!MetisListSetType.BASE.equals(pBaseSet.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* Create a new editSet */
        final MetisListSetVersioned myEditSet = new MetisListSetVersioned(MetisListSetType.EDIT, pBaseSet);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = pBaseSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisListVersioned<MetisFieldVersionedItem> myBase = pBaseSet.getList(myKey);

            /* Create the new list */
            final MetisListVersioned<MetisFieldVersionedItem> myList = new MetisListVersioned<>(myEditSet, myBase);

            /* Add the list to the listSet */
            myEditSet.declareList(myKey, myList);

            /* configure the edit list */
            deriveEdits(myList, myEditSet);
        }

        /* Register event handlers */
        final TethysEventRegistrar<MetisListEvent> myRegistrar = pBaseSet.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> deriveEdits(myEditSet));
        myRegistrar.addEventListener(MetisListEvent.VERSION, e -> handleReWindOfBase(myEditSet, e));

        /* Return the editSet */
        return myEditSet;
    }

    /**
     * Derive edit items as a result of refresh in the base listSet.
     * @param pEditSet the edit listSet
     */
    private static void deriveEdits(final MetisListSetVersioned pEditSet) {
        /* Reset the Paired Items */
        pEditSet.resetPairedItems();

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = pEditSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the edit list */
            final MetisListVersioned<MetisFieldVersionedItem> myEdit = pEditSet.getList(myKey);

            /* Derive items from the baseList */
            deriveEdits(myEdit, pEditSet);
        }

        /* Update the listSet version */
        pEditSet.setVersion(0);

        /* fire event */
        final MetisListSetChange myChanges = new MetisListSetChange(MetisListEvent.REFRESH);
        pEditSet.fireEvent(myChanges);
    }

    /**
     * Derive the edit list.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pEditSet the edit listSet
     */
    private static <T extends MetisFieldVersionedItem> void deriveEdits(final MetisListVersioned<T> pList,
                                                                        final MetisListSetVersioned pEditSet) {
        /* Clear the list */
        pList.clear();

        /* Access the base list */
        final MetisListVersioned<T> myBaseList = pList.getBaseList();

        /* Loop through the base list */
        final Iterator<T> myIterator = myBaseList.iterator();
        while (myIterator.hasNext()) {
            final T myCurr = myIterator.next();

            /* No need to add the item if it is deleted */
            if (!myCurr.isDeleted()) {
                /* Create the new item */
                final T myItem = newItemFromBase(pList, myCurr, pEditSet);
                pList.add(myItem);
            }
        }

        /* Reset the version */
        pList.setVersion(0);
    }

    /**
     * Amend edit items as a result of reWind in the base listSet.
     * @param pEditSet the edit listSet
     * @param pEvent the event
     */
    private static void handleReWindOfBase(final MetisListSetVersioned pEditSet,
                                           final TethysEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        final MetisListSetChange myChanges = pEvent.getDetails(MetisListSetChange.class);

        /* Create a new ListSet event */
        final MetisListSetChange myNewChanges = new MetisListSetChange(0);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = pEditSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* Obtain the update list and associated change */
            final MetisListVersioned<MetisFieldVersionedItem> myList = pEditSet.getList(myKey);
            MetisListChange<MetisFieldVersionedItem> myChange = myChanges.getListChange(myKey);

            /* If there are changes */
            if (myChange != null) {
                /* handle changes in the base list */
                myChange = doHandleReWindOfBase(myList, pEditSet, myChange);
                if (!myChange.isEmpty()) {
                    myNewChanges.registerChangedList(myChange);
                }
            }
        }

        /* Fire the event */
        if (!myNewChanges.isEmpty()) {
            pEditSet.fireEvent(myNewChanges);
        }
    }

    /**
     * Handle reWind of Base.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pEditSet the edit listSet
     * @param pChange the change
     * @return the editList change
     */
    private static <T extends MetisFieldVersionedItem> MetisListChange<T> doHandleReWindOfBase(final MetisListVersioned<T> pList,
                                                                                               final MetisListSetVersioned pEditSet,
                                                                                               final MetisListChange<T> pChange) {
        /* Create a new change */
        final MetisListChange<T> myChange = new MetisListChange<>(pList.getItemType(), MetisListEvent.VERSION);

        /* Handle underlying deleted items */
        handleBaseDeletedItems(pList, pEditSet, myChange, pChange.deletedIterator());
        handleBaseDeletedItems(pList, pEditSet, myChange, pChange.hiddenIterator());

        /* Handle base changed items */
        handleBaseChangedItems(pList, pEditSet, myChange, pChange.changedIterator());
        handleBaseChangedItems(pList, pEditSet, myChange, pChange.restoredIterator());

        /* Items are never added by a reWind */

        /* return the change */
        return myChange;
    }

    /**
     * Handle changed items in the base.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pEditSet the edit listSet
     * @param pChange the change
     * @param pIterator the iterator
     */
    private static <T extends MetisFieldVersionedItem> void handleBaseChangedItems(final MetisListVersioned<T> pList,
                                                                                   final MetisListSetVersioned pEditSet,
                                                                                   final MetisListChange<T> pChange,
                                                                                   final Iterator<T> pIterator) {
        /* Loop through the changed items */
        while (pIterator.hasNext()) {
            final T myCurr = pIterator.next();
            final Integer myId = myCurr.getIndexedId();

            /* Obtain the item to be changed */
            T myItem = pList.getItemById(myId);

            /* if we do not currently have the item in the list */
            if (myItem == null) {
                /* Create the new item */
                myItem = newItemFromBase(pList, myCurr, pEditSet);
                pList.add(myItem);

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

                /* ensure links */
                ensureLinks(myItem, pEditSet);

                /* Record change */
                pChange.registerChanged(myItem);
            }
        }
    }

    /**
     * Handle Underlying deleted changes.
     * @param <T> the itemType for the list
     * @param pList the edit list
     * @param pEditSet the edit listSet
     * @param pChange the change
     * @param pIterator the iterator
     */
    private static <T extends MetisFieldVersionedItem> void handleBaseDeletedItems(final MetisListVersioned<T> pList,
                                                                                   final MetisListSetVersioned pEditSet,
                                                                                   final MetisListChange<T> pChange,
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

                /* Cleanup lists */
                pEditSet.cleanupDeletedItem(myCurr);

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
     * @param pEditSet the edit listSet
     * @return the new item
     */
    private static <T extends MetisFieldVersionedItem> T newItemFromBase(final MetisListVersioned<T> pList,
                                                                         final T pBase,
                                                                         final MetisListSetVersioned pEditSet) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pBase.getIndexedId());

        /* Access the valueSet */
        final MetisFieldVersionValues mySet = myNew.getValueSet();

        /* Obtain a clone of the value set as the base value */
        final MetisFieldVersionValues myBaseSet = pBase.getValueSet();
        mySet.copyFrom(myBaseSet);
        myNew.adjustState();

        /* ensure links */
        ensureLinks(myNew, pEditSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Ensure links are within the listSet.
     * @param <T> the itemType for the list
     * @param pItem the item
     * @param pEditSet the edit listSet
     */
    protected static <T extends MetisFieldVersionedItem> void ensureLinks(final T pItem,
                                                                          final MetisListSetVersioned pEditSet) {
        /* If the item has links */
        final MetisFieldSetDef myFields = pItem.getDataFieldSet();
        if (myFields.hasLinks() || myFields.hasPairedLinks()) {
            /* Loop through the fields */
            final Iterator<MetisFieldDef> myIterator = myFields.fieldIterator();
            while (myIterator.hasNext()) {
                final MetisFieldDef myFieldDef = myIterator.next();

                /* Skip non-versioned Fields */
                if (!(myFieldDef instanceof MetisFieldVersionedDef)) {
                    continue;
                }
                final MetisFieldVersionedDef myField = (MetisFieldVersionedDef) myFieldDef;

                /* If this is a link field */
                if (MetisDataType.LINK.equals(myField.getDataType())) {
                    /* Correct the link */
                    ensureLink(pItem, myField, pEditSet);
                }

                /* If this is a linkPair field */
                if (MetisDataType.LINKPAIR.equals(myField.getDataType())) {
                    /* Correct the link */
                    ensurePairedLink(pItem, myField, pEditSet);
                }
            }
        }
    }

    /**
     * Ensure link value is within the listSet.
     * @param <T> the itemType for the list
     * @param pItem the item
     * @param pField the field
     * @param pEditSet the edit listSet
     */
    private static <T extends MetisFieldVersionedItem> void ensureLink(final T pItem,
                                                                       final MetisFieldVersionedDef pField,
                                                                       final MetisListSetVersioned pEditSet) {
        /* Access the current link */
        MetisFieldVersionedItem myLink = pField.getFieldValue(pItem, MetisFieldVersionedItem.class);
        if (myLink != null) {
            final Integer myId = pEditSet.getIdForItem(myLink);
            myLink = pEditSet.getItemForId(myId);
            pField.setFieldUncheckedValue(pItem, myLink);
        }
    }

    /**
     * Ensure pairedlink value is within the listSet.
     * @param <T> the itemType for the list
     * @param pItem the item
     * @param pField the field
     * @param pEditSet the edit listSet
     */
    private static <T extends MetisFieldVersionedItem> void ensurePairedLink(final T pItem,
                                                                             final MetisFieldVersionedDef pField,
                                                                             final MetisListSetVersioned pEditSet) {
        /* Access the current link */
        Object myLink = pField.getFieldValue(pItem);

        /* Handle singleton link */
        if (myLink instanceof MetisFieldVersionedItem) {
            ensureLink(pItem, pField, pEditSet);

            /* handle Paired link */
        } else if (myLink instanceof MetisFieldPairedItem) {
            final Long myId = pEditSet.getIdForPairedItem(myLink);
            myLink = pEditSet.getPairedItemForId(myId);
            pField.setFieldUncheckedValue(pItem, myLink);

            /* Reject unknown object */
        } else if (myLink != null) {
            throw new IllegalArgumentException("Invalid PairedLink");
        }
    }
}
