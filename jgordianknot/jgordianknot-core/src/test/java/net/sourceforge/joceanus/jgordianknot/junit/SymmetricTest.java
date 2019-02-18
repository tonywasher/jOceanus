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
package net.sourceforge.joceanus.jgordianknot.junit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite - Test Symmetric/Stream and Digest/MAC Algorithms.
 */
public class SymmetricTest {
    /**
     * The factories.
     */
    private static GordianFactory BCFULLFACTORY;
    private static GordianFactory JCAFULLFACTORY;
    private static GordianFactory BCCUTFACTORY;
    private static GordianFactory JCACUTFACTORY;

    @BeforeAll
    static void createSecurityFactories() throws OceanusException {
        BCFULLFACTORY = GordianGenerator.createFactory(new GordianParameters(false, GordianFactoryType.BC));
        BCCUTFACTORY = GordianGenerator.createFactory(new GordianParameters(true, GordianFactoryType.BC));
        JCAFULLFACTORY = GordianGenerator.createFactory(new GordianParameters(false, GordianFactoryType.JCA));
        JCACUTFACTORY = GordianGenerator.createFactory(new GordianParameters(true, GordianFactoryType.JCA));
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
     * Factory Spec interface.
     * @param <T> the object type
     */
    interface FactorySpec<T> {
        /**
         * Obtain the factory.
         * @return the factory.
         */
        GordianFactory getFactory();

        /**
         * Obtain the spec.
         * @return the spec.
         */
        T getSpec();
    }

    /**
     * Factory and Digest definition.
     */
    static class FactoryDigestSpec
            implements FactorySpec<GordianDigestSpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The digestSpec.
         */
        private final GordianDigestSpec theDigestSpec;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pDigestSpec the digestSpec
         */
        FactoryDigestSpec(final GordianFactory pFactory,
                          final GordianDigestSpec pDigestSpec) {
            theFactory = pFactory;
            theDigestSpec = pDigestSpec;
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianDigestSpec getSpec() {
            return theDigestSpec;
        }

        @Override
        public String toString() {
            return theFactory.getFactoryType() + ":" + theDigestSpec;
        }
    }

    /**
     * Factory and Mac definition.
     */
    static class FactoryMacSpec
            implements FactorySpec<GordianMacSpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The macSpec.
         */
        private final GordianMacSpec theMacSpec;

        /**
         * The key.
         */
        private GordianKey<GordianMacSpec> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pMacSpec the macSpec
         */
        FactoryMacSpec(final GordianFactory pFactory,
                       final GordianMacSpec pMacSpec) {
            theFactory = pFactory;
            theMacSpec = pMacSpec;
        }

        /**
         * Obtain (or create) the key for the FactoryMacSpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianMacSpec> getKey() throws OceanusException {
            /* Return key if it exists */
            if (theKey != null) {
                return theKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKey != null) {
                    return theKey;
                }

                /* Generate the key */
                GordianMacFactory myFactory = theFactory.getMacFactory();
                GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(theMacSpec);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianMacSpec getSpec() {
            return theMacSpec;
        }

        @Override
        public String toString() {
            return theFactory.getFactoryType() + ":" + theMacSpec;
        }
    }

    /**
     * Factory and symKey definition.
     */
    static class FactorySymKeySpec
            implements FactorySpec<GordianSymKeySpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The symKeySpec.
         */
        private final GordianSymKeySpec theSymKeySpec;

        /**
         * The key.
         */
        private GordianKey<GordianSymKeySpec> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSymKeySpec the symKeySpec
         */
        FactorySymKeySpec(final GordianFactory pFactory,
                          final GordianSymKeySpec pSymKeySpec) {
            theFactory = pFactory;
            theSymKeySpec = pSymKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getKey() throws OceanusException {
            /* Return key if it exists */
            if (theKey != null) {
                return theKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKey != null) {
                    return theKey;
                }

                /* Generate the key */
                GordianCipherFactory myFactory = theFactory.getCipherFactory();
                GordianKeyGenerator<GordianSymKeySpec> myGenerator = myFactory.getKeyGenerator(theSymKeySpec);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianSymKeySpec getSpec() {
            return theSymKeySpec;
        }

        @Override
        public String toString() {
            return theFactory.getFactoryType() + ":" + theSymKeySpec;
        }
    }

    /**
     * Factory and symCipher definition.
     */
    static class FactorySymCipherSpec
            implements FactorySpec<GordianSymCipherSpec> {
        /**
         * The factory.
         */
        private final FactorySymKeySpec theOwner;

        /**
         * The symKeySpec.
         */
        private final GordianSymCipherSpec theCipherSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pCipherSpec the symCipherSpec
         */
        FactorySymCipherSpec(final FactorySymKeySpec pOwner,
                             final GordianSymCipherSpec pCipherSpec) {
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getKey() throws OceanusException {
            return theOwner.getKey();
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianSymCipherSpec getSpec() {
            return theCipherSpec;
        }

        @Override
        public String toString() {
            return getFactory().getFactoryType() + ":" + theCipherSpec;
        }
    }

    /**
     * Factory and streamKey definition.
     */
    static class FactoryStreamKeyType
            implements FactorySpec<GordianStreamKeyType> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The streamKeyType.
         */
        private final GordianStreamKeyType theKeyType;

        /**
         * The key.
         */
        private GordianKey<GordianStreamKeyType> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeyType the keyType
         */
        FactoryStreamKeyType(final GordianFactory pFactory,
                             final GordianStreamKeyType pKeyType) {
            theFactory = pFactory;
            theKeyType = pKeyType;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianStreamKeyType> getKey() throws OceanusException {
            /* Return key if it exists */
            if (theKey != null) {
                return theKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKey != null) {
                    return theKey;
                }

                /* Generate the key */
                GordianCipherFactory myFactory = theFactory.getCipherFactory();
                GordianKeyGenerator<GordianStreamKeyType> myGenerator = myFactory.getKeyGenerator(theKeyType);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianStreamKeyType getSpec() {
            return theKeyType;
        }

        @Override
        public String toString() {
            return theFactory.getFactoryType() + ":" + theKeyType;
        }
    }

    /**
     * Factory and streamCipher definition.
     */
    static class FactoryStreamCipherSpec
            implements FactorySpec<GordianStreamCipherSpec> {
        /**
         * The owner.
         */
        private final FactoryStreamKeyType theOwner;

        /**
         * The streamKeyType.
         */
        private final GordianStreamCipherSpec theCipherSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pCipherSpec the cipherSpec
         */
        FactoryStreamCipherSpec(final FactoryStreamKeyType pOwner,
                                final GordianStreamCipherSpec pCipherSpec) {
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianStreamCipherSpec getSpec() {
            return theCipherSpec;
        }

        @Override
        public String toString() {
            return getFactory().getFactoryType() + ":" + theCipherSpec;
        }
    }

    /**
     * Obtain the list of digests to test.
     * @param pFactory the factory
     * @return the list
     */
    static List<FactoryDigestSpec> digestProvider(final GordianFactory pFactory) {
        /* Loop through the possible digestSpecs */
        final List<FactoryDigestSpec> myResult = new ArrayList<>();
        final GordianDigestFactory myDigestFactory = pFactory.getDigestFactory();
        for (GordianDigestSpec mySpec : myDigestFactory.listAllSupportedSpecs()) {
            /* Add the digestSpec */
            myResult.add(new FactoryDigestSpec(pFactory, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of macs to test.
     * @param pFactory the factory
     * @return the list
     */
    static List<FactoryMacSpec> macProvider(final GordianFactory pFactory) {
        /* Loop through the possible macSpecs */
        final List<FactoryMacSpec> myResult = new ArrayList<>();
        final GordianMacFactory myMacFactory = pFactory.getMacFactory();
        for (GordianMacSpec mySpec : myMacFactory.listAllSupportedSpecs()) {
            /* Add the macSpec */
            myResult.add(new FactoryMacSpec(pFactory, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symKeySpecs to test.
     * @param pFactory the factory
     * @return the list
     */
    static List<FactorySymKeySpec> symKeyProvider(final GordianFactory pFactory) {
        /* Loop through the possible keySpecs */
        final List<FactorySymKeySpec> myResult = new ArrayList<>();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        for (GordianSymKeySpec mySpec : myCipherFactory.listAllSupportedSymKeySpecs()) {
            /* Add the symKeySpec */
            myResult.add(new FactorySymKeySpec(pFactory, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of streamKeySpecs to test.
     * @param pFactory the factory
     * @return the list
     */
    static List<FactoryStreamKeyType> streamKeyProvider(final GordianFactory pFactory) {
        /* Loop through the possible keySpecs */
        final List<FactoryStreamKeyType> myResult = new ArrayList<>();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        for (GordianStreamKeyType myType : myCipherFactory.listAllSupportedStreamKeyTypes()) {
            /* Add the streamKeySpec */
            myResult.add(new FactoryStreamKeyType(pFactory, myType));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Create the symmetric test suite.
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> symmetricTests() {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Create tests */
        final Stream<DynamicNode> myBCCut = symmetricTests(BCCUTFACTORY);
        myStream = Stream.concat(myStream, myBCCut);
        final Stream<DynamicNode> myBCFull = symmetricTests(BCFULLFACTORY);
        myStream = Stream.concat(myStream, myBCFull);
        final Stream<DynamicNode> myJCACut = symmetricTests(JCACUTFACTORY);
        myStream = Stream.concat(myStream, myJCACut);
        final Stream<DynamicNode> myJCAFull = symmetricTests(JCAFULLFACTORY);
        return Stream.concat(myStream, myJCAFull);
    }

    /**
     * Create the asymmetric test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    Stream<DynamicNode> symmetricTests(final GordianFactory pFactory) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Add digest Tests */
        List<FactoryDigestSpec> myDigests = digestProvider(pFactory);
        if (!myDigests.isEmpty()) {
            Stream<DynamicNode> myTests = myDigests.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testDigest(x)));
            myTests = Stream.of(DynamicContainer.dynamicContainer("Digests", myTests));
            myStream = Stream.concat(myStream, myTests);
        }

        /* Add mac Tests */
        List<FactoryMacSpec> myMacs = macProvider(pFactory);
        if (!myMacs.isEmpty()) {
            Stream<DynamicNode> myTests = myMacs.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testMac(x)));
            myTests = Stream.of(DynamicContainer.dynamicContainer("Macs", myTests));
            myStream = Stream.concat(myStream, myTests);
        }

        /* Add symKey Tests */
        List<FactorySymKeySpec> mySymKeys = symKeyProvider(pFactory);
        if (!mySymKeys.isEmpty()) {
            Stream<DynamicNode> myTests = mySymKeys.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testSymKey(x)));
            myTests = Stream.of(DynamicContainer.dynamicContainer("symKeys", myTests));
            myStream = Stream.concat(myStream, myTests);
        }

        /* Add streamKey Tests */
        List<FactoryStreamKeyType> myStreamKeys = streamKeyProvider(pFactory);
        if (!myStreamKeys.isEmpty()) {
            Stream<DynamicNode> myTests = myStreamKeys.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> testStreamKey(x)));
            myTests = Stream.of(DynamicContainer.dynamicContainer("streamKeys", myTests));
            myStream = Stream.concat(myStream, myTests);
        }

        /* Return the stream */
        myStream = Stream.of(DynamicContainer.dynamicContainer(pFactory.getFactoryType().toString(), myStream));
        return myStream;
    }

    /**
     * Perform digest tests.
     * @param pDigestSpec the digestSpec to test
     * @throws OceanusException on error
     */
    void testDigest(final FactoryDigestSpec pDigestSpec) throws OceanusException {
        profileDigest(pDigestSpec);
        checkExternalId(pDigestSpec);
    }

    /**
     * Perform mac tests.
     * @param pMacSpec the macSpec to test
     * @throws OceanusException on error
     */
    void testMac(final FactoryMacSpec pMacSpec) throws OceanusException {
        profileMac(pMacSpec);
        checkExternalId(pMacSpec);
    }

    /**
     * Perform symKey tests.
     * @param pSymKeySpec the symKeySpec to test
     * @throws OceanusException on error
     */
    void testSymKey(final FactorySymKeySpec pSymKeySpec) throws OceanusException {
        profileSymKey(pSymKeySpec);
        checkCipherModes(pSymKeySpec);
        checkWrapCipher(pSymKeySpec);
        checkExternalId(pSymKeySpec);
    }

    /**
     * Perform streamKey tests.ds
     * @param pStreamKey the streamKey to test
     * @throws OceanusException on error
     */
    void testStreamKey(final FactoryStreamKeyType pStreamKey) throws OceanusException {
        profileStreamKey(pStreamKey);
        checkCipher(pStreamKey);
        checkExternalId(pStreamKey);
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

        /* Loop 100 times */
        final byte[] myBytes = "DigestInput".getBytes();
        final long myStart = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            myDigest.update(myBytes);
            myDigest.finish();
        }

        /* Calculate elapsed time */
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100000;
        System.out.println(pDigestSpec.toString() + ":" + myElapsed);
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
        final GordianMac myMac1 = myMacFactory.createMac(pMacSpec.theMacSpec);
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();

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
        for (int i = 0; i < 500; i++) {
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
        myElapsed /= 100000;
        System.out.println(pMacSpec.toString() + ":" + myElapsed);
        Assertions.assertFalse(isInconsistent, pMacSpec.toString() + " inconsistent");
    }

    /**
     * Check cipher modes.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkCipherModes(final FactorySymKeySpec pKeySpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianSymKeySpec mySpec = pKeySpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        for (GordianSymCipherSpec myCipherSpec : myCipherFactory.listAllSupportedSymCipherSpecs(mySpec, false)) {
            checkSymCipher(new FactorySymCipherSpec(pKeySpec, myCipherSpec));
        }
        for (GordianSymCipherSpec myCipherSpec : myCipherFactory.listAllSupportedSymCipherSpecs(mySpec, true)) {
            checkAADCipher(new FactorySymCipherSpec(pKeySpec, myCipherSpec));
        }
    }

    /**
     * Check cipher mode/padding.
     * @param pCipherSpec the cipherSpec
     * @throws OceanusException on error
     */
    private void checkSymCipher(final FactorySymCipherSpec pCipherSpec) throws OceanusException {
        /* Access details */
        final GordianFactory myFactory = pCipherSpec.getFactory();
        final GordianSymCipherSpec mySpec = pCipherSpec.getSpec();
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final GordianKey<GordianSymKeySpec> myKey = pCipherSpec.getKey();

        /* Access Data */
        final byte[] myTestData = getTestData();

        /* Create the Spec */
        final GordianCipher<GordianSymKeySpec> myCipher = myCipherFactory.createSymKeyCipher(mySpec);
        final boolean hasIV = mySpec.getCipherMode().needsIV();
        myCipher.initCipher(myKey);
        final byte[] myIV = hasIV ? myCipher.getInitVector() : null;
        final byte[] myEncrypted = myCipher.finish(myTestData);
        myCipher.initCipher(myKey, myIV, false);
        final byte[] myResult = myCipher.finish(myEncrypted);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
        checkExternalId(pCipherSpec);
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
        checkExternalId(new FactoryStreamCipherSpec(pStreamKeySpec, mySpec));
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

        /* Create the Cipher */
        final GordianWrapper myWrapper = myCipherFactory.createKeyWrapper(mySpec);
        final byte[] myWrapped = myWrapper.secureBytes(mySymKey, myTestData);
        final byte[] myResult = myWrapper.deriveBytes(mySymKey, myWrapped);
        Assertions.assertArrayEquals(myTestData, myResult, "Failed to encrypt/decrypt");
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
        final long myStart = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            myCipher.initCipher(mySymKey);
            myCipher.update(myBytes);
            myBytes = myCipher.finish();
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100;
        System.out.println(mySpec.toString() + ":" + myElapsed);
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
        for (int i = 0; i < 100; i++) {
            myCipher.initCipher(myStreamKey);
            myCipher.update(myBytes);
            myBytes = myCipher.finish();
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 100;
        System.out.println(myType.toString() + ":" + myElapsed);
    }

    /**
     * Check externalId.
     * @param pSpec the Spec to check
     * @throws OceanusException on error
     */
    private void checkExternalId(final FactorySpec<?> pSpec) throws OceanusException {
        /* Access the factories */
        final GordianKeySetFactory myKeySets = pSpec.getFactory().getKeySetFactory();
        final GordianKnuthObfuscater myKnuth = myKeySets.getObfuscater();
        final Class<?> myClazz = pSpec.getSpec().getClass();

        /* Check standard obfuscation */
        int myId = myKnuth.deriveExternalIdFromType(pSpec.getSpec());
        Object myResult = myKnuth.deriveTypeFromExternalId(myId, myClazz);
        Assertions.assertEquals(pSpec, myResult,
                "Standard obfuscation for " + myClazz.getSimpleName() + ":" + pSpec);

        final int myOffset = 205;
        myId = myKnuth.deriveExternalIdFromType(pSpec, myOffset);
        myResult = myKnuth.deriveTypeFromExternalId(myId, myOffset, myClazz);
        Assertions.assertEquals(pSpec, myResult,
                "Offset obfuscation for " + myClazz.getSimpleName() + ":" + pSpec);
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
