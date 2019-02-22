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
package net.sourceforge.joceanus.jgordianknot.junit.extensions;

import java.util.Arrays;
import java.util.stream.Stream;

import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.newengines.RabbitEngine;
import org.bouncycastle.crypto.newengines.Snow3GEngine;
import org.bouncycastle.crypto.newengines.SosemanukEngine;
import org.bouncycastle.crypto.newengines.Zuc128Engine;
import org.bouncycastle.crypto.newengines.Zuc256Engine;
import org.bouncycastle.crypto.newmacs.Zuc128Mac;
import org.bouncycastle.crypto.newmacs.Zuc256Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Stream Cipher Tests.
 */
public class StreamCipherTest {
    /**
     * The TestCase.
     */
    private static class TestCase {
        /**
         * The testCase.
         */
        private final String theKey;
        private final String theIV;
        private final String theExpected;

        /**
         * Constructor.
         * @param pKey the key
         * @param pIV the IV
         * @param pExpected the expected results.
         */
        TestCase(final String pKey,
                 final String pIV,
                 final String pExpected) {
            theKey = pKey;
            theIV = pIV;
            theExpected = pExpected;
        }
    }

    /**
     * The keys.
     */
    private static final String KEY128_1 = "0558ABFE51A4F74A9DF04396E93C8FE2";
    private static final String KEY128_2 = "0A5DB00356A9FC4FA2F5489BEE4194E7";
    private static final String KEY128_3 = "0F62B5085BAE0154A7FA4DA0F34699EC";
    private static final String KEY128_4 = "00000000000000000000000000000000";
    private static final String KEY128_5 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    private static final String KEY256_1 = "0558ABFE51A4F74A9DF04396E93C8FE2" +
                                           "3588DB2E81D4277ACD2073C6196CBF12";
    private static final String KEY256_2 = "0A5DB00356A9FC4FA2F5489BEE4194E7" +
                                           "3A8DE03386D92C7FD22578CB1E71C417";
    private static final String KEY256_3 = "0F62B5085BAE0154A7FA4DA0F34699EC" +
                                           "3F92E5388BDE3184D72A7DD02376C91C";
    private static final String KEY256_4 = "00000000000000000000000000000000" +
                                           "00000000000000000000000000000000";
    private static final String KEY256_5 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF" +
                                           "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

    /**
     * The IVs
     */
    private static final String IV64_1  = "167DE44BB21980E7";
    private static final String IV64_2  = "1F86ED54BB2289F0";
    private static final String IV64_3  = "288FF65DC42B92F9";
    private static final String IV128_1 = "167DE44BB21980E74EB51C83EA51B81F";
    private static final String IV128_2 = "1F86ED54BB2289F057BE258CF35AC128";
    private static final String IV128_3 = "288FF65DC42B92F960C72E95FC63CA31";
    private static final String IV128_4 = "00000000000000000000000000000000";
    private static final String IV128_5 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
    private static final String IV184_1 = "167DE44BB21980E74EB51C83EA51B81F00000000000000";
    private static final String IV184_2 = "1F86ED54BB2289F057BE258CF35AC128123456789ABCDE";
    private static final String IV184_3 = "288FF65DC42B92F960C72E95FC63CA31FFFFFFFFFFFFFF";
    private static final String IV184_4 = "0000000000000000000000000000000000000000000000";
    private static final String IV184_5 = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";

    /**
     * Define the bit limits for engines.
     */
    private static final int SNOWLIMIT = 20000;
    private static final int ZUCLIMIT = 65504;

    /**
     * Create the streamCipher test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> streamCipherTests() throws OceanusException {
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
                )),
                DynamicContainer.dynamicContainer("Zuc", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Zuc128Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new Zuc256Test().testTheCipher())
                ))
        )));
    }

    /**
     * Create the streamMac test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> streamMacTests() throws OceanusException {
        /* Create tests */
        return Stream.of(DynamicContainer.dynamicContainer("streamMacs", Stream.of(
                DynamicContainer.dynamicContainer("Zuc128Mac", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Zuc128MacTest().testTheMac())
                )),
                DynamicContainer.dynamicContainer("Zuc256Mac", Stream.of(
                        DynamicTest.dynamicTest("32", () -> new Zuc256Mac32Test().testTheMac()),
                        DynamicTest.dynamicTest("64", () -> new Zuc256Mac64Test().testTheMac()),
                        DynamicTest.dynamicTest("128", () -> new Zuc256Mac128Test().testTheMac())
                ))
        )));
    }

    /**
     * Test the Cipher against the results.
     * @param pCipher the cipher to test.
     * @param pTestCase the testCase
     * @throws OceanusException on error
     */
    static void testCipher(final StreamCipher pCipher,
                           final TestCase pTestCase) throws OceanusException {
        /* Access the expected bytes */
        final byte[] myExpected = TethysDataConverter.hexStringToBytes(pTestCase.theExpected);

        /* Create the output buffer */
        final byte[] myOutput = new byte[myExpected.length];
        final byte[] myData = new byte[myExpected.length];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(TethysDataConverter.hexStringToBytes(pTestCase.theKey));
        final byte[] myIV = TethysDataConverter.hexStringToBytes(pTestCase.theIV);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        pCipher.init(true, myParms);
        pCipher.processBytes(myData, 0, myData.length, myOutput, 0);

        /* Check the encryption */
        Assertions.assertArrayEquals(myExpected, myOutput, "Encryption mismatch");
    }

    /**
     * Test the Mac against the results.
     * @param pMac the mac to test.
     * @param pOnes use all ones as data?
     * @param pTestCase the testCase
     * @throws OceanusException on error
     */
    static void testMac(final Mac pMac,
                        final boolean pOnes,
                        final TestCase pTestCase) throws OceanusException {
        /* Access the expected bytes */
        final byte[] myExpected = TethysDataConverter.hexStringToBytes(pTestCase.theExpected);

        /* Create the output buffer and the data */
        final byte[] myOutput = new byte[pMac.getMacSize()];
        final byte[] myData = new byte[(pOnes ? 4000 : 400) / 8];
        Arrays.fill(myData, (byte) (pOnes ? 0x11 : 0));

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(TethysDataConverter.hexStringToBytes(pTestCase.theKey));
        final byte[] myIV = TethysDataConverter.hexStringToBytes(pTestCase.theIV);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        pMac.init(myParms);
        pMac.update(myData, 0, myData.length);
        pMac.doFinal(myOutput, 0);

        /* Check the encryption */
        Assertions.assertArrayEquals(myExpected, myOutput, "Mac mismatch");
    }

    /**
     * Test the Stream Cipher against the limit.
     * @param pCipher the cipher to test.
     * @param pTestCase the testCase
     * @param pLimit the limit in bits.
     * @throws OceanusException on error
     */
    static void testStreamLimit(final StreamCipher pCipher,
                                final TestCase pTestCase,
                                final int pLimit) throws OceanusException {
        /* Check the limit is a whole numbet of integers */
        Assertions.assertTrue((pLimit % Integer.SIZE == 0), "Invalid limit");
        final int myNumBytes = pLimit / Byte.SIZE;

        /* Create the maximum # of bytes */
        final byte[] myData = new byte[myNumBytes];
        final byte[] myOutput = new byte[myNumBytes];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(TethysDataConverter.hexStringToBytes(pTestCase.theKey));
        final byte[] myIV = TethysDataConverter.hexStringToBytes(pTestCase.theIV);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the cipher and create the keyStream */
        pCipher.init(true, myParms);
        pCipher.processBytes(myData, 0, myData.length, myOutput, 0);

        /* Check that next encryption throws exception */
        Assertions.assertThrows(IllegalStateException.class, () -> pCipher.processBytes(myData, 0, 1, myOutput, 0), "Limit failure");
    }

    /**
     * Test the Mac against the limit.
     * @param pMac the mac to test.
     * @param pTestCase the testCase
     * @param pLimit the limit in bits.
     * @throws OceanusException on error
     */
    static void testMacLimit(final Mac pMac,
                             final TestCase pTestCase,
                             final int pLimit) throws OceanusException {
        /* Check the limit is a whole numbet of integers */
        Assertions.assertTrue((pLimit % Integer.SIZE == 0), "Invalid limit");
        final int myNumBytes = pLimit / Byte.SIZE;

        /* Create the maximum # of bytes */
        final byte[] myData = new byte[myNumBytes];
        final byte[] myOutput = new byte[myNumBytes];

        /* Access the key and the iv */
        final KeyParameter myKey = new KeyParameter(TethysDataConverter.hexStringToBytes(pTestCase.theKey));
        final byte[] myIV = TethysDataConverter.hexStringToBytes(pTestCase.theIV);
        final ParametersWithIV myParms = new ParametersWithIV(myKey, myIV);

        /* Initialise the mac and create the result */
        pMac.init(myParms);
        pMac.update(myData, 0, myData.length);
        pMac.doFinal(myOutput, 0);

        /* Initialise the mac and process as much data as possible */
        pMac.init(myParms);
        pMac.update(myData, 0, myData.length);

        /* We expect a failure on processing a further byte */
        Assertions.assertThrows(IllegalStateException.class, () -> {
            pMac.update(myData, 0, 1);
            pMac.doFinal(myOutput, 0);
        }, "Limit failure");
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            final RabbitEngine myEngine = new RabbitEngine();
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            final SosemanukEngine myEngine = new SosemanukEngine();
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            final SosemanukEngine myEngine = new SosemanukEngine();
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            final Snow3GEngine myEngine = new Snow3GEngine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
            testStreamLimit(myEngine, TEST3, SNOWLIMIT);
        }
    }

    /**
     * Zuc128.
     */
    static class Zuc128Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY128_1, IV128_1,
                "08bdf5e7d2652f94150bf52b1dc78cd5f9cf9bd32fd058c5dad5f1cb75b5d1ea569c749f5b620df75992410bcf344c774af17d0d421e7a4dc0b03a1a1b6f7b99"
        );
        private static final TestCase TEST2 = new TestCase(KEY128_2, IV128_2,
                "85817ac6e441dafc7a13933805e22c1627fa5dc5a8aaf6127c2d4847e916c92fb31831b524202b3c86832ea76c6dbe9b5a1cd051a7cf8ff6e5ee5794ed584a2a"
        );
        private static final TestCase TEST3 = new TestCase(KEY128_3, IV128_3,
                "700e9a98aec45d1b62864307161d74bc239daf29990b983c7577fbb984fa089cc2241ddc9d53b89ca77bdbfe0ab90cd8e4e10298fe244263a7c0ec23e18c376c"
        );
        private static final TestCase TEST4 = new TestCase(KEY128_4, IV128_4,
                "27bede74018082da87d4e5b69f18bf6632070e0f39b7b692b4673edc3184a48e27636f4414510d62cc15cfe194ec4f6d4b8c8fcc630648badf41b6f9d16a36ca"
        );
        private static final TestCase TEST5 = new TestCase(KEY128_5, IV128_5,
                "0657cfa07096398b734b6cb4883eedf4257a76eb97595208d884adcdb1cbffb8e0f9d15846a0eed015328503351138f740d079af17296c232c4f022d6e4acac6"
        );

        /**
         * Test cipher.
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            final Zuc128Engine myEngine = new Zuc128Engine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
            testCipher(myEngine, TEST4);
            testCipher(myEngine, TEST5);
            testStreamLimit(myEngine, TEST5, ZUCLIMIT);
        }
    }

    /**
     * Zuc256.
     */
    static class Zuc256Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY256_1, IV184_1,
                "5a307e175b33de347b928b0bfee7ef34ac500c78b2d5636da4164edd450f6fa9cead94468e19f02400d94f170112a49757177adcbcfb27ef1bce8fb8ec59b5f4"
        );
        private static final TestCase TEST2 = new TestCase(KEY256_2, IV184_2,
                "f215c465eb3d6ed56ce2b0d6f515514c6e3b639d7a6837972f00233aef6d182098639ee1925083f6e7d8fbedb33e8b3df9d434a4fb312fcc677cf1abdf16a100"
        );
        private static final TestCase TEST3 = new TestCase(KEY256_3, IV184_3,
                "8b2befa4b2f733ac10bb5278d07ba5becc8f0ddfa1c4e7c55d128d9592cfca660e71a4b893f54686ac779e9b22d658f1eb5f0c266f6068a18d66352769150ec1"
        );
        private static final TestCase TEST4 = new TestCase(KEY256_4, IV184_4,
                "58d03ad62e032ce2dafc683a39bdcb0352a2bc67f1b7de74163ce3a101ef55589639d75b95fa681b7f090df756391ccc903b7612744d544c17bc3fad8b163b08"
        );
        private static final TestCase TEST5 = new TestCase(KEY256_5, IV184_5,
                "3356cbaed1a1c18b6baa4ffe343f777c9e15128f251ab65b949f7b26ef7157f296dd2fa9df95e3ee7a5be02ec32ba585505af316c2f9ded27cdbd935e441ce11"
        );

        /**
         * Test cipher.
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            final Zuc256Engine myEngine = new Zuc256Engine();
            testCipher(myEngine, TEST1);
            testCipher(myEngine, TEST2);
            testCipher(myEngine, TEST3);
            testCipher(myEngine, TEST4);
            testCipher(myEngine, TEST5);
            testStreamLimit(myEngine, TEST5, SNOWLIMIT);
        }
    }


    /**
     * Zuc128Mac.
     */
    static class Zuc128MacTest {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY128_4, IV128_4,
                "508dd5ff"
        );
        private static final TestCase TEST2 = new TestCase(KEY128_4, IV128_4,
                "fbed4c12"
        );
        private static final TestCase TEST3 = new TestCase(KEY128_5, IV128_5,
                "55e01504"
        );
        private static final TestCase TEST4 = new TestCase(KEY128_5, IV128_5,
                "9ce9a0c4"
        );

        /**
         * Test Mac.
         * @throws OceanusException on error
         */
        void testTheMac() throws OceanusException {
            final Zuc128Mac myMac = new Zuc128Mac();
            testMac(myMac, false, TEST1);
            testMac(myMac, true, TEST2);
            testMac(myMac, false, TEST3);
            testMac(myMac, true, TEST4);
            testMacLimit(myMac, TEST4, ZUCLIMIT - (2 * Integer.SIZE));
        }
    }

    /**
     * Zuc256Mac32.
     */
    static class Zuc256Mac32Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY256_4, IV184_4,
                "9b972a74"
        );
        private static final TestCase TEST2 = new TestCase(KEY256_4, IV184_4,
                "8754f5cf"
        );
        private static final TestCase TEST3 = new TestCase(KEY256_5, IV184_5,
                "1f3079b4"
        );
        private static final TestCase TEST4 = new TestCase(KEY256_5, IV184_5,
                "5c7c8b88"
        );

        /**
         * Test Mac.
         * @throws OceanusException on error
         */
        void testTheMac() throws OceanusException {
            final Zuc256Mac myMac = new Zuc256Mac(32);
            testMac(myMac, false, TEST1);
            testMac(myMac, true, TEST2);
            testMac(myMac, false, TEST3);
            testMac(myMac, true, TEST4);
            testMacLimit(myMac, TEST4, SNOWLIMIT - (2 * myMac.getMacSize() * Byte.SIZE));
        }
    }

    /**
     * Zuc256Mac64.
     */
    static class Zuc256Mac64Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY256_4, IV184_4,
                "673e54990034d38c"
        );
        private static final TestCase TEST2 = new TestCase(KEY256_4, IV184_4,
                "130dc225e72240cc"
        );
        private static final TestCase TEST3 = new TestCase(KEY256_5, IV184_5,
                "8c71394d39957725"
        );
        private static final TestCase TEST4 = new TestCase(KEY256_5, IV184_5,
                "ea1dee544bb6223b"
        );

        /**
         * Test Mac.
         * @throws OceanusException on error
         */
        void testTheMac() throws OceanusException {
            final Zuc256Mac myMac = new Zuc256Mac(64);
            testMac(myMac, false, TEST1);
            testMac(myMac, true, TEST2);
            testMac(myMac, false, TEST3);
            testMac(myMac, true, TEST4);
            testMacLimit(myMac, TEST4, SNOWLIMIT - (2 * myMac.getMacSize() * Byte.SIZE));
        }
    }

    /**
     * Zuc256Mac128.
     */
    static class Zuc256Mac128Test {
        /**
         * TestCases.
         */
        private static final TestCase TEST1 = new TestCase(KEY256_4, IV184_4,
                "d85e54bbcb9600967084c952a1654b26"
        );
        private static final TestCase TEST2 = new TestCase(KEY256_4, IV184_4,
                "df1e8307b31cc62beca1ac6f8190c22f"
        );
        private static final TestCase TEST3 = new TestCase(KEY256_5, IV184_5,
                "a35bb274b567c48b28319f111af34fbd"
        );
        private static final TestCase TEST4 = new TestCase(KEY256_5, IV184_5,
                "3a83b554be408ca5494124ed9d473205"
        );

        /**
         * Test Mac.
         * @throws OceanusException on error
         */
        void testTheMac() throws OceanusException {
            final Zuc256Mac myMac = new Zuc256Mac(128);
            testMac(myMac, false, TEST1);
            testMac(myMac, true, TEST2);
            testMac(myMac, false, TEST3);
            testMac(myMac, true, TEST4);
            testMacLimit(myMac, TEST4, SNOWLIMIT - (2 * myMac.getMacSize() * Byte.SIZE));
        }
    }
}
