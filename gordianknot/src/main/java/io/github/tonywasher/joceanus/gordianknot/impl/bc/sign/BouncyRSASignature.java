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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyRSAKeyPair.BouncyRSAPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyRSAKeyPair.BouncyRSAPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.params.ParametersWithRandom;

/**
 * RSA signature.
 */
public class BouncyRSASignature
        extends BouncyPSSSignature {
    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec
     * @throws GordianException on error
     */
    BouncyRSASignature(final GordianBaseFactory pFactory,
                       final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);
    }

    @Override
    protected BouncyKeyPair getKeyPair() {
        return (BouncyKeyPair) super.getKeyPair();
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        final BouncyRSAPrivateKey myPrivate = (BouncyRSAPrivateKey) myPair.getPrivateKey();
        final CipherParameters myParms = getSignatureSpec().getCoreType().isPSS()
                ? new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom())
                : myPrivate.getPrivateKey();
        getSigner().init(true, myParms);
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final BouncyKeyPair myPair = getKeyPair();
        BouncyKeyPair.checkKeyPair(myPair);

        /* Initialise and set the signer */
        final BouncyRSAPublicKey myPublic = (BouncyRSAPublicKey) myPair.getPublicKey();
        getSigner().init(false, myPublic.getPublicKey());
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        try {
            return getSigner().generateSignature();
        } catch (DataLengthException
                 | CryptoException e) {
            throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        return getSigner().verifySignature(pSignature);
    }
}
