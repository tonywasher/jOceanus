/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jGordianKnot;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataValues;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedField;

/**
 * Encrypted ValueSet class.
 */
public class EncryptedValueSet extends ValueSet {
    /**
     * Constructor.
     * @param pItem the item
     */
    public EncryptedValueSet(final JDataValues pItem) {
        super(pItem);
    }

    @Override
    public EncryptedValueSet cloneIt() {
        /* Create the valueSet and initialise to existing values */
        EncryptedValueSet mySet = new EncryptedValueSet(getItem());
        mySet.copyFrom(this);
        return mySet;
    }

    @Override
    public void declareActive() {
        getItem().declareValues(this);
    }

    /**
     * Get EncryptedField bytes.
     * @param pField the field
     * @return the bytes
     */
    public byte[] getEncryptedFieldBytes(final JDataField pField) {
        /* Access the field and return null if required */
        Object myObject = getValue(pField);
        if (myObject == null) {
            return null;
        }

        /* Handle bad usage */
        if (!EncryptedField.class.isInstance(myObject)) {
            throw new IllegalArgumentException("Encrypted access for non-encrypted field " + pField.getName());
        }

        /* Return the bytes */
        EncryptedField<?> myField = (EncryptedField<?>) myObject;
        return myField.getBytes();
    }

    /**
     * Get EncryptedField value.
     * @param <X> the field value class
     * @param pField the field
     * @param pClass the class
     * @return the value
     */
    public <X> X getEncryptedFieldValue(final JDataField pField,
                                        final Class<X> pClass) {
        /* Access the field and return null if required */
        Object myObject = getValue(pField);
        if (myObject == null) {
            return null;
        }

        /* Handle bad usage */
        if (!EncryptedField.class.isInstance(myObject)) {
            throw new IllegalArgumentException("Encrypted access for non-encrypted field " + pField.getName());
        }

        /* Return the value */
        EncryptedField<?> myField = (EncryptedField<?>) myObject;
        Object myValue = myField.getValue();
        return pClass.cast(myValue);
    }

    /**
     * Update security for the values.
     * @param pGenerator the generator
     * @throws JDataException on error
     */
    public void updateSecurity(final EncryptionGenerator pGenerator) throws JDataException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if ((myValue == null) || (!EncryptedField.class.isInstance(myValue))) {
                continue;
            }

            /* Update Security */
            EncryptedField<?> myField = (EncryptedField<?>) myValue;
            myValues[i] = pGenerator.encryptValue(myField, myField.getValue());
        }
    }

    /**
     * Adopt security for the values.
     * @param pGenerator the generator
     * @param pBaseValues the base values
     * @throws JDataException on error
     */
    public void adoptSecurity(final EncryptionGenerator pGenerator,
                              final EncryptedValueSet pBaseValues) throws JDataException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;
        Object[] myBaseValues = (pBaseValues == null) ? null : pBaseValues.getValues();

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if ((myValue == null) || (!EncryptedField.class.isInstance(myValue))) {
                continue;
            }

            /* Access relevant fields */
            EncryptedField<?> myField = (EncryptedField<?>) myValue;
            EncryptedField<?> myBaseField = null;
            Object myBaseObj = (myBaseValues == null) ? null : myBaseValues[i];
            if (EncryptedField.class.isInstance(myBaseObj)) {
                myBaseField = (EncryptedField<?>) myBaseObj;
            }

            /* Adopt encryption */
            pGenerator.adoptEncryption(myField, myBaseField);
        }
    }
}
