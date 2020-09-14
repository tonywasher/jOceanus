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

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core keyPairSet generator.
 */
public class GordianCoreKeyPairSetGenerator
    implements GordianKeyPairSetGenerator {
    /**
     * The keyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theSpec;

    /**
     * The list of generators.
     */
    private final List<GordianKeyPairGenerator> theGenerators;

    /**
     * Constructor.
     * @param pFactory the asymFactory.
     * @param pSpec the keyPairSetSpec.
     * @throws OceanusException on error
     */
    GordianCoreKeyPairSetGenerator(final GordianAsymFactory pFactory,
                                   final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Store the spec. */
        theSpec = pSpec;
        theGenerators = new ArrayList<>();

        /* Loop through the asymKeySpecs */
        final Iterator<GordianAsymKeySpec> myIterator = pSpec.iterator();
        while (myIterator.hasNext()) {
            final GordianAsymKeySpec mySpec = myIterator.next();

            /* create generator and add it to list */
            theGenerators.add(pFactory.getKeyPairGenerator(mySpec));
        }
    }

    @Override
    public GordianKeyPairSetSpec getKeyPairSetSpec() {
        return theSpec;
    }

    @Override
    public GordianKeyPairSet generateKeyPairSet() throws OceanusException {
        /* Create the new empty keyPairSet */
        final GordianCoreKeyPairSet myKeyPairSet = new GordianCoreKeyPairSet(theSpec);

        /* Loop through the generators */
        for (GordianKeyPairGenerator myGenerator : theGenerators) {
            /* create keyPair and add it to set */
            myKeyPairSet.addKeyPair(myGenerator.generateKeyPair());
        }

        /* Return the keyPairSet */
        return myKeyPairSet;
    }

    @Override
    public X509EncodedKeySpec getX509Encoding(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Create the new empty keyPairSet */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final GordianKeyPairSetPublicASN1 myASN1 = new GordianKeyPairSetPublicASN1(theSpec);

        /* Loop through the generators */
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianKeyPairGenerator myGenerator : theGenerators) {
            /* create encodedKeySpec and add it to set */
            myASN1.addKey(myGenerator.getX509Encoding(myIterator.next()));
        }

        /* Return the keyPairSet */
        return myASN1.getEncodedKeySpec();
    }

    @Override
    public PKCS8EncodedKeySpec getPKCS8Encoding(final GordianKeyPairSet pKeyPairSet) throws OceanusException {
        /* Create the new empty keyPairSet */
        final GordianCoreKeyPairSet mySet = (GordianCoreKeyPairSet) pKeyPairSet;
        final GordianKeyPairSetPrivateASN1 myASN1 = new GordianKeyPairSetPrivateASN1(theSpec);

        /* Loop through the generators */
        final Iterator<GordianKeyPair> myIterator = mySet.iterator();
        for (GordianKeyPairGenerator myGenerator : theGenerators) {
            /* create encodedKeySpec and add it to set */
            myASN1.addKey(myGenerator.getPKCS8Encoding(myIterator.next()));
        }

        /* Return the keyPairSet */
        return myASN1.getEncodedKeySpec();
    }

    @Override
    public GordianKeyPairSet deriveKeyPairSet(final X509EncodedKeySpec pPublicKeySet,
                                              final PKCS8EncodedKeySpec pPrivateKeySet) throws OceanusException {
        /* CheckKeySpecs */
        checkKeySpec(pPublicKeySet);
        checkKeySpec(pPrivateKeySet);

        /* Parse the ASN1 */
        byte[] myEncoded = pPublicKeySet.getEncoded();
        final GordianKeyPairSetPublicASN1 myPublic = GordianKeyPairSetPublicASN1.getInstance(myEncoded);
        myEncoded = pPrivateKeySet.getEncoded();
        final GordianKeyPairSetPrivateASN1 myPrivate = GordianKeyPairSetPrivateASN1.getInstance(myEncoded);

        /* Create the keySet */
        final GordianCoreKeyPairSet mySet = new GordianCoreKeyPairSet(theSpec);

        /* Loop through the keys */
        final Iterator<X509EncodedKeySpec> myPubIterator = myPublic.keyIterator();
        final Iterator<PKCS8EncodedKeySpec> myPrivIterator = myPrivate.keyIterator();
        for (GordianKeyPairGenerator myGenerator : theGenerators) {
            final X509EncodedKeySpec myX509 = myPubIterator.next();
            final PKCS8EncodedKeySpec myPKCS8 = myPrivIterator.next();

            /* derive the keyPair and add to set */
            mySet.addKeyPair(myGenerator.deriveKeyPair(myX509, myPKCS8));
        }

        /* return the set */
        return mySet;
    }

    @Override
    public GordianKeyPairSet derivePublicOnlyKeyPairSet(final X509EncodedKeySpec pPublicKeySet) throws OceanusException {
        /* CheckKeySpecs */
        checkKeySpec(pPublicKeySet);

        /* Parse the ASN1 */
        final byte[] myEncoded = pPublicKeySet.getEncoded();
        final GordianKeyPairSetPublicASN1 myPublic = GordianKeyPairSetPublicASN1.getInstance(myEncoded);

        /* Create the keySet */
        final GordianCoreKeyPairSet mySet = new GordianCoreKeyPairSet(theSpec);

        /* Loop through the keys */
        final Iterator<X509EncodedKeySpec> myIterator = myPublic.keyIterator();
        for (GordianKeyPairGenerator myGenerator : theGenerators) {
            final X509EncodedKeySpec myX509 = myIterator.next();

            /* derive the keyPair and add to set */
            mySet.addKeyPair(myGenerator.derivePublicOnlyKeyPair(myX509));
        }

        /* return the set */
        return mySet;
    }

    /**
     * Check keySpec.
     * @param pKeySpec the keySpec.
     * @throws OceanusException on error
     */
    private void checkKeySpec(final PKCS8EncodedKeySpec pKeySpec) throws OceanusException {
        final GordianKeyPairSetSpec myKeySpec = GordianKeyPairSetAlgId.determineKeyPairSetSpec(pKeySpec);
        if (!theSpec.equals(myKeySpec)) {
            throw new GordianDataException("KeySpec not supported by this KeyPairGenerator");
        }
    }

    /**
     * Check keySpec.
     * @param pKeySpec the keySpec.
     * @throws OceanusException on error
     */
    private void checkKeySpec(final X509EncodedKeySpec pKeySpec) throws OceanusException {
        final GordianKeyPairSetSpec myKeySpec = GordianKeyPairSetAlgId.determineKeyPairSetSpec(pKeySpec);
        if (!theSpec.equals(myKeySpec)) {
            throw new GordianDataException("KeySpec not supported by this KeyPairGenerator");
        }
    }
}
