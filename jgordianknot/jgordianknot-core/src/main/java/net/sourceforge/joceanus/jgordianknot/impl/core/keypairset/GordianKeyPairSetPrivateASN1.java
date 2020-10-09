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
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * ASN1 Encoding of KeyPairSetPrivate.
 * <pre>
 * GordianKeyPairSetPrivateASN1 ::= SEQUENCE OF {
 *      algId AlgorithmIdentifier
 *      privateKeys keys
 * }
 *
 * privateKeys ::= SEQUENCE OF pkcs8EncodedKeySpec
 * </pre>
 */
public class GordianKeyPairSetPrivateASN1
        extends GordianASN1Object {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The x509EncodedKeySpecs.
     */
    private final List<PKCS8EncodedKeySpec> thePrivateKeys;

    /**
     * Create the ASN1 sequence.
     * @param pKeyPairSetSpec the keyPairSetSpec
     */
    public GordianKeyPairSetPrivateASN1(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        /* Store the Spec */
        theSpec = pKeyPairSetSpec;
        thePrivateKeys = new ArrayList<>();
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianKeyPairSetPrivateASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the list */
            thePrivateKeys = new ArrayList<>();

            /* Access as PrivateKeyInfo */
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pSequence);
            final AlgorithmIdentifier myId = myInfo.getPrivateKeyAlgorithm();
            final ASN1Sequence myKeys = ASN1Sequence.getInstance(myInfo.getPrivateKey().getOctets());

            /* Build the keyPairSetSpec */
            theSpec = GordianKeyPairSetAlgId.determineKeyPairSetSpec(myId);

            /* Build the list from the keys sequence */
            final Enumeration<?> en = myKeys.getObjects();
            while (en.hasMoreElements()) {
                final byte[] myBytes = ASN1OctetString.getInstance(en.nextElement()).getOctets();
                addKey(new PKCS8EncodedKeySpec(myBytes));
            }

            /* Check that we have the right number of keys */
            if (thePrivateKeys.size() != theSpec.numKeyPairs()) {
                throw new GordianDataException("Invalid keySpec");
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
    public static GordianKeyPairSetPrivateASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianKeyPairSetPrivateASN1) {
            return (GordianKeyPairSetPrivateASN1) pObject;
        } else if (pObject != null) {
            return new GordianKeyPairSetPrivateASN1(ASN1Sequence.getInstance(pObject));
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
     * Obtain the iterator of the private keys.
     * @return the iterator
     */
    public Iterator<PKCS8EncodedKeySpec> keyIterator() {
        return thePrivateKeys.iterator();
    }

    /**
     * Add a private key to the list.
     * @param pKey the privateKey to add
     */
    void addKey(final PKCS8EncodedKeySpec pKey) {
        thePrivateKeys.add(pKey);
    }

    /**
     * Obtain the PKCS8EncodedKeySpec.
     * @return encodedKeySpec
     * @throws OceanusException on error
     */
    PKCS8EncodedKeySpec getEncodedKeySpec() throws OceanusException {
        try {
            final AlgorithmIdentifier myId = GordianKeyPairSetAlgId.determineAlgorithmId(theSpec);
            final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, toASN1Primitive());
            return new PKCS8EncodedKeySpec(myInfo.getEncoded());
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode keySpec", e);
        }
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        /* Build the privateKeySequence */
        final ASN1EncodableVector ks = new ASN1EncodableVector();
        for (PKCS8EncodedKeySpec myKeySpec : thePrivateKeys) {
            ks.add(new DEROctetString(myKeySpec.getEncoded()));
        }

        /* Return the sequence */
        return new DERSequence(ks);
    }
}
