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
package io.github.tonywasher.joceanus.gordianknot.impl.bc;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianWrapper;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.digests.GordianBlake2bDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.digests.GordianBlake2sDigest;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianAnubisEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianBlake2XEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianBlake3Engine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianLeaEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianMARSEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianRabbitEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSimonEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSkeinXofEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSnow3GEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSosemanukEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSpeckEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianXChaCha20Engine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianZuc128Engine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianZuc256Engine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.modes.GordianChaChaPoly1305;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.modes.GordianGCMSIVBlockCipher;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.engines.ARIAEngine;
import org.bouncycastle.crypto.engines.AsconAEAD128;
import org.bouncycastle.crypto.engines.BlowfishEngine;
import org.bouncycastle.crypto.engines.CAST5Engine;
import org.bouncycastle.crypto.engines.CAST6Engine;
import org.bouncycastle.crypto.engines.CamelliaEngine;
import org.bouncycastle.crypto.engines.ChaCha7539Engine;
import org.bouncycastle.crypto.engines.ChaChaEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.engines.ElephantEngine;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.engines.GOST3412_2015Engine;
import org.bouncycastle.crypto.engines.Grain128Engine;
import org.bouncycastle.crypto.engines.HC128Engine;
import org.bouncycastle.crypto.engines.HC256Engine;
import org.bouncycastle.crypto.engines.IDEAEngine;
import org.bouncycastle.crypto.engines.ISAACEngine;
import org.bouncycastle.crypto.engines.ISAPEngine;
import org.bouncycastle.crypto.engines.NoekeonEngine;
import org.bouncycastle.crypto.engines.PhotonBeetleEngine;
import org.bouncycastle.crypto.engines.PhotonBeetleEngine.PhotonBeetleParameters;
import org.bouncycastle.crypto.engines.RC2Engine;
import org.bouncycastle.crypto.engines.RC4Engine;
import org.bouncycastle.crypto.engines.RC532Engine;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.engines.RC6Engine;
import org.bouncycastle.crypto.engines.RomulusEngine;
import org.bouncycastle.crypto.engines.SEEDEngine;
import org.bouncycastle.crypto.engines.SM4Engine;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.engines.SerpentEngine;
import org.bouncycastle.crypto.engines.Shacal2Engine;
import org.bouncycastle.crypto.engines.SkipjackEngine;
import org.bouncycastle.crypto.engines.SparkleEngine;
import org.bouncycastle.crypto.engines.TEAEngine;
import org.bouncycastle.crypto.engines.ThreefishEngine;
import org.bouncycastle.crypto.engines.TwofishEngine;
import org.bouncycastle.crypto.engines.VMPCEngine;
import org.bouncycastle.crypto.engines.VMPCKSA3Engine;
import org.bouncycastle.crypto.engines.XSalsa20Engine;
import org.bouncycastle.crypto.engines.XTEAEngine;
import org.bouncycastle.crypto.engines.XoodyakEngine;
import org.bouncycastle.crypto.generators.DESedeKeyGenerator;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.AEADCipher;
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
import org.bouncycastle.crypto.paddings.ISO7816d4Padding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.paddings.TBCPadding;
import org.bouncycastle.crypto.paddings.X923Padding;
import org.bouncycastle.crypto.patch.modes.GordianKCCMBlockCipher;
import org.bouncycastle.crypto.patch.modes.GordianKGCMBlockCipher;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for BouncyCastle Ciphers.
 */
public class BouncyCipherFactory
        extends GordianCoreCipherFactory {
    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeySpec, BouncyKeyGenerator<? extends GordianKeySpec>> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyCipherFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> BouncyKeyGenerator<T> getKeyGenerator(final T pKeySpec) throws GordianException {
        /* Look up in the cache */
        BouncyKeyGenerator<T> myGenerator = (BouncyKeyGenerator<T>) theCache.get(pKeySpec);
        if (myGenerator == null) {
            /* Create the new generator */
            final CipherKeyGenerator myBCGenerator = getBCKeyGenerator(pKeySpec);
            myGenerator = new BouncyKeyGenerator<>(getFactory(), pKeySpec, myBCGenerator);

            /* Add to cache */
            theCache.put(pKeySpec, myGenerator);
        }
        return myGenerator;
    }

    @Override
    public GordianSymCipher createSymKeyCipher(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        /* Check validity of SymKeySpec */
        checkSymCipherSpec(pCipherSpec);
        final GordianCoreSymCipherSpec mySpec = (GordianCoreSymCipherSpec) pCipherSpec;

        /* If this is an AAD cipher */
        if (mySpec.isAAD()) {
            /* Create the AAD cipher */
            final AEADBlockCipher myBCCipher = getBCAADCipher(mySpec);
            return new BouncySymKeyAEADCipher(getFactory(), mySpec, myBCCipher);

            /* else create the standard cipher */
        } else {
            /* Create the cipher */
            final BufferedBlockCipher myBCCipher = getBCBlockCipher(mySpec);
            return new BouncySymKeyCipher(getFactory(), mySpec, myBCCipher);
        }
    }

    @Override
    public GordianStreamCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        /* Check validity of StreamKeySpec */
        checkStreamCipherSpec(pCipherSpec);
        final GordianCoreStreamCipherSpec mySpec = (GordianCoreStreamCipherSpec) pCipherSpec;

        /* Create the cipher */
        return pCipherSpec.isAEAD()
                ? new BouncyStreamKeyAEADCipher(getFactory(), mySpec, getBCAEADStreamCipher(mySpec))
                : new BouncyStreamKeyCipher(getFactory(), mySpec, getBCStreamCipher(mySpec));
    }

    @Override
    public GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        /* Create the cipher */
        final BouncyKey<GordianSymKeySpec> myKey = BouncyKey.accessKey(pKey);
        final GordianSymCipherSpecBuilder myBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        final GordianSymCipherSpec mySpec = myBuilder.ecb(myKey.getKeyType(), GordianPadding.NONE);
        final BouncySymKeyCipher myBCCipher = (BouncySymKeyCipher) createSymKeyCipher(mySpec);
        return createKeyWrapper(myKey, myBCCipher);
    }

    /**
     * Create the BouncyCastle KeyGenerator.
     *
     * @param pKeySpec the keySpec
     * @return the KeyGenerator
     * @throws GordianException on error
     */
    private CipherKeyGenerator getBCKeyGenerator(final GordianKeySpec pKeySpec) throws GordianException {
        checkKeySpec(pKeySpec);
        return pKeySpec instanceof GordianSymKeySpec mySpec
                && GordianSymKeyType.DESEDE.equals(mySpec.getSymKeyType())
                ? new DESedeKeyGenerator()
                : new CipherKeyGenerator();
    }

    /**
     * Create the BouncyCastle Block Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static BufferedBlockCipher getBCBlockCipher(final GordianCoreSymCipherSpec pCipherSpec) throws GordianException {
        /* Build the cipher */
        final BlockCipher myEngine = getBCSymEngine(pCipherSpec.getCoreKeySpec());
        final BlockCipher myMode = getBCSymModeCipher(myEngine, pCipherSpec.getCipherMode());
        return getBCSymBufferedCipher(myMode, pCipherSpec.getPadding());
    }

    /**
     * Create the BouncyCastle Stream Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static StreamCipher getBCStreamCipher(final GordianCoreStreamCipherSpec pCipherSpec) throws GordianException {
        final GordianCoreStreamKeySpec mySpec = pCipherSpec.getCoreKeySpec();
        return switch (mySpec.getStreamKeyType()) {
            case HC -> GordianLength.LEN_128 == mySpec.getKeyLength()
                    ? new HC128Engine()
                    : new HC256Engine();
            case CHACHA20 -> switch ((GordianChaCha20Key) mySpec.getSubKeyType()) {
                case XCHACHA -> new GordianXChaCha20Engine();
                case ISO7539 -> new ChaCha7539Engine();
                default -> new ChaChaEngine();
            };
            case SALSA20 -> mySpec.getSubKeyType() == GordianSalsa20Key.STD
                    ? new Salsa20Engine()
                    : new XSalsa20Engine();
            case VMPC -> mySpec.getSubKeyType() == GordianVMPCKey.STD
                    ? new VMPCEngine()
                    : new VMPCKSA3Engine();
            case GRAIN -> new Grain128Engine();
            case ISAAC -> new ISAACEngine();
            case RC4 -> new RC4Engine();
            case SOSEMANUK -> new GordianSosemanukEngine();
            case RABBIT -> new GordianRabbitEngine();
            case SNOW3G -> new GordianSnow3GEngine();
            case ZUC -> GordianLength.LEN_128 == mySpec.getKeyLength()
                    ? new GordianZuc128Engine()
                    : new GordianZuc256Engine();
            case SKEINXOF -> {
                final GordianSkeinXofKey mySkeinKeyType = (GordianSkeinXofKey) mySpec.getSubKeyType();
                yield new GordianSkeinXofEngine(GordianCoreStreamKeySubType.getLengthForSkeinXofKey(mySkeinKeyType).getLength());
            }
            case BLAKE2XOF -> {
                final GordianBlakeXofKey myBlakeKeyType = (GordianBlakeXofKey) mySpec.getSubKeyType();
                yield new GordianBlake2XEngine(GordianBlakeXofKey.BLAKE2XB == myBlakeKeyType
                        ? new GordianBlake2bDigest()
                        : new GordianBlake2sDigest());
            }
            case BLAKE3XOF -> new GordianBlake3Engine();
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        };
    }

    /**
     * Create the BouncyCastle AEAD Stream Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static AEADCipher getBCAEADStreamCipher(final GordianCoreStreamCipherSpec pCipherSpec) throws GordianException {
        final GordianCoreStreamKeySpec mySpec = pCipherSpec.getCoreKeySpec();
        return switch (mySpec.getStreamKeyType()) {
            case CHACHA20 -> switch ((GordianChaCha20Key) mySpec.getSubKeyType()) {
                case XCHACHA -> new GordianChaChaPoly1305(new GordianXChaCha20Engine());
                default -> new GordianChaChaPoly1305(new ChaCha7539Engine());
            };
            case ASCON -> new AsconAEAD128();
            case ELEPHANT ->
                    new ElephantEngine(GordianCoreStreamKeySubType.getParameters((GordianElephantKey) mySpec.getSubKeyType()));
            case ISAP ->
                    new ISAPEngine(GordianCoreStreamKeySubType.getISAPType((GordianISAPKey) mySpec.getSubKeyType()));
            case PHOTONBEETLE -> new PhotonBeetleEngine(PhotonBeetleParameters.pb128);
            case ROMULUS ->
                    new RomulusEngine(GordianCoreStreamKeySubType.getParameters((GordianRomulusKey) mySpec.getSubKeyType()));
            case SPARKLE ->
                    new SparkleEngine(GordianCoreStreamKeySubType.getParameters((GordianSparkleKey) mySpec.getSubKeyType()));
            case XOODYAK -> new XoodyakEngine();
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        };
    }

    /**
     * Create the BouncyCastle Cipher Engine.
     *
     * @param pKeySpec the SymKeySpec
     * @return the Engine
     * @throws GordianException on error
     */
    static BlockCipher getBCSymEngine(final GordianCoreSymKeySpec pKeySpec) throws GordianException {
        return switch (pKeySpec.getSymKeyType()) {
            case AES -> AESEngine.newInstance();
            case SERPENT -> new SerpentEngine();
            case TWOFISH -> new TwofishEngine();
            case CAMELLIA -> new CamelliaEngine();
            case RC6 -> new RC6Engine();
            case CAST6 -> new CAST6Engine();
            case ARIA -> new ARIAEngine();
            case THREEFISH -> new ThreefishEngine(pKeySpec.getBlockLength().getLength());
            case KALYNA -> new DSTU7624Engine(pKeySpec.getBlockLength().getLength());
            case SM4 -> new SM4Engine();
            case NOEKEON -> new NoekeonEngine();
            case SEED -> new SEEDEngine();
            case BLOWFISH -> new BlowfishEngine();
            case SKIPJACK -> new SkipjackEngine();
            case IDEA -> new IDEAEngine();
            case TEA -> new TEAEngine();
            case XTEA -> new XTEAEngine();
            case RC2 -> new RC2Engine();
            case RC5 -> GordianLength.LEN_128.equals(pKeySpec.getBlockLength())
                    ? new RC564Engine()
                    : new RC532Engine();
            case CAST5 -> new CAST5Engine();
            case DESEDE -> new DESedeEngine();
            case GOST -> new GOST28147Engine();
            case KUZNYECHIK -> new GOST3412_2015Engine();
            case SHACAL2 -> new Shacal2Engine();
            case SPECK -> new GordianSpeckEngine();
            case ANUBIS -> new GordianAnubisEngine();
            case SIMON -> new GordianSimonEngine();
            case MARS -> new GordianMARSEngine();
            case LEA -> new GordianLeaEngine();
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        };
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     *
     * @param pEngine the underlying engine
     * @param pMode   the cipher mode
     * @return the Cipher
     * @throws GordianException on error
     */
    private static BlockCipher getBCSymModeCipher(final BlockCipher pEngine,
                                                  final GordianCipherMode pMode) throws GordianException {
        return switch (pMode) {
            case ECB -> pEngine;
            case CBC -> CBCBlockCipher.newInstance(pEngine);
            case SIC -> SICBlockCipher.newInstance(pEngine);
            case KCTR -> new KCTRBlockCipher(pEngine);
            case CFB -> CFBBlockCipher.newInstance(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case CFB8 -> CFBBlockCipher.newInstance(pEngine, Byte.SIZE);
            case GCFB -> new GCFBBlockCipher(pEngine);
            case OFB -> new OFBBlockCipher(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case OFB8 -> new OFBBlockCipher(pEngine, Byte.SIZE);
            case GOFB -> new GOFBBlockCipher(pEngine);
            case G3413CBC -> new G3413CBCBlockCipher(pEngine);
            case G3413CTR -> new G3413CTRBlockCipher(pEngine);
            case G3413CFB -> new G3413CFBBlockCipher(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case G3413OFB -> new G3413OFBBlockCipher(pEngine);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pMode));
        };
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static AEADBlockCipher getBCAADCipher(final GordianCoreSymCipherSpec pCipherSpec) throws GordianException {
        final GordianCoreSymKeySpec mySpec = pCipherSpec.getCoreKeySpec();
        return switch (pCipherSpec.getCipherMode()) {
            case EAX -> new EAXBlockCipher(getBCSymEngine(mySpec));
            case CCM -> CCMBlockCipher.newInstance(getBCSymEngine(mySpec));
            case KCCM -> new GordianKCCMBlockCipher(getBCSymEngine(mySpec));
            case GCM -> GCMBlockCipher.newInstance(getBCSymEngine(mySpec));
            case GCMSIV -> new GordianGCMSIVBlockCipher(getBCSymEngine(mySpec));
            case KGCM -> new GordianKGCMBlockCipher(getBCSymEngine(mySpec));
            case OCB -> new OCBBlockCipher(getBCSymEngine(mySpec), getBCSymEngine(mySpec));
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pCipherSpec));
        };
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
        return switch (pPadding) {
            case CTS -> new CTSBlockCipher(pEngine);
            case X923 -> new PaddedBufferedBlockCipher(pEngine, new X923Padding());
            case PKCS7 -> new PaddedBufferedBlockCipher(pEngine, new PKCS7Padding());
            case ISO7816D4 -> new PaddedBufferedBlockCipher(pEngine, new ISO7816d4Padding());
            case TBC -> new PaddedBufferedBlockCipher(pEngine, new TBCPadding());
            default -> new DefaultBufferedBlockCipher(pEngine);
        };
    }
}
