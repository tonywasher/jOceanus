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
     * The standard index of this item.
     */
    private int theIndex = -1;

    /**
     * The next node in the sequence.
     */
    private OrderedNode<T> theNext = null;

    /**
     * The previous node in the sequence.
     */
    private OrderedNode<T> thePrev = null;

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
     * add Node to the list immediately before the passed node.
     * @param pPoint the node before which we have to insert
     */
    protected void addBeforeNode(final OrderedNode<T> pPoint) {
        /* Set values for the new item */
        thePrev = pPoint.thePrev;
        theNext = pPoint;

        /* Copy Index from insert point */
        theIndex = pPoint.theIndex;

        /* Add to the list */
        pPoint.thePrev = this;
        if (thePrev != null) {
            thePrev.theNext = this;
        }

        /* Loop through subsequent elements increasing the indices */
        adjustIndicesAfterInsert(theNext);
    }

    /**
     * add Node to the tail of the list (note that we cannot be an empty list at this point).
     */
    protected void addToTail() {
        /* Set values for the new item */
        thePrev = theList.getLast();
        theNext = null;

        /* Set new index */
        theIndex = thePrev.theIndex + 1;

        /* Add to the list */
        thePrev.theNext = this;
    }

    /**
     * add Node to the list immediately after the passed node.
     * @param pPoint the node after which we have to insert
     */
    protected void addAfterNode(final OrderedNode<T> pPoint) {
        /* Set values for the new item */
        theNext = pPoint.theNext;
        thePrev = pPoint;

        /* Set new index from insert point */
        theIndex = pPoint.theIndex + 1;

        /* Add to the list */
        pPoint.theNext = this;
        if (theNext != null) {
            theNext.thePrev = this;
        }

        /* Adjust following indices */
        adjustIndicesAfterInsert(theNext);
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

        /* Adjust the following link */
        if (theNext != null) {
            theNext.thePrev = this;
        }

        /* Adjust following indices */
        adjustIndicesAfterInsert(theNext);
    }

    /**
     * Adjust indexes after insert.
     * @param pNode the first node to adjust
     */
    private static void adjustIndicesAfterInsert(final OrderedNode<?> pNode) {
        OrderedNode<?> myCurr = pNode;
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
    private static void adjustIndicesAfterRemove(final OrderedNode<?> pNode) {
        OrderedNode<?> myCurr = pNode;
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
        /* Adjust nodes either side of this node */
        if (thePrev != null) {
            thePrev.theNext = theNext;
        }
        if (theNext != null) {
            theNext.thePrev = thePrev;
        }

        /* Adjust following indices */
        adjustIndicesAfterRemove(theNext);

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
     * Get the previous node in the sequence.
     * @return the Previous node
     */
    protected OrderedNode<T> getPrev() {
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
