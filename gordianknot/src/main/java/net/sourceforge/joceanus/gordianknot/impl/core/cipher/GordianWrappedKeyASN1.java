/*******************************************************************************
 * GordianKnot: Security Suite
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
package net.sourceforge.joceanus.gordianknot.impl.core.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of WrappedKey.
 * <pre>
 * GordianWrappedKeyASN1 ::= SEQUENCE  {
 *      id AlgorithmIdentifier
 *      wrappedKey OCTET STRING OPTIONAL
 * }
 * </pre>
 */
public class GordianWrappedKeyASN1
        extends GordianASN1Object {
    /**
     * The AgreementSpec.
     */
    private final AlgorithmIdentifier theKeySpec;

    /**
     * The WrappedKey.
     */
    private final byte[] theWrappedKey;

    /**
     * Create the ASN1 sequence.
     * @param pKeySpec the keySpec
     * @param pWrappedKey the wrappedKey
     */
    public GordianWrappedKeyASN1(final AlgorithmIdentifier pKeySpec,
                                 final byte[] pWrappedKey) {
        /* Store the Details */
        theKeySpec = pKeySpec;
        theWrappedKey = pWrappedKey;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianWrappedKeyASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access message parts */
            theKeySpec = AlgorithmIdentifier.getInstance(en.nextElement());
            theWrappedKey = ASN1OctetString.getInstance(en.nextElement()).getOctets();

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianWrappedKeyASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianWrappedKeyASN1) {
            return (GordianWrappedKeyASN1) pObject;
        } else if (pObject != null) {
            return new GordianWrappedKeyASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public AlgorithmIdentifier getKeySpecId() {
        return theKeySpec;
    }

    /**
     * Obtain the wrappedKey.
     * @return the wrappedKey
     */
    public byte[] getWrappedKey() {
        return theWrappedKey;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theKeySpec);
        v.add(new BEROctetString(theWrappedKey));

        return new DERSequence(v);
    }

    /**
     * Obtain the byte length for a given wrapped keyLength and keyAlgId.
     * @param pAlgId the algorithmId
     * @param pWrappedKeyLen the wrapped keyLength
     * @return the byte length
     */
    static int getEncodedLength(final AlgorithmIdentifier pAlgId,
                                final int pWrappedKeyLen) {
        /* Key length is type + length + value */
        int myLength = GordianASN1Util.getLengthByteArrayField(pWrappedKeyLen);

        /* AlgorithmId length  */
        myLength += GordianASN1Util.getLengthAlgorithmField(pAlgId);

        /* Calculate the length of the sequence */
        return GordianASN1Util.getLengthSequence(myLength);
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the classes are the same */
        if (!(pThat instanceof GordianWrappedKeyASN1)) {
            return false;
        }
        final GordianWrappedKeyASN1 myThat = (GordianWrappedKeyASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theKeySpec, myThat.getKeySpecId())
                && Arrays.equals(getWrappedKey(), myThat.getWrappedKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeySpecId())
                + Arrays.hashCode(getWrappedKey());
    }
}
