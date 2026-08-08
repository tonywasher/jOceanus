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
package io.github.tonywasher.joceanus.gordianknot.junit.extensions;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianRabbitEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSnow3GEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSosemanukEngine;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Stream Cipher Tests.
 */
class StreamCipherTest {
    /**
     * The TestCase.
     */
    private static class TestCase {
        /**
         * The testCase.
         */
        private final String theKey;
        private final String theIV;
        private final String theAAD;
        private final String thePlainText;
        private final String theExpected;

        /**
         * Constructor.
         *
         * @param pKey      the key
         * @param pIV       the IV
         * @param pExpected the expected results.
         */
        TestCase(final String pKey,
                 final String pIV,
                 final String pExpected) {
            this(pKey, pIV, null, null, pExpected);
        }

        /**
         * Constructor.
         *
         * @param pKey      the key
         * @param pIV       the IV
         * @param pPlain    the plainText
         * @param pExpected the expected results.
         */
        TestCase(final String pKey,
                 final String pIV,
                 final String pPlain,
                 final String pExpected) {
            this(pKey, pIV, null, pPlain, pExpected);
        }

        /**
         * Constructor.
         *
         * @param pKey      the key
         * @param pIV       the IV
         * @param pAAD      the AAD
         * @param pPlain    the plainText
         * @param pExpected the expected results.
         */
        TestCase(final String pKey,
                 final String pIV,
                 final String pAAD,
                 final String pPlain,
                 final String pExpected) {
            theKey = pKey;
            theIV = pIV;
            theAAD = pAAD;
            thePlainText = pPlain;
            theExpected = pExpected;
        }
    }

    /**
     * The keys.
     */
    private static final String KEY128_1 = "0558ABFE51A4F74A9DF04396E93C8FE2";
    private static final String KEY128_2 = "0A5DB00356A9FC4FA2F5489BEE4194E7";
    private static final String KEY128_3 = "0F62B5085BAE0154A7FA4DA0F34699EC";
    private static final String KEY256_1 = "0558ABFE51A4F74A9DF04396E93C8FE2" +
            "3588DB2E81D4277ACD2073C6196CBF12";
    private static final String KEY256_2 = "0A5DB00356A9FC4FA2F5489BEE4194E7" +
            "3A8DE03386D92C7FD22578CB1E71C417";
    private static final String KEY256_3 = "0F62B5085BAE0154A7FA4DA0F34699EC" +
            "3F92E5388BDE3184D72A7DD02376C91C";

    /**
     * The IVs
     */
    private static final String IV64_1 = "167DE44BB21980E7";
    private static final String IV64_2 = "1F86ED54BB2289F0";
    private static final String IV64_3 = "288FF65DC42B92F9";
    private static final String IV128_1 = "167DE44BB21980E74EB51C83EA51B81F";
    private static final String IV128_2 = "1F86ED54BB2289F057BE258CF35AC128";
    private static final String IV128_3 = "288FF65DC42B92F960C72E95FC63CA31";

    /**
     * Define the bit limits for engines.
     */
    private static final int SNOWLIMIT = 20000;

    /**
     * Create the streamCipher test suite.
     *
     * @return the test stream
     */
    @TestFactory
    Stream<DynamicNode> streamCipherTests() {
        /* Create tests */
        return Stream.of(DynamicContainer.dynamicContainer("streamCiphers", Stream.of(
                DynamicContainer.dynamicContainer("Rabbit", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Rabbit128Test().testTheCipher())
                )),
                DynamicContainer.dynamicContainer("Sosemanuk", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Sosemanuk128Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new Sosemanuk256Test().testTheCipher())
                )),
                DynamicContainer.dynamicContainer("Snow3G", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Snow3G128Test().testTheCipher())
                ))
        )));
    }

    /**
     * Test the Cipher against the results.
     *
     * @param pCipher   the cipher to test.
     * @param pTestCase the testCase
     */
    static void testCipher(final StreamCipher pCipher,
                           final TestCase pTestCase) {
        /* Access the expected bytes */
        final byte[] myExpected = Hex.decode(pTestCase.theExpected);

        /* Create the output buffer */
        final byte[] myOutput = new byte[myExpected.length];

        /* Access plainText or nulls */
        final byte[] myData = pTestCase.thePlainText != null
                ? Hex.decode(pTestCase.thePlainText)
                : new byte[myExpected.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(pTestCase.theKey));
        final byte[] myIV = Hex.decode(pTestCase.theIV);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        pCipher.init(true, myParms);
        pCipher.processBytes(myData, 0, myData.length, myOutput, 0);

        /* Check the encryption */
        if (!Arrays.equals(myExpected, myOutput)) {
            System.out.println(Hex.toHexString(myOutput, 0, myOutput.length));
        }
        Assertions.assertArrayEquals(myExpected, myOutput, "Encryption mismatch");
    }

    /**
     * Test the Stream Cipher against the limit.
     *
     * @param pCipher   the cipher to test.
     * @param pTestCase the testCase
     * @param pLimit    the limit in bits.
     */
    static void testStreamLimit(final StreamCipher pCipher,
                                final TestCase pTestCase,
                                final int pLimit) {
        /* Check the limit is a whole number of integers */
        Assertions.assertTrue((pLimit % Integer.SIZE == 0), "Invalid limit");
        final int myNumBytes = pLimit / Byte.SIZE;

        /* Create the maximum # of bytes */
        final byte[] myData = new byte[myNumBytes];
        final byte[] myOutput = new byte[myNumBytes];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(Hex.decode(pTestCase.theKey));
        final byte[] myIV = Hex.decode(pTestCase.theIV);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        pCipher.init(true, myParms);
        pCipher.processBytes(myData, 0, myData.length, myOutput, 0);

        /* Check that next encryption throws exception */
        Assertions.assertThrows(IllegalStateException.class, () -> pCipher.processBytes(myData, 0, 1, myOutput, 0), "Limit failure");
    }

    /**
     * Rabbit128.
     */
    static class Rabbit128Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY128_1, IV64_1,
                "476E2750C73856C93563B5F546F56A6A" +
                        "E5F97D8888655222812E3EDDB86BB8AD" +
                        "214AE0AA107CEAB4993CC74F63932885" +
                        "F0A585C735D590D194AC90717D0BADCF"
        );
        private static final TestCase TEST2 = new TestCase(KEY128_2, IV64_2,
                "921FCF4983891365A7DC901924B5E24B" +
                        "50F615D59FCD61CBD27280474F3D23C9" +
                        "ADF14BACADF99E5A163B836B0CFF02C0" +
                        "FF60F4B64EE7C824C98C3481EF656894"
        );
        private static final TestCase TEST3 = new TestCase(KEY128_3, IV64_3,
                "613CB0BA96AFF6CACF2A459A102A7F78" +
                        "CA985CF8FDD1474018758E36AE9923F5" +
                        "19D13D718DAF8D7C0C109B79D5749439" +
                        "B7EFA4C4C9C8D29DC5B3888314A6816F"
        );

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            final GordianRabbitEngine myEngine = new GordianRabbitEngine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
        }
    }

    /**
     * Sosemanuk128.
     */
    static class Sosemanuk128Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY128_1, IV128_1,
                "72BEE9C2C8CE2D1A23CCA5C78390651F" +
                        "706AD47A118E4630DC7E7396B2B53068" +
                        "A990D82B7DFA41928D6EA6339787A4A1" +
                        "82637CF51DEB8CDB988224254D244D3B"
        );
        private static final TestCase TEST2 = new TestCase(KEY128_2, IV128_2,
                "77AC8B7E56E6E54C44814153609BF542" +
                        "4DA5F6C200D36729E5F515CF3E057CF5" +
                        "165211BB31CBCCDAA60F9FD565965769" +
                        "055A8AC5B963EAD96646970B9E59EF85"
        );
        private static final TestCase TEST3 = new TestCase(KEY128_3, IV128_3,
                "7C6E4E44FA699770979E20A41C52F5BD" +
                        "06D1BB47C5FF5BBECA0F8AB399246DD8" +
                        "BEAC75FD5EC7F984AA0A42326DF4D49B" +
                        "12130B4013D6CE624A5C6376D623A88D"
        );

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            final GordianSosemanukEngine myEngine = new GordianSosemanukEngine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
        }
    }

    /**
     * Sosemanuk256.
     */
    static class Sosemanuk256Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY256_1, IV128_1,
                "AD5CBBC643ED2EC7C9D6013313D6AFA1" +
                        "A648889299429BFA893F397BD2444C8A" +
                        "C2742BE74E7BFBD3DD032B2A3CAFAD45" +
                        "DEAACB53DF96E717886885103A72AA3B"
        );
        private static final TestCase TEST2 = new TestCase(KEY256_2, IV128_2,
                "8ACFB48C93EF7B3EB06FB909D4BBD7B8" +
                        "F8B99735435CD78411D8C2D4BBA564AD" +
                        "5C5EC93F80CA40C4A8A9C119B59F0397" +
                        "583DE1B0050A7CC3E47619A3B0960037"
        );
        private static final TestCase TEST3 = new TestCase(KEY256_3, IV128_3,
                "1FC4F2E266B21C24FDDB3492D40A3FA6" +
                        "DE32CDF13908511E84420ABDFA1D3B0F" +
                        "EC600F83409C57CBE0394B90CDB1D759" +
                        "243EFD8B8E2AB7BC453A8D8A3515183E"
        );

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            final GordianSosemanukEngine myEngine = new GordianSosemanukEngine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
        }
    }

    /**
     * Snow3G128.
     */
    static class Snow3G128Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY128_1, IV128_1,
                "8594d7dcb0a2c886f88fc493ff870c8e70e86f0b27fd792c90b5794b2cb92c326532335cc94d0021c122c83b3dee685f1b3eaa470834e9389bd43344bc6ac86e"
        );
        private static final TestCase TEST2 = new TestCase(KEY128_2, IV128_2,
                "a02e0a09cdd2243bea0e129e2205f4f49930c2635c35f257d499c5ba3836ffe623e6ae0892eebe1e03464950f5c3546e85aaefd945ffac707cd09533f71f1507"
        );
        private static final TestCase TEST3 = new TestCase(KEY128_3, IV128_3,
                "6bcca6f60951d1b24088aecbed18bd5883c819f78208621363c43bcbd4c1c09d27ff391f40ce2b152e8f2c8781668870cd9c2d631dd4c1995c05a4edfa879de8"
        );

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            final GordianSnow3GEngine myEngine = new GordianSnow3GEngine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
            testStreamLimit(myEngine, TEST3, SNOWLIMIT);
        }
    }
}
