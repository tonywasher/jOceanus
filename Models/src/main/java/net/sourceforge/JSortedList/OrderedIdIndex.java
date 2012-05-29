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

/**
 * Index for an Ordered Id list. This provides improved lookup from object to node.
 * @author Tony Washer
 * @param <I> the date-type of the id
 * @param <T> the data-type of the list
 */
public class OrderedIdIndex<I, T extends Comparable<T> & OrderedIdItem<I>> extends OrderedIndex<T> {
    /**
     * Hash map.
     */
    private final NestedHashMap<I, OrderedNode<T>> theHashMap;

    /**
     * Constructor.
     * @param pList the list
     */
    protected OrderedIdIndex(final OrderedList<T> pList) {
        /* Call super-constructor */
        super(pList);

        /* Allocate hash map */
        theHashMap = new NestedHashMap<I, OrderedNode<T>>();
    }

    @Override
    protected OrderedNode<T> findNodeForObject(final T pItem) {
        /* Lookup the node in the map */
        OrderedNode<T> myNode = theHashMap.get(pItem.getOrderedId());

        /* Return the correct node */
        return myNode;
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
