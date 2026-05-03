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
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymCipher;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianWrapper;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianCipherMode;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPadding;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAEADCipher.JcaStreamAEADCipher;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaAEADCipher.JcaSymAEADCipher;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaCipher.JcaStreamCipher;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.JcaCipher.JcaSymCipher;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Jca Cipher Factory.
 */
public class JcaCipherFactory
        extends GordianCoreCipherFactory {
    /**
     * Cipher Algorithm Separator.
     */
    private static final Character ALGO_SEP = '/';

    /**
     * Kalyna algorithm name.
     */
    private static final String KALYNA_ALGORITHM = "DSTU7624";

    /**
     * KeyGenerator Cache.
     */
    private final Map<GordianKeySpec, JcaKeyGenerator<? extends GordianKeySpec>> theCache;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    JcaCipherFactory(final GordianBaseFactory pFactory) {
        /* Initialise underlying class */
        super(pFactory);

        /* Create the cache */
        theCache = new HashMap<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> GordianKeyGenerator<T> getKeyGenerator(final T pKeySpec) throws GordianException {
        /* Look up in the cache */
        JcaKeyGenerator<T> myGenerator = (JcaKeyGenerator<T>) theCache.get(pKeySpec);
        if (myGenerator == null) {
            /* Check the KeySpec */
            checkKeySpec(pKeySpec);

            /* Create the new generator */
            final String myAlgorithm = getKeyAlgorithm(pKeySpec);
            final KeyGenerator myJavaGenerator = getJavaKeyGenerator(myAlgorithm);
            myGenerator = new JcaKeyGenerator<>(getFactory(), pKeySpec, myJavaGenerator);

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
            /* Create the cipher */
            final Cipher myBCCipher = getJavaCipher(pCipherSpec);
            return new JcaSymAEADCipher(getFactory(), mySpec, myBCCipher);

            /* else create the standard cipher */
        } else {
            /* Create the cipher */
            final Cipher myBCCipher = getJavaCipher(pCipherSpec);
            return new JcaSymCipher(getFactory(), mySpec, myBCCipher);
        }
    }

    @Override
    public GordianStreamCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        /* Check validity of StreamKeySpec */
        checkStreamCipherSpec(pCipherSpec);
        final GordianCoreStreamCipherSpec mySpec = (GordianCoreStreamCipherSpec) pCipherSpec;

        final Cipher myJCACipher = getJavaCipher(pCipherSpec);
        return pCipherSpec.isAEAD()
                ? new JcaStreamAEADCipher(getFactory(), mySpec, myJCACipher)
                : new JcaStreamCipher(getFactory(), mySpec, myJCACipher);
    }

    @Override
    public GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        /* Create the cipher */
        final JcaKey<GordianSymKeySpec> myKey = JcaKey.accessKey(pKey);
        final GordianSymCipherSpecBuilder myBuilder = GordianCoreSymCipherSpecBuilder.newInstance();
        final GordianSymCipherSpec mySpec = myBuilder.ecb(myKey.getKeyType(), GordianPadding.NONE);
        final JcaSymCipher myJcaCipher = (JcaSymCipher) createSymKeyCipher(mySpec);
        return createKeyWrapper(myKey, myJcaCipher);
    }

    /**
     * Obtain the algorithm for the keySpec.
     *
     * @param pKeySpec the keySpec
     * @param <T>      the SpecType
     * @return the name of the algorithm
     * @throws GordianException on error
     */
    private static <T extends GordianKeySpec> String getKeyAlgorithm(final T pKeySpec) throws GordianException {
        if (pKeySpec instanceof GordianStreamKeySpec mySpec) {
            return getStreamKeyAlgorithm(mySpec);
        }
        if (pKeySpec instanceof GordianSymKeySpec mySpec) {
            return getSymKeyAlgorithm(mySpec);
        }
        throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
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

            /* Note that DSTU7624 has only a single keyGenerator */
            if (myAlgorithm.startsWith(KALYNA_ALGORITHM)) {
                myAlgorithm = KALYNA_ALGORITHM;
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
     * Create the BouncyCastle SymKey Cipher via JCA.
     *
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static Cipher getJavaCipher(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        final String myAlgo = getSymKeyAlgorithm(pCipherSpec.getKeySpec())
                + ALGO_SEP
                + getCipherModeAlgorithm(pCipherSpec)
                + ALGO_SEP
                + getPaddingAlgorithm(pCipherSpec.getPadding());
        return getJavaCipher(myAlgo);
    }

    /**
     * Create the BouncyCastle StreamKey Cipher via JCA.
     *
     * @param pCipherSpec the StreamCipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static Cipher getJavaCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        final GordianStreamKeySpec myKeySpec = pCipherSpec.getKeySpec();
        String myAlgo = getStreamKeyAlgorithm(myKeySpec);
        if (pCipherSpec.isAEAD()
                && GordianStreamKeyType.CHACHA20 == myKeySpec.getStreamKeyType()) {
            myAlgo = "CHACHA20-POLY1305";
        }
        return getJavaCipher(myAlgo);
    }

    /**
     * Create the StreamKey Cipher via JCA.
     *
     * @param pAlgorithm the Algorithm
     * @return the KeyGenerator
     * @throws GordianException on error
     */
    private static Cipher getJavaCipher(final String pAlgorithm) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a Cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException
                 | NoSuchPaddingException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Cipher", e);
        }
    }

    /**
     * Obtain the SymKey algorithm.
     *
     * @param pKeySpec the keySpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    static String getSymKeyAlgorithm(final GordianSymKeySpec pKeySpec) throws GordianException {
        return switch (pKeySpec.getSymKeyType()) {
            case TWOFISH -> "TwoFish";
            case SERPENT -> "Serpent";
            case THREEFISH -> "ThreeFish-" + pKeySpec.getBlockLength().getLength();
            case GOST -> "GOST28147";
            case SHACAL2 -> "Shacal-2";
            case KUZNYECHIK -> "GOST3412-2015";
            case KALYNA -> KALYNA_ALGORITHM + "-" + pKeySpec.getBlockLength().getLength();
            case RC5 -> GordianLength.LEN_128.equals(pKeySpec.getBlockLength())
                    ? "RC5-64"
                    : "RC5";
            case AES, CAMELLIA, CAST6, RC6, ARIA, NOEKEON, SM4, SEED, SKIPJACK, TEA, XTEA, IDEA, RC2, CAST5, BLOWFISH,
                 DESEDE -> pKeySpec.getSymKeyType().name();
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        };
    }

    /**
     * Obtain the CipherMode algorithm.
     *
     * @param pSpec the cipherSpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    private static String getCipherModeAlgorithm(final GordianSymCipherSpec pSpec) throws GordianException {
        final GordianCipherMode myMode = pSpec.getCipherMode();
        return switch (pSpec.getCipherMode()) {
            case ECB, SIC, EAX, CCM, GCM, OCB, GOFB, GCFB, CFB8, OFB8 -> myMode.name();
            case CBC, G3413CBC -> GordianCipherMode.CBC.name();
            case CFB, G3413CFB -> "CFB";
            case OFB, G3413OFB -> GordianCipherMode.OFB.name();
            case KCTR, G3413CTR -> "CTR";
            case KCCM -> GordianCipherMode.CCM.name();
            case KGCM -> GordianCipherMode.GCM.name();
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(myMode));
        };
    }

    /**
     * Obtain the Padding algorithm.
     *
     * @param pPadding use padding true/false
     * @return the Algorithm
     */
    private static String getPaddingAlgorithm(final GordianPadding pPadding) {
        return switch (pPadding) {
            case CTS -> "withCTS";
            case X923 -> "X923Padding";
            case PKCS7 -> "PKCS7Padding";
            case ISO7816D4 -> "ISO7816-4Padding";
            case TBC -> "TBCPadding";
            default -> "NoPadding";
        };
    }

    /**
     * Obtain the StreamKey algorithm.
     *
     * @param pKeySpec the keySpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    private static String getStreamKeyAlgorithm(final GordianStreamKeySpec pKeySpec) throws GordianException {
        return switch (pKeySpec.getStreamKeyType()) {
            case HC -> GordianLength.LEN_128 == pKeySpec.getKeyLength()
                    ? "HC128"
                    : "HC256";
            case ZUC -> GordianLength.LEN_128 == pKeySpec.getKeyLength()
                    ? "ZUC-128"
                    : "ZUC-256";
            case CHACHA20 -> pKeySpec.getSubKeyType() == GordianChaCha20Key.STD
                    ? "CHACHA"
                    : "CHACHA7539";
            case SALSA20 -> pKeySpec.getSubKeyType() == GordianSalsa20Key.STD
                    ? pKeySpec.getStreamKeyType().name()
                    : "XSALSA20";
            case VMPC -> pKeySpec.getSubKeyType() == GordianVMPCKey.STD
                    ? pKeySpec.getStreamKeyType().name()
                    : "VMPC-KSA3";
            case GRAIN -> "Grain128";
            case ISAAC, RC4 -> pKeySpec.getStreamKeyType().name();
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        };
    }

    @Override
    protected boolean validStreamKeySpec(final GordianStreamKeySpec pKeySpec) {
        /* Check basic validity */
        if (!super.validStreamKeySpec(pKeySpec)) {
            return false;
        }

        /* Reject XChaCha20 */
        return pKeySpec.getStreamKeyType() != GordianStreamKeyType.CHACHA20
                || pKeySpec.getSubKeyType() != GordianChaCha20Key.XCHACHA;
    }

    @Override
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec) {
        /* Check standard features */
        if (!super.validSymCipherSpec(pCipherSpec)) {
            return false;
        }

        /* Disallow GCM-SIV */
        final GordianCipherMode myMode = pCipherSpec.getCipherMode();
        if (GordianCipherMode.GCMSIV.equals(myMode)) {
            return false;
        }

        /* Additional Checks */
        return switch (pCipherSpec.getKeySpec().getSymKeyType()) {
            case KALYNA ->
                /* Disallow OCB, CCM and GCM */
                    !GordianCipherMode.OCB.equals(myMode)
                            && !GordianCipherMode.KCCM.equals(myMode)
                            && !GordianCipherMode.KGCM.equals(myMode)
                            && !GordianCipherMode.CCM.equals(myMode)
                            && !GordianCipherMode.GCM.equals(myMode);
            case GOST ->
                /* Disallow OFB and CFB */
                    !GordianCipherMode.OFB.equals(myMode)
                            && !GordianCipherMode.CFB.equals(myMode);
            case KUZNYECHIK ->
                /* Disallow OCB, OFB, CFB and CBC */
                    !GordianCipherMode.OCB.equals(myMode)
                            && !GordianCipherMode.OFB.equals(myMode)
                            && !GordianCipherMode.CFB.equals(myMode)
                            && !GordianCipherMode.CBC.equals(myMode);
            default -> true;
        };
    }
}
