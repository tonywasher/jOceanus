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
package net.sourceforge.joceanus.gordianknot.impl.core.lock;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

import java.util.Enumeration;
import java.util.Objects;

/**
 * ASN1 Encoding of passwordLockSpec.
 * <pre>
 * GordianPasswordLockSpecASN1 ::= SEQUENCE {
 *      keySetSpec GordianKeySetSpecASN1
 *      iterations INTEGER
 * }
 * </pre>
 */
public class GordianPasswordLockSpecASN1
        extends GordianASN1Object {
    /**
     * The PasswordLockSpec.
     */
    private final GordianPasswordLockSpec theLockSpec;

    /**
     * Create the ASN1 sequence.
     * @param pLockSpec the passwordLockSpec
     */
    public GordianPasswordLockSpecASN1(final GordianPasswordLockSpec pLockSpec) {
        /* Store the Spec */
        theLockSpec = pLockSpec;
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    public GordianPasswordLockSpecASN1(final ASN1Sequence pSequence) throws GordianException {
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
     * @throws GordianException on error
     */
    public static GordianPasswordLockSpecASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianPasswordLockSpecASN1) {
            return (GordianPasswordLockSpecASN1) pObject;
        } else if (pObject != null) {
            return new GordianPasswordLockSpecASN1(ASN1Sequence.getInstance(pObject));
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

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new GordianKeySetSpecASN1(theLockSpec.getKeySetSpec()).toASN1Primitive());
        v.add(new ASN1Integer(theLockSpec.getKIterations()));
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
        if (!(pThat instanceof GordianPasswordLockSpecASN1)) {
            return false;
        }
        final GordianPasswordLockSpecASN1 myThat = (GordianPasswordLockSpecASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theLockSpec, myThat.getLockSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLockSpec());
    }
}
