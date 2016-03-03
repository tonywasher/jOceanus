/*******************************************************************************
 * jGordianKnot: Security Suite
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
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.KeccakDigest;
import org.bouncycastle.crypto.digests.RIPEMD320Digest;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.HC128Engine;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.crypto.engines.ISAACEngine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
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
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.modes.CFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyEncapsulation.GordianKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacType;
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
     * Skein state size.
     */
    protected static final int SKEIN_STATE = 512;

    /**
     * ThreeFish block size.
     */
    protected static final int THREEFISH_BLOCK = 256;

    /**
     * Preferred length.
     */
    protected static final int PREFERRED_LENGTH = 512;

    /**
     * Predicate for all digestTypes.
     */
    private static final Predicate<GordianDigestType> PREDICATE_DIGESTS = p -> true;

    /**
     * Predicate for all supported macTypes.
     */
    private static final Predicate<GordianMacType> PREDICATE_MACS = p -> true;

    /**
     * Predicate for all supported streamKeyTypes.
     */
    private final Predicate<GordianStreamKeyType> theStreamPredicate = p -> p.validForRestriction(isRestricted());

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
    private final BouncyKeyGeneratorCache theGeneratorCache;

    /**
     * SP800 Factory.
     */
    private final SP800Factory theSP800Factory;

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
    public Predicate<GordianDigestType> supportedDigests() {
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
                                                 final boolean pPadding) throws OceanusException {
        /* Check validity of SymKey */
        if (!supportedSymKeys().test(pKeyType)) {
            throw new GordianDataException(getInvalidText(pKeyType));
        }

        /* Create the cipher */
        BufferedBlockCipher myBCCipher = getBCBlockCipher(pKeyType, pMode, pPadding);
        return new BouncySymKeyCipher(this, pKeyType, pMode, pPadding, myBCCipher);
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
        BouncySymKeyCipher myBCCipher = createSymKeyCipher(pKeyType, GordianCipherMode.CBC, false);
        return new BouncyWrapCipher(this, myBCCipher);
    }

    @Override
    public GordianSigner createSigner(final GordianPrivateKey pPrivateKey,
                                      final GordianDigestType pDigestType) throws OceanusException {
        /* Create the signer */
        return getBCSigner((BouncyPrivateKey) pPrivateKey, createDigest(pDigestType));
    }

    @Override
    public GordianValidator createValidator(final GordianPublicKey pPublicKey,
                                            final GordianDigestType pDigestType) throws OceanusException {
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
                return new SHA512Digest();
            case TIGER:
                return new TigerDigest();
            case WHIRLPOOL:
                return new WhirlpoolDigest();
            case GOST:
                return new GOST3411Digest();
            case RIPEMD:
                return new RIPEMD320Digest();
            case SKEIN:
                return new SkeinDigest(SKEIN_STATE, PREFERRED_LENGTH);
            case SM3:
                return new SM3Digest();
            case BLAKE:
                return new Blake2bDigest();
            case KECCAK:
                return new KeccakDigest(PREFERRED_LENGTH);
            default:
                throw new GordianDataException(getInvalidText(pDigestType));
        }
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
                return getBCHMac(pMacSpec.getDigestType());
            case GMAC:
                return getBCGMac(pMacSpec.getKeyType());
            case POLY1305:
                return getBCPoly1305Mac(pMacSpec.getKeyType());
            case SKEIN:
                return getBCSkeinMac();
            case VMPC:
                return getBCVMPCMac();
            default:
                throw new GordianDataException(getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle HMAC.
     * @param pDigestType the digest type
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCHMac(final GordianDigestType pDigestType) throws OceanusException {
        BouncyDigest myDigest = createDigest(pDigestType);
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
     * @return the MAC
     */
    private static Mac getBCSkeinMac() {
        return new SkeinMac(SKEIN_STATE, PREFERRED_LENGTH);
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
                                                        final boolean pPadding) throws OceanusException {
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
     * Create the BouncyCastle Mode Cipher.
     * @param pEngine the underlying engine
     * @param pPadding use padding true/false
     * @return the Cipher
     */
    private static BufferedBlockCipher getBCSymBufferedCipher(final BlockCipher pEngine,
                                                              final boolean pPadding) {
        return pPadding
                        ? new PaddedBufferedBlockCipher(pEngine, new ISO7816d4Padding())
                        : new BufferedBlockCipher(pEngine);
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
    private GordianValidator getBCValidator(final BouncyPublicKey pPublicKey,
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
}
