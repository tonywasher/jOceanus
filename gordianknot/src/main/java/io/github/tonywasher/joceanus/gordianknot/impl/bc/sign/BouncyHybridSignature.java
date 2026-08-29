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

package io.github.tonywasher.joceanus.gordianknot.impl.bc.sign;

import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParamsBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyHybridKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreHybridSignSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/**
 * BouncyCastle Hybrid Ssigner.
 */

public class BouncyHybridSignature
        extends GordianCoreSignature {
    /**
     * The prefix.
     */
    private static final byte[] PREFIX = Strings.toByteArray("CompositeAlgorithmSignatures2025");

    /**
     * The Sign Factory.
     */
    private final GordianSignatureFactory theSignFactory;

    /**
     * The hybridSpec.
     */
    private GordianCoreHybridSignSpec theHybrid;

    /**
     * The Digest.
     */
    private GordianDigest theDigest;

    /**
     * The Primary Signer.
     */
    private GordianCoreSignature thePrimary;

    /**
     * The Traditional Signer.
     */
    private GordianCoreSignature theTraditional;

    /**
     * The Context.
     */
    private byte[] theContext;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     */
    BouncyHybridSignature(final GordianBaseFactory pFactory,
                          final GordianSignatureSpec pSpec) {
        super(pFactory, pSpec);
        theSignFactory = pFactory.getAsyncFactory().getSignatureFactory();
    }

    /**
     * Check for bouncyKeyPair.
     *
     * @return the keyPair
     * @throws GordianException on error
     */
    BouncyHybridKeyPair checkKeyPair() throws GordianException {
        return BouncyHybridKeyPair.checkKeyPair(super.getKeyPair());
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyHybridKeyPair myPair = checkKeyPair();
        theHybrid = ((GordianCoreKeyPairSpec) myPair.getKeyPairSpec()).getHybridSignSpec();

        /* Access builders */
        final GordianSignParamsBuilder myParamsBuilder = theSignFactory.newSignParamsBuilder();
        theContext = pParams.getContext();

        /* Initialise the primary */
        thePrimary = (GordianCoreSignature) theSignFactory.createSigner(theHybrid.getPrimarySignatureSpec());
        final BouncyKeyPair myPrimary = myPair.getPrimary();
        final GordianSignParams myPrimaryParams = myParamsBuilder.keyPairAndContext(myPrimary, theHybrid.getLabel());
        thePrimary.initForSigning(myPrimaryParams);

        /* Initialise the traditional */
        theTraditional = (GordianCoreSignature) theSignFactory.createSigner(theHybrid.getTraditionalSignatureSpec());
        final BouncyKeyPair myTraditional = myPair.getTraditional();
        final GordianSignParams myTradParams = myParamsBuilder.keyPair(myTraditional);
        theTraditional.initForSigning(myTradParams);

        /* Initialise the digest */
        theDigest = getDigestFactory().createDigest(theHybrid.getDigestSpec());
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyHybridKeyPair myPair = checkKeyPair();
        theHybrid = ((GordianCoreKeyPairSpec) myPair.getKeyPairSpec()).getHybridSignSpec();

        /* Access builders */
        final GordianSignParamsBuilder myParamsBuilder = theSignFactory.newSignParamsBuilder();
        theContext = pParams.getContext();

        /* Initialise the primary */
        thePrimary = (GordianCoreSignature) theSignFactory.createSigner(theHybrid.getPrimarySignatureSpec());
        final BouncyKeyPair myPrimary = myPair.getPrimary();
        final GordianSignParams myPrimaryParams = myParamsBuilder.keyPairAndContext(myPrimary, theHybrid.getLabel());
        thePrimary.initForVerify(myPrimaryParams);

        /* Initialise the traditional */
        theTraditional = (GordianCoreSignature) theSignFactory.createSigner(theHybrid.getTraditionalSignatureSpec());
        final BouncyKeyPair myTraditional = myPair.getTraditional();
        final GordianSignParams myTradParams = myParamsBuilder.keyPair(myTraditional);
        theTraditional.initForVerify(myTradParams);

        /* Initialise the digest */
        theDigest = getDigestFactory().createDigest(theHybrid.getDigestSpec());
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) throws GordianException {
        checkInit();
        if (checkBuffer(pBytes, pOffset, pLength)) {
            theDigest.update(pBytes, pOffset, pLength);
        }
    }

    @Override
    public void update(final byte pByte) throws GordianException {
        checkInit();
        theDigest.update(pByte);
    }

    @Override
    protected BouncyHybridKeyPair getKeyPair() {
        return (BouncyHybridKeyPair) super.getKeyPair();
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        prepareSigners();
        final byte[] myPrimary = thePrimary.sign();
        final byte[] myTraditional = theTraditional.sign();

        /* Concatenate and return */
        return Arrays.concatenate(myPrimary, myTraditional);
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Check minimum lengths */
        final int mySigLength = theHybrid.getSignatureLength();
        if (pSignature.length < mySigLength) {
            throw new GordianDataException("Signature too short");
        }

        /* Split the signature */
        final byte[] myPrimeSignature = Arrays.copyOfRange(pSignature, 0, mySigLength);
        final byte[] myTradSignature = Arrays.copyOfRange(pSignature, mySigLength, pSignature.length);

        /* Prepare the signers */
        prepareSigners();

        /* Verify the signatures */
        int numFails = 0;
        if (!thePrimary.verify(myPrimeSignature)) {
            numFails++;
        }
        if (!theTraditional.verify(myTradSignature)) {
            numFails++;
        }

        /* Concatenate and return */
        return numFails == 0;
    }

    /**
     * Prepare the signers.
     *
     * @throws GordianException on error
     */
    private void prepareSigners() throws GordianException {
        /* Complete the digest */
        final byte[] myDigest = theDigest.finish();

        /* Prepare the signers */
        prepareSigner(thePrimary, myDigest);
        prepareSigner(theTraditional, myDigest);
    }

    /**
     * Update the signer with the details.
     *
     * @param pSigner  the signer
     * @param pPreHash the preHash value
     * @throws GordianException on error
     */
    private void prepareSigner(final GordianCoreSignature pSigner,
                               final byte[] pPreHash) throws GordianException {
        /* Update with the prefix */
        pSigner.update(PREFIX);

        /* Update with the label */
        pSigner.update(theHybrid.getLabel());

        /* Update with the context */
        final byte myLen = (byte) (theContext == null ? 0 : theContext.length);
        pSigner.update(myLen);
        if (theContext != null) {
            pSigner.update(theContext);
        }

        /* Update with the preHash */
        pSigner.update(pPreHash);
    }
}
