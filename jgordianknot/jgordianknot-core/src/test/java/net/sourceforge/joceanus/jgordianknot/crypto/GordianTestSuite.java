/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.io.File;
import java.util.function.Predicate;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Security Test suite.
 */
public class GordianTestSuite {
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
        GordianHashManager newSecureManager(final GordianParameters pParams) throws OceanusException;
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
        GordianTestZip myZipTest = new GordianTestZip(theCreator);

        /* Obtain the home directory */
        String myHome = System.getProperty("user.home");

        /* Run the tests */
        File myZipFile = new File(myHome, "TestStdZip.zip");
        myZipTest.createZipFile(myZipFile, new File(myHome, "tester"), true);
        myZipTest.extractZipFile(myZipFile, new File(myHome, "testcomp"));
    }

    /**
     * Check the supported algorithms.
     * @throws OceanusException on error
     */
    protected void checkAlgorithms() throws OceanusException {
        /* Create the Algorithm Tester */
        GordianTestAlgorithms myAlgTest = new GordianTestAlgorithms(theCreator);

        /* Check Algorithms */
        myAlgTest.checkAlgorithms();
    }

    /**
     * Test security algorithms.
     * @throws OceanusException on error
     */
    protected void testSecurity() throws OceanusException {
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
        String myTestName = pType.toString() + "-" + (pRestricted
                                                                  ? "Restricted"
                                                                  : "Unlimited");

        /* Create new Password Hash */
        GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        GordianHashManager myManager = theCreator.newSecureManager(myParams);
        GordianKeySetHash myHash = myManager.newKeySetHash(myTestName);
        GordianKeySet myKeySet = myHash.getKeySet();
        GordianFactory myFactory = myKeySet.getFactory();

        /* Create new symmetric key and stream Key */
        GordianKey<GordianSymKeySpec> mySym = myFactory.generateRandomSymKey();
        GordianKey<GordianStreamKeyType> myStream = myFactory.generateRandomStreamKey();

        /* Secure the keys */
        byte[] mySymSafe = myKeySet.secureKey(mySym);
        byte[] myStreamSafe = myKeySet.secureKey(myStream);

        /* Encrypt short block */
        String myTest1 = "TestString";
        byte[] myBytes = TethysDataConverter.stringToByteArray(myTest1);
        byte[] myEncrypt1 = myKeySet.encryptBytes(myBytes);

        /* Encrypt full block */
        String myTest2 = "TestString123456";
        myBytes = TethysDataConverter.stringToByteArray(myTest2);
        byte[] myEncrypt2 = myKeySet.encryptBytes(myBytes);

        /* Encrypt some multi-block */
        String myTest3 = "TestString1234567";
        myBytes = TethysDataConverter.stringToByteArray(myTest3);
        byte[] myEncrypt3 = myKeySet.encryptBytes(myBytes);

        /* Loop through the digests */
        Predicate<GordianDigestSpec> myDigestPredicate = myFactory.supportedDigestSpecs();
        for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            /* If the digest is supported */
            if (myDigestPredicate.test(mySpec)) {
                /* Check the externalId */
                long myId = myKeySet.deriveExternalIdForType(mySpec);
                GordianDigestSpec myResult = myKeySet.deriveTypeFromExternalId(myId, GordianDigestSpec.class);
                if (!mySpec.equals(myResult)) {
                    System.out.println("Failed to resolve externalId for digest: " + mySpec);
                }
            }
        }

        /* Loop through the macs */
        Predicate<GordianMacSpec> myMacPredicate = myFactory.supportedMacSpecs();
        for (GordianMacSpec mySpec : GordianMacSpec.listAll()) {
            /* If the mac is supported */
            if (myMacPredicate.test(mySpec)) {
                /* Check the externalId */
                long myId = myKeySet.deriveExternalIdForType(mySpec);
                GordianMacSpec myResult = myKeySet.deriveTypeFromExternalId(myId, GordianMacSpec.class);
                if (!mySpec.equals(myResult)) {
                    System.out.println("Failed to resolve externalId for mac: " + mySpec);
                }
            }
        }

        /* Create a data digest */
        GordianDigest myDigest = myFactory.generateRandomDigest();
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        byte[] myDigestBytes = myDigest.finish();

        /* Create a data MAC */
        GordianMac myMac = myFactory.generateRandomMac();
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        byte[] myMacBytes = myMac.finish();

        /* Secure the keys */
        byte[] myMacSafe = myKeySet.secureKey(myMac.getKey());
        byte[] myIV = myMac.getInitVector();
        long myMacId = myKeySet.deriveExternalIdForType(myMac.getMacSpec());

        /* Create AsymTest Control */
        GordianTestAsymmetric myAsymTest = new GordianTestAsymmetric(mySymSafe, myStreamSafe);
        // myAsymTest.testKeyPairs(myFactory, myKeySet);
        myAsymTest.createKeyPairs(myFactory, myKeySet);

        /* Start a new session */
        myManager = theCreator.newSecureManager(myParams);
        GordianKeySetHash myNewHash = myManager.resolveKeySetHash(myHash.getHash(), myTestName);
        GordianKeySet myKeySet1 = myNewHash.getKeySet();
        myFactory = myKeySet.getFactory();

        /* Check the keySets are the same */
        if (!myKeySet1.equals(myKeySet)) {
            System.out.println("Failed to derive keySet");
        }

        /* Derive the Mac */
        GordianMacSpec myMacSpec = myKeySet1.deriveTypeFromExternalId(myMacId, GordianMacSpec.class);
        GordianKey<GordianMacSpec> myMacKey = myKeySet1.deriveKey(myMacSafe, myMacSpec);
        myMac = myFactory.createMac(myMacSpec);
        myMac.initMac(myMacKey, myIV);
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        byte[] myMac1Bytes = myMac.finish();

        /* Create a message digest */
        myDigest = myFactory.createDigest(myDigest.getDigestSpec());
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        byte[] myNewBytes = myDigest.finish();

        /* Check the digests are the same */
        if (!Arrays.areEqual(myDigestBytes, myNewBytes)) {
            System.out.println("Failed to recalculate digest");
        }
        if (!Arrays.areEqual(myMacBytes, myMac1Bytes)) {
            System.out.println("Failed to recalculate mac");
        }

        /* Derive the keys */
        GordianKey<GordianSymKeySpec> mySym1 = myKeySet1.deriveKey(mySymSafe, mySym.getKeyType());
        GordianKey<GordianStreamKeyType> myStm1 = myKeySet1.deriveKey(myStreamSafe, myStream.getKeyType());

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
