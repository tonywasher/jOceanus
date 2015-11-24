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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for Message Digests.
 */
public interface GordianDigest {
    /**
     * Obtain DigestType.
     * @return the digest type
     */
    GordianDigestType getDigestType();

    /**
     * Obtain the digest size.
     * @return the digest size
     */
    int getDigestSize();

    /**
     * Update the digest with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    void update(final byte[] pBytes,
                final int pOffset,
                final int pLength);

    /**
     * Update the digest with a single byte.
     * @param pByte the byte to update with.
     */
    void update(final byte pByte);

    /**
     * Update the digest with a byte array.
     * @param pBytes the bytes to update with.
     */
    void update(final byte[] pBytes);

    /**
     * Reset the digest.
     */
    void reset();

    /**
     * Calculate the digest.
     * @return the digest
     */
    byte[] finish();

    /**
     * Calculate the Digest, and return it in the buffer provided.
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @return the number of bytes placed into buffer
     * @throws OceanusException on error
     */
    int finish(final byte[] pBuffer,
               final int pOffset) throws OceanusException;

    /**
     * Update the digest, calculate and reset it.
     * @param pBytes the bytes to update with.
     * @return the digest
     */
    default byte[] finish(final byte[] pBytes) {
        update(pBytes);
        return finish();
    }
}
