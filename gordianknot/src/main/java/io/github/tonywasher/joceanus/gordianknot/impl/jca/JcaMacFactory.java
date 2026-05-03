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
package io.github.tonywasher.joceanus.gordianknot.impl.jca;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.mac.GordianCoreMacFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.mac.GordianCoreMacSpec;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
     * GOST algorithm name.
     */
    private static final String GOST_ALGORITHM = "GOST28147";

    /**
     * ZUC-128 algorithm name.
     */
    private static final String ZUC256_ALGORITHM = "ZUC-256";

    /**
     * KeyGenerator Cache.
     */
    private final Map<GordianKeySpec, JcaKeyGenerator<? extends GordianKeySpec>> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaMacFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> GordianKeyGenerator<T> getKeyGenerator(final T pMacSpec) throws GordianException {
        /* Look up in the cache */
        JcaKeyGenerator<T> myGenerator = (JcaKeyGenerator<T>) theCache.get(pMacSpec);
        if (myGenerator == null) {
            /* Check validity of MacSpec */
            checkMacSpec(pMacSpec);

            /* Create the new generator */
            final String myAlgorithm = getMacSpecAlgorithm((GordianCoreMacSpec) pMacSpec);
            final KeyGenerator myJavaGenerator = getJavaKeyGenerator(myAlgorithm);
            myGenerator = new JcaKeyGenerator<>(getFactory(), pMacSpec, myJavaGenerator);

            /* Add to cache */
            theCache.put(pMacSpec, myGenerator);
        }
        return myGenerator;
    }

    @Override
    public JcaMac createMac(final GordianMacSpec pMacSpec) throws GordianException {
        /* Check validity of MacSpec */
        checkMacSpec(pMacSpec);

        /* Create Mac */
        final Mac myJavaMac = getJavaMac((GordianCoreMacSpec) pMacSpec);
        return new JcaMac(getFactory(), pMacSpec, myJavaMac);
    }

    @Override
    protected boolean validMacType(final GordianMacType pMacType) {
        return switch (pMacType) {
            case BLAKE2, BLAKE3, KALYNA, CBCMAC, CFBMAC -> false;
            default -> super.validMacType(pMacType);
        };
    }

    /**
     * Create the BouncyCastle MAC via JCA.
     *
     * @param pMacSpec the MacSpec
     * @return the MAC
     * @throws GordianException on error
     */
    private Mac getJavaMac(final GordianCoreMacSpec pMacSpec) throws GordianException {
        return switch (pMacSpec.getMacType()) {
            case HMAC, GMAC, CMAC, POLY1305, SKEIN, KUPYNA, SIPHASH, GOST, ZUC, KMAC ->
                    getJavaMac(getMacSpecAlgorithm(pMacSpec));
            case VMPC -> getJavaMac("VMPC-MAC");
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pMacSpec));
        };
    }

    /**
     * Create the BouncyCastle MAC via JCA.
     *
     * @param pAlgorithm the Algorithm
     * @return the MAC
     * @throws GordianException on error
     */
    private static Mac getJavaMac(final String pAlgorithm) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a MAC for the algorithm */
            return Mac.getInstance(pAlgorithm, JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Mac", e);
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator via JCA.
     *
     * @param pAlgorithm the Algorithm
     * @return the KeyGenerator
     * @throws GordianException on error
     */
    private static KeyGenerator getJavaKeyGenerator(final String pAlgorithm) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Massage the keyGenerator name */
            String myAlgorithm = pAlgorithm;

            /* CMAC generators use the symKeyGenerator */
            if (myAlgorithm.endsWith(CMAC_ALGORITHM)) {
                myAlgorithm = myAlgorithm.substring(0, myAlgorithm.length() - CMAC_ALGORITHM.length());
            }

            /* GOST generators use the GOST28147 key generator */
            if (myAlgorithm.startsWith(GOST_ALGORITHM)) {
                myAlgorithm = GOST_ALGORITHM;
            }

            /* ZUC-256 generators use the ZUC-256 key generator */
            if (myAlgorithm.startsWith(ZUC256_ALGORITHM)) {
                myAlgorithm = ZUC256_ALGORITHM;
            }

            /* Return a KeyGenerator for the algorithm */
            return KeyGenerator.getInstance(myAlgorithm, JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyGenerator", e);
        }
    }

    /**
     * Obtain the MacSpec Key algorithm.
     *
     * @param pMacSpec the MacSpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    private static String getMacSpecAlgorithm(final GordianCoreMacSpec pMacSpec) throws GordianException {
        return switch (pMacSpec.getMacType()) {
            case HMAC -> getHMacAlgorithm(pMacSpec.getDigestSpec());
            case GMAC -> getGMacAlgorithm(pMacSpec.getSymKeySpec());
            case CMAC -> getCMacAlgorithm(pMacSpec.getSymKeySpec());
            case POLY1305 -> getPoly1305Algorithm(pMacSpec.getSymKeySpec());
            case SKEIN -> getSkeinMacAlgorithm(pMacSpec.getDigestSpec());
            case KUPYNA -> getKupynaMacAlgorithm(pMacSpec.getDigestSpec());
            case ZUC -> getZucMacAlgorithm(pMacSpec);
            case SIPHASH -> pMacSpec.toString();
            case KMAC -> pMacSpec.getMacType().toString() + pMacSpec.getDigestSpec().getCoreDigestState();
            case GOST -> "GOST28147MAC";
            case VMPC -> "VMPC-KSA3";
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pMacSpec));
        };
    }

    /**
     * Return the associated HMac algorithm.
     *
     * @param pDigestSpec the digestSpec
     * @return the algorithm
     * @throws GordianException on error
     */
    private static String getHMacAlgorithm(final GordianDigestSpec pDigestSpec) throws GordianException {
        return "HMac" + JcaDigest.getHMacAlgorithm((GordianCoreDigestSpec) pDigestSpec);
    }

    /**
     * Obtain the GMAC algorithm.
     *
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws GordianException on error
     */
    private static String getGMacAlgorithm(final GordianSymKeySpec pKeySpec) throws GordianException {
        /* Return algorithm name */
        return JcaCipherFactory.getSymKeyAlgorithm(pKeySpec) + "GMAC";
    }

    /**
     * Obtain the GMAC algorithm.
     *
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws GordianException on error
     */
    private static String getCMacAlgorithm(final GordianSymKeySpec pKeySpec) throws GordianException {
        /* Return algorithm name */
        return JcaCipherFactory.getSymKeyAlgorithm(pKeySpec) + CMAC_ALGORITHM;
    }

    /**
     * Obtain the Poly1305 algorithm.
     *
     * @param pKeySpec the symmetric keySpec
     * @return the algorithm
     * @throws GordianException on error
     */
    private static String getPoly1305Algorithm(final GordianSymKeySpec pKeySpec) throws GordianException {
        /* Return algorithm name */
        return pKeySpec == null
                ? "POLY1305"
                : "POLY1305-" + JcaCipherFactory.getSymKeyAlgorithm(pKeySpec);
    }

    /**
     * Obtain the Skein MAC algorithm.
     *
     * @param pSpec the digestSpec
     * @return the algorithm
     */
    private static String getSkeinMacAlgorithm(final GordianDigestSpec pSpec) {
        return "Skein-MAC-"
                + ((GordianCoreDigestSpec) pSpec).getCoreDigestState()
                + '-'
                + pSpec.getDigestLength();
    }

    /**
     * Obtain the Skein MAC algorithm.
     *
     * @param pSpec the digestSpec
     * @return the algorithm
     */
    private static String getZucMacAlgorithm(final GordianCoreMacSpec pSpec) {
        final String myKeyLen = Integer.toString(pSpec.getKeyLength().getLength());
        final String myMacLen = Integer.toString(pSpec.getMacLength().getLength());
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append("ZUC-")
                .append(myKeyLen);
        if (GordianLength.LEN_256 == pSpec.getKeyLength()) {
            myBuilder.append('-')
                    .append(myMacLen);
        }
        return myBuilder.toString();
    }

    /**
     * Obtain the Kupyna MAC algorithm.
     *
     * @param pSpec the digestSpec
     * @return the algorithm
     * @throws GordianException on error
     */
    private static String getKupynaMacAlgorithm(final GordianDigestSpec pSpec) throws GordianException {
        /* For some reason this is accessed as HMAC !!! */
        return getHMacAlgorithm(pSpec);
    }

    @Override
    protected boolean validCMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        return switch (pKeySpec.getSymKeyType()) {
            case AES, DESEDE, BLOWFISH, SHACAL2, THREEFISH, SEED, SM4 -> super.validCMacSymKeySpec(pKeySpec);
            default -> false;
        };
    }

    @Override
    protected boolean validGMacSymKeySpec(final GordianSymKeySpec pKeySpec) {
        return switch (pKeySpec.getSymKeyType()) {
            case KUZNYECHIK, KALYNA -> false;
            default -> super.validGMacSymKeySpec(pKeySpec);
        };
    }

    @Override
    protected boolean validPoly1305SymKeySpec(final GordianSymKeySpec pKeySpec) {
        if (GordianSymKeyType.KALYNA.equals(pKeySpec.getSymKeyType())) {
            return false;
        }
        return super.validPoly1305SymKeySpec(pKeySpec);
    }
}
