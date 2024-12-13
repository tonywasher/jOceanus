/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianKeyedCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSparkleKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Objects;

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
    private final GordianCoreCipherParameters<T> theParameters;

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

    @Override
    public T getKeyType() {
        return theCipherSpec.getKeyType();
    }

    @Override
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
    public void initForEncrypt(final GordianCipherParameters pParams) throws GordianException {
        init(true, pParams);
    }

    @Override
    public void initForDecrypt(final GordianCipherParameters pParams) throws GordianException {
        init(false, pParams);
    }

    /**
     * Initialise the cipher for encryption or decryption.
     * @param pEncrypt true/false
     * @param pParams the parameters
     * @throws GordianException on error
     */
    public abstract void init(boolean pEncrypt,
                              GordianCipherParameters pParams) throws GordianException;

    /**
     * Init with bytes as key.
     * @param pKeyBytes the bytes to use
     * @throws GordianException on error
     */
    public void initKeyBytes(final byte[] pKeyBytes) throws GordianException {
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
     * @throws GordianException on error
     */
    protected void processParameters(final GordianCipherParameters pParams) throws GordianException {
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
            final GordianStreamKeySpec mySpec = ((GordianStreamCipherSpec) theCipherSpec).getKeyType();
            if (GordianStreamKeyType.SPARKLE.equals(mySpec.getStreamKeyType())) {
                final GordianSparkleKey myKeyType = (GordianSparkleKey) mySpec.getSubKeyType();
                switch (myKeyType) {
                    case SPARKLE256_256:
                        return GordianLength.LEN_256.getLength();
                    case SPARKLE192_192:
                        return GordianLength.LEN_192.getLength();
                    default:
                        return GordianLength.LEN_128.getLength();
                }
            }
            return GordianLength.LEN_128.getLength();
        }

        /* No Mac */
        return 0;
    }

    @Override
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput,
                      final int pOutOffset) throws GordianException {
        /* Make sure that there is no overlap between buffers */
        byte[] myInput = pBytes;
        int myOffset = pOffset;
        if (check4UpdateOverLap(pBytes, pOffset, pLength, pOutput, pOutOffset)) {
            myInput = new byte[pLength];
            myOffset = 0;
            System.arraycopy(pBytes, pOffset, myInput, myOffset, pLength);
        }

        /* process the bytes */
        return doUpdate(myInput, myOffset, pLength, pOutput, pOutOffset);
    }

    /**
     * Perform update operation.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws GordianException on error
     */
    public abstract int doUpdate(byte[] pBytes,
                                 int pOffset,
                                 int pLength,
                                 byte[] pOutput,
                                 int pOutOffset) throws GordianException;

    /**
     * Check for buffer overlap in update.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return is there overlap between the two buffers? true/false overlap
     * @throws GordianException on error
     */
    public boolean check4UpdateOverLap(final byte[] pBytes,
                                       final int pOffset,
                                       final int pLength,
                                       final byte[] pOutput,
                                       final int pOutOffset) throws GordianException {
        /* Check that the buffers are sufficient */
        final int myInBufLen = pBytes == null ? 0 : pBytes.length;
        if (myInBufLen < (pLength + pOffset)) {
            throw new GordianLogicException("Input buffer too short.");
        }
        final int myOutBufLen = pOutput == null ? 0 : pOutput.length;
        if (myOutBufLen < (getOutputLength(pLength) + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* Only relevant when the two buffers are the same */
        if (pBytes != pOutput) {
            return false;
        }

        /* Check for overlap */
        return pOutOffset < pOffset + pLength
            && pOffset < pOutOffset + getOutputLength(pLength);
    }

    @Override
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws GordianException {
        /* Check that the buffers are sufficient */
        final int myOutBufLen = pOutput == null ? 0 : pOutput.length;
        if (myOutBufLen < (getOutputLength(0) + pOutOffset)) {
            throw new GordianLogicException("Output buffer too short.");
        }

        /* finish the cipher */
        return doFinish(pOutput, pOutOffset);
    }

    /**
     * Complete the Cipher operation and return final results.
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws GordianException on error
     */
    public abstract int doFinish(byte[] pOutput,
                                 int pOutOffset) throws GordianException;

    /**
     * Check that the key matches the keyType.
     * @param pKey the passed key.
     * @throws GordianException on error
     */
    void checkValidKey(final GordianKey<T> pKey) throws GordianException {
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
