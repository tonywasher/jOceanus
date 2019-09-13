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
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySetSpec.
 */
public class GordianKeySetSpecASN1
        extends ASN1Object {
    /**
     * Base our algorithmId off bouncyCastle.
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
     * @throws OceanusException on error
     */
    private GordianKeySetSpecASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
           /* Extract the parameters from the sequence */
            final Enumeration e = pSequence.getObjects();
            final int myId = ASN1Integer.getInstance(e.nextElement()).getValue().intValue();
            final int myNumSteps = ASN1Integer.getInstance(e.nextElement()).getValue().intValue();
            final GordianLength myLen = GordianKeyLengths.getKeyLengthForId(myId);

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
     * @throws OceanusException on error
     */
    public static GordianKeySetSpecASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeySetSpecASN1) {
            return (GordianKeySetSpecASN1) pObject;
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

    /**
     * Produce an object suitable for an ASN1OutputStream.
     * <pre>
     * GordianKeySetSpecASN1 ::= SEQUENCE  {
     *      keyLengthId INTEGER
     *      numCipherSteps INTEGER
     * }
     * </pre>
     * @return the ASN1 Encoding
     */
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
    static int getEncodedLength() {
        /* KeyType has type + length + value (all single byte) */
        int myLength  =  GordianKeySetASN1.getLengthIntegerField(1);
        myLength += GordianKeySetASN1.getLengthIntegerField(1);

        /* Calculate the length of the sequence */
        return  GordianKeySetASN1.getLengthSequence(myLength);
    }

    /**
     * Obtain the algorithmId.
     * @return  the algorithmId
     */
    public AlgorithmIdentifier getAlgorithmId() {
        return new AlgorithmIdentifier(KEYSETALGID, toASN1Primitive());
    }
}