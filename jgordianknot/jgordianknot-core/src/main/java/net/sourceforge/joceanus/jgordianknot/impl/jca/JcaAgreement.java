/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;

import org.bouncycastle.jcajce.SecretKeyWithEncapsulation;
import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.KEMExtractSpec;
import org.bouncycastle.jcajce.spec.KEMGenerateSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianAgreementMessageASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreSignedAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.impl.jca.JcaKeyPair.JcaPublicKey;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Agreement classes.
 */
public final class JcaAgreement {
    /**
     * Failed agreement message.
     */
    private static final String ERR_AGREEMENT = "Failed Agreement";

    /**
     * Private constructor.
     */
    private JcaAgreement() {
    }

    /**
     * Jca Encapsulation Agreement.
     */
    public static class JcaEncapsulationAgreement
            extends GordianCoreAnonymousAgreement {
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
        JcaEncapsulationAgreement(final JcaFactory pFactory,
                                  final GordianAgreementSpec pSpec,
                                  final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
            enableDerivation();
        }

        @Override
        public byte[] createClientHello(final GordianKeyPair pServer) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPairs */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Derive the secret */
                theAgreement.init(null, getRandom());
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pServer);
                final PublicKey myKey = (PublicKey) theAgreement.doPhase(myTarget.getPublicKey(), true);

                /* Create the clientHello */
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKey.getEncoded());
                final byte[] myClientHello = buildClientHello(myKeySpec);
                storeSecret(theAgreement.generateSecret());
                return myClientHello;

            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void acceptClientHello(final GordianKeyPair pServer,
                                      final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Obtain keySpec */
                final GordianAgreementMessageASN1 myHello = parseClientHello(pClientHello);
                final X509EncodedKeySpec myKeySpec = myHello.getEphemeral();

                /* Derive ephemeral Public key */
                final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());
                final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(myPair);

                /* Derive the secret */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pServer);
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
     * Jca PostQuantum Agreement.
     */
    public static class JcaPostQuantumAgreement
            extends GordianCoreAnonymousAgreement {
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
        JcaPostQuantumAgreement(final JcaFactory pFactory,
                                final GordianAgreementSpec pSpec,
                                final KeyGenerator pGenerator) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the generator */
            theGenerator = pGenerator;
        }

        @Override
        public byte[] createClientHello(final GordianKeyPair pServer) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPairs */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Derive the secret */
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pServer);
                theGenerator.init(new KEMGenerateSpec(myTarget.getPublicKey(), GordianSymKeyType.AES.toString()), getRandom());
                final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();

                /* Create the clientHello */
                final byte[] myClientHello = buildClientHello(mySecret.getEncapsulation());
                storeSecret(mySecret.getEncoded());
                return myClientHello;

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void acceptClientHello(final GordianKeyPair pServer,
                                      final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Parse client hello */
                final GordianAgreementMessageASN1 myHello = parseClientHello(pClientHello);

                /* Derive the secret */
                final JcaPrivateKey myTarget = (JcaPrivateKey) getPrivateKey(pServer);
                theGenerator.init(new KEMExtractSpec(myTarget.getPrivateKey(), myHello.getEncapsulated(), GordianSymKeyType.AES.toString()));
                final SecretKeyWithEncapsulation mySecret = (SecretKeyWithEncapsulation) theGenerator.generateKey();

                /* Store secret */
                storeSecret(mySecret.getEncoded());

            } catch (InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Jca Anonymous Agreement.
     */
    public static class JcaAnonymousAgreement
            extends GordianCoreAnonymousAgreement {
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
        JcaAnonymousAgreement(final JcaFactory pFactory,
                              final GordianAgreementSpec pSpec,
                              final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] createClientHello(final GordianKeyPair pServer) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPairs */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Establish agreement */
                establishAgreement(pServer);

                /* Create an ephemeral keyPair */
                final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());
                final GordianKeyPair myPair = myGenerator.generateKeyPair();

                /* Initialise the agreement taking care in case of null parameters */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(myPair);
                if (getAgreementSpec().getKDFType() == GordianKDFType.NONE) {
                    theAgreement.init(myPrivate.getPrivateKey(), getRandom());
                } else {
                    theAgreement.init(myPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }

                /* Create the clientHello */
                final X509EncodedKeySpec myKeySpec = myGenerator.getX509Encoding(myPair);
                final byte[] myClientHello = buildClientHello(myKeySpec);

                /* Derive the secret */
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pServer);
                theAgreement.doPhase(myTarget.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());
                return myClientHello;


            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void acceptClientHello(final GordianKeyPair pServer,
                                      final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Establish agreement */
                establishAgreement(pServer);

                /* Obtain keySpec */
                final GordianAgreementMessageASN1 myHello = parseClientHello(pClientHello);
                final X509EncodedKeySpec myKeySpec = myHello.getEphemeral();

                /* Derive ephemeral Public key */
                final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pServer.getKeyPairSpec());
                final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);

                /* Initialise the agreement taking care in case of null parameters */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pServer);
                if (getAgreementSpec().getKDFType() == GordianKDFType.NONE) {
                    theAgreement.init(myPrivate.getPrivateKey(), getRandom());
                } else {
                    theAgreement.init(myPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }

                /* Derive and store the secret */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(myPair);
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        /**
         * Establish the agreement.
         * @param pKeyPair the keyPair
         * @throws OceanusException on error
         */
        private void establishAgreement(final GordianKeyPair pKeyPair) throws OceanusException {
            if (getAgreementSpec().getKeyPairSpec().getKeyPairType().equals(GordianKeyPairType.XDH)) {
                final String myBase = pKeyPair.getKeyPairSpec().toString();
                final String myName = JcaAgreementFactory.getFullAgreementName(myBase, getAgreementSpec());
                theAgreement = JcaAgreementFactory.getJavaKeyAgreement(myName, false);
            }
        }
    }

    /**
     * Jca Basic Agreement.
     */
    public static class JcaBasicAgreement
            extends GordianCoreBasicAgreement {
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
        JcaBasicAgreement(final JcaFactory pFactory,
                          final GordianAgreementSpec pSpec,
                          final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pClient,
                                        final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                JcaKeyPair.checkKeyPair(pClient);
                checkKeyPair(pClient);
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Establish agreement */
                establishAgreement(pClient);

                /* Process the clientHello */
                processClientHello(pServer, pClientHello);
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pServer);

                /* Initialise the agreement taking care in case of null parameters */
                if (getAgreementSpec().getKDFType() == GordianKDFType.NONE) {
                    theAgreement.init(myPrivate.getPrivateKey(), getRandom());
                } else {
                    theAgreement.init(myPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }

                /* Derive the secret */
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(pClient);
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return the serverHello */
                return buildServerHello().getEncodedBytes();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public byte[] acceptServerHello(final GordianKeyPair pServer,
                                        final byte[] pServerHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                JcaKeyPair.checkKeyPair(pServer);
                checkKeyPair(pServer);

                /* Establish agreement */
                establishAgreement(pServer);

                /* process the serverHello */
                processServerHello(pServerHello);
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientKeyPair());

                /* Initialise the agreement taking care in case of null parameters */
                if (getAgreementSpec().getKDFType() == GordianKDFType.NONE) {
                    theAgreement.init(myPrivate.getPrivateKey(), getRandom());
                } else {
                    theAgreement.init(myPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }

                /* Calculate agreement */
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pServer);
                theAgreement.doPhase(myTarget.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return confirmation if needed */
                return buildClientConfirm();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        /**
         * Establish the agreement.
         * @param pKeyPair the keyPair
         * @throws OceanusException on error
         */
        private void establishAgreement(final GordianKeyPair pKeyPair) throws OceanusException {
            if (getAgreementSpec().getKeyPairSpec().getKeyPairType().equals(GordianKeyPairType.XDH)) {
                final String myBase = pKeyPair.getKeyPairSpec().toString();
                final String myName = JcaAgreementFactory.getFullAgreementName(myBase, getAgreementSpec());
                theAgreement = JcaAgreementFactory.getJavaKeyAgreement(myName, false);
            }
        }
    }

    /**
     * Jca Signed Agreement.
     */
    public static class JcaSignedAgreement
            extends GordianCoreSignedAgreement {
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
        JcaSignedAgreement(final JcaFactory pFactory,
                           final GordianAgreementSpec pSpec,
                           final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Process the clientHello */
                JcaKeyPair.checkKeyPair(pServer);
                processClientHello(pClientHello);
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getServerEphemeralKeyPair());
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(getClientEphemeralKeyPair());

                /* Establish agreement */
                establishAgreement(getServerEphemeralKeyPair());

                /* Initialise the agreement taking care in case of null parameters */
                if (getAgreementSpec().getKDFType() == GordianKDFType.NONE) {
                    theAgreement.init(myPrivate.getPrivateKey(), getRandom());
                } else {
                    theAgreement.init(myPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }

                /* Derive the secret */
                theAgreement.doPhase(myPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return the serverHello */
                return buildServerHello(pServer);

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void acceptServerHello(final GordianKeyPair pServer,
                                      final byte[] pServerHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* process the serverHello */
                JcaKeyPair.checkKeyPair(pServer);
                processServerHello(pServer, pServerHello);
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeralKeyPair());
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(getServerEphemeralKeyPair());

                /* Establish agreement */
                establishAgreement(getClientEphemeralKeyPair());

                /* Initialise the agreement taking care in case of null parameters */
                if (getAgreementSpec().getKDFType() == GordianKDFType.NONE) {
                    theAgreement.init(myPrivate.getPrivateKey(), getRandom());
                } else {
                    theAgreement.init(myPrivate.getPrivateKey(), new UserKeyingMaterialSpec(new byte[0]), getRandom());
                }

                /* Calculate agreement */
                theAgreement.doPhase(myTarget.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        /**
         * Establish the agreement.
         * @param pKeyPair the keyPair
         * @throws OceanusException on error
         */
        private void establishAgreement(final GordianKeyPair pKeyPair) throws OceanusException {
            if (getAgreementSpec().getKeyPairSpec().getKeyPairType().equals(GordianKeyPairType.XDH)) {
                final String myBase = pKeyPair.getKeyPairSpec().toString();
                final String myName = JcaAgreementFactory.getFullAgreementName(myBase, getAgreementSpec());
                theAgreement = JcaAgreementFactory.getJavaKeyAgreement(myName, false);
            }
        }
    }

    /**
     * Jca Unified Agreement.
     */
    public static class JcaUnifiedAgreement
            extends GordianCoreEphemeralAgreement {
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
        JcaUnifiedAgreement(final JcaFactory pFactory,
                            final GordianAgreementSpec pSpec,
                            final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pClient,
                                        final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Establish agreement */
                JcaKeyPair.checkKeyPair(pClient);
                JcaKeyPair.checkKeyPair(pServer);
                establishAgreement(pServer);

                /* process clientHello */
                processClientHello(pClient, pServer, pClientHello);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pServer);
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getServerEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeralKeyPair());
                final DHUParameterSpec myParams = new DHUParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams, getRandom());

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pClient);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return the serverHello */
                return buildServerHello().getEncodedBytes();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public byte[] acceptServerHello(final GordianKeyPair pServer,
                                        final byte[] pServerHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Establish agreement */
                JcaKeyPair.checkKeyPair(pServer);
                establishAgreement(pServer);

                /* parse the serverHello */
                processServerHello(pServer, pServerHello);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientKeyPair());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeralKeyPair());
                final DHUParameterSpec myParams = new DHUParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pServer);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);

                /* Store secret */
                storeSecret(theAgreement.generateSecret());

                /* Return confirmation if needed */
                final GordianAgreementMessageASN1 myConfirm = buildClientConfirm();
                return myConfirm == null ? null : myConfirm.getEncodedBytes();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        /**
         * Establish the agreement.
         * @param pKeyPair the keyPair
         * @throws OceanusException on error
         */
        private void establishAgreement(final GordianKeyPair pKeyPair) throws OceanusException {
            if (getAgreementSpec().getKeyPairSpec().getKeyPairType().equals(GordianKeyPairType.XDH)) {
                final String myBase = pKeyPair.getKeyPairSpec().toString();
                final String myName = JcaAgreementFactory.getFullAgreementName(myBase + "U", getAgreementSpec());
                theAgreement = JcaAgreementFactory.getJavaKeyAgreement(myName, false);
            }
        }
    }

    /**
     * Jca MQV Agreement.
     */
    public static class JcaMQVAgreement
            extends GordianCoreEphemeralAgreement {
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
        JcaMQVAgreement(final JcaFactory pFactory,
                        final GordianAgreementSpec pSpec,
                        final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] acceptClientHello(final GordianKeyPair pClient,
                                        final GordianKeyPair pServer,
                                        final byte[] pClientHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* process clientHello */
                JcaKeyPair.checkKeyPair(pClient);
                JcaKeyPair.checkKeyPair(pServer);
                processClientHello(pClient, pServer, pClientHello);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pServer);
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getServerEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeralKeyPair());
                final MQVParameterSpec myParams = new MQVParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pClient);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return the serverHello */
                return buildServerHello().getEncodedBytes();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public byte[] acceptServerHello(final GordianKeyPair pServer,
                                        final byte[] pServerHello) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* process the serverHello */
                JcaKeyPair.checkKeyPair(pServer);
                processServerHello(pServer, pServerHello);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getClientKeyPair());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getClientEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getClientEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getServerEphemeralKeyPair());
                final MQVParameterSpec myParams = new MQVParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pServer);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);

                /* Store secret */
                storeSecret(theAgreement.generateSecret());

                /* Return confirmation if needed */
                final GordianAgreementMessageASN1 myConfirm = buildClientConfirm();
                return myConfirm == null ? null : myConfirm.getEncodedBytes();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }
}
