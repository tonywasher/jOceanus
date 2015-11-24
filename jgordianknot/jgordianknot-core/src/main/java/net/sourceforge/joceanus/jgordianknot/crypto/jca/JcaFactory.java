/*******************************************************************************
0o * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jgordianknot/jgordianknot-core/src/main/java/net/sourceforge/joceanus/jgordianknot/crypto/CipherSetRecipe.java $
 * $Revision: 647 $
 * $Author: Tony $
 * $Date: 2015-11-04 08:58:02 +0000 (Wed, 04 Nov 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

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
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSP800Type;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
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
    private static final Predicate<GordianDigestType> PREDICATE_DIGESTS = p -> JcaDigest.isSupported(p);

    /**
     * Predicate for all supported macTypes.
     */
    private static final Predicate<GordianMacType> PREDICATE_MACS = p -> true;

    /**
     * Predicate for all supported streamKeyTypes.
     */
    private final Predicate<GordianStreamKeyType> theStreamPredicate = p -> p.validForRestriction(isRestricted()) && p != GordianStreamKeyType.ISAAC;

    /**
     * Predicate for all supported symKeyTypes.
     */
    private final Predicate<GordianSymKeyType> theSymPredicate = p -> p.validForRestriction(isRestricted());

    /**
     * Predicate for all standard symKeyTypes.
     */
    private final Predicate<GordianSymKeyType> theStdSymPredicate = p -> p.validForRestriction(isRestricted()) && p.isStdBlock();

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
    public Predicate<GordianDigestType> supportedDigests() {
        return PREDICATE_DIGESTS;
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
            myGenerator = new JcaKeyGenerator<T>(this, pKeyType, myJavaGenerator);

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
    public JcaCipher<GordianSymKeyType> createSymKeyCipher(final GordianSymKeyType pKeyType,
                                                           final GordianCipherMode pMode,
                                                           final boolean pPadding) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        Cipher myBCCipher = getJavaCipher(pKeyType, pMode, pPadding);
        return new JcaCipher<GordianSymKeyType>(this, pKeyType, pMode, pPadding, myBCCipher);
    }

    @Override
    public JcaCipher<GordianStreamKeyType> createStreamKeyCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        /* Check validity of StreamKey */
        if (!supportedStreamKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        Cipher myJavaCipher = getJavaCipher(pKeyType);
        return new JcaCipher<GordianStreamKeyType>(this, pKeyType, null, false, myJavaCipher);
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
        JcaCipher<GordianSymKeyType> myJcaCipher = createSymKeyCipher(pKeyType, GordianCipherMode.CBC, false);
        return new JcaWrapCipher(this, myJcaCipher);
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
                                        final boolean pPadding) throws OceanusException {
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
        switch (pDigestType) {
            case KECCAK:
                return "HMacKECCAK512";
            default:
                return "HMac"
                       + JcaDigest.getAlgorithm(pDigestType);
        }
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
            case OFB:
            case SIC:
            case CBC:
            case CFB:
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
    private static String getPaddingAlgorithm(final boolean pPadding) {
        return pPadding
                        ? "ISO7816-4Padding"
                        : "NoPadding";
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
                return pKeyType.name();
            default:
                throw new GordianDataException(getInvalidText(pKeyType));
        }
    }
}
