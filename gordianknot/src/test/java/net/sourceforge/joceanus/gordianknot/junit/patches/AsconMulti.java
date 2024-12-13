package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.patch.digests.GordianAsconCXof128;
import org.bouncycastle.crypto.patch.digests.GordianAsconHash256;
import org.bouncycastle.crypto.patch.digests.GordianAsconXof128;

import java.security.SecureRandom;
import java.util.Arrays;

public class AsconMulti {
    /**
     * The secure random.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * DataLength.
     */
    private static final int DATALEN = 67; //1429;

    /**
     * Partial length.
     */
    private static final int PARTIALLEN = 13; //317;

    /**
     * Main.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        checkDigest(new GordianAsconHash256());
        checkDigest(new GordianAsconXof128());
        checkDigest(new GordianAsconCXof128());
        checkXof(new GordianAsconXof128());
        checkXof(new GordianAsconCXof128());
    }

    /**
     * Check digest.
     * @param pDigest the digest
     */
    private static void checkDigest(final Digest pDigest) {
        /* Create the data */
        final byte[] myData = new byte[DATALEN];
        RANDOM.nextBytes(myData);

        /* Create the digest as a single block */
        pDigest.update(myData, 0, DATALEN);
        final byte[] myFull = new byte[pDigest.getDigestSize()];
        pDigest.doFinal(myFull, 0);

        /* Create the digest as partial blocks */
        for (int myPos = 0; myPos < DATALEN; myPos += PARTIALLEN) {
            final int myLen = Math.min(PARTIALLEN, DATALEN - myPos);
            pDigest.update(myData, myPos, myLen);
        }
        final byte[] myPart = new byte[pDigest.getDigestSize()];
        pDigest.doFinal(myPart, 0);

        /* Check that they are identical */
        if (!Arrays.equals(myPart, myFull)) {
            System.out.println("Mismatch on partial vs full digest");
        }
    }

    /**
     * Check xof.
     * @param pXof the xof
     */
    private static void checkXof(final Xof pXof) {
        /* Create the data */
        final byte[] myData = new byte[DATALEN];
        RANDOM.nextBytes(myData);

        /* Update the Xof with the data */
        pXof.update(myData, 0, DATALEN);

        /* Extract Xof as single block */
        final byte[] myFull = new byte[DATALEN];
        pXof.doFinal(myFull, 0, DATALEN);

        /* Update the Xof with the data */
        pXof.update(myData, 0, DATALEN);
        final byte[] myPart = new byte[DATALEN];

        /* Create the xof as partial blocks */
        for (int myPos = 0; myPos < DATALEN; myPos += PARTIALLEN) {
            final int myLen = Math.min(PARTIALLEN, DATALEN - myPos);
            pXof.doOutput(myPart, myPos, myLen);
        }
        pXof.doFinal(myPart, 0, 0);

        /* Check that they are identical */
        if (!Arrays.equals(myPart, myFull)) {
            System.out.println("Mismatch on partial vs full xof");
        }
    }
}
