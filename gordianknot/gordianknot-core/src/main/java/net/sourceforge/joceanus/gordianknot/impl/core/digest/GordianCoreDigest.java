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
package net.sourceforge.joceanus.gordianknot.impl.core.digest;


import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Core Digest implementation.
 */
public abstract class GordianCoreDigest
        implements GordianDigest {
    /**
     * DigestSpec.
     */
    private final GordianDigestSpec theDigestSpec;

    /**
     * Constructor.
     * @param pDigestSpec the digestSpec
     */
    protected GordianCoreDigest(final GordianDigestSpec pDigestSpec) {
        theDigestSpec = pDigestSpec;
    }

    @Override
    public GordianDigestSpec getDigestSpec() {
        return theDigestSpec;
    }

    @Override
    public int getDigestSize() {
        return theDigestSpec.getDigestLength().getByteLength();
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        /* Check that the buffers are sufficient */
        final int myInBufLen = pBytes == null ? 0 : pBytes.length;
        if (myInBufLen < (pLength + pOffset)) {
            throw new IllegalArgumentException("Input buffer too short.");
        }

        /* Process the bytes */
        if (pLength != 0) {
            doUpdate(pBytes, pOffset, pLength);
        }
    }

    /**
     * Update the digest with a portion of a byte array.
     * @param pBytes the bytes to update with.
     * @param pOffset the offset of the data within the byte array
     * @param pLength the length of the data to use
     */
    public abstract void doUpdate(byte[] pBytes,
                                  int pOffset,
                                  int pLength);

    @Override
    public int finish(final byte[] pBuffer,
                      final int pOffset) throws OceanusException {
        /* Check that the buffers are sufficient */
        if (pBuffer.length < (getDigestSize() + pOffset)) {
            throw new IllegalArgumentException("Output buffer too short.");
        }

        /* Finish the digest */
        return doFinish(pBuffer, pOffset);
    }

    /**
     * Calculate the Digest, and return it in the buffer provided.
     * @param pBuffer the buffer to return the digest in.
     * @param pOffset the offset in the buffer to store the digest.
     * @return the number of bytes placed into buffer
     * @throws OceanusException on error
     */
    public abstract int doFinish(byte[] pBuffer,
                                 int pOffset) throws OceanusException;
}
