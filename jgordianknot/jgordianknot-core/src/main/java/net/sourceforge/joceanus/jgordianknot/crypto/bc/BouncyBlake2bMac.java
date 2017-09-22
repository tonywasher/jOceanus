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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Bouncy implementation of Blake2bMac.
 */
public class BouncyBlake2bMac implements Mac {
    /**
     * Digest.
     */
    private Blake2bDigest theDigest;

    /**
     * MacSize.
     */
    private final int theMacSize;

    /**
     * Create a blake2Mac with the specified macSize. The macSize must be a supported Blake2b
     * digestSize
     * @param pMacSize the macSize in bits.
     */
    public BouncyBlake2bMac(final int pMacSize) {
        theDigest = new Blake2bDigest(pMacSize);
        theMacSize = pMacSize / Byte.SIZE;
    }

    @Override
    public String getAlgorithmName() {
        return "Blake2bMac";
    }

    @Override
    public void init(final CipherParameters params) {
        /* Access the key */
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("Blake2b requires a key.");
        }
        final KeyParameter keyParams = (KeyParameter) params;

        /* Recreate the digest */
        theDigest = new Blake2bDigest(keyParams.getKey(), theMacSize, null, null);
    }

    @Override
    public int getMacSize() {
        return theMacSize;
    }

    @Override
    public void update(final byte in) {
        theDigest.update(in);
    }

    @Override
    public void update(final byte[] in, final int inOff, final int len) {
        theDigest.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff) {
        return theDigest.doFinal(out, outOff);
    }

    @Override
    public void reset() {
        theDigest.reset();
    }
}
