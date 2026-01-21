/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.impl.core.base;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.io.IOException;

/**
 * ASN1 Utilities.
 */
public final class GordianASN1Util {
    /**
     * Base our ids off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier BASEOID = BCObjectIdentifiers.bc.branch("100");

    /**
     * FactoryOID.
     */
    public static final ASN1ObjectIdentifier FACTORYOID = BASEOID.branch("1");

    /**
     * SymmetricOID branch.
     */
    public static final ASN1ObjectIdentifier SYMOID = BASEOID.branch("2");

    /**
     * AsymmetricOID branch.
     */
    public static final ASN1ObjectIdentifier ASYMOID = BASEOID.branch("3");

    /**
     * ExtensionsOID branch.
     */
    public static final ASN1ObjectIdentifier EXTOID = BASEOID.branch("4");

    /**
     * Private constructor.
     */
    private GordianASN1Util() {
    }

    /**
     * Obtain the byte Length of an encoded sequence field.
     *
     * @param pLength the length of the sequence
     * @return the byte length
     */
    public static int getLengthSequence(final int pLength) {
        /* Type + length + encoded length */
        return 1 + pLength + getLengthValue(pLength);
    }

    /**
     * Obtain the byte Length of an encoded byte array field.
     *
     * @param pLength the length of the field
     * @return the byte length
     */
    public static int getLengthByteArrayField(final int pLength) {
        /* Type + length + encoded length */
        return 1 + pLength + getLengthValue(pLength);
    }

    /**
     * Obtain the byte Length of an encoded integer field.
     *
     * @param pValue the value
     * @return the byte length
     */
    public static int getLengthIntegerField(final int pValue) {
        /* Type + length + encoded value */
        return 2 + getLengthValue(pValue);
    }

    /**
     * Obtain the byte Length of an encoded integer value.
     *
     * @param pValue the value
     * @return the byte length
     */
    public static int getLengthValue(final int pValue) {
        /* Handle small lengths */
        if (pValue <= Byte.MAX_VALUE) {
            return 1;
        }

        /*  Loop while we work out the length */
        int myLen = pValue >> Byte.SIZE;
        int myResult = 2;
        while (myLen > 0) {
            myResult++;
            myLen >>= Byte.SIZE;
        }

        /* Return the length */
        return myResult;
    }

    /**
     * Obtain the Length of an algorithmId field.
     *
     * @param pValue the value
     * @return the byte length
     */
    public static int getLengthAlgorithmField(final AlgorithmIdentifier pValue) {
        /* Protect against exceptions */
        try {
            /* Get the encoded length */
            final byte[] myEncoded = pValue.toASN1Primitive().getEncoded();

            /* Type + encoded value */
            return myEncoded.length;

            /* handle exceptions */
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Extension class for ASN1Object.
     */
    public abstract static class GordianASN1Object
            extends ASN1Object {
        /**
         * Obtain encodedBytes.
         *
         * @return the bytes
         * @throws GordianException on error
         */
        public byte[] getEncodedBytes() throws GordianException {
            try {
                return toASN1Primitive().getEncoded();
            } catch (IOException e) {
                throw new GordianIOException("Failed to generate ASN1", e);
            }
        }
    }
}
