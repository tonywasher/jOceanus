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
package net.sourceforge.joceanus.metis.lethe.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;

/**
 * List Difference Generator.
 */
public final class MetisListDiffManager {
    /**
     * Private constructor.
     */
    private MetisListDiffManager() {
    }

    /**
     * Derive a difference set between two baseListSets.
     * @param pNew the new listSet
     * @param pOld the old listSet to compare to
     * @return the difference set
     */
    public static MetisLetheListSetVersioned deriveDifferences(final MetisLetheListSetVersioned pNew,
                                                               final MetisLetheListSetVersioned pOld) {
        /* Only allowed for Base ListSets */
        if (!MetisListSetType.BASE.equals(pNew.getListSetType())
            || !MetisListSetType.BASE.equals(pOld.getListSetType())) {
            throw new IllegalArgumentException();
        }

        /* Create a new difference set */
        final MetisLetheListSetVersioned myDifferences = new MetisLetheListSetVersioned(MetisListSetType.DIFF);

        /* Determine the new Version */
        int myNewVersion = 0;

        /* Loop through the lists */
        final Iterator<MetisLetheListKey> myIterator = obtainAllItemTypes(pNew, pOld);
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* Obtain the source list */
            final MetisLetheListVersioned<MetisFieldVersionedItem> myOld = pOld.getList(myKey);
            final MetisLetheListVersioned<MetisFieldVersionedItem> myNew = pNew.getList(myKey);

            /* Create the new list */
            final MetisLetheListVersioned<MetisFieldVersionedItem> myDifference = new MetisLetheListVersioned<>(myDifferences, myKey);

            /* If we only have an old list */
            if (myNew == null) {
                generateOldDifferences(myDifference, myOld);

                /* If we only have a new list */
            } else if (myOld == null) {
                generateNewDifferences(myDifference, myNew);

                /* We must have both lists */
            } else {
                generateDifferences(myDifference, myNew, myOld);
            }

            /* If we have differences */
            if (!myDifference.isEmpty()) {
                /* Make sure that the version is correct */
                myDifference.setVersion(1);
                myNewVersion = 1;

                /* Sort the list */
                myDifference.sortList();

                /* Add the list to the listSet */
                myDifferences.declareList(myKey, myDifference);
            }
        }

        /* Return the differenceSet */
        myDifferences.setVersion(myNewVersion);
        return myDifferences;
    }

    /**
     * Obtain all itemTypes contained in the two listSets.
     * @param pNewListSet the new listSet
     * @param pOldListSet the old listSet
     * @return the iterator
     */
    private static Iterator<MetisLetheListKey> obtainAllItemTypes(final MetisLetheListSetVersioned pNewListSet,
                                                                  final MetisLetheListSetVersioned pOldListSet) {
        /* Create the new List */
        final List<MetisLetheListKey> myList = new ArrayList<>();

        /* Loop through the item types in the first set */
        Iterator<MetisLetheListKey> myIterator = pNewListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* Add to the list */
            myList.add(myKey);
        }

        /* Loop through the item types in the second set */
        myIterator = pOldListSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisLetheListKey myKey = myIterator.next();

            /* Add to the list if it does not already exist */
            if (!myList.contains(myKey)) {
                myList.add(myKey);
            }
        }

        /* Return the iterator to the list */
        return myList.iterator();
    }

    /**
     * Generate difference list between two lists.
     * @param <T> the item type
     * @param pList the list to populate
     * @param pNew the new list
     * @param pOld the old list
     */
    private static <T extends MetisFieldVersionedItem> void generateDifferences(final MetisLetheListVersioned<T> pList,
                                                                                final MetisLetheListVersioned<T> pNew,
                                                                                final MetisLetheListVersioned<T> pOld) {
        /* Access a copy of the idMap of the old list */
        final Map<Integer, T> myOld = pOld.copyIdMap();

        /* Set the sort comparator */
        pList.setComparator(pNew.getComparator());

        /* Loop through the new list */
        Iterator<T> myIterator = pNew.iterator();
        while (myIterator.hasNext()) {
            /* Locate the item in the old list */
            final T myCurr = myIterator.next();
            final Integer myId = myCurr.getIndexedId();
            T myItem = myOld.get(myId);

            /* If the item does not exist in the old list */
            if (myItem == null) {
                /* Insert a new item */
                myItem = newDiffAddedItem(pList, myCurr);
                pList.add(myItem);

                /* else the item exists in the old list */
            } else {
                /* If the item has changed */
                if (!myCurr.equals(myItem)) {
                    /* Copy the item */
                    myItem = newDiffChangedItem(pList, myCurr, myItem);
                    pList.add(myItem);
                }

                /* Remove the item from the map */
                myOld.remove(myId);
            }
        }

        /* Loop through the remaining items in the old list */
        myIterator = myOld.values().iterator();
        while (myIterator.hasNext()) {
            /* Insert a new item */
            final T myCurr = myIterator.next();
            final T myItem = newDiffDeletedItem(pList, myCurr);
            pList.add(myItem);
        }
    }

    /**
     * Generate difference list where old list does not exist.
     * @param <T> the item type
     * @param pList the list to populate
     * @param pNew the new list
     */
    private static <T extends MetisFieldVersionedItem> void generateNewDifferences(final MetisLetheListVersioned<T> pList,
                                                                                   final MetisLetheListVersioned<T> pNew) {
        /* Set the sort comparator */
        pList.setComparator(pNew.getComparator());

        /* Loop through the new list */
        final Iterator<T> myIterator = pNew.iterator();
        while (myIterator.hasNext()) {
            /* Insert a new item */
            final T myCurr = myIterator.next();
            final T myItem = newDiffAddedItem(pList, myCurr);
            pList.add(myItem);
        }
    }

    /**
     * Generate old difference list where new list does not exist.
     * @param <T> the item type
     * @param pList the list to populate
     * @param pOld the old list
     */
    private static <T extends MetisFieldVersionedItem> void generateOldDifferences(final MetisLetheListVersioned<T> pList,
                                                                                   final MetisLetheListVersioned<T> pOld) {
        /* Set the sort comparator */
        pList.setComparator(pOld.getComparator());

        /* Loop through the remaining items in the old list */
        final Iterator<T> myIterator = pOld.iterator();
        while (myIterator.hasNext()) {
            /* Insert a new item */
            final T myCurr = myIterator.next();
            final T myItem = newDiffDeletedItem(pList, myCurr);
            pList.add(myItem);
        }
    }

    /**
     * Create a New item representing a deletion.
     * @param <T> the item type
     * @param pList the target list
     * @param pBase the base item
     * @return the new item
     */
    protected static <T extends MetisFieldVersionedItem> T newDiffDeletedItem(final MetisLetheListVersioned<T> pList,
                                                                              final T pBase) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pBase.getIndexedId());

        /* Obtain a deleted values set as the current value */
        MetisFieldVersionValues myBaseSet = pBase.getValues();
        final MetisFieldVersionValues mySet = myBaseSet.cloneIt();
        mySet.setDeletion(true);

        /* Obtain an undeleted set as the base value */
        myBaseSet = mySet.cloneIt();
        myBaseSet.setDeletion(false);

        /* Record as the history of the item */
        myNew.setValues(mySet);
        myNew.setHistory(myBaseSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "changed" item for a difference list.
     * @param <T> the item type
     * @param pList the target list
     * @param pCurr the current item
     * @param pBase the base item
     * @return the new item
     */
    protected static <T extends MetisFieldVersionedItem> T newDiffChangedItem(final MetisLetheListVersioned<T> pList,
                                                                              final T pCurr,
                                                                              final T pBase) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisFieldVersionValues mySet = pCurr.getValues();
        mySet = mySet.cloneIt();

        /* Obtain a clone of the value set as the base value */
        MetisFieldVersionValues myBaseSet = pBase.getValues();
        myBaseSet = myBaseSet.cloneIt();

        /* Record as the history of the item */
        myNew.setValues(mySet);
        myNew.setHistory(myBaseSet);

        /* Return the new item */
        return myNew;
    }

    /**
     * Create a New "added" item for an update/difference list.
     * @param <T> the item type
     * @param pList the target list
     * @param pCurr the current item
     * @return the new item
     */
    protected static <T extends MetisFieldVersionedItem> T newDiffAddedItem(final MetisLetheListVersioned<T> pList,
                                                                            final T pCurr) {
        /* Obtain a new item */
        final T myNew = pList.newListItem(pCurr.getIndexedId());

        /* Obtain a clone of the value set as the current value */
        MetisFieldVersionValues mySet = pCurr.getValues();
        mySet = mySet.cloneIt();
        mySet.setVersion(1);

        /* Record as the history of the item */
        myNew.setValues(mySet);

        /* Return the new item */
        return myNew;
    }
}
