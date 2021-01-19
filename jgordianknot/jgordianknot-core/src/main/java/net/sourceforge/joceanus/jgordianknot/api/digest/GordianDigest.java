/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.digest;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianConsumer;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot interface for Message Digests.
 */
public interface GordianDigest
        extends GordianConsumer {
    /**
     * Obtain DigestSpec.
     * @return the digestSpec
     */
    GordianDigestSpec getDigestSpec();

    /**
     * Obtain the digest size.
     * @return the digest size
     */
    int getDigestSize();

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
    int finish(byte[] pBuffer,
               int pOffset) throws OceanusException;

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
