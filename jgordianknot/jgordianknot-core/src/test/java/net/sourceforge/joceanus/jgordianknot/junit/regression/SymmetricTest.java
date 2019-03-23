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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Stream;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactoryDigestSpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactoryMacSpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactoryRandomSpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactoryRandomType;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactorySpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactoryStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactoryStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactorySymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.SymmetricStore.FactorySymKeySpec;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite - Test Symmetric/Stream and Digest/MAC Algorithms.
 */
public class SymmetricTest {
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
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> symmetricTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = symmetricTests(GordianLength.LEN_256, GordianFactoryType.BC);
        myStream = Stream.concat(myStream, symmetricTests(GordianLength.LEN_192, GordianFactoryType.BC));
        myStream = Stream.concat(myStream, symmetricTests(GordianLength.LEN_128, GordianFactoryType.BC));
        myStream = Stream.concat(myStream, symmetricTests(GordianLength.LEN_256, GordianFactoryType.JCA));
        myStream = Stream.concat(myStream, symmetricTests(GordianLength.LEN_192, GordianFactoryType.JCA));
        return Stream.concat(myStream, symmetricTests(GordianLength.LEN_128, GordianFactoryType.JCA));
    }

    /**
     * Create the symmetric test suite for a factory.
     * @param pKeyLen the factory keyLength
     * @param pType the factoryType
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> symmetricTests(final GordianLength pKeyLen,
                                               final GordianFactoryType pType) throws OceanusException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createFactory(new GordianParameters(pKeyLen, pType));

        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Add digest Tests */
        Stream<DynamicNode> mySubStream = digestTests(myFactory);
        if (mySubStream != null) {
            myStream = Stream.concat(myStream, mySubStream);
        }

        /* Add mac Tests */
        mySubStream = macTests(myFactory);
        if (mySubStream != null) {
            myStream = Stream.concat(myStream, mySubStream);
        }

        /* Add symKey Tests */
        mySubStream = symKeyTests(myFactory);
        if (mySubStream != null) {
            myStream = Stream.concat(myStream, mySubStream);
        }

        /* Add streamKey Tests */
        mySubStream = streamKeyTests(myFactory);
        if (mySubStream != null) {
            myStream = Stream.concat(myStream, mySubStream);
        }

        /* Add random Tests */
        mySubStream = randomTests(myFactory);
        myStream = Stream.concat(myStream, mySubStream);

        /* Return the stream */
        final String myName = pType.toString() + "-" + pKeyLen;
        myStream = Stream.of(DynamicContainer.dynamicContainer(myName, myStream));
        return myStream;
    }

    /**
     * Create the digest test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> digestTests(final GordianFactory pFactory) {
        /* Add digest Tests */
        List<FactoryDigestSpec> myDigests = SymmetricStore.digestProvider(pFactory);
        if (!myDigests.isEmpty()) {
            Stream<DynamicNode> myTests = myDigests.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), Stream.of(
                        DynamicTest.dynamicTest("profile", () -> profileDigest(x)),
                        DynamicTest.dynamicTest("algID", () -> checkDigestAlgId(x)),
                        DynamicTest.dynamicTest("externalID", () -> checkExternalId(x)))
                     ));
            return Stream.of(DynamicContainer.dynamicContainer("Digests", myTests));
        }

        /* No digest Tests */
        return null;
    }

    /**
     * Create the mac test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> macTests(final GordianFactory pFactory) {
        /* Add mac Tests */
        List<FactoryMacSpec> myMacs = SymmetricStore.macProvider(pFactory);
        if (!myMacs.isEmpty()) {
            Stream<DynamicNode> myTests = myMacs.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), Stream.of(
                    DynamicTest.dynamicTest("profile", () -> profileMac(x)),
                    DynamicTest.dynamicTest("algID", () -> checkMacAlgId(x)),
                    DynamicTest.dynamicTest("externalID", () -> checkExternalId(x)))
            ));
            return Stream.of(DynamicContainer.dynamicContainer("Macs", myTests));
        }

        /* No mac Tests */
        return null;
    }

    /**
     * Create the symKey test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> symKeyTests(final GordianFactory pFactory) {
        /* Add symKey Test */
        List<FactorySymKeySpec> myKeys = SymmetricStore.symKeyProvider(pFactory);
        if (!myKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myKeys.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), Stream.of(
                    DynamicTest.dynamicTest("profile", () -> profileSymKey(x)),
                    DynamicContainer.dynamicContainer("checkModes",
                            SymmetricStore.symCipherProvider(x).stream().map(y -> DynamicTest.dynamicTest(y.toString(), () -> checkSymCipher(y)))
                    ),
                    DynamicTest.dynamicTest("checkWrapCipher", () -> checkWrapCipher(x)),
                    DynamicTest.dynamicTest("externalID", () -> checkExternalId(x)))
            ));
            return Stream.of(DynamicContainer.dynamicContainer("symKeys", myTests));
        }

        /* No sym Tests */
        return null;
    }

    /**
     * Create the streamKey test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> streamKeyTests(final GordianFactory pFactory) {
        /* Add streamKey Tests */
        List<FactoryStreamKeyType> myKeys = SymmetricStore.streamKeyProvider(pFactory);
        if (!myKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myKeys.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), Stream.of(
                    DynamicTest.dynamicTest("profile", () -> profileStreamKey(x)),
                    DynamicTest.dynamicTest("checkCipher", () -> checkCipher(x)),
                    DynamicTest.dynamicTest("externalID", () -> checkExternalId(x)))
            ));
            return Stream.of(DynamicContainer.dynamicContainer("streamKeys", myTests));
        }

        /* No stream Tests */
        return null;
    }

    /**
     * Create the random test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> randomTests(final GordianFactory pFactory) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the possible keySpecs */
        for (final FactoryRandomType myType : SymmetricStore.randomProvider(pFactory)) {
            /* Access the specs */
            List<FactoryRandomSpec> mySpecs = myType.getSpecs();
            Stream<DynamicNode> myTests = mySpecs.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> checkRandomSpec(x)));
            myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myType.toString(), myTests)));
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
        final byte[] myBytes = "DigestInput".getBytes();
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
            myMac1.initMac(myKey);
            myMac1.update(myBytes);
            final byte[] myFirst = myMac1.finish();

            /* If we need to reInitialise */
            if (needsReInit) {
                myMac2.initMac(myKey, myMac1.getInitVector());
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
     * Check symKey CipherMode.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
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
        myCipher.initCipher(myKey);
        if (!mySpec.getCipherMode().hasPadding()
                || !GordianPadding.NONE.equals(mySpec.getPadding())) {
            /* Check encryption */
            final byte[] myIV = myCipher.getInitVector();
            final byte[] myEncrypted = myCipher.finish(myTestData);
            myCipher.initCipher(myKey, myIV, false);
            final byte[] myResult = myCipher.finish(myEncrypted);
            Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        } else {
            /* Check that the blockLength is correct */
            Assertions.assertEquals(mySpec.getBlockLength().getByteLength(), myCipher.getBlockSize(), "BlockLength incorrect");
        }

        /* Check the external ID */
        checkExternalId(pCipherSpec);
        checkSymCipherAlgId(pCipherSpec);
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
        final GordianAADCipher myCipher = myCipherFactory.createAADCipher(mySpec);
        myCipher.initCipher(myKey);
        final byte[] myIV = myCipher.getInitVector();
        myCipher.updateAAD(myAADData);
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initCipher(myKey, myIV, false);
        myCipher.updateAAD(myAADData);
        final byte[] myResult = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        checkExternalId(pCipherSpec);
        checkSymCipherAlgId(pCipherSpec);
    }

    /**
     * Check stream cipher.
     * @param pStreamKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkCipher(final FactoryStreamKeyType pStreamKeySpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pStreamKeySpec.getFactory();
        final GordianStreamKeyType myType = pStreamKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeyType> myStreamKey = pStreamKeySpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Create the Cipher */
        final GordianStreamCipherSpec mySpec = GordianStreamCipherSpec.stream(myType);
        final GordianCipher<GordianStreamKeyType> myCipher = myCipherFactory.createStreamKeyCipher(mySpec);
        myCipher.initCipher(myStreamKey);
        final byte[] myIV = myCipher.getInitVector();
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initCipher(myStreamKey, myIV, false);
        final byte[] myResult = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        final FactoryStreamCipherSpec myCipherSpec = new FactoryStreamCipherSpec(pStreamKeySpec, mySpec);
        checkExternalId(myCipherSpec);
        checkStreamCipherAlgId(myCipherSpec);
    }

    /**
     * Check wrap cipher.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkWrapCipher(final FactorySymKeySpec pKeySpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianSymKeySpec mySpec = pKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Check wrapping bytes */
        final GordianCoreWrapper myWrapper = (GordianCoreWrapper) myCipherFactory.createKeyWrapper(mySpec);
        byte[] myWrapped = myWrapper.secureBytes(mySymKey, myTestData);
        final byte[] myResult = myWrapper.deriveBytes(mySymKey, myWrapped);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to wrap/unwrap bytes");
        Assertions.assertEquals(myWrapper.getDataWrapLength(myTestData.length), myWrapped.length, "Incorrect wrapped length");

        /* Check wrapping key */
        myWrapped = myWrapper.secureKey(mySymKey, mySymKey);
        final GordianKey<GordianSymKeySpec> myResultKey = myWrapper.deriveKey(mySymKey, myWrapped, mySymKey.getKeyType());
        Assertions.assertEquals(mySymKey, myResultKey, "Failed to wrap/unwrap key");
        Assertions.assertEquals(myWrapper.getKeyWrapLength(), myWrapped.length, "Incorrect wrapped length");
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
        final GordianCipher<GordianSymKeySpec> myCipher = myCipherFactory.createSymKeyCipher(myCipherSpec);

        /* Start loop */
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            myCipher.initCipher(mySymKey);
            myCipher.update(myBytes);
            myBytes = myCipher.finish();
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
    private void profileStreamKey(final FactoryStreamKeyType pStreamKeySpec) throws OceanusException {
        final GordianFactory myFactory = pStreamKeySpec.getFactory();
        final GordianStreamKeyType myType = pStreamKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianStreamKeyType> myStreamKey = pStreamKeySpec.getKey();
        final int myLen = 128;
        byte[] myBytes = new byte[myLen];
        final GordianStreamCipherSpec mySpec = GordianStreamCipherSpec.stream(myType);
        final GordianCipher<GordianStreamKeyType> myCipher = myCipherFactory.createStreamKeyCipher(mySpec);
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            myCipher.initCipher(myStreamKey);
            myCipher.update(myBytes);
            myBytes = myCipher.finish();
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println(myType.toString() + ":" + myElapsed);
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
        final GordianKeySetFactory myKeySets = pSpec.getFactory().getKeySetFactory();
        final GordianKnuthObfuscater myKnuth = myKeySets.getObfuscater();

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
     * @throws OceanusException on error
     */
    private void checkDigestAlgId(final FactoryDigestSpec pSpec) throws OceanusException {
        /* Access the factory */
        final GordianCoreDigestFactory myFactory = (GordianCoreDigestFactory) pSpec.getFactory().getDigestFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianDigestSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check cipherAlgId.
     * @param pSpec the Spec to check
     * @throws OceanusException on error
     */
    private void checkSymCipherAlgId(final FactorySymCipherSpec pSpec) throws OceanusException {
        /* Access the factory */
        final GordianCoreCipherFactory myFactory = (GordianCoreCipherFactory) pSpec.getFactory().getCipherFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianCipherSpec<?> mySpec = myFactory.getSymSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check streamCipherAlgId.
     * @param pSpec the Spec to check
     * @throws OceanusException on error
     */
    private void checkStreamCipherAlgId(final FactoryStreamCipherSpec pSpec) throws OceanusException {
        /* Access the factory */
        final GordianCoreCipherFactory myFactory = (GordianCoreCipherFactory) pSpec.getFactory().getCipherFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianCipherSpec<?> mySpec = myFactory.getStreamSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Check macAlgId.
     * @param pSpec the Spec to check
     * @throws OceanusException on error
     */
    private void checkMacAlgId(final FactoryMacSpec pSpec) throws OceanusException {
        /* Access the factory */
        final GordianCoreMacFactory myFactory = (GordianCoreMacFactory) pSpec.getFactory().getMacFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianMacSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Obtain the testData.
     * @return the testData
     */
    private byte[] getTestData() {
        if (theTestData == null) {
            theTestData = TethysDataConverter.stringToByteArray("TestDataStringThatIsNotRidiculouslyShort");
        }
        return theTestData;
    }

    /**
     * Obtain the aadData.
     * @return the aadData
     */
    private byte[] getAADData() {
        if (theAADData == null) {
            theAADData = TethysDataConverter.stringToByteArray("SomeAADBytes");
        }
        return theAADData;
    }
}
