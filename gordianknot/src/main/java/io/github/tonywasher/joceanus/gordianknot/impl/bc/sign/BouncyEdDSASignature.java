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
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreEdwardsSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.bouncycastle.crypto.signers.Ed25519ctxSigner;
import org.bouncycastle.crypto.signers.Ed25519phSigner;
import org.bouncycastle.crypto.signers.Ed448Signer;
import org.bouncycastle.crypto.signers.Ed448phSigner;

/**
 * EdDSA signature.
 */
public class BouncyEdDSASignature
        extends GordianCoreSignature {
    /**
     * The Empty Context.
     */
    private static final byte[] EMPTY_CONTEXT = new byte[0];

    /**
     * Is this a preHash signature?
     */
    private final boolean preHash;

    /**
     * The Signer.
     */
    private Signer theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     */
    BouncyEdDSASignature(final GordianBaseFactory pFactory,
                         final GordianSignatureSpec pSpec) {
        /* Initialise underlying class */
        super(pFactory, pSpec);

        /* Determine preHash */
        preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
    }

    /**
     * Create the signer according to the keyPair.
     *
     * @param pKeyPair the keyPair
     * @return the signer
     */
    private Signer createSigner(final GordianKeyPair pKeyPair) {
        /* Determine the EdwardsCurve */
        final GordianCoreKeyPairSpec myKeySpec = (GordianCoreKeyPairSpec) pKeyPair.getKeyPairSpec();
        final GordianCoreEdwardsSpec myEdwardsSpec = myKeySpec.getEdwardsSpec();
        final boolean is25519 = myEdwardsSpec.is25519();
        byte[] myContext = getContext();
        myContext = myContext != null ? myContext : EMPTY_CONTEXT;

        /* If we are Ed25519 */
        if (is25519) {
            /* Handle preHash */
            if (preHash) {
                return new Ed25519phSigner(myContext);
            }

            /* Handle null context separately */
            return myContext.length == 0
                    ? new Ed25519Signer()
                    : new Ed25519ctxSigner(myContext);
        }

        /* Create the Ed448 signers */
        return preHash
                ? new Ed448phSigner(myContext)
                : new Ed448Signer(myContext);
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final GordianKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        theSigner = createSigner(myPair);
        final BouncyPrivateKey<?> myPrivate = getKeyPair().getPrivateKey();
        theSigner.init(true, myPrivate.getPrivateKey());
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final GordianKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        theSigner = createSigner(myPair);
        final BouncyPublicKey<?> myPublic = getKeyPair().getPublicKey();
        theSigner.init(false, myPublic.getPublicKey());
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
