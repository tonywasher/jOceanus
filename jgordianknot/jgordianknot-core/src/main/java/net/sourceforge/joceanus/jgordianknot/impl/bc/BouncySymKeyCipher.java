/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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

import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC5Parameters;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Cipher for BouncyCastle Symmetric Ciphers.
 */
public final class BouncySymKeyCipher
        extends GordianCoreCipher<GordianSymKeySpec>
        implements GordianSymCipher {
    /**
     * Cipher.
     */
    private final BufferedBlockCipher theCipher;

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
    protected BouncySymKeyCipher(final BouncyFactory pFactory,
                                 final GordianSymCipherSpec pCipherSpec,
                                 final BufferedBlockCipher pCipher) {
        super(pFactory, pCipherSpec);
        theCipher = pCipher;
    }

    @Override
    public BouncyKey<GordianSymKeySpec> getKey() {
        return (BouncyKey<GordianSymKeySpec>) super.getKey();
    }

    @Override
    public GordianSymCipherSpec getCipherSpec() {
        return (GordianSymCipherSpec) super.getCipherSpec();
    }

    @Override
    public void init(final boolean pEncrypt,
                     final GordianCipherParameters pParams) throws OceanusException {
        /* Process the parameters and access the key */
        processParameters(pParams);
        final BouncyKey<GordianSymKeySpec> myKey = BouncyKey.accessKey(getKey());

        /* Initialise the cipher */
        final CipherParameters myParms = generateParameters(myKey, getInitVector());
        theCipher.init(pEncrypt, myParms);
        isEncrypting = pEncrypt;
    }

    /**
     * Generate CipherParameters.
     * @param pKey the key
     * @param pIV the initVector
     * @return the parameters
     */
    private static CipherParameters generateParameters(final BouncyKey<GordianSymKeySpec> pKey,
                                                       final byte[] pIV) {
        /* Default parameter */
        CipherParameters myParams = new KeyParameter(pKey.getKey());

        /* Build Key parameter */
        final GordianSymKeySpec myKeySpec = pKey.getKeyType();
        if (myKeySpec != null) {
            final GordianSymKeyType myType = myKeySpec.getSymKeyType();
            if (GordianSymKeyType.RC5.equals(myType)) {
                myParams = new RC5Parameters(pKey.getKey(), GordianCoreFactory.RC5_ROUNDS);
            }
        }

        /* Handle IV */
        if (pIV != null) {
            myParams = new ParametersWithIV(myParams, pIV);
        }

        /* Return the parameters */
        return myParams;
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
            return theCipher.processBytes(pBytes, pOffset, pLength, pOutput, pOutOffset);

            /* Handle exceptions */
        } catch (DataLengthException
                | IllegalStateException e) {
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
        } catch (DataLengthException
                | IllegalStateException
                | InvalidCipherTextException e) {
            throw new GordianCryptoException("Failed to finish operation", e);
        }
    }

    @Override
    public int getBlockSize() {
        return getCipherSpec().getCipherMode().hasPadding()
               ? theCipher.getBlockSize() : 0;
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
        if (!(pThat instanceof BouncySymKeyCipher)) {
            return false;
        }
        final BouncySymKeyCipher myThat = (BouncySymKeyCipher) pThat;

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
