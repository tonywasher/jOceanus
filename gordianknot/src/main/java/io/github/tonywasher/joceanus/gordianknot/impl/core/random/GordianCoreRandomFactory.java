/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.impl.core.random;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMac;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacParameters;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.random.GordianRandomFactory;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianIdManager;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipher;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.mac.GordianCoreMacFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.random.GordianCoreRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.random.GordianCoreRandomSpecBuilder;
import org.bouncycastle.crypto.prng.BasicEntropySourceProvider;
import org.bouncycastle.crypto.prng.EntropySource;
import org.bouncycastle.crypto.prng.EntropySourceProvider;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    private final GordianBaseFactory theFactory;

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
     *
     * @param pFactory the factory
     * @throws GordianException on error
     */
    public GordianCoreRandomFactory(final GordianBaseFactory pFactory) throws GordianException {
        /* Access the initial stringRandom */
        theFactory = pFactory;
        theRandomSource = theFactory.getRandomSource();
        theRandom = theRandomSource.getRandom();

        /* Store parameters and create an entropy provider */
        theEntropyProvider = new BasicEntropySourceProvider(theRandom, true);

        /* Create a random combinedRandom and register it */
        theRandomSource.setRandom(generateRandomCombined());
    }

    @Override
    public GordianSecureRandom createRandom(final GordianNewRandomSpec pRandomSpec) throws GordianException {
        /* Check validity of RandomSpec */
        if (!supportedRandomSpecs().test(pRandomSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pRandomSpec));
        }

        /* Access factories */
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) theFactory.getMacFactory();
        final GordianNewSymCipherSpecBuilder myCipherBuilder = myCiphers.newSymCipherSpecBuilder();
        final GordianNewMacSpecBuilder myMacBuilder = myMacs.newMacSpecBuilder();

        /* Access the digestSpec */
        final GordianCoreRandomSpec mySpec = (GordianCoreRandomSpec) pRandomSpec;
        final GordianNewDigestSpec myDigest = mySpec.getDigestSpec();
        final boolean isResistent = pRandomSpec.isPredictionResistant();
        switch (pRandomSpec.getRandomType()) {
            case HASH:
                return buildHash(myDigests.createDigest(myDigest), isResistent);
            case HMAC:
                final GordianNewMacSpec myMacSpec = myMacBuilder.hMac(myDigest);
                return buildHMAC(myMacs.createMac(myMacSpec), isResistent);
            case CTR:
                GordianNewSymCipherSpec myCipherSpec = myCipherBuilder.ecb(mySpec.getSymKeySpec(), GordianNewPadding.NONE);
                return buildCTR(myCiphers.createSymKeyCipher(myCipherSpec), isResistent);
            case X931:
                myCipherSpec = myCipherBuilder.ecb(mySpec.getSymKeySpec(), GordianNewPadding.NONE);
                return buildX931(myCiphers.createSymKeyCipher(myCipherSpec), isResistent);
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pRandomSpec));
        }
    }

    @Override
    public GordianCombinedRandom createRandom(final GordianNewRandomSpec pCtrSpec,
                                              final GordianNewRandomSpec pHashSpec) throws GordianException {
        /* Check validity of ctrSpecs */
        if (!validCombinedSpec(pCtrSpec, pHashSpec)) {
            throw new GordianDataException(GordianBaseData.getInvalidText(pCtrSpec)
                    + "-" + pHashSpec);
        }

        /* Build pair of randoms */
        final GordianSecureRandom myCtr = createRandom(pCtrSpec);
        final GordianSecureRandom myHash = createRandom(pHashSpec);

        /* return the combined random */
        return new GordianCombinedRandom(myCtr, myHash);
    }

    @Override
    public Predicate<GordianNewRandomSpec> supportedRandomSpecs() {
        return this::validRandomSpec;
    }

    @Override
    public BiPredicate<GordianNewRandomSpec, GordianNewRandomSpec> supportedCombinedSpecs() {
        return this::validCombinedSpec;
    }

    @Override
    public GordianNewRandomSpecBuilder newRandomSpecBuilder() {
        return GordianCoreRandomSpecBuilder.newInstance();
    }

    /**
     * Check RandomSpec.
     *
     * @param pRandomSpec the randomSpec
     * @return true/false
     */
    private boolean validRandomSpec(final GordianNewRandomSpec pRandomSpec) {
        /* Reject invalid randomSpec */
        if (pRandomSpec == null || !pRandomSpec.isValid()) {
            return false;
        }

        /* Access details */
        final GordianCoreRandomSpec mySpec = (GordianCoreRandomSpec) pRandomSpec;
        final GordianNewRandomType myType = pRandomSpec.getRandomType();
        final GordianNewDigestSpec myDigest = mySpec.getDigestSpec();
        final GordianNewSymKeySpec mySymKey = mySpec.getSymKeySpec();

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
     * Check combined RandomSpec.
     *
     * @param pCtrSpec  the Counter Spec.
     * @param pHashSpec the Hash Spec
     * @return true/false
     */
    private boolean validCombinedSpec(final GordianNewRandomSpec pCtrSpec,
                                      final GordianNewRandomSpec pHashSpec) {
        /* Check validity of ctrSpecs */
        final GordianCoreRandomSpec myCtrSpec = (GordianCoreRandomSpec) pCtrSpec;
        if (!supportedRandomSpecs().test(pCtrSpec)
                || pCtrSpec.getRandomType() != GordianNewRandomType.CTR
                || myCtrSpec.getSymKeySpec().getKeyLength() != GordianLength.LEN_128
                || myCtrSpec.getSymKeySpec().getBlockLength() != GordianLength.LEN_128) {
            return false;
        }

        /* Validate the hashSpec */
        final GordianCoreRandomSpec myHashSpec = (GordianCoreRandomSpec) pHashSpec;
        return supportedRandomSpecs().test(pHashSpec)
                && pHashSpec.getRandomType() == GordianNewRandomType.HASH
                && myHashSpec.getDigestSpec().getDigestLength() == GordianLength.LEN_512;
    }

    /**
     * Create a random combinedRandom.
     *
     * @return the combinedRandom
     * @throws GordianException on error
     */
    GordianCombinedRandom generateRandomCombined() throws GordianException {
        /* Create a random ctrSpec */
        final GordianNewRandomSpecBuilder myBuilder = GordianCoreRandomSpecBuilder.newInstance();
        final GordianNewSymKeySpec mySymKeySpec = generateRandomSymKeySpec();
        final GordianNewRandomSpec myCtrSpec = myBuilder.ctr(mySymKeySpec);

        /* Create a random hashSpec */
        final GordianNewDigestSpec myDigestSpec = generateRandomDigestSpec();
        final GordianNewRandomSpec myHashSpec = myBuilder.hash(myDigestSpec);

        /* Build the combinedRandom */
        return createRandom(myCtrSpec, myHashSpec);
    }

    /**
     * Obtain random SymKeySpec with blockLength/keySize of 128 bits.
     *
     * @return the random symKeySpec
     */
    private GordianNewSymKeySpec generateRandomSymKeySpec() {
        /* Access the list of symKeySpecs and unique symKeyTypes */
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final List<GordianNewSymKeySpec> mySpecs = myCiphers.listAllSupportedSymKeySpecs(GordianLength.LEN_128);

        /* Remove the specs that are wrong block size and obtain keyTypes */
        mySpecs.removeIf(s -> s.getBlockLength() != GordianLength.LEN_128);
        final List<GordianNewSymKeyType> myTypes
                = mySpecs.stream().map(GordianNewSymKeySpec::getSymKeyType).collect(Collectors.toCollection(ArrayList::new));

        /* Determine a random index into the list and obtain the symKeyType */
        int myIndex = theRandom.nextInt(myTypes.size());
        final GordianNewSymKeyType myKeyType = myTypes.get(myIndex);

        /* Select from among possible keySpecs of this type */
        mySpecs.removeIf(s -> s.getSymKeyType() != myKeyType);
        myIndex = theRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
    }

    /**
     * Obtain random DigestSpec for a large data output length of 512-bits.
     *
     * @return the random digestSpec
     */
    private GordianNewDigestSpec generateRandomDigestSpec() {
        /* Access the list to select from */
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) theFactory.getDigestFactory();
        final List<GordianNewDigestSpec> mySpecs = myDigests.listAllSupportedSpecs();
        mySpecs.removeIf(s -> !GordianCoreDigestType.supportsLargeData(s.getDigestType())
                || s.getDigestLength() != GordianLength.LEN_512);
        final List<GordianNewDigestType> myTypes
                = mySpecs.stream().map(GordianNewDigestSpec::getDigestType).collect(Collectors.toCollection(ArrayList::new));

        /* Determine a random index into the list and obtain the digestType */
        final SecureRandom myRandom = theFactory.getRandomSource().getRandom();
        int myIndex = myRandom.nextInt(myTypes.size());
        final GordianNewDigestType myDigestType = myTypes.get(myIndex);

        /* Select from among possible digestSpecs of this type */
        mySpecs.removeIf(s -> s.getDigestType() != myDigestType);
        myIndex = myRandom.nextInt(mySpecs.size());
        return mySpecs.get(myIndex);
    }

    @Override
    public GordianDigest generateRandomDigest(final boolean pLargeData) throws GordianException {
        /* Access Digest Factory and IdManager */
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Determine a random specification and create it */
        final GordianNewDigestSpec mySpec = myIds.generateRandomDigestSpec(pLargeData);
        return myDigests.createDigest(mySpec);
    }

    @Override
    public GordianMac generateRandomMac(final GordianLength pKeyLen,
                                        final boolean pLargeData) throws GordianException {
        /* Access Mac Factory and IdManager */
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Determine a random specification */
        final GordianNewMacSpec mySpec = myIds.generateRandomMacSpec(pKeyLen, pLargeData);

        /* Determine a random key */
        final GordianKeyGenerator<GordianNewMacSpec> myGenerator = myMacs.getKeyGenerator(mySpec);
        final GordianKey<GordianNewMacSpec> myKey = myGenerator.generateKey();

        /* Create and initialise the MAC */
        final GordianMac myMac = myMacs.createMac(mySpec);
        myMac.init(GordianMacParameters.keyWithRandomNonce(myKey));

        /* Return it */
        return myMac;
    }

    @Override
    public GordianKey<GordianNewSymKeySpec> generateRandomSymKey(final GordianLength pKeyLen) throws GordianException {
        /* Access Cipher Factory and IdManager */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Determine a random keySpec */
        final GordianNewSymKeySpec mySpec = myIds.generateRandomSymKeySpec(pKeyLen);

        /* Generate a random key */
        final GordianKeyGenerator<GordianNewSymKeySpec> myGenerator = myCiphers.getKeyGenerator(mySpec);
        return myGenerator.generateKey();
    }

    @Override
    public GordianKey<GordianNewStreamKeySpec> generateRandomStreamKey(final GordianLength pKeyLen,
                                                                       final boolean pLargeData) throws GordianException {
        /* Access Cipher Factory and IdManager */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianIdManager myIds = theFactory.getIdManager();

        /* Generate a random keySpec */
        final GordianNewStreamKeySpec mySpec = myIds.generateRandomStreamKeySpec(pKeyLen, pLargeData);

        /* Generate a random key */
        final GordianKeyGenerator<GordianNewStreamKeySpec> myGenerator = myCiphers.getKeyGenerator(mySpec);
        return myGenerator.generateKey();
    }

    /**
     * Build a SecureRandom based on a SP 800-90A Hash DRBG.
     *
     * @param pDigest               digest to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     *                              SecureRandom should re-seed on each request for bytes.
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
     *
     * @param hMac                  hMac to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     *                              SecureRandom should re-seed on each request for bytes.
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
     * Build a SecureRandom based on a SP 800-90A CTR DRBG.
     *
     * @param pCipher               cipher to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     *                              SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a CTR DRBG.
     * @throws GordianException on error
     */
    @SuppressWarnings("unchecked")
    private GordianSecureRandom buildCTR(final GordianSymCipher pCipher,
                                         final boolean isPredictionResistant) throws GordianException {
        /* Create initVector */
        final byte[] myInit = theRandom.generateSeed(NUM_ENTROPY_BYTES_REQUIRED);

        /* Build DRBG */
        final GordianCoreCipher<GordianNewSymKeySpec> myCipher = (GordianCoreCipher<GordianNewSymKeySpec>) pCipher;
        final EntropySource myEntropy = theEntropyProvider.get(NUM_ENTROPY_BITS_REQUIRED);
        final GordianSP800CTRDRBG myProvider = new GordianSP800CTRDRBG(myCipher,
                myEntropy, theRandomSource.defaultPersonalisation(), myInit);
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    /**
     * Build a SecureRandom based on a X931 Cipher DRBG.
     *
     * @param pCipher               ctr cipher to use in the DRBG underneath the SecureRandom.
     * @param isPredictionResistant specify whether the underlying DRBG in the resulting
     *                              SecureRandom should re-seed on each request for bytes.
     * @return a SecureRandom supported by a HMAC DRBG.
     * @throws GordianException on error
     */
    private GordianSecureRandom buildX931(final GordianSymCipher pCipher,
                                          final boolean isPredictionResistant) throws GordianException {
        /* Initialise the cipher with a random key */
        final GordianCipherFactory myCiphers = theFactory.getCipherFactory();
        final GordianKeyGenerator<GordianNewSymKeySpec> myGenerator = myCiphers.getKeyGenerator(pCipher.getKeyType());
        final GordianKey<GordianNewSymKeySpec> myKey = myGenerator.generateKey();
        pCipher.initForEncrypt(GordianCipherParameters.key(myKey));

        /* Build DRBG */
        final EntropySource myEntropy = theEntropyProvider.get(pCipher.getKeyType().getBlockLength().getLength());
        final GordianX931CipherDRBG myProvider = new GordianX931CipherDRBG(pCipher, myEntropy, theRandomSource.defaultPersonalisation());
        return new GordianSecureRandom(myProvider, theRandom, myEntropy, isPredictionResistant);
    }

    @Override
    public List<GordianNewRandomSpec> listAllSupportedRandomSpecs() {
        return listAllPossibleSpecs()
                .stream()
                .filter(supportedRandomSpecs())
                .toList();
    }

    @Override
    public List<GordianNewRandomSpec> listAllSupportedRandomSpecs(final GordianNewRandomType pType) {
        return listAllPossibleSpecs()
                .stream()
                .filter(s -> s.getRandomType().equals(pType))
                .filter(supportedRandomSpecs())
                .toList();
    }

    @Override
    public List<GordianNewRandomSpec> listAllSupportedRandomSpecs(final GordianNewRandomType pType,
                                                                  final GordianLength pKeyLen) {
        return listAllPossibleSpecs()
                .stream()
                .filter(s -> s.getRandomType().equals(pType))
                .filter(s -> s.getRandomType().hasSymKeySpec())
                .filter(s -> GordianCoreRandomSpec.getSymKeySpec(s).getKeyLength() == pKeyLen)
                .filter(supportedRandomSpecs())
                .toList();
    }

    /**
     * List all possible randomSpecs.
     *
     * @return the list
     */
    private List<GordianNewRandomSpec> listAllPossibleSpecs() {
        return GordianCoreRandomSpecBuilder.listAllPossibleSpecs();
    }
}
