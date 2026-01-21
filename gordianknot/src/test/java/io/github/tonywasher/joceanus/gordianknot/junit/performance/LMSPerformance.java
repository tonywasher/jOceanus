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

package io.github.tonywasher.joceanus.gordianknot.junit.performance;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.HSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.HSSSigner;
import org.bouncycastle.pqc.crypto.lms.LMOtsParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.lms.LMSParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSSigner;
import org.bouncycastle.pqc.crypto.lms.LMSigParameters;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * LMS performance tests.
 */
public class LMSPerformance {
    /**
     * The testMessage length.
     */
    private static final int MSGLEN = 1024;

    /**
     * The number of signatures.
     * Keep this low while performance is poor.
     */
    private static final int NUMSIGNS = 4;

    /**
     * The secureRandom.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * The testMessage.
     */
    private static final byte[] MESSAGE = new byte[MSGLEN];

    /**
     * main.
     *
     * @param pArgs the program arguments
     */
    public static void main(final String[] pArgs) {
        /* Initialise the message */
        RANDOM.nextBytes(MESSAGE);

        /* Run the performance tests */
        new LMSPerformance();
    }

    /**
     * Constructor
     */
    public LMSPerformance() {
        runLMSTestCase(LMSigParameters.lms_sha256_n32_h5);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h5, 2);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h5, 3);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h5, 4);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h5, 5);
        runLMSTestCase(LMSigParameters.lms_sha256_n32_h10);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h10, 2);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h10, 3);
        runLMSTestCase(LMSigParameters.lms_sha256_n32_h15);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h15, 2);
        runHSSTestCase(LMSigParameters.lms_sha256_n32_h15, 3);

        /* Don't bother yet with the remaining tests */
        //runLMSTestCase(LMSigParameters.lms_sha256_n32_h20);
        //runHSSTestCase(LMSigParameters.lms_sha256_n32_h20, 2);
        //runHSSTestCase(LMSigParameters.lms_sha256_n32_h20, 3);
        //runLMSTestCase(LMSigParameters.lms_sha256_n32_h25);
        //runHSSTestCase(LMSigParameters.lms_sha256_n32_h25, 2);
        //runHSSTestCase(LMSigParameters.lms_sha256_n32_h25, 3);
    }

    /**
     * run LMS testCase.
     *
     * @param pSigParms the sigParams
     */
    void runLMSTestCase(final LMSigParameters pSigParms) {
        runLMSTestCase(pSigParms, LMOtsParameters.sha256_n32_w1);
        runLMSTestCase(pSigParms, LMOtsParameters.sha256_n32_w2);
        runLMSTestCase(pSigParms, LMOtsParameters.sha256_n32_w4);
        runLMSTestCase(pSigParms, LMOtsParameters.sha256_n32_w8);
    }

    /**
     * run LMS testCase.
     *
     * @param pSigParms the sigParams
     * @param pOtsParms the otsParms
     */
    void runLMSTestCase(final LMSigParameters pSigParms,
                        final LMOtsParameters pOtsParms) {
        final LMSTestCase myTest = new LMSTestCase(pSigParms, pOtsParms);
        myTest.runTest();
        System.out.println(myTest);
    }

    /**
     * run LMS testCase.
     *
     * @param pSigParms the sigParams
     * @param pDepth    the treeDepth
     */
    void runHSSTestCase(final LMSigParameters pSigParms,
                        final int pDepth) {
        runHSSTestCase(pSigParms, LMOtsParameters.sha256_n32_w1, pDepth);
        runHSSTestCase(pSigParms, LMOtsParameters.sha256_n32_w2, pDepth);
        runHSSTestCase(pSigParms, LMOtsParameters.sha256_n32_w4, pDepth);
        runHSSTestCase(pSigParms, LMOtsParameters.sha256_n32_w8, pDepth);
    }

    /**
     * run HSS testCase.
     *
     * @param pSigParms the sigParams
     * @param pOtsParms the otsParms
     * @param pDepth    the treeDepth
     */
    void runHSSTestCase(final LMSigParameters pSigParms,
                        final LMOtsParameters pOtsParms,
                        final int pDepth) {
        final HSSTestCase myTest = new HSSTestCase(pSigParms, pOtsParms, pDepth);
        myTest.runTest();
        System.out.println(myTest);
    }

    /**
     * Obtain id of SigParameter
     *
     * @param pParm the parameter
     * @return the Id
     */
    public static String sigToString(final LMSigParameters pParm) {
        if (pParm == LMSigParameters.lms_sha256_n32_h5) {
            return "H05";
        } else if (pParm == LMSigParameters.lms_sha256_n32_h10) {
            return "H10";
        } else if (pParm == LMSigParameters.lms_sha256_n32_h15) {
            return "H15";
        } else if (pParm == LMSigParameters.lms_sha256_n32_h20) {
            return "H20";
        } else if (pParm == LMSigParameters.lms_sha256_n32_h25) {
            return "H25";
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Obtain id of otsParameter
     *
     * @param pParm the parameter
     * @return the Id
     */
    public static String otsToString(final LMOtsParameters pParm) {
        if (pParm == LMOtsParameters.sha256_n32_w1) {
            return "W1";
        } else if (pParm == LMOtsParameters.sha256_n32_w2) {
            return "W2";
        } else if (pParm == LMOtsParameters.sha256_n32_w4) {
            return "W4";
        } else if (pParm == LMOtsParameters.sha256_n32_w8) {
            return "W8";
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Obtain seconds from elapsed
     *
     * @param pElapsed the elapsed
     * @return the seconds
     */
    public static String elapsedToString(final long pElapsed) {
        StringBuilder myNanos = new StringBuilder(Long.toString(pElapsed));
        while (myNanos.length() < 12) {
            myNanos.insert(0, '0');
        }
        myNanos.insert(myNanos.length() - 9, '.');
        return myNanos.toString();
    }

    /**
     * LMS testCase
     */
    public static class LMSTestCase {
        /**
         * The keyPair.
         */
        private final LMSKeyPair theKeyPair;

        /**
         * The Signer.
         */
        private final LMSSign theSigner;

        /**
         * The verifier.
         */
        private final LMSVerify theVerify;

        /**
         * Constructor.
         *
         * @param pSigParms the sigParams
         * @param pOtsParms the otsParms
         */
        LMSTestCase(final LMSigParameters pSigParms,
                    final LMOtsParameters pOtsParms) {
            theKeyPair = new LMSKeyPair(pSigParms, pOtsParms);
            theSigner = new LMSSign();
            theVerify = new LMSVerify();
        }

        /**
         * Run testCase.
         */
        void runTest() {
            theKeyPair.createKeyPair();
            theSigner.signWithKeyPair(theKeyPair);
            theVerify.verifyWithKeyPair(theKeyPair, theSigner);
        }

        @Override
        public String toString() {
            return theKeyPair.toString() + " generate=" + elapsedToString(theKeyPair.theElapsed)
                    + " sign=" + elapsedToString(theSigner.theElapsed)
                    + " verify=" + elapsedToString(theVerify.theElapsed)
                    + " #sigs=" + theKeyPair.theNumSigs
                    + theKeyPair.calculateKeySizes()
                    + theSigner.getSigSize();
        }
    }

    /**
     * HSS testCase
     */
    public static class HSSTestCase {
        /**
         * The keyPair.
         */
        private final HSSKeyPair theKeyPair;

        /**
         * The Signer.
         */
        private final HSSSign theSigner;

        /**
         * The verifier.
         */
        private final HSSVerify theVerify;

        /**
         * Constructor.
         *
         * @param pSigParms the sigParams
         * @param pOtsParms the otsParms
         * @param pDepth    the depth
         */
        HSSTestCase(final LMSigParameters pSigParms,
                    final LMOtsParameters pOtsParms,
                    final int pDepth) {
            theKeyPair = new HSSKeyPair(pSigParms, pOtsParms, pDepth);
            theSigner = new HSSSign();
            theVerify = new HSSVerify();
        }

        /**
         * Run testCase.
         */
        void runTest() {
            theKeyPair.createKeyPair();
            theSigner.signWithKeyPair(theKeyPair);
            theVerify.verifyWithKeyPair(theKeyPair, theSigner);
        }

        @Override
        public String toString() {
            return theKeyPair.toString() + " generate=" + elapsedToString(theKeyPair.theElapsed)
                    + " initsign=" + elapsedToString(theSigner.theInitial)
                    + " sign=" + elapsedToString(theSigner.theElapsed)
                    + " verify=" + elapsedToString(theVerify.theElapsed)
                    + " #sigs=" + theKeyPair.theNumSigs
                    + theKeyPair.calculateKeySizes()
                    + theSigner.getSigSize();
        }
    }

    /**
     * The LMSKeyPair class.
     */
    public static class LMSKeyPair {
        /**
         * The LMSParameters.
         */
        private final LMSParameters theParms;

        /**
         * The LMSPublicKey.
         */
        private LMSPublicKeyParameters thePublicKey;

        /**
         * The LMSPrivateKey.
         */
        private LMSPrivateKeyParameters thePrivateKey;

        /**
         * The Signature Space.
         */
        private long theNumSigs;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         *
         * @param pSigParms the sigParams
         * @param pOtsParms the otsParms
         */
        LMSKeyPair(final LMSigParameters pSigParms,
                   final LMOtsParameters pOtsParms) {
            theParms = new LMSParameters(pSigParms, pOtsParms);
        }

        /**
         * createKeyPair.
         */
        void createKeyPair() {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create and initialise the generator */
            final LMSKeyPairGenerator myGenerator = new LMSKeyPairGenerator();
            final KeyGenerationParameters myParams = new LMSKeyGenerationParameters(theParms, RANDOM);
            myGenerator.init(myParams);

            /* Generate the keyPair */
            final AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();
            thePublicKey = (LMSPublicKeyParameters) myPair.getPublic();
            thePrivateKey = (LMSPrivateKeyParameters) myPair.getPrivate();
            theNumSigs = thePrivateKey.getUsagesRemaining();

            /* Extract a keyShard of NUMSIGNS signatures */
            thePrivateKey = thePrivateKey.extractKeyShard(NUMSIGNS);

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
        }

        /**
         * Obtain keySizes.
         *
         * @return the keySizes
         */
        String calculateKeySizes() {
            try {
                byte[] myPublic = thePublicKey.getEncoded();
                byte[] myPrivate = thePrivateKey.getEncoded();
                return " Prv=" + myPrivate.length + " Pub=" + myPublic.length;
            } catch (Exception e) {
                throw new IllegalStateException();
            }
        }

        @Override
        public String toString() {
            return "LMS-" + sigToString(theParms.getLMSigParam())
                    + "-" + otsToString(theParms.getLMOTSParam());
        }
    }

    /**
     * The LMSSign class.
     */
    public static class LMSSign {
        /**
         * The LMSSigner.
         */
        private final LMSSigner theSigner;

        /**
         * The Signatures.
         */
        private byte[][] theSignatures;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         */
        LMSSign() {
            theSigner = new LMSSigner();
        }

        /**
         * sign with keyPair.
         *
         * @param pKeyPair the keyPair
         */
        void signWithKeyPair(final LMSKeyPair pKeyPair) {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create the results array */
            theSignatures = new byte[(int) pKeyPair.thePrivateKey.getUsagesRemaining()][];

            /* Initialise the signer */
            theSigner.init(true, pKeyPair.thePrivateKey);

            /* Loop through the signatures */
            for (int i = 0; i < theSignatures.length; i++) {
                /* Sign the message */
                theSignatures[i] = theSigner.generateSignature(MESSAGE);
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= theSignatures.length;
        }

        /**
         * Obtain sigSize.
         *
         * @return the signature size
         */
        String getSigSize() {
            return " Sig=" + theSignatures[0].length;
        }
    }

    /**
     * The LMSVerify class.
     */
    public static class LMSVerify {
        /**
         * The LMSSigner.
         */
        private final LMSSigner theSigner;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         */
        LMSVerify() {
            theSigner = new LMSSigner();
        }

        /**
         * Verify signature.
         *
         * @param pKeyPair   the keyPair
         * @param pSignature the signature
         */
        void verifyWithKeyPair(final LMSKeyPair pKeyPair,
                               final LMSSign pSignature) {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Initialise the signer */
            theSigner.init(false, pKeyPair.thePublicKey);

            /* Loop through the signatures */
            final byte[][] mySignatures = pSignature.theSignatures;
            for (byte[] mySignature : mySignatures) {
                /* Sign the message */
                if (!theSigner.verifySignature(MESSAGE, mySignature)) {
                    throw new IllegalStateException();
                }
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= mySignatures.length;
        }
    }

    /**
     * The HSSKeyPair class.
     */
    public static class HSSKeyPair {
        /**
         * The LMSParameters.
         */
        private final LMSParameters theParms;

        /**
         * The Depth.
         */
        private final int theDepth;

        /**
         * The HSSPublicKey.
         */
        private HSSPublicKeyParameters thePublicKey;

        /**
         * The HSSPrivateKey.
         */
        private HSSPrivateKeyParameters thePrivateKey;

        /**
         * The Signature Space.
         */
        private long theNumSigs;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         *
         * @param pSigParms the sigParams
         * @param pOtsParms the otsParms
         * @param pDepth    the depth
         */
        HSSKeyPair(final LMSigParameters pSigParms,
                   final LMOtsParameters pOtsParms,
                   final int pDepth) {
            theParms = new LMSParameters(pSigParms, pOtsParms);
            theDepth = pDepth;
        }

        /**
         * createKeyPair.
         */
        void createKeyPair() {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create and initialise the generator */
            final HSSKeyPairGenerator myGenerator = new HSSKeyPairGenerator();
            final LMSParameters[] myParms = new LMSParameters[theDepth];
            Arrays.fill(myParms, theParms);
            final KeyGenerationParameters myParams = new HSSKeyGenerationParameters(myParms, RANDOM);
            myGenerator.init(myParams);

            /* Generate the keyPair */
            final AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();
            thePublicKey = (HSSPublicKeyParameters) myPair.getPublic();
            thePrivateKey = (HSSPrivateKeyParameters) myPair.getPrivate();
            theNumSigs = thePrivateKey.getUsagesRemaining();

            /* Extract a keyShard of NUMSIGNS+1 signatures */
            thePrivateKey = thePrivateKey.extractKeyShard(NUMSIGNS + 1);

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
        }

        /**
         * Obtain keySizes.
         *
         * @return the keySizes
         */
        String calculateKeySizes() {
            try {
                byte[] myPublic = thePublicKey.getEncoded();
                byte[] myPrivate = thePrivateKey.getEncoded();
                return " Prv=" + myPrivate.length + " Pub=" + myPublic.length;
            } catch (Exception e) {
                throw new IllegalStateException();
            }
        }

        @Override
        public String toString() {
            return "HS" + theDepth + "-" + sigToString(theParms.getLMSigParam())
                    + "-" + otsToString(theParms.getLMOTSParam());
        }
    }

    /**
     * The HSSSign class.
     */
    public static class HSSSign {
        /**
         * The LMSSigner.
         */
        private final HSSSigner theSigner;

        /**
         * The Signatures.
         */
        private byte[][] theSignatures;

        /**
         * The Initial.
         */
        private long theInitial;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         */
        HSSSign() {
            theSigner = new HSSSigner();
        }

        /**
         * sign with keyPair.
         *
         * @param pKeyPair the keyPair
         */
        void signWithKeyPair(final HSSKeyPair pKeyPair) {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create the results array */
            theSignatures = new byte[(int) pKeyPair.thePrivateKey.getUsagesRemaining() - 1][];

            /* Initialise the signer */
            theSigner.init(true, pKeyPair.thePrivateKey);

            /* Do an initial sign */
            theSignatures[0] = theSigner.generateSignature(MESSAGE);

            /* Complete the timeStamp */
            final long myBase = System.nanoTime();
            theInitial = myBase - myStart;

            /* Loop through the signatures */
            for (int i = 0; i < theSignatures.length; i++) {
                /* Sign the message */
                theSignatures[i] = theSigner.generateSignature(MESSAGE);
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myBase;
            theElapsed /= theSignatures.length;
        }

        /**
         * Obtain sigSize.
         *
         * @return th signature size
         */
        String getSigSize() {
            return " Sig=" + theSignatures[0].length;
        }
    }

    /**
     * The HSSVerify class.
     */
    public static class HSSVerify {
        /**
         * The LMSSigner.
         */
        private final HSSSigner theSigner;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         */
        HSSVerify() {
            theSigner = new HSSSigner();
        }

        /**
         * Verify signature.
         *
         * @param pKeyPair   the keyPair
         * @param pSignature the signature
         */
        void verifyWithKeyPair(final HSSKeyPair pKeyPair,
                               final HSSSign pSignature) {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Initialise the signer */
            theSigner.init(false, pKeyPair.thePublicKey);

            /* Loop through the signatures */
            final byte[][] mySignatures = pSignature.theSignatures;
            for (byte[] mySignature : mySignatures) {
                /* Sign the message */
                if (!theSigner.verifySignature(MESSAGE, mySignature)) {
                    throw new IllegalStateException();
                }
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= mySignatures.length;
        }
    }
}
