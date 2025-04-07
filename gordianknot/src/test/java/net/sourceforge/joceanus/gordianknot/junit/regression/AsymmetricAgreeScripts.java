/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianHandshakeAgreement;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianSignedAgreement;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryAgreement;
import net.sourceforge.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
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
public final class AsymmetricAgreeScripts {
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

    /* The Agreement signers */
    private static GordianKeyPair sgBCSIGNER;
    private static GordianKeyPair sgJCASIGNER;

    /* The resultType count */
    private static final AtomicInteger RESULTTYPE = new AtomicInteger(0);

    /**
     * Private constructor.
     */
    private AsymmetricAgreeScripts() {
    }

    /**
     * Initialise Signers.
     * @param pBCFactory the BC Factory
     * @param pJCAFactory the JCA Factory
     * @throws GordianException on error
     */
    static void createSecuritySigners(final GordianFactory pBCFactory,
                                      final GordianFactory pJCAFactory) throws GordianException {
        /* Create the BC Signer */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.ed448();
        GordianKeyPairFactory myFactory = pBCFactory.getKeyPairFactory();
        GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(mySpec);
        sgBCSIGNER = myGenerator.generateKeyPair();

        /* Derive the JCASigner */
        final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(sgBCSIGNER);
        final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(sgBCSIGNER);
        myFactory = pJCAFactory.getKeyPairFactory();
        myGenerator = myFactory.getKeyPairGenerator(mySpec);
        sgJCASIGNER = myGenerator.deriveKeyPair(myPublic, myPrivate);
    }

    /**
     * Obtain signer keyPair for factory.
     * @param pFactory the factory
     * @return the keyPair
     */
    private static GordianKeyPair getFactorySigner(final FactoryAgreement pFactory) {
        return pFactory.getOwner().getFactoryType() == GordianFactoryType.BC
                ? sgBCSIGNER
                : sgJCASIGNER;
    }

    /**
     * Obtain partner keyPair for factory.
     * @param pFactory the factory
     * @return the keyPair
     */
    private static GordianKeyPair getPartnerSigner(final FactoryAgreement pFactory) {
        return pFactory.getOwner().getFactoryType() == GordianFactoryType.BC
                ? sgJCASIGNER
                : sgBCSIGNER;
    }

    /**
     * Create the agreement test suite for an agreementSpec.
     * @param pAgreement the agreement
     * @return the test stream or null
     */
    static Stream<DynamicNode> agreementTests(final FactoryAgreement pAgreement) {
        /* Add self agreement test */
        Stream<DynamicNode> myTests = Stream.of(DynamicContainer.dynamicContainer("SelfAgree", Stream.of(
                selfAgreementTest(pAgreement)
        )));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkAgreementAlgId(pAgreement))));

        /* Check that the partner supports this keySpec*/
        final GordianKeyPairFactory myTgtAsym = pAgreement.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this agreement */
            final GordianAgreementFactory myTgtAgrees = pAgreement.getOwner().getPartner().getAgreementFactory();
            if (myTgtAgrees.validAgreementSpecForKeyPairSpec(pAgreement.getOwner().getKeySpec(), pAgreement.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerAgree", () -> checkPartnerAgreement(pAgreement))));
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
     * @param pAgreement the agreementSpec
     * @param pResultType the resultType
     * @throws GordianException on error
     */
    private static void checkSelfAgreement(final FactoryAgreement pAgreement,
                                           final Object pResultType) throws GordianException {
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

        /* Handle Anonymous */
        if (mySender instanceof GordianAnonymousAgreement
                && myResponder instanceof GordianAnonymousAgreement) {
            final byte[] myClientHello = ((GordianAnonymousAgreement) mySender).createClientHello(myPair);
            ((GordianAnonymousAgreement) myResponder).acceptClientHello(myPair, myClientHello);

            /* Handle Signed */
        } else if (mySender instanceof GordianSignedAgreement
                && myResponder instanceof GordianSignedAgreement) {
            /* Access the signer pair */
            final GordianKeyPair mySignPair = getFactorySigner(pAgreement);

            /* Check the agreement */
            final byte[] myClientHello = ((GordianSignedAgreement) mySender).createClientHello();
            final byte[] myServerHello
                    = ((GordianSignedAgreement) myResponder).acceptClientHello(mySignPair, myClientHello);
            ((GordianSignedAgreement) mySender).acceptServerHello(mySignPair, myServerHello);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianHandshakeAgreement
                && myResponder instanceof GordianHandshakeAgreement) {
            final byte[] myClientHello = ((GordianHandshakeAgreement) mySender).createClientHello(myTarget);
            final byte[] myServerHello = ((GordianHandshakeAgreement) myResponder).acceptClientHello(myTarget, myPair, myClientHello);
            final byte[] myClientConfirm = ((GordianHandshakeAgreement) mySender).acceptServerHello(myPair, myServerHello);
            if (myClientConfirm != null) {
                ((GordianHandshakeAgreement) myResponder).acceptClientConfirm(myClientConfirm);
            }

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
     * @throws GordianException on error
     */
    private static void checkPartnerAgreement(final FactoryAgreement pAgreement) throws GordianException {
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
        mySender.setResultType(BYTEARRAY);
        final GordianAgreement myResponder = myPartnerAgrees.createAgreement(mySpec);

        /* Handle Anonymous */
        if (mySender instanceof GordianAnonymousAgreement
                && myResponder instanceof GordianAnonymousAgreement) {
            final byte[] myClientHello = ((GordianAnonymousAgreement) mySender).createClientHello(myTarget);
            ((GordianAnonymousAgreement) myResponder).acceptClientHello(myPartnerTarget, myClientHello);

            /* Handle Signed */
        } else if (mySender instanceof GordianSignedAgreement
                && myResponder instanceof GordianSignedAgreement) {
            /* Access the signer pair */
            final GordianKeyPair mySignPair = getFactorySigner(pAgreement);
            final GordianKeyPair myPartnerSignPair = getPartnerSigner(pAgreement);

            /* Check the agreement */
            final byte[] myClientHello = ((GordianSignedAgreement) mySender).createClientHello();
            final byte[] myServerHello
                    = ((GordianSignedAgreement) myResponder).acceptClientHello(myPartnerSignPair, myClientHello);
            ((GordianSignedAgreement) mySender).acceptServerHello(mySignPair, myServerHello);

            /* Handle ephemeral */
        } else if (mySender instanceof GordianHandshakeAgreement
                && myResponder instanceof GordianHandshakeAgreement) {
            final byte[] myClientHello = ((GordianHandshakeAgreement) mySender).createClientHello(myPair);
            final byte[] myServerHello = ((GordianHandshakeAgreement) myResponder).acceptClientHello(myPartnerSelf, myPartnerTarget, myClientHello);
            final byte[] myClientConfirm = ((GordianHandshakeAgreement) mySender).acceptServerHello(myTarget, myServerHello);
            if (myClientConfirm != null) {
                ((GordianHandshakeAgreement) myResponder).acceptClientConfirm(myClientConfirm);
            }

        } else {
            Assertions.fail("Invalid Agreement");
        }

        /* Check that the values match */
        final byte[] myFirst = (byte[]) mySender.getResult();
        final byte[] mySecond = (byte[]) myResponder.getResult();
        Assertions.assertArrayEquals(myFirst, mySecond, "Failed to agree crossFactory keySet");
    }

    /**
     * Check agreementAlgId.
     * @param pAgreement the agreement to check
     */
    private static void checkAgreementAlgId(final FactoryAgreement pAgreement) {
        /* Access the factory */
        final GordianCoreAgreementFactory myFactory = (GordianCoreAgreementFactory) pAgreement.getOwner().getFactory().getAgreementFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pAgreement.getSpec());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pAgreement.getSpec());

        /* Check unique mapping */
        final GordianAgreementSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pAgreement.getSpec(), mySpec, "Invalid mapping for  " + pAgreement.getSpec());
    }
}
