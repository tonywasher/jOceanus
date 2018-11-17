/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto.jca;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;

import org.bouncycastle.jcajce.spec.DHUParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;

import net.sourceforge.joceanus.jgordianknot.GordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianBasicAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEncapsulationAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreement.GordianEphemeralAgreement;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPrivateKey;
import net.sourceforge.joceanus.jgordianknot.crypto.jca.JcaKeyPair.JcaPublicKey;
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
            extends GordianEncapsulationAgreement {
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
        public byte[] initiateAgreement(final GordianKeyPair pTarget) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPairs */
                checkKeyPair(pTarget);

                /* Derive the secret */
                theAgreement.init(null);
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pTarget);
                final PublicKey myKey = (PublicKey) theAgreement.doPhase(myTarget.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Create the message  */
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myKey.getEncoded());
                final byte[] myKeySpecBytes = myKeySpec.getEncoded();
                return createMessage(myKeySpecBytes);
            } catch (InvalidKeyException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                checkKeyPair(pSelf);

                /* Obtain keySpec */
                final byte[] myX509bytes = parseMessage(pMessage);
                final X509EncodedKeySpec myKeySpec = new X509EncodedKeySpec(myX509bytes);

                /* Derive ephemeral Public key */
                final GordianKeyPairGenerator myGenerator = getFactory().getKeyPairGenerator(pSelf.getKeySpec());
                final GordianKeyPair myPair = myGenerator.derivePublicOnlyKeyPair(myKeySpec);
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(myPair);

                /* Derive the secret */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pSelf);
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
     * Jca Basic Agreement.
     */
    public static class JcaBasicAgreement
            extends GordianBasicAgreement {
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
        JcaBasicAgreement(final JcaFactory pFactory,
                          final GordianAgreementSpec pSpec,
                          final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] initiateAgreement(final GordianKeyPair pSource,
                                        final GordianKeyPair pTarget) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPairs */
                checkKeyPair(pSource);
                checkKeyPair(pTarget);

                /* Derive the secret */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pSource);
                final UserKeyingMaterialSpec myParams = getAgreementSpec().getKDFType() == GordianKDFType.NONE
                                                        ? null
                                                        : new UserKeyingMaterialSpec(new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);
                final JcaPublicKey myTarget = (JcaPublicKey) getPublicKey(pTarget);
                theAgreement.doPhase(myTarget.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Create the message  */
                return createMessage();
            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void acceptAgreement(final GordianKeyPair pSource,
                                    final GordianKeyPair pSelf,
                                    final byte[] pMessage) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                checkKeyPair(pSource);
                checkKeyPair(pSelf);

                /* Determine initVector */
                parseMessage(pMessage);
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pSelf);
                final JcaPublicKey myPublic = (JcaPublicKey) getPublicKey(pSource);

                /* Derive the secret */
                final UserKeyingMaterialSpec myParams = getAgreementSpec().getKDFType() == GordianKDFType.NONE
                                                        ? null
                                                        : new UserKeyingMaterialSpec(new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);
                theAgreement.doPhase(myPublic.getPublicKey(), true);

                /* Store secret */
                storeSecret(theAgreement.generateSecret());
            } catch (InvalidKeyException
                     | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }

    /**
     * Jca Unified Agreement.
     */
    public static class JcaUnifiedAgreement
            extends GordianEphemeralAgreement {
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
        JcaUnifiedAgreement(final JcaFactory pFactory,
                            final GordianAgreementSpec pSpec,
                            final KeyAgreement pAgreement) {
            /* Initialise underlying class */
            super(pFactory, pSpec);

            /* Store the agreement */
            theAgreement = pAgreement;
        }

        @Override
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* process message */
                final byte[] myResponse = parseMessage(pResponder, pMessage);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pResponder);
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
                final DHUParameterSpec myParams = new DHUParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pSource);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return the response */
                return myResponse;
            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void confirmAgreement(final GordianKeyPair pResponder,
                                     final byte[] pMessage) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                checkKeyPair(pResponder);

                /* parse the ephemeral message */
                parseEphemeral(pMessage);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getOwnerKeyPair());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
                final DHUParameterSpec myParams = new DHUParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pResponder);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);

                /* Store secret */
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
    public static class JcaMQVAgreement
            extends GordianEphemeralAgreement {
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
        public byte[] acceptAgreement(final GordianKeyPair pSource,
                                      final GordianKeyPair pResponder,
                                      final byte[] pMessage) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* process message */
                final byte[] myResponse = parseMessage(pResponder, pMessage);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(pResponder);
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
                final MQVParameterSpec myParams = new MQVParameterSpec(myEphPublic.getPublicKey(),
                        myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pSource);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);
                storeSecret(theAgreement.generateSecret());

                /* Return the response */
                return myResponse;
            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }

        @Override
        public void confirmAgreement(final GordianKeyPair pResponder,
                                     final byte[] pMessage) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Check keyPair */
                checkKeyPair(pResponder);

                /* parse the ephemeral message */
                parseEphemeral(pMessage);

                /* Initialise agreement */
                final JcaPrivateKey myPrivate = (JcaPrivateKey) getPrivateKey(getOwnerKeyPair());
                final JcaPrivateKey myEphPrivate = (JcaPrivateKey) getPrivateKey(getEphemeralKeyPair());
                final JcaPublicKey myEphPublic = (JcaPublicKey) getPublicKey(getEphemeralKeyPair());
                final JcaPublicKey mySrcEphPublic = (JcaPublicKey) getPublicKey(getPartnerEphemeralKeyPair());
                final MQVParameterSpec myParams = new MQVParameterSpec(myEphPublic.getPublicKey(),
                            myEphPrivate.getPrivateKey(), mySrcEphPublic.getPublicKey(), new byte[0]);
                theAgreement.init(myPrivate.getPrivateKey(), myParams);

                /* Calculate agreement */
                final JcaPublicKey mySrcPublic = (JcaPublicKey) getPublicKey(pResponder);
                theAgreement.doPhase(mySrcPublic.getPublicKey(), true);

                /* Store secret */
                storeSecret(theAgreement.generateSecret());
            } catch (InvalidKeyException
                    | InvalidAlgorithmParameterException e) {
                throw new GordianCryptoException(ERR_AGREEMENT, e);
            }
        }
    }
}
