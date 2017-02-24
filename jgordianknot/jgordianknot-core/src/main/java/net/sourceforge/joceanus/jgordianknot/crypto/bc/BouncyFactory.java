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
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
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
import org.bouncycastle.crypto.digests.SHA512tDigest;
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
import org.bouncycastle.crypto.macs.CMac;
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
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianWrapCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyDiffieHellmanReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyDiffieHellmanSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyECIESReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyECIESSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyNewHopeReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyNewHopeSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyRSAKEMReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyEncapsulation.BouncyRSAKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDiffieHellmanPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyDiffieHellmanPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyNewHopePrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyNewHopePublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRainbowPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncyRainbowPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncySPHINCSPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPair.BouncySPHINCSPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyDiffieHellmanKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyElGamalKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncyRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyKeyPairGenerator.BouncySPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyECSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyECValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyRSASigner;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyRSAValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyRainbowSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncyRainbowValidator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncySPHINCSSigner;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySignature.BouncySPHINCSValidator;
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
    public BouncyDigest createDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of DigestType */
        if (!supportedDigestSpecs().test(pDigestSpec)) {
            throw new GordianDataException(getInvalidText(pDigestSpec));
        }

        /* Create digest */
        Digest myBCDigest = getBCDigest(pDigestSpec);
        return new BouncyDigest(pDigestSpec, myBCDigest);
    }

    @Override
    public Predicate<GordianDigestType> supportedDigestTypes() {
        return PREDICATE_DIGESTS;
    }

    @Override
    public Predicate<GordianDigestType> supportedHMacDigestTypes() {
        return PREDICATE_DIGESTS;
    }

    @Override
    public BouncyMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        Mac myBCMac = getBCMac(pMacSpec);
        return new BouncyMac(this, pMacSpec, myBCMac);
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
        return theSymPredicate;
    }

    @Override
    public Predicate<GordianSymKeyType> supportedPoly1305SymKeyTypes() {
        return theStdSymPredicate;
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
    public BouncyKeyPairGenerator getKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        /* Look up in the cache */
        BouncyKeyPairGenerator myGenerator = theGeneratorCache.getCachedKeyPairGenerator(pKeySpec);
        if (myGenerator == null) {
            /* Create the new generator */
            myGenerator = getBCKeyPairGenerator(pKeySpec);

            /* Add to cache */
            theGeneratorCache.cacheKeyPairGenerator(myGenerator);
        }
        return myGenerator;
    }

    @Override
    public BouncySymKeyCipher createSymKeyCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
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
        BufferedBlockCipher myBCCipher = getBCBlockCipher(pCipherSpec);
        return new BouncySymKeyCipher(this, pCipherSpec, myBCCipher);
    }

    @Override
    public BouncyAADCipher createAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
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
        AEADBlockCipher myBCCipher = getBCAADCipher(pCipherSpec);
        return new BouncyAADCipher(this, pCipherSpec, myBCCipher);
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
    public BouncyStreamKeyCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of StreamKey */
        GordianStreamKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedStreamKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(myKeyType));
        }

        /* Create the cipher */
        StreamCipher myBCCipher = getBCStreamCipher(myKeyType);
        return new BouncyStreamKeyCipher(this, pCipherSpec, myBCCipher);
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
        GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(pKeyType, GordianPadding.NONE);
        BouncySymKeyCipher myBCCipher = createSymKeyCipher(mySpec);
        return createWrapCipher(myBCCipher);
    }

    @Override
    public BiPredicate<GordianKeyPair, GordianSignatureSpec> supportedSignatures() {
        return this::validSignatureSpec;
    }

    @Override
    public GordianSigner createSigner(final GordianKeyPair pKeyPair,
                                      final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        if (!supportedSignatures().test(pKeyPair, pSignatureSpec)) {
            throw new GordianDataException(getInvalidText(pSignatureSpec));
        }

        /* Create the signer */
        return getBCSigner((BouncyKeyPair) pKeyPair, pSignatureSpec);
    }

    @Override
    public GordianValidator createValidator(final GordianKeyPair pKeyPair,
                                            final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        /* Check validity of Signature */
        if (!supportedSignatures().test(pKeyPair, pSignatureSpec)) {
            throw new GordianDataException(getInvalidText(pSignatureSpec));
        }

        /* Create the validator */
        return getBCValidator((BouncyKeyPair) pKeyPair, pSignatureSpec);
    }

    @Override
    public GordianKEMSender createKEMessage(final GordianKeyPair pKeyPair,
                                            final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of Exchange */
        if (!supportedKeyExchanges().test(pKeyPair, pDigestSpec)) {
            throw new GordianDataException(getInvalidText(pDigestSpec));
        }

        /* Create the sender */
        return getBCKEMSender((BouncyKeyPair) pKeyPair, pDigestSpec);
    }

    @Override
    public GordianKeyEncapsulation parseKEMessage(final GordianKeyPair pKeyPair,
                                                  final GordianDigestSpec pDigestSpec,
                                                  final byte[] pMessage) throws OceanusException {
        /* Check validity of Exchange */
        if (!supportedKeyExchanges().test(pKeyPair, pDigestSpec)) {
            throw new GordianDataException(getInvalidText(pDigestSpec));
        }

        /* Create the parser */
        return getBCKEMParser((BouncyKeyPair) pKeyPair, pDigestSpec, pMessage);
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
     * Create the BouncyCastle digest.
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws OceanusException on error
     */
    protected static Digest getBCDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Access digest details */
        GordianDigestType myType = pDigestSpec.getDigestType();
        GordianLength myLen = pDigestSpec.getDigestLength();

        /* Switch on digest type */
        switch (myType) {
            case SHA2:
                return getSHA2Digest(pDigestSpec);
            case RIPEMD:
                return getRIPEMDDigest(myLen);
            case SKEIN:
                return getSkeinDigest(pDigestSpec.getStateLength(), myLen);
            case SHA3:
                return getSHA3Digest(myLen);
            case BLAKE:
                return getBlake2bDigest(myLen);
            case STREEBOG:
                return getStreebogDigest(myLen);
            case GOST:
                return new GOST3411Digest();
            case TIGER:
                return new TigerDigest();
            case WHIRLPOOL:
                return new WhirlpoolDigest();
            case SM3:
                return new SM3Digest();
            case SHA1:
                return new SHA1Digest();
            case MD5:
                return new MD5Digest();
            default:
                throw new GordianDataException(getInvalidText(pDigestSpec.toString()));
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
     * @param pSpec the digestSpec
     * @return the digest
     */
    private static Digest getSHA2Digest(final GordianDigestSpec pSpec) {
        GordianLength myLen = pSpec.getDigestLength();
        GordianLength myState = pSpec.getStateLength();
        switch (myLen) {
            case LEN_224:
                return myState == null
                                       ? new SHA224Digest()
                                       : new SHA512tDigest(myLen.getLength());
            case LEN_256:
                return myState == null
                                       ? new SHA256Digest()
                                       : new SHA512tDigest(myLen.getLength());
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
     * Create the BouncyCastle skeinDigest.
     * @param pStateLength the state length
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSkeinDigest(final GordianLength pStateLength,
                                         final GordianLength pLength) {
        return new SkeinDigest(pStateLength.getLength(), pLength.getLength());
    }

    /**
     * Create the BouncyCastle Streebog digest.
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getStreebogDigest(final GordianLength pLength) {
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
                return getBCHMac(pMacSpec.getDigestSpec());
            case GMAC:
                return getBCGMac(pMacSpec.getKeyType());
            case CMAC:
                return getBCCMac(pMacSpec.getKeyType());
            case POLY1305:
                return getBCPoly1305Mac(pMacSpec.getKeyType());
            case SKEIN:
                return getBCSkeinMac(pMacSpec.getDigestSpec());
            case VMPC:
                return getBCVMPCMac();
            default:
                throw new GordianDataException(getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle HMAC.
     * @param pDigestSpec the digestSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCHMac(final GordianDigestSpec pDigestSpec) throws OceanusException {
        BouncyDigest myDigest = createDigest(pDigestSpec);
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
     * Create the BouncyCastle CMac.
     * @param pSymKeyType the SymKeyType
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCCMac(final GordianSymKeyType pSymKeyType) throws OceanusException {
        return new CMac(getBCSymEngine(pSymKeyType));
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
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCSkeinMac(final GordianDigestSpec pSpec) {
        return new SkeinMac(pSpec.getStateLength().getLength(), pSpec.getDigestLength().getLength());
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
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static BufferedBlockCipher getBCBlockCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Build the cipher */
        BlockCipher myEngine = getBCSymEngine(pCipherSpec.getKeyType());
        BlockCipher myMode = getBCSymModeCipher(myEngine, pCipherSpec.getCipherMode());
        return getBCSymBufferedCipher(myMode, pCipherSpec.getPadding());
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
        if (!supportedKeySetSymKeyTypes().test(pKeyType)) {
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
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static AEADBlockCipher getBCAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        GordianSymKeyType myType = pCipherSpec.getKeyType();
        switch (pCipherSpec.getCipherMode()) {
            case EAX:
                return new EAXBlockCipher(getBCSymEngine(myType));
            case CCM:
                return new CCMBlockCipher(getBCSymEngine(myType));
            case GCM:
                return new GCMBlockCipher(getBCSymEngine(myType));
            case OCB:
                return new OCBBlockCipher(getBCSymEngine(myType), getBCSymEngine(myType));
            default:
                throw new GordianDataException(getInvalidText(pCipherSpec));
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
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private BouncyKeyPairGenerator getBCKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return new BouncyRSAKeyPairGenerator(this, pKeySpec);
            case EC:
                return new BouncyECKeyPairGenerator(this, pKeySpec);
            case ELGAMAL:
                return new BouncyElGamalKeyPairGenerator(this, pKeySpec);
            case DIFFIEHELLMAN:
                return new BouncyDiffieHellmanKeyPairGenerator(this, pKeySpec);
            case SPHINCS:
                return new BouncySPHINCSKeyPairGenerator(this, pKeySpec);
            case RAINBOW:
                return new BouncyRainbowKeyPairGenerator(this, pKeySpec);
            case MCELIECE:
                return new BouncyMcElieceKeyPairGenerator(this, pKeySpec);
            case NEWHOPE:
                return new BouncyNewHopeKeyPairGenerator(this, pKeySpec);
            default:
                throw new GordianDataException(getInvalidText(pKeySpec.getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle Signer.
     * @param pKeyPair the privateKey
     * @param pSignatureSpec the signatureSpec
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianSigner getBCSigner(final BouncyKeyPair pKeyPair,
                                      final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getAsymKeyType()) {
            case RSA:
                return new BouncyRSASigner(this, (BouncyRSAPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            case EC:
                return new BouncyECSigner(this, (BouncyECPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            case SPHINCS:
                return new BouncySPHINCSSigner(this, (BouncySPHINCSPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            case RAINBOW:
                return new BouncyRainbowSigner(this, (BouncyRainbowPrivateKey) pKeyPair.getPrivateKey(), pSignatureSpec, getRandom());
            default:
                throw new GordianDataException(getInvalidText(pKeyPair.getKeySpec().getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle Validator.
     * @param pKeyPair the keyPair
     * @param pSignatureSpec the signatureSpec
     * @return the Validator
     * @throws OceanusException on error
     */
    private GordianValidator getBCValidator(final BouncyKeyPair pKeyPair,
                                            final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getAsymKeyType()) {
            case RSA:
                return new BouncyRSAValidator(this, (BouncyRSAPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            case EC:
                return new BouncyECValidator(this, (BouncyECPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            case SPHINCS:
                return new BouncySPHINCSValidator(this, (BouncySPHINCSPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            case RAINBOW:
                return new BouncyRainbowValidator(this, (BouncyRainbowPublicKey) pKeyPair.getPublicKey(), pSignatureSpec);
            default:
                throw new GordianDataException(getInvalidText(pKeyPair.getKeySpec().getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle KEM Sender.
     * @param pKeyPair the keyPair
     * @param pDigestSpec the digestSpec
     * @return the KEMSender
     * @throws OceanusException on error
     */
    private GordianKEMSender getBCKEMSender(final BouncyKeyPair pKeyPair,
                                            final GordianDigestSpec pDigestSpec) throws OceanusException {
        switch (pKeyPair.getKeySpec().getKeyType()) {
            case RSA:
                return new BouncyRSAKEMSender(this, (BouncyRSAPublicKey) pKeyPair.getPublicKey(), pDigestSpec);
            case EC:
                return new BouncyECIESSender(this, (BouncyECPublicKey) pKeyPair.getPublicKey(), pDigestSpec);
            case DIFFIEHELLMAN:
                return new BouncyDiffieHellmanSender(this, (BouncyDiffieHellmanPublicKey) pKeyPair.getPublicKey(), pDigestSpec);
            case NEWHOPE:
                return new BouncyNewHopeSender(this, (BouncyNewHopePublicKey) pKeyPair.getPublicKey(), pDigestSpec);
            default:
                throw new GordianDataException(getInvalidText(pKeyPair.getKeySpec().getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle KEM Receiver.
     * @param pKeyPair the keyPair
     * @param pDigestSpec the digestSpec
     * @param pCipherText the cipherText
     * @return the KEMParser
     * @throws OceanusException on error
     */
    private GordianKeyEncapsulation getBCKEMParser(final BouncyKeyPair pKeyPair,
                                                   final GordianDigestSpec pDigestSpec,
                                                   final byte[] pCipherText) throws OceanusException {
        switch (pKeyPair.getKeySpec().getKeyType()) {
            case RSA:
                return new BouncyRSAKEMReceiver(this, (BouncyRSAPrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            case EC:
                return new BouncyECIESReceiver(this, (BouncyECPrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            case DIFFIEHELLMAN:
                return new BouncyDiffieHellmanReceiver(this, (BouncyDiffieHellmanPrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            case NEWHOPE:
                return new BouncyNewHopeReceiver(this, (BouncyNewHopePrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            default:
                throw new GordianDataException(getInvalidText(pKeyPair.getKeySpec().getKeyType()));
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
