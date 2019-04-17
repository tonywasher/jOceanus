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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.util.Enumeration;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySetSpec.
 */
public class GordianKeySetHashASN1
        extends ASN1Object {
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
            final Enumeration e = pSequence.getObjects();
            theSpec = GordianKeySetHashSpecASN1.getInstance(e.nextElement()).getSpec();
            theHashBytes = ASN1OctetString.getInstance(e.nextElement()).getOctets();

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
    byte[] getHashBytes() {
        return theHashBytes;
    }

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * GordianKeySetHashASN1 ::= SEQUENCE  {
     *      spec GordianKeySetSpecASN1
     *      hashBytes OCTET STRING
     * }
     * </pre>
     * @return the ASN1 Encoding
     */
    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new GordianKeySetHashSpecASN1(theSpec).toASN1Primitive());
        v.add(new DEROctetString(theHashBytes));

        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded a given wrapped keyLength and # of keys.
     * @return the byte length
     */
    public static int getEncodedLength() {
        /* KeyType has type + length + value (all single byte) */
        int myLength  =  GordianKeySetHashSpecASN1.getEncodedLength();
        myLength += GordianKeySetASN1.getLengthByteArrayField(GordianKeySetHashRecipe.HASHLEN);

        /* Calculate the length of the sequence */
        return  GordianKeySetASN1.getLengthSequence(myLength);
    }
}
