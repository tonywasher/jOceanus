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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.mac.GordianCoreMacFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.digests.GordianBlake3Digest;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianBlake2Mac;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianBlake2XMac;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianBlake3Mac;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianKMACWrapper;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianSkeinMac;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianSkeinXMac;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianZuc128Mac;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.macs.GordianZuc256Mac;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.macs.SipHash;
import org.bouncycastle.crypto.macs.SipHash128;
import org.bouncycastle.crypto.macs.VMPCMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.patch.macs.GordianDSTU7624Mac;
import org.bouncycastle.crypto.patch.macs.GordianKGMac;
import org.bouncycastle.crypto.patch.modes.GordianKGCMBlockCipher;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Factory for BouncyCastle Macs.
 */
public class BouncyMacFactory
        extends GordianCoreMacFactory {
    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianKeySpec, BouncyKeyGenerator<? extends GordianKeySpec>> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    BouncyMacFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> BouncyKeyGenerator<T> getKeyGenerator(final T pMacSpec) throws GordianException {
        /* Look up in the cache */
        BouncyKeyGenerator<T> myGenerator = (BouncyKeyGenerator<T>) theCache.get(pMacSpec);
        if (myGenerator == null) {
            /* Check validity of MacSpec */
            checkMacSpec(pMacSpec);

            /* Create the new generator */
            final CipherKeyGenerator myBCGenerator = getBCKeyGenerator((GordianMacSpec) pMacSpec);
            myGenerator = new BouncyKeyGenerator<>(getFactory(), pMacSpec, myBCGenerator);

            /* Add to cache */
            theCache.put(pMacSpec, myGenerator);
        }
        return myGenerator;
    }

    /**
     * Create the BouncyCastle KeyGenerator.
     *
     * @param pKeyType the keyType
     * @return the KeyGenerator
     */
    private static CipherKeyGenerator getBCKeyGenerator(final GordianMacSpec pKeyType) {
        return GordianMacType.POLY1305.equals(pKeyType.getMacType())
                ? new Poly1305KeyGenerator()
                : new CipherKeyGenerator();
    }

    @Override
    public BouncyMac createMac(final GordianMacSpec pMacSpec) throws GordianException {
        /* Check validity of MacSpec */
        checkMacSpec(pMacSpec);

        /* Create Mac */
        final Mac myBCMac = getBCMac((GordianCoreMacSpec) pMacSpec);
        return myBCMac instanceof Xof myXof
                ? new BouncyMacXof(getFactory(), pMacSpec, myXof)
                : new BouncyMac(getFactory(), pMacSpec, myBCMac);
    }

    /**
     * Create the BouncyCastle MAC.
     *
     * @param pMacSpec the MacSpec
     * @return the MAC
     * @throws GordianException on error
     */
    private Mac getBCMac(final GordianCoreMacSpec pMacSpec) throws GordianException {
        return switch (pMacSpec.getMacType()) {
            case HMAC -> getBCHMac(pMacSpec);
            case GMAC -> getBCGMac(Objects.requireNonNull(pMacSpec.getSymKeySpec()));
            case CMAC -> getBCCMac(Objects.requireNonNull(pMacSpec.getSymKeySpec()));
            case POLY1305 -> getBCPoly1305Mac(pMacSpec.getSymKeySpec());
            case SKEIN -> getBCSkeinMac(Objects.requireNonNull(pMacSpec.getDigestSpec()));
            case BLAKE2 -> getBCBlake2Mac(Objects.requireNonNull(pMacSpec.getDigestSpec()));
            case BLAKE3 -> getBCBlake3Mac(Objects.requireNonNull(pMacSpec.getDigestSpec()));
            case KMAC -> getBCKMAC(Objects.requireNonNull(pMacSpec.getDigestSpec()));
            case KALYNA -> getBCKalynaMac(Objects.requireNonNull(pMacSpec.getSymKeySpec()));
            case KUPYNA -> getBCKupynaMac(Objects.requireNonNull(pMacSpec.getDigestSpec()));
            case VMPC -> getBCVMPCMac();
            case CBCMAC -> getBCCBCMac(Objects.requireNonNull(pMacSpec.getSymKeySpec()));
            case CFBMAC -> getBCCFBMac(Objects.requireNonNull(pMacSpec.getSymKeySpec()));
            case SIPHASH -> getBCSipHash(Objects.requireNonNull(pMacSpec.getSipHashSpec()));
            case GOST -> getBCGOSTMac();
            case ZUC -> getBCZucMac(pMacSpec);
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pMacSpec));
        };
    }

    /**
     * Create the BouncyCastle HMAC.
     *
     * @param pMacSpec the macSpec
     * @return the MAC
     * @throws GordianException on error
     */
    private Mac getBCHMac(final GordianMacSpec pMacSpec) throws GordianException {
        return new BouncyHMac(getFactory().getDigestFactory(), pMacSpec);
    }

    /**
     * Create the BouncyCastle GMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws GordianException on error
     */
    private static Mac getBCGMac(final GordianSymKeySpec pSymKeySpec) throws GordianException {
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSymKeySpec;
        return GordianSymKeyType.KALYNA.equals(pSymKeySpec.getSymKeyType())
                ? new GordianKGMac(new GordianKGCMBlockCipher(BouncyCipherFactory.getBCSymEngine(mySpec)))
                : new GMac(GCMBlockCipher.newInstance(BouncyCipherFactory.getBCSymEngine(mySpec)));
    }

    /**
     * Create the BouncyCastle CMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws GordianException on error
     */
    private static Mac getBCCMac(final GordianSymKeySpec pSymKeySpec) throws GordianException {
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSymKeySpec;
        return new CMac(BouncyCipherFactory.getBCSymEngine(mySpec));
    }

    /**
     * Create the BouncyCastle Poly1305Mac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws GordianException on error
     */
    private static Mac getBCPoly1305Mac(final GordianSymKeySpec pSymKeySpec) throws GordianException {
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSymKeySpec;
        return pSymKeySpec == null
                ? new Poly1305()
                : new Poly1305(BouncyCipherFactory.getBCSymEngine(mySpec));
    }

    /**
     * Create the BouncyCastle SkeinMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCSkeinMac(final GordianDigestSpec pSpec) {
        final GordianCoreDigestSpec mySpec = (GordianCoreDigestSpec) pSpec;
        return pSpec.isXofMode()
                ? new GordianSkeinXMac(mySpec.getCoreDigestState().getLength().getLength())
                : new GordianSkeinMac(mySpec.getCoreDigestState().getLength().getLength(), pSpec.getDigestLength().getLength());
    }

    /**
     * Create the BouncyCastle KalynaMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     */
    private static Mac getBCKalynaMac(final GordianSymKeySpec pSymKeySpec) {
        final GordianLength myLen = pSymKeySpec.getBlockLength();
        return new GordianDSTU7624Mac(myLen.getLength(), myLen.getLength());
    }

    /**
     * Create the BouncyCastle kupynaMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCKupynaMac(final GordianDigestSpec pSpec) {
        return new DSTU7564Mac(pSpec.getDigestLength().getLength());
    }

    /**
     * Create the BouncyCastle blakeMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCBlake2Mac(final GordianDigestSpec pSpec) {
        return pSpec.isXofMode()
                ? new GordianBlake2XMac(BouncyDigestFactory.getBlake2Xof((GordianCoreDigestSpec) pSpec))
                : new GordianBlake2Mac(BouncyDigestFactory.getBlake2Digest((GordianCoreDigestSpec) pSpec));
    }

    /**
     * Create the BouncyCastle blake3Mac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCBlake3Mac(final GordianDigestSpec pSpec) {
        final GordianBlake3Digest myDigest = new GordianBlake3Digest(pSpec.getDigestLength().getByteLength());
        return new GordianBlake3Mac(myDigest);
    }

    /**
     * Create the BouncyCastle KMAC.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCKMAC(final GordianDigestSpec pSpec) {
        return new GordianKMACWrapper(((GordianCoreDigestSpec) pSpec).getCoreDigestState().getLength().getLength());
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
     * Create the BouncyCastle CBCMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws GordianException on error
     */
    private static Mac getBCCBCMac(final GordianSymKeySpec pSymKeySpec) throws GordianException {
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSymKeySpec;
        return new CBCBlockCipherMac(BouncyCipherFactory.getBCSymEngine(mySpec));
    }

    /**
     * Create the BouncyCastle CFBMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws GordianException on error
     */
    private static Mac getBCCFBMac(final GordianSymKeySpec pSymKeySpec) throws GordianException {
        final GordianCoreSymKeySpec mySpec = (GordianCoreSymKeySpec) pSymKeySpec;
        return new CFBBlockCipherMac(BouncyCipherFactory.getBCSymEngine(mySpec));
    }

    /**
     * Create the BouncyCastle SipHash.
     *
     * @param pSpec the SipHashSpec
     * @return the MAC
     */
    private static Mac getBCSipHash(final GordianCoreSipHashType pSpec) {
        return pSpec.isLong()
                ? new SipHash128(pSpec.getCompression(), pSpec.getFinalisation())
                : new SipHash(pSpec.getCompression(), pSpec.getFinalisation());
    }

    /**
     * Create the BouncyCastle GOSTMac.
     *
     * @return the MAC
     */
    private static Mac getBCGOSTMac() {
        return new GOST28147Mac();
    }

    /**
     * Create the BouncyCastle ZucMac.
     *
     * @param pMacSpec the macSpec
     * @return the MAC
     */
    private static Mac getBCZucMac(final GordianCoreMacSpec pMacSpec) {
        return GordianLength.LEN_128 == pMacSpec.getKeyLength()
                ? new GordianZuc128Mac()
                : new GordianZuc256Mac(pMacSpec.getMacLength().getLength());
    }
}
