package org.bouncycastle.crypto.ext.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.ext.digests.SkeinBase;
import org.bouncycastle.crypto.ext.digests.SkeinXof;
import org.bouncycastle.crypto.ext.params.SkeinXParameters.Builder;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Memoable;

/**
 * SkeinXof used as a stream Cipher.
 */
public class SkeinXofEngine
        implements StreamCipher, Memoable {
    /**
     * index of next byte in keyStream.
     */
    private int theIndex;

    /**
     * Advanced stream.
     */
    private final byte[] theKeyStream;

    /**
     * Underlying digest.
     */
    private final SkeinXof theSkeinXof;

    /**
     * Reset state.
     */
    private SkeinXof theResetState;

    /**
     * Constructor.
     * @param pLength the underlying stateLength
     */
    public SkeinXofEngine(final int pLength) {
        final SkeinBase myBase = new SkeinBase(pLength, pLength);
        theSkeinXof = new SkeinXof(myBase);
        theKeyStream = new byte[myBase.getBlockSize()];
    }

    /**
     * Constructor.
     * @param pSource the source engine
     */
    private SkeinXofEngine(final SkeinXofEngine pSource) {
        theSkeinXof = new SkeinXof(pSource.theSkeinXof);
        theKeyStream = new byte[theSkeinXof.getByteLength()];
        reset(pSource);
    }

    /**
     * initialise a SkeinXof cipher.
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is inappropriate.
     */
    public void init(final boolean forEncryption,
                     final CipherParameters params) {
        /*
         * SkeinXof encryption and decryption is completely symmetrical, so the 'forEncryption' is
         * irrelevant. (Like 90% of stream ciphers)
         */

        /* Determine parameters */
        CipherParameters myParams = params;
        byte[] newKey = null;
        byte[] newIV = null;
        if ((myParams instanceof ParametersWithIV)) {
            final ParametersWithIV ivParams = (ParametersWithIV) myParams;
            newIV = ivParams.getIV();
            myParams = ivParams.getParameters();
        }
        if (myParams instanceof KeyParameter) {
            final KeyParameter keyParam = (KeyParameter) myParams;
            newKey = keyParam.getKey();
        }
        if (newKey == null) {
            throw new IllegalArgumentException("A key must be provided");
        }

        /* Initialise engine and mark as initialised */
        final Builder myBuilder = new Builder()
                .setKey(newKey)
                .setMaxOutputLen(-1);
        if (newIV != null) {
            myBuilder.setNonce(newIV);
        }
        theSkeinXof.init(myBuilder.build());

        /* Save reset state */
        theResetState = theSkeinXof.copy();

        /* Initialise the stream block */
        theIndex = 0;
        makeStreamBlock();
    }

    @Override
    public String getAlgorithmName() {
        return theSkeinXof.getAlgorithmName();
    }

    @Override
    public int processBytes(final byte[] in,
                            final int inOff,
                            final int len,
                            final byte[] out,
                            final int outOff) {
        /* Check for errors */
        if (theResetState == null) {
            throw new IllegalStateException(getAlgorithmName() + " not initialised");
        }
        if ((inOff + len) > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if ((outOff + len) > out.length) {
            throw new OutputLengthException("output buffer too short");
        }

        /* Loop through the input bytes */
        for (int i = 0; i < len; i++) {
            out[i + outOff] = returnByte(in[i + inOff]);
        }
        return len;
    }

    @Override
    public void reset() {
        if (theResetState != null) {
            theSkeinXof.reset(theResetState);
            theIndex = 0;
            makeStreamBlock();
        }
    }

    @Override
    public byte returnByte(final byte in) {
        final byte out = (byte) (theKeyStream[theIndex] ^ in);
        theIndex = (theIndex + 1) % theKeyStream.length;

        if (theIndex == 0) {
            makeStreamBlock();
        }
        return out;
    }

    /**
     * Generate keystream.
     */
    private void makeStreamBlock() {
        /* Generate next output block */
        theSkeinXof.doOutput(theKeyStream, 0, theKeyStream.length);
    }

    @Override
    public SkeinXofEngine copy() {
        return new SkeinXofEngine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final SkeinXofEngine e = (SkeinXofEngine) pState;
        if (theKeyStream.length != e.theKeyStream.length) {
            throw new IllegalArgumentException();
        }
        theSkeinXof.reset(e.theSkeinXof);
        System.arraycopy(e.theKeyStream, 0, theKeyStream, 0, theKeyStream.length);
        theIndex = e.theIndex;
        theResetState = e.theResetState;
    }
}

