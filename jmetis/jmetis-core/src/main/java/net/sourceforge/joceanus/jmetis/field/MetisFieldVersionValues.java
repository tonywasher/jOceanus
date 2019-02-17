/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmetis.field;

import java.util.Arrays;
import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference.MetisDataDiffers;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    private static final String ERROR_NOTVERSIONED = "Field is not versioned";

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

        /* Create the values array */
        final Object[] mySrc = pPrevious.theValues;
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
     * @throws OceanusException on error
     */
    public void setValue(final MetisFieldDef pField,
                         final Object pValue) throws OceanusException {
        /* Reject if not in valueSet */
        if (!pField.getStorage().isVersioned()) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Check value type */
        checkValueType(pField, pValue);

        /* Store the value */
        theValues[pField.getIndex()] = pValue;
    }

    /**
     * Set the unchecked value.
     * @param pField the field
     * @param pValue the value
     */
    public void setUncheckedValue(final MetisFieldDef pField,
                                  final Object pValue) {
        /* Reject if not in valueSet */
        if (!pField.getStorage().isVersioned()) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Store the value */
        theValues[pField.getIndex()] = pValue;
    }

    /**
     * Get the value.
     * @param pField the field
     * @return the value
     */
    public Object getValue(final MetisFieldDef pField) {
        /* Reject if not in valueSet */
        if (!pField.getStorage().isVersioned()) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* Return the value */
        return theValues[pField.getIndex()];
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
        Object myValue = getValue(pField);

        /* If this is an encrypted value */
        if (myValue instanceof MetisFieldEncryptedValue) {
            /* Access correct part of pair */
            final MetisFieldEncryptedValue myEncrypted = (MetisFieldEncryptedValue) myValue;
            myValue = byte[].class.equals(pClazz)
                                                  ? myEncrypted.getEncryption()
                                                  : myEncrypted.getValue(pClazz);
        }

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
            if (!myField.getEquality().isEquality()
                || !myField.getStorage().isVersioned()) {
                continue;
            }

            /* Not equal if the value is different */
            final int iIndex = myField.getIndex();
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
            if (!myField.getEquality().isEquality()
                || !myField.getStorage().isVersioned()) {
                continue;
            }

            /* Adjust existing hash */
            iHashCode *= MetisFieldSet.HASH_PRIME;

            /* Access value and add hash if non-null */
            final Object o = theValues[myField.getIndex()];
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
            if (!myField.getEquality().isEquality()
                || !myField.getStorage().isVersioned()) {
                continue;
            }

            /* Check the field */
            final int iIndex = myField.getIndex();
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
     * @param pField the field to check for differences
     * @param pOriginal the original value set
     * @return the difference
     */
    public MetisDataDifference fieldChanged(final MetisFieldDef pField,
                                            final MetisFieldVersionValues pOriginal) {
        /* No difference if field does not exist, is not-equality or is not versioned */
        if (pField == null
            || !pField.getEquality().isEquality()
            || !pField.getStorage().isVersioned()) {
            return MetisDataDifference.IDENTICAL;
        }

        /* Determine the difference */
        final int iIndex = pField.getIndex();
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

        /* Check expected dataType */
        final Class<?> myClass = myDataType.getDataTypeClass();
        final boolean bAllowed = myClass == null || myClass.isInstance(pValue);

        /* If we are not allowed */
        if (!bAllowed) {
            throw new MetisDataException(ERROR_VALUETYPE);
        }
    }

    /**
     * EncryptedValue.
     */
    public static final class MetisFieldEncryptedValue
            implements MetisDataObjectFormat, MetisDataDiffers {
        /**
         * The value.
         */
        private final Object theValue;

        /**
         * The encryption.
         */
        private final byte[] theEncryption;

        /**
         * Constructor.
         * @param pValue the value
         * @param pEncryption the encryption
         */
        protected MetisFieldEncryptedValue(final Object pValue,
                                           final byte[] pEncryption) {
            theValue = pValue;
            theEncryption = pEncryption;
        }

        /**
         * Obtain the value.
         * @return the value
         */
        public Object getValue() {
            return theValue;
        }

        /**
         * Get the value as an object type.
         * @param <X> the required type
         * @param pClazz the class
         * @return the value
         */
        public <X> X getValue(final Class<X> pClazz) {
            return pClazz.cast(theValue);
        }

        /**
         * Obtain the encryption.
         * @return the encryption
         */
        public byte[] getEncryption() {
            return theEncryption;
        }

        @Override
        public String toString() {
            return theValue.toString();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return pFormatter.formatObject(theValue);
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

            /* Make sure that the object is the same class */
            if (!(pThat instanceof MetisFieldEncryptedValue)) {
                return false;
            }

            /* Access the target field */
            final MetisFieldEncryptedValue myThat = (MetisFieldEncryptedValue) pThat;

            /* Check differences */
            if (MetisDataDifference.difference(getValue(), myThat.getValue()).isDifferent()) {
                return false;
            }

            /* Check encryption */
            return Arrays.equals(getEncryption(), myThat.getEncryption());
        }

        @Override
        public int hashCode() {
            /* Calculate hash allowing for field that has not been encrypted yet */
            int myHashCode = MetisFieldSet.HASH_PRIME
                             * getValue().hashCode();
            myHashCode += Arrays.hashCode(getEncryption());
            return myHashCode;
        }

        @Override
        public MetisDataDifference differs(final Object pThat) {
            /* Reject if null */
            if (pThat == null) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Reject if wrong class */
            if (!(pThat instanceof MetisFieldEncryptedValue)) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Access as correct class */
            final MetisFieldEncryptedValue myThat = (MetisFieldEncryptedValue) pThat;

            /* Compare value */
            if (MetisDataDifference.difference(getValue(), myThat.getValue()).isDifferent()) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Compare Encrypted value */
            if (!Arrays.equals(getEncryption(), myThat.getEncryption())) {
                return MetisDataDifference.SECURITY;
            }

            /* Item is the Same */
            return MetisDataDifference.IDENTICAL;
        }
    }
}
