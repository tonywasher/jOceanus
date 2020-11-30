package org.bouncycastle.crypto.ext.modes;

import java.io.ByteArrayOutputStream;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.gcm.GCMMultiplier;
import org.bouncycastle.crypto.modes.gcm.Tables4kGCMMultiplier;
import org.bouncycastle.crypto.params.AEADParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/**
 * GCM-SIV Mode.
 * <p>It should be noted that the limit of 2<sup>36</sup> bytes is not checked. This is because all bytes are
 * cached in a <b>ByteArrayOutputStream</b> which has a limit of 2<sup>31</sup> bytes. While it would be possible to
 * implement a list of <b>ByteArrayOutputStream</b>s to enable data up to the specified limit, only 2<sup>31</sup>
 * bytes can be processed on the <b>doFinal</b>() call. Since all bytes must be processed on the <b>doFinal</b>()
 * call,  that limit takes precedence. No attempt is made to process more than 2<sup>31</sup> AEAD bytes.
 */
public class GCMSIVBlockCipher
        implements AEADBlockCipher {
    /**
     * The buffer length.
     */
    private static final int BUFLEN = 16;

    /**
     * The halfBuffer length.
     */
    private static final int HALFBUFLEN = BUFLEN >> 1;

    /**
     * The nonce length.
     */
    private static final int NONCELEN = 12;

    /**
     * The top bit mask.
     */
    private static final byte MASK = (byte) 0b10000000;

    /**
     * The addition constant.
     */
    private static final byte ADD = (byte) 0b11100001;

    /**
     * The aeadDataStream.
     */
    private  ExposedByteArrayOutputStream theAEAD = new ExposedByteArrayOutputStream();

    /**
     * The plainDataStream.
     */
    private ExposedByteArrayOutputStream thePlain = new ExposedByteArrayOutputStream();

    /**
     * The encryptedDataStream (decryption only).
     */
    private ExposedByteArrayOutputStream theEncData = new ExposedByteArrayOutputStream();

    /**
     * The cipher.
     */
    private final BlockCipher theCipher;

    /**
     * The multiplier.
     */
    private final GCMMultiplier theMultiplier;

    /**
     * Are we encrypting?
     */
    private boolean forEncryption;

    /**
     * The initialAEAD.
     */
    private byte[] theInitialAEAD;

    /**
     * The nonce.
     */
    private byte[] theNonce;

    /**
     * Constructor.
     */
    public GCMSIVBlockCipher() {
        this(new AESEngine());
    }

    /**
     * Constructor.
     * @param pCipher the underlying cipher
     */
    public GCMSIVBlockCipher(final BlockCipher pCipher) {
        this(pCipher, new Tables4kGCMMultiplier());
    }

    /**
     * Constructor.
     * @param pCipher the underlying cipher
     * @param pMultiplier the multiplier
     */
    public GCMSIVBlockCipher(final BlockCipher pCipher,
                             final GCMMultiplier pMultiplier) {
        /* Ensure that the cipher is the correct size */
        if (pCipher.getBlockSize() != BUFLEN) {
            throw new IllegalArgumentException("Cipher required with a block size of " + BUFLEN + ".");
        }

        /* Store parameters */
        theCipher = pCipher;
        theMultiplier = pMultiplier;
    }

    @Override
    public BlockCipher getUnderlyingCipher() {
        return theCipher;
    }

    @Override
    public void init(final boolean pEncrypt,
                     final CipherParameters cipherParameters) throws IllegalArgumentException {
        /* Set defaults */
        byte[] myInitialAEAD = null;
        byte[] myNonce = null;
        KeyParameter myKey = null;

        /* Access parameters */
        if (cipherParameters instanceof AEADParameters) {
            final AEADParameters myAEAD = (AEADParameters) cipherParameters;
            myInitialAEAD = myAEAD.getAssociatedText();
            myNonce = myAEAD.getNonce();
            myKey = myAEAD.getKey();
        } else if (cipherParameters instanceof ParametersWithIV) {
            final ParametersWithIV myParms = (ParametersWithIV) cipherParameters;
            myNonce = myParms.getIV();
            myKey = (KeyParameter) myParms.getParameters();
        } else {
            throw new IllegalArgumentException("invalid parameters passed to GCM_SIV");
        }

        /* Check nonceSize */
        if (myNonce == null || myNonce.length != NONCELEN) {
            throw new IllegalArgumentException("Invalid nonce");
        }

        /* Check keysize */
        if (myKey == null
            || (myKey.getKey().length != BUFLEN
                && myKey.getKey().length != (BUFLEN << 1))) {
            throw new IllegalArgumentException("Invalid key");
        }

        /* Reset details */
        forEncryption = pEncrypt;
        theInitialAEAD = myInitialAEAD;
        theNonce = myNonce;
        resetStreams();

        /* Initialise the keys */
        deriveKeys(myKey);
    }

    @Override
    public String getAlgorithmName() {
        return theCipher.getAlgorithmName() + "-GCM-SIV";
    }

    @Override
    public void processAADByte(final byte pByte) {
        /* Check that we have initialised */
        checkInitialised();

        /* Store the data */
        theAEAD.write(pByte);
    }

    @Override
    public void processAADBytes(final byte[] pData,
                                final int pOffset,
                                final int pLen) {
        /* Check that we have initialised */
        checkInitialised();

        /* Check input buffer */
        if (bufLength(pData) < (pLen + pOffset)) {
            throw new DataLengthException("Input buffer too short.");
        }

        /* Store the data */
        theAEAD.write(pData, pOffset, pLen);
    }

    @Override
    public int processByte(final byte pByte,
                           final byte[] pOutput,
                           final int pOutOffset) throws DataLengthException {
        /* Check that we have initialised */
        checkInitialised();

        /* Store the data */
        if (forEncryption) {
            thePlain.write(pByte);
        } else {
            theEncData.write(pByte);
        }

        /* No data returned */
        return 0;
    }

    @Override
    public int processBytes(final byte[] pData,
                            final int pOffset,
                            final int pLen,
                            final byte[] pOutput,
                            final int pOutOffset) throws DataLengthException {
        /* Check that we have initialised */
        checkInitialised();

        /* Check input buffer */
        if (bufLength(pData) < (pLen + pOffset)) {
            throw new DataLengthException("Input buffer too short.");
        }

        /* Store the data */
        if (forEncryption) {
            thePlain.write(pData, pOffset, pLen);
        } else {
            theEncData.write(pData, pOffset, pLen);
        }

        /* No data returned */
        return 0;
    }

    @Override
    public int doFinal(final byte[] pOutput,
                       final int pOffset) throws IllegalStateException, InvalidCipherTextException {
        /* Check that we have initialised */
        checkInitialised();

        /* Check output buffer */
        if (bufLength(pOutput) < (pOffset + getOutputSize(0))) {
            throw new OutputLengthException("Output buffer too short.");
        }

        /* If we are encrypting */
        if (forEncryption) {
            /* Derive the tag */
            final byte[] myTag = calculateTag();

            /* encrypt the plain text */
            final int myDataLen = BUFLEN + encryptPlain(myTag, pOutput, pOffset);

            /* Add the tag to the output */
            System.arraycopy(myTag, 0, pOutput, pOffset + thePlain.size(), BUFLEN);

            /* Reset the streams */
            resetStreams();
            return myDataLen;

            /* else we are decrypting */
        } else {
            /* decrypt to plain text */
            final byte[] myExpected = decryptPlain();
            final int myDataLen = thePlain.size();

            /* Derive and check the tag */
            final byte[] myTag = calculateTag();
            if (!Arrays.constantTimeAreEqual(myTag, myExpected)) {
                reset();
                throw new InvalidCipherTextException("mac check failed");
            }

            /* Release plain text */
            final byte[] mySrc = thePlain.getBuffer();
            System.arraycopy(mySrc, 0, pOutput, pOffset, myDataLen);

            /* Reset the streams */
            resetStreams();
            return myDataLen;
        }
    }

    @Override
    public byte[] getMac() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getUpdateOutputSize(final int pLen) {
        return 0;
    }

    @Override
    public int getOutputSize(final int pLen) {
        if (forEncryption) {
            return pLen + thePlain.size() + BUFLEN;
        }
        final int myCurr = pLen + theEncData.size();
        return myCurr > BUFLEN ? myCurr - BUFLEN : 0;
    }

    @Override
    public void reset() {
        resetStreams();
        theCipher.reset();
    }

    /**
     * Reset Streams.
     */
    private void resetStreams() {
        theAEAD = new ExposedByteArrayOutputStream();
        thePlain = new ExposedByteArrayOutputStream();
        theEncData = forEncryption ? null : new ExposedByteArrayOutputStream();
        if (theInitialAEAD != null) {
            theAEAD.write(theInitialAEAD, 0, theInitialAEAD.length);
        }
    }

    /**
     * Check initialised status.
     */
    private void checkInitialised() {
        if (theNonce == null) {
            throw new IllegalStateException("Cipher not initialised");
        }
    }

    /**
     * Obtain buffer length (allowing for null).
     * @param pBuffer the buffere
     * @return the length
     */
    private static int bufLength(final byte[] pBuffer) {
        return pBuffer == null ? 0 : pBuffer.length;
    }

    /**
     * encrypt data stream.
     * @param pCounter the counter
     * @param pTarget the target buffer
     * @param pOffset the target offset
     * @return the length of data encrypted
     */
    private int encryptPlain(final byte[] pCounter,
                             final byte[] pTarget,
                             final int pOffset) {
        /* Access buffer and length */
        final byte[] mySrc = thePlain.getBuffer();
        final byte[] myCounter = Arrays.clone(pCounter);
        myCounter[BUFLEN - 1] |= MASK;
        final byte[] myMask = new byte[BUFLEN];
        int myRemaining = thePlain.size();
        int myOff = 0;

        /* While we have data to process */
        while (myRemaining > 0) {
            /* Generate the next mask */
            theCipher.processBlock(myCounter, 0, myMask, 0);

            /* Xor data into mask */
            final int myLen = Math.min(BUFLEN, myRemaining);
            xorBlock(myMask, mySrc, myOff, myLen);

            /* Copy encrypted data to output */
            System.arraycopy(myMask, 0, pTarget, pOffset + myOff, myLen);

            /* Adjust counters */
            myRemaining -= myLen;
            myOff += myLen;
            incrementCounter(myCounter);
       }

        /* Return the amount of data processed */
        return thePlain.size();
    }

    /**
     * decrypt data stream.
     * @return the expected tag
     * @throws InvalidCipherTextException on data too short
     */
    private byte[] decryptPlain() throws InvalidCipherTextException {
        /* Access buffer and length */
        final byte[] mySrc = theEncData.getBuffer();
        int myRemaining = theEncData.size() - BUFLEN;

        /* Check for insufficient data */
        if (myRemaining < 0) {
            throw new InvalidCipherTextException("Data too short");
        }

        /* Access counter */
        final byte[] myTag = Arrays.copyOfRange(mySrc, myRemaining, myRemaining + BUFLEN);
        final byte[] myCounter = Arrays.clone(myTag);
        myCounter[BUFLEN - 1] |= MASK;
        final byte[] myMask = new byte[BUFLEN];
        int myOff = 0;

        /* While we have data to process */
        while (myRemaining > 0) {
            /* Generate the next mask */
            theCipher.processBlock(myCounter, 0, myMask, 0);

            /* Xor data into mask */
            final int myLen = Math.min(BUFLEN, myRemaining);
            xorBlock(myMask, mySrc, myOff, myLen);

            /* Write data to plain dataStream */
            thePlain.write(myMask, 0, myLen);

            /* Adjust counters */
            myRemaining -= myLen;
            myOff += myLen;
            incrementCounter(myCounter);
        }

        /* Return the expected tag */
        return myTag;
    }

    /**
     * calculate tag.
     * @return the calculated tag
     */
    private byte[] calculateTag() {
        /* calculate polyVal */
        final byte[] myVal = polyVal();
        final byte[] myResult = new byte[BUFLEN];

        /* Fold in the nonce */
        for (int i = 0; i < NONCELEN; i++) {
            myVal[i] ^= theNonce[i];
        }

        /* Clear top bit */
        myVal[BUFLEN - 1] &= (MASK - 1);

        /* Calculate tag and return it */
        theCipher.processBlock(myVal, 0, myResult, 0);
        return myResult;
    }

    /**
     * calculate polyVAL.
     * @return the calculated value
     */
    private byte[] polyVal() {
        /* Create value and result buffer */
        final byte[] myVal = new byte[BUFLEN];
        final byte[] myResult = new byte[BUFLEN];

        /* Hash the constituent parts */
        gHashStream(myVal, theAEAD);
        gHashStream(myVal, thePlain);
        gHashLengths(myVal);

        /* Calculate result and return it */
        fillReverse(myVal, 0, BUFLEN, myResult);
        return myResult;
    }

    /**
     * gHash data stream.
     * @param pCurrent the current value
     * @param pStream the buffer to process
     */
    private void gHashStream(final byte[] pCurrent,
                             final ExposedByteArrayOutputStream pStream) {
        /* Access buffer and length */
        final byte[] mySrc = pStream.getBuffer();
        final byte[] myIn = new byte[BUFLEN];
        int myLen = pStream.size();
        int myOff = 0;

        /* While we have full blocks */
        while (myLen >= BUFLEN) {
            /* Access the next data */
            fillReverse(mySrc, myOff, BUFLEN, myIn);
            myLen -= BUFLEN;
            myOff += BUFLEN;

            /* hash value */
            gHASH(pCurrent, myIn);
        }

        /* If we have remaining data */
        if (myLen > 0) {
            /* Access the next data */
            Arrays.fill(myIn, (byte) 0);
            fillReverse(mySrc, myOff, myLen, myIn);

            /* hash value */
            gHASH(pCurrent, myIn);
        }
    }

    /**
     * process lengths.
     * @param pCurrent the current value
     */
    private void gHashLengths(final byte[] pCurrent) {
        /* Create reversed bigEndian buffer to keep it simple */
        final byte[] myIn = new byte[BUFLEN];
        Pack.longToBigEndian(Byte.SIZE * (long) thePlain.size(), myIn, 0);
        Pack.longToBigEndian(Byte.SIZE * (long) theAEAD.size(), myIn, Long.BYTES);

        /* hash value */
        gHASH(pCurrent, myIn);
    }

    /**
     * perform the next GHASH step.
     * @param pCurr the current value
     * @param pNext the next value
     */
    private void gHASH(final byte[] pCurr,
                       final byte[] pNext) {
        xorBlock(pCurr, pNext);
        theMultiplier.multiplyH(pCurr);
    }

    /**
     * Byte reverse a buffer.
     * @param pInput the input buffer
     * @param pOffset the offset
     * @param pLength the length of data (<= BUFLEN)
     * @param pOutput the output buffer
     */
    private static void fillReverse(final byte[] pInput,
                                    final int pOffset,
                                    final int pLength,
                                    final byte[] pOutput) {
        /* Loop through the buffer */
        for (int i = 0, j = BUFLEN - 1; i < pLength; i++, j--) {
            /* Copy byte */
            pOutput[j] = pInput[pOffset + i];
        }
    }

    /**
     * xor a full block buffer.
     * @param pLeft the left operand and result
     * @param pRight the right operand
     */
    private static void xorBlock(final byte[] pLeft,
                                 final byte[] pRight) {
        /* Loop through the bytes */
        for (int i = 0; i < BUFLEN; i++) {
            pLeft[i] ^= pRight[i];
        }
    }

    /**
     * xor a partial block buffer.
     * @param pLeft the left operand and result
     * @param pRight the right operand
     * @param pOffset the offset in the right operand
     * @param pLength the length of data in the right operand
     */
    private static void xorBlock(final byte[] pLeft,
                                 final byte[] pRight,
                                 final int pOffset,
                                 final int pLength) {
        /* Loop through the bytes */
        for (int i = 0; i < pLength; i++) {
            pLeft[i] ^= pRight[i + pOffset];
        }
    }

    /**
     * increment the counter.
     * @param pCounter the counter to increment
     */
    private static void incrementCounter(final byte[] pCounter) {
        /* Loop through the bytes incrementing counter */
        for (int i = 0; i < Integer.BYTES; i++) {
            if (++pCounter[i] != 0) {
                break;
            }
        }
    }

    /**
     * multiply by X.
     * @param pValue the value to adjust
     */
    private static void mulX(final byte[] pValue) {
        /* Loop through the bytes */
        byte myMask = (byte) 0;
        for (int i = 0; i < BUFLEN; i++) {
            final byte myValue = pValue[i];
            pValue[i] = (byte) (((myValue >> 1) & ~MASK) | myMask);
            myMask = (myValue & 1) == 0 ? 0 : MASK;
        }

        /* Xor in addition if last bit was set */
        if (myMask != 0) {
            pValue[0] ^= ADD;
        }
    }

    /**
     * Derive Keys.
     * @param pKey the keyGeneration key
     */
    private void deriveKeys(final KeyParameter pKey) {
        /* Create the buffers */
        final byte[] myIn = new byte[BUFLEN];
        final byte[] myOut = new byte[BUFLEN];
        final byte[] myResult = new byte[BUFLEN];
        final byte[] myEncKey = new byte[pKey.getKey().length];

        /* Prepare for encryption */
        System.arraycopy(theNonce, 0, myIn, BUFLEN - NONCELEN, NONCELEN);
        theCipher.init(true, pKey);

        /* Derive authentication key */
        int myOff = 0;
        theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myResult, myOff, HALFBUFLEN);
        myIn[0]++;
        myOff += HALFBUFLEN;
        theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myResult, myOff, HALFBUFLEN);

        /* Derive encryption key */
        myIn[0]++;
        myOff = 0;
        theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myEncKey, myOff, HALFBUFLEN);
        myIn[0]++;
        myOff += HALFBUFLEN;
        theCipher.processBlock(myIn, 0, myOut, 0);
        System.arraycopy(myOut, 0, myEncKey, myOff, HALFBUFLEN);

        /* If we have a 32byte key */
        if (myEncKey.length == BUFLEN << 1) {
            /* Derive remainder of encryption key */
            myIn[0]++;
            myOff += HALFBUFLEN;
            theCipher.processBlock(myIn, 0, myOut, 0);
            System.arraycopy(myOut, 0, myEncKey, myOff, HALFBUFLEN);
            myIn[0]++;
            myOff += HALFBUFLEN;
            theCipher.processBlock(myIn, 0, myOut, 0);
            System.arraycopy(myOut, 0, myEncKey, myOff, HALFBUFLEN);
        }

        /* Initialise the Cipher */
        theCipher.init(true, new KeyParameter(myEncKey));

        /* Initialise the multiplier */
        fillReverse(myResult, 0, BUFLEN, myOut);
        mulX(myOut);
        theMultiplier.init(myOut);
    }

    /**
     * Exposed ByteArrayOutputStream, allowing direct access to buffer.
     */
    private static class ExposedByteArrayOutputStream
            extends ByteArrayOutputStream {
        ExposedByteArrayOutputStream() {
        }

        byte[] getBuffer() {
            return this.buf;
        }
    }
}
