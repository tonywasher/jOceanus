/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2023 Tony Washer
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
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jcajce.spec.AEADParameterSpec;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymAADCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for JCA BouncyCastle AAD Ciphers.
 * @param <T> the key Type
 */
public class JcaAADCipher<T extends GordianKeySpec>
        extends GordianCoreCipher<T>
        implements GordianAADCipher {
    /**
     * Cipher.
     */
    private final Cipher theCipher;

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
    JcaAADCipher(final JcaFactory pFactory,
                 final GordianCipherSpec<T> pCipherSpec,
                 final Cipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public JcaKey<T> getKey() {
        return (JcaKey<T>) super.getKey();
    }


    @Override
    public void init(final boolean pEncrypt,
                     final GordianCipherParameters pParams) throws OceanusException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final JcaKey<T> myJcaKey = JcaKey.accessKey(getKey());

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
                                             : new AEADParameterSpec(getInitVector(), getAEADMacSize(), myAEAD);
            theCipher.init(myMode, myKey, myParms);
            isEncrypting = pEncrypt;
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
    public int doUpdate(final byte[] pBytes,
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
    public int doFinish(final byte[] pOutput,
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

    /**
     * JcaSymAADCipher.
     */
    public static class JcaSymAADCipher
            extends JcaAADCipher<GordianSymKeySpec>
            implements GordianSymAADCipher {
        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pCipherSpec the cipherSpec
         * @param pCipher the cipher
         */
        JcaSymAADCipher(final JcaFactory pFactory,
                        final GordianSymCipherSpec pCipherSpec,
                        final Cipher pCipher) {
            super(pFactory, pCipherSpec, pCipher);
        }
    }

    /**
     * JcaStreamAADCipher.
     */
    public static class JcaStreamAADCipher
            extends JcaAADCipher<GordianStreamKeySpec>
            implements GordianStreamAADCipher {
        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pCipherSpec the cipherSpec
         * @param pCipher the cipher
         */
        JcaStreamAADCipher(final JcaFactory pFactory,
                           final GordianStreamCipherSpec pCipherSpec,
                           final Cipher pCipher) {
            super(pFactory, pCipherSpec, pCipher);
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
        if (!(pThat instanceof JcaAADCipher)) {
            return false;
        }
        final JcaAADCipher<?> myThat = (JcaAADCipher<?>) pThat;

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
