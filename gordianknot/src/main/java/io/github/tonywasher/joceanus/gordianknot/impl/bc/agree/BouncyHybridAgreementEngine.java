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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyHybridKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementParticipant;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementState;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreHybridKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Arrays;

import java.io.IOException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Hybrid Agreement Engine.
 */
public class BouncyHybridAgreementEngine
        extends GordianCoreAgreementEngine {
    /**
     * The hybridSpec.
     */
    private final GordianCoreHybridKEMSpec theHybrid;

    /**
     * The primary agreement.
     */
    private final GordianCoreAgreementEngine thePrimary;

    /**
     * The traditional agreement.
     */
    private final GordianCoreAgreementEngine theTraditional;

    /**
     * The traditional generator.
     */
    private final GordianKeyPairGenerator theGenerator;

    /**
     * The digest.
     */
    private final GordianDigest theDigest;

    /**
     * Constructor.
     *
     * @param pFactory the security factory
     * @param pSpec    the agreementSpec
     * @throws GordianException on error
     */
    BouncyHybridAgreementEngine(final GordianCoreAgreementFactory pFactory,
                                final GordianCoreAgreementSpec pSpec) throws GordianException {
        /* Initialize underlying class */
        super(pFactory, pSpec);
        theHybrid = ((GordianCoreKeyPairSpec) pSpec.getKeyPairSpec()).getHybridKEMSpec();
        final GordianBaseFactory myFactory = pFactory.getFactory();

        /* Create the generator */
        final GordianKeyPairFactory myKPFactory = myFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairSpecBuilder myKeyPairBuilder = myKPFactory.newKeyPairSpecBuilder();
        theGenerator = myKPFactory.getKeyPairGenerator(theHybrid.getTraditionalKeyPairSpec(myKeyPairBuilder));

        /* Create primary and traditional engines */
        final GordianAgreementSpecBuilder myAgreementBuilder = pFactory.newAgreementSpecBuilder();
        thePrimary = pFactory.createEngine(theHybrid.getPrimaryAgreementSpec(myKeyPairBuilder, myAgreementBuilder));
        theTraditional = pFactory.createEngine(theHybrid.getTraditionalAgreementSpec(myKeyPairBuilder, myAgreementBuilder));

        /* Create digest */
        theDigest = myFactory.getDigestFactory().createDigest(theHybrid.getDigestSpec());
    }

    @Override
    public void buildClientHello() throws GordianException {
        /* Access details */
        final BouncyHybridKeyPair myHybridPair = (BouncyHybridKeyPair) getServerKeyPair();
        final BouncyKeyPair myPrimaryPair = myHybridPair.getPrimary();
        final BouncyKeyPair myTradPair = myHybridPair.getTraditional();
        final GordianCoreAgreementParticipant myClient = getBuilder().getState().getClient();
        final byte[] myInitVector = myClient.getInitVector();

        /* Access primary engine details */
        final GordianCoreAgreementState myPrimaryState = thePrimary.getBuilder().getState();
        final GordianCoreAgreementParticipant myPrimaryClient = myPrimaryState.getClient();
        final GordianCoreAgreementParticipant myPrimaryServer = myPrimaryState.getServer();

        /* Build primary engine details */
        myPrimaryServer.setKeyPair(myPrimaryPair);
        myPrimaryClient.setInitVector(myInitVector);

        /* Build clientHello details in the engine */
        thePrimary.buildClientHello();

        /* Access traditional engine details */
        final GordianCoreAgreementState myTradState = theTraditional.getBuilder().getState();
        final GordianCoreAgreementParticipant myTradClient = myTradState.getClient();
        final GordianCoreAgreementParticipant myTradServer = myTradState.getServer();

        /* Build traditional engine details */
        myTradServer.setKeyPair(myTradPair);
        myTradClient.setInitVector(myInitVector);
        final GordianKeyPair myEphemeral = theHybrid.needTraditionalEphemeral()
                ? theGenerator.generateKeyPair() : null;
        myTradClient.setEphemeralKeyPair(myEphemeral);

        /* Build clientHello details in the engine */
        theTraditional.buildClientHello();

        /* Build the encapsulated message */
        final byte[] myPrimaryEncapsulated = myPrimaryState.getClientEncapsulated();
        final byte[] myTradEncapsulated = theHybrid.needTraditionalEphemeral()
                ? getSubjectPublicKeyBytes(myEphemeral) : myTradState.getClientEncapsulated();
        final byte[] myEncapsulated = Arrays.concatenate(myPrimaryEncapsulated, myTradEncapsulated);
        setClientEncapsulated(myEncapsulated);

        /* Store the secret */
        mergeResults(myTradEncapsulated, myTradPair);
    }

    @Override
    public void processClientHello() throws GordianException {
        /* Access details */
        final BouncyHybridKeyPair myHybridPair = (BouncyHybridKeyPair) getServerKeyPair();
        final BouncyKeyPair myPrimaryPair = myHybridPair.getPrimary();
        final BouncyKeyPair myTradPair = myHybridPair.getTraditional();
        final GordianCoreAgreementParticipant myClient = getBuilder().getState().getClient();
        final byte[] myInitVector = myClient.getInitVector();

        /* Access primary engine details */
        final GordianCoreAgreementState myPrimaryState = thePrimary.getBuilder().getState();
        final GordianCoreAgreementParticipant myPrimaryClient = myPrimaryState.getClient();
        final GordianCoreAgreementParticipant myPrimaryServer = myPrimaryState.getServer();

        /* Check minimum lengths */
        final byte[] myEncapsulated = getClientEncapsulated();
        final int myCipherTextLength = theHybrid.getCipherTextLength();
        if (myEncapsulated.length < myCipherTextLength) {
            throw new GordianDataException("CipherText length too short");
        }
        final byte[] myPrimaryEncapsulated = Arrays.copyOfRange(myEncapsulated, 0, myCipherTextLength);
        final byte[] myTradEncapsulated = Arrays.copyOfRange(myEncapsulated, myCipherTextLength, myEncapsulated.length);

        /* Update keyPairs and initVector */
        myPrimaryServer.setKeyPair(myPrimaryPair);
        myPrimaryClient.setInitVector(myInitVector);
        myPrimaryClient.setEncapsulated(myPrimaryEncapsulated);

        /* Process clientHello details in the engine */
        thePrimary.processClientHello();

        /* Access traditional engine details */
        final GordianCoreAgreementState myTradState = theTraditional.getBuilder().getState();
        final GordianCoreAgreementParticipant myTradClient = myTradState.getClient();
        final GordianCoreAgreementParticipant myTradServer = myTradState.getServer();

        /* Update keyPairs and initVector */
        myTradServer.setKeyPair(myTradPair);
        myTradClient.setInitVector(myInitVector);
        if (theHybrid.needTraditionalEphemeral()) {
            myTradClient.setEphemeralKeyPair(deriveEphemeral(myTradEncapsulated));
        } else {
            myTradClient.setEncapsulated(myTradEncapsulated);
        }

        /* Process clientHello details in the engine */
        theTraditional.processClientHello();

        /* Store the secret */
        mergeResults(myTradEncapsulated, myTradPair);
    }

    /**
     * Obtain the encoded publicKey bytes.
     *
     * @param pKeyPair the keyPair
     * @return the bytes
     */
    private byte[] getSubjectPublicKeyBytes(final GordianKeyPair pKeyPair) throws GordianException {
        final X509EncodedKeySpec myKeySpec = theGenerator.getX509Encoding(pKeyPair);
        final SubjectPublicKeyInfo myPubInfo = SubjectPublicKeyInfo.getInstance(myKeySpec.getEncoded());
        return myPubInfo.getPublicKeyData().getOctets();
    }

    /**
     * Obtain the ephemeral keyPair.
     *
     * @param pEncapsulated the encapsulated bytes
     * @return the keyPair
     */
    private GordianKeyPair deriveEphemeral(final byte[] pEncapsulated) throws GordianException {
        /* Protect against exceptions */
        try {
            final SubjectPublicKeyInfo myTradInfo = new SubjectPublicKeyInfo(theHybrid.getSecondaryIdentifier(), pEncapsulated);
            final X509EncodedKeySpec myTradSpec = new X509EncodedKeySpec(myTradInfo.getEncoded());
            return theGenerator.derivePublicOnlyKeyPair(myTradSpec);
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse encapsulated ephemeral", e);
        }
    }

    /**
     * Merge results.
     *
     * @param pTradCipherText the traditional cipher text
     * @param pTradKeyPair    the traditional keyPair
     * @throws GordianException on error
     */
    private void mergeResults(final byte[] pTradCipherText,
                              final GordianKeyPair pTradKeyPair) throws GordianException {
        /* Update with the results */
        final byte[] myPrimaryResult = (byte[]) thePrimary.getBuilder().getState().getResult();
        theDigest.update(myPrimaryResult);
        Arrays.fill(myPrimaryResult, (byte) 0);
        final byte[] myTraditionalResult = (byte[]) theTraditional.getBuilder().getState().getResult();
        theDigest.update(myTraditionalResult);
        Arrays.fill(myTraditionalResult, (byte) 0);

        /* Update with traditional cipherText */
        theDigest.update(pTradCipherText);

        /* Update with the traditional public bytes */
        theDigest.update(getSubjectPublicKeyBytes(pTradKeyPair));

        /* Update with the label */
        theDigest.update(theHybrid.getLabel());

        /* Calculate and store the digest */
        getBuilder().storeSecret(theDigest.finish());
    }
}
