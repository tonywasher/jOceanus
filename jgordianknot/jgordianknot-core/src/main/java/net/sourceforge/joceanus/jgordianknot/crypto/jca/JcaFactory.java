/*******************************************************************************
0o * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianWrapCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaDiffieHellmanKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaElGamalKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaSPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaECSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaECValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRSASigner;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRSAValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRainbowSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRainbowValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaSPHINCSSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaSPHINCSValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.sp800.SP800Factory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Factory for JCA BouncyCastle Classes.
 */
public final class JcaFactory
        extends GordianFactory {
    /**
     * Cipher Algorithm Separator.
     */
    private static final Character ALGO_SEP = '/';

    /**
     * Note the standard provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Note the post quantum provider.
     */
    private static final Provider BCPQPROV = new BouncyCastlePQCProvider();

    /**
     * Predicate for all supported digest types.
     */
    private static final Predicate<GordianDigestType> PREDICATE_DIGESTS;

    /**
     * Predicate for all supported hMac digest types.
     */
    private static final Predicate<GordianDigestType> PREDICATE_HMACDIGESTS;

    /**
     * Predicate for all supported macTypes.
     */
    private static final Predicate<GordianMacType> PREDICATE_MACS;

    /**
     * Array for Max Cipher Steps.
     */
    private static final int[] MAX_CIPHER_STEPS;

    /**
     * Predicate for all supported streamKeyTypes.
     */
    private final Predicate<GordianStreamKeyType> theStreamPredicate;

    /**
     * Predicate for all supported symKeyTypes.
     */
    private final Predicate<GordianSymKeyType> theSymPredicate;

    /**
     * Predicate for all standard symKeyTypes.
     */
    private final Predicate<GordianSymKeyType> theStdSymPredicate;

    /**
     * Cache for KeyGenerators.
     */
    private final JcaKeyGeneratorCache theGeneratorCache;

    /**
     * SP800 Factory.
     */
    private final SP800Factory theSP800Factory;

    /**
     * Static Constructor.
     */
    static {
        /* Make sure that the Bouncy Castle Providers are installed */
        Security.addProvider(BCPROV);
        Security.addProvider(BCPQPROV);

        /* Create the Predicates */
        PREDICATE_DIGESTS = generateDigestPredicate();
        PREDICATE_HMACDIGESTS = generateHMacDigestPredicate();
        PREDICATE_MACS = p -> p != GordianMacType.CMAC;

        /* Calculate max cipher Steps */
        MAX_CIPHER_STEPS = new int[2];
        MAX_CIPHER_STEPS[0] = determineMaximumCipherSteps(false);
        MAX_CIPHER_STEPS[1] = determineMaximumCipherSteps(true);
    }

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public JcaFactory() throws OceanusException {
        this(new GordianParameters());
    }

    /**
     * Constructor.
     * @param pParameters the parameters
     * @throws OceanusException on error
     */
    public JcaFactory(final GordianParameters pParameters) throws OceanusException {
        /* Initialise underlying class */
        super(pParameters);

        /* Generate the predicates */
        boolean isRestricted = pParameters.useRestricted();
        theSymPredicate = generateSymKeyPredicate(isRestricted);
        theStdSymPredicate = generateStdSymKeyPredicate(isRestricted);
        theStreamPredicate = generateStreamKeyPredicate(isRestricted);

        /* Create the keyGenerator cache */
        theGeneratorCache = new JcaKeyGeneratorCache();

        /* Create the SP800 Factory */
        theSP800Factory = new SP800Factory();
        theSP800Factory.setSecurityBytes(getPersonalisation());

        /* Create the SecureRandom instance */
        SecureRandom myRandom = createRandom(defaultRandomSpec());
        setSecureRandom(myRandom);
    }

    @Override
    public SecureRandom createRandom(final GordianRandomSpec pRandomSpec) throws OceanusException {
        /* Check validity of randomSpec */
        if (!supportedRandomSpecs().test(pRandomSpec)) {
            throw new GordianDataException(getInvalidText(pRandomSpec));
        }

        /* Create the secureRandom */
        return getSP800SecureRandom(pRandomSpec);
    }

    @Override
    public JcaDigest createDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of DigestType */
        if (!supportedDigestSpecs().test(pDigestSpec)) {
            throw new GordianDataException(getInvalidText(pDigestSpec));
        }

        /* Create digest */
        MessageDigest myJavaDigest = getJavaDigest(pDigestSpec);
        return new JcaDigest(pDigestSpec, myJavaDigest);
    }

    @Override
    public Predicate<GordianDigestType> supportedDigestTypes() {
        return PREDICATE_DIGESTS;
    }

    @Override
    public Predicate<GordianDigestType> supportedHMacDigestTypes() {
        return PREDICATE_HMACDIGESTS;
    }

    @Override
    public JcaMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        Mac myJavaMac = getJavaMac(pMacSpec);
        return new JcaMac(this, pMacSpec, myJavaMac);
    }

    @Override
    public Predicate<GordianMacType> supportedMacTypes() {
        return PREDICATE_MACS;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedGMacSymKeyTypes() {
        return theStdSymPredicate;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedCMacSymKeyTypes() {
        return theSymPredicate.and(JcaFactory::isSupportedCMAC);
    }

    @Override
    public Predicate<GordianSymKeyType> supportedPoly1305SymKeyTypes() {
        return theStdSymPredicate;
    }

    @Override
    public <T> JcaKeyGenerator<T> getKeyGenerator(final T pKeyType) throws OceanusException {
        /* Look up in the cache */
        JcaKeyGenerator<T> myGenerator = theGeneratorCache.getCachedKeyGenerator(pKeyType);
        if (myGenerator == null) {
            /* Create the new generator */
            String myAlgorithm = getKeyAlgorithm(pKeyType);
            KeyGenerator myJavaGenerator = getJavaKeyGenerator(myAlgorithm);
            myGenerator = new JcaKeyGenerator<>(this, pKeyType, myJavaGenerator);

            /* Add to cache */
            theGeneratorCache.cacheKeyGenerator(myGenerator);
        }
        return myGenerator;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedSymKeyTypes() {
        return theSymPredicate;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedKeySetSymKeyTypes() {
        return theStdSymPredicate;
    }

    @Override
    public JcaKeyPairGenerator getKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Look up in the cache */
        JcaKeyPairGenerator myGenerator = theGeneratorCache.getCachedKeyPairGenerator(pKeySpec);
        if (myGenerator == null) {
            /* Create the new generator */
            myGenerator = getJcaKeyPairGenerator(pKeySpec);

            /* Add to cache */
            theGeneratorCache.cacheKeyPairGenerator(myGenerator);
        }
        return myGenerator;
    }

    @Override
    public JcaCipher<GordianSymKeyType> createSymKeyCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKey */
        GordianSymKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedSymKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!pCipherSpec.validate(false)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Create the cipher */
        Cipher myBCCipher = getJavaCipher(pCipherSpec);
        return new JcaCipher<>(this, pCipherSpec, myBCCipher);
    }

    @Override
    public JcaAADCipher createAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKey */
        GordianSymKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedSymKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!pCipherSpec.validate(true)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Create the cipher */
        Cipher myBCCipher = getJavaCipher(pCipherSpec);
        return new JcaAADCipher(this, pCipherSpec, myBCCipher);
    }

    @Override
    public JcaCipher<GordianStreamKeyType> createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of StreamKey */
        GordianStreamKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedStreamKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(myKeyType));
        }

        /* Create the cipher */
        Cipher myJavaCipher = getJavaCipher(myKeyType);
        return new JcaCipher<>(this, pCipherSpec, myJavaCipher);
    }

    @Override
    public Predicate<GordianStreamKeyType> supportedStreamKeyTypes() {
        return theStreamPredicate;
    }

    @Override
    protected GordianWrapCipher createWrapCipher(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeyTypes().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        GordianSymCipherSpec mySpec = GordianSymCipherSpec.cbc(pKeyType, GordianPadding.NONE);
        JcaCipher<GordianSymKeyType> myJcaCipher = createSymKeyCipher(mySpec);
        return createWrapCipher(myJcaCipher);
    }

    @Override
    public BiPredicate<GordianKeyPair, GordianSignatureSpec> supportedSignatures() {
        return this::validSignature;
    }

    @Override
    public GordianSigner createSigner(final GordianKeyPair pKeyPair,
                                      final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        if (!supportedSignatures().test(pKeyPair, pSignatureSpec)) {
            throw new GordianDataException(getInvalidText(pSignatureSpec));
        }

        /* Create the signer */
        return getJcaSigner((JcaKeyPair) pKeyPair, pSignatureSpec);
    }

    @Override
    public GordianValidator createValidator(final GordianKeyPair pKeyPair,
                                            final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        if (!supportedSignatures().test(pKeyPair, pSignatureSpec)) {
            throw new GordianDataException(getInvalidText(pSignatureSpec));
        }

        /* Create the validator */
        return getJcaValidator((JcaKeyPair) pKeyPair, pSignatureSpec);
    }

    /**
     * Create the SP800 SecureRandom instance.
     * @param pRandomSpec the randomSpec
     * @return the secureRandom
     * @throws OceanusException on error
     */
    private SecureRandom getSP800SecureRandom(final GordianRandomSpec pRandomSpec) throws OceanusException {
        GordianDigestSpec myDigest = pRandomSpec.getDigestSpec();
        switch (pRandomSpec.getRandomType()) {
            case HASH:
                return theSP800Factory.buildHash(createDigest(myDigest), true);
            case HMAC:
                GordianMacSpec mySpec = GordianMacSpec.hMac(myDigest);
                return theSP800Factory.buildHMAC(createMac(mySpec), true);
            default:
                throw new GordianDataException(getInvalidText(pRandomSpec));
        }
    }

    /**
     * Create the BouncyCastle digest via JCA.
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws OceanusException on error
     */
    private static MessageDigest getJavaDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(JcaDigest.getAlgorithm(pDigestSpec), BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Digest", e);
        }
    }

    /**
     * Create the BouncyCastle MAC via JCA.
     * @param pMacSpec the MacSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getJavaMac(final GordianMacSpec pMacSpec) throws OceanusException {
        switch (pMacSpec.getMacType()) {
            case HMAC:
            case GMAC:
            case CMAC:
            case POLY1305:
            case SKEIN:
                return getJavaMac(getMacSpecAlgorithm(pMacSpec));
            case VMPC:
                return getJavaMac("VMPC-MAC");
            default:
                throw new GordianDataException(getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle MAC via JCA.
     * @param pAlgorithm the Algorithm
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getJavaMac(final String pAlgorithm) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a MAC for the algorithm */
            return Mac.getInstance(pAlgorithm, BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Mac", e);
        }
    }

    @Override
    public <T> String getKeyAlgorithm(final T pKeyType) throws OceanusException {
        if (pKeyType instanceof GordianMacSpec) {
            return getMacSpecAlgorithm((GordianMacSpec) pKeyType);
        }
        if (pKeyType instanceof GordianStreamKeyType) {
            return getStreamKeyAlgorithm((GordianStreamKeyType) pKeyType);
        }
        if (pKeyType instanceof GordianSymKeyType) {
            return getSymKeyAlgorithm((GordianSymKeyType) pKeyType);
        }
        throw new GordianDataException(getInvalidText(pKeyType));
    }

    /**
     * Create the BouncyCastle SymKey Cipher via JCA.
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static Cipher getJavaCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(getSymKeyAlgorithm(pCipherSpec.getKeyType()));
        myBuilder.append(ALGO_SEP);
        myBuilder.append(getCipherModeAlgorithm(pCipherSpec.getCipherMode()));
        myBuilder.append(ALGO_SEP);
        myBuilder.append(getPaddingAlgorithm(pCipherSpec.getPadding()));
        return getJavaCipher(myBuilder.toString());
    }

    /**
     * Create the BouncyCastle StreamKey Cipher via JCA.
     * @param pKeyType the StreamKeyType
     * @return the Cipher
     * @throws OceanusException on error
     */
    private Cipher getJavaCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        return getJavaCipher(getStreamKeyAlgorithm(pKeyType));
    }

    /**
     * Obtain the MacSpec Key algorithm.
     * @param pMacSpec the MacSpec
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private String getMacSpecAlgorithm(final GordianMacSpec pMacSpec) throws OceanusException {
        switch (pMacSpec.getMacType()) {
            case HMAC:
                return getHMacAlgorithm(pMacSpec.getDigestSpec());
            case GMAC:
                return getGMacAlgorithm(pMacSpec.getKeyType());
            case CMAC:
                return getCMacAlgorithm(pMacSpec.getKeyType());
            case POLY1305:
                return getPoly1305Algorithm(pMacSpec.getKeyType());
            case SKEIN:
                return getSkeinMacAlgorithm(pMacSpec.getDigestSpec());
            case VMPC:
                return getStreamKeyAlgorithm(GordianStreamKeyType.VMPC);
            default:
                throw new GordianDataException(getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator via JCA.
     * @param pAlgorithm the Algorithm
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private static KeyGenerator getJavaKeyGenerator(final String pAlgorithm) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyGenerator for the algorithm */
            return KeyGenerator.getInstance(pAlgorithm, BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyGenerator", e);
        }
    }

    /**
     * Create the BouncyCastle KeyPairGenerator via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws OceanusException on error
     */
    protected static KeyPairGenerator getJavaKeyPairGenerator(final String pAlgorithm,
                                                              final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return KeyPairGenerator.getInstance(pAlgorithm, postQuantum
                                                                        ? BCPQPROV
                                                                        : BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyPairGenerator", e);
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws OceanusException on error
     */
    protected static KeyFactory getJavaKeyFactory(final String pAlgorithm,
                                                  final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyFactory for the algorithm */
            return KeyFactory.getInstance(pAlgorithm, postQuantum
                                                                  ? BCPQPROV
                                                                  : BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyFactory", e);
        }
    }

    /**
     * Create the StreamKey Cipher via JCA.
     * @param pAlgorithm the Algorithm
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private static Cipher getJavaCipher(final String pAlgorithm) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a Cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Cipher", e);
        }
    }

    /**
     * Return the associated HMac algorithm.
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getHMacAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
        return "HMac" + JcaDigest.getHMacAlgorithm(pDigestSpec);
    }

    /**
     * Obtain the GMAC algorithm.
     * @param pKeyType the symmetric key type
     * @return the algorithm
     * @throws OceanusException on error
     */
    private String getGMacAlgorithm(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedKeySetSymKeyTypes().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Return algorithm name */
        return pKeyType.name() + "-GMAC";
    }

    /**
     * Obtain the GMAC algorithm.
     * @param pKeyType the symmetric key type
     * @return the algorithm
     * @throws OceanusException on error
     */
    private String getCMacAlgorithm(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedCMacSymKeyTypes().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Return algorithm name */
        return getSymKeyAlgorithm(pKeyType) + "CMAC";
    }

    /**
     * Obtain the Poly1305 algorithm.
     * @param pKeyType the symmetric key type
     * @return the algorithm
     * @throws OceanusException on error
     */
    private String getPoly1305Algorithm(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedKeySetSymKeyTypes().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Return algorithm name */
        return "POLY1305-" + pKeyType.name();
    }

    /**
     * Obtain the Skein MAC algorithm.
     * @param pSpec the digestSpec
     * @return the algorithm
     */
    private static String getSkeinMacAlgorithm(final GordianDigestSpec pSpec) {
        String myLen = Integer.toString(pSpec.getDigestLength().getLength());
        String myState = Integer.toString(pSpec.getStateLength().getLength());
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Skein-MAC-");
        myBuilder.append(myState);
        myBuilder.append("-");
        myBuilder.append(myLen);
        return myBuilder.toString();
    }

    /**
     * Obtain the SymKey algorithm.
     * @param pKeyType the keyType
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private static String getSymKeyAlgorithm(final GordianSymKeyType pKeyType) throws OceanusException {
        switch (pKeyType) {
            case TWOFISH:
                return "TwoFish";
            case SERPENT:
                return "Serpent";
            case THREEFISH:
                return "ThreeFish-256";
            case AES:
            case CAMELLIA:
            case CAST6:
            case RC6:
            case NOEKEON:
            case SM4:
            case SEED:
                return pKeyType.name();
            default:
                throw new GordianDataException(getInvalidText(pKeyType));
        }
    }

    /**
     * Obtain the CipherMode algorithm.
     * @param pMode the cipherMode
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private static String getCipherModeAlgorithm(final GordianCipherMode pMode) throws OceanusException {
        switch (pMode) {
            case ECB:
            case OFB:
            case SIC:
            case CBC:
            case CFB:
            case EAX:
            case CCM:
            case GCM:
            case OCB:
                return pMode.name();
            default:
                throw new GordianDataException(getInvalidText(pMode));
        }
    }

    /**
     * Obtain the Padding algorithm.
     * @param pPadding use padding true/false
     * @return the Algorithm
     */
    private static String getPaddingAlgorithm(final GordianPadding pPadding) {
        switch (pPadding) {
            case CTS:
                return "withCTS";
            case X923:
                return "X923Padding";
            case PKCS7:
                return "PKCS7Padding";
            case ISO7816D4:
                return "ISO7816-4Padding";
            case NONE:
            default:
                return "NoPadding";
        }
    }

    /**
     * Obtain the StreamKey algorithm.
     * @param pKeyType the keyType
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private String getStreamKeyAlgorithm(final GordianStreamKeyType pKeyType) throws OceanusException {
        switch (pKeyType) {
            case HC:
                return isRestricted()
                                      ? "HC128"
                                      : "HC256";
            case VMPC:
                return "VMPC-KSA3";
            case GRAIN:
                return "Grain128";
            case CHACHA:
            case CHACHA7539:
            case SALSA20:
            case XSALSA20:
            case ISAAC:
            case RC4:
                return pKeyType.name();
            default:
                throw new GordianDataException(getInvalidText(pKeyType));
        }
    }

    /**
     * Create the BouncyCastle KeyPairGenerator.
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private JcaKeyPairGenerator getJcaKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return new JcaRSAKeyPairGenerator(this, pKeySpec);
            case EC:
                return new JcaECKeyPairGenerator(this, pKeySpec);
            case ELGAMAL:
                return new JcaElGamalKeyPairGenerator(this, pKeySpec);
            case DIFFIEHELLMAN:
                return new JcaDiffieHellmanKeyPairGenerator(this, pKeySpec);
            case SPHINCS:
                return new JcaSPHINCSKeyPairGenerator(this, pKeySpec);
            case RAINBOW:
                return new JcaRainbowKeyPairGenerator(this, pKeySpec);
            case MCELIECE:
                return new JcaMcElieceKeyPairGenerator(this, pKeySpec);
            case NEWHOPE:
                return new JcaNewHopeKeyPairGenerator(this, pKeySpec);
            default:
                throw new GordianDataException(getInvalidText(pKeySpec.getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle Signer.
     * @param pKeyPair the keyPair
     * @param pSignatureSpec the digestSpec
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianSigner getJcaSigner(final JcaKeyPair pKeyPair,
                                       final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getAsymKeyType()) {
            case RSA:
                return new JcaRSASigner((JcaPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            case EC:
                return new JcaECSigner((JcaPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            case SPHINCS:
                return new JcaSPHINCSSigner((JcaPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            case RAINBOW:
                return new JcaRainbowSigner((JcaPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            default:
                throw new GordianDataException(getInvalidText(pSignatureSpec.getAsymKeyType()));
        }
    }

    /**
     * Create the BouncyCastle KEM Sender.
     * @param pKeyPair the keyPair
     * @param pSignatureSpec the signatureSpec
     * @return the Validator
     * @throws OceanusException on error
     */
    private static GordianValidator getJcaValidator(final JcaKeyPair pKeyPair,
                                                    final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getAsymKeyType()) {
            case RSA:
                return new JcaRSAValidator((JcaPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            case EC:
                return new JcaECValidator((JcaPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            case SPHINCS:
                return new JcaSPHINCSValidator((JcaPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            case RAINBOW:
                return new JcaRainbowValidator((JcaPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            default:
                throw new GordianDataException(getInvalidText(pSignatureSpec.getAsymKeyType()));
        }
    }

    @Override
    public BiPredicate<GordianKeyPair, GordianDigestSpec> supportedKeyExchanges() {
        return (p, d) -> false;
    }

    @Override
    public GordianKEMSender createKEMessage(final GordianKeyPair pKeyPair,
                                            final GordianDigestSpec pDigestSpec) {
        throw new UnsupportedOperationException();
    }

    @Override
    public GordianKeyEncapsulation parseKEMessage(final GordianKeyPair pKeyPair,
                                                  final GordianDigestSpec pDigestSpec,
                                                  final byte[] pMessage) {
        throw new UnsupportedOperationException();
    }

    /**
     * Generate Digest predicate.
     * @return the predicate
     */
    public static Predicate<GordianDigestType> generateDigestPredicate() {
        return p -> true;
    }

    /**
     * Generate Digest predicate.
     * @return the predicate
     */
    public static Predicate<GordianDigestType> generateHMacDigestPredicate() {
        return JcaDigest::isHMacSupported;
    }

    /**
     * Generate symKey predicate.
     * @param pRestricted are keys restricted?
     * @return the predicate
     */
    private static Predicate<GordianSymKeyType> generateSymKeyPredicate(final boolean pRestricted) {
        return p -> p.validForRestriction(pRestricted);
    }

    /**
     * Generate standard symKey predicate.
     * @param pRestricted are keys restricted?
     * @return the predicate
     */
    private static Predicate<GordianSymKeyType> generateStdSymKeyPredicate(final boolean pRestricted) {
        return p -> p.validForRestriction(pRestricted) && p.isStdBlock();
    }

    /**
     * Generate streamKey predicate.
     * @param pRestricted are keys restricted?
     * @return the predicate
     */
    private static Predicate<GordianStreamKeyType> generateStreamKeyPredicate(final boolean pRestricted) {
        return p -> p.validForRestriction(pRestricted) && p != GordianStreamKeyType.ISAAC;
    }

    /**
     * Obtain maximum cipherSteps.
     * @param pRestricted are keys restricted
     * @return the maximum
     */
    public static int getMaximumCipherSteps(final boolean pRestricted) {
        return MAX_CIPHER_STEPS[pRestricted
                                            ? 1
                                            : 0];
    }

    /**
     * Determine maximum cipherSteps.
     * @param pRestricted are keys restricted?
     * @return the maximum
     */
    private static int determineMaximumCipherSteps(final boolean pRestricted) {
        /* generate the predicate */
        Predicate<GordianSymKeyType> myFilter = generateStdSymKeyPredicate(pRestricted);

        /* Count valid values */
        int myCount = 0;
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (myFilter.test(myType)) {
                myCount++;
            }
        }

        /* Maximum is 1 less than the count */
        return myCount - 1;
    }

    /**
     * Determine supported CMAC algorithms.
     * @param pKeyType the keyType
     * @return true/false
     */
    private static boolean isSupportedCMAC(final GordianSymKeyType pKeyType) {
        switch (pKeyType) {
            case SEED:
            case SM4:
                return true;
            default:
                return false;
        }
    }

    /**
     * Check Signature.
     * @param pKeyPair the keyPair
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private boolean validSignature(final GordianKeyPair pKeyPair,
                                   final GordianSignatureSpec pSpec) {
        /* validate the signatureSpec */
        if (!validSignatureSpec(pKeyPair, pSpec)) {
            return false;
        }

        /* Switch on KeyType */
        GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (pSpec.getAsymKeyType()) {
            case RSA:
                return validRSASignature(pSpec);
            case EC:
                return validECSignature(pSpec);
            case SPHINCS:
                return validSPHINCSSignature(myDigest);
            case RAINBOW:
                return validRainbowSignature(myDigest);
            case DIFFIEHELLMAN:
            case NEWHOPE:
            case ELGAMAL:
            case MCELIECE:
            default:
                return false;
        }
    }

    /**
     * Check RSASignature.
     * @param pSpec the signatureSpec
     * @return true/false
     */
    private static boolean validRSASignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case SHA1:
            case SHA2:
                return true;
            case SHA3:
                return GordianSignatureType.PSS.equals(pSpec.getSignatureType());
            case WHIRLPOOL:
                return !GordianSignatureType.PSS.equals(pSpec.getSignatureType());
            default:
                return false;
        }
    }

    /**
     * Check ECSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validECSignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case SHA2:
                return myDigest.getStateLength() == null;
            case SHA3:
                return !GordianSignatureType.NR.equals(pSpec.getSignatureType());
            default:
                return false;
        }
    }

    /**
     * Check SPHINCSSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validSPHINCSSignature(final GordianDigestSpec pSpec) {
        return pSpec.getDigestType() == GordianDigestType.SHA3
               && pSpec.getDigestLength() == GordianLength.LEN_512;
    }

    /**
     * Check RainbowSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validRainbowSignature(final GordianDigestSpec pSpec) {
        return pSpec.getDigestType() == GordianDigestType.SHA2
               && pSpec.getStateLength() == null;
    }
}
