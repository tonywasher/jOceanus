/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC5Parameters;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for BouncyCastle Symmetric Ciphers.
 */
public final class BouncySymKeyCipher
        extends GordianCoreCipher<GordianSymKeySpec> {
    /**
     * Cipher.
     */
    private final BufferedBlockCipher theCipher;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher the cipher
     */
    protected BouncySymKeyCipher(final BouncyFactory pFactory,
                                 final GordianSymCipherSpec pCipherSpec,
                                 final BufferedBlockCipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public BouncyKey<GordianSymKeySpec> getKey() {
        return (BouncyKey<GordianSymKeySpec>) super.getKey();
    }

    @Override
    public GordianSymCipherSpec getCipherSpec() {
        return (GordianSymCipherSpec) super.getCipherSpec();
    }

    /**
     * Do we need an initVector.
     * @return true/false
     */
    private boolean needsIV() {
        return getCipherSpec().needsIV();
    }

    @Override
    public void initCipher(final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        /* IV bytes */
        byte[] myIV = null;

        /* If we need an IV */
        if (needsIV()) {
            /* Create a random IV */
            final int myLen = getCipherSpec().getIVLength(false);
            myIV = new byte[myLen];
            getRandom().nextBytes(myIV);
        }

        /* initialise with this IV */
        initCipher(pKey, myIV, true);
    }

    @Override
    public void initCipher(final GordianKey<GordianSymKeySpec> pKey,
                           final byte[] pIV,
                           final boolean pEncrypt) throws OceanusException {
        /* Access and validate the key */
        final BouncyKey<GordianSymKeySpec> myKey = BouncyKey.accessKey(pKey);
        checkValidKey(pKey);
        final boolean useIV = needsIV();

        /* Initialise the cipher */
        CipherParameters myParms = generateParameters(myKey);
        if (useIV) {
            myParms = new ParametersWithIV(myParms, pIV);
        }
        theCipher.init(pEncrypt, myParms);

        /* Store key and initVector */
        setKey(pKey);
        setInitVector(useIV
                      ? pIV
                      : null);
    }

    /**
     * Generate CipherParameters.
     * @param pKey the key
     * @return the parameters
     */
    static CipherParameters generateParameters(final BouncyKey<GordianSymKeySpec> pKey) {
        final GordianSymKeySpec myKeySpec = pKey.getKeyType();
        if (myKeySpec != null) {
            final GordianSymKeyType myType = myKeySpec.getSymKeyType();
            if (GordianSymKeyType.RC5.equals(myType)) {
                return new RC5Parameters(pKey.getKey(), GordianCoreFactory.RC5_ROUNDS);
            }
        }
        return new KeyParameter(pKey.getKey());
    }

    @Override
    public int getOutputLength(final int pLength) {
        return theCipher.getOutputSize(pLength);
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
        } catch (DataLengthException
                | IllegalStateException e) {
            throw new GordianCryptoException("Failed to process bytes", e);
        }
    }

    @Override
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Finish the operation */
            return theCipher.doFinal(pOutput, pOutOffset);

            /* Handle exceptions */
        } catch (DataLengthException
                | IllegalStateException
                | InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to finish operation", e);
        }
    }
}
