/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package org.bouncycastle.crypto.ext.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.ext.digests.Blake2;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Bouncy implementation of Blake2Mac.
 */
public class Blake2Mac
        implements Mac {
    /**
     * Digest.
     */
    private final Blake2 theDigest;

    /**
     * Create a blake2Mac with the specified digest.
     * @param pDigest the base digest.
     */
    public Blake2Mac(final Blake2 pDigest) {
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
        byte[] myIV = null;
        if (myParams instanceof ParametersWithIV) {
            final ParametersWithIV ivParams = (ParametersWithIV) myParams;
            myIV = ivParams.getIV();
            myParams = ivParams.getParameters();
        }

        /* Access the key */
        if (!(myParams instanceof KeyParameter)) {
            throw new IllegalArgumentException(getAlgorithmName() + " requires a key.");
        }
        final KeyParameter keyParams = (KeyParameter) myParams;
        final byte[] myKey = keyParams.getKey();

        /* Configure the digest */
        theDigest.setKey(myKey);
        theDigest.setSalt(myIV);
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

    /**
     * Set the personalisation.
     * @param pPersonal the personalisation.
     */
    public void setPersonalisation(final byte[] pPersonal) {
        theDigest.setPersonalisation(pPersonal);
    }
}

