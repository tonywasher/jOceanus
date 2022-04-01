/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.atlas.field;

import java.util.Arrays;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference.MetisDataDiffers;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData.MetisEncryptedField;

/**
 * Encrypted Pair.
 */
public class PrometheusEncryptedPair
    implements MetisDataObjectFormat, MetisDataDiffers {
    /**
     * The value.
     */
    private final Object theValue;

    /**
     * The encryptedBytes.
     */
    private final byte[] theBytes;

    /**
     * Constructor.
     * @param pValue the value.
     * @param pBytes the encrypted bytes
     */
    PrometheusEncryptedPair(final Object pValue,
                            final byte[] pBytes) {
        theValue = pValue;
        theBytes = pBytes;
    }

    /**
     * Obtain the value.
     * @return the value.
     */
    public Object getValue() {
        return theValue;
    }

    /**
     * Obtain the bytes.
     * @return the bytes.
     */
    public byte[] getBytes() {
        return theBytes;
    }

    @Override
    public String formatObject(final MetisDataFormatter pFormatter) {
        /* Format the unencrypted field */
        return pFormatter.formatObject(theValue);
    }

    @Override
    public String toString() {
        return theValue.toString();
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
        final PrometheusEncryptedPair myThat = (PrometheusEncryptedPair) pThat;

        /* Check differences */
        if (MetisDataDifference.difference(getValue(), myThat.getValue()).isDifferent()) {
            return false;
        }

        /* Check encryption */
        return Arrays.equals(getBytes(), myThat.getBytes());
    }

    @Override
    public int hashCode() {
        /* Calculate hash */
        int myHashCode = MetisFieldSet.HASH_PRIME
                * getValue().hashCode();
        myHashCode += Arrays.hashCode(theBytes);
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
        final MetisEncryptedField<?> myField = (MetisEncryptedField<?>) pThat;

        /* Compare Unencrypted value */
        if (MetisDataDifference.difference(getValue(), myField.getValue()).isDifferent()) {
            return MetisDataDifference.DIFFERENT;
        }

        /* Compare Encrypted value */
        if (!Arrays.equals(getBytes(), myField.getBytes())) {
            return MetisDataDifference.SECURITY;
        }

        /* Item is the Same */
        return MetisDataDifference.IDENTICAL;
    }
}
