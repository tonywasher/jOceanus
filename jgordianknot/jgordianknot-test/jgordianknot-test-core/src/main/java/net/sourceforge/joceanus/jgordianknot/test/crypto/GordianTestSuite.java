/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.test.crypto;

import java.io.File;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite.
 */
public class GordianTestSuite {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * Interface for Security Manager creator.
     */
    public interface SecurityManagerCreator {
        /**
         * Create a new SecureManager with default parameters.
         * @return the new SecureManager
         * @throws OceanusException on error
         */
        GordianHashManager newSecureManager() throws OceanusException;

        /**
         * Create a new SecureManager.
         * @param pParams the security parameters
         * @return the new SecureManager
         * @throws OceanusException on error
         */
        GordianHashManager newSecureManager(GordianParameters pParams) throws OceanusException;
    }

    /**
     * The Security Manager creator.
     */
    private final SecurityManagerCreator theCreator;

    /**
     * Constructor.
     * @param pCreator the Secure Manager creator
     */
    public GordianTestSuite(final SecurityManagerCreator pCreator) {
        theCreator = pCreator;
    }

    /**
     * Test Zip File.
     * @throws OceanusException on error
     */
    public void testZipFile() throws OceanusException {
        /* Create the Zip Tester */
        final GordianTestZip myZipTest = new GordianTestZip(theCreator);

        /* Obtain the home directory */
        final String myHome = System.getProperty("user.home");

        /* Run the tests */
        final File myZipFile = new File(myHome, "TestStdZip.zip");
        myZipTest.createZipFile(myZipFile, new File(myHome, "tester"), true);
        myZipTest.extractZipFile(myZipFile, new File(myHome, "testcomp"));
    }

    /**
     * Check the supported algorithms.
     * @throws OceanusException on error
     */
    public void checkAlgorithms() throws OceanusException {
        /* Create the Algorithm Tester */
        final GordianTestAlgorithms myAlgTest = new GordianTestAlgorithms(theCreator);

        /* Check Algorithms */
        myAlgTest.checkAlgorithms();
    }

    /**
     * Test security algorithms.
     * @throws OceanusException on error
     */
    public void testSecurity() throws OceanusException {
        testSecurity(true, GordianFactoryType.BC);
        testSecurity(false, GordianFactoryType.BC);
        testSecurity(true, GordianFactoryType.JCA);
        testSecurity(false, GordianFactoryType.JCA);
    }

    /**
     * Test security algorithms.
     * @param pRestricted is the factory restricted
     * @param pType the type of factory
     * @throws OceanusException on error
     */
    private void testSecurity(final boolean pRestricted,
                              final GordianFactoryType pType) throws OceanusException {
        /* Determine test name */
        final String myTestName = pType.toString() + "-" + (pRestricted
                                                                        ? "Restricted"
                                                                        : "Unlimited");
        System.out.println("Running tests for " + myTestName);

        /* Create new Password Hash */
        final GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        GordianHashManager myManager = theCreator.newSecureManager(myParams);
        final GordianKeySetHash myHash = myManager.getSecurityFactory().generateKeySetHash(DEF_PASSWORD.clone());
        final GordianKeySet myKeySet = myHash.getKeySet();
        GordianFactory myFactory = myKeySet.getFactory();
        GordianKnuthObfuscater myKnuth = myFactory.getObfuscater();

        /* Create new symmetric key and stream Key */
        final GordianKey<GordianSymKeySpec> mySym = myFactory.generateRandomSymKey();
        final GordianKey<GordianStreamKeyType> myStream = myFactory.generateRandomStreamKey();

        /* Secure the keys */
        GordianKeyGenerator<GordianSymKeySpec> mySymGen = myFactory.getKeyGenerator(mySym.getKeyType());
        final byte[] mySymSafe = mySymGen.secureKey(mySym, myKeySet);
        GordianKeyGenerator<GordianStreamKeyType> myStreamGen = myFactory.getKeyGenerator(myStream.getKeyType());
        final byte[] myStreamSafe = myStreamGen.secureKey(myStream, myKeySet);

        /* Encrypt short block */
        final String myTest1 = "TestString";
        byte[] myBytes = TethysDataConverter.stringToByteArray(myTest1);
        final byte[] myEncrypt1 = myKeySet.encryptBytes(myBytes);

        /* Encrypt full block */
        final String myTest2 = "TestString123456";
        myBytes = TethysDataConverter.stringToByteArray(myTest2);
        final byte[] myEncrypt2 = myKeySet.encryptBytes(myBytes);

        /* Encrypt some multi-block */
        final String myTest3 = "TestString1234567";
        myBytes = TethysDataConverter.stringToByteArray(myTest3);
        final byte[] myEncrypt3 = myKeySet.encryptBytes(myBytes);

        /* Create a data digest */
        GordianDigest myDigest = myFactory.generateRandomDigest();
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        final byte[] myDigestBytes = myDigest.finish();

        /* Create a data MAC */
        GordianMac myMac = myFactory.generateRandomMac();
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        final byte[] myMacBytes = myMac.finish();

        /* Secure the keys */
        GordianKeyGenerator<GordianMacSpec> myMacGen = myFactory.getKeyGenerator(myMac.getKey().getKeyType());
        final byte[] myMacSafe = myMacGen.secureKey(myMac.getKey(), myKeySet);
        final byte[] myIV = myMac.getInitVector();
        final int myMacId = myKnuth.deriveExternalIdFromType(myMac.getMacSpec());

        /* Create AsymTest Control */
        final GordianTestAsymmetric myAsymTest = new GordianTestAsymmetric(mySymSafe, myStreamSafe);
        // myAsymTest.testKeyPairs(myFactory, myKeySet);
        myAsymTest.createKeyPairs(myFactory, myKeySet);

        /* Start a new session */
        myManager = theCreator.newSecureManager(myParams);
        final GordianKeySetHash myNewHash = myManager.getSecurityFactory().deriveKeySetHash(myHash.getHash(), DEF_PASSWORD.clone());
        final GordianKeySet myKeySet1 = myNewHash.getKeySet();
        myFactory = myKeySet.getFactory();
        myKnuth = myFactory.getObfuscater();

        /* Check the keySets are the same */
        if (!myKeySet1.equals(myKeySet)) {
            System.out.println("Failed to derive keySet");
        }

        /* Derive the Mac */
        final GordianMacSpec myMacSpec = myKnuth.deriveTypeFromExternalId(myMacId, GordianMacSpec.class);
        myMacGen = myFactory.getKeyGenerator(myMacSpec);
        final GordianKey<GordianMacSpec> myMacKey = myMacGen.deriveKey(myMacSafe, myKeySet1);
        myMac = myFactory.createMac(myMacSpec);
        myMac.initMac(myMacKey, myIV);
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        final byte[] myMac1Bytes = myMac.finish();

        /* Create a message digest */
        myDigest = myFactory.createDigest(myDigest.getDigestSpec());
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        final byte[] myNewBytes = myDigest.finish();

        /* Check the digests are the same */
        if (!Arrays.areEqual(myDigestBytes, myNewBytes)) {
            System.out.println("Failed to recalculate digest");
        }
        if (!Arrays.areEqual(myMacBytes, myMac1Bytes)) {
            System.out.println("Failed to recalculate mac");
        }

        /* Derive the keys */
        mySymGen = myFactory.getKeyGenerator(mySym.getKeyType());
        final GordianKey<GordianSymKeySpec> mySym1 = mySymGen.deriveKey(mySymSafe, myKeySet1);
        myStreamGen = myFactory.getKeyGenerator(myStream.getKeyType());
        final GordianKey<GordianStreamKeyType> myStm1 = myStreamGen.deriveKey(myStreamSafe, myKeySet1);

        /* Check the keys are the same */
        if (!mySym1.equals(mySym)) {
            System.out.println("Failed to decrypt SymmetricKey");
        }
        if (!myStm1.equals(myStream)) {
            System.out.println("Failed to decrypt StreamKey");
        }

        /* Validate the Asymmetric Tests */
        myAsymTest.validateKeyPairs(myFactory, myKeySet1);

        /* Decrypt the bytes */
        byte[] myResult = myKeySet1.decryptBytes(myEncrypt1);
        String myAnswer = TethysDataConverter.byteArrayToString(myResult);
        if (!myAnswer.equals(myTest1)) {
            System.out.println("Failed to decrypt test1 string");
        }
        myResult = myKeySet1.decryptBytes(myEncrypt2);
        myAnswer = TethysDataConverter.byteArrayToString(myResult);
        if (!myAnswer.equals(myTest2)) {
            System.out.println("Failed to decrypt test2 string");
        }
        myResult = myKeySet1.decryptBytes(myEncrypt3);
        myAnswer = TethysDataConverter.byteArrayToString(myResult);
        if (!myAnswer.equals(myTest3)) {
            System.out.println("Failed to decrypt test3 string");
        }
    }
}
