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
package net.sourceforge.joceanus.jgordianknot.impl.bc;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianSipHashSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.ext.digests.Blake2;
import org.bouncycastle.crypto.ext.digests.Blake3Digest;
import org.bouncycastle.crypto.ext.macs.Blake2Mac;
import org.bouncycastle.crypto.ext.macs.Blake3Mac;
import org.bouncycastle.crypto.ext.macs.KMACWrapper;
import org.bouncycastle.crypto.ext.macs.SkeinMac;
import org.bouncycastle.crypto.ext.macs.Zuc128Mac;
import org.bouncycastle.crypto.ext.macs.Zuc256Mac;
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
import org.bouncycastle.crypto.patch.macs.DSTUX7624Mac;
import org.bouncycastle.crypto.patch.macs.KXGMac;
import org.bouncycastle.crypto.patch.modes.KGCMXBlockCipher;

import java.util.HashMap;
import java.util.Map;

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
    BouncyMacFactory(final GordianCoreFactory pFactory) {
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
    public <T extends GordianKeySpec> BouncyKeyGenerator<T> getKeyGenerator(final T pMacSpec) throws OceanusException {
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
    public BouncyMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Check validity of MacSpec */
        checkMacSpec(pMacSpec);

        /* Create Mac */
        final Mac myBCMac = getBCMac(pMacSpec);
        return myBCMac instanceof Xof
                ? new BouncyMacXof(getFactory(), pMacSpec, (Xof) myBCMac)
                : new BouncyMac(getFactory(), pMacSpec, myBCMac);
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
                return getBCHMac(pMacSpec);
            case GMAC:
                return getBCGMac(pMacSpec.getSymKeySpec());
            case CMAC:
                return getBCCMac(pMacSpec.getSymKeySpec());
            case POLY1305:
                return getBCPoly1305Mac(pMacSpec.getSymKeySpec());
            case SKEIN:
                return getBCSkeinMac(pMacSpec.getDigestSpec());
            case BLAKE2:
                return getBCBlake2Mac(pMacSpec);
            case BLAKE3:
                return getBCBlake3Mac(pMacSpec);
            case KMAC:
                return getBCKMAC(pMacSpec);
            case KALYNA:
                return getBCKalynaMac(pMacSpec.getSymKeySpec());
            case KUPYNA:
                return getBCKupynaMac(pMacSpec.getDigestSpec());
            case VMPC:
                return getBCVMPCMac();
            case CBCMAC:
                return getBCCBCMac(pMacSpec.getSymKeySpec());
            case CFBMAC:
                return getBCCFBMac(pMacSpec.getSymKeySpec());
            case SIPHASH:
                return getBCSipHash(pMacSpec.getSipHashSpec());
            case GOST:
                return getBCGOSTMac();
            case ZUC:
                return getBCZucMac(pMacSpec);
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pMacSpec));
        }
    }

    /**
     * Create the BouncyCastle HMAC.
     *
     * @param pMacSpec the macSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCHMac(final GordianMacSpec pMacSpec) throws OceanusException {
         return new BouncyHMac(getFactory().getDigestFactory(), pMacSpec);
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
               ? new KXGMac(new KGCMXBlockCipher(BouncyCipherFactory.getBCSymEngine(pSymKeySpec)))
               : new GMac(GCMBlockCipher.newInstance(BouncyCipherFactory.getBCSymEngine(pSymKeySpec)));
    }

    /**
     * Create the BouncyCastle CMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getBCCMac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return new CMac(BouncyCipherFactory.getBCSymEngine(pSymKeySpec));
    }

    /**
     * Create the BouncyCastle Poly1305Mac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getBCPoly1305Mac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return pSymKeySpec == null
               ? new Poly1305()
               : new Poly1305(BouncyCipherFactory.getBCSymEngine(pSymKeySpec));
    }

    /**
     * Create the BouncyCastle SkeinMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCSkeinMac(final GordianDigestSpec pSpec) {
        return new SkeinMac(pSpec.getDigestState().getLength().getLength(), pSpec.getDigestLength().getLength());
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
        return new DSTU7564Mac(pSpec.getDigestLength().getLength());
    }

    /**
     * Create the BouncyCastle blakeMac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCBlake2Mac(final GordianMacSpec pSpec) {
        final Blake2 myDigest = BouncyDigestFactory.getBlake2Digest(pSpec.getDigestSpec());
        return new Blake2Mac(myDigest);
    }

    /**
     * Create the BouncyCastle blake3Mac.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCBlake3Mac(final GordianMacSpec pSpec) {
        final Blake3Digest myDigest = new Blake3Digest(pSpec.getDigestSpec().getDigestLength().getByteLength());
        return new Blake3Mac(myDigest);
    }

    /**
     * Create the BouncyCastle KMAC.
     *
     * @param pSpec the digestSpec
     * @return the MAC
     */
    private static Mac getBCKMAC(final GordianMacSpec pSpec) {
        final GordianDigestSpec mySpec = pSpec.getDigestSpec();
        return new KMACWrapper(mySpec.getDigestState().getLength().getLength());
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
     * @throws OceanusException on error
     */
    private static Mac getBCCBCMac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return new CBCBlockCipherMac(BouncyCipherFactory.getBCSymEngine(pSymKeySpec));
    }

    /**
     * Create the BouncyCastle CFBMac.
     *
     * @param pSymKeySpec the SymKeySpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private static Mac getBCCFBMac(final GordianSymKeySpec pSymKeySpec) throws OceanusException {
        return new CFBBlockCipherMac(BouncyCipherFactory.getBCSymEngine(pSymKeySpec));
    }

    /**
     * Create the BouncyCastle SipHash.
     *
     * @param pSpec the SipHashSpec
     * @return the MAC
     */
    private static Mac getBCSipHash(final GordianSipHashSpec pSpec) {
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
    private static Mac getBCZucMac(final GordianMacSpec pMacSpec) {
        return GordianLength.LEN_128 == pMacSpec.getKeyLength()
               ? new Zuc128Mac()
               : new Zuc256Mac(pMacSpec.getMacLength().getLength());
    }
}
