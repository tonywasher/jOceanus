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
import java.util.function.Predicate;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSP800Type;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaECDSASigner;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaECDSAValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRSASigner;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRSAValidator;
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
     * Note the provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

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
     * Predicate for all signature digests.
     */
    private static final Predicate<GordianDigestType> PREDICATE_SIGNDIGESTS;

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
        /* Make sure that the Bouncy Castle Provider is installed */
        Security.addProvider(BCPROV);

        /* Create the Predicates */
        PREDICATE_DIGESTS = generateDigestPredicate();
        PREDICATE_HMACDIGESTS = generateHMacDigestPredicate();
        PREDICATE_SIGNDIGESTS = p -> p == GordianDigestType.SHA2;
        PREDICATE_MACS = p -> true;

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
        SecureRandom myRandom = createRandom(getDefaultSP800());
        setSecureRandom(myRandom);
    }

    @Override
    public SecureRandom createRandom(final GordianSP800Type pRandomType) throws OceanusException {
        /* Create random instance */
        return getSP800SecureRandom(pRandomType);
    }

    @Override
    public JcaDigest createDigest(final GordianDigestType pDigestType) throws OceanusException {
        /* Check validity of Digests */
        if (!supportedDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Create digest */
        MessageDigest myJavaDigest = getJavaDigest(pDigestType);
        return new JcaDigest(pDigestType, myJavaDigest);
    }

    @Override
    public JcaDigest createDigest(final GordianDigestType pDigestType,
                                  final GordianLength pLength) throws OceanusException {
        /* Check validity of Digests */
        if (!supportedDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Adjust the length to ensure support */
        GordianLength myLength = pDigestType.adjustLength(pLength);

        /* Create digest */
        MessageDigest myJavaDigest = getJavaDigest(pDigestType, myLength);
        return new JcaDigest(pDigestType, myJavaDigest);
    }

    @Override
    public Predicate<GordianDigestType> supportedDigests() {
        return PREDICATE_DIGESTS;
    }

    @Override
    public Predicate<GordianDigestType> supportedHMacDigests() {
        return PREDICATE_HMACDIGESTS;
    }

    @Override
    public JcaMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        Mac myJavaMac = getJavaMac(pMacSpec);
        return new JcaMac(this, pMacSpec, myJavaMac);
    }

    @Override
    public Predicate<GordianMacType> supportedMacs() {
        return PREDICATE_MACS;
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
    public Predicate<GordianSymKeyType> supportedSymKeys() {
        return theSymPredicate;
    }

    @Override
    public Predicate<GordianSymKeyType> standardSymKeys() {
        return theStdSymPredicate;
    }

    @Override
    public JcaKeyPairGenerator getKeyPairGenerator(final GordianAsymKeyType pKeyType) throws OceanusException {
        /* Look up in the cache */
        JcaKeyPairGenerator myGenerator = theGeneratorCache.getCachedKeyPairGenerator(pKeyType);
        if (myGenerator == null) {
            /* Create the new generator */
            myGenerator = getJcaKeyPairGenerator(pKeyType);

            /* Add to cache */
            theGeneratorCache.cacheKeyPairGenerator(myGenerator);
        }
        return myGenerator;
    }

    @Override
    public JcaCipher<GordianSymKeyType> createSymKeyCipher(final GordianSymKeyType pKeyType,
                                                           final GordianCipherMode pMode,
                                                           final GordianPadding pPadding) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Check validity of Mode */
        if ((pMode == null) || pMode.isAAD()) {
            throw new GordianDataException(getInvalidText(pMode));
        }

        /* Check validity of Padding */
        if (pPadding == null
            || (!GordianPadding.NONE.equals(pPadding)
                && !pMode.allowsPadding())) {
            throw new GordianDataException(getInvalidText(pPadding));
        }

        /* Create the cipher */
        Cipher myBCCipher = getJavaCipher(pKeyType, pMode, pPadding);
        return new JcaCipher<>(this, pKeyType, pMode, pPadding, myBCCipher);
    }

    @Override
    public JcaAADCipher createAADCipher(final GordianSymKeyType pKeyType,
                                        final GordianCipherMode pMode) throws OceanusException {
        /* Check validity of SymKey */
        if (!standardSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Check validity of Mode */
        if ((pMode == null) || !pMode.isAAD()) {
            throw new GordianDataException(getInvalidText(pMode));
        }

        /* Create the cipher */
        Cipher myBCCipher = getJavaCipher(pKeyType, pMode, GordianPadding.NONE);
        return new JcaAADCipher(this, pKeyType, pMode, myBCCipher);
    }

    @Override
    public JcaCipher<GordianStreamKeyType> createStreamKeyCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        /* Check validity of StreamKey */
        if (!supportedStreamKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        Cipher myJavaCipher = getJavaCipher(pKeyType);
        return new JcaCipher<>(this, pKeyType, null, GordianPadding.NONE, myJavaCipher);
    }

    @Override
    public Predicate<GordianStreamKeyType> supportedStreamKeys() {
        return theStreamPredicate;
    }

    @Override
    public JcaWrapCipher createWrapCipher(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        JcaCipher<GordianSymKeyType> myJcaCipher = createSymKeyCipher(pKeyType, GordianCipherMode.CBC, GordianPadding.NONE);
        return new JcaWrapCipher(this, myJcaCipher);
    }

    @Override
    public Predicate<GordianDigestType> signatureDigests() {
        return PREDICATE_SIGNDIGESTS;
    }

    @Override
    public GordianSigner createSigner(final GordianPrivateKey pPrivateKey,
                                      final GordianDigestType pDigestType) throws OceanusException {
        /* Check validity of Digest */
        if (!signatureDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Create the signer */
        return getJcaSigner((JcaPrivateKey) pPrivateKey);
    }

    @Override
    public GordianValidator createValidator(final GordianPublicKey pPublicKey,
                                            final GordianDigestType pDigestType) throws OceanusException {
        /* Check validity of Digest */
        if (!signatureDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Create the validator */
        return getJcaValidator((JcaPublicKey) pPublicKey);
    }

    /**
     * Create the SP800 SecureRandom instance.
     * @param pRandomType the SP800 type
     * @return the MAC
     * @throws OceanusException on error
     */
    private SecureRandom getSP800SecureRandom(final GordianSP800Type pRandomType) throws OceanusException {
        switch (pRandomType) {
            case HASH:
                return theSP800Factory.buildHash(createDigest(getDefaultDigest()), null, false);
            case HMAC:
                GordianMacSpec mySpec = new GordianMacSpec(GordianMacType.HMAC, getDefaultDigest());
                return theSP800Factory.buildHMAC(createMac(mySpec), null, false);
            default:
                throw new GordianDataException(getInvalidText(pRandomType));
        }
    }

    /**
     * Create the BouncyCastle digest via JCA.
     * @param pDigestType the digest type
     * @return the digest
     * @throws OceanusException on error
     */
    private static MessageDigest getJavaDigest(final GordianDigestType pDigestType) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(JcaDigest.getAlgorithm(pDigestType), BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Digest", e);
        }
    }

    /**
     * Create the BouncyCastle digest via JCA.
     * @param pDigestType the digest type
     * @param pLength the length
     * @return the digest
     * @throws OceanusException on error
     */
    private static MessageDigest getJavaDigest(final GordianDigestType pDigestType,
                                               final GordianLength pLength) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a digest for the algorithm */
            return MessageDigest.getInstance(JcaDigest.getAlgorithm(pDigestType, pLength), BCPROV);

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
                return getJavaMac(getHMacAlgorithm(pMacSpec.getDigestType()));
            case GMAC:
                return getJavaMac(getGMacAlgorithm(pMacSpec.getKeyType()));
            case POLY1305:
                return getJavaMac(getPoly1305Algorithm(pMacSpec.getKeyType()));
            case SKEIN:
            case VMPC:
                return getJavaMac(getMacAlgorithm(pMacSpec.getMacType()));
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
     * @param pKeyType the SymKeyType
     * @param pMode the cipher mode
     * @param pPadding use padding true/false
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static Cipher getJavaCipher(final GordianSymKeyType pKeyType,
                                        final GordianCipherMode pMode,
                                        final GordianPadding pPadding) throws OceanusException {
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(getSymKeyAlgorithm(pKeyType));
        myBuilder.append(ALGO_SEP);
        myBuilder.append(getCipherModeAlgorithm(pMode));
        myBuilder.append(ALGO_SEP);
        myBuilder.append(getPaddingAlgorithm(pPadding));
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
                return getHMacAlgorithm(pMacSpec.getDigestType());
            case GMAC:
                return getGMacAlgorithm(pMacSpec.getKeyType());
            case POLY1305:
                return getPoly1305Algorithm(pMacSpec.getKeyType());
            case SKEIN:
                return getMacAlgorithm(pMacSpec.getMacType());
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
     * @return the KeyPairGenerator
     * @throws OceanusException on error
     */
    protected static KeyPairGenerator getJavaKeyPairGenerator(final String pAlgorithm) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return KeyPairGenerator.getInstance(pAlgorithm, BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyPairGenerator", e);
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     * @param pAlgorithm the Algorithm
     * @return the KeyFactory
     * @throws OceanusException on error
     */
    protected static KeyFactory getJavaKeyFactory(final String pAlgorithm) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyFactory for the algorithm */
            return KeyFactory.getInstance(pAlgorithm, BCPROV);

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
     * @param pDigestType the digest type
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getHMacAlgorithm(final GordianDigestType pDigestType) throws OceanusException {
        return GordianDigestType.SHA3.equals(pDigestType)
                                                          ? "HMacKECCAK512"
                                                          : "HMac" + JcaDigest.getAlgorithm(pDigestType);
    }

    /**
     * Obtain the GMAC algorithm.
     * @param pKeyType the symmetric key type
     * @return the algorithm
     * @throws OceanusException on error
     */
    private String getGMacAlgorithm(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!standardSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Return algorithm name */
        return pKeyType.name() + "-GMAC";
    }

    /**
     * Obtain the Poly1305 algorithm.
     * @param pKeyType the symmetric key type
     * @return the algorithm
     * @throws OceanusException on error
     */
    private String getPoly1305Algorithm(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!standardSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Return algorithm name */
        return "POLY1305-" + pKeyType.name();
    }

    /**
     * Obtain the MAC algorithm.
     * @param pMacType the MAC type
     * @return the algorithm
     */
    private static String getMacAlgorithm(final GordianMacType pMacType) {
        switch (pMacType) {
            case VMPC:
                return "VMPC-MAC";
            case SKEIN:
                return "SKEIN-MAC-512-512";
            default:
                return null;
        }
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
     * @param pKeyType the keyType
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private JcaKeyPairGenerator getJcaKeyPairGenerator(final GordianAsymKeyType pKeyType) throws OceanusException {
        if (GordianAsymKeyType.RSA.equals(pKeyType)) {
            return new JcaRSAKeyPairGenerator(this);
        } else {
            return new JcaECKeyPairGenerator(this, pKeyType);
        }
    }

    /**
     * Create the BouncyCastle Signer.
     * @param pPrivateKey the privateKey
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianSigner getJcaSigner(final JcaPrivateKey pPrivateKey) throws OceanusException {
        if (GordianAsymKeyType.RSA.equals(pPrivateKey.getKeyType())) {
            return new JcaRSASigner(pPrivateKey, getRandom());
        } else {
            return new JcaECDSASigner(pPrivateKey, getRandom());
        }
    }

    /**
     * Create the BouncyCastle KEM Sender.
     * @param pPublicKey the publicKey
     * @return the Validator
     * @throws OceanusException on error
     */
    private static GordianValidator getJcaValidator(final JcaPublicKey pPublicKey) throws OceanusException {
        if (GordianAsymKeyType.RSA.equals(pPublicKey.getKeyType())) {
            return new JcaRSAValidator(pPublicKey);
        } else {
            return new JcaECDSAValidator(pPublicKey);
        }
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
     * @return the maximum
     */
    private static Predicate<GordianSymKeyType> generateSymKeyPredicate(final boolean pRestricted) {
        return p -> p.validForRestriction(pRestricted);
    }

    /**
     * Generate standard symKey predicate.
     * @param pRestricted are keys restricted?
     * @return the maximum
     */
    private static Predicate<GordianSymKeyType> generateStdSymKeyPredicate(final boolean pRestricted) {
        return p -> p.validForRestriction(pRestricted) && p.isStdBlock();
    }

    /**
     * Generate streamKey predicate.
     * @param pRestricted are keys restricted?
     * @return the maximum
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
}
