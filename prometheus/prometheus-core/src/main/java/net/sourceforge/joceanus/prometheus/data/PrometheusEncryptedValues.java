/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.prometheus.data;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldVersionedDef;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionValues;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.util.Iterator;

/**
 * Prometheus Set of versioned Values.
 */
public class PrometheusEncryptedValues
        extends MetisFieldVersionValues {
    /**
     * The Encryptor.
     */
    private PrometheusEncryptor theEncryptor;

    /**
     * Constructor.
     * @param pItem the associated item
     */
    public PrometheusEncryptedValues(final PrometheusEncryptedDataItem pItem) {
        super(pItem);
    }

    @Override
    public PrometheusEncryptedValues cloneIt() {
        /* Create the valueSet and initialise to existing values */
        final PrometheusEncryptedValues mySet = new PrometheusEncryptedValues(getItem());
        mySet.copyFrom(this);
        return mySet;
    }

    @Override
    protected PrometheusEncryptedDataItem getItem() {
        return (PrometheusEncryptedDataItem) super.getItem();
    }

    @Override
    public void copyFrom(final MetisFieldVersionValues pPrevious) {
        /* Perform main copy */
        super.copyFrom(pPrevious);

        /* Copy the keySet */
        if (pPrevious instanceof PrometheusEncryptedValues) {
            final PrometheusEncryptedValues myPrevious = (PrometheusEncryptedValues) pPrevious;
            theEncryptor = myPrevious.theEncryptor;
        }
    }

    @Override
    public Object getValue(final MetisFieldDef pField) {
        /* Access the underlying object */
        final Object myValue = super.getValue(pField);

        /* If this is an encrypted value */
        return myValue instanceof PrometheusEncryptedPair
                ? ((PrometheusEncryptedPair) myValue).getValue()
                : myValue;
    }

    /**
     * Obtain the encrypted bytes.
     * @param pFieldId the fieldId
     * @return the encrypted bytes
     */
    public byte[] getEncryptedBytes(final MetisDataFieldId pFieldId) {
        /* Access the underlying object */
        final MetisFieldDef myField = getItem().getDataFieldSet().getField(pFieldId);
        return getEncryptedBytes(myField);
    }

    /**
     * Obtain the encrypted bytes.
     * @param pField the field
     * @return the encrypted bytes
     */
    public byte[] getEncryptedBytes(final MetisFieldDef pField) {
        /* Access the underlying object */
        final Object myValue = super.getValue(pField);

        /* If this is an encrypted value */
        return myValue instanceof PrometheusEncryptedPair
                ? ((PrometheusEncryptedPair) myValue).getBytes()
                : null;
    }

    /**
     * Obtain the encrypted pair.
     * @param pFieldId the fieldId
     * @return the encrypted pair
     */
    public PrometheusEncryptedPair getEncryptedPair(final MetisDataFieldId pFieldId) {
        /* Access the underlying object */
        final MetisFieldDef myField = getItem().getDataFieldSet().getField(pFieldId);
        return getEncryptedPair(myField);
    }

    /**
     * Obtain the encrypted pair.
     * @param pField the field
     * @return the encrypted pair
     */
    public PrometheusEncryptedPair getEncryptedPair(final MetisFieldDef pField) {
        /* Access the underlying object */
        final Object myValue = super.getValue(pField);

        /* If this is an encrypted value */
        return myValue instanceof PrometheusEncryptedPair
                ? (PrometheusEncryptedPair) myValue
                : null;
    }

    @Override
    public void setValue(final MetisFieldDef pField,
                         final Object pValue) throws OceanusException {
        /* Reject if not in valueSet */
        if (!(pField instanceof MetisFieldVersionedDef)) {
            throw new IllegalArgumentException(ERROR_NOTVERSIONED);
        }

        /* check the value type */
        checkValueType(pField, pValue);

        /* If this is an encrypted field */
        Object myValue = pValue;
        if (pField instanceof PrometheusEncryptedField
                && theEncryptor != null
                && myValue != null) {
            /* Encrypt the value */
            myValue = theEncryptor.encryptValue(pValue, pField);
        }

        /* Store the value */
        setUncheckedValue(pField, myValue);
    }

    /**
     * Set the encryptor.
     * @param pEncryptor the encryptor
     * @throws OceanusException on error
     */
    void setTheEncryptor(final PrometheusEncryptor pEncryptor) throws OceanusException {
        /* Record the encryptor */
        theEncryptor = pEncryptor;
    }

    /**
     * Set the encryptor.
     * @param pEncryptor the encryptor
     * @throws OceanusException on error
     */
    void setEncryptor(final PrometheusEncryptor pEncryptor) throws OceanusException {
        /* Record the encryptor */
        theEncryptor = pEncryptor;

        /* Loop through the fields */
        final MetisFieldSetDef myFieldSet = getFields();
        final Iterator<MetisFieldDef> myIterator = myFieldSet.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* Ignore non-encrypted fields */
            if (!(myField instanceof PrometheusEncryptedField)) {
                continue;
            }

            /* Access the value */
            final Object myValue = getValue(myField);

            /* If this is a byte array */
            if (myValue instanceof byte[]) {
                /* Decrypt the value */
                setUncheckedValue(myField, theEncryptor.decryptValue((byte[]) myValue, myField));
            } else if (myValue != null) {
                /* Encrypt the value */
                setUncheckedValue(myField, theEncryptor.encryptValue(myValue, myField));
            }
        }
    }

    /**
     * Update security for the values.
     * @throws OceanusException on error
     */
    public void updateSecurity() throws OceanusException {
        /* Loop through the fields */
        final MetisFieldSetDef myFieldSet = getFields();
        final Iterator<MetisFieldDef> myIterator = myFieldSet.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* Ignore non-encrypted fields */
            if (!(myField instanceof PrometheusEncryptedField)) {
                continue;
            }

            /* Access the value */
            final Object myValue = getValue(myField);

            /* If this is a byte array */
            if (theEncryptor == null) {
                int i = 0;
            }
            /* Encrypt the value */
            setUncheckedValue(myField, theEncryptor.encryptValue(myValue, myField));
        }
    }

    /**
     * Adopt security for the values.
     * @param pBaseValues the base values
     * @throws OceanusException on error
     */
    public void adoptSecurity(final PrometheusEncryptedValues pBaseValues) throws OceanusException {
        /* Loop through the fields */
        final MetisFieldSetDef myFieldSet = getFields();
        final Iterator<MetisFieldDef> myIterator = myFieldSet.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* Ignore non-encrypted fields */
            if (!(myField instanceof PrometheusEncryptedField)) {
                continue;
            }
            final PrometheusEncryptedPair myValue = getEncryptedPair(myField);
            if (myValue == null) {
                continue;
            }

            /* Access the base object */
            Object myBaseObj = pBaseValues == null
                    ? null
                    : pBaseValues.getValue(myField);
            if (!(myBaseObj instanceof PrometheusEncryptedPair)) {
                myBaseObj = null;
            }

            /* Adopt encryption */
            theEncryptor.adoptEncryption(myValue, (PrometheusEncryptedPair) myBaseObj);
        }
    }
}
