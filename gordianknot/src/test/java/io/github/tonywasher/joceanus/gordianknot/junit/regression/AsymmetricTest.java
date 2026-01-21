/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianStateAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySetFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.factory.GordianCoreAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeySpec;
import io.github.tonywasher.joceanus.gordianknot.util.GordianGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.stream.Stream;

/**
 * Security Test suite - Test Asymmetric functionality.
 */
class AsymmetricTest {
    /**
     * The factories.
     */
    private static GordianFactory fcBCFACTORY;
    private static GordianFactory fcJCAFACTORY;

    /**
     * Random source.
     */
    static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Test buffer size.
     */
    static final int TESTLEN = 1024;

    /**
     * Perform setup operations.
     */
    @BeforeAll
    static void setUp() throws GordianException {
        AsymmetricStore.parseOptions();
        createSecurityFactories();
        AsymmetricXAgreeScripts.createSecuritySigners(fcBCFACTORY, fcJCAFACTORY);
    }

    /**
     * Initialise Factories.
     *
     * @throws GordianException on error
     */
    private static void createSecurityFactories() throws GordianException {
        /* Create the factories */
        fcBCFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
        fcJCAFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.JCA);
    }

    /**
     * Create the bouncyCastle asymmetric test suite.
     *
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> bouncycastle() {
        return asymmetricTests(fcBCFACTORY, fcJCAFACTORY);
    }

    /**
     * Create the jca asymmetric test suite.
     *
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> jca() {
        return asymmetricTests(fcJCAFACTORY, fcBCFACTORY);
    }

    /**
     * Create the asymmetric test suite for a factory.
     *
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @return the test stream
     */
    Stream<DynamicNode> asymmetricTests(final GordianFactory pFactory,
                                        final GordianFactory pPartner) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the possible keySpecs */
        for (final FactoryKeySpec myKeySpec : AsymmetricStore.keySpecProvider(pFactory, pPartner)) {
            /* Create a stream */
            Stream<DynamicNode> myKeyStream = Stream.of(DynamicTest.dynamicTest("generate", () -> generateKeyPairs(myKeySpec)));
            myKeyStream = Stream.concat(myKeyStream, Stream.of(DynamicTest.dynamicTest("keySpec", () -> checkKeyPair(myKeySpec))));
            myKeyStream = Stream.concat(myKeyStream, Stream.of(DynamicTest.dynamicTest("keyWrap", () -> checkKeyWrap(myKeySpec))));

            /* Add signature Tests */
            AsymmetricStore.signatureProvider(myKeySpec);
            if (!myKeySpec.getSignatures().isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.getSignatures().stream()
                        .map(x -> DynamicContainer.dynamicContainer(x.toString(), AsymmetricSignScripts.signatureTests(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Signatures", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add agreement Tests */
            AsymmetricStore.agreementProvider(myKeySpec);
            if (!myKeySpec.getAgreements().isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.getAgreements().stream()
                        .map(x -> DynamicContainer.dynamicContainer(x.toString(), AsymmetricXAgreeScripts.xAgreementTests(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("XAgreements", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add encryptor Tests */
            AsymmetricStore.encryptorProvider(myKeySpec);
            if (!myKeySpec.getEncryptors().isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.getEncryptors().stream()
                        .map(x -> DynamicContainer.dynamicContainer(x.toString(), AsymmetricEncryptScripts.encryptorTests(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Encryptors", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add the stream */
            myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myKeySpec.getKeySpec().toString(), myKeyStream)));
        }

        /* Return the stream */
        return myStream;
    }

    /**
     * Generate KeyPairs.
     *
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    private void generateKeyPairs(final FactoryKeySpec pKeySpec) throws GordianException {
        /* Access the keyPairs */
        final FactoryKeyPairs myPairs = pKeySpec.getKeyPairs();

        /* Force creation of the pairs */
        myPairs.getKeyPair();
        myPairs.getMirrorKeyPair();
        myPairs.getPartnerSelfKeyPair();
        myPairs.getPartnerTargetKeyPair();
        myPairs.getTargetKeyPair();
    }

    /**
     * Check KeyPair.
     *
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    private void checkKeyPair(final FactoryKeySpec pKeySpec) throws GordianException {
        /* Access the keyPairs */
        final FactoryKeyPairs myPairs = pKeySpec.getKeyPairs();
        final GordianKeyPairFactory myFactory = pKeySpec.getFactory().getKeyPairFactory();
        final GordianKeyPairSpec mySpec = pKeySpec.getKeySpec();

        /* Check X509Encodings */
        final X509EncodedKeySpec myPublic = myPairs.getX509Encoding();
        Assertions.assertEquals(mySpec, myFactory.determineKeyPairSpec(myPublic), "X509 has wrong keySpec");

        /* Check PKCS8Encodings */
        final PKCS8EncodedKeySpec myPrivate = myPairs.getPKCS8Encoding();
        Assertions.assertEquals(mySpec, myFactory.determineKeyPairSpec(myPrivate), "PKCS8 has wrong keySpec");

        /* Derive identical keyPair */
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myMirror = myPairs.getMirrorKeyPair();
        Assertions.assertEquals(myPair, myMirror, "Derived pair has wrong keySpec");

        /* If the keyPair is stateAware */
        if (mySpec.isStateAware()) {
            /* Check for StateAware */
            Assertions.assertInstanceOf(GordianStateAwareKeyPair.class, myPair, "Pair");
            Assertions.assertInstanceOf(GordianStateAwareKeyPair.class, myMirror, "Mirror");
            Assertions.assertInstanceOf(GordianStateAwareKeyPair.class, myPairs.getTargetKeyPair(), "Target");
            Assertions.assertInstanceOf(GordianStateAwareKeyPair.class, myPairs.getPartnerSelfKeyPair(), "PartnerSelf");
            Assertions.assertInstanceOf(GordianStateAwareKeyPair.class, myPairs.getPartnerTargetKeyPair(), "PartnerTarget");
        }
    }

    /**
     * Check KeyPair.
     *
     * @param pKeySpec the keySpec
     * @throws GordianException on error
     */
    private void checkKeyWrap(final FactoryKeySpec pKeySpec) throws GordianException {
        /* Access the keyPairs */
        final FactoryKeyPairs myPairs = pKeySpec.getKeyPairs();
        final GordianCoreAsyncFactory myFactory = (GordianCoreAsyncFactory) pKeySpec.getFactory();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final X509EncodedKeySpec myPublic = myPairs.getX509Encoding();

        /* Create a keySet */
        final GordianKeySetFactory myKeySetFactory = myFactory.getFactory().getKeySetFactory();
        final GordianKeySet myKeySet = myKeySetFactory.generateKeySet(AsymmetricXAgreeScripts.KEYSETSPEC);
        final byte[] mySecured = myKeySet.securePrivateKey(myPair);
        final GordianKeyPair myDerived = myKeySet.deriveKeyPair(myPublic, mySecured);
        Assertions.assertEquals(myPair, myDerived, "Incorrect derived pair");
        Assertions.assertEquals(myKeySet.getPrivateKeyWrapLength(myPair), mySecured.length, "Incorrect wrapped length");
    }
}
