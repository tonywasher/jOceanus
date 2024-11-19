/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.metis.field;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.MetisDataException;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * Set of dataValues.
 */
public class MetisFieldVersionValues {
    /**
     * The hash value for deletion flag.
     */
    private static final int DELETION_HASH = 3;

    /**
     * The versioned error.
     */
    protected static final String ERROR_NOTVERSIONED = "Field is not versioned";

    /**
     * The valueType error.
     */
    private static final String ERROR_VALUETYPE = "Invalid valueType";

    /**
     * The item to which the valueSet belongs.
     */
    private final MetisFieldVersionedItem theItem;

    /**
     * The fields for this valueSet.
     */
    private final MetisFieldSetDef theFields;

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
    protected MetisFieldVersionValues(final MetisFieldVersionedItem pItem) {
        /* Create the values array and initialise to null */
        theItem = pItem;
        theFields = pItem.getDataFieldSet();
        theNumValues = theFields.getNumVersioned();
        theValues = new Object[theNumValues];
    }

    /**
     * Obtain the field definitions.
     * @return the field definitions
     */
    public MetisFieldSetDef getFields() {
        return theFields;
    }

    /**
     * Obtain the underlying item.
     * @return the item
     */
    protected MetisFieldVersionedItem getItem() {
        return theItem;
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
    public MetisFieldVersionValues cloneIt() {
        /* Create the valueSet and initialise to existing values */
        final MetisFieldVersionValues mySet = new MetisFieldVersionValues(theItem);
        mySet.copyFrom(this);
        return mySet;
    }

    /**
     * Initialise values from a previous set.
     * @param pPrevious the previous valueSet
     */
    public void copyFrom(final MetisFieldVersionValues pPrevious) {
        /* Copy deletion flag */
        isDeletion = pPrevious.isDeletion();

        /* Determine the copyLength */
        int myCopyLen = pPrevious.theNumValues;
        if (myCopyLen > theNumValues) {
            myCopyLen = theNumValues;
        }

        /* Copy values */
        if (myCopyLen > 0) {
            System.arraycopy(pPrevious.theValues, 0, theValues, 0, myCopyLen);
        }
    }

    /**
     * Set the value.
     * @param pFieldId the fieldId
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setValue(final MetisDataFieldId pFieldId,
                         final Object pValue) throws OceanusException {
        final MetisFieldDef myField = theFields.getField(pFieldId);
        setValue(myField, pValue);
    }

    /**
     * Set the value.
     * @param pField the field
     * @param pValue the value
     * @throws OceanusException on error
     */
    public void setValue(final MetisFieldDef pField,
                         final Object pValue) throws OceanusException {
        /* Reject if not in valueSet */
        if (!(pField instanceof MetisFieldVersionedDef)) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Check value type */
        checkValueType(pField, pValue);

        /* Store the value */
        theValues[((MetisFieldVersionedDef) pField).getIndex()] = pValue;
    }

    /**
     * Set the value.
     * @param pFieldId the fieldId
     * @param pValue the value
     */
    public void setUncheckedValue(final MetisDataFieldId pFieldId,
                                  final Object pValue) {
        final MetisFieldDef myField = theFields.getField(pFieldId);
        setUncheckedValue(myField, pValue);
    }

    /**
     * Set the unchecked value.
     * @param pField the field
     * @param pValue the value
     */
    public void setUncheckedValue(final MetisFieldDef pField,
                                  final Object pValue) {
        /* Reject if not in valueSet */
        if (!(pField instanceof MetisFieldVersionedDef)) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Store the value */
        theValues[((MetisFieldVersionedDef) pField).getIndex()] = pValue;
    }

    /**
     * Get the value.
     * @param pFieldId the fieldId
     * @return the value
     */
    public Object getValue(final MetisDataFieldId pFieldId) {
        final MetisFieldDef myField = theFields.getField(pFieldId);
        return getValue(myField);
    }

    /**
     * Get the value.
     * @param pField the field
     * @return the value
     */
    public Object getValue(final MetisFieldDef pField) {
        /* Reject if not in valueSet */
        if (!(pField instanceof MetisFieldVersionedDef)) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Return the value */
        return theValues[((MetisFieldVersionedDef) pField).getIndex()];
    }

    /**
     * Get the value.
     * @param <X> the required type
     * @param pFieldId the fieldId
     * @param pClazz the class
     * @return the value
     */
    public <X> X getValue(final MetisDataFieldId pFieldId,
                          final Class<X> pClazz) {
        final MetisFieldDef myField = theFields.getField(pFieldId);
        return getValue(myField, pClazz);
    }

    /**
     * Get the value as an object type.
     * @param <X> the required type
     * @param pField the field
     * @param pClazz the class
     * @return the value
     */
    public <X> X getValue(final MetisFieldDef pField,
                          final Class<X> pClazz) {
        /* Access the value */
        final Object myValue = getValue(pField);

        /* Return the value */
        return pClazz.cast(myValue);
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
        final MetisFieldVersionValues mySet = (MetisFieldVersionValues) pThat;
        final Object[] myObj = mySet.theValues;

        /* Check for deletion flag and # of values */
        if (isDeletion != mySet.isDeletion
            || theNumValues != mySet.theNumValues) {
            return false;
        }

        /* Loop through the values */
        final Iterator<MetisFieldDef> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-versioned fields */
            final MetisFieldDef myField = myIterator.next();
            if (!(myField instanceof MetisFieldVersionedDef)
                    || !((MetisFieldVersionedDef) myField).isEquality()) {
                continue;
            }

            /* Not equal if the value is different */
            final int iIndex = ((MetisFieldVersionedDef) myField).getIndex();
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
        final Iterator<MetisFieldDef> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-versioned fields */
            final MetisFieldDef myField = myIterator.next();
            if (!(myField instanceof MetisFieldVersionedDef)
                    || !((MetisFieldVersionedDef) myField).isEquality()) {
                continue;
            }

            /* Adjust existing hash */
            iHashCode *= MetisFieldSet.HASH_PRIME;

            /* Access value and add hash if non-null */
            final int iIndex = ((MetisFieldVersionedDef) myField).getIndex();
            final Object o = theValues[iIndex];
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
    public MetisDataDifference differs(final MetisFieldVersionValues pOriginal) {
        boolean isSecureDiff = false;

        /* Access the test values */
        final Object[] myObj = pOriginal.theValues;

        /* Check for deletion flag and # of values */
        if (isDeletion != pOriginal.isDeletion
            || theNumValues != pOriginal.theNumValues) {
            return MetisDataDifference.DIFFERENT;
        }

        /* Loop through the values */
        final Iterator<MetisFieldDef> myIterator = theFields.fieldIterator();
        while (myIterator.hasNext()) {
            /* Ignore non-equality and non-versioned fields */
            final MetisFieldDef myField = myIterator.next();
            if (!(myField instanceof MetisFieldVersionedDef)
                 || !((MetisFieldVersionedDef) myField).isEquality()) {
                continue;
            }

            /* Check the field */
            final int iIndex = ((MetisFieldVersionedDef) myField).getIndex();
            final MetisDataDifference myDiff = MetisDataDifference.difference(theValues[iIndex], myObj[iIndex]);
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
     * @param pFieldId the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisDataFieldId pFieldId,
                                            final MetisFieldVersionValues pOriginal) {
        final MetisFieldDef myField = theFields.getField(pFieldId);
        return fieldChanged(myField, pOriginal);
    }

    /**
     * Check for a difference in a particular field.
     * @param pField the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisFieldDef pField,
                                            final MetisFieldVersionValues pOriginal) {
        /* No difference if field does not exist, is not-equality or is not versioned */
        if (!(pField instanceof MetisFieldVersionedDef)
                || !((MetisFieldVersionedDef) pField).isEquality()) {
            return MetisDataDifference.IDENTICAL;
        }

        /* Determine the difference */
        final int iIndex = ((MetisFieldVersionedDef) pField).getIndex();
        return MetisDataDifference.difference(theValues[iIndex], pOriginal.theValues[iIndex]);
    }

    /**
     * Check the value.
     * @param pField the field
     * @param pValue the value
     * @throws OceanusException on error
     */
    protected void checkValueType(final MetisFieldDef pField,
                                  final Object pValue) throws OceanusException {
        /* Null/String is always allowed */
        if (pValue == null
            || pValue instanceof String) {
            return;
        }

        /* Integer is allowed for Link type */
        final MetisDataType myDataType = pField.getDataType();
        if (MetisDataType.LINK.equals(myDataType)
            && pValue instanceof Integer) {
            return;
        }

        /* Long is allowed for LinkPair type */
        if (MetisDataType.LINKPAIR.equals(myDataType)
                && pValue instanceof Long) {
            return;
        }

        /* Check expected dataType */
        final Class<?> myClass = myDataType.getDataTypeClass();
        final boolean bAllowed = myClass == null || myClass.isInstance(pValue);

        /* If we are not allowed */
        if (!bAllowed) {
            throw new MetisDataException(ERROR_VALUETYPE);
        }
    }
}
