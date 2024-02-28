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
import org.bouncycastle.crypto.ext.digests.SkeinBase.Configuration;
import org.bouncycastle.crypto.ext.params.SkeinXParameters;
import org.bouncycastle.util.Memoable;

/**
 * SkeinXof implementation.
 * <p>This implementation supports the following elements of SkeinXParameters
 * <ul>
 *     <li>Key
 *     <li>Nonce
 *     <li>Personalisation
 *     <li>PublicKey
 *     <li>Key Identifier
 *     <li>Max Output length
 * </ul>
 * <p>If OutputLen is set to zero, then SkeinXof defaults to the underlying digestLength.
 */
public class SkeinXof
        implements ExtendedDigest, Memoable, Xof {
    /**
     * The underlying Skein instance.
     */
    private final SkeinBase theUnderlying;

    /**
     * The single byte buffer.
     */
    private final byte[] singleByte = new byte[1];

    /**
     * The next output index.
     */
    private byte[] outputCache;

    /**
     * The XofLen.
     */
    private long theXofLen;

    /**
     * The XofRemaining.
     */
    private long theXofRemaining;

    /**
     * The next output index.
     */
    private long nextOutputIdx;

    /**
     * Bytes in cache.
     */
    private int bytesInCache;

    /**
     * Constructor.
     * @param pDigest the underlying digest.
     */
    public SkeinXof(final SkeinBase pDigest) {
        /* Store the digest */
        theUnderlying = pDigest;
        outputCache = new byte[theUnderlying.getBlockSize()];

        /* Clear outputting flag */
        theXofRemaining = -1L;
    }

    /**
     * Constructor.
     * @param pSource the source digest.
     */
    public SkeinXof(final SkeinXof pSource) {
        /* Create hashes */
        theUnderlying = (SkeinBase) pSource.theUnderlying.copy();
        outputCache = new byte[theUnderlying.getBlockSize()];

        /* Initialise from source */
        reset(pSource);
    }

    /**
     * Initialise.
     * @param pParams the parameters.
     */
    public void init(final SkeinXParameters pParams) {
        /* Reject a negative Xof length */
        final long myXofLen = pParams.getMaxOutputLength();
        if (myXofLen < -1) {
            throw new IllegalArgumentException("Invalid output length");
        }
        theXofLen = myXofLen;

        /* Declare the configuration */
        declareConfig();

        /* Pass selective parameters to the underlying hash */
        theUnderlying.init(pParams);
        theXofRemaining = -1L;
    }

    /**
     * Declare extended configuration.
     */
    private void declareConfig() {
        /* Declare the configuration */
        long myLen = theXofLen == 0 ? theUnderlying.getOutputSize() : theXofLen;
        myLen = myLen == -1 ? -1L : myLen * Byte.SIZE;
        final Configuration myConfig = new Configuration(myLen);
        theUnderlying.setConfiguration(myConfig);
    }

    @Override
    public String getAlgorithmName() {
        final String myBase = "SkeinXof";
        if (theXofLen == -1) {
            return myBase;
        }
        final long myLen = theXofLen == 0 ? theUnderlying.getOutputSize() : theXofLen;
        return myBase + "-" + (theUnderlying.getBlockSize() * Byte.SIZE) + "-" + (myLen * Byte.SIZE);
    }

    @Override
    public int getDigestSize() {
        return theXofLen == 0 ? theUnderlying.getOutputSize() : (int) theXofLen;
    }

    @Override
    public int getByteLength() {
        return theUnderlying.getBlockSize();
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
        /* If wa are switching to output */
        if (theXofRemaining == -1) {
            /* Initialise values */
            nextOutputIdx = 0;
            bytesInCache = 0;

            /* If we have a null Xof */
            if (theXofLen == 0) {
                /* Calculate the number of bytes available */
                theXofRemaining = theUnderlying.getOutputSize();

                /* Else we are handling a normal Xof */
            } else {
                /* Calculate the number of bytes available */
                theXofRemaining = theXofLen == -1
                                  ? -2
                                  : theXofLen;
            }

            /* Initiate output */
            theUnderlying.initiateOutput();
        }

        /* Reject if there is insufficient Xof remaining */
        if (pOutLen < 0
                || (theXofRemaining > 0  && pOutLen > theXofRemaining)) {
            throw new IllegalArgumentException("Insufficient bytes remaining");
        }

        /* If we have some remaining data in the cache */
        int dataLeft = pOutLen;
        int outPos = pOutOffset;
        if (bytesInCache > 0) {
            /* Copy data from current hash */
            final int dataToCopy = Math.min(dataLeft, bytesInCache);
            System.arraycopy(outputCache, outputCache.length - bytesInCache, pOut, outPos, dataToCopy);

            /* Adjust counters */
            theXofRemaining -= dataToCopy;
            bytesInCache -= dataToCopy;
            outPos += dataToCopy;
            dataLeft -= dataToCopy;
        }

        /* Loop until we have completed the request */
        while (dataLeft > 0) {
            /* Obtain the next set of output bytes */
            theUnderlying.output(nextOutputIdx++, outputCache, 0, outputCache.length);
            bytesInCache = outputCache.length;

            /* Copy data from current hash */
            final int dataToCopy = Math.min(dataLeft, outputCache.length);
            System.arraycopy(outputCache, 0, pOut, outPos, dataToCopy);

            /* Adjust counters */
            theXofRemaining -= dataToCopy;
            bytesInCache -= dataToCopy;
            outPos += dataToCopy;
            dataLeft -= dataToCopy;
        }

        /* Return the number of bytes transferred */
        return pOutLen;
    }

    @Override
    public void reset() {
        theUnderlying.reset();
        theXofRemaining = -1L;
    }

    @Override
    public void reset(final Memoable pSource) {
        /* Access source */
        final SkeinXof mySource = (SkeinXof) pSource;

        /* Reset digests */
        theUnderlying.reset(mySource.theUnderlying);

        /* Copy state */
        theXofLen = mySource.theXofLen;
        theXofRemaining = mySource.theXofRemaining;
        bytesInCache = mySource.bytesInCache;
        nextOutputIdx = mySource.nextOutputIdx;

        /* Copy cache */
        System.arraycopy(mySource.outputCache, 0, outputCache, 0, outputCache.length);

        /* Declare extended configuration */
        declareConfig();
    }

    @Override
    public SkeinXof copy() {
        return new SkeinXof(this);
    }
}

