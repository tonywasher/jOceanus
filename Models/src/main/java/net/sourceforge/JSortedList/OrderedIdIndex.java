/*******************************************************************************
 * JSortedList: A random access linked list implementation
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JSortedList;

import java.util.Map;

/**
 * Index for an Ordered Id list. This provides improved lookup from object to node.
 * @author Tony Washer
 * @param <I> the data-type of the id
 * @param <T> the data-type of the list
 */
public class OrderedIdIndex<I, T extends Comparable<T> & OrderedIdItem<I>> extends OrderedIndex<T> {
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

    @Override
    protected OrderedNode<T> findNodeForObject(final T pItem) {
        /* Lookup the node in the map */
        return theHashMap.get(pItem.getOrderedId());
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
