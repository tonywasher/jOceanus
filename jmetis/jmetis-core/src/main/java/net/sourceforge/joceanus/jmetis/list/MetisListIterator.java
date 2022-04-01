/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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

import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataIndexedItem;

/**
 * Indexed List Iterator.
 * @param <T> the item type
 */
public class MetisListIterator<T extends MetisDataIndexedItem>
        implements ListIterator<T> {
    /**
     * The underlying list.
     */
    private final MetisListIndexed<T> theList;

    /**
     * The underlying iterator.
     */
    private final ListIterator<T> theIterator;

    /**
     * The last item referenced.
     */
    private T theLastItem;

    /**
     * Constructor.
     * @param pList the list
     */
    MetisListIterator(final MetisListIndexed<T> pList) {
        /* Store parameters */
        theList = pList;

        /* Create the iterator */
        theIterator = theList.getUnderlyingList().listIterator();
    }

    /**
     * Constructor.
     * @param pList the list
     * @param pIndex the starting index
     */
    protected MetisListIterator(final MetisListIndexed<T> pList,
                                final int pIndex) {
        /* Store parameters */
        theList = pList;

        /* Create the iterator */
        theIterator = theList.getUnderlyingList().listIterator(pIndex);
    }

    @Override
    public boolean hasNext() {
        return theIterator.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return theIterator.hasPrevious();
    }

    @Override
    public T next() {
        theLastItem = theIterator.next();
        return theLastItem;
    }

    @Override
    public int nextIndex() {
        return theIterator.nextIndex();
    }

    @Override
    public T previous() {
        theLastItem = theIterator.previous();
        return theLastItem;
    }

    @Override
    public int previousIndex() {
        return theIterator.previousIndex();
    }

    @Override
    public void remove() {
        theIterator.remove();
        theList.getIdMap().remove(theLastItem.getIndexedId());
    }

    @Override
    public void add(final T pItem) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void set(final T pItem) {
        throw new UnsupportedOperationException();
    }
}
