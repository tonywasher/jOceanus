/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for BouncyCastle Stream Ciphers.
 */
public final class BouncyStreamKeyCipher
        extends GordianCipher<GordianStreamKeyType> {
    /**
     * Cipher.
     */
    private final StreamCipher theCipher;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher the cipher
     */
    protected BouncyStreamKeyCipher(final BouncyFactory pFactory,
                                    final GordianStreamCipherSpec pCipherSpec,
                                    final StreamCipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public BouncyKey<GordianStreamKeyType> getKey() {
        return (BouncyKey<GordianStreamKeyType>) super.getKey();
    }

    @Override
    public void initCipher(final GordianKey<GordianStreamKeyType> pKey) throws OceanusException {
        /* Determine the required length of IV */
        final int myLen = getKeyType().getIVLength();
        byte[] myIV = null;

        /* If we need an IV */
        if (myLen > 0) {
            /* Create a random IV */
            myIV = new byte[myLen];
            getRandom().nextBytes(myIV);
        }

        /* initialise with this IV */
        initCipher(pKey, myIV, true);
    }

    @Override
    public void initCipher(final GordianKey<GordianStreamKeyType> pKey,
                           final byte[] pIV,
                           final boolean pEncrypt) throws OceanusException {
        /* Access and validate the key */
        final BouncyKey<GordianStreamKeyType> myKey = BouncyKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Initialise the cipher */
        CipherParameters myParms = new KeyParameter(myKey.getKey());

        /* If we have an IV */
        if (pIV != null) {
            /* Adjust parameters */
            myParms = new ParametersWithIV(myParms, pIV);
        }

        /* Initialise the cipher */
        theCipher.init(pEncrypt, myParms);

        /* Store key and initVector */
        setKey(pKey);
        setInitVector(pIV);
    }

    @Override
    public int getOutputLength(final int pLength) {
        return pLength;
    }

    @Override
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
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
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
        /* Null operation */
        return 0;
    }
}
