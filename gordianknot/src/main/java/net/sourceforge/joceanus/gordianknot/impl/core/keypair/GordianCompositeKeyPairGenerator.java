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
package net.sourceforge.joceanus.gordianknot.impl.core.keypair;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCompositeKeyPair.GordianStateAwareCompositeKeyPair;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * CompositeKeyPair generator.
 */
public class GordianCompositeKeyPairGenerator
        implements GordianKeyPairGenerator {
    /**
     * The keyPairSpec.
     */
    private final GordianKeyPairSpec theSpec;

    /**
     * The keyPairFactory.
     */
    private final GordianKeyPairFactory theFactory;

    /**
     * The list of generators.
     */
    private final List<GordianKeyPairGenerator> theGenerators;

    /**
     * is the composite keyPair stateAware?.
     */
    private final boolean isStateAware;

    /**
     * Constructor.
     * @param pFactory the asymFactory.
     * @param pSpec the keyPairSetSpec.
     * @throws GordianException on error
     */
    GordianCompositeKeyPairGenerator(final GordianKeyPairFactory pFactory,
                                     final GordianKeyPairSpec pSpec) throws GordianException {
        /* Store the spec. */
        theSpec = pSpec;
        theFactory = pFactory;
        theGenerators = new ArrayList<>();
        boolean stateAware = false;

        /* Loop through the asymKeySpecs */
        final Iterator<GordianKeyPairSpec> myIterator = pSpec.keySpecIterator();
        while (myIterator.hasNext()) {
            final GordianKeyPairSpec mySpec = myIterator.next();

            /* create generator and add it to list */
            theGenerators.add(pFactory.getKeyPairGenerator(mySpec));
            stateAware = mySpec.isStateAware();
        }

        /* Record stateAwareness */
        isStateAware = stateAware;
    }

    @Override
    public GordianKeyPairSpec getKeySpec() {
        return theSpec;
    }

    @Override
    public GordianCompositeKeyPair generateKeyPair() {
        /* Create the new empty keyPairSet */
        final GordianCompositeKeyPair myKeyPair = isStateAware ? new GordianStateAwareCompositeKeyPair(theSpec)
                                                               : new GordianCompositeKeyPair(theSpec);

        /* Loop through the generators */
        for (GordianKeyPairGenerator myGenerator : theGenerators) {
            /* create keyPair and add it to composite */
            myKeyPair.addKeyPair(myGenerator.generateKeyPair());
        }

        /* Return the keyPair */
        return myKeyPair;
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create the new empty keyPair */
            final GordianCompositeKeyPair myPair = (GordianCompositeKeyPair) pKeyPair;

            /* Loop through the generators */
            final Iterator<GordianKeyPair> myIterator = myPair.iterator();
            final ASN1EncodableVector ks = new ASN1EncodableVector();
            for (GordianKeyPairGenerator myGenerator : theGenerators) {
                /* create encodedKeySpec and add it to set */
                ks.add(SubjectPublicKeyInfo.getInstance(myGenerator.getX509Encoding(myIterator.next()).getEncoded()));
            }

            /* Build the x509 encoding */
            final AlgorithmIdentifier myId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
            final SubjectPublicKeyInfo myInfo = new SubjectPublicKeyInfo(myId, new DERSequence(ks).getEncoded());
            return new X509EncodedKeySpec(myInfo.getEncoded());
        } catch (IOException e) {
            throw new GordianIOException("Failed to derive keySpec", e);
        }
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPair pKeyPair) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create the new empty keyPair */
            final GordianCompositeKeyPair myPair = (GordianCompositeKeyPair) pKeyPair;

            /* Loop through the generators */
            final Iterator<GordianKeyPair> myIterator = myPair.iterator();
            final ASN1EncodableVector ks = new ASN1EncodableVector();
            for (GordianKeyPairGenerator myGenerator : theGenerators) {
                /* create encodedKeySpec and add it to set */
                ks.add(PrivateKeyInfo.getInstance(myGenerator.getPKCS8Encoding(myIterator.next()).getEncoded()));
            }

            final AlgorithmIdentifier myId = new AlgorithmIdentifier(MiscObjectIdentifiers.id_alg_composite);
            final PrivateKeyInfo myInfo = new PrivateKeyInfo(myId, new DERSequence(ks));
            return new PKCS8EncodedKeySpec(myInfo.getEncoded());
        } catch (IOException e) {
            throw new GordianIOException("Failed to derive keySpec", e);
        }
    }

    @Override
    public GordianCompositeKeyPair deriveKeyPair(final X509EncodedKeySpec pPublicKeySet,
                                                 final PKCS8EncodedKeySpec pPrivateKeySet) throws GordianException {
        /* CheckKeySpecs */
        checkKeySpec(pPublicKeySet);
        checkKeySpec(pPrivateKeySet);

        /* Protect against exceptions */
        try {
            /* Parse the Public ASN1 */
            final ASN1Sequence myPubSequence = ASN1Sequence.getInstance(pPublicKeySet.getEncoded());
            final SubjectPublicKeyInfo myPubInfo = SubjectPublicKeyInfo.getInstance(myPubSequence);
            final ASN1Sequence myPubKeys = ASN1Sequence.getInstance(myPubInfo.getPublicKeyData().getBytes());

            /* Parse the Private ASN1 */
            final ASN1Sequence myPrivSequence = ASN1Sequence.getInstance(pPrivateKeySet.getEncoded());
            final PrivateKeyInfo myPrivInfo = PrivateKeyInfo.getInstance(myPrivSequence);
            final ASN1Sequence myPrivKeys = ASN1Sequence.getInstance(myPrivInfo.getPrivateKey().getOctets());

            /* Create the keySet */
            final GordianCompositeKeyPair myPair = isStateAware ? new GordianStateAwareCompositeKeyPair(theSpec)
                                                                : new GordianCompositeKeyPair(theSpec);

            /* Build the list from the keys sequence */
            final Enumeration<?> enPub = myPubKeys.getObjects();
            final Enumeration<?> enPriv = myPrivKeys.getObjects();
            for (GordianKeyPairGenerator myGenerator : theGenerators) {
                final SubjectPublicKeyInfo myPubKInfo = SubjectPublicKeyInfo.getInstance(enPub.nextElement());
                final X509EncodedKeySpec myX509 = new X509EncodedKeySpec(myPubKInfo.getEncoded());
                final PrivateKeyInfo myPrivKInfo = PrivateKeyInfo.getInstance(enPriv.nextElement());
                final PKCS8EncodedKeySpec myPKCS8 = new PKCS8EncodedKeySpec(myPrivKInfo.getEncoded());
                myPair.addKeyPair(myGenerator.deriveKeyPair(myX509, myPKCS8));
            }

            /* return the composite pair */
            return myPair;

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode keySpec", e);
        }
    }

    @Override
    public GordianCompositeKeyPair derivePublicOnlyKeyPair(final X509EncodedKeySpec pPublicKeySet) throws GordianException {
        /* CheckKeySpecs */
        checkKeySpec(pPublicKeySet);

        /* Protect against exceptions */
        try {
            /* Parse the ASN1 */
            final ASN1Sequence mySequence = ASN1Sequence.getInstance(pPublicKeySet.getEncoded());
            final SubjectPublicKeyInfo myInfo = SubjectPublicKeyInfo.getInstance(mySequence);
            final ASN1Sequence myKeys = ASN1Sequence.getInstance(myInfo.getPublicKeyData().getBytes());

            /* Create the keySet */
            final GordianCompositeKeyPair myPair = new GordianCompositeKeyPair(theSpec);

            /* Build the list from the keys sequence */
            final Enumeration<?> en = myKeys.getObjects();
            for (GordianKeyPairGenerator myGenerator : theGenerators) {
                final SubjectPublicKeyInfo myPKInfo = SubjectPublicKeyInfo.getInstance(en.nextElement());
                final X509EncodedKeySpec myX509 = new X509EncodedKeySpec(myPKInfo.getEncoded());
                myPair.addKeyPair(myGenerator.derivePublicOnlyKeyPair(myX509));
            }

            /* return the composite pair */
            return myPair;

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to encode keySpec", e);
        }
    }

    /**
     * Check keySpec.
     * @param pKeySpec the keySpec.
     * @throws GordianException on error
     */
    private void checkKeySpec(final PKCS8EncodedKeySpec pKeySpec) throws GordianException {
        final GordianKeyPairSpec myKeySpec = theFactory.determineKeyPairSpec(pKeySpec);
        if (!theSpec.equals(myKeySpec)) {
            throw new GordianDataException("KeySpec not supported by this KeyPairGenerator");
        }
    }

    /**
     * Check keySpec.
     * @param pKeySpec the keySpec.
     * @throws GordianException on error
     */
    private void checkKeySpec(final X509EncodedKeySpec pKeySpec) throws GordianException {
        final GordianKeyPairSpec myKeySpec = theFactory.determineKeyPairSpec(pKeySpec);
        if (!theSpec.equals(myKeySpec)) {
            throw new GordianDataException("KeySpec not supported by this KeyPairGenerator");
        }
    }
}
