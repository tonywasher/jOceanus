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
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySLHDSAKeyPair.BouncySLHDSAPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySLHDSAKeyPair.BouncySLHDSAPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.sign.BouncySignature.BouncyDigestSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.HashSLHDSASigner;
import org.bouncycastle.crypto.signers.SLHDSASigner;

/**
 * SLHDSA signer.
 */
public class BouncySLHDSASignature
        extends BouncyDigestSignature {
    /**
     * The SLHDSA Signer.
     */
    private final SLHDSASigner theSigner;

    /**
     * The SLHDSAHash Signer.
     */
    private final HashSLHDSASigner theHashSigner;

    /**
     * Is this a hash signer?
     */
    private boolean isHash;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncySLHDSASignature(final GordianBaseFactory pFactory,
                          final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
        theSigner = new SLHDSASigner();
        theHashSigner = new HashSLHDSASigner();
    }

    @Override
    public void initForSigning(final GordianNewSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Determine whether this is a hashSigner */
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) myPair.getKeyPairSpec();
        isHash = myKeySpec.getSLHDSASpec().isHash();

        /* Initialise and set the signer */
        final BouncySLHDSAPrivateKey myPrivate = (BouncySLHDSAPrivateKey) myPair.getPrivateKey();
        CipherParameters myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
        if (myContext != null) {
            myParms = new ParametersWithContext(myParms, myContext);
        }
        if (isHash) {
            theHashSigner.init(true, myParms);
        } else {
            theSigner.init(true, myParms);
        }
    }

    @Override
    public void initForVerify(final GordianNewSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Determine whether this is a hashSigner */
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) myPair.getKeyPairSpec();
        isHash = myKeySpec.getSLHDSASpec().isHash();

        /* Initialise and set the signer */
        final BouncySLHDSAPublicKey myPublic = (BouncySLHDSAPublicKey) myPair.getPublicKey();
        CipherParameters myParms = myPublic.getPublicKey();
        if (myContext != null) {
            myParms = new ParametersWithContext(myParms, myContext);
        }
        if (isHash) {
            theHashSigner.init(false, myParms);
        } else {
            theSigner.init(false, myParms);
        }
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        if (isHash) {
            theHashSigner.update(pBytes, pOffset, pLength);
        } else {
            super.update(pBytes, pOffset, pLength);
        }
    }

    @Override
    public void update(final byte pByte) {
        if (isHash) {
            theHashSigner.update(pByte);
        } else {
            super.update(pByte);
        }
    }

    @Override
    public void update(final byte[] pBytes) {
        if (isHash) {
            theHashSigner.update(pBytes, 0, pBytes.length);
        } else {
            super.update(pBytes);
        }
    }

    @Override
    public void reset() {
        if (isHash) {
            theHashSigner.reset();
        } else {
            super.reset();
        }
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        try {
            return isHash
                    ? theHashSigner.generateSignature()
                    : theSigner.generateSignature(getDigest());
        } catch (CryptoException e) {
            throw new GordianCryptoException("Failed to sign message", e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        return isHash
                ? theHashSigner.verifySignature(pSignature)
                : theSigner.verifySignature(getDigest(), pSignature);
    }
}
