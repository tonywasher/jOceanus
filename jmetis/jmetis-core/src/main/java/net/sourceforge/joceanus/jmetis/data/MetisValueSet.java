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

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataValues;

/**
 * ValueSet class.
 */
public class MetisValueSet {
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
    private final MetisDataValues theItem;

    /**
     * The fields for this valueSet.
     */
    private final MetisFields theFields;

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
    public MetisValueSet(final MetisDataValues pItem) {
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
    public MetisFields getFields() {
        return theFields;
    }

    /**
     * Obtain the underlying item.
     * @return the item
     */
    protected MetisDataValues getItem() {
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
    public MetisValueSet cloneIt() {
        /* Create the valueSet and initialise to existing values */
        MetisValueSet mySet = new MetisValueSet(theItem);
        mySet.copyFrom(this);
        return mySet;
    }

    /**
     * Initialise values from a previous set.
     * @param pPrevious the previous valueSet
     */
    public void copyFrom(final MetisValueSet pPrevious) {
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
    public void setValue(final MetisField pField,
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
    public Object getValue(final MetisField pField) {
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
    public <X> X getValue(final MetisField pField,
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
        MetisValueSet mySet = (MetisValueSet) pThat;
        Object[] myObj = mySet.theValues;

        /* Check for deletion flag and # of values */
        if ((isDeletion != mySet.isDeletion)
            || (theNumValues != mySet.theNumValues)) {
            return false;
        }

        /* Loop through the values */
        Iterator<MetisField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-valueSet fields */
            MetisField myField = myIterator.next();
            if ((!myField.isEqualityField())
                || (!myField.isValueSetField())) {
                continue;
            }

            /* Not equal if the value is different */
            int iIndex = myField.getIndex();
            if (MetisDifference.getDifference(theValues[iIndex], myObj[iIndex]).isDifferent()) {
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
        Iterator<MetisField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-valueSet fields */
            MetisField myField = myIterator.next();
            if ((!myField.isEqualityField())
                || (!myField.isValueSetField())) {
                continue;
            }

            /* Adjust existing hash */
            iHashCode *= MetisFields.HASH_PRIME;

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
    public MetisDifference differs(final MetisValueSet pOriginal) {
        boolean isSecureDiff = false;

        /* Access the test values */
        Object[] myObj = pOriginal.theValues;

        /* Check for deletion flag and # of values */
        if ((isDeletion != pOriginal.isDeletion)
            || (theNumValues != pOriginal.theNumValues)) {
            return MetisDifference.DIFFERENT;
        }

        /* Loop through the values */
        Iterator<MetisField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-valueSet fields */
            MetisField myField = myIterator.next();
            if ((!myField.isEqualityField())
                || (!myField.isValueSetField())) {
                continue;
            }

            /* Check the field */
            int iIndex = myField.getIndex();
            MetisDifference myDiff = MetisDifference.getDifference(theValues[iIndex], myObj[iIndex]);
            if (myDiff == MetisDifference.DIFFERENT) {
                return myDiff;
            }
            if (myDiff == MetisDifference.SECURITY) {
                isSecureDiff = true;
            }
        }

        /* Determine the difference */
        return isSecureDiff
                            ? MetisDifference.SECURITY
                            : MetisDifference.IDENTICAL;
    }

    /**
     * Check for a difference in a particular field.
     * @param pField the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public MetisDifference fieldChanged(final MetisField pField,
                                   final MetisValueSet pOriginal) {
        /*
         * No difference if field does not exist, is not-equality or is not valueSet
         */
        if ((pField == null)
            || (!pField.isEqualityField())
            || (!pField.isValueSetField())) {
            return MetisDifference.IDENTICAL;
        }

        /* Determine the difference */
        int iIndex = pField.getIndex();
        return MetisDifference.getDifference(theValues[iIndex], pOriginal.theValues[iIndex]);
    }
}
