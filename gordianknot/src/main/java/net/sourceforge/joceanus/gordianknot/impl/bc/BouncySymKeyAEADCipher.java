/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymAEADCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.util.Arrays;

/**
 * Cipher for BouncyCastle AAD Symmetric Ciphers.
 */
public class BouncySymKeyAEADCipher
        extends GordianCoreCipher<GordianSymKeySpec>
        implements GordianSymAEADCipher {
    /**
     * Cipher.
     */
    private final AEADBlockCipher theCipher;

    /**
     * is the cipher encrypting?
     */
    private boolean isEncrypting;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher the cipher
     */
    BouncySymKeyAEADCipher(final BouncyFactory pFactory,
                           final GordianSymCipherSpec pCipherSpec,
                           final AEADBlockCipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public BouncyKey<GordianSymKeySpec> getKey() {
        return (BouncyKey<GordianSymKeySpec>) super.getKey();
    }

    @Override
    public void init(final boolean pEncrypt,
                     final GordianCipherParameters pParams) throws GordianException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final BouncyKey<GordianSymKeySpec> myKey = BouncyKey.accessKey(getKey());

        /* Initialise the cipher */
        final KeyParameter myKeyParms = new KeyParameter(myKey.getKey());
        final byte[] myAEAD = getInitialAEAD();
        final CipherParameters myParms = myAEAD == null
                  ? new ParametersWithIV(myKeyParms, getInitVector())
                  : new AEADParameters(myKeyParms, getAEADMacSize(), getInitVector(), getInitialAEAD());
        theCipher.init(pEncrypt, myParms);
        isEncrypting = pEncrypt;
    }

    @Override
    public int getOutputLength(final int pLength) {
        return theCipher.getOutputSize(pLength);
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
        } catch (DataLengthException
                | IllegalStateException e) {
            throw new GordianCryptoException("Failed to process bytes", e);
        }
    }

    @Override
    public void updateAAD(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Process the bytes */
            theCipher.processAADBytes(pBytes, pOffset, pLength);

            /* Handle exceptions */
        } catch (DataLengthException
                | IllegalStateException e) {
            throw new GordianCryptoException("Failed to process AAD bytes", e);
        }
    }

    @Override
    public int doFinish(final byte[] pOutput,
                        final int pOutOffset) throws GordianException {
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
        if (!(pThat instanceof BouncySymKeyAEADCipher)) {
            return false;
        }
        final BouncySymKeyAEADCipher myThat = (BouncySymKeyAEADCipher) pThat;

        /* Check that the fields are equal */
        return isEncrypting == myThat.isEncrypting
                && Arrays.equals(getInitialAEAD(), myThat.getInitialAEAD())
                && super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return super.hashCode()
                + Arrays.hashCode(getInitialAEAD())
                + (isEncrypting ? 1 : 0);
    }
}
