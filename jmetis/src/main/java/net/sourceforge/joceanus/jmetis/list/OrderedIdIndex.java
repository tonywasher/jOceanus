/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.list;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Index for an Ordered Id list. This provides improved lookup from object to node.
 * @author Tony Washer
 * @param <I> the data-type of the id
 * @param <T> the data-type of the list
 */
public class OrderedIdIndex<I, T extends Comparable<? super T> & OrderedIdItem<I>>
        extends OrderedIndex<T> {
    /**
     * Hash map.
     */
    private final Map<I, OrderedNode<T>> theHashMap;

    /**
     * Constructor.
     */
    protected OrderedIdIndex() {
        /* Use default granularity */
        this(DEFAULT_GRANULARITY_SHIFT);
    }

    /**
     * Constructor.
     * @param pIndexGranularity the granularity
     */
    protected OrderedIdIndex(final int pIndexGranularity) {
        /* Call super constructor */
        super(pIndexGranularity);

        /* Allocate hash map */
        theHashMap = new NestedHashMap<I, OrderedNode<T>>();
    }

    @Override
    protected OrderedIndex<T> newIndex(final OrderedList<T> pList) {
        OrderedIndex<T> myIndex = new OrderedIdIndex<I, T>(getGranularityShift());
        myIndex.declareList(pList);
        return myIndex;
    }

    /**
     * Obtain map of elements in the list.
     * @return the map
     */
    protected Map<I, T> getElementMap() {
        /* Create the new map */
        Map<I, T> myMap = new NestedHashMap<I, T>();

        /* Loop through the elements */
        Iterator<Entry<I, OrderedNode<T>>> myIterator = theHashMap.entrySet().iterator();
        while (myIterator.hasNext()) {
            /* Access entry and place details into map */
            Entry<I, OrderedNode<T>> myEntry = myIterator.next();
            myMap.put(myEntry.getKey(), myEntry.getValue().getObject());
        }

        /* Return the map */
        return myMap;
    }

    /**
     * Is id present in list?
     * @param pId the id to lookup
     * @return true/false
     */
    protected boolean isIdPresent(final I pId) {
        /* Lookup the node in the map */
        return findItemById(pId) != null;
    }

    /**
     * Obtain item by id?
     * @param pId the id to lookup
     * @return the item (or null if not present)
     */
    protected T findItemById(final I pId) {
        /* Look up the node */
        OrderedNode<T> myNode = theHashMap.get(pId);

        /* Return results */
        return (myNode == null)
                ? null
                : myNode.getObject();
    }

    @Override
    protected OrderedNode<T> findNodeForObject(final T pItem) {
        /* Lookup the node in the map */
        I myId = pItem.getOrderedId();
        return theHashMap.get(myId);
    }

    @Override
    protected OrderedNode<T> findUnsortedNodeForObject(final T pItem) {
        /* Same as normal */
        return findNodeForObject(pItem);
    }

    @Override
    protected void registerLink(final OrderedNode<T> pNode) {
        /* Access object */
        T myItem = pNode.getObject();

        /* Insert the link into the map */
        theHashMap.put(myItem.getOrderedId(), pNode);
    }

    @Override
    protected void deRegisterLink(final OrderedNode<T> pNode) {
        /* Access object */
        T myItem = pNode.getObject();

        /* Remove the link from the map */
        theHashMap.remove(myItem.getOrderedId());
    }

    @Override
    protected void clear() {
        /* Pass to super-class */
        super.clear();

        /* Clear the map */
        theHashMap.clear();
    }
}
