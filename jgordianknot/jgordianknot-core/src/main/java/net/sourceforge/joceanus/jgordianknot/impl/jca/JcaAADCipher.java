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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jcajce.spec.AEADParameterSpec;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreAADCipher;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for JCA BouncyCastle AAD Ciphers.
 */
public class JcaAADCipher
        extends GordianCoreAADCipher
        implements GordianSymAADCipher {
    /**
     * Cipher.
     */
    private final Cipher theCipher;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher the cipher
     */
    JcaAADCipher(final JcaFactory pFactory,
                 final GordianSymCipherSpec pCipherSpec,
                 final Cipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public JcaKey<GordianSymKeySpec> getKey() {
        return (JcaKey<GordianSymKeySpec>) super.getKey();
    }

    @Override
    public void init(final boolean pEncrypt,
                     final GordianCipherParameters pParams) throws OceanusException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final JcaKey<GordianSymKeySpec> myJcaKey = JcaKey.accessKey(getKey());

        /* Access details */
        final int myMode = pEncrypt
                           ? Cipher.ENCRYPT_MODE
                           : Cipher.DECRYPT_MODE;
        final SecretKey myKey = myJcaKey.getKey();
        final byte[] myAEAD = getInitialAEAD();

        /* Protect against exceptions */
        try {
            /* Initialise as required */
            final AlgorithmParameterSpec myParms = myAEAD == null
                                             ? new IvParameterSpec(getInitVector())
                                             : new AEADParameterSpec(getInitVector(), 16, myAEAD);
            theCipher.init(myMode, myKey, myParms);
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to initialise cipher", e);
        }
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
            return theCipher.update(pBytes, pOffset, pLength, pOutput, pOutOffset);

            /* Handle exceptions */
        } catch (ShortBufferException e) {
            throw new GordianCryptoException("Failed to process bytes", e);
        }
    }

    @Override
    public void updateAAD(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Process the bytes */
            theCipher.updateAAD(pBytes, pOffset, pLength);

            /* Handle exceptions */
        } catch (IllegalStateException e) {
            throw new GordianCryptoException("Failed to process AAD bytes", e);
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
        } catch (ShortBufferException
                | IllegalBlockSizeException
                | BadPaddingException e) {
            throw new GordianCryptoException("Failed to finish operation", e);
        }
    }

    @Override
    public int getBlockSize() {
        return 0;
    }
}
