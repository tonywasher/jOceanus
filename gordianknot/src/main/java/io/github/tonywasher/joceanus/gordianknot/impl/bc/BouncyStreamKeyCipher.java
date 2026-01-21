/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Cipher for BouncyCastle Stream Ciphers.
 */
public class BouncyStreamKeyCipher
        extends GordianCoreCipher<GordianStreamKeySpec>
        implements GordianStreamCipher {
    /**
     * Cipher.
     */
    private final StreamCipher theCipher;

    /**
     * is the cipher encrypting?
     */
    private boolean isEncrypting;

    /**
     * Constructor.
     *
     * @param pFactory    the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher     the cipher
     */
    protected BouncyStreamKeyCipher(final GordianBaseFactory pFactory,
                                    final GordianStreamCipherSpec pCipherSpec,
                                    final StreamCipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public BouncyKey<GordianStreamKeySpec> getKey() {
        return (BouncyKey<GordianStreamKeySpec>) super.getKey();
    }

    @Override
    public void init(final boolean pEncrypt,
                     final GordianCipherParameters pParams) throws GordianException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final BouncyKey<GordianStreamKeySpec> myKey = BouncyKey.accessKey(getKey());

        /* Initialise the cipher */
        final CipherParameters myParms = generateParameters(myKey, getInitVector());
        theCipher.init(pEncrypt, myParms);
        isEncrypting = pEncrypt;
    }

    /**
     * Generate CipherParameters.
     *
     * @param pKey the key
     * @param pIV  the initVector
     * @return the parameters
     */
    private static CipherParameters generateParameters(final BouncyKey<GordianStreamKeySpec> pKey,
                                                       final byte[] pIV) {
        /* Default parameter */
        CipherParameters myParams = new KeyParameter(pKey.getKey());

        /* Handle IV */
        if (pIV != null) {
            myParams = new ParametersWithIV(myParams, pIV);
        }

        /* Return the parameters */
        return myParams;
    }

    @Override
    public int getOutputLength(final int pLength) {
        return pLength;
    }

    @Override
    public int doUpdate(final byte[] pBytes,
                        final int pOffset,
                        final int pLength,
                        final byte[] pOutput,
                        final int pOutOffset) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Process the bytes */
            return theCipher.processBytes(pBytes, pOffset, pLength, pOutput, pOutOffset);

            /* Handle exceptions */
        } catch (DataLengthException e) {
            throw new GordianCryptoException("Failed to process bytes", e);
        }
    }

    @Override
    public int doFinish(final byte[] pOutput,
                        final int pOutOffset) throws GordianException {
        /* Reset the cipher */
        theCipher.reset();
        return 0;
    }

    @Override
    public int getBlockSize() {
        return 0;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the classes are the same */
        if (!(pThat instanceof BouncyStreamKeyCipher)) {
            return false;
        }
        final BouncyStreamKeyCipher myThat = (BouncyStreamKeyCipher) pThat;

        /* Check that the fields are equal */
        return isEncrypting == myThat.isEncrypting
                && super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                + (isEncrypting ? 1 : 0);
    }
}
