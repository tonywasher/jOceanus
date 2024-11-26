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
package net.sourceforge.joceanus.gordianknot.impl.ext.macs;

import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake3Digest;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianBlake3Parameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Bouncy implementation of Blake3Mac.
 */
public class GordianBlake3Mac
        implements Mac, Xof {
    /**
     * Digest.
     */
    private final GordianBlake3Digest theDigest;

    /**
     * Create a blake2Mac with the specified digest.
     * @param pDigest the base digest.
     */
    public GordianBlake3Mac(final GordianBlake3Digest pDigest) {
        /* Store the digest */
        theDigest = pDigest;
    }

    @Override
    public String getAlgorithmName() {
        return theDigest.getAlgorithmName() + "Mac";
    }

    @Override
    public void init(final CipherParameters pParams) {
        CipherParameters myParams = pParams;
        if (myParams instanceof KeyParameter) {
            myParams = GordianBlake3Parameters.key(((KeyParameter) myParams).getKey());
        }
        if (!(myParams instanceof GordianBlake3Parameters)) {
            throw new IllegalArgumentException("Invalid parameter passed to Blake3Mac init - "
                    + pParams.getClass().getName());
        }
        final GordianBlake3Parameters myBlakeParams = (GordianBlake3Parameters) myParams;
        if (myBlakeParams.getKey() == null) {
            throw new IllegalArgumentException("Blake3Mac requires a key parameter.");
        }

        /* Configure the digest */
        theDigest.init(myBlakeParams);
    }

    @Override
    public int getMacSize() {
        return theDigest.getDigestSize();
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

    @Override
    public int doFinal(final byte[] out, final int outOff, final int outLen) {
        return theDigest.doFinal(out, outOff, outLen);
    }

    @Override
    public int doOutput(final byte[] out, final int outOff, final int outLen) {
        return theDigest.doOutput(out, outOff, outLen);
    }

    @Override
    public int getByteLength() {
        return theDigest.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestSize();
    }
}
