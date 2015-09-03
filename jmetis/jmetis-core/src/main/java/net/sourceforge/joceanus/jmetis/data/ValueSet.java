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
package net.sourceforge.joceanus.jmetis.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataValues;

/**
 * ValueSet class.
 */
public class ValueSet {
    /**
     * The hash value for deletion flag.
     */
    private static final int DELETION_HASH = 3;

    /**
     * the name of the version field.
     */
    public static final String FIELD_VERSION = "Version";

    /**
     * the name of the deletion field.
     */
    public static final String FIELD_DELETION = "isDeletion";

    /**
     * The item to which the valueSet belongs.
     */
    private final JDataValues theItem;

    /**
     * The fields for this valueSet.
     */
    private final JDataFields theFields;

    /**
     * The number of values.
     */
    private final int theNumValues;

    /**
     * The values.
     */
    private final Object[] theValues;

    /**
     * Version # of the values.
     */
    private int theVersion;

    /**
     * Is this valueSet a record of a deletion event.
     */
    private boolean isDeletion;

    /**
     * Constructor.
     * @param pItem the associated item
     */
    public ValueSet(final JDataValues pItem) {
        /* Create the values array and initialise to null */
        theItem = pItem;
        theFields = pItem.getDataFields();
        theNumValues = theFields.getNumValues();
        theValues = new Object[theNumValues];
    }

    /**
     * Obtain the field definitions.
     * @return the field definitions
     */
    public JDataFields getFields() {
        return theFields;
    }

    /**
     * Obtain the underlying item.
     * @return the item
     */
    protected JDataValues getItem() {
        return theItem;
    }

    /**
     * Obtain the values.
     * @return the values
     */
    protected Object[] getValues() {
        return theValues;
    }

    /**
     * Obtain the version # of the values.
     * @return the version #
     */
    public int getVersion() {
        return theVersion;
    }

    /**
     * Set the version # of the values.
     * @param pVersion the version #
     */
    public void setVersion(final int pVersion) {
        theVersion = pVersion;
    }

    /**
     * Determine if this object is a record of a deletion event.
     * @return true/false
     */
    public boolean isDeletion() {
        return isDeletion;
    }

    /**
     * Adjust deletion flag.
     * @param pDeletion true/false
     */
    public void setDeletion(final boolean pDeletion) {
        isDeletion = pDeletion;
    }

    /**
     * Clone this ValueSet.
     * @return the cloned set
     */
    public ValueSet cloneIt() {
        /* Create the valueSet and initialise to existing values */
        ValueSet mySet = new ValueSet(theItem);
        mySet.copyFrom(this);
        return mySet;
    }

    /**
     * Initialise values from a previous set.
     * @param pPrevious the previous valueSet
     */
    public void copyFrom(final ValueSet pPrevious) {
        /* Copy deletion flag */
        isDeletion = pPrevious.isDeletion();

        /* Create the values array */
        Object[] mySrc = pPrevious.theValues;
        int myCopyLen = pPrevious.theNumValues;
        if (myCopyLen > theNumValues) {
            myCopyLen = theNumValues;
        }
        if (myCopyLen > 0) {
            System.arraycopy(mySrc, 0, theValues, 0, myCopyLen);
        }
    }

    /**
     * Declare the valueSet as active.
     */
    public void declareActive() {
        theItem.declareValues(this);
    }

    /**
     * Set the value.
     * @param pField the field
     * @param pValue the value
     */
    public void setValue(final JDataField pField,
                         final Object pValue) {
        /* Ignore if not in valueSet */
        if (!pField.isValueSetField()) {
            return;
        }

        /* Store the value */
        theValues[pField.getIndex()] = pValue;
    }

    /**
     * Get the value.
     * @param pField the field
     * @return the value
     */
    public Object getValue(final JDataField pField) {
        /* Return null if not in valueSet */
        if (!pField.isValueSetField()) {
            return null;
        }

        /* Return the value */
        return theValues[pField.getIndex()];
    }

    /**
     * Get the indexed value.
     * @param pIndex the index
     * @return the value
     */
    protected Object getValue(final int pIndex) {
        /* Return the value */
        return theValues[pIndex];
    }

    /**
     * Get the value as an object type.
     * @param <X> the required type
     * @param pField the field
     * @param pClass the class
     * @return the value
     */
    public <X> X getValue(final JDataField pField,
                          final Class<X> pClass) {
        /* Return the value */
        return pClass.cast(getValue(pField));
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a ValueSet */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a ValueSet */
        ValueSet mySet = (ValueSet) pThat;
        Object[] myObj = mySet.theValues;

        /* Check for deletion flag and # of values */
        if ((isDeletion != mySet.isDeletion)
            || (theNumValues != mySet.theNumValues)) {
            return false;
        }

        /* Loop through the values */
        Iterator<JDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-valueSet fields */
            JDataField myField = myIterator.next();
            if ((!myField.isEqualityField())
                || (!myField.isValueSetField())) {
                continue;
            }

            /* Not equal if the value is different */
            int iIndex = myField.getIndex();
            if (Difference.getDifference(theValues[iIndex], myObj[iIndex]).isDifferent()) {
                return false;
            }
        }

        /* Identical if all fields match */
        return true;
    }

    @Override
    public int hashCode() {
        /* Use deletion flag in hash Code */
        int iHashCode = isDeletion
                                   ? DELETION_HASH
                                   : 1;

        /* Loop through the values */
        Iterator<JDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-valueSet fields */
            JDataField myField = myIterator.next();
            if ((!myField.isEqualityField())
                || (!myField.isValueSetField())) {
                continue;
            }

            /* Adjust existing hash */
            iHashCode *= JDataFields.HASH_PRIME;

            /* Access value and add hash if non-null */
            Object o = theValues[myField.getIndex()];
            if (o != null) {
                iHashCode += o.hashCode();
            }
        }

        /* Return the hash */
        return iHashCode;

    }

    /**
     * Check for differences.
     * @param pOriginal the object to check for differences
     * @return the difference
     */
    public Difference differs(final ValueSet pOriginal) {
        boolean isSecureDiff = false;

        /* Access the test values */
        Object[] myObj = pOriginal.theValues;

        /* Check for deletion flag and # of values */
        if ((isDeletion != pOriginal.isDeletion)
            || (theNumValues != pOriginal.theNumValues)) {
            return Difference.DIFFERENT;
        }

        /* Loop through the values */
        Iterator<JDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-valueSet fields */
            JDataField myField = myIterator.next();
            if ((!myField.isEqualityField())
                || (!myField.isValueSetField())) {
                continue;
            }

            /* Check the field */
            int iIndex = myField.getIndex();
            Difference myDiff = Difference.getDifference(theValues[iIndex], myObj[iIndex]);
            if (myDiff == Difference.DIFFERENT) {
                return myDiff;
            }
            if (myDiff == Difference.SECURITY) {
                isSecureDiff = true;
            }
        }

        /* Determine the difference */
        return isSecureDiff
                            ? Difference.SECURITY
                            : Difference.IDENTICAL;
    }

    /**
     * Check for a difference in a particular field.
     * @param pField the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public Difference fieldChanged(final JDataField pField,
                                   final ValueSet pOriginal) {
        /*
         * No difference if field does not exist, is not-equality or is not valueSet
         */
        if ((pField == null)
            || (!pField.isEqualityField())
            || (!pField.isValueSetField())) {
            return Difference.IDENTICAL;
        }

        /* Determine the difference */
        int iIndex = pField.getIndex();
        return Difference.getDifference(theValues[iIndex], pOriginal.theValues[iIndex]);
    }
}
