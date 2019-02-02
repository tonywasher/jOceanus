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
package net.sourceforge.joceanus.jgordianknot.test.api;

import java.io.File;
import java.util.List;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKnuthObfuscater;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureType;
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
     * The test to run.
     */
    private String theTest;

    /**
     * The keyType to test.
     */
    private String theKeyType;

    /**
     * The signatureType to test.
     */
    private String theSigType;

    /**
     * Do we test allSpecs.
     */
    private boolean allSpecs;

    /**
     * Constructor.
     * @param pCreator the Secure Manager creator
     */
    public GordianTestSuite(final SecurityManagerCreator pCreator) {
        theCreator = pCreator;
    }

    /**
     * Run test
     * @param pArgs the parameters
     * @throws OceanusException on error
     */
    public void runTests(final List<String> pArgs) throws OceanusException {
        /* Process the arguments */
        processArgs(pArgs);

        /* handle symmetric algorithms */
        if ("sym".equals(theTest)) {
            checkAlgorithms();

            /* handle keySet security */
        } else if ("keyset".equals(theTest)) {
            testSecurity();

            /* handle asym tests */
        } else if ("asym".equals(theTest)) {
            testAsync();

            /* handle zip file creation */
        } else if ("zip".equals(theTest)) {
            testZipFile();

        } else {
            GordianListAlgorithms.listAlgorithms();
        }
    }

    /**
     * Constructor.
     * @param pArgs the parameters
     */
    private void processArgs(final List<String> pArgs) {
        /* Loop through the arguments */
        for (String myArg : pArgs) {
            /* If this is the test */
            if (myArg.startsWith("--test=")) {
                theTest = myArg.substring("--test=".length());

                /* if this is the key */
            } else if (myArg.startsWith("--keyType=")) {
                theKeyType = myArg.substring("--keyType=".length());

                /* if this is the signature */
            } else if (myArg.startsWith("--sigType=")) {
                theSigType = myArg.substring("--sigType=".length());

                /* If this is allSpecs */
            } else if ("--allSpecs".equals(myArg)) {
                allSpecs = true;
            }
        }
    }

    /**
     * Test Zip File.
     * @throws OceanusException on error
     */
    private void testZipFile() throws OceanusException {
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
    private void checkAlgorithms() throws OceanusException {
        /* Create the Algorithm Tester */
        final GordianTestAlgorithms myAlgTest = new GordianTestAlgorithms(theCreator);

        /* Check Algorithms */
        myAlgTest.checkAlgorithms();
    }

    /**
     * Test security algorithms.
     * @throws OceanusException on error
     */
    private void testSecurity() throws OceanusException {
        testSecurity(true, GordianFactoryType.BC);
        testSecurity(false, GordianFactoryType.BC);
        testSecurity(true, GordianFactoryType.JCA);
        testSecurity(false, GordianFactoryType.JCA);
    }

    /**
     * Test async functionality.
     * @throws OceanusException on error
     */
    private void testAsync() throws OceanusException {
        /* Create new Jca factory */
        final GordianParameters myJcaParams = new GordianParameters(false);
        myJcaParams.setFactoryType(GordianFactoryType.JCA);
        final GordianFactory myJCA = theCreator.newSecureManager(myJcaParams).getSecurityFactory();

        /* Create new Bc Factory */
        final GordianParameters myBcParams = new GordianParameters(false);
        myBcParams.setFactoryType(GordianFactoryType.BC);
        final GordianFactory myBC = theCreator.newSecureManager(myBcParams).getSecurityFactory();

        /* Determine the singleKeyType */
        GordianAsymKeyType myKeyType = null;
        for (final GordianAsymKeyType myType : GordianAsymKeyType.values()) {
            if (myType.toString().equalsIgnoreCase(theKeyType)) {
                myKeyType = myType;
            }
        }

        /* Determine the signatureType */
        GordianSignatureType mySigType = null;
        for (final GordianSignatureType myType : GordianSignatureType.values()) {
            if (myType.toString().equalsIgnoreCase(theSigType)) {
                mySigType = myType;
            }
        }

        /* Test from Jca to Bc */
        GordianTestAsymmetric myTest = new GordianTestAsymmetric(myJCA,  myBC, myKeyType, mySigType, allSpecs);
        myTest.checkKeyPairs();

        /* Test from Jca to Bc */
        myTest = new GordianTestAsymmetric(myBC,  myJCA, myKeyType, mySigType, allSpecs);
        myTest.checkKeyPairs();
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
        GordianFactory myFactory = myManager.getSecurityFactory();
        final GordianRandomFactory myRandoms = myFactory.getRandomFactory();
        GordianKeySetFactory myKeySets = myFactory.getKeySetFactory();
        final GordianKeySetHash myHash = myKeySets.generateKeySetHash(DEF_PASSWORD.clone());
        final GordianKeySet myKeySet = myHash.getKeySet();
        GordianKnuthObfuscater myKnuth = myKeySets.getObfuscater();

        /* Create new symmetric key and stream Key */
        final GordianKey<GordianSymKeySpec> mySym = myRandoms.generateRandomSymKey();
        final GordianKey<GordianStreamKeyType> myStream = myRandoms.generateRandomStreamKey();

        /* Secure the keys */
        final byte[] mySymSafe = myKeySet.secureKey(mySym);
        final byte[] myStreamSafe = myKeySet.secureKey(myStream);

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
        GordianDigest myDigest = myRandoms.generateRandomDigest();
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        final byte[] myDigestBytes = myDigest.finish();

        /* Create a data MAC */
        GordianMac myMac = myRandoms.generateRandomMac();
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        final byte[] myMacBytes = myMac.finish();

        /* Secure the keys */
        final byte[] myMacSafe = myKeySet.secureKey(myMac.getKey());
        final byte[] myIV = myMac.getInitVector();
        final int myMacId = myKnuth.deriveExternalIdFromType(myMac.getMacSpec());

        /* Start a new session */
        myManager = theCreator.newSecureManager(myParams);
        myFactory = myManager.getSecurityFactory();
        final GordianDigestFactory myDigests = myFactory.getDigestFactory();
        final GordianMacFactory myMacs = myFactory.getMacFactory();
        myKeySets = myFactory.getKeySetFactory();
        final GordianKeySetHash myNewHash = myKeySets.deriveKeySetHash(myHash.getHash(), DEF_PASSWORD.clone());
        final GordianKeySet myKeySet1 = myNewHash.getKeySet();
        myKnuth = myKeySets.getObfuscater();

        /* Check the keySets are the same */
        if (!myKeySet1.equals(myKeySet)) {
            System.out.println("Failed to derive keySet");
        }

        /* Derive the Mac */
        final GordianMacSpec myMacSpec = myKnuth.deriveTypeFromExternalId(myMacId, GordianMacSpec.class);
        final GordianKey<GordianMacSpec> myMacKey = myKeySet1.deriveKey(myMacSafe, myMacSpec);
        myMac = myMacs.createMac(myMacSpec);
        myMac.initMac(myMacKey, myIV);
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        final byte[] myMac1Bytes = myMac.finish();

        /* Create a message digest */
        myDigest = myDigests.createDigest(myDigest.getDigestSpec());
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
        final GordianKey<GordianSymKeySpec> mySym1 = myKeySet1.deriveKey(mySymSafe, mySym.getKeyType());
        final GordianKey<GordianStreamKeyType> myStm1 = myKeySet1.deriveKey(myStreamSafe, myStream.getKeyType());

        /* Check the keys are the same */
        if (!mySym1.equals(mySym)) {
            System.out.println("Failed to decrypt SymmetricKey");
        }
        if (!myStm1.equals(myStream)) {
            System.out.println("Failed to decrypt StreamKey");
        }

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
