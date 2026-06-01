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
package io.github.tonywasher.joceanus.gordianknot.impl.jca.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignParams;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.sign.GordianCoreSignature;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.base.JcaProvider;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;
import org.bouncycastle.jcajce.spec.ContextParameterSpec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;

/**
 * Jca implementation of signature.
 */
public abstract class JcaSignature
        extends GordianCoreSignature {
    /**
     * The Signature error.
     */
    private static final String SIG_ERROR = "Signature error";

    /**
     * The RSA PSS MGF1 Algorithm.
     */
    private static final String RSA_PSSMGF1_ALGOBASE = "withRSAandMGF1";

    /**
     * The RSA PSS SHAKE128 Algorithm.
     */
    private static final String RSA_PSS128_ALGOBASE = "withRSAandSHAKE128";

    /**
     * The RSA PSS SHAKE256 Algorithm.
     */
    private static final String RSA_PSS256_ALGOBASE = "withRSAandSHAKE256";

    /**
     * The RSA PSS PureSHAKE Algorithm.
     */
    private static final String RSA_PSSSHAKE_ALGOBASE = "withRSA/PSS";

    /**
     * The RSA X9.31 Algorithm.
     */
    private static final String RSA_X931_ALGOBASE = "withRSA/X9.31";

    /**
     * The RSA ISO9796d2 Algorithm.
     */
    private static final String RSA_ISO9796D2_ALGOBASE = "withRSA/ISO9796-2";

    /**
     * The RSA preHash Algorithm.
     */
    private static final String RSA_PREHASH_ALGOBASE = "withRSAEncryption";

    /**
     * The ECDSA Signature.
     */
    private static final String EC_DSA_ALGOBASE = "withECDSA";

    /**
     * The ECDDSA Signature.
     */
    private static final String EC_DDSA_ALGOBASE = "withECDDSA";

    /**
     * The DSA Signature.
     */
    private static final String DSA_ALGOBASE = "withDSA";

    /**
     * The DDSA Signature.
     */
    private static final String DDSA_ALGOBASE = "withDDSA";

    /**
     * The ECNR Signature.
     */
    private static final String EC_NR_ALGOBASE = "withECNR";

    /**
     * The SM2 Signature.
     */
    private static final String EC_SM2_ALGOBASE = "WITHSM2";

    /**
     * The PQC Hash prefix.
     */
    static final String PQC_HASH_PFX = "HASH-";

    /**
     * The RSA Signer.
     */
    private Signature theSigner;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     * @param pSpec    the signature Spec
     */
    JcaSignature(final GordianBaseFactory pFactory,
                 final GordianSignatureSpec pSpec) {
        super(pFactory, pSpec);
    }

    /**
     * Set the signer.
     *
     * @param pSigner the signer.
     */
    protected void setSigner(final Signature pSigner) {
        theSigner = pSigner;
    }

    /**
     * Obtain the signer.
     *
     * @return the signer.
     */
    protected Signature getSigner() {
        return theSigner;
    }

    @Override
    public void initForSigning(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForSigning(pParams);
        final JcaKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        JcaKeyPair.checkKeyPair(myPair);

        /* Initialise for signing */
        try {
            /* Determine whether we should use random for signatures */
            final GordianCoreKeyPairType myType = GordianCoreKeyPairType.mapCoreType(getSignatureSpec().getKeyPairType());
            final boolean useRandom = myType.useRandomForSignatures();

            /* Initialise the signing */
            if (useRandom) {
                getSigner().initSign(myPair.getPrivateKey().getPrivateKey(), getRandom());
            } else {
                getSigner().initSign(myPair.getPrivateKey().getPrivateKey());
            }

            /* If we support context */
            if (getSignatureSpec().supportsContext()) {
                /* Declare the context to the signer */
                final ContextParameterSpec mySpec = myContext == null ? null : new ContextParameterSpec(myContext);
                getSigner().setParameter(mySpec);
            }

            /* Catch exceptions */
        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void initForVerify(final GordianSignParams pParams) throws GordianException {
        /* Initialise detail */
        super.initForVerify(pParams);
        final JcaKeyPair myPair = getKeyPair();
        final byte[] myContext = getContext();
        JcaKeyPair.checkKeyPair(myPair);

        /* Initialise for signing */
        try {
            /* Initialise for verification */
            getSigner().initVerify(myPair.getPublicKey().getPublicKey());

            /* If we support context */
            if (getSignatureSpec().supportsContext()) {
                /* Declare the context to the signer */
                final ContextParameterSpec mySpec = myContext == null ? null : new ContextParameterSpec(myContext);
                getSigner().setParameter(mySpec);
            }

            /* Catch exceptions */
        } catch (InvalidKeyException
                 | InvalidAlgorithmParameterException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void update(final byte[] pBytes,
                       final int pOffset,
                       final int pLength) {
        try {
            theSigner.update(pBytes, pOffset, pLength);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public void update(final byte pByte) {
        try {
            theSigner.update(pByte);
        } catch (SignatureException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public byte[] sign() throws GordianException {
        /* Check that we are in signing mode */
        checkMode(GordianSignatureMode.SIGN);

        /* Protect against exception */
        try {
            return getSigner().sign();

        } catch (SignatureException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public boolean verify(final byte[] pSignature) throws GordianException {
        /* Check that we are in verify mode */
        checkMode(GordianSignatureMode.VERIFY);

        /* Protect against exception */
        try {
            return getSigner().verify(pSignature);
        } catch (SignatureException e) {
            throw new GordianCryptoException(SIG_ERROR, e);
        }
    }

    @Override
    public void reset() {
        /* NoOp */
    }

    @Override
    protected JcaKeyPair getKeyPair() {
        return (JcaKeyPair) super.getKeyPair();
    }

    /**
     * Obtain Signer base.
     *
     * @param pSignatureSpec the signatureSpec
     * @return the base
     */
    static String getSignatureBase(final GordianSignatureSpec pSignatureSpec) {
        /* Handle SM2 explicitly */
        if (GordianKeyPairType.SM2.equals(pSignatureSpec.getKeyPairType())) {
            return EC_SM2_ALGOBASE;
        }

        /* Note if we are DSA */
        final GordianCoreSignatureSpec mySpec = (GordianCoreSignatureSpec) pSignatureSpec;
        final boolean isDSA = GordianKeyPairType.DSA.equals(pSignatureSpec.getKeyPairType());
        final boolean isSHAKE = GordianDigestType.SHAKE.equals(mySpec.getDigestSpec().getDigestType());

        /* Switch on signature type */
        return switch (pSignatureSpec.getSignatureType()) {
            case PSSMGF1 -> RSA_PSSMGF1_ALGOBASE;
            case PSS128 -> isSHAKE ? RSA_PSSSHAKE_ALGOBASE : RSA_PSS128_ALGOBASE;
            case PSS256 -> isSHAKE ? RSA_PSSSHAKE_ALGOBASE : RSA_PSS256_ALGOBASE;
            case X931 -> RSA_X931_ALGOBASE;
            case ISO9796D2 -> RSA_ISO9796D2_ALGOBASE;
            case PREHASH -> RSA_PREHASH_ALGOBASE;
            case DSA -> isDSA
                    ? DSA_ALGOBASE
                    : EC_DSA_ALGOBASE;
            case DDSA -> isDSA
                    ? DDSA_ALGOBASE
                    : EC_DDSA_ALGOBASE;
            case NR -> EC_NR_ALGOBASE;
            default -> null;
        };
    }

    /**
     * Create the BouncyCastle Signature via JCA.
     *
     * @param pAlgorithm  the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyPairGenerator
     * @throws GordianException on error
     */
    static Signature getJavaSignature(final String pAlgorithm,
                                      final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a Signature for the algorithm */
            return Signature.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create Signature", e);
        }
    }
}
