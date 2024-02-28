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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.util.Enumeration;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeySetHashSpec.
 * <pre>
 * GordianKeySetHashSpecASN1 ::= SEQUENCE {
 *      keySetSpec GordianKeySetSpecASN1
 *      iterations INTEGER
 * }
 * </pre>
 */
public class GordianKeySetHashSpecASN1
        extends GordianASN1Object {
    /**
     * The KeySetHashSpec.
     */
    private final GordianKeySetHashSpec theSpec;

    /**
     * Create the ASN1 sequence.
     * @param pKeySetHashSpec the keySetHashSpec
     */
    public GordianKeySetHashSpecASN1(final GordianKeySetHashSpec pKeySetHashSpec) {
        /* Store the Spec */
        theSpec = pKeySetHashSpec;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeySetHashSpecASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract the parameters from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            final GordianKeySetSpec mySpec = GordianKeySetSpecASN1.getInstance(en.nextElement()).getSpec();
            final int myIterations = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* Create the keySpec */
            theSpec = new GordianKeySetHashSpec(myIterations, mySpec);

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
    public static GordianKeySetHashSpecASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeySetHashSpecASN1) {
            return (GordianKeySetHashSpecASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeySetHashSpecASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public GordianKeySetHashSpec getSpec() {
        return theSpec;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new GordianKeySetSpecASN1(theSpec.getKeySetSpec()).toASN1Primitive());
        v.add(new ASN1Integer(theSpec.getKIterations()));
        return new DERSequence(v);
    }

    /**
     * Obtain the byte length of the encoded sequence.
     * @return the byte length
     */
    static int getEncodedLength() {
        /* KeyType has type + length + value (all single byte) */
        int myLength  =  GordianASN1Util.getLengthIntegerField(1);
        myLength += GordianKeySetSpecASN1.getEncodedLength();

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
        if (!(pThat instanceof GordianKeySetHashSpecASN1)) {
            return false;
        }
        final GordianKeySetHashSpecASN1 myThat = (GordianKeySetHashSpecASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theSpec, myThat.getSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSpec());
    }
}
