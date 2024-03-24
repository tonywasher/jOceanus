/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.password;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordLockSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySetHashSpec.
 * <pre>
 * GordianPasswordLockASN1 ::= SEQUENCE {
 *      keySetSpec GordianKeySetSpecASN1
 *      iterations INTEGER
 *      hashBytes OCTET STRING
 *      payload OCTET STRING
 * }
 * </pre>
 */
public class GordianPasswordLockASN1
        extends GordianASN1Object {
    /**
     * The PasswordLockSpec.
     */
    private final GordianPasswordLockSpec theLockSpec;

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
        theLockSpec = pLockSpec;
        theHashBytes = pHashBytes;
        thePayload = pPayload;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    public GordianPasswordLockASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            final GordianKeySetSpec mySpec = GordianKeySetSpecASN1.getInstance(en.nextElement()).getSpec();
            final int myIterations = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            theHashBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();
            thePayload = ASN1OctetString.getInstance(en.nextElement()).getOctets();

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* Create the lockSpec */
            theLockSpec = new GordianPasswordLockSpec(myIterations, mySpec);

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
    public static GordianPasswordLockASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianPasswordLockASN1) {
            return (GordianPasswordLockASN1) pObject;
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
        return theLockSpec;
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
        v.add(new GordianKeySetSpecASN1(theLockSpec.getKeySetSpec()).toASN1Primitive());
        v.add(new ASN1Integer(theLockSpec.getKIterations()));
        v.add(new DEROctetString(theHashBytes));
        v.add(new DEROctetString(thePayload));
        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @return the byte length
     */
    static int getBaseEncodedLength() {
        /* KeyType has type + length + value (all single byte) */
        int myLength  =  GordianASN1Util.getLengthIntegerField(1);
        myLength += GordianKeySetSpecASN1.getEncodedLength();
        myLength += GordianASN1Util.getLengthByteArrayField(GordianPasswordLockRecipe.HASHSIZE);

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
        if (!(pThat instanceof GordianPasswordLockASN1)) {
            return false;
        }
        final GordianPasswordLockASN1 myThat = (GordianPasswordLockASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theLockSpec, myThat.getLockSpec())
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
