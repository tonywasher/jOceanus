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
 * Ordered Id list. This provides an improved ordered list implementation for items that have a non-mutable
 * identity, and is the preferred implementation. Care should be taken to ensure that the identity is part of
 * the natural ordering to prevent duplicates.
 * <p>
 * The identity is used to directly look up the linked list node for the items via a HashMap. This provides
 * significant performance improvements over {@link OrderedList}, and also solves the problems associated with
 * <b>dirty</b> lists.
 * <ol>
 * <li>{@link #remove(Object)}, {@link #contains} and {@link #indexOf} are all accurate on a dirty list.
 * <li>{@link #add} will accurately reject duplicates in a dirty list
 * </ol>
 * <p>
 * The {@link #add} method will still add objects in a best can do fashion, and the {@link #reSort} method
 * should still be used to repair a dirty list.
 * @author Tony Washer
 * @param <I> the data-type of the id
 * @param <T> the data-type of the list
 */
public class OrderedIdList<I, T extends Comparable<? super T> & OrderedIdItem<I>> extends OrderedList<T> {
    @SuppressWarnings("unchecked")
    @Override
    protected OrderedIdIndex<I, T> getIndex() {
        return (OrderedIdIndex<I, T>) super.getIndex();
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     */
    public OrderedIdList(final Class<T> pClass) {
        super(pClass, new OrderedIdIndex<I, T>());
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     * @param pIndexGranularity the index granularity
     */
    public OrderedIdList(final Class<T> pClass,
                         final int pIndexGranularity) {
        super(pClass, new OrderedIdIndex<I, T>(pIndexGranularity));
    }

    /**
     * Construct a list.
     * @param pClass the class of the sortedItem
     * @param pIndex the index.
     */
    protected OrderedIdList(final Class<T> pClass,
                            final OrderedIdIndex<I, T> pIndex) {
        super(pClass, pIndex);
    }

    /**
     * Obtain item by id.
     * @param pId the id to lookup
     * @return the item (or null if not present)
     */
    public T findItemById(final I pId) {
        /* Return results */
        return getIndex().findItemById(pId);
    }

    /**
     * Obtain an Id Map of the list.
     * @return the Id map.
     */
    public Map<I, T> getIdMap() {
        /* Return the map */
        return getIndex().getElementMap();
    }
}
