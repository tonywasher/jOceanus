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

import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySLHDSAKeyPair.BouncySLHDSAPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySLHDSAKeyPair.BouncySLHDSAPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
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
     * Is this a preHash signature?
     */
    private final boolean preHash;

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
        preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = checkKeyPair();
        final byte[] myContext = getContext();

        /* Initialise and set the signer */
        final BouncySLHDSAPrivateKey myPrivate = (BouncySLHDSAPrivateKey) myPair.getPrivateKey();
        CipherParameters myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
        if (myContext != null) {
            myParms = new ParametersWithContext(myParms, myContext);
        }
        if (preHash) {
            theHashSigner.init(true, myParms);
        } else {
            theSigner.init(true, myParms);
        }
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = checkKeyPair();
        final byte[] myContext = getContext();

        /* Initialise and set the signer */
        final BouncySLHDSAPublicKey myPublic = (BouncySLHDSAPublicKey) myPair.getPublicKey();
        CipherParameters myParms = myPublic.getPublicKey();
        if (myContext != null) {
            myParms = new ParametersWithContext(myParms, myContext);
        }
        if (preHash) {
            theHashSigner.init(false, myParms);
        } else {
            theSigner.init(false, myParms);
        }
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) throws GordianException {
        if (preHash) {
            checkInit();
            if (checkBuffer(pBytes, pOffset, pLength)) {
                theHashSigner.update(pBytes, pOffset, pLength);
            }
        } else {
            super.update(pBytes, pOffset, pLength);
        }
    }

    @Override
    public void update(final byte pByte) throws GordianException {
        if (preHash) {
            checkInit();
            theHashSigner.update(pByte);
        } else {
            super.update(pByte);
        }
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        try {
            return preHash
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
        return preHash
                ? theHashSigner.verifySignature(pSignature)
                : theSigner.verifySignature(getDigest(), pSignature);
    }
}
