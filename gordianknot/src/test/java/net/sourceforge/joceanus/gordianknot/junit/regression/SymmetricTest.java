/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKnuthObfuscater;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomSpec;
import net.sourceforge.joceanus.gordianknot.api.random.GordianRandomType;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianDataConverter;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryRandomSpec;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryRandomType;
import net.sourceforge.joceanus.gordianknot.junit.regression.SymmetricStore.FactorySpec;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.security.SecureRandom;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Security Test suite - Test Symmetric/Stream and Digest/MAC Algorithms.
 */
class SymmetricTest {
    /**
     * NanoSeconds in milliSeconds.
     */
    static final int MILLINANOS = 1000;

    /**
     * DataLength (a prime #).
     */
    static final int DATALEN = 1021; // 1429;

    /**
     * Partial length (a smaller prime #)..
     */
    static final int PARTIALLEN = 317;

    /**
     * Run full profiles.
     */
    static boolean fullProfiles;

    /**
     * Repeat count.
     */
    static int profileRepeat;

    /**
     * Perform setup operations.
     */
    @BeforeAll
    public static void setUp() throws GordianException {
        parseOptions();
        createSecurityFactories();
    }

    /**
     * Configure the test according to system properties.
     */
    private static void parseOptions() {
        /* If this is a full build */
        final String myBuildType = System.getProperty("joceanus.fullBuild");
        if (myBuildType != null) {
            /* Test everything */
            fullProfiles=false;
            profileRepeat=5;

            /* else allow further configuration */
        } else {
            /* Access system properties */
            fullProfiles = System.getProperty("fullProfiles") != null;
            profileRepeat = fullProfiles
                            ? 100
                            : 5;
        }
    }

    /**
     * The factories.
     */
    private static GordianFactory BCFACTORY;
    private static GordianFactory JCAFACTORY;

    /**
     * Initialise Factories.
     * @throws GordianException on error
     */
    private static void createSecurityFactories() throws GordianException {
        BCFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.BC);
        JCAFACTORY = GordianGenerator.createRandomFactory(GordianFactoryType.JCA);
    }

    /**
     * The TestData.
     */
    private static byte[] theTestData;

    /**
     * The AADData.
     */
    private static byte[] theAADData;

    /**
     * Create the symmetric test suite.
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> symmetricTests() {
        /* Create tests */
        final Stream<DynamicNode> myBC = symmetricTests(BCFACTORY, JCAFACTORY);
        final Stream<DynamicNode> myJCA = symmetricTests(JCAFACTORY, BCFACTORY);
        return Stream.concat(myBC, myJCA);
    }

    /**
     * Create the symmetric test suite for a factory.
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream
     */
    private Stream<DynamicNode> symmetricTests(final GordianFactory pFactory,
                                               final GordianFactory pPartner) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Add digest Tests */
        Stream<DynamicNode> mySubStream = SymmetricDigestScripts.digestTests(pFactory, pPartner);
        if (mySubStream != null) {
            myStream = Stream.concat(myStream, mySubStream);
        }

        /* Add mac Tests */
        mySubStream = SymmetricMacScripts.macTests(pFactory, pPartner);
        myStream = Stream.concat(myStream, mySubStream);

        /* Add symKey Tests */
        mySubStream = SymmetricSymScripts.symKeyTests(pFactory, pPartner);
        myStream = Stream.concat(myStream, mySubStream);

        /* Add streamKey Tests */
        mySubStream = SymmetricStreamScripts.streamKeyTests(pFactory, pPartner);
        myStream = Stream.concat(myStream, mySubStream);

        /* Add random Tests */
        mySubStream = randomTests(pFactory);
        myStream = Stream.concat(myStream, mySubStream);

        /* Return the stream */
        final String myName = pFactory.getFactoryType().toString();
        myStream = Stream.of(DynamicContainer.dynamicContainer(myName, myStream));
        return myStream;
    }

    /**
     * Create the random test suite for a factory.
     * @param pFactory the factory
     * @return the test stream or null
     */
    private Stream<DynamicNode> randomTests(final GordianFactory pFactory) {
        /* Create an empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the random types */
        for (GordianRandomType myType : GordianRandomType.values()) {
            /* If this type has symKeySpecs */
            if (myType.hasSymKeySpec()) {
                /* Create an empty stream */
                Stream<DynamicNode> myTypeStream = Stream.empty();

                /* Loop through the keyLengths */
                Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
                while (myIterator.hasNext()) {
                    final GordianLength myKeyLen = myIterator.next();

                    /* Access the specs */
                    FactoryRandomType myRandomType = SymmetricStore.randomProvider(pFactory, myType, myKeyLen);
                    final List<FactoryRandomSpec> mySpecs = myRandomType.getSpecs();
                    Stream<DynamicNode> myTests = mySpecs.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> checkRandomSpec(x)));
                    myTypeStream = Stream.concat(myTypeStream, Stream.of(DynamicContainer.dynamicContainer(myRandomType.toString(), myTests)));
                }
                myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myType.toString(), myTypeStream)));

                /* else simple randomType */
            } else {
                /* Access the specs */
                FactoryRandomType myRandomType = SymmetricStore.randomProvider(pFactory, myType);
                final List<FactoryRandomSpec> mySpecs = myRandomType.getSpecs();
                Stream<DynamicNode> myTests = mySpecs.stream().map(x -> DynamicTest.dynamicTest(x.toString(), () -> checkRandomSpec(x)));
                myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer(myRandomType.toString(), myTests)));
            }
        }

        /* return random Tests */
        return Stream.of(DynamicContainer.dynamicContainer("randoms", myStream));
    }


    /**
     * check randomSpec.
     * @param pRandomSpec the randomSpec
     * @throws GordianException on error
     */
    private void checkRandomSpec(final FactoryRandomSpec pRandomSpec) throws GordianException {
        /* Create the secure random */
        final GordianFactory myFactory = pRandomSpec.getFactory();
        final GordianRandomSpec mySpec = pRandomSpec.getSpec();
        final GordianRandomFactory myRandomFactory = myFactory.getRandomFactory();
        final SecureRandom myRandom = myRandomFactory.createRandom(mySpec);

        /* Generate some random bytes */
        final int myLen = 128;
        byte[] myBytes = new byte[myLen];
        myRandom.nextBytes(myBytes);
    }

    /**
     * Check externalId.
     * @param pSpec the Spec to check
     * @throws GordianException on error
     */
    static void checkExternalId(final FactorySpec<? extends GordianIdSpec> pSpec) throws GordianException {
        /* Access the factories */
        final GordianKnuthObfuscater myKnuth = pSpec.getFactory().getObfuscater();

        /* Check standard obfuscation */
        int myId = myKnuth.deriveExternalIdFromType(pSpec.getSpec());
        GordianIdSpec myResult = myKnuth.deriveTypeFromExternalId(myId);
        Assertions.assertEquals(pSpec.getSpec(), myResult,
                "Standard obfuscation for " + pSpec.getClass().getSimpleName() + ":" + pSpec);

        /* Check offset obfuscation */
        final int myOffset = 205;
        myId = myKnuth.deriveExternalIdFromType(pSpec.getSpec(), myOffset);
        myResult = myKnuth.deriveTypeFromExternalId(myId, myOffset);
        Assertions.assertEquals(pSpec.getSpec(), myResult,
                "Offset obfuscation for " + pSpec.getClass().getSimpleName() + ":" + pSpec);
    }

    /**
     * Obtain the testData.
     * @return the testData
     */
    static byte[] getTestData() {
        if (theTestData == null) {
            theTestData = new byte[DATALEN];
            final SecureRandom myRandom = new SecureRandom();
            myRandom.nextBytes(theTestData);
        }
        return theTestData;
    }

    /**
     * Obtain the aadData.
     * @return the aadData
     */
    static byte[] getAADData() {
        if (theAADData == null) {
            theAADData = GordianDataConverter.stringToByteArray("SomeAADBytes");
        }
        return theAADData;
    }
}
