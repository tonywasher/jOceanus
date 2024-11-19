/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamAEADCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.tethys.OceanusException;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.util.Arrays;

/**
 * Cipher for BouncyCastle Stream Ciphers.
 */
public class BouncyStreamKeyAEADCipher
        extends GordianCoreCipher<GordianStreamKeySpec>
        implements GordianStreamAEADCipher {
    /**
     * The cipher.
     */
    private final AEADCipher theCipher;

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
    BouncyStreamKeyAEADCipher(final BouncyFactory pFactory,
                              final GordianStreamCipherSpec pCipherSpec,
                              final AEADCipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public void init(final boolean pEncrypt,
                     final GordianCipherParameters pParams) throws OceanusException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final BouncyKey<GordianStreamKeySpec> myKey = BouncyKey.accessKey(getKey());

        /* Initialise the cipher */
        final KeyParameter myKeyParms = new KeyParameter(myKey.getKey());
        final byte[] myAEAD = getInitialAEAD();
        final CipherParameters myParms = myAEAD == null
                                         ? new ParametersWithIV(myKeyParms, getInitVector())
                                         : new AEADParameters(myKeyParms, getAEADMacSize(), getInitVector(), myAEAD);
        theCipher.init(pEncrypt, myParms);
        isEncrypting = pEncrypt;
    }

    @Override
    public void updateAAD(final byte[] in,
                          final int inOff,
                          final int len) {
        /* Pass call on */
        theCipher.processAADBytes(in, inOff, len);
    }

    @Override
    public int getOutputLength(final int len) {
        return theCipher.getOutputSize(len);
    }

    @Override
    public int doUpdate(final byte[] pBytes,
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
    public int getBlockSize() {
        return 0;
    }

    @Override
    public int doFinish(final byte[] out,
                        final int outOff) throws OceanusException {
        try {
            return theCipher.doFinal(out, outOff);
        } catch (InvalidCipherTextException e) {
            throw new GordianCryptoException("Mac mismatch", e);
        }
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
        if (!(pThat instanceof BouncyStreamKeyAEADCipher)) {
            return false;
        }
        final BouncyStreamKeyAEADCipher myThat = (BouncyStreamKeyAEADCipher) pThat;

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
