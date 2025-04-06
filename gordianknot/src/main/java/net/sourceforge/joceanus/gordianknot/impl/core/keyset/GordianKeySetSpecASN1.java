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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of KeySetSpec.
 * <pre>
 * GordianKeySetSpecASN1 ::= SEQUENCE {
 *      keyLengthId INTEGER
 *      numCipherSteps INTEGER
 * }
 * </pre>
 */
public class GordianKeySetSpecASN1
        extends GordianASN1Object {
    /**
     * KeySetSpecOID.
     */
    public static final ASN1ObjectIdentifier KEYSETALGID = GordianCoreKeySetFactory.KEYSETOID.branch("1");

    /**
     * The KeySetSpec.
     */
    private final GordianKeySetSpec theSpec;

    /**
     * Create the ASN1 sequence.
     * @param pKeySetSpec the keySetSpec
     */
    public GordianKeySetSpecASN1(final GordianKeySetSpec pKeySetSpec) {
        /* Store the Spec */
        theSpec = pKeySetSpec;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianKeySetSpecASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
           /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            final int myId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            final int myNumSteps = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            final GordianLength myLen = GordianKeyLengths.getKeyLengthForId(myId);

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* Create the keySpec */
            theSpec = new GordianKeySetSpec(myLen, myNumSteps);

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
    public static GordianKeySetSpecASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianKeySetSpecASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianKeySetSpecASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public GordianKeySetSpec getSpec() {
        return theSpec;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(GordianKeyLengths.getIdForKeyLength(theSpec.getKeyLength())));
        v.add(new ASN1Integer(theSpec.getCipherSteps()));

        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @return the byte length
     */
    public static int getEncodedLength() {
        /* KeyType has type + length + value (all single byte) */
        int myLength  =  GordianASN1Util.getLengthIntegerField(1);
        myLength += GordianASN1Util.getLengthIntegerField(1);

        /* Calculate the length of the sequence */
        return GordianASN1Util.getLengthSequence(myLength);
    }

    /**
     * Obtain the algorithmId.
     * @return  the algorithmId
     */
    public AlgorithmIdentifier getAlgorithmId() {
        return new AlgorithmIdentifier(KEYSETALGID, toASN1Primitive());
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
        return pThat instanceof GordianKeySetSpecASN1 myThat
                && Objects.equals(theSpec, myThat.getSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpec());
    }
}
