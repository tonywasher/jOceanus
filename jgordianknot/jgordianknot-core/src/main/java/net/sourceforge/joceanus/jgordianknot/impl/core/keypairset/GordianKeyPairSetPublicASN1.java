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
package net.sourceforge.joceanus.jgordianknot.impl.core.keypairset;

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeyPairSetPublic.
 * <pre>
 * GordianKeyPairSetPublicASN1 ::= SEQUENCE OF {
 *      algId AlgorithmIdentifier
 *      keys publicKeys
 * }
 *
 * publicKeys ::= SEQUENCE OF SubjectPublicKeyInfo
 * </pre>
 */
public class GordianKeyPairSetPublicASN1
        extends GordianASN1Object {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The x509EncodedKeySpecs.
     */
    private final List<X509EncodedKeySpec> thePublicKeys;

    /**
     * Create the ASN1 sequence.
     * @param pKeyPairSetSpec the keyPairSetSpec
     */
    public GordianKeyPairSetPublicASN1(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        /* Store the Spec */
        theSpec = pKeyPairSetSpec;
        thePublicKeys = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeyPairSetPublicASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the list */
            thePublicKeys = new ArrayList<>();

            /* Access as SubjectPublicKeyInfo */
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(pSequence);
            final AlgorithmIdentifier myId = myInfo.getAlgorithm();
            final ASN1Sequence myKeys = ASN1Sequence.getInstance(myInfo.getPublicKeyData().getBytes());

            /* Build the keyPairSetSpec */
            theSpec = GordianKeyPairSetAlgId.determineKeyPairSetSpec(myId);

            /* Build the list from the keys sequence */
            final Enumeration<?> en = myKeys.getObjects();
            while (en.hasMoreElements()) {
                final SubjectPublicKeyInfo myPKInfo = SubjectPublicKeyInfo.getInstance(en.nextElement());
                thePublicKeys.add(new X509EncodedKeySpec(myPKInfo.getEncoded()));
            }

            /* Check that we have the right number of keys */
            if (thePublicKeys.size() != theSpec.numKeyPairs()) {
                throw new GordianDataException("Invalid keySpec");
            }

            /* handle exceptions */
        } catch (IllegalArgumentException
                | IOException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws OceanusException on error
     */
    public static GordianKeyPairSetPublicASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeyPairSetPublicASN1) {
            return (GordianKeyPairSetPublicASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeyPairSetPublicASN1(ASN1Sequence.getInstance(pObject));
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
     * Obtain the iterator of the public keys.
     * @return the iterator
     */
    public Iterator<X509EncodedKeySpec> keyIterator() {
        return thePublicKeys.iterator();
    }

    /**
     * Add a public key to the list.
     * @param pKey the publicKey to add
     */
    void addKey(final X509EncodedKeySpec pKey) {
        thePublicKeys.add(pKey);
    }

    /**
     * Obtain the X509EncodedKeySpec.
     * @return encodedKeySpec
     * @throws OceanusException on error
     */
    X509EncodedKeySpec getEncodedKeySpec() throws OceanusException {
        try {
            final AlgorithmIdentifier myId = GordianKeyPairSetAlgId.determineAlgorithmId(theSpec);
            final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myId, toASN1Primitive().getEncoded());
            return new X509EncodedKeySpec(myInfo.getEncoded());
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode keySpec", e);
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Build the publicKeySequence */
        final ASN1EncodableVector ks = new ASN1EncodableVector();
        for (X509EncodedKeySpec myKeySpec : thePublicKeys) {
            ks.add(SubjectPublicKeyInfo.getInstance(myKeySpec.getEncoded()));
        }

        /* Return the sequence */
        return new DERSequence(ks);
    }
}
