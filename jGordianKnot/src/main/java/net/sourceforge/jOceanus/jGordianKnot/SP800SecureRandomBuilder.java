package net.sourceforge.jOceanus.jGordianKnot;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Mac;

import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;
import org.bouncycastle.crypto.prng.drbg.SP80090DRBG;

/**
 * Builder class for SP800 DRBG SecureRandom instances, based on the BouncyCastle Code.
 */
public class SP800SecureRandomBuilder {
    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BITS_REQUIRED = 256;

    /**
     * The Basic Secure Random instance.
     */
    private final SecureRandom theRandom;

    /**
     * The Entropy Source Provider.
     */
    private final EntropySourceProvider theEntropyProvider;

    /**
     * The Security Bytes.
     */
    private byte[] theSecurityBytes = null;

    /**
     * Basic constructor, creates a builder using an EntropySourceProvider based on the default SecureRandom with predictionResistant set to false.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed to SecureRandom.setSeed() if the default SecureRandom does
     * for its generateSeed() call.
     * </p>
     */
    public SP800SecureRandomBuilder() {
        /* Use the default Secure Random and no prediction resistance */
        this(new SecureRandom(), false);
    }

    /**
     * Construct a builder with an EntropySourceProvider based on the passed in SecureRandom and the passed in value for prediction resistance.
     * <p>
     * Any SecureRandom created from a builder constructed like this will make use of input passed to SecureRandom.setSeed() if the passed in SecureRandom does
     * for its generateSeed() call.
     * </p>
     * @param pEntropySource the entropy source
     * @param isPredictionResistant is the random generator to be prediction resistant?
     */
    public SP800SecureRandomBuilder(final SecureRandom pEntropySource,
                                    final boolean isPredictionResistant) {
        /* Store parameters and create an entropy provider */
        theRandom = pEntropySource;
        theEntropyProvider = new BasicEntropySourceProvider(theRandom, isPredictionResistant);
    }

    /**
     * Create a builder which makes creates the SecureRandom objects from a specified entropy source provider.
     * <p>
     * <b>Note:</b> If this constructor is used any calls to setSeed() in the resulting SecureRandom will be ignored.
     * </p>
     * @param pEntropy the provider of entropy
     */
    public SP800SecureRandomBuilder(final EntropySourceProvider pEntropy) {
        theRandom = null;
        theEntropyProvider = pEntropy;
    }

    /**
     * Set the personalisation string for DRBG SecureRandoms created by this builder
     * @param pSecurityBytes the personalisation string for the underlying DRBG.
     */
    public void setSecurityBytes(byte[] pSecurityBytes) {
        theSecurityBytes = pSecurityBytes;
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * 
     * @param pDigest digest algorithm to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting SecureRandom should reseed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    protected SP800SecureRandom buildHash(final MessageDigest pDigest,
                                          final byte[] pInitVector,
                                          final boolean isPredictionResistant) {
        EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        HashSP800DRBG myProvider = new HashSP800DRBG(pDigest, myEntropy, theSecurityBytes, pInitVector);
        return new SP800SecureRandom(myProvider, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * 
     * @param hMac HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param pInitVector nonce value to use in DRBG construction.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting SecureRandom should reseed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    protected SP800SecureRandom buildHMAC(Mac hMac,
                                          byte[] pInitVector,
                                          final boolean isPredictionResistant) {
        EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        HMacSP800DRBG myProvider = new HMacSP800DRBG(hMac, myEntropy, theSecurityBytes, pInitVector);
        return new SP800SecureRandom(myProvider, myEntropy, isPredictionResistant);
    }

    /**
     * SecureRandom wrapper class.
     */
    protected class SP800SecureRandom
            extends SecureRandom {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 781744191004794480L;

        /**
         * The DRBG generator
         */
        private final SP80090DRBG theGenerator;

        /**
         * The DRBG provider
         */
        private final EntropySource theEntropy;

        /**
         * Is this instance prediction resistant?
         */
        private final boolean predictionResistant;

        /**
         * Constructor.
         * @param isPredictionResistant
         */
        private SP800SecureRandom(final SP80090DRBG pGenerator,
                                  final EntropySource pEntropy,
                                  final boolean isPredictionResistant) {
            /* Store parameters */
            theGenerator = pGenerator;
            theEntropy = pEntropy;
            predictionResistant = isPredictionResistant;
        }

        @Override
        public void setSeed(byte[] seed) {
            synchronized (this) {
                /* Ensure that the random generator is seeded if it exists */
                if (theRandom != null) {
                    theRandom.setSeed(seed);
                }
            }
        }

        @Override
        public void setSeed(long seed) {
            synchronized (this) {
                /* this will happen when SecureRandom() is created */
                if (theRandom != null) {
                    theRandom.setSeed(seed);
                }
            }
        }

        @Override
        public void nextBytes(byte[] bytes) {
            synchronized (this) {
                /* Generate, checking for reSeed request */
                if (theGenerator.generate(bytes, null, predictionResistant) < 0) {
                    /* ReSeed and regenerate */
                    theGenerator.reseed(theEntropy.getEntropy());
                    theGenerator.generate(bytes, null, predictionResistant);
                }
            }
        }

        public byte[] generateSeed(int numBytes) {
            /* Generate a new seed */
            byte[] bytes = new byte[numBytes];
            nextBytes(bytes);
            return bytes;
        }
    }
}
