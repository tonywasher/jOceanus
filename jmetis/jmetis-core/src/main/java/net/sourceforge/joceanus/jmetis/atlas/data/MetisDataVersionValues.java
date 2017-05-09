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
package net.sourceforge.joceanus.jmetis.atlas.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisIndexedItem;

/**
 * Metis Set of versioned Values.
 */
public class MetisDataVersionValues {
    /**
     * ValueSet object interface.
     */
    public interface MetisDataVersionedItem
            extends MetisDataFieldItem, MetisIndexedItem {
        /**
         * Does the object support versions?
         * @return true/false
         */
        default boolean isVersioned() {
            return false;
        }

        /**
         * Obtain Object Version Control.
         * @return the versionControl of the object
         */
        default MetisDataVersionControl getVersionControl() {
            return null;
        }
    }

    /**
     * The hash value for deletion flag.
     */
    private static final int DELETION_HASH = 3;

    /**
     * The versioned error.
     */
    private static final String ERROR_NOTVERSIONED = "Field is not versioned";

    /**
     * The valueType error.
     */
    private static final String ERROR_VALUETYPE = "Invalid valueType";

    /**
     * The item to which the valueSet belongs.
     */
    private final MetisDataVersionedItem theItem;

    /**
     * The fields for this valueSet.
     */
    private final MetisDataFieldSet theFields;

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
    protected MetisDataVersionValues(final MetisDataVersionedItem pItem) {
        /* Create the values array and initialise to null */
        theItem = pItem;
        theFields = pItem.getDataFieldSet();
        theNumValues = theFields.getNumValues();
        theValues = new Object[theNumValues];
    }

    /**
     * Obtain the field definitions.
     * @return the field definitions
     */
    public MetisDataFieldSet getFields() {
        return theFields;
    }

    /**
     * Obtain the underlying item.
     * @return the item
     */
    protected MetisDataVersionedItem getItem() {
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
    public MetisDataVersionValues cloneIt() {
        /* Create the valueSet and initialise to existing values */
        MetisDataVersionValues mySet = new MetisDataVersionValues(theItem);
        mySet.copyFrom(this);
        return mySet;
    }

    /**
     * Initialise values from a previous set.
     * @param pPrevious the previous valueSet
     */
    public void copyFrom(final MetisDataVersionValues pPrevious) {
        /* Copy deletion flag */
        isDeletion = pPrevious.isDeletion();

        /* Create the values array */
        Object[] mySrc = pPrevious.theValues;
        int myCopyLen = pPrevious.theNumValues;
        if (myCopyLen > theNumValues) {
            myCopyLen = theNumValues;
        }

        /* Copy values */
        if (myCopyLen > 0) {
            System.arraycopy(mySrc, 0, theValues, 0, myCopyLen);
        }
    }

    /**
     * Set the value.
     * @param pField the field
     * @param pValue the value
     */
    public void setValue(final MetisDataField pField,
                         final Object pValue) {
        /* Ignore if not in valueSet */
        if (!pField.getStorage().isVersioned()) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Check value type */
        checkValueType(pField, pValue);

        /* Store the value */
        theValues[pField.getIndex()] = pValue;
    }

    /**
     * Get the value.
     * @param pField the field
     * @return the value
     */
    public Object getValue(final MetisDataField pField) {
        /* Return null if not in valueSet */
        if (!pField.getStorage().isVersioned()) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
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
    public <X> X getValue(final MetisDataField pField,
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
        MetisDataVersionValues mySet = (MetisDataVersionValues) pThat;
        Object[] myObj = mySet.theValues;

        /* Check for deletion flag and # of values */
        if ((isDeletion != mySet.isDeletion)
            || (theNumValues != mySet.theNumValues)) {
            return false;
        }

        /* Loop through the values */
        Iterator<MetisDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-versioned fields */
            MetisDataField myField = myIterator.next();
            if ((!myField.getEquality().isEquality())
                || (!myField.getStorage().isVersioned())) {
                continue;
            }

            /* Not equal if the value is different */
            int iIndex = myField.getIndex();
            if (MetisDataDifference.difference(theValues[iIndex], myObj[iIndex]).isDifferent()) {
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
        Iterator<MetisDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-versioned fields */
            MetisDataField myField = myIterator.next();
            if ((!myField.getEquality().isEquality())
                || (!myField.getStorage().isVersioned())) {
                continue;
            }

            /* Adjust existing hash */
            iHashCode *= MetisDataFieldSet.HASH_PRIME;

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
    public MetisDataDifference differs(final MetisDataVersionValues pOriginal) {
        boolean isSecureDiff = false;

        /* Access the test values */
        Object[] myObj = pOriginal.theValues;

        /* Check for deletion flag and # of values */
        if ((isDeletion != pOriginal.isDeletion)
            || (theNumValues != pOriginal.theNumValues)) {
            return MetisDataDifference.DIFFERENT;
        }

        /* Loop through the values */
        Iterator<MetisDataField> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-versioned fields */
            MetisDataField myField = myIterator.next();
            if ((!myField.getEquality().isEquality())
                || (!myField.getStorage().isVersioned())) {
                continue;
            }

            /* Check the field */
            int iIndex = myField.getIndex();
            MetisDataDifference myDiff = MetisDataDifference.difference(theValues[iIndex], myObj[iIndex]);
            if (myDiff == MetisDataDifference.DIFFERENT) {
                return myDiff;
            }
            if (myDiff == MetisDataDifference.SECURITY) {
                isSecureDiff = true;
            }
        }

        /* Determine the difference */
        return isSecureDiff
                            ? MetisDataDifference.SECURITY
                            : MetisDataDifference.IDENTICAL;
    }

    /**
     * Check for a difference in a particular field.
     * @param pField the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisDataField pField,
                                            final MetisDataVersionValues pOriginal) {
        /* No difference if field does not exist, is not-equality or is not versioned */
        if ((pField == null)
            || (!pField.getEquality().isEquality())
            || (!pField.getStorage().isVersioned())) {
            return MetisDataDifference.IDENTICAL;
        }

        /* Determine the difference */
        int iIndex = pField.getIndex();
        return MetisDataDifference.difference(theValues[iIndex], pOriginal.theValues[iIndex]);
    }

    /**
     * Check the value.
     * @param pField the field
     * @param pValue the value
     */
    protected void checkValueType(final MetisDataField pField,
                                  final Object pValue) {
        /* Null/String is always allowed */
        if (pValue == null
            || pValue instanceof String) {
            return;
        }

        /* Integer is allowed for Link type */
        MetisDataType myDataType = pField.getDataType();
        if (MetisDataType.LINK.equals(myDataType)
            && pValue instanceof Integer) {
            return;
        }

        /* Check expected dataType */
        Class<?> myClass = myDataType.getDataTypeClass();
        boolean bAllowed = myClass == null || myClass.isInstance(pValue);

        /* If we are not allowed */
        if (!bAllowed) {
            throw new IllegalArgumentException(ERROR_VALUETYPE);
        }
    }
}