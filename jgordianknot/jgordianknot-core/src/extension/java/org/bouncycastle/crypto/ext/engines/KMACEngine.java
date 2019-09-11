package org.bouncycastle.crypto.ext.engines;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.ext.macs.KMAC;
import org.bouncycastle.crypto.ext.params.KeccakParameters.Builder;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Memoable;

/**
 * KMAC used as a stream Cipher.
 */
public class KMACEngine
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
     * Underlying kMac.
     */
    private final KMAC theKMAC;

    /**
     * Reset state.
     */
    private KMAC theResetState;

    /**
     * Constructor.
     * @param pMac the underlying mac
     */
    public KMACEngine(final KMAC pMac) {
        theKMAC = pMac;
        theKeyStream = new byte[pMac.getMacSize()];
    }

    /**
     * Constructor.
     * @param pSource the source engine
     */
    private KMACEngine(final KMACEngine pSource) {
        theKMAC = new KMAC(pSource.theKMAC);
        theKeyStream = new byte[theKMAC.getByteLength()];
        reset(pSource);
    }

    /**
     * initialise a Blake2X cipher.
     * @param forEncryption whether or not we are for encryption.
     * @param params the parameters required to set up the cipher.
     * @exception IllegalArgumentException if the params argument is inappropriate.
     */
    public void init(final boolean forEncryption,
                     final CipherParameters params) {
        /*
         * Blake2X encryption and decryption is completely symmetrical, so the 'forEncryption' is
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
        theKMAC.init(myBuilder.build());
        if (newIV != null) {
            theKMAC.update(newIV, 0, newIV.length);
        }

        /* Save reset state */
        theResetState = theKMAC.copy();

        /* Initialise the stream block */
        theIndex = 0;
        makeStreamBlock();
    }

    @Override
    public String getAlgorithmName() {
        return theKMAC.getAlgorithmName();
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
            theKMAC.reset(theResetState);
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
        theKMAC.doOutput(theKeyStream, 0, theKeyStream.length);
    }

    @Override
    public KMACEngine copy() {
        return new KMACEngine(this);
    }

    @Override
    public void reset(final Memoable pState) {
        final KMACEngine e = (KMACEngine) pState;
        if (theKeyStream.length != e.theKeyStream.length) {
            throw new IllegalArgumentException();
        }
        theKMAC.reset(e.theKMAC);
        System.arraycopy(e.theKeyStream, 0, theKeyStream, 0, theKeyStream.length);
        theIndex = e.theIndex;
        theResetState = e.theResetState;
    }
}

