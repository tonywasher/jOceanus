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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAADCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for BouncyCastle AAD Symmetric Ciphers.
 */
public class BouncyAADCipher
        extends GordianAADCipher {
    /**
     * Cipher.
     */
    private final AEADBlockCipher theCipher;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     * @param pCipher the cipher
     */
    protected BouncyAADCipher(final BouncyFactory pFactory,
                              final GordianSymKeyType pKeyType,
                              final GordianCipherMode pMode,
                              final AEADBlockCipher pCipher) {
        super(pFactory, pKeyType, pMode);
        theCipher = pCipher;
    }

    @Override
    public BouncyKey<GordianSymKeyType> getKey() {
        return (BouncyKey<GordianSymKeyType>) super.getKey();
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
        BouncyKey<GordianSymKeyType> myKey = BouncyKey.accessKey(pKey);
        checkValidKey(pKey);

        /* Initialise the cipher */
        CipherParameters myParms = new KeyParameter(myKey.getKey());
        myParms = new ParametersWithIV(myParms, pIV);
        theCipher.init(pEncrypt, myParms);

        /* Store key and initVector */
        setKey(pKey);
        setInitVector(pIV);
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
            return theCipher.processBytes(pBytes, pOffset, pLength, pOutput, pOutOffset);

            /* Handle exceptions */
        } catch (DataLengthException
                | IllegalStateException e) {
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
            theCipher.processAADBytes(pBytes, pOffset, pLength);

            /* Handle exceptions */
        } catch (DataLengthException
                | IllegalStateException e) {
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
        } catch (DataLengthException
                | IllegalStateException
                | InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to finish operation", e);
        }
    }
}