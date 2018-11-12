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
     * Private Constructor.
     */
    private GordianTestAsymmetric() {
    }

    /**
     * Create keyPairs.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @throws OceanusException on error
     */
    static void checkKeyPair(final GordianFactory pSource,
                             final GordianFactory pTarget) throws OceanusException {
        /**
         * AsyncSig table.
         */
        final GordianSignatureAlgId sigID = new GordianSignatureAlgId(pSource);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.RSA);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.EC);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.DSA);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.DIFFIEHELLMAN);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.SM2);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.GOST2012);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.DSTU4145);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.SPHINCS);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.RAINBOW);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.NEWHOPE);
        checkKeyPair(pSource, pTarget, GordianAsymKeyType.MCELIECE);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.XMSS);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.XMSSMT);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.QTESLA);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.ED25519);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.ED448);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.X25519);
        //checkKeyPair(pSource, pTarget, GordianAsymKeyType.X448);
    }

    /**
     * Create keyPairs.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @throws OceanusException on error
     */
    static void checkKeyPair(final GordianFactory pSource,
                             final GordianFactory pTarget,
                             final GordianAsymKeyType pKeyType) throws OceanusException {

        /* For each possible keySpec */
        for (GordianAsymKeySpec mySpec : GordianAsymKeySpec.listPossibleKeySpecs(pKeyType)) {
            /* Check the keyPair */
            checkKeyPair(pSource, pTarget, mySpec);
        }
    }

    /**
     * Check KeyPair.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private static void checkKeyPair(final GordianFactory pSource,
                                     final GordianFactory pTarget,
                                     final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Create and record the pair */
        System.out.println(" Checking " + pKeySpec.toString() + " from " + (GordianFactoryType.BC.equals(pSource.getFactoryType())
                                                                            ? "BC"
                                                                            : "Jca"));

        /* Create new KeyPair */
        final GordianKeyPairGenerator mySrcGen = pSource.getKeyPairGenerator(pKeySpec);
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
        final GordianKeyPairGenerator myTgtGen = pTarget.getKeyPairGenerator(pKeySpec);
        final GordianKeyPair myResult = myTgtGen.deriveKeyPair(myPublic, myPrivate);

        /* check signatures */
        checkSignatures(pSource, pTarget, myPair, myMirror, myResult);

        /* check agreements */
        checkAgreements(pSource, myPair);

        /* check encryptors */
        checkEncryptors(pSource, myPair);
    }

    /**
     * Check the signatures.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @param pSrcPair the source pair
     * @param pMirrorPair the source pair
     * @param pTgtPair the source pair
     * @throws OceanusException on error
     */
    private static void checkSignatures(final GordianFactory pSource,
                                        final GordianFactory pTarget,
                                        final GordianKeyPair pSrcPair,
                                        final GordianKeyPair pMirrorPair,
                                        final GordianKeyPair pTgtPair) throws OceanusException {
        /* For each possible signature */
        final GordianAsymKeyType myType = pSrcPair.getKeySpec().getKeyType();
        for (GordianSignatureSpec mySign : GordianSignatureSpec.listPossibleSignatures(pSrcPair)) {
            /* If the signature is supported */
            if (pSource.validSignatureSpecForKeyPair(pSrcPair, mySign)) {
                /* Check it */
                checkSignature(pSource, pTarget, pSrcPair, pMirrorPair, pTgtPair, mySign);
            }
        }
    }

    /**
     * Check Signature.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @param pSrcPair the source pair
     * @param pMirrorPair the source pair
     * @param pTgtPair the source pair
     * @param pSpec the signature spec
     * @throws OceanusException on error
     */
    private static void checkSignature(final GordianFactory pSource,
                                       final GordianFactory pTarget,
                                       final GordianKeyPair pSrcPair,
                                       final GordianKeyPair pMirrorPair,
                                       final GordianKeyPair pTgtPair,
                                       final GordianSignatureSpec pSpec) throws OceanusException {
        /* Report test */
        System.out.println("  Signature " + pSpec.toString());

        /* Check outgoing signature */
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = pSource.createSigner(pSpec);
        mySigner.initForSigning(pMirrorPair);
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();
        mySigner.initForVerify(pSrcPair);
        mySigner.update(myMessage);
        if (!mySigner.verify(mySignature)) {
            System.out.println("Failed to verify own signature");
        }

        /* If the spec is supported in the target */
        if (pTarget.validSignatureSpecForKeyPair(pTgtPair, pSpec)) {
            mySigner = pTarget.createSigner(pSpec);
            mySigner.initForVerify(pTgtPair);
            mySigner.update(myMessage);
            if (!mySigner.verify(mySignature)) {
                System.out.println("Failed to verify sent signature");
            }

            /* Check incoming signature */
            mySigner = pTarget.createSigner(pSpec);
            mySigner.initForSigning(pTgtPair);
            mySigner.update(myMessage);
            mySignature = mySigner.sign();
            mySigner = pSource.createSigner(pSpec);
            mySigner.initForVerify(pSrcPair);
            mySigner.update(myMessage);
            if (!mySigner.verify(mySignature)) {
                System.out.println("Failed to verify returned signature");
            }
        }
    }

    /**
     * Check Agreements.
     * @param pFactory the factory
     * @param pTarget the target pair
      * @throws OceanusException on error
     */
    private static void checkAgreements(final GordianFactory pFactory,
                                        final GordianKeyPair pTarget) throws OceanusException {
        /* Access the KeySpec */
        final GordianAsymKeySpec myKeySpec = pTarget.getKeySpec();
        GordianKeyPair mySource = null;

        /* Loop through the possible agreements */
        for (final GordianAgreementSpec mySpec : GordianAgreementSpec.listPossibleAgreements(pTarget)) {
            /* If the agreement is valid */
            if (pFactory.validAgreementSpecForKeyPair(pTarget, mySpec)) {
                /* Check the agreement */
                System.out.println("  Checking Agreement " + mySpec.toString());
                final GordianAgreement mySender = pFactory.createAgreement(mySpec);
                final GordianAgreement myResponder = pFactory.createAgreement(mySpec);

                /* Initialise the encryptors */

                if (mySource == null
                        && !(mySender instanceof GordianEncapsulationAgreement)) {
                    final GordianKeyPairGenerator myKeyGen = pFactory.getKeyPairGenerator(myKeySpec);
                    mySource = myKeyGen.generateKeyPair();
                }

                /* Handle Encapsulation */
                if (mySender instanceof GordianEncapsulationAgreement
                        && myResponder instanceof GordianEncapsulationAgreement) {
                    final byte[] myMsg = ((GordianEncapsulationAgreement) mySender).initiateAgreement(pTarget);
                    ((GordianEncapsulationAgreement) myResponder).acceptAgreement(pTarget, myMsg);

                } else if (mySender instanceof GordianBasicAgreement
                        && myResponder instanceof GordianBasicAgreement) {
                    final byte[] myMsg = ((GordianBasicAgreement) mySender).initiateAgreement(mySource, pTarget);
                    ((GordianBasicAgreement) myResponder).acceptAgreement(mySource, pTarget, myMsg);

                } else if (mySender instanceof GordianEphemeralAgreement
                        && myResponder instanceof GordianEphemeralAgreement) {
                    final byte[] myMsg = ((GordianEphemeralAgreement) mySender).initiateAgreement(mySource);
                    final byte[] myResp = ((GordianEphemeralAgreement) myResponder).acceptAgreement(mySource, pTarget, myMsg);
                    ((GordianEphemeralAgreement) mySender).confirmAgreement(pTarget, myResp);

                } else {
                    System.out.println("Invalid Agreement");
                    continue;
                }

                /* Check that the values match */
                final GordianKeySet myFirst = mySender.deriveKeySet();
                final GordianKeySet mySecond = myResponder.deriveKeySet();
                if (!myFirst.equals(mySecond)) {
                    System.out.println("Exchange failed");
                }
            }
        }
    }

    /**
     * Check Encryptors.
     * @param pFactory the factory
     * @param pTarget the target pair
     * @throws OceanusException on error
     */
    private static void checkEncryptors(final GordianFactory pFactory,
                                        final GordianKeyPair pTarget) throws OceanusException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];
        RANDOM.nextBytes(mySrc);

        /* Loop through the possible encryptors */
        /* If the encryptor is valid */
        for (final GordianEncryptorSpec mySpec : GordianEncryptorSpec.listPossibleEncryptors(pTarget)) {
            if (pFactory.validEncryptorSpecForKeyPair(pTarget, mySpec)) {
                /* Check the agreement */
                System.out.println("  Checking Encryptor " + mySpec.toString());
                final GordianEncryptor mySender = pFactory.createEncryptor(mySpec);
                final GordianEncryptor myReceiver = pFactory.createEncryptor(mySpec);

                /* Handle Initialisation */
                mySender.initForEncrypt(pTarget);
                myReceiver.initForDecrypt(pTarget);

                /* Perform the encryption and decryption */
                final byte[] myEncrypted = mySender.encrypt(mySrc);
                final byte[] myResult = myReceiver.decrypt(myEncrypted);

                /* Check that the values match */
                if (!Arrays.equals(mySrc, myResult)) {
                    System.out.println("Encryption failed");
                }
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
