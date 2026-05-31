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
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyEllipticKeyPair.BouncyECPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.keypair.BouncyKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.bc.sign.BouncySignature.BouncyDSACoder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.ua.DSTU4145Params;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.DSTU4145Signer;

import java.math.BigInteger;

/**
 * DSTU signer.
 */
public class BouncyDSTUSignature
        extends BouncyDigestSignature {
    /**
     * Expanded sandBox length.
     */
    private static final int EXPANDED_LEN = 128;

    /**
     * The Signer.
     */
    private final DSTU4145Signer theSigner;

    /**
     * The Coder.
     */
    private final BouncyDSTUCoder theCoder;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     */
    BouncyDSTUSignature(final GordianBaseFactory pFactory,
                        final GordianSignatureSpec pSpec) {
        /* Initialise underlying class */
        super(pFactory, pSpec, newDigest());

        /* Create the signer and Coder */
        theSigner = new DSTU4145Signer();
        theCoder = new BouncyDSTUCoder();
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
        final BigInteger[] myValues = theSigner.generateSignature(getDigest());
        return theCoder.dsaEncode(myValues[0], myValues[1]);
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Verify the message */
        final BigInteger[] myValues = theCoder.dsaDecode(pSignature);
        return theSigner.verifySignature(getDigest(), myValues[0], myValues[1]);
    }

    /**
     * Obtain new digest for DSTU signer.
     *
     * @return the new digest
     */
    private static Digest newDigest() {
        final byte[] myCompressed = DSTU4145Params.getDefaultDKE();
        final byte[] myExpanded = new byte[EXPANDED_LEN];

        for (int i = 0; i < myCompressed.length; i++) {
            myExpanded[i * 2] = (byte) ((myCompressed[i] >> GordianDataConverter.NYBBLE_SHIFT)
                    & GordianDataConverter.NYBBLE_MASK);
            myExpanded[i * 2 + 1] = (byte) (myCompressed[i] & GordianDataConverter.NYBBLE_MASK);
        }
        return new GOST3411Digest(myExpanded);
    }

    /**
     * DSTU encoder.
     */
    static final class BouncyDSTUCoder implements BouncyDSACoder {
        @Override
        public byte[] dsaEncode(final BigInteger r,
                                final BigInteger s) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access byteArrays */
                final byte[] myFirst = s.toByteArray();
                final byte[] mySecond = r.toByteArray();
                final byte[] myResult = myFirst.length > mySecond.length
                        ? new byte[myFirst.length * 2]
                        : new byte[mySecond.length * 2];

                /* Build array and return */
                System.arraycopy(myFirst, 0, myResult, myResult.length / 2 - myFirst.length, myFirst.length);
                System.arraycopy(mySecond, 0, myResult, myResult.length - mySecond.length, mySecond.length);
                return new DEROctetString(myResult).getEncoded();
            } catch (Exception e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGGEN, e);
            }
        }

        @Override
        public BigInteger[] dsaDecode(final byte[] pEncoded) throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access the bytes */
                final byte[] bytes = ((ASN1OctetString) ASN1Primitive.fromByteArray(pEncoded)).getOctets();

                /* Build the value arrays */
                final byte[] myFirst = new byte[bytes.length / 2];
                final byte[] mySecond = new byte[bytes.length / 2];
                System.arraycopy(bytes, 0, myFirst, 0, myFirst.length);
                System.arraycopy(bytes, myFirst.length, mySecond, 0, mySecond.length);

                /* Create the signature values and return */
                final BigInteger[] sig = new BigInteger[2];
                sig[1] = new BigInteger(1, myFirst);
                sig[0] = new BigInteger(1, mySecond);
                return sig;
            } catch (Exception e) {
                throw new GordianCryptoException(BouncySignature.ERROR_SIGPARSE, e);
            }
        }
    }
}
