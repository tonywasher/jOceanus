/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPBESpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPBESpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomType;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.random.GordianCoreRandomFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Symmetric Tests Data Classes.
 */
class SymmetricStore {
    /**
     * Factory Spec interface.
     * @param <T> the object type
     */
    interface FactorySpec<T> {
        /**
         * Obtain the factory.
         * @return the factory.
         */
        GordianFactory getFactory();

        /**
         * Obtain the spec.
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
         * @return the partner.
         */
        GordianFactory getPartner();
    }

    /**
     * Factory and Digest definition.
     */
    static class FactoryDigestSpec
            implements FactorySpec<GordianDigestSpec>, PartneredSpec {
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
        private final GordianDigestSpec theDigestSpec;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPartner the partner
         * @param pDigestSpec the digestSpec
         */
        FactoryDigestSpec(final GordianFactory pFactory,
                          final GordianFactory pPartner,
                          final GordianDigestSpec pDigestSpec) {
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
        public GordianDigestSpec getSpec() {
            return theDigestSpec;
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
            implements FactorySpec<GordianMacSpec>, PartneredSpec {
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
        private final GordianMacSpec theMacSpec;

        /**
         * The key.
         */
        private volatile GordianCoreKey<GordianMacSpec> theKey;

        /**
         * The partnerKey.
         */
        private volatile GordianKey<GordianMacSpec> thePartnerKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPartner the partner
         * @param pMacSpec the macSpec
         */
        FactoryMacSpec(final GordianFactory pFactory,
                       final GordianFactory pPartner,
                       final GordianMacSpec pMacSpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theMacSpec = pMacSpec;
        }

        /**
         * Obtain (or create) the key for the FactoryMacSpec.
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianMacSpec> getKey() throws OceanusException {
            /* Return key if it exists */
            GordianCoreKey<GordianMacSpec> myKey = theKey;
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
                GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(theMacSpec);
                myKey = (GordianCoreKey<GordianMacSpec>) myGenerator.generateKey();
                theKey = myKey;
                return myKey;
            }
        }

        /**
         * Obtain (or translate) the key for the Partner.
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianMacSpec> getPartnerKey() throws OceanusException {
            /* Return key if it exists */
            GordianKey<GordianMacSpec> myPartnerKey = thePartnerKey;
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
                GordianCoreKeyGenerator<GordianMacSpec> myGenerator
                        = (GordianCoreKeyGenerator<GordianMacSpec>) myFactory.getKeyGenerator(theMacSpec);
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
        public GordianMacSpec getSpec() {
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
            implements FactorySpec<GordianSymKeySpec>, PartneredSpec {
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
        private final GordianSymKeySpec theSymKeySpec;

        /**
         * The key.
         */
        private volatile GordianCoreKey<GordianSymKeySpec> theKey;

        /**
         * The partnerKey.
         */
        private volatile GordianKey<GordianSymKeySpec> thePartnerKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPartner the partner
         * @param pSymKeySpec the symKeySpec
         */
        FactorySymKeySpec(final GordianFactory pFactory,
                          final GordianFactory pPartner,
                          final GordianSymKeySpec pSymKeySpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theSymKeySpec = pSymKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getKey() throws OceanusException {
            /* Return key if it exists */
            GordianCoreKey<GordianSymKeySpec> myKey = theKey;
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
                GordianKeyGenerator<GordianSymKeySpec> myGenerator = myFactory.getKeyGenerator(theSymKeySpec);
                myKey = (GordianCoreKey<GordianSymKeySpec>) myGenerator.generateKey();
                theKey = myKey;
                return myKey;
            }
        }

        /**
         * Obtain (or translate) the key for the Partner.
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getPartnerKey() throws OceanusException {
            /* Return key if it exists */
            GordianKey<GordianSymKeySpec> myPartnerKey = thePartnerKey;
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
                GordianCoreKeyGenerator<GordianSymKeySpec> myGenerator
                        = (GordianCoreKeyGenerator<GordianSymKeySpec>) myFactory.getKeyGenerator(theSymKeySpec);
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
        public GordianSymKeySpec getSpec() {
            return theSymKeySpec;
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
            implements FactorySpec<GordianSymCipherSpec>, PartneredSpec {
        /**
         * The owner.
         */
        private final FactorySymKeySpec theOwner;

        /**
         * The symKeySpec.
         */
        private final GordianSymCipherSpec theCipherSpec;

        /**
         * supported by partner?
         */
        private final boolean hasPartner;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pCipherSpec the symCipherSpec
         * @param pPredicate the partner predicate
         */
        FactorySymCipherSpec(final FactorySymKeySpec pOwner,
                             final GordianSymCipherSpec pCipherSpec,
                             final Predicate<GordianSymCipherSpec> pPredicate) {
            /* Store parameters */
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;

            /* Determine whether we have partner support */
            hasPartner = pPredicate != null && pPredicate.test(pCipherSpec);
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getKey() throws OceanusException {
            return theOwner.getKey();
        }

        /**
         * Obtain (or translate) the key for the Partner.
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getPartnerKey() throws OceanusException {
            return theOwner.getPartnerKey();
        }

        /**
         * Obtain the owner.
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
        public GordianSymCipherSpec getSpec() {
            return theCipherSpec;
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
            implements FactorySpec<GordianPBESpec> {
        /**
         * The owner.
         */
        private final FactorySymCipherSpec theOwner;

        /**
         * The pbeSpec.
         */
        private final GordianPBESpec thePBESpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pPBESpec the symCipherSpec
         */
        FactorySymPBECipherSpec(final FactorySymCipherSpec pOwner,
                                final GordianPBESpec pPBESpec) {
            /* Store parameters */
            theOwner = pOwner;
            thePBESpec = pPBESpec;
        }

        /**
         * Obtain the owner.
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
        public GordianPBESpec getSpec() {
            return thePBESpec;
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
            implements FactorySpec<GordianStreamKeySpec>, PartneredSpec {
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
        private final GordianStreamKeySpec theKeySpec;

        /**
         * The key.
         */
        private volatile GordianCoreKey<GordianStreamKeySpec> theKey;

        /**
         * The partnerKey.
         */
        private volatile GordianKey<GordianStreamKeySpec> thePartnerKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pPartner the partner
         * @param pKeySpec the keySpec
         */
        FactoryStreamKeySpec(final GordianFactory pFactory,
                             final GordianFactory pPartner,
                             final GordianStreamKeySpec pKeySpec) {
            theFactory = pFactory;
            thePartner = pPartner;
            theKeySpec = pKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianStreamKeySpec> getKey() throws OceanusException {
            /* Return key if it exists */
            GordianCoreKey<GordianStreamKeySpec> myKey = theKey;
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
                GordianKeyGenerator<GordianStreamKeySpec> myGenerator = myFactory.getKeyGenerator(theKeySpec);
                myKey = (GordianCoreKey<GordianStreamKeySpec>) myGenerator.generateKey();
                theKey = myKey;
                return myKey;
            }
        }

        /**
         * Obtain (or translate) the key for the Partner.
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianStreamKeySpec> getPartnerKey() throws OceanusException {
            /* Return key if it exists */
            GordianKey<GordianStreamKeySpec> myPartnerKey = thePartnerKey;
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
                GordianCoreKeyGenerator<GordianStreamKeySpec> myGenerator
                        = (GordianCoreKeyGenerator<GordianStreamKeySpec>) myFactory.getKeyGenerator(theKeySpec);
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
        public GordianStreamKeySpec getSpec() {
            return theKeySpec;
        }

        /**
         * Does this keySpec have an AAD Mode?
         * @return true/false
         */
        public boolean hasAAD() {
            if (theKeySpec.supportsAAD()) {
                final GordianStreamCipherSpec myAADSpec = GordianStreamCipherSpecBuilder.stream(theKeySpec, true);
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
            implements FactorySpec<GordianStreamCipherSpec>, PartneredSpec {
        /**
         * The owner.
         */
        private final FactoryStreamKeySpec theOwner;

        /**
         * The streamKeySpec.
         */
        private final GordianStreamCipherSpec theCipherSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pCipherSpec the cipherSpec
         */
        FactoryStreamCipherSpec(final FactoryStreamKeySpec pOwner,
                                final GordianStreamCipherSpec pCipherSpec) {
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;
        }

        /**
         * Obtain the owner.
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
        public GordianStreamCipherSpec getSpec() {
            return theCipherSpec;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianStreamKeySpec> getKey() throws OceanusException {
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
            implements FactorySpec<GordianPBESpec> {
        /**
         * The owner.
         */
        private final FactoryStreamCipherSpec theOwner;

        /**
         * The pbeSpec.
         */
        private final GordianPBESpec thePBESpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pPBESpec the symCipherSpec
         */
        FactoryStreamPBECipherSpec(final FactoryStreamCipherSpec pOwner,
                                   final GordianPBESpec pPBESpec) {
            /* Store parameters */
            theOwner = pOwner;
            thePBESpec = pPBESpec;
        }

        /**
         * Obtain the owner.
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
        public GordianPBESpec getSpec() {
            return thePBESpec;
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
            implements FactorySpec<GordianRandomType> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The randomType.
         */
        private final GordianRandomType theRandomType;

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
         * @param pFactory the factory
         * @param pRandomType the randomType
         */
        FactoryRandomType(final GordianFactory pFactory,
                          final GordianRandomType pRandomType) {
            this(pFactory, pRandomType, null);
        }

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pRandomType the randomType
         * @param pLength the keyLength
         */
        FactoryRandomType(final GordianFactory pFactory,
                          final GordianRandomType pRandomType,
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
        public GordianRandomType getSpec() {
            return theRandomType;
        }

        /**
         * Obtain the length.
         * @return the length
         */
        public GordianLength getLength() {
            return theLength;
        }

        /**
         * Obtain the randomList.
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
            implements FactorySpec<GordianRandomSpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The randomSpec.
         */
        private final GordianRandomSpec theRandomSpec;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pRandomSpec the randomSpec
         */
        FactoryRandomSpec(final GordianFactory pFactory,
                          final GordianRandomSpec pRandomSpec) {
            theFactory = pFactory;
            theRandomSpec = pRandomSpec;
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianRandomSpec getSpec() {
            return theRandomSpec;
        }

        @Override
        public String toString() {
            return theRandomSpec.toString();
        }
    }

    /**
     * Obtain the list of digests to test.
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @return the list
     */
    static List<FactoryDigestSpec> digestProvider(final GordianFactory pFactory,
                                                  final GordianFactory pPartner) {
        /* Loop through the possible digestSpecs */
        final List<FactoryDigestSpec> myResult = new ArrayList<>();
        final GordianCoreDigestFactory myDigestFactory = (GordianCoreDigestFactory) pFactory.getDigestFactory();
        final Predicate<GordianDigestSpec> myPredicate = pPartner.getDigestFactory().supportedDigestSpecs();
        for (GordianDigestSpec mySpec : myDigestFactory.listAllSupportedSpecs()) {
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
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @param pKeyLen the keyLength
     * @return the list
     */
    static List<FactoryMacSpec> macProvider(final GordianFactory pFactory,
                                            final GordianFactory pPartner,
                                            final GordianLength pKeyLen) {
        /* Loop through the possible macSpecs */
        final List<FactoryMacSpec> myResult = new ArrayList<>();
        final GordianCoreMacFactory myMacFactory = (GordianCoreMacFactory) pFactory.getMacFactory();
        final Predicate<GordianMacSpec> myPredicate = pPartner.getMacFactory().supportedMacSpecs();
        for (GordianMacSpec mySpec : myMacFactory.listAllSupportedSpecs(pKeyLen)) {
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
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @param pKeyLen the keyLength
     * @return the list
     */
    static List<FactorySymKeySpec> symKeyProvider(final GordianFactory pFactory,
                                                  final GordianFactory pPartner,
                                                  final GordianLength pKeyLen) {
        /* Loop through the possible keySpecs */
        final List<FactorySymKeySpec> myResult = new ArrayList<>();
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) pFactory.getCipherFactory();
        final Predicate<GordianSymKeySpec> myPredicate = pPartner.getCipherFactory().supportedSymKeySpecs();
        for (GordianSymKeySpec mySpec : myCipherFactory.listAllSupportedSymKeySpecs(pKeyLen)) {
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
     * @param pKeySpec the keySpec
     * @return the list
     */
    static List<FactorySymCipherSpec> symCipherProvider(final FactorySymKeySpec pKeySpec) {
        /* Access details */
        final GordianFactory myFactory = pKeySpec.getFactory();
        final GordianSymKeySpec mySpec = pKeySpec.getSpec();
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) myFactory.getCipherFactory();
        final List<FactorySymCipherSpec> myResult = new ArrayList<>();
        final GordianFactory myPartner = pKeySpec.getPartner();
        final Predicate<GordianSymCipherSpec> myPredicate
                = myPartner == null ? null : myPartner.getCipherFactory().supportedSymCipherSpecs();

        /* Build the list */
        for (GordianSymCipherSpec myCipherSpec : myCipherFactory.listAllSupportedSymCipherSpecs(mySpec)) {
            myResult.add(new FactorySymCipherSpec(pKeySpec, myCipherSpec, myPredicate));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symPBEKeyCiphers to test.
     * @param pCipherSpec the cipherSpec
     * @return the list
     */
    static List<FactorySymPBECipherSpec> symPBECipherProvider(final FactorySymCipherSpec pCipherSpec) {
        /* Access details */
        final List<FactorySymPBECipherSpec> myResult = new ArrayList<>();

        /* Build the list */
        GordianPBESpec myPBESpec = GordianPBESpecBuilder.pbKDF2(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = GordianPBESpecBuilder.pkcs12(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = GordianPBESpecBuilder.scrypt(16, 1, 1);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = GordianPBESpecBuilder.argon2(1, 4096, 2);
        myResult.add(new FactorySymPBECipherSpec(pCipherSpec, myPBESpec));

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of streamKeySpecs to test.
     * @param pFactory the factory
     * @param pPartner the partner factory
     * @param pKeyLen the keyLength
     * @return the list
     */
    static List<FactoryStreamKeySpec> streamKeyProvider(final GordianFactory pFactory,
                                                        final GordianFactory pPartner,
                                                        final GordianLength pKeyLen) {
        /* Loop through the possible keySpecs */
        final List<FactoryStreamKeySpec> myResult = new ArrayList<>();
        final GordianCoreCipherFactory myCipherFactory = (GordianCoreCipherFactory) pFactory.getCipherFactory();
        final Predicate<GordianStreamKeySpec> myPredicate = pPartner.getCipherFactory().supportedStreamKeySpecs();
        for (GordianStreamKeySpec mySpec : myCipherFactory.listAllSupportedStreamKeySpecs(pKeyLen)) {
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
     * @param pCipherSpec the cipherSpec
     * @return the list
     */
    static List<FactoryStreamPBECipherSpec> streamPBECipherProvider(final FactoryStreamCipherSpec pCipherSpec) {
        /* Access details */
        final List<FactoryStreamPBECipherSpec> myResult = new ArrayList<>();

        /* Build the list */
        GordianPBESpec myPBESpec = GordianPBESpecBuilder.pbKDF2(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = GordianPBESpecBuilder.pkcs12(GordianDigestSpecBuilder.sha2(GordianLength.LEN_512), 2048);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = GordianPBESpecBuilder.scrypt(16, 1, 1);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));
        myPBESpec = GordianPBESpecBuilder.argon2(1, 4096, 2);
        myResult.add(new FactoryStreamPBECipherSpec(pCipherSpec, myPBESpec));

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of randomSpecs to test.
     * @param pFactory the factory
     * @param pType the random type
     * @return the randomType
     */
    static FactoryRandomType randomProvider(final GordianFactory pFactory,
                                            final GordianRandomType pType) {
        /* Create the random type */
        final GordianCoreRandomFactory myRandomFactory = (GordianCoreRandomFactory) pFactory.getRandomFactory();
        final FactoryRandomType myFactoryType = new FactoryRandomType(pFactory, pType);

        /* Populate the list of specs */
        final List<FactoryRandomSpec> myList = myFactoryType.getSpecs();
        for (GordianRandomSpec mySpec : myRandomFactory.listAllSupportedRandomSpecs(pType)) {
            /* Add the randomSpec */
            myList.add(new FactoryRandomSpec(pFactory, mySpec));
        }

        /* Return the type */
        return myFactoryType;
    }

    /**
     * Obtain the list of randomSpecs to test.
     * @param pFactory the factory
     * @param pType the random type
     * @param pKeyLen the keyLength
     * @return the randomType
     */
    static FactoryRandomType randomProvider(final GordianFactory pFactory,
                                            final GordianRandomType pType,
                                            final GordianLength pKeyLen) {
        /* Create the random type */
        final GordianCoreRandomFactory myRandomFactory = (GordianCoreRandomFactory)  pFactory.getRandomFactory();
        final FactoryRandomType myFactoryType = new FactoryRandomType(pFactory, pType, pKeyLen);

        /* Populate the list of specs */
        final List<FactoryRandomSpec> myList = myFactoryType.getSpecs();
        for (GordianRandomSpec mySpec : myRandomFactory.listAllSupportedRandomSpecs(pType, pKeyLen)) {
            /* Add the randomSpec */
            myList.add(new FactoryRandomSpec(pFactory, mySpec));
        }

        /* Return the type */
        return myFactoryType;
    }
}
