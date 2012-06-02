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

import java.util.ConcurrentModificationException;
import java.util.ListIterator;

/**
 * Sorted Linked list iterator.
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedListIterator<T extends Comparable<T>> implements ListIterator<T> {
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
     * Should we show all elements.
     */
    private boolean showAll = true;

    /**
     * The modification count.
     */
    private int theExpectedModCount = 0;

    /**
     * Constructor for iterator that can show all elements.
     * @param pList the list to build the iterator on
     * @param bShowAll show all items in the list
     */
    protected OrderedListIterator(final OrderedList<T> pList,
                                  final boolean bShowAll) {
        showAll = bShowAll;
        theList = pList;
        theExpectedModCount = theList.getModCount();
    }

    /**
     * Constructor for iterator at particular position.
     * @param pList the list to build the iterator on
     * @param pNode the node at which to start
     * @param bShowAll show all items in the list
     */
    protected OrderedListIterator(final OrderedList<T> pList,
                                  final OrderedNode<T> pNode,
                                  final boolean bShowAll) {
        /* Call standard constructor */
        this(pList, bShowAll);

        /* Record position */
        theNodeAfter = pNode;
        theNodeBefore = pNode.getPrev(!showAll);
    }

    @Override
    public boolean hasNext() {
        /* Access the next node */
        OrderedNode<T> myNext = (theNodeBefore != null)
                                                       ? theNodeBefore.getNext(!showAll)
                                                       : ((showAll) ? theList.getHead() : theList.getFirst());

        /* Return whether we have a next node */
        return (myNext != null);
    }

    @Override
    public boolean hasPrevious() {
        /* Access the next node */
        OrderedNode<T> myPrev = (theNodeAfter != null)
                                                      ? theNodeAfter.getPrev(!showAll)
                                                      : ((showAll) ? theList.getTail() : theList.getLast());

        /* Return whether we have a previous node */
        return (myPrev != null);
    }

    /**
     * Peek at the next item.
     * @return the next item or <code>null</code>
     */
    public T peekNext() {
        /* Access the next node */
        OrderedNode<T> myNext = (theNodeBefore != null)
                                                       ? theNodeBefore.getNext(!showAll)
                                                       : ((showAll) ? theList.getHead() : theList.getFirst());

        /* Return the next object */
        return (myNext == null) ? null : myNext.getObject();
    }

    /**
     * Peek at the previous item.
     * @return the previous item or <code>null</code>
     */
    public T peekPrevious() {
        /* Access the next node */
        OrderedNode<T> myPrev = (theNodeAfter != null)
                                                      ? theNodeAfter.getPrev(!showAll)
                                                      : ((showAll) ? theList.getTail() : theList.getLast());

        /* Return the previous object */
        return (myPrev == null) ? null : myPrev.getObject();
    }

    /**
     * Peek at the first item.
     * @return the first item or <code>null</code>
     */
    public T peekFirst() {
        /* Access the first node */
        OrderedNode<T> myFirst = ((showAll) ? theList.getHead() : theList.getFirst());

        /* Return the next object */
        return (myFirst == null) ? null : myFirst.getObject();
    }

    /**
     * Peek at the last item.
     * @return the last item or <code>null</code>
     */
    public T peekLast() {
        /* Access the last node */
        OrderedNode<T> myLast = ((showAll) ? theList.getTail() : theList.getLast());

        /* Return the previous object */
        return (myLast == null) ? null : myLast.getObject();
    }

    @Override
    public T next() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* Access the next node */
        OrderedNode<T> myNext = (theNodeBefore != null)
                                                       ? theNodeBefore.getNext(!showAll)
                                                       : ((showAll) ? theList.getHead() : theList.getFirst());

        /* If we have a next then move the cursor */
        if (myNext != null) {
            /* Record the cursor */
            theNodeBefore = myNext;
            theNodeAfter = myNext.getNext();
            wasForward = true;
            canRemove = true;
        }

        /* Return the next item */
        return (myNext != null) ? myNext.getObject() : null;
    }

    @Override
    public T previous() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* Access the previous node */
        OrderedNode<T> myPrev = (theNodeAfter != null)
                                                      ? theNodeAfter.getPrev(!showAll)
                                                      : ((showAll) ? theList.getTail() : theList.getLast());

        /* If we have a previous then move the cursor */
        if (myPrev != null) {
            /* Record the cursor */
            theNodeBefore = myPrev.getPrev();
            theNodeAfter = myPrev;
            wasForward = false;
            canRemove = true;
        }

        /* Return the previous item */
        return (myPrev != null) ? myPrev.getObject() : null;
    }

    @Override
    public int nextIndex() {
        /* Access the next node */
        OrderedNode<T> myNext = (theNodeBefore != null)
                                                       ? theNodeBefore.getNext(!showAll)
                                                       : ((showAll) ? theList.getHead() : theList.getFirst());

        /* If we have a next then calculate its index */
        if (myNext != null) {
            return myNext.getIndex(!showAll);
        }

        /* Return the next item */
        return -1;
    }

    @Override
    public int previousIndex() {
        /* Access the previous node */
        OrderedNode<T> myPrev = (theNodeAfter != null)
                                                      ? theNodeAfter.getPrev(!showAll)
                                                      : ((showAll) ? theList.getTail() : theList.getLast());

        /* If we have a previous then calculate its index */
        if (myPrev != null) {
            return myPrev.getIndex(!showAll);
        }

        /* Return the index */
        return -1;
    }

    /**
     * Was the last item hidden.
     * @return true/false
     */
    public boolean wasHidden() {
        /* Handle changed list */
        if (theExpectedModCount != theList.getModCount()) {
            throw new ConcurrentModificationException();
        }

        /* If we cannot remove the last item throw exception */
        if (!canRemove) {
            throw new IllegalStateException();
        }

        /* If the last operation was forward */
        return (wasForward) ? theNodeBefore.isHidden() : theNodeAfter.isHidden();
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
            theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev() : theList.getTail();

            /* else the last operation was backwards */
        } else {
            /* Remove the item */
            theList.removeNode(theNodeAfter);

            /* Record the new node after */
            theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext() : theList.getHead();
        }

        /* Record new modification count */
        theExpectedModCount = theList.getModCount();
    }

    /**
     * ReSort the last referenced item.
     */
    public void reSort() {
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
            /* Determine Node to remove */
            OrderedNode<T> myNode = theNodeBefore;

            /* Remove the item */
            theList.removeNode(myNode);

            /* Record the new node before */
            theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev() : theList.getTail();

            /* Re-insert the node */
            theList.insertNode(myNode, false);

            /* else the last operation was backwards */
        } else {
            /* Determine Node to remove */
            OrderedNode<T> myNode = theNodeAfter;

            /* Remove the item */
            theList.removeNode(myNode);

            /* Record the new node after */
            theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext() : theList.getHead();

            /* Re-insert the node */
            theList.insertNode(myNode, false);
        }

        /* Record new modification count */
        theExpectedModCount = theList.getModCount();
    }

    /**
     * Remove the last referenced item.
     * @return the item that was removed
     */
    protected T removeLastItem() {
        T myItem;

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
            myItem = theNodeBefore.getObject();

            /* Record the new node before */
            theNodeBefore = (theNodeAfter != null) ? theNodeAfter.getPrev() : theList.getTail();

            /* else the last operation was backwards */
        } else {
            /* Remove the item */
            myItem = theNodeAfter.getObject();
            theList.removeNode(theNodeAfter);

            /* Record the new node after */
            theNodeAfter = (theNodeBefore != null) ? theNodeBefore.getNext() : theList.getHead();
        }

        /* Record new modification count */
        theExpectedModCount = theList.getModCount();

        /* Return the deleted object */
        return myItem;
    }
}