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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamAEADCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymAEADCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryDigestSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryMacSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryRandomSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryRandomType;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryStreamPBECipherSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySymCipherSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySymKeySpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySymPBECipherSpec;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.OceanusDataConverter;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Security Test suite - Test Symmetric/Stream and Digest/MAC Algorithms.
 */
class SymmetricTest {
    /**
     * NanoSeconds in milliSeconds.
     */
    static final int MILLINANOS = 1000;

    /**
     * Run full profiles.
     */
    private static final boolean fullProfiles;

    /**
     * Run full profiles.
     */
    private static final int profileRepeat;

    /**
     * Configure the test according to system properties.
     */
    static {
        /* If this is a full build */
        final String myBuildType = System.getProperty("joceanus.fullBuild");
        if (myBuildType != null) {
            /* Test everything */
            fullProfiles=false;
            profileRepeat=5;

            /* else allow further configuration */
        } else {
            /* Access system properties */
            fullProfiles = System.getProperty("fullProfiles") != null;
            profileRepeat = fullProfiles
                            ? 100
                            : 5;
        }
    }

    /**
     * The factories.
     */
    private static GordianFactory BCFACTORY;
    private static GordianFactory JCAFACTORY;

    /**
     * Initialise Factories.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void createSecurityFactories() throws OceanusException {
        BCFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
        JCAFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.JCA);
    }

    /**
     * The TestData.
     */
    private byte[] theTestData;

    /**
     * The AADData.
     */
    private byte[] theAADData;

    /**
     * Create the symmetric test suite.
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> symmetricTests() {
        /* Create tests */
        final Stream<DynamicNode> myBC = symmetricTests(BCFACTORY, JCAFACTORY);
        final Stream<DynamicNode> myJCA = symmetricTests(JCAFACTORY, BCFACTORY);
        return Stream.concat(myBC, myJCA);
    }

    /**
     * Create the symmetric test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream
     */
    private Stream<DynamicNode> symmetricTests(final GordianFactory pFactory,
                                               final GordianFactory pPartner) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Add digest Tests */
        Stream<DynamicNode> mySubStream = digestTests(pFactory, pPartner);
        if (mySubStream != null) {
            myStream = Stream.concat(myStream, mySubStream);
        }

        /* Add mac Tests */
        mySubStream = macTests(pFactory, pPartner);
        myStream = Stream.concat(myStream, mySubStream);

        /* Add symKey Tests */
        mySubStream = symKeyTests(pFactory, pPartner);
        myStream = Stream.concat(myStream, mySubStream);

        /* Add streamKey Tests */
        mySubStream = streamKeyTests(pFactory, pPartner);
        myStream = Stream.concat(myStream, mySubStream);

        /* Add random Tests */
        mySubStream = randomTests(pFactory);
        myStream = Stream.concat(myStream, mySubStream);

        /* Return the stream */
        final String myName = pFactory.getFactoryType().toString();
        myStream = Stream.of(DynamicContainer.dynamicContainer(myName, myStream));
        return myStream;
    }

    /**
     * Create the digest test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream or null
     */
    private Stream<DynamicNode> digestTests(final GordianFactory pFactory,
                                            final GordianFactory pPartner) {
        /* Add digest Tests */
        List<FactoryDigestSpec> myDigests = SymmetricStore.digestProvider(pFactory, pPartner);
        if (!myDigests.isEmpty()) {
            Stream<DynamicNode> myTests = myDigests.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), digestTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer("Digests", myTests));
        }

        /* No digest Tests */
        return null;
    }

    /**
     * Create the digest test suite for a digestSpec.
     * @param pDigestSpec the digestSpec
     * @return the test stream
     */
    private Stream<DynamicNode> digestTests(final FactoryDigestSpec pDigestSpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileDigest(pDigestSpec)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkDigestAlgId(pDigestSpec))));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> checkExternalId(pDigestSpec))));

        /* Add partner test if the partner supports this digestSpec */
        if (pDigestSpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerDigest(pDigestSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the mac test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream
     */
    private Stream<DynamicNode> macTests(final GordianFactory pFactory,
                                         final GordianFactory pPartner) {
        /* Create the default stream */
        Stream<DynamicNode> myTests = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Build tests for this keyLength */
            final Stream<DynamicNode> myTest = macTests(pFactory, pPartner, myKeyLen);
            if (myTest != null) {
                myTests = Stream.concat(myTests, myTest);
            }
        }

        /* Return the tests */
        return Stream.of(DynamicContainer.dynamicContainer("Macs", myTests));
    }

    /**
     * Create the mac test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyLen the keyLength
     * @return the test stream or null
     */
    private Stream<DynamicNode> macTests(final GordianFactory pFactory,
                                         final GordianFactory pPartner,
                                         final GordianLength pKeyLen) {
        /* Add mac Tests */
        List<FactoryMacSpec> myMacs = SymmetricStore.macProvider(pFactory, pPartner, pKeyLen);
        if (!myMacs.isEmpty()) {
            Stream<DynamicNode> myTests = myMacs.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), macTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer(pKeyLen.toString(), myTests));
        }

        /* No mac Tests */
        return null;
    }

    /**
     * Create the mac test suite for a macSpec.
     * @param pMacSpec the macSpec
     * @return the test stream
     */
    private Stream<DynamicNode> macTests(final FactoryMacSpec pMacSpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileMac(pMacSpec)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkMacAlgId(pMacSpec))));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> checkExternalId(pMacSpec))));

        /* Add partner test if  the partner supports this macSpec */
        if (pMacSpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerMac(pMacSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the symKey test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> symKeyTests(final GordianFactory pFactory,
                                            final GordianFactory pPartner) {
        /* Create the default stream */
        Stream<DynamicNode> myTests = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Build tests for this keyLength */
            final Stream<DynamicNode> myTest = symKeyTests(pFactory, pPartner, myKeyLen);
            if (myTest != null) {
                myTests = Stream.concat(myTests, myTest);
            }
        }

        /* Return the tests */
        return Stream.of(DynamicContainer.dynamicContainer("symKeys", myTests));
    }

    /**
     * Create the symKey test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyLen the keyLength
     * @return the test stream or null
     */
    private Stream<DynamicNode> symKeyTests(final GordianFactory pFactory,
                                            final GordianFactory pPartner,
                                            final GordianLength pKeyLen) {
        /* Add symKey Test */
        List<FactorySymKeySpec> myKeys = SymmetricStore.symKeyProvider(pFactory, pPartner, pKeyLen);
        if (!myKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myKeys.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), symKeyTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer(pKeyLen.toString(), myTests));
        }

        /* No sym Tests */
        return null;
    }

    /**
     * Create the symKey test suite for a symKeySpec.
     * @param pKeySpec the keySpec
     * @return the test stream
     */
    private Stream<DynamicNode> symKeyTests(final FactorySymKeySpec pKeySpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileSymKey(pKeySpec)));

        /* Add modes test */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("checkModes",
                SymmetricStore.symCipherProvider(pKeySpec).stream().map(y -> DynamicContainer.dynamicContainer(y.toString(), symCipherTests(y)))
        )));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> checkExternalId(pKeySpec))));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmId", () -> checkSymKeyAlgId(pKeySpec))));

        /* Add wrapCipher test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("wrapCipher", () -> checkWrapCipher(pKeySpec))));

        /* Add partner test if  the partner supports this symKeySpec */
        if (pKeySpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerWrap", () -> checkPartnerWrapCipher(pKeySpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the symKey test suite for a symCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private Stream<DynamicNode> symCipherTests(final FactorySymCipherSpec pCipherSpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("cipher", () -> checkSymCipher(pCipherSpec)));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> checkExternalId(pCipherSpec))));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmId", () -> checkSymCipherAlgId(pCipherSpec))));

        /* Add partner test if  the partner supports this symCipherSpec */
        if (pCipherSpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerSymCipher(pCipherSpec))));
        }

        /* Add PBE tests, ignoring ECB/CBC with no padding */
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        if (!mySpec.getCipherMode().hasPadding()
                || !GordianPadding.NONE.equals(mySpec.getPadding())) {
            myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("PBE", symPBECipherTests(pCipherSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the symPBECipher test suite for a symCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private Stream<DynamicNode> symPBECipherTests(final FactorySymCipherSpec pCipherSpec) {
        /* Add PBE  tests */
        Stream<DynamicNode> myTests = Stream.empty();
        for (FactorySymPBECipherSpec myPBESpec : SymmetricStore.symPBECipherProvider(pCipherSpec)) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest(myPBESpec.toString(), () -> checkSymPBECipher(myPBESpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the streamKey test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream or null
     */
    private Stream<DynamicNode> streamKeyTests(final GordianFactory pFactory,
                                               final GordianFactory pPartner) {
        /* Create the default stream */
        Stream<DynamicNode> myTests = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Build tests for this keyLength */
            final Stream<DynamicNode> myTest = streamKeyTests(pFactory, pPartner, myKeyLen);
            if (myTest != null) {
                myTests = Stream.concat(myTests, myTest);
            }
        }

        /* Return the tests */
        return Stream.of(DynamicContainer.dynamicContainer("streamKeys", myTests));
    }

    /**
     * Create the streamKey test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyLen the keyLength
     * @return the test stream or null
     */
    private Stream<DynamicNode> streamKeyTests(final GordianFactory pFactory,
                                               final GordianFactory pPartner,
                                               final GordianLength pKeyLen) {
        /* Add streamKey Tests */
        List<FactoryStreamKeySpec> myKeys = SymmetricStore.streamKeyProvider(pFactory, pPartner, pKeyLen);
        if (!myKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myKeys.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), streamKeyTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer(pKeyLen.toString(), myTests));
        }

        /* No stream Tests */
        return null;
    }

    /**
     * Create the streamKey test suite for a streamKeySpec.
     * @param pKeySpec the keySpec
     * @return the test stream
     */
    private Stream<DynamicNode> streamKeyTests(final FactoryStreamKeySpec pKeySpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileStreamKey(pKeySpec)));

        /* Add cipher test */
        final FactoryStreamCipherSpec myCipherSpec = new FactoryStreamCipherSpec(pKeySpec, GordianStreamCipherSpecBuilder.stream(pKeySpec.getSpec()));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkCipher", () -> checkCipher(myCipherSpec))));

        /* Add externalId tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalKeyId", () -> checkExternalId(pKeySpec))));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalCipherId", () -> checkExternalId(myCipherSpec))));

        /* Add algorithmId tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmKeyId", () -> checkStreamKeyAlgId(pKeySpec))));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmCipherId", () -> checkStreamCipherAlgId(myCipherSpec))));

        /* Add partner test if  the partner supports this streamKeySpec */
        if (pKeySpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerStreamKey(pKeySpec))));
        }

        /* Add PBE tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("PBE", streamPBECipherTests(myCipherSpec))));

        /* Add AAD cipher tests if required */
        if (pKeySpec.hasAAD()) {
            final FactoryStreamCipherSpec myAADSpec = new FactoryStreamCipherSpec(pKeySpec, GordianStreamCipherSpecBuilder.stream(pKeySpec.getSpec(), true));
            myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("AAD", streamAADCipherTests(myAADSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the streamPBECipher test suite for a streamCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private Stream<DynamicNode> streamPBECipherTests(final FactoryStreamCipherSpec pCipherSpec) {
        /* Add PBE  tests */
        Stream<DynamicNode> myTests = Stream.empty();
        for (FactoryStreamPBECipherSpec mySpec : SymmetricStore.streamPBECipherProvider(pCipherSpec)) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest(mySpec.toString(), () -> checkStreamPBECipher(mySpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the streamPBECipher test suite for a streamCipherSpec.
     * @param pCipherSpec the cipherSpec
     * @return the test stream
     */
    private Stream<DynamicNode> streamAADCipherTests(final FactoryStreamCipherSpec pCipherSpec) {
        /* Build standard tests */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("checkCipher", () -> checkAADCipher(pCipherSpec)));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalCipherId", () -> checkExternalId(pCipherSpec))));
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("algorithmId", () -> checkStreamCipherAlgId(pCipherSpec))));

        /* Add PBE tests */
        myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("PBE", streamPBECipherTests(pCipherSpec))));

        /* Return the tests */
        return myTests;
    }

    /**
     * Create the random test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> randomTests(final GordianFactory pFactory) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the random types */
        for (GordianRandomType myType : GordianRandomType.values()) {
            /* If this type has symKeySpecs */
            if (myType.hasSymKeySpec()) {
                /* Create an empty stream */
                Stream<DynamicNode> myTypeStream = Stream.empty();

                /* Loop through the keyLengths */
                Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
                while (myIterator.hasNext()) {
                    final GordianLength myKeyLen = myIterator.next();

                    /* Access the specs */
                    FactoryRandomType myRandomType = SymmetricStore.randomProvider(pFactory, myType, myKeyLen);
                    final List<FactoryRandomSpec> mySpecs = myRandomType.getSpecs();
                    Stream<DynamicNode> myTests = mySpecs.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> checkRandomSpec(x)));
                    myTypeStream = Stream.concat(myTypeStream, Stream.of(DynamicContainer.dynamicContainer(myRandomType.toString(), myTests)));
                }
                myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myType.toString(), myTypeStream)));

                /* else simple randomType */
            } else {
                /* Access the specs */
                FactoryRandomType myRandomType = SymmetricStore.randomProvider(pFactory, myType);
                final List<FactoryRandomSpec> mySpecs = myRandomType.getSpecs();
                Stream<DynamicNode> myTests = mySpecs.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> checkRandomSpec(x)));
                myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myRandomType.toString(), myTests)));
            }
        }

        /* return random Tests */
        return Stream.of(DynamicContainer.dynamicContainer("randoms", myStream));
    }

    /**
     * Profile digest.
     * @param pDigestSpec the digest to profile
     */
    private void profileDigest(final FactoryDigestSpec pDigestSpec) throws OceanusException {
        /* Create the digest */
        final GordianFactory myFactory = pDigestSpec.getFactory();
        final GordianDigestSpec mySpec = pDigestSpec.getSpec();
        final GordianDigestFactory myDigestFactory = myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigestFactory.createDigest(mySpec);

        /* Check that the digestLength is correct */
        Assertions.assertEquals(mySpec.getDigestLength().getByteLength(), myDigest.getDigestSize(), "DigestLength incorrect");

        /* Loop 100 times */
        final byte[] myBytes = getDigestInput(mySpec);
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            myDigest.update(myBytes);
            myDigest.finish();
        }

        /* Calculate elapsed time */
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println(pDigestSpec.toString() + ":" + myElapsed);
        }
    }

    /**
     * Check partner digest.
     * @param pDigestSpec the digest to check
     */
    private void checkPartnerDigest(final FactoryDigestSpec pDigestSpec) throws OceanusException {
        /* Create the digests */
        final GordianFactory myFactory = pDigestSpec.getFactory();
        final GordianFactory myPartner = pDigestSpec.getPartner();
        final GordianDigestSpec mySpec = pDigestSpec.getSpec();
        final GordianDigestFactory myDigestFactory = myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigestFactory.createDigest(mySpec);
        final GordianDigestFactory myPartnerFactory = myPartner.getDigestFactory();
        final GordianDigest myPartnerDigest = myPartnerFactory.createDigest(mySpec);

        /* Calculate digests */
        final byte[] myBytes = getDigestInput(mySpec);
        myDigest.update(myBytes);
        final byte[] myFirst = myDigest.finish();
        myPartnerDigest.update(myBytes);
        final byte[] mySecond = myPartnerDigest.finish();

        /* Check that the digests match */
        Assertions.assertArrayEquals(myFirst, mySecond, "Digest misMatch");
    }

    /**
     * Obtain digest test input.
     * @param pDigestSpec the digestSpec
     * @return the input
     */
    private byte[] getDigestInput(final GordianDigestSpec pDigestSpec) {
        /* Obtain basic input */
        final byte[] myBytes = "DigestInput".getBytes();
        return pDigestSpec.getDigestType().supportsLargeData()
               ? myBytes
               : Arrays.copyOf(myBytes, pDigestSpec.getDigestState().getLength().getByteLength());
      }

    /**
     * Profile mac.
     * @param pMacSpec the mac to profile
     * @throws OceanusException on error
     */
    private void profileMac(final FactoryMacSpec pMacSpec) throws OceanusException {
        final GordianFactory myFactory = pMacSpec.getFactory();
        final GordianMacSpec mySpec = pMacSpec.getSpec();
        final GordianMacFactory myMacFactory = myFactory.getMacFactory();
        final GordianMac myMac1 = myMacFactory.createMac(pMacSpec.getSpec());
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();

        /* Check that the macLength is correct */
        Assertions.assertEquals(mySpec.getMacLength().getByteLength(), myMac1.getMacSize(), "MacLength incorrect");

        /* Define the input */
        final byte[] myBytes = "MacInput".getBytes();
        boolean isInconsistent = false;

        /* Access the two macs */
        final GordianMacType myType = mySpec.getMacType();
        final boolean twoMacs = GordianMacType.GMAC.equals(myType);
        final boolean needsReInit = myType.needsReInitialisation();
        final GordianMac myMac2 = twoMacs
                                  ? myMacFactory.createMac(mySpec)
                                  : myMac1;

        /* Start loop */
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            /* Use first mac */
            myMac1.init(GordianMacParameters.keyWithRandomNonce(myKey));
            myMac1.update(myBytes);
            final byte[] myFirst = myMac1.finish();

            /* If we need to reInitialise */
            if (needsReInit) {
                myMac2.init(GordianMacParameters.keyAndNonce(myKey, myMac1.getInitVector()));
            }

            /* Use second mac */
            myMac2.update(myBytes);
            final byte[] mySecond = myMac2.finish();
            if (!Arrays.areEqual(myFirst, mySecond)) {
                isInconsistent = true;
            }
        }

        /* Record elapsed */
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 2 * MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println(pMacSpec.toString() + ":" + myElapsed);
        }
        Assertions.assertFalse(isInconsistent, pMacSpec.toString() + " inconsistent");
    }

    /**
     * Check partner mac.
     * @param pMacSpec the mac to check
     */
    private void checkPartnerMac(final FactoryMacSpec pMacSpec) throws OceanusException {
        /* Create the macs */
        final GordianFactory myFactory = pMacSpec.getFactory();
        final GordianFactory myPartner = pMacSpec.getPartner();
        final GordianMacSpec mySpec = pMacSpec.getSpec();
        final GordianMacFactory myMacFactory = myFactory.getMacFactory();
        final GordianMac myMac = myMacFactory.createMac(mySpec);
        final GordianMacFactory myPartnerFactory = myPartner.getMacFactory();
        final GordianMac myPartnerMac = myPartnerFactory.createMac(mySpec);
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();
        final GordianKey<GordianMacSpec> myPartnerKey = pMacSpec.getPartnerKey();

        /* Calculate macs */
        final byte[] myBytes = "MacInput".getBytes();
        myMac.init(GordianMacParameters.keyWithRandomNonce(myKey));
        final byte[] myIV = myMac.getInitVector();
        myMac.update(myBytes);
        final byte[] myFirst = myMac.finish();
        if (myIV == null) {
            myPartnerMac.init(GordianMacParameters.key(myPartnerKey));
        } else {
            myPartnerMac.init(GordianMacParameters.keyAndNonce(myPartnerKey, myIV));
        }
        myPartnerMac.update(myBytes);
        final byte[] mySecond = myPartnerMac.finish();

        /* Check that the macs match */
        Assertions.assertArrayEquals(myFirst, mySecond, "Mac misMatch");
    }

    /**
     * Check symKey CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    @SuppressWarnings("unchecked")
    private void checkSymCipher(final FactorySymCipherSpec pCipherSpec) throws OceanusException {
        /* Split out AAD cipher */
        if (pCipherSpec.getSpec().isAAD()) {
            checkAADCipher(pCipherSpec);
            return;
        }

        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Create the Spec */
        final GordianCoreCipher<GordianSymKeySpec> myCipher = (GordianCoreCipher<GordianSymKeySpec>) myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.init(true, myParms);
        if (!mySpec.getCipherMode().hasPadding()
                || !GordianPadding.NONE.equals(mySpec.getPadding())) {
            /* Check encryption */
            final byte[] myIV = myCipher.getInitVector();
            myParms = GordianCipherParameters.keyAndNonce(myKey, myIV);
            final byte[] myEncrypted = myCipher.finish(myTestData);
            final byte[] myEncrypted2 = myCipher.finish(myTestData);
            myCipher.init(false, myParms);
            final byte[] myResult = myCipher.finish(myEncrypted);
            myCipher.init(false, myParms);
            final byte[] myResult2 = myCipher.finish(myEncrypted2);
            Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
            Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
        } else {
            /* Check that the blockLength is correct */
            Assertions.assertEquals(mySpec.getBlockLength().getByteLength(), myCipher.getBlockSize(), "BlockLength incorrect");
        }
    }

    /**
     * Check symKey PBE CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkSymPBECipher(final FactorySymPBECipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final FactorySymCipherSpec myOwner = pCipherSpec.getOwner();
        final GordianSymCipherSpec myCipherSpec = myOwner.getSpec();
        final GordianPBESpec myPBESpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();

        /* Access Data */
        final byte[] myTestData = getTestData();
        final char[] myPassword = "HelloThere".toCharArray();

        /* Create the Spec */
        final GordianSymCipher myCipher = myCipherFactory.createSymKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.pbe(myPBESpec, myPassword);
        myCipher.initForEncrypt(myParms);

        /* Check encryption */
        final byte[] mySalt = myCipher.getPBESalt();
        myParms = GordianCipherParameters.pbeAndNonce(myPBESpec, myPassword, mySalt);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check partner symKey CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkPartnerSymCipher(final FactorySymCipherSpec pCipherSpec) throws OceanusException {
        /* Split out AAD cipher */
        if (pCipherSpec.getSpec().isAAD()) {
            checkPartnerAADCipher(pCipherSpec);
            return;
        }

        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianFactory myPartner = pCipherSpec.getPartner();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();
        final GordianKey<GordianSymKeySpec> myPartnerKey = pCipherSpec.getPartnerKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Create the Spec */
        final GordianKeyedCipher<GordianSymKeySpec> myCipher = myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        if (!mySpec.getCipherMode().hasPadding()
                || !GordianPadding.NONE.equals(mySpec.getPadding())) {
            /* Check encryption */
            final byte[] myIV = myCipher.getInitVector();
            myParms = GordianCipherParameters.keyAndNonce(myPartnerKey, myIV);
            final byte[] myEncrypted = myCipher.finish(myTestData);
            final GordianKeyedCipher<GordianSymKeySpec> myPartnerCipher = myPartnerFactory.createSymKeyCipher(mySpec);
            myPartnerCipher.initForDecrypt(myParms);
            final byte[] myResult = myPartnerCipher.finish(myEncrypted);
            Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        }
    }

    /**
     * Check AAD cipher mode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkAADCipher(final FactorySymCipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();
        final byte[] myAADData = getAADData();
        final GordianSymAEADCipher myCipher = (GordianSymAEADCipher) myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        byte[] myEncrypted2 = null;
        if (!mySpec.getCipherMode().needsReInitialisation()) {
            myCipher.updateAAD(myAADData);
            myEncrypted2 = myCipher.finish(myTestData);
        }
        myParms = GordianCipherParameters.aeadAndNonce(myKey, myAADData, myIV);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        if (myEncrypted2 != null) {
            myCipher.initForDecrypt(myParms);
            final byte[] myResult2 = myCipher.finish(myEncrypted2);
            Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
        }
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check Partner AAD cipher mode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkPartnerAADCipher(final FactorySymCipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianFactory myPartner = pCipherSpec.getPartner();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();
        final GordianKey<GordianSymKeySpec> myPartnerKey = pCipherSpec.getPartnerKey();

        /* Encrypt Data */
        final byte[] myTestData = getTestData();
        final byte[] myAADData = getAADData();
        final GordianSymAEADCipher myCipher = (GordianSymAEADCipher) myCipherFactory.createSymKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);

        /* Decrypt data at partner */
        final GordianSymAEADCipher myPartnerCipher = (GordianSymAEADCipher) myPartnerFactory.createSymKeyCipher(mySpec);
        myParms = GordianCipherParameters.keyAndNonce(myPartnerKey, myIV);
        myPartnerCipher.initForDecrypt(myParms);
        myPartnerCipher.updateAAD(myAADData);
        final byte[] myResult = myPartnerCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check stream cipher.
     * @param pCipherSpec the keySpec
     * @throws OceanusException on error
     */
    private void checkCipher(final FactoryStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final FactoryStreamKeySpec myOwner = pCipherSpec.getOwner();
        final GordianFactory myFactory = myOwner.getFactory();
        final GordianStreamKeySpec myKeySpec = myOwner.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeySpec> myStreamKey = myOwner.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Create the Cipher */
        final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(myKeySpec);
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myStreamKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myParms = GordianCipherParameters.keyAndNonce(myStreamKey, myIV);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        if (myCipherSpec.getKeyType().getStreamKeyType().needsReInit()) {
            myCipher.initForEncrypt(myParms);
        }
        final byte[] myEncrypted2 = myCipher.finish(myTestData);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult2 = myCipher.finish(myEncrypted2);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
    }

    /**
     * Check streamKey PBE CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkStreamPBECipher(final FactoryStreamPBECipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final FactoryStreamCipherSpec myOwner = pCipherSpec.getOwner();
        final GordianStreamCipherSpec myCipherSpec = myOwner.getSpec();
        final GordianPBESpec myPBESpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();

        /* Access Data */
        final byte[] myTestData = getTestData();
        final char[] myPassword = "HelloThere".toCharArray();

        /* Create the Spec */
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.pbe(myPBESpec, myPassword);
        myCipher.initForEncrypt(myParms);

        /* Check encryption */
        final byte[] mySalt = myCipher.getPBESalt();
        myParms = GordianCipherParameters.pbeAndNonce(myPBESpec, myPassword, mySalt);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check AAD cipher mode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkAADCipher(final FactoryStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianStreamCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();
        final byte[] myAADData = getAADData();
        final GordianStreamAEADCipher myCipher = (GordianStreamAEADCipher) myCipherFactory.createStreamKeyCipher(mySpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myParms = GordianCipherParameters.aeadAndNonce(myKey, myAADData, myIV);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult = myCipher.finish(myEncrypted);
        myCipher.initForDecrypt(myParms);
        final byte[] myResult2 = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myResult, myResult2, "Failed to reset properly");
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
    }

    /**
     * Check partner streamKey.
     * @param pKeySpec the streamKey to check
     */
    private void checkPartnerStreamKey(final FactoryStreamKeySpec pKeySpec) throws OceanusException {
        /* Create the macs */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianFactory myPartner = pKeySpec.getPartner();
        final GordianStreamKeySpec mySpec = pKeySpec.getSpec();
        final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(mySpec);
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianStreamCipher myPartnerCipher = myPartnerFactory.createStreamKeyCipher(myCipherSpec);
        final GordianKey<GordianStreamKeySpec> myKey = pKeySpec.getKey();
        final GordianKey<GordianStreamKeySpec> myPartnerKey = pKeySpec.getPartnerKey();

        /* Create message and buffers  */
        final byte[] myBytes = getTestData();

        /* Encrypt and decrypt the message */
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myKey);
        myCipher.initForEncrypt(myParms);
        final byte[] myEncrypted = myCipher.finish(myBytes);
        final byte[] myIV = myCipher.getInitVector();
        myPartnerCipher.initForDecrypt(GordianCipherParameters.keyAndNonce(myPartnerKey, myIV));
        final byte[] myDecrypted = myPartnerCipher.finish(myEncrypted);

        /* Check that the decryption worked */
        Assertions.assertArrayEquals(myBytes, myDecrypted, "cipher misMatch");
    }

    /**
     * Check wrap cipher.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkWrapCipher(final FactorySymKeySpec pKeySpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Check wrapping bytes */
        final GordianCoreWrapper myWrapper = (GordianCoreWrapper) myCipherFactory.createKeyWrapper(mySymKey);
        byte[] myWrapped = myWrapper.secureBytes( myTestData);
        final byte[] myResult = myWrapper.deriveBytes(myWrapped);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to wrap/unwrap bytes");
        Assertions.assertEquals(myWrapper.getDataWrapLength(myTestData.length), myWrapped.length, "Incorrect wrapped length");

        /* Check wrapping key */
        myWrapped = myWrapper.secureKey(mySymKey);
        final GordianKey<GordianSymKeySpec> myResultKey = myWrapper.deriveKey(myWrapped, mySymKey.getKeyType());
        Assertions.assertEquals(mySymKey, myResultKey, "Failed to wrap/unwrap key");
        Assertions.assertEquals(myWrapper.getKeyWrapLength(mySymKey), myWrapped.length, "Incorrect wrapped length");
    }

    /**
     * Check partner wrap cipher.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkPartnerWrapCipher(final FactorySymKeySpec pKeySpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianFactory myPartner = pKeySpec.getPartner();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianCipherFactory myPartnerFactory = myPartner.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();
        final GordianKey<GordianSymKeySpec> myPartnerKey = pKeySpec.getPartnerKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Check wrapping bytes */
        final GordianCoreWrapper myWrapper = (GordianCoreWrapper) myCipherFactory.createKeyWrapper(mySymKey);
        byte[] myWrapped = myWrapper.secureBytes(myTestData);
        final GordianCoreWrapper myPartnerWrapper = (GordianCoreWrapper) myPartnerFactory.createKeyWrapper(myPartnerKey);
        final byte[] myResult = myPartnerWrapper.deriveBytes(myWrapped);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to wrap/unwrap bytes");
        Assertions.assertEquals(myWrapper.getDataWrapLength(myTestData.length), myWrapped.length, "Incorrect wrapped length");

        /* Check wrapping key */
        myWrapped = myWrapper.secureKey(mySymKey);
        final GordianKey<GordianSymKeySpec> myResultKey = myPartnerWrapper.deriveKey(myWrapped, mySymKey.getKeyType());
        Assertions.assertEquals(myPartnerKey, myResultKey, "Failed to wrap/unwrap key");
        Assertions.assertEquals(myWrapper.getKeyWrapLength(mySymKey), myWrapped.length, "Incorrect wrapped length");
    }

    /**
     * Profile symKey.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void profileSymKey(final FactorySymKeySpec pKeySpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianSymKeySpec mySpec = pKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();
        final int myLen = mySpec.getBlockLength().getByteLength();

        /* Build the cipher */
        byte[] myBytes = new byte[myLen];
        final GordianSymCipherSpec myCipherSpec = new GordianSymCipherSpec(mySpec, GordianCipherMode.ECB, GordianPadding.NONE);
        final GordianSymCipher myCipher = myCipherFactory.createSymKeyCipher(myCipherSpec);

        /* Start loop */
        final long myStart = System.nanoTime();
        GordianCipherParameters myParms = GordianCipherParameters.key(mySymKey);
        for (int i = 0; i < profileRepeat; i++) {
            myCipher.initForEncrypt(myParms);
            myBytes = myCipher.finish(myBytes);
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println(mySpec.toString() + ":" + myElapsed);
        }
    }

    /**
     * Profile streamKey.
     * @param pStreamKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void profileStreamKey(final FactoryStreamKeySpec pStreamKeySpec) throws OceanusException {
        final GordianFactory myFactory = pStreamKeySpec.getFactory();
        final GordianStreamKeySpec myKeySpec = pStreamKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeySpec> myStreamKey = pStreamKeySpec.getKey();
        byte[] myBytes = getTestData();
        final GordianStreamCipherSpec myCipherSpec = GordianStreamCipherSpecBuilder.stream(myKeySpec);
        final GordianStreamCipher myCipher = myCipherFactory.createStreamKeyCipher(myCipherSpec);
        GordianCipherParameters myParms = GordianCipherParameters.keyWithRandomNonce(myStreamKey);
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            myCipher.initForEncrypt(myParms);
            myBytes = myCipher.finish(myBytes);
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println(myKeySpec.toString() + ":" + myElapsed);
        }
    }

    /**
     * check randomSpec.
     * @param pRandomSpec the randomSpec
     * @throws OceanusException on error
     */
    private void checkRandomSpec(final FactoryRandomSpec pRandomSpec) throws OceanusException {
        /* Create the secure random */
        final GordianFactory myFactory = pRandomSpec.getFactory();
        final GordianRandomSpec mySpec = pRandomSpec.getSpec();
        final GordianRandomFactory myRandomFactory = myFactory.getRandomFactory();
        final SecureRandom myRandom = myRandomFactory.createRandom(mySpec);

        /* Generate some random bytes */
        final int myLen = 128;
        byte[] myBytes = new byte[myLen];
        myRandom.nextBytes(myBytes);
    }

    /**
     * Check externalId.
     * @param pSpec the Spec to check
     * @throws OceanusException on error
     */
    private void checkExternalId(final FactorySpec<? extends GordianIdSpec> pSpec) throws OceanusException {
        /* Access the factories */
        final GordianKnuthObfuscater myKnuth = pSpec.getFactory().getObfuscater();

        /* Check standard obfuscation */
        int myId = myKnuth.deriveExternalIdFromType(pSpec.getSpec());
        GordianIdSpec myResult = myKnuth.deriveTypeFromExternalId(myId);
        Assertions.assertEquals(pSpec.getSpec(), myResult,
                "Standard obfuscation for " + pSpec.getClass().getSimpleName() + ":" + pSpec);

        /* Check offset obfuscation */
        final int myOffset = 205;
        myId = myKnuth.deriveExternalIdFromType(pSpec.getSpec(), myOffset);
        myResult = myKnuth.deriveTypeFromExternalId(myId, myOffset);
        Assertions.assertEquals(pSpec.getSpec(), myResult,
                "Offset obfuscation for " + pSpec.getClass().getSimpleName() + ":" + pSpec);
    }

    /**
     * Check digestAlgId.
     * @param pSpec the Spec to check
     */
    private void checkDigestAlgId(final FactoryDigestSpec pSpec) {
        /* Access the factory */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pSpec.getFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianDigestSpec mySpec = myFactory.getDigestSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check keyAlgId.
     * @param pSpec the Spec to check
     */
    private void checkSymKeyAlgId(final FactorySymKeySpec pSpec) {
        /* Access the factory */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pSpec.getFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianKeySpec mySpec = myFactory.getKeySpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check cipherAlgId.
     * @param pSpec the Spec to check
     */
    private void checkSymCipherAlgId(final FactorySymCipherSpec pSpec) {
        /* Access the factory */
        final GordianCoreCipherFactory myFactory = (GordianCoreCipherFactory) pSpec.getFactory().getCipherFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianCipherSpec<?> mySpec = myFactory.getCipherSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check keyAlgId.
     * @param pSpec the Spec to check
     */
    private void checkStreamKeyAlgId(final FactoryStreamKeySpec pSpec) {
        /* Access the factory */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pSpec.getFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianKeySpec mySpec = myFactory.getKeySpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check streamCipherAlgId.
     * @param pSpec the Spec to check
     */
    private void checkStreamCipherAlgId(final FactoryStreamCipherSpec pSpec) {
        /* Access the factory */
        final GordianCoreCipherFactory myFactory = (GordianCoreCipherFactory) pSpec.getFactory().getCipherFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianCipherSpec<?> mySpec = myFactory.getCipherSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check macAlgId.
     * @param pSpec the Spec to check
     */
    private void checkMacAlgId(final FactoryMacSpec pSpec) {
        /* Access the factory */
        final GordianCoreFactory myFactory = (GordianCoreFactory) pSpec.getFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianKeySpec mySpec = myFactory.getKeySpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Obtain the testData.
     * @return the testData
     */
    private byte[] getTestData() {
        if (theTestData == null) {
            /* Needs to be larger than the largest block size to enable CTS Mode to work */
            theTestData = new byte[GordianLength.LEN_1024.getByteLength() + 1];
            final SecureRandom myRandom = new SecureRandom();
            myRandom.nextBytes(theTestData);
        }
        return theTestData;
    }

    /**
     * Obtain the aadData.
     * @return the aadData
     */
    private byte[] getAADData() {
        if (theAADData == null) {
            theAADData = OceanusDataConverter.stringToByteArray("SomeAADBytes");
        }
        return theAADData;
    }
}
