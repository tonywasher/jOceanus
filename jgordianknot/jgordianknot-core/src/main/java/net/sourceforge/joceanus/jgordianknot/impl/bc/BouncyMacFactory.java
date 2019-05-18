/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import java.util.HashMap;
import java.util.Map;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.ext.digests.Blake2;
import org.bouncycastle.crypto.ext.macs.Blake2Mac;
import org.bouncycastle.crypto.ext.macs.Zuc128Mac;
import org.bouncycastle.crypto.ext.macs.Zuc256Mac;
import org.bouncycastle.crypto.generators.Poly1305KeyGenerator;
import org.bouncycastle.crypto.macs.CBCBlockCipherMac;
import org.bouncycastle.crypto.macs.CFBBlockCipherMac;
import org.bouncycastle.crypto.macs.CMac;
import org.bouncycastle.crypto.macs.GMac;
import org.bouncycastle.crypto.macs.GOST28147Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.macs.Poly1305;
import org.bouncycastle.crypto.macs.SipHash;
import org.bouncycastle.crypto.macs.SkeinMac;
import org.bouncycastle.crypto.macs.VMPCMac;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.patch.macs.DSTUX7564Mac;
import org.bouncycastle.crypto.patch.macs.DSTUX7624Mac;
import org.bouncycastle.crypto.patch.macs.KXGMac;
import org.bouncycastle.crypto.patch.modes.KGCMXBlockCipher;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Factory for BouncyCastle Macs.
 */
public class BouncyMacFactory
    extends GordianCoreMacFactory {
    /**
     * SipHash standard constant.
     */
    private static final int SIPHASH_STD = 4;

    /**
     * KeyPairGenerator Cache.
     */
    private final Map<GordianMacSpec, BouncyKeyGenerator<GordianMacSpec>> theCache;

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
    public BouncyKeyGenerator<GordianMacSpec> getKeyGenerator(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Look up in the cache */
        BouncyKeyGenerator<GordianMacSpec> myGenerator = theCache.get(pMacSpec);
        if (myGenerator == null) {
            /* Check validity of MacSpec */
            checkMacSpec(pMacSpec);

            /* Create the new generator */
            final CipherKeyGenerator myBCGenerator = getBCKeyGenerator(pMacSpec);
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
        return new BouncyMac(getFactory(), pMacSpec, myBCMac);
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
                return getBCGMac(pMacSpec.getSymKeySpec());
            case CMAC:
                return getBCCMac(pMacSpec.getSymKeySpec());
            case POLY1305:
                return getBCPoly1305Mac(pMacSpec.getSymKeySpec());
            case SKEIN:
                return getBCSkeinMac(pMacSpec.getDigestSpec());
            case BLAKE:
                return getBCBlakeMac(pMacSpec);
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
                return getBCSipHash(pMacSpec.getBoolean());
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
     * @param pDigestSpec the digestSpec
     * @return the MAC
     * @throws OceanusException on error
     */
    private Mac getBCHMac(final GordianDigestSpec pDigestSpec) throws OceanusException {
        final BouncyDigest myDigest = getFactory().getDigestFactory().createDigest(pDigestSpec);
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
               ? new KXGMac(new KGCMXBlockCipher(BouncyCipherFactory.getBCSymEngine(pSymKeySpec)))
               : new GMac(new GCMBlockCipher(BouncyCipherFactory.getBCSymEngine(pSymKeySpec)));
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
        return new Poly1305(BouncyCipherFactory.getBCSymEngine(pSymKeySpec));
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
        final Blake2 myDigest = BouncyDigestFactory.getBlake2Digest(pSpec.getDigestSpec());
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
     * @param pFast use fast sipHas?
     * @return the MAC
     */
    private static Mac getBCSipHash(final Boolean pFast) {
        return pFast ? new SipHash()
                     : new SipHash(SIPHASH_STD, SIPHASH_STD << 1);
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
