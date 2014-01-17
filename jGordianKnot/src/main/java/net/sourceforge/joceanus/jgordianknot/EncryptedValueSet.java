/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot;

import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataValues;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jgordianknot.EncryptedData.EncryptedField;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Encrypted ValueSet class.
 */
public class EncryptedValueSet
        extends ValueSet {
    /**
     * Field access error text.
     */
    private static final String ERROR_ACCESS = "Encrypted access for non-encrypted field ";

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
            throw new IllegalArgumentException(ERROR_ACCESS
                                               + pField.getName());
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
            throw new IllegalArgumentException(ERROR_ACCESS
                                               + pField.getName());
        }

        /* Return the value */
        EncryptedField<?> myField = (EncryptedField<?>) myObject;
        Object myValue = myField.getValue();
        return pClass.cast(myValue);
    }

    /**
     * Update security for the values.
     * @param pGenerator the generator
     * @throws JOceanusException on error
     */
    public void updateSecurity(final EncryptionGenerator pGenerator) throws JOceanusException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if ((myValue == null)
                || (!EncryptedField.class.isInstance(myValue))) {
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
     * @throws JOceanusException on error
     */
    public void adoptSecurity(final EncryptionGenerator pGenerator,
                              final EncryptedValueSet pBaseValues) throws JOceanusException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;
        Object[] myBaseValues = (pBaseValues == null)
                ? null
                : pBaseValues.getValues();

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if ((myValue == null)
                || (!EncryptedField.class.isInstance(myValue))) {
                continue;
            }

            /* Access relevant fields */
            EncryptedField<?> myField = (EncryptedField<?>) myValue;
            EncryptedField<?> myBaseField = null;
            Object myBaseObj = (myBaseValues == null)
                    ? null
                    : myBaseValues[i];
            if (EncryptedField.class.isInstance(myBaseObj)) {
                myBaseField = (EncryptedField<?>) myBaseObj;
            }

            /* Adopt encryption */
            pGenerator.adoptEncryption(myField, myBaseField);
        }
    }
}
