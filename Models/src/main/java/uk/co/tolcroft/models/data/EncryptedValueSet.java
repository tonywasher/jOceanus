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
package uk.co.tolcroft.models.data;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ReportFields.ReportField;
import uk.co.tolcroft.models.data.EncryptedData.EncryptedField;
import uk.co.tolcroft.models.data.EncryptedData.EncryptionGenerator;

public class EncryptedValueSet<T extends EncryptedItem<T>> extends ValueSet<T> {
    /**
     * The item to which the valueSet belongs
     */
    private final T theItem;

    /**
     * Constructor
     * @param pItem
     */
    public EncryptedValueSet(T pItem) {
        super(pItem);
        theItem = pItem;
    }

    /**
     * Clone this EncryptedValueSet
     * @return the cloned set
     */
    @Override
    public EncryptedValueSet<T> cloneIt() {
        /* Create the valueSet and initialise to existing values */
        EncryptedValueSet<T> mySet = new EncryptedValueSet<T>(getItem());
        mySet.copyFrom(this);
        return mySet;
    }

    @Override
    public void declareActive() {
        theItem.declareValues(this);
    }

    /**
     * Get EncryptedField bytes
     * @param pField the field
     * @return the bytes
     */
    public byte[] getEncryptedFieldBytes(ReportField pField) {
        /* Access the field and return null if required */
        Object myObject = getValue(pField);
        if (myObject == null)
            return null;

        /* Handle bad usage */
        if (!EncryptedField.class.isInstance(myObject))
            throw new IllegalArgumentException("Encrypted access for non-encrypted field " + pField.getName());

        /* Return the bytes */
        EncryptedField<?> myField = (EncryptedField<?>) myObject;
        return myField.getBytes();
    }

    /**
     * Get EncryptedField value
     * @param <X> the field value class
     * @param pField the field
     * @param pClass the class
     * @return the value
     */
    public <X> X getEncryptedFieldValue(ReportField pField,
                                        Class<X> pClass) {
        /* Access the field and return null if required */
        Object myObject = getValue(pField);
        if (myObject == null)
            return null;

        /* Handle bad usage */
        if (!EncryptedField.class.isInstance(myObject))
            throw new IllegalArgumentException("Encrypted access for non-encrypted field " + pField.getName());

        /* Return the value */
        EncryptedField<?> myField = (EncryptedField<?>) myObject;
        Object myValue = myField.getValue();
        return pClass.cast(myValue);
    }

    /**
     * Update security for the values
     * @param pGenerator the generator
     * @throws ModelException
     */
    public void updateSecurity(EncryptionGenerator pGenerator) throws ModelException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if (myValue == null)
                continue;
            if (!EncryptedField.class.isInstance(myValue))
                continue;

            /* Update Security */
            EncryptedField<?> myField = (EncryptedField<?>) myValue;
            myValues[i] = pGenerator.encryptValue(myField, myField.getValue());
        }
    }

    /**
     * Adopt security for the values
     * @param pGenerator the generator
     * @param pBaseValues the base values
     * @throws ModelException
     */
    public void adoptSecurity(EncryptionGenerator pGenerator,
                              EncryptedValueSet<T> pBaseValues) throws ModelException {
        /* Access the values */
        Object[] myValues = getValues();
        int iLen = myValues.length;
        Object[] myBaseValues = (pBaseValues == null) ? null : pBaseValues.getValues();

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            Object myValue = myValues[i];
            if (myValue == null)
                continue;
            if (!EncryptedField.class.isInstance(myValue))
                continue;

            /* Access relevant fields */
            EncryptedField<?> myField = (EncryptedField<?>) myValue;
            EncryptedField<?> myBaseField = null;
            Object myBaseObj = (myBaseValues == null) ? null : myBaseValues[i];
            if (EncryptedField.class.isInstance(myBaseObj))
                myBaseField = (EncryptedField<?>) myBaseObj;

            /* Adopt encryption */
            pGenerator.adoptEncryption(myField, myBaseField);
        }
    }
}
