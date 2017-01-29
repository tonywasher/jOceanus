/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.security.SecureRandom;
import java.util.function.Predicate;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.MD5Digest;
import org.bouncycastle.crypto.digests.RIPEMD128Digest;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.RIPEMD256Digest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.digests.SHA224Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.digests.SHA384Digest;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.HC128Engine;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.crypto.engines.ISAACEngine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.crypto.engines.XSalsa20Engine;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.crypto.macs.VMPCMac;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.CTSBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.X923Padding;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
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
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyECIESReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyECIESSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyRSAKEMReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyRSAKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyECDSASigner;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyECDSAValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyRSASigner;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyRSAValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.sp800.SP800Factory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Factory for BouncyCastle Classes.
 */
public final class BouncyFactory
        extends GordianFactory {
    /**
     * ThreeFish block size.
     */
    protected static final int THREEFISH_BLOCK = 256;

    /**
     * Predicate for all digestTypes.
     */
    private static final Predicate<GordianDigestType> PREDICATE_DIGESTS;

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
    private final BouncyKeyGeneratorCache theGeneratorCache;

    /**
     * SP800 Factory.
     */
    private final SP800Factory theSP800Factory;

    /**
     * Static Constructor.
     */
    static {
        /* Create the Predicates */
        PREDICATE_DIGESTS = generateDigestPredicate();
        PREDICATE_SIGNDIGESTS = GordianDigestType::isSignatureDigest;
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
    public BouncyFactory() throws OceanusException {
        this(new GordianParameters());
    }

    /**
     * Constructor.
     * @param pParameters the parameters
     * @throws OceanusException on error
     */
    public BouncyFactory(final GordianParameters pParameters) throws OceanusException {
        /* Initialise underlying class */
        super(pParameters);

        /* Generate the predicates */
        boolean isRestricted = pParameters.useRestricted();
        theSymPredicate = generateSymKeyPredicate(isRestricted);
        theStdSymPredicate = generateStdSymKeyPredicate(isRestricted);
        theStreamPredicate = generateStreamKeyPredicate(isRestricted);

        /* Create the keyGenerator cache */
        theGeneratorCache = new BouncyKeyGeneratorCache();

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
    public BouncyDigest createDigest(final GordianDigestType pDigestType) throws OceanusException {
        /* Check validity of Digests */
        if (!supportedDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Create digest */
        Digest myBCDigest = getBCDigest(pDigestType);
        return new BouncyDigest(pDigestType, myBCDigest);
    }

    @Override
    public BouncyDigest createDigest(final GordianDigestType pDigestType,
                                     final GordianLength pLength) throws OceanusException {
        /* Check validity of Digests */
        if (!supportedDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Adjust the length to ensure support */
        GordianLength myLength = pDigestType.adjustLength(pLength);

        /* Create digest */
        Digest myBCDigest = getBCDigest(pDigestType, myLength);
        return new BouncyDigest(pDigestType, myBCDigest);
    }

    @Override
    public Predicate<GordianDigestType> supportedDigests() {
        return PREDICATE_DIGESTS;
    }

    @Override
    public Predicate<GordianDigestType> supportedHMacDigests() {
        return PREDICATE_DIGESTS;
    }

    @Override
    public BouncyMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        Mac myBCMac = getBCMac(pMacSpec);
        return new BouncyMac(this, pMacSpec, myBCMac);
    }

    @Override
    public Predicate<GordianMacType> supportedMacs() {
        return PREDICATE_MACS;
    }

    @Override
    public <T> BouncyKeyGenerator<T> getKeyGenerator(final T pKeyType) throws OceanusException {
        /* Look up in the cache */
        BouncyKeyGenerator<T> myGenerator = theGeneratorCache.getCachedKeyGenerator(pKeyType);
        if (myGenerator == null) {
            /* Create the new generator */
            CipherKeyGenerator myBCGenerator = getBCKeyGenerator(pKeyType);
            myGenerator = new BouncyKeyGenerator<>(this, pKeyType, myBCGenerator);

            /* Add to cache */
            theGeneratorCache.cacheKeyGenerator(myGenerator);
        }
        return myGenerator;
    }

    @Override
    public BouncyKeyPairGenerator getKeyPairGenerator(final GordianAsymKeyType pKeyType) throws OceanusException {
        /* Look up in the cache */
        BouncyKeyPairGenerator myGenerator = theGeneratorCache.getCachedKeyPairGenerator(pKeyType);
        if (myGenerator == null) {
            /* Create the new generator */
            myGenerator = getBCKeyPairGenerator(pKeyType);

            /* Add to cache */
            theGeneratorCache.cacheKeyPairGenerator(myGenerator);
        }
        return myGenerator;
    }

    @Override
    public BouncySymKeyCipher createSymKeyCipher(final GordianSymKeyType pKeyType,
                                                 final GordianCipherMode pMode,
                                                 final GordianPadding pPadding) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Check validity of Mode */
        if (pMode == null) {
            throw new GordianDataException(getInvalidText(pMode));
        }

        /* Check validity of Padding */
        if (pPadding == null
            || (!GordianPadding.NONE.equals(pPadding)
                && !pMode.allowsPadding())) {
            throw new GordianDataException(getInvalidText(pPadding));
        }

        /* Create the cipher */
        BufferedBlockCipher myBCCipher = getBCBlockCipher(pKeyType, pMode, pPadding);
        return new BouncySymKeyCipher(this, pKeyType, pMode, pPadding, myBCCipher);
    }

    @Override
    public BouncyAADCipher createAADCipher(final GordianSymKeyType pKeyType,
                                           final GordianCipherMode pMode) throws OceanusException {
        /* Check validity of SymKey */
        if (!standardSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Check validity of Mode */
        if (pMode == null) {
            throw new GordianDataException(getInvalidText(pMode));
        }

        /* Create the cipher */
        AEADBlockCipher myBCCipher = getBCAADCipher(pKeyType, pMode);
        return new BouncyAADCipher(this, pKeyType, pMode, myBCCipher);
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
    public BouncyStreamKeyCipher createStreamKeyCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        /* Check validity of StreamKey */
        if (!supportedStreamKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        StreamCipher myBCCipher = getBCStreamCipher(pKeyType);
        return new BouncyStreamKeyCipher(this, pKeyType, myBCCipher);
    }

    @Override
    public Predicate<GordianStreamKeyType> supportedStreamKeys() {
        return theStreamPredicate;
    }

    @Override
    public BouncyWrapCipher createWrapCipher(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        BouncySymKeyCipher myBCCipher = createSymKeyCipher(pKeyType, GordianCipherMode.CBC, GordianPadding.NONE);
        return new BouncyWrapCipher(this, myBCCipher);
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
        return getBCSigner((BouncyPrivateKey) pPrivateKey, createDigest(pDigestType));
    }

    @Override
    public GordianValidator createValidator(final GordianPublicKey pPublicKey,
                                            final GordianDigestType pDigestType) throws OceanusException {
        /* Check validity of Digest */
        if (!signatureDigests().test(pDigestType)) {
            throw new GordianDataException(getInvalidText(pDigestType));
        }

        /* Create the validator */
        return getBCValidator((BouncyPublicKey) pPublicKey, createDigest(pDigestType));
    }

    /**
     * Create KEMessage.
     * @param pPublicKey the publicKey
     * @return the KEMSender
     * @throws OceanusException on error
     */
    public GordianKEMSender createKEMessage(final GordianPublicKey pPublicKey) throws OceanusException {
        /* Create the sender */
        return getBCKEMSender((BouncyPublicKey) pPublicKey);
    }

    /**
     * Parse KEMessage.
     * @param pPrivateKey the publicKey
     * @param pMessage the cipherText
     * @return the parsed KEMessage
     * @throws OceanusException on error
     */
    public GordianKeyEncapsulation parseKEMessage(final GordianPrivateKey pPrivateKey,
                                                  final byte[] pMessage) throws OceanusException {
        /* Create the parser */
        return getBCKEMParser((BouncyPrivateKey) pPrivateKey, pMessage);
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
     * Create the BouncyCastle digest.
     * @param pDigestType the digest type
     * @return the digest
     * @throws OceanusException on error
     */
    private static Digest getBCDigest(final GordianDigestType pDigestType) throws OceanusException {
        switch (pDigestType) {
            case SHA2:
                return getSHA2Digest(GordianDigestType.SHA2.getDefaultLength());
            case TIGER:
                return new TigerDigest();
            case WHIRLPOOL:
                return new WhirlpoolDigest();
            case GOST:
                return getGOSTDigest(GordianDigestType.GOST.getDefaultLength());
            case RIPEMD:
                return getRIPEMDDigest(GordianDigestType.RIPEMD.getDefaultLength());
            case SKEIN:
                return getSkeinDigest(GordianDigestType.SKEIN.getDefaultLength());
            case SM3:
                return new SM3Digest();
            case BLAKE:
                return getBlake2bDigest(GordianDigestType.BLAKE.getDefaultLength());
            case SHA3:
                return getSHA3Digest(GordianDigestType.SHA3.getDefaultLength());
            case SHA1:
                return new SHA1Digest();
            case MD5:
                return new MD5Digest();
            default:
                throw new GordianDataException(getInvalidText(pDigestType));
        }
    }

    /**
     * Create the BouncyCastle digest.
     * @param pDigestType the digest type
     * @param pLength the digest length
     * @return the digest
     * @throws OceanusException on error
     */
    private static Digest getBCDigest(final GordianDigestType pDigestType,
                                      final GordianLength pLength) throws OceanusException {
        switch (pDigestType) {
            case SHA2:
                return getSHA2Digest(pLength);
            case GOST:
                return getGOSTDigest(pLength);
            case RIPEMD:
                return getRIPEMDDigest(pLength);
            case SKEIN:
                return getSkeinDigest(pLength);
            case SHA3:
                return getSHA3Digest(pLength);
            case BLAKE:
                return getBlake2bDigest(pLength);
            default:
                return getBCDigest(pDigestType);
        }
    }

    /**
     * Create the BouncyCastle RIPEMD digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getRIPEMDDigest(final GordianLength pLength) {
        switch (pLength) {
            case LEN_128:
                return new RIPEMD128Digest();
            case LEN_160:
                return new RIPEMD160Digest();
            case LEN_256:
                return new RIPEMD256Digest();
            case LEN_320:
            default:
                return new RIPEMD320Digest();
        }
    }

    /**
     * Create the BouncyCastle Blake2B digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getBlake2bDigest(final GordianLength pLength) {
        return new Blake2bDigest(pLength.getLength());
    }

    /**
     * Create the BouncyCastle SHA2 digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSHA2Digest(final GordianLength pLength) {
        switch (pLength) {
            case LEN_224:
                return new SHA224Digest();
            case LEN_256:
                return new SHA256Digest();
            case LEN_384:
                return new SHA384Digest();
            case LEN_512:
            default:
                return new SHA512Digest();
        }
    }

    /**
     * Create the BouncyCastle SHA3 digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSHA3Digest(final GordianLength pLength) {
        return new SHA3Digest(pLength.getLength());
    }

    /**
     * Create the BouncyCastle Skein digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSkeinDigest(final GordianLength pLength) {
        return new SkeinDigest(pLength.getSkeinState().getLength(), pLength.getLength());
    }

    /**
     * Create the BouncyCastle GOST digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getGOSTDigest(final GordianLength pLength) {
        return GordianLength.LEN_256.equals(pLength)
                                                     ? new GOST3411_2012_256Digest()
                                                     : new GOST3411_2012_512Digest();
    }

    /**
     * Create the BouncyCastle MAC.
     * @param pMacSpec the MacSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCMac(final GordianMacSpec pMacSpec) throws OceanusException {
        switch (pMacSpec.getMacType()) {
            case HMAC:
                return getBCHMac(pMacSpec.getDigestType(), pMacSpec.getDigestLength());
            case GMAC:
                return getBCGMac(pMacSpec.getKeyType());
            case POLY1305:
                return getBCPoly1305Mac(pMacSpec.getKeyType());
            case SKEIN:
                return getBCSkeinMac(pMacSpec.getDigestLength());
            case VMPC:
                return getBCVMPCMac();
            default:
                throw new GordianDataException(getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle HMAC.
     * @param pDigestType the digest type
     * @param pLength the length
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCHMac(final GordianDigestType pDigestType,
                          final GordianLength pLength) throws OceanusException {
        BouncyDigest myDigest = createDigest(pDigestType, pLength);
        return new HMac(myDigest.getDigest());
    }

    /**
     * Create the BouncyCastle GMac.
     * @param pSymKeyType the SymKeyType
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCGMac(final GordianSymKeyType pSymKeyType) throws OceanusException {
        return new GMac(new GCMBlockCipher(getBCStdSymEngine(pSymKeyType)));
    }

    /**
     * Create the BouncyCastle Poly1305Mac.
     * @param pSymKeyType the SymKeyType
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCPoly1305Mac(final GordianSymKeyType pSymKeyType) throws OceanusException {
        return new Poly1305(getBCStdSymEngine(pSymKeyType));
    }

    /**
     * Create the BouncyCastle SkeinMac.
     * @param pLength the length
     * @return the MAC
     */
    private static Mac getBCSkeinMac(final GordianLength pLength) {
        GordianLength myLength = pLength == null
                                                 ? GordianDigestType.SKEIN.getDefaultLength()
                                                 : pLength;
        return new SkeinMac(myLength.getSkeinState().getLength(), myLength.getLength());
    }

    /**
     * Create the BouncyCastle VMPCMac.
     * @return the MAC
     */
    private static Mac getBCVMPCMac() {
        return new VMPCMac();
    }

    /**
     * Create the BouncyCastle Block Cipher.
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     * @param pPadding use padding true/false
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static BufferedBlockCipher getBCBlockCipher(final GordianSymKeyType pKeyType,
                                                        final GordianCipherMode pMode,
                                                        final GordianPadding pPadding) throws OceanusException {
        /* Build the cipher */
        BlockCipher myEngine = getBCSymEngine(pKeyType);
        BlockCipher myMode = getBCSymModeCipher(myEngine, pMode);
        return getBCSymBufferedCipher(myMode, pPadding);
    }

    /**
     * Create the BouncyCastle Stream Cipher.
     * @param pKeyType the keyType
     * @return the Cipher
     * @throws OceanusException on error
     */
    private StreamCipher getBCStreamCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        switch (pKeyType) {
            case HC:
                return isRestricted()
                                      ? new HC128Engine()
                                      : new HC256Engine();
            case CHACHA:
                return new ChaChaEngine();
            case CHACHA7539:
                return new ChaCha7539Engine();
            case SALSA20:
                return new Salsa20Engine();
            case XSALSA20:
                return new XSalsa20Engine();
            case VMPC:
                return new VMPCEngine();
            case GRAIN:
                return new Grain128Engine();
            case ISAAC:
                return new ISAACEngine();
            case RC4:
                return new RC4Engine();
            default:
                throw new GordianDataException(getInvalidText(pKeyType));
        }
    }

    /**
     * Create the BouncyCastle Standard Cipher Engine.
     * @param pKeyType the SymKeyType
     * @return the Engine
     * @throws OceanusException on error
     */
    private BlockCipher getBCStdSymEngine(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Check validity of SymKey */
        if (!standardSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        return getBCSymEngine(pKeyType);
    }

    /**
     * Create the BouncyCastle Cipher Engine.
     * @param pKeyType the SymKeyType
     * @return the Engine
     * @throws OceanusException on error
     */
    private static BlockCipher getBCSymEngine(final GordianSymKeyType pKeyType) throws OceanusException {
        switch (pKeyType) {
            case AES:
                return new AESEngine();
            case SERPENT:
                return new SerpentEngine();
            case TWOFISH:
                return new TwofishEngine();
            case CAMELLIA:
                return new CamelliaEngine();
            case RC6:
                return new RC6Engine();
            case CAST6:
                return new CAST6Engine();
            case THREEFISH:
                return new ThreefishEngine(THREEFISH_BLOCK);
            case SM4:
                return new SM4Engine();
            case NOEKEON:
                return new NoekeonEngine();
            case SEED:
                return new SEEDEngine();
            default:
                throw new GordianDataException(getInvalidText(pKeyType));
        }
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     * @param pEngine the underlying engine
     * @param pMode the cipher mode
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static BlockCipher getBCSymModeCipher(final BlockCipher pEngine,
                                                  final GordianCipherMode pMode) throws OceanusException {
        switch (pMode) {
            case ECB:
                return pEngine;
            case CBC:
                return new CBCBlockCipher(pEngine);
            case SIC:
                return new SICBlockCipher(pEngine);
            case CFB:
                return new CFBBlockCipher(pEngine, pEngine.getBlockSize());
            case OFB:
                return new OFBBlockCipher(pEngine, pEngine.getBlockSize());
            default:
                throw new GordianDataException(getInvalidText(pMode));
        }
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     * @param pKeyType the keyType
     * @param pMode the cipher mode
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static AEADBlockCipher getBCAADCipher(final GordianSymKeyType pKeyType,
                                                  final GordianCipherMode pMode) throws OceanusException {
        switch (pMode) {
            case EAX:
                return new EAXBlockCipher(getBCSymEngine(pKeyType));
            case CCM:
                return new CCMBlockCipher(getBCSymEngine(pKeyType));
            case GCM:
                return new GCMBlockCipher(getBCSymEngine(pKeyType));
            case OCB:
                return new OCBBlockCipher(getBCSymEngine(pKeyType), getBCSymEngine(pKeyType));
            default:
                throw new GordianDataException(getInvalidText(pMode));
        }
    }

    /**
     * Create the BouncyCastle Mode Cipher.
     * @param pEngine the underlying engine
     * @param pPadding use padding true/false
     * @return the Cipher
     */
    private static BufferedBlockCipher getBCSymBufferedCipher(final BlockCipher pEngine,
                                                              final GordianPadding pPadding) {
        switch (pPadding) {
            case CTS:
                return new CTSBlockCipher(pEngine);
            case X923:
                return new PaddedBufferedBlockCipher(pEngine, new X923Padding());
            case PKCS7:
                return new PaddedBufferedBlockCipher(pEngine, new PKCS7Padding());
            case ISO7816D4:
                return new PaddedBufferedBlockCipher(pEngine, new ISO7816d4Padding());
            case NONE:
            default:
                return new BufferedBlockCipher(pEngine);
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator.
     * @param pKeyType the keyType
     * @return the KeyGenerator
     */
    private static CipherKeyGenerator getBCKeyGenerator(final Object pKeyType) {
        return (pKeyType instanceof GordianMacSpec)
               && (((GordianMacSpec) pKeyType).getMacType() == GordianMacType.POLY1305)
                                                                                        ? new Poly1305KeyGenerator()
                                                                                        : new CipherKeyGenerator();
    }

    /**
     * Create the BouncyCastle KeyPairGenerator.
     * @param pKeyType the keyType
     * @return the KeyGenerator
     */
    private BouncyKeyPairGenerator getBCKeyPairGenerator(final GordianAsymKeyType pKeyType) {
        if (GordianAsymKeyType.RSA.equals(pKeyType)) {
            return new BouncyRSAKeyPairGenerator(this);
        } else {
            return new BouncyECKeyPairGenerator(this, pKeyType);
        }
    }

    /**
     * Create the BouncyCastle Signer.
     * @param pPrivateKey the privateKey
     * @param pDigest the digest
     * @return the Signer
     */
    private GordianSigner getBCSigner(final BouncyPrivateKey pPrivateKey,
                                      final BouncyDigest pDigest) {
        if (GordianAsymKeyType.RSA.equals(pPrivateKey.getKeyType())) {
            return new BouncyRSASigner((BouncyRSAPrivateKey) pPrivateKey, pDigest, getRandom());
        } else {
            return new BouncyECDSASigner((BouncyECPrivateKey) pPrivateKey, pDigest, getRandom());
        }
    }

    /**
     * Create the BouncyCastle KEM Sender.
     * @param pPublicKey the publicKey
     * @param pDigest the digest
     * @return the Validator
     */
    private static GordianValidator getBCValidator(final BouncyPublicKey pPublicKey,
                                                   final BouncyDigest pDigest) {
        if (GordianAsymKeyType.RSA.equals(pPublicKey.getKeyType())) {
            return new BouncyRSAValidator((BouncyRSAPublicKey) pPublicKey, pDigest);
        } else {
            return new BouncyECDSAValidator((BouncyECPublicKey) pPublicKey, pDigest);
        }
    }

    /**
     * Create the BouncyCastle KEM Sender.
     * @param pPublicKey the publicKey
     * @return the KEMSender
     * @throws OceanusException on error
     */
    private GordianKEMSender getBCKEMSender(final BouncyPublicKey pPublicKey) throws OceanusException {
        if (GordianAsymKeyType.RSA.equals(pPublicKey.getKeyType())) {
            return new BouncyRSAKEMSender(this, (BouncyRSAPublicKey) pPublicKey);
        } else {
            return new BouncyECIESSender(this, (BouncyECPublicKey) pPublicKey);
        }
    }

    /**
     * Create the BouncyCastle KEM Receiver.
     * @param pPrivateKey the privateKey
     * @param pCipherText the cipherText
     * @return the KEMParser
     * @throws OceanusException on error
     */
    private GordianKeyEncapsulation getBCKEMParser(final BouncyPrivateKey pPrivateKey,
                                                   final byte[] pCipherText) throws OceanusException {
        if (GordianAsymKeyType.RSA.equals(pPrivateKey.getKeyType())) {
            return new BouncyRSAKEMReceiver(this, (BouncyRSAPrivateKey) pPrivateKey, pCipherText);
        } else {
            return new BouncyECIESReceiver(this, (BouncyECPrivateKey) pPrivateKey, pCipherText);
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
        return p -> p.validForRestriction(pRestricted);
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
