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

import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * Sorted Linked list spliterator.
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedListSpliterator<T extends Comparable<? super T>>
        implements Spliterator<T> {
    /**
     * Owning list.
     */
    private final OrderedList<T> theList;

    /**
     * Index map.
     */
    private final OrderedIndex<T> theIndexMap;

    /**
     * Last node accessed.
     */
    private OrderedNode<T> theLastNode = null;

    /**
     * The modification count.
     */
    private final int theExpectedModCount;

    /**
     * The current index.
     */
    private int theCurrIndex;

    /**
     * The number of rows.
     */
    private int theLastIndex;

    /**
     * Constructor for iterator that can show all elements.
     * @param pList the list to build the iterator on
     */
    protected OrderedListSpliterator(final OrderedList<T> pList) {
        theList = pList;
        theIndexMap = theList.getIndex();
        theExpectedModCount = theList.getModCount();
        theCurrIndex = 0;
        theLastIndex = theList.size();
    }

    /**
     * Constructor.
     * @param pBase the base spliterator.
     * @param pSplit the split point
     */
    private OrderedListSpliterator(final OrderedListSpliterator<T> pBase,
                                   final int pSplit) {
        theList = pBase.theList;
        theIndexMap = pBase.theIndexMap;
        theExpectedModCount = theList.getModCount();
        theCurrIndex = pBase.theCurrIndex + pSplit;
        theLastIndex = pBase.theLastIndex;
    }

    @Override
    public Spliterator<T> trySplit() {
        /* Check the size and don't split if too small */
        long myRemaining = estimateSize();
        int myGranularity = 1 << theIndexMap.getGranularityShift();
        if (myRemaining < myGranularity) {
            return null;
        }

        /* Determine the split point */
        int mySplit = (int) (myRemaining >> 1);

        /* Create the spliterator */
        OrderedListSpliterator<T> mySpliterator = new OrderedListSpliterator<T>(this, mySplit);

        /* Adjust self */
        theLastIndex = theCurrIndex + mySplit;

        /* Return */
        return mySpliterator;
    }

    /**
     * Is there a next row in this spliterator?
     * @return true/false
     */
    private boolean hasNext() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* Check that the row is within the view */
        return theCurrIndex < theLastIndex;
    }

    /**
     * Obtain the next element in this spliterator.
     * @return the next element
     */
    private T next() {
        /* If we are a new iterator */
        if (theLastNode == null) {
            /* Access the first element of the view */
            theLastNode = theIndexMap.getNodeAtIndex(theCurrIndex);
        } else {
            /* Return the next row */
            theLastNode = theLastNode.getNext();
        }

        /* Increment the index */
        theCurrIndex++;

        /* Return the next item */
        return theLastNode.getObject();
    }

    @Override
    public boolean tryAdvance(final Consumer<? super T> pAction) {
        if (hasNext()) {
            pAction.accept(next());
            return true;
        }
        return false;
    }

    @Override
    public void forEachRemaining(final Consumer<? super T> pAction) {
        while (hasNext()) {
            pAction.accept(next());
        }
    }

    @Override
    public long estimateSize() {
        return theLastIndex - theCurrIndex;
    }

    @Override
    public long getExactSizeIfKnown() {
        return estimateSize();
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL + Spliterator.ORDERED + Spliterator.SORTED + Spliterator.SIZED + Spliterator.SUBSIZED;
    }

    @Override
    public boolean hasCharacteristics(final int pCharacteristics) {
        return (pCharacteristics & characteristics()) == pCharacteristics;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return null;
    }
}
