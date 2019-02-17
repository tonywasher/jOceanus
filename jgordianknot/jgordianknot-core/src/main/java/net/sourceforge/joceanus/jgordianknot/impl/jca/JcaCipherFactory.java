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
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
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
    public JcaCipher<GordianSymKeySpec> createSymKeyCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKeySpec */
        checkSymCipherSpec(pCipherSpec, false);

        /* Create the cipher */
        final Cipher myBCCipher = getJavaCipher(pCipherSpec);
        return new JcaCipher<>(getFactory(), pCipherSpec, myBCCipher);
    }

    @Override
    public JcaAADCipher createAADCipher(final GordianSymCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of SymKeySpec */
        checkSymCipherSpec(pCipherSpec, true);

        /* Create the cipher */
        final Cipher myBCCipher = getJavaCipher(pCipherSpec);
        return new JcaAADCipher(getFactory(), pCipherSpec, myBCCipher);
    }

    @Override
    public JcaCipher<GordianStreamKeyType> createStreamKeyCipher(final GordianStreamCipherSpec pCipherSpec) throws OceanusException {
        /* Check validity of StreamKeySpec */
        checkStreamCipherSpec(pCipherSpec);

        /* Create the cipher */
        final Cipher myJavaCipher = getJavaCipher(pCipherSpec.getKeyType());
        return new JcaCipher<>(getFactory(), pCipherSpec, myJavaCipher);
    }

    @Override
    public GordianWrapper createKeyWrapper(final GordianSymKeySpec pKeySpec) throws OceanusException {
        /* Check validity of SymKey */
        checkSymKeySpec(pKeySpec);

        /* Create the cipher */
        final GordianSymCipherSpec mySpec = GordianSymCipherSpec.ecb(pKeySpec, GordianPadding.NONE);
        final JcaCipher<GordianSymKeySpec> myJcaCipher = createSymKeyCipher(mySpec);
        return createKeyWrapper(myJcaCipher);
    }

    /**
     * Obtain the algorithm for the keySpec.
     * @param pKeySpec the keySpec
     * @param <T> the SpecType
     * @return the name of the algorithm
     * @throws OceanusException on error
     */
    private <T extends GordianKeySpec> String getKeyAlgorithm(final T pKeySpec) throws OceanusException {
        if (pKeySpec instanceof GordianStreamKeyType) {
            return getStreamKeyAlgorithm((GordianStreamKeyType) pKeySpec);
        }
        if (pKeySpec instanceof GordianSymKeySpec) {
            return getSymKeyAlgorithm((GordianSymKeySpec) pKeySpec);
        }
        throw new GordianDataException(JcaFactory.getInvalidText(pKeySpec));
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
     * @param pKeyType the StreamKeyType
     * @return the Cipher
     * @throws OceanusException on error
     */
    private Cipher getJavaCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        return getJavaCipher(getStreamKeyAlgorithm(pKeyType));
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
                return myMode.name();
            case CBC:
            case G3413CBC:
                return GordianCipherMode.CBC.name();
            case CFB:
            case G3413CFB:
                return "CFB8";
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
     * @param pKeyType the keyType
     * @return the Algorithm
     * @throws OceanusException on error
     */
    String getStreamKeyAlgorithm(final GordianStreamKeyType pKeyType) throws OceanusException {
        switch (pKeyType) {
            case HC:
                return getFactory().isRestricted()
                       ? "HC128"
                       : "HC256";
            case CHACHA:
                return getFactory().isRestricted()
                       ? pKeyType.name()
                       : "CHACHA7539";
            case VMPC:
                return "VMPC-KSA3";
            case GRAIN:
                return "Grain128";
            case SALSA20:
            case XSALSA20:
            case ISAAC:
            case RC4:
                return pKeyType.name();
            default:
                throw new GordianDataException(GordianCoreFactory.getInvalidText(pKeyType));
        }
    }

    @Override
    public boolean validSymKeyType(final GordianSymKeyType pKeyType) {
        return supportedSymKeyType(pKeyType)
                && super.validSymKeyType(pKeyType);
    }

    /**
     * Is this symKeyType supported by Jca?
     * @param pKeyType the keyType
     * @return true/false
     */
    private static boolean supportedSymKeyType(final GordianSymKeyType pKeyType) {
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
    protected boolean validStreamKeyType(final GordianStreamKeyType pKeyType) {
        switch (pKeyType) {
            case ISAAC:
            case SOSEMANUK:
            case XCHACHA20:
            case RABBIT:
            case SNOW3G:
            case ZUC:
                return false;
            default:
                return super.validStreamKeyType(pKeyType);
        }
    }

    @Override
    protected boolean validSymCipherSpec(final GordianSymCipherSpec pCipherSpec,
                                         final Boolean isAAD) {
        /* Check standard features */
        if (!super.validSymCipherSpec(pCipherSpec, isAAD)) {
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
                        && !GordianCipherMode.G3413CTR.equals(myMode)
                        && !GordianCipherMode.CFB.equals(myMode)
                        && !GordianCipherMode.CBC.equals(myMode);
            default:
                return true;
        }
    }

    /**
     * Determine maximum cipherSteps.
     * @param pRestricted are keys restricted?
     * @return the maximum
     */
    public static int getMaximumCipherSteps(final boolean pRestricted) {
        /* Count valid values */
        int myCount = 0;
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (GordianCoreCipherFactory.validStdBlockSymKeyTypeForRestriction(myType, pRestricted)
                    && supportedSymKeyType(myType)) {
                myCount++;
            }
        }

        /* Maximum is 1 less than the count */
        return myCount - 1;
    }
}
