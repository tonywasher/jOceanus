/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.ext.digests;

import io.github.tonywasher.joceanus.gordianknot.impl.ext.params.GordianSkeinParameters;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.crypto.params.SkeinParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Implementation of the Skein family of parameterised hash functions in 256, 512 and 1024 bit block
 * sizes, based on the {@link ThreefishEngine Threefish} tweakable block cipher.
 * <p>
 * This is the 1.3 version of Skein defined in the Skein hash function submission to the NIST SHA-3
 * competition in October 2010.
 * <p>
 * Skein was designed by Niels Ferguson - Stefan Lucks - Bruce Schneier - Doug Whiting - Mihir
 * Bellare - Tadayoshi Kohno - Jon Callas - Jesse Walker.
 * <p>
 * This implementation is the basis for SkeinDigest and {@link SkeinMac}, implementing the
 * parameter based configuration system that allows Skein to be adapted to multiple applications. <br>
 * Initialising the engine with {@link GordianSkeinParameters} allows standard and arbitrary parameters to
 * be applied during the Skein hash function.
 * <p>
 * Implemented:
 * <ul>
 * <li>256, 512 and 1024 bit internal states.</li>
 * <li>Full 96 bit input length.</li>
 * <li>Parameters defined in the Skein specification, and arbitrary other pre and post message
 * parameters.</li>
 * <li>Arbitrary output size in 1 byte intervals.</li>
 * </ul>
 * <p>
 * Not implemented:
 * <ul>
 * <li>Sub-byte length input (bit padding).</li>
 * <li>Tree hashing.</li>
 * </ul>
 *
 * @see GordianSkeinParameters
 */
@SuppressWarnings("checkstyle:MagicNumber")
public class GordianSkeinBase
        implements Memoable {
    /**
     * 256 bit block size - Skein 256.
     */
    public static final int SKEIN_256 = ThreefishEngine.BLOCKSIZE_256;
    /**
     * 512 bit block size - Skein 512.
     */
    public static final int SKEIN_512 = ThreefishEngine.BLOCKSIZE_512;

    /**
     * 1024 bit block size - Skein 1024.
     */
    public static final int SKEIN_1024 = ThreefishEngine.BLOCKSIZE_1024;

    /**
     * Configuration. Minimal at present, but more complex when tree hashing is implemented
     */
    static class Configuration {
        /**
         * Configuration bytes.
         */
        private byte[] bytes = new byte[32];

        /**
         * Constructor.
         *
         * @param outputSizeBits the output size in bits
         */
        Configuration(final long outputSizeBits) {
            // 0..3 = ASCII SHA3
            bytes[0] = (byte) 'S';
            bytes[1] = (byte) 'H';
            bytes[2] = (byte) 'A';
            bytes[3] = (byte) '3';

            // 4..5 = version number in LSB order
            bytes[4] = 1;
            bytes[5] = 0;

            // 8..15 = output length
            Pack.longToLittleEndian(outputSizeBits, bytes, 8);
        }

        /**
         * Obtain bytes.
         *
         * @return the bytes.
         */
        public byte[] getBytes() {
            return bytes;
        }
    }

    /**
     * Parameter class.
     */
    public static class Parameter {
        /**
         * The type.
         */
        private final int type;

        /**
         * The value.
         */
        private final byte[] value;

        /**
         * Constructor.
         *
         * @param pType  the type
         * @param pValue the value
         */
        public Parameter(final int pType, final byte[] pValue) {
            this.type = pType;
            this.value = pValue;
        }

        /**
         * Obtain the type.
         *
         * @return the type
         */
        public int getType() {
            return type;
        }

        /**
         * Obtain the value.
         *
         * @return the value
         */
        public byte[] getValue() {
            return value;
        }
    }

    /**
     * The parameter type for the Skein key.
     */
    private static final int PARAM_TYPE_KEY = 0;

    /**
     * The parameter type for the Skein configuration block.
     */
    private static final int PARAM_TYPE_CONFIG = 4;

    /**
     * The parameter type for the message.
     */
    private static final int PARAM_TYPE_MESSAGE = 48;

    /**
     * The parameter type for the output transformation.
     */
    private static final int PARAM_TYPE_OUTPUT = 63;

    /**
     * Precalculated UBI(CFG) states for common state/output combinations without key or other
     * pre-message params.
     */
    private static final Map<Integer, long[]> INITIAL_STATES = new HashMap<>();

    static {
        // From Appendix C of the Skein 1.3 NIST submission
        final long[] skein256o128 = {
                0xe1111906964d7260L,
                0x883daaa77c8d811cL,
                0x10080df491960f7aL,
                0xccf7dde5b45bc1c2L
        };
        initialState(SKEIN_256, 128, skein256o128);

        final long[] skein256o160 = {
                0x1420231472825e98L,
                0x2ac4e9a25a77e590L,
                0xd47a58568838d63eL,
                0x2dd2e4968586ab7dL
        };
        initialState(SKEIN_256, 160, skein256o160);

        final long[] skein256o224 = {
                0xc6098a8c9ae5ea0bL,
                0x876d568608c5191cL,
                0x99cb88d7d7f53884L,
                0x384bddb1aeddb5deL
        };
        initialState(SKEIN_256, 224, skein256o224);

        final long[] skein256o256 = {
                0xfc9da860d048b449L,
                0x2fca66479fa7d833L,
                0xb33bc3896656840fL,
                0x6a54e920fde8da69L
        };
        initialState(SKEIN_256, 256, skein256o256);

        final long[] skein512o128 = {
                0xa8bc7bf36fbf9f52L,
                0x1e9872cebd1af0aaL,
                0x309b1790b32190d3L,
                0xbcfbb8543f94805cL,
                0x0da61bcd6e31b11bL,
                0x1a18ebead46a32e3L,
                0xa2cc5b18ce84aa82L,
                0x6982ab289d46982dL
        };
        initialState(SKEIN_512, 128, skein512o128);

        final long[] skein512o160 = {
                0x28b81a2ae013bd91L,
                0xc2f11668b5bdf78fL,
                0x1760d8f3f6a56f12L,
                0x4fb747588239904fL,
                0x21ede07f7eaf5056L,
                0xd908922e63ed70b8L,
                0xb8ec76ffeccb52faL,
                0x01a47bb8a3f27a6eL
        };
        initialState(SKEIN_512, 160, skein512o160);

        final long[] skein512o224 = {
                0xccd0616248677224L,
                0xcba65cf3a92339efL,
                0x8ccd69d652ff4b64L,
                0x398aed7b3ab890b4L,
                0x0f59d1b1457d2bd0L,
                0x6776fe6575d4eb3dL,
                0x99fbc70e997413e9L,
                0x9e2cfccfe1c41ef7L
        };
        initialState(SKEIN_512, 224, skein512o224);

        final long[] skein512o384 = {
                0xa3f6c6bf3a75ef5fL,
                0xb0fef9ccfd84faa4L,
                0x9d77dd663d770cfeL,
                0xd798cbf3b468fddaL,
                0x1bc4a6668a0e4465L,
                0x7ed7d434e5807407L,
                0x548fc1acd4ec44d6L,
                0x266e17546aa18ff8L
        };
        initialState(SKEIN_512, 384, skein512o384);

        final long[] skein512o512 = {
                0x4903adff749c51ceL,
                0x0d95de399746df03L,
                0x8fd1934127c79bceL,
                0x9a255629ff352cb1L,
                0x5db62599df6ca7b0L,
                0xeabe394ca9d5c3f4L,
                0x991112c71a75b523L,
                0xae18a40b660fcc33L
        };
        initialState(SKEIN_512, 512, skein512o512);
    }

    private static void initialState(final int blockSize, final int outputSize, final long[] state) {
        INITIAL_STATES.put(variantIdentifier(blockSize / 8, outputSize / 8), state);
    }

    private static Integer variantIdentifier(final int blockSizeBytes, final int outputSizeBytes) {
        return Integers.valueOf((outputSizeBytes << 16) | blockSizeBytes);
    }

    /**
     * Ubi Tweak class.
     */
    private static class UbiTweak {
        /**
         * Point at which position might overflow long, so switch to add with carry logic.
         */
        private static final long LOW_RANGE = Long.MAX_VALUE - Integer.MAX_VALUE;

        /**
         * Bit 127 = final.
         */
        private static final long T1_FINAL = 1L << 63;

        /**
         * Bit 126 = first.
         */
        private static final long T1_FIRST = 1L << 62;

        /**
         * UBI uses a 128 bit tweak.
         */
        private long[] tweak = new long[2];

        /**
         * Whether 64 bit position exceeded.
         */
        private boolean extendedPosition;

        /**
         * Constructor.
         */
        UbiTweak() {
            reset();
        }

        /**
         * Reset according to source.
         *
         * @param pTweak the source
         */
        public void reset(final UbiTweak pTweak) {
            this.tweak = Arrays.clone(pTweak.tweak, this.tweak);
            this.extendedPosition = pTweak.extendedPosition;
        }

        /**
         * Reset.
         */
        public void reset() {
            tweak[0] = 0;
            tweak[1] = 0;
            extendedPosition = false;
            setFirst(true);
        }

        public void setType(final int type) {
            // Bits 120..125 = type
            tweak[1] = (tweak[1] & 0xFFFFFFC000000000L) | ((type & 0x3FL) << 56);
        }

        public int getType() {
            return (int) ((tweak[1] >>> 56) & 0x3FL);
        }

        public void setTreeLocation(final int level, final int hiOffset, final long loOffset) {
            // Bits 112..119 = level
            tweak[1] = (tweak[1] & 0xFF00FFC000000000L) | ((level & 0xFFL) << 48);

            /* Calculate high and lo parts of offset */
            tweak[0] = loOffset;
            tweak[1] |= hiOffset;

            /* Determine whether we have an extended position */
            extendedPosition = loOffset < 0 || loOffset > LOW_RANGE || hiOffset != 0;
        }

        public int getLevel() {
            return (int) ((tweak[1] >>> 48) & 0xFFL);
        }

        public void setFirst(final boolean first) {
            if (first) {
                tweak[1] |= T1_FIRST;
            } else {
                tweak[1] &= ~T1_FIRST;
            }
        }

        public boolean isFirst() {
            return ((tweak[1] & T1_FIRST) != 0);
        }

        public void setFinal(final boolean last) {
            if (last) {
                tweak[1] |= T1_FINAL;
            } else {
                tweak[1] &= ~T1_FINAL;
            }
        }

        public boolean isFinal() {
            return ((tweak[1] & T1_FINAL) != 0);
        }

        /**
         * Advances the position in the tweak by the specified value.
         *
         * @param advance the advance count
         */
        public void advancePosition(final int advance) {
            // Bits 0..95 = position
            if (extendedPosition) {
                final long[] parts = new long[3];
                parts[0] = tweak[0] & 0xFFFFFFFFL;
                parts[1] = (tweak[0] >>> 32) & 0xFFFFFFFFL;
                parts[2] = tweak[1] & 0xFFFFFFFFL;

                long carry = advance;
                for (int i = 0; i < parts.length; i++) {
                    carry += parts[i];
                    parts[i] = carry;
                    carry >>>= 32;
                }
                tweak[0] = ((parts[1] & 0xFFFFFFFFL) << 32) | (parts[0] & 0xFFFFFFFFL);
                tweak[1] = (tweak[1] & 0xFFFFFFFF00000000L) | (parts[2] & 0xFFFFFFFFL);
            } else {
                long position = tweak[0];
                position += advance;
                tweak[0] = position;
                if (position > LOW_RANGE) {
                    extendedPosition = true;
                }
            }
        }

        public long[] getWords() {
            return tweak;
        }

        public String toString() {
            return getType() + " first: " + isFirst() + ", final: " + isFinal();
        }

    }

    /**
     * The Unique Block Iteration chaining mode.
     */
    private class UBI {
        /**
         * The tweak.
         */
        private final UbiTweak tweak = new UbiTweak();

        /**
         * Buffer for the current block of message data.
         */
        private byte[] currentBlock;

        /**
         * Offset into the current message block.
         */
        private int currentOffset;

        /**
         * Buffer for message words for feedback into encrypted block.
         */
        private long[] message;

        UBI(final int blockSize) {
            currentBlock = new byte[blockSize];
            message = new long[currentBlock.length / 8];
        }

        public void reset(final UBI pUbi) {
            currentBlock = Arrays.clone(pUbi.currentBlock, currentBlock);
            currentOffset = pUbi.currentOffset;
            message = Arrays.clone(pUbi.message, this.message);
            tweak.reset(pUbi.tweak);
        }

        public void reset(final int type) {
            tweak.reset();
            tweak.setType(type);
            currentOffset = 0;
        }

        public void setTreeLocation(final int level, final long offSet, final int pShift) {
            int shift = getBlockSize() << 3;
            shift += pShift; //level == 0 ? leafLen : fanOut;
            final int hiOffset = (int) offSet >>> 64 - shift;
            final long loOffset = offSet << shift;
            tweak.setTreeLocation(level, hiOffset, loOffset);
        }

        public void update(final byte[] value, final int offset, final int len, final long[] output) {
            /*
             * Buffer complete blocks for the underlying Threefish cipher, only flushing when there
             * are subsequent bytes (last block must be processed in doFinal() with final=true set).
             */
            int copied = 0;
            while (len > copied) {
                if (currentOffset == currentBlock.length) {
                    processBlock(output);
                    tweak.setFirst(false);
                    currentOffset = 0;
                }

                final int toCopy = Math.min((len - copied), currentBlock.length - currentOffset);
                System.arraycopy(value, offset + copied, currentBlock, currentOffset, toCopy);
                copied += toCopy;
                currentOffset += toCopy;
                tweak.advancePosition(toCopy);
            }
        }

        private void processBlock(final long[] output) {
            threefish.init(true, chain, tweak.getWords());
            for (int i = 0; i < message.length; i++) {
                message[i] = Pack.littleEndianToLong(currentBlock, i * 8);
            }

            threefish.processBlock(message, output);

            for (int i = 0; i < output.length; i++) {
                output[i] ^= message[i];
            }
        }

        public void doFinal(final long[] output) {
            // Pad remainder of current block with zeroes
            for (int i = currentOffset; i < currentBlock.length; i++) {
                currentBlock[i] = 0;
            }

            tweak.setFinal(true);
            processBlock(output);
        }
    }

    /**
     * Underlying Threefish tweakable block cipher.
     */
    private final ThreefishEngine threefish;

    /**
     * Size of the digest output, in bytes.
     */
    private final int outputSizeBytes;

    /**
     * The current chaining/state value.
     */
    private long[] chain;

    /**
     * The initial state value.
     */
    private long[] initialState;

    /**
     * The (optional) key parameter.
     */
    private byte[] key;

    /**
     * Parameters to apply prior to the message.
     */
    private Parameter[] preMessageParameters;

    /**
     * Parameters to apply after the message, but prior to output.
     */
    private Parameter[] postMessageParameters;

    /**
     * The current UBI operation.
     */
    private final UBI ubi;

    /**
     * Buffer for single byte update method.
     */
    private final byte[] singleByte = new byte[1];

    /**
     * The explicit configuration.
     */
    private Configuration theConfig;

    /**
     * Constructs a Skein engine.
     *
     * @param blockSizeBits  the internal state size in bits - one of {@link #SKEIN_256}, {@link #SKEIN_512} or
     *                       {@link #SKEIN_1024}.
     * @param outputSizeBits the output/digest size to produce in bits, which must be an integral number of
     *                       bytes.
     */
    public GordianSkeinBase(final int blockSizeBits, final int outputSizeBits) {
        if (outputSizeBits % 8 != 0) {
            throw new IllegalArgumentException("Output size must be a multiple of 8 bits. :" + outputSizeBits);
        }
        this.outputSizeBytes = outputSizeBits / Byte.SIZE;

        this.threefish = new ThreefishEngine(blockSizeBits);
        this.ubi = new UBI(threefish.getBlockSize());
    }

    /**
     * Creates a SkeinEngine as an exact copy of an existing instance.
     *
     * @param engine the base engine
     */
    public GordianSkeinBase(final GordianSkeinBase engine) {
        this(engine.getBlockSize() * 8, engine.getOutputSize() * 8);
        copyIn(engine);
    }

    private void copyIn(final GordianSkeinBase engine) {
        this.ubi.reset(engine.ubi);
        this.chain = Arrays.clone(engine.chain, this.chain);
        this.initialState = Arrays.clone(engine.initialState, this.initialState);
        this.key = Arrays.clone(engine.key, this.key);
        this.preMessageParameters = clone(engine.preMessageParameters, this.preMessageParameters);
        this.postMessageParameters = clone(engine.postMessageParameters, this.postMessageParameters);
    }

    private static Parameter[] clone(final Parameter[] data, final Parameter[] existing) {
        if (data == null) {
            return null;
        }
        Parameter[] myExisting = existing;
        if ((myExisting == null) || (myExisting.length != data.length)) {
            myExisting = new Parameter[data.length];
        }
        System.arraycopy(data, 0, myExisting, 0, myExisting.length);
        return existing;
    }

    @Override
    public Memoable copy() {
        return new GordianSkeinBase(this);
    }

    @Override
    public void reset(final Memoable other) {
        final GordianSkeinBase s = (GordianSkeinBase) other;
        if ((getBlockSize() != s.getBlockSize()) || (outputSizeBytes != s.outputSizeBytes)) {
            throw new IllegalArgumentException("Incompatible parameters in provided SkeinEngine.");
        }
        copyIn(s);
    }

    /**
     * Get output size.
     *
     * @return the output size
     */
    public int getOutputSize() {
        return outputSizeBytes;
    }

    /**
     * Obtain the block size.
     *
     * @return the block size
     */
    public int getBlockSize() {
        return threefish.getBlockSize();
    }

    /**
     * Set the extended configuration.
     *
     * @param pConfig the extended configuration
     */
    void setConfiguration(final Configuration pConfig) {
        theConfig = pConfig;
    }

    /**
     * Initialises the Skein engine with the provided parameters. See {@link GordianSkeinParameters} for
     * details on the parameterisation of the Skein hash function.
     *
     * @param params the parameters to apply to this engine, or <code>null</code> to use no parameters.
     */
    @SuppressWarnings("unchecked")
    public void init(final GordianSkeinParameters params) {
        this.chain = null;
        this.key = null;
        this.preMessageParameters = null;
        this.postMessageParameters = null;

        if (params != null) {
            final byte[] theKey = params.getKey();
            if (theKey != null && theKey.length < 16) {
                throw new IllegalArgumentException("Skein key must be at least 128 bits.");
            }
            initParams(params.getParameters());
        }
        createInitialState();

        // Initialise message block
        ubiInit(PARAM_TYPE_MESSAGE);
    }

    private void initParams(final Hashtable<Integer, byte[]> parameters) {
        final Enumeration<Integer> keys = parameters.keys();
        final Vector<Parameter> pre = new Vector<>();
        final Vector<Parameter> post = new Vector<>();

        while (keys.hasMoreElements()) {
            final Integer type = keys.nextElement();
            final byte[] value = parameters.get(type);

            if (type == PARAM_TYPE_KEY) {
                this.key = value;
            } else if (type < PARAM_TYPE_MESSAGE) {
                pre.addElement(new Parameter(type, value));
            } else {
                post.addElement(new Parameter(type, value));
            }
        }
        preMessageParameters = new Parameter[pre.size()];
        pre.copyInto(preMessageParameters);
        sort(preMessageParameters);

        postMessageParameters = new Parameter[post.size()];
        post.copyInto(postMessageParameters);
        sort(postMessageParameters);
    }

    private static void sort(final Parameter[] params) {
        if (params == null) {
            return;
        }

        // Insertion sort, for Java 1.1 compatibility
        for (int i = 1; i < params.length; i++) {
            final Parameter param = params[i];
            int hole = i;
            while (hole > 0 && param.getType() < params[hole - 1].getType()) {
                params[hole] = params[hole - 1];
                hole = hole - 1;
            }
            params[hole] = param;
        }
    }

    /**
     * Calculate the initial (pre message block) chaining state.
     */
    private void createInitialState() {
        final boolean xtendedConfig = theConfig != null;
        final long[] precalc = xtendedConfig ? null : INITIAL_STATES.get(variantIdentifier(getBlockSize(), getOutputSize()));
        if ((key == null) && (precalc != null)) {
            // Precalculated UBI(CFG)
            chain = Arrays.clone(precalc);
        } else {
            // Blank initial state
            chain = new long[getBlockSize() / 8];

            // Process key block
            if (key != null) {
                ubiComplete(SkeinParameters.PARAM_TYPE_KEY, key);
            }

            // Process configuration block
            final Configuration myConfig = xtendedConfig ? theConfig : new Configuration(outputSizeBytes * 8L);
            ubiComplete(PARAM_TYPE_CONFIG, myConfig.getBytes());
        }

        // Process additional pre-message parameters
        if (preMessageParameters != null) {
            for (int i = 0; i < preMessageParameters.length; i++) {
                final Parameter param = preMessageParameters[i];
                ubiComplete(param.getType(), param.getValue());
            }
        }
        initialState = Arrays.clone(chain);
    }

    /**
     * Reset the engine to the initial state (with the key and any pre-message parameters , ready to
     * accept message input.
     */
    public void reset() {
        System.arraycopy(initialState, 0, chain, 0, chain.length);

        ubiInit(PARAM_TYPE_MESSAGE);
    }

    void initTreeNode(final int level, final long offset, final int shift) {
        reset();
        ubi.setTreeLocation(level, offset, shift);
    }

    private void ubiComplete(final int type, final byte[] value) {
        ubiInit(type);
        this.ubi.update(value, 0, value.length, chain);
        ubiFinal();
    }

    private void ubiInit(final int type) {
        this.ubi.reset(type);
    }

    private void ubiFinal() {
        ubi.doFinal(chain);
    }

    private void checkInitialised() {
        if (this.ubi == null) {
            throw new IllegalArgumentException("Skein engine is not initialised.");
        }
    }

    /**
     * Update the digest.
     *
     * @param in the byte to update with
     */
    public void update(final byte in) {
        singleByte[0] = in;
        update(singleByte, 0, 1);
    }

    /**
     * Update the digest.
     *
     * @param in    the input buffer
     * @param inOff the input offset
     * @param len   the input length
     */
    public void update(final byte[] in, final int inOff, final int len) {
        checkInitialised();
        ubi.update(in, inOff, len, chain);
    }

    void calculateNode(final byte[] out, final int outOff) {
        checkInitialised();
        if (out.length < (outOff + outputSizeBytes)) {
            throw new OutputLengthException("Output buffer is too short to hold output");
        }

        // Finalise message block
        ubiFinal();

        /* Output the state */
        for (int i = 0; i < chain.length; i++) {
            Pack.longToLittleEndian(chain[i], out, outOff + (i * 8));
        }
    }

    /**
     * Finalise the digest.
     *
     * @param out    the output buffer
     * @param outOff the output offset
     * @return the number of bytes returned
     */
    public int doFinal(final byte[] out, final int outOff) {
        checkInitialised();
        if (out.length < (outOff + outputSizeBytes)) {
            throw new OutputLengthException("Output buffer is too short to hold output");
        }

        // Initiate output
        initiateOutput();

        // Perform the output transform
        final int blockSize = getBlockSize();
        final int blocksRequired = ((outputSizeBytes + blockSize - 1) / blockSize);
        for (int i = 0; i < blocksRequired; i++) {
            final int toWrite = Math.min(blockSize, outputSizeBytes - (i * blockSize));
            output(i, out, outOff + (i * blockSize), toWrite);
        }

        reset();

        return outputSizeBytes;
    }

    void postProcessMessage() {
        // Process additional post-message parameters
        if (postMessageParameters != null) {
            for (int i = 0; i < postMessageParameters.length; i++) {
                final Parameter param = postMessageParameters[i];
                ubiComplete(param.getType(), param.getValue());
            }
        }
    }

    void initiateOutput() {
        // Finalise message block
        ubiFinal();

        // Process additional post-message parameters
        postProcessMessage();
    }

    void restoreForOutput(final byte[] pState) {
        /* Restore the state */
        for (int i = 0; i < chain.length; i++) {
            chain[i] = Pack.littleEndianToLong(pState, i * 8);
        }

        // Initiate output
        initiateOutput();
    }

    void output(final long outputSequence, final byte[] out, final int outOff, final int outputBytes) {
        final byte[] currentBytes = new byte[8];
        Pack.longToLittleEndian(outputSequence, currentBytes, 0);

        // Output is a sequence of UBI invocations all of which use and preserve the pre-output
        // state
        final long[] outputWords = new long[chain.length];
        ubiInit(PARAM_TYPE_OUTPUT);
        this.ubi.update(currentBytes, 0, currentBytes.length, outputWords);
        ubi.doFinal(outputWords);

        final int wordsRequired = ((outputBytes + 8 - 1) / 8);
        for (int i = 0; i < wordsRequired; i++) {
            final int toWrite = Math.min(8, outputBytes - (i * 8));
            if (toWrite == 8) {
                Pack.longToLittleEndian(outputWords[i], out, outOff + (i * 8));
            } else {
                Pack.longToLittleEndian(outputWords[i], currentBytes, 0);
                System.arraycopy(currentBytes, 0, out, outOff + (i * 8), toWrite);
            }
        }
    }
}
