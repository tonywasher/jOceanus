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
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Assymmetric Test Data Classes.
 */
class AsymmetricStore {
    /**
     * The single keyType to test.
     */
    private static final GordianAsymKeyType theKeyType;

    /**
     * The single signatureType to test.
     */
    private static final GordianSignatureType theSigType;

    /**
     * Do we process all specs.
     */
    private static final boolean allSpecs;

    /**
     * Configure the test according to system properties.
     */
    static {
        /* If this is a full build */
        final String myBuildType = System.getProperty("joceanus.fullBuild");
        if (myBuildType != null) {
            /* Test everything */
            allSpecs=true;
            theSigType=null;
            theKeyType=null;

            /* else allow further configuration */
        } else {
            /* Check for explicit request for all keySpecs */
            allSpecs = System.getProperty("allSpecs") != null;

            /* Look for request to test a single keyType */
            final String myPropKeyType = System.getProperty("keyType");
            GordianAsymKeyType myKeyType = null;
            for (final GordianAsymKeyType myType : GordianAsymKeyType.values()) {
                if (myType.toString().equalsIgnoreCase(myPropKeyType)) {
                    myKeyType = myType;
                }
            }
            theKeyType = myKeyType;

            /* Look for request to test a single keyType */
            String myPropSigType = System.getProperty("sigType");
            GordianSignatureType mySigType = null;
            for (final GordianSignatureType myType : GordianSignatureType.values()) {
                if (myType.toString().equalsIgnoreCase(myPropSigType)) {
                    mySigType = myType;
                }
            }
            theSigType = mySigType;
        }
    }

    /**
     * Factory and KeySpec definition.
     */
    static class FactoryKeySpec {
        /**
         * The FactoryType.
         */
        private final GordianFactoryType theFactoryType;

        /**
         * The Factory.
         */
        private final GordianAsymFactory theFactory;

        /**
         * The partner Factory.
         */
        private final GordianAsymFactory thePartner;

        /**
         * The KeySpec.
         */
        private final GordianAsymKeySpec theKeySpec;

        /**
         * The list of signatures.
         */
        private final List<FactorySignature> theSignatures;

        /**
         * The list of agreements.
         */
        private final List<FactoryAgreement> theAgreements;

        /**
         * The list of encryptors.
         */
        private final List<FactoryEncryptor> theEncryptors;

        /**
         * The keyPairs.
         */
        private final FactoryKeyPairs theKeyPairs;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPartner the partner factory
         * @param pKeySpec the keySpec
         */
        FactoryKeySpec(final GordianFactory pFactory,
                       final GordianFactory pPartner,
                       final GordianAsymKeySpec pKeySpec) {
            /* Store parameters */
            theFactoryType = pFactory.getFactoryType();
            theFactory = pFactory.getAsymmetricFactory();
            theKeySpec = pKeySpec;

            /* Initialise data */
            theKeyPairs = new FactoryKeyPairs(this);
            theSignatures = new ArrayList<>();
            theAgreements = new ArrayList<>();
            theEncryptors = new ArrayList<>();

            /* Check whether the keySpec is supported by the partner */
            thePartner = pPartner.getAsymmetricFactory().supportedAsymKeySpecs().test(pKeySpec)
                         ? pPartner.getAsymmetricFactory()
                         : null;
        }

        /**
         * Obtain the factoryType.
         * @return the factoryType
         */
        GordianFactoryType getFactoryType() {
            return theFactoryType;
        }

        /**
         * Obtain the factory.
         * @return the factory
         */
        GordianAsymFactory getFactory() {
            return theFactory;
        }

        /**
         * Obtain the partner factory.
         * @return the factory
         */
        GordianAsymFactory getPartner() {
            return thePartner;
        }

        /**
         * Obtain the keySpec.
         * @return the keySpec
         */
        GordianAsymKeySpec getKeySpec() {
            return theKeySpec;
        }

        /**
         * Obtain the keyPairs for the FactoryKeySpec
         * @return the keyPairs
         */
        FactoryKeyPairs getKeyPairs() {
            return theKeyPairs;
        }

        /**
         * Obtain the signatureList.
         * @return the signature list
         */
        List<FactorySignature> getSignatures() {
            return theSignatures;
        }

        /**
         * Obtain the agreementList.
         * @return the agreement list
         */
        List<FactoryAgreement> getAgreements() {
            return theAgreements;
        }

        /**
         * Obtain the encryptorList.
         * @return the encryptor list
         */
        List<FactoryEncryptor> getEncryptors() {
            return theEncryptors;
        }

        @Override
        public boolean equals(final Object pThat) {
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }
            if (!(pThat instanceof FactoryKeySpec)) {
                return false;
            }
            final FactoryKeySpec myThat = (FactoryKeySpec) pThat;
            return getFactoryType() == myThat.getFactoryType()
                    && theKeySpec.equals(myThat.theKeySpec);
        }

        @Override
        public int hashCode() {
            return theFactoryType.hashCode() + theKeySpec.hashCode();
        }

        @Override
        public String toString() {
            return theKeySpec.toString();
        }
    }

    /**
     * FactoryKeyPairs.
     */
    static class FactoryKeyPairs {
        /**
         * The owner.
         */
        private final FactoryKeySpec theOwner;

        /**
         * The keyPair.
         */
        private GordianKeyPair theKeyPair;

        /**
         * The mirrorPair.
         */
        private GordianKeyPair theMirror;

        /**
         * The partnerSelf.
         */
        private GordianKeyPair thePartnerSelf;

        /**
         * The targetPair.
         */
        private GordianKeyPair theTarget;

        /**
         * The partnerTarget.
         */
        private GordianKeyPair thePartnerTarget;

        /**
         * Constructor.
         * @param pOwner the owner
         */
        FactoryKeyPairs(final FactoryKeySpec pOwner) {
            theOwner = pOwner;
        }

        /**
         * Obtain (or create) the keyPair for the FactoryKeySpec
         * @return the keyPair
         * @throws OceanusException on error
         */
        GordianKeyPair getKeyPair() throws OceanusException {
            /* Return keyPair if it exists */
            if (theKeyPair != null) {
                return theKeyPair;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKeyPair != null) {
                    return theKeyPair;
                }

                /* Generate the keyPair */
                final GordianAsymFactory myFactory = theOwner.getFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                theKeyPair = myGenerator.generateKeyPair();
                return theKeyPair;
            }
        }

        /**
         * Obtain the X509 encoding.
         * @return the encoding
         * @throws OceanusException on error
         */
        X509EncodedKeySpec getX509Encoding() throws OceanusException {
            final GordianAsymFactory myFactory = theOwner.getFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
            return myGenerator.getX509Encoding(getKeyPair());
        }

        /**
         * Obtain the PKCS8 encoding.
         * @return the encoding
         * @throws OceanusException on error
         */
        PKCS8EncodedKeySpec getPKCS8Encoding() throws OceanusException {
            final GordianAsymFactory myFactory = theOwner.getFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
            return myGenerator.getPKCS8Encoding(getKeyPair());
        }

        /**
         * Obtain (or create) the keyPair for the target
         * @return the keyPair
         * @throws OceanusException on error
         */
        GordianKeyPair getTargetKeyPair() throws OceanusException {
            /* Return keyPair if it exists */
            if (theTarget != null) {
                return theTarget;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theTarget != null) {
                    return theTarget;
                }

                /* Generate the keyPair */
                final GordianAsymFactory myFactory = theOwner.getFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                theTarget = myGenerator.generateKeyPair();
                return theTarget;
            }
        }

        /**
         * Obtain (or create) the mirror keyPair
         * @return the mirror keyPair
         * @throws OceanusException on error
         */
        GordianKeyPair getMirrorKeyPair() throws OceanusException {
            /* Return mirror keyPair if it exists */
            if (theMirror != null) {
                return theMirror;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theMirror != null) {
                    return theMirror;
                }

                /* Access the keyPair */
                GordianKeyPair myPair = getKeyPair();

                /* Generate the keyPair */
                final GordianAsymFactory myFactory = theOwner.getFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myPair);
                final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myPair);
                theMirror = myGenerator.deriveKeyPair(myPublic, myPrivate);
                return theMirror;
            }
        }

        /**
         * Obtain (or create) the partner Self keyPair
         * @return the partnerSelf keyPair
         * @throws OceanusException on error
         */
        GordianKeyPair getPartnerSelfKeyPair() throws OceanusException {
            /* Return partnerSelf keyPair if it exists */
            if (thePartnerSelf != null
                 || theOwner.getPartner() == null) {
                return thePartnerSelf;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (thePartnerSelf != null) {
                    return thePartnerSelf;
                }

                /* Access the keyPair */
                GordianKeyPair myPair = getKeyPair();
                GordianAsymFactory myFactory = theOwner.getFactory();
                GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myPair);
                final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myPair);

                /* Derive the partner keyPair */
                myFactory = theOwner.getPartner();
                myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                thePartnerSelf = myGenerator.deriveKeyPair(myPublic, myPrivate);
                return thePartnerSelf;
            }
        }

        /**
         * Obtain (or create) the partner Target keyPair
         * @return the partnerTarget keyPair
         * @throws OceanusException on error
         */
        GordianKeyPair getPartnerTargetKeyPair() throws OceanusException {
            /* Return partnerTarget keyPair if it exists */
            if (thePartnerTarget != null
                    || theOwner.getPartner() == null) {
                return thePartnerTarget;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (thePartnerTarget != null) {
                    return thePartnerTarget;
                }

                /* Access the target keyPair */
                GordianKeyPair myPair = getTargetKeyPair();
                GordianAsymFactory myFactory = theOwner.getFactory();
                GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myPair);
                final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myPair);

                /* Derive the keyPair */
                myFactory = theOwner.getPartner();
                myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                thePartnerTarget = myGenerator.deriveKeyPair(myPublic, myPrivate);
                return thePartnerTarget;
            }
        }
    }

    /**
     * FactoryKeySpec and signature definition.
     */
    static class FactorySignature {
        /**
         * The Owner.
         */
        private final FactoryKeySpec theOwner;

        /**
         * The Spec.
         */
        private final GordianSignatureSpec theSignSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pSignSpec the signatureSpec
         */
        FactorySignature(final FactoryKeySpec pOwner,
                         final GordianSignatureSpec pSignSpec) {
            theOwner = pOwner;
            theSignSpec = pSignSpec;
        }

        /**
         * Obtain the owner.
         * @return the owner
         */
        FactoryKeySpec getOwner() {
            return theOwner;
        }

        /**
         * Obtain the spec.
         * @return the spec
         */
        GordianSignatureSpec getSpec() {
            return theSignSpec;
        }

        @Override
        public String toString() {
            return theSignSpec.toString();
        }
    }

    /**
     * FactoryKeySpec and agreement definition.
     */
    static class FactoryAgreement {
        /**
         * The Owner.
         */
        private final FactoryKeySpec theOwner;

        /**
         * The Spec.
         */
        private final GordianAgreementSpec theAgreeSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pAgreeSpec the agreementSpec
         */
        FactoryAgreement(final FactoryKeySpec pOwner,
                         final GordianAgreementSpec pAgreeSpec) {
            theOwner = pOwner;
            theAgreeSpec = pAgreeSpec;
        }

        /**
         * Obtain the owner.
         * @return the owner
         */
        FactoryKeySpec getOwner() {
            return theOwner;
        }

        /**
         * Obtain the spec.
         * @return the spec
         */
        GordianAgreementSpec getSpec() {
            return theAgreeSpec;
        }

        @Override
        public String toString() {
            return theAgreeSpec.toString();
        }
    }

    /**
     * FactoryKeySpec and encryptor definition.
     */
    static class FactoryEncryptor {
        /**
         * The Owner.
         */
        private final FactoryKeySpec theOwner;

        /**
         * The Spec.
         */
        private final GordianEncryptorSpec theEncryptSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pEncryptSpec the encryptorSpec
         */
        FactoryEncryptor(final FactoryKeySpec pOwner,
                         final GordianEncryptorSpec pEncryptSpec) {
            theOwner = pOwner;
            theEncryptSpec = pEncryptSpec;
        }

        /**
         * Obtain the owner.
         * @return the owner
         */
        FactoryKeySpec getOwner() {
            return theOwner;
        }

        /**
         * Obtain the spec.
         * @return the spec
         */
        GordianEncryptorSpec getSpec() {
            return theEncryptSpec;
        }

        @Override
        public String toString() {
            return theEncryptSpec.toString();
        }
    }

    /**
     * Obtain the list of KeySpecs to test for a given factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the list
     */
    static List<FactoryKeySpec> keySpecProvider(final GordianFactory pFactory,
                                                final GordianFactory pPartner) {
        /* Loop through the possible keyTypes */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        for (GordianAsymKeyType myKeyType : GordianAsymKeyType.values()) {
            /* If we are testing a single keyType, make sure this is the right one */
            if (theKeyType != null
                    && !myKeyType.equals(theKeyType)) {
                continue;
            }

            /* Add all Specs for this keyType */
            myResult.addAll(keySpecProvider(pFactory, pPartner, myKeyType));
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Obtain the list of KeySpecs to test for a given factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyType the keyType
     * @return the list
     */
    private static List<FactoryKeySpec> keySpecProvider(final GordianFactory pFactory,
                                                        final GordianFactory pPartner,
                                                        final GordianAsymKeyType pKeyType) {
        /* Loop through all the possible specs for this keyType */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        final GordianAsymFactory myFactory = pFactory.getAsymmetricFactory();
        List<GordianAsymKeySpec> mySpecs = myFactory.listAllSupportedAsymSpecs(pKeyType);
        for (GordianAsymKeySpec myKeySpec : mySpecs) {
            /* Add the keySpec */
            myResult.add(new FactoryKeySpec(pFactory, pPartner, myKeySpec));

            /* If we are only testing one keySpec per type, break the loop */
            if (!allSpecs) {
                break;
            }
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Update the list of Signatures to test.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    static void signatureProvider(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the list */
        List<FactorySignature> myResult = pKeySpec.theSignatures;

        /* Access the list of possible signatures */
        final GordianAsymFactory myFactory = pKeySpec.theFactory;
        final GordianSignatureFactory mySignFactory = myFactory.getSignatureFactory();
        final List<GordianSignatureSpec> mySignSpecs = mySignFactory.listAllSupportedSignatures(pKeySpec.theKeySpec.getKeyType());

        /* Skip key if there are no possible signatures */
        if (mySignSpecs.isEmpty()) {
            return;
        }

        /* Access keyPair and loop through the possible signatures */
        final GordianKeyPair myKeyPair = pKeySpec.getKeyPairs().getKeyPair();
        for (GordianSignatureSpec mySign : mySignSpecs) {
            /* If we are testing a single sigType, make sure this is the right one */
            if (theSigType != null
                    && !mySign.getSignatureType().equals(theSigType)) {
                continue;
            }

            /* Add the signature if it is supported */
            if (mySignFactory.validSignatureSpecForKeyPair(myKeyPair, mySign)) {
                myResult.add(new FactorySignature(pKeySpec, mySign));
            }
        }
    }

    /**
     * Update the list of Agreements to test.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    static void agreementProvider(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the list */
        List<FactoryAgreement> myResult = pKeySpec.theAgreements;

        /* Access the list of possible agreements */
        final GordianAsymFactory myFactory = pKeySpec.theFactory;
        final GordianAgreementFactory myAgreeFactory = myFactory.getAgreementFactory();
        final List<GordianAgreementSpec> myAgreeSpecs = myAgreeFactory.listAllSupportedAgreements(pKeySpec.theKeySpec.getKeyType());

        /* Skip key if there are no possible agreements */
        if (myAgreeSpecs.isEmpty()) {
            return;
        }

        /* Access keyPair and loop through the possible agreements */
        final GordianKeyPair myKeyPair = pKeySpec.getKeyPairs().getKeyPair();
        for (GordianAgreementSpec myAgree : myAgreeSpecs) {
            /* Add the agreement if it is supported */
            if (myAgreeFactory.validAgreementSpecForKeyPair(myKeyPair, myAgree)) {
                myResult.add(new FactoryAgreement(pKeySpec, myAgree));
            }
        }
    }

    /**
     * Update the list of Encryptors to test.
     * @param pKeySpec the keySpec
     * @throws OceanusException on error
     */
    static void encryptorProvider(final FactoryKeySpec pKeySpec) throws OceanusException {
        /* Access the list */
        List<FactoryEncryptor> myResult = pKeySpec.theEncryptors;

        /* Access the list of possible encryptors */
        final GordianAsymFactory myFactory = pKeySpec.theFactory;
        final GordianEncryptorFactory myEncryptFactory = myFactory.getEncryptorFactory();
        final List<GordianEncryptorSpec> mySpecs = myEncryptFactory.listAllSupportedEncryptors(pKeySpec.theKeySpec.getKeyType());

        /* Skip key if there are no possible encryptors */
        if (mySpecs.isEmpty()) {
            return;
        }

        /* Access keyPair and loop through the possible encryptors */
        final GordianKeyPair myKeyPair = pKeySpec.getKeyPairs().getKeyPair();
        for (GordianEncryptorSpec myEncrypt : mySpecs) {
            /* Add the encryptor if it is supported */
            if (myEncryptFactory.validEncryptorSpecForKeyPair(myKeyPair, myEncrypt)) {
                myResult.add(new FactoryEncryptor(pKeySpec, myEncrypt));
            }
        }
    }
}
