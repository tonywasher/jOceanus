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
package net.sourceforge.JDataManager;

import java.util.Arrays;
import java.util.Iterator;

import net.sourceforge.JDataManager.ReportFields.ReportField;
import uk.co.tolcroft.models.data.DataItem;

/**
 * ValueSet class
 * @param <T> the type for the valueSet
 */
public class ValueSet<T extends DataItem<T>> {
    /**
     * The item to which the valueSet belongs
     */
    private final T theItem;

    /**
     * The fields for this valueSet
     */
    private final ReportFields theFields;

    /**
     * The number of values
     */
    private final int theNumValues;

    /**
     * The values
     */
    private final Object[] theValues;

    /**
     * Version # of the values
     */
    private int theVersion;

    /**
     * Is this valueSet a record of a deletion event
     */
    private boolean isDeletion;

    /**
     * Obtain the field definitions
     * @return the field definitions
     */
    public ReportFields getFields() {
        return theFields;
    }

    /**
     * Obtain the underlying item
     * @return the item
     */
    protected T getItem() {
        return theItem;
    }

    /**
     * Obtain the values
     * @return the values
     */
    protected Object[] getValues() {
        return theValues;
    }

    /**
     * Obtain the version # of the values
     * @return the version #
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Set the version # of the values
     * @param pVersion the version #
     */
    public void setVersion(int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Determine if this object is a record of a deletion event
     * @return true/false
     */
    public boolean isDeletion() {
        return isDeletion;
    }

    /**
     * Constructor
     * @param pItem the associated item
     */
    public ValueSet(T pItem) {
        /* Create the values array and initialise to null */
        theItem = pItem;
        theFields = pItem.getReportFields();
        theNumValues = theFields.getNumValues();
        theValues = new Object[theNumValues];
        if (theNumValues > 0)
            Arrays.fill(theValues, null);
    }

    /**
     * Clone this ValueSet
     * @return the cloned set
     */
    public ValueSet<T> cloneIt() {
        /* Create the valueSet and initialise to existing values */
        ValueSet<T> mySet = new ValueSet<T>(theItem);
        mySet.copyFrom(this);
        return mySet;
    }

    /**
     * Initialise values from a previous set
     * @param pPrevious the previous valueSet
     */
    public void copyFrom(ValueSet<?> pPrevious) {
        /* Create the values array */
        Object[] mySrc = pPrevious.theValues;
        int myCopyLen = pPrevious.theNumValues;
        if (myCopyLen > theNumValues)
            myCopyLen = theNumValues;
        if (myCopyLen > 0)
            System.arraycopy(mySrc, 0, theValues, 0, myCopyLen);
    }

    /**
     * Declare the valueSet as active
     */
    public void declareActive() {
        theItem.declareValues(this);
    }

    /**
     * Set the value
     * @param pField the field
     * @param pValue the value
     */
    public void setValue(ReportField pField,
                         Object pValue) {
        /* Ignore if not in valueSet */
        if (!pField.isValueSetField())
            return;

        /* Store the value */
        theValues[pField.getIndex()] = pValue;
    }

    /**
     * Get the value
     * @param pField the field
     * @return the value
     */
    public Object getValue(ReportField pField) {
        /* Return null if not in valueSet */
        if (!pField.isValueSetField())
            return null;

        /* Return the value */
        return theValues[pField.getIndex()];
    }

    /**
     * Get the value as an object type
     * @param <X> the required type
     * @param pField the field
     * @param pClass the class
     * @return the value
     */
    public <X> X getValue(ReportField pField,
                          Class<X> pClass) {
        /* Return the value */
        return pClass.cast(getValue(pField));
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a ValueSet */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the object as a ValueSet */
        ValueSet<?> mySet = (ValueSet<?>) pThat;
        Object[] myObj = mySet.theValues;

        /* Check for number of values */
        if (theNumValues != mySet.theNumValues)
            return false;

        /* Loop through the values */
        Iterator<ReportField> myIterator = theFields.fieldIterator();
        for (int i = 0; myIterator.hasNext(); i++) {
            /* Ignore non-equality fields */
            if (!myIterator.next().isEqualityField())
                continue;

            /* Not equal if the value is different */
            if (Difference.getDifference(theValues[i], myObj[i]).isDifferent())
                return false;
        }

        /* Identical if all fields match */
        return true;
    }

    @Override
    public int hashCode() {
        int iHashCode = 1;

        /* Loop through the values */
        Iterator<ReportField> myIterator = theFields.fieldIterator();
        for (int i = 0; myIterator.hasNext(); i++) {
            /* Ignore non-equality fields */
            if (!myIterator.next().isEqualityField())
                continue;

            /* Adjust existing hash */
            iHashCode *= 17;

            /* Access value and add hash if non-null */
            Object o = theValues[i];
            if (o != null)
                iHashCode += o.hashCode();
        }

        /* Return the hash */
        return iHashCode;

    }

    /**
     * Check for differences
     * @param pOriginal the object to check for differences
     * @return the difference
     */
    public Difference differs(ValueSet<T> pOriginal) {
        boolean isSecureDiff = false;

        /* Access the test values */
        Object[] myObj = pOriginal.theValues;

        /* Loop through the values */
        Iterator<ReportField> myIterator = theFields.fieldIterator();
        for (int i = 0; myIterator.hasNext(); i++) {
            /* Ignore non-equality fields */
            if (!myIterator.next().isEqualityField())
                continue;

            /* Check the field */
            Difference myDiff = Difference.getDifference(theValues[i], myObj[i]);
            if (myDiff == Difference.Different)
                return myDiff;
            if (myDiff == Difference.Security)
                isSecureDiff = true;
        }

        /* Determine the difference */
        return (isSecureDiff) ? Difference.Security : Difference.Identical;
    }

    /**
     * Check for a difference in a particular field
     * @param pField the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public Difference fieldChanged(ReportField pField,
                                   ValueSet<T> pOriginal) {
        /*
         * No difference if field does not exist, is not-equality or is not valueSet
         */
        if ((pField == null) || (!pField.isEqualityField()) || (!pField.isValueSetField()))
            return Difference.Identical;

        /* Determine the difference */
        int iIndex = pField.getIndex();
        return Difference.getDifference(theValues[iIndex], pOriginal.theValues[iIndex]);
    }
}
