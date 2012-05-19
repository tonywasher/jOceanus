/*******************************************************************************
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

import net.sourceforge.JDataManager.ReportFields;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import net.sourceforge.JDataManager.ReportObject;
import net.sourceforge.JDataManager.ReportObject.ReportDetail;

public class LinkNode<T extends LinkObject<T>> implements ReportDetail {
    /**
     * Report fields
     */
    protected static final ReportFields theFields = new ReportFields(LinkNode.class.getSimpleName());

    /* Field IDs */
    public static final ReportField FIELD_ITEM = theFields.declareLocalField("Item");
    public static final ReportField FIELD_LIST = theFields.declareLocalField("List");
    public static final ReportField FIELD_NEXT = theFields.declareLocalField("Next");
    public static final ReportField FIELD_PREV = theFields.declareLocalField("Previous");
    public static final ReportField FIELD_HIDN = theFields.declareLocalField("isHidden");
    public static final ReportField FIELD_IDX = theFields.declareLocalField("Index");
    public static final ReportField FIELD_HIDX = theFields.declareLocalField("HiddenIndex");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_ITEM)
            return theObject;
        if (pField == FIELD_LIST)
            return theList;
        if (pField == FIELD_NEXT)
            return theNext;
        if (pField == FIELD_PREV)
            return thePrev;
        if (pField == FIELD_HIDN)
            return (isHidden) ? true : ReportObject.skipField;
        if (pField == FIELD_IDX)
            return theIndex;
        if (pField == FIELD_HIDX)
            return theHiddenIndex;
        return null;
    }

    @Override
    public String getObjectSummary() {
        return "LinkNode(" + theIndex + "," + theHiddenIndex + ")";
    }

    /**
     * The object that this node refers to
     */
    private T theObject = null;

    /**
     * The list that this node belongs to
     */
    private SortedList<T> theList = null;

    /**
     * Is the object hidden
     */
    private boolean isHidden = false;

    /**
     * The standard index of this item
     */
    private int theIndex = -1;

    /**
     * The hidden index of this item
     */
    private int theHiddenIndex = -1;

    /**
     * The next node in the sequence
     */
    private LinkNode<T> theNext = null;

    /**
     * The previous node in the sequence
     */
    private LinkNode<T> thePrev = null;

    /**
     * Is the Node hidden
     * @return <code>true/false</code>
     */
    public boolean isHidden() {
        return isHidden;
    }

    /**
     * Get object
     * @return <code>true/false</code>
     */
    protected T getObject() {
        return theObject;
    }

    /**
     * Initialiser
     * @param pList the list that this node belongs to
     * @param pObject the object that this node represents
     */
    protected LinkNode(SortedList<T> pList,
                       T pObject) {
        theList = pList;
        theObject = pObject;
    }

    /**
     * Set Hidden flag
     * @param isHidden <code>true/false</code>
     */
    protected void setHidden(boolean isHidden) {
        LinkNode<T> myNode = this;

        /* Record the hidden flag */
        this.isHidden = isHidden;

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
     * add Node to the list searching from the start
     * @param pFirst - first node in list (or null)
     * @param pLast - last node in list (or null)
     */
    protected void addFromStart(LinkNode<T> pFirst,
                                LinkNode<T> pLast) {
        LinkNode<T> myCurr;
        boolean isVisible;

        /* Determine whether this item is hidden */
        isVisible = !isHidden;

        /* Loop through the current items */
        for (myCurr = pFirst; myCurr != null; myCurr = myCurr.theNext) {
            /* Break if we have found an element that should be later */
            if (myCurr.compareTo(this) >= 0)
                break;
        }

        /* If we found an insert point */
        if (myCurr != null) {
            /* Set values for the new item */
            thePrev = myCurr.thePrev;
            theNext = myCurr;

            /* Copy Indices from insert point */
            theIndex = myCurr.theIndex;
            theHiddenIndex = myCurr.theHiddenIndex;

            /* If hidden status differs, adjust hidden index */
            if (myCurr.isHidden == isVisible)
                theHiddenIndex += (isVisible) ? 1 : -1;

            /* Add to the list */
            myCurr.thePrev = this;
            if (thePrev != null)
                thePrev.theNext = this;

            /* Loop through subsequent elements increasing the indices */
            while (myCurr != null) {
                /* Increment indices */
                myCurr.theIndex++;
                if (isVisible)
                    myCurr.theHiddenIndex++;
                myCurr = myCurr.theNext;
            }
        }

        /* else we need to add to the end of the list */
        else {
            /* Set values for the new item */
            thePrev = pLast;
            theNext = null;

            /* If this is the first item */
            if (pLast == null) {
                /* Set new indices */
                theIndex = 0;
                theHiddenIndex = (isVisible) ? 0 : -1;
            }

            /* else we have a previous item */
            else {
                /* Set new indices */
                theIndex = thePrev.theIndex + 1;
                theHiddenIndex = (isVisible) ? thePrev.theHiddenIndex + 1 : thePrev.theHiddenIndex;

                /* Add to the list */
                thePrev.theNext = this;
            }
        }
    }

    /**
     * add Node to the list searching from the end
     * @param pFirst - first node in list (or null)
     * @param pLast - last node in list (or null)
     */
    protected void addFromEnd(LinkNode<T> pFirst,
                              LinkNode<T> pLast) {
        LinkNode<T> myCurr;
        boolean isVisible;

        /* Determine whether this item is hidden */
        isVisible = !isHidden;

        /* Loop backwards through the current items */
        for (myCurr = pLast; myCurr != null; myCurr = myCurr.thePrev) {
            /* Break if we have found an element that should be earlier */
            if (myCurr.compareTo(this) <= 0)
                break;
        }

        /* If we found an insert point */
        if (myCurr != null) {
            /* Set values for the new item */
            theNext = myCurr.theNext;
            thePrev = myCurr;

            /* Set new indices from insert point */
            theIndex = myCurr.theIndex + 1;
            theHiddenIndex = myCurr.theHiddenIndex + 1;
            if (!isVisible)
                theHiddenIndex--;

            /* Add to the list */
            myCurr.theNext = this;
        }

        /* else we need to add to the beginning of the list */
        else {
            /* Set values for the new item */
            theNext = pFirst;
            thePrev = null;

            /* Set new indices */
            theIndex = 0;
            theHiddenIndex = (!isVisible) ? -1 : 0;
        }

        /* Adjust the following link */
        if (theNext != null)
            theNext.thePrev = this;

        /* Loop through subsequent elements increasing the indices */
        myCurr = theNext;
        while (myCurr != null) {
            /* Increment indices */
            myCurr.theIndex++;
            if (isVisible)
                myCurr.theHiddenIndex++;
            myCurr = myCurr.theNext;
        }
    }

    /**
     * Remove node from list
     */
    protected void remove() {
        LinkNode<T> myCurr;
        boolean isVisible;

        /* Determine whether this item is visible */
        isVisible = !isHidden;

        /* Adjust nodes either side of this node */
        if (thePrev != null)
            thePrev.theNext = theNext;
        if (theNext != null)
            theNext.thePrev = thePrev;

        /* Loop through subsequent elements decreasing the indices */
        myCurr = theNext;
        while (myCurr != null) {
            /* Decrement indices */
            myCurr.theIndex--;
            if (isVisible)
                myCurr.theHiddenIndex--;
            myCurr = myCurr.theNext;
        }

        /* clean our links */
        theNext = null;
        thePrev = null;
    }

    /**
     * Get the next node in the sequence
     * @param doSkipHidden skip hidden items
     * @return the Next visible node
     */
    protected LinkNode<T> getNext(boolean doSkipHidden) {
        LinkNode<T> myNext;

        /* Access the next item */
        myNext = theNext;

        /* If we should skip hidden items */
        if (doSkipHidden)
            /* Loop skipping hidden items */
            while ((myNext != null) && (myNext.isHidden))
                myNext = myNext.theNext;

        /* Return to caller */
        return myNext;
    }

    /**
     * Get the previous node in the sequence
     * @param doSkipHidden skip hidden items
     * @return the previous visible node
     */
    protected LinkNode<T> getPrev(boolean doSkipHidden) {
        LinkNode<T> myPrev;

        /* Access the previous item */
        myPrev = thePrev;

        /* If we should skip hidden items */
        if (doSkipHidden)
            /* Loop skipping hidden items */
            while ((myPrev != null) && (myPrev.isHidden))
                myPrev = myPrev.thePrev;

        /* Return to caller */
        return myPrev;
    }

    /**
     * Get the index of the item
     * @param doSkipHidden skip hidden items
     * @return the relevant index of the item
     */
    public int getIndex(boolean doSkipHidden) {
        /* Return the relevant index */
        return (doSkipHidden) ? theHiddenIndex : theIndex;
    }

    /**
     * Get the list to which this node belongs
     * @return the holding list
     */
    protected SortedList<T> getList() {
        /* Return the list */
        return theList;
    }

    /**
     * Compare this node to another
     * @param pThat the node to compare to
     * @return (-1,0,1) depending on order
     */
    protected int compareTo(LinkNode<T> pThat) {
        return theObject.compareTo(pThat.theObject);
    }
}
