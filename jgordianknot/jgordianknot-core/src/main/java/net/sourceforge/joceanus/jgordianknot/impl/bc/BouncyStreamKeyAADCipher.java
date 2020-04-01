/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.util.Arrays;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.ext.modes.ChaChaPoly1305;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for BouncyCastle Stream Ciphers.
 */
public class BouncyStreamKeyAADCipher
        extends BouncyStreamKeyCipher
        implements GordianStreamAADCipher {
    /**
     * The cipher.
     */
    private final ChaChaPoly1305 theCipher;

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
    BouncyStreamKeyAADCipher(final BouncyFactory pFactory,
                             final GordianStreamCipherSpec pCipherSpec,
                             final ChaChaPoly1305 pCipher) {
        super(pFactory, pCipherSpec, pCipher);
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
    public int finish(final byte[] out,
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
        if (!(pThat instanceof BouncyStreamKeyAADCipher)) {
            return false;
        }
        final BouncyStreamKeyAADCipher myThat = (BouncyStreamKeyAADCipher) pThat;

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
