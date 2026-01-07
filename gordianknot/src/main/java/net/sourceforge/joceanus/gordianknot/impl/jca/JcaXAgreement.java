/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseData;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianCryptoException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianPublicKey;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementEngine;
import net.sourceforge.joceanus.gordianknot.impl.core.xagree.GordianXCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.gordianknot.impl.jca.JcaKeyPair.JcaPublicKey;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;

import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.security.auth.DestroyFailedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.PublicKey;

/**
 * Agreement classes.
 */
public final class JcaXAgreement {
    /**
     * Failed agreement message.
     */
    private static final String ERR_AGREEMENT = "Failed Agreement";

    /**
     * Private constructor.
     */
    private JcaXAgreement() {
    }

    /**
     * Jca PostQuantum Agreement.
     */
    public static class JcaPostQuantumXEngine
            extends JcaXAgreementBase {
        /**
         * Key Agreement.
         */
        private final KeyGenerator theGenerator;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @param pGenerator the generator
         */
        JcaPostQuantumXEngine(final GordianXCoreAgreementFactory pFactory,
                              final GordianAgreementSpec pSpec,
                              final KeyGenerator pGenerator) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Store the generator */
            theGenerator = pGenerator;
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Create encapsulation */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
                final KEMGenerateSpec mySpec = new KEMGenerateSpec.Builder(myPublic.getPublicKey(),
                        GordianSymKeyType.AES.toString(), GordianLength.LEN_256.getLength()).withKdfAlgorithm(derivationAlgorithmId()).build();
                theGenerator.init(mySpec, getRandom());
                final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();

                /* Store the encapsulation */
                setEncapsulated(mySecret.getEncapsulation());

                /* Store secret */
                storeSecret(mySecret.getEncoded());
                mySecret.destroy();

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);

            } catch (DestroyFailedException e) {
                throw new GordianIOException("Failed to destroy secret", e);
            }
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Create extractor */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
                final KEMExtractSpec mySpec = new KEMExtractSpec.Builder(myPrivate.getPrivateKey(), getEncapsulated(),
                        GordianSymKeyType.AES.toString(), GordianLength.LEN_256.getLength()).withKdfAlgorithm(derivationAlgorithmId()).build();
                theGenerator.init(mySpec);

                /* Store secret */
                final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();
                storeSecret(mySecret.getEncoded());
                mySecret.destroy();

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);

            } catch (DestroyFailedException e) {
                throw new GordianIOException("Failed to destroy secret", e);
            }
        }
    }

    /**
     * Jca NewHope Agreement.
     */
    public static class JcaNewHopeXEngine
            extends JcaXAgreementBase {
        /**
         * Key Agreement.
         */
        private final KeyAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @param pAgreement the agreement
         */
        JcaNewHopeXEngine(final GordianXCoreAgreementFactory pFactory,
                          final GordianAgreementSpec pSpec,
                          final KeyAgreement pAgreement) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Derive the secret */
                theAgreement.init(null, getRandom());
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(getServerKeyPair());
                final PublicKey myKey = (PublicKey) theAgreement.doPhase(myTarget.getPublicKey(), true);

                /* Store the ephemeral */
                final GordianKeyPairSpec mySpec = getSpec().getKeyPairSpec();
                final JcaPublicKey myPublic = new JcaPublicKey(mySpec, myKey);
                final JcaKeyPair myEphemeral = new JcaKeyPair(myPublic);
                setClientEphemeral(myEphemeral);

                /* Store secret */
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Derive the secret */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
                theAgreement.init(myPrivate.getPrivateKey());
                theAgreement.doPhase(myPublic.getPublicKey(), true);

                /* Store secret */
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Jca Anonymous Agreement.
     */
    public static class JcaAnonXEngine
            extends JcaXAgreementBase {
        /**
         * Key Agreement.
         */
        private KeyAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @param pAgreement the agreement
         */
        JcaAnonXEngine(final GordianXCoreAgreementFactory pFactory,
                       final GordianAgreementSpec pSpec,
                       final KeyAgreement pAgreement) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public void buildClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeral());
                theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

                /* Derive the secret */
                initAgreement(theAgreement, myPrivate);
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
                theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

                /* Derive the secret */
                initAgreement(theAgreement, myPrivate);
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Jca Basic Agreement.
     */
    public static class JcaBasicXEngine
            extends JcaXAgreementBase {
        /**
         * Key Agreement.
         */
        private KeyAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @param pAgreement the agreement
         */
        JcaBasicXEngine(final GordianXCoreAgreementFactory pFactory,
                        final GordianAgreementSpec pSpec,
                        final KeyAgreement pAgreement) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getClientKeyPair());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
                theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

                /* Derive the secret */
                initAgreement(theAgreement, myPrivate);
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientKeyPair());
                theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

                /* Derive the secret */
                initAgreement(theAgreement, myPrivate);
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Jca Unified Agreement.
     */
    public static class JcaUnifiedXEngine
            extends JcaXAgreementBase {
        /**
         * Key Agreement.
         */
        private KeyAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @param pAgreement the agreement
         */
        JcaUnifiedXEngine(final GordianXCoreAgreementFactory pFactory,
                          final GordianAgreementSpec pSpec,
                          final KeyAgreement pAgreement) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myClientPublic = (JcaPublicKey) getPublicKey(getClientKeyPair());
                final JcaPublicKey myClientEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeral());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getServerEphemeral());
                theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

                /* Derive the secret */
                final DHUParameterSpec myParams = new DHUParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), myClientEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams, getRandom());
                theAgreement.doPhase(myClientPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myServerPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
                final JcaPublicKey myServerEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeral());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeral());
                theAgreement = adjustAgreement(theAgreement, getServerKeyPair());

                /* Derive the secret */
                final DHUParameterSpec myParams = new DHUParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), myServerEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);
                theAgreement.doPhase(myServerPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Jca MQV Agreement.
     */
    public static class JcaMQVXEngine
            extends JcaXAgreementBase {
        /**
         * Key Agreement.
         */
        private final KeyAgreement theAgreement;

        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @param pAgreement the agreement
         */
        JcaMQVXEngine(final GordianXCoreAgreementFactory pFactory,
                      final GordianAgreementSpec pSpec,
                      final KeyAgreement pAgreement) throws GordianException {
            /* Initialize underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public void processClientHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myClientPublic = (JcaPublicKey) getPublicKey(getClientKeyPair());
                final JcaPublicKey myClientEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeral());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getServerEphemeral());

                /* Derive the secret */
                final MQVParameterSpec myParams = new MQVParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), myClientEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams, getRandom());
                theAgreement.doPhase(myClientPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException
                     | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void processServerHello() throws GordianException {
            /* Protect against exceptions */
            try {
                /* Access keys */
                final JcaPublicKey myServerPublic = (JcaPublicKey) getPublicKey(getServerKeyPair());
                final JcaPublicKey myServerEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeral());
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeral());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeral());

                /* Derive the secret */
                final MQVParameterSpec myParams = new MQVParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), myServerEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);
                theAgreement.doPhase(myServerPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException
                     | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Base Agreement Engine class.
     */
    public abstract static class JcaXAgreementBase
            extends GordianXCoreAgreementEngine {
        /**
         * Constructor.
         * @param pFactory the security factory
         * @param pSpec the agreementSpec
         * @throws GordianException on error
         */
        JcaXAgreementBase(final GordianXCoreAgreementFactory pFactory,
                          final GordianAgreementSpec pSpec) throws GordianException {
            /* Invoke underlying constructor */
            super(pFactory, pSpec);

            /* Enable derivation for NewHope */
            if (GordianKeyPairType.NEWHOPE.equals(pSpec.getKeyPairSpec().getKeyPairType())) {
                enableDerivation();
            }
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
         * @param pAgreement the agreement
         * @param pPrivate the private key
         */
        void initAgreement(final KeyAgreement pAgreement,
                           final JcaPrivateKey pPrivate) throws GordianException {
            /* Protect against exceptions */
            try {
                if (getSpec().getKDFType() == GordianKDFType.NONE) {
                    pAgreement.init(pPrivate.getPrivateKey(), getRandom());
                } else {
                    pAgreement.init(pPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }
            } catch (InvalidKeyException
                     | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        /**
         * adjust agreement if necessary.
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
                final String myName = getFullAgreementName(myBase + "U", getSpec());
                return getJavaKeyAgreement(myName, false);
            }

            /* Just return the current agreement */
            return pCurrent;
        }

        /**
         * Obtain the required derivation id.
         * @return the derivation id
         */
        AlgorithmIdentifier derivationAlgorithmId() {
            final GordianAgreementSpec mySpec = getSpec();
            switch (mySpec.getKDFType()) {
                case SHA256KDF:     return new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf2, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
                case SHA512KDF:     return new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf2, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
                case SHA256CKDF:    return new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256));
                case SHA512CKDF:    return new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512));
                case NONE:
                default:            return null;
            }
        }
    }

    /**
     * Create the BouncyCastle KeyGenerator via JCA.
     * @param pSpec the KeySpec
     * @return the KeyFactory
     * @throws GordianException on error
     */
    static KeyGenerator getJavaKeyGenerator(final GordianKeyPairSpec pSpec) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Determine the algorithm name */
            String myName = pSpec.getKeyPairType().toString();
            switch (pSpec.getKeyPairType()) {
                case NTRUPRIME:
                    final GordianNTRUPrimeSpec myNTRUSpec = pSpec.getNTRUPrimeKeySpec();
                    myName = myNTRUSpec.getType() + "PRIME";
                    break;
                case MLKEM:
                    myName = "ML-KEM";
                    break;
                default:
                    break;
            }

            /* Determine source of keyGenerator */
            final Provider myProvider = pSpec.getKeyPairType().isStandardJca() ? JcaProvider.BCPROV : JcaProvider.BCPQPROV;

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
     * @param pAlgorithm the Algorithm
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
     * @param pBase the base agreement
     * @param pAgreementSpec the agreementSpec
     * @return the full agreement name
     * @throws GordianException on error
     */
    static String getFullAgreementName(final String pBase,
                                       final GordianAgreementSpec pAgreementSpec) throws GordianException {
        switch (pAgreementSpec.getKDFType()) {
            case NONE:
                return pBase;
            case SHA256KDF:
                return pBase + "withSHA256KDF";
            case SHA512KDF:
                return pBase + "withSHA512KDF";
            case SHA256CKDF:
                return pBase + "withSHA256CKDF";
            case SHA512CKDF:
                return pBase + "withSHA512CKDF";
            default:
                throw new GordianDataException(GordianBaseData.getInvalidText(pAgreementSpec));
        }
    }
}
