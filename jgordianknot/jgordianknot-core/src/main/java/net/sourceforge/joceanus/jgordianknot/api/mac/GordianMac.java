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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
     * Initialise the MAC with KeyBytes and random IV (if needed).
     * @param pKeyBytes the keyBytes
     * @throws OceanusException on error
     */
    void initMac(byte[] pKeyBytes) throws OceanusException;

    /**
     * Initialise the MAC with Key and random IV (if needed).
     * @param pKey the key
     * @throws OceanusException on error
     */
    void initMac(GordianKey<GordianMacSpec> pKey) throws OceanusException;

    /**
     * Initialise with key.
     * @param pKey the key to initialise with
     * @param pIV the initialisation vector (or null)
     * @throws OceanusException on error
     */
    void initMac(GordianKey<GordianMacSpec> pKey,
                 byte[] pIV) throws OceanusException;

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
     * @throws OceanusException on error
     */
    int finish(byte[] pBuffer,
               int pOffset) throws OceanusException;

    /**
     * Update the MAC, calculate and reset it.
     * @param pBytes the bytes to update with.
     * @return the MAC
     */
    default byte[] finish(final byte[] pBytes) {
        update(pBytes);
        return finish();
    }
}
