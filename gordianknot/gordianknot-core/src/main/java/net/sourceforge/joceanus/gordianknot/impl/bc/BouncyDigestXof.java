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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import org.bouncycastle.crypto.Xof;

import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianXof;

/**
 * BouncyCastle Digest Xof.
 */
public class BouncyDigestXof
    extends BouncyDigest implements GordianXof {
    /**
     * The Xof.
     */
    private final Xof theXof;

    /**
     * Constructor.
     * @param pDigestSpec the digestSpec
     * @param pXof the digestXof
     */
    BouncyDigestXof(final GordianDigestSpec pDigestSpec,
                    final Xof pXof) {
        super(pDigestSpec, pXof);
        theXof = pXof;
    }

    @Override
    public int finish(final byte[] pOutBuf,
                      final int pOutOff,
                      final int pOutLen) {
        return theXof.doFinal(pOutBuf, pOutOff, pOutLen);
    }

    @Override
    public int output(byte[] pOutBuf, int pOutOff, int pOutLen) {
        return theXof.doOutput(pOutBuf, pOutOff, pOutLen);
    }

    @Override
    public int doFinish(final byte[] pBuffer,
                        final int pOffset) {
        return theXof.doFinal(pBuffer, pOffset, getDigestSize());
    }
}
