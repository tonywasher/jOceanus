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
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.crypto.signers.ECGOST3410Signer;

import java.math.BigInteger;

/**
 * GOST signer.
 */
public class BouncyGOSTSignature
        extends BouncyDigestSignature {
    /**
     * The Signer.
     */
    private final ECGOST3410Signer theSigner;

    /**
     * The Coder.
     */
    private final BouncyGOSTCoder theCoder;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signatureSpec.
     * @throws GordianException on error
     */
    BouncyGOSTSignature(final GordianBaseFactory pFactory,
                        final GordianSignatureSpec pSpec) throws GordianException {
        /* Initialise underlying class */
        super(pFactory, pSpec);

        /* Create the signer and Coder */
        theSigner = new ECGOST3410Signer();
        theCoder = new BouncyGOSTCoder(((GordianCoreSignatureSpec) pSpec).getDigestSpec().getDigestLength().getByteLength() << 1);
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
     * GOST encoder.
     */
    static final class BouncyGOSTCoder
            implements BouncyDSACoder {
        /**
         * The fixed length (if any).
         */
        private final Integer theLen;

        /**
         * Constructor.
         *
         * @param pLen the fixed length (or null)
         */
        BouncyGOSTCoder(final Integer pLen) {
            theLen = pLen;
        }

        @Override
        public byte[] dsaEncode(final BigInteger r,
                                final BigInteger s) {
            /* Access byteArrays */
            final byte[] myFirst = makeUnsigned(s);
            final byte[] mySecond = makeUnsigned(r);
            final byte[] myResult = new byte[theLen];

            /* Build array and return */
            System.arraycopy(myFirst, 0, myResult, theLen / 2 - myFirst.length, myFirst.length);
            System.arraycopy(mySecond, 0, myResult, theLen - mySecond.length, mySecond.length);
            return myResult;
        }

        /**
         * Make the value unsigned.
         *
         * @param pValue the value
         * @return the unsigned value
         */
        private static byte[] makeUnsigned(final BigInteger pValue) {
            /* Convert to byteArray and return if OK */
            final byte[] myResult = pValue.toByteArray();
            if (myResult[0] != 0) {
                return myResult;
            }

            /* Shorten the array */
            final byte[] myTmp = new byte[myResult.length - 1];
            System.arraycopy(myResult, 1, myTmp, 0, myTmp.length);
            return myTmp;
        }

        @Override
        public BigInteger[] dsaDecode(final byte[] pEncoded) {
            /* Build the value arrays */
            final byte[] myFirst = new byte[pEncoded.length / 2];
            final byte[] mySecond = new byte[pEncoded.length / 2];
            System.arraycopy(pEncoded, 0, myFirst, 0, myFirst.length);
            System.arraycopy(pEncoded, myFirst.length, mySecond, 0, mySecond.length);

            /* Create the signature values and return */
            final BigInteger[] sig = new BigInteger[2];
            sig[1] = new BigInteger(1, myFirst);
            sig[0] = new BigInteger(1, mySecond);
            return sig;
        }
    }
}
