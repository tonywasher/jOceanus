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
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;

/**
 * Bouncy implementation of Blake2bMac.
 */
public class BouncyBlake2Mac implements Mac {
    /**
     * MacSpec.
     */
    private final GordianMacSpec theSpec;

    /**
     * Use Blake2b?
     */
    private final boolean useBlake2b;

    /**
     * MacSize.
     */
    private final int theMacSize;

    /**
     * Digest.
     */
    private Digest theDigest;

    /**
     * Create a blake2Mac with the specified macSpec.
     * @param pSpec the macSize.
     */
    public BouncyBlake2Mac(final GordianMacSpec pSpec) {
        theSpec = pSpec;
        final GordianDigestSpec myDigest = theSpec.getDigestSpec();
        theMacSize = myDigest.getDigestLength().getByteLength();
        final int myBitLength = myDigest.getDigestLength().getLength();
        useBlake2b = GordianDigestType.isBlake2bState(myDigest.getStateLength());
        theDigest = useBlake2b
                               ? new Blake2bDigest(myBitLength)
                               : new Blake2sDigest(myBitLength);
    }

    @Override
    public String getAlgorithmName() {
        return theSpec.toString();
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

        /* Recreate the digest */
        theDigest = useBlake2b
                               ? new Blake2bDigest(myKey, theMacSize, myIV, null)
                               : new Blake2sDigest(myKey, theMacSize, myIV, null);
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
