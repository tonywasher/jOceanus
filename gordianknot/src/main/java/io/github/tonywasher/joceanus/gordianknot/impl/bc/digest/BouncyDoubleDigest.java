/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.bc.digest;

/**
 * BouncyCastle Double Digest.
 */

public class BouncyDoubleDigest
        extends BouncyDigest {
    /**
     * Digest.
     */
    private final BouncyDigestXof theDigest;

    /**
     * Constructor.
     *
     * @param pDigestXof the digestXof
     */
    public BouncyDoubleDigest(final BouncyDigestXof pDigestXof) {
        super(pDigestXof.getDigestSpec(), pDigestXof.getDigest());
        theDigest = pDigestXof;
    }

    @Override
    public int getDigestSize() {
        return theDigest.getDigestSize() << 1;
    }

    @Override
    public int doFinish(final byte[] pBuffer,
                        final int pOffset) {
        return theDigest.finish(pBuffer, pOffset, getDigestSize());
    }
}
