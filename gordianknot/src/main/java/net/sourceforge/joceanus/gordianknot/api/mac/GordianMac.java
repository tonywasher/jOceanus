/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.mac;

import net.sourceforge.joceanus.gordianknot.api.base.GordianConsumer;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;

/**
 * GordianKnot interface for Message Authentication Codes.
 */
public interface GordianMac
        extends GordianConsumer {
    /**
     * Obtain MacSpec.
     * @return the MacSpec
     */
    GordianMacSpec getMacSpec();

    /**
     * Obtain the key.
     * @return the key
     */
    GordianKey<GordianMacSpec> getKey();

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    byte[] getInitVector();

    /**
     * Obtain the MAC size.
     * @return the MAC size
     */
    int getMacSize();

    /**
     * Initialise the MAC with the given parameters.
     * @param pParams the parameters
     * @throws GordianException on error
     */
    void init(GordianMacParameters pParams) throws GordianException;

    /**
     * Init with bytes as key.
     * @param pKeyBytes the bytes to use
     * @throws GordianException on error
     */
    void initKeyBytes(byte[] pKeyBytes) throws GordianException;

    /**
     * Calculate the MAC.
     * @return the MAC
     */
    byte[] finish();

    /**
     * Calculate the MAC, and return it in the buffer provided.
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @return the number of bytes placed into buffer
     * @throws GordianException on error
     */
    int finish(byte[] pBuffer,
               int pOffset) throws GordianException;

    /**
     * Update the MAC, calculate and reset it.
     * @param pBytes the bytes to update with.
     * @return the MAC
     * @throws GordianException on error
     */
    default byte[] finish(final byte[] pBytes) throws GordianException {
        update(pBytes);
        return finish();
    }
}
