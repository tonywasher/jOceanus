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
package io.github.tonywasher.joceanus.gordianknot.junit.regression.asymmetric;

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsymFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianIdAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.asymmetric.AsymmetricStore.FactoryKeyPairs;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.asymmetric.AsymmetricStore.FactorySignature;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

/**
 * Signature scripts.
 */
public final class AsymmetricSignScripts {
    /**
     * Private constructor.
     */
    private AsymmetricSignScripts() {
    }

    /**
     * Create the signature test suite for a signatureSpec.
     *
     * @param pSignature the signature
     * @return the test stream or null
     */
    static Stream<DynamicNode> signatureTests(final FactorySignature pSignature) {
        /* Add self signature test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfSign", () -> checkSelfSignature(pSignature)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkSignatureAlgId(pSignature))));

        /* Add destroy test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("destroy", () -> checkDestroySignature(pSignature))));

        /* Check that the partner supports this keySpec*/
        final GordianAsymFactory myTgtAsym = pSignature.getOwner().getPartner();
        if (myTgtAsym != null) {
            /* Add partner test if the partner supports this signature */
            final GordianSignatureFactory myTgtSigns = myTgtAsym.getSignatureFactory();
            if (myTgtSigns.validSignatureSpecForKeyPairSpec(pSignature.getOwner().getKeySpec(), pSignature.getSpec())) {
                myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("PartnerSign", () -> checkPartnerSignature(pSignature))));
            }
        }

        /* Return the test stream */
        return myTests;
    }

    /**
     * Check Self Signature.
     *
     * @param pSignature the signature
     * @throws GordianException on error
     */
    private static void checkSelfSignature(final FactorySignature pSignature) throws GordianException {
        /* Access the KeySpec */
        final GordianSignatureSpec mySpec = pSignature.getSpec();
        final FactoryKeyPairs myPairs = pSignature.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myMirror = myPairs.getMirrorKeyPair();
        final byte[] myContext = getContextForSpec(mySpec);
        final boolean isIdMaster = myPair instanceof GordianIdAwareKeyPair myIdAware
                && !myIdAware.getSubKeyType().isUserKey();

        /* Check outgoing signature */
        final GordianSignatureFactory mySigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = mySigns.createSigner(mySpec);
        final GordianSignParamsBuilder myBuilder = mySigns.newSignParamsBuilder();
        final GordianSignParams mySignParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(myMirror, AsymmetricStore.SOURCEID)
                : myBuilder.keyPairAndContext(myMirror, myContext);
        mySigner.initForSigning(mySignParams);
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();
        final GordianSignParams myVerifyParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(myPair, AsymmetricStore.SOURCEID)
                : myBuilder.keyPairAndContext(myPair, myContext);
        mySigner.initForVerify(myVerifyParams);
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature), "Failed to verify own signature");
    }

    /**
     * Check Destroy Signature.
     *
     * @param pSignature the signature
     * @throws GordianException on error
     */
    private static void checkDestroySignature(final FactorySignature pSignature) throws GordianException {
        /* Access the KeySpec */
        final GordianSignatureSpec mySpec = pSignature.getSpec();
        final FactoryKeyPairs myPairs = pSignature.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final boolean isIdMaster = myPair instanceof GordianIdAwareKeyPair myIdAware
                && !myIdAware.getSubKeyType().isUserKey();

        /* Create a second copy of the keyPair */
        final GordianKeyPair mySecondCopy = pSignature.getOwner().getKeyPairs().copyKeyPair(myPair);

        /* Create signer and verifier */
        final GordianAsymFactory myFactory = pSignature.getOwner().getFactory();
        final GordianSignatureFactory mySigns = myFactory.getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        final GordianSignature mySigner = mySigns.createSigner(mySpec);
        final GordianSignature myVerifier = mySigns.createSigner(mySpec);

        /* Can't update/sign/verify before init */
        Assertions.assertThrows(GordianException.class, () -> mySigner.update(myMessage), "update preInit");
        Assertions.assertThrows(GordianException.class, mySigner::sign, "sign preInit");
        Assertions.assertThrows(GordianException.class, () -> mySigner.verify(myMessage), "verify preInit");

        /* Prime the signer and verifier */
        final GordianSignParamsBuilder myBuilder = mySigns.newSignParamsBuilder();
        final GordianSignParams myParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(mySecondCopy, AsymmetricStore.SOURCEID)
                : myBuilder.keyPair(mySecondCopy);
        mySigner.initForSigning(myParams);
        mySigner.update(myMessage);
        myVerifier.initForVerify(myParams);
        mySigner.update(myMessage);

        /* Can't update with null/short buffers */
        Assertions.assertThrows(GordianException.class, () -> mySigner.update(null, 0, 1), "update null/length");
        Assertions.assertThrows(GordianException.class, () -> mySigner.update(new byte[]{}, 0, 1), "update short");
        Assertions.assertDoesNotThrow(() -> mySigner.update(null, 0, 0), "update null/zeroLength");
        Assertions.assertDoesNotThrow(() -> mySigner.update(null), "update null");

        /* Destroy the second copy */
        mySecondCopy.destroy();

        /* Can't update with a destroyed key pair */
        Assertions.assertThrows(GordianException.class, () -> mySigner.update(myMessage), "update destroyed");

        /* Can't sign/verify with a destroyed keyPair */
        Assertions.assertThrows(GordianException.class, mySigner::sign, "sign destroyed");
        Assertions.assertThrows(GordianException.class, () -> myVerifier.verify(myMessage), "verify destroyed");

        /* Can't init with a destroyed key pair */
        Assertions.assertThrows(GordianException.class, () -> mySigner.initForSigning(myParams), "initSign destroyed");
        Assertions.assertThrows(GordianException.class, () -> myVerifier.initForVerify(myParams), "initVerify destroyed");
    }

    /**
     * Check Partner Signature.
     *
     * @param pSignature the signature
     * @throws GordianException on error
     */
    private static void checkPartnerSignature(final FactorySignature pSignature) throws GordianException {
        /* Access the KeySpec */
        final GordianSignatureSpec mySpec = pSignature.getSpec();
        final FactoryKeyPairs myPairs = pSignature.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myPartnerSelf = myPairs.getPartnerSelfKeyPair();
        final byte[] myContext = getContextForSpec(mySpec);
        final boolean isIdMaster = myPair instanceof GordianIdAwareKeyPair myIdAware
                && !myIdAware.getSubKeyType().isUserKey();

        /* Check outgoing signature */
        final GordianSignatureFactory mySrcSigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final GordianSignatureFactory myTgtSigns = pSignature.getOwner().getPartner().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        final GordianSignature mySigner = mySrcSigns.createSigner(mySpec);
        final GordianSignParamsBuilder myBuilder = mySrcSigns.newSignParamsBuilder();
        final GordianSignParams mySignParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(myPair, AsymmetricStore.SOURCEID)
                : myBuilder.keyPairAndContext(myPair, myContext);
        mySigner.initForSigning(mySignParams);
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();

        /* Check sent signature */
        final GordianSignature myPartnerSigner = myTgtSigns.createSigner(mySpec);
        final GordianSignParams myVerifyParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(myPartnerSelf, AsymmetricStore.SOURCEID)
                : myBuilder.keyPairAndContext(myPartnerSelf, myContext);
        myPartnerSigner.initForVerify(myVerifyParams);
        myPartnerSigner.update(myMessage);
        Assertions.assertTrue(myPartnerSigner.verify(mySignature), "Failed to verify sent signature");

        /* Check incoming signature */
        final GordianSignParams myPartnerSignParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(myPartnerSelf, AsymmetricStore.TARGETID)
                : myBuilder.keyPairAndContext(myPartnerSelf, myContext);
        myPartnerSigner.initForSigning(myPartnerSignParams);
        myPartnerSigner.update(myMessage);
        mySignature = myPartnerSigner.sign();
        final GordianSignParams myPartnerVerifyParams = isIdMaster
                ? myBuilder.keyPairAndIdentity(myPair, AsymmetricStore.TARGETID)
                : myBuilder.keyPairAndContext(myPair, myContext);
        mySigner.initForVerify(myPartnerVerifyParams);
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature), "Failed to verify returned signature");

        /* Check for wrong factory */
        Assertions.assertThrows(GordianException.class, () -> mySigner.initForSigning(myBuilder.keyPair(myPartnerSelf)), "Wrong Factory");
        Assertions.assertThrows(GordianException.class, () -> mySigner.initForVerify(myBuilder.keyPair(myPartnerSelf)), "Wrong Factory");
    }

    /**
     * Check signatureAlgId.
     *
     * @param pSignature the signature to check
     * @throws GordianException on error
     */
    private static void checkSignatureAlgId(final FactorySignature pSignature) throws GordianException {
        /* Access the factory */
        final GordianCoreSignatureFactory myFactory = (GordianCoreSignatureFactory) pSignature.getOwner().getFactory().getSignatureFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpecAndKeyPair(pSignature.getSpec(), pSignature.getOwner().getKeyPairs().getKeyPair());
        Assertions.assertNotNull(myId, "Unknown AlgorithmId for " + pSignature.getSpec());

        /* Check unique mapping */
        GordianCoreSignatureSpec myBaseSpec = (GordianCoreSignatureSpec) pSignature.getSpec();
        final GordianSignatureSpec myDerivedSpec = myFactory.getSpecForIdentifier(myId);
        if (GordianSignatureType.DDSA.equals(myBaseSpec.getSignatureType())) {
            myBaseSpec = myBaseSpec.asSignatureType(GordianSignatureType.DSA);
        }
        Assertions.assertEquals(myBaseSpec, myDerivedSpec, "Invalid mapping for  " + pSignature.getSpec());
    }

    /**
     * Obtain context for signature spec.
     *
     * @param pSpec the spec
     */
    private static byte[] getContextForSpec(final GordianSignatureSpec pSpec) {
        return ((GordianCoreSignatureSpec) pSpec).supportsContext() ? "SomeContext".getBytes(StandardCharsets.UTF_8) : null;
    }
}
