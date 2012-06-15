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
 * Ordered Id list. This provides improved lookup from object to node.
 * @author Tony Washer
 * @param <I> the date-type of the id
 * @param <T> the data-type of the list
 */
public class OrderedIdList<I, T extends Comparable<T> & OrderedIdItem<I>> extends OrderedList<T> {
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
}
