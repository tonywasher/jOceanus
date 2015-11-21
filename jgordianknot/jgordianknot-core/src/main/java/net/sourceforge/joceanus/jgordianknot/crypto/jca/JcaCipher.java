/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
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

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     * @param pPadding is the cipher padded true/false?
     * @param pCipher the cipher
     */
    protected JcaCipher(final JcaFactory pFactory,
                        final T pKeyType,
                        final GordianCipherMode pMode,
                        final boolean pPadding,
                        final Cipher pCipher) {
        super(pFactory, pKeyType, pMode, pPadding);
        theCipher = pCipher;
    }

    @Override
    public JcaKey<T> getKey() {
        return (JcaKey<T>) super.getKey();
    }

    @Override
    public void initCipher(final GordianKey<T> pKey) throws JOceanusException {
        /* Determine the required length of IV */
        int myLen = getIVLength();
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
        T myType = getKeyType();
        return myType instanceof GordianStreamKeyType
                                                      ? ((GordianStreamKeyType) myType).getIVLength()
                                                      : theCipher.getBlockSize();
    }

    @Override
    public void initCipher(final GordianKey<T> pKey,
                           final byte[] pIV,
                           final boolean pEncrypt) throws JOceanusException {
        /* Access and validate the key */
        JcaKey<T> myJcaKey = JcaKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Access details */
        int myMode = pEncrypt
                              ? Cipher.ENCRYPT_MODE
                              : Cipher.DECRYPT_MODE;
        SecretKey myKey = myJcaKey.getKey();

        /* Protect against exceptions */
        try {
            /* Initialise as required */
            if (pIV != null) {
                AlgorithmParameterSpec myParms = new IvParameterSpec(pIV);
                theCipher.init(myMode, myKey, myParms);
            } else {
                theCipher.init(myMode, myKey);
            }
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
                      final int pOutOffset) throws JOceanusException {
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
                      final int pOutOffset) throws JOceanusException {
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
