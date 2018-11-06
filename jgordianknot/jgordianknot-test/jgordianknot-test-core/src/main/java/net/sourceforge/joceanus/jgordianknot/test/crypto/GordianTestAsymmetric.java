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

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymAlgId;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMcElieceKeySpec.GordianMcElieceDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianModulus;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianQTESLAKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianXMSSKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * AsymKeys Test Control.
 */
public class GordianTestAsymmetric {
    /**
     * AsyncKey table.
     */
    private final Map<GordianAsymKeyType, AsymPairControl> theMap;

    /**
     * AsyncKey table.
     */
    private static final GordianAsymAlgId ALGID = new GordianAsymAlgId();

    /**
     * Signature Source.
     */
    private final byte[][] theSignatureSource;

    /**
     * Constructor.
     * @param pSignatures the signature sources
     */
    GordianTestAsymmetric(final byte[]... pSignatures) {
        /* Create the map */
        theMap = new EnumMap<>(GordianAsymKeyType.class);

        /* Create the signature sources */
        theSignatureSource = pSignatures;
    }

     /**
     * Create keyPairs.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    void createKeyPairs(final GordianFactory pFactory,
                        final GordianKeySet pKeySet) throws OceanusException {
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.rsa(GordianModulus.MOD2048));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ec(GordianDSAElliptic.SECT571K1));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dsa(GordianDSAKeyType.MOD2048_2));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dh(GordianModulus.MOD4096));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sm2(GordianSM2Elliptic.SM2P256V1));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sphincs(GordianSPHINCSKeyType.SHA3));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.rainbow());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.newHope());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dstu4145(GordianDSTU4145Elliptic.DSTU9));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.gost2012(GordianGOSTElliptic.GOST512A));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.xmss(GordianXMSSKeyType.SHA256));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.xmssmt(GordianXMSSKeyType.SHA256));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.qTESLA(GordianQTESLAKeyType.HEURISTIC_I));
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ed25519());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ed448());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.x25519());
        createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.x448());
    }

    /**
     * Create keyPairs.
     * @param pSource the source factory
     * @param pTarget the target factory
     * @throws OceanusException on error
     */
    static void checkKeyPair(final GordianFactory pSource,
                             final GordianFactory pTarget) throws OceanusException {
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.rsa(GordianModulus.MOD2048));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.ec(GordianDSAElliptic.SECT571K1));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.dsa(GordianDSAKeyType.MOD2048_2));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.dh(GordianModulus.MOD4096));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.sm2(GordianSM2Elliptic.SM2P256V1));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.sphincs(GordianSPHINCSKeyType.SHA2));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.rainbow());
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.newHope());
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.dstu4145(GordianDSTU4145Elliptic.DSTU9));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.gost2012(GordianGOSTElliptic.GOST512A));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.xmss(GordianXMSSKeyType.SHA256));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.xmssmt(GordianXMSSKeyType.SHA256));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.mcEliece(GordianMcElieceKeySpec.standard()));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.mcEliece(GordianMcElieceKeySpec.cca2(GordianMcElieceDigestType.SHA256)));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.qTESLA(GordianQTESLAKeyType.HEURISTIC_I));
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.ed25519());
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.ed448());
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.x25519());
        checkKeyPair(pSource, pTarget, GordianAsymKeySpec.x448());
    }

    /**
     * Test keyPairs.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    void testKeyPairs(final GordianFactory pFactory,
                      final GordianKeySet pKeySet) throws OceanusException {
        for (GordianDSAElliptic myCurve : GordianDSAElliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.ec(myCurve));
        }
        for (GordianSM2Elliptic myCurve : GordianSM2Elliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.sm2(myCurve));
        }
        for (GordianDSTU4145Elliptic myCurve : GordianDSTU4145Elliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.dstu4145(myCurve));
        }
        for (GordianGOSTElliptic myCurve : GordianGOSTElliptic.values()) {
            createKeyPair(pFactory, pKeySet, GordianAsymKeySpec.gost2012(myCurve));
        }
        theMap.clear();
    }

    /**
     * Create keyPair.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void createKeyPair(final GordianFactory pFactory,
                               final GordianKeySet pKeySet,
                               final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Create and record the pair */
        System.out.println(" Creating " + pKeySpec.toString());
        final AsymPairControl myPair = new AsymPairControl(pFactory, pKeySpec);
        theMap.put(pKeySpec.getKeyType(), myPair);

        /* Create signatures */
        myPair.createSignatures(theSignatureSource);

        /* Check agreements */
        myPair.checkAgreements();

        /* Secure the privateKey */
        myPair.securePrivateKey(pKeySet);
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
        /* Access the list of possible digests */
        final List<GordianDigestSpec> myDigests = GordianDigestSpec.listAll();

        /* For each possible signature */
        final GordianAsymKeyType myType = pSrcPair.getKeySpec().getKeyType();
        for (GordianSignatureType mySignType : myType.getSupportedSignatures()) {
            /* If we need null-digestSpec */
            if (myType.nullDigestForSignatures()) {
                /* If the signature is supported */
                final GordianSignatureSpec mySign = new GordianSignatureSpec(myType, mySignType);
                if (pSource.validSignatureSpecForKeyPair(pSrcPair, mySign)) {
                    checkSignature(pSource, pTarget, pSrcPair, pMirrorPair, pTgtPair, mySign);
                }
                continue;
            }

            /* For each possible digestSpec */
            for (GordianDigestSpec mySpec : myDigests) {
                /* Create the corresponding signatureSpec */
                final GordianSignatureSpec mySign = new GordianSignatureSpec(myType, mySignType, mySpec);

                /* If the signature is supported */
                if (pSource.validSignatureSpecForKeyPair(pSrcPair, mySign)) {
                    checkSignature(pSource, pTarget, pSrcPair, pMirrorPair, pTgtPair, mySign);
                }
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
        /* Access the Exchange predicate */
        final Predicate<GordianAgreementSpec> myPredicate = pFactory.supportedAgreements();
        final GordianAsymKeySpec myKeySpec = pTarget.getKeySpec();
        GordianKeyPair mySource = null;

        /* Loop through the agreement types */
        for (final GordianAgreementType myType : GordianAgreementType.values()) {
            /* If the agreement is valid */
            final GordianAgreementSpec mySpec = new GordianAgreementSpec(myKeySpec.getKeyType(), myType);
            if (myPredicate.test(mySpec)) {
                /* Check the agreement */
                System.out.println("  Checking Agreement " + mySpec.toString());
                final GordianAgreement mySender = pFactory.createAgreement(mySpec);
                final GordianAgreement myResponder = pFactory.createAgreement(mySpec);

                /* Create source pair if needed */
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
     * Check KeySpec for PrivateKey.
     * @param pEncoded key
     * @param pSpec the keySpec
     * @throws OceanusException on error
     */
    private static void checkKeySpec(final PKCS8EncodedKeySpec pEncoded,
                                     final GordianAsymKeySpec pSpec) throws OceanusException {
        final GordianAsymKeySpec mySpec = ALGID.determineKeySpec(pEncoded);
        if (!pSpec.equals(mySpec)) {
            System.out.println("Help");
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
            System.out.println("Help");
        }
    }

    /**
     * Create keyPairs.
     * @param pFactory the factory
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    protected void validateKeyPairs(final GordianFactory pFactory,
                                    final GordianKeySet pKeySet) throws OceanusException {
        /* For each control that has been created */
        final Iterator<AsymPairControl> myIterator = theMap.values().iterator();
        while (myIterator.hasNext()) {
            AsymPairControl myControl = myIterator.next();
            System.out.println(" Restoring " + myControl.getKeySpec().toString());
            myControl = new AsymPairControl(pFactory, pKeySet, myControl);
            myControl.validateSignatures(theSignatureSource);
        }
    }

    /**
     * AsymmetricPair Control.
     */
    private static final class AsymPairControl {
        /**
         * The Factory.
         */
        private final GordianFactory theFactory;

        /**
         * The KeyPairGenerator.
         */
        private final GordianKeyPairGenerator theGenerator;

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
        private byte[] thePrivate;

        /**
         * Signature Map.
         */
        private final Map<GordianSignatureSpec, byte[]> theSignatures;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySpec the Asymmetric KeyType.
         * @throws OceanusException on error
         */
        private AsymPairControl(final GordianFactory pFactory,
                                final GordianAsymKeySpec pKeySpec) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            theGenerator = pFactory.getKeyPairGenerator(pKeySpec);
            thePair = theGenerator.generateKeyPair();

            /* Secure the public key */
            thePublic = theGenerator.getX509Encoding(thePair);

            /* Create the signature map */
            theSignatures = new LinkedHashMap<>();
        }

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySet the keySet
         * @param pStatus the Base Status.
         * @throws OceanusException on error
         */
        private AsymPairControl(final GordianFactory pFactory,
                                final GordianKeySet pKeySet,
                                final AsymPairControl pStatus) throws OceanusException {
            /* Store the factory */
            theFactory = pFactory;

            /* Create new KeyPair */
            theGenerator = pFactory.getKeyPairGenerator(pStatus.getKeySpec());
            thePair = theGenerator.deriveKeyPair(pStatus.thePublic, pStatus.thePrivate, pKeySet);

            /* Don't worry about the keySpecs */
            thePrivate = null;
            thePublic = null;

            /* Record the signature map */
            theSignatures = pStatus.theSignatures;

            /* Check that the pairs are identical */
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
         * Secure the privateKey.
         * @param pKeySet the keySet
         * @throws OceanusException on error
         */
        private void securePrivateKey(final GordianKeySet pKeySet) throws OceanusException {
            thePrivate = theGenerator.securePrivateKey(thePair, pKeySet);
        }

        /**
         * Create the signatures.
         * @param pSources the sources to sign
         * @throws OceanusException on error
         */
        private void createSignatures(final byte[][] pSources) throws OceanusException {
            /* Access the list of possible digests */
            final List<GordianDigestSpec> myDigests = GordianDigestSpec.listAll();

            /* For each possible signature */
            final GordianAsymKeyType myType = getKeySpec().getKeyType();
            for (GordianSignatureType mySignType : myType.getSupportedSignatures()) {
                /* If we need null-digestSpec */
                if (myType.nullDigestForSignatures()) {
                    /* If the signature is supported */
                    final GordianSignatureSpec mySign = new GordianSignatureSpec(myType, mySignType);
                    if (theFactory.validSignatureSpecForKeyPair(thePair, mySign)) {
                        createSignature(mySign, pSources);
                    }
                    continue;
                }

                /* For each possible digestSpec */
                for (GordianDigestSpec mySpec : myDigests) {
                    /* Create the corresponding signatureSpec */
                    final GordianSignatureSpec mySign = new GordianSignatureSpec(myType, mySignType, mySpec);

                    /* If the signature is supported */
                    if (theFactory.validSignatureSpecForKeyPair(thePair, mySign)) {
                        createSignature(mySign, pSources);
                    }
                }
            }
        }

        /**
         * Create the signature.
         * @param pSignatureSpec the signatureSpec
         * @param pSources the sources to sign
         * @throws OceanusException on error
         */
        private void createSignature(final GordianSignatureSpec pSignatureSpec,
                                     final byte[][] pSources) throws OceanusException {
            System.out.println("  Signing " + pSignatureSpec.toString());
            final GordianSignature mySigner = theFactory.createSigner(pSignatureSpec);
            mySigner.initForSigning(thePair);
            for (byte[] mySource : pSources) {
                mySigner.update(mySource);
            }
            theSignatures.put(pSignatureSpec, mySigner.sign());
        }

        /**
         * Validate the signatures.
         * @param pSources the sources that were signed
         * @throws OceanusException on error
         */
        private void validateSignatures(final byte[][] pSources) throws OceanusException {
            /* For each signature that has been created */
            final Iterator<GordianSignatureSpec> myIterator = theSignatures.keySet().iterator();
            while (myIterator.hasNext()) {
                final GordianSignatureSpec mySpec = myIterator.next();
                validateSignature(mySpec, pSources);
            }
        }

        /**
         * Validate the signature.
         * @param pSignatureSpec the signatureSpec
         * @param pSources the sources that were signed
         * @throws OceanusException on error
         */
        private void validateSignature(final GordianSignatureSpec pSignatureSpec,
                                       final byte[][] pSources) throws OceanusException {
            System.out.println("  Validating " + pSignatureSpec.toString());
            final GordianSignature myValidator = theFactory.createSigner(pSignatureSpec);
            myValidator.initForVerify(thePair);
            for (byte[] mySource : pSources) {
                myValidator.update(mySource);
            }
            if (!myValidator.verify(theSignatures.get(pSignatureSpec))) {
                System.out.println("Failed to validate signature: " + pSignatureSpec);
            }
        }

        /**
         * Check Agreements.
         * @throws OceanusException on error
         */
        private void checkAgreements() throws OceanusException {
            /* Access the Exchange predicate */
            final Predicate<GordianAgreementSpec> myPredicate = theFactory.supportedAgreements();

            /* Loop through the agreement types */
            for (final GordianAgreementType myType : GordianAgreementType.values()) {
                /* If the agreement is valid */
                final GordianAgreementSpec mySpec = new GordianAgreementSpec(getKeySpec().getKeyType(), myType);
                if (myPredicate.test(mySpec)) {
                    /* Check the agreement */
                    System.out.println("  Checking Agreement " + mySpec.toString());
                    final GordianAgreement mySender = theFactory.createAgreement(mySpec);
                    final GordianAgreement myResponder = theFactory.createAgreement(mySpec);

                    /* Handle Encapsulation */
                    if (mySender instanceof GordianEncapsulationAgreement
                            && myResponder instanceof GordianEncapsulationAgreement) {
                        final byte[] myMsg = ((GordianEncapsulationAgreement) mySender).initiateAgreement(thePair);
                        ((GordianEncapsulationAgreement) myResponder).acceptAgreement(thePair, myMsg);

                    } else if (mySender instanceof GordianBasicAgreement
                                  && myResponder instanceof GordianBasicAgreement) {
                        final byte[] myMsg = ((GordianBasicAgreement) mySender).initiateAgreement(thePair, thePair);
                        ((GordianBasicAgreement) myResponder).acceptAgreement(thePair, thePair, myMsg);

                    } else if (mySender instanceof GordianEphemeralAgreement
                                  && myResponder instanceof GordianEphemeralAgreement) {
                        final byte[] myMsg = ((GordianEphemeralAgreement) mySender).initiateAgreement(thePair);
                        final byte[] myResp = ((GordianEphemeralAgreement) myResponder).acceptAgreement(thePair, thePair, myMsg);
                        ((GordianEphemeralAgreement) mySender).confirmAgreement(thePair, myResp);

                    } else {
                        System.out.println("Invalid");
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
    }
}
