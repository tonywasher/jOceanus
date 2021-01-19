/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.sign;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetAlgId;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeyPairSetSign.
 * <pre>
 * GordianKeyPairSetSignASN1 ::= SEQUENCE {
 *      algId AlgorithmIdentifier
 *      signatures SEQUENCE OF OCTET STRINGS
 * }
 * </pre>
 */
public class GordianKeyPairSetSignASN1
        extends GordianASN1Object {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The signatures.
     */
    private final List<byte[]> theSignatures;

    /**
     * Create the ASN1 sequence.
     * @param pKeyPairSetSpec the keyPairSetSpec
     */
    public GordianKeyPairSetSignASN1(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        /* Store the Spec */
        theSpec = pKeyPairSetSpec;
        theSignatures = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeyPairSetSignASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the list */
            theSignatures = new ArrayList<>();

            /* Extract the parameters from the sequence */
            Enumeration<?> en = pSequence.getObjects();
            final AlgorithmIdentifier myId = AlgorithmIdentifier.getInstance(en.nextElement());
            final ASN1Sequence mySigns = ASN1Sequence.getInstance(en.nextElement());

            /* Make sure that we have completed the sequence */
            if (en.hasMoreElements()) {
                throw new GordianDataException("Unexpected additional values in ASN1 sequence");
            }

            /* Build the keyPairSetSpec */
            theSpec = GordianKeyPairSetAlgId.determineKeyPairSetSpec(myId);

            /* Build the list from the signatures sequence */
            en = mySigns.getObjects();
            while (en.hasMoreElements()) {
                final byte[] myBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();
                addSignature(myBytes);
            }

            /* Check that we have the right number of signatures */
            if (theSignatures.size() != theSpec.numKeyPairs()) {
                throw new GordianDataException("Invalid signature");
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
    public static GordianKeyPairSetSignASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeyPairSetSignASN1) {
            return (GordianKeyPairSetSignASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeyPairSetSignASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Obtain the spec.
     * @return the Spec
     */
    public GordianKeyPairSetSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the iterator of the signatures.
     * @return the iterator
     */
    public Iterator<byte[]> signIterator() {
        return theSignatures.iterator();
    }

    /**
     * Add a signature to the list.
     * @param pSign the signature to add
     */
    void addSignature(final byte[] pSign) {
        theSignatures.add(pSign);
    }

    /**
     * Obtain the Signature.
     * @return the signature
     * @throws OceanusException on error
     */
    byte[] getEncodedSignature() throws OceanusException {
        try {
            return toASN1Primitive().getEncoded();
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode signature", e);
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Build the signatureSequence */
        final ASN1EncodableVector ss = new ASN1EncodableVector();
        for (byte[] mySign : theSignatures) {
            ss.add(new DEROctetString(mySign));
        }

        /* Build the overall sequence */
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(GordianKeyPairSetAlgId.determineAlgorithmId(theSpec).toASN1Primitive());
        v.add(new DERSequence(ss));
        return new DERSequence(v);
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
        if (!(pThat instanceof GordianKeyPairSetSignASN1)) {
            return false;
        }
        final GordianKeyPairSetSignASN1 myThat = (GordianKeyPairSetSignASN1) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theSpec, myThat.theSpec)
                && Objects.equals(theSignatures, myThat.theSignatures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSpec, theSignatures);
    }
}
