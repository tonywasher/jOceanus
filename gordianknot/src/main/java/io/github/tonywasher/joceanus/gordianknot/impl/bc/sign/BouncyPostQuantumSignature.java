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
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairType;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;

/**
 * PostQuantum signer.
 */
public abstract class BouncyPostQuantumSignature
        extends BouncyDigestSignature {
    /**
     * The Mayo Signer.
     */
    private MessageSigner theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncyPostQuantumSignature(final GordianBaseFactory pFactory,
                               final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
    }

    /**
     * Set the signer.
     *
     * @param pSigner the signer
     */
    void setSigner(final MessageSigner pSigner) {
        theSigner = pSigner;
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        BouncyKeyPair.checkKeyPair(getKeyPair());

        /* Determine whether we should use random for signatures */
        final GordianCoreKeyPairType myType = GordianCoreKeyPairType.mapCoreType(getSignatureSpec().getKeyPairType());
        final boolean useRandom = myType.useRandomForSignatures();

        /* Initialise and set the signer */
        final BouncyPrivateKey<?> myPrivate = getKeyPair().getPrivateKey();
        final CipherParameters myParms = useRandom
                ? new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom())
                : myPrivate.getPrivateKey();
        theSigner.init(true, myParms);
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        BouncyKeyPair.checkKeyPair(getKeyPair());

        /* Initialise and set the signer */
        final BouncyPublicKey<?> myPublic = getKeyPair().getPublicKey();
        theSigner.init(false, myPublic.getPublicKey());
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        return theSigner.generateSignature(getDigest());
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        return theSigner.verifySignature(getDigest(), pSignature);
    }
}
