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
package net.sourceforge.joceanus.jprometheus.atlas.field;

import java.util.Arrays;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataDifference.MetisDataDiffers;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Encrypted Pair.
 */
public class PrometheusEncryptedPair
    implements MetisDataObjectFormat, MetisDataDiffers {
    /**
     * Encrypted Money length.
     */
    public static final int MONEYLEN = 15;

    /**
     * Encrypted Units length.
     */
    public static final int UNITSLEN = 15;

    /**
     * Encrypted Rate length.
     */
    public static final int RATELEN = 10;

    /**
     * Encrypted Price length.
     */
    public static final int PRICELEN = 15;

    /**
     * Encrypted Ratio length.
     */
    public static final int RATIOLEN = 10;

    /**
     * The value.
     */
    private final Object theValue;

    /**
     * The encryptedBytes.
     */
    private byte[] theBytes;

    /**
     * The keyset.
     */
    private GordianKeySet theKeySet;

    /**
     * Constructor.
     * @param pKeySet the keySet
     * @param pValue the value.
     * @param pBytes the encrypted bytes
     */
    PrometheusEncryptedPair(final GordianKeySet pKeySet,
                            final Object pValue,
                            final byte[] pBytes) {
        theKeySet = pKeySet;
        theValue = pValue;
        theBytes = pBytes;
    }

    /**
     * Obtain the keySet.
     * @return the keySet.
     */
    public GordianKeySet getKeySet() {
        return theKeySet;
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

    /**
     * Adopt Encryption.
     * @param pGenerator the generator
     * @param pSource field to adopt encryption from
     * @throws OceanusException on error
     */
    protected void adoptEncryption(final PrometheusFieldGenerator pGenerator,
                                   final PrometheusEncryptedPair pSource) throws OceanusException {
        /* Store the keySet */
        theKeySet = pGenerator.getKeySet();

        /* If we need to renew the encryption */
        if (pSource == null
                || MetisDataDifference.difference(theKeySet, pSource.getKeySet()).isDifferent()
                || MetisDataDifference.difference(getValue(), pSource.getValue()).isDifferent()) {
            /* encrypt the value */
            theBytes = pGenerator.encryptValue(getValue());

            /* else we can simply adopt the underlying encryption */
        } else {
            /* Pick up the underlying encryption */
            theBytes = pSource.getBytes();
        }
    }

    /**
     * Adopt Encryption.
     * @param pEncryptor the encryptor
     * @param pSource field to adopt encryption from
     * @throws OceanusException on error
     */
    protected void adoptEncryption(final PrometheusEncryptor pEncryptor,
                                   final PrometheusEncryptedPair pSource) throws OceanusException {
        /* Store the keySet */
        theKeySet = pEncryptor.getKeySet();

        /* If we need to renew the encryption */
        if (pSource == null
                || MetisDataDifference.difference(theKeySet, pSource.getKeySet()).isDifferent()
                || MetisDataDifference.difference(getValue(), pSource.getValue()).isDifferent()) {
            /* encrypt the value */
            theBytes = pEncryptor.encryptValue(getValue());

            /* else we can simply adopt the underlying encryption */
        } else {
            /* Pick up the underlying encryption */
            theBytes = pSource.getBytes();
        }
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
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
        if (!theKeySet.equals(myThat.getKeySet())) {
            return false;
        }

        /* Check differences */
        if (MetisDataDifference.difference(getValue(), myThat.getValue()).isDifferent()) {
            return false;
        }

        /* Check encryption */
        return Arrays.equals(getBytes(), myThat.getBytes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeySet, theValue, Arrays.hashCode(theBytes));
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
        final PrometheusEncryptedPair myField = (PrometheusEncryptedPair) pThat;

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
