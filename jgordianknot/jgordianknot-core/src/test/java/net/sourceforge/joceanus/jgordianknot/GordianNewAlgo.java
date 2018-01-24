package net.sourceforge.joceanus.jgordianknot;

import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.ExtendedDigest;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyGroestlDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyJHDigest;

/**
 * Check new algorithm.
 */
public class GordianNewAlgo {
    /**
     * The test data.
     */
    private static final byte[] BYTES = ("The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog" +
                                         "The quick brown fox jumped over the lazy dog").getBytes();

    /**
     * Main test case.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        /* Test JH Digest */
        List<Results> myJH = testDigest(GordianDigestType.JH);
        List<Results> myGroestl = testDigest(GordianDigestType.GROESTL);

        System.out.println("Complete");
    }

    /**
     * testDigest.
     * @param pDigestType the digestType to check
     * @return the resultSet
     */
    private static List<Results> testDigest(final GordianDigestType pDigestType) {
        /* Lengths to check */
        int[] myLengths =
        { 224, 256, 384, 512 };

        /* Create the list */
        List<Results> myList = new ArrayList<>();

        /* Loop through the lengths */
        for (int mySize : myLengths) {
            /* Create the digest */
            Results myDigest = createDigest(pDigestType, mySize);
            myList.add(myDigest);

            /* check and profile it */
            checkDigest(myDigest);
            profileDigest(myDigest);
        }

        /* Return the list */
        return myList;
    }

    /**
     * Create digest.
     * @param pDigestType the digestType to create
     * @param pSize the size of the digest
     * @return the result
     */
    private static Results createDigest(final GordianDigestType pDigestType,
                                        final int pSize) {
        /* Switch on digestType */
        switch (pDigestType) {
            case JH:
                return new Results(pDigestType, new BouncyJHDigest(pSize));
            case GROESTL:
                return new Results(pDigestType, new BouncyGroestlDigest(pSize));
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Create standard result.
     * @param pDigest the digest
     */
    private static void checkDigest(final Results pDigest) {
        /* Perform a simple hash */
        byte[] myResult = new byte[pDigest.getSize()];
        ExtendedDigest myDigest = pDigest.getDigest();
        myDigest.update(BYTES, 0, BYTES.length);
        myDigest.doFinal(myResult, 0);
        pDigest.setResult(myResult);
    }

    /**
     * Profile digest.
     * @param pDigest the digest
     */
    private static void profileDigest(final Results pDigest) {
        /* Perform a simple loop */
        byte[] myResult = new byte[pDigest.getSize()];
        ExtendedDigest myDigest = pDigest.getDigest();
        long myStart = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            myDigest.update(BYTES, 0, BYTES.length);
            myDigest.doFinal(myResult, 0);
        }
        final long myElapsed = System.nanoTime() - myStart;
        pDigest.setElapsed(myElapsed / 1000);
    }

    /**
     * ResultSet.
     */
    private static class Results {
        /**
         * DigestType.
         */
        private final GordianDigestType theDigestType;

        /**
         * Size.
         */
        private final int theSize;

        /**
         * Size.
         */
        private final ExtendedDigest theDigest;

        /**
         * hashResult.
         */
        private byte[] theHashResult;

        /**
         * Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         * @param pDigestType the digestType
         * @param pSize the size
         */
        Results(final GordianDigestType pDigestType,
                final ExtendedDigest pDigest) {
            theDigestType = pDigestType;
            theSize = pDigest.getDigestSize();
            theDigest = pDigest;
        }

        /**
         * Obtain the digestType.
         * @return the digestType
         */
        public GordianDigestType getDigestType() {
            return theDigestType;
        }

        /**
         * Obtain the size.
         * @return the size
         */
        public int getSize() {
            return theSize;
        }

        /**
         * Obtain the digest.
         * @return the digest
         */
        public ExtendedDigest getDigest() {
            return theDigest;
        }

        /**
         * Obtain the hashResult.
         * @return the result
         */
        public byte[] getResult() {
            return theHashResult;
        }

        /**
         * Set the hashResult.
         * @param pResult the result
         */
        public void setResult(final byte[] pResult) {
            theHashResult = pResult;
        }

        /**
         * Obtain the elapsed.
         * @return the elapsed
         */
        public long getElapsed() {
            return theElapsed;
        }

        /**
         * Set the elapsed.
         * @param pResult the elapsed
         */
        public void setElapsed(final long pElapsed) {
            theElapsed = pElapsed;
        }
    }
}
