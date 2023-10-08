/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.data;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataValues;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jprometheus.atlas.field.PrometheusEncryptedPair;
import net.sourceforge.joceanus.jprometheus.atlas.field.PrometheusFieldGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Encrypted ValueSet class.
 */
public class PrometheusEncryptedValueSet
        extends MetisValueSet {
    /**
     * Field access error text.
     */
    private static final String ERROR_ACCESS = "Encrypted access for non-encrypted field ";

    /**
     * Constructor.
     * @param pItem the item
     */
    public PrometheusEncryptedValueSet(final MetisDataValues pItem) {
        super(pItem);
    }

    @Override
    public PrometheusEncryptedValueSet cloneIt() {
        /* Create the valueSet and initialise to existing values */
        final PrometheusEncryptedValueSet mySet = new PrometheusEncryptedValueSet(getItem());
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
    public byte[] getEncryptedFieldBytes(final MetisLetheField pField) {
        /* Access the field and return null if required */
        final Object myObject = getValue(pField);
        if (myObject == null) {
            return null;
        }

        /* Handle bad usage */
        if (!(myObject instanceof PrometheusEncryptedPair)) {
            throw new IllegalArgumentException(ERROR_ACCESS
                    + pField.getName());
        }

        /* Return the bytes */
        final PrometheusEncryptedPair myField = (PrometheusEncryptedPair) myObject;
        return myField.getBytes();
    }

    /**
     * Get EncryptedField value.
     * @param <X> the field value class
     * @param pField the field
     * @param pClass the class
     * @return the value
     */
    public <X> X getEncryptedFieldValue(final MetisLetheField pField,
                                        final Class<X> pClass) {
        /* Access the field and return null if required */
        final Object myObject = getValue(pField);
        if (myObject == null) {
            return null;
        }

        /* Handle bad usage */
        if (!(myObject instanceof PrometheusEncryptedPair)) {
            throw new IllegalArgumentException(ERROR_ACCESS
                    + pField.getName());
        }

        /* Return the value */
        final PrometheusEncryptedPair myField = (PrometheusEncryptedPair) myObject;
        final Object myValue = myField.getValue();
        return pClass.cast(myValue);
    }

    /**
     * Update security for the values.
     * @param pGenerator the generator
     * @throws OceanusException on error
     */
    public void updateSecurity(final PrometheusFieldGenerator pGenerator) throws OceanusException {
        /* Access the values */
        final Object[] myValues = getValues();
        final int iLen = myValues.length;

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            final Object myValue = myValues[i];
            if (!(myValue instanceof PrometheusEncryptedPair)) {
                continue;
            }

            /* Update Security */
            final PrometheusEncryptedPair myField = (PrometheusEncryptedPair) myValue;
            myValues[i] = pGenerator.encryptValue(myField, myField.getValue());
        }
    }

    /**
     * Adopt security for the values.
     * @param pGenerator the generator
     * @param pBaseValues the base values
     * @throws OceanusException on error
     */
    public void adoptSecurity(final PrometheusFieldGenerator pGenerator,
                              final PrometheusEncryptedValueSet pBaseValues) throws OceanusException {
        /* Access the values */
        final Object[] myValues = getValues();
        final int iLen = myValues.length;
        final Object[] myBaseValues = (pBaseValues == null)
                ? null
                : pBaseValues.getValues();

        /* Loop through the values */
        for (int i = 0; i < iLen; i++) {
            /* Skip null and non-encrypted fields */
            final Object myValue = myValues[i];
            if (!(myValue instanceof PrometheusEncryptedPair)) {
                continue;
            }

            /* Access relevant fields */
            final PrometheusEncryptedPair myField = (PrometheusEncryptedPair) myValue;
            PrometheusEncryptedPair myBaseField = null;
            final Object myBaseObj = myBaseValues == null
                    ? null
                    : myBaseValues[i];
            if (myBaseObj instanceof PrometheusEncryptedPair) {
                myBaseField = (PrometheusEncryptedPair) myBaseObj;
            }

            /* Adopt encryption */
            pGenerator.adoptEncryption(myField, myBaseField);
        }
    }
}