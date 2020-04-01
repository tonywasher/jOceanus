/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.random;

import java.security.SecureRandom;
import java.util.function.Predicate;

import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacParameters;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipher;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Core RandomFactory.
 */
public class GordianCoreRandomFactory
    implements GordianRandomFactory {
    /**
     * The SP800 prefix.
     */
    static final String SP800_PREFIX = "SP800-";

    /**
     * The bit shift.
     */
    static final int BIT_SHIFT = 3;

    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BITS_REQUIRED = GordianSP800HashDRBG.LONG_SEED_LENGTH;

    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BYTES_REQUIRED = NUM_ENTROPY_BITS_REQUIRED / Byte.SIZE;

    /**
     * The power of 2 for RESEED calculation.
     */
    private static final int RESEED_POWER = 48;

    /**
     * The length of time before a reSeed is required.
     */
    static final long RESEED_MAX = 1L << (RESEED_POWER - 1);

    /**
     * The power of 2 for BITS calculation.
     */
    private static final int BITS_POWER = 19;

    /**
     * The maximum # of bits that can be requested.
     */
    static final int MAX_BITS_REQUEST = 1 << (BITS_POWER - 1);

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The Secure Random source.
     */
    private final GordianRandomSource theRandomSource;

    /**
     * The Basic Secure Random instance.
     */
    private final SecureRandom theRandom;

    /**
     * The Entropy Source Provider.
     */
    private final EntropySourceProvider theEntropyProvider;

    /**
     * Construct a builder with an EntropySourceProvider based on the initial random.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    public GordianCoreRandomFactory(final GordianCoreFactory pFactory) throws OceanusException {
        /* Access the initial stringRandom */
        theFactory = pFactory;
        theRandomSource = theFactory.getRandomSource();
        theRandom = theRandomSource.getRandom();

        /* Store parameters and create an entropy provider */
        theEntropyProvider = new BasicEntropySourceProvider(theRandom, true);

        /* Create a random secureRandom and register it */
        final GordianRandomSpec mySpec = generateRandomSpec();
        final GordianSecureRandom myRandom = createRandom(mySpec);
        theRandomSource.setRandom(myRandom);
    }

    @Override
    public GordianSecureRandom createRandom(final GordianRandomSpec pRandomSpec) throws OceanusException {
        /* Check validity of RandomSpec */
        if (!supportedRandomSpecs().test(pRandomSpec)) {
            throw new GordianDataException(GordianCoreFactory.getInvalidText(pRandomSpec));
        }

        /* Access factories */
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) theFactory.getMacFactory();

        /* Access the digestSpec */
        final GordianDigestSpec myDigest = pRandomSpec.getDigestSpec();
        final boolean isResistent = pRandomSpec.isPredictionResistant();
        switch (pRandomSpec.getRandomType()) {
            case HASH:
                return buildHash(myDigests.createDigest(myDigest), isResistent);
            case HMAC:
                final GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigest);
                return buildHMAC((GordianCoreMac) myMacs.createMac(myMacSpec), isResistent);
            case CTR:
                GordianSymCipherSpec myCipherSpec = GordianSymCipherSpec.ecb(pRandomSpec.getSymKeySpec(), GordianPadding.NONE);
                return buildCTR(myCiphers.createSymKeyCipher(myCipherSpec), isResistent);
            case X931:
                myCipherSpec = GordianSymCipherSpec.ecb(pRandomSpec.getSymKeySpec(), GordianPadding.NONE);
                return buildX931(myCiphers.createSymKeyCipher(myCipherSpec), isResistent);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pRandomSpec));
        }
    }

    @Override
    public Predicate<GordianRandomSpec> supportedRandomSpecs() {
        return this::validRandomSpec;
    }

    /**
     * Check RandomSpec.
     * @param pRandomSpec the randomSpec
     * @return true/false
     */
    private boolean validRandomSpec(final GordianRandomSpec pRandomSpec) {
        /* Reject invalid randomSpec */
        if (pRandomSpec == null || !pRandomSpec.isValid()) {
            return false;
        }

        /* Access details */
        final GordianRandomType myType = pRandomSpec.getRandomType();
        final GordianDigestSpec myDigest = pRandomSpec.getDigestSpec();
        final GordianSymKeySpec mySymKey = pRandomSpec.getSymKeySpec();

        /* Check that the randomType is supported */
        switch (myType) {
            case HASH:
                final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
                return myDigest != null && myDigests.validDigestSpec(myDigest);
            case HMAC:
                final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) theFactory.getMacFactory();
                return myDigest != null && myMacs.validHMacSpec(myDigest);
            case CTR:
            case X931:
                final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
                return mySymKey != null && myCiphers.validSymKeySpec(mySymKey);
            default:
                return false;
        }
    }

    /**
     * Create a random randomSpec.
     * @return the randomSpec
     */
    private GordianRandomSpec generateRandomSpec() {
        /* Access factories */
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) theFactory.getMacFactory();

        /* Determine the type of random generator */
        final boolean isHMac = theRandom.nextBoolean();
        final GordianRandomType myType = isHMac
                                         ? GordianRandomType.HMAC
                                         : GordianRandomType.HASH;
        final Predicate<GordianDigestSpec> myPredicate = isHMac
                                                         ? myMacs.supportedHMacDigestSpecs()
                                                         : myDigests.supportedDigestSpecs();

        /* Access the digestTypes */
        final GordianDigestType[] myDigestTypes = GordianDigestType.values();

        /* Keep looping until we find a valid digest */
        for (;;) {
            /* Obtain the candidate DigestSpec */
            final int myInt = theRandom.nextInt(myDigestTypes.length);
            final GordianDigestType myDigestType = myDigestTypes[myInt];
            final GordianDigestSpec mySpec = new GordianDigestSpec(myDigestType, GordianLength.LEN_512);

            /* If this is a valid digestSpec, return it */
            if (myPredicate.test(mySpec)) {
                return new GordianRandomSpec(myType, new GordianDigestSpec(myDigestTypes[myInt]), false);
            }
        }
    }

    @Override
    public GordianDigest generateRandomDigest(final boolean pLargeData) throws OceanusException {
        /* Access Digest Factory and IdManager */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Determine a random specification and create it */
        final GordianDigestSpec mySpec = myIds.generateRandomDigestSpec(pLargeData);
        return myDigests.createDigest(mySpec);
    }

    @Override
    public GordianMac generateRandomMac(final GordianLength pKeyLen,
                                        final boolean pLargeData) throws OceanusException {
        /* Access Mac Factory and IdManager */
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Determine a random specification */
        final GordianMacSpec mySpec = myIds.generateRandomMacSpec(pKeyLen, pLargeData);

        /* Determine a random key */
        final GordianKeyGenerator<GordianMacSpec> myGenerator = myMacs.getKeyGenerator(mySpec);
        final GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();

        /* Create and initialise the MAC */
        final GordianMac myMac = myMacs.createMac(mySpec);
        myMac.init(GordianMacParameters.keyWithRandomNonce(myKey));

        /* Return it */
        return myMac;
    }

    @Override
    public GordianKey<GordianSymKeySpec> generateRandomSymKey(final GordianLength pKeyLen) throws OceanusException {
        /* Access Cipher Factory and IdManager */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Determine a random keySpec */
        final GordianSymKeySpec mySpec = myIds.generateRandomSymKeySpec(pKeyLen);

        /* Generate a random key */
        final GordianKeyGenerator<GordianSymKeySpec> myGenerator = myCiphers.getKeyGenerator(mySpec);
        return myGenerator.generateKey();
    }

    @Override
    public GordianKey<GordianStreamKeySpec> generateRandomStreamKey(final GordianLength pKeyLen,
                                                                    final boolean pLargeData) throws OceanusException {
        /* Access Cipher Factory and IdManager */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Generate a random keySpec */
        final GordianStreamKeySpec mySpec = myIds.generateRandomStreamKeySpec(pKeyLen, pLargeData);

        /* Generate a random key */
        final GordianKeyGenerator<GordianStreamKeySpec> myGenerator = myCiphers.getKeyGenerator(mySpec);
        return myGenerator.generateKey();
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     * @param pDigest digest to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a Hash DRBG.
     */
    private GordianSecureRandom buildHash(final GordianDigest pDigest,
                                          final boolean isPredictionResistant) {
        /* Create initVector */
        final byte[] myInit = theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED);

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800HashDRBG myProvider = new GordianSP800HashDRBG(pDigest, myEntropy, theRandomSource.defaultPersonalisation(), myInit);
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A HMAC DRBG.
     * @param hMac hMac to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    private GordianSecureRandom buildHMAC(final GordianCoreMac hMac,
                                          final boolean isPredictionResistant) {
        /* Create initVector */
        final byte[] myInit = theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED);

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800HMacDRBG myProvider = new GordianSP800HMacDRBG(hMac, myEntropy, theRandomSource.defaultPersonalisation(), myInit);
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a SP 800-90A CTR DRBG.
     * @param pCipher cipher to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a CTR DRBG.
     * @throws OceanusException on error
     */
    @SuppressWarnings("unchecked")
    private GordianSecureRandom buildCTR(final GordianSymCipher pCipher,
                                         final boolean isPredictionResistant) throws OceanusException {
        /* Create initVector */
        final byte[] myInit = theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED);

        /* Build DRBG */
        final GordianCoreCipher<GordianSymKeySpec> myCipher = (GordianCoreCipher<GordianSymKeySpec>) pCipher;
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800CTRDRBG myProvider = new GordianSP800CTRDRBG(myCipher,
                myEntropy, theRandomSource.defaultPersonalisation(), myInit);
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a X931 Cipher DRBG.
     * @param pCipher ctr cipher to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     * @throws OceanusException on error
     */
    private GordianSecureRandom buildX931(final GordianSymCipher pCipher,
                                          final boolean isPredictionResistant) throws OceanusException {
        /* Initialise the cipher with a random key */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianKeyGenerator<GordianSymKeySpec> myGenerator = myCiphers.getKeyGenerator(pCipher.getKeyType());
        final GordianKey<GordianSymKeySpec> myKey = myGenerator.generateKey();
        pCipher.init(true, GordianCipherParameters.key(myKey));

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(pCipher.getKeyType().getBlockLength().getLength());
        final GordianX931CipherDRBG myProvider = new GordianX931CipherDRBG(pCipher, myEntropy, theRandomSource.defaultPersonalisation());
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }
}
