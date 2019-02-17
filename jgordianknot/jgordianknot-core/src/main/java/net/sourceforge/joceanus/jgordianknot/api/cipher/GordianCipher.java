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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for Cipher.
 * @param <T> the keyType
 */
public interface GordianCipher<T extends GordianKeySpec> {
    /**
     * Obtain the keyType.
     * @return the keyType
     */
    T getKeyType();

    /**
     * Obtain the cipherSpec.
     * @return the spec
     */
    GordianCipherSpec<T> getCipherSpec();

    /**
     * Is the cipher restricted?
     * @return true/false
     */
    boolean isRestricted();

    /**
     * Obtain the key.
     * @return the key
     */
    GordianKey<T> getKey();

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    byte[] getInitVector();

    /**
     * Initialise the cipher for encryption with random IV.
     * @param pKey the key
     * @throws OceanusException on error
     */
    void initCipher(GordianKey<T> pKey) throws OceanusException;

    /**
     * Initialise the cipher.
     * @param pKey the key
     * @param pIV the initialisation vector
     * @param pEncrypt true/false
     * @throws OceanusException on error
     */
    void initCipher(GordianKey<T> pKey,
                    byte[] pIV,
                    boolean pEncrypt) throws OceanusException;

    /**
     * Determine the maximum number of output bytes that will be produced for the given number of
     * input bytes.
     * @param pLength the number of input bytes
     * @return # of output bytes
     */
    int getOutputLength(int pLength);

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @return the intermediate processed data
     * @throws OceanusException on error
     */
    default byte[] update(final byte[] pBytes) throws OceanusException {
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
    default byte[] update(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = update(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
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
    default int update(final byte[] pBytes,
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
    int update(byte[] pBytes,
               int pOffset,
               int pLength,
               byte[] pOutput,
               int pOutOffset) throws OceanusException;

    /**
     * Complete the Cipher operation and return final results.
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    default byte[] finish() throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(0);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    default byte[] finish(final byte[] pBytes) throws OceanusException {
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
    default byte[] finish(final byte[] pBytes,
                          final int pOffset,
                          final int pLength) throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
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
    default int finish(final byte[] pBytes,
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
    default int finish(final byte[] pBytes,
                       final int pOffset,
                       final int pLength,
                       final byte[] pOutput,
                       final int pOutOffset) throws OceanusException {
        /* Update the data */
        final int myLen = update(pBytes, pOffset, pLength, pOutput, pOutOffset);

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
    int finish(byte[] pOutput,
               int pOutOffset) throws OceanusException;
}

