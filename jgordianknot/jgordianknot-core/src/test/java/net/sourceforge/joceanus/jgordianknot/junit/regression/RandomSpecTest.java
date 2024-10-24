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

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.jgordianknot.api.random.GordianRandomFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.digest.GordianCoreDigestFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security Test suite - Test Symmetric/Stream and Digest/MAC Algorithms.
 */
class RandomSpecTest {
    /**
     * The factories.
     */
    private static GordianFactory BCFACTORY;
    private static GordianFactory JCAFACTORY;

    /**
     * Initialise Factories.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void createSecurityFactories() throws OceanusException {
        BCFACTORY = GordianGenerator.createFactory(GordianFactoryType.BC);
        JCAFACTORY = GordianGenerator.createFactory(GordianFactoryType.JCA);
    }

    /**
     * Create the randomSpec test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    Stream<DynamicNode> randomSpecTests() throws OceanusException {
        /* Create tests */
        final Stream<DynamicNode> myBC = randomSpecTests(BCFACTORY);
        final Stream<DynamicNode> myJCA = randomSpecTests(JCAFACTORY);
        return Stream.concat(myBC, myJCA);
    }

    /**
     * Create the randomSpec test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> randomSpecTests(final GordianFactory pFactory) throws OceanusException {
        /* Add digestSpec test */
        Stream<DynamicNode> myStream = Stream.of(DynamicTest.dynamicTest("digestSpec", () -> checkDigestSpecs(pFactory)));

        /* Add symKeySpec test */
        myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer("symKeySpec", symKeySpecTests(pFactory))));

        /* Add streamKeySpec test */
        myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer("streamKeySpec", streamKeySpecTests(pFactory))));

        /* Add macSpec test */
        myStream = Stream.concat(myStream, Stream.of(DynamicContainer.dynamicContainer("macSpec", macSpecTests(pFactory))));

        /* Return the stream */
        final String myName = pFactory.getFactoryType().toString();
        myStream = Stream.of(DynamicContainer.dynamicContainer(myName, myStream));
        return myStream;
    }

    /**
     * Create the symKeySpec test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    private Stream<DynamicNode> symKeySpecTests(final GordianFactory pFactory) {
        /* Create empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myLength = myIterator.next();
            myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest(myLength.toString(),
                    () -> checkSymKeySpecs(pFactory, myLength))));
        }

        /* Return the stream */
        return myStream;
    }

    /**
     * Create the streamKeySpec test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    private Stream<DynamicNode> streamKeySpecTests(final GordianFactory pFactory) {
        /* Create empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myLength = myIterator.next();
            myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest(myLength.toString(),
                    () -> checkStreamKeySpecs(pFactory, myLength))));
        }

        /* Return the stream */
        return myStream;
    }

    /**
     * Create the macSpec test suite for a factory.
     * @param pFactory the factory
     * @return the test stream
     */
    private Stream<DynamicNode> macSpecTests(final GordianFactory pFactory) {
        /* Create empty stream */
        Stream<DynamicNode> myStream = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myLength = myIterator.next();
            myStream = Stream.concat(myStream, Stream.of(DynamicTest.dynamicTest(myLength.toString(),
                    () -> checkMacSpecs(pFactory, myLength))));
        }

        /* Return the stream */
        return myStream;
    }

    /**
     * Check DigestSpecs.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    private void checkDigestSpecs(final GordianFactory pFactory) throws OceanusException {
        /* Generate digestSpecs */
        final GordianRandomFactory myRandom = pFactory.getRandomFactory();
        final GordianCoreDigestFactory myDigests = (GordianCoreDigestFactory) pFactory.getDigestFactory();
        final List<GordianDigestSpec> myValid = myDigests.listAllSupportedSpecs();

        /* Loop a large number of times to ensure that all digests are generated */
        for (int i = 0; i < 10000 && !myValid.isEmpty(); i++) {
            final GordianDigest myDigest = myRandom.generateRandomDigest(false);
            final GordianDigestSpec mySpec = myDigest.getDigestSpec();
            myValid.remove(mySpec);
        }

        /* Check that we have generated all specs */
        Assertions.assertTrue(myValid.isEmpty(), "Not all specs generated");
    }

    /**
     * Check symKeySpecs.
     * @param pFactory the factory
     * @param pLength the key length
     * @throws OceanusException on error
     */
    private void checkSymKeySpecs(final GordianFactory pFactory,
                                  final GordianLength pLength) throws OceanusException {
        /* Generate digestSpecs */
        final GordianRandomFactory myRandom = pFactory.getRandomFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) pFactory.getCipherFactory();
        final List<GordianSymKeySpec> myValid = myCiphers.listAllSupportedSymKeySpecs(pLength);

        /* Loop a large number of times to ensure that all digests are generated */
        for (int i = 0; i < 10000 && !myValid.isEmpty(); i++) {
            final GordianKey<GordianSymKeySpec> myKey = myRandom.generateRandomSymKey(pLength);
            final GordianSymKeySpec mySpec = myKey.getKeyType();
            myValid.remove(mySpec);
        }

        /* Check that we have generated all specs */
        Assertions.assertTrue(myValid.isEmpty(), "Not all specs generated");
    }

    /**
     * Check streamKeySpecs.
     * @param pFactory the factory
     * @param pLength the key length
     * @throws OceanusException on error
     */
    private void checkStreamKeySpecs(final GordianFactory pFactory,
                                     final GordianLength pLength) throws OceanusException {
        /* Generate digestSpecs */
        final GordianRandomFactory myRandom = pFactory.getRandomFactory();
        final GordianCoreCipherFactory myCiphers = (GordianCoreCipherFactory) pFactory.getCipherFactory();
        final List<GordianStreamKeySpec> myValid = myCiphers.listAllSupportedStreamKeySpecs(pLength);

        /* Loop a large number of times to ensure that all digests are generated */
        for (int i = 0; i < 10000 && !myValid.isEmpty(); i++) {
            final GordianKey<GordianStreamKeySpec> myKey = myRandom.generateRandomStreamKey(pLength, false);
            final GordianStreamKeySpec mySpec = myKey.getKeyType();
            myValid.remove(mySpec);
        }

        /* Check that we have generated all specs */
        Assertions.assertTrue(myValid.isEmpty(), "Not all specs generated");
    }

    /**
     * Check MacSpecs.
     * @param pFactory the factory
     * @param pLength the key length
     * @throws OceanusException on error
     */
    private void checkMacSpecs(final GordianFactory pFactory,
                               final GordianLength pLength) throws OceanusException {
        /* Generate macSpecs */
        final GordianRandomFactory myRandom = pFactory.getRandomFactory();
        final GordianCoreMacFactory myMacs = (GordianCoreMacFactory) pFactory.getMacFactory();
        final List<GordianMacSpec> myValid = myMacs.listAllSupportedSpecs(pLength);
        myValid.remove(GordianMacSpecBuilder.poly1305Mac());

        /* Loop a large number of times to ensure that all macs are generated */
        for (int i = 0; i < 10000 && !myValid.isEmpty(); i++) {
            final GordianMac myMac = myRandom.generateRandomMac(pLength, false);
            final GordianMacSpec mySpec = myMac.getMacSpec();
            myValid.remove(mySpec);
        }

        /* Check that we have generated all specs */
        Assertions.assertTrue(myValid.isEmpty(), "Not all specs generated");
    }
}
