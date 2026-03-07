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
package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.agree.GordianAgreementFactory;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPair;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDHSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewRSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSM2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.sign.GordianSignatureFactory;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.GordianCoreKeyPairFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree.GordianCoreAgreementSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreLMSSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreXMSSSpec;
import io.github.tonywasher.joceanus.gordianknot.util.GordianUtilities;
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
    private static GordianNewKeyPairType theKeyType;

    /**
     * The single signatureType to test.
     */
    private static GordianNewSignatureType theSigType;

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
            GordianNewKeyPairType myKeyType = null;
            for (final GordianNewKeyPairType myType : GordianNewKeyPairType.values()) {
                if (myType.toString().equalsIgnoreCase(myPropKeyType)) {
                    myKeyType = myType;
                }
            }
            theKeyType = myKeyType;

            /* Look for request to test a single keyType */
            String myPropSigType = System.getProperty("sigType");
            GordianNewSignatureType mySigType = null;
            for (final GordianNewSignatureType myType : GordianNewSignatureType.values()) {
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
        private final GordianNewKeyPairSpec theKeySpec;

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
                       final GordianNewKeyPairSpec pKeySpec) {
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
        GordianNewKeyPairSpec getKeySpec() {
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
        private final GordianNewSignatureSpec theSignSpec;

        /**
         * Constructor.
         *
         * @param pOwner    the owner
         * @param pSignSpec the signatureSpec
         */
        FactorySignature(final FactoryKeySpec pOwner,
                         final GordianNewSignatureSpec pSignSpec) {
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
        GordianNewSignatureSpec getSpec() {
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
        private final GordianNewAgreementSpec theAgreeSpec;

        /**
         * Constructor.
         *
         * @param pOwner     the owner
         * @param pAgreeSpec the agreementSpec
         */
        FactoryAgreement(final FactoryKeySpec pOwner,
                         final GordianNewAgreementSpec pAgreeSpec) {
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
        GordianNewAgreementSpec getSpec() {
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
        private final GordianNewEncryptorSpec theEncryptSpec;

        /**
         * Constructor.
         *
         * @param pOwner       the owner
         * @param pEncryptSpec the encryptorSpec
         */
        FactoryEncryptor(final FactoryKeySpec pOwner,
                         final GordianNewEncryptorSpec pEncryptSpec) {
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
        GordianNewEncryptorSpec getSpec() {
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
        for (GordianNewKeyPairType myKeyType : GordianNewKeyPairType.values()) {
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
                                                        final GordianNewKeyPairType pKeyType) {
        /* Loop through all the possible specs for this keyType */
        final List<FactoryKeySpec> myResult = new ArrayList<>();
        final GordianCoreKeyPairFactory myFactory = (GordianCoreKeyPairFactory) pFactory.getAsyncFactory().getKeyPairFactory();
        List<GordianNewKeyPairSpec> mySpecs = pKeyType == GordianNewKeyPairType.COMPOSITE
                ? compositeKeySpecProvider()
                : myFactory.listAllSupportedKeyPairSpecs(pKeyType);
        for (GordianNewKeyPairSpec myKeySpec : mySpecs) {
            /* If the keyType is LMS */
            final GordianCoreKeyPairSpec mySpec = (GordianCoreKeyPairSpec) myKeySpec;
            if (pKeyType == GordianNewKeyPairType.LMS) {
                /* Access the keySpec */
                final GordianCoreLMSSpec myLMSSpec = mySpec.getLMSSpec();

                /* Ignore high configs for performance */
                if (myLMSSpec.isHigh()) {
                    continue;
                }
            }

            /* If the keyType is XMSS */
            if (pKeyType == GordianNewKeyPairType.XMSS) {
                /* Access the keySpec */
                GordianCoreXMSSSpec myXMSSSpec = mySpec.getXMSSSpec();

                /* Ignore high configs for performance */
                if (myXMSSSpec.isHigh()) {
                    continue;
                }
            }

            /* Add the keySpec */
            myResult.add(new FactoryKeySpec(pFactory, pPartner, myKeySpec));

            /* If we are only testing one keySpec per type, break the loop */
            if (!allSpecs && pKeyType != GordianNewKeyPairType.COMPOSITE) {
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
    static void signatureProvider(final FactoryKeySpec pKeySpec) {
        /* Access the list */
        List<AsymmetricStore.FactorySignature> myResult = pKeySpec.theSignatures;

        /* Access the list of possible signatures */
        final GordianAsyncFactory myFactory = pKeySpec.theFactory;
        final GordianSignatureFactory mySignFactory = myFactory.getSignatureFactory();
        final List<GordianNewSignatureSpec> mySignSpecs = pKeySpec.getKeySpec().getKeyPairType() == GordianNewKeyPairType.COMPOSITE
                ? compositeSignatureSpecProvider(pKeySpec)
                : mySignFactory.listAllSupportedSignatures(pKeySpec.theKeySpec.getKeyPairType());

        /* Skip key if there are no possible signatures */
        if (mySignSpecs.isEmpty()) {
            return;
        }

        /* Loop through the possible signatures */
        for (GordianNewSignatureSpec mySign : mySignSpecs) {
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
        final GordianAgreementFactory myAgreeFactory = myFactory.getAgreementFactory();
        final List<GordianNewAgreementSpec> myAgreeSpecs = pKeySpec.getKeySpec().getKeyPairType() == GordianNewKeyPairType.COMPOSITE
                ? compositeAgreementSpecProvider(pKeySpec)
                : myAgreeFactory.listAllSupportedAgreements(pKeySpec.theKeySpec);

        /* Skip key if there are no possible agreements */
        if (myAgreeSpecs.isEmpty()) {
            return;
        }

        /* Loop through the possible agreements */
        for (GordianNewAgreementSpec myAgree : myAgreeSpecs) {
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
        final List<GordianNewEncryptorSpec> mySpecs = pKeySpec.getKeySpec().getKeyPairType() == GordianNewKeyPairType.COMPOSITE
                ? compositeEncryptorSpecProvider(pKeySpec)
                : myEncryptFactory.listAllSupportedEncryptors(pKeySpec.theKeySpec.getKeyPairType());

        /* Skip key if there are no possible encryptors */
        if (mySpecs.isEmpty()) {
            return;
        }

        /* Loop through the possible encryptors */
        for (GordianNewEncryptorSpec myEncrypt : mySpecs) {
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
    private static List<GordianNewKeyPairSpec> compositeKeySpecProvider() {
        final List<GordianNewKeyPairSpec> myResult = new ArrayList<>();
        final GordianNewKeyPairSpecBuilder myBuilder = GordianUtilities.newKeyPairSpecBuilder();
        myResult.add(myBuilder.composite(myBuilder.rsa(GordianNewRSASpec.MOD2048),
                myBuilder.falcon(GordianNewFalconSpec.FALCON512),
                myBuilder.ed25519()));
        myResult.add(myBuilder.composite(myBuilder.dh(GordianNewDHSpec.FFDHE2048),
                myBuilder.x25519(),
                myBuilder.ec(GordianNewECSpec.SECP256R1)));
        myResult.add(myBuilder.composite(myBuilder.sm2(GordianNewSM2Spec.SM2P256V1),
                myBuilder.ec(GordianNewECSpec.SECP256R1)));
        myResult.add(myBuilder.composite(myBuilder.newHope(),
                myBuilder.ec(GordianNewECSpec.SECP256R1),
                myBuilder.saber(GordianNewSABERSpec.BASE128)));
        myResult.add(myBuilder.composite(myBuilder.rsa(GordianNewRSASpec.MOD2048),
                myBuilder.elGamal(GordianNewDHSpec.FFDHE2048),
                myBuilder.sm2(GordianNewSM2Spec.SM2P256V1)));
        myResult.add(myBuilder.composite(myBuilder.xmss(GordianNewXMSSDigestType.SHA256, GordianNewXMSSHeight.H10),
                myBuilder.lms(GordianNewLMSHash.SHA256, GordianNewLMSHeight.H5,
                        GordianNewLMSWidth.W1, GordianLength.LEN_256)));
        return myResult;
    }

    /**
     * Composite signatureSpec provider.
     *
     * @param pKeySpec the keySpec
     * @return the list
     */
    private static List<GordianNewSignatureSpec> compositeSignatureSpecProvider(final FactoryKeySpec pKeySpec) {
        final GordianNewSignatureSpec mySpec = pKeySpec.getFactory().getSignatureFactory().defaultForKeyPair(pKeySpec.getKeySpec());
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
    private static List<GordianNewAgreementSpec> compositeAgreementSpecProvider(final FactoryKeySpec pKeySpec) {
        final List<GordianNewAgreementSpec> mySpecs = new ArrayList<>();
        mySpecs.addAll(GordianCoreAgreementSpecBuilder.listAllPossibleSpecs(pKeySpec.getKeySpec()));
        mySpecs.removeIf(s -> s == null || !s.isValid());
        return mySpecs;
    }

    /**
     * Composite encryptorSpec provider.
     *
     * @param pKeySpec the keySpec
     * @return the list
     */
    private static List<GordianNewEncryptorSpec> compositeEncryptorSpecProvider(final FactoryKeySpec pKeySpec) {
        final GordianNewEncryptorSpec mySpec = pKeySpec.getFactory().getEncryptorFactory().defaultForKeyPair(pKeySpec.getKeySpec());
        return mySpec != null && mySpec.isValid()
                ? Collections.singletonList(mySpec)
                : Collections.emptyList();
    }
}
