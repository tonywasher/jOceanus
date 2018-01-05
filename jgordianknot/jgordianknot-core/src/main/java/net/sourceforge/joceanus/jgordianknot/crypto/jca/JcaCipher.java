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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

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

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for JCA BouncyCastle Ciphers.
 * @param <T> the key Type
 */
public final class JcaCipher<T>
        extends GordianCipher<T> {
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
    protected JcaCipher(final JcaFactory pFactory,
                        final GordianCipherSpec<T> pCipherSpec,
                        final Cipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public JcaKey<T> getKey() {
        return (JcaKey<T>) super.getKey();
    }

    /**
     * Do we need an IV?
     * @return true/false
     */
    private boolean needsIV() {
        return getCipherSpec().needsIV();
    }

    @Override
    public void initCipher(final GordianKey<T> pKey) throws OceanusException {
        /* Determine the required length of IV */
        final int myLen = getIVLength();
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

    /**
     * Obtain IV length.
     * @return the IV length
     */
    private int getIVLength() {
        final T myType = getKeyType();
        if (myType instanceof GordianStreamKeyType) {
            return ((GordianStreamKeyType) myType).getIVLength();
        }
        return needsIV()
                         ? getCipherSpec().getIVLength()
                         : 0;
    }

    @Override
    public void initCipher(final GordianKey<T> pKey,
                           final byte[] pIV,
                           final boolean pEncrypt) throws OceanusException {
        /* Access and validate the key */
        final JcaKey<T> myJcaKey = JcaKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Access details */
        final int myMode = pEncrypt
                                    ? Cipher.ENCRYPT_MODE
                                    : Cipher.DECRYPT_MODE;
        final SecretKey myKey = myJcaKey.getKey();

        /* Protect against exceptions */
        try {
            /* Initialise as required */
            if (pIV != null) {
                final AlgorithmParameterSpec myParms = generateParameters(myJcaKey, pIV);
                theCipher.init(myMode, myKey, myParms);
            } else {
                theCipher.init(myMode, myKey);
            }
        } catch (InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException("Failed to initialise cipher", e);
        }

        /* Store key and initVector */
        setKey(pKey);
        setInitVector(pIV != null
                                  ? pIV
                                  : null);
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
                return new RC5ParameterSpec(1, GordianFactory.RC5_ROUNDS, myLen.getLength() >> 1, pIV);
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
}
