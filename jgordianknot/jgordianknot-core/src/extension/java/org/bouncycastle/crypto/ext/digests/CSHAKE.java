package org.bouncycastle.crypto.ext.digests;

import org.bouncycastle.crypto.ext.params.KeccakParameters;
import org.bouncycastle.util.Arrays;

/**
 * Customizable SHAKE function.
 */
public class CSHAKE
        extends SHAKE {
    /**
     * Padding.
     */
    private static final byte[] padding = new byte[100];

    /**
     * NameSpace.
     */
    private final byte[] nameSpace;

    /**
     * Customisation.
     */
    private byte[] diff;

    /**
     * Base constructor.
     *
     * @param bitLength bit length of the underlying SHAKE function, 128 or 256.
     * @param N the function name string, note this is reserved for use by NIST. Avoid using it if not required.
     */
    public CSHAKE(final int bitLength,
                  final byte[] N) {
        super(bitLength);
        nameSpace = Arrays.clone(N);
        buildDiff(null);
        diffPadAndAbsorb();
    }

    /**
     * Copy constructor.
     * @param pSource the source for the copy
     */
    public CSHAKE(final CSHAKE pSource) {
        super(pSource);
        nameSpace = Arrays.clone(pSource.nameSpace);
        diff = Arrays.clone(pSource.diff);
    }

    /**
     * Build the diff construct.
     *
     * @param S the customization string - available for local use.
     */
    private void buildDiff(final byte[] S) {
        if ((nameSpace == null || nameSpace.length == 0)
                && (S == null || S.length == 0)) {
            diff = null;
        } else {
            diff = Arrays.concatenate(leftEncode(rate / 8), encodeString(nameSpace), encodeString(S));
        }
    }

    /**
     * Process the diff.
     */
    private void diffPadAndAbsorb()  {
        if (diff != null) {
            final int blockSize = rate / 8;
            absorb(diff, 0, diff.length);

            int required = blockSize - (diff.length % blockSize);

            while (required > padding.length) {
                absorb(padding, 0, padding.length);
                required -= padding.length;
            }

            absorb(padding, 0, required);
        }
    }

    @Override
    public void init(final KeccakParameters pParams) {
        /* Build the new diff */
        buildDiff(pParams.getPersonalisation());

        /* Process remaining parameters */
        super.init(pParams);
    }

    /**
     * Encode a string.
     * @param str the string to encode
     * @return the encoded string
     */
    private byte[] encodeString(final byte[] str) {
        if (str == null || str.length == 0) {
            return leftEncode(0);
        }

        return Arrays.concatenate(leftEncode(str.length * 8L), str);
    }

    /**
     * Left encode a length.
     * @param strLen the length to encode
     * @return the encoded length
     */
    public static byte[] leftEncode(final long strLen) {
        byte n = 1;

        long v = strLen;
        while ((v >>= 8) != 0) {
            n++;
        }

        final byte[] b = new byte[n + 1];

        b[0] = n;

        for (int i = 1; i <= n; i++) {
            b[i] = (byte) (strLen >> (8 * (n - i)));
        }

        return b;
    }

    @Override
    public int doOutput(final byte[] out,
                        final int outOff,
                        final int outLen) {
        /* If we are pure SHAKE */
        if (diff == null) {
            return super.doOutput(out, outOff, outLen);
        }

        /* If we are starting outputting */
        if (theXofRemaining == -1L) {
            /* Absorb trailer */
            absorbBits(0x00, 2);

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

    @Override
    public void reset() {
        super.reset();
        diffPadAndAbsorb();
    }
}

