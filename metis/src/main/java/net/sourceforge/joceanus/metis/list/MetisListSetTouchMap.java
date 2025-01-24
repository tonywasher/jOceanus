/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.metis.field.MetisFieldPairedItem;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.oceanus.event.OceanusEvent;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;

/**
 * TouchMap implementation.
 */
public class MetisListSetTouchMap {
    /**
     * The underlying listSet.
     */
    private final MetisListSetVersioned theListSet;

    /**
     * The map of touchMaps for this list.
     */
    private final Map<Integer, MetisListTouchMap> theListMap;

    /**
     * Constructor.
     * @param pListSet the owning listSet
     */
    public MetisListSetTouchMap(final MetisListSetVersioned pListSet) {
        /* Store parameters */
        theListSet = pListSet;
        theListMap = new HashMap<>();

        /* Attach listeners */
        final OceanusEventRegistrar<MetisListEvent> myRegistrar = theListSet.getEventRegistrar();
        myRegistrar.addEventListener(MetisListEvent.REFRESH, e -> processRefreshEvent());
        myRegistrar.addEventListener(MetisListEvent.VERSION, this::processVersionEvent);
    }

    /**
     * Is the item referenced?
     * @param pItem the item
     * @return true/false
     */
    public boolean isReferenced(final MetisFieldVersionedItem pItem) {
        /* Obtain the id for this item */
        final Integer myId = MetisListSetVersioned.buildItemId(pItem);

        /* Locate the Items TouchMap */
        final Integer myItemType = MetisListSetVersioned.getItemTypeFromId(myId);
        final Integer myIndexedId = MetisListSetVersioned.getIndexedIdFromId(myId);

        /* Pass call on to correct ListTouchMap */
        final MetisListTouchMap myListMap = theListMap.get(myItemType);
        return myListMap == null || myListMap.isReferenced(myIndexedId);
    }

    /**
     * Process a refresh event.
     */
    private void processRefreshEvent() {
        /* Reset the map */
        theListMap.clear();

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* If the list has references */
            if (myKey.hasReferences()) {
                /* Access the list */
                final MetisListVersioned<MetisFieldVersionedItem> myList = theListSet.getList(myKey);

                /* Process each item in the list */
                processNewItems(myList.iterator());
            }
        }
    }

    /**
     * Process a version event.
     * @param pEvent the event
     */
    private void processVersionEvent(final OceanusEvent<MetisListEvent> pEvent) {
        /* Access the change details */
        final MetisListSetChange myChanges = pEvent.getDetails(MetisListSetChange.class);

        /* Loop through the lists */
        final Iterator<MetisListKey> myIterator = theListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();

            /* If the list has references */
            if (myKey.hasReferences()) {
                /* Obtain the associated change */
                final MetisListChange<MetisFieldVersionedItem> myChange = myChanges.getListChange(myKey);

                /* If there are changes */
                if (myChange != null) {
                    /* handle changes in the base list */
                    processVersionChanges(myChange);
                }
            }
        }
    }

    /**
     * Process changes as a result of a version change.
     * @param pChange the change event
     */
    private void processVersionChanges(final MetisListChange<MetisFieldVersionedItem> pChange) {
        /* Process deleted items */
        processDeletedItems(pChange.hiddenIterator());
        processDeletedItems(pChange.deletedIterator());

        /* Process changed items */
        processChangedItems(pChange.changedIterator());

        /* Process new items */
        processNewItems(pChange.addedIterator());
        processNewItems(pChange.restoredIterator());
    }

    /**
     * Process a list of new items.
     * @param pIterator the iterator
     */
    private void processNewItems(final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Process each item in the list */
        while (pIterator.hasNext()) {
            final MetisFieldVersionedItem myItem = pIterator.next();
            if (!myItem.isDeleted()) {
                processNewItem(myItem);
            }
        }
    }

    /**
     * Process newItem.
     * @param pItem the item
     */
    private void processNewItem(final MetisFieldVersionedItem pItem) {
        /* Obtain the id for this item */
        final Integer myId = MetisListSetVersioned.buildItemId(pItem);

        /* Access list of touched items */
        final List<Integer> myTouches = obtainTouchedItems(pItem);

        /* Loop through the items */
        for (Integer myTouched : myTouches) {
            /* Apply the touch */
            setTouchForItem(myTouched, myId);
            setTouchedItem(myId, myTouched);
        }
    }

    /**
     * Process a list of changed items.
     * @param pIterator the iterator
     */
    private void processChangedItems(final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Process each item in the list */
        while (pIterator.hasNext()) {
            processChangedItem(pIterator.next());
        }
    }

    /**
     * Process changedItem.
     * @param pItem the item
     */
    private void processChangedItem(final MetisFieldVersionedItem pItem) {
        /* Obtain the id for this item */
        final Integer myId = MetisListSetVersioned.buildItemId(pItem);

        /* Access lists of touched items */
        final List<Integer> myCurrentTouches = obtainTouchedItems(pItem);
        final List<Integer> myPreviousTouches = getTouches(myId);

        /* Process new touches */
        final List<Integer> myNewTouches = obtainUniqueElements(myCurrentTouches, myPreviousTouches);
        for (Integer myTouched : myNewTouches) {
            /* Apply the touch */
            setTouchForItem(myTouched, myId);
            setTouchedItem(myId, myTouched);
        }

        /* Process old touches */
        final List<Integer> myOldTouches = obtainUniqueElements(myPreviousTouches, myCurrentTouches);
        for (Integer myTouched : myOldTouches) {
            /* Clear the touch */
            clearTouchForItem(myTouched, myId);
            clearTouchedItem(myId, myTouched);
        }
    }

    /**
     * Process a list of deleted items.
     * @param pIterator the iterator
     */
    private void processDeletedItems(final Iterator<MetisFieldVersionedItem> pIterator) {
        /* Process each item in the list */
        while (pIterator.hasNext()) {
            processDeletedItem(pIterator.next());
        }
    }

    /**
     * Process deletedItem.
     * @param pItem the item
     */
    private void processDeletedItem(final MetisFieldVersionedItem pItem) {
        /* Obtain the id for this item */
        final Integer myId = MetisListSetVersioned.buildItemId(pItem);

        /* Access list of touched items */
        final List<Integer> myTouches = obtainTouchedItems(pItem);

        /* Loop through the items */
        for (Integer myTouched : myTouches) {
            /* Clear the touch */
            clearTouchForItem(myTouched, myId);
            clearTouchedItem(myId, myTouched);
        }
    }

    /**
     * Set touch for item.
     * @param pItemId the itemId
     * @param pTouchId the touchId
     */
    private void setTouchForItem(final Integer pItemId,
                                 final Integer pTouchId) {
        /* Locate the Items TouchMap */
        final Integer myItemType = MetisListSetVersioned.getItemTypeFromId(pItemId);
        final Integer myIndexedId = MetisListSetVersioned.getIndexedIdFromId(pItemId);

        /* Pass call on to correct ListTouchMap */
        final MetisListTouchMap myListMap = theListMap.computeIfAbsent(myItemType, x -> new MetisListTouchMap());
        myListMap.setTouchForItem(myIndexedId, pTouchId);
    }

    /**
     * Clear touch for item.
     * @param pItemId the itemId
     * @param pTouchId the touchId
     */
    private void clearTouchForItem(final Integer pItemId,
                                   final Integer pTouchId) {
        /* Locate the Items TouchMap */
        final Integer myItemType = MetisListSetVersioned.getItemTypeFromId(pItemId);
        final Integer myIndexedId = MetisListSetVersioned.getIndexedIdFromId(pItemId);

        /* Pass call on to correct ListTouchMap */
        final MetisListTouchMap myListMap = theListMap.get(myItemType);
        if (myListMap != null) {
            myListMap.clearTouchForItem(myIndexedId, pTouchId);
        }
    }

    /**
     * Set touch for item.
     * @param pItemId the itemId
     * @param pTouchedId the touchedId
     */
    private void setTouchedItem(final Integer pItemId,
                                final Integer pTouchedId) {
        /* Locate the Items TouchMap */
        final Integer myItemType = MetisListSetVersioned.getItemTypeFromId(pItemId);
        final Integer myIndexedId = MetisListSetVersioned.getIndexedIdFromId(pItemId);

        /* Pass call on to correct ListTouchMap */
        final MetisListTouchMap myListMap = theListMap.computeIfAbsent(myItemType, x -> new MetisListTouchMap());
        myListMap.setTouchedItem(myIndexedId, pTouchedId);
    }

    /**
     * Clear touch for item.
     * @param pItemId the itemId
     * @param pTouchedId the touchedId
     */
    private void clearTouchedItem(final Integer pItemId,
                                  final Integer pTouchedId) {
        /* Locate the Items TouchMap */
        final Integer myItemType = MetisListSetVersioned.getItemTypeFromId(pItemId);
        final Integer myIndexedId = MetisListSetVersioned.getIndexedIdFromId(pItemId);

        /* Pass call on to correct ListTouchMap */
        final MetisListTouchMap myListMap = theListMap.get(myItemType);
        if (myListMap != null) {
            myListMap.clearTouchedItem(myIndexedId, pTouchedId);
        }
    }

    /**
     * Obtain the list of touches for an item.
     * @param pItemId the itemId
     * @return the list of touches
     */
    private List<Integer> getTouches(final Integer pItemId) {
        /* Locate the Items TouchMap */
        final Integer myItemType = MetisListSetVersioned.getItemTypeFromId(pItemId);
        final Integer myIndexedId = MetisListSetVersioned.getIndexedIdFromId(pItemId);

        /* Pass call on to correct ListTouchMap */
        final MetisListTouchMap myListMap = theListMap.get(myItemType);
        return myListMap != null
                ? myListMap.getTouches(myIndexedId)
                : Collections.emptyList();
    }

    /**
     * Obtain list of touched items.
     * @param pItem the item
     * @return the list of touches
     */
    private static List<Integer> obtainTouchedItems(final MetisFieldVersionedItem pItem) {
        /* Process links if they exist */
        final MetisFieldSetDef myFields = pItem.getDataFieldSet();
        return  (myFields.hasLinks() || myFields.hasPairedLinks())
                ? obtainTouchedItems(pItem, myFields)
                : Collections.emptyList();
    }

    /**
     * Obtain list of touched items.
     * @param pItem the item
     * @param pFields the field definitions
     * @return the list of touches
     */
    private static List<Integer> obtainTouchedItems(final MetisFieldVersionedItem pItem,
                                                    final MetisFieldSetDef pFields) {
        /* Create the new list */
        final List<Integer> myItems = new ArrayList<>();

        /* Loop through the fields */
        final Iterator<MetisFieldDef> myIterator = pFields.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myFieldDef = myIterator.next();

            /* Skip non-versioned Fields */
            if (!(myFieldDef instanceof MetisFieldVersionedDef)) {
                continue;
            }
            final MetisFieldVersionedDef myField = (MetisFieldVersionedDef) myFieldDef;

            /* If this is a link/linkPair field */
            if (MetisDataType.LINK.equals(myField.getDataType())
                    || MetisDataType.LINKPAIR.equals(myField.getDataType())) {
                /* Access the current link */
                final Object myLink = myField.getFieldValue(pItem);

                /* Handle singleton link */
                if (myLink instanceof MetisFieldVersionedItem) {
                    addUniqueItemToList(myItems, MetisListSetVersioned.buildItemId((MetisFieldVersionedItem) myLink));

                    /* handle Paired link */
                } else if (myLink instanceof MetisFieldPairedItem) {
                    final MetisFieldPairedItem myPairedItem = (MetisFieldPairedItem) myLink;
                    addUniqueItemToList(myItems, MetisFieldPairedItem.getParentIdFromPairedId(myPairedItem.getExternalId()));
                    addUniqueItemToList(myItems, MetisFieldPairedItem.getChildIdFromPairedId(myPairedItem.getExternalId()));
                }
            }
        }

        /* Return the list */
        return myItems;
    }

    /**
     * Add item to list if not already in list.
     * @param pList the list to add to
     * @param pItem the item to add
     */
    private static void addUniqueItemToList(final List<Integer> pList,
                                            final Integer pItem) {
        if (!pList.contains(pItem)) {
            pList.add(pItem);
        }
    }

    /**
     * Obtain list of items in first list that are not in second.
     * @param pFirst the first list
     * @param pSecond the second list
     * @return the list of unique items
     */
    private static List<Integer> obtainUniqueElements(final List<Integer> pFirst,
                                                      final List<Integer> pSecond) {
        /* Create list */
        final List<Integer> myList = new ArrayList<>();

        /* Loop through the items */
        for (Integer myTouch : pFirst) {
            if (!pSecond.contains(myTouch)) {
                myList.add(myTouch);
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * TouchMap for Item.
     */
    static class MetisListTouchMap {
        /**
         * The map of touchMaps for this list.
         */
        private final Map<Integer, MetisItemTouchMap> theItemMap;

        /**
         * Constructor.
         */
        MetisListTouchMap() {
            theItemMap = new HashMap<>();
        }

        /**
         * Is the item referenced?
         * @param pIndexedId the itemId
         * @return true/false
         */
        boolean isReferenced(final Integer pIndexedId) {
            /* Pass call on to correct ItemTouchMap */
            final MetisItemTouchMap myItemMap = theItemMap.get(pIndexedId);
            return myItemMap == null || myItemMap.isReferenced();
        }

        /**
         * Obtain the list of touches.
         * @param pIndexedId the itemId
         * @return the list of touches
         */
        List<Integer> getTouches(final Integer pIndexedId) {
            /* Pass call on to correct ItemTouchMap */
            final MetisItemTouchMap myItemMap = theItemMap.get(pIndexedId);
            return myItemMap == null
                   ? Collections.emptyList()
                   : myItemMap.getTouches();
        }

        /**
         * Set touch for item.
         * @param pIndexedId the itemId
         * @param pTouchId the touchId
         */
        void setTouchForItem(final Integer pIndexedId,
                             final Integer pTouchId) {
            /* Pass call on to correct ItemTouchMap */
            final MetisItemTouchMap myItemMap = theItemMap.computeIfAbsent(pIndexedId, x -> new MetisItemTouchMap());
            myItemMap.setTouchingItem(pTouchId);
        }

        /**
         * clear touch for item.
         * @param pIndexedId the indexedId
         * @param pTouchId the touchId
         */
        void clearTouchForItem(final Integer pIndexedId,
                               final Integer pTouchId) {
            /* Pass call on to correct ItemTouchMap */
            final MetisItemTouchMap myItemMap = theItemMap.get(pIndexedId);
            if (myItemMap != null) {
                myItemMap.clearTouchingItem(pTouchId);
            }
        }

        /**
         * Set touched item.
         * @param pIndexedId the itemId
         * @param pTouchedId the touchedId
         */
        void setTouchedItem(final Integer pIndexedId,
                            final Integer pTouchedId) {
            /* Pass call on to correct ItemTouchMap */
            final MetisItemTouchMap myItemMap = theItemMap.computeIfAbsent(pIndexedId, x -> new MetisItemTouchMap());
            myItemMap.setTouchedItem(pTouchedId);
        }

        /**
         * clear touch for item.
         * @param pIndexedId the indexedId
         * @param pTouchedId the touchedId
         */
        void clearTouchedItem(final Integer pIndexedId,
                              final Integer pTouchedId) {
            /* Pass call on to correct ItemTouchMap */
            final MetisItemTouchMap myItemMap = theItemMap.get(pIndexedId);
            if (myItemMap != null) {
                myItemMap.clearTouchedItem(pTouchedId);
            }
        }
    }

    /**
     * TouchMap for Item.
     */
    private static class MetisItemTouchMap {
        /**
         * The list of itemIds that touch this this element.
         */
        private final List<Integer> theTouching;

        /**
         * The list of itemIds that this item touches.
         */
        private final List<Integer> theTouches;

        /**
         * Constructor.
         */
        MetisItemTouchMap() {
            theTouching = new ArrayList<>();
            theTouches = new ArrayList<>();
        }

        /**
         * Obtain the list of touches.
         * @return the list of touches
         */
        List<Integer> getTouches() {
            return theTouches;
        }

        /**
         * Is the item referenced?
         * @return true/false
         */
        boolean isReferenced() {
            return theTouching.isEmpty();
        }

        /**
         * Set the touching itemId.
         * @param pId the itemId
         */
        void setTouchingItem(final Integer pId) {
            theTouching.add(pId);
        }

        /**
         * Clear the touching itemId.
         * @param pId the itemId
         */
        void clearTouchingItem(final Integer pId) {
            theTouching.remove(pId);
        }

        /**
         * Set the touched itemId.
         * @param pId the itemId
         */
        void setTouchedItem(final Integer pId) {
            theTouches.add(pId);
        }

        /**
         * Clear the touching itemId.
         * @param pId the itemId
         */
        void clearTouchedItem(final Integer pId) {
            theTouches.remove(pId);
        }
    }
}
