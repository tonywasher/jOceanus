package info.groestl;

public class GroestlDigest {
    /* Constants */
    private static final int ROWS = 8;
    private static final int LENGTHFIELDLEN = ROWS;
    private static final int COLS512 = 8;
    private static final int COLS1024 = 16;
    private static final int SIZE512 = (ROWS * COLS512);
    private static final int SIZE1024 = (ROWS * COLS1024);
    private static final int ROUNDS512 = 10;
    private static final int ROUNDS1024 = 14;

    /* State */
    private byte chaining[][] = new byte[ROWS][COLS1024]; /* the actual state */
    private long block_counter; /* block counter */
    private int hashbitlen; /* output length */
    private byte buffer[] = new byte[SIZE1024]; /* block buffer */
    private int buf_ptr; /* buffer pointer */
    private int bits_in_last_byte; /* number of bits in incomplete byte */
    private int columns; /* number of columns in state */
    private int rounds; /* number of rounds in P and Q */
    private int statesize; /* size of state (ROWS*columns) */

    enum Variant {
        P512(0), Q512(1), P1024(2), Q1024(3);
        private int value;

        Variant(int pValue) {
            value = pValue;
        }

        int getValue() {
            return value;
        }
    }

    /* S-box */
    private final byte S[] = new byte[]
    {
            (byte) 0x63, (byte) 0x7c, (byte) 0x77, (byte) 0x7b, (byte) 0xf2, (byte) 0x6b, (byte) 0x6f, (byte) 0xc5,
            (byte) 0x30, (byte) 0x01, (byte) 0x67, (byte) 0x2b, (byte) 0xfe, (byte) 0xd7, (byte) 0xab, (byte) 0x76,
            (byte) 0xca, (byte) 0x82, (byte) 0xc9, (byte) 0x7d, (byte) 0xfa, (byte) 0x59, (byte) 0x47, (byte) 0xf0,
            (byte) 0xad, (byte) 0xd4, (byte) 0xa2, (byte) 0xaf, (byte) 0x9c, (byte) 0xa4, (byte) 0x72, (byte) 0xc0,
            (byte) 0xb7, (byte) 0xfd, (byte) 0x93, (byte) 0x26, (byte) 0x36, (byte) 0x3f, (byte) 0xf7, (byte) 0xcc,
            (byte) 0x34, (byte) 0xa5, (byte) 0xe5, (byte) 0xf1, (byte) 0x71, (byte) 0xd8, (byte) 0x31, (byte) 0x15,
            (byte) 0x04, (byte) 0xc7, (byte) 0x23, (byte) 0xc3, (byte) 0x18, (byte) 0x96, (byte) 0x05, (byte) 0x9a,
            (byte) 0x07, (byte) 0x12, (byte) 0x80, (byte) 0xe2, (byte) 0xeb, (byte) 0x27, (byte) 0xb2, (byte) 0x75,
            (byte) 0x09, (byte) 0x83, (byte) 0x2c, (byte) 0x1a, (byte) 0x1b, (byte) 0x6e, (byte) 0x5a, (byte) 0xa0,
            (byte) 0x52, (byte) 0x3b, (byte) 0xd6, (byte) 0xb3, (byte) 0x29, (byte) 0xe3, (byte) 0x2f, (byte) 0x84,
            (byte) 0x53, (byte) 0xd1, (byte) 0x00, (byte) 0xed, (byte) 0x20, (byte) 0xfc, (byte) 0xb1, (byte) 0x5b,
            (byte) 0x6a, (byte) 0xcb, (byte) 0xbe, (byte) 0x39, (byte) 0x4a, (byte) 0x4c, (byte) 0x58, (byte) 0xcf,
            (byte) 0xd0, (byte) 0xef, (byte) 0xaa, (byte) 0xfb, (byte) 0x43, (byte) 0x4d, (byte) 0x33, (byte) 0x85,
            (byte) 0x45, (byte) 0xf9, (byte) 0x02, (byte) 0x7f, (byte) 0x50, (byte) 0x3c, (byte) 0x9f, (byte) 0xa8,
            (byte) 0x51, (byte) 0xa3, (byte) 0x40, (byte) 0x8f, (byte) 0x92, (byte) 0x9d, (byte) 0x38, (byte) 0xf5,
            (byte) 0xbc, (byte) 0xb6, (byte) 0xda, (byte) 0x21, (byte) 0x10, (byte) 0xff, (byte) 0xf3, (byte) 0xd2,
            (byte) 0xcd, (byte) 0x0c, (byte) 0x13, (byte) 0xec, (byte) 0x5f, (byte) 0x97, (byte) 0x44, (byte) 0x17,
            (byte) 0xc4, (byte) 0xa7, (byte) 0x7e, (byte) 0x3d, (byte) 0x64, (byte) 0x5d, (byte) 0x19, (byte) 0x73,
            (byte) 0x60, (byte) 0x81, (byte) 0x4f, (byte) 0xdc, (byte) 0x22, (byte) 0x2a, (byte) 0x90, (byte) 0x88,
            (byte) 0x46, (byte) 0xee, (byte) 0xb8, (byte) 0x14, (byte) 0xde, (byte) 0x5e, (byte) 0x0b, (byte) 0xdb,
            (byte) 0xe0, (byte) 0x32, (byte) 0x3a, (byte) 0x0a, (byte) 0x49, (byte) 0x06, (byte) 0x24, (byte) 0x5c,
            (byte) 0xc2, (byte) 0xd3, (byte) 0xac, (byte) 0x62, (byte) 0x91, (byte) 0x95, (byte) 0xe4, (byte) 0x79,
            (byte) 0xe7, (byte) 0xc8, (byte) 0x37, (byte) 0x6d, (byte) 0x8d, (byte) 0xd5, (byte) 0x4e, (byte) 0xa9,
            (byte) 0x6c, (byte) 0x56, (byte) 0xf4, (byte) 0xea, (byte) 0x65, (byte) 0x7a, (byte) 0xae, (byte) 0x08,
            (byte) 0xba, (byte) 0x78, (byte) 0x25, (byte) 0x2e, (byte) 0x1c, (byte) 0xa6, (byte) 0xb4, (byte) 0xc6,
            (byte) 0xe8, (byte) 0xdd, (byte) 0x74, (byte) 0x1f, (byte) 0x4b, (byte) 0xbd, (byte) 0x8b, (byte) 0x8a,
            (byte) 0x70, (byte) 0x3e, (byte) 0xb5, (byte) 0x66, (byte) 0x48, (byte) 0x03, (byte) 0xf6, (byte) 0x0e,
            (byte) 0x61, (byte) 0x35, (byte) 0x57, (byte) 0xb9, (byte) 0x86, (byte) 0xc1, (byte) 0x1d, (byte) 0x9e,
            (byte) 0xe1, (byte) 0xf8, (byte) 0x98, (byte) 0x11, (byte) 0x69, (byte) 0xd9, (byte) 0x8e, (byte) 0x94,
            (byte) 0x9b, (byte) 0x1e, (byte) 0x87, (byte) 0xe9, (byte) 0xce, (byte) 0x55, (byte) 0x28, (byte) 0xdf,
            (byte) 0x8c, (byte) 0xa1, (byte) 0x89, (byte) 0x0d, (byte) 0xbf, (byte) 0xe6, (byte) 0x42, (byte) 0x68,
            (byte) 0x41, (byte) 0x99, (byte) 0x2d, (byte) 0x0f, (byte) 0xb0, (byte) 0x54, (byte) 0xbb, (byte) 0x16
    };

    /* Shift values for short/ long variants */
    private final int Shift[][][] =
    {
            {
                    { 0, 1, 2, 3, 4, 5, 6, 7 },
                    { 1, 3, 5, 7, 0, 2, 4, 6 } },
            {
                    { 0, 1, 2, 3, 4, 5, 6, 11 },
                    { 1, 3, 5, 11, 0, 2, 4, 6 } }
    };

    /**
     * Constructor.
     * @param pHashBitLen the hash bit length
     */
    public GroestlDigest(int pHashBitLen) {
        /* Check the hashBitLength */
        switch (pHashBitLen) {
            case 224:
            case 256:
            case 384:
            case 512:
                break;
            default:
                throw new IllegalArgumentException("Groestl digest restricted to one of [224, 256, 384, 512]");
        }

        /* Store value and initialise */
        hashbitlen = pHashBitLen;
        Init();
    }

    private byte mul1(byte b) {
        return b;
    }

    private byte mul2(byte b) {
        return (byte) (b >> 7 != 0
                                   ? (b << 1) ^ 0x1b
                                   : b << 1);
    }

    private byte mul3(byte b) {
        return (byte) (mul2(b) ^ mul1(b));
    }

    private byte mul4(byte b) {
        return mul2(mul2(b));
    }

    private byte mul5(byte b) {
        return (byte) (mul4(b) ^ mul1(b));
    }

    private byte mul6(byte b) {
        return (byte) (mul4(b) ^ mul2(b));
    }

    private byte mul7(byte b) {
        return (byte) (mul4(b) ^ mul2(b) ^ mul1(b));
    }

    /* AddRoundConstant xors a round-dependent constant to the state */
    private void AddRoundConstant(byte x[][], int columns, byte round, Variant v) {
        int i, j;
        switch (v.getValue() & 1) {
            case 0:
                for (i = 0; i < columns; i++)
                    x[0][i] ^= (i << 4) ^ round;
                break;
            case 1:
                for (i = 0; i < columns; i++)
                    for (j = 0; j < ROWS - 1; j++)
                        x[j][i] ^= 0xff;
                for (i = 0; i < columns; i++)
                    x[ROWS - 1][i] ^= (i << 4) ^ 0xff ^ round;
                break;
        }
    }

    /* SubBytes replaces each byte by a value from the S-box */
    private void SubBytes(byte x[][], int columns) {
        int i, j;

        for (i = 0; i < ROWS; i++)
            for (j = 0; j < columns; j++)
                x[i][j] = S[x[i][j] & 0xFF];
    }

    /*
     * ShiftBytes cyclically shifts each row to the left by a number of positions
     */
    private void ShiftBytes(byte x[][], int columns, Variant v) {
        int[] R = Shift[v.getValue() / 2][v.getValue() & 1];
        int i, j;
        byte[] temp = new byte[COLS1024];

        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < columns; j++) {
                temp[j] = x[i][(j + R[i]) % columns];
            }
            for (j = 0; j < columns; j++) {
                x[i][j] = temp[j];
            }
        }
    }

    /* MixBytes reversibly mixes the bytes within a column */
    private void MixBytes(byte x[][], int columns) {
        int i, j;
        byte[] temp = new byte[ROWS];

        for (i = 0; i < columns; i++) {
            for (j = 0; j < ROWS; j++) {
                temp[j] = (byte) (mul2(x[(j + 0) % ROWS][i]) ^
                                  mul2(x[(j + 1) % ROWS][i]) ^
                                  mul3(x[(j + 2) % ROWS][i]) ^
                                  mul4(x[(j + 3) % ROWS][i]) ^
                                  mul5(x[(j + 4) % ROWS][i]) ^
                                  mul3(x[(j + 5) % ROWS][i]) ^
                                  mul5(x[(j + 6) % ROWS][i]) ^
                                  mul7(x[(j + 7) % ROWS][i]));
            }
            for (j = 0; j < ROWS; j++) {
                x[j][i] = temp[j];
            }
        }
    }

    /* apply P-permutation to x */
    private void P(byte x[][]) {
        byte i;
        Variant v = columns == 8
                                 ? Variant.P512
                                 : Variant.P1024;
        for (i = 0; i < rounds; i++) {
            AddRoundConstant(x, columns, i, v);
            SubBytes(x, columns);
            ShiftBytes(x, columns, v);
            MixBytes(x, columns);
        }
    }

    /* apply Q-permutation to x */
    private void Q(byte x[][]) {
        byte i;
        Variant v = columns == 8
                                 ? Variant.Q512
                                 : Variant.Q1024;
        for (i = 0; i < rounds; i++) {
            AddRoundConstant(x, columns, i, v);
            SubBytes(x, columns);
            ShiftBytes(x, columns, v);
            MixBytes(x, columns);
        }
    }

    /* digest (up to) msglen bytes */
    private void Transform(byte[] input,
                           int pOffset,
                           int msglen) {
        int i, j;
        int index = pOffset;
        byte[][] temp1 = new byte[ROWS][COLS1024];
        byte[][] temp2 = new byte[ROWS][COLS1024];

        /* digest one message block at the time */
        for (; msglen >= statesize; msglen -= statesize, index += statesize) {
            /*
             * store message block (m) in temp2, and xor of chaining (h) and message block in temp1
             */
            for (i = 0; i < ROWS; i++) {
                for (j = 0; j < columns; j++) {
                    temp1[i][j] = (byte) (chaining[i][j] ^ input[index + j * ROWS + i]);
                    temp2[i][j] = input[index + j * ROWS + i];
                }
            }

            P(temp1); /* P(h+m) */
            Q(temp2); /* Q(m) */

            /* xor P(h+m) and Q(m) onto chaining, yielding P(h+m)+Q(m)+h */
            for (i = 0; i < ROWS; i++) {
                for (j = 0; j < columns; j++) {
                    chaining[i][j] ^= temp1[i][j] ^ temp2[i][j];
                }
            }

            /* increment block counter */
            block_counter++;
        }
    }

    /* do output transformation, P(h)+h */
    private void OutputTransformation() {
        int i, j;
        byte[][] temp = new byte[ROWS][COLS1024];

        /* store chaining ("h") in temp */
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < columns; j++) {
                temp[i][j] = chaining[i][j];
            }
        }

        /* compute P(temp) = P(h) */
        P(temp);

        /* feed chaining forward, yielding P(h)+h */
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < columns; j++) {
                chaining[i][j] ^= temp[i][j];
            }
        }
    }

    /* initialise context */
    public void Init() {
        int i, j;

        if (hashbitlen <= 256) {
            rounds = ROUNDS512;
            columns = COLS512;
            statesize = SIZE512;
        } else {
            rounds = ROUNDS1024;
            columns = COLS1024;
            statesize = SIZE1024;
        }

        /* zeroise chaining variable */
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < columns; j++) {
                chaining[i][j] = 0;
            }
        }

        /* set initial value */
        for (i = ROWS - Integer.BYTES; i < ROWS; i++) {
            chaining[i][columns - 1] = (byte) (hashbitlen >> (8 * (7 - i)));
        }

        /* initialise other variables */
        buf_ptr = 0;
        block_counter = 0;
        bits_in_last_byte = 0;
    }

    public void Update(byte[] input,
                       int pOffset,
                       long databitlen) {
        int index = 0;
        int msglen = (int) (databitlen / 8); /* no. of (full) bytes supplied */
        int rem = (int) databitlen % 8; /* no. of additional bits */

        if (bits_in_last_byte != 0)
            throw new IllegalStateException("FAIL");

        /* if the buffer contains data that still needs to be digested */
        if (buf_ptr != 0) {
            /*
             * copy data into buffer until buffer is full, or there is no more data
             */
            for (; buf_ptr < statesize && index < msglen; index++, buf_ptr++) {
                buffer[buf_ptr] = input[pOffset + index];
            }

            if (buf_ptr < statesize) {
                /* this chunk of message does not fill the buffer */
                if (rem != 0) {
                    /* if there are additional bits, add them to the buffer */
                    bits_in_last_byte = rem;
                    buffer[buf_ptr++] = input[pOffset + index];
                }
                return;
            }

            /* the buffer is full, digest */
            buf_ptr = 0;
            Transform(buffer, 0, statesize);
        }

        /* digest remainder of data modulo the block size */
        Transform(input, pOffset + index, msglen - index);
        index += ((msglen - index) / statesize) * statesize;

        /* copy remaining data to buffer */
        for (; index < msglen; index++, buf_ptr++) {
            buffer[buf_ptr] = input[index];
        }

        if (rem != 0) {
            bits_in_last_byte = rem;
            buffer[buf_ptr++] = input[index];
        }
    }

    public void Final(byte[] output, int pOffset) {
        int i, j, hashbytelen = hashbitlen / 8;

        /* 100... padding */
        if (bits_in_last_byte != 0) {
            buffer[buf_ptr - 1] &= ((1 << bits_in_last_byte) - 1) << (8 - bits_in_last_byte);
            buffer[buf_ptr - 1] ^= 0x1 << (7 - bits_in_last_byte);
        } else
            buffer[buf_ptr++] = (byte) 0x80;

        if (buf_ptr > statesize - LENGTHFIELDLEN) {
            /* padding requires two blocks */
            while (buf_ptr < statesize) {
                buffer[buf_ptr++] = 0;
            }
            Transform(buffer, 0, statesize);
            buf_ptr = 0;
        }
        while (buf_ptr < statesize - LENGTHFIELDLEN) {
            buffer[buf_ptr++] = 0;
        }

        /* length padding */
        block_counter++;
        buf_ptr = statesize;
        while (buf_ptr > statesize - LENGTHFIELDLEN) {
            buffer[--buf_ptr] = (byte) block_counter;
            block_counter >>= 8;
        }

        /* digest (last) padding block */
        Transform(buffer, 0, statesize);

        /* output transformation */
        OutputTransformation();

        /* store hash output */
        j = 0;
        for (i = statesize - hashbytelen; i < statesize; i++, j++) {
            output[pOffset + j] = chaining[i % ROWS][i / ROWS];
        }

        /* zeroise */
        for (i = 0; i < ROWS; i++) {
            for (j = 0; j < columns; j++) {
                chaining[i][j] = 0;
            }
        }
        for (i = 0; i < statesize; i++) {
            buffer[i] = 0;
        }
    }
}
