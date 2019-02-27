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

import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.api.impl.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.AsymmetricStore.FactoryAgreement;
import net.sourceforge.joceanus.jgordianknot.junit.regression.AsymmetricStore.FactoryEncryptor;
import net.sourceforge.joceanus.jgordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
import net.sourceforge.joceanus.jgordianknot.junit.regression.AsymmetricStore.FactoryKeySpec;
import net.sourceforge.joceanus.jgordianknot.junit.regression.AsymmetricStore.FactorySignature;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Test Asymmetric functionality.
 */
public class AsymmetricTest {
    /**
     * The factories.
     */
    private static GordianFactory BCFACTORY;
    private static GordianFactory JCAFACTORY;

    /**
     * Random source.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Test buffer size.
     */
    private static final int TESTLEN = 1024;

    /**
     * Initialise Factories.
     */
    @BeforeAll
    public static void createSecurityFactories() throws OceanusException {
        BCFACTORY = GordianGenerator.createFactory(new GordianParameters(GordianFactoryType.BC));
        JCAFACTORY = GordianGenerator.createFactory(new GordianParameters(GordianFactoryType.JCA));
    }

    /**
     * Create the asymmetric test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> asymmetricTests() throws OceanusException {
        /* Create tests */
        final Stream<DynamicNode> myBC = asymmetricTests(BCFACTORY, JCAFACTORY);
        final Stream<DynamicNode> myJCA = asymmetricTests(JCAFACTORY, BCFACTORY);
        return Stream.concat(myBC, myJCA);
    }

    /**
     * Create the asymmetric test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @return the test stream
     * @throws OceanusException on error
     */
    Stream<DynamicNode> asymmetricTests(final GordianFactory pFactory,
                                        final GordianFactory pPartner) throws OceanusException {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the possible keySpecs */
        for (final FactoryKeySpec myKeySpec : AsymmetricStore.keySpecProvider(pFactory, pPartner)) {
            /* Create an empty stream */
            Stream<DynamicNode> myKeyStream = Stream.of(DynamicTest.dynamicTest("keySpec", () -> checkKeyPair(myKeySpec)));

            /* Add signature Tests */
            AsymmetricStore.signatureProvider(myKeySpec);
            if (!myKeySpec.getSignatures().isEmpty()) {
                final GordianKeyPair myPartnerSelf = myKeySpec.getKeyPairs().getPartnerSelfKeyPair();
                Stream<DynamicNode> myTests = myKeySpec.getSignatures().stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), signatureTests(x, myPartnerSelf)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Signatures", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add agreement Tests */
            AsymmetricStore.agreementProvider(myKeySpec);
            if (!myKeySpec.getAgreements().isEmpty()) {
                final GordianKeyPair myPartnerSelf = myKeySpec.getKeyPairs().getPartnerSelfKeyPair();
                Stream<DynamicNode> myTests = myKeySpec.getAgreements().stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), agreementTests(x, myPartnerSelf)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Agreements", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add encryptor Tests */
            AsymmetricStore.encryptorProvider(myKeySpec);
            if (!myKeySpec.getEncryptors().isEmpty()) {
                final GordianKeyPair myPartnerSelf = myKeySpec.getKeyPairs().getPartnerSelfKeyPair();
                Stream<DynamicNode> myTests = myKeySpec.getEncryptors().stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), encryptorTests(x, myPartnerSelf)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Encryptors", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add the stream */
            myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myKeySpec.getKeySpec().toString(), myKeyStream)));
        }

        /* Return the stream */
        myStream = Stream.of(DynamicContainer.dynamicContainer(pFactory.getFactoryType().toString(), myStream));
        return myStream;
    }

    /**
     * Create the signature test suite for a signatureSpec.
     * @param pSignature the signature
     * @return the test stream or null
     */
    private Stream<DynamicNode> signatureTests(final FactorySignature pSignature,
                                               final GordianKeyPair pPartnerSelf) {
        /* Add self signature test */
         Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfSign", () -> checkSelfSignature(pSignature)));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pSignature.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this signature */
            final GordianSignatureFactory myTgtSigns = myTgtAsym.getSignatureFactory();
            if (myTgtSigns.validSignatureSpecForKeyPair(pPartnerSelf, pSignature.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerSign", () -> checkPartnerSignature(pSignature))));
            }
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Create the agreement test suite for an agreementSpec.
     * @param pAgreement the agreement
     * @return the test stream or null
     */
    private Stream<DynamicNode> agreementTests(final FactoryAgreement pAgreement,
                                               final GordianKeyPair pPartnerSelf) {
        /* Add self agreement test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfAgree", () -> checkSelfAgreement(pAgreement)));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pAgreement.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this agreement */
            final GordianAgreementFactory myTgtAgrees = pAgreement.getOwner().getPartner().getAgreementFactory();
            if (myTgtAgrees.validAgreementSpecForKeyPair(pPartnerSelf, pAgreement.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerAgree", () -> checkPartnerAgreement(pAgreement))));
            }
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Create the encryptor test suite for an encryptgorSpec.
     * @param pEncryptor the encryptor
     * @return the test stream or null
     */
    private Stream<DynamicNode> encryptorTests(final FactoryEncryptor pEncryptor,
                                               final GordianKeyPair pPartnerSelf) {
        /* Add self encrypt test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfEncrypt", () -> checkSelfEncryptor(pEncryptor)));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pEncryptor.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this encryptore */
            final GordianEncryptorFactory myTgtEncrypts = pEncryptor.getOwner().getPartner().getEncryptorFactory();
            if (myTgtEncrypts.validEncryptorSpecForKeyPair(pPartnerSelf, pEncryptor.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerEncrypt", () -> checkPartnerEncryptor(pEncryptor))));
            }
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Check KeyPair.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkKeyPair(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the keyPairs */
        final FactoryKeyPairs myPairs = pKeySpec.getKeyPairs();
        final GordianAsymFactory myFactory = pKeySpec.getFactory();
        final GordianAsymKeySpec mySpec = pKeySpec.getKeySpec();

        /* Check X509Encodings */
        final X509EncodedKeySpec myPublic = myPairs.getX509Encoding();
        Assertions.assertEquals(mySpec, myFactory.determineKeySpec(myPublic), "X509 has wrong keySpec");

        /* Check PKCS8Encodings */
        final PKCS8EncodedKeySpec myPrivate = myPairs.getPKCS8Encoding();
        Assertions.assertEquals(mySpec, myFactory.determineKeySpec(myPrivate), "PKCS8 has wrong keySpec");

        /* Skip test if necessary to bypass DH JCA bug */
        if (!mySpec.getKeyType().differentDerivedKey()) {
            /* Derive identical keyPair */
            final GordianKeyPair myPair = myPairs.getKeyPair();
            final GordianKeyPair myMirror = myPairs.getMirrorKeyPair();
            Assertions.assertEquals(myPair, myMirror, "Derived pair has wrong keySpec");
        }
    }

    /**
     * Check Self Signature.
     * @param pSignature the signature
     * @throws OceanusException on error
     */
    private void checkSelfSignature(final FactorySignature pSignature) throws OceanusException {
        /* Access the KeySpec */
        final GordianSignatureSpec mySpec = pSignature.getSpec();
        final FactoryKeyPairs myPairs = pSignature.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myMirror = myPairs.getMirrorKeyPair();

        /* Check outgoing signature */
        final GordianSignatureFactory mySigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = mySigns.createSigner(mySpec);
        mySigner.initForSigning(myMirror);
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();
        mySigner.initForVerify(myPair);
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature),"Failed to verify own signature");
    }

    /**
     * Check Partner Signature.
     * @param pSignature the signature
     * @throws OceanusException on error
     */
    private void checkPartnerSignature(final FactorySignature pSignature) throws OceanusException {
        /* Access the KeySpec */
        final GordianSignatureSpec mySpec = pSignature.getSpec();
        final FactoryKeyPairs myPairs = pSignature.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myPartnerSelf = myPairs.getPartnerSelfKeyPair();

        /* Check outgoing signature */
        final GordianSignatureFactory mySrcSigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final GordianSignatureFactory myTgtSigns = pSignature.getOwner().getPartner().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = mySrcSigns.createSigner(mySpec);
        mySigner.initForSigning(myPair);
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();

        /* Check sent signature */
        mySigner = myTgtSigns.createSigner(mySpec);
        mySigner.initForVerify(myPartnerSelf);
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature),"Failed to verify sent signature");

        /* Check incoming signature */
        mySigner.initForSigning(myPartnerSelf);
        mySigner.update(myMessage);
        mySignature = mySigner.sign();
        mySigner = mySrcSigns.createSigner(mySpec);
        mySigner.initForVerify(myPair);
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature),"Failed to verify returned signature");
    }

    /**
     * Test Self Agreement.
     * @param pAgreement the agreementSpec
     * @throws OceanusException on error
     */
    private void checkSelfAgreement(final FactoryAgreement pAgreement) throws OceanusException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        GordianKeyPair myTarget = null;

        /* Check the agreement */
        final GordianAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getAgreementFactory();
        final GordianAgreement mySender = myAgrees.createAgreement(mySpec);
        final GordianAgreement myResponder = myAgrees.createAgreement(mySpec);

        /* Access target if we are using one */
        if (!(mySender instanceof GordianEncapsulationAgreement)) {
            myTarget = myPairs.getTargetKeyPair();
        }

        /* Handle Encapsulation */
        if (mySender instanceof GordianEncapsulationAgreement
                && myResponder instanceof GordianEncapsulationAgreement) {
            final byte[] myMsg = ((GordianEncapsulationAgreement) mySender).initiateAgreement(myPair);
            ((GordianEncapsulationAgreement) myResponder).acceptAgreement(myPair, myMsg);

            /* Handle Basic */
        } else if (mySender instanceof GordianBasicAgreement
                && myResponder instanceof GordianBasicAgreement) {
            final byte[] myMsg = ((GordianBasicAgreement) mySender).initiateAgreement(myTarget, myPair);
            ((GordianBasicAgreement) myResponder).acceptAgreement(myTarget, myPair, myMsg);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianEphemeralAgreement
                && myResponder instanceof GordianEphemeralAgreement) {
            final byte[] myMsg = ((GordianEphemeralAgreement) mySender).initiateAgreement(myTarget);
            final byte[] myResp = ((GordianEphemeralAgreement) myResponder).acceptAgreement(myTarget, myPair, myMsg);
            ((GordianEphemeralAgreement) mySender).confirmAgreement(myPair, myResp);

        } else {
            Assertions.fail("Invalid Agreement");
        }

        /* Check that the values match */
        final GordianKeySet myFirst = mySender.deriveKeySet();
        final GordianKeySet mySecond = myResponder.deriveKeySet();
        Assertions.assertEquals(myFirst, mySecond, "Failed to agree keySet");
    }

    /**
     * Check Partner Agreement.
     * @param pAgreement the agreementSpec
     * @throws OceanusException on error
     */
    private void checkPartnerAgreement(final FactoryAgreement pAgreement) throws OceanusException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myTarget = myPairs.getTargetKeyPair();
        final GordianKeyPair myPartnerSelf = myPairs.getPartnerSelfKeyPair();
        final GordianKeyPair myPartnerTarget = myPairs.getPartnerTargetKeyPair();

        /* Check the agreement */
        final GordianAgreementFactory mySrcAgrees = pAgreement.getOwner().getFactory().getAgreementFactory();
        final GordianAgreementFactory myPartnerAgrees = pAgreement.getOwner().getPartner().getAgreementFactory();
        final GordianAgreement mySender = mySrcAgrees.createAgreement(mySpec);
        final GordianAgreement myResponder = myPartnerAgrees.createAgreement(mySpec);

        /* Handle Encapsulation */
        if (mySender instanceof GordianEncapsulationAgreement
                && myResponder instanceof GordianEncapsulationAgreement) {
            final byte[] myMsg = ((GordianEncapsulationAgreement) mySender).initiateAgreement(myTarget);
            ((GordianEncapsulationAgreement) myResponder).acceptAgreement(myPartnerTarget, myMsg);

            /* Handle Basic */
        } else if (mySender instanceof GordianBasicAgreement
                && myResponder instanceof GordianBasicAgreement) {
            final byte[] myMsg = ((GordianBasicAgreement) mySender).initiateAgreement(myPair, myTarget);
            ((GordianBasicAgreement) myResponder).acceptAgreement(myPartnerSelf, myPartnerTarget, myMsg);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianEphemeralAgreement
                && myResponder instanceof GordianEphemeralAgreement) {
            final byte[] myMsg = ((GordianEphemeralAgreement) mySender).initiateAgreement(myPair);
            final byte[] myResp = ((GordianEphemeralAgreement) myResponder).acceptAgreement(myPartnerSelf, myPartnerTarget, myMsg);
            ((GordianEphemeralAgreement) mySender).confirmAgreement(myTarget, myResp);

        } else {
            Assertions.fail("Invalid Agreement");
        }

        /* Check that the values match */
        final GordianKeySet myFirst = mySender.deriveIndependentKeySet();
        final GordianKeySet mySecond = myResponder.deriveIndependentKeySet();
        Assertions.assertEquals(myFirst, mySecond, "Failed to agree crossFactory keySet");
    }

    /**
     * Check Self Encryption.
     * @param pEncryptor the encryptor
     * @throws OceanusException on error
     */
    private void checkSelfEncryptor(final FactoryEncryptor pEncryptor) throws OceanusException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];
        RANDOM.nextBytes(mySrc);

        /* Access the KeySpec */
        final GordianEncryptorSpec mySpec = pEncryptor.getSpec();
        final FactoryKeyPairs myPairs = pEncryptor.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();

        /* Check the encryptor */
        final GordianEncryptorFactory myEncrypts = pEncryptor.getOwner().getFactory().getEncryptorFactory();
        final GordianEncryptor mySender = myEncrypts.createEncryptor(mySpec);
        final GordianEncryptor myReceiver = myEncrypts.createEncryptor(mySpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(myPair);
        myReceiver.initForDecrypt(myPair);

        /* Perform the encryption and decryption */
        final byte[] myEncrypted = mySender.encrypt(mySrc);
        final byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption");
    }

    /**
     * Check Partner Encryption.
     * @param pEncryptor the encryptor
     * @throws OceanusException on error
     */
    private void checkPartnerEncryptor(final FactoryEncryptor pEncryptor) throws OceanusException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];
        RANDOM.nextBytes(mySrc);

        /* Access the KeySpec */
        final GordianEncryptorSpec mySpec = pEncryptor.getSpec();
        final FactoryKeyPairs myPairs = pEncryptor.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myPartnerSelf = myPairs.getPartnerSelfKeyPair();

        /* Check the encryptor */
        final GordianEncryptorFactory mySrcEncrypts = pEncryptor.getOwner().getFactory().getEncryptorFactory();
        final GordianEncryptorFactory myTgtEncrypts = pEncryptor.getOwner().getPartner().getEncryptorFactory();
        final GordianEncryptor mySender = mySrcEncrypts.createEncryptor(mySpec);
        final GordianEncryptor myReceiver = myTgtEncrypts.createEncryptor(mySpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(myPair);
        myReceiver.initForDecrypt(myPartnerSelf);

        /* Perform the encryption and decryption */
        final byte[] myEncrypted = mySender.encrypt(mySrc);
        final byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed sent encryption");

        /* Create a new target encryption and decrypt at receiver */
        myReceiver.initForEncrypt(myPartnerSelf);
        mySender.initForDecrypt(myPair);

        /* Perform the encryption and decryption */
        final byte[] myEncrypted2 = myReceiver.encrypt(mySrc);
        final byte[] myResult2 = mySender.decrypt(myEncrypted2);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult2, "Failed received encryption");
    }
}
