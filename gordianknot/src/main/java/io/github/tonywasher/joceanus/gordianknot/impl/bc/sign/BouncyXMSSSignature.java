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
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianNewSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyXMSSKeyPair.BouncyXMSSMTPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyXMSSKeyPair.BouncyXMSSMTPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyXMSSKeyPair.BouncyXMSSPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyXMSSKeyPair.BouncyXMSSPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.pqc.crypto.xmss.XMSSSigner;

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
    private final XMSSSigner theSigner;

    /**
     * The XMSSMT Signer.
     */
    private final XMSSMTSigner theMTSigner;

    /**
     * Are we using the MT signer?
     */
    private boolean isMT;

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
        theSigner = new XMSSSigner();
        theMTSigner = new XMSSMTSigner();

        /* Determine preHash */
        preHash = GordianSignatureType.PREHASH.equals(pSpec.getSignatureType());
    }

    @Override
    public void initForSigning(final GordianNewSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Set the digest */
        final GordianCoreKeyPairSpec myKeyPairSpec = (GordianCoreKeyPairSpec) myPair.getKeyPairSpec();
        final GordianCoreXMSSSpec myKeySpec = myKeyPairSpec.getXMSSSpec();
        final GordianDigestSpec myDigestSpec = myKeySpec.getDigestSpec();
        setDigest(preHash ? myDigestSpec : null);

        /* Initialise and set the signer */
        isMT = myKeySpec.isMT();
        if (isMT) {
            final BouncyXMSSMTPrivateKey myPrivate = (BouncyXMSSMTPrivateKey) myPair.getPrivateKey();
            theMTSigner.init(true, myPrivate.getPrivateKey());
        } else {
            final BouncyXMSSPrivateKey myPrivate = (BouncyXMSSPrivateKey) myPair.getPrivateKey();
            theSigner.init(true, myPrivate.getPrivateKey());
        }
    }

    @Override
    public void initForVerify(final GordianNewSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Set the digest */
        final GordianCoreKeyPairSpec myKeyPairSpec = (GordianCoreKeyPairSpec) myPair.getKeyPairSpec();
        final GordianCoreXMSSSpec myKeySpec = myKeyPairSpec.getXMSSSpec();
        final GordianDigestSpec myDigestSpec = myKeySpec.getDigestSpec();
        setDigest(preHash ? myDigestSpec : null);

        /* Initialise and set the signer */
        isMT = myKeySpec.isMT();
        if (isMT) {
            final BouncyXMSSMTPublicKey myPublic = (BouncyXMSSMTPublicKey) myPair.getPublicKey();
            theMTSigner.init(false, myPublic.getPublicKey());
        } else {
            final BouncyXMSSPublicKey myPublic = (BouncyXMSSPublicKey) myPair.getPublicKey();
            theSigner.init(false, myPublic.getPublicKey());
        }
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        return isMT
                ? theMTSigner.generateSignature(getDigest())
                : theSigner.generateSignature(getDigest());
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        return isMT
                ? theMTSigner.verifySignature(getDigest(), pSignature)
                : theSigner.verifySignature(getDigest(), pSignature);
    }
}
