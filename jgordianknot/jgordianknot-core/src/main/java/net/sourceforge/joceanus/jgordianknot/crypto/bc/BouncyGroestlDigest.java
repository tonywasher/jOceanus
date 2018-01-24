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

import info.groestl.GroestlFastDigest;

/**
 * Java implementation of JH Digest.
 * <p>
 * This is a direct port from the JH Reference C implementation, with minimal modifications.
 */
public class BouncyGroestlDigest
        implements ExtendedDigest {
    /**
     * The underlying StateLength.
     */
    private static final int STATE_LENGTH = 1024;

    /**
     * The underlying digest.
     */
    private final GroestlFastDigest theDigest;

    /**
     * The digest length.
     */
    private final int theDigestLen;

    /**
     * Constructor.
     * @param pHashBitLen the hash bit length
     */
    public BouncyGroestlDigest(final int pHashBitLen) {
        theDigest = new GroestlFastDigest(pHashBitLen);
        theDigestLen = pHashBitLen / Byte.SIZE;
    }

    @Override
    public int doFinal(final byte[] pHash, final int pOffset) {
        theDigest.Final(pHash, pOffset);
        return getDigestSize();
    }

    @Override
    public String getAlgorithmName() {
        return "Groestl";
    }

    @Override
    public int getDigestSize() {
        return theDigestLen;
    }

    @Override
    public void reset() {
        theDigest.reset();
    }

    @Override
    public void update(final byte arg0) {
        final byte[] myByte = new byte[]
        { arg0 };
        update(myByte, 0, 1);
    }

    @Override
    public void update(final byte[] pData, final int pOffset, final int pLength) {
        theDigest.Update(pData, pOffset, ((long) pLength) * Byte.SIZE);
    }

    @Override
    public int getByteLength() {
        return STATE_LENGTH;
    }
}
