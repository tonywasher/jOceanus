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
package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignature;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.AsymmetricStore.FactorySignature;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.nio.charset.StandardCharsets;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
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
        final GordianAsyncFactory myTgtAsym = pSignature.getOwner().getPartner();
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

        /* Check outgoing signature */
        final GordianSignatureFactory mySigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = mySigns.createSigner(mySpec);
        final GordianSignParamsBuilder myBuilder = mySigns.newSignParamsBuilder();
        mySigner.initForSigning(myBuilder.keyPairAndContext(myMirror, myContext));
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();
        mySigner.initForVerify(myBuilder.keyPairAndContext(myPair, myContext));
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

        /* Create a second copy of the keyPair */
        final GordianAsyncFactory myFactory = pSignature.getOwner().getFactory();
        final GordianKeyPairFactory myKPFactory = myFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myKPFactory.getKeyPairGenerator(myPair.getKeyPairSpec());
        final PKCS8EncodedKeySpec myPKCS8 = myPairs.getPKCS8Encoding();
        final X509EncodedKeySpec myX509 = myPairs.getX509Encoding();
        final GordianKeyPair mySecondCopy = myGenerator.deriveKeyPair(myX509, myPKCS8);

        /* Create signer and verifier */
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
        final GordianSignParams myParams = myBuilder.keyPair(mySecondCopy);
        mySigner.initForSigning(myParams);
        mySigner.update(myMessage);
        myVerifier.initForVerify(myParams);
        mySigner.update(myMessage);

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

        /* Check outgoing signature */
        final GordianSignatureFactory mySrcSigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final GordianSignatureFactory myTgtSigns = pSignature.getOwner().getPartner().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = mySrcSigns.createSigner(mySpec);
        final GordianSignParamsBuilder myBuilder = mySrcSigns.newSignParamsBuilder();
        mySigner.initForSigning(myBuilder.keyPairAndContext(myPair, myContext));
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();

        /* Check sent signature */
        mySigner = myTgtSigns.createSigner(mySpec);
        mySigner.initForVerify(myBuilder.keyPairAndContext(myPartnerSelf, myContext));
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature), "Failed to verify sent signature");

        /* Check incoming signature */
        mySigner.initForSigning(myBuilder.keyPairAndContext(myPartnerSelf, myContext));
        mySigner.update(myMessage);
        mySignature = mySigner.sign();
        mySigner = mySrcSigns.createSigner(mySpec);
        mySigner.initForVerify(myBuilder.keyPairAndContext(myPair, myContext));
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature), "Failed to verify returned signature");
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
        final GordianSignatureSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pSignature.getSpec(), mySpec, "Invalid mapping for  " + pSignature.getSpec());
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
