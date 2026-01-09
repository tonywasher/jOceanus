/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.jca;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianChaCha20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianSalsa20Key;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpec.GordianVMPCKey;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipherSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAEADCipher.JcaStreamAEADCipher;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaAEADCipher.JcaSymAEADCipher;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaCipher.JcaStreamCipher;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaCipher.JcaSymCipher;

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

        /* If this is an AAD cipher */
        if (pCipherSpec.isAAD()) {
            /* Create the cipher */
            final Cipher myBCCipher = getJavaCipher(pCipherSpec);
            return new JcaSymAEADCipher(getFactory(), pCipherSpec, myBCCipher);

            /* else create the standard cipher */
        } else {
            /* Create the cipher */
            final Cipher myBCCipher = getJavaCipher(pCipherSpec);
            return new JcaSymCipher(getFactory(), pCipherSpec, myBCCipher);
        }
    }

    @Override
    public GordianStreamCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        /* Check validity of StreamKeySpec */
        checkStreamCipherSpec(pCipherSpec);

        final Cipher myJCACipher = getJavaCipher(pCipherSpec);
        return pCipherSpec.isAEAD()
               ? new JcaStreamAEADCipher(getFactory(), pCipherSpec, myJCACipher)
               : new JcaStreamCipher(getFactory(), pCipherSpec, myJCACipher);
    }

    @Override
    public GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        /* Create the cipher */
        final JcaKey<GordianSymKeySpec> myKey = JcaKey.accessKey(pKey);
        final GordianSymCipherSpec mySpec = GordianSymCipherSpecBuilder.ecb(myKey.getKeyType(), GordianPadding.NONE);
        final JcaSymCipher myJcaCipher = (JcaSymCipher) createSymKeyCipher(mySpec);
        return createKeyWrapper(myKey, myJcaCipher);
    }

    /**
     * Obtain the algorithm for the keySpec.
     * @param pKeySpec the keySpec
     * @param <T> the SpecType
     * @return the name of the algorithm
     * @throws GordianException on error
     */
    private static <T extends GordianKeySpec> String getKeyAlgorithm(final T pKeySpec) throws GordianException {
        if (pKeySpec instanceof GordianStreamKeySpec) {
            return getStreamKeyAlgorithm((GordianStreamKeySpec) pKeySpec);
        }
        if (pKeySpec instanceof GordianSymKeySpec) {
            return getSymKeyAlgorithm((GordianSymKeySpec) pKeySpec);
        }
        throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
    }

    /**
     * Create the BouncyCastle KeyGenerator via JCA.
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
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static Cipher getJavaCipher(final GordianSymCipherSpec pCipherSpec) throws GordianException {
        final String myAlgo = getSymKeyAlgorithm(pCipherSpec.getKeyType())
                + ALGO_SEP
                + getCipherModeAlgorithm(pCipherSpec)
                + ALGO_SEP
                + getPaddingAlgorithm(pCipherSpec.getPadding());
        return getJavaCipher(myAlgo);
    }

    /**
     * Create the BouncyCastle StreamKey Cipher via JCA.
     * @param pCipherSpec the StreamCipherSpec
     * @return the Cipher
     * @throws GordianException on error
     */
    private static Cipher getJavaCipher(final GordianStreamCipherSpec pCipherSpec) throws GordianException {
        final GordianStreamKeySpec myKeySpec = pCipherSpec.getKeyType();
        String myAlgo = getStreamKeyAlgorithm(myKeySpec);
        if (pCipherSpec.isAEAD()
                && GordianStreamKeyType.CHACHA20 == myKeySpec.getStreamKeyType()) {
            myAlgo = "CHACHA20-POLY1305";
        }
        return getJavaCipher(myAlgo);
    }

    /**
     * Create the StreamKey Cipher via JCA.
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
     * @param pKeySpec the keySpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    static String getSymKeyAlgorithm(final GordianSymKeySpec pKeySpec) throws GordianException {
        switch (pKeySpec.getSymKeyType()) {
            case TWOFISH:
                return "TwoFish";
            case SERPENT:
                return "Serpent";
            case THREEFISH:
                return "ThreeFish-" + pKeySpec.getBlockLength().getLength();
            case GOST:
                return "GOST28147";
            case SHACAL2:
                return "Shacal-2";
            case KUZNYECHIK:
                return "GOST3412-2015";
            case KALYNA:
                return KALYNA_ALGORITHM + "-" + pKeySpec.getBlockLength().getLength();
            case RC5:
                return GordianLength.LEN_128.equals(pKeySpec.getBlockLength())
                       ? "RC5-64"
                       : "RC5";
            case AES:
            case CAMELLIA:
            case CAST6:
            case RC6:
            case ARIA:
            case NOEKEON:
            case SM4:
            case SEED:
            case SKIPJACK:
            case TEA:
            case XTEA:
            case IDEA:
            case RC2:
            case CAST5:
            case BLOWFISH:
            case DESEDE:
                return pKeySpec.getSymKeyType().name();
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        }
    }

    /**
     * Obtain the CipherMode algorithm.
     * @param pSpec the cipherSpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    private static String getCipherModeAlgorithm(final GordianSymCipherSpec pSpec) throws GordianException {
        final GordianCipherMode myMode = pSpec.getCipherMode();
        switch (pSpec.getCipherMode()) {
            case ECB:
            case SIC:
            case EAX:
            case CCM:
            case GCM:
            case OCB:
            case GOFB:
            case GCFB:
            case CFB8:
            case OFB8:
                return myMode.name();
            case CBC:
            case G3413CBC:
                return GordianCipherMode.CBC.name();
            case CFB:
            case G3413CFB:
                return "CFB";
            case OFB:
            case G3413OFB:
                return GordianCipherMode.OFB.name();
            case KCTR:
            case G3413CTR:
                return "CTR";
            case KCCM:
                return GordianCipherMode.CCM.name();
            case KGCM:
                return GordianCipherMode.GCM.name();
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(myMode));
        }
    }

    /**
     * Obtain the Padding algorithm.
     * @param pPadding use padding true/false
     * @return the Algorithm
     */
    private static String getPaddingAlgorithm(final GordianPadding pPadding) {
        switch (pPadding) {
            case CTS:
                return "withCTS";
            case X923:
                return "X923Padding";
            case PKCS7:
                return "PKCS7Padding";
            case ISO7816D4:
                return "ISO7816-4Padding";
            case TBC:
                return "TBCPadding";
            case NONE:
            default:
                return "NoPadding";
        }
    }

    /**
     * Obtain the StreamKey algorithm.
     * @param pKeySpec the keySpec
     * @return the Algorithm
     * @throws GordianException on error
     */
    private static String getStreamKeyAlgorithm(final GordianStreamKeySpec pKeySpec) throws GordianException {
        switch (pKeySpec.getStreamKeyType()) {
            case HC:
                return GordianLength.LEN_128 == pKeySpec.getKeyLength()
                       ? "HC128"
                       : "HC256";
            case ZUC:
                return GordianLength.LEN_128 == pKeySpec.getKeyLength()
                       ? "ZUC-128"
                       : "ZUC-256";
            case CHACHA20:
                return pKeySpec.getSubKeyType() == GordianChaCha20Key.STD
                       ? "CHACHA"
                       : "CHACHA7539";
            case SALSA20:
                return pKeySpec.getSubKeyType() == GordianSalsa20Key.STD
                       ? pKeySpec.getStreamKeyType().name()
                       : "XSALSA20";
            case VMPC:
                return pKeySpec.getSubKeyType() == GordianVMPCKey.STD
                       ? pKeySpec.getStreamKeyType().name()
                       : "VMPC-KSA3";
            case GRAIN:
                return "Grain128";
            case ISAAC:
            case RC4:
                return pKeySpec.getStreamKeyType().name();
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pKeySpec));
        }
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
        switch (pCipherSpec.getKeyType().getSymKeyType()) {
            case KALYNA:
                /* Disallow OCB, CCM and GCM */
                return !GordianCipherMode.OCB.equals(myMode)
                        && !GordianCipherMode.KCCM.equals(myMode)
                        && !GordianCipherMode.KGCM.equals(myMode)
                        && !GordianCipherMode.CCM.equals(myMode)
                        && !GordianCipherMode.GCM.equals(myMode);
            case GOST:
                /* Disallow OFB and CFB */
                return !GordianCipherMode.OFB.equals(myMode)
                        && !GordianCipherMode.CFB.equals(myMode);
            case KUZNYECHIK:
                /* Disallow OCB, OFB, CFB and CBC */
                return !GordianCipherMode.OCB.equals(myMode)
                        && !GordianCipherMode.OFB.equals(myMode)
                        && !GordianCipherMode.CFB.equals(myMode)
                        && !GordianCipherMode.CBC.equals(myMode);
            default:
                return true;
        }
    }
}
