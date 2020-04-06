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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core Cipher implementation.
 * @param <T> the keyType
 */
public abstract class GordianCoreCipher<T extends GordianKeySpec>
    implements GordianKeyedCipher<T> {
    /**
     * CipherSpec.
     */
    private final GordianCipherSpec<T> theCipherSpec;

    /**
     * The Random Generator.
     */
    private final GordianRandomSource theRandom;

    /**
     * Parameters.
     */
    private GordianCoreCipherParameters<T> theParameters;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipherSpec the cipherSpec
     */
    protected GordianCoreCipher(final GordianCoreFactory pFactory,
                                final GordianCipherSpec<T> pCipherSpec) {
        theCipherSpec = pCipherSpec;
        theRandom = pFactory.getRandomSource();
        theParameters = new GordianCoreCipherParameters<>(pFactory, theCipherSpec);
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public T getKeyType() {
        return theCipherSpec.getKeyType();
    }

    /**
     * Obtain the cipherSpec.
     * @return the mode
     */
    public GordianCipherSpec<T> getCipherSpec() {
        return theCipherSpec;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    public SecureRandom getRandom() {
        return theRandom.getRandom();
    }

    /**
     * Obtain the keyLength.
     * @return the keyLength
     */
    public GordianLength getKeyLength() {
        return getKeyType().getKeyLength();
    }

    /**
     * Obtain the blockSize.
     * @return true/false
     */
    public abstract int getBlockSize();

    /**
     * Obtain the key.
     * @return the key
     */
    public GordianKey<T> getKey() {
        return theParameters.getKey();
    }

    @Override
    public byte[] getInitVector() {
        return theParameters.getInitVector();
    }

    @Override
    public byte[] getInitialAEAD() {
        return theParameters.getInitialAEAD();
    }

    @Override
    public byte[] getPBESalt() {
        return theParameters.getPBESalt();
    }

    @Override
    public GordianPBESpec getPBESpec() {
        return theParameters.getPBESpec();
    }

    @Override
    public void initForEncrypt(final GordianCipherParameters pParams) throws OceanusException {
        init(true, pParams);
    }

    @Override
    public void initForDecrypt(final GordianCipherParameters pParams) throws OceanusException {
        init(false, pParams);
    }

    /**
     * Initialise the cipher for encryption or decryption.
     * @param pEncrypt true/false
     * @param pParams the parameters
     * @throws OceanusException on error
     */
    public abstract void init(boolean pEncrypt,
                              GordianCipherParameters pParams) throws OceanusException;

    /**
     * Init with bytes as key.
     * @param pKeyBytes the bytes to use
     * @throws OceanusException on error
     */
    public void initKeyBytes(final byte[] pKeyBytes) throws OceanusException {
        /* Check that the key length is correct */
        if (getKeyLength().getByteLength() != pKeyBytes.length) {
            throw new GordianLogicException("incorrect keyLength");
        }

        /* Create the key and initialise */
        final GordianKey<T> myKey = theParameters.buildKeyFromBytes(pKeyBytes);
        initForEncrypt(GordianCipherParameters.key(myKey));
    }

    /**
     * Process cipherParameters.
     * @param pParams the cipher parameters
     * @throws OceanusException on error
     */
    protected void processParameters(final GordianCipherParameters pParams) throws OceanusException {
        /* Process the parameters */
        theParameters.processParameters(pParams);
        checkValidKey(getKey());
    }

    /**
     * Obtain AEAD MacSize.
     * @return the MacSize
     */
    protected int getAEADMacSize() {
        /* SymCipher depends on BlockSize */
        if (theCipherSpec instanceof GordianSymCipherSpec) {
            final GordianSymCipherSpec mySymSpec = (GordianSymCipherSpec) theCipherSpec;
            final GordianLength myBlkLen = mySymSpec.getBlockLength();

            /* Switch on cipher Mode */
            switch (mySymSpec.getCipherMode()) {
                case CCM:
                case EAX:
                    return myBlkLen.getLength() / 2;
                case KCCM:
                case KGCM:
                case GCM:
                case OCB:
                    return myBlkLen.getLength();
                default:
                    return 0;
            }

            /* Stream Cipher uses Poly1305 */
        } else if (theCipherSpec instanceof GordianStreamCipherSpec) {
            return GordianLength.LEN_128.getLength();
        }

        /* No Mac */
        return 0;
    }

    /**
     * Check that the key matches the keyType.
     * @param pKey the passed key.
     * @throws OceanusException on error
     */
    void checkValidKey(final GordianKey<T> pKey) throws OceanusException {
        if (!getKeyType().equals(pKey.getKeyType())) {
            throw new GordianLogicException("MisMatch on keyType");
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
        if (!(pThat instanceof GordianCoreCipher)) {
            return false;
        }
        final GordianCoreCipher<?> myThat = (GordianCoreCipher<?>) pThat;

        /* Check that the fields are equal */
        return Objects.equals(theCipherSpec, myThat.getCipherSpec())
                && Objects.equals(getKey(), myThat.getKey())
                && Arrays.equals(getInitVector(), myThat.getInitVector());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getCipherSpec())
                + Arrays.hashCode(getInitVector());
    }
}
