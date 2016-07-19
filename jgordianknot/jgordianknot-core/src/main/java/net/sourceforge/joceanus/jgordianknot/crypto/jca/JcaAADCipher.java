/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for JCA BouncyCastle AAD Ciphers.
 */
public class JcaAADCipher
        extends GordianAADCipher {
    /**
     * Cipher.
     */
    private final Cipher theCipher;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     * @param pCipher the cipher
     */
    protected JcaAADCipher(final JcaFactory pFactory,
                           final GordianSymKeyType pKeyType,
                           final GordianCipherMode pMode,
                           final Cipher pCipher) {
        super(pFactory, pKeyType, pMode);
        theCipher = pCipher;
    }

    @Override
    public JcaKey<GordianSymKeyType> getKey() {
        return (JcaKey<GordianSymKeyType>) super.getKey();
    }

    @Override
    public void initCipher(final GordianKey<GordianSymKeyType> pKey) throws OceanusException {
        /* Create a random IV */
        byte[] myIV = new byte[AADIVLEN];
        getRandom().nextBytes(myIV);

        /* initialise with this IV */
        initCipher(pKey, myIV, true);
    }

    @Override
    public void initCipher(final GordianKey<GordianSymKeyType> pKey,
                           final byte[] pIV,
                           final boolean pEncrypt) throws OceanusException {
        /* Access and validate the key */
        JcaKey<GordianSymKeyType> myJcaKey = JcaKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Access details */
        int myMode = pEncrypt
                              ? Cipher.ENCRYPT_MODE
                              : Cipher.DECRYPT_MODE;
        SecretKey myKey = myJcaKey.getKey();

        /* Protect against exceptions */
        try {
            /* Initialise as required */
            AlgorithmParameterSpec myParms = new IvParameterSpec(pIV);
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
}
