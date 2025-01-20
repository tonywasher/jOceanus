/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.bc;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianBlakeXofKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianChaCha20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianElephantKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianISAPKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSalsa20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSkeinXofKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSparkleKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianVMPCKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2bDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.digests.GordianBlake2sDigest;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianAnubisEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianBlake2XEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianBlake3Engine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianLeaEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianMARSEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianRabbitEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianSimonEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianSkeinXofEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianSnow3GEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianSosemanukEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianSpeckEngine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianXChaCha20Engine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianZuc128Engine;
import net.sourceforge.joceanus.gordianknot.impl.ext.engines.GordianZuc256Engine;
import net.sourceforge.joceanus.gordianknot.impl.ext.modes.GordianChaChaPoly1305;
import net.sourceforge.joceanus.gordianknot.impl.ext.modes.GordianGCMSIVBlockCipher;
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
    BouncyCipherFactory(final GordianCoreFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    public BouncyFactory getFactory() {
        return (BouncyFactory) super.getFactory();
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

        /* If this is an AAD cipher */
        if (pCipherSpec.isAAD()) {
            /* Create the AAD cipher */
            final AEADBlockCipher myBCCipher = getBCAADCipher(pCipherSpec);
            return new BouncySymKeyAEADCipher(getFactory(), pCipherSpec, myBCCipher);

            /* else create the standard cipher */
        } else {
            /* Create the cipher */
            final BufferedBlockCipher myBCCipher = getBCBlockCipher(pCipherSpec);
            return new BouncySymKeyCipher(getFactory(), pCipherSpec, myBCCipher);
        }
    }

    @Override
    public GordianStreamCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        /* Check validity of StreamKeySpec */
        checkStreamCipherSpec(pCipherSpec);

        /* Create the cipher */
        return pCipherSpec.isAEAD()
               ? new BouncyStreamKeyAEADCipher(getFactory(), pCipherSpec, getBCAEADStreamCipher(pCipherSpec))
               : new BouncyStreamKeyCipher(getFactory(), pCipherSpec, getBCStreamCipher(pCipherSpec));
    }

    @Override
    public GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        /* Create the cipher */
        final BouncyKey<GordianSymKeySpec> myKey = BouncyKey.accessKey(pKey);
        final GordianSymCipherSpec mySpec = GordianSymCipherSpecBuilder.ecb(myKey.getKeyType(), GordianPadding.NONE);
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
        return pKeySpec instanceof GordianSymKeySpec
                && GordianSymKeyType.DESEDE.equals(((GordianSymKeySpec) pKeySpec).getSymKeyType())
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
    private static BufferedBlockCipher getBCBlockCipher(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        /* Build the cipher */
        final BlockCipher myEngine = getBCSymEngine(pCipherSpec.getKeyType());
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
    private static StreamCipher getBCStreamCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        final GordianStreamKeySpec mySpec = pCipherSpec.getKeyType();
        switch (mySpec.getStreamKeyType()) {
            case HC:
                return GordianLength.LEN_128 == mySpec.getKeyLength()
                       ? new HC128Engine()
                       : new HC256Engine();
            case CHACHA20:
                switch ((GordianChaCha20Key) mySpec.getSubKeyType()) {
                    case XCHACHA:
                        return new GordianXChaCha20Engine();
                    case ISO7539:
                        return new ChaCha7539Engine();
                    default:
                        return new ChaChaEngine();
                }
            case SALSA20:
                return mySpec.getSubKeyType() == GordianSalsa20Key.STD
                        ? new Salsa20Engine()
                        : new XSalsa20Engine();
            case VMPC:
                return mySpec.getSubKeyType() == GordianVMPCKey.STD
                       ? new VMPCEngine()
                       : new VMPCKSA3Engine();
            case GRAIN:
                return new Grain128Engine();
            case ISAAC:
                return new ISAACEngine();
            case RC4:
                return new RC4Engine();
            case SOSEMANUK:
                return new GordianSosemanukEngine();
            case RABBIT:
                return new GordianRabbitEngine();
            case SNOW3G:
                return new GordianSnow3GEngine();
            case ZUC:
                return GordianLength.LEN_128 == mySpec.getKeyLength()
                       ? new GordianZuc128Engine()
                       : new GordianZuc256Engine();
            case SKEINXOF:
                final GordianSkeinXofKey mySkeinKeyType = (GordianSkeinXofKey) mySpec.getSubKeyType();
                return new GordianSkeinXofEngine(mySkeinKeyType.getLength().getLength());
            case BLAKE2XOF:
                final GordianBlakeXofKey myBlakeKeyType = (GordianBlakeXofKey) mySpec.getSubKeyType();
                return new GordianBlake2XEngine(GordianBlakeXofKey.BLAKE2XB == myBlakeKeyType
                        ? new GordianBlake2bDigest()
                        : new GordianBlake2sDigest());
            case BLAKE3XOF:
                return new GordianBlake3Engine();
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Create the BouncyCastle AEAD Stream Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static AEADCipher getBCAEADStreamCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        final GordianStreamKeySpec mySpec = pCipherSpec.getKeyType();
        switch (mySpec.getStreamKeyType()) {
            case CHACHA20:
                switch ((GordianChaCha20Key) mySpec.getSubKeyType()) {
                    case XCHACHA:
                        return new GordianChaChaPoly1305(new GordianXChaCha20Engine());
                    case ISO7539:
                    default:
                        return new GordianChaChaPoly1305(new ChaCha7539Engine());
                }
            case ASCON:
                return new AsconAEAD128();
            case ELEPHANT:
                return new ElephantEngine(((GordianElephantKey) mySpec.getSubKeyType()).getParameters());
            case ISAP:
                return new ISAPEngine(((GordianISAPKey) mySpec.getSubKeyType()).getType());
            case PHOTONBEETLE:
                return new PhotonBeetleEngine(PhotonBeetleParameters.pb128);
            case SPARKLE:
                return new SparkleEngine(((GordianSparkleKey) mySpec.getSubKeyType()).getParameters());
            case XOODYAK:
                return new XoodyakEngine();
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
        }
    }

    /**
     * Create the BouncyCastle Cipher Engine.
     *
     * @param pKeySpec the SymKeySpec
     * @return the Engine
     * @throws GordianException on error
     */
    static BlockCipher getBCSymEngine(final GordianSymKeySpec pKeySpec) throws GordianException {
        switch (pKeySpec.getSymKeyType()) {
            case AES:
                return AESEngine.newInstance();
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
                return new GordianSpeckEngine();
            case ANUBIS:
                return new GordianAnubisEngine();
            case SIMON:
                return new GordianSimonEngine();
            case MARS:
                return new GordianMARSEngine();
            case LEA:
                return new GordianLeaEngine();
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
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
        switch (pMode) {
            case ECB:
                return pEngine;
            case CBC:
                return CBCBlockCipher.newInstance(pEngine);
            case SIC:
                return SICBlockCipher.newInstance(pEngine);
            case KCTR:
                return new KCTRBlockCipher(pEngine);
            case CFB:
                return CFBBlockCipher.newInstance(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case CFB8:
                return CFBBlockCipher.newInstance(pEngine, Byte.SIZE);
            case GCFB:
                return new GCFBBlockCipher(pEngine);
            case OFB:
                return new OFBBlockCipher(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case OFB8:
                return new OFBBlockCipher(pEngine, Byte.SIZE);
            case GOFB:
                return new GOFBBlockCipher(pEngine);
            case G3413CBC:
                return new G3413CBCBlockCipher(pEngine);
            case G3413CTR:
                return new G3413CTRBlockCipher(pEngine);
            case G3413CFB:
                return new G3413CFBBlockCipher(pEngine, Byte.SIZE * pEngine.getBlockSize());
            case G3413OFB:
                return new G3413OFBBlockCipher(pEngine);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pMode));
        }
    }

    /**
     * Create the BouncyCastle Buffered Cipher.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static AEADBlockCipher getBCAADCipher(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        final GordianSymKeySpec mySpec = pCipherSpec.getKeyType();
        switch (pCipherSpec.getCipherMode()) {
            case EAX:
                return new EAXBlockCipher(getBCSymEngine(mySpec));
            case CCM:
                return CCMBlockCipher.newInstance(getBCSymEngine(mySpec));
            case KCCM:
                return new GordianKCCMBlockCipher(getBCSymEngine(mySpec));
            case GCM:
                return GCMBlockCipher.newInstance(getBCSymEngine(mySpec));
            case GCMSIV:
                return new GordianGCMSIVBlockCipher(getBCSymEngine(mySpec));
            case KGCM:
                return new GordianKGCMBlockCipher(getBCSymEngine(mySpec));
            case OCB:
                return new OCBBlockCipher(getBCSymEngine(mySpec), getBCSymEngine(mySpec));
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pCipherSpec));
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
                return new DefaultBufferedBlockCipher(pEngine);
        }
    }
}
