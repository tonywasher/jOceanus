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

import net.sourceforge.joceanus.gordianknot.impl.ext.params.GordianKeccakParameters;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.macs.KMAC;
import org.bouncycastle.crypto.params.KeyParameter;

/**
 * KMAC.
 */
public class GordianKMACWrapper
        implements Mac, Xof {
    /**
     * The bit length for the Mac.
     */
    private final int theBitLength;

    /**
     * The base digest.
     */
    private KMAC theMac;

    /**
     * Constructor.
     * @param pBitLength the bitLength
     */
    public GordianKMACWrapper(final int pBitLength) {
        theBitLength = pBitLength;
        theMac = new KMAC(theBitLength, null);
    }

    @Override
    public String getAlgorithmName() {
        return theMac.getAlgorithmName();
    }

    @Override
    public void init(final CipherParameters pParams) {
        CipherParameters myParams = pParams;
        byte[] myPersonal = null;
        if (myParams instanceof GordianKeccakParameters) {
            GordianKeccakParameters myKeccakParams = (GordianKeccakParameters) myParams;
            myParams = new KeyParameter(myKeccakParams.getKey());
            myPersonal = myKeccakParams.getPersonalisation();
        }
        if (!(myParams instanceof KeyParameter)) {
            throw new IllegalArgumentException("Invalid parameter passed to KMAC init - "
                    + pParams.getClass().getName());
        }
        final KeyParameter myKeyParams = (KeyParameter) myParams;
        if (myKeyParams.getKey() == null) {
            throw new IllegalArgumentException("KMAC requires a key parameter.");
        }

        /* Configure the Mac */
        theMac = new KMAC(theBitLength, myPersonal);
        theMac.init(myKeyParams);
    }

    @Override
    public int getMacSize() {
        return theMac.getMacSize();
    }

    @Override
    public void update(final byte in) {
        theMac.update(in);
    }

    @Override
    public void update(final byte[] in, final int inOff, final int len) {
        theMac.update(in, inOff, len);
    }

    @Override
    public int doFinal(final byte[] out, final int outOff) {
        return theMac.doFinal(out, outOff);
    }

    @Override
    public void reset() {
        theMac.reset();
    }

    @Override
    public int doFinal(final byte[] out, final int outOff, final int outLen) {
        return theMac.doFinal(out, outOff, outLen);
    }

    @Override
    public int doOutput(final byte[] out, final int outOff, final int outLen) {
        return theMac.doOutput(out, outOff, outLen);
    }

    @Override
    public int getByteLength() {
        return theMac.getByteLength();
    }

    @Override
    public int getDigestSize() {
        return theMac.getDigestSize();
    }
}
