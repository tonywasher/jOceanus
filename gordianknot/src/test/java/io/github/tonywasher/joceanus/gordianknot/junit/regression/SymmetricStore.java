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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyGenerator;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.GordianCoreCipherFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.key.GordianCoreKey;
import io.github.tonywasher.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import io.github.tonywasher.joceanus.gordianknot.impl.core.mac.GordianCoreMacFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.random.GordianCoreRandomFactory;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymCipherSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpecBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Symmetric Tests Data Classes.
 */
class SymmetricStore {
    /**
     * Factory Spec interface.
     *
     * @param <T> the object type
     */
    interface FactorySpec<T> {
        /**
         * Obtain the factory.
         *
         * @return the factory.
         */
        GordianFactory getFactory();

        /**
         * Obtain the spec.
         *
         * @return the spec.
         */
        T getSpec();
    }

    /**
     * Partnered Spec interface.
     */
    @FunctionalInterface
    interface PartneredSpec {
        /**
         * Obtain the partner.
         *
         * @return the partner.
         */
        GordianFactory getPartner();
    }

    /**
     * Factory and Digest definition.
     */
    static class FactoryDigestSpec
            implements FactorySpec<GordianNewDigestSpec>, PartneredSpec {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The partner.
         */
        private final GordianFactory thePartner;

        /**
         * The digestSpec.
         */
        private final GordianNewDigestSpec theDigestSpec;

        /**
         * Constructor.
         *
         * @param pFactory    the factory
         * @param pPartner    the partner
         * @param pDigestSpec the digestSpec
         */
        FactoryDigestSpec(final GordianFactory pFactory,
                          final GordianFactory pPartner,
                          final GordianNewDigestSpec pDigestSpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theDigestSpec = pDigestSpec;
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianFactory getPartner() {
            return thePartner;
        }

        @Override
        public GordianCoreDigestSpec getSpec() {
            return (GordianCoreDigestSpec) theDigestSpec;
        }

        @Override
        public String toString() {
            return theDigestSpec.toString();
        }
    }

    /**
     * Factory and Mac definition.
     */
    static class FactoryMacSpec
            implements FactorySpec<GordianNewMacSpec>, PartneredSpec {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The partner.
         */
        private final GordianFactory thePartner;

        /**
         * The macSpec.
         */
        private final GordianNewMacSpec theMacSpec;

        /**
         * The key.
         */
        private volatile GordianCoreKey<GordianNewMacSpec> theKey;

        /**
         * The partnerKey.
         */
        private volatile GordianKey<GordianNewMacSpec> thePartnerKey;

        /**
         * Constructor.
         *
         * @param pFactory the factory
         * @param pPartner the partner
         * @param pMacSpec the macSpec
         */
        FactoryMacSpec(final GordianFactory pFactory,
                       final GordianFactory pPartner,
                       final GordianNewMacSpec pMacSpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theMacSpec = pMacSpec;
        }

        /**
         * Obtain (or create) the key for the FactoryMacSpec.
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewMacSpec> getKey() throws GordianException {
            /* Return key if it exists */
            GordianCoreKey<GordianNewMacSpec> myKey = theKey;
            if (myKey != null) {
                return myKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myKey = theKey;
                if (myKey != null) {
                    return myKey;
                }

                /* Generate the key */
                GordianMacFactory myFactory = theFactory.getMacFactory();
                GordianKeyGenerator<GordianNewMacSpec> myGenerator = myFactory.getKeyGenerator(theMacSpec);
                myKey = (GordianCoreKey<GordianNewMacSpec>) myGenerator.generateKey();
                theKey = myKey;
                return myKey;
            }
        }

        /**
         * Obtain (or translate) the key for the Partner.
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewMacSpec> getPartnerKey() throws GordianException {
            /* Return key if it exists */
            GordianKey<GordianNewMacSpec> myPartnerKey = thePartnerKey;
            if (myPartnerKey != null || thePartner == null) {
                return myPartnerKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myPartnerKey = thePartnerKey;
                if (myPartnerKey != null) {
                    return myPartnerKey;
                }

                /* Build the key */
                GordianMacFactory myFactory = thePartner.getMacFactory();
                GordianCoreKeyGenerator<GordianNewMacSpec> myGenerator
                        = (GordianCoreKeyGenerator<GordianNewMacSpec>) myFactory.getKeyGenerator(theMacSpec);
                myPartnerKey = myGenerator.buildKeyFromBytes(theKey.getKeyBytes());
                thePartnerKey = myPartnerKey;
                return myPartnerKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianFactory getPartner() {
            return thePartner;
        }

        @Override
        public GordianNewMacSpec getSpec() {
            return theMacSpec;
        }

        @Override
        public String toString() {
            return theMacSpec.toString();
        }
    }

    /**
     * Factory and symKey definition.
     */
    static class FactorySymKeySpec
            implements FactorySpec<GordianNewSymKeySpec>, PartneredSpec {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The partner.
         */
        private final GordianFactory thePartner;

        /**
         * The symKeySpec.
         */
        private final GordianNewSymKeySpec theSymKeySpec;

        /**
         * The key.
         */
        private volatile GordianCoreKey<GordianNewSymKeySpec> theKey;

        /**
         * The partnerKey.
         */
        private volatile GordianKey<GordianNewSymKeySpec> thePartnerKey;

        /**
         * Constructor.
         *
         * @param pFactory    the factory
         * @param pPartner    the partner
         * @param pSymKeySpec the symKeySpec
         */
        FactorySymKeySpec(final GordianFactory pFactory,
                          final GordianFactory pPartner,
                          final GordianNewSymKeySpec pSymKeySpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theSymKeySpec = pSymKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewSymKeySpec> getKey() throws GordianException {
            /* Return key if it exists */
            GordianCoreKey<GordianNewSymKeySpec> myKey = theKey;
            if (myKey != null) {
                return myKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myKey = theKey;
                if (myKey != null) {
                    return myKey;
                }

                /* Generate the key */
                GordianCipherFactory myFactory = theFactory.getCipherFactory();
                GordianKeyGenerator<GordianNewSymKeySpec> myGenerator = myFactory.getKeyGenerator(theSymKeySpec);
                myKey = (GordianCoreKey<GordianNewSymKeySpec>) myGenerator.generateKey();
                theKey = myKey;
                return myKey;
            }
        }

        /**
         * Obtain (or translate) the key for the Partner.
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewSymKeySpec> getPartnerKey() throws GordianException {
            /* Return key if it exists */
            GordianKey<GordianNewSymKeySpec> myPartnerKey = thePartnerKey;
            if (myPartnerKey != null || thePartner == null) {
                return myPartnerKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myPartnerKey = thePartnerKey;
                if (myPartnerKey != null) {
                    return myPartnerKey;
                }

                /* Build the key */
                GordianCipherFactory myFactory = thePartner.getCipherFactory();
                GordianCoreKeyGenerator<GordianNewSymKeySpec> myGenerator
                        = (GordianCoreKeyGenerator<GordianNewSymKeySpec>) myFactory.getKeyGenerator(theSymKeySpec);
                myPartnerKey = myGenerator.buildKeyFromBytes(theKey.getKeyBytes());
                thePartnerKey = myPartnerKey;
                return myPartnerKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianFactory getPartner() {
            return thePartner;
        }

        @Override
        public GordianCoreSymKeySpec getSpec() {
            return (GordianCoreSymKeySpec) theSymKeySpec;
        }

        @Override
        public String toString() {
            return theSymKeySpec.toString();
        }
    }

    /**
     * Factory and symCipher definition.
     */
    static class FactorySymCipherSpec
            implements FactorySpec<GordianNewSymCipherSpec>, PartneredSpec {
        /**
         * The owner.
         */
        private final FactorySymKeySpec theOwner;

        /**
         * The symKeySpec.
         */
        private final GordianNewSymCipherSpec theCipherSpec;

        /**
         * supported by partner?
         */
        private final boolean hasPartner;

        /**
         * Constructor.
         *
         * @param pOwner      the owner
         * @param pCipherSpec the symCipherSpec
         * @param pPredicate  the partner predicate
         */
        FactorySymCipherSpec(final FactorySymKeySpec pOwner,
                             final GordianNewSymCipherSpec pCipherSpec,
                             final Predicate<GordianNewSymCipherSpec> pPredicate) {
            /* Store parameters */
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;

            /* Determine whether we have partner support */
            hasPartner = pPredicate != null && pPredicate.test(pCipherSpec);
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewSymKeySpec> getKey() throws GordianException {
            return theOwner.getKey();
        }

        /**
         * Obtain (or translate) the key for the Partner.
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewSymKeySpec> getPartnerKey() throws GordianException {
            return theOwner.getPartnerKey();
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        public FactorySymKeySpec getOwner() {
            return theOwner;
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianFactory getPartner() {
            return hasPartner ? theOwner.getPartner() : null;
        }

        @Override
        public GordianCoreSymCipherSpec getSpec() {
            return (GordianCoreSymCipherSpec) theCipherSpec;
        }

        @Override
        public String toString() {
            return theCipherSpec.toString();
        }
    }

    /**
     * Factory and symPBECipher definition.
     */
    static class FactorySymPBECipherSpec
            implements FactorySpec<GordianNewPBESpec> {
        /**
         * The owner.
         */
        private final FactorySymCipherSpec theOwner;

        /**
         * The pbeSpec.
         */
        private final GordianNewPBESpec thePBESpec;

        /**
         * Constructor.
         *
         * @param pOwner   the owner
         * @param pPBESpec the symCipherSpec
         */
        FactorySymPBECipherSpec(final FactorySymCipherSpec pOwner,
                                final GordianNewPBESpec pPBESpec) {
            /* Store parameters */
            theOwner = pOwner;
            thePBESpec = pPBESpec;
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        public FactorySymCipherSpec getOwner() {
            return theOwner;
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianCorePBESpec getSpec() {
            return (GordianCorePBESpec) thePBESpec;
        }

        @Override
        public String toString() {
            return thePBESpec.toString();
        }
    }

    /**
     * Factory and streamKey definition.
     */
    static class FactoryStreamKeySpec
            implements FactorySpec<GordianNewStreamKeySpec>, PartneredSpec {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The partner.
         */
        private final GordianFactory thePartner;

        /**
         * The streamKeyType.
         */
        private final GordianNewStreamKeySpec theKeySpec;

        /**
         * The key.
         */
        private volatile GordianCoreKey<GordianNewStreamKeySpec> theKey;

        /**
         * The partnerKey.
         */
        private volatile GordianKey<GordianNewStreamKeySpec> thePartnerKey;

        /**
         * Constructor.
         *
         * @param pFactory the factory
         * @param pPartner the partner
         * @param pKeySpec the keySpec
         */
        FactoryStreamKeySpec(final GordianFactory pFactory,
                             final GordianFactory pPartner,
                             final GordianNewStreamKeySpec pKeySpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theKeySpec = pKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewStreamKeySpec> getKey() throws GordianException {
            /* Return key if it exists */
            GordianCoreKey<GordianNewStreamKeySpec> myKey = theKey;
            if (myKey != null) {
                return myKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myKey = theKey;
                if (myKey != null) {
                    return myKey;
                }

                /* Generate the key */
                GordianCipherFactory myFactory = theFactory.getCipherFactory();
                GordianKeyGenerator<GordianNewStreamKeySpec> myGenerator = myFactory.getKeyGenerator(theKeySpec);
                myKey = (GordianCoreKey<GordianNewStreamKeySpec>) myGenerator.generateKey();
                theKey = myKey;
                return myKey;
            }
        }

        /**
         * Obtain (or translate) the key for the Partner.
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewStreamKeySpec> getPartnerKey() throws GordianException {
            /* Return key if it exists */
            GordianKey<GordianNewStreamKeySpec> myPartnerKey = thePartnerKey;
            if (myPartnerKey != null || thePartner == null) {
                return myPartnerKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                myPartnerKey = thePartnerKey;
                if (myPartnerKey != null) {
                    return myPartnerKey;
                }

                /* Build the key */
                GordianCipherFactory myFactory = thePartner.getCipherFactory();
                GordianCoreKeyGenerator<GordianNewStreamKeySpec> myGenerator
                        = (GordianCoreKeyGenerator<GordianNewStreamKeySpec>) myFactory.getKeyGenerator(theKeySpec);
                myPartnerKey = myGenerator.buildKeyFromBytes(theKey.getKeyBytes());
                thePartnerKey = myPartnerKey;
                return myPartnerKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianFactory getPartner() {
            return thePartner;
        }

        @Override
        public GordianCoreStreamKeySpec getSpec() {
            return (GordianCoreStreamKeySpec) theKeySpec;
        }

        /**
         * Does this keySpec have an AAD Version?
         *
         * @return true/false
         */
        public boolean hasAADVersion() {
            if (((GordianCoreStreamKeySpec) theKeySpec).supportsAEAD()) {
                final GordianNewStreamCipherSpec myAADSpec = GordianStreamCipherSpecBuilder.stream(theKeySpec, true);
                return theFactory.getCipherFactory().supportedStreamCipherSpecs().test(myAADSpec);
            }
            return false;
        }

        @Override
        public String toString() {
            return theKeySpec.toString();
        }
    }

    /**
     * Factory and streamCipher definition.
     */
    static class FactoryStreamCipherSpec
            implements FactorySpec<GordianNewStreamCipherSpec>, PartneredSpec {
        /**
         * The owner.
         */
        private final FactoryStreamKeySpec theOwner;

        /**
         * The streamKeySpec.
         */
        private final GordianNewStreamCipherSpec theCipherSpec;

        /**
         * Constructor.
         *
         * @param pOwner      the owner
         * @param pCipherSpec the cipherSpec
         */
        FactoryStreamCipherSpec(final FactoryStreamKeySpec pOwner,
                                final GordianNewStreamCipherSpec pCipherSpec) {
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        public FactoryStreamKeySpec getOwner() {
            return theOwner;
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianFactory getPartner() {
            return theOwner.getPartner();
        }

        @Override
        public GordianCoreStreamCipherSpec getSpec() {
            return (GordianCoreStreamCipherSpec) theCipherSpec;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         *
         * @return the key
         * @throws GordianException on error
         */
        GordianKey<GordianNewStreamKeySpec> getKey() throws GordianException {
            return theOwner.getKey();
        }

        @Override
        public String toString() {
            return getFactory().getFactoryType() + ":" + theCipherSpec;
        }
    }

    /**
     * Factory and streamPBECipher definition.
     */
    static class FactoryStreamPBECipherSpec
            implements FactorySpec<GordianNewPBESpec> {
        /**
         * The owner.
         */
        private final FactoryStreamCipherSpec theOwner;

        /**
         * The pbeSpec.
         */
        private final GordianNewPBESpec thePBESpec;

        /**
         * Constructor.
         *
         * @param pOwner   the owner
         * @param pPBESpec the symCipherSpec
         */
        FactoryStreamPBECipherSpec(final FactoryStreamCipherSpec pOwner,
                                   final GordianNewPBESpec pPBESpec) {
            /* Store parameters */
            theOwner = pOwner;
            thePBESpec = pPBESpec;
        }

        /**
         * Obtain the owner.
         *
         * @return the owner
         */
        public FactoryStreamCipherSpec getOwner() {
            return theOwner;
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianCorePBESpec getSpec() {
            return (GordianCorePBESpec) thePBESpec;
        }

        @Override
        public String toString() {
            return thePBESpec.toString();
        }
    }

    /**
     * Factory and randomType definition.
     */
    static class FactoryRandomType
            implements FactorySpec<GordianNewRandomType> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The randomType.
         */
        private final GordianNewRandomType theRandomType;

        /**
         * The length.
         */
        private final GordianLength theLength;

        /**
         * The list of randomSpecs.
         */
        private final List<FactoryRandomSpec> theSpecs;

        /**
         * Constructor.
         *
         * @param pFactory    the factory
         * @param pRandomType the randomType
         */
        FactoryRandomType(final GordianFactory pFactory,
                          final GordianNewRandomType pRandomType) {
            this(pFactory, pRandomType, null);
        }

        /**
         * Constructor.
         *
         * @param pFactory    the factory
         * @param pRandomType the randomType
         * @param pLength     the keyLength
         */
        FactoryRandomType(final GordianFactory pFactory,
                          final GordianNewRandomType pRandomType,
                          final GordianLength pLength) {
            theFactory = pFactory;
            theRandomType = pRandomType;
            theLength = pLength;
            theSpecs = new ArrayList<>();
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianNewRandomType getSpec() {
            return theRandomType;
        }

        /**
         * Obtain the length.
         *
         * @return the length
         */
        public GordianLength getLength() {
            return theLength;
        }

        /**
         * Obtain the randomList.
         *
         * @return the random list
         */
        List<FactoryRandomSpec> getSpecs() {
            return theSpecs;
        }

        @Override
        public String toString() {
            return theRandomType.toString() + (theLength == null ? "" : "-" + theLength);
        }
    }

    /**
     * Factory and randomSpec definition.
     */
    static class FactoryRandomSpec
            implements FactorySpec<GordianNewRandomSpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The randomSpec.
         */
        private final GordianNewRandomSpec theRandomSpec;

        /**
         * Constructor.
         *
         * @param pFactory    the factory
         * @param pRandomSpec the randomSpec
         */
        FactoryRandomSpec(final GordianFactory pFactory,
                          final GordianNewRandomSpec pRandomSpec) {
            theFactory = pFactory;
            theRandomSpec = pRandomSpec;
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianNewRandomSpec getSpec() {
            return theRandomSpec;
        }

        @Override
        public String toString() {
            return theRandomSpec.toString();
        }
    }

    /**
     * Obtain the list of digests to test.
     *
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @return the list
     */
    static List<FactoryDigestSpec> digestProvider(final GordianFactory pFactory,
                                                  final GordianFactory pPartner) {
        /* Loop through the possible digestSpecs */
        final List<FactoryDigestSpec> myResult = new ArrayList<>();
        final GordianCoreDigestFactory myDigestFactory = (GordianCoreDigestFactory) pFactory.getDigestFactory();
        final Predicate<GordianNewDigestSpec> myPredicate = pPartner.getDigestFactory().supportedDigestSpecs();
        for (GordianNewDigestSpec mySpec : myDigestFactory.listAllSupportedSpecs()) {
            /* Determine whether the digestSpec is supported by the partner */
            GordianFactory myPartner = myPredicate.test(mySpec) ? pPartner : null;

            /* Add the digestSpec */
            myResult.add(new FactoryDigestSpec(pFactory, myPartner, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of macs to test.
     *
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @param pKeyLen  the keyLength
     * @return the list
     */
    static List<FactoryMacSpec> macProvider(final GordianFactory pFactory,
                                            final GordianFactory pPartner,
                                            final GordianLength pKeyLen) {
        /* Loop through the possible macSpecs */
        final List<FactoryMacSpec> myResult = new ArrayList<>();
        final GordianCoreMacFactory myMacFactory = (GordianCoreMacFactory) pFactory.getMacFactory();
        final Predicate<GordianNewMacSpec> myPredicate = pPartner.getMacFactory().supportedMacSpecs();
        for (GordianNewMacSpec mySpec : myMacFactory.listAllSupportedSpecs(pKeyLen)) {
            /* Determine whether the macSpec is supported by the partner */
            GordianFactory myPartner = myPredicate.test(mySpec) ? pPartner : null;

            /* Add the macSpec */
            myResult.add(new FactoryMacSpec(pFactory, myPartner, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symKeySpecs to test.
     *
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @param pKeyLen  the keyLength
     * @return the list
     */
    static List<FactorySymKeySpec> symKeyProvider(final GordianFactory pFactory,
                                                  final GordianFactory pPartner,
                                                  final GordianLength pKeyLen) {
        /* Loop through the possible keySpecs */
        final List<FactorySymKeySpec> myResult = new ArrayList<>();
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) pFactory.getCipherFactory();
        final Predicate<GordianNewSymKeySpec> myPredicate = pPartner.getCipherFactory().supportedSymKeySpecs();
        for (GordianNewSymKeySpec mySpec : myCipherFactory.listAllSupportedSymKeySpecs(pKeyLen)) {
            /* Determine whether the keySpec is supported by the partner */
            GordianFactory myPartner = myPredicate.test(mySpec) ? pPartner : null;

            /* Add the symKeySpec */
            myResult.add(new FactorySymKeySpec(pFactory, myPartner, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symKeyCiphers to test.
     *
     * @param pKeySpec the keySpec
     * @return the list
     */
    static List<FactorySymCipherSpec> symCipherProvider(final FactorySymKeySpec pKeySpec) {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianNewSymKeySpec mySpec = pKeySpec.getSpec();
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        final List<FactorySymCipherSpec> myResult = new ArrayList<>();
        final GordianFactory myPartner = pKeySpec.getPartner();
        final Predicate<GordianNewSymCipherSpec> myPredicate
                = myPartner == null ? null : myPartner.getCipherFactory().supportedSymCipherSpecs();

        /* Build the list */
        for (GordianNewSymCipherSpec myCipherSpec : myCipherFactory.listAllSupportedSymCipherSpecs(mySpec)) {
            myResult.add(new FactorySymCipherSpec(pKeySpec, myCipherSpec, myPredicate));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symPBEKeyCiphers to test.
     *
     * @param pCipherSpec the cipherSpec
     * @return the list
     */
    static List<FactorySymPBECipherSpec> symPBECipherProvider(final FactorySymCipherSpec pCipherSpec) {
        /* Access details */
        final List<FactorySymPBECipherSpec> myResult = new ArrayList<>();

        /* Build the list */
        final GordianNewDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
        final GordianNewPBESpecBuilder myPBEBuilder = GordianCorePBESpecBuilder.newInstance();
        GordianNewPBESpec myPBESpec = myPBEBuilder.pbKDF2(myBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = myPBEBuilder.pkcs12(myBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = myPBEBuilder.scrypt(16, 1, 1);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = myPBEBuilder.argon2(1, 4096, 2);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of streamKeySpecs to test.
     *
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @param pKeyLen  the keyLength
     * @return the list
     */
    static List<FactoryStreamKeySpec> streamKeyProvider(final GordianFactory pFactory,
                                                        final GordianFactory pPartner,
                                                        final GordianLength pKeyLen) {
        /* Loop through the possible keySpecs */
        final List<FactoryStreamKeySpec> myResult = new ArrayList<>();
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) pFactory.getCipherFactory();
        final Predicate<GordianNewStreamKeySpec> myPredicate = pPartner.getCipherFactory().supportedStreamKeySpecs();
        for (GordianNewStreamKeySpec mySpec : myCipherFactory.listAllSupportedStreamKeySpecs(pKeyLen)) {
            /* Determine whether the keySpec is supported by the partner */
            GordianFactory myPartner = myPredicate.test(mySpec) ? pPartner : null;

            /* Add the streamKeySpec */
            myResult.add(new FactoryStreamKeySpec(pFactory, myPartner, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of streamPBEKeyCiphers to test.
     *
     * @param pCipherSpec the cipherSpec
     * @return the list
     */
    static List<FactoryStreamPBECipherSpec> streamPBECipherProvider(final FactoryStreamCipherSpec pCipherSpec) {
        /* Access details */
        final List<FactoryStreamPBECipherSpec> myResult = new ArrayList<>();

        /* Build the list */
        final GordianNewDigestSpecBuilder myBuilder = GordianCoreDigestSpecBuilder.newInstance();
        final GordianNewPBESpecBuilder myPBEBuilder = GordianCorePBESpecBuilder.newInstance();
        GordianNewPBESpec myPBESpec = myPBEBuilder.pbKDF2(myBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = myPBEBuilder.pkcs12(myBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = myPBEBuilder.scrypt(16, 1, 1);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = myPBEBuilder.argon2(1, 4096, 2);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of randomSpecs to test.
     *
     * @param pFactory the factory
     * @param pType    the random type
     * @return the randomType
     */
    static FactoryRandomType randomProvider(final GordianFactory pFactory,
                                            final GordianNewRandomType pType) {
        /* Create the random type */
        final GordianCoreRandomFactory myRandomFactory = (GordianCoreRandomFactory) pFactory.getRandomFactory();
        final FactoryRandomType myFactoryType = new FactoryRandomType(pFactory, pType);

        /* Populate the list of specs */
        final List<FactoryRandomSpec> myList = myFactoryType.getSpecs();
        for (GordianNewRandomSpec mySpec : myRandomFactory.listAllSupportedRandomSpecs(pType)) {
            /* Add the randomSpec */
            myList.add(new FactoryRandomSpec(pFactory, mySpec));
        }

        /* Return the type */
        return myFactoryType;
    }

    /**
     * Obtain the list of randomSpecs to test.
     *
     * @param pFactory the factory
     * @param pType    the random type
     * @param pKeyLen  the keyLength
     * @return the randomType
     */
    static FactoryRandomType randomProvider(final GordianFactory pFactory,
                                            final GordianNewRandomType pType,
                                            final GordianLength pKeyLen) {
        /* Create the random type */
        final GordianCoreRandomFactory myRandomFactory = (GordianCoreRandomFactory) pFactory.getRandomFactory();
        final FactoryRandomType myFactoryType = new FactoryRandomType(pFactory, pType, pKeyLen);

        /* Populate the list of specs */
        final List<FactoryRandomSpec> myList = myFactoryType.getSpecs();
        for (GordianNewRandomSpec mySpec : myRandomFactory.listAllSupportedRandomSpecs(pType, pKeyLen)) {
            /* Add the randomSpec */
            myList.add(new FactoryRandomSpec(pFactory, mySpec));
        }

        /* Return the type */
        return myFactoryType;
    }
}
