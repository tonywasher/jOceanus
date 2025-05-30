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
package net.sourceforge.joceanus.gordianknot.impl.core.lock;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of passwordLock.
 * <pre>
 * GordianPasswordLockASN1 ::= SEQUENCE {
 *      passwordLockSpec GordianPasswordLockSpecASN1
 *      hashBytes OCTET STRING
 *      payload OCTET STRING OPTIONAL
 * }
 * </pre>
 */
public class GordianPasswordLockASN1
        extends GordianASN1Object {
    /**
     * The PasswordLockSpecASN1.
     */
    private final GordianPasswordLockSpecASN1 theLockSpec;

    /**
     * The HashBytes.
     */
    private final byte[] theHashBytes;

    /**
     * The Payload.
     */
    private final byte[] thePayload;

    /**
     * Create the ASN1 sequence.
     * @param pLockSpec the passwordLockSpec
     * @param pHashBytes the hash bytes
     * @param pPayload the payload
     */
    public GordianPasswordLockASN1(final GordianPasswordLockSpec pLockSpec,
                                   final byte[] pHashBytes,
                                   final byte[] pPayload) {
        /* Store the Spec */
        theLockSpec = new GordianPasswordLockSpecASN1(pLockSpec);
        theHashBytes = pHashBytes;
        thePayload = pPayload;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    public GordianPasswordLockASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theLockSpec = GordianPasswordLockSpecASN1.getInstance(en.nextElement());
            theHashBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            thePayload = en.hasMoreElements()
                    ? ASN1OctetString.getInstance(en.nextElement()).getOctets()
                    : null;

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
    public static GordianPasswordLockASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianPasswordLockASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianPasswordLockASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the lockSpec.
     * @return the lockSpec
     */
    public GordianPasswordLockSpec getLockSpec() {
        return theLockSpec.getLockSpec();
    }

    /**
     * Obtain the hashBytes.
     * @return the hashBytes
     */
    public byte[] getHashBytes() {
        return theHashBytes;
    }

    /**
     * Obtain the payload.
     * @return the payload
     */
    public byte[] getPayload() {
        return thePayload;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(theLockSpec.toASN1Primitive());
        v.add(new DEROctetString(theHashBytes));
        if (thePayload != null) {
            v.add(new DEROctetString(thePayload));
        }
        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @param pPayloadLen the payload length
     * @return the byte length
     */
    static int getEncodedLength(final int pPayloadLen) {
        /* KeyType has type + length + value (all single byte) + value */
        int myLength  = GordianPasswordLockSpecASN1.getEncodedLength();
        myLength += GordianASN1Util.getLengthByteArrayField(GordianPasswordLockRecipe.HASHSIZE);
        myLength += GordianASN1Util.getLengthByteArrayField(pPayloadLen);

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

        /* Check that the fields are equal */
        return pThat instanceof GordianPasswordLockASN1 myThat
                && Objects.equals(getLockSpec(), myThat.getLockSpec())
                && Arrays.equals(getHashBytes(), myThat.getHashBytes())
                && Arrays.equals(getPayload(), myThat.getPayload());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLockSpec())
               + Arrays.hashCode(getHashBytes())
               + Arrays.hashCode(getPayload());
    }
}
