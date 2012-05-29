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
 * Linked list element.
 * @author Tony Washer
 * @param <T> the data-type of the list
 */
public class OrderedNode<T extends Comparable<T>> {
    /**
     * The object that this node refers to.
     */
    private final T theObject;

    /**
     * The list that this node belongs to.
     */
    private final OrderedList<T> theList;

    /**
     * Is the object hidden.
     */
    private boolean isHidden = false;

    /**
     * The standard index of this item.
     */
    private int theIndex = -1;

    /**
     * The hidden index of this item.
     */
    private int theHiddenIndex = -1;

    /**
     * The next node in the sequence.
     */
    private OrderedNode<T> theNext = null;

    /**
     * The previous node in the sequence.
     */
    private OrderedNode<T> thePrev = null;

    /**
     * Is the Node hidden.
     * @return <code>true/false</code>
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Get object.
     * @return <code>true/false</code>
     */
    protected T getObject() {
        return theObject;
    }

    /**
     * Initialiser.
     * @param pList the list that this node belongs to
     * @param pObject the object that this node represents
     */
    protected OrderedNode(final OrderedList<T> pList,
                          final T pObject) {
        /* Store details */
        theList = pList;
        theObject = pObject;
    }

    /**
     * Set Hidden flag.
     * @param pHidden <code>true/false</code>
     */
    protected void setHidden(final boolean pHidden) {
        /* Start with this node */
        OrderedNode<T> myNode = this;

        /* Record the hidden flag */
        isHidden = pHidden;

        /* Determine adjustment factor */
        int iAdjust = (isHidden) ? -1 : 1;

        /* Loop through nodes */
        while (myNode != null) {
            /* Adjust hidden index */
            myNode.theHiddenIndex += iAdjust;

            /* Shift to next node */
            myNode = myNode.theNext;
        }
    }

    /**
     * add Node to the list immediately before the passed node.
     * @param pPoint the node before which we have to insert
     */
    protected void addBeforeNode(final OrderedNode<T> pPoint) {
        /* Determine whether this item is hidden */
        boolean isVisible = !isHidden;

        /* Set values for the new item */
        thePrev = pPoint.thePrev;
        theNext = pPoint;

        /* Copy Indices from insert point */
        theIndex = pPoint.theIndex;
        theHiddenIndex = pPoint.theHiddenIndex;

        /* If hidden status differs, adjust hidden index */
        if (pPoint.isHidden == isVisible) {
            theHiddenIndex += (isVisible) ? 1 : -1;
        }

        /* Add to the list */
        pPoint.thePrev = this;
        if (thePrev != null) {
            thePrev.theNext = this;
        }

        /* Loop through subsequent elements increasing the indices */
        adjustIndicesAfterInsert(theNext, isVisible);
    }

    /**
     * add Node to the tail of the list (note that we cannot be an empty list at this point).
     */
    protected void addToTail() {
        /* Determine whether this item is hidden */
        boolean isVisible = !isHidden;

        /* Set values for the new item */
        thePrev = theList.getTail();
        theNext = null;

        /* Set new indices */
        theIndex = thePrev.theIndex + 1;
        theHiddenIndex = (isVisible) ? thePrev.theHiddenIndex + 1 : thePrev.theHiddenIndex;

        /* Add to the list */
        thePrev.theNext = this;
    }

    /**
     * add Node to the list immediately after the passed node.
     * @param pPoint the node after which we have to insert
     */
    protected void addAfterNode(final OrderedNode<T> pPoint) {
        /* Determine whether this item is hidden */
        boolean isVisible = !isHidden;

        /* Set values for the new item */
        theNext = pPoint.theNext;
        thePrev = pPoint;

        /* Set new indices from insert point */
        theIndex = pPoint.theIndex + 1;
        theHiddenIndex = pPoint.theHiddenIndex + 1;
        if (!isVisible) {
            theHiddenIndex--;
        }

        /* Add to the list */
        pPoint.theNext = this;
        if (theNext != null) {
            theNext.thePrev = this;
        }

        /* Adjust following indices */
        adjustIndicesAfterInsert(theNext, isVisible);
    }

    /**
     * add Node to the head of the list.
     */
    protected void addToHead() {
        /* Determine whether this item is hidden */
        boolean isVisible = !isHidden;

        /* Set values for the new item */
        theNext = theList.getHead();
        thePrev = null;

        /* Set new indices */
        theIndex = 0;
        theHiddenIndex = (isVisible) ? 0 : -1;

        /* Adjust the following link */
        if (theNext != null) {
            theNext.thePrev = this;
        }

        /* Adjust following indices */
        adjustIndicesAfterInsert(theNext, isVisible);
    }

    /**
     * Adjust indexes after insert.
     * @param pNode the first node to adjust
     * @param isVisible is the insert visible
     */
    private static void adjustIndicesAfterInsert(final OrderedNode<?> pNode,
                                                 final boolean isVisible) {
        OrderedNode<?> myCurr = pNode;
        while (myCurr != null) {
            /* Increment indices */
            myCurr.theIndex++;
            if (isVisible) {
                myCurr.theHiddenIndex++;
            }
            myCurr = myCurr.theNext;
        }
    }

    /**
     * Adjust indexes after remove.
     * @param pNode the first node to adjust
     * @param isVisible is the removal visible
     */
    private static void adjustIndicesAfterRemove(final OrderedNode<?> pNode,
                                                 final boolean isVisible) {
        OrderedNode<?> myCurr = pNode;
        while (myCurr != null) {
            /* Decrement indices */
            myCurr.theIndex--;
            if (isVisible) {
                myCurr.theHiddenIndex--;
            }
            myCurr = myCurr.theNext;
        }
    }

    /**
     * Remove node from list.
     */
    protected void remove() {
        boolean isVisible;

        /* Determine whether this item is visible */
        isVisible = !isHidden;

        /* Adjust nodes either side of this node */
        if (thePrev != null) {
            thePrev.theNext = theNext;
        }
        if (theNext != null) {
            theNext.thePrev = thePrev;
        }

        /* Adjust following indices */
        adjustIndicesAfterRemove(theNext, isVisible);

        /* clean our links */
        theNext = null;
        thePrev = null;
    }

    /**
     * Get the next node in the sequence.
     * @return the Next node
     */
    protected OrderedNode<T> getNext() {
        return theNext;
    }

    /**
     * Get the next node in the sequence.
     * @param doSkipHidden skip hidden items
     * @return the Next visible node
     */
    protected OrderedNode<T> getNext(final boolean doSkipHidden) {
        /* Access the next item */
        OrderedNode<T> myNext = theNext;

        /* If we should skip hidden items */
        if (doSkipHidden) {
            /* Loop skipping hidden items */
            while ((myNext != null) && (myNext.isHidden)) {
                myNext = myNext.theNext;
            }
        }

        /* Return to caller */
        return myNext;
    }

    /**
     * Get the previous node in the sequence.
     * @return the Previous node
     */
    protected OrderedNode<T> getPrev() {
        return thePrev;
    }

    /**
     * Get the previous node in the sequence.
     * @param doSkipHidden skip hidden items
     * @return the previous visible node
     */
    protected OrderedNode<T> getPrev(final boolean doSkipHidden) {
        /* Access the previous item */
        OrderedNode<T> myPrev = thePrev;

        /* If we should skip hidden items */
        if (doSkipHidden) {
            /* Loop skipping hidden items */
            while ((myPrev != null) && (myPrev.isHidden)) {
                myPrev = myPrev.thePrev;
            }
        }

        /* Return to caller */
        return myPrev;
    }

    /**
     * Get the index of the item.
     * @return the relevant index of the item
     */
    public int getIndex() {
        return theIndex;
    }

    /**
     * Get the index of the item.
     * @param doSkipHidden skip hidden items
     * @return the relevant index of the item
     */
    public int getIndex(final boolean doSkipHidden) {
        /* Return the relevant index */
        return (doSkipHidden) ? theHiddenIndex : theIndex;
    }

    /**
     * Get the list to which this node belongs.
     * @return the holding list
     */
    protected OrderedList<T> getList() {
        /* Return the list */
        return theList;
    }

    /**
     * Compare this node to another.
     * @param pThat the node to compare to
     * @return (-1,0,1) depending on order
     */
    protected int compareTo(final OrderedNode<T> pThat) {
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
}
