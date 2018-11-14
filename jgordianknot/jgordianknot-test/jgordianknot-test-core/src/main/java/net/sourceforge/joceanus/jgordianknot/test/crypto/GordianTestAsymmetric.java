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

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymAlgId;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureAlgId;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * AsymKeys Test Control.
 */
public final class GordianTestAsymmetric {
    /**
     * AsyncKey table.
     */
    private static final GordianAsymAlgId ALGID = new GordianAsymAlgId();

    /**
     * Random source.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Test buffer size.
     */
    private static final int TESTLEN = 1024;

    /**
     * The source factory.
     */
    private final GordianFactory theSource;

    /**
     * The target factory.
     */
    private final GordianFactory theTarget;

    /**
     * The single keyType to test.
     */
    private final GordianAsymKeyType theKeyType;

    /**
     * Do we process all specs.
     */
    private final boolean allSpecs;

    /**
     * AsyncKey table.
     */
    private final GordianSignatureAlgId sigID;

    /**
     * Private Constructor.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @param pSingleType the type of key that is to be tested (or null for all)
     * @param pAllSpecs test all specs for each keyType
     */
    GordianTestAsymmetric(final GordianFactory pSource,
                          final GordianFactory pTarget,
                          final GordianAsymKeyType pSingleType,
                          final boolean pAllSpecs) {
        /* Store parameters */
        theSource = pSource;
        theTarget = pTarget;
        theKeyType = pSingleType;
        allSpecs = pAllSpecs;

        /* Build signatire table */
        sigID = new GordianSignatureAlgId(pSource);
    }

    /**
     * Create keyPairs.
     * @throws OceanusException on error
     */
    void checkKeyPairs() throws OceanusException {
        /* Check the different types */
        for (final GordianAsymKeyType myKeyType: GordianAsymKeyType.values()) {
            checkKeyPairs(myKeyType);
        }
    }

    /**
     * Create keyPairs.
     * @param pKeyType the keyType
     * @throws OceanusException on error
     */
    private void checkKeyPairs(final GordianAsymKeyType pKeyType) throws OceanusException {
        /* If we are testing a single keyType, make sure this is the right one */
        if (theKeyType != null
                && !pKeyType.equals(theKeyType)) {
            return;
        }

        /* For each possible keySpec */
        for (GordianAsymKeySpec mySpec : GordianAsymKeySpec.listPossibleKeySpecs(pKeyType)) {
            /* Check the keyPair */
            checkKeyPair(mySpec);

            /* Break loop if we are only testing a single Spec */
            if (!allSpecs) {
                break;
            }
        }
    }

    /**
     * Check KeyPair.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkKeyPair(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Create and record the pair */
        System.out.println(" Checking " + pKeySpec.toString() + " from " + (GordianFactoryType.BC.equals(theSource.getFactoryType())
                                                                            ? "BC"
                                                                            : "Jca"));

        /* Create new KeyPair */
        final GordianKeyPairGenerator mySrcGen = theSource.getKeyPairGenerator(pKeySpec);
        final GordianKeyPair myPair = mySrcGen.generateKeyPair();
        final X509EncodedKeySpec myPublic = mySrcGen.getX509Encoding(myPair);
        final PKCS8EncodedKeySpec myPrivate = mySrcGen.getPKCS8Encoding(myPair);
        checkKeySpec(myPublic, pKeySpec);
        checkKeySpec(myPrivate, pKeySpec);

        /* Derive identical keyPair */
        final GordianKeyPair myMirror = mySrcGen.deriveKeyPair(myPublic, myPrivate);
        if (!myPair.equals(myMirror)) {
            System.out.println("Failed to derive Mirror keyPair");
        }

        /* Derive receiving keyPair */
        final GordianKeyPairGenerator myTgtGen = theTarget.getKeyPairGenerator(pKeySpec);
        final GordianKeyPair myTarget = myTgtGen.deriveKeyPair(myPublic, myPrivate);

        /* check signatures */
        checkSignatures(myPair, myMirror, myTarget);

        /* check agreements */
        checkAgreements(myPair, myTarget);

        /* check encryptors */
        checkEncryptors(myPair, myTarget);
    }

    /**
     * Check the signatures.
     * @param pSrcPair the source pair
     * @param pMirrorPair the source pair
     * @param pTgtPair the source pair
     * @throws OceanusException on error
     */
    private void checkSignatures(final GordianKeyPair pSrcPair,
                                 final GordianKeyPair pMirrorPair,
                                 final GordianKeyPair pTgtPair) throws OceanusException {
        /* For each possible signature */
        final GordianAsymKeyType myType = pSrcPair.getKeySpec().getKeyType();
        for (GordianSignatureSpec mySign : GordianSignatureSpec.listPossibleSignatures(pSrcPair)) {
            /* If the signature is supported */
            if (theSource.validSignatureSpecForKeyPair(pSrcPair, mySign)) {
                /* Check it */
                checkSignature(pSrcPair, pMirrorPair, pTgtPair, mySign);
            }
        }
    }

    /**
     * Check Signature.
     * @param pSrcPair the source pair
     * @param pMirrorPair the mirror pair
     * @param pTgtPair the target pair
     * @param pSpec the signature spec
     * @throws OceanusException on error
     */
    private void checkSignature(final GordianKeyPair pSrcPair,
                                final GordianKeyPair pMirrorPair,
                                final GordianKeyPair pTgtPair,
                                final GordianSignatureSpec pSpec) throws OceanusException {
        /* Report test */
        System.out.println("  Signature " + pSpec.toString());

        /* Check outgoing signature */
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = theSource.createSigner(pSpec);
        mySigner.initForSigning(pMirrorPair);
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();
        mySigner.initForVerify(pSrcPair);
        mySigner.update(myMessage);
        if (!mySigner.verify(mySignature)) {
            System.out.println("Failed to verify own signature");
        }

        /* If the spec is supported in the target */
        if (theTarget.validSignatureSpecForKeyPair(pTgtPair, pSpec)) {
            mySigner = theTarget.createSigner(pSpec);
            mySigner.initForVerify(pTgtPair);
            mySigner.update(myMessage);
            if (!mySigner.verify(mySignature)) {
                System.out.println("Failed to verify sent signature");
            }

            /* Check incoming signature */
            mySigner = theTarget.createSigner(pSpec);
            mySigner.initForSigning(pTgtPair);
            mySigner.update(myMessage);
            mySignature = mySigner.sign();
            mySigner = theSource.createSigner(pSpec);
            mySigner.initForVerify(pSrcPair);
            mySigner.update(myMessage);
            if (!mySigner.verify(mySignature)) {
                System.out.println("Failed to verify returned signature");
            }
        }
    }

    /**
     * Check Agreements.
     * @param pSrcPair the source keyPair
     * @param pTgtPair the target keyPair
     * @throws OceanusException on error
     */
    private void checkAgreements(final GordianKeyPair pSrcPair,
                                 final GordianKeyPair pTgtPair) throws OceanusException {
        /* Loop through the possible agreements */
        for (final GordianAgreementSpec mySpec : GordianAgreementSpec.listPossibleAgreements(pSrcPair)) {
            /* If the agreement is valid */
            if (theSource.validAgreementSpecForKeyPair(pSrcPair, mySpec)) {
                /* Check the agreement */
                checkAgreement(pSrcPair, mySpec);

                /* If the agreement is valid for the target */
                if (theTarget.validAgreementSpecForKeyPair(pTgtPair, mySpec)) {
                    /* Check the agreement */
                    checkAgreement(pSrcPair, pTgtPair, mySpec);
                }
            }
        }
    }

    /**
     * Check Agreement.
     * @param pPair the keyPair
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    private void checkAgreement(final GordianKeyPair pPair,
                                final GordianAgreementSpec pSpec) throws OceanusException {
        /* Access the KeySpec */
        final GordianAsymKeySpec myKeySpec = pPair.getKeySpec();
        GordianKeyPair myPartner = null;

        /* Check the agreement */
        System.out.println("  Checking Agreement " + pSpec.toString());
        final GordianAgreement mySender = theSource.createAgreement(pSpec);
        final GordianAgreement myResponder = theSource.createAgreement(pSpec);

        /* Initialise the encryptors */
        if (!(mySender instanceof GordianEncapsulationAgreement)) {
            final GordianKeyPairGenerator myKeyGen = theSource.getKeyPairGenerator(myKeySpec);
            myPartner = myKeyGen.generateKeyPair();
        }

        /* Handle Encapsulation */
        if (mySender instanceof GordianEncapsulationAgreement
                 && myResponder instanceof GordianEncapsulationAgreement) {
            final byte[] myMsg = ((GordianEncapsulationAgreement) mySender).initiateAgreement(pPair);
            ((GordianEncapsulationAgreement) myResponder).acceptAgreement(pPair, myMsg);

            /* Handle Basic */
        } else if (mySender instanceof GordianBasicAgreement
                 && myResponder instanceof GordianBasicAgreement) {
            final byte[] myMsg = ((GordianBasicAgreement) mySender).initiateAgreement(myPartner, pPair);
            ((GordianBasicAgreement) myResponder).acceptAgreement(myPartner, pPair, myMsg);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianEphemeralAgreement
                 && myResponder instanceof GordianEphemeralAgreement) {
            final byte[] myMsg = ((GordianEphemeralAgreement) mySender).initiateAgreement(myPartner);
            final byte[] myResp = ((GordianEphemeralAgreement) myResponder).acceptAgreement(myPartner, pPair, myMsg);
            ((GordianEphemeralAgreement) mySender).confirmAgreement(pPair, myResp);

        } else {
            System.out.println("Invalid Agreement");
            return;
        }

        /* Check that the values match */
        final GordianKeySet myFirst = mySender.deriveKeySet();
        final GordianKeySet mySecond = myResponder.deriveKeySet();
        if (!myFirst.equals(mySecond)) {
            System.out.println("Failed to agree keySet");
        }
    }

    /**
     * Check Agreement.
     * @param pSrcPair the source keyPair
     * @param pTgtPair the target keyPair
     * @param pSpec the agreementSpec
     * @throws OceanusException on error
     */
    private void checkAgreement(final GordianKeyPair pSrcPair,
                                final GordianKeyPair pTgtPair,
                                final GordianAgreementSpec pSpec) throws OceanusException {
        /* Check the agreement */
        final GordianAgreement mySender = theSource.createAgreement(pSpec);
        final GordianAgreement myResponder = theTarget.createAgreement(pSpec);

         /* Handle Encapsulation */
        if (mySender instanceof GordianEncapsulationAgreement
                && myResponder instanceof GordianEncapsulationAgreement) {
            final byte[] myMsg = ((GordianEncapsulationAgreement) mySender).initiateAgreement(pSrcPair);
            ((GordianEncapsulationAgreement) myResponder).acceptAgreement(pTgtPair, myMsg);

            /* Handle Basic */
        } else if (mySender instanceof GordianBasicAgreement
                && myResponder instanceof GordianBasicAgreement) {
            final byte[] myMsg = ((GordianBasicAgreement) mySender).initiateAgreement(pSrcPair, pSrcPair);
            ((GordianBasicAgreement) myResponder).acceptAgreement(pTgtPair, pTgtPair, myMsg);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianEphemeralAgreement
                && myResponder instanceof GordianEphemeralAgreement) {
            final byte[] myMsg = ((GordianEphemeralAgreement) mySender).initiateAgreement(pSrcPair);
            final byte[] myResp = ((GordianEphemeralAgreement) myResponder).acceptAgreement(pTgtPair, pTgtPair, myMsg);
            ((GordianEphemeralAgreement) mySender).confirmAgreement(pSrcPair, myResp);

        } else {
            System.out.println("Invalid Agreement");
            return;
        }

        /* Check that the values match */
        final GordianKeySet myFirst = mySender.deriveIndependentKeySet();
        final GordianKeySet mySecond = myResponder.deriveIndependentKeySet();
        if (!myFirst.equals(mySecond)) {
            System.out.println("Failed to agree crossFactory keySet");
        }
    }

    /**
     * Check Encryptors.
     * @param pPair the keyPair
     * @param pTgtPair the target pair
     * @throws OceanusException on error
     */
    private void checkEncryptors(final GordianKeyPair pPair,
                                 final GordianKeyPair pTgtPair) throws OceanusException {
        /* Loop through the possible encryptors */
        for (final GordianEncryptorSpec mySpec : GordianEncryptorSpec.listPossibleEncryptors(pPair)) {
            /* If the encryptor is valid */
            if (theSource.validEncryptorSpecForKeyPair(pPair, mySpec)) {
                /* Check the encryptor */
                checkEncryptor(pPair, pTgtPair, mySpec);
            }
        }
    }

    /**
     * Check Encryptors.
     * @param pPair the keyPair
     * @param pTgtPair the target pair
     * @param pSpec the encryptorSpec
     * @throws OceanusException on error
     */
    private void checkEncryptor(final GordianKeyPair pPair,
                                final GordianKeyPair pTgtPair,
                                final GordianEncryptorSpec pSpec) throws OceanusException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];
        RANDOM.nextBytes(mySrc);

        /* Check the agreement */
        System.out.println("  Checking Encryptor " + pSpec.toString());
        final GordianEncryptor mySender = theSource.createEncryptor(pSpec);
        final GordianEncryptor myReceiver = theSource.createEncryptor(pSpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(pPair);
        myReceiver.initForDecrypt(pPair);

        /* Perform the encryption and decryption */
        final byte[] myEncrypted = mySender.encrypt(mySrc);
        final byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        if (!Arrays.equals(mySrc, myResult)) {
            System.out.println("Failed self Encryption");
        }

        /* If the spec is supported in the target */
        if (theTarget.validEncryptorSpecForKeyPair(pTgtPair, pSpec)) {
            /* Create a target encryptor and decrypt message */
            final GordianEncryptor myTarget = theTarget.createEncryptor(pSpec);
            myTarget.initForDecrypt(pTgtPair);
            final byte[] myResult1 = myTarget.decrypt(myEncrypted);

            /* Check that the values match */
            if (!Arrays.equals(mySrc, myResult1)) {
                System.out.println("Failed sent Encryption");
            }

            /* Create a new target encryption and decrypt at receiver */
            myTarget.initForEncrypt(pTgtPair);
            final byte[] myEncrypted2 = myTarget.encrypt(mySrc);
            final byte[] myResult2 = myReceiver.decrypt(myEncrypted2);

            /* Check that the values match */
            if (!Arrays.equals(mySrc, myResult2)) {
                System.out.println("Failed received Encryption");
            }
        }
    }

    /**
     * Check KeySpec for PrivateKey.
     * @param pEncoded key
     * @param pSpec the keySpec
     * @throws OceanusException on error
     */
    private static void checkKeySpec(final PKCS8EncodedKeySpec pEncoded,
                                     final GordianAsymKeySpec pSpec) throws OceanusException {
        final GordianAsymKeySpec mySpec = ALGID.determineKeySpec(pEncoded);
        if (!pSpec.equals(mySpec)) {
            System.out.println("PKCS8 mismatch: " + mySpec);
        }
    }

    /**
     * Check KeySpec for PublicKey.
     * @param pEncoded key
     * @param pSpec the keySpec
     * @throws OceanusException on error
     */
    private static void checkKeySpec(final X509EncodedKeySpec pEncoded,
                                     final GordianAsymKeySpec pSpec) throws OceanusException {
        final GordianAsymKeySpec mySpec = ALGID.determineKeySpec(pEncoded);
        if (!pSpec.equals(mySpec)) {
            System.out.println("X509 mismatch: " + mySpec);
        }
    }
}
