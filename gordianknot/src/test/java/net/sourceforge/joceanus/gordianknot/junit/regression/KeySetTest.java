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

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianLockFactory;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetAADCipher;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetCipher;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianKeySetLock;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.gordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.oceanus.format.OceanusDataConverter;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.SecureRandom;
import java.util.stream.Stream;

/**
 * Security Test suite - Test KeySet functionality.
 */
class KeySetTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * TestString1.
     */
    private static final String TEST_STRING1 = "TestString";

    /**
     * TestString2.
     */
    private static final String TEST_STRING2 = "TestString123456";

    /**
     * TestString3.
     */
    private static final String TEST_STRING3 = "TestString1234567";

    /**
     * AAD.
     */
    private static final String TEST_AAD = "SomeAAD";

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
     * Factory definition.
     */
    static class FactoryBase {
        /**
         * The Factory.
         */
        private final GordianFactory theFactory;

        /**
         * Constructor.
         * @param pType the factoryType
         * @throws OceanusException on error
         */
        FactoryBase(final GordianFactoryType pType) throws OceanusException {
            /* Create the factory */
            theFactory = GordianGenerator.createRandomFactory(pType);
        }

        /**
         * Obtain the factory.
         * @return the factory
         */
        GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public String toString() {
            return theFactory.getFactoryType().toString();
        }
    }

    /**
     * Factory and KeySet definition.
     */
    static class FactoryKeySet {
        /**
         * The Factory.
         */
        private final FactoryBase theFactory;

        /**
         * Maximum cipher steps?
         */
        private final boolean maxSteps;

        /**
         * the KeySetSpec.
         */
        private final GordianPasswordLockSpec theSpec;

        /**
         * The keyHash.
         */
        private final GordianKeySetLock theKeySetLock;

        /**
         * The keySet Cipher.
         */
        private final GordianKeySetCipher theKeySetCipher;

        /**
         * The keySet AAD Cipher.
         */
        private final GordianKeySetAADCipher theKeySetAADCipher;

        /**
         * The symKey.
         */
        private final GordianKey<GordianSymKeySpec> theSymKey;

        /**
         * The streamKey.
         */
        private final GordianKey<GordianStreamKeySpec> theStreamKey;

        /**
         * The macKey.
         */
        private final GordianKey<GordianMacSpec> theMacKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeyLen the factory keyLength
         * @param pMaxSteps true/false use Max Cipher Steps (or else Min)
         * @throws OceanusException on error
         */
        FactoryKeySet(final FactoryBase pFactory,
                      final GordianLength pKeyLen,
                      final boolean pMaxSteps) throws OceanusException {
            /* Store parameters */
            theFactory = pFactory;
            maxSteps = pMaxSteps;

            /* Create the keySetHashSpec */
            final int myMaxSteps = pMaxSteps ? GordianKeySetSpec.MAXIMUM_CIPHER_STEPS
                                             : GordianKeySetSpec.MINIMUM_CIPHER_STEPS;
            theSpec = new GordianPasswordLockSpec(new GordianKeySetSpec(pKeyLen, myMaxSteps));

            /* Generate the hash */
            final GordianFactory myFactory = theFactory.getFactory();
            final GordianLockFactory myKeySets = myFactory.getLockFactory();
            theKeySetLock = myKeySets.newKeySetLock(theSpec, DEF_PASSWORD.clone());

            /* Create the ciphers */
            theKeySetCipher = theKeySetLock.getKeySet().createCipher();
            theKeySetAADCipher = theKeySetLock.getKeySet().createAADCipher();

            /* Initialise data */
            final GordianRandomFactory myRandoms = myFactory.getRandomFactory();
            theSymKey = myRandoms.generateRandomSymKey(pKeyLen);
            theStreamKey = myRandoms.generateRandomStreamKey(pKeyLen, false);
            GordianMac myMac = myRandoms.generateRandomMac(pKeyLen, false);
            theMacKey = myMac.getKey();
        }

        /**
         * Obtain the factory.
         * @return the factory
         */
        GordianFactory getFactory() {
            return theFactory.getFactory();
        }

        /**
         * Obtain the passwordLockSpec.
         * @return the passwordLockSpec
         */
        GordianPasswordLockSpec getPasswordLockSpec() {
            return theSpec;
        }

        /**
         * Obtain the keySetLock.
         * @return the keySetLock
         */
        GordianKeySetLock getKeySetLock() {
            return theKeySetLock;
        }

        /**
         * Obtain the keySetCipher.
         * @return the keySetCipher
         */
        GordianKeySetCipher getKeySetCipher() {
            return theKeySetCipher;
        }

        /**
         * Obtain the keySetAADCipher.
         * @return the keySetAADCipher
         */
        GordianKeySetAADCipher getKeySetAADCipher() {
            return theKeySetAADCipher;
        }

        /**
         * Obtain the keySet.
         * @return the keySet
         */
        GordianKeySet getKeySet() {
            return theKeySetLock.getKeySet();
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
        GordianKey<GordianStreamKeySpec> getStreamKey() {
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
            return theSpec.getKeySetSpec().getKeyLength()
                    + (maxSteps ? "-Max" : "-Min");
        }
    }

    /**
     * Create the keySet test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> keySetTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = keySetTests(GordianFactoryType.BC);
        return Stream.concat(myStream, keySetTests(GordianFactoryType.JCA));
    }

    /**
     * Create the keySet test suite for a factoryType.
     * @param pFactoryType the factoryType
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> keySetTests(final GordianFactoryType pFactoryType) throws OceanusException {
        /* Create the factory */
        final FactoryBase myFactory = new FactoryBase(pFactoryType);

        Stream<DynamicNode> myStream = keySetTests(myFactory, GordianLength.LEN_256, false);
        myStream = Stream.concat(myStream, keySetTests(myFactory, GordianLength.LEN_256, true));
        myStream = Stream.concat(myStream, keySetTests(myFactory, GordianLength.LEN_192, false));
        myStream = Stream.concat(myStream, keySetTests(myFactory, GordianLength.LEN_192, true));
        myStream = Stream.concat(myStream, keySetTests(myFactory, GordianLength.LEN_128, false));
        myStream = Stream.concat(myStream, keySetTests(myFactory, GordianLength.LEN_128, true));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("random", () -> testRandomFactory())));

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer(pFactoryType.toString(), myStream));
    }

    /**
     * Create the keySet test suite for a factory/SpecSet.
     * @param pFactory the factory
     * @param pKeyLen the keyLength
     * @param pMaxSteps true/false use Max Cipher Steps (or else Min)
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> keySetTests(final FactoryBase pFactory,
                                            final GordianLength pKeyLen,
                                            final boolean pMaxSteps) throws OceanusException {
        /* Create the factory */
        final FactoryKeySet myKeySet = new FactoryKeySet(pFactory, pKeyLen, pMaxSteps);

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer(myKeySet.toString(), Stream.of(
                DynamicTest.dynamicTest("encrypt", () -> checkEncrypt(myKeySet)),
                DynamicTest.dynamicTest("encryptAAD", () -> checkEncryptAAD(myKeySet)),
                DynamicTest.dynamicTest("wrap", () -> checkWrap(myKeySet)),
                DynamicTest.dynamicTest("factory", () -> checkFactory(myKeySet)),
                DynamicTest.dynamicTest("profile", () -> profileEncrypt(myKeySet))
        )));
    }

    /**
     * Check encrypt.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkEncrypt(final FactoryKeySet pKeySet) throws OceanusException {
        /* Check short string */
        checkEncrypt(pKeySet, TEST_STRING1);

        /* Check full block */
        checkEncrypt(pKeySet, TEST_STRING2);

        /* Check multi-block */
        checkEncrypt(pKeySet, TEST_STRING3);
    }

    /**
     * Check encryptAAD.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkEncryptAAD(final FactoryKeySet pKeySet) throws OceanusException {
        /* Check short string */
        checkEncryptAAD(pKeySet, TEST_STRING1);

        /* Check full block */
        checkEncryptAAD(pKeySet, TEST_STRING2);

        /* Check multi-block */
        checkEncryptAAD(pKeySet, TEST_STRING3);
    }

    /**
     * Check encrypt.
     * @param pKeySet the keySet
     * @param pTest the test string
     * @throws OceanusException on error
     */
    private void checkEncrypt(final FactoryKeySet pKeySet,
                              final String pTest) throws OceanusException {
        /* Encrypt oneOff string */
        byte[] myEncrypt = encryptOneOff(pKeySet, pTest);
        String myAnswer = decryptOneOff(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed oneOff-oneOff decrypt " + pTest);
        myAnswer = decryptOneOffCipher(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed oneOff-cipher decrypt "+ pTest);
        myAnswer = decryptCacheCipher(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed oneOff-cache decrypt "+ pTest);

        /* Encrypt cipher string */
        myEncrypt = encryptOneOffCipher(pKeySet, pTest);
        myAnswer = decryptOneOff(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed cipher-oneOff decrypt " + pTest);
        myAnswer = decryptOneOffCipher(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed cipher-cipher decrypt "+ pTest);
        myAnswer = decryptCacheCipher(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed cipher-cache decrypt "+ pTest);

        /* Encrypt cache string */
        myEncrypt = encryptCacheCipher(pKeySet, pTest);
        myAnswer = decryptOneOff(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed cache-oneOff decrypt " + pTest);
        myAnswer = decryptOneOffCipher(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed cache-cipher decrypt "+ pTest);
        myAnswer = decryptCacheCipher(pKeySet, myEncrypt);
        Assertions.assertEquals(pTest, myAnswer, "Failed cache-cache decrypt "+ pTest);
    }

    /**
     * Check encrypt.
     * @param pKeySet the keySet
     * @param pTest the test string
     * @throws OceanusException on error
     */
    private void checkEncryptAAD(final FactoryKeySet pKeySet,
                                 final String pTest) throws OceanusException {
        /* Encrypt cipher string */
        byte[] myEncrypt = encryptOneOffAADCipher(pKeySet, pTest, null);
        String myAnswer = decryptOneOffAADCipher(pKeySet, myEncrypt, null);
        Assertions.assertEquals(pTest, myAnswer, "Failed nullAAD cipher-cipher decrypt "+ pTest);
        myAnswer = decryptCacheAADCipher(pKeySet, myEncrypt, null);
        Assertions.assertEquals(pTest, myAnswer, "Failed nullAAD cipher-cache decrypt "+ pTest);

        /* Encrypt cache string */
        myEncrypt = encryptCacheAADCipher(pKeySet, pTest, null);
        myAnswer = decryptOneOffAADCipher(pKeySet, myEncrypt, null);
        Assertions.assertEquals(pTest, myAnswer, "Failed nullAAD cache-cipher decrypt "+ pTest);
        myAnswer = decryptCacheAADCipher(pKeySet, myEncrypt, null);
        Assertions.assertEquals(pTest, myAnswer, "Failed nullAAD cache-cache decrypt "+ pTest);

        /* Encrypt cipher string */
        myEncrypt = encryptOneOffAADCipher(pKeySet, pTest, TEST_AAD);
        myAnswer = decryptOneOffAADCipher(pKeySet, myEncrypt, TEST_AAD);
        Assertions.assertEquals(pTest, myAnswer, "Failed AAD cipher-cipher decrypt "+ pTest);
        myAnswer = decryptCacheAADCipher(pKeySet, myEncrypt, TEST_AAD);
        Assertions.assertEquals(pTest, myAnswer, "Failed AAD cipher-cache decrypt "+ pTest);

        /* Encrypt cache string */
        myEncrypt = encryptCacheAADCipher(pKeySet, pTest, TEST_AAD);
        myAnswer = decryptOneOffAADCipher(pKeySet, myEncrypt, TEST_AAD);
        Assertions.assertEquals(pTest, myAnswer, "Failed AAD cache-cipher decrypt "+ pTest);
        myAnswer = decryptCacheAADCipher(pKeySet, myEncrypt, TEST_AAD);
        Assertions.assertEquals(pTest, myAnswer, "Failed AAD cache-cache decrypt "+ pTest);
    }

    /**
     * encrypt data via oneOff call.
     * @param pKeySet the keySet
     * @param pData the data to encrypt
     * @return the encrypted data
     * @throws OceanusException on error
     */
    private byte[] encryptOneOff(final FactoryKeySet pKeySet,
                                 final String pData) throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();

        /* Encrypt string */
        final byte[] myBytes = OceanusDataConverter.stringToByteArray(pData);
        final byte[] myEncrypted = myKeySet.encryptBytes(myBytes);

        /* Check encryption length */
        Assertions.assertEquals(GordianCoreKeySet.getEncryptionLength(myBytes.length),
                myEncrypted.length, "Incorrect encrypted length");

        /* return the result */
        return myEncrypted;
    }

    /**
     * encrypt data via oneOff cipher.
     * @param pKeySet the keySet
     * @param pData the data to encrypt
     * @return the encrypted data
     * @throws OceanusException on error
     */
    private byte[] encryptOneOffCipher(final FactoryKeySet pKeySet,
                                       final String pData) throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();
        final GordianKeySetCipher myCipher = myKeySet.createCipher();

        /* Encrypt string */
        myCipher.initForEncrypt();
        final byte[] myBytes = OceanusDataConverter.stringToByteArray(pData);
        final byte[] myEncrypted = myCipher.finish(myBytes, 0, myBytes.length);

        /* Check encryption length */
        Assertions.assertEquals(GordianCoreKeySet.getEncryptionLength(myBytes.length),
                myEncrypted.length, "Incorrect encrypted length");

        /* return the result */
        return myEncrypted;
    }

    /**
     * encrypt data via cached cipher.
     * @param pKeySet the keySet
     * @param pData the data to encrypt
     * @return the encrypted data
     * @throws OceanusException on error
     */
    private byte[] encryptCacheCipher(final FactoryKeySet pKeySet,
                                      final String pData) throws OceanusException {
        /* Access the keySet */
        final GordianKeySetCipher myCipher = pKeySet.getKeySetCipher();

        /* Encrypt string */
        myCipher.initForEncrypt();
        final byte[] myBytes = OceanusDataConverter.stringToByteArray(pData);
        final byte[] myEncrypted = myCipher.finish(myBytes, 0, myBytes.length);

        /* Check that a second decryption works */
        final byte[] myEncrypted2 = myCipher.finish(myBytes, 0, myBytes.length);

        /* Check encryption length */
        Assertions.assertEquals(GordianCoreKeySet.getEncryptionLength(myBytes.length),
                myEncrypted.length, "Incorrect encrypted length");

        /* Check for short output buffer */
        Assertions.assertThrows(GordianLogicException.class,
                () -> myCipher.finish(myBytes,0, myBytes.length, myEncrypted2, 1), "Short output");

       /* return the result */
        return myEncrypted2;
    }

    /**
     * encrypt data via oneOff cipher.
     * @param pKeySet the keySet
     * @param pData the data to encrypt
     * @param pAAD the AAD
     * @return the encrypted data
     * @throws OceanusException on error
     */
    private byte[] encryptOneOffAADCipher(final FactoryKeySet pKeySet,
                                          final String pData,
                                          final String pAAD) throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();
        final GordianKeySetAADCipher myCipher = myKeySet.createAADCipher();
        final byte[] myAAD = pAAD == null
                             ? null
                             : OceanusDataConverter.stringToByteArray(pAAD);

        /* Encrypt string */
        myCipher.initForEncrypt(myAAD);
        final byte[] myBytes = OceanusDataConverter.stringToByteArray(pData);
        final byte[] myEncrypted = myCipher.finish(myBytes, 0, myBytes.length);

        /* Check encryption length */
        Assertions.assertEquals(GordianCoreKeySet.getAADEncryptionLength(myBytes.length),
                    myEncrypted.length, "Incorrect encrypted length");

        /* return the result */
        return myEncrypted;
    }

    /**
     * encrypt data via cached cipher.
     * @param pKeySet the keySet
     * @param pData the data to encrypt
     * @return the encrypted data
     * @throws OceanusException on error
     */
    private byte[] encryptCacheAADCipher(final FactoryKeySet pKeySet,
                                         final String pData,
                                         final String pAAD) throws OceanusException {
        /* Access the keySet */
        final GordianKeySetAADCipher myCipher = pKeySet.getKeySetAADCipher();
        final byte[] myAAD = pAAD == null
                             ? null
                             : OceanusDataConverter.stringToByteArray(pAAD);

        /* Encrypt string */
        myCipher.initForEncrypt(myAAD);
        final byte[] myBytes = OceanusDataConverter.stringToByteArray(pData);
        final byte[] myEncrypted = myCipher.finish(myBytes, 0, myBytes.length);

        /* Check that a second decryption works */
        final byte[] myEncrypted2 = myCipher.finish(myBytes, 0, myBytes.length);

        /* Check encryption length */
        Assertions.assertEquals(GordianCoreKeySet.getAADEncryptionLength(myBytes.length),
                myEncrypted.length, "Incorrect encrypted length");

        /* Check for short output buffer */
        Assertions.assertThrows(GordianLogicException.class,
                () -> myCipher.finish(myBytes,0, myBytes.length, myEncrypted, 1), "Short output");

        /* return the result */
        return myEncrypted2;
    }

    /**
     * decrypt data via oneOff call.
     * @param pKeySet the keySet
     * @param pData the data to dencrypt
     * @return the decrypted string
     * @throws OceanusException on error
     */
    private String decryptOneOff(final FactoryKeySet pKeySet,
                                 final byte[] pData) throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();

        /* Decrypt string */
        final byte[] myResult = myKeySet.decryptBytes(pData);
        return OceanusDataConverter.byteArrayToString(myResult);
    }

    /**
     * decrypt data via oneOff cipher.
     * @param pKeySet the keySet
     * @param pData the data to decrypt
     * @return the decrypted string
     * @throws OceanusException on error
     */
    private String decryptOneOffCipher(final FactoryKeySet pKeySet,
                                       final byte[] pData) throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();
        final GordianKeySetCipher myCipher = myKeySet.createCipher();

        /* Decrypt string */
        myCipher.initForDecrypt();
        final byte[] myResult = myCipher.finish(pData, 0, pData.length);
        return OceanusDataConverter.byteArrayToString(myResult);
    }

    /**
     * decrypt data via cached cipher.
     * @param pKeySet the keySet
     * @param pData the data to decrypt
     * @return the decrypted string
     * @throws OceanusException on error
     */
    private String decryptCacheCipher(final FactoryKeySet pKeySet,
                                      final byte[] pData) throws OceanusException {
        /* Access the keySet */
        final GordianKeySetCipher myCipher = pKeySet.getKeySetCipher();

        /* Decrypt string */
        myCipher.initForDecrypt();
        final byte[] myResult = myCipher.finish(pData, 0, pData.length);

        /* Check that a second decryption matches */
        final byte[] myResult2 = myCipher.finish(pData, 0, pData.length);
        Assertions.assertArrayEquals(myResult, myResult2, "Incorrect reset");

        /* Check for short input buffer */
        Assertions.assertThrows(GordianDataException.class,
                () -> myCipher.finish(pData,0, myResult.length - 1, myResult2, 0), "Short input");

        /* Check for short output buffer */
        Assertions.assertThrows(GordianLogicException.class,
                () -> myCipher.finish(pData,0, pData.length, myResult2, 1), "Short output");

        /* return the result */
        return OceanusDataConverter.byteArrayToString(myResult);
    }

    /**
     * decrypt data via oneOff cipher.
     * @param pKeySet the keySet
     * @param pData the data to decrypt
     * @param pAAD the AAD
     * @return the decrypted string
     * @throws OceanusException on error
     */
    private String decryptOneOffAADCipher(final FactoryKeySet pKeySet,
                                          final byte[] pData,
                                          final String pAAD) throws OceanusException {
        /* Access the keySet */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();
        final GordianKeySetAADCipher myCipher = myKeySet.createAADCipher();
        final byte[] myAAD = pAAD == null
                             ? null
                             : OceanusDataConverter.stringToByteArray(pAAD);

        /* Decrypt string */
        myCipher.initForDecrypt(myAAD);
        final byte[] myResult = myCipher.finish(pData, 0, pData.length);
        return OceanusDataConverter.byteArrayToString(myResult);
    }

    /**
     * decrypt data via cached AAD cipher.
     * @param pKeySet the keySet
     * @param pData the data to decrypt
     * @param pAAD the AAD
     * @return the decrypted string
     * @throws OceanusException on error
     */
    private String decryptCacheAADCipher(final FactoryKeySet pKeySet,
                                         final byte[] pData,
                                         final String pAAD) throws OceanusException {
        /* Access the keySet */
        final GordianKeySetAADCipher myCipher = pKeySet.getKeySetAADCipher();
        final byte[] myAAD = pAAD == null
                             ? null
                             : OceanusDataConverter.stringToByteArray(pAAD);

        /* Decrypt string */
        myCipher.initForDecrypt(myAAD);
        final byte[] myResult = myCipher.finish(pData, 0, pData.length);

        /* Check that a second decryption matches */
        final byte[] myResult2 = myCipher.finish(pData, 0, pData.length);
        Assertions.assertArrayEquals(myResult, myResult2, "Incorrect reset");

        /* Check for short input buffer */
        Assertions.assertThrows(GordianDataException.class,
                () -> myCipher.finish(pData,0, myResult.length - 1, myResult2, 0), "Short input");

        /* Check for short output buffer */
        Assertions.assertThrows(GordianLogicException.class,
                () -> myCipher.finish(pData,0, pData.length, myResult2, 1), "Short output");

        /* return the result */
        return OceanusDataConverter.byteArrayToString(myResult);
    }

    /**
     * Check wrapping.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkWrap(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keys */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();
        final GordianKey<GordianSymKeySpec> mySymKey = pKeySet.getSymKey();
        final GordianKey<GordianStreamKeySpec> myStreamKey = pKeySet.getStreamKey();
        final GordianKey<GordianMacSpec> myMacKey = pKeySet.getMacKey();
        final GordianLength myKeyLen = pKeySet.getPasswordLockSpec().getKeySetSpec().getKeyLength();

        /* Check wrap of symKey */
        final byte[] mySymSafe = myKeySet.secureKey(mySymKey);
        final GordianKey<GordianSymKeySpec> mySymResult = myKeySet.deriveKey(mySymSafe, mySymKey.getKeyType());
        Assertions.assertEquals(mySymKey, mySymResult, "Failed to wrap/unwrap symKey");
        Assertions.assertEquals(myKeySet.getKeyWrapLength(myKeyLen), mySymSafe.length, "Incorrect wrapped symLength");

        /* Check wrap of streamKey */
        final byte[] myStreamSafe = myKeySet.secureKey(myStreamKey);
        final GordianKey<GordianStreamKeySpec> myStreamResult = myKeySet.deriveKey(myStreamSafe, myStreamKey.getKeyType());
        Assertions.assertEquals(myStreamKey, myStreamResult, "Failed to wrap/unwrap streamKey");
        Assertions.assertEquals(myKeySet.getKeyWrapLength(myKeyLen), myStreamSafe.length, "Incorrect wrapped streamLength");

        /* Check wrap of macKey */
        final byte[] myMacSafe = myKeySet.secureKey(myMacKey);
        final GordianKey<GordianMacSpec> myMacResult = myKeySet.deriveKey(myMacSafe, myMacKey.getKeyType());
        Assertions.assertEquals(myMacKey, myMacResult, "Failed to wrap/unwrap macKey");
        Assertions.assertEquals(myKeySet.getKeyWrapLength(myKeyLen), myMacSafe.length, "Incorrect wrapped macLength: " + myMacKey.getKeyType());

        /* Check wrap of keySet */
        final byte[] myKeySetSafe = myKeySet.secureKeySet(myKeySet);
        final GordianKeySet myKeySetResult = myKeySet.deriveKeySet(myKeySetSafe);
        Assertions.assertEquals(myKeySet, myKeySetResult, "Failed to wrap/unwrap keySet");
        Assertions.assertEquals(myKeySet.getKeySetWrapLength(), myKeySetSafe.length, "Incorrect wrapped keySetLength");
    }

    /**
     * Check wrapping.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void checkFactory(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keys */
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) pKeySet.getKeySet();
        final GordianFactory myFactory = GordianGenerator.createRandomFactory(myKeySet.getFactory().getFactoryType());
        final byte[] myWrapped = myKeySet.secureFactory(myFactory);
        final GordianFactory myUnWrapped = myKeySet.deriveFactory(myWrapped);
        Assertions.assertEquals(myFactory, myUnWrapped, "Failed to secure/derive factory");
    }

    /**
     * Profile encrypt.
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private void profileEncrypt(final FactoryKeySet pKeySet) throws OceanusException {
        /* Access the keys */
        final GordianKeySet myKeySet = pKeySet.getKeySet();

        /* Create the test data */
        final byte[] myData = new byte[1000];
        new SecureRandom().nextBytes(myData);

        /* Loop through encrypt/decrypt */
        final long myStart = System.nanoTime();
        for (int i = 0; i < profileRepeat; i++) {
            final byte[] myEncrypt = myKeySet.encryptBytes(myData);
            final byte[] myResult = myKeySet.decryptBytes(myEncrypt);
            Assertions.assertArrayEquals(myData, myResult, "Failed to decrypt data");
        }
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= (long) SymmetricTest.MILLINANOS * profileRepeat;
        if (fullProfiles) {
            System.out.println("Elapsed: " + myElapsed);
        }
    }

    /**
     * create a random factory and lock/resolve it.
     * @throws OceanusException on error
     */
    private void testRandomFactory() throws OceanusException {
        /* Create the random factory */
        final GordianFactory myFactory = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
        final GordianLockFactory myLockFactory = myFactory.getLockFactory();
        final GordianFactoryLock mySecured = myLockFactory.newFactoryLock(myFactory, DEF_PASSWORD.clone());
        final GordianFactoryLock myResolved = myLockFactory.resolveFactoryLock(mySecured.getLockBytes(), DEF_PASSWORD.clone());
        Assertions.assertEquals(myFactory, myResolved.getFactory(), "Failed to lock/resolve factory");
        Assertions.assertEquals(GordianUtilities.getFactoryLockLen(), mySecured.getLockBytes().length, "Incorrect factoryLockLength");
    }
}
