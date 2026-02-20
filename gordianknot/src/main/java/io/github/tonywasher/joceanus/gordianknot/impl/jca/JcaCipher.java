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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.RC2ParameterSpec;
import javax.crypto.spec.RC5ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;

/**
 * Cipher for JCA BouncyCastle Ciphers.
 *
 * @param <T> the key Type
 */
public abstract class JcaCipher<T extends GordianKeySpec>
        extends GordianCoreCipher<T> {
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
     *
     * @param pFactory    the Security Factory
     * @param pCipherSpec the cipherSpec
     * @param pCipher     the cipher
     */
    JcaCipher(final GordianBaseFactory pFactory,
              final GordianNewCipherSpec<T> pCipherSpec,
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
                     final GordianCipherParameters pParams) throws GordianException {
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
            final boolean isRC5 = myKeyType instanceof GordianNewSymKeySpec
                    && GordianNewSymKeyType.RC5.equals(((GordianNewSymKeySpec) myKeyType).getSymKeyType());

            /* Initialise as required */
            if (myIV != null || isRC5) {
                final AlgorithmParameterSpec myParms = generateParameters(myJcaKey, myIV);
                theCipher.init(myMode, myKey, myParms);
            } else {
                theCipher.init(myMode, myKey);
            }
            isEncrypting = pEncrypt;
        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to initialise cipher", e);
        }
    }

    /**
     * Generate AlgorithmParameters.
     *
     * @param pKey the key
     * @param pIV  the Initialisation vector
     * @return the parameters
     */
    static AlgorithmParameterSpec generateParameters(final JcaKey<?> pKey,
                                                     final byte[] pIV) {
        final Object myKeyType = pKey.getKeyType();
        if (myKeyType instanceof GordianNewSymKeySpec) {
            final GordianNewSymKeySpec mySpec = (GordianNewSymKeySpec) myKeyType;
            final GordianNewSymKeyType myType = mySpec.getSymKeyType();
            final GordianLength myLen = mySpec.getBlockLength();
            if (GordianNewSymKeyType.RC2.equals(myType)) {
                return new RC2ParameterSpec(pKey.getKeyBytes().length * Byte.SIZE, pIV);
            }
            if (GordianNewSymKeyType.RC5.equals(myType)) {
                return pIV == null
                        ? new RC5ParameterSpec(1, GordianBaseData.RC5_ROUNDS, myLen.getLength() >> 1)
                        : new RC5ParameterSpec(1, GordianBaseData.RC5_ROUNDS, myLen.getLength() >> 1, pIV);
            }
        }
        return new IvParameterSpec(pIV);
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
            return theCipher.update(pBytes, pOffset, pLength, pOutput, pOutOffset);

            /* Handle exceptions */
        } catch (ShortBufferException e) {
            throw new GordianCryptoException("Failed to process bytes", e);
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
        } catch (ShortBufferException
                 | IllegalBlockSizeException
                 | BadPaddingException e) {
            throw new GordianCryptoException("Failed to finish operation", e);
        }
    }

    @Override
    public int getBlockSize() {
        final GordianNewCipherSpec<T> mySpec = getCipherSpec();
        return (mySpec instanceof GordianCoreSymCipherSpec
                && ((GordianCoreSymCipherSpec) mySpec).getCoreCipherMode().hasPadding())
                ? theCipher.getBlockSize() : 0;
    }

    /**
     * JcaSymCipher.
     */
    public static class JcaSymCipher
            extends JcaCipher<GordianNewSymKeySpec>
            implements GordianSymCipher {
        /**
         * Constructor.
         *
         * @param pFactory    the Security Factory
         * @param pCipherSpec the cipherSpec
         * @param pCipher     the cipher
         */
        JcaSymCipher(final GordianBaseFactory pFactory,
                     final GordianNewSymCipherSpec pCipherSpec,
                     final Cipher pCipher) {
            super(pFactory, pCipherSpec, pCipher);
        }
    }

    /**
     * JcaStreamCipher.
     */
    public static class JcaStreamCipher
            extends JcaCipher<GordianNewStreamKeySpec>
            implements GordianStreamCipher {
        /**
         * Constructor.
         *
         * @param pFactory    the Security Factory
         * @param pCipherSpec the cipherSpec
         * @param pCipher     the cipher
         */
        JcaStreamCipher(final GordianBaseFactory pFactory,
                        final GordianNewStreamCipherSpec pCipherSpec,
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
        if (!(pThat instanceof JcaCipher)) {
            return false;
        }
        final JcaCipher<?> myThat = (JcaCipher<?>) pThat;

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
