/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreement;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementParams;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianSM9EncryptionMode;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.KeyStoreUtils.KeyStoreAlias;
import io.github.tonywasher.joceanus.gordianknot.util.GordianGenerator;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * SM9 Tests.
 */
class SM9Test {
    /**
     * The factories.
     */
    private static GordianFactory fcBCFACTORY;
    private static GordianFactory fcJCAFACTORY;

    /**
     * Perform setup operations.
     */
    @BeforeAll
    static void setUp() throws GordianException {
        /* Create the factories */
        fcBCFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
        fcJCAFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.JCA);
    }

    /**
     * Create the bouncyCastle SM9 test suite.
     *
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> bouncycastle() {
        return asymmetricTests(fcBCFACTORY, fcJCAFACTORY);
    }

    /**
     * Create the jca CM9 test suite.
     *
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> jca() {
        return asymmetricTests(fcJCAFACTORY, fcBCFACTORY);
    }

    /**
     * Create the asymmetric test suite for a factory.
     *
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @return the test stream
     */
    Stream<DynamicNode> asymmetricTests(final GordianFactory pFactory,
                                        final GordianFactory pPartner) {
        /* Create encryptor stream */
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("encryptors", () -> testEncryptors(pFactory)));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("crossEncryptors", () -> testCrossEncryptors(pFactory, pPartner))));

        /* Create exchange stream */
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("Exchange", () -> testExchanges(pFactory))));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("crossXchg", () -> testCrossExchanges(pFactory, pPartner))));

        /* Create kem stream */
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("KEMs", () -> testKEMs(pFactory))));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("crossKEMs", () -> testCrossKEMs(pFactory, pPartner))));

        /* Create signature stream */
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("signatures", () -> testSignatures(pFactory))));
        myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest("crossSignatures", () -> testCrossSignatures(pFactory, pPartner))));

        /* Return the stream */
        return myStream;
    }

    private static void testExchanges(final GordianFactory pFactory) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory myAsync = pFactory.getAsyncFactory();
        final GordianKeyPairFactory myKeyPairs = myAsync.getKeyPairFactory();
        final GordianAgreementFactory myAgrees = myAsync.getAgreementFactory();
        final byte[] mySourceId = "SourceID".getBytes();
        final byte[] myTargetId = "TargetID".getBytes();

        /* For BC Factory use withConfirm */
        final boolean withConfirm = pFactory.getFactoryType() == GordianFactoryType.BC;

        /* Create Encrypt keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = myKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = myKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianKeyPair myEncMasterPair = myEncGenerator.generateKeyPair();

        /* Certificates */
        final X500Name myClientName = KeyStoreUtils.buildX500Name(KeyStoreAlias.AGREE);
        final GordianCertificate myClientCert = myAgrees.newMiniCertificate(myClientName, myEncMasterPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final X500Name myTargetName = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);
        final GordianCertificate myTargetCert = myAgrees.newMiniCertificate(myTargetName, myEncMasterPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create agreement */
        final GordianAgreementSpecBuilder myAgreeBuilder = myAgrees.newAgreementSpecBuilder();
        final GordianAgreementSpec myAgreeSpec = withConfirm
                ? myAgreeBuilder.sm9Confirm(myEncMasterSpec, GordianAgreementKDF.NONE)
                : myAgreeBuilder.sm9(myEncMasterSpec, GordianAgreementKDF.NONE);
        final GordianAgreementParams myClientParams = myAgrees.newAgreementParams(myAgreeSpec, GordianLength.LEN_200.getLength())
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert)
                .setClientName(mySourceId)
                .setServerName(myTargetId);
        final GordianAgreement myClient = myAgrees.createAgreement(myClientParams);
        final byte[] myClientHello = myClient.nextMessage();
        final GordianAgreement myServer = myAgrees.parseAgreementMessage(myClientHello);
        final GordianAgreementParams myServerParams = myServer.getAgreementParams()
                .setServerCertificate(myTargetCert);
        myServer.updateParams(myServerParams);
        final byte[] myServerHello = myServer.nextMessage();
        myAgrees.parseAgreementMessage(myServerHello);
        final byte[] myClientResult = myClient.getByteArrayResult();
        if (withConfirm) {
            final byte[] myClientConfirm = myClient.nextMessage();
            myAgrees.parseAgreementMessage(myClientConfirm);
        }
        final byte[] myServerResult = myServer.getByteArrayResult();
        Assertions.assertArrayEquals(myClientResult, myServerResult, "Matching results");
    }

    private static void testCrossExchanges(final GordianFactory pSource,
                                           final GordianFactory pTarget) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory mySource = pSource.getAsyncFactory();
        final GordianKeyPairFactory mySourceKeyPairs = mySource.getKeyPairFactory();
        final GordianAgreementFactory mySourceAgrees = mySource.getAgreementFactory();
        final byte[] mySourceId = "SourceID".getBytes();
        final byte[] myTargetId = "TargetID".getBytes();

        /* Create Encrypt keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = mySourceKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = mySourceKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianKeyPair myEncMasterPair = myEncGenerator.generateKeyPair();

        /* Certificates */
        final X500Name myClientName = KeyStoreUtils.buildX500Name(KeyStoreAlias.AGREE);
        final GordianCertificate myClientCert = mySourceAgrees.newMiniCertificate(myClientName, myEncMasterPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final X500Name myTargetName = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);
        final GordianCertificate myTargetCert = mySourceAgrees.newMiniCertificate(myTargetName, myEncMasterPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create agreement */
        final GordianAgreementSpecBuilder myAgreeBuilder = mySourceAgrees.newAgreementSpecBuilder();
        final GordianAgreementSpec myAgreeSpec = myAgreeBuilder.sm9(myEncMasterSpec, GordianAgreementKDF.NONE);
        final GordianAgreementParams myClientParams = mySourceAgrees.newAgreementParams(myAgreeSpec, GordianLength.LEN_200.getLength())
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert)
                .setClientName(mySourceId)
                .setServerName(myTargetId);
        final GordianAgreement myClient = mySourceAgrees.createAgreement(myClientParams);
        final byte[] myClientHello = myClient.nextMessage();

        /* Create server agreement */
        final GordianAsyncFactory myTarget = pTarget.getAsyncFactory();
        final GordianKeyPairFactory myTargetKeyPairs = myTarget.getKeyPairFactory();
        final GordianAgreementFactory myTargetAgrees = myTarget.getAgreementFactory();
        final GordianKeyPairGenerator myTargetGenerator = myTargetKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final X509EncodedKeySpec myX509 = myEncGenerator.getX509Encoding(myEncMasterPair);
        final PKCS8EncodedKeySpec myPKCS8 = myEncGenerator.getPKCS8Encoding(myEncMasterPair);
        final GordianIdAwareKeyPair myDerivedMaster = (GordianIdAwareKeyPair) myTargetGenerator.deriveKeyPair(myX509, myPKCS8);
        final GordianCertificate myNewTargetCert = myTargetAgrees.newMiniCertificate(myTargetName, myDerivedMaster,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianAgreement myServer = myTargetAgrees.parseAgreementMessage(myClientHello);
        final GordianAgreementParams myServerParams = myServer.getAgreementParams()
                .setServerCertificate(myNewTargetCert);
        myServer.updateParams(myServerParams);
        final byte[] myServerHello = myServer.nextMessage();
        final byte[] myServerResult = myServer.getByteArrayResult();
        mySourceAgrees.parseAgreementMessage(myServerHello);
        final byte[] myClientResult = myClient.getByteArrayResult();
        Assertions.assertArrayEquals(myClientResult, myServerResult, "Matching results");
    }

    private static void testKEMs(final GordianFactory pFactory) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory myAsync = pFactory.getAsyncFactory();
        final GordianKeyPairFactory myKeyPairs = myAsync.getKeyPairFactory();
        final GordianAgreementFactory myAgrees = myAsync.getAgreementFactory();
        final byte[] myTargetId = "TargetID".getBytes();

        /* Create Encrypt keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = myKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = myKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianKeyPair myEncMasterPair = myEncGenerator.generateKeyPair();

        /* Certificates */
        final X500Name myTargetName = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);
        final GordianCertificate myTargetCert = myAgrees.newMiniCertificate(myTargetName, myEncMasterPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create agreement */
        final GordianAgreementSpecBuilder myAgreeBuilder = myAgrees.newAgreementSpecBuilder();
        final GordianAgreementSpec myAgreeSpec = myAgreeBuilder.kem(myEncMasterSpec, GordianAgreementKDF.NONE);
        final GordianAgreementParams myClientParams = myAgrees.newAgreementParams(myAgreeSpec, GordianLength.LEN_200.getLength())
                .setServerCertificate(myTargetCert)
                .setServerName(myTargetId);
        final GordianAgreement myClient = myAgrees.createAgreement(myClientParams);
        final byte[] myClientHello = myClient.nextMessage();
        final byte[] myClientResult = myClient.getByteArrayResult();
        final GordianAgreement myServer = myAgrees.parseAgreementMessage(myClientHello);
        final GordianAgreementParams myServerParams = myServer.getAgreementParams()
                .setServerCertificate(myTargetCert);
        myServer.updateParams(myServerParams);
        final byte[] myServerResult = myServer.getByteArrayResult();
        Assertions.assertArrayEquals(myClientResult, myServerResult, "Matching results");
    }

    private static void testCrossKEMs(final GordianFactory pSource,
                                      final GordianFactory pTarget) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory mySource = pSource.getAsyncFactory();
        final GordianKeyPairFactory mySourceKeyPairs = mySource.getKeyPairFactory();
        final GordianAgreementFactory mySourceAgrees = mySource.getAgreementFactory();
        final byte[] myTargetId = "TargetID".getBytes();

        /* Create Encrypt keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = mySourceKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = mySourceKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianKeyPair myEncMasterPair = myEncGenerator.generateKeyPair();

        /* Certificates */
        final X500Name myTargetName = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);
        final GordianCertificate mySourceCert = mySourceAgrees.newMiniCertificate(myTargetName, myEncMasterPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create agreement */
        final GordianAgreementSpecBuilder myAgreeBuilder = mySourceAgrees.newAgreementSpecBuilder();
        final GordianAgreementSpec myAgreeSpec = myAgreeBuilder.kem(myEncMasterSpec, GordianAgreementKDF.NONE);
        final GordianAgreementParams myClientParams = mySourceAgrees.newAgreementParams(myAgreeSpec, GordianLength.LEN_200.getLength())
                .setServerCertificate(mySourceCert)
                .setServerName(myTargetId);
        final GordianAgreement myClient = mySourceAgrees.createAgreement(myClientParams);
        final byte[] myClientHello = myClient.nextMessage();
        final byte[] myClientResult = myClient.getByteArrayResult();

        /* Create server agreement */
        final GordianAsyncFactory myTarget = pTarget.getAsyncFactory();
        final GordianKeyPairFactory myTargetKeyPairs = myTarget.getKeyPairFactory();
        final GordianAgreementFactory myTargetAgrees = myTarget.getAgreementFactory();
        final GordianKeyPairGenerator myTargetGenerator = myTargetKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final X509EncodedKeySpec myX509 = myEncGenerator.getX509Encoding(myEncMasterPair);
        final PKCS8EncodedKeySpec myPKCS8 = myEncGenerator.getPKCS8Encoding(myEncMasterPair);
        final GordianIdAwareKeyPair myDerivedMaster = (GordianIdAwareKeyPair) myTargetGenerator.deriveKeyPair(myX509, myPKCS8);
        final GordianAgreement myServer = myTargetAgrees.parseAgreementMessage(myClientHello);
        final GordianCertificate myTargetCert = myTargetAgrees.newMiniCertificate(myTargetName, myDerivedMaster,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianAgreementParams myServerParams = myServer.getAgreementParams()
                .setServerCertificate(myTargetCert);
        myServer.updateParams(myServerParams);
        final byte[] myServerResult = myServer.getByteArrayResult();
        Assertions.assertArrayEquals(myClientResult, myServerResult, "Matching results");
    }

    private static void testEncryptors(final GordianFactory pFactory) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory myAsync = pFactory.getAsyncFactory();
        final GordianKeyPairFactory myKeyPairs = myAsync.getKeyPairFactory();
        final GordianEncryptorFactory myEncs = myAsync.getEncryptorFactory();
        final byte[] myTargetId = "TargetID".getBytes();
        final byte[] myMessage = "ASimpleMessage".getBytes();

        /* Create Encrypt keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = myKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = myKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianIdAwareKeyPair myEncMasterPair = (GordianIdAwareKeyPair) myEncGenerator.generateKeyPair();
        final GordianIdAwareKeyPair myEncPair = myEncMasterPair.newUserKeyPair(GordianSM9EncryptType.ENCRYPT, myTargetId);
        final GordianKeyPair myXchgPair = myEncMasterPair.newUserKeyPair(GordianSM9EncryptType.EXCHANGE, myTargetId);
        final GordianIdAwareKeyPair myPOEncPair = myEncMasterPair.derivePublicOnlyUserKeyPair(GordianSM9EncryptType.ENCRYPT, myTargetId);
        final GordianIdAwareKeyPair myPOEncPair2 = myEncPair.derivePublicOnlyUserKeyPair(GordianSM9EncryptType.ENCRYPT, myTargetId);
        Assertions.assertEquals(myPOEncPair, myPOEncPair2, "derived Public Only");

        /* Can't access non-Master keyGenerators */
        final GordianKeyPairSpec myEncEncSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCRYPT);
        Assertions.assertThrows(GordianException.class, () -> myKeyPairs.getKeyPairGenerator(myEncEncSpec), "Encrypt keyPairGenerator");
        final GordianKeyPairSpec myEncXchgSpec = myKPBuilder.sm9(GordianSM9EncryptType.EXCHANGE);
        Assertions.assertThrows(GordianException.class, () -> myKeyPairs.getKeyPairGenerator(myEncXchgSpec), "Exchange keyPairGenerator");

        /* Check for arguments on userKey calls */
        Assertions.assertThrows(GordianException.class, () -> myEncMasterPair.newUserKeyPair(GordianSM9SignType.SIGN, myTargetId), "Wrong keyType");
        Assertions.assertThrows(GordianException.class, () -> myEncMasterPair.newUserKeyPair(GordianSM9EncryptType.ENCRYPT, null), "Null Id");
        Assertions.assertThrows(GordianException.class, () -> myEncMasterPair.newUserKeyPair(null, myTargetId), "Null keyType");
        Assertions.assertThrows(GordianException.class, () -> myEncMasterPair.derivePublicOnlyUserKeyPair(GordianSM9SignType.SIGN, myTargetId), "Wrong keyType");
        Assertions.assertThrows(GordianException.class, () -> myEncMasterPair.derivePublicOnlyUserKeyPair(GordianSM9EncryptType.ENCRYPT, null), "Null Id");
        Assertions.assertThrows(GordianException.class, () -> myEncMasterPair.derivePublicOnlyUserKeyPair(null, myTargetId), "Null keyType");
        Assertions.assertThrows(GordianException.class, () -> myEncPair.derivePublicOnlyUserKeyPair(GordianSM9SignType.SIGN, myTargetId), "Wrong keyType");
        Assertions.assertThrows(GordianException.class, () -> myEncPair.derivePublicOnlyUserKeyPair(GordianSM9EncryptType.ENCRYPT, null), "Null Id");
        Assertions.assertThrows(GordianException.class, () -> myEncPair.derivePublicOnlyUserKeyPair(null, myTargetId), "Null keyType");

        /* Can't create userKeys from userKeys */
        Assertions.assertThrows(GordianException.class, () -> myEncPair.newUserKeyPair(GordianSM9EncryptType.ENCRYPT, myTargetId), "UserKey usage");

        /* Obtain representations keyPair */
        final X509EncodedKeySpec myX509 = myEncGenerator.getX509Encoding(myEncMasterPair);
        final PKCS8EncodedKeySpec myPKCS8 = myEncGenerator.getPKCS8Encoding(myEncMasterPair);
        final GordianKeyPair myDerived = myEncGenerator.deriveKeyPair(myX509, myPKCS8);
        final boolean isIdentical = Objects.equals(myEncMasterPair, myDerived);
        Assertions.assertTrue(isIdentical, "Derive Master keyPair");

        /* Create an Encryptor */
        final GordianEncryptorSpecBuilder myEncBuilder = myEncs.newEncryptorSpecBuilder();
        final GordianEncryptorSpec myEncryptorSpec = myEncBuilder.sm9(GordianSM9EncryptionMode.STREAM);
        final GordianEncryptor myEncSender = myEncs.createEncryptor(myEncryptorSpec);
        myEncSender.initForEncrypt(myEncPair);
        Assertions.assertThrows(GordianException.class, () -> myEncSender.encrypt(null), "Null encrypt");
        final byte[] myEncrypted = myEncSender.encrypt(myMessage);
        myEncSender.initForDecrypt(myEncPair);
        Assertions.assertThrows(GordianException.class, () -> myEncSender.decrypt(null), "Null decrypt");
        final byte[] myDecrypted = myEncSender.decrypt(myEncrypted);
        final boolean matches = Arrays.equals(myMessage, myDecrypted);
        Assertions.assertTrue(matches, "Decryption");

        /* Can't initialise with master encryptor */
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForEncrypt(myEncMasterPair), "Encrypt with master");
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForDecrypt(myEncMasterPair), "Decrypt with master");

        /* Can't initialise with exchange encryptor */
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForEncrypt(myXchgPair), "Encrypt with exchange");
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForDecrypt(myXchgPair), "Decrypt with exchange");

        /* Check for inits with Sign key */
        final GordianKeyPairSpec mySigMasterSpec = myKPBuilder.sm9(GordianSM9SignType.SIGNMASTER);
        final GordianKeyPairGenerator mySigGenerator = myKeyPairs.getKeyPairGenerator(mySigMasterSpec);
        final GordianKeyPair mySigMasterPair = mySigGenerator.generateKeyPair();
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForEncrypt(mySigMasterPair), "Encrypt with SigKey");
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForDecrypt(mySigMasterPair), "Decrypt with SigKey");
    }

    private static void testCrossEncryptors(final GordianFactory pSource,
                                            final GordianFactory pTarget) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory mySource = pSource.getAsyncFactory();
        final GordianAsyncFactory myTarget = pTarget.getAsyncFactory();
        final GordianKeyPairFactory mySourceKeyPairs = mySource.getKeyPairFactory();
        final GordianEncryptorFactory mySourceEncs = mySource.getEncryptorFactory();
        final byte[] myTargetId = "TargetID".getBytes();
        final byte[] myMessage = "ASimpleMessage".getBytes();

        /* Create Encrypt keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = mySourceKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = mySourceKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianIdAwareKeyPair myEncMasterPair = (GordianIdAwareKeyPair) myEncGenerator.generateKeyPair();
        final GordianKeyPair myEncPair = myEncMasterPair.newUserKeyPair(GordianSM9EncryptType.ENCRYPT, myTargetId);

        /* Obtain representations keyPair */
        final X509EncodedKeySpec myX509 = myEncGenerator.getX509Encoding(myEncMasterPair);
        final PKCS8EncodedKeySpec myPKCS8 = myEncGenerator.getPKCS8Encoding(myEncMasterPair);
        final GordianKeyPairFactory myTargetKeyPairs = myTarget.getKeyPairFactory();
        final GordianEncryptorFactory myTargetEncs = myTarget.getEncryptorFactory();
        final GordianKeyPairGenerator myTargetGenerator = myTargetKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianIdAwareKeyPair myDerivedMaster = (GordianIdAwareKeyPair) myTargetGenerator.deriveKeyPair(myX509, myPKCS8);
        final GordianKeyPair myTargetPair = myDerivedMaster.newUserKeyPair(GordianSM9EncryptType.ENCRYPT, myTargetId);

        /* Create an Encryptor */
        final GordianEncryptorSpecBuilder myEncBuilder = mySourceEncs.newEncryptorSpecBuilder();
        final GordianEncryptorSpec myEncryptorSpec = myEncBuilder.sm9(GordianSM9EncryptionMode.STREAM);
        final GordianEncryptor myEncSender = mySourceEncs.createEncryptor(myEncryptorSpec);
        myEncSender.initForEncrypt(myEncPair);
        final byte[] myEncrypted = myEncSender.encrypt(myMessage);
        final GordianEncryptor myEncReceiver = myTargetEncs.createEncryptor(myEncryptorSpec);
        myEncReceiver.initForDecrypt(myTargetPair);
        final byte[] myDecrypted = myEncReceiver.decrypt(myEncrypted);
        final boolean matches = Arrays.equals(myMessage, myDecrypted);
        Assertions.assertTrue(matches, "Decryption");

        /* Destroy the keyPairs */
        myEncPair.destroy();
        myTargetPair.destroy();

        /* Can't encrypt using destroyed key */
        Assertions.assertThrows(GordianException.class, () -> myEncSender.encrypt(myMessage), "Encrypt with destroyed keyPair");
        Assertions.assertThrows(GordianException.class, () -> myEncReceiver.decrypt(myEncrypted), "Decrypt with destroyed keyPair");

        /* Can't init using destroyed key */
        Assertions.assertThrows(GordianException.class, () -> myEncSender.initForEncrypt(myEncPair), "initEncrypt with destroyed keyPair");
        Assertions.assertThrows(GordianException.class, () -> myEncReceiver.initForDecrypt(myTargetPair), "initDecrypt with destroyed keyPair");
    }

    private static void testSignatures(final GordianFactory pFactory) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory myAsync = pFactory.getAsyncFactory();
        final GordianKeyPairFactory myKeyPairs = myAsync.getKeyPairFactory();
        final GordianSignatureFactory mySigns = myAsync.getSignatureFactory();
        final byte[] mySignerId = "SignerID".getBytes();
        final byte[] myMessage = "ASimpleMessage".getBytes();

        /* Create Signature keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = myKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec mySigMasterSpec = myKPBuilder.sm9(GordianSM9SignType.SIGNMASTER);
        final GordianKeyPairGenerator mySigGenerator = myKeyPairs.getKeyPairGenerator(mySigMasterSpec);
        final GordianIdAwareKeyPair mySigMasterPair = (GordianIdAwareKeyPair) mySigGenerator.generateKeyPair();
        final GordianIdAwareKeyPair mySigPair = mySigMasterPair.newUserKeyPair(GordianSM9SignType.SIGN, mySignerId);
        final GordianIdAwareKeyPair myPOSigPair = mySigMasterPair.derivePublicOnlyUserKeyPair(GordianSM9SignType.SIGN, mySignerId);
        final GordianIdAwareKeyPair myPOSigPair2 = mySigPair.derivePublicOnlyUserKeyPair(GordianSM9SignType.SIGN, mySignerId);
        Assertions.assertEquals(myPOSigPair, myPOSigPair2, "derived Public Only");

        /* Can't access non-Master keyGenerators */
        final GordianKeyPairSpec mySignSignSpec = myKPBuilder.sm9(GordianSM9SignType.SIGN);
        Assertions.assertThrows(GordianException.class, () -> myKeyPairs.getKeyPairGenerator(mySignSignSpec), "Sign keyPairGenerator");

        /* Check for arguments on userKey calls */
        Assertions.assertThrows(GordianException.class, () -> mySigMasterPair.newUserKeyPair(GordianSM9EncryptType.ENCRYPT, mySignerId), "Wrong keyType");
        Assertions.assertThrows(GordianException.class, () -> mySigMasterPair.newUserKeyPair(GordianSM9SignType.SIGN, null), "Null Id");
        Assertions.assertThrows(GordianException.class, () -> mySigMasterPair.newUserKeyPair(null, mySignerId), "Null keyType");
        Assertions.assertThrows(GordianException.class, () -> mySigMasterPair.derivePublicOnlyUserKeyPair(GordianSM9EncryptType.ENCRYPT, mySignerId), "Wrong keyType");
        Assertions.assertThrows(GordianException.class, () -> mySigMasterPair.derivePublicOnlyUserKeyPair(GordianSM9SignType.SIGN, null), "Null Id");
        Assertions.assertThrows(GordianException.class, () -> mySigMasterPair.derivePublicOnlyUserKeyPair(null, mySignerId), "Null keyType");
        Assertions.assertThrows(GordianException.class, () -> mySigPair.derivePublicOnlyUserKeyPair(GordianSM9EncryptType.ENCRYPT, mySignerId), "Wrong keyType");
        Assertions.assertThrows(GordianException.class, () -> mySigPair.derivePublicOnlyUserKeyPair(GordianSM9SignType.SIGN, null), "Null Id");
        Assertions.assertThrows(GordianException.class, () -> mySigPair.derivePublicOnlyUserKeyPair(null, mySignerId), "Null keyType");

        /* Can't create userKeys from userKeys */
        Assertions.assertThrows(GordianException.class, () -> mySigPair.newUserKeyPair(GordianSM9SignType.SIGN, mySignerId), "UserKey usage");

        /* Obtain representations keyPair */
        final X509EncodedKeySpec myX509 = mySigGenerator.getX509Encoding(mySigMasterPair);
        final PKCS8EncodedKeySpec myPKCS8 = mySigGenerator.getPKCS8Encoding(mySigMasterPair);
        final GordianKeyPair myDerived = mySigGenerator.deriveKeyPair(myX509, myPKCS8);
        final boolean isIdentical = Objects.equals(mySigMasterPair, myDerived);
        Assertions.assertTrue(isIdentical, "derive MasterKeyPair");

        /* Create a signature */
        final GordianSignatureSpecBuilder mySigBuilder = mySigns.newSignatureSpecBuilder();
        final GordianSignatureSpec mySigSpec = mySigBuilder.sm9();
        final GordianSignature mySigner = mySigns.createSigner(mySigSpec);
        final GordianSignParamsBuilder mySigParamsBuilder = mySigns.newSignParamsBuilder();
        final GordianSignParams mySigParams = mySigParamsBuilder.keyPair(mySigPair);
        mySigner.initForSigning(mySigParams);
        mySigner.update(myMessage);
        final byte[] mySignature = mySigner.sign();
        mySigner.initForVerify(mySigParams);
        mySigner.update(myMessage);
        final boolean myResult = mySigner.verify(mySignature);
        Assertions.assertTrue(myResult, "Verify");

        /* Check for inits with Enc key */
        final GordianKeyPairSpec myEncMasterSpec = myKPBuilder.sm9(GordianSM9EncryptType.ENCMASTER);
        final GordianKeyPairGenerator myEncGenerator = myKeyPairs.getKeyPairGenerator(myEncMasterSpec);
        final GordianKeyPair myEncMasterPair = myEncGenerator.generateKeyPair();
        final GordianSignParams myBadParams = mySigParamsBuilder.keyPair(myEncMasterPair);
        Assertions.assertThrows(GordianException.class, () -> mySigner.initForSigning(myBadParams), "init with EncKey");
        Assertions.assertThrows(GordianException.class, () -> mySigner.initForVerify(myBadParams), "init with EncKey");
    }

    private static void testCrossSignatures(final GordianFactory pSource,
                                            final GordianFactory pTarget) throws GordianException {
        /* Access factories */
        final GordianAsyncFactory mySource = pSource.getAsyncFactory();
        final GordianAsyncFactory myTarget = pTarget.getAsyncFactory();
        final GordianKeyPairFactory mySourceKeyPairs = mySource.getKeyPairFactory();
        final GordianSignatureFactory mySourceSigns = mySource.getSignatureFactory();
        final byte[] mySignerId = "SignerID".getBytes();
        final byte[] myMessage = "ASimpleMessage".getBytes();

        /* Create Signature keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = mySourceKeyPairs.newKeyPairSpecBuilder();
        final GordianKeyPairSpec mySigMasterSpec = myKPBuilder.sm9(GordianSM9SignType.SIGNMASTER);
        final GordianKeyPairGenerator mySigGenerator = mySourceKeyPairs.getKeyPairGenerator(mySigMasterSpec);
        final GordianIdAwareKeyPair mySigMasterPair = (GordianIdAwareKeyPair) mySigGenerator.generateKeyPair();
        final GordianKeyPair mySigPair = mySigMasterPair.newUserKeyPair(GordianSM9SignType.SIGN, mySignerId);

        /* Obtain keyPair in target */
        final X509EncodedKeySpec myX509 = mySigGenerator.getX509Encoding(mySigMasterPair);
        final PKCS8EncodedKeySpec myPKCS8 = mySigGenerator.getPKCS8Encoding(mySigMasterPair);
        final GordianKeyPairFactory myTargetKeyPairs = myTarget.getKeyPairFactory();
        final GordianSignatureFactory myTargetSigns = myTarget.getSignatureFactory();
        final GordianKeyPairGenerator myTargetGenerator = myTargetKeyPairs.getKeyPairGenerator(mySigMasterSpec);
        final GordianIdAwareKeyPair myDerivedMaster = (GordianIdAwareKeyPair) myTargetGenerator.deriveKeyPair(myX509, myPKCS8);
        final GordianKeyPair myTargetPair = myDerivedMaster.newUserKeyPair(GordianSM9SignType.SIGN, mySignerId);

        /* Create a signature */
        final GordianSignatureSpecBuilder mySigBuilder = mySourceSigns.newSignatureSpecBuilder();
        final GordianSignatureSpec mySigSpec = mySigBuilder.sm9();
        final GordianSignature mySourceSigner = mySourceSigns.createSigner(mySigSpec);
        final GordianSignature myTargetSigner = myTargetSigns.createSigner(mySigSpec);
        final GordianSignParamsBuilder mySigParamsBuilder = mySourceSigns.newSignParamsBuilder();
        final GordianSignParams mySourceParams = mySigParamsBuilder.keyPair(mySigPair);
        mySourceSigner.initForSigning(mySourceParams);
        mySourceSigner.update(myMessage);
        final byte[] mySignature = mySourceSigner.sign();
        final GordianSignParams myTargetParams = mySigParamsBuilder.keyPair(myTargetPair);
        myTargetSigner.initForVerify(myTargetParams);
        myTargetSigner.update(myMessage);
        final boolean myResult = myTargetSigner.verify(mySignature);
        Assertions.assertTrue(myResult, "Verify");

        /* Destroy the keyPairs */
        mySigPair.destroy();
        myTargetPair.destroy();

        /* Can't sign using destroyed key */
        Assertions.assertThrows(GordianException.class, mySourceSigner::sign, "Sign with destroyed keyPair");
        Assertions.assertThrows(GordianException.class, () -> myTargetSigner.verify(mySignature), "Verify with destroyed keyPair");

        /* Can't update using destroyed key */
        Assertions.assertThrows(GordianException.class, () -> mySourceSigner.update(myMessage), "Update with destroyed keyPair");

        /* Can't init using destroyed key */
        Assertions.assertThrows(GordianException.class, () -> mySourceSigner.initForSigning(mySourceParams), "initEncrypt with destroyed keyPair");
        Assertions.assertThrows(GordianException.class, () -> myTargetSigner.initForVerify(myTargetParams), "initDecrypt with destroyed keyPair");
    }
}
