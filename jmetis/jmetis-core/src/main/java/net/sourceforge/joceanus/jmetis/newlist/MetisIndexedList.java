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
package net.sourceforge.joceanus.jmetis.newlist;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataList;

/**
 * Java Indexed List.
 * @param <T> the item type
 */
public class MetisIndexedList<T extends MetisVersionedItem>
        implements MetisDataList<T> {
    /**
     * The underlying list.
     */
    private final List<T> theList;

    /**
     * The id Map.
     */
    private final Map<Integer, T> theIdMap;

    /**
     * Constructor.
     * @param pList the list
     */
    protected MetisIndexedList(final List<T> pList) {
        /* Store parameters */
        theList = pList;

        /* Create the map */
        theIdMap = new HashMap<>();
    }

    /**
     * Obtain the idMap.
     * @return the map
     */
    protected Map<Integer, T> getIdMap() {
        return theIdMap;
    }

    @Override
    public List<T> getUnderlyingList() {
        return theList;
    }

    /**
     * Add item to the list.
     * @param pItem the item to add
     */
    public void addToList(final T pItem) {
        /* Access the id */
        Integer myId = pItem.getIndexedId();

        /* Check that the id is not currently present */
        if (theIdMap.get(myId) != null) {
            throw new IllegalArgumentException("Already present");
        }

        /* Add to the list */
        theIdMap.put(myId, pItem);
        theList.add(pItem);
    }

    /**
     * Remove item from the list.
     * @param pItem the item to remove
     */
    public void removeFromList(final T pItem) {
        /* Access the id */
        Integer myId = pItem.getIndexedId();

        /* Check that the id is present */
        if (theIdMap.get(myId) != null) {
            /* Add to the list */
            theIdMap.remove(myId);
            theList.remove(pItem);
        }
    }

    /**
     * Obtain item by Id.
     * @param pId the id
     * @return the item or null
     */
    public T getItemById(final Integer pId) {
        return theIdMap.get(pId);
    }

    /**
     * Determine whether an item is in the list.
     * @param pItem the item
     * @return true/false
     */
    public boolean containsItem(final T pItem) {
        return getItemById(pItem.getIndexedId()) != null;
    }

    /**
     * Obtain the size of the list.
     * @return the size of the list
     */
    public int getSize() {
        return theList.size();
    }

    /**
     * Is the list empty?
     * @return true/false
     */
    public boolean isEmpty() {
        return theList.isEmpty();
    }

    /**
     * Clear the list.
     */
    public void clear() {
        theList.clear();
        theIdMap.clear();
    }

    /**
     * Obtain the index of the item.
     * @param pItem the item
     * @return the index or -1 if not present
     */
    public int indexOfItem(final T pItem) {
        return theList.indexOf(pItem);
    }

    /**
     * Obtain the item at the index.
     * @param pIndex the index of the item
     * @return the item
     */
    public T getAtIndex(final int pIndex) {
        return theList.get(pIndex);
    }

    /**
     * Remove the item at the index.
     * @param pIndex the index of the item
     * @return the item that was removed
     */
    public T removeAtIndex(final int pIndex) {
        T myItem = theList.remove(pIndex);
        theIdMap.remove(myItem.getIndexedId());
        return myItem;
    }

    /**
     * Insert the item at the index.
     * @param pItem the item to insert
     * @param pIndex the index to insert at
     */
    public void insertAtIndex(final T pItem,
                              final int pIndex) {
        /* Access the id */
        Integer myId = pItem.getIndexedId();

        /* Check that the id is not currently present */
        if (theIdMap.get(myId) != null) {
            throw new IllegalArgumentException("Already present");
        }

        /* Add to the list */
        theList.add(pIndex, pItem);
        theIdMap.put(myId, pItem);
    }

    /**
     * Obtain an iterator.
     * @return the iterator
     */
    public Iterator<T> iterator() {
        return new MetisIndexedListIterator<>(this);
    }

    /**
     * Obtain a list iterator.
     * @return the iterator
     */
    public ListIterator<T> listIterator() {
        return new MetisIndexedListIterator<>(this);
    }

    /**
     * Obtain a list iterator.
     * @param pIndex the index at which to start the iterator
     * @return the iterator
     */
    public ListIterator<T> listIterator(final int pIndex) {
        return new MetisIndexedListIterator<>(this, pIndex);
    }

    /**
     * Sort the list.
     * @param pComparator the comparator
     */
    public void sortList(final Comparator<T> pComparator) {
        theList.sort(pComparator);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (!(pThat instanceof MetisIndexedList)) {
            return false;
        }

        /* Cast as list */
        MetisIndexedList<?> myThat = (MetisIndexedList<?>) pThat;

        /* Check list */
        return theList.equals(myThat.theList);
    }

    @Override
    public int hashCode() {
        return theList.hashCode();
    }

    @Override
    public String toString() {
        return theList.toString();
    }
}
