package org.bouncycastle.crypto.ext.digests;

import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.ext.params.KeccakParameters;
import org.bouncycastle.util.Memoable;

/**
 * implementation of SHAKE based on following KeccakNISTInterface.c from http://keccak.noekeon.org/
 * <p>
 * Following the naming conventions used in the C source code to enable easy review of the implementation.
 */
public class SHAKE
        extends KeccakDigest
        implements Xof, Memoable {
    /**
     * The single byte buffer.
     */
    private final byte[] singleByte = new byte[1];

    /**
     * The bitLength.
     */
    private final int theBitLength;

    /**
     * The XofLen.
     */
    long theXofLen;

    /**
     * The XofRemaining.
     */
    long theXofRemaining;

    /**
     * Check supported bitLength.
     * @param bitLength the bit length
     * @return the bit length
     */
    private static int checkBitLength(final int bitLength) {
        switch (bitLength) {
            case 128:
            case 256:
                return bitLength;
            default:
                throw new IllegalArgumentException("'bitLength' " + bitLength + " not supported for SHAKE");
        }
    }

    /**
     * Default constructor.
     */
    public SHAKE()  {
        this(128);
    }

    /**
     * Constructor for specified bitLength.
     * @param bitLength the bit length
     */
    public SHAKE(final int bitLength) {
        /* Create the underlying digest */
        super(checkBitLength(bitLength));
        theBitLength = bitLength;

        /* Clear outputting flag */
        theXofRemaining = -1L;
    }

    /**
     * Copy constructor.
     * @param pSource the source for the copy
     */
    public SHAKE(final SHAKE pSource) {
        super(pSource);
        theBitLength = pSource.theBitLength;
        theXofLen = pSource.theXofLen;
        theXofRemaining = pSource.theXofRemaining;
    }

    @Override
    public String getAlgorithmName() {
        return "SHAKE" + theBitLength;
    }

    @Override
    public int getDigestSize() {
        return theXofLen == 0 ? super.getDigestSize() : (int) theXofLen;
    }

    /**
     * Initialise.
     * @param pParams the parameters.
     */
    public void init(final KeccakParameters pParams) {
        /* Reject a negative Xof length */
        final long myXofLen = pParams.getMaxOutputLength();
        if (myXofLen < -1) {
            throw new IllegalArgumentException("Invalid output length");
        }
        theXofLen = myXofLen;

        /* Reset everything */
        reset();
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
        super.update(pMessage, pOffset, pLen);
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
    public int doFinal(final byte[] out,
                       final int outOff,
                       final int outLen) {
        /* Reject if we are already outputting */
        if (theXofRemaining != -1) {
            throw new IllegalStateException("Already outputting");
        }

        /* Build the required output */
        final int length = doOutput(out, outOff, outLen);

        /* reset the underlying digest and return the length */
        reset();
        return length;
    }

    @Override
    public void reset() {
        super.reset();
        theXofRemaining = -1L;
    }

    @Override
    public void reset(final Memoable pSource) {
        /* Access source */
        final SHAKE mySource = (SHAKE) pSource;
        if (theBitLength != mySource.theBitLength) {
            throw new IllegalArgumentException();
        }

        /* Reset underlying digest */
        System.arraycopy(mySource.state, 0, this.state, 0, mySource.state.length);
        System.arraycopy(mySource.dataQueue, 0, this.dataQueue, 0, mySource.dataQueue.length);
        this.bitsInQueue = mySource.bitsInQueue;
        this.squeezing = mySource.squeezing;

        /* Copy counters */
        theXofLen = mySource.theXofLen;
        theXofRemaining = mySource.theXofRemaining;
    }

    @Override
    public SHAKE copy() {
        return new SHAKE(this);
    }

    @Override
    public int doOutput(final byte[] out,
                        final int outOff,
                        final int outLen) {
        /* If we are starting outputting */
        if (theXofRemaining == -1L) {
            /* Absorb trailer */
            absorbBits(0x0F, 4);

            /* If we have a null Xof */
            if (theXofLen == 0) {
                /* Calculate the number of bytes available */
                theXofRemaining = super.getDigestSize();

                /* Else we are handling a normal Xof */
            } else {
                /* Calculate the number of bytes available */
                theXofRemaining = theXofLen == -1
                                  ? -2
                                  : theXofLen;
            }
        }

        /* Reject if there is insufficient Xof remaining */
        if (outLen < 0
                || (theXofRemaining > 0  && outLen > theXofRemaining)) {
            throw new IllegalArgumentException("Insufficient bytes remaining");
        }

        /* Output the data */
        squeeze(out, outOff, ((long) outLen) * 8);
        theXofRemaining -= outLen;
        return outLen;
    }
}
