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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianLength;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSPHINCSKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianWrapCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaDSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaDiffieHellmanKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaEdKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaQTESLAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaSPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPairGenerator.JcaXMSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaDSASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaEdDSASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaGOSTSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaQTESLASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRSASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaRainbowSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaSPHINCSSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaSignature.JcaXMSSSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.prng.GordianBaseSecureRandom;
import net.sourceforge.joceanus.jgordianknot.crypto.prng.GordianRandomFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.jcajce.provider.BouncyCastlePQCProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

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
     * Kalyna algorithm name.
     */
    private static final String KALYNA_ALGORITHM = "DSTU7624";

    /**
     * CMAC algorithm name.
     */
    private static final String CMAC_ALGORITHM = "CMAC";

    /**
     * Static Constructor.
     */
    static {
        /* Select unlimited security */
        Security.setProperty("crypto.policy", "unlimited");
    }

    /**
     * Note the standard provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Note the post quantum provider.
     */
    private static final Provider BCPQPROV = new BouncyCastlePQCProvider();

    /**
     * Cache for KeyGenerators.
     */
    private final JcaKeyGeneratorCache theGeneratorCache;

    /**
     * SP800 Factory.
     */
    private final GordianRandomFactory theSP800Factory;

    /**
     * Static Constructor.
     */
    static {
        /* Make sure that the Bouncy Castle Providers are installed */
        Security.addProvider(BCPROV);
        Security.addProvider(BCPQPROV);
    }

    /**
     * Constructor.
     * @param pParameters the parameters
     * @param pGenerator the factoryGenerator
     * @throws OceanusException on error
     */
    public JcaFactory(final GordianParameters pParameters,
                      final GordianFactoryGenerator pGenerator) throws OceanusException {
        /* Initialise underlying class */
        super(pParameters, pGenerator);

        /* Create the keyGenerator cache */
        theGeneratorCache = new JcaKeyGeneratorCache();

        /* Create the SP800 Factory */
        theSP800Factory = new GordianRandomFactory();
        setSecureRandom(theSP800Factory.getInitialRandom());

        /* Create the SecureRandom instance */
        final GordianBaseSecureRandom myRandom = createRandom(theSP800Factory.generateRandomSpec(this));
        setSecureRandom(myRandom);
        theGeneratorCache.resetCache();
    }

    @Override
    public GordianBaseSecureRandom createRandom(final GordianRandomSpec pRandomSpec) throws OceanusException {
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
        final MessageDigest myJavaDigest = getJavaDigest(pDigestSpec);
        return new JcaDigest(pDigestSpec, myJavaDigest);
    }

    @Override
    public JcaMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Check validity of MacSpec */
        if (!supportedMacSpecs().test(pMacSpec)) {
            throw new GordianDataException(getInvalidText(pMacSpec));
        }

        /* Create Mac */
        final Mac myJavaMac = getJavaMac(pMacSpec);
        return new JcaMac(this, pMacSpec, myJavaMac);
    }

    @Override
    protected boolean validMacType(final GordianMacType pMacType) {
        switch (pMacType) {
            case BLAKE:
            case KALYNA:
                return false;
            default:
                return super.validMacType(pMacType);
        }
    }

    @Override
    public <T> JcaKeyGenerator<T> getKeyGenerator(final T pKeyType) throws OceanusException {
        /* Look up in the cache */
        JcaKeyGenerator<T> myGenerator = theGeneratorCache.getCachedKeyGenerator(pKeyType);
        if (myGenerator == null) {
            /* Create the new generator */
            final String myAlgorithm = getKeyAlgorithm(pKeyType);
            final KeyGenerator myJavaGenerator = getJavaKeyGenerator(myAlgorithm);
            myGenerator = new JcaKeyGenerator<>(this, pKeyType, myJavaGenerator);

            /* Add to cache */
            theGeneratorCache.cacheKeyGenerator(myGenerator);
        }
        return myGenerator;
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
    public JcaCipher<GordianSymKeySpec> createSymKeyCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKey */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        if (!supportedSymKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!validSymCipherSpec(pCipherSpec, false)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Create the cipher */
        final Cipher myBCCipher = getJavaCipher(pCipherSpec);
        return new JcaCipher<>(this, pCipherSpec, myBCCipher);
    }

    @Override
    public JcaAADCipher createAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKey */
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        if (!supportedSymKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!validSymCipherSpec(pCipherSpec, true)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Create the cipher */
        final Cipher myBCCipher = getJavaCipher(pCipherSpec);
        return new JcaAADCipher(this, pCipherSpec, myBCCipher);
    }

    @Override
    public JcaCipher<GordianStreamKeyType> createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of StreamKey */
        final GordianStreamKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedStreamKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(myKeyType));
        }

        /* Create the cipher */
        final Cipher myJavaCipher = getJavaCipher(myKeyType);
        return new JcaCipher<>(this, pCipherSpec, myJavaCipher);
    }

    @Override
    public GordianWrapCipher createWrapCipher(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeySpecs().test(pKeySpec)) {
            throw new GordianDataException(getInvalidText(pKeySpec));
        }

        /* Create the cipher */
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(pKeySpec, GordianPadding.NONE);
        final JcaCipher<GordianSymKeySpec> myJcaCipher = createSymKeyCipher(mySpec);
        return createWrapCipher(myJcaCipher);
    }

    @Override
    public Predicate<GordianSignatureSpec> supportedSignatureSpec() {
        return this::validSignatureSpec;
    }

    @Override
    public GordianSignature createSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        if (!validSignatureSpec(pSignatureSpec)) {
            throw new GordianDataException(getInvalidText(pSignatureSpec));
        }

        /* Create the signer */
        return getJcaSigner(pSignatureSpec);
    }

    /**
     * Create the SP800 SecureRandom instance.
     * @param pRandomSpec the randomSpec
     * @return the secureRandom
     * @throws OceanusException on error
     */
    private GordianBaseSecureRandom getSP800SecureRandom(final GordianRandomSpec pRandomSpec) throws OceanusException {
        final GordianDigestSpec myDigest = pRandomSpec.getDigestSpec();
        switch (pRandomSpec.getRandomType()) {
            case HASH:
                return theSP800Factory.buildHash(createDigest(myDigest), true);
            case HMAC:
                final GordianMacSpec mySpec = GordianMacSpec.hMac(myDigest);
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
            case KUPYNA:
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
        if (pKeyType instanceof GordianSymKeySpec) {
            return getSymKeyAlgorithm((GordianSymKeySpec) pKeyType);
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
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(getSymKeyAlgorithm(pCipherSpec.getKeyType()))
                .append(ALGO_SEP)
                .append(getCipherModeAlgorithm(pCipherSpec))
                .append(ALGO_SEP)
                .append(getPaddingAlgorithm(pCipherSpec.getPadding()));
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
                return getGMacAlgorithm(pMacSpec.getKeySpec());
            case CMAC:
                return getCMacAlgorithm(pMacSpec.getKeySpec());
            case POLY1305:
                return getPoly1305Algorithm(pMacSpec.getKeySpec());
            case SKEIN:
                return getSkeinMacAlgorithm(pMacSpec.getDigestSpec());
            case KUPYNA:
                return getKupynaMacAlgorithm(pMacSpec.getDigestSpec());
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
            /* Massage the keyGenerator name */
            String myAlgorithm = pAlgorithm;

            /* Note that DSTU7624 has only a single keyGenerator */
            if (myAlgorithm.startsWith(KALYNA_ALGORITHM)) {
                myAlgorithm = KALYNA_ALGORITHM;
            }

            /* CMAC generators use the symKeyGenerator */
            if (myAlgorithm.endsWith(CMAC_ALGORITHM)) {
                myAlgorithm = myAlgorithm.substring(0, myAlgorithm.length() - CMAC_ALGORITHM.length());
            }

            /* Return a KeyGenerator for the algorithm */
            return KeyGenerator.getInstance(myAlgorithm, BCPROV);

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
     * Create the BouncyCastle Signature via JCA.
     * @param pAlgorithm the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws OceanusException on error
     */
    protected static Signature getJavaSignature(final String pAlgorithm,
                                                final boolean postQuantum) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a KeyPairGenerator for the algorithm */
            return Signature.getInstance(pAlgorithm, postQuantum
                                                                 ? BCPQPROV
                                                                 : BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Signature", e);
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
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getGMacAlgorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Return algorithm name */
        return getSymKeyAlgorithm(pKeySpec) + "GMAC";
    }

    /**
     * Obtain the GMAC algorithm.
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getCMacAlgorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Return algorithm name */
        return getSymKeyAlgorithm(pKeySpec) + CMAC_ALGORITHM;
    }

    /**
     * Obtain the Poly1305 algorithm.
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getPoly1305Algorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Return algorithm name */
        return "POLY1305-" + getSymKeyAlgorithm(pKeySpec);
    }

    /**
     * Obtain the Skein MAC algorithm.
     * @param pSpec the digestSpec
     * @return the algorithm
     */
    private static String getSkeinMacAlgorithm(final GordianDigestSpec pSpec) {
        final String myLen = Integer.toString(pSpec.getDigestLength().getLength());
        final String myState = Integer.toString(pSpec.getStateLength().getLength());
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Skein-MAC-")
                .append(myState)
                .append('-')
                .append(myLen);
        return myBuilder.toString();
    }

    /**
     * Obtain the Kupyna MAC algorithm.
     * @param pSpec the digestSpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getKupynaMacAlgorithm(final GordianDigestSpec pSpec) throws OceanusException {
        /* For some reason this is accessed as HMAC !!! */
        return getHMacAlgorithm(pSpec);
    }

    /**
     * Obtain the SymKey algorithm.
     * @param pKeySpec the keySpec
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private static String getSymKeyAlgorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getSymKeyType()) {
            case TWOFISH:
                return "TwoFish";
            case SERPENT:
                return "Serpent";
            case THREEFISH:
                return "ThreeFish-" + pKeySpec.getBlockLength().getLength();
            case GOST:
                return "GOST28147";
            case SHACAL2:
                return "Shacal-2";
            case KUZNYECHIK:
                return "GOST3412-2015";
            case KALYNA:
                return KALYNA_ALGORITHM + "-" + pKeySpec.getBlockLength().getLength();
            case RC5:
                return GordianLength.LEN_128.equals(pKeySpec.getBlockLength())
                                                                               ? "RC5-64"
                                                                               : "RC5";
            case AES:
            case CAMELLIA:
            case CAST6:
            case RC6:
            case ARIA:
            case NOEKEON:
            case SM4:
            case SEED:
            case SKIPJACK:
            case TEA:
            case XTEA:
            case IDEA:
            case RC2:
            case CAST5:
            case BLOWFISH:
            case DESEDE:
                return pKeySpec.getSymKeyType().name();
            default:
                throw new GordianDataException(getInvalidText(pKeySpec));
        }
    }

    /**
     * Obtain the CipherMode algorithm.
     * @param pSpec the cipherSpec
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private static String getCipherModeAlgorithm(final GordianSymCipherSpec pSpec) throws OceanusException {
        final GordianCipherMode myMode = pSpec.getCipherMode();
        switch (pSpec.getCipherMode()) {
            case ECB:
            case SIC:
            case EAX:
            case CCM:
            case GCM:
            case OCB:
            case GOFB:
            case GCFB:
                return myMode.name();
            case CBC:
            case G3413CBC:
                return GordianCipherMode.CBC.name();
            case CFB:
            case G3413CFB:
                return "CFB8";
            case OFB:
            case G3413OFB:
                return GordianCipherMode.OFB.name();
            case KCTR:
            case G3413CTR:
                return "CTR";
            case KCCM:
                return GordianCipherMode.CCM.name();
            case KGCM:
                return GordianCipherMode.GCM.name();
            default:
                throw new GordianDataException(getInvalidText(myMode));
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
            case TBC:
                return "TBCPadding";
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
            case SM2:
            case DSTU4145:
            case GOST2012:
                return new JcaECKeyPairGenerator(this, pKeySpec);
            case DSA:
                return new JcaDSAKeyPairGenerator(this, pKeySpec);
            case X25519:
            case X448:
            case ED25519:
            case ED448:
                return new JcaEdKeyPairGenerator(this, pKeySpec);
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
            case XMSS:
            case XMSSMT:
                return new JcaXMSSKeyPairGenerator(this, pKeySpec);
            case QTESLA:
                return new JcaQTESLAKeyPairGenerator(this, pKeySpec);
            default:
                throw new GordianDataException(getInvalidText(pKeySpec.getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle Signer.
     * @param pSignatureSpec the digestSpec
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianSignature getJcaSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getAsymKeyType()) {
            case RSA:
                return new JcaRSASignature(this, pSignatureSpec);
            case EC:
            case SM2:
            case DSA:
                return new JcaDSASignature(this, pSignatureSpec);
            case ED25519:
            case ED448:
                return new JcaEdDSASignature(this, pSignatureSpec);
            case GOST2012:
            case DSTU4145:
                return new JcaGOSTSignature(this, pSignatureSpec);
            case XMSS:
            case XMSSMT:
                return new JcaXMSSSignature(this, pSignatureSpec);
            case SPHINCS:
                return new JcaSPHINCSSignature(this, pSignatureSpec);
            case RAINBOW:
                return new JcaRainbowSignature(this, pSignatureSpec);
            case QTESLA:
                return new JcaQTESLASignature(this, pSignatureSpec);
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

    @Override
    protected boolean validHMacDigestType(final GordianDigestType pDigestType) {
        return JcaDigest.isHMacSupported(pDigestType) && super.validDigestType(pDigestType);
    }

    @Override
    protected boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        switch (pKeyType) {
            case SPECK:
            case ANUBIS:
            case SIMON:
            case MARS:
                return false;
            default:
                return super.validSymKeyType(pKeyType);
        }
    }

    @Override
    protected boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        switch (pKeyType) {
            case ISAAC:
            case SOSEMANUK:
                return false;
            default:
                return super.validStreamKeyType(pKeyType);
        }
    }

    @Override
    protected boolean validCMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case AES:
            case DESEDE:
            case BLOWFISH:
            case SHACAL2:
            case THREEFISH:
            case SEED:
            case SM4:
                return super.validCMacSymKeySpec(pKeySpec);
            default:
                return false;
        }
    }

    @Override
    protected boolean validGMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case KUZNYECHIK:
            case KALYNA:
                return false;
            default:
                return super.validGMacSymKeySpec(pKeySpec);
        }
    }

    @Override
    protected boolean validPoly1305SymKeySpec(final GordianSymKeySpec pKeySpec) {
        if (GordianSymKeyType.KALYNA.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        return super.validPoly1305SymKeySpec(pKeySpec);
    }

    @Override
    protected boolean validDigestType(final GordianDigestType pDigestType) {
        /* Perform standard checks */
        if (!super.validDigestType(pDigestType)) {
            return false;
        }

        /* Disable JH, and Groestl */
        switch (pDigestType) {
            case JH:
            case GROESTL:
                return false;
            default:
                return true;
        }
    }

    @Override
    protected boolean validDigestSpec(final GordianDigestSpec pDigestSpec) {
        /* Perform standard checks */
        if (!super.validDigestSpec(pDigestSpec)) {
            return false;
        }

        /* Disable SHAKE via DigestSpec */
        return !GordianDigestType.SHAKE.equals(pDigestSpec.getDigestType());
    }

    @Override
    protected boolean validSignatureDigestSpec(final GordianDigestSpec pDigestSpec) {
        return super.validDigestSpec(pDigestSpec);
    }

    @Override
    protected boolean validSignatureSpec(final GordianSignatureSpec pSpec) {
        /* validate the signatureSpec */
        if (!super.validSignatureSpec(pSpec)) {
            return false;
        }

        /* Switch on KeyType */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (pSpec.getAsymKeyType()) {
            case RSA:
                return validRSASignature(pSpec);
            case EC:
                return validECSignature(pSpec);
            case DSTU4145:
            case GOST2012:
            case SM2:
                return true;
            case DSA:
                return validDSASignature(pSpec);
            case RAINBOW:
                return validRainbowSignature(myDigest);
            case XMSS:
            case XMSSMT:
            case SPHINCS:
            case QTESLA:
            case ED25519:
            case ED448:
                return true;
            case DIFFIEHELLMAN:
            case NEWHOPE:
            case MCELIECE:
            case X25519:
            case X448:
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
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
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
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
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
     * Check ECSignature.
     * @param pSpec the digestSpec
     * @return true/false
     */
    private static boolean validDSASignature(final GordianSignatureSpec pSpec) {
        /* Switch on DigestType */
        final GordianDigestSpec myDigest = pSpec.getDigestSpec();
        switch (myDigest.getDigestType()) {
            case SHA2:
                return myDigest.getStateLength() == null;
            case SHA1:
            case SHA3:
                return true;
            default:
                return false;
        }
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

    @Override
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec,
                                         final Boolean isAAD) {
        /* Check standard features */
        if (!super.validSymCipherSpec(pCipherSpec, isAAD)) {
            return false;
        }

        /* Additional Checks */
        final GordianCipherMode myMode = pCipherSpec.getCipherMode();
        switch (pCipherSpec.getKeyType().getSymKeyType()) {
            case KALYNA:
                /* Disallow OCB, CCM and GCM */
                return !GordianCipherMode.OCB.equals(myMode)
                       && !GordianCipherMode.KCCM.equals(myMode)
                       && !GordianCipherMode.KGCM.equals(myMode)
                       && !GordianCipherMode.CCM.equals(myMode)
                       && !GordianCipherMode.GCM.equals(myMode);
            case GOST:
                /* Disallow OFB and CFB */
                return !GordianCipherMode.OFB.equals(myMode)
                       && !GordianCipherMode.CFB.equals(myMode);
            case KUZNYECHIK:
                /* Disallow OCB, OFB, CFB and CBC */
                return !GordianCipherMode.OCB.equals(myMode)
                       && !GordianCipherMode.OFB.equals(myMode)
                       && !GordianCipherMode.G3413CTR.equals(myMode)
                       && !GordianCipherMode.CFB.equals(myMode)
                       && !GordianCipherMode.CBC.equals(myMode);
            default:
                return true;
        }
    }
}
