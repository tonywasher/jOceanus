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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreEphemeralAgreement;
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
                checkKeyPair(pServer);

                /* Derive the secret */
                theAgreement.init(null, getRandom());
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pServer);
                final PublicKey myKey = (PublicKey) theAgreement.doPhase(myTarget.getPublicKey(), true);

                /* Create the clientHello */
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKey.getEncoded());
                final byte[] myKeySpecBytes = myKeySpec.getEncoded();
                final byte[] myClientHello = buildClientHello(myKeySpecBytes);
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
                checkKeyPair(pServer);

                /* Obtain keySpec */
                final byte[] myX509bytes = parseClientHello(pClientHello);
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

                /* Derive ephemeral Public key */
                final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
                final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pServer.getKeySpec());
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
                checkKeyPair(pServer);

                /* Establish agreement */
                establishAgreement(pServer);

                /* Create an ephemeral keyPair */
                final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
                final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pServer.getKeySpec());
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
                final byte[] myKeySpecBytes = myKeySpec.getEncoded();
                final byte[] myClientHello = buildClientHello(myKeySpecBytes);

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
                checkKeyPair(pServer);

                /* Establish agreement */
                establishAgreement(pServer);

                /* Obtain keySpec */
                final byte[] myX509bytes = parseClientHello(pClientHello);
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

                /* Derive ephemeral Public key */
                final GordianAsymFactory myAsym = getFactory().getAsymmetricFactory();
                final GordianKeyPairGenerator myGenerator = myAsym.getKeyPairGenerator(pServer.getKeySpec());
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
            if (getAgreementSpec().getAsymKeyType() == GordianAsymKeyType.XDH) {
                final String myBase = pKeyPair.getKeySpec().toString();
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
                checkKeyPair(pClient);
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
                return buildServerHello();

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
            if (getAgreementSpec().getAsymKeyType() == GordianAsymKeyType.XDH) {
                final String myBase = pKeyPair.getKeySpec().toString();
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
                return buildServerHello();

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
            if (getAgreementSpec().getAsymKeyType() == GordianAsymKeyType.XDH) {
                final String myBase = pKeyPair.getKeySpec().toString();
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

                /* Return the serverhello */
                return buildServerHello();

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
                return buildClientConfirm();

            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }
}
