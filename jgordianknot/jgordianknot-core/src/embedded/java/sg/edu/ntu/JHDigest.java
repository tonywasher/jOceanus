/**
   This program gives the reference implementation of JH, directly ported from C to Java.
   It implements the standard description of JH (not bitslice)
   The description given in this program is suitable for hardware implementation

   --------------------------------
   Comparing to the original reference implementation,
   two functions are added to make the porgram more readable.
   One function is E8_initialgroup() at the beginning of E8;
   another function is E8_finaldegroup() at the end of E8.

   --------------------------------

   Last Modified: January 16, 2011
*/
package sg.edu.ntu;

/**
 * JHDigest. Changes to reference implementation are only to change types and to avoid allocating
 * temporary arrays.
 */
public class JHDigest {
    /**
     * The state.
     */
    private final int hashbitlen; /* the message digest size */
    private long databitlen; /* the message size in bits */
    private long datasizeInBuffer; /*
                                    * the size of the message remained in buffer; assumed to be
                                    * multiple of 8bits except for the last partial block at the end
                                    * of the message
                                    */
    private byte[] H = new byte[128]; /* the hash value H; 128 bytes */
    private byte[] A = new byte[256]; /* the temporary round value. 256 4-bit elements */
    private byte[] roundconstant = new byte[64]; /*
                                                  * round constant for one round; 64 4-bit elements
                                                  */
    private byte[] buffer = new byte[64]; /* the message block to be hashed; 64 bytes */
    private byte[] roundconstantExpanded = new byte[256]; /*
                                                           * the round constant expanded into 256
                                                           * 1-bit element
                                                           */

    /* ScratchPad */
    private byte[] tem = new byte[256];

    /**
     * The constant for the Round 0 of E8.
     */
    private static final byte[] roundconstant_zero =
    { 0x6, 0xa, 0x0, 0x9, 0xe, 0x6, 0x6, 0x7, 0xf, 0x3, 0xb, 0xc, 0xc, 0x9, 0x0, 0x8, 0xb, 0x2, 0xf, 0xb, 0x1, 0x3, 0x6, 0x6, 0xe, 0xa, 0x9, 0x5, 0x7, 0xd, 0x3, 0xe, 0x3, 0xa, 0xd, 0xe, 0xc, 0x1, 0x7,
            0x5, 0x1, 0x2, 0x7, 0x7, 0x5, 0x0, 0x9, 0x9, 0xd, 0xa, 0x2, 0xf, 0x5, 0x9, 0x0, 0xb, 0x0, 0x6, 0x6, 0x7, 0x3, 0x2, 0x2, 0xa };

    /* The two Sboxes S0 and S1 */
    private static byte[][] S =
    {
            { 9, 0, 4, 11, 13, 12, 3, 15, 1, 10, 2, 6, 7, 5, 8, 14 },
            { 3, 12, 6, 13, 5, 7, 1, 9, 15, 2, 0, 4, 11, 10, 14, 8 } };

    /**
     * Constructor.
     * @param pHashBitLen the hash bit length
     */
    public JHDigest(int pHashBitLen) {
        /* Check the hashBitLength */
        switch (pHashBitLen) {
            case 224:
            case 256:
            case 384:
            case 512:
                break;
            default:
                throw new IllegalArgumentException("JH digest restricted to one of [224, 256, 384, 512]");
        }

        /* Store value and initialise */
        hashbitlen = pHashBitLen;
        Init();
    }

    /* The linear transformation L, the MDS code */
    private void L(int a, int b) {
        byte myVal = tem[a];
        tem[b] ^= (((myVal) << 1) ^ ((myVal) >> 3) ^ (((myVal) >> 2) & 2)) & 0xf;
        myVal = tem[b];
        tem[a] ^= (((myVal) << 1) ^ ((myVal) >> 3) ^ (((myVal) >> 2) & 2)) & 0xf;
    }

    /* the round function of E8 */
    private void R8() {
        int i;
        byte t;

        /* expand the round constant into 256 one-bit element */
        for (i = 0; i < 256; i++) {
            roundconstantExpanded[i] = (byte) ((roundconstant[i >> 2] >> (3 - (i & 3))) & 1);
        }

        /* S box layer, each constant bit selects one Sbox from S0 and S1 */
        for (i = 0; i < 256; i++) {
            tem[i] = S[roundconstantExpanded[i]][A[i]]; /*
                                                         * constant bits are used to determine which
                                                         * Sbox to use
                                                         */
        }

        /* MDS Layer */
        for (i = 0; i < 256; i = i + 2)
            L(i, i + 1);

        /*
         * The following is the permutation layer P_8$ /*initial swap Pi_8
         */
        for (i = 0; i < 256; i = i + 4) {
            t = tem[i + 2];
            tem[i + 2] = tem[i + 3];
            tem[i + 3] = t;
        }

        /* permutation P'_8 */
        for (i = 0; i < 128; i = i + 1) {
            A[i] = tem[i << 1];
            A[i + 128] = tem[(i << 1) + 1];
        }

        /* final swap Phi_8 */
        for (i = 128; i < 256; i = i + 2) {
            t = A[i];
            A[i] = A[i + 1];
            A[i + 1] = t;
        }
    }

    /**
     * The following function generates the next round constant from the current round constant; R6
     * is used for generating round constants for E8, with the round constants of R6 being set as 0;
     */
    private void updateRoundconstant() {
        int i;
        byte t;

        /* Sbox layer */
        for (i = 0; i < 64; i++)
            tem[i] = S[0][roundconstant[i]];

        /* MDS layer */
        for (i = 0; i < 64; i = i + 2)
            L(i, i + 1);

        /* The following is the permutation layer P_6 */

        /* initial swap Pi_6 */
        for (i = 0; i < 64; i = i + 4) {
            t = tem[i + 2];
            tem[i + 2] = tem[i + 3];
            tem[i + 3] = t;
        }

        /* permutation P'_6 */
        for (i = 0; i < 32; i = i + 1) {
            roundconstant[i] = tem[i << 1];
            roundconstant[i + 32] = tem[(i << 1) + 1];
        }

        /* final swap Phi_6 */
        for (i = 32; i < 64; i = i + 2) {
            t = roundconstant[i];
            roundconstant[i] = roundconstant[i + 1];
            roundconstant[i + 1] = t;
        }
    }

    /*
     * initial group at the beginning of E_8: group the bits of H into 4-bit elements of A. After
     * the grouping, the i-th, (i+256)-th, (i+512)-th, (i+768)-th bits of state->H become the i-th
     * 4-bit element of state->A
     */
    private void E8Initialgroup() {
        int i;
        byte t0, t1, t2, t3;

        /* t0 is the i-th bit of H, i = 0, 1, 2, 3, ... , 127 */
        /* t1 is the (i+256)-th bit of H */
        /* t2 is the (i+512)-th bit of H */
        /* t3 is the (i+768)-th bit of H */
        for (i = 0; i < 256; i++) {
            t0 = (byte) ((H[i >> 3] >> (7 - (i & 7))) & 1);
            t1 = (byte) ((H[(i + 256) >> 3] >> (7 - (i & 7))) & 1);
            t2 = (byte) ((H[(i + 512) >> 3] >> (7 - (i & 7))) & 1);
            t3 = (byte) ((H[(i + 768) >> 3] >> (7 - (i & 7))) & 1);
            tem[i] = (byte) ((t0 << 3) | (t1 << 2) | (t2 << 1) | (t3 << 0));
        }
        /* padding the odd-th elements and even-th elements separately */
        for (i = 0; i < 128; i++) {
            A[i << 1] = tem[i];
            A[(i << 1) + 1] = tem[i + 128];
        }
    }

    /*
     * de-group at the end of E_8: it is the inverse of E8_initialgroup The 256 4-bit elements in
     * state->A are de-grouped into the 1024-bit state->H
     */
    private void E8Finaldegroup() {
        int i;
        byte t0, t1, t2, t3;

        for (i = 0; i < 128; i++) {
            tem[i] = A[i << 1];
            tem[i + 128] = A[(i << 1) + 1];
        }

        for (i = 0; i < 128; i++)
            H[i] = 0;

        for (i = 0; i < 256; i++) {
            t0 = (byte) ((tem[i] >> 3) & 1);
            t1 = (byte) ((tem[i] >> 2) & 1);
            t2 = (byte) ((tem[i] >> 1) & 1);
            t3 = (byte) ((tem[i] >> 0) & 1);

            H[i >> 3] |= t0 << (7 - (i & 7));
            H[(i + 256) >> 3] |= t1 << (7 - (i & 7));
            H[(i + 512) >> 3] |= t2 << (7 - (i & 7));
            H[(i + 768) >> 3] |= t3 << (7 - (i & 7));
        }
    }

    /* bijective function E8 */
    private void E8() {
        int i;

        /* initialise the round constant */
        for (i = 0; i < 64; i++)
            roundconstant[i] = roundconstant_zero[i];

        /*
         * initial group at the beginning of E_8: group the H value into 4-bit elements and store
         * them in A
         */
        E8Initialgroup();

        /* 42 rounds */
        for (i = 0; i < 42; i++) {
            R8();
            updateRoundconstant();
        }

        /* de-group at the end of E_8: decompose the 4-bit elements of A into the 1024-bit H */
        E8Finaldegroup();
    }

    /* compression function F8 */
    private void F8() {
        int i;

        /* xor the message with the first half of H */
        for (i = 0; i < 64; i++)
            H[i] ^= buffer[i];

        /* Bijective function E8 */
        E8();

        /* xor the message with the last half of H */
        for (i = 0; i < 64; i++)
            H[i + 64] ^= buffer[i];
    }

    /* before hashing a message, initialize the hash state as H0 */
    public void Init() {
        int i;

        databitlen = 0;
        datasizeInBuffer = 0;

        for (i = 0; i < 64; i++)
            buffer[i] = 0;
        for (i = 0; i < 128; i++)
            H[i] = 0;

        /* initialise the initial hash value of JH */
        /* step 1: set H(-1) to the message digest size */
        H[1] = (byte) (hashbitlen & 0xff);
        H[0] = (byte) ((hashbitlen >> 8) & 0xff);

        /* step 2: compute H0 from H(-1) with message M(0) being set as 0 */
        F8();
    }

    /* hash each 512-bit message block, except the last partial block */
    public void Update(byte[] data, int pOffset, long pDatabitlen) {
        int index; /* the starting address of the data to be compressed */

        databitlen += pDatabitlen;
        index = 0;

        /* if there is remaining data in the buffer, fill it to a full message block first */
        /*
         * we assume that the size of the data in the buffer is the multiple of 8 bits if it is not
         * at the end of a message
         */

        /* There is data in the buffer, but the incoming data is insufficient for a full block */
        if ((datasizeInBuffer > 0) && ((datasizeInBuffer + pDatabitlen) < 512)) {
            int copyDataLen = (int) (pDatabitlen >> 3);
            if ((pDatabitlen & 7) != 0) {
                copyDataLen++;
            }
            System.arraycopy(data, pOffset, buffer, (int) (datasizeInBuffer >> 3), copyDataLen);
            datasizeInBuffer += pDatabitlen;
            pDatabitlen = 0;
        }

        /* There is data in the buffer, and the incoming data is sufficient for a full block */
        if ((datasizeInBuffer > 0) && ((datasizeInBuffer + pDatabitlen) >= 512)) {
            System.arraycopy(data, pOffset, buffer, (int) (datasizeInBuffer >> 3), (int) (64 - (datasizeInBuffer >> 3)));
            index = (int) (64 - (datasizeInBuffer >> 3));
            pDatabitlen = pDatabitlen - (512 - (int) datasizeInBuffer);
            F8();
            datasizeInBuffer = 0;
        }

        /* hash the remaining full message blocks */
        for (; pDatabitlen >= 512; index = index + 64, pDatabitlen = pDatabitlen - 512) {
            System.arraycopy(data, pOffset + index, buffer, 0, 64);
            F8();
        }

        /*
         * store the partial block into buffer, assume that -- if part of the last byte is not part
         * of the message, then that part consists of 0 bits
         */
        if (pDatabitlen > 0) {
            if ((pDatabitlen & 7) == 0)
                System.arraycopy(data, pOffset + index, buffer, 0, (int) ((databitlen & 0x1ff) >> 3));
            else
                System.arraycopy(data, pOffset + index, buffer, 0, (int) (((databitlen & 0x1ff) >> 3) + 1));
            datasizeInBuffer = pDatabitlen;
        }
    }

    /* padding the message, truncate the hash value H and obtain the message digest */
    public void Final(byte[] hashval, int pOffset) {
        int i;

        if ((databitlen & 0x1ff) == 0) {
            /*
             * pad the message when databitlen is multiple of 512 bits, then process the padded
             * block
             */
            for (i = 0; i < 64; i++)
                buffer[i] = 0;
            buffer[0] = (byte) 0x80;
            buffer[63] = (byte) (databitlen & 0xff);
            buffer[62] = (byte) ((databitlen >> 8) & 0xff);
            buffer[61] = (byte) ((databitlen >> 16) & 0xff);
            buffer[60] = (byte) ((databitlen >> 24) & 0xff);
            buffer[59] = (byte) ((databitlen >> 32) & 0xff);
            buffer[58] = (byte) ((databitlen >> 40) & 0xff);
            buffer[57] = (byte) ((databitlen >> 48) & 0xff);
            buffer[56] = (byte) ((databitlen >> 56) & 0xff);
            F8();
        } else {
            /* set the rest of the bytes in the buffer to 0 */
            if ((datasizeInBuffer & 7) == 0)
                for (i = (int) (databitlen & 0x1ff) >> 3; i < 64; i++)
                    buffer[i] = 0;
            else
                for (i = (int) ((databitlen & 0x1ff) >> 3) + 1; i < 64; i++)
                    buffer[i] = 0;

            /*
             * pad and process the partial block when databitlen is not multiple of 512 bits, then
             * hash the padded blocks
             */
            buffer[(int) ((databitlen & 0x1ff) >> 3)] |= 1 << (7 - (databitlen & 7));
            F8();
            for (i = 0; i < 64; i++)
                buffer[i] = 0;
            buffer[63] = (byte) (databitlen & 0xff);
            buffer[62] = (byte) ((databitlen >> 8) & 0xff);
            buffer[61] = (byte) ((databitlen >> 16) & 0xff);
            buffer[60] = (byte) ((databitlen >> 24) & 0xff);
            buffer[59] = (byte) ((databitlen >> 32) & 0xff);
            buffer[58] = (byte) ((databitlen >> 40) & 0xff);
            buffer[57] = (byte) ((databitlen >> 48) & 0xff);
            buffer[56] = (byte) ((databitlen >> 56) & 0xff);
            F8();
        }

        /* truncating the final hash value to generate the message digest */
        switch (hashbitlen) {
            case 224:
                System.arraycopy(H, 100, hashval, pOffset, 28);
                break;
            case 256:
                System.arraycopy(H, 96, hashval, pOffset, 32);
                break;
            case 384:
                System.arraycopy(H, 80, hashval, pOffset, 48);
                break;
            case 512:
                System.arraycopy(H, 64, hashval, pOffset, 64);
                break;
        }
    }
}
