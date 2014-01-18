/*******************************************************************************
 * jSortedList: A random access linked list implementation
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
package net.sourceforge.joceanus.jsortedlist;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Ordered Id list. This provides an improved ordered list implementation for items that have a non-mutable identity, and is the preferred implementation. Care
 * should be taken to ensure that the identity is part of the natural ordering to ensure consistency.
 * <p>
 * The identity is used to directly look up the linked list node for the item via a HashMap. This provides significant performance improvements over
 * {@link OrderedList}, and also solves the problems associated with <b>dirty</b> lists.
 * <ol>
 * <li>{@link #remove(Object)}, {@link #contains} and {@link #indexOf} are all accurate on a dirty list.
 * <li>{@link #add} will reject entries for which the identity is already in the list
 * </ol>
 * <p>
 * The {@link #add} method will still add objects in a best can do fashion, and the {@link #reSort} method should still be used to repair a dirty list.
 * @author Tony Washer
 * @param <I> the data-type of the id
 * @param <T> the data-type of the list
 */
public class OrderedIdList<I, T extends Comparable<? super T> & OrderedIdItem<I>>
        extends OrderedList<T> {
    /**
     * Get the index as an OrderedIdIndex.
     * @return the index
     */
    @SuppressWarnings("unchecked")
    private OrderedIdIndex<I, T> getTheIndex() {
        return (OrderedIdIndex<I, T>) super.getIndex();
    }

    /**
     * Ordered Index.
     */
    private final OrderedIdIndex<I, T> theIndex;

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     */
    public OrderedIdList(final Class<T> pClass) {
        super(pClass, new OrderedIdIndex<I, T>());
        theIndex = getTheIndex();
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     * @param pIndexGranularity the index granularity
     */
    public OrderedIdList(final Class<T> pClass,
                         final int pIndexGranularity) {
        super(pClass, new OrderedIdIndex<I, T>(pIndexGranularity));
        theIndex = getTheIndex();
    }

    /**
     * Construct a list containing the elements of the passed list.
     * @param pSource the source ordered list
     */
    public OrderedIdList(final OrderedIdList<I, T> pSource) {
        /* Initialise for the correct class */
        this(pSource.getBaseClass(), pSource);
    }

    /**
     * Construct a list containing the elements of the passed list.
     * @param pClass the class of the sortedItem
     * @param pSource the source ordered list
     */
    public OrderedIdList(final Class<T> pClass,
                         final List<T> pSource) {
        /* Initialise for the correct class */
        this(pClass);

        /* Loop through the source members */
        Iterator<T> myIterator = pSource.iterator();
        while (myIterator.hasNext()) {
            T myItem = myIterator.next();

            /* Add the item */
            addTheItem(myItem);
        }
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     * @param pIndex the index.
     */
    protected OrderedIdList(final Class<T> pClass,
                            final OrderedIdIndex<I, T> pIndex) {
        super(pClass, pIndex);
        theIndex = pIndex;
    }

    /**
     * Obtain item by id.
     * @param pId the id to lookup
     * @return the item (or null if not present)
     */
    public T findItemById(final I pId) {
        /* Return results */
        return theIndex.findItemById(pId);
    }

    /**
     * Obtain an Id Map of the list.
     * @return the Id map.
     */
    public Map<I, T> getIdMap() {
        /* Return the map */
        return theIndex.getElementMap();
    }

    /**
     * Peek at the next item.
     * @param pItem the item from which to find the next item
     * @return the next item or <code>null</code>
     */
    public T peekNext(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndex.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return null;
        }

        /* Access the next node */
        myNode = myNode.getNext();

        /* Return the next object */
        return (myNode == null)
                ? null
                : myNode.getObject();
    }

    /**
     * Peek at the previous item.
     * @param pItem the item from which to find the previous item
     * @return the previous item or <code>null</code>
     */
    public T peekPrevious(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Access the node of the item */
        OrderedNode<T> myNode = theIndex.findNodeForObject(pItem);

        /* If the node does not belong to the list then ignore */
        if (myNode == null) {
            return null;
        }

        /* Access the previous node */
        myNode = myNode.getPrev();

        /* Return the previous object */
        return (myNode == null)
                ? null
                : myNode.getObject();
    }

    /**
     * Add item into correct sort order in the list. If the list is not currently sorted, then the item will not be correctly placed into the list. If the item
     * has the same Id as an existing item in the list it will not be added.
     * @param pItem the item to add
     * @return true if the item was added to the list.
     */
    @Override
    public boolean add(final T pItem) {
        /* Call standard method */
        return addTheItem(pItem);
    }

    /**
     * Add item to list.
     * @param pItem the item to add
     * @return was the item added? true/false
     */
    protected final boolean addTheItem(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Reject if the object is already a link member of this list */
        if (theIndex.findNodeForObject(pItem) != null) {
            return false;
        }

        /* Pass call down */
        return super.addItem(pItem);
    }

    /**
     * Append item directly to the end of the list. The list needs to be sorted after this operation. If the item has the same Id as an existing item in the
     * list it will not be added.
     * @param pItem the item to add
     * @return true if the item was added to the list.
     */
    @Override
    public boolean append(final T pItem) {
        /* Reject if the object is null */
        if (pItem == null) {
            throw new IllegalArgumentException(NULL_DISALLOWED);
        }

        /* Reject if the object is already a link member of this list */
        if (theIndex.findNodeForObject(pItem) != null) {
            return false;
        }

        /* Pass call down */
        return super.append(pItem);
    }
}
