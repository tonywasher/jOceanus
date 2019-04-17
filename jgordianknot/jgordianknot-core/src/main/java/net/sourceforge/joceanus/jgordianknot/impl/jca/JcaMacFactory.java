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
package net.sourceforge.joceanus.jgordianknot.impl.jca;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Jca Cipher Factory.
 */
public class JcaMacFactory
    extends GordianCoreMacFactory {
    /**
     * CMAC algorithm name.
     */
    private static final String CMAC_ALGORITHM = "CMAC";

    /**
     * KeyGenerator Cache.
     */
    private final Map<GordianMacSpec, JcaKeyGenerator<GordianMacSpec>> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaMacFactory(final GordianCoreFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    public JcaFactory getFactory() {
        return (JcaFactory) super.getFactory();
    }

    @Override
    public GordianKeyGenerator<GordianMacSpec> getKeyGenerator(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Look up in the cache */
        JcaKeyGenerator<GordianMacSpec> myGenerator = theCache.get(pMacSpec);
        if (myGenerator == null) {
            /* Check validity of MacSpec */
            checkMacSpec(pMacSpec);

            /* Create the new generator */
            final String myAlgorithm = getMacSpecAlgorithm(pMacSpec);
            final KeyGenerator myJavaGenerator = getJavaKeyGenerator(myAlgorithm);
            myGenerator = new JcaKeyGenerator<>(getFactory(), pMacSpec, myJavaGenerator);

            /* Add to cache */
            theCache.put(pMacSpec, myGenerator);
        }
        return myGenerator;
    }

    @Override
    public JcaMac createMac(final GordianMacSpec pMacSpec) throws OceanusException {
        /* Check validity of MacSpec */
        checkMacSpec(pMacSpec);

        /* Create Mac */
        final Mac myJavaMac = getJavaMac(pMacSpec);
        return new JcaMac(getFactory(), pMacSpec, myJavaMac);
    }

    @Override
    protected boolean validMacType(final GordianMacType pMacType) {
        switch (pMacType) {
            case BLAKE:
            case KALYNA:
            case KUPYNA:
            case ZUC:
            case CBCMAC:
            case CFBMAC:
                return false;
            default:
                return super.validMacType(pMacType);
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
            case GMAC:
            case CMAC:
            case POLY1305:
            case SKEIN:
            case KUPYNA:
            case SIPHASH:
            case GOST:
                return getJavaMac(getMacSpecAlgorithm(pMacSpec));
            case VMPC:
                return getJavaMac("VMPC-MAC");
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pMacSpec));
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
            return Mac.getInstance(pAlgorithm, JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Mac", e);
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
            /* Massage the keyGenerator name */
            String myAlgorithm = pAlgorithm;

            /* CMAC generators use the symKeyGenerator */
            if (myAlgorithm.endsWith(CMAC_ALGORITHM)) {
                myAlgorithm = myAlgorithm.substring(0, myAlgorithm.length() - CMAC_ALGORITHM.length());
            }

            /* GOST generators use the GOST28147 key generator */
            if (myAlgorithm.startsWith("GOST28147")) {
                myAlgorithm = "GOST28147";
            }

            /* Return a KeyGenerator for the algorithm */
            return KeyGenerator.getInstance(myAlgorithm, JcaFactory.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyGenerator", e);
        }
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
                return getHMacAlgorithm(pMacSpec.getDigestSpec());
            case GMAC:
                return getGMacAlgorithm(pMacSpec.getSymKeySpec());
            case CMAC:
                return getCMacAlgorithm(pMacSpec.getSymKeySpec());
            case POLY1305:
                return getPoly1305Algorithm(pMacSpec.getSymKeySpec());
            case SKEIN:
                return getSkeinMacAlgorithm(pMacSpec.getDigestSpec());
            case KUPYNA:
                return getKupynaMacAlgorithm(pMacSpec.getDigestSpec());
            case SIPHASH:
                return pMacSpec.toString();
            case GOST:
                return "GOST28147MAC";
            case VMPC:
                return "VMPC-KSA3";
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pMacSpec));
        }
    }

    /**
     * Return the associated HMac algorithm.
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getHMacAlgorithm(final GordianDigestSpec pDigestSpec) throws OceanusException {
        return "HMac" + JcaDigest.getHMacAlgorithm(pDigestSpec);
    }

    /**
     * Obtain the GMAC algorithm.
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getGMacAlgorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Return algorithm name */
        return JcaCipherFactory.getSymKeyAlgorithm(pKeySpec) + "GMAC";
    }

    /**
     * Obtain the GMAC algorithm.
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getCMacAlgorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Return algorithm name */
        return JcaCipherFactory.getSymKeyAlgorithm(pKeySpec) + CMAC_ALGORITHM;
    }

    /**
     * Obtain the Poly1305 algorithm.
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getPoly1305Algorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Return algorithm name */
        return "POLY1305-" + JcaCipherFactory.getSymKeyAlgorithm(pKeySpec);
    }

    /**
     * Obtain the Skein MAC algorithm.
     * @param pSpec the digestSpec
     * @return the algorithm
     */
    private static String getSkeinMacAlgorithm(final GordianDigestSpec pSpec) {
        final String myLen = Integer.toString(pSpec.getDigestLength().getLength());
        final String myState = Integer.toString(pSpec.getStateLength().getLength());
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("Skein-MAC-")
                .append(myState)
                .append('-')
                .append(myLen);
        return myBuilder.toString();
    }

    /**
     * Obtain the Kupyna MAC algorithm.
     * @param pSpec the digestSpec
     * @return the algorithm
     * @throws OceanusException on error
     */
    private static String getKupynaMacAlgorithm(final GordianDigestSpec pSpec) throws OceanusException {
        /* For some reason this is accessed as HMAC !!! */
        return getHMacAlgorithm(pSpec);
    }

    @Override
    protected boolean validHMacDigestType(final GordianDigestType pDigestType) {
        return JcaDigest.isHMacSupported(pDigestType)
                && getFactory().getDigestFactory().validDigestType(pDigestType);
    }

    @Override
    protected boolean validCMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case AES:
            case DESEDE:
            case BLOWFISH:
            case SHACAL2:
            case THREEFISH:
            case SEED:
            case SM4:
                return super.validCMacSymKeySpec(pKeySpec);
            default:
                return false;
        }
    }
    @Override
    protected boolean validGMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        switch (pKeySpec.getSymKeyType()) {
            case KUZNYECHIK:
            case KALYNA:
                return false;
            default:
                return super.validGMacSymKeySpec(pKeySpec);
        }
    }

    @Override
    protected boolean validPoly1305SymKeySpec(final GordianSymKeySpec pKeySpec) {
        if (GordianSymKeyType.KALYNA.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        return super.validPoly1305SymKeySpec(pKeySpec);
    }
}
