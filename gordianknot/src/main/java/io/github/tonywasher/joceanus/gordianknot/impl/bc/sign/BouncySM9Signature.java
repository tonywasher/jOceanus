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
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9SignType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair.BouncyIdAwareKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9SignMasterPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9SignMasterPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9SignUserPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncySM9KeyPair.BouncySM9SignUserPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.gm.SM9Signature;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.params.ParametersWithID;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.SM9Signer;
import org.bouncycastle.util.Arrays;

import java.io.IOException;

/**
 * SM2 signature.
 */
public class BouncySM9Signature
        extends GordianCoreSignature {
    /**
     * The Signer.
     */
    private final SM9Signer theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncySM9Signature(final GordianBaseFactory pFactory,
                       final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);

        /* Create the signer */
        theSigner = new SM9Signer();
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) throws GordianException {
        checkInit();
        theSigner.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) throws GordianException {
        checkInit();
        theSigner.update(pByte);
    }

    @Override
    public void update(final byte[] pBytes) throws GordianException {
        checkInit();
        theSigner.update(pBytes, 0, pBytes.length);
    }

    @Override
    protected BouncyIdAwareKeyPair getKeyPair() {
        return (BouncyIdAwareKeyPair) super.getKeyPair();
    }

    /**
     * Check for bouncyKeyPair.
     *
     * @return the keyPair
     * @throws GordianException on error
     */
    BouncyIdAwareKeyPair checkKeyPair() throws GordianException {
        return (BouncyIdAwareKeyPair) BouncyKeyPair.checkKeyPair(super.getKeyPair());
    }

    /**
     * Obtain the User publicKey.
     *
     * @param pParams the parameters
     * @return the publicKey
     * @throws GordianException on error
     */
    private BouncySM9SignUserPublicKey getUserPublicKey(final GordianSignParams pParams) throws GordianException {
        final BouncyIdAwareKeyPair myKeyPair = checkKeyPair();
        final GordianSM9SignType myKeyType = (GordianSM9SignType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case SIGNMASTER -> {
                final BouncySM9SignMasterPublicKey myPublic = (BouncySM9SignMasterPublicKey) myKeyPair.getPublicKey();
                yield myPublic.deriveUserPublicKey(GordianSM9SignType.SIGN, pParams.getIdentity());
            }
            case SIGN -> (BouncySM9SignUserPublicKey) myKeyPair.getPublicKey();
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    /**
     * Obtain the User privateKey.
     *
     * @param pParams the parameters
     * @return the privateKey
     * @throws GordianException on error
     */
    private BouncySM9SignUserPrivateKey getUserPrivateKey(final GordianSignParams pParams) throws GordianException {
        final BouncyIdAwareKeyPair myKeyPair = checkKeyPair();
        final GordianSM9SignType myKeyType = (GordianSM9SignType) myKeyPair.getKeyPairSpec().getSubSpec();
        return switch (myKeyType) {
            case SIGNMASTER -> {
                final BouncySM9SignMasterPrivateKey myPrivate = (BouncySM9SignMasterPrivateKey) myKeyPair.getPrivateKey();
                yield myPrivate.newUserPrivateKey(GordianSM9EncryptType.ENCRYPT, pParams.getIdentity());
            }
            case SIGN -> (BouncySM9SignUserPrivateKey) myKeyPair.getPrivateKey();
            default -> throw new GordianDataException("Unsupported keyPairType: " + myKeyType);
        };
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);

        /* Initialise and set the signer */
        final BouncySM9SignUserPrivateKey myPrivate = getUserPrivateKey(pParams);
        final ParametersWithRandom myParms = new ParametersWithRandom(myPrivate.getPrivateKey(), getRandom());
        theSigner.init(true, myParms);
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);

        /* Initialise and set the signer */
        final BouncySM9SignUserPublicKey myPublic = getUserPublicKey(pParams);
        final ParametersWithID myParms = new ParametersWithID(myPublic.getPublicKey(), myPublic.getIdentity());
        theSigner.init(false, myParms);
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Sign the message */
        try {
            final byte[] myRaw = theSigner.generateSignature();
            return new SM9Signature(Arrays.copyOfRange(myRaw, 0, GordianLength.LEN_32.getLength()),
                    Arrays.copyOfRange(myRaw, GordianLength.LEN_32.getLength(), myRaw.length))
                    .getEncoded(ASN1Encoding.DER);

        } catch (CryptoException
                 | IOException e) {
            throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        final SM9Signature sig = SM9Signature.getInstance(pSignature);
        final byte[] myRaw = Arrays.concatenate(sig.getH(), sig.getS());
        return theSigner.verifySignature(myRaw);
    }
}
