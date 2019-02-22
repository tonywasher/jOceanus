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

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite - Test KeySet functionality.
 */
public class KeySetTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

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
                            ? 1000
                            : 5;
        }
    }

    /**
     * The factories.
     */
    private static GordianFactory BCFULLFACTORY;
    private static GordianFactory JCAFULLFACTORY;
    private static GordianFactory BCCUTFACTORY;
    private static GordianFactory JCACUTFACTORY;

    /**
     * Initialise Factories.
     */
    @BeforeAll
    public static void createSecurityFactories() throws OceanusException {
        BCFULLFACTORY = GordianGenerator.createFactory(new GordianParameters(false, GordianFactoryType.BC));
        BCCUTFACTORY = GordianGenerator.createFactory(new GordianParameters(true, GordianFactoryType.BC));
        JCAFULLFACTORY = GordianGenerator.createFactory(new GordianParameters(false, GordianFactoryType.JCA));
        JCACUTFACTORY = GordianGenerator.createFactory(new GordianParameters(true, GordianFactoryType.JCA));
    }

    /**
     * Factory and KeySet definition.
     */
    static class FactoryKeySet {
        /**
         * The Factory.
         */
        private final GordianFactory theFactory;

        /**
         * The keyHash.
         */
        private final GordianKeySetHash theKeySetHash;

        /**
         * The symKey.
         */
        private final GordianKey<GordianSymKeySpec> theSymKey;

        /**
         * The streamKey.
         */
        private final GordianKey<GordianStreamKeyType> theStreamKey;

        /**
         * The macKey.
         */
        private final GordianKey<GordianMacSpec> theMacKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @throws OceanusException on error
         */
        FactoryKeySet(final GordianFactory pFactory) throws OceanusException {
            /* Store parameters */
            theFactory = pFactory;

            /* Generate the hash */
            final GordianKeySetFactory myKeySets = pFactory.getKeySetFactory();
            theKeySetHash = myKeySets.generateKeySetHash(DEF_PASSWORD.clone());

            /* Initialise data */
            final GordianRandomFactory myRandoms = theFactory.getRandomFactory();
            theSymKey = myRandoms.generateRandomSymKey();
            theStreamKey = myRandoms.generateRandomStreamKey(false);
            GordianMac myMac = myRandoms.generateRandomMac(false);
            theMacKey = myMac.getKey();
        }

        /**
         * Obtain the factory.
         * @return the factory
         */
        GordianFactory getFactory() {
            return theFactory;
        }

        /**
         * Obtain the keySetHash.
         * @return the keySetHash
         */
        GordianKeySetHash getKeySetHash() {
            return theKeySetHash;
        }

        /**
         * Obtain the keySet.
         * @return the keySet
         */
        GordianKeySet getKeySet() {
            return theKeySetHash.getKeySet();
        }

        /**
         * Obtain the symKey.
         * @return the symKey
         */
        GordianKey<GordianSymKeySpec> getSymKey() {
            return theSymKey;
        }

        /**
         * Obtain the streamKey.
         * @return the streamKey
         */
        GordianKey<GordianStreamKeyType> getStreamKey() {
            return theStreamKey;
        }

        /**
         * Obtain the macKey.
         * @return the macKey
         */
        GordianKey<GordianMacSpec> getMacKey() {
            return theMacKey;
        }

        @Override
        public String toString() {
            return theFactory.getFactoryType().toString()
                    + (theFactory.isRestricted() ? "-Restricted" : "-Full");
        }
    }

    /**
     * Create the keySet test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> keySetTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = keySetTests(BCCUTFACTORY);
        myStream = Stream.concat(myStream, keySetTests(BCFULLFACTORY));
        myStream = Stream.concat(myStream, keySetTests(JCACUTFACTORY));
        return Stream.concat(myStream, keySetTests(JCAFULLFACTORY));
    }

    /**
     * Create the keySet test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> keySetTests(final GordianFactory pFactory) throws OceanusException {
        /* Return the stream */
        final FactoryKeySet myKeySet = new FactoryKeySet(pFactory);
        return Stream.of(DynamicContainer.dynamicContainer(pFactory.toString(), Stream.of(
                DynamicTest.dynamicTest("keySet", () -> checkKeySetHash(myKeySet)),
                DynamicTest.dynamicTest("encrypt", () -> checkEncrypt(myKeySet)),
                DynamicTest.dynamicTest("wrap", () -> checkWrap(myKeySet)),
                DynamicTest.dynamicTest("profile", () -> profileEncrypt(myKeySet))
        )));
    }

    /**
     * Check keySetHash.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkKeySetHash(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keySet factory */
        GordianKeySetFactory myKeySets = pKeySet.getFactory().getKeySetFactory();
        final GordianKeySetHash myHash = myKeySets.deriveKeySetHash(pKeySet.getKeySetHash().getHash(), DEF_PASSWORD.clone());
        final GordianKeySet myKeySet = myHash.getKeySet();

        /* Check the keySets are the same */
        Assertions.assertEquals(myKeySet, pKeySet.getKeySet(), "Failed to derive keySet");
    }

    /**
     * Check encrypt.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkEncrypt(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keys */
        final GordianKeySet myKeySet = pKeySet.getKeySet();

        /* Encrypt short block */
        final String myTest1 = "TestString";
        byte[] myBytes = TethysDataConverter.stringToByteArray(myTest1);
        byte[] myEncrypt = myKeySet.encryptBytes(myBytes);
        byte[] myResult = myKeySet.decryptBytes(myEncrypt);
        String myAnswer = TethysDataConverter.byteArrayToString(myResult);
        Assertions.assertEquals(myTest1, myAnswer, "Failed to decrypt test1 string");

        /* Encrypt full block */
        final String myTest2 = "TestString123456";
        myBytes = TethysDataConverter.stringToByteArray(myTest2);
        myEncrypt = myKeySet.encryptBytes(myBytes);
        myResult = myKeySet.decryptBytes(myEncrypt);
        myAnswer = TethysDataConverter.byteArrayToString(myResult);
        Assertions.assertEquals(myTest2, myAnswer, "Failed to decrypt test2 string");

        /* Encrypt some multi-block */
        final String myTest3 = "TestString1234567";
        myBytes = TethysDataConverter.stringToByteArray(myTest3);
        myEncrypt = myKeySet.encryptBytes(myBytes);
        myResult = myKeySet.decryptBytes(myEncrypt);
        myAnswer = TethysDataConverter.byteArrayToString(myResult);
        Assertions.assertEquals(myTest3, myAnswer, "Failed to decrypt test3 string");
    }

    /**
     * Check wrapping.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkWrap(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keys */
        final GordianKeySet myKeySet = pKeySet.getKeySet();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySet.getSymKey();
        final GordianKey<GordianStreamKeyType> myStreamKey = pKeySet.getStreamKey();
        final GordianKey<GordianMacSpec> myMacKey = pKeySet.getMacKey();

        /* Check wrap of symKey */
        final byte[] mySymSafe = myKeySet.secureKey(mySymKey);
        final GordianKey<GordianSymKeySpec> mySymResult = myKeySet.deriveKey(mySymSafe, mySymKey.getKeyType());
        Assertions.assertEquals(mySymKey, mySymResult, "Failed to wrap/unwrap symKey");

        /* Check wrap of streamKey */
        final byte[] myStreamSafe = myKeySet.secureKey(myStreamKey);
        final GordianKey<GordianStreamKeyType> myStreamResult = myKeySet.deriveKey(myStreamSafe, myStreamKey.getKeyType());
        Assertions.assertEquals(myStreamKey, myStreamResult, "Failed to wrap/unwrap streamKey");

        /* Check wrap of macKey */
        final byte[] myMacSafe = myKeySet.secureKey(myMacKey);
        final GordianKey<GordianMacSpec> myMacResult = myKeySet.deriveKey(myMacSafe, myMacKey.getKeyType());
        Assertions.assertEquals(myMacKey, myMacResult, "Failed to wrap/unwrap macKey");
    }

    /**
     * Profile encrypt.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void profileEncrypt(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keys */
        final GordianKeySet myKeySet = pKeySet.getKeySet();

        /* Creat the test data */
        final byte[] myData = new byte[1000];

        /* Loop through encrypt/decrypt */
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            final byte[] myEncrypt = myKeySet.encryptBytes(myData);
            final byte[] myResult = myKeySet.decryptBytes(myEncrypt);
            Assertions.assertArrayEquals(myData, myResult, "Failed to decrypt test1 string");
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= SymmetricTest.MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println("Elapsed: " + myElapsed);
        }
    }
}
