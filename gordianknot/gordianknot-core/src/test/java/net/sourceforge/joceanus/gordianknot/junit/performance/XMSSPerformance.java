package net.sourceforge.joceanus.gordianknot.junit.performance;

import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTKeyPairGenerator;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSMTSigner;
import org.bouncycastle.pqc.crypto.xmss.XMSSParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.bouncycastle.pqc.crypto.xmss.XMSSSigner;

/**
 * XMSS performance tests.
 */
public class XMSSPerformance {
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
     * @param pArgs the program arguments
     */
    public static void main(final String[] pArgs) {
        /* Initialise the message */
        RANDOM.nextBytes(MESSAGE);

        /* Run the performance tests */
        new XMSSPerformance();
    }

    /**
     * Constructor
     */
    public XMSSPerformance() {
        runXMSSTestCase(10);
        runXMSSTestCase(16);
        runXMSSTestCase(20);
        runXMSSMTTestCase(20, 2);
        runXMSSMTTestCase(20, 4);
        runXMSSMTTestCase(40, 2);
        runXMSSMTTestCase(40, 4);
        runXMSSMTTestCase(40, 8);
        runXMSSMTTestCase(60, 3);
        runXMSSMTTestCase(60, 6);
        runXMSSMTTestCase(60, 12);
    }

    /**
     * run XMSS testCase.
     * @param pHeight the height
     */
    void runXMSSTestCase(final int pHeight) {
        runXMSSTestCase("SHA256", pHeight);
        runXMSSTestCase("SHA512", pHeight);
        runXMSSTestCase("SHAKE128", pHeight);
        runXMSSTestCase("SHAKE256", pHeight);
    }

    /**
     * run XMSS testCase.
     * @param pDigestType the digest
     * @param pHeight the height
     */
    void runXMSSTestCase(final String pDigestType,
                         final int pHeight) {
        final XMSSTestCase myTest = new XMSSTestCase(pDigestType, pHeight);
        myTest.runTest();
        System.out.println(myTest);
    }

    /**
     * run XMSSMT testCase.
     * @param pHeight the height
     * @param pLayers the layers
     */
    void runXMSSMTTestCase(final int pHeight,
                           final int pLayers) {
        runXMSSMTTestCase("SHA256", pHeight, pLayers);
        runXMSSMTTestCase("SHA512", pHeight, pLayers);
        runXMSSMTTestCase("SHAKE128", pHeight, pLayers);
        runXMSSMTTestCase("SHAKE256", pHeight, pLayers);
    }

    /**
     * run XMSSMT testCase.
     * @param pDigestType the digest
     * @param pHeight the height
     * @param pLayers the layers
      */
    void runXMSSMTTestCase(final String pDigestType,
                           final int pHeight,
                           final int pLayers) {
        final XMSSMTTestCase myTest = new XMSSMTTestCase(pDigestType, pHeight, pLayers);
        myTest.runTest();
        System.out.println(myTest);
    }

    /**
     * Obtain digest OID for XMSSKeyType.
     * @param pKeyType the keyType
     * @return the OIDt
     */
    static ASN1ObjectIdentifier getOID(final String pKeyType) {
        switch (pKeyType) {
            case "SHAKE128":
                return NISTObjectIdentifiers.id_shake128;
            case "SHAKE256":
                return NISTObjectIdentifiers.id_shake256;
            case "SHA256":
                return NISTObjectIdentifiers.id_sha256;
            case "SHA512":
            default:
                return NISTObjectIdentifiers.id_sha512;
        }
    }

    /**
     * Obtain seconds from elapsed
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
     * XMSS testCase
     */
    public static class XMSSTestCase {
        /**
         * The keyPair.
         */
        private final XMSSKeyPair theKeyPair;

        /**
         * The Signer.
         */
        private final XMSSSign theSigner;

        /**
         * The verifier.
         */
        private final XMSSVerify theVerify;

        /**
         * Constructor.
         * @param pDigest the digestType
         * @param pHeight the height
         */
        XMSSTestCase(final String pDigest,
                     final int pHeight) {
            theKeyPair = new XMSSKeyPair(pDigest, pHeight);
            theSigner = new XMSSSign();
            theVerify = new XMSSVerify();
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
    public static class XMSSMTTestCase {
        /**
         * The keyPair.
         */
        private final XMSSMTKeyPair theKeyPair;

        /**
         * The Signer.
         */
        private final XMSSMTSign theSigner;

        /**
         * The verifier.
         */
        private final XMSSMTVerify theVerify;

        /**
         * Constructor.
         * @param pDigest the digestType
         * @param pHeight the height
         * @param pLayers the Layers
         */
        XMSSMTTestCase(final String pDigest,
                       final int pHeight,
                       final int pLayers) {
            theKeyPair = new XMSSMTKeyPair(pDigest, pHeight, pLayers);
            theSigner = new XMSSMTSign();
            theVerify = new XMSSMTVerify();
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
            return theKeyPair.toString() + " generate=" + elapsedToString( theKeyPair.theElapsed)
                    + " sign=" + elapsedToString(theSigner.theElapsed)
                    + " verify=" + elapsedToString(theVerify.theElapsed)
                    + " #sigs=" + theKeyPair.theNumSigs
                    + theKeyPair.calculateKeySizes()
                    + theSigner.getSigSize();
        }
    }

    /**
     * The XMSSKeyPair class.
     */
    public static class XMSSKeyPair {
        /**
         * The XMSSParameters.
         */
        private final XMSSParameters theParms;

        /**
         * The KeySpec.
         */
        private final String theKeySpec;

        /**
         * The XMSSPublicKey.
         */
        private XMSSPublicKeyParameters thePublicKey;

        /**
         * The XMSSPrivateKey.
         */
        private XMSSPrivateKeyParameters thePrivateKey;

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
         * @param pDigest the digestType
         * @param pHeight the height
         */
        XMSSKeyPair(final String pDigest,
                    final int pHeight) {
            theKeySpec = "XMSS-" + pDigest + "-H=" + pHeight;
            theParms = new XMSSParameters(pHeight, getOID(pDigest));
        }

        /**
         * createKeyPair.
         */
        void createKeyPair() {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create and initialise the generator */
            final XMSSKeyPairGenerator myGenerator = new XMSSKeyPairGenerator();
            final KeyGenerationParameters myParams = new XMSSKeyGenerationParameters(theParms, RANDOM);
            myGenerator.init(myParams);

            /* Generate the keyPair */
            final AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();
            thePublicKey = (XMSSPublicKeyParameters) myPair.getPublic();
            thePrivateKey = (XMSSPrivateKeyParameters) myPair.getPrivate();
            theNumSigs = thePrivateKey.getUsagesRemaining();

            /* Extract a keyShard of 32 signatures */
            thePrivateKey = thePrivateKey.extractKeyShard(NUMSIGNS);

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
        }

        /**
         * Obtain keySizes.
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
            return theKeySpec;
        }
    }

    /**
     * The XMSSSign class.
     */
    public static class XMSSSign {
        /**
         * The LMSSigner.
         */
        private final XMSSSigner theSigner;

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
        XMSSSign() {
            theSigner = new XMSSSigner();
        }

        /**
         * sign with keyPair.
         * @param pKeyPair the keyPair
         */
        void signWithKeyPair(final XMSSKeyPair pKeyPair) {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create the results array */
            theSignatures = new byte[(int) pKeyPair.thePrivateKey.getUsagesRemaining()][];

            /* Initialise the signer */
            theSigner.init(true, pKeyPair.thePrivateKey);

            /* Loop through the signatures */
            for (int i = 0; i < theSignatures.length; i++) {
                /* Sign the message */
                theSignatures[i] =  theSigner.generateSignature(MESSAGE);
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= theSignatures.length;
        }

        /**
         * Obtain sigSize.
         * @return the signature size
         */
        String getSigSize() {
            return " Sig=" + theSignatures[0].length;
        }
    }

    /**
     * The XMSSVerify class.
     */
    public static class XMSSVerify {
        /**
         * The LMSSigner.
         */
        private final XMSSSigner theSigner;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         */
        XMSSVerify() {
            theSigner = new XMSSSigner();
        }

        /**
         * Verify signature.
         * @param pKeyPair the keyPair
         * @param pSignature the signature
         */
        void verifyWithKeyPair(final XMSSKeyPair pKeyPair,
                               final XMSSSign pSignature) {
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
     * The XMSSMTKeyPair class.
     */
    public static class XMSSMTKeyPair {
        /**
         * The XMSSMTParameters.
         */
        private final XMSSMTParameters theParms;

        /**
         * The KeySpec.
         */
        private final String theKeySpec;

        /**
         * The XMSSMTPublicKey.
         */
        private XMSSMTPublicKeyParameters thePublicKey;

        /**
         * The XMSSMTPrivateKey.
         */
        private XMSSMTPrivateKeyParameters thePrivateKey;

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
         * @param pDigest the digestType
         * @param pHeight the height
         * @param pLayers the Layers
         */
        XMSSMTKeyPair(final String pDigest,
                      final int pHeight,
                      final int pLayers) {
            theKeySpec = "XMSS^MT-" + pDigest + "-H=" + pHeight + "-L" + pLayers;
            theParms = new XMSSMTParameters(pHeight, pLayers, getOID(pDigest));
        }

        /**
         * createKeyPair.
         */
        void createKeyPair() {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create and initialise the generator */
            final XMSSMTKeyPairGenerator myGenerator = new XMSSMTKeyPairGenerator();
            final KeyGenerationParameters myParams = new XMSSMTKeyGenerationParameters(theParms, RANDOM);
            myGenerator.init(myParams);

            /* Generate the keyPair */
            final AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();
            thePublicKey = (XMSSMTPublicKeyParameters) myPair.getPublic();
            thePrivateKey = (XMSSMTPrivateKeyParameters) myPair.getPrivate();
            theNumSigs = thePrivateKey.getUsagesRemaining();

            /* Extract a keyShard of NUMSIGNS signatures */
            thePrivateKey = thePrivateKey.extractKeyShard(NUMSIGNS);

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
        }

        /**
         * Obtain keySizes.
         * @return the keySizes.
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
            return theKeySpec;
        }
    }

    /**
     * The XMSSMTSign class.
     */
    public static class XMSSMTSign {
        /**
         * The LMSSigner.
         */
        private final XMSSMTSigner theSigner;

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
        XMSSMTSign() {
            theSigner = new XMSSMTSigner();
        }

        /**
         * sign with keyPair.
         * @param pKeyPair the keyPair
         */
        void signWithKeyPair(final XMSSMTKeyPair pKeyPair) {
            /* Take a timeStamp */
            final long myStart = System.nanoTime();

            /* Create the results array */
            theSignatures = new byte[(int) pKeyPair.thePrivateKey.getUsagesRemaining()][];

            /* Initialise the signer */
            theSigner.init(true, pKeyPair.thePrivateKey);

            /* Loop through the signatures */
            for (int i = 0; i < theSignatures.length; i++) {
                /* Sign the message */
                theSignatures[i] =  theSigner.generateSignature(MESSAGE);
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= theSignatures.length;
        }

        /**
         * Obtain sigSize.
         * @return the signature size
         */
        String getSigSize() {
            return " Sig=" + theSignatures[0].length;
        }
    }

    /**
     * The XMSSMTVerify class.
     */
    public static class XMSSMTVerify {
        /**
         * The LMSSigner.
         */
        private final XMSSMTSigner theSigner;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         */
        XMSSMTVerify() {
            theSigner = new XMSSMTSigner();
        }

        /**
         * Verify signature.
         * @param pKeyPair the keyPair
         * @param pSignature the signature
         */
        void verifyWithKeyPair(final XMSSMTKeyPair pKeyPair,
                               final XMSSMTSign pSignature) {
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
