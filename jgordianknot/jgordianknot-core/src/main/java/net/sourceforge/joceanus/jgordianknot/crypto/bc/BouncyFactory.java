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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
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
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianWrapCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDSAAsymKey.BouncyDSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDSAAsymKey.BouncyDSASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDSTUAsymKey.BouncyDSTUKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDSTUAsymKey.BouncyDSTUSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDiffieHellmanAsymKey.BouncyDiffieHellmanKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDiffieHellmanAsymKey.BouncyDiffieHellmanPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDiffieHellmanAsymKey.BouncyDiffieHellmanPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDiffieHellmanAsymKey.BouncyDiffieHellmanReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyDiffieHellmanAsymKey.BouncyDiffieHellmanSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEdwardsDSAAsymKey.BouncyEd25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEdwardsDSAAsymKey.BouncyEd448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEdwardsDSAAsymKey.BouncyEdDSASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEdwardsXDHAsymKey.BouncyX25519KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEdwardsXDHAsymKey.BouncyX448KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncyECIESReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncyECIESSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncyECKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncyECPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncyECPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncyECSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyEllipticAsymKey.BouncySM2Signature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyGOSTAsymKey.BouncyGOSTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyGOSTAsymKey.BouncyGOSTSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyMcElieceAsymKey.BouncyMcElieceCCA2KeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyMcElieceAsymKey.BouncyMcElieceKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyNewHopeAsymKey.BouncyNewHopeKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyNewHopeAsymKey.BouncyNewHopePrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyNewHopeAsymKey.BouncyNewHopePublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyNewHopeAsymKey.BouncyNewHopeReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyNewHopeAsymKey.BouncyNewHopeSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyQTESLAAsymKey.BouncyQTESLAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyQTESLAAsymKey.BouncyQTESLASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRSAAsymKey.BouncyRSAKEMReceiver;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRSAAsymKey.BouncyRSAKEMSender;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRSAAsymKey.BouncyRSAKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRSAAsymKey.BouncyRSAPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRSAAsymKey.BouncyRSAPublicKey;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRSAAsymKey.BouncyRSASignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRainbowAsymKey.BouncyRainbowKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyRainbowAsymKey.BouncyRainbowSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySPHINCSAsymKey.BouncySPHINCSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncySPHINCSAsymKey.BouncySPHINCSSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyXMSSAsymKey.BouncyXMSSKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyXMSSAsymKey.BouncyXMSSMTKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyXMSSAsymKey.BouncyXMSSMTSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyXMSSAsymKey.BouncyXMSSSignature;
import net.sourceforge.joceanus.jgordianknot.crypto.prng.GordianBaseSecureRandom;
import net.sourceforge.joceanus.jgordianknot.crypto.prng.GordianRandomFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.digests.Blake2bDigest;
import org.bouncycastle.crypto.digests.Blake2sDigest;
import org.bouncycastle.crypto.digests.DSTU7564Digest;
import org.bouncycastle.crypto.digests.GOST3411Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_256Digest;
import org.bouncycastle.crypto.digests.GOST3411_2012_512Digest;
import org.bouncycastle.crypto.digests.MD2Digest;
import org.bouncycastle.crypto.digests.MD4Digest;
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
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.digests.SM3Digest;
import org.bouncycastle.crypto.digests.SkeinDigest;
import org.bouncycastle.crypto.digests.TigerDigest;
import org.bouncycastle.crypto.digests.WhirlpoolDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.HC128Engine;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.crypto.engines.ISAACEngine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.engines.RC532Engine;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.Shacal2Engine;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.engines.TEAEngine;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.crypto.engines.XSalsa20Engine;
import org.bouncycastle.crypto.engines.XTEAEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
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
import org.bouncycastle.crypto.modes.G3413CBCBlockCipher;
import org.bouncycastle.crypto.modes.G3413CFBBlockCipher;
import org.bouncycastle.crypto.modes.G3413CTRBlockCipher;
import org.bouncycastle.crypto.modes.G3413OFBBlockCipher;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.GOFBBlockCipher;
import org.bouncycastle.crypto.modes.KCTRBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.modes.OFBBlockCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.newdigests.GroestlDigest;
import org.bouncycastle.crypto.newdigests.JHDigest;
import org.bouncycastle.crypto.newengines.AnubisEngine;
import org.bouncycastle.crypto.newengines.MARSEngine;
import org.bouncycastle.crypto.newengines.SimonEngine;
import org.bouncycastle.crypto.newengines.SosemanukEngine;
import org.bouncycastle.crypto.newengines.SpeckEngine;
import org.bouncycastle.crypto.newmacs.Blake2Mac;
import org.bouncycastle.crypto.newmacs.DSTUX7564Mac;
import org.bouncycastle.crypto.newmacs.DSTUX7624Mac;
import org.bouncycastle.crypto.newmacs.KXGMac;
import org.bouncycastle.crypto.newmodes.KCCMXBlockCipher;
import org.bouncycastle.crypto.newmodes.KGCMXBlockCipher;
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.X923Padding;

import java.util.function.Predicate;

/**
 * Factory for BouncyCastle Classes.
 */
public final class BouncyFactory
        extends GordianFactory {
    /**
     * Cache for KeyGenerators.
     */
    private final BouncyKeyGeneratorCache theGeneratorCache;

    /**
     * SP800 Factory.
     */
    private final GordianRandomFactory theSP800Factory;

    /**
     * Constructor.
     *
     * @param pParameters the parameters
     * @param pGenerator the factoryGenerator
     * @throws OceanusException on error
     */
    public BouncyFactory(final GordianParameters pParameters,
                         final GordianFactoryGenerator pGenerator) throws OceanusException {
        /* Initialise underlying class */
        super(pParameters, pGenerator);

        /* Create the keyGenerator cache */
        theGeneratorCache = new BouncyKeyGeneratorCache();

        /* Create the SP800 Factory */
        theSP800Factory = new GordianRandomFactory();
        setSecureRandom(theSP800Factory.getInitialRandom());

        /* Create the SecureRandom instance */
        final GordianBaseSecureRandom myRandom = createRandom(theSP800Factory.generateRandomSpec(this));
        setSecureRandom(myRandom);
        theGeneratorCache.resetCache();
    }

    @Override
    public <X> String getKeyAlgorithm(final X pKeyType) {
        return pKeyType.toString();
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
    public BouncyDigest createDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Check validity of DigestSpec */
        if (!supportedDigestSpecs().test(pDigestSpec)) {
            throw new GordianDataException(getInvalidText(pDigestSpec));
        }

        /* Create digest */
        final Digest myBCDigest = getBCDigest(pDigestSpec);
        return new BouncyDigest(pDigestSpec, myBCDigest);
    }

    @Override
    public BouncyMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Check validity of MacSpec */
        if (!supportedMacSpecs().test(pMacSpec)) {
            throw new GordianDataException(getInvalidText(pMacSpec));
        }

        /* Create Mac */
        final Mac myBCMac = getBCMac(pMacSpec);
        return new BouncyMac(this, pMacSpec, myBCMac);
    }

    @Override
    public <T> BouncyKeyGenerator<T> getKeyGenerator(final T pKeyType) throws OceanusException {
        /* Look up in the cache */
        BouncyKeyGenerator<T> myGenerator = theGeneratorCache.getCachedKeyGenerator(pKeyType);
        if (myGenerator == null) {
            /* Create the new generator */
            final CipherKeyGenerator myBCGenerator = getBCKeyGenerator(pKeyType);
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
        final GordianSymKeySpec myKeySpec = pCipherSpec.getKeyType();
        if (!supportedSymKeySpecs().test(myKeySpec)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Check validity of Mode */
        if (!validSymCipherSpec(pCipherSpec, false)) {
            throw new GordianDataException(getInvalidText(pCipherSpec));
        }

        /* Create the cipher */
        final BufferedBlockCipher myBCCipher = getBCBlockCipher(pCipherSpec);
        return new BouncySymKeyCipher(this, pCipherSpec, myBCCipher);
    }

    @Override
    public BouncyAADCipher createAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
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
        final AEADBlockCipher myBCCipher = getBCAADCipher(pCipherSpec);
        return new BouncyAADCipher(this, pCipherSpec, myBCCipher);
    }

    @Override
    public BouncyStreamKeyCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of StreamKey */
        final GordianStreamKeyType myKeyType = pCipherSpec.getKeyType();
        if (!supportedStreamKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(myKeyType));
        }

        /* Create the cipher */
        final StreamCipher myBCCipher = getBCStreamCipher(myKeyType);
        return new BouncyStreamKeyCipher(this, pCipherSpec, myBCCipher);
    }

    @Override
    public GordianWrapCipher createWrapCipher(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Check validity of SymKey */
        final GordianSymKeyType myKeyType = pKeySpec.getSymKeyType();
        if (!supportedSymKeyTypes().test(myKeyType)) {
            throw new GordianDataException(getInvalidText(pKeySpec));
        }

        /* Create the cipher */
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(pKeySpec, GordianPadding.NONE);
        final BouncySymKeyCipher myBCCipher = createSymKeyCipher(mySpec);
        return createWrapCipher(myBCCipher);
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
        return getBCSigner(pSignatureSpec);
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
     *
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
     * Create the BouncyCastle digest.
     *
     * @param pDigestSpec the digestSpec
     * @return the digest
     * @throws OceanusException on error
     */
    protected static Digest getBCDigest(final GordianDigestSpec pDigestSpec) throws OceanusException {
        /* Access digest details */
        final GordianDigestType myType = pDigestSpec.getDigestType();
        final GordianLength myLen = pDigestSpec.getDigestLength();

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
            case SHAKE:
                return new SHAKEDigest(pDigestSpec.getStateLength().getLength());
            case BLAKE:
                return getBlake2Digest(pDigestSpec);
            case STREEBOG:
                return getStreebogDigest(myLen);
            case KUPYNA:
                return getKupynaDigest(myLen);
            case GROESTL:
                return new GroestlDigest(myLen.getLength());
            case JH:
                return new JHDigest(myLen.getLength());
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
            case MD4:
                return new MD4Digest();
            case MD2:
                return new MD2Digest();
            default:
                throw new GordianDataException(getInvalidText(pDigestSpec.toString()));
        }
    }

    /**
     * Create the BouncyCastle RIPEMD digest.
     *
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
     * Create the BouncyCastle Blake2 digest.
     *
     * @param pSpec the digest spec
     * @return the digest
     */
    private static Digest getBlake2Digest(final GordianDigestSpec pSpec) {
        final int myLength = pSpec.getDigestLength().getLength();
        return GordianDigestType.isBlake2bState(pSpec.getStateLength())
               ? new Blake2bDigest(myLength)
               : new Blake2sDigest(myLength);
    }

    /**
     * Create the BouncyCastle SHA2 digest.
     *
     * @param pSpec the digestSpec
     * @return the digest
     */
    private static Digest getSHA2Digest(final GordianDigestSpec pSpec) {
        final GordianLength myLen = pSpec.getDigestLength();
        final GordianLength myState = pSpec.getStateLength();
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
     *
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getSHA3Digest(final GordianLength pLength) {
        return new SHA3Digest(pLength.getLength());
    }

    /**
     * Create the BouncyCastle Kupyna digest.
     *
     * @param pLength the digest length
     * @return the digest
     */
    private static Digest getKupynaDigest(final GordianLength pLength) {
        return new DSTU7564Digest(pLength.getLength());
    }

    /**
     * Create the BouncyCastle skeinDigest.
     *
     * @param pStateLength the state length
     * @param pLength      the digest length
     * @return the digest
     */
    private static Digest getSkeinDigest(final GordianLength pStateLength,
                                         final GordianLength pLength) {
        return new SkeinDigest(pStateLength.getLength(), pLength.getLength());
    }

    /**
     * Create the BouncyCastle Streebog digest.
     *
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
     *
     * @param pMacSpec the MacSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCMac(final GordianMacSpec pMacSpec) throws OceanusException {
        switch (pMacSpec.getMacType()) {
            case HMAC:
                return getBCHMac(pMacSpec.getDigestSpec());
            case GMAC:
                return getBCGMac(pMacSpec.getKeySpec());
            case CMAC:
                return getBCCMac(pMacSpec.getKeySpec());
            case POLY1305:
                return getBCPoly1305Mac(pMacSpec.getKeySpec());
            case SKEIN:
                return getBCSkeinMac(pMacSpec.getDigestSpec());
            case BLAKE:
                return getBCBlakeMac(pMacSpec);
            case KALYNA:
                return getBCKalynaMac(pMacSpec.getKeySpec());
            case KUPYNA:
                return getBCKupynaMac(pMacSpec.getDigestSpec());
            case VMPC:
                return getBCVMPCMac();
            default:
                throw new GordianDataException(getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle HMAC.
     *
     * @param pDigestSpec the digestSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCHMac(final GordianDigestSpec pDigestSpec) throws OceanusException {
        final BouncyDigest myDigest = createDigest(pDigestSpec);
        return new HMac(myDigest.getDigest());
    }

    /**
     * Create the BouncyCastle GMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getBCGMac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return GordianSymKeyType.KALYNA.equals(pSymKeySpec.getSymKeyType())
               ? new KXGMac(new KGCMXBlockCipher(getBCSymEngine(pSymKeySpec)))
               : new GMac(new GCMBlockCipher(getBCSymEngine(pSymKeySpec)));
    }

    /**
     * Create the BouncyCastle CMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getBCCMac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return new CMac(getBCSymEngine(pSymKeySpec));
    }

    /**
     * Create the BouncyCastle Poly1305Mac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getBCPoly1305Mac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return new Poly1305(getBCSymEngine(pSymKeySpec));
    }

    /**
     * Create the BouncyCastle SkeinMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCSkeinMac(final GordianDigestSpec pSpec) {
        return new SkeinMac(pSpec.getStateLength().getLength(), pSpec.getDigestLength().getLength());
    }

    /**
     * Create the BouncyCastle KalynaMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     */
    private static Mac getBCKalynaMac(final GordianSymKeySpec pSymKeySpec) {
        final GordianLength myLen = pSymKeySpec.getBlockLength();
        return new DSTUX7624Mac(myLen.getLength(), myLen.getLength());
    }

    /**
     * Create the BouncyCastle kupynaMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCKupynaMac(final GordianDigestSpec pSpec) {
        return new DSTUX7564Mac(pSpec.getDigestLength().getLength());
    }

    /**
     * Create the BouncyCastle blakeMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCBlakeMac(final GordianMacSpec pSpec) {
        final Digest myDigest = getBlake2Digest(pSpec.getDigestSpec());
        return new Blake2Mac(myDigest);
    }

    /**
     * Create the BouncyCastle VMPCMac.
     *
     * @return the MAC
     */
    private static Mac getBCVMPCMac() {
        return new VMPCMac();
    }

    /**
     * Create the BouncyCastle Block Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static BufferedBlockCipher getBCBlockCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Build the cipher */
        final BlockCipher myEngine = getBCSymEngine(pCipherSpec.getKeyType());
        final BlockCipher myMode = getBCSymModeCipher(myEngine, pCipherSpec.getCipherMode());
        return getBCSymBufferedCipher(myMode, pCipherSpec.getPadding());
    }

    /**
     * Create the BouncyCastle Stream Cipher.
     *
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
            case SOSEMANUK:
                return new SosemanukEngine();
            default:
                throw new GordianDataException(getInvalidText(pKeyType));
        }
    }

    /**
     * Create the BouncyCastle Cipher Engine.
     *
     * @param pKeySpec the SymKeySpec
     * @return the Engine
     * @throws OceanusException on error
     */
    private static BlockCipher getBCSymEngine(final GordianSymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getSymKeyType()) {
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
            case ARIA:
                return new ARIAEngine();
            case THREEFISH:
                return new ThreefishEngine(pKeySpec.getBlockLength().getLength());
            case KALYNA:
                return new DSTU7624Engine(pKeySpec.getBlockLength().getLength());
            case SM4:
                return new SM4Engine();
            case NOEKEON:
                return new NoekeonEngine();
            case SEED:
                return new SEEDEngine();
            case BLOWFISH:
                return new BlowfishEngine();
            case SKIPJACK:
                return new SkipjackEngine();
            case IDEA:
                return new IDEAEngine();
            case TEA:
                return new TEAEngine();
            case XTEA:
                return new XTEAEngine();
            case RC2:
                return new RC2Engine();
            case RC5:
                return GordianLength.LEN_128.equals(pKeySpec.getBlockLength())
                       ? new RC564Engine()
                       : new RC532Engine();
            case CAST5:
                return new CAST5Engine();
            case DESEDE:
                return new DESedeEngine();
            case GOST:
                return new GOST28147Engine();
            case KUZNYECHIK:
                return new GOST3412_2015Engine();
            case SHACAL2:
                return new Shacal2Engine();
            case SPECK:
                return new SpeckEngine(pKeySpec.getBlockLength().getLength());
            case ANUBIS:
                return new AnubisEngine();
            case SIMON:
                return new SimonEngine(pKeySpec.getBlockLength().getLength());
            case MARS:
                return new MARSEngine();
            default:
                throw new GordianDataException(getInvalidText(pKeySpec));
        }
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     *
     * @param pEngine the underlying engine
     * @param pMode   the cipher mode
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
            case KCTR:
                return new KCTRBlockCipher(pEngine);
            case CFB:
                return new CFBBlockCipher(pEngine, Byte.SIZE);
            case GCFB:
                return new GCFBBlockCipher(pEngine);
            case OFB:
                return new OFBBlockCipher(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case GOFB:
                return new GOFBBlockCipher(pEngine);
            case G3413CBC:
                return new G3413CBCBlockCipher(pEngine);
            case G3413CTR:
                return new G3413CTRBlockCipher(pEngine);
            case G3413CFB:
                return new G3413CFBBlockCipher(pEngine, Byte.SIZE);
            case G3413OFB:
                return new G3413OFBBlockCipher(pEngine);
            default:
                throw new GordianDataException(getInvalidText(pMode));
        }
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static AEADBlockCipher getBCAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        final GordianSymKeySpec mySpec = pCipherSpec.getKeyType();
        switch (pCipherSpec.getCipherMode()) {
            case EAX:
                return new EAXBlockCipher(getBCSymEngine(mySpec));
            case CCM:
                return new CCMBlockCipher(getBCSymEngine(mySpec));
            case KCCM:
                return new KCCMXBlockCipher(getBCSymEngine(mySpec));
            case GCM:
                return new GCMBlockCipher(getBCSymEngine(mySpec));
            case KGCM:
                return new KGCMXBlockCipher(getBCSymEngine(mySpec));
            case OCB:
                return new OCBBlockCipher(getBCSymEngine(mySpec), getBCSymEngine(mySpec));
            default:
                throw new GordianDataException(getInvalidText(pCipherSpec));
        }
    }

    /**
     * Create the BouncyCastle Mode Cipher.
     *
     * @param pEngine  the underlying engine
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
            case TBC:
                return new PaddedBufferedBlockCipher(pEngine, new TBCPadding());
            case NONE:
            default:
                return new BufferedBlockCipher(pEngine);
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator.
     *
     * @param pKeyType the keyType
     * @return the KeyGenerator
     */
    private static CipherKeyGenerator getBCKeyGenerator(final Object pKeyType) {
        if (pKeyType instanceof GordianMacSpec
                && GordianMacType.POLY1305.equals(((GordianMacSpec) pKeyType).getMacType())) {
            return new Poly1305KeyGenerator();
        }
        return GordianSymKeyType.DESEDE.equals(pKeyType)
               ? new DESedeKeyGenerator()
               : new CipherKeyGenerator();
    }

    /**
     * Create the BouncyCastle KeyPairGenerator.
     *
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private BouncyKeyPairGenerator getBCKeyPairGenerator(final GordianAsymKeySpec pKeySpec) throws OceanusException {
        switch (pKeySpec.getKeyType()) {
            case RSA:
                return new BouncyRSAKeyPairGenerator(this, pKeySpec);
            case EC:
            case SM2:
                return new BouncyECKeyPairGenerator(this, pKeySpec);
            case DSTU4145:
                return new BouncyDSTUKeyPairGenerator(this, pKeySpec);
            case GOST2012:
                return new BouncyGOSTKeyPairGenerator(this, pKeySpec);
            case X25519:
                return new BouncyX25519KeyPairGenerator(this, pKeySpec);
            case X448:
                return new BouncyX448KeyPairGenerator(this, pKeySpec);
            case ED25519:
                return new BouncyEd25519KeyPairGenerator(this, pKeySpec);
            case ED448:
                return new BouncyEd448KeyPairGenerator(this, pKeySpec);
            case DSA:
                return new BouncyDSAKeyPairGenerator(this, pKeySpec);
            case DIFFIEHELLMAN:
                return new BouncyDiffieHellmanKeyPairGenerator(this, pKeySpec);
            case SPHINCS:
                return new BouncySPHINCSKeyPairGenerator(this, pKeySpec);
            case RAINBOW:
                return new BouncyRainbowKeyPairGenerator(this, pKeySpec);
            case MCELIECE:
                return pKeySpec.getMcElieceSpec().isCCA2()
                       ? new BouncyMcElieceCCA2KeyPairGenerator(this, pKeySpec)
                       : new BouncyMcElieceKeyPairGenerator(this, pKeySpec);
            case NEWHOPE:
                return new BouncyNewHopeKeyPairGenerator(this, pKeySpec);
            case XMSS:
                return new BouncyXMSSKeyPairGenerator(this, pKeySpec);
            case XMSSMT:
                return new BouncyXMSSMTKeyPairGenerator(this, pKeySpec);
            case QTESLA:
                return new BouncyQTESLAKeyPairGenerator(this, pKeySpec);
            default:
                throw new GordianDataException(getInvalidText(pKeySpec.getKeyType()));
        }
    }

    /**
     * Create the BouncyCastle Signer.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the Signer
     * @throws OceanusException on error
     */
    private GordianSignature getBCSigner(final GordianSignatureSpec pSignatureSpec) throws OceanusException {
        switch (pSignatureSpec.getAsymKeyType()) {
            case RSA:
                return new BouncyRSASignature(this, pSignatureSpec);
            case EC:
                return new BouncyECSignature(this, pSignatureSpec);
            case DSTU4145:
                return new BouncyDSTUSignature(this, pSignatureSpec);
            case GOST2012:
                return new BouncyGOSTSignature(this, pSignatureSpec);
            case SM2:
                return new BouncySM2Signature(this, pSignatureSpec);
            case ED25519:
            case ED448:
                return new BouncyEdDSASignature(this, pSignatureSpec);
            case DSA:
                return new BouncyDSASignature(this, pSignatureSpec);
            case SPHINCS:
                return new BouncySPHINCSSignature(this, pSignatureSpec);
            case RAINBOW:
                return new BouncyRainbowSignature(this, pSignatureSpec);
            case XMSS:
                return new BouncyXMSSSignature(this, pSignatureSpec);
            case XMSSMT:
                return new BouncyXMSSMTSignature(this, pSignatureSpec);
            case QTESLA:
                return new BouncyQTESLASignature(this, pSignatureSpec);
            default:
                throw new GordianDataException(getInvalidText(pSignatureSpec.getAsymKeyType()));
        }
    }

    /**
     * Create the BouncyCastle KEM Sender.
     *
     * @param pKeyPair    the keyPair
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
            case SM2:
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
     *
     * @param pKeyPair    the keyPair
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
            case SM2:
                return new BouncyECIESReceiver(this, (BouncyECPrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            case DIFFIEHELLMAN:
                return new BouncyDiffieHellmanReceiver(this, (BouncyDiffieHellmanPrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            case NEWHOPE:
                return new BouncyNewHopeReceiver(this, (BouncyNewHopePrivateKey) pKeyPair.getPrivateKey(), pDigestSpec, pCipherText);
            default:
                throw new GordianDataException(getInvalidText(pKeyPair.getKeySpec().getKeyType()));
        }
    }
}
