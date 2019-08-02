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
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for JCA BouncyCastle Ciphers.
 * @param <T> the key Type
 */
public abstract class JcaCipher<T extends GordianKeySpec>
        extends GordianCoreCipher<T> {
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
    JcaCipher(final JcaFactory pFactory,
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
        final byte[] myIV = getInitVector();

        /* Protect against exceptions */
        try {
            /* Careful of RC5 */
            final T myKeyType = myJcaKey.getKeyType();
            final boolean isRC5 = myKeyType instanceof GordianSymKeySpec
                    && GordianSymKeyType.RC5.equals(((GordianSymKeySpec) myKeyType).getSymKeyType());

            /* Initialise as required */
            if (myIV != null || isRC5) {
                final AlgorithmParameterSpec myParms = generateParameters(myJcaKey, myIV);
                theCipher.init(myMode, myKey, myParms);
            } else {
                theCipher.init(myMode, myKey);
            }
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to initialise cipher", e);
        }
    }

    /**
     * Generate AlgorithmParameters.
     * @param pKey the key
     * @param pIV the Initialisation vector
     * @return the parameters
     */
    static AlgorithmParameterSpec generateParameters(final JcaKey<?> pKey,
                                                     final byte[] pIV) {
        final Object myKeyType = pKey.getKeyType();
        if (myKeyType instanceof GordianSymKeySpec) {
            final GordianSymKeySpec mySpec = (GordianSymKeySpec) myKeyType;
            final GordianSymKeyType myType = mySpec.getSymKeyType();
            final GordianLength myLen = mySpec.getBlockLength();
            if (GordianSymKeyType.RC2.equals(myType)) {
                return new RC2ParameterSpec(pKey.getKeyBytes().length * Byte.SIZE, pIV);
            }
            if (GordianSymKeyType.RC5.equals(myType)) {
                return pIV == null
                       ? new RC5ParameterSpec(1, GordianCoreFactory.RC5_ROUNDS, myLen.getLength() >> 1)
                       : new RC5ParameterSpec(1, GordianCoreFactory.RC5_ROUNDS, myLen.getLength() >> 1, pIV);
            }
        }
        return new IvParameterSpec(pIV);
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
        final GordianCipherSpec<T> mySpec = getCipherSpec();
        return (mySpec instanceof GordianSymCipherSpec
                        && ((GordianSymCipherSpec) mySpec).getCipherMode().hasPadding())
               ? theCipher.getBlockSize() : 0;
    }

    /**
     * JcaSymCipher.
     */
    public static class JcaSymCipher
            extends JcaCipher<GordianSymKeySpec>
            implements GordianSymCipher {
        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pCipherSpec the cipherSpec
         * @param pCipher the cipher
         */
        JcaSymCipher(final JcaFactory pFactory,
                     final GordianSymCipherSpec pCipherSpec,
                     final Cipher pCipher) {
            super(pFactory, pCipherSpec, pCipher);
        }
    }

    /**
     * JcaStreamCipher.
     */
    public static class JcaStreamCipher
            extends JcaCipher<GordianStreamKeySpec>
            implements GordianStreamCipher {
        /**
         * Constructor.
         * @param pFactory the Security Factory
         * @param pCipherSpec the cipherSpec
         * @param pCipher the cipher
         */
        JcaStreamCipher(final JcaFactory pFactory,
                        final GordianStreamCipherSpec pCipherSpec,
                        final Cipher pCipher) {
            super(pFactory, pCipherSpec, pCipher);
        }
    }
}
