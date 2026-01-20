/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreement;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementParams;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementStatus;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreement;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryAgreement;
import net.sourceforge.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
import net.sourceforge.joceanus.gordianknot.junit.regression.KeyStoreUtils.KeyStoreAlias;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * Agreement scripts.
 */
public final class AsymmetricXAgreeScripts {
    /**
     * KeySetSpec.
     */
    static final GordianKeySetSpec KEYSETSPEC = new GordianKeySetSpec();

    /**
     * SymCipherSpec.
     */
    private static final GordianSymCipherSpec SYMKEYSPEC = GordianSymCipherSpecBuilder.sic(GordianSymKeySpecBuilder.aes(GordianLength.LEN_256));

    /**
     * StreamCipherSpec.
     */
    private static final GordianStreamCipherSpec STREAMKEYSPEC = GordianStreamCipherSpecBuilder.stream(GordianStreamKeySpecBuilder.chacha(GordianLength.LEN_256));

    /**
     * ByteArrayResult.
     */
    private static final Integer BYTEARRAY = GordianLength.LEN_128.getLength();

    /**
     * ClientName.
     */
    private static final X500Name CLIENTNAME = KeyStoreUtils.buildX500Name(KeyStoreAlias.AGREE);

    /**
     * ClientName.
     */
    private static final X500Name SERVERNAME = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);

    /**
     * ClientName.
     */
    private static final X500Name SIGNERNAME = KeyStoreUtils.buildX500Name(KeyStoreAlias.SIGNER);

    /**
     * The Bouncy Signer.
     */
    private static GordianCertificate sgBCSIGNER;

    /**
     * The jca Signer.
     */
    private static GordianCertificate sgJCASIGNER;

    /**
     * The resultType count.
     */
    private static final AtomicInteger RESULTTYPE = new AtomicInteger(0);

    /**
     * Private constructor.
     */
    private AsymmetricXAgreeScripts() {
    }

    /**
     * Initialise Signers.
     *
     * @param pBCFactory  the BC Factory
     * @param pJCAFactory the JCA Factory
     * @throws GordianException on error
     */
    static void createSecuritySigners(final GordianFactory pBCFactory,
                                      final GordianFactory pJCAFactory) throws GordianException {
        /* Create the BC Signer */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.ed448();
        GordianAsyncFactory myFactory = pBCFactory.getAsyncFactory();
        GordianKeyPairFactory myKPFactory = myFactory.getKeyPairFactory();
        GordianXAgreementFactory myAgreeFactory = myFactory.getXAgreementFactory();
        GordianKeyPairGenerator myGenerator = myKPFactory.getKeyPairGenerator(mySpec);
        GordianKeyPair myKeyPair = myGenerator.generateKeyPair();
        sgBCSIGNER = myAgreeFactory.newMiniCertificate(SIGNERNAME, myKeyPair, new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE));

        /* Derive the JCASigner */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myKeyPair);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myKeyPair);
        myFactory = pJCAFactory.getAsyncFactory();
        myKPFactory = myFactory.getKeyPairFactory();
        myAgreeFactory = myFactory.getXAgreementFactory();
        myGenerator = myKPFactory.getKeyPairGenerator(mySpec);
        myKeyPair = myGenerator.deriveKeyPair(myPublic, myPrivate);
        sgJCASIGNER = myAgreeFactory.newMiniCertificate(SIGNERNAME, myKeyPair, new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE));
    }

    /**
     * Obtain signerCert for factory.
     *
     * @param pFactory the factory
     * @return the keyPair
     */
    private static GordianCertificate getFactorySigner(final FactoryAgreement pFactory) {
        return pFactory.getOwner().getFactoryType() == GordianFactoryType.BC
                ? sgBCSIGNER
                : sgJCASIGNER;
    }

    /**
     * Obtain partner signerCert for factory.
     *
     * @param pFactory the factory
     * @return the keyPair
     */
    private static GordianCertificate getPartnerSigner(final FactoryAgreement pFactory) {
        return pFactory.getOwner().getFactoryType() == GordianFactoryType.BC
                ? sgJCASIGNER
                : sgBCSIGNER;
    }

    /**
     * Create the agreement test suite for an agreementSpec.
     *
     * @param pAgreement the agreement
     * @return the test stream or null
     */
    static Stream<DynamicNode> xAgreementTests(final FactoryAgreement pAgreement) {
        /* Add self agreement test */
        Stream<DynamicNode> myTests = Stream.of(DynamicContainer.dynamicContainer("SelfAgree", Stream.of(
                selfAgreementTest(pAgreement)
        )));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkAgreementAlgId(pAgreement))));

        /* Check that the partner supports this keySpec */
        final GordianAsyncFactory myTgtAsym = pAgreement.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this agreement */
            final GordianXAgreementFactory myTgtAgrees = pAgreement.getOwner().getPartner().getXAgreementFactory();
            if (myTgtAgrees.validAgreementSpecForKeyPairSpec(pAgreement.getOwner().getKeySpec(), pAgreement.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerAgree", () -> checkPartnerAgreement(pAgreement))));
            }
        }

        /* Add rejection tests for non-anonymous agreements */
        if (!pAgreement.getSpec().getAgreementType().isAnonymous()) {
            myTests = Stream.concat(myTests, Stream.of(DynamicContainer.dynamicContainer("Rejection", rejectionTests(pAgreement))));
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Create the agreement test suite for an agreementSpec.
     *
     * @param pAgreement the agreement
     * @return the test stream or null
     */
    private static DynamicTest selfAgreementTest(final FactoryAgreement pAgreement) {
        /* Add self agreement test */
        switch (RESULTTYPE.getAndIncrement() % 5) {
            case 0:
                return DynamicTest.dynamicTest("factory", () -> checkSelfAgreement(pAgreement, GordianFactoryType.BC));
            case 1:
                return DynamicTest.dynamicTest("keySet", () -> checkSelfAgreement(pAgreement, KEYSETSPEC));
            case 2:
                return DynamicTest.dynamicTest("symCipher", () -> checkSelfAgreement(pAgreement, SYMKEYSPEC));
            case 3:
                return DynamicTest.dynamicTest("streamCipher", () -> checkSelfAgreement(pAgreement, STREAMKEYSPEC));
            case 4:
            default:
                return DynamicTest.dynamicTest("byteArray", () -> checkSelfAgreement(pAgreement, BYTEARRAY));
        }
    }

    /**
     * Test Self Agreement.
     *
     * @param pAgreement  the agreementSpec
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    private static void checkSelfAgreement(final FactoryAgreement pAgreement,
                                           final Object pResultType) throws GordianException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final GordianAgreementType myType = mySpec.getAgreementType();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myTarget = myType.isAnonymous() ? myPair : myPairs.getTargetKeyPair();
        final byte[] myAdditional = GordianKDFType.NONE.equals(mySpec.getKDFType()) ? null : "HelloThere".getBytes();

        /* Create mini-certificates */
        final GordianXAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getXAgreementFactory();
        final GordianCertificate myClientCert = (myType.isSigned() || myType.isAnonymous())
                ? null : myAgrees.newMiniCertificate(CLIENTNAME, myPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate myTargetCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(SERVERNAME, myTarget, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate mySignerCert = myType.isSigned()
                ? getFactorySigner(pAgreement) : null;

        /* Create the client hello */
        GordianXAgreementParams myParams = myAgrees.newAgreementParams(mySpec, pResultType)
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert)
                .setAdditionalData(myAdditional);
        final GordianXAgreement mySender = myAgrees.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();

        /* Handle receipt at server */
        final GordianXAgreement myResponder = myAgrees.parseAgreementMessage(myClientHello);
        myParams = myResponder.getAgreementParams()
                .setServerCertificate(myTargetCert)
                .setSigner(mySignerCert)
                .setAdditionalData(myAdditional);
        myResponder.updateParams(myParams);

        /* If we are not anonymous */
        if (!myType.isAnonymous()) {
            final byte[] myServerHello = myResponder.nextMessage();
            final GordianXAgreement myClient = myAgrees.parseAgreementMessage(myServerHello);
            Assertions.assertSame(myClient, mySender, "Did not match client");
        } else {
            Assertions.assertNull(myResponder.nextMessage(), "Unexpected ongoing message after anonymous agreement");
        }

        /* If we are confirming */
        if (mySpec.withConfirm()) {
            final byte[] myClientConfirm = mySender.nextMessage();
            final GordianXAgreement myServer = myAgrees.parseAgreementMessage(myClientConfirm);
            Assertions.assertSame(myServer, myResponder, "Did not match server");
            Assertions.assertNull(myServer.nextMessage(), "Unexpected ongoing message at server");
        } else if (!myType.isAnonymous()) {
            Assertions.assertNull(mySender.nextMessage(), "Unexpected ongoing message at client");
        }

        /* Check that the values match */
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
        final Object myFirst = mySender.getResult();
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
        final Object mySecond = myResponder.getResult();
        final boolean isEqual = Objects.deepEquals(myFirst, mySecond);
        Assertions.assertTrue(isEqual, "Failed to agree result");
    }

    /**
     * Check Partner Agreement.
     *
     * @param pAgreement the agreementSpec
     * @throws GordianException on error
     */
    private static void checkPartnerAgreement(final FactoryAgreement pAgreement) throws GordianException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final GordianAgreementType myType = mySpec.getAgreementType();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myTarget = myPairs.getTargetKeyPair();
        final GordianKeyPair myPartnerTarget = myPairs.getPartnerTargetKeyPair();

        /* Check the miniCertificates */
        final GordianXAgreementFactory mySrcAgrees = pAgreement.getOwner().getFactory().getXAgreementFactory();
        final GordianXAgreementFactory myPartnerAgrees = pAgreement.getOwner().getPartner().getXAgreementFactory();
        final GordianCertificate myClientCert = (myType.isSigned() || myType.isAnonymous())
                ? null : mySrcAgrees.newMiniCertificate(CLIENTNAME, myPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate myTargetCert = myType.isSigned()
                ? null : mySrcAgrees.newMiniCertificate(SERVERNAME, myTarget, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate myServerCert = myType.isSigned()
                ? null : myPartnerAgrees.newMiniCertificate(SERVERNAME, myPartnerTarget, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate mySignerCert = myType.isSigned()
                ? getPartnerSigner(pAgreement) : null;

        /* Create the client hello */
        GordianXAgreementParams myParams = mySrcAgrees.newAgreementParams(mySpec, BYTEARRAY)
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert);
        final GordianXAgreement mySender = mySrcAgrees.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();

        /* Handle receipt at server */
        final GordianXAgreement myResponder = myPartnerAgrees.parseAgreementMessage(myClientHello);
        myParams = myResponder.getAgreementParams()
                .setServerCertificate(myServerCert)
                .setSigner(mySignerCert);
        myResponder.updateParams(myParams);

        /* If we are not anonymous */
        if (!myType.isAnonymous()) {
            final byte[] myServerHello = myResponder.nextMessage();
            final GordianXAgreement myClient = mySrcAgrees.parseAgreementMessage(myServerHello);
            Assertions.assertSame(myClient, mySender, "Did not match client");
        } else {
            Assertions.assertNull(myResponder.nextMessage(), "Unexpected ongoing message on anonymous agreement");
        }

        /* If we are confirming */
        if (mySpec.withConfirm()) {
            final byte[] myClientConfirm = mySender.nextMessage();
            final GordianXAgreement myServer = myPartnerAgrees.parseAgreementMessage(myClientConfirm);
            Assertions.assertSame(myServer, myResponder, "Did not match server");
            Assertions.assertNull(myServer.nextMessage(), "Unexpected ongoing message at server");
        } else if (!myType.isAnonymous()) {
            Assertions.assertNull(mySender.nextMessage(), "Unexpected ongoing message at client");
        }

        /* Check that the values match */
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
        final byte[] myFirst = (byte[]) mySender.getResult();
        Assertions.assertInstanceOf(byte[].class, myFirst, "Unexpected Client resultType");
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
        final byte[] mySecond = (byte[]) myResponder.getResult();
        Assertions.assertInstanceOf(byte[].class, mySecond, "Unexpected Server resultType");
        Assertions.assertArrayEquals(myFirst, mySecond, "Failed to agree crossFactory bytes");
    }

    /**
     * Create the agreement test suite for an agreementSpec.
     *
     * @param pAgreement the agreement
     * @return the test stream or null
     */
    private static Stream<DynamicNode> rejectionTests(final FactoryAgreement pAgreement) {
        /* Add server rejection test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("server", () -> checkServerRejection(pAgreement)));

        /* If we are signing */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        if (mySpec.getAgreementType().isSigned()) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("signature", () -> checkSignatureRejection(pAgreement))));
        }

        /* If we are confirming */
        if (mySpec.withConfirm()) {
            /* Add confirmation rejection tests */
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("clientConfirm", () -> checkClientConfirmRejection(pAgreement))));
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("serverConfirm", () -> checkServerConfirmRejection(pAgreement))));
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Test Server Rejection.
     *
     * @param pAgreement the agreementSpec
     * @throws GordianException on error
     */
    private static void checkServerRejection(final FactoryAgreement pAgreement) throws GordianException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final GordianAgreementType myType = mySpec.getAgreementType();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myTarget = myPairs.getTargetKeyPair();

        /* Create mini-certificates */
        final GordianXAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getXAgreementFactory();
        final GordianCertificate myClientCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(CLIENTNAME, myPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate myTargetCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(SERVERNAME, myTarget, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create the client hello */
        GordianXAgreementParams myParams = myAgrees.newAgreementParams(mySpec, KEYSETSPEC)
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert);
        final GordianXAgreement mySender = myAgrees.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();

        /* Handle receipt at server */
        final GordianXAgreement myResponder = myAgrees.parseAgreementMessage(myClientHello);
        myResponder.setError("Rejected by server");

        /* Process rejection at client */
        final byte[] myServerHello = myResponder.nextMessage();
        final GordianXAgreement myClient = myAgrees.parseAgreementMessage(myServerHello);
        Assertions.assertSame(myClient, mySender, "Did not match client");
        Assertions.assertNull(myClient.nextMessage(), "Unexpected ongoing message at client");

        /* Check that the values match */
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
        Assertions.assertTrue(mySender.isRejected(), "Client result not rejected");
        final Object myFirst = mySender.getResult();
        Assertions.assertInstanceOf(GordianException.class, myFirst, "Unexpected Client resultType");
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
        Assertions.assertTrue(myResponder.isRejected(), "Server result not rejected");
        final Object mySecond = myResponder.getResult();
        Assertions.assertInstanceOf(GordianException.class, mySecond, "Unexpected Server resultType");
    }

    /**
     * Test Signature Rejection.
     *
     * @param pAgreement the agreementSpec
     * @throws GordianException on error
     */
    private static void checkSignatureRejection(final FactoryAgreement pAgreement) throws GordianException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();

        /* Create mini-certificates */
        final GordianXAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getXAgreementFactory();
        final GordianCertificate mySignerCert = getFactorySigner(pAgreement);

        /* Create the client hello */
        GordianXAgreementParams myParams = myAgrees.newAgreementParams(mySpec, GordianFactoryType.BC);
        final GordianXCoreAgreement mySender = (GordianXCoreAgreement) myAgrees.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();

        /* Handle receipt at server */
        final GordianXAgreement myResponder = myAgrees.parseAgreementMessage(myClientHello);
        myParams = myResponder.getAgreementParams()
                .setSigner(mySignerCert);
        myResponder.updateParams(myParams);

        /* Handle receipt at client */
        final byte[] myServerHello = myResponder.nextMessage();
        mySender.failSignature();
        final GordianXAgreement myClient = myAgrees.parseAgreementMessage(myServerHello);
        Assertions.assertSame(myClient, mySender, "Did not match client");
        Assertions.assertNull(myClient.nextMessage(), "Unexpected ongoing message at client");

        /* Check that the values match */
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
        Assertions.assertTrue(mySender.isRejected(), "Client result not rejected");
        final Object myFirst = mySender.getResult();
        Assertions.assertInstanceOf(GordianException.class, myFirst, "Unexpected Client resultType");
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
        final Object mySecond = myResponder.getResult();
        Assertions.assertFalse(myResponder.isRejected(), "Server result rejected");
        Assertions.assertInstanceOf(GordianFactory.class, mySecond, "Unexpected Server resultType");
    }

    /**
     * Test Confirm rejection at client.
     *
     * @param pAgreement the agreementSpec
     * @throws GordianException on error
     */
    private static void checkClientConfirmRejection(final FactoryAgreement pAgreement) throws GordianException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final GordianAgreementType myType = mySpec.getAgreementType();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myTarget = myPairs.getTargetKeyPair();

        /* Create mini-certificates */
        final GordianXAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getXAgreementFactory();
        final GordianCertificate myClientCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(CLIENTNAME, myPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate myTargetCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(SERVERNAME, myTarget, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create the client hello */
        GordianXAgreementParams myParams = myAgrees.newAgreementParams(mySpec, KEYSETSPEC)
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert);
        final GordianXCoreAgreement mySender = (GordianXCoreAgreement) myAgrees.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();

        /* Handle receipt at server */
        final GordianXAgreement myResponder = myAgrees.parseAgreementMessage(myClientHello);
        myParams = myResponder.getAgreementParams()
                .setServerCertificate(myTargetCert);
        myResponder.updateParams(myParams);

        /* Handle receipt at client */
        mySender.failConfirmation();
        final byte[] myServerHello = myResponder.nextMessage();
        final GordianXAgreement myClient = myAgrees.parseAgreementMessage(myServerHello);
        Assertions.assertSame(myClient, mySender, "Did not match client");

        /* Process rejection at server */
        final byte[] myClientConfirm = mySender.nextMessage();
        final GordianXAgreement myServer = myAgrees.parseAgreementMessage(myClientConfirm);
        Assertions.assertSame(myServer, myResponder, "Did not match server");
        Assertions.assertNull(myServer.nextMessage(), "Unexpected ongoing message at server");

        /* Check that the values match */
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
        Assertions.assertTrue(mySender.isRejected(), "Client result not rejected");
        final Object myFirst = mySender.getResult();
        Assertions.assertInstanceOf(GordianException.class, myFirst, "Unexpected Client resultType");
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
        Assertions.assertTrue(myResponder.isRejected(), "Server result not rejected");
        final Object mySecond = myResponder.getResult();
        Assertions.assertInstanceOf(GordianException.class, mySecond, "Unexpected Server resultType");
    }

    /**
     * Test Confirm rejection at client.
     *
     * @param pAgreement the agreementSpec
     * @throws GordianException on error
     */
    private static void checkServerConfirmRejection(final FactoryAgreement pAgreement) throws GordianException {
        /* Access the KeySpec */
        final GordianAgreementSpec mySpec = pAgreement.getSpec();
        final GordianAgreementType myType = mySpec.getAgreementType();
        final FactoryKeyPairs myPairs = pAgreement.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myTarget = myPairs.getTargetKeyPair();

        /* Create mini-certificates */
        final GordianXAgreementFactory myAgrees = pAgreement.getOwner().getFactory().getXAgreementFactory();
        final GordianCertificate myClientCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(CLIENTNAME, myPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        final GordianCertificate myTargetCert = myType.isSigned()
                ? null : myAgrees.newMiniCertificate(SERVERNAME, myTarget, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));

        /* Create the client hello */
        GordianXAgreementParams myParams = myAgrees.newAgreementParams(mySpec, KEYSETSPEC)
                .setClientCertificate(myClientCert)
                .setServerCertificate(myTargetCert);
        final GordianXAgreement mySender = myAgrees.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();

        /* Handle receipt at server */
        final GordianXCoreAgreement myResponder = (GordianXCoreAgreement) myAgrees.parseAgreementMessage(myClientHello);
        myParams = myResponder.getAgreementParams()
                .setServerCertificate(myTargetCert);
        myResponder.updateParams(myParams);

        /* Handle receipt at client */
        final byte[] myServerHello = myResponder.nextMessage();
        final GordianXAgreement myClient = myAgrees.parseAgreementMessage(myServerHello);
        Assertions.assertSame(myClient, mySender, "Did not match client");

        /* Handle receipt at server */
        myResponder.failConfirmation();
        final byte[] myClientConfirm = mySender.nextMessage();
        final GordianXAgreement myServer = myAgrees.parseAgreementMessage(myClientConfirm);
        Assertions.assertSame(myServer, myResponder, "Did not match server");
        Assertions.assertNull(myServer.nextMessage(), "Unexpected ongoing message at server");

        /* Check that the values match */
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
        Assertions.assertFalse(mySender.isRejected(), "Client result rejected");
        final Object myFirst = mySender.getResult();
        Assertions.assertInstanceOf(GordianKeySet.class, myFirst, "Unexpected Client resultType");
        Assertions.assertEquals(GordianXAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
        Assertions.assertTrue(myResponder.isRejected(), "Server result not rejected");
        final Object mySecond = myResponder.getResult();
        Assertions.assertInstanceOf(GordianException.class, mySecond, "Unexpected Server resultType");
    }

    /**
     * Check agreementAlgId.
     *
     * @param pAgreement the agreement to check
     */
    private static void checkAgreementAlgId(final FactoryAgreement pAgreement) {
        /* Access the factory */
        final GordianXCoreAgreementFactory myFactory = (GordianXCoreAgreementFactory) pAgreement.getOwner().getFactory().getXAgreementFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pAgreement.getSpec());
        Assertions.assertNotNull(myId, "Unknown AlgorithmId for " + pAgreement.getSpec());

        /* Check unique mapping */
        final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pAgreement.getSpec(), mySpec, "Invalid mapping for  " + pAgreement.getSpec());
    }
}
