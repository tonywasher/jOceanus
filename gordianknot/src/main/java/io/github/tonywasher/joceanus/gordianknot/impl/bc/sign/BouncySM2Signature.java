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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM2Signer;

/**
 * SM2 signature.
 */
public class BouncySM2Signature
        extends GordianCoreSignature {
    /**
     * The Signer.
     */
    private final SM2Signer theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncySM2Signature(final GordianBaseFactory pFactory,
                       final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);

        /* Create the signer */
        final GordianDigestSpec mySpec = ((GordianCoreSignatureSpec) pSpec).getDigestSpec();
        if (GordianDigestType.SM3.equals(mySpec.getDigestType())) {
            theSigner = new SM2Signer();
        } else {
            final BouncyDigest myDigest = (BouncyDigest) pFactory.getDigestFactory().createDigest(mySpec);
            theSigner = new SM2Signer(myDigest.getDigest());
        }
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theSigner.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theSigner.update(pByte);
    }

    @Override
    public void update(final byte[] pBytes) {
        theSigner.update(pBytes, 0, pBytes.length);
    }

    @Override
    public void reset() {
        theSigner.reset();
    }

    @Override
    protected BouncyKeyPair getKeyPair() {
        return (BouncyKeyPair) super.getKeyPair();
    }

    @Override
    public void initForSigning(final GordianNewSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        final BouncyECPrivateKey myPrivate = (BouncyECPrivateKey) myPair.getPrivateKey();
        final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
        theSigner.init(true, myParms);
    }

    @Override
    public void initForVerify(final GordianNewSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        final BouncyECPublicKey myPublic = (BouncyECPublicKey) myPair.getPublicKey();
        theSigner.init(false, myPublic.getPublicKey());
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        try {
            return theSigner.generateSignature();
        } catch (CryptoException e) {
            throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        return theSigner.verifySignature(pSignature);
    }
}
