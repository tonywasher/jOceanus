/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec.GordianChaCha20Key;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec.GordianSalsa20Key;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec.GordianVMPCKey;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAADCipher.JcaStreamAADCipher;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaAADCipher.JcaSymAADCipher;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaCipher.JcaStreamCipher;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaCipher.JcaSymCipher;
import net.sourceforge.joceanus.jtethys.OceanusException;

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
    JcaCipherFactory(final GordianCoreFactory pFactory) {
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
    @SuppressWarnings("unchecked")
    public <T extends GordianKeySpec> GordianKeyGenerator<T> getKeyGenerator(final T pKeySpec) throws OceanusException {
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
    public GordianSymCipher createSymKeyCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKeySpec */
        checkSymCipherSpec(pCipherSpec);

        /* If this is an AAD cipher */
        if (pCipherSpec.isAAD()) {
            /* Create the cipher */
            final Cipher myBCCipher = getJavaCipher(pCipherSpec);
            return new JcaSymAADCipher(getFactory(), pCipherSpec, myBCCipher);

            /* else create the standard cipher */
        } else {
            /* Create the cipher */
            final Cipher myBCCipher = getJavaCipher(pCipherSpec);
            return new JcaSymCipher(getFactory(), pCipherSpec, myBCCipher);
        }
    }

    @Override
    public GordianStreamCipher createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of StreamKeySpec */
        checkStreamCipherSpec(pCipherSpec);

        final Cipher myJCACipher = getJavaCipher(pCipherSpec);
        return pCipherSpec.isAAD()
               ? new JcaStreamAADCipher(getFactory(), pCipherSpec, myJCACipher)
               : new JcaStreamCipher(getFactory(), pCipherSpec, myJCACipher);
    }

    @Override
    public GordianWrapper createKeyWrapper(final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
        /* Create the cipher */
        final JcaKey<GordianSymKeySpec> myKey = JcaKey.accessKey(pKey);
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(myKey.getKeyType(), GordianPadding.NONE);
        final JcaSymCipher myJcaCipher = (JcaSymCipher) createSymKeyCipher(mySpec);
        return createKeyWrapper(myKey, myJcaCipher);
    }

    /**
     * Obtain the algorithm for the keySpec.
     * @param pKeySpec the keySpec
     * @param <T> the SpecType
     * @return the name of the algorithm
     * @throws OceanusException on error
     */
    private <T extends GordianKeySpec> String getKeyAlgorithm(final T pKeySpec) throws OceanusException {
        if (pKeySpec instanceof GordianStreamKeySpec) {
            return getStreamKeyAlgorithm((GordianStreamKeySpec) pKeySpec);
        }
        if (pKeySpec instanceof GordianSymKeySpec) {
            return getSymKeyAlgorithm((GordianSymKeySpec) pKeySpec);
        }
        throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
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

            /* Note that DSTU7624 has only a single keyGenerator */
            if (myAlgorithm.startsWith(KALYNA_ALGORITHM)) {
                myAlgorithm = KALYNA_ALGORITHM;
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
     * Create the BouncyCastle SymKey Cipher via JCA.
     * @param pCipherSpec the cipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private static Cipher getJavaCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(getSymKeyAlgorithm(pCipherSpec.getKeyType()))
                .append(ALGO_SEP)
                .append(getCipherModeAlgorithm(pCipherSpec))
                .append(ALGO_SEP)
                .append(getPaddingAlgorithm(pCipherSpec.getPadding()));
        return getJavaCipher(myBuilder.toString());
    }

    /**
     * Create the BouncyCastle StreamKey Cipher via JCA.
     * @param pCipherSpec the StreamCipherSpec
     * @return the Cipher
     * @throws OceanusException on error
     */
    private Cipher getJavaCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        final GordianStreamKeySpec myKeySpec = pCipherSpec.getKeyType();
        String myAlgo = getStreamKeyAlgorithm(myKeySpec);
        if (pCipherSpec.isAAD()
                && GordianStreamKeyType.CHACHA20 == myKeySpec.getStreamKeyType()) {
            myAlgo = "CHACHA20-POLY1305";
        }
        return getJavaCipher(myAlgo);
    }

    /**
     * Create the StreamKey Cipher via JCA.
     * @param pAlgorithm the Algorithm
     * @return the KeyGenerator
     * @throws OceanusException on error
     */
    private static Cipher getJavaCipher(final String pAlgorithm) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Return a Cipher for the algorithm */
            return Cipher.getInstance(pAlgorithm, JcaFactory.BCPROV);

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
     * @throws OceanusException on error
     */
    static String getSymKeyAlgorithm(final GordianSymKeySpec pKeySpec) throws OceanusException {
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
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    /**
     * Obtain the CipherMode algorithm.
     * @param pSpec the cipherSpec
     * @return the Algorithm
     * @throws OceanusException on error
     */
    private static String getCipherModeAlgorithm(final GordianSymCipherSpec pSpec) throws OceanusException {
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
                throw new GordianDataException(GordianCoreFactory.getInvalidText(myMode));
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
     * @throws OceanusException on error
     */
    private static String getStreamKeyAlgorithm(final GordianStreamKeySpec pKeySpec) throws OceanusException {
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
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeySpec));
        }
    }

    /**
     * Is this symKeyType supported by Jca?
     * @param pKeyType the keyType
     * @return true/false
     */
    static boolean supportedSymKeyType(final GordianSymKeyType pKeyType) {
        if (pKeyType == null) {
            return false;
        }
        switch (pKeyType) {
            case SPECK:
            case ANUBIS:
            case SIMON:
            case MARS:
                return false;
            default:
                return true;
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
    protected boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        if (pKeyType == null) {
            return false;
        }
        switch (pKeyType) {
            case ISAAC:
            case SOSEMANUK:
            case RABBIT:
            case SNOW3G:
            case SKEINXOF:
            case BLAKEXOF:
            case KMACXOF:
                return false;
            default:
                return super.validStreamKeyType(pKeyType);
        }
    }

    @Override
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec) {
        /* Check standard features */
        if (!super.validSymCipherSpec(pCipherSpec)) {
            return false;
        }

        /* Additional Checks */
        final GordianCipherMode myMode = pCipherSpec.getCipherMode();
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
