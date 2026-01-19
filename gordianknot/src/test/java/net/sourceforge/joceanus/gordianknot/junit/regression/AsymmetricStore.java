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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementType;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianFalconSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianLMSHash;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianLMSHeight;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianLMSWidth;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSABERSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSM2Elliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureType;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import org.junit.jupiter.api.BeforeAll;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Assymmetric Test Data Classes.
 */
class AsymmetricStore {
    /**
     * The single keyType to test.
     */
    private static GordianKeyPairType theKeyType;

    /**
     * The single signatureType to test.
     */
    private static GordianSignatureType theSigType;

    /**
     * Do we process all specs.
     */
    private static boolean allSpecs;

    /**
     * Configure the test according to system properties.
     */
    @BeforeAll
    static void parseOptions() {
        /* If this is a full build */
        final String myBuildType = System.getProperty("joceanus.fullBuild");
        if (myBuildType != null) {
            /* Test everything */
            allSpecs = true;
            theSigType = null;
            theKeyType = null;

            /* else allow further configuration */
        } else {
            /* Check for explicit request for all keySpecs */
            allSpecs = System.getProperty("allSpecs") != null;

            /* Look for request to test a single keyType */
            final String myPropKeyType = System.getProperty("keyType");
            GordianKeyPairType myKeyType = null;
            for (final GordianKeyPairType myType : GordianKeyPairType.values()) {
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
        private final GordianAsyncFactory theFactory;

        /**
         * The partner Factory.
         */
        private final GordianAsyncFactory thePartner;

        /**
         * The KeySpec.
         */
        private final GordianKeyPairSpec theKeySpec;

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
         *
         * @param pFactory the factory
         * @param pPartner the partner factory
         * @param pKeySpec the keySpec
         */
        FactoryKeySpec(final GordianFactory pFactory,
                       final GordianFactory pPartner,
                       final GordianKeyPairSpec pKeySpec) {
            /* Store parameters */
            theFactoryType = pFactory.getFactoryType();
            theFactory = pFactory.getAsyncFactory();
            theKeySpec = pKeySpec;

            /* Initialise data */
            theKeyPairs = new FactoryKeyPairs(this);
            theSignatures = new ArrayList<>();
            theAgreements = new ArrayList<>();
            theEncryptors = new ArrayList<>();

            /* Check whether the keySpec is supported by the partner */
            thePartner = pPartner.getAsyncFactory().getKeyPairFactory().supportedKeyPairSpecs().test(pKeySpec)
                    ? pPartner.getAsyncFactory()
                    : null;
        }

        /**
         * Obtain the factoryType.
         *
         * @return the factoryType
         */
        GordianFactoryType getFactoryType() {
            return theFactoryType;
        }

        /**
         * Obtain the factory.
         *
         * @return the factory
         */
        GordianAsyncFactory getFactory() {
            return theFactory;
        }

        /**
         * Obtain the partner factory.
         *
         * @return the factory
         */
        GordianAsyncFactory getPartner() {
            return thePartner;
        }

        /**
         * Obtain the keySpec.
         *
         * @return the keySpec
         */
        GordianKeyPairSpec getKeySpec() {
            return theKeySpec;
        }

        /**
         * Obtain the keyPairs for the FactoryKeySpec
         *
         * @return the keyPairs
         */
        FactoryKeyPairs getKeyPairs() {
            return theKeyPairs;
        }

        /**
         * Obtain the signatureList.
         *
         * @return the signature list
         */
        List<FactorySignature> getSignatures() {
            return theSignatures;
        }

        /**
         * Obtain the agreementList.
         *
         * @return the agreement list
         */
        List<FactoryAgreement> getAgreements() {
            return theAgreements;
        }

        /**
         * Obtain the encryptorList.
         *
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
        private volatile GordianKeyPair theKeyPair;

        /**
         * The targetPair.
         */
        private volatile GordianKeyPair theTarget;

        /**
         * The mirrorPair.
         */
        private volatile GordianKeyPair theMirror;

        /**
         * The partnerSelf.
         */
        private volatile GordianKeyPair thePartnerSelf;

        /**
         * The partnerTarget.
         */
        private volatile GordianKeyPair thePartnerTarget;

        /**
         * Constructor.
         *
         * @param pOwner the owner
         */
        FactoryKeyPairs(final FactoryKeySpec pOwner) {
            theOwner = pOwner;
        }

        /**
         * Obtain (or create) the keyPair for the FactoryKeySpec
         *
         * @return the keyPair
         * @throws GordianException on error
         */
        GordianKeyPair getKeyPair() throws GordianException {
            /* If the keyPair has not yet been initialized */
            GordianKeyPair myKeyPair = theKeyPair;
            if (myKeyPair != null) {
                return myKeyPair;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myKeyPair = theKeyPair;
                if (myKeyPair != null) {
                    return myKeyPair;
                }

                /* Generate the keyPair */
                final GordianAsyncFactory myFactory = theOwner.getFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairFactory().getKeyPairGenerator(theOwner.getKeySpec());
                myKeyPair = myGenerator.generateKeyPair();
                theKeyPair = myKeyPair;
                return myKeyPair;
            }
        }

        /**
         * Obtain the X509 encoding.
         *
         * @return the encoding
         * @throws GordianException on error
         */
        X509EncodedKeySpec getX509Encoding() throws GordianException {
            final GordianKeyPairFactory myFactory = theOwner.getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
            return myGenerator.getX509Encoding(getKeyPair());
        }

        /**
         * Obtain the PKCS8 encoding.
         *
         * @return the encoding
         * @throws GordianException on error
         */
        PKCS8EncodedKeySpec getPKCS8Encoding() throws GordianException {
            final GordianKeyPairFactory myFactory = theOwner.getFactory().getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
            return myGenerator.getPKCS8Encoding(getKeyPair());
        }

        /**
         * Obtain (or create) the keyPair for the target
         *
         * @return the keyPair
         * @throws GordianException on error
         */
        GordianKeyPair getTargetKeyPair() throws GordianException {
            /* Return keyPair if it exists */
            GordianKeyPair myTarget = theTarget;
            if (myTarget != null) {
                return myTarget;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myTarget = theTarget;
                if (myTarget != null) {
                    return myTarget;
                }

                /* Generate the keyPair */
                final GordianKeyPairFactory myFactory = theOwner.getFactory().getKeyPairFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                myTarget = myGenerator.generateKeyPair();
                theTarget = myTarget;
                return myTarget;
            }
        }

        /**
         * Obtain (or create) the mirror keyPair
         *
         * @return the mirror keyPair
         * @throws GordianException on error
         */
        GordianKeyPair getMirrorKeyPair() throws GordianException {
            /* Return mirror keyPair if it exists */
            GordianKeyPair myMirror = theMirror;
            if (myMirror != null) {
                return myMirror;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myMirror = theMirror;
                if (myMirror != null) {
                    return myMirror;
                }

                /* Access the keyPair */
                GordianKeyPair myPair = getKeyPair();

                /* Generate the keyPair */
                final GordianKeyPairFactory myFactory = theOwner.getFactory().getKeyPairFactory();
                final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myPair);
                final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myPair);
                myMirror = myGenerator.deriveKeyPair(myPublic, myPrivate);
                theMirror = myMirror;
                return myMirror;
            }
        }

        /**
         * Obtain (or create) the partner Self keyPair
         *
         * @return the partnerSelf keyPair
         * @throws GordianException on error
         */
        GordianKeyPair getPartnerSelfKeyPair() throws GordianException {
            /* Return partnerSelf keyPair if it exists */
            GordianKeyPair myPartnerSelf = thePartnerSelf;
            if (myPartnerSelf != null
                    || theOwner.getPartner() == null) {
                return myPartnerSelf;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myPartnerSelf = thePartnerSelf;
                if (myPartnerSelf != null) {
                    return myPartnerSelf;
                }

                /* Access the keyPair */
                GordianKeyPair myPair = getKeyPair();
                GordianKeyPairFactory myFactory = theOwner.getFactory().getKeyPairFactory();
                GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myPair);
                final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myPair);

                /* Derive the partner keyPair */
                myFactory = theOwner.getPartner().getKeyPairFactory();
                myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                myPartnerSelf = myGenerator.deriveKeyPair(myPublic, myPrivate);
                thePartnerSelf = myPartnerSelf;
                return myPartnerSelf;
            }
        }

        /**
         * Obtain (or create) the partner Target keyPair
         *
         * @return the partnerTarget keyPair
         * @throws GordianException on error
         */
        GordianKeyPair getPartnerTargetKeyPair() throws GordianException {
            /* Return partnerTarget keyPair if it exists */
            GordianKeyPair myPartnerTarget = thePartnerTarget;
            if (myPartnerTarget != null
                    || theOwner.getPartner() == null) {
                return myPartnerTarget;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myPartnerTarget = thePartnerTarget;
                if (myPartnerTarget != null) {
                    return myPartnerTarget;
                }

                /* Access the target keyPair */
                GordianKeyPair myPair = getTargetKeyPair();
                GordianKeyPairFactory myFactory = theOwner.getFactory().getKeyPairFactory();
                GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                final X509EncodedKeySpec myPublic = myGenerator.getX509Encoding(myPair);
                final PKCS8EncodedKeySpec myPrivate = myGenerator.getPKCS8Encoding(myPair);

                /* Derive the keyPair */
                myFactory = theOwner.getPartner().getKeyPairFactory();
                myGenerator = myFactory.getKeyPairGenerator(theOwner.getKeySpec());
                myPartnerTarget = myGenerator.deriveKeyPair(myPublic, myPrivate);
                thePartnerTarget = myPartnerTarget;
                return myPartnerTarget;
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
         *
         * @param pOwner    the owner
         * @param pSignSpec the signatureSpec
         */
        FactorySignature(final FactoryKeySpec pOwner,
                         final GordianSignatureSpec pSignSpec) {
            theOwner = pOwner;
            theSignSpec = pSignSpec;
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        FactoryKeySpec getOwner() {
            return theOwner;
        }

        /**
         * Obtain the spec.
         *
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
         *
         * @param pOwner     the owner
         * @param pAgreeSpec the agreementSpec
         */
        FactoryAgreement(final FactoryKeySpec pOwner,
                         final GordianAgreementSpec pAgreeSpec) {
            theOwner = pOwner;
            theAgreeSpec = pAgreeSpec;
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        FactoryKeySpec getOwner() {
            return theOwner;
        }

        /**
         * Obtain the spec.
         *
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
         *
         * @param pOwner       the owner
         * @param pEncryptSpec the encryptorSpec
         */
        FactoryEncryptor(final FactoryKeySpec pOwner,
                         final GordianEncryptorSpec pEncryptSpec) {
            theOwner = pOwner;
            theEncryptSpec = pEncryptSpec;
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        FactoryKeySpec getOwner() {
            return theOwner;
        }

        /**
         * Obtain the spec.
         *
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
     *
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the list
     */
    static List<FactoryKeySpec> keySpecProvider(final GordianFactory pFactory,
                                                final GordianFactory pPartner) {
        /* Loop through the possible keyTypes */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        for (GordianKeyPairType myKeyType : GordianKeyPairType.values()) {
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
     *
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyType the keyType
     * @return the list
     */
    private static List<FactoryKeySpec> keySpecProvider(final GordianFactory pFactory,
                                                        final GordianFactory pPartner,
                                                        final GordianKeyPairType pKeyType) {
        /* Loop through all the possible specs for this keyType */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        final GordianCoreKeyPairFactory myFactory = (GordianCoreKeyPairFactory) pFactory.getAsyncFactory().getKeyPairFactory();
        List<GordianKeyPairSpec> mySpecs = pKeyType == GordianKeyPairType.COMPOSITE
                ? compositeKeySpecProvider()
                : myFactory.listAllSupportedKeyPairSpecs(pKeyType);
        for (GordianKeyPairSpec myKeySpec : mySpecs) {
            /* If the keyType is LMS */
            if (pKeyType == GordianKeyPairType.LMS) {
                /* Access the keySpec */
                GordianLMSKeySpec myLMSSpec = myKeySpec.getHSSKeySpec().getKeySpec();

                /* Ignore high configs for performance */
                if (myLMSSpec.isHigh()) {
                    continue;
                }
            }

            /* If the keyType is XMSS */
            if (pKeyType == GordianKeyPairType.XMSS) {
                /* Access the keySpec */
                GordianXMSSKeySpec myXMSSSpec = myKeySpec.getXMSSKeySpec();

                /* Ignore high configs for performance */
                if (myXMSSSpec.isHigh()) {
                    continue;
                }
            }

            /* Add the keySpec */
            myResult.add(new FactoryKeySpec(pFactory, pPartner, myKeySpec));

            /* If we are only testing one keySpec per type, break the loop */
            if (!allSpecs && pKeyType != GordianKeyPairType.COMPOSITE) {
                break;
            }
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Update the list of Signatures to test.
     *
     * @param pKeySpec the keySpec
     */
    static void signatureProvider(final AsymmetricStore.FactoryKeySpec pKeySpec) {
        /* Access the list */
        List<AsymmetricStore.FactorySignature> myResult = pKeySpec.theSignatures;

        /* Access the list of possible signatures */
        final GordianAsyncFactory myFactory = pKeySpec.theFactory;
        final GordianSignatureFactory mySignFactory = myFactory.getSignatureFactory();
        final List<GordianSignatureSpec> mySignSpecs = pKeySpec.getKeySpec().getKeyPairType() == GordianKeyPairType.COMPOSITE
                ? compositeSignatureSpecProvider(pKeySpec)
                : mySignFactory.listAllSupportedSignatures(pKeySpec.theKeySpec.getKeyPairType());

        /* Skip key if there are no possible signatures */
        if (mySignSpecs.isEmpty()) {
            return;
        }

        /* Loop through the possible signatures */
        for (GordianSignatureSpec mySign : mySignSpecs) {
            /* If we are testing a single sigType, make sure this is the right one */
            if (theSigType != null
                    && !mySign.getSignatureType().equals(theSigType)) {
                continue;
            }

            /* Add the signature if it is supported */
            if (mySignFactory.validSignatureSpecForKeyPairSpec(pKeySpec.getKeySpec(), mySign)) {
                myResult.add(new FactorySignature(pKeySpec, mySign));
            }
        }
    }

    /**
     * Update the list of Agreements to test.
     *
     * @param pKeySpec the keySpec
     */
    static void agreementProvider(final FactoryKeySpec pKeySpec) {
        /* Access the list */
        List<AsymmetricStore.FactoryAgreement> myResult = pKeySpec.theAgreements;

        /* Access the list of possible agreements */
        final GordianAsyncFactory myFactory = pKeySpec.theFactory;
        final GordianXAgreementFactory myAgreeFactory = myFactory.getXAgreementFactory();
        final List<GordianAgreementSpec> myAgreeSpecs = pKeySpec.getKeySpec().getKeyPairType() == GordianKeyPairType.COMPOSITE
                ? compositeAgreementSpecProvider(pKeySpec)
                : myAgreeFactory.listAllSupportedAgreements(pKeySpec.theKeySpec);

        /* Skip key if there are no possible agreements */
        if (myAgreeSpecs.isEmpty()) {
            return;
        }

        /* Loop through the possible agreements */
        for (GordianAgreementSpec myAgree : myAgreeSpecs) {
            /* Add the agreement if it is supported */
            if (myAgreeFactory.validAgreementSpecForKeyPairSpec(pKeySpec.getKeySpec(), myAgree)) {
                myResult.add(new FactoryAgreement(pKeySpec, myAgree));
            }
        }
    }

    /**
     * Update the list of Encryptors to test.
     *
     * @param pKeySpec the keySpec
     */
    static void encryptorProvider(final FactoryKeySpec pKeySpec) {
        /* Access the list */
        List<AsymmetricStore.FactoryEncryptor> myResult = pKeySpec.theEncryptors;

        /* Access the list of possible encryptors */
        final GordianAsyncFactory myFactory = pKeySpec.theFactory;
        final GordianEncryptorFactory myEncryptFactory = myFactory.getEncryptorFactory();
        final List<GordianEncryptorSpec> mySpecs = pKeySpec.getKeySpec().getKeyPairType() == GordianKeyPairType.COMPOSITE
                ? compositeEncryptorSpecProvider(pKeySpec)
                : myEncryptFactory.listAllSupportedEncryptors(pKeySpec.theKeySpec.getKeyPairType());

        /* Skip key if there are no possible encryptors */
        if (mySpecs.isEmpty()) {
            return;
        }

        /* Loop through the possible encryptors */
        for (GordianEncryptorSpec myEncrypt : mySpecs) {
            /* Add the encryptor if it is supported */
            if (myEncryptFactory.validEncryptorSpecForKeyPairSpec(pKeySpec.getKeySpec(), myEncrypt)) {
                myResult.add(new FactoryEncryptor(pKeySpec, myEncrypt));
            }
        }
    }

    /**
     * Composite keyPairSpec provider.
     *
     * @return the list
     */
    private static List<GordianKeyPairSpec> compositeKeySpecProvider() {
        final List<GordianKeyPairSpec> myResult = new ArrayList<>();
        myResult.add(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                GordianKeyPairSpecBuilder.falcon(GordianFalconSpec.FALCON512),
                GordianKeyPairSpecBuilder.ed25519()));
        myResult.add(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.dh(GordianDHGroup.FFDHE2048),
                GordianKeyPairSpecBuilder.x25519(),
                GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECP256R1)));
        myResult.add(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.sm2(GordianSM2Elliptic.SM2P256V1),
                GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECP256R1)));
        myResult.add(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.newHope(),
                GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECP256R1),
                GordianKeyPairSpecBuilder.saber(GordianSABERSpec.BASE128)));
        myResult.add(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                GordianKeyPairSpecBuilder.elGamal(GordianDHGroup.FFDHE2048),
                GordianKeyPairSpecBuilder.sm2(GordianSM2Elliptic.SM2P256V1)));
        myResult.add(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.xmss(GordianXMSSDigestType.SHA256, GordianXMSSHeight.H10),
                GordianKeyPairSpecBuilder.lms(new GordianLMSKeySpec(GordianLMSHash.SHA256, GordianLMSHeight.H5,
                        GordianLMSWidth.W1, GordianLength.LEN_256))));
        return myResult;
    }

    /**
     * Composite signatureSpec provider.
     *
     * @param pKeySpec the keySpec
     * @return the list
     */
    private static List<GordianSignatureSpec> compositeSignatureSpecProvider(final FactoryKeySpec pKeySpec) {
        final GordianSignatureSpec mySpec = pKeySpec.getFactory().getSignatureFactory().defaultForKeyPair(pKeySpec.getKeySpec());
        return mySpec != null && mySpec.isValid()
                ? Collections.singletonList(mySpec)
                : Collections.emptyList();
    }

    /**
     * Composite encryptorSpec provider.
     *
     * @param pKeySpec the keySpec
     * @return the list
     */
    private static List<GordianAgreementSpec> compositeAgreementSpecProvider(final FactoryKeySpec pKeySpec) {
        final List<GordianAgreementSpec> mySpecs = new ArrayList<>();
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.KEM));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.ANON));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.BASIC));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.SIGNED));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.UNIFIED));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.UNIFIED, Boolean.TRUE));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.MQV));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.MQV, Boolean.TRUE));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.SM2));
        mySpecs.addAll(GordianCoreAgreementFactory.listAllKDFs(pKeySpec.getKeySpec(), GordianAgreementType.SM2, Boolean.TRUE));
        mySpecs.removeIf(s -> s == null || !s.isValid());
        return mySpecs;
    }

    /**
     * Composite encryptorSpec provider.
     *
     * @param pKeySpec the keySpec
     * @return the list
     */
    private static List<GordianEncryptorSpec> compositeEncryptorSpecProvider(final FactoryKeySpec pKeySpec) {
        final GordianEncryptorSpec mySpec = pKeySpec.getFactory().getEncryptorFactory().defaultForKeyPair(pKeySpec.getKeySpec());
        return mySpec != null && mySpec.isValid()
                ? Collections.singletonList(mySpec)
                : Collections.emptyList();
    }
}
