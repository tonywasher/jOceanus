package net.sourceforge.joceanus.jgordianknot.junit.bc;

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

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSMTLayers;

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
        runXMSSTestCase(GordianXMSSHeight.H10);
        runXMSSTestCase(GordianXMSSHeight.H16);
        runXMSSTestCase(GordianXMSSHeight.H20);
        runXMSSMTTestCase(GordianXMSSHeight.H20, GordianXMSSMTLayers.L2);
        runXMSSMTTestCase(GordianXMSSHeight.H20, GordianXMSSMTLayers.L4);
        runXMSSMTTestCase(GordianXMSSHeight.H40, GordianXMSSMTLayers.L2);
        runXMSSMTTestCase(GordianXMSSHeight.H40, GordianXMSSMTLayers.L4);
        runXMSSMTTestCase(GordianXMSSHeight.H40, GordianXMSSMTLayers.L8);
        runXMSSMTTestCase(GordianXMSSHeight.H60, GordianXMSSMTLayers.L3);
        runXMSSMTTestCase(GordianXMSSHeight.H60, GordianXMSSMTLayers.L6);
        runXMSSMTTestCase(GordianXMSSHeight.H60, GordianXMSSMTLayers.L12);
    }

    /**
     * run XMSS testCase.
     * @param pHeight the height
     */
    void runXMSSTestCase(final GordianXMSSHeight pHeight) {
        runXMSSTestCase(GordianXMSSDigestType.SHA256, pHeight);
        runXMSSTestCase(GordianXMSSDigestType.SHA512, pHeight);
        runXMSSTestCase(GordianXMSSDigestType.SHAKE128, pHeight);
        runXMSSTestCase(GordianXMSSDigestType.SHAKE256, pHeight);
    }

    /**
     * run XMSS testCase.
     * @param pDigestType the digest
     * @param pHeight the height
     */
    void runXMSSTestCase(final GordianXMSSDigestType pDigestType,
                         final GordianXMSSHeight pHeight) {
        final XMSSTestCase myTest = new XMSSTestCase(pDigestType, pHeight);
        myTest.runTest();
        System.out.println(myTest);
    }

    /**
     * run XMSSMT testCase.
     * @param pHeight the height
     * @param pLayers the layers
     */
    void runXMSSMTTestCase(final GordianXMSSHeight pHeight,
                           final GordianXMSSMTLayers pLayers) {
        runXMSSMTTestCase(GordianXMSSDigestType.SHA256, pHeight, pLayers);
        runXMSSMTTestCase(GordianXMSSDigestType.SHA512, pHeight, pLayers);
        runXMSSMTTestCase(GordianXMSSDigestType.SHAKE128, pHeight, pLayers);
        runXMSSMTTestCase(GordianXMSSDigestType.SHAKE256, pHeight, pLayers);
    }

    /**
     * run XMSSMT testCase.
     * @param pDigestType the digest
     * @param pHeight the height
     * @param pLayers the layers
      */
    void runXMSSMTTestCase(final GordianXMSSDigestType pDigestType,
                           final GordianXMSSHeight pHeight,
                           final GordianXMSSMTLayers pLayers) {
        final XMSSMTTestCase myTest = new XMSSMTTestCase(pDigestType, pHeight, pLayers);
        myTest.runTest();
        System.out.println(myTest);
    }

    /**
     * Obtain digest OID for XMSSKeyType.
     * @param pKeyType the keyType
     * @return the OIDt
     */
    static ASN1ObjectIdentifier getOID(final GordianXMSSDigestType pKeyType) {
        switch (pKeyType) {
            case SHAKE128:
                return NISTObjectIdentifiers.id_shake128;
            case SHAKE256:
                return NISTObjectIdentifiers.id_shake256;
            case SHA256:
                return NISTObjectIdentifiers.id_sha256;
            case SHA512:
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
        XMSSTestCase(final GordianXMSSDigestType pDigest,
                     final GordianXMSSHeight pHeight) {
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
                    + " verify=" + elapsedToString(theVerify.theElapsed);
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
        XMSSMTTestCase(final GordianXMSSDigestType pDigest,
                       final GordianXMSSHeight pHeight,
                       final GordianXMSSMTLayers pLayers) {
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
                    + " verify=" + elapsedToString(theVerify.theElapsed);
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
        private final GordianXMSSKeySpec theKeySpec;

        /**
         * The XMSSPublicKey.
         */
        private XMSSPublicKeyParameters thePublicKey;

        /**
         * The XMSSPrivateKey.
         */
        private XMSSPrivateKeyParameters thePrivateKey;

        /**
         * The Elapsed.
         */
        private long theElapsed;

        /**
         * Constructor.
         * @param pDigest the digestType
         * @param pHeight the height
         */
        XMSSKeyPair(final GordianXMSSDigestType pDigest,
                    final GordianXMSSHeight pHeight) {
            theKeySpec = GordianXMSSKeySpec.xmss(pDigest, pHeight);
            theParms = new XMSSParameters(pHeight.getHeight(), getOID(pDigest));
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

            /* Extract a keyShard of 32 signatures */
            thePrivateKey = thePrivateKey.extractKeyShard(NUMSIGNS);

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
        }

        @Override
        public String toString() {
            return theKeySpec.toString();
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
            theSignatures = pSignature.theSignatures;
            for (byte[] mySignature : theSignatures) {
                /* Sign the message */
                if (!theSigner.verifySignature(MESSAGE, mySignature)) {
                    throw new IllegalStateException();
                }
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= theSignatures.length;
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
        private final GordianXMSSKeySpec theKeySpec;

        /**
         * The XMSSMTPublicKey.
         */
        private XMSSMTPublicKeyParameters thePublicKey;

        /**
         * The XMSSMTPrivateKey.
         */
        private XMSSMTPrivateKeyParameters thePrivateKey;

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
        XMSSMTKeyPair(final GordianXMSSDigestType pDigest,
                      final GordianXMSSHeight pHeight,
                      final GordianXMSSMTLayers pLayers) {
            theKeySpec = GordianXMSSKeySpec.xmssmt(pDigest, pHeight, pLayers);
            theParms = new XMSSMTParameters(pHeight.getHeight(), pLayers.getLayers(), getOID(pDigest));
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

            /* Extract a keyShard of NUMSIGNS signatures */
            thePrivateKey = thePrivateKey.extractKeyShard(NUMSIGNS);

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
        }

        @Override
        public String toString() {
            return theKeySpec.toString();
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
            theSignatures = pSignature.theSignatures;
            for (byte[] mySignature : theSignatures) {
                /* Sign the message */
                if (!theSigner.verifySignature(MESSAGE, mySignature)) {
                    throw new IllegalStateException();
                }
            }

            /* Complete the timeStamp */
            theElapsed = System.nanoTime() - myStart;
            theElapsed /= theSignatures.length;
        }
    }
}
