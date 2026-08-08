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
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianAnubisEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianMARSEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSimonEngine;
import io.github.tonywasher.joceanus.gordianknot.impl.ext.engines.GordianSpeckEngine;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

/**
 * BlockCipher Tests.
 */
class BlockCipherTest {
    /**
     * The 128 byte key.
     */
    private static final String KEY128 = "000102030405060708090a0b0c0d0e0f";

    /**
     * The 192 byte key.
     */
    private static final String KEY192 = "000102030405060708090a0b0c0d0e0f1011121314151617";

    /**
     * The 256 byte key.
     */
    private static final String KEY256 = "000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f";

    /**
     * The testData
     */
    private static final String TESTDATA = "00112233445566778899aabbccddeeff";

    /**
     * Create the blockCipher test suite.
     *
     * @return the test stream
     * @throws GordianException on error
     */
    @TestFactory
    Stream<DynamicNode> blockCipherTests() throws GordianException {
        /* Create tests */
        return Stream.of(DynamicContainer.dynamicContainer("BlockCiphers", Stream.of(
                DynamicContainer.dynamicContainer("Anubis", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Anubis128Test().testTheCipher()),
                        DynamicTest.dynamicTest("192", () -> new Anubis192Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new Anubis256Test().testTheCipher())
                )),
                DynamicContainer.dynamicContainer("MARS", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new MARS128Test().testTheCipher()),
                        DynamicTest.dynamicTest("192", () -> new MARS192Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new MARS256Test().testTheCipher())
                )),
                DynamicContainer.dynamicContainer("Speck", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Speck128Test().testTheCipher()),
                        DynamicTest.dynamicTest("192", () -> new Speck192Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new Speck256Test().testTheCipher())
                )),
                DynamicContainer.dynamicContainer("Simon", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Simon128Test().testTheCipher()),
                        DynamicTest.dynamicTest("192", () -> new Simon192Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new Simon256Test().testTheCipher())
                ))
        )));
    }

    /**
     * Test the Cipher against the results.
     *
     * @param pCipher   the cipher to test.
     * @param pKey      the key to test
     * @param pData     the data to test
     * @param pExpected the expected results
     */
    static void testCipher(final BlockCipher pCipher,
                           final String pKey,
                           final String pData,
                           final String pExpected) {
        /* Create the output buffer */
        final byte[] myOutput = new byte[pCipher.getBlockSize()];
        final byte[] myFinal = new byte[pCipher.getBlockSize()];

        /* Access the key and the data */
        final KeyParameter myKey = new KeyParameter(Hex.decode(pKey));
        final byte[] myData = Hex.decode(pData);

        /* Initialise the cipher */
        pCipher.init(true, myKey);
        pCipher.processBlock(myData, 0, myOutput, 0);

        /* Check the encryption */
        final byte[] myExpected = Hex.decode(pExpected);
        Assertions.assertArrayEquals(myExpected, myOutput, "Encryption mismatch");

        /* Initialise the cipher */
        pCipher.init(false, myKey);
        pCipher.processBlock(myOutput, 0, myFinal, 0);
        Assertions.assertArrayEquals(myData, myFinal, "Decryption mismatch");
    }

    /**
     * Anubis128.
     */
    static class Anubis128Test {
        /**
         * Expected results.
         */
        private static final String EXPECTED =
                "4d384bf9eaeb03cc6507971c04cde7bb";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianAnubisEngine(), KEY128, TESTDATA, EXPECTED);
        }
    }

    /**
     * Anubis192.
     */
    static class Anubis192Test {
        /**
         * Expected results.
         */
        private static final String EXPECTED =
                "213c3d791c6d403d9e9288fce3f61794";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianAnubisEngine(), KEY192, TESTDATA, EXPECTED);
        }
    }

    /**
     * Anubis256.
     */
    static class Anubis256Test {
        /**
         * Expected results.
         */
        private static final String EXPECTED =
                "f0ce4d9a173f71c61e46926f643db171";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianAnubisEngine(), KEY256, TESTDATA, EXPECTED);
        }
    }

    /**
     * MARS128.
     */
    static class MARS128Test {
        /**
         * Expected results.
         */
        private static final String EXPECTED =
                "672db14c7714fd2477ca8499b0808ff7";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianMARSEngine(), KEY128, TESTDATA, EXPECTED);
        }
    }

    /**
     * MARS128.
     */
    static class MARS192Test {
        /**
         * Expected results.
         */
        private static final String EXPECTED =
                "98fde4ef2eef3386bf81b9434aa8b6dd";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianMARSEngine(), KEY192, TESTDATA, EXPECTED);
        }
    }

    /**
     * MARS256.
     */
    static class MARS256Test {
        /**
         * Expected results.
         */
        private static final String EXPECTED =
                "fc7e7ca35ed9fe729635cbdf078c8f1c";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianMARSEngine(), KEY256, TESTDATA, EXPECTED);
        }
    }

    /**
     * Speck128.
     */
    static class Speck128Test {
        /**
         * Test details.
         */
        private static final String KEY = "0f0e0d0c0b0a09080706050403020100";
        private static final String TESTDATA = "6c617669757165207469206564616d20";
        private static final String EXPECTED = "a65d9851797832657860fedf5c570d18";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianSpeckEngine(), KEY, TESTDATA, EXPECTED);
        }
    }

    /**
     * Speck192.
     */
    static class Speck192Test {
        /**
         * Test details.
         */
        private static final String KEY = "17161514131211100f0e0d0c0b0a09080706050403020100";
        private static final String TESTDATA = "726148206665696843206f7420746e65";
        private static final String EXPECTED = "1be4cf3a13135566f9bc185de03c1886";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianSpeckEngine(), KEY, TESTDATA, EXPECTED);
        }
    }

    /**
     * Speck256.
     */
    static class Speck256Test {
        /**
         * Test details.
         */
        private static final String KEY = "1f1e1d1c1b1a191817161514131211100f0e0d0c0b0a09080706050403020100";
        private static final String TESTDATA = "65736f6874206e49202e72656e6f6f70";
        private static final String EXPECTED = "4109010405c0f53e4eeeb48d9c188f43";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianSpeckEngine(), KEY, TESTDATA, EXPECTED);
        }
    }

    /**
     * Speck128.
     */
    static class Simon128Test {
        /**
         * Test details.
         */
        private static final String KEY = "0f0e0d0c0b0a09080706050403020100";
        private static final String TESTDATA = "63736564207372656c6c657661727420";
        private static final String EXPECTED = "49681b1e1e54fe3f65aa832af84e0bbc";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianSimonEngine(), KEY, TESTDATA, EXPECTED);
        }
    }

    /**
     * Simon192.
     */
    static class Simon192Test {
        /**
         * Test details.
         */
        private static final String KEY = "17161514131211100f0e0d0c0b0a09080706050403020100";
        private static final String TESTDATA = "206572656874206e6568772065626972";
        private static final String EXPECTED = "c4ac61effcdc0d4f6c9c8d6e2597b85b";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianSimonEngine(), KEY, TESTDATA, EXPECTED);
        }
    }

    /**
     * Simon256.
     */
    static class Simon256Test {
        /**
         * Test details.
         */
        private static final String KEY = "1f1e1d1c1b1a191817161514131211100f0e0d0c0b0a09080706050403020100";
        private static final String TESTDATA = "74206e69206d6f6f6d69732061207369";
        private static final String EXPECTED = "8d2b5579afc8a3a03bf72a87efe7b868";

        /**
         * Test cipher.
         *
         * @throws GordianException on error
         */
        void testTheCipher() throws GordianException {
            testCipher(new GordianSimonEngine(), KEY, TESTDATA, EXPECTED);
        }
    }
}
