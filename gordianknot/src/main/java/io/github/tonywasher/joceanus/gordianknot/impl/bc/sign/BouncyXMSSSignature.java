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

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDigestXof;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.digest.BouncyDoubleDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.signers.XMSSMTSigner;
import org.bouncycastle.crypto.signers.XMSSSigner;

/**
 * XMSS signature.
 */
public class BouncyXMSSSignature
        extends BouncyDigestSignature {
    /**
     * Is this a preHash signature?
     */
    private final boolean preHash;

    /**
     * The XMSS Signer.
     */
    private final XMSSSigner theStdSigner;

    /**
     * The XMSSMT Signer.
     */
    private final XMSSMTSigner theMTSigner;

    /**
     * Is this a double digest?
     */
    private final boolean isDouble;

    /**
     * The active Signer.
     */
    private Signer theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncyXMSSSignature(final GordianBaseFactory pFactory,
                        final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);

        /* Create the signers */
        theStdSigner = new XMSSSigner();
        theMTSigner = new XMSSMTSigner();

        /* Determine preHash */
        preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
        isDouble = Boolean.TRUE.equals(pSpec.getSignatureSpec());
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = checkKeyPair();

        /* Set the digest */
        final GordianCoreKeyPairSpec myKeyPairSpec = (GordianCoreKeyPairSpec) myPair.getKeyPairSpec();
        final GordianCoreXMSSSpec myKeySpec = myKeyPairSpec.getXMSSSpec();
        BouncyDigest myDigest = null;
        if (preHash) {
            final GordianDigestSpec myDigestSpec = myKeySpec.getDigestSpec();
            myDigest = (BouncyDigest) getFactory().getDigestFactory().createDigest(myDigestSpec);
            if (isDouble) {
                myDigest = new BouncyDoubleDigest((BouncyDigestXof) myDigest);
            }
        }
        setDigest(myDigest);

        /* Initialise and set the signer */
        theSigner = myKeySpec.isMT() ? theMTSigner : theStdSigner;
        theSigner.init(true, myPair.getPrivateKey().getPrivateKey());
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = checkKeyPair();

        /* Set the digest */
        final GordianCoreKeyPairSpec myKeyPairSpec = (GordianCoreKeyPairSpec) myPair.getKeyPairSpec();
        final GordianCoreXMSSSpec myKeySpec = myKeyPairSpec.getXMSSSpec();
        BouncyDigest myDigest = null;
        if (preHash) {
            final GordianDigestSpec myDigestSpec = myKeySpec.getDigestSpec();
            myDigest = (BouncyDigest) getFactory().getDigestFactory().createDigest(myDigestSpec);
            if (isDouble) {
                myDigest = new BouncyDoubleDigest((BouncyDigestXof) myDigest);
            }
        }
        setDigest(myDigest);

        /* Initialise and set the signer */
        theSigner = myKeySpec.isMT() ? theMTSigner : theStdSigner;
        theSigner.init(false, myPair.getPublicKey().getPublicKey());
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Update signer with digest and create signature */
        try {
            final byte[] myDigest = getDigest();
            theSigner.update(myDigest, 0, myDigest.length);
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
        /* Update signer with digest and verify signature */
        final byte[] myDigest = getDigest();
        theSigner.update(myDigest, 0, myDigest.length);
        return theSigner.verifySignature(pSignature);
    }
}
