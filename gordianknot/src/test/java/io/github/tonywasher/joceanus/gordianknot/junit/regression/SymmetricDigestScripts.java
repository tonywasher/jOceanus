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
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigest;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianXof;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.GordianCoreDigestFactory;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryDigestSpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.List;
import java.util.stream.Stream;

/**
 * Digest scripts.
 */
public class SymmetricDigestScripts {
    /**
     * Private constructor.
     */
    private SymmetricDigestScripts() {
    }

    /**
     * Create the digest test suite for a factory.
     *
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream or null
     */
    static Stream<DynamicNode> digestTests(final GordianFactory pFactory,
                                           final GordianFactory pPartner) {
        /* Add digest Tests */
        List<FactoryDigestSpec> myDigests = SymmetricStore.digestProvider(pFactory, pPartner);
        if (!myDigests.isEmpty()) {
            Stream<DynamicNode> myTests = myDigests.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), digestTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer("Digests", myTests));
        }

        /* No digest Tests */
        return null;
    }

    /**
     * Create the digest test suite for a digestSpec.
     *
     * @param pDigestSpec the digestSpec
     * @return the test stream
     */
    private static Stream<DynamicNode> digestTests(final FactoryDigestSpec pDigestSpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileDigest(pDigestSpec)));

        /* Add Multi test as long as large data is supported */
        if (pDigestSpec.getSpec().getDigestType().supportsLargeData()) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("multi", () -> multiDigest(pDigestSpec))));
        }

        /* Add Xof test if this is a Xof */
        if (pDigestSpec.getSpec().isXof()
                && GordianFactoryType.BC.equals(pDigestSpec.getFactory().getFactoryType())) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("xof", () -> checkXof(pDigestSpec))));
        }

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkDigestAlgId(pDigestSpec))));


        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> SymmetricTest.checkExternalId(pDigestSpec))));

        /* Add partner test if the partner supports this digestSpec */
        if (pDigestSpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerDigest(pDigestSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Profile digest.
     *
     * @param pDigestSpec the digest to profile
     */
    private static void profileDigest(final FactoryDigestSpec pDigestSpec) throws GordianException {
        /* Create the digest */
        final GordianFactory myFactory = pDigestSpec.getFactory();
        final GordianDigestSpec mySpec = pDigestSpec.getSpec();
        final GordianDigestFactory myDigestFactory = myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigestFactory.createDigest(mySpec);

        /* Check that the digestLength is correct */
        Assertions.assertEquals(mySpec.getDigestLength().getByteLength(), myDigest.getDigestSize(), "DigestLength incorrect");

        /* Loop 100 times */
        final byte[] myBytes = getDigestInput(mySpec);
        final long myStart = System.nanoTime();
        for (int i = 0; i < SymmetricTest.profileRepeat; i++) {
            myDigest.update(myBytes);
            myDigest.finish();
        }

        /* Calculate elapsed time */
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= SymmetricTest.MILLINANOS * (long) SymmetricTest.profileRepeat;
        if (SymmetricTest.fullProfiles) {
            System.out.println(pDigestSpec + ":" + myElapsed);
        }
    }

    /**
     * Multi-call digest.
     *
     * @param pDigestSpec the digest to profile
     */
    private static void multiDigest(final FactoryDigestSpec pDigestSpec) throws GordianException {
        /* Create the digest */
        final GordianFactory myFactory = pDigestSpec.getFactory();
        final GordianDigestSpec mySpec = pDigestSpec.getSpec();
        final GordianDigestFactory myDigestFactory = myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigestFactory.createDigest(mySpec);

        /* Check that the digestLength is correct */
        Assertions.assertEquals(mySpec.getDigestLength().getByteLength(), myDigest.getDigestSize(), "DigestLength incorrect");

        /* Create the digest as a single block */
        final byte[] myBytes = SymmetricTest.getTestData();
        myDigest.update(myBytes);
        final byte[] mySingle = myDigest.finish();

        /* Create the digest as partial blocks */
        for (int myPos = 0; myPos < SymmetricTest.DATALEN; myPos += SymmetricTest.PARTIALLEN) {
            final int myLen = Math.min(SymmetricTest.PARTIALLEN, SymmetricTest.DATALEN - myPos);
            myDigest.update(myBytes, myPos, myLen);
        }
        final byte[] myMulti = myDigest.finish();

        /* Check that the results are identical */
        Assertions.assertArrayEquals(mySingle, myMulti, "Multi-Block and Single-Block results differ");
    }

    /**
     * Check xof.
     *
     * @param pDigestSpec the digestSpec
     * @throws GordianException on error
     */
    private static void checkXof(final FactoryDigestSpec pDigestSpec) throws GordianException {
        /* Create the digest */
        final GordianFactory myFactory = pDigestSpec.getFactory();
        final GordianDigestSpec mySpec = pDigestSpec.getSpec();
        final GordianDigestFactory myDigestFactory = myFactory.getDigestFactory();
        final GordianXof myXof = (GordianXof) myDigestFactory.createDigest(mySpec);

        /* Create the data */
        final byte[] myData = SymmetricTest.getTestData();

        /* Update the Xofs with the data */
        myXof.update(myData, 0, SymmetricTest.DATALEN);

        /* Extract Xofs as single block */
        final byte[] myFull = new byte[SymmetricTest.DATALEN];
        myXof.finish(myFull, 0, SymmetricTest.DATALEN);

        /* Update the Xofs with the data */
        myXof.update(myData, 0, SymmetricTest.DATALEN);
        final byte[] myPart = new byte[SymmetricTest.DATALEN];

        /* Create the xof as partial blocks */
        for (int myPos = 0; myPos < SymmetricTest.DATALEN; ) {
            final int myLen = Math.min(SymmetricTest.PARTIALLEN, SymmetricTest.DATALEN - myPos);
            myPos += myXof.output(myPart, myPos, myLen);
        }
        myXof.finish(myPart, 0, 0);

        /* Check that they are identical */
        Assertions.assertArrayEquals(myPart, myFull, "Mismatch on partial vs full xof");
    }

    /**
     * Check partner digest.
     *
     * @param pDigestSpec the digest to check
     */
    private static void checkPartnerDigest(final FactoryDigestSpec pDigestSpec) throws GordianException {
        /* Create the digests */
        final GordianFactory myFactory = pDigestSpec.getFactory();
        final GordianFactory myPartner = pDigestSpec.getPartner();
        final GordianDigestSpec mySpec = pDigestSpec.getSpec();
        final GordianDigestFactory myDigestFactory = myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigestFactory.createDigest(mySpec);
        final GordianDigestFactory myPartnerFactory = myPartner.getDigestFactory();
        final GordianDigest myPartnerDigest = myPartnerFactory.createDigest(mySpec);

        /* Calculate digests */
        final byte[] myBytes = getDigestInput(mySpec);
        myDigest.update(myBytes);
        final byte[] myFirst = myDigest.finish();
        myPartnerDigest.update(myBytes);
        final byte[] mySecond = myPartnerDigest.finish();

        /* Check that the digests match */
        Assertions.assertArrayEquals(myFirst, mySecond, "Digest misMatch");
    }

    /**
     * Check digestAlgId.
     *
     * @param pSpec the Spec to check
     */
    private static void checkDigestAlgId(final FactoryDigestSpec pSpec) {
        /* Access the factory */
        final GordianCoreDigestFactory myFactory = (GordianCoreDigestFactory) pSpec.getFactory().getDigestFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId, "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianDigestSpec mySpec = myFactory.getDigestSpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }

    /**
     * Obtain digest test input.
     *
     * @param pDigestSpec the digestSpec
     * @return the input
     */
    private static byte[] getDigestInput(final GordianDigestSpec pDigestSpec) {
        /* Obtain basic input */
        final byte[] myBytes = "DigestInput".getBytes();
        return pDigestSpec.getDigestType().supportsLargeData()
                ? myBytes
                : Arrays.copyOf(myBytes, pDigestSpec.getDigestState().getLength().getByteLength());
    }
}
