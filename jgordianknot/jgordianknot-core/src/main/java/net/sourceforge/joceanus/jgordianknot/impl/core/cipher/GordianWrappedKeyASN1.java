/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of WrappedKey Request.
 * <pre>
 * GordianAgreementRequestASN1 ::= SEQUENCE  {
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
     * @throws OceanusException on error
     */
    private GordianWrappedKeyASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the sequence */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pSequence);
            final Enumeration<?> en = mySequence.getObjects();

            /* Access message parts */
            theKeySpec = AlgorithmIdentifier.getInstance(en.nextElement());
            theWrappedKey = ASN1OctetString.getInstance(en.nextElement()).getOctets();

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws OceanusException on error
     */
    public static GordianWrappedKeyASN1 getInstance(final Object pObject) throws OceanusException {
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
}
