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
package io.github.tonywasher.joceanus.gordianknot.api.mac;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;

/**
 * GordianKnot interface for Message Authentication Codes.
 */
public interface GordianMac {
    /**
     * Obtain MacSpec.
     *
     * @return the MacSpec
     */
    GordianMacSpec getMacSpec();

    /**
     * Obtain the key.
     *
     * @return the key
     */
    GordianKey<GordianMacSpec> getKey();

    /**
     * Obtain the keyType.
     *
     * @return the keyType
     */
    byte[] getInitVector();

    /**
     * Obtain the MAC size.
     *
     * @return the MAC size
     */
    int getMacSize();

    /**
     * Initialise the MAC with the given parameters.
     *
     * @param pParams the parameters
     * @throws GordianException on error
     */
    void init(GordianMacParams pParams) throws GordianException;

    /**
     * Init with bytes as key.
     *
     * @param pKeyBytes the bytes to use
     * @throws GordianException on error
     */
    void initKeyBytes(byte[] pKeyBytes) throws GordianException;

    /**
     * Update the consumer with a portion of a byte array.
     *
     * @param pBytes  the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     * @throws GordianException on error
     */
    void update(byte[] pBytes,
                int pOffset,
                int pLength) throws GordianException;

    /**
     * Update the consumer with a single byte.
     *
     * @param pByte the byte to update with.
     * @throws GordianException on error
     */
    void update(byte pByte) throws GordianException;

    /**
     * Update the consumer with a byte array.
     *
     * @param pBytes the bytes to update with.
     * @throws GordianException on error
     */
    default void update(final byte[] pBytes) throws GordianException {
        if (pBytes != null) {
            update(pBytes, 0, pBytes.length);
        }
    }

    /**
     * Reset the Consumer.
     *
     * @throws GordianException on error
     */
    void reset() throws GordianException;

    /**
     * Calculate the MAC.
     *
     * @return the MAC
     * @throws GordianException on error
     */
    byte[] finish() throws GordianException;

    /**
     * Calculate the MAC, and return it in the buffer provided.
     *
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @return the number of bytes placed into buffer
     * @throws GordianException on error
     */
    int finish(byte[] pBuffer,
               int pOffset) throws GordianException;

    /**
     * Update the MAC, calculate and reset it.
     *
     * @param pBytes the bytes to update with.
     * @return the MAC
     * @throws GordianException on error
     */
    default byte[] finish(final byte[] pBytes) throws GordianException {
        update(pBytes);
        return finish();
    }
}
