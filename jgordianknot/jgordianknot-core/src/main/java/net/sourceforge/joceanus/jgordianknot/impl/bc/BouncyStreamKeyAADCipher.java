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

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.ext.modes.ChaChaPoly1305;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
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
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher the cipher
     */
    protected BouncyStreamKeyAADCipher(final BouncyFactory pFactory,
                                       final GordianStreamCipherSpec pCipherSpec,
                                       final ChaChaPoly1305 pCipher) {
        super(pFactory, pCipherSpec, pCipher);
        theCipher = pCipher;
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
}
