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

import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
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
    static class FactoryStreamKeyType
            implements FactorySpec<GordianStreamKeyType> {
        /**
         * The factory.
         */
        private final GordianFactory theFactory;

        /**
         * The streamKeyType.
         */
        private final GordianStreamKeyType theKeyType;

        /**
         * The key.
         */
        private GordianKey<GordianStreamKeyType> theKey;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKeyType the keyType
         */
        FactoryStreamKeyType(final GordianFactory pFactory,
                             final GordianStreamKeyType pKeyType) {
            theFactory = pFactory;
            theKeyType = pKeyType;
        }

        /**
         * Obtain (or create) the key for the FactoryStreamKeySpec
         * @return the key
         * @throws OceanusException on error
         */
        GordianKey<GordianStreamKeyType> getKey() throws OceanusException {
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
                GordianKeyGenerator<GordianStreamKeyType> myGenerator = myFactory.getKeyGenerator(theKeyType);
                theKey = myGenerator.generateKey();
                return theKey;
            }
        }

        @Override
        public GordianFactory getFactory() {
            return theFactory;
        }

        @Override
        public GordianStreamKeyType getSpec() {
            return theKeyType;
        }

        @Override
        public String toString() {
            return theKeyType.toString();
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
        private final FactoryStreamKeyType theOwner;

        /**
         * The streamKeyType.
         */
        private final GordianStreamCipherSpec theCipherSpec;

        /**
         * Constructor.
         * @param pOwner the owner
         * @param pCipherSpec the cipherSpec
         */
        FactoryStreamCipherSpec(final FactoryStreamKeyType pOwner,
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
     * @return the list
     */
    static List<FactoryMacSpec> macProvider(final GordianFactory pFactory) {
        /* Loop through the possible macSpecs */
        final List<FactoryMacSpec> myResult = new ArrayList<>();
        final GordianMacFactory myMacFactory = pFactory.getMacFactory();
        for (GordianMacSpec mySpec : myMacFactory.listAllSupportedSpecs()) {
            /* Add the macSpec */
            myResult.add(new FactoryMacSpec(pFactory, mySpec));
        }

        /* Return the list */
        return myResult;
    }

    /**
     * Obtain the list of symKeySpecs to test.
     * @param pFactory the factory
     * @return the list
     */
    static List<FactorySymKeySpec> symKeyProvider(final GordianFactory pFactory) {
        /* Loop through the possible keySpecs */
        final List<FactorySymKeySpec> myResult = new ArrayList<>();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        for (GordianSymKeySpec mySpec : myCipherFactory.listAllSupportedSymKeySpecs()) {
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
     * @return the list
     */
    static List<FactoryStreamKeyType> streamKeyProvider(final GordianFactory pFactory) {
        /* Loop through the possible keySpecs */
        final List<FactoryStreamKeyType> myResult = new ArrayList<>();
        final GordianCipherFactory myCipherFactory = pFactory.getCipherFactory();
        for (GordianStreamKeyType myType : myCipherFactory.listAllSupportedStreamKeyTypes()) {
            /* Add the streamKeySpec */
            myResult.add(new FactoryStreamKeyType(pFactory, myType));
        }

        /* Return the list */
        return myResult;
    }
}
