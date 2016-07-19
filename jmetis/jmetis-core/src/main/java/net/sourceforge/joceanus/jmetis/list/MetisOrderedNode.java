/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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

/**
 * Linked list element.
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class MetisOrderedNode<T extends Comparable<? super T>> {
    /**
     * The object that this node refers to.
     */
    private final T theObject;

    /**
     * The list that this node belongs to.
     */
    private final MetisOrderedList<T> theList;

    /**
     * The standard index of this item.
     */
    private int theIndex = -1;

    /**
     * The next node in the sequence.
     */
    private MetisOrderedNode<T> theNext = null;

    /**
     * The previous node in the sequence.
     */
    private MetisOrderedNode<T> thePrev = null;

    /**
     * Initialiser.
     * @param pList the list that this node belongs to
     * @param pObject the object that this node represents
     */
    protected MetisOrderedNode(final MetisOrderedList<T> pList,
                               final T pObject) {
        /* Store details */
        theList = pList;
        theObject = pObject;
    }

    /**
     * Get object.
     * @return <code>true/false</code>
     */
    protected T getObject() {
        return theObject;
    }

    /**
     * add Node to the list immediately before the passed node.
     * @param pPoint the node before which we have to insert
     */
    protected void addBeforeNode(final MetisOrderedNode<T> pPoint) {
        /* Set values for the new node */
        thePrev = pPoint.thePrev;
        theNext = pPoint;

        /* Copy Index from insert point */
        theIndex = pPoint.theIndex;

        /* Add to the list */
        pPoint.thePrev = this;

        /* If we have a preceding node */
        if (thePrev != null) {
            /* Adjust preceding node to point to this */
            thePrev.theNext = this;

            /* else set this node as first in list */
        } else {
            theList.setFirst(this);
        }

        /* Loop through subsequent nodes increasing the indices */
        adjustIndicesAfterInsert(theNext);
    }

    /**
     * add Node to the tail of the list.
     */
    protected void addToTail() {
        /* Set values for the new node */
        thePrev = theList.getLast();
        theNext = null;

        /* If the list is empty */
        if (thePrev == null) {
            /* Set new index */
            theIndex = 0;

            /* Record this node as first in the list */
            theList.setFirst(this);

            /* else we have a preceding node */
        } else {
            /* Set new index */
            theIndex = thePrev.theIndex + 1;

            /* Add to the list */
            thePrev.theNext = this;
        }

        /* Set node as last in list */
        theList.setLast(this);
    }

    /**
     * add Node to the list immediately after the passed node.
     * @param pPoint the node after which we have to insert
     */
    protected void addAfterNode(final MetisOrderedNode<T> pPoint) {
        /* Set values for the new node */
        theNext = pPoint.theNext;
        thePrev = pPoint;

        /* Set new index from insert point */
        theIndex = pPoint.theIndex + 1;

        /* Add to the list */
        pPoint.theNext = this;

        /* If we have a following node */
        if (theNext != null) {
            /* Link the next node back to us */
            theNext.thePrev = this;

            /* Adjust following indices */
            adjustIndicesAfterInsert(theNext);

            /* else set this node as last in list */
        } else {
            theList.setLast(this);
        }
    }

    /**
     * add Node to the head of the list.
     */
    protected void addToHead() {
        /* Set values for the new item */
        theNext = theList.getFirst();
        thePrev = null;

        /* Set new index */
        theIndex = 0;

        /* Record this node as first in the list */
        theList.setFirst(this);

        /* If we have a following node */
        if (theNext != null) {
            /* Link the next node back to us */
            theNext.thePrev = this;

            /* Adjust following indices */
            adjustIndicesAfterInsert(theNext);

            /* else set this node as last in list */
        } else {
            theList.setLast(this);
        }
    }

    /**
     * Adjust indexes after insert.
     * @param pNode the first node to adjust
     */
    private static void adjustIndicesAfterInsert(final MetisOrderedNode<?> pNode) {
        MetisOrderedNode<?> myCurr = pNode;
        while (myCurr != null) {
            /* Increment index */
            myCurr.theIndex++;
            myCurr = myCurr.theNext;
        }
    }

    /**
     * Adjust indexes after remove.
     * @param pNode the first node to adjust
     */
    private static void adjustIndicesAfterRemove(final MetisOrderedNode<?> pNode) {
        MetisOrderedNode<?> myCurr = pNode;
        while (myCurr != null) {
            /* Decrement index */
            myCurr.theIndex--;
            myCurr = myCurr.theNext;
        }
    }

    /**
     * Remove node from list.
     */
    protected void remove() {
        /* If we have a preceding node */
        if (thePrev != null) {
            /* Link previous node past us */
            thePrev.theNext = theNext;

            /* else set next as first list */
        } else {
            theList.setFirst(theNext);
        }

        /* If we have a following node */
        if (theNext != null) {
            /* Link following node back to previous */
            theNext.thePrev = thePrev;

            /* Adjust following indices */
            adjustIndicesAfterRemove(theNext);

            /* else set previous as last in list */
        } else {
            theList.setLast(thePrev);
        }

        /* clean our links */
        theNext = null;
        thePrev = null;
    }

    /**
     * Get the next node in the sequence.
     * @return the Next node
     */
    protected MetisOrderedNode<T> getNext() {
        return theNext;
    }

    /**
     * Get the previous node in the sequence.
     * @return the Previous node
     */
    protected MetisOrderedNode<T> getPrev() {
        return thePrev;
    }

    /**
     * Get the index of the item.
     * @return the relevant index of the item
     */
    public int getIndex() {
        return theIndex;
    }

    /**
     * Get the list to which this node belongs.
     * @return the holding list
     */
    protected MetisOrderedList<T> getList() {
        /* Return the list */
        return theList;
    }

    /**
     * Compare this node to another.
     * @param pThat the node to compare to
     * @return (-1,0,1) depending on order
     */
    protected int compareTo(final MetisOrderedNode<T> pThat) {
        return theObject.compareTo(pThat.theObject);
    }

    /**
     * Compare this node to another.
     * @param pThat the object to compare to
     * @return (-1,0,1) depending on order
     */
    protected int compareTo(final T pThat) {
        return theObject.compareTo(pThat);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is the same class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Access as ordered node */
        MetisOrderedNode<?> myThat = (MetisOrderedNode<?>) pThat;

        /* Compare the objects */
        return theObject.equals(myThat.theObject);
    }

    @Override
    public int hashCode() {
        return theObject.hashCode();
    }

    /**
     * Obtain first item in sequence of similar items.
     * @param pNode the node to check
     * @param <X> the data type
     * @return the first item
     */
    protected static <X extends Comparable<? super X>> MetisOrderedNode<X> getFirstNodeInSequence(final MetisOrderedNode<X> pNode) {
        /* While we have a preceding item that is equal */
        MetisOrderedNode<X> myNode = pNode;
        while ((myNode.thePrev != null)
               && (myNode.compareTo(myNode.thePrev) == 0)) {
            /* Shift node */
            myNode = myNode.thePrev;
        }

        /* Return the first in the sequence */
        return myNode;
    }

    /**
     * Obtain last item in sequence of similar items.
     * @param pNode the node to check
     * @param <X> the data type
     * @return the first item
     */
    protected static <X extends Comparable<? super X>> MetisOrderedNode<X> getLastNodeInSequence(final MetisOrderedNode<X> pNode) {
        /* While we have a following item that is equal */
        MetisOrderedNode<X> myNode = pNode;
        while ((myNode.theNext != null)
               && (myNode.compareTo(myNode.theNext) == 0)) {
            /* Shift node */
            myNode = myNode.theNext;
        }

        /* Return the last in the sequence */
        return myNode;
    }

    /**
     * Swap node with previous node.
     */
    protected void swapWithPrevious() {
        /* Access previous node and following node */
        MetisOrderedNode<T> myPrev = thePrev;
        MetisOrderedNode<T> myNext = theNext;

        /* Swap linkages */
        thePrev = myPrev.thePrev;
        myPrev.thePrev = this;
        myPrev.theNext = myNext;
        theNext = myPrev;

        /* Adjust indices */
        theIndex--;
        myPrev.theIndex++;

        /* Adjust nodes above and below the pair */
        if (thePrev == null) {
            theList.setFirst(this);
        } else {
            thePrev.theNext = this;
        }
        if (myNext == null) {
            theList.setLast(myPrev);
        } else {
            myNext.thePrev = myPrev;
        }
    }
}
