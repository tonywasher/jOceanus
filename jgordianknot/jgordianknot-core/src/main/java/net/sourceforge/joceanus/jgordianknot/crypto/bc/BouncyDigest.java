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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import org.bouncycastle.crypto.Digest;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;

/**
 * Wrapper for BouncyCastle Digest.
 */
public final class BouncyDigest
        implements GordianDigest {
    /**
     * Digest Type.
     */
    private final GordianDigestType theDigestType;

    /**
     * Digest.
     */
    private final Digest theDigest;

    /**
     * Constructor.
     * @param pDigestType the digest type
     * @param pDigest the digest
     */
    protected BouncyDigest(final GordianDigestType pDigestType,
                           final Digest pDigest) {
        theDigestType = pDigestType;
        theDigest = pDigest;
    }

    @Override
    public GordianDigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Obtain Underlying Digest.
     * @return the underlying digest
     */
    protected Digest getDigest() {
        return theDigest;
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestSize();
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theDigest.update(pBytes, pOffset, pLength);
    }

    @Override
    public void update(final byte pByte) {
        theDigest.update(pByte);
    }

    @Override
    public void update(final byte[] pBytes) {
        theDigest.update(pBytes, 0, pBytes.length);
    }

    @Override
    public void reset() {
        theDigest.reset();
    }

    @Override
    public byte[] finish() {
        byte[] myResult = new byte[getDigestSize()];
        theDigest.doFinal(myResult, 0);
        return myResult;
    }

    @Override
    public int finish(final byte[] pBuffer,
                      final int pOffset) {
        return theDigest.doFinal(pBuffer, pOffset);
    }
}
