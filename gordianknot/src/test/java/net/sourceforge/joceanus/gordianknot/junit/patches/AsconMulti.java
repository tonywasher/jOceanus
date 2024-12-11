package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.patch.digests.AsconCXof128;
import org.bouncycastle.crypto.patch.digests.AsconHash256;
import org.bouncycastle.crypto.patch.digests.AsconXof128;
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
        checkDigest(new GordianAsconHash256(), new AsconHash256());
        checkDigest(new GordianAsconXof128(), new AsconXof128());
        checkDigest(new GordianAsconCXof128(), new AsconCXof128());
        checkXof(new GordianAsconXof128(), new AsconXof128());
        checkXof(new GordianAsconCXof128(), new AsconCXof128());
    }

    /**
     * Check digest.
     * @param pNew the new digest
     * @param pOld the old digest
     */
    private static void checkDigest(final Digest pNew,
                                    final Digest pOld) {
        /* Create the data */
        final byte[] myData = new byte[DATALEN];
        RANDOM.nextBytes(myData);

        /* Create the digest as a single block */
        pNew.update(myData, 0, DATALEN);
        final byte[] myNewFull = new byte[pNew.getDigestSize()];
        pNew.doFinal(myNewFull, 0);
        pOld.update(myData, 0, DATALEN);
        final byte[] myOldFull = new byte[pOld.getDigestSize()];
        pOld.doFinal(myOldFull, 0);

        /* Check that they are identical */
        if (!Arrays.equals(myNewFull, myOldFull)) {
            System.out.println("Mismatch on full digest");
        }

        /* Create the digest as partial blocks */
        for (int myPos = 0; myPos < DATALEN; myPos += PARTIALLEN) {
            final int myLen = Math.min(PARTIALLEN, DATALEN - myPos);
            pNew.update(myData, myPos, myLen);
            pOld.update(myData, myPos, myLen);
        }
        final byte[] myNewPart = new byte[pNew.getDigestSize()];
        pNew.doFinal(myNewPart, 0);
        final byte[] myOldPart = new byte[pOld.getDigestSize()];
        pOld.doFinal(myOldPart, 0);

        /* Check that they are identical */
        if (!Arrays.equals(myNewPart, myOldPart)) {
            System.out.println("Mismatch on partial digest");
        }
        if (!Arrays.equals(myOldPart, myOldFull)) {
            System.out.println("Mismatch on old partial vs full digest");
        }
        if (!Arrays.equals(myNewPart, myNewFull)) {
            System.out.println("Mismatch on new partial vs full digest");
        }
    }

    /**
     * Check xof.
     * @param pNew the new xof
     * @param pOld the old xof
     */
    private static void checkXof(final Xof pNew,
                                 final Xof pOld) {
        /* Create the data */
        final byte[] myData = new byte[DATALEN];
        RANDOM.nextBytes(myData);

        /* Update the Xofs with the data */
        pNew.update(myData, 0, DATALEN);
        pOld.update(myData, 0, DATALEN);

        /* Extract Xofs as single block */
        final byte[] myOldFull = new byte[DATALEN];
        pOld.doFinal(myOldFull, 0, DATALEN);
        final byte[] myNewFull = new byte[DATALEN];
        pNew.doFinal(myNewFull, 0, DATALEN);

        /* Check that they are identical */
        if (!Arrays.equals(myNewFull, myOldFull)) {
            System.out.println("Mismatch on full xof");
        }

        /* Update the Xofs with the data */
        pOld.update(myData, 0, DATALEN);
        pNew.update(myData, 0, DATALEN);
        final byte[] myOldPart = new byte[DATALEN];
        final byte[] myNewPart = new byte[DATALEN];

        /* Create the xof as partial blocks */
        for (int myPos = 0; myPos < DATALEN; myPos += PARTIALLEN) {
            final int myLen = Math.min(PARTIALLEN, DATALEN - myPos);
            pOld.doOutput(myOldPart, myPos, myLen);
            pNew.doOutput(myNewPart, myPos, myLen);
        }
        pOld.doFinal(myOldPart, 0, 0);
        pNew.doFinal(myNewPart, 0, 0);

        /* Check that they are identical */
        if (!Arrays.equals(myNewPart, myOldPart)) {
            System.out.println("Mismatch on partial xof");
        }
        if (!Arrays.equals(myNewPart, myNewFull)) {
            System.out.println("Mismatch on partial vs full xof");
        }
    }
}
