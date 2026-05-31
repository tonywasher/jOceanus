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
package io.github.tonywasher.joceanus.gordianknot.impl.jca.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseData;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreNTRUPrimeSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.base.JcaProvider;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair;
import io.github.tonywasher.joceanus.gordianknot.impl.jca.keypair.JcaKeyPair.JcaPrivateKey;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;

import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

/**
 * Agreement classes.
 */
public final class JcaAgreement {
    /**
     * Failed agreement message.
     */
    static final String ERR_AGREEMENT = "Failed Agreement";

    /**
     * Private constructor.
     */
    private JcaAgreement() {
    }

    /**
     * Base Agreement Engine class.
     */
    public abstract static class JcaAgreementBase
            extends GordianCoreAgreementEngine {
        /**
         * Empty byteArray.
         */
        static final byte[] EMPTY = new byte[0];

        /**
         * Constructor.
         *
         * @param pFactory the security factory
         * @param pSpec    the agreementSpec
         * @throws GordianException on error
         */
        JcaAgreementBase(final GordianCoreAgreementFactory pFactory,
                         final GordianCoreAgreementSpec pSpec) throws GordianException {
            /* Invoke underlying constructor */
            super(pFactory, pSpec);
        }

        @Override
        protected GordianPublicKey getPublicKey(final GordianKeyPair pKeyPair) throws GordianException {
            /* Validate the keyPair */
            if (!(pKeyPair instanceof JcaKeyPair)) {
                /* Reject keyPair */
                throw new GordianDataException("Invalid KeyPair");
            }

            /* Access private key */
            return super.getPublicKey(pKeyPair);
        }

        @Override
        protected GordianPrivateKey getPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
            /* Validate the keyPair */
            if (!(pKeyPair instanceof JcaKeyPair)) {
                /* Reject keyPair */
                throw new GordianDataException("Invalid KeyPair");
            }

            /* Access private key */
            return super.getPrivateKey(pKeyPair);
        }

        /**
         * Initialise agreement.
         *
         * @param pAgreement the agreement
         * @param pPrivate   the private key
         */
        void initAgreement(final KeyAgreement pAgreement,
                           final JcaPrivateKey pPrivate) throws GordianException {
            /* Protect against exceptions */
            try {
                if (getSpec().getKDFType() == GordianAgreementKDF.NONE) {
                    pAgreement.init(pPrivate.getPrivateKey(), getRandom());
                } else {
                    pAgreement.init(pPrivate.getPrivateKey(), new UserKeyingMaterialSpec(getAdditional()), getRandom());
                }
            } catch (InvalidKeyException
                     | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        /**
         * Obtain the additionalInfo.
         *
         * @return the additionalData
         */
        byte[] getAdditional() {
            final byte[] myAdditional = this.getBuilder().getState().getAdditionalData();
            return myAdditional == null ? EMPTY : myAdditional;
        }

        /**
         * adjust agreement if necessary.
         *
         * @param pCurrent the current agreement
         * @param pKeyPair the keyPair
         * @return the adjusted agreement
         * @throws GordianException on error
         */
        KeyAgreement adjustAgreement(final KeyAgreement pCurrent,
                                     final GordianKeyPair pKeyPair) throws GordianException {
            /* If we need to change agreement based on keySpec */
            if (getSpec().getKeyPairSpec().getKeyPairType().equals(GordianKeyPairType.XDH)) {
                final String myBase = pKeyPair.getKeyPairSpec().toString();
                final String myXtra = GordianAgreementType.UNIFIED.equals(getSpec().getAgreementType())
                        ? "U" : "";
                final String myName = getFullAgreementName(myBase + myXtra, getSpec());
                return getJavaKeyAgreement(myName, false);
            }

            /* Just return the current agreement */
            return pCurrent;
        }

        /**
         * Obtain the required derivation id.
         *
         * @return the derivation id
         */
        AlgorithmIdentifier derivationAlgorithmId() {
            final GordianCoreAgreementSpec mySpec = getSpec();
            return switch (mySpec.getKDFType()) {
                case SHA256KDF ->
                        new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf2, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
                case SHA512KDF ->
                        new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf2, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
                case SHA256CKDF ->
                        new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
                case SHA512CKDF ->
                        new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
                case SHA256HKDF -> new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hkdf_with_sha256, null);
                case SHA512HKDF -> new AlgorithmIdentifier(PKCSObjectIdentifiers.id_alg_hkdf_with_sha512, null);
                case KMAC128 -> new AlgorithmIdentifier(NISTObjectIdentifiers.id_Kmac128, null);
                case KMAC256 -> new AlgorithmIdentifier(NISTObjectIdentifiers.id_Kmac256, null);
                case SHAKE256 -> new AlgorithmIdentifier(NISTObjectIdentifiers.id_shake256, null);
                default -> null;
            };
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator via JCA.
     *
     * @param pSpec the KeySpec
     * @return the KeyFactory
     * @throws GordianException on error
     */
    static KeyGenerator getJavaKeyGenerator(final GordianKeyPairSpec pSpec) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Determine the algorithm name */
            String myName = pSpec.getKeyPairType().toString();
            final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) pSpec;
            switch (pSpec.getKeyPairType()) {
                case NTRUPRIME:
                    final GordianCoreNTRUPrimeSpec myNTRUSpec = mySpec.getNTRUPrimeSpec();
                    myName = myNTRUSpec.getType() + "PRIME";
                    break;
                case MLKEM:
                    myName = "ML-KEM";
                    break;
                default:
                    break;
            }

            /* Determine source of keyGenerator */
            final Provider myProvider = mySpec.getCoreKeyPairType().isStandardJca() ? JcaProvider.BCPROV : JcaProvider.BCPQPROV;

            /* Return a KeyAgreement for the algorithm */
            return KeyGenerator.getInstance(myName, myProvider);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyGenerator", e);
        }
    }

    /**
     * Create the BouncyCastle KeyFactory via JCA.
     *
     * @param pAlgorithm  the Algorithm
     * @param postQuantum is this a postQuantum algorithm?
     * @return the KeyFactory
     * @throws GordianException on error
     */
    static KeyAgreement getJavaKeyAgreement(final String pAlgorithm,
                                            final boolean postQuantum) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Return a KeyAgreement for the algorithm */
            return KeyAgreement.getInstance(pAlgorithm, postQuantum
                    ? JcaProvider.BCPQPROV
                    : JcaProvider.BCPROV);

            /* Catch exceptions */
        } catch (NoSuchAlgorithmException e) {
            /* Throw the exception */
            throw new GordianCryptoException("Failed to create KeyAgreement", e);
        }
    }

    /**
     * Obtain the agreement name.
     *
     * @param pBase          the base agreement
     * @param pAgreementSpec the agreementSpec
     * @return the full agreement name
     * @throws GordianException on error
     */
    static String getFullAgreementName(final String pBase,
                                       final GordianCoreAgreementSpec pAgreementSpec) throws GordianException {
        return switch (pAgreementSpec.getKDFType()) {
            case NONE -> pBase;
            case SHA256KDF -> pBase + "withSHA256KDF";
            case SHA512KDF -> pBase + "withSHA512KDF";
            case SHA256CKDF -> pBase + "withSHA256CKDF";
            case SHA512CKDF -> pBase + "withSHA512CKDF";
            case SHA256HKDF -> pBase + "withSHA256HKDF";
            case SHA512HKDF -> pBase + "withSHA512HKDF";
            default -> throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        };
    }
}
