/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2Xof;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianBlake2Parameters;
import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianBlake2Parameters.GordianBlake2ParametersBuilder;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * Bouncy implementation of Blake2Mac.
 */
public class GordianBlake2XMac
        implements Mac, Xof {
    /**
     * Digest.
     */
    private final GordianBlake2Xof theXof;

    /**
     * Create a blake2XMac with the specified xof.
     * @param pXof the base xof.
     */
    public GordianBlake2XMac(final GordianBlake2Xof pXof) {
        theXof = pXof;
    }

    @Override
    public String getAlgorithmName() {
        return theXof.getAlgorithmName() + "Mac";
    }

    @Override
    public void init(final CipherParameters pParams) {
        CipherParameters myParams = pParams;
        if (myParams instanceof KeyParameter) {
            myParams = new GordianBlake2ParametersBuilder()
                    .setKey(((KeyParameter) myParams).getKey())
                    .setMaxOutputLen(-1L)
                    .build();
        }
        if (!(myParams instanceof GordianBlake2Parameters)) {
            throw new IllegalArgumentException("Invalid parameter passed to Blake2XMac init - "
                    + pParams.getClass().getName());
        }
        final GordianBlake2Parameters myBlakeParams = (GordianBlake2Parameters) myParams;
        if (myBlakeParams.getKey() == null) {
            throw new IllegalArgumentException("Blake2XMac requires a key parameter.");
        }

        /* Configure the xof */
        theXof.init(myBlakeParams);
    }

    @Override
    public int getMacSize() {
        return theXof.getDigestSize();
    }

    @Override
    public void update(final byte in) {
        theXof.update(in);
    }

    @Override
    public void update(final byte[] in, final int inOff, final int len) {
        theXof.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff) {
        return theXof.doFinal(out, outOff);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff, final int outLen) {
        return theXof.doFinal(out, outOff, outLen);
    }

    @Override
    public int doOutput(final byte[] out, final int outOff, final int outLen) {
        return theXof.doOutput(out, outOff, outLen);
    }

    @Override
    public int getByteLength() {
        return theXof.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return theXof.getDigestSize();
    }

    @Override
    public void reset() {
        theXof.reset();
    }
}
