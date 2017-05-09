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
package net.sourceforge.joceanus.jmetis.lethe.data;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Encrypted ValueSet class.
 */
public class MetisEncryptedValueSet
        extends MetisValueSet {
    /**
     * Field access error text.
     */
    private static final String ERROR_ACCESS = "Encrypted access for non-encrypted field ";

    /**
     * Constructor.
     * @param pItem the item
     */
    public MetisEncryptedValueSet(final MetisDataValues pItem) {
        super(pItem);
    }

    @Override
    public MetisEncryptedValueSet cloneIt() {
        /* Create the valueSet and initialise to existing values */
        MetisEncryptedValueSet mySet = new MetisEncryptedValueSet(getItem());
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
    public byte[] getEncryptedFieldBytes(final MetisField pField) {
        /* Access the field and return null if required */
        Object myObject = getValue(pField);
        if (myObject == null) {
            return null;
        }

        /* Handle bad usage */
        if (!MetisEncryptedField.class.isInstance(myObject)) {
            throw new IllegalArgumentException(ERROR_ACCESS
                                               + pField.getName());
        }

        /* Return the bytes */
        MetisEncryptedField<?> myField = (MetisEncryptedField<?>) myObject;
        return myField.getBytes();
    }

    /**
     * Get EncryptedField value.
     * @param <X> the field value class
     * @param pField the field
     * @param pClass the class
     * @return the value
     */
    public <X> X getEncryptedFieldValue(final MetisField pField,
                                        final Class<X> pClass) {
        /* Access the field and return null if required */
        Object myObject = getValue(pField);
        if (myObject == null) {
            return null;
        }

        /* Handle bad usage */
        if (!MetisEncryptedField.class.isInstance(myObject)) {
            throw new IllegalArgumentException(ERROR_ACCESS
                                               + pField.getName());
        }

        /* Return the value */
        MetisEncryptedField<?> myField = (MetisEncryptedField<?>) myObject;
        Object myValue = myField.getValue();
        return pClass.cast(myValue);
    }

    /**
     * Update security for the values.
     * @param pGenerator the generator
     * @throws OceanusException on error
     */
    public void updateSecurity(final MetisEncryptionGenerator pGenerator) throws OceanusException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if ((myValue == null)
                || (!MetisEncryptedField.class.isInstance(myValue))) {
                continue;
            }

            /* Update Security */
            MetisEncryptedField<?> myField = (MetisEncryptedField<?>) myValue;
            myValues[i] = pGenerator.encryptValue(myField, myField.getValue());
        }
    }

    /**
     * Adopt security for the values.
     * @param pGenerator the generator
     * @param pBaseValues the base values
     * @throws OceanusException on error
     */
    public void adoptSecurity(final MetisEncryptionGenerator pGenerator,
                              final MetisEncryptedValueSet pBaseValues) throws OceanusException {
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
                || (!MetisEncryptedField.class.isInstance(myValue))) {
                continue;
            }

            /* Access relevant fields */
            MetisEncryptedField<?> myField = (MetisEncryptedField<?>) myValue;
            MetisEncryptedField<?> myBaseField = null;
            Object myBaseObj = (myBaseValues == null)
                                                     ? null
                                                     : myBaseValues[i];
            if (MetisEncryptedField.class.isInstance(myBaseObj)) {
                myBaseField = (MetisEncryptedField<?>) myBaseObj;
            }

            /* Adopt encryption */
            pGenerator.adoptEncryption(myField, myBaseField);
        }
    }
}