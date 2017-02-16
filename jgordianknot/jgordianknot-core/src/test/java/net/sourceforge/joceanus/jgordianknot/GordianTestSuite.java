/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

import java.io.File;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyFactory;
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
        /* Create new Password Hash */
        GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        GordianHashManager myManager = theCreator.newSecureManager(myParams);
        GordianKeySetHash myHash = myManager.resolveKeySetHash(null, "New");
        GordianKeySet myKeySet = myHash.getKeySet();
        GordianFactory myFactory = myKeySet.getFactory();

        /* Create AsymKeyPairs */
        AsymPairStatus myRSAStatus = new AsymPairStatus(myFactory, myKeySet, GordianAsymKeySpec.rsa(GordianModulus.MOD2048));
        AsymPairStatus myECStatus = new AsymPairStatus(myFactory, myKeySet, GordianAsymKeySpec.ec(GordianElliptic.SECT571K1));
        AsymPairStatus myDHStatus = new AsymPairStatus(myFactory, myKeySet, GordianAsymKeySpec.dh(GordianModulus.MOD4096));
        AsymPairStatus mySPHINCSStatus = new AsymPairStatus(myFactory, myKeySet, GordianAsymKeySpec.sphincs());
        AsymPairStatus myRainbowStatus = new AsymPairStatus(myFactory, myKeySet, GordianAsymKeySpec.rainbow());
        AsymPairStatus myNHStatus = new AsymPairStatus(myFactory, myKeySet, GordianAsymKeySpec.newHope());

        /* Check Key Exchange */
        myRSAStatus.checkKEMS();
        myECStatus.checkKEMS();
        myDHStatus.checkKEMS();
        myNHStatus.checkKEMS();

        /* Create new symmetric key and stream Key */
        GordianKey<GordianSymKeyType> mySym = myFactory.generateRandomSymKey();
        GordianKey<GordianStreamKeyType> myStream = myFactory.generateRandomStreamKey();

        /* Secure the keys */
        byte[] mySymSafe = myKeySet.secureKey(mySym);
        byte[] myStreamSafe = myKeySet.secureKey(myStream);

        /* Encrypt some bytes */
        String myTest = "TestString";
        byte[] myBytes = TethysDataConverter.stringToByteArray(myTest);
        byte[] myEncrypt = myKeySet.encryptBytes(myBytes);

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
        int myMacId = myKeySet.deriveExternalIdForType(myMac.getMacSpec());

        /* Create signatures */
        myRSAStatus.createSignatures(mySymSafe, myStreamSafe);
        myECStatus.createSignatures(mySymSafe, myStreamSafe);
        mySPHINCSStatus.createSignatures(mySymSafe, myStreamSafe);
        myRainbowStatus.createSignatures(mySymSafe, myStreamSafe);

        /* Start a new session */
        myManager = theCreator.newSecureManager(myParams);
        GordianKeySetHash myNewHash = myManager.resolveKeySetHash(myHash.getHash(), "Test");
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
        GordianKey<GordianSymKeyType> mySym1 = myKeySet1.deriveKey(mySymSafe, mySym.getKeyType());
        GordianKey<GordianStreamKeyType> myStm1 = myKeySet1.deriveKey(myStreamSafe, myStream.getKeyType());

        /* Derive the new AsymStatii */
        myRSAStatus = new AsymPairStatus(myFactory, myKeySet1, myRSAStatus);
        myECStatus = new AsymPairStatus(myFactory, myKeySet1, myECStatus);
        myDHStatus = new AsymPairStatus(myFactory, myKeySet1, myDHStatus);
        mySPHINCSStatus = new AsymPairStatus(myFactory, myKeySet1, mySPHINCSStatus);
        myRainbowStatus = new AsymPairStatus(myFactory, myKeySet1, myRainbowStatus);
        myNHStatus = new AsymPairStatus(myFactory, myKeySet1, myNHStatus);

        /* Check the keys are the same */
        if (!mySym1.equals(mySym)) {
            System.out.println("Failed to decrypt SymmetricKey");
        }
        if (!myStm1.equals(myStream)) {
            System.out.println("Failed to decrypt StreamKey");
        }

        /* Validate the signatures */
        myRSAStatus.validateSignatures(mySymSafe, myStreamSafe);
        myECStatus.validateSignatures(mySymSafe, myStreamSafe);
        mySPHINCSStatus.validateSignatures(mySymSafe, myStreamSafe);
        myRainbowStatus.validateSignatures(mySymSafe, myStreamSafe);

        /* Decrypt the bytes */
        byte[] myResult = myKeySet1.decryptBytes(myEncrypt);
        String myAnswer = TethysDataConverter.byteArrayToString(myResult);
        if (!myAnswer.equals(myTest)) {
            System.out.println("Failed to decrypt test string");
        }
    }

    /**
     * Asymmetric Status.
     */
    private static class AsymPairStatus {
        /**
         * The Factory.
         */
        private final GordianFactory theFactory;

        /**
         * The KeyPair.
         */
        private final GordianKeyPair thePair;

        /**
         * The Public KeySpec.
         */
        private final X509EncodedKeySpec thePublic;

        /**
         * The Private KeySpec.
         */
        private final byte[] thePrivate;

        /**
         * Signature Map.
         */
        private final Map<GordianSignatureSpec, byte[]> theSignatures;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySet the keySet
         * @param pKeySpec the Asymmetric KeyType.
         * @throws OceanusException on error
         */
        private AsymPairStatus(final GordianFactory pFactory,
                               final GordianKeySet pKeySet,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            GordianKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pKeySpec);
            thePair = myGenerator.generateKeyPair();

            /* Secure the keys */
            thePrivate = myGenerator.securePrivateKey(thePair, pKeySet);
            thePublic = myGenerator.getX509Encoding(thePair);

            /* Create the signature map */
            theSignatures = new HashMap<>();
        }

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySet the keySet
         * @param pStatus the Base Status.
         * @throws OceanusException on error
         */
        private AsymPairStatus(final GordianFactory pFactory,
                               final GordianKeySet pKeySet,
                               final AsymPairStatus pStatus) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            GordianKeyPairGenerator myGenerator = pFactory.getKeyPairGenerator(pStatus.getKeySpec());
            thePair = myGenerator.deriveKeyPair(pStatus.thePublic, pStatus.thePrivate, pKeySet);

            /* Don't worry about the keySpes */
            thePrivate = null;
            thePublic = null;

            /* Record the signature map */
            theSignatures = pStatus.theSignatures;

            if (!thePair.equals(pStatus.thePair)) {
                System.out.println("Failed to decrypt KeyPair for: " + getKeySpec());
            }
        }

        /**
         * Obtain the keySpec.
         * @return the keySpec
         */
        private GordianAsymKeySpec getKeySpec() {
            return thePair.getKeySpec();
        }

        /**
         * Create the signatures.
         * @param pSymSafe the encrypted SymKey
         * @param pStreamSafe the encrypted StreamKey
         * @throws OceanusException on error
         */
        public void createSignatures(final byte[] pSymSafe,
                                     final byte[] pStreamSafe) throws OceanusException {
            /* Access the signature predicate */
            Predicate<GordianSignatureSpec> mySignPredicate = theFactory.supportedSignatures();

            /* For each possible digestSpec */
            for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
                /* Create the corresponding signatureSpec */
                GordianSignatureSpec mySign = new GordianSignatureSpec(getKeySpec().getKeyType(), mySpec);

                /* If the signature is supported */
                if (mySignPredicate.test(mySign)) {
                    createSignature(mySign, pSymSafe, pStreamSafe);
                }
            }
        }

        /**
         * Create the signature.
         * @param pSignatureSpec the signatureSpec
         * @param pSymSafe the encrypted SymKey
         * @param pStreamSafe the encrypted StreamKey
         * @throws OceanusException on error
         */
        private void createSignature(final GordianSignatureSpec pSignatureSpec,
                                     final byte[] pSymSafe,
                                     final byte[] pStreamSafe) throws OceanusException {
            GordianSigner mySigner = theFactory.createSigner(thePair, pSignatureSpec);
            mySigner.update(pSymSafe);
            mySigner.update(pStreamSafe);
            theSignatures.put(pSignatureSpec, mySigner.sign());
        }

        /**
         * Validate the signatures.
         * @param pSymSafe the encrypted SymKey
         * @param pStreamSafe the encrypted StreamKey
         * @throws OceanusException on error
         */
        public void validateSignatures(final byte[] pSymSafe,
                                       final byte[] pStreamSafe) throws OceanusException {
            /* For each signature that has been created */
            Iterator<GordianSignatureSpec> myIterator = theSignatures.keySet().iterator();
            while (myIterator.hasNext()) {
                GordianSignatureSpec mySpec = myIterator.next();
                validateSignature(mySpec, pSymSafe, pStreamSafe);
            }
        }

        /**
         * Validate the signature.
         * @param pSignatureSpec the signatureSpec
         * @param pSymSafe the encrypted SymKey
         * @param pStreamSafe the encrypted StreamKey
         * @throws OceanusException on error
         */
        private void validateSignature(final GordianSignatureSpec pSignatureSpec,
                                       final byte[] pSymSafe,
                                       final byte[] pStreamSafe) throws OceanusException {
            GordianValidator myValidator = theFactory.createValidator(thePair, pSignatureSpec);
            myValidator.update(pSymSafe);
            myValidator.update(pStreamSafe);
            if (!myValidator.verify(theSignatures.get(pSignatureSpec))) {
                System.out.println("Failed to validate signature: " + pSignatureSpec);
            }
        }

        /**
         * Check KEMS.
         * @param pDigestType the digestType
         * @param pSymSafe the encrypted SymKey
         * @param pStreamSafe the encrypted StreamKey
         * @throws OceanusException on error
         */
        private void checkKEMS() throws OceanusException {
            /* If the factory is a Bouncy Factory */
            if (theFactory instanceof BouncyFactory) {
                BouncyFactory myFactory = (BouncyFactory) theFactory;
                /* Perform the key exchange */
                GordianKEMSender mySender = myFactory.createKEMessage(thePair);
                GordianKeyEncapsulation myReceiver = myFactory.parseKEMessage(thePair, mySender.getCipherText());

                /* Check agreement */
                GordianKeySet myKeySet = mySender.deriveKeySet();
                if (!myKeySet.equals(myReceiver.deriveKeySet())) {
                    System.out.println("Failed to agree keys: " + getKeySpec());
                }
            }
        }
    }
}
