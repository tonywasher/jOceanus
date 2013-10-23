/*******************************************************************************
 * jSortedList: A random access linked list implementation
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jSortedList;

import java.util.ConcurrentModificationException;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Sorted Linked list iterator.
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedListIterator<T extends Comparable<? super T>> implements ListIterator<T> {
    /**
     * Owning list.
     */
    private final OrderedList<T> theList;

    /**
     * Last node accessed.
     */
    private OrderedNode<T> theNodeBefore = null;

    /**
     * Last node accessed.
     */
    private OrderedNode<T> theNodeAfter = null;

    /**
     * Can we remove the last item.
     */
    private boolean canRemove = false;

    /**
     * Which direction was the last scan.
     */
    private boolean wasForward = true;

    /**
     * The modification count.
     */
    private int theExpectedModCount = 0;

    /**
     * Constructor for iterator that can show all elements.
     * @param pList the list to build the iterator on
     */
    protected OrderedListIterator(final OrderedList<T> pList) {
        theList = pList;
        theExpectedModCount = theList.getModCount();
    }

    /**
     * Constructor for iterator at particular position.
     * @param pList the list to build the iterator on
     * @param pNode the node at which to start
     */
    protected OrderedListIterator(final OrderedList<T> pList,
                                  final OrderedNode<T> pNode) {
        /* Call standard constructor */
        this(pList);

        /* Record position */
        theNodeAfter = pNode;
        theNodeBefore = pNode.getPrev();
    }

    /**
     * Obtain the next node.
     * @return the next node (or null)
     */
    private OrderedNode<T> nextNode() {
        /* Return the next node */
        return (theNodeBefore != null) ? theNodeBefore.getNext() : theList.getFirst();
    }

    /**
     * Obtain the previous node.
     * @return the previous node (or null)
     */
    private OrderedNode<T> previousNode() {
        /* Return the previous node */
        return (theNodeAfter != null) ? theNodeAfter.getPrev() : theList.getLast();
    }

    @Override
    public boolean hasNext() {
        /* Access the next node */
        OrderedNode<T> myNext = nextNode();

        /* Return whether we have a next node */
        return (myNext != null);
    }

    @Override
    public boolean hasPrevious() {
        /* Access the previous node */
        OrderedNode<T> myPrev = previousNode();

        /* Return whether we have a previous node */
        return (myPrev != null);
    }

    /**
     * Peek at the next item.
     * @return the next item or <code>null</code>
     */
    public T peekNext() {
        /* Access the next node */
        OrderedNode<T> myNext = nextNode();

        /* Return the next object */
        return (myNext != null) ? myNext.getObject() : null;
    }

    /**
     * Peek at the previous item.
     * @return the previous item or <code>null</code>
     */
    public T peekPrevious() {
        /* Access the next node */
        OrderedNode<T> myPrev = previousNode();

        /* Return the previous object */
        return (myPrev != null) ? myPrev.getObject() : null;
    }

    /**
     * Peek at the first item.
     * @return the first item or <code>null</code>
     */
    public T peekFirst() {
        /* Access the first node */
        OrderedNode<T> myFirst = theList.getFirst();

        /* Return the next object */
        return (myFirst != null) ? myFirst.getObject() : null;
    }

    /**
     * Peek at the last item.
     * @return the last item or <code>null</code>
     */
    public T peekLast() {
        /* Access the last node */
        OrderedNode<T> myLast = theList.getLast();

        /* Return the previous object */
        return (myLast != null) ? myLast.getObject() : null;
    }

    @Override
    public T next() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* Access the next node */
        OrderedNode<T> myNext = nextNode();
        if (myNext == null) {
            throw new NoSuchElementException();
        }

        /* Record the cursor */
        theNodeBefore = myNext;
        theNodeAfter = myNext.getNext();
        wasForward = true;
        canRemove = true;

        /* Return the next item */
        return myNext.getObject();
    }

    @Override
    public T previous() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* Access the previous node */
        OrderedNode<T> myPrev = previousNode();
        if (myPrev == null) {
            throw new NoSuchElementException();
        }

        /* Record the cursor */
        theNodeBefore = myPrev.getPrev();
        theNodeAfter = myPrev;
        wasForward = false;
        canRemove = true;

        /* Return the previous item */
        return myPrev.getObject();
    }

    @Override
    public int nextIndex() {
        /* Access the next node */
        OrderedNode<T> myNext = nextNode();

        /* Return the index */
        return (myNext != null) ? myNext.getIndex() : theList.size();
    }

    @Override
    public int previousIndex() {
        /* Access the previous node */
        OrderedNode<T> myPrev = previousNode();

        /* Return the index */
        return (myPrev != null) ? myPrev.getIndex() : -1;
    }

    /**
     * Set the contents of the item. Disallowed.
     * @param o object to set
     */
    @Override
    public void set(final T o) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    /**
     * Add the item at this position. Disallowed.
     * @param o object to add
     */
    @Override
    public void add(final T o) {
        /* Throw exception */
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* If we cannot remove the last item throw exception */
        if (!canRemove) {
            throw new IllegalStateException();
        }

        /* Note that we can no longer remove the item */
        canRemove = false;

        /* If the last operation was forward */
        if (wasForward) {
            /* Remove the item */
            theList.removeNode(theNodeBefore);

            /* Record the new node before */
            theNodeBefore = previousNode();

            /* else the last operation was backwards */
        } else {
            /* Remove the item */
            theList.removeNode(theNodeAfter);

            /* Record the new node after */
            theNodeAfter = nextNode();
        }

        /* Record new modification count */
        theExpectedModCount = theList.getModCount();
    }
}
