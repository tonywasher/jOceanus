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
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianXof;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactoryType;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMac;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacFactory;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacParameters;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.GordianMacType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.SymmetricStore.FactoryMacSpec;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Mac scripts.
 */
public final class SymmetricMacScripts {
    /**
     * Private constructor.
     */
    private SymmetricMacScripts() {
    }

    /**
     * Create the mac test suite for a factory.
     *
     * @param pFactory the factory
     * @param pPartner the partner
     * @return the test stream
     */
    static Stream<DynamicNode> macTests(final GordianFactory pFactory,
                                        final GordianFactory pPartner) {
        /* Create the default stream */
        Stream<DynamicNode> myTests = Stream.empty();

        /* Loop through the keyLengths */
        Iterator<GordianLength> myIterator = GordianKeyLengths.iterator();
        while (myIterator.hasNext()) {
            final GordianLength myKeyLen = myIterator.next();

            /* Build tests for this keyLength */
            final Stream<DynamicNode> myTest = macTests(pFactory, pPartner, myKeyLen);
            if (myTest != null) {
                myTests = Stream.concat(myTests, myTest);
            }
        }

        /* Return the tests */
        return Stream.of(DynamicContainer.dynamicContainer("Macs", myTests));
    }

    /**
     * Create the mac test suite for a factory.
     *
     * @param pFactory the factory
     * @param pPartner the partner
     * @param pKeyLen  the keyLength
     * @return the test stream or null
     */
    private static Stream<DynamicNode> macTests(final GordianFactory pFactory,
                                                final GordianFactory pPartner,
                                                final GordianLength pKeyLen) {
        /* Add mac Tests */
        List<FactoryMacSpec> myMacs = SymmetricStore.macProvider(pFactory, pPartner, pKeyLen);
        if (!myMacs.isEmpty()) {
            Stream<DynamicNode> myTests = myMacs.stream().map(x -> DynamicContainer.dynamicContainer(x.toString(), macTests(x)));
            return Stream.of(DynamicContainer.dynamicContainer(pKeyLen.toString(), myTests));
        }

        /* No mac Tests */
        return null;
    }

    /**
     * Create the mac test suite for a macSpec.
     *
     * @param pMacSpec the macSpec
     * @return the test stream
     */
    private static Stream<DynamicNode> macTests(final FactoryMacSpec pMacSpec) {
        /* Add profile test */
        Stream<DynamicNode> myTests = Stream.of(DynamicTest.dynamicTest("profile", () -> profileMac(pMacSpec)));

        /* Add Multi test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("multi", () -> multiMac(pMacSpec))));

        /* Add Xof test if this is a Xof */
        if (pMacSpec.getSpec().isXof()
                && GordianFactoryType.BC.equals(pMacSpec.getFactory().getFactoryType())) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("xof", () -> checkXof(pMacSpec))));
        }

        /* Add algorithmId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("checkAlgId", () -> checkMacAlgId(pMacSpec))));

        /* Add externalId test */
        myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("externalId", () -> SymmetricTest.checkExternalId(pMacSpec))));

        /* Add partner test if  the partner supports this macSpec */
        if (pMacSpec.getPartner() != null) {
            myTests = Stream.concat(myTests, Stream.of(DynamicTest.dynamicTest("Partner", () -> checkPartnerMac(pMacSpec))));
        }

        /* Return the tests */
        return myTests;
    }

    /**
     * Profile mac.
     *
     * @param pMacSpec the mac to profile
     * @throws GordianException on error
     */
    private static void profileMac(final FactoryMacSpec pMacSpec) throws GordianException {
        final GordianFactory myFactory = pMacSpec.getFactory();
        final GordianMacSpec mySpec = pMacSpec.getSpec();
        final GordianMacFactory myMacFactory = myFactory.getMacFactory();
        final GordianMac myMac1 = myMacFactory.createMac(pMacSpec.getSpec());
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();

        /* Check that the macLength is correct */
        Assertions.assertEquals(mySpec.getMacLength().getByteLength(), myMac1.getMacSize(), "MacLength incorrect");

        /* Define the input */
        final byte[] myBytes = "MacInput".getBytes();
        boolean isInconsistent = false;

        /* Access the two macs */
        final GordianMacType myType = mySpec.getMacType();
        final boolean twoMacs = GordianMacType.GMAC.equals(myType);
        final boolean needsReInit = myType.needsReInitialisation();
        final GordianMac myMac2 = twoMacs
                ? myMacFactory.createMac(mySpec)
                : myMac1;

        /* Start loop */
        final long myStart = System.nanoTime();
        for (int i = 0; i < SymmetricTest.profileRepeat; i++) {
            /* Use first mac */
            myMac1.init(GordianMacParameters.keyWithRandomNonce(myKey));
            myMac1.update(myBytes);
            final byte[] myFirst = myMac1.finish();

            /* If we need to reInitialise */
            if (needsReInit) {
                myMac2.init(GordianMacParameters.keyAndNonce(myKey, myMac1.getInitVector()));
            }

            /* Use second mac */
            myMac2.update(myBytes);
            final byte[] mySecond = myMac2.finish();
            if (!Arrays.areEqual(myFirst, mySecond)) {
                isInconsistent = true;
            }
        }

        /* Record elapsed */
        long myElapsed = System.nanoTime() - myStart;
        myElapsed /= 2 * SymmetricTest.MILLINANOS * (long) SymmetricTest.profileRepeat;
        if (SymmetricTest.fullProfiles) {
            System.out.println(pMacSpec + ":" + myElapsed);
        }
        Assertions.assertFalse(isInconsistent, pMacSpec + " inconsistent");
    }

    /**
     * Multi-call mac.
     *
     * @param pMacSpec the mac to profile
     */
    private static void multiMac(final FactoryMacSpec pMacSpec) throws GordianException {
        /* Create the mac */
        final GordianFactory myFactory = pMacSpec.getFactory();
        final GordianMacSpec mySpec = pMacSpec.getSpec();
        final GordianMacFactory myMacFactory = myFactory.getMacFactory();
        final GordianMac myMac1 = myMacFactory.createMac(mySpec);
        final GordianMac myMac2 = myMacFactory.createMac(mySpec);
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();

        /* Check that the macLength is correct */
        Assertions.assertEquals(mySpec.getMacLength().getByteLength(), myMac1.getMacSize(), "MacLength incorrect");

        /* Create the mac as a single block */
        final byte[] myBytes = SymmetricTest.getTestData();
        myMac1.init(GordianMacParameters.keyWithRandomNonce(myKey));
        myMac1.update(myBytes);
        final byte[] mySingle = myMac1.finish();

        /* Create the mac as partial blocks */
        myMac2.init(GordianMacParameters.keyAndNonce(myKey, myMac1.getInitVector()));
        for (int myPos = 0; myPos < SymmetricTest.DATALEN; myPos += SymmetricTest.PARTIALLEN) {
            final int myLen = Math.min(SymmetricTest.PARTIALLEN, SymmetricTest.DATALEN - myPos);
            myMac2.update(myBytes, myPos, myLen);
        }
        final byte[] myMulti = myMac2.finish();

        /* Check that the results are identical */
        Assertions.assertArrayEquals(mySingle, myMulti, "Multi-Block and Single-Block results differ");
    }

    /**
     * Check xof.
     *
     * @param pMacSpec the macSpec
     * @throws GordianException on error
     */
    private static void checkXof(final FactoryMacSpec pMacSpec) throws GordianException {
        /* Create the digest */
        final GordianFactory myFactory = pMacSpec.getFactory();
        final GordianMacSpec mySpec = pMacSpec.getSpec();
        final GordianMacFactory myMacFactory = myFactory.getMacFactory();
        final GordianMac myMac = myMacFactory.createMac(mySpec);
        final GordianXof myXof = (GordianXof) myMac;
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();

        /* Create the data */
        final byte[] myData = SymmetricTest.getTestData();
        myMac.init(GordianMacParameters.keyWithRandomNonce(myKey));

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
     * Check partner mac.
     *
     * @param pMacSpec the mac to check
     */
    private static void checkPartnerMac(final FactoryMacSpec pMacSpec) throws GordianException {
        /* Create the macs */
        final GordianFactory myFactory = pMacSpec.getFactory();
        final GordianFactory myPartner = pMacSpec.getPartner();
        final GordianMacSpec mySpec = pMacSpec.getSpec();
        final GordianMacFactory myMacFactory = myFactory.getMacFactory();
        final GordianMac myMac = myMacFactory.createMac(mySpec);
        final GordianMacFactory myPartnerFactory = myPartner.getMacFactory();
        final GordianMac myPartnerMac = myPartnerFactory.createMac(mySpec);
        final GordianKey<GordianMacSpec> myKey = pMacSpec.getKey();
        final GordianKey<GordianMacSpec> myPartnerKey = pMacSpec.getPartnerKey();

        /* Calculate macs */
        final byte[] myBytes = "MacInput".getBytes();
        myMac.init(GordianMacParameters.keyWithRandomNonce(myKey));
        final byte[] myIV = myMac.getInitVector();
        myMac.update(myBytes);
        final byte[] myFirst = myMac.finish();
        if (myIV == null) {
            myPartnerMac.init(GordianMacParameters.key(myPartnerKey));
        } else {
            myPartnerMac.init(GordianMacParameters.keyAndNonce(myPartnerKey, myIV));
        }
        myPartnerMac.update(myBytes);
        final byte[] mySecond = myPartnerMac.finish();

        /* Check that the macs match */
        Assertions.assertArrayEquals(myFirst, mySecond, "Mac misMatch");
    }

    /**
     * Check macAlgId.
     *
     * @param pSpec the Spec to check
     */
    private static void checkMacAlgId(final FactoryMacSpec pSpec) {
        /* Access the factory */
        final GordianBaseFactory myFactory = (GordianBaseFactory) pSpec.getFactory();

        /* Check that we have an id */
        final AlgorithmIdentifier myId = myFactory.getIdentifierForSpec(pSpec.getSpec());
        Assertions.assertNotNull(myId, "Unknown AlgorithmId for " + pSpec.getSpec());

        /* Check unique mapping */
        final GordianKeySpec mySpec = myFactory.getKeySpecForIdentifier(myId);
        Assertions.assertEquals(pSpec.getSpec(), mySpec, "Invalid mapping for  " + pSpec.getSpec());
    }
}
