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

package io.github.tonywasher.joceanus.gordianknot.junit.regression.asymmetric;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreement;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementParams;
import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementStatus;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianCertificate;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsymFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHybridKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHybridSignSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.keystore.KeyStoreUtils;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.keystore.KeyStoreUtils.KeyStoreAlias;
import io.github.tonywasher.joceanus.gordianknot.util.GordianGenerator;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Hybrid Tests.
 */
class HybridTest {
    /**
     * ServerName.
     */
    private static final X500Name SERVERNAME = KeyStoreUtils.buildX500Name(KeyStoreAlias.TARGET);

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
        Stream<DynamicNode> myStream = Stream.of(DynamicContainer.dynamicContainer("KEMS",
                testKeyPairs(pFactory, pPartner, GordianHybridKEMSpec.class)));
        myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer("Signs",
                testKeyPairs(pFactory, pPartner, GordianHybridSignSpec.class))));
        return myStream;
    }

    /**
     * Test keyPairs.
     *
     * @param pFactory the source asym factory
     * @param pPartner the target asym factory
     * @param pClazz   the keyPairSpec class
     */
    private static Stream<DynamicNode> testKeyPairs(final GordianFactory pFactory,
                                                    final GordianFactory pPartner,
                                                    final Class<? extends Enum<?>> pClazz) {
        final GordianAsymFactory mySource = pFactory.getAsymFactory();
        final GordianAsymFactory myTarget = pPartner.getAsymFactory();
        Stream<DynamicNode> myStream = Stream.empty();
        for (Enum<?> mySpec : pClazz.getEnumConstants()) {
            myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest(mySpec.toString(),
                    () -> testKeyPair(mySource, myTarget, mySpec))));
        }
        return myStream;
    }

    /**
     * Test keyPair.
     *
     * @param pSource the source asym factory
     * @param pTarget the target asym factory
     * @param pSpec   the keyPairSpec
     * @throws GordianException on error
     */
    private static void testKeyPair(final GordianAsymFactory pSource,
                                    final GordianAsymFactory pTarget,
                                    final Object pSpec) throws GordianException {
        /* Access factories */
        final GordianKeyPairFactory mySource = pSource.getKeyPairFactory();
        final GordianKeyPairFactory myTarget = pTarget.getKeyPairFactory();

        /* Create keyPairs */
        final GordianKeyPairSpecBuilder myKPBuilder = mySource.newKeyPairSpecBuilder();
        final GordianKeyPairSpec myKeyPairSpec = pSpec instanceof GordianHybridKEMSpec myKEM
                ? myKPBuilder.hybridKEM(myKEM) : myKPBuilder.hybridSign((GordianHybridSignSpec) pSpec);
        final GordianKeyPairGenerator mySourceGenerator = mySource.getKeyPairGenerator(myKeyPairSpec);
        final GordianKeyPair myKeyPair = mySourceGenerator.generateKeyPair();
        final X509EncodedKeySpec myX509 = mySourceGenerator.getX509Encoding(myKeyPair);
        final PKCS8EncodedKeySpec myPKCS8 = mySourceGenerator.getPKCS8Encoding(myKeyPair);
        final GordianKeyPair myDerived = mySourceGenerator.deriveKeyPair(myX509, myPKCS8);
        Assertions.assertEquals(myKeyPair, myDerived, "Matching results");

        /* Create target KeyPair */
        final GordianKeyPairGenerator myTargetGenerator = myTarget.getKeyPairGenerator(myKeyPairSpec);
        final GordianKeyPair myTargetDerived = myTargetGenerator.deriveKeyPair(myX509, myPKCS8);

        if (pSpec instanceof GordianHybridSignSpec) {
            /* Create Signer */
            final GordianSignatureFactory mySourceSigns = pSource.getSignatureFactory();
            final GordianSignatureSpecBuilder mySpecBuilder = mySourceSigns.newSignatureSpecBuilder();
            final GordianSignatureSpec mySignSpec = mySpecBuilder.hybrid();
            final GordianSignature mySourceSigner = mySourceSigns.createSigner(mySignSpec);
            final GordianSignParamsBuilder myParmBuilder = mySourceSigns.newSignParamsBuilder();
            final byte[] myContext = "SomeContext".getBytes();
            final GordianSignParams mySourceParams = myParmBuilder.keyPairAndContext(myKeyPair, myContext);
            final byte[] myMessage = "MyMessage".getBytes();
            mySourceSigner.initForSigning(mySourceParams);
            mySourceSigner.update(myMessage);
            final byte[] mySignature = mySourceSigner.sign();
            mySourceSigner.initForVerify(mySourceParams);
            mySourceSigner.update(myMessage);
            boolean bSuccess = mySourceSigner.verify(mySignature);
            Assertions.assertTrue(bSuccess, "Verify");

            /* Verify with Target */
            final GordianSignatureFactory myTargetSigns = pTarget.getSignatureFactory();
            final GordianSignature myTargetSigner = myTargetSigns.createSigner(mySignSpec);
            final GordianSignParams myTargetParams = myParmBuilder.keyPairAndContext(myTargetDerived, myContext);
            myTargetSigner.initForVerify(myTargetParams);
            myTargetSigner.update(myMessage);
            myTargetSigner.verify(mySignature);
            Assertions.assertTrue(bSuccess, "CrossVerify");
        }

        if (pSpec instanceof GordianHybridKEMSpec) {
            /* Create Agreement */
            final GordianAgreementFactory mySourceAgrees = pSource.getAgreementFactory();
            final GordianAgreementSpecBuilder mySpecBuilder = mySourceAgrees.newAgreementSpecBuilder();
            final GordianAgreementSpec myAgreeSpec = mySpecBuilder.kem(myKeyPairSpec, GordianAgreementKDF.NONE);
            final GordianCertificate myTargetCert = mySourceAgrees.newMiniCertificate(SERVERNAME, myKeyPair, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
            GordianAgreementParams myParams = mySourceAgrees.newAgreementParams(myAgreeSpec, GordianLength.LEN_128.getLength())
                    .setServerCertificate(myTargetCert);
            final GordianAgreement mySender = mySourceAgrees.createAgreement(myParams);

            /* Accept agreement */
            final byte[] myMessage = mySender.nextMessage();
            final GordianAgreement myResponder = mySourceAgrees.parseAgreementMessage(myMessage);
            myParams = myResponder.getAgreementParams().setServerCertificate(myTargetCert);
            myResponder.updateParams(myParams);

            /* Check that the values match */
            Assertions.assertEquals(GordianAgreementStatus.RESULT_AVAILABLE, mySender.getStatus(), "Sender result not available");
            final Object myFirst = mySender.getResult();
            Assertions.assertEquals(GordianAgreementStatus.RESULT_AVAILABLE, myResponder.getStatus(), "Responder result not available");
            final Object mySecond = myResponder.getResult();
            boolean isEqual = Objects.deepEquals(myFirst, mySecond);
            Assertions.assertTrue(isEqual, "Failed to agree result");

            /* Accept agreement */
            final GordianAgreementFactory myTargetAgrees = pTarget.getAgreementFactory();
            final GordianAgreement myOtherResponder = myTargetAgrees.parseAgreementMessage(myMessage);
            final GordianCertificate myOtherCert = myTargetAgrees.newMiniCertificate(SERVERNAME, myTargetDerived, new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
            myParams = myOtherResponder.getAgreementParams().setServerCertificate(myOtherCert);
            myOtherResponder.updateParams(myParams);

            /* Check that the values match */
            Assertions.assertEquals(GordianAgreementStatus.RESULT_AVAILABLE, myOtherResponder.getStatus(), "Responder result not available");
            final Object myOther = myOtherResponder.getResult();
            isEqual = Objects.deepEquals(myFirst, myOther);
            Assertions.assertTrue(isEqual, "Failed to agree cross result");
        }
    }
}
