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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianLogicException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for Cipher.
 * @param <T> the keyType
 */
public abstract class GordianCipher<T> {
    /**
     * KeyType.
     */
    private final T theKeyType;

    /**
     * Cipher Mode.
     */
    private final GordianCipherMode theMode;

    /**
     * The Random Generator.
     */
    private final SecureRandom theRandom;

    /**
     * IsPadded?
     */
    private final GordianPadding thePadding;

    /**
     * Key.
     */
    private GordianKey<T> theKey;

    /**
     * InitialisationVector.
     */
    private byte[] theInitVector;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     * @param pPadding the padding
     */
    protected GordianCipher(final GordianFactory pFactory,
                            final T pKeyType,
                            final GordianCipherMode pMode,
                            final GordianPadding pPadding) {
        theKeyType = pKeyType;
        theMode = pMode;
        thePadding = pPadding;
        theRandom = pFactory.getRandom();
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public T getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the Mode.
     * @return the mode
     */
    public GordianCipherMode getMode() {
        return theMode;
    }

    /**
     * is the cipher padded?
     * @return true/false
     */
    public GordianPadding getPadding() {
        return thePadding;
    }

    /**
     * Obtain random generator.
     * @return the generator
     */
    protected SecureRandom getRandom() {
        return theRandom;
    }

    /**
     * Obtain the key.
     * @return the key
     */
    public GordianKey<T> getKey() {
        return theKey;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public byte[] getInitVector() {
        return theInitVector;
    }

    /**
     * Store key.
     * @param pKey the key
     */
    protected void setKey(final GordianKey<T> pKey) {
        theKey = pKey;
    }

    /**
     * Store initVector.
     * @param pInitVector the initVector
     */
    protected void setInitVector(final byte[] pInitVector) {
        theInitVector = pInitVector;
    }

    /**
     * Check that the key matches the keyType.
     * @param pKey the passed key.
     * @throws OceanusException on error
     */
    protected void checkValidKey(final GordianKey<T> pKey) throws OceanusException {
        if (!theKeyType.equals(pKey.getKeyType())) {
            throw new GordianLogicException("MisMatch on keyType");
        }
    }

    /**
     * Initialise the cipher for encryption with random IV.
     * @param pKey the key
     * @throws OceanusException on error
     */
    public abstract void initCipher(final GordianKey<T> pKey) throws OceanusException;

    /**
     * Initialise the cipher.
     * @param pKey the key
     * @param pIV the initialisation vector
     * @param pEncrypt true/false
     * @throws OceanusException on error
     */
    public abstract void initCipher(final GordianKey<T> pKey,
                                    final byte[] pIV,
                                    final boolean pEncrypt) throws OceanusException;

    /**
     * Determine the maximum number of output bytes that will be produced for the given number of
     * input bytes.
     * @param pLength the number of input bytes
     * @return # of output bytes
     */
    public abstract int getOutputLength(final int pLength);

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @return the intermediate processed data
     * @throws OceanusException on error
     */
    public byte[] update(final byte[] pBytes) throws OceanusException {
        return update(pBytes, 0, pBytes.length);
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @return the intermediate processed data
     * @throws OceanusException on error
     */
    public byte[] update(final byte[] pBytes,
                         final int pOffset,
                         final int pLength) throws OceanusException {
        /* Create output buffer */
        int myLen = getOutputLength(pLength);
        byte[] myOutput = new byte[myLen];

        /* Process the data */
        int myOut = update(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full or partial buffer */
        return (myOut == myLen)
                                ? myOutput
                                : Arrays.copyOf(myOutput, myOut);
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput) throws OceanusException {
        return update(pBytes, pOffset, pLength, pOutput, 0);
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    public abstract int update(final byte[] pBytes,
                               final int pOffset,
                               final int pLength,
                               final byte[] pOutput,
                               final int pOutOffset) throws OceanusException;

    /**
     * Complete the Cipher operation and return final results.
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    public byte[] finish() throws OceanusException {
        /* Create output buffer */
        int myLen = getOutputLength(0);
        byte[] myOutput = new byte[myLen];

        /* Process the data */
        int myOut = finish(myOutput, 0);

        /* Return full or partial buffer */
        return (myOut == myLen)
                                ? myOutput
                                : Arrays.copyOf(myOutput, myOut);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    public byte[] finish(final byte[] pBytes) throws OceanusException {
        return finish(pBytes, 0, pBytes.length);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    public byte[] finish(final byte[] pBytes,
                         final int pOffset,
                         final int pLength) throws OceanusException {
        /* Create output buffer */
        int myLen = getOutputLength(pLength);
        byte[] myOutput = new byte[myLen];

        /* Process the data */
        int myOut = finish(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full or partial buffer */
        return (myOut == myLen)
                                ? myOutput
                                : Arrays.copyOf(myOutput, myOut);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    public int finish(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput) throws OceanusException {
        return finish(pBytes, pOffset, pLength, pOutput, 0);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    public int finish(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput,
                      final int pOutOffset) throws OceanusException {
        /* Update the data */
        int myLen = update(pBytes, pOffset, pLength, pOutput, pOutOffset);

        /* Complete the operation */
        return myLen + finish(pOutput, myLen);
    }

    /**
     * Complete the Cipher operation and return final results.
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    public abstract int finish(final byte[] pOutput,
                               final int pOutOffset) throws OceanusException;
}
