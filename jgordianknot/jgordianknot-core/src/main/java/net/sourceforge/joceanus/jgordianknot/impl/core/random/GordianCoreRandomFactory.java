/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianIdManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot Core RandomFactory.
 */
public class GordianCoreRandomFactory
    implements GordianRandomFactory {
    /**
     * The number of entropy bits required.
     */
    private static final int NUM_ENTROPY_BITS_REQUIRED = 256;

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
        final boolean isResistent = pRandomSpec.isPredictionResistent();
        switch (pRandomSpec.getRandomType()) {
            case HASH:
                return buildHash(myDigests.createDigest(myDigest), isResistent);
            case HMAC:
                final GordianMacSpec myMacSpec = GordianMacSpec.hMac(myDigest);
                return buildHMAC(myMacs.createMac(myMacSpec), isResistent);
            case X931:
                final GordianSymCipherSpec myCipherSpec = GordianSymCipherSpec.ecb(pRandomSpec.getSymKeySpec(), GordianPadding.NONE);
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
                return new GordianRandomSpec(myType, new GordianDigestSpec(myDigestTypes[myInt]), true);
            }
        }
    }

    @Override
    public GordianDigest generateRandomDigest() throws OceanusException {
        /* Access Digest Factory and IdManager */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianIdManager myIds = myKeySets.getIdManager();

        /* Keep looping until we find a valid digest */
        for (;;) {
            final GordianDigestType myType = myIds.generateRandomDigestType();
            final GordianDigestSpec mySpec = new GordianDigestSpec(myType);
            if (myDigests.supportedDigestSpecs().test(mySpec)) {
                return myDigests.createDigest(new GordianDigestSpec(myType));
            }
        }
    }

    @Override
    public GordianMac generateRandomMac(final boolean pLargeData) throws OceanusException {
        /* Access Mac Factory and IdManager */
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianIdManager myIds = myKeySets.getIdManager();

        /* Determine a random specification */
        final GordianMacSpec mySpec = myIds.generateRandomMacSpec(pLargeData);

        /* Determine a random key */
        final GordianKeyGenerator<GordianMacSpec> myGenerator = myMacs.getKeyGenerator(mySpec);
        final GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();

        /* Create and initialise the MAC */
        final GordianMac myMac = myMacs.createMac(mySpec);
        myMac.initMac(myKey);

        /* Return it */
        return myMac;
    }

    @Override
    public GordianKey<GordianSymKeySpec> generateRandomSymKey() throws OceanusException {
        /* Access Cipher Factory and IdManager */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianIdManager myIds = myKeySets.getIdManager();

        /* Determine a random keyType */
        final GordianSymKeyType myType = myIds.generateRandomSymKeyType();

        /* Generate a random key */
        final GordianKeyGenerator<GordianSymKeySpec> myGenerator = myCiphers.getKeyGenerator(new GordianSymKeySpec(myType));
        return myGenerator.generateKey();
    }

    @Override
    public GordianKey<GordianStreamKeyType> generateRandomStreamKey(final boolean pLargeData) throws OceanusException {
        /* Access Cipher Factory and IdManager */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) theFactory.getKeySetFactory();
        final GordianIdManager myIds = myKeySets.getIdManager();

        /* Generate a random key */
        final GordianStreamKeyType myType =  myIds.generateRandomStreamKeyType(pLargeData);

        /* Generate a random key */
        final GordianKeyGenerator<GordianStreamKeyType> myGenerator = myCiphers.getKeyGenerator(myType);
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
    private GordianSecureRandom buildHMAC(final GordianMac hMac,
                                          final boolean isPredictionResistant) {
        /* Create initVector */
        final byte[] myInit = theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED);

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800HMacDRBG myProvider = new GordianSP800HMacDRBG(hMac, myEntropy, theRandomSource.defaultPersonalisation(), myInit);
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a X931 Cipher DRBG.
     * @param pCipher HMAC algorithm to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     * SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     */
    private GordianSecureRandom buildX931(final GordianCipher<GordianSymKeySpec> pCipher,
                                          final boolean isPredictionResistant) {
        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianX931CipherDRBG myProvider = new GordianX931CipherDRBG(pCipher, myEntropy, theRandomSource.defaultPersonalisation());
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }
}
