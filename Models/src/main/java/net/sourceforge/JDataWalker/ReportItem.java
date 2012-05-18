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
package net.sourceforge.JDataWalker;

import java.util.Iterator;

import net.sourceforge.JDataWalker.ReportFields.ReportField;
import net.sourceforge.JDataWalker.ReportObject.ReportDetail;
import net.sourceforge.JSortedList.SortedItem;

public abstract class ReportItem<T extends ReportItem<T>> extends SortedItem<T> implements ReportDetail {
    /**
     * Local Report fields
     */
    protected static final ReportFields theLocalFields = new ReportFields(ReportItem.class.getSimpleName());

    /**
     * Instance ReportFields
     */
    private final ReportFields theFields;

    /**
     * Declare fields
     * @return the fields
     */
    public abstract ReportFields declareFields();

    /* Field IDs */
    public static final ReportField FIELD_LIST = theLocalFields.declareLocalField("List");
    public static final ReportField FIELD_NODE = theLocalFields.declareLocalField("Node");

    @Override
    public ReportFields getReportFields() {
        return theFields;
    }

    @Override
    public String getObjectSummary() {
        return getReportFields().getName();
    }

    @Override
    public Object getFieldValue(ReportField pField) {
        if (pField == FIELD_LIST)
            return getList();
        if (pField == FIELD_NODE)
            return this.getLinkNode(getList());
        return null;
    }

    /**
     * Construct a new item
     * @param pList the list that this item is associated with
     */
    public ReportItem(ReportList<T> pList) {
        /* Initialise as an item in the list */
        super(pList);

        /* Declare fields (allowing for subclasses) */
        theFields = declareFields();
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is the same class */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the object as a ReportItem */
        ReportItem<?> myItem = (ReportItem<?>) pThat;

        /* Loop through the fields */
        Iterator<ReportField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            ReportField myField = myIterator.next();

            /* Skip if not used in equality */
            if (!myField.isEqualityField())
                continue;

            /* Access the values */
            Object myValue = getFieldValue(myField);
            Object myNew = myItem.getFieldValue(myField);

            /* Check the field */
            Difference myDiff = Difference.getDifference(myValue, myNew);
            if (myDiff == Difference.Different)
                return false;
        }

        /* Return identical */
        return true;
    }

    /**
     * Determine whether two ReportItem objects differ.
     * @param pNew The new report item
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public Difference differs(ReportItem<?> pNew) {
        /* Handle trivial cases */
        if (this == pNew)
            return Difference.Identical;
        if (pNew == null)
            return Difference.Different;

        /* Handle class differences */
        if (this.getClass() != pNew.getClass())
            return Difference.Different;

        /* Loop through the fields */
        Iterator<ReportField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Access Field */
            ReportField myField = myIterator.next();

            /* Skip if not used in equality */
            if (!myField.isEqualityField())
                continue;

            /* Access the values */
            Object myValue = getFieldValue(myField);
            Object myNew = pNew.getFieldValue(myField);

            /* Check the field */
            Difference myDiff = Difference.getDifference(myValue, myNew);
            if (myDiff == Difference.Different)
                return myDiff;
        }

        /* Return identical */
        return Difference.Identical;
    }
}
