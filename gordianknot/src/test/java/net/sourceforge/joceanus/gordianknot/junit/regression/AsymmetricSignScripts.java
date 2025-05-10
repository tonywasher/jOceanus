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

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.gordianknot.junit.regression.AsymmetricStore.FactoryKeyPairs;
import net.sourceforge.joceanus.gordianknot.junit.regression.AsymmetricStore.FactorySignature;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

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
     * @param pSignature the signature
     * @return the test stream or null
     */
    static Stream<DynamicNode> signatureTests(final FactorySignature pSignature) {
        /* Add self signature test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("SelfSign", () -> checkSelfSignature(pSignature)));

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkSignatureAlgId(pSignature))));

        /* Check that the partner supports this keySpec*/
        final GordianKeyPairFactory myTgtAsym = pSignature.getOwner().getPartner();
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
     * @param pSignature the signature
     * @throws GordianException on error
     */
    private static void checkSelfSignature(final FactorySignature pSignature) throws GordianException {
        /* Access the KeySpec */
        final GordianSignatureSpec mySpec = pSignature.getSpec();
        final FactoryKeyPairs myPairs = pSignature.getOwner().getKeyPairs();
        final GordianKeyPair myPair = myPairs.getKeyPair();
        final GordianKeyPair myMirror = myPairs.getMirrorKeyPair();

        /* Check outgoing signature */
        final GordianSignatureFactory mySigns = pSignature.getOwner().getFactory().getSignatureFactory();
        final byte[] myMessage = "Hello there. How is life treating you?".getBytes();
        GordianSignature mySigner = mySigns.createSigner(mySpec);
        mySigner.initForSigning(GordianSignParams.keyPair(myMirror));
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();
        mySigner.initForVerify(GordianSignParams.keyPair(myPair));
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature),"Failed to verify own signature");
    }

    /**
     * Check Partner Signature.
     * @param pSignature the signature
     * @throws GordianException on error
     */
    private static void checkPartnerSignature(final FactorySignature pSignature) throws GordianException {
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
        mySigner.initForSigning(GordianSignParams.keyPair(myPair));
        mySigner.update(myMessage);
        byte[] mySignature = mySigner.sign();

        /* Check sent signature */
        mySigner = myTgtSigns.createSigner(mySpec);
        mySigner.initForVerify(GordianSignParams.keyPair(myPartnerSelf));
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature),"Failed to verify sent signature");

        /* Check incoming signature */
        mySigner.initForSigning(GordianSignParams.keyPair(myPartnerSelf));
        mySigner.update(myMessage);
        mySignature = mySigner.sign();
        mySigner = mySrcSigns.createSigner(mySpec);
        mySigner.initForVerify(GordianSignParams.keyPair(myPair));
        mySigner.update(myMessage);
        Assertions.assertTrue(mySigner.verify(mySignature),"Failed to verify returned signature");
    }

    /**
     * Check signatureAlgId.
     * @param pSignature the signature to check
     * @throws GordianException on error
     */
    private static void checkSignatureAlgId(final FactorySignature pSignature) throws GordianException {
        /* Access the factory */
        final GordianCoreSignatureFactory myFactory = (GordianCoreSignatureFactory) pSignature.getOwner().getFactory().getSignatureFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpecAndKeyPair(pSignature.getSpec(), pSignature.getOwner().getKeyPairs().getKeyPair());
        Assertions.assertNotNull(myId,  "Unknown AlgorithmId for " + pSignature.getSpec());

        /* Check unique mapping */
        final GordianSignatureSpec mySpec = myFactory.getSpecForIdentifier(myId);
        Assertions.assertEquals(pSignature.getSpec(), mySpec, "Invalid mapping for  " + pSignature.getSpec());
    }
}
