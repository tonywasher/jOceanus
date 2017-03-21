/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.atlas.data;

import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference.MetisDataDiffers;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataVersionValues;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;

/**
 * Prometheus Set of versioned Values.
 */
public class PrometheusDataValueSet
        extends MetisDataVersionValues {
    /**
     * The Hash prime.
     */
    private static final int HASH_PRIME = 19;

    /**
     * The KeySet.
     */
    private GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pItem the associated item
     */
    public PrometheusDataValueSet(final MetisDataVersionedItem pItem) {
        super(pItem);
    }

    @Override
    public PrometheusDataValueSet cloneIt() {
        /* Create the valueSet and initialise to existing values */
        PrometheusDataValueSet mySet = new PrometheusDataValueSet(getItem());
        mySet.copyFrom(this);
        return mySet;
    }

    @Override
    public void copyFrom(final MetisDataVersionValues pPrevious) {
        /* Perform main copy */
        super.copyFrom(pPrevious);

        /* Copy the keySet */
        if (pPrevious instanceof PrometheusDataValueSet) {
            PrometheusDataValueSet myPrevious = (PrometheusDataValueSet) pPrevious;
            theKeySet = myPrevious.theKeySet;
        }
    }

    @Override
    protected void checkValueType(final MetisDataField pField,
                                  final Object pValue) {
        /* Allow byteArray */
        if (pValue instanceof byte[]) {
            return;
        }

        /* Allow EncryptedField if we have a keySet */
        if (pValue instanceof PrometheusEncryptedField
            && theKeySet != null) {
            return;
        }

        /* Pass on */
        super.checkValueType(pField, pValue);
    }

    /**
     * The generic encrypted object class.
     * @param <T> the data type
     */
    public static final class PrometheusEncryptedField<T>
            implements MetisDataFieldItem, MetisDataDiffers {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(PrometheusEncryptedField.class.getSimpleName());

        /**
         * Value Field Id.
         */
        private static final MetisDataField FIELD_VALUE = FIELD_DEFS.declareLocalField("Value");

        /**
         * Encrypted Field Id.
         */
        private static final MetisDataField FIELD_ENCRYPTED = FIELD_DEFS.declareLocalField("Encrypted");

        /**
         * Value.
         */
        private final T theValue;

        /**
         * Encrypted value.
         */
        private final byte[] theEncrypted;

        /**
         * Constructor.
         * @param pValue the value
         * @param pEncrypted the encrypted value
         */
        private PrometheusEncryptedField(final T pValue,
                                         final byte[] pEncrypted) {
            theValue = pValue;
            theEncrypted = pEncrypted;
        }

        /**
         * Obtain the encrypted value.
         * @return the encrypted value
         */
        public byte[] getEncryption() {
            return (theEncrypted == null)
                                          ? null
                                          : Arrays.copyOf(theEncrypted, theEncrypted.length);
        }

        /**
         * Obtain the value.
         * @return the value
         */
        public T getValue() {
            return theValue;
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
            if (pThat.getClass() != this.getClass()) {
                return false;
            }

            /* Access the target field */
            PrometheusEncryptedField<?> myThat = (PrometheusEncryptedField<?>) pThat;

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
            int myHashCode = HASH_PRIME
                             * getValue().hashCode();
            myHashCode += Arrays.hashCode(theEncrypted);
            return myHashCode;
        }

        @Override
        public MetisDataDifference differs(final Object pThat) {
            /* Reject if null */
            if (pThat == null) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Reject if wrong class */
            if (!getClass().equals(pThat.getClass())) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Access as correct class */
            PrometheusEncryptedField<?> myField = (PrometheusEncryptedField<?>) pThat;

            /* Compare value */
            if (MetisDataDifference.difference(getValue(), myField.getValue()).isDifferent()) {
                return MetisDataDifference.DIFFERENT;
            }

            /* Compare Encrypted value */
            if (!Arrays.equals(getEncryption(), myField.getEncryption())) {
                return MetisDataDifference.SECURITY;
            }

            /* Item is the Same */
            return MetisDataDifference.IDENTICAL;
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return pFormatter.formatObject(theValue);
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_VALUE.equals(pField)) {
                return getValue();
            }
            if (FIELD_ENCRYPTED.equals(pField)) {
                return getEncryption();
            }

            /* Not recognised */
            return MetisFieldValue.UNKNOWN;
        }
    }
}
