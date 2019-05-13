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
package org.bouncycastle.crypto.ext.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;

public class Blake2X
        implements ExtendedDigest, Xof {
    /**
     * The underlying Blake instance.
     */
    private final Blake2 theUnderlying;

    /**
     * The combining Blake instance.
     */
    private final Blake2 theComposite;

    /**
     * The XofLen.
     */
    private long theXofLen;

    /**
     * The Root hash.
     */
    private byte[] theRoot;

    /**
     * The XofRemaining.
     */
    private long theXofRemaining;

    /**
     * The current hash.
     */
    private byte[] theCurrent;

    /**
     * The data index within the current hash.
     */
    private int theHashIndex;

    /**
     * The Xof NodeIndex.
     */
    private int theNodeIndex;

    /**
     * Constructor.
     * @param p2b use Blake2b?
     */
    public Blake2X(final boolean p2b) {
        /* Create the two digests */
        theUnderlying = p2b ? new Blake2b(512) : new Blake2s(256);
        theComposite = p2b ? new Blake2b(512) : new Blake2s(256);

        /* Configure the composite */
        theComposite.setTreeConfig(0, 0, theUnderlying.getDigestSize());
        theComposite.setInnerLength(theUnderlying.getDigestSize());
        theComposite.setXofLen(-1);
        theXofLen = -1;
    }

    /**
     * Set the key.
     * @param pKey the key.
     */
    public void setKey(final byte[] pKey) {
        theUnderlying.setKey(pKey);
    }

    /**
     * Set the salt.
     * @param pSalt the salt.
     */
    public void setSalt(final byte[] pSalt) {
        theUnderlying.setSalt(pSalt);
        theComposite.setSalt(pSalt);
    }

    /**
     * Set the personalisation.
     * @param pPersonal the personalisation.
     */
    public void setPersonalisation(final byte[] pPersonal) {
        theUnderlying.setPersonalisation(pPersonal);
        theComposite.setSalt(pPersonal);
    }

    /**
     * Set the XofLength.
     * @param pXofLen the XofLen.
     */
    public void setXofLen(final long pXofLen) {
        theUnderlying.setXofLen(pXofLen);
        theComposite.setXofLen(pXofLen);
        theXofLen = pXofLen;
    }

    @Override
    public String getAlgorithmName() {
        return "Blake2Xb";
    }

    @Override
    public int getDigestSize() {
        return theUnderlying.getDigestSize();
    }

    @Override
    public int getByteLength() {
        return theUnderlying.getByteLength();
    }

    @Override
    public void update(final byte b) {
        theUnderlying.update(b);
    }

    @Override
    public void update(final byte[] pMessage,
                       final int pOffset,
                       final int pLen) {
        theUnderlying.update(pMessage, pOffset, pLen);
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOffset) {
        return doFinal(pOut, pOutOffset, getDigestSize());
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOffset,
                       final int pOutLen) {
        /* Build the required output */
        final int length = doOutput(pOut, pOutOffset, pOutLen);

        /* reset the underlying digest and return the length */
        reset();
        return length;
    }

    @Override
    public int doOutput(final byte[] pOut,
                        final int pOutOffset,
                        final int pOutLen) {
        /* If we have not created the root hash yet */
        if (theRoot == null) {
            /* Calculate the underlying hash */
            theRoot = new byte[theUnderlying.getDigestSize()];
            theUnderlying.doFinal(theRoot, 0);
            theNodeIndex = 0;

            /* Calculate the number of bytes available */
            theXofRemaining = theXofLen == -1
                              ? (1L << Integer.SIZE) * theUnderlying.getDigestSize()
                              : theXofLen;

            /* Allocate a new current hash buffer */
            theCurrent = new byte[theUnderlying.getDigestSize()];
            theHashIndex = theCurrent.length;
        }

        /* Reject if there is insufficient Xof remaining */
        if (pOutLen < 0 || pOutLen > theXofRemaining) {
            throw new IllegalArgumentException("Insufficient bytes remaining");
        }

        /* If we have some remaining data in the current hash */
        int dataLeft = pOutLen;
        int outPos = pOutOffset;
        if (theHashIndex < theCurrent.length) {
            /* Copy data from current hash */
            final int dataToCopy = Math.min(dataLeft, theCurrent.length - theHashIndex);
            System.arraycopy(theCurrent, theHashIndex, pOut, outPos, dataToCopy);

            /* Adjust counters */
            theXofRemaining -= dataToCopy;
            theHashIndex += dataToCopy;
            outPos += dataToCopy;
            dataLeft -= dataToCopy;
        }

        /* Loop until we have completed the request */
        while (dataLeft > 0) {
            /* Calculate the next hash */
            obtainNextHash();

            /* Copy data from current hash */
            final int dataToCopy = Math.min(dataLeft, theCurrent.length);
            System.arraycopy(theCurrent, theHashIndex, pOut, outPos, dataToCopy);

            /* Adjust counters */
            theXofRemaining -= dataToCopy;
            theHashIndex += dataToCopy;
            outPos += dataToCopy;
            dataLeft -= dataToCopy;
        }

        /* Return the number of bytes transferred */
        return pOutLen;
    }

    @Override
    public void reset() {
        theUnderlying.reset();
        theRoot = null;
        theCurrent = null;
    }

    /**
     * Obtain the next hash.
     */
    private void obtainNextHash() {
        /* Set the digestLength */
        int digestLen = getDigestSize();
        if (theXofRemaining < digestLen) {
            digestLen = (int) theXofRemaining;
        }
        theComposite.setDigestLength(digestLen);

        /* Calculate the hash */
        theComposite.setNodePosition(theNodeIndex++, (short) 0);
        theComposite.update(theRoot, 0, theRoot.length);
        theComposite.doFinal(theCurrent, 0);
        theHashIndex = 0;
    }
}
