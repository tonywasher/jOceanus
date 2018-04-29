/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.test;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.newdigests.GroestlDigest;
import org.bouncycastle.crypto.newdigests.JHDigest;
import org.bouncycastle.crypto.newengines.MARSEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;

/**
 * Check new algorithm.
 */
public final class GordianNewAlgo {
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
        // List<Results> myJH = testDigest(GordianDigestType.JH);
        // List<Results> myGroestl = testDigest(GordianDigestType.GROESTL);
        testCipher();

        System.out.println("Complete");
    }

    /**
     * Private constructor.
     */
    private GordianNewAlgo() {
    }

    /**
     * testCipher.
     */
    private static void testCipher() {
        /* Create message and key */
        final byte[] myMessage = "ASmall16byteMsg.".getBytes();
        final byte[] key = ("abcdefghijklmnop" +
                            "abcdefghijklmnop").getBytes();

        /* Create buffers */
        final byte[] myEncrypted = new byte[16];
        final byte[] myDecrypted = new byte[16];

        /* Setup the cipher */
        final MARSEngine myCipher = new MARSEngine();
        final CipherParameters myParms = new KeyParameter(key);

        /* Perform encrypt and decrypt */
        myCipher.init(true, myParms);
        myCipher.processBlock(myMessage, 0, myEncrypted, 0);

        myCipher.init(false, myParms);
        myCipher.processBlock(myEncrypted, 0, myDecrypted, 0);
        return;
    }

    /**
     * testDigest.
     * @param pDigestType the digestType to check
     * @return the resultSet
     */
    private static List<Results> testDigest(final GordianDigestType pDigestType) {
        /* Lengths to check */
        final int[] myLengths =
        { 224, 256, 384, 512 };

        /* Create the list */
        final List<Results> myList = new ArrayList<>();

        /* Loop through the lengths */
        for (int mySize : myLengths) {
            /* Create the digest */
            final Results myDigest = createDigest(pDigestType, mySize);
            myList.add(myDigest);

            /* check and profile it */
            checkDigest(myDigest);
            checkLargeDigest(myDigest);
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
                return new Results(pDigestType, new JHDigest(pSize));
            case GROESTL:
                return new Results(pDigestType, new GroestlDigest(pSize));
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
        final ExtendedDigest myDigest = pDigest.getDigest();
        final byte[] myResult = new byte[pDigest.getSize()];
        myDigest.update(BYTES, 0, BYTES.length);
        myDigest.doFinal(myResult, 0);
        pDigest.setResult(myResult);
    }

    /**
     * Create standard result.
     * @param pDigest the digest
     */
    private static void checkLargeDigest(final Results pDigest) {
        /* Create a large, randomly populated buffer */
        final int myDataLength = 21819;
        final byte[] myInput = new byte[myDataLength];
        final SecureRandom myRandom = new SecureRandom();
        myRandom.nextBytes(myInput);

        /* Create a digest using buffer all in one hit */
        final ExtendedDigest myDigest = pDigest.getDigest();
        final byte[] myResult = new byte[pDigest.getSize()];
        myDigest.update(myInput, 0, myDataLength);
        myDigest.doFinal(myResult, 0);

        /* Now create a digest in parts that are discrete arrays */
        final int myBoundary = 1427;
        final byte[] myResult2 = new byte[pDigest.getSize()];
        final byte[] myPartial = new byte[myBoundary];
        for (int i = 0; i < myDataLength; i += myBoundary) {
            int myLen = myDataLength - i;
            if (myLen > myBoundary) {
                myLen = myBoundary;
            }
            System.arraycopy(myInput, i, myPartial, 0, myLen);
            myDigest.update(myPartial, 0, myLen);
        }
        myDigest.doFinal(myResult2, 0);

        /* Check that we have the same result */
        if (!Arrays.areEqual(myResult, myResult2)) {
            System.out.println("Help");
        }

        /* Now create a digest in parts using offsets */
        for (int i = 0; i < myDataLength; i += myBoundary) {
            int myLen = myDataLength - i;
            if (myLen > myBoundary) {
                myLen = myBoundary;
            }
            myDigest.update(myInput, i, myLen);
        }
        myDigest.doFinal(myResult2, 0);

        /* Check that we have the same result */
        if (!Arrays.areEqual(myResult, myResult2)) {
            System.out.println("Help");
        }
    }

    /**
     * Profile digest.
     * @param pDigest the digest
     */
    private static void profileDigest(final Results pDigest) {
        /* Perform a simple loop */
        final byte[] myResult = new byte[pDigest.getSize()];
        final ExtendedDigest myDigest = pDigest.getDigest();
        final long myStart = System.nanoTime();
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
         * @param pDigest the digest
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
         * @param pElapsed the elapsed
         */
        public void setElapsed(final long pElapsed) {
            theElapsed = pElapsed;
        }
    }
}
