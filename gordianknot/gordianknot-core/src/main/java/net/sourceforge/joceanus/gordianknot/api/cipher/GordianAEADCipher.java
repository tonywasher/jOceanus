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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * GordianKnot base for AEAD Cipher.
 */
public interface GordianAEADCipher {
    /**
     * Process the passed AEAD data.
     * @param pBytes AEAD Bytes to update cipher with
     * @throws OceanusException on error
     */
    default void updateAAD(final byte[] pBytes) throws OceanusException {
        updateAAD(pBytes, 0, pBytes.length);
    }

    /**
     * Process the passed AEAD data.
     * @param pBytes AEAD Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @throws OceanusException on error
     */
    void updateAAD(byte[] pBytes,
                   int pOffset,
                   int pLength) throws OceanusException;
}
