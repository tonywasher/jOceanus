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
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
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
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianHandshakeAgreement;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
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
     * KeySetSpec.
     */
    private static final GordianKeySetHashSpec KEYSETHASHSPEC = new GordianKeySetHashSpec();

    /**
     * SymCipherSpec.
     */
    private static final GordianSymCipherSpec SYMKEYSPEC = GordianSymCipherSpec.sic(GordianSymKeySpec.aes(GordianLength.LEN_256));

    /**
     * StreamCipherSpec.
     */
    private static final GordianStreamCipherSpec STREAMKEYSPEC = GordianStreamCipherSpec.stream(GordianStreamKeySpec.chacha(GordianLength.LEN_256));

    /**
     * Initialise Factories.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void createSecurityFactories() throws OceanusException {
        BCFACTORY = GordianGenerator.createFactory(GordianFactoryType.BC);
        JCAFACTORY = GordianGenerator.createFactory(GordianFactoryType.JCA);
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
            /* Create a stream */
            Stream<DynamicNode> myKeyStream = Stream.of(DynamicTest.dynamicTest("generate", () -> generateKeyPairs(myKeySpec)));
            myKeyStream = Stream.concat(myKeyStream, Stream.of(DynamicTest.dynamicTest("keySpec", () -> checkKeyPair(myKeySpec))));
            myKeyStream = Stream.concat(myKeyStream, Stream.of(DynamicTest.dynamicTest("keyWrap", () -> checkKeyWrap(myKeySpec))));

            /* Add signature Tests */
            AsymmetricStore.signatureProvider(myKeySpec);
            if (!myKeySpec.getSignatures().isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.getSignatures().stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), signatureTests(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Signatures", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add agreement Tests */
            AsymmetricStore.agreementProvider(myKeySpec);
            if (!myKeySpec.getAgreements().isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.getAgreements().stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), agreementTests(x)));
                myTests = Stream.of(DynamicContainer.dynamicContainer("Agreements", myTests));
                myKeyStream = Stream.concat(myKeyStream, myTests);
            }

            /* Add encryptor Tests */
            AsymmetricStore.encryptorProvider(myKeySpec);
            if (!myKeySpec.getEncryptors().isEmpty()) {
                Stream<DynamicNode> myTests = myKeySpec.getEncryptors().stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), encryptorTests(x)));
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
    private Stream<DynamicNode> signatureTests(final FactorySignature pSignature) {
        /* Add self signature test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfSign", () -> checkSelfSignature(pSignature)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkSignatureAlgId(pSignature))));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pSignature.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this signature */
            final GordianSignatureFactory myTgtSigns = myTgtAsym.getSignatureFactory();
            if (myTgtSigns.validSignatureSpecForKeySpec(pSignature.getOwner().getKeySpec(), pSignature.getSpec())) {
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
    private Stream<DynamicNode> agreementTests(final FactoryAgreement pAgreement) {
        /* Add self agreement test */
        Stream<DynamicNode> myTests = Stream.of(DynamicContainer.dynamicContainer("SelfAgree", Stream.of(
                DynamicTest.dynamicTest("factory", () -> checkSelfAgreement(pAgreement, GordianFactoryType.BC)),
                DynamicTest.dynamicTest("keySet", () -> checkSelfAgreement(pAgreement, KEYSETHASHSPEC.getKeySetSpec())),
                DynamicTest.dynamicTest("symCipher", () -> checkSelfAgreement(pAgreement, SYMKEYSPEC)),
                DynamicTest.dynamicTest("streamCipher", () -> checkSelfAgreement(pAgreement, STREAMKEYSPEC)),
                DynamicTest.dynamicTest("basic", () -> checkSelfAgreement(pAgreement, null))
         )));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkAgreementAlgId(pAgreement))));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pAgreement.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this agreement */
            final GordianAgreementFactory myTgtAgrees = pAgreement.getOwner().getPartner().getAgreementFactory();
            if (myTgtAgrees.validAgreementSpecForKeySpec(pAgreement.getOwner().getKeySpec(), pAgreement.getSpec())) {
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
    private Stream<DynamicNode> encryptorTests(final FactoryEncryptor pEncryptor) {
        /* Add self encrypt test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfEncrypt", () -> checkSelfEncryptor(pEncryptor)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkEncryptorAlgId(pEncryptor))));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pEncryptor.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this encryptore */
            final GordianEncryptorFactory myTgtEncrypts = pEncryptor.getOwner().getPartner().getEncryptorFactory();
            if (myTgtEncrypts.validEncryptorSpecForKeySpec(pEncryptor.getOwner().getKeySpec(), pEncryptor.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerEncrypt", () -> checkPartnerEncryptor(pEncryptor))));
            }
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Generate KeyPairs.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void generateKeyPairs(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the keyPairs */
        final FactoryKeyPairs myPairs = pKeySpec.getKeyPairs();

        /* Force creation of the pairs */
        myPairs.getKeyPair();
        myPairs.getMirrorKeyPair();
        myPairs.getPartnerSelfKeyPair();
        myPairs.getPartnerTargetKeyPair();
        myPairs.getTargetKeyPair();
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
     * Check KeyPair.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    private void checkKeyWrap(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the keyPairs */
        final FactoryKeyPairs myPairs = pKeySpec.getKeyPairs();
        final GordianAsymFactory myFactory = pKeySpec.getFactory();
        final GordianAsymKeySpec mySpec = pKeySpec.getKeySpec();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final X509EncodedKeySpec myPublic = myPairs.getX509Encoding();

        /* Create a keySet */
        final GordianKeySetFactory myKeySetFactory = myFactory.getFactory().getKeySetFactory();
        final GordianKeySetHash myHash = myKeySetFactory.generateKeySetHash(KEYSETHASHSPEC, "Hello".toCharArray());
        final GordianCoreKeySet myKeySet = (GordianCoreKeySet) myHash.getKeySet();
        final byte[] mySecured = myKeySet.securePrivateKey(myPair);
        final GordianKeyPair myDerived = myKeySet.deriveKeyPair(myPublic, mySecured);
        if (!mySpec.getKeyType().differentDerivedKey()) {
            Assertions.assertEquals(myPair, myDerived, "Incorrect derived pair");
        }
        Assertions.assertEquals(myKeySet.getPrivateKeyWrapLength(myPair), mySecured.length, "Incorrect wrapped length");
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
     * @param pResultType the resultType
     * @throws OceanusException on error
     */
    private void checkSelfAgreement(final FactoryAgreement pAgreement,
                                    final Object pResultType) throws OceanusException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        GordianKeyPair myTarget = null;

        /* Check the agreement */
        final GordianAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getAgreementFactory();
        final GordianAgreement mySender = myAgrees.createAgreement(mySpec);
        mySender.setResultType(pResultType);
        final GordianAgreement myResponder = myAgrees.createAgreement(mySpec);

        /* Access target if we are using one */
        if (!(mySender instanceof GordianAnonymousAgreement)) {
            myTarget = myPairs.getTargetKeyPair();
        }

        /* Handle Encapsulation */
        if (mySender instanceof GordianAnonymousAgreement
                && myResponder instanceof GordianAnonymousAgreement) {
            final byte[] myClientHello = ((GordianAnonymousAgreement) mySender).createClientHello(myPair);
            ((GordianAnonymousAgreement) myResponder).acceptClientHello(myPair, myClientHello);

            /* Handle Basic */
        } else if (mySender instanceof GordianBasicAgreement
                && myResponder instanceof GordianBasicAgreement) {
            final byte[] myClientHello = ((GordianBasicAgreement) mySender).createClientHello(myTarget, myPair);
            ((GordianBasicAgreement) myResponder).acceptClientHello(myTarget, myPair, myClientHello);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianHandshakeAgreement
                && myResponder instanceof GordianHandshakeAgreement) {
            final byte[] myClientHello = ((GordianHandshakeAgreement) mySender).createClientHello(myTarget);
            final byte[] myServerHello = ((GordianHandshakeAgreement) myResponder).acceptClientHello(myTarget, myPair, myClientHello);
            ((GordianHandshakeAgreement) mySender).acceptServerHello(myPair, myServerHello);

        } else {
            Assertions.fail("Invalid Agreement");
        }

        /* Check that the values match */
        final Object myFirst = mySender.getResult();
        final Object mySecond = myResponder.getResult();
        final boolean isEqual = Objects.deepEquals(myFirst, mySecond);
        Assertions.assertTrue(isEqual, "Failed to agree result");
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
        mySender.setResultType(new GordianKeySetSpec());
        final GordianAgreement myResponder = myPartnerAgrees.createAgreement(mySpec);

        /* Handle Anonymous */
        if (mySender instanceof GordianAnonymousAgreement
                && myResponder instanceof GordianAnonymousAgreement) {
            final byte[] myClientHello = ((GordianAnonymousAgreement) mySender).createClientHello(myTarget);
            ((GordianAnonymousAgreement) myResponder).acceptClientHello(myPartnerTarget, myClientHello);

            /* Handle Basic */
        } else if (mySender instanceof GordianBasicAgreement
                && myResponder instanceof GordianBasicAgreement) {
            final byte[] myClientHello = ((GordianBasicAgreement) mySender).createClientHello(myPair, myTarget);
            ((GordianBasicAgreement) myResponder).acceptClientHello(myPartnerSelf, myPartnerTarget, myClientHello);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianHandshakeAgreement
                && myResponder instanceof GordianHandshakeAgreement) {
            final byte[] myClientHello = ((GordianHandshakeAgreement) mySender).createClientHello(myPair);
            final byte[] myServerHello = ((GordianHandshakeAgreement) myResponder).acceptClientHello(myPartnerSelf, myPartnerTarget, myClientHello);
            ((GordianHandshakeAgreement) mySender).acceptServerHello(myTarget, myServerHello);

        } else {
            Assertions.fail("Invalid Agreement");
        }

        /* Check that the values match */
        final GordianKeySet myFirst = (GordianKeySet) mySender.getResult();
        final GordianKeySet mySecond = (GordianKeySet) myResponder.getResult();
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

        /* Perform the encryption and decryption for all zeros */
        byte[] myEncrypted = mySender.encrypt(mySrc);
        byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption for all zeros");

        /* Perform the encryption and decryption for all ones */
        Arrays.fill(mySrc, (byte) 0xFF);
        myEncrypted = mySender.encrypt(mySrc);
        myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption for all ones");

        /* Perform the encryption and decryption for random data */
        RANDOM.nextBytes(mySrc);
        myEncrypted = mySender.encrypt(mySrc);
        myResult = myReceiver.decrypt(myEncrypted);

        /* Check that the values match */
        Assertions.assertArrayEquals(mySrc, myResult, "Failed self encryption for random data");
    }

    /**
     * Check Partner Encryption.
     * @param pEncryptor the encryptor
     * @throws OceanusException on error
     */
    private void checkPartnerEncryptor(final FactoryEncryptor pEncryptor) throws OceanusException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];

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

        /* Perform the encryption and decryption on random data */
        RANDOM.nextBytes(mySrc);
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

    /**
     * Check signatureAlgId.
     * @param pSignature the signature to check
     * @throws OceanusException on error
     */
    private void checkSignatureAlgId(final FactorySignature pSignature) throws OceanusException {
        /* Access the factory */
        final GordianCoreSignatureFactory myFactory = (GordianCoreSignatureFactory) pSignature.getOwner().getFactory().getSignatureFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpecAndKeyPair(pSignature.getSpec(), pSignature.getOwner().getKeyPairs().getKeyPair());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSignature.getSpec());

        /* Check unique mapping */
        final GordianSignatureSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pSignature.getSpec(), mySpec, "Invalid mapping for  " + pSignature.getSpec());
    }


    /**
     * Check agreementAlgId.
     * @param pAgreement the agreement to check
     * @throws OceanusException on error
     */
    private void checkAgreementAlgId(final FactoryAgreement pAgreement) throws OceanusException {
        /* Access the factory */
        final GordianCoreAgreementFactory myFactory = (GordianCoreAgreementFactory) pAgreement.getOwner().getFactory().getAgreementFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pAgreement.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pAgreement.getSpec());

        /* Check unique mapping */
        final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pAgreement.getSpec(), mySpec, "Invalid mapping for  " + pAgreement.getSpec());
    }

    /**
     * Check encryptorAlgId.
     * @param pEncryptor the encryptor to check
     * @throws OceanusException on error
     */
    private void checkEncryptorAlgId(final FactoryEncryptor pEncryptor) throws OceanusException {
        /* Access the factory */
        final GordianCoreEncryptorFactory myFactory = (GordianCoreEncryptorFactory) pEncryptor.getOwner().getFactory().getEncryptorFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pEncryptor.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pEncryptor.getSpec());

        /* Check unique mapping */
        final GordianEncryptorSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pEncryptor.getSpec(), mySpec, "Invalid mapping for  " + pEncryptor.getSpec());
    }
}
