/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.ExtendedDigest;

import edu.ecrypt.JHDigest;

/**
 * Java implementation of JH Digest.
 * <p>
 * This is a direct port from the JH Reference C implementation, with minimal modifications.
 */
public class BouncyJHDigest
        implements ExtendedDigest {
    /**
     * The underlying ByteLength????.
     */
    private static final int BYTE_LENGTH = 256;

    /**
     * The underlying digest.
     */
    private final JHDigest theDigest;

    /**
     * The digest length.
     */
    private final int theDigestLen;

    /**
     * Constructor.
     * @param pHashBitLen the hash bit length
     */
    public BouncyJHDigest(final int pHashBitLen) {
        theDigest = new JHDigest(pHashBitLen);
        theDigestLen = pHashBitLen / Byte.SIZE;
    }

    @Override
    public int doFinal(final byte[] pHash, final int pOffset) {
        theDigest.Final(pHash, pOffset);
        theDigest.Init();
        return getDigestSize();
    }

    @Override
    public String getAlgorithmName() {
        return "JH";
    }

    @Override
    public int getDigestSize() {
        return theDigestLen;
    }

    @Override
    public void reset() {
        theDigest.Init();
    }

    @Override
    public void update(final byte arg0) {
        final byte[] myByte = new byte[]
        { arg0 };
        update(myByte, 0, 1);
    }

    @Override
    public void update(final byte[] pData, final int pOffset, final int pLength) {
        theDigest.Update(pData, pOffset, pLength * Byte.SIZE);
    }

    @Override
    public int getByteLength() {
        return BYTE_LENGTH;
    }
}
