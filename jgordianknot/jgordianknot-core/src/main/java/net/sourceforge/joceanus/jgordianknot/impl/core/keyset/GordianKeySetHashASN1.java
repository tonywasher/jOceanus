/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySetHash.
 * <pre>
 * GordianKeySetHashASN1 ::= SEQUENCE  {
 *      spec GordianKeySetHashSpecASN1
 *      hashBytes OCTET STRING
 * }
 * </pre>
 */
public class GordianKeySetHashASN1
        extends GordianASN1Object {
    /**
     * Base our algorithmId off bouncyCastle.
     */
    private static final ASN1ObjectIdentifier KEYSETHASHALGID = GordianCoreKeySetFactory.KEYSETOID.branch("2");

    /**
     * The KeySetSpec.
     */
    private final GordianKeySetHashSpec theSpec;

    /**
     * The HashBytes.
     */
    private final byte[] theHashBytes;

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHashSpec the keySetHashSpec
     * @param pHashBytes the hashBytes
     */
    public GordianKeySetHashASN1(final GordianKeySetHashSpec pKeySetHashSpec,
                                 final byte[] pHashBytes) {
        /* Store the parameters */
        theSpec = pKeySetHashSpec;
        theHashBytes = pHashBytes;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeySetHashASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theSpec = GordianKeySetHashSpecASN1.getInstance(en.nextElement()).getSpec();
            theHashBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();

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
     * @throws OceanusException on error
     */
    public static GordianKeySetHashASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeySetHashASN1) {
            return (GordianKeySetHashASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeySetHashASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    GordianKeySetHashSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the hashBytes.
     * @return the hashBytes
     */
    public byte[] getHashBytes() {
        return theHashBytes;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new GordianKeySetHashSpecASN1(theSpec).toASN1Primitive());
        v.add(new DEROctetString(theHashBytes));

        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded object.
     * @return the byte length
     */
    public static int getEncodedLength() {
        /* KeyType has type + length + value (all single byte) */
        int myLength  =  GordianKeySetHashSpecASN1.getEncodedLength();
        myLength += GordianASN1Util.getLengthByteArrayField(GordianKeySetHashRecipe.HASHLEN);

        /* Calculate the length of the sequence */
        return GordianASN1Util.getLengthSequence(myLength);
    }

    /**
     * Obtain the algorithmId.
     * @return  the algorithmId
     */
    public AlgorithmIdentifier getAlgorithmId() {
        return new AlgorithmIdentifier(KEYSETHASHALGID, toASN1Primitive());
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
        if (!(pThat instanceof GordianKeySetHashASN1)) {
            return false;
        }
        final GordianKeySetHashASN1 myThat = (GordianKeySetHashASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theSpec, myThat.getSpec())
                && Arrays.equals(getHashBytes(), myThat.getHashBytes());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpec())
                + Arrays.hashCode(getHashBytes());
    }
}
