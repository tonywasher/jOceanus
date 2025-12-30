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
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
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
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;

import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.security.auth.DestroyFailedException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
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
    public static class JcaPostQuantumXAgreement
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
        JcaPostQuantumXAgreement(final GordianXCoreAgreementFactory pFactory,
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
    public static class JcaNewHopeXAgreement
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
        JcaNewHopeXAgreement(final GordianXCoreAgreementFactory pFactory,
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
     * Base Agreement Engine class
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
}
