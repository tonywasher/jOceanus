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
package org.bouncycastle.crypto.ext.digests;

import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.ext.params.Blake2Parameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;

/**
 * Blake2X implementation.
 * <p>This implementation supports the following elements of Blake2Parameters
 * <ul>
 *     <li>Key
 *     <li>Nonce
 *     <li>Personalisation
 *     <li>Max Output length
 * </ul>
 * <p>If OutputLen is set to zero, then Blake2X defaults to the underlying digestLength.
 */
public class Blake2X
        implements ExtendedDigest, Memoable, Xof {
    /**
     * The underlying Blake instance.
     */
    private final Blake2 theUnderlying;

    /**
     * The combining Blake instance.
     */
    private final Blake2 theComposite;

    /**
     * The single byte buffer.
     */
    private final byte[] singleByte = new byte[1];

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
     * @param pDigest the underlying digest.
     */
    public Blake2X(final Blake2 pDigest) {
        /* Create the two digests */
        theUnderlying = pDigest;
        theComposite = (Blake2) theUnderlying.copy();

        /* Configure the composite */
        theComposite.setTreeConfig(0, 0, theUnderlying.getDigestSize());
        theComposite.setInnerLength(theUnderlying.getDigestSize());
        theComposite.setXofLen(0);

        /* Clear outputting flag */
        theXofRemaining = -1L;
    }

    /**
     * Constructor.
     * @param pSource the source digest.
     */
    public Blake2X(final Blake2X pSource) {
        /* Create hashes */
        theUnderlying = pSource.theUnderlying instanceof Blake2b ? new Blake2b() : new Blake2s();
        theComposite = (Blake2) theUnderlying.copy();

        /* Initialise from source */
        reset(pSource);
    }

    /**
     * Initialise.
     * @param pParams the parameters.
     */
    public void init(final Blake2Parameters pParams) {
        /* Pass selective parameters to the underlying hash */
        theUnderlying.setKey(pParams.getKey());
        theUnderlying.setSalt(pParams.getSalt());
        theUnderlying.setPersonalisation(pParams.getPersonalisation());
        theUnderlying.setXofLen(pParams.getMaxOutputLength());
        theUnderlying.reset();

        /* Pass selective parameters to the underlying hash */
        theComposite.setSalt(pParams.getSalt());
        theComposite.setPersonalisation(pParams.getPersonalisation());
        theComposite.setXofLen(pParams.getMaxOutputLength());
        theComposite.reset();

        /* Adjust XofLength */
        theXofLen = theUnderlying.getXofLen();
        theXofRemaining = -1L;
        theRoot = null;
        theCurrent = null;
    }

    @Override
    public String getAlgorithmName() {
        final String myBase = theUnderlying instanceof Blake2b ? "Blake2Xb" : "Blake2Xs";
        if (theXofLen == -1) {
            return myBase;
        }
        final long myLen = theXofLen == 0 ? theUnderlying.getDigestSize() : theXofLen;
        return myBase + "-" + (myLen * 8);
    }

    @Override
    public int getDigestSize() {
        return theXofLen == 0 ? theUnderlying.getDigestSize() : (int) theXofLen;
    }

    @Override
    public int getByteLength() {
        return theUnderlying.getByteLength();
    }

    @Override
    public void update(final byte b) {
        singleByte[0] = b;
        update(singleByte, 0, 1);
    }

    @Override
    public void update(final byte[] pMessage,
                       final int pOffset,
                       final int pLen) {
        if (theXofRemaining != -1) {
            throw new IllegalStateException("Already outputting");
        }
        theUnderlying.update(pMessage, pOffset, pLen);
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOffset) {
        if (theXofLen == -1) {
            throw new IllegalStateException("No defined output length");
        }
        return doFinal(pOut, pOutOffset, getDigestSize());
    }

    @Override
    public int doFinal(final byte[] pOut,
                       final int pOutOffset,
                       final int pOutLen) {
        /* Reject if we are already outputting */
        if (theXofRemaining != -1) {
            throw new IllegalStateException("Already outputting");
        }

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

            /* If we have a null Xof */
            if (theXofLen == 0) {
                /* Calculate the number of bytes available */
                theXofRemaining = theUnderlying.getDigestSize();
                theCurrent = theRoot;

                /* Else we are handling a normal Xof */
            } else {
                /* Calculate the number of bytes available */
                theXofRemaining = theXofLen == -1
                                  ? (1L << Integer.SIZE) * theUnderlying.getDigestSize()
                                  : theXofLen;

                /* Allocate a new current hash buffer */
                theCurrent = new byte[theUnderlying.getDigestSize()];
                theHashIndex = theCurrent.length;
            }
        }

        /* Reject if there is insufficient Xof remaining */
        if (pOutLen < 0
                || (theXofRemaining > 0  && pOutLen > theXofRemaining)) {
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
            System.arraycopy(theCurrent, 0, pOut, outPos, dataToCopy);

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
        theComposite.reset();
        theRoot = null;
        theCurrent = null;
        theXofRemaining = -1L;
        theHashIndex = 0;
        theNodeIndex = 0;
    }

    @Override
    public void reset(final Memoable pSource) {
        /* Access source */
        final Blake2X mySource = (Blake2X) pSource;

        /* Reset digests */
        theUnderlying.reset(mySource.theUnderlying);
        theComposite.reset(mySource.theComposite);

        /* Clone hashes */
        theRoot = Arrays.clone(mySource.theRoot);
        theCurrent = Arrays.clone(mySource.theCurrent);

        /* Copy state */
        theXofLen = mySource.theXofLen;
        theXofRemaining = mySource.theXofRemaining;
        theHashIndex = mySource.theHashIndex;
        theNodeIndex = mySource.theNodeIndex;
    }

    @Override
    public Blake2X copy() {
        return new Blake2X(this);
    }

    /**
     * Obtain the next hash.
     */
    private void obtainNextHash() {
        /* Set the digestLength */
        int digestLen = theUnderlying.getDigestSize();
        if (theXofRemaining < digestLen) {
            digestLen = (int) theXofRemaining;
        }
        theComposite.setDigestLength(digestLen);

        /* Calculate the hash */
        theComposite.setNodePosition(theNodeIndex++, 0);
        theComposite.update(theRoot, 0, theRoot.length);
        theComposite.doFinal(theCurrent, 0);
        theHashIndex = 0;
    }
}
