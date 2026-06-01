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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyMLDSAKeyPair.BouncyMLDSAPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyMLDSAKeyPair.BouncyMLDSAPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.ParametersWithContext;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.HashMLDSASigner;
import org.bouncycastle.crypto.signers.MLDSASigner;

/**
 * MLDSA signer.
 */
public class BouncyMLDSASignature
        extends GordianCoreSignature {
    /**
     * The MLDSA Signer.
     */
    private Signer theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     */
    BouncyMLDSASignature(final GordianBaseFactory pFactory,
                         final GordianSignatureSpec pSpec) {
        /* Initialise underlying class */
        super(pFactory, pSpec);
    }

    /**
     * Create the signer according to the keyPair.
     *
     * @param pKeyPair the keyPair
     * @return the signer
     */
    private static Signer createSigner(final GordianKeyPair pKeyPair) {
        /* Determine whether this is a hashSigner */
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeyPair.getKeyPairSpec();
        final boolean isHash = myKeySpec.getMLDSASpec().isHash();

        /* Create the internal digests */
        return isHash
                ? new HashMLDSASigner()
                : new MLDSASigner();
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        theSigner = createSigner(myPair);
        final BouncyMLDSAPrivateKey myPrivate = (BouncyMLDSAPrivateKey) myPair.getPrivateKey();
        CipherParameters myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
        if (myContext != null) {
            myParms = new ParametersWithContext(myParms, myContext);
        }
        theSigner.init(true, myParms);
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        theSigner = createSigner(myPair);
        final BouncyMLDSAPublicKey myPublic = (BouncyMLDSAPublicKey) myPair.getPublicKey();
        CipherParameters myParms = myPublic.getPublicKey();
        if (myContext != null) {
            myParms = new ParametersWithContext(myParms, myContext);
        }
        theSigner.init(false, myParms);
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
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        try {
            return theSigner.generateSignature();
        } catch (CryptoException e) {
            throw new GordianCryptoException("Failed to sign message", e);
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
