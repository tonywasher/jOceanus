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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomType;
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
     * Factory and Digest definition.
     */
    static class FactoryDigestSpec
            implements FactorySpec<GordianDigestSpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The digestSpec.
         */
        private final GordianDigestSpec theDigestSpec;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pDigestSpec the digestSpec
         */
        FactoryDigestSpec(final GordianFactory pFactory,
                          final GordianDigestSpec pDigestSpec) {
            theFactory = pFactory;
            theDigestSpec = pDigestSpec;
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
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
            implements FactorySpec<GordianMacSpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The macSpec.
         */
        private final GordianMacSpec theMacSpec;

        /**
         * The key.
         */
        private GordianKey<GordianMacSpec> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pMacSpec the macSpec
         */
        FactoryMacSpec(final GordianFactory pFactory,
                       final GordianMacSpec pMacSpec) {
            theFactory = pFactory;
            theMacSpec = pMacSpec;
        }

        /**
         * Obtain (or create) the key for the FactoryMacSpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianMacSpec> getKey() throws OceanusException {
            /* Return key if it exists */
            if (theKey != null) {
                return theKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKey != null) {
                    return theKey;
                }

                /* Generate the key */
                GordianMacFactory myFactory = theFactory.getMacFactory();
                GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(theMacSpec);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
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
            implements FactorySpec<GordianSymKeySpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The symKeySpec.
         */
        private final GordianSymKeySpec theSymKeySpec;

        /**
         * The key.
         */
        private GordianKey<GordianSymKeySpec> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pSymKeySpec the symKeySpec
         */
        FactorySymKeySpec(final GordianFactory pFactory,
                          final GordianSymKeySpec pSymKeySpec) {
            theFactory = pFactory;
            theSymKeySpec = pSymKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getKey() throws OceanusException {
            /* Return key if it exists */
            if (theKey != null) {
                return theKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKey != null) {
                    return theKey;
                }

                /* Generate the key */
                GordianCipherFactory myFactory = theFactory.getCipherFactory();
                GordianKeyGenerator<GordianSymKeySpec> myGenerator = myFactory.getKeyGenerator(theSymKeySpec);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
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
            implements FactorySpec<GordianSymCipherSpec> {
        /**
         * The factory.
         */
        private final FactorySymKeySpec theOwner;

        /**
         * The symKeySpec.
         */
        private final GordianSymCipherSpec theCipherSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pCipherSpec the symCipherSpec
         */
        FactorySymCipherSpec(final FactorySymKeySpec pOwner,
                             final GordianSymCipherSpec pCipherSpec) {
            theOwner = pOwner;
            theCipherSpec = pCipherSpec;
        }

        /**
         * Obtain (or create) the key for the FactorySymKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianSymKeySpec> getKey() throws OceanusException {
            return theOwner.getKey();
        }

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
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
     * Factory and streamKey definition.
     */
    static class FactoryStreamKeySpec
            implements FactorySpec<GordianStreamKeySpec> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The streamKeyType.
         */
        private final GordianStreamKeySpec theKeySpec;

        /**
         * The key.
         */
        private GordianKey<GordianStreamKeySpec> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeySpec the keySpec
         */
        FactoryStreamKeySpec(final GordianFactory pFactory,
                             final GordianStreamKeySpec pKeySpec) {
            theFactory = pFactory;
            theKeySpec = pKeySpec;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianStreamKeySpec> getKey() throws OceanusException {
            /* Return key if it exists */
            if (theKey != null) {
                return theKey;
            }

            /* Synchronize access */
            synchronized (this) {
                /* Check for race condition */
                if (theKey != null) {
                    return theKey;
                }

                /* Generate the key */
                GordianCipherFactory myFactory = theFactory.getCipherFactory();
                GordianKeyGenerator<GordianStreamKeySpec> myGenerator = myFactory.getKeyGenerator(theKeySpec);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianStreamKeySpec getSpec() {
            return theKeySpec;
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
            implements FactorySpec<GordianStreamCipherSpec> {
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

        @Override
        public GordianFactory getFactory() {
            return theOwner.getFactory();
        }

        @Override
        public GordianStreamCipherSpec getSpec() {
            return theCipherSpec;
        }

        @Override
        public String toString() {
            return getFactory().getFactoryType() + ":" + theCipherSpec;
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
     * @return the list
     */
    static List<FactoryDigestSpec> digestProvider(final GordianFactory pFactory) {
        /* Loop through the possible digestSpecs */
        final List<FactoryDigestSpec> myResult = new ArrayList<>();
        final GordianDigestFactory myDigestFactory = pFactory.getDigestFactory();
        for (GordianDigestSpec mySpec : myDigestFactory.listAllSupportedSpecs()) {
            /* Add the digestSpec */
            myResult.add(new FactoryDigestSpec(pFactory, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of macs to test.
     * @param pFactory the factory
     * @param pKeyLen the keyLength
     * @return the list
     */
    static List<FactoryMacSpec> macProvider(final GordianFactory pFactory,
                                            final GordianLength pKeyLen) {
        /* Loop through the possible macSpecs */
        final List<FactoryMacSpec> myResult = new ArrayList<>();
        final GordianMacFactory myMacFactory = pFactory.getMacFactory();
        for (GordianMacSpec mySpec : myMacFactory.listAllSupportedSpecs(pKeyLen)) {
            /* Add the macSpec */
            myResult.add(new FactoryMacSpec(pFactory, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symKeySpecs to test.
     * @param pFactory the factory
     * @param pKeyLen the keyLength
     * @return the list
     */
    static List<FactorySymKeySpec> symKeyProvider(final GordianFactory pFactory,
                                                  final GordianLength pKeyLen) {
        /* Loop through the possible keySpecs */
        final List<FactorySymKeySpec> myResult = new ArrayList<>();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        for (GordianSymKeySpec mySpec : myCipherFactory.listAllSupportedSymKeySpecs(pKeyLen)) {
            /* Add the symKeySpec */
            myResult.add(new FactorySymKeySpec(pFactory, mySpec));
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
        final GordianCipherFactory myCipherFactory = myFactory.getCipherFactory();
        final List<FactorySymCipherSpec> myResult = new ArrayList<>();

        for (GordianSymCipherSpec myCipherSpec : myCipherFactory.listAllSupportedSymCipherSpecs(mySpec, false)) {
            myResult.add(new FactorySymCipherSpec(pKeySpec, myCipherSpec));
        }
        for (GordianSymCipherSpec myCipherSpec : myCipherFactory.listAllSupportedSymCipherSpecs(mySpec, true)) {
            myResult.add(new FactorySymCipherSpec(pKeySpec, myCipherSpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of streamKeySpecs to test.
     * @param pFactory the factory
     * @param pKeyLen the keyLength
     * @return the list
     */
    static List<FactoryStreamKeySpec> streamKeyProvider(final GordianFactory pFactory,
                                                        final GordianLength pKeyLen) {
        /* Loop through the possible keySpecs */
        final List<FactoryStreamKeySpec> myResult = new ArrayList<>();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        for (GordianStreamKeySpec mySpec : myCipherFactory.listAllSupportedStreamKeySpecs(pKeyLen)) {
            /* Add the streamKeySpec */
            myResult.add(new FactoryStreamKeySpec(pFactory, mySpec));
        }

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
        final GordianRandomFactory myRandomFactory = pFactory.getRandomFactory();
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
        final GordianRandomFactory myRandomFactory = pFactory.getRandomFactory();
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
