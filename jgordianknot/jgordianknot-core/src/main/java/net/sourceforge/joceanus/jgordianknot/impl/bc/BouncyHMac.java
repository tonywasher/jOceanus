/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import java.util.Arrays;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Memoable;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * HMac implementation that utilises Xof digests.
 */
public class BouncyHMac
        implements Mac {
    /**
     * IPAD Value.
     */
    private static final byte IPAD = (byte) 0x36;

    /**
     * OPAD value.
     */
    private static final byte OPAD = (byte) 0x5C;

    /**
     * The macSpec.
     */
    private final GordianMacSpec theMacSpec;

    /**
     * The underlying digest.
     */
    private final Digest theDigest;

    /**
     * The blockLength.
     */
    private final int theBlockLen;

    /**
     * The digestLength.
     */
    private final int theDigestLen;

    /**
     * The iPadBuffer.
     */
    private final byte[] theIPadBuffer;

    /**
     * The oPadBuffer.
     */
    private final byte[] theOPadBuffer;

    /**
     * The ipadState.
     */
    private Memoable theIPadState;

    /**
     * The oPadState.
     */
    private Memoable theOPadState;

    /**
     * Constructor.
     * @param pFactory the DigestFactory
     * @param pMacSpec the MacSpec
     */
    BouncyHMac(final BouncyDigestFactory pFactory,
               final GordianMacSpec pMacSpec) throws OceanusException  {
        theMacSpec = pMacSpec;
        theDigest = pFactory.createDigest(pMacSpec.getDigestSpec()).getDigest();
        theBlockLen = theDigest instanceof ExtendedDigest
                ? ((ExtendedDigest) theDigest).getByteLength()
                : GordianLength.LEN_64.getLength();
        theDigestLen = theMacSpec.getMacLength().getByteLength();
        theIPadBuffer = new byte[theBlockLen];
        theOPadBuffer = new byte[theBlockLen + theDigestLen];
    }

    @Override
    public void reset() {
        theDigest.reset();
        theDigest.update(theIPadBuffer, 0, theBlockLen);
    }

    @Override
    public String getAlgorithmName() {
        return null;
    }

    @Override
    public int getMacSize() {
        return theMacSpec.getMacLength().getByteLength();
    }

    @Override
    public void init(final CipherParameters pParams) {
        /* Reset the digest */
        theDigest.reset();

        /* Access the key */
        final byte[] myKey = ((KeyParameter) pParams).getKey();
        int myLength = myKey.length;

        /* Build the adjusted key */
        if (myLength > theBlockLen) {
            theDigest.update(myKey, 0, myLength);
            theDigest.doFinal(theIPadBuffer, 0);
            myLength = theDigest.getDigestSize();
        } else {
            System.arraycopy(myKey, 0, theIPadBuffer, 0, myLength);
        }
        Arrays.fill(theIPadBuffer, myLength, theIPadBuffer.length, (byte) 0);

        /* Build adjusted iPad and oPad */
        System.arraycopy(theIPadBuffer, 0, theOPadBuffer, 0, theBlockLen);
        xorPad(theIPadBuffer, theBlockLen, IPAD);
        xorPad(theOPadBuffer, theBlockLen, OPAD);

        /* Create memoable states if possible */
        if (theDigest instanceof Memoable) {
            theOPadState = ((Memoable) theDigest).copy();
            ((Digest) theOPadState).update(theOPadBuffer, 0, theBlockLen);
        }
        theDigest.update(theIPadBuffer, 0, theBlockLen);
        if (theDigest instanceof Memoable) {
            theIPadState = ((Memoable) theDigest).copy();
        }
    }

    @Override
    public void update(final byte pByte) {
        theDigest.update(pByte);
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        theDigest.update(pBytes, pOffset, pLength);
    }

    @Override
    public int doFinal(final byte[] pBuffer,
                       final int pOffset) {
        /* Finish the digest */
        if (theDigest instanceof Xof) {
            ((Xof) theDigest).doFinal(theOPadBuffer, theBlockLen, theDigestLen);
        } else {
            theDigest.doFinal(theOPadBuffer, theBlockLen);
        }

        /* Generate the final result */
        if (theOPadState != null) {
            ((Memoable) theDigest).reset(theOPadState);
            theDigest.update(theOPadBuffer, theBlockLen, theDigestLen);
        } else {
            theDigest.update(theOPadBuffer, 0, theOPadBuffer.length);
        }
        final int myLen = theDigest instanceof Xof
                ? ((Xof) theDigest).doFinal(pBuffer, pOffset, theDigestLen)
                : theDigest.doFinal(pBuffer, pOffset);
        Arrays.fill(theOPadBuffer, theBlockLen, theOPadBuffer.length, (byte) 0);

        /* Reset state */
        if (theIPadState != null)        {
            ((Memoable) theDigest).reset(theIPadState);
        } else {
            theDigest.update(theIPadBuffer, 0, theBlockLen);
        }

        /* Return the length */
        return myLen;
    }

    /**
     * Xor a value into a pad.
     * @param pBuffer the pad buffer
     * @param pLen the length of the buffer
     * @param pValue the value
     */
    private static void xorPad(final byte[] pBuffer,
                               final int pLen,
                               final byte pValue) {
        for (int i = 0; i < pLen; i++) {
            pBuffer[i] ^= pValue;
        }
    }
}
