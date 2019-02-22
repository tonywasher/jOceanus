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

import java.util.stream.Stream;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.newengines.AnubisEngine;
import org.bouncycastle.crypto.newengines.MARSEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * BlockCipjher Tests.
 */
public class BlockCipherTest {
    /**
     * The 128 byte key.
     */
    private static final String KEY128 = "000102030405060708090a0b0c0d0e0f";

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
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> blockCipherTests() throws OceanusException {
        /* Create tests */
        return Stream.of(DynamicContainer.dynamicContainer("BlockCiphers", Stream.of(
                DynamicContainer.dynamicContainer("Anubis", Stream.of(
                        DynamicTest.dynamicTest("128", () -> new Anubis128Test().testTheCipher()),
                        DynamicTest.dynamicTest("256", () -> new Anubis256Test().testTheCipher())
                )),
                DynamicContainer.dynamicContainer("MARS", Stream.of(
                    DynamicTest.dynamicTest("128", () -> new MARS128Test().testTheCipher()),
                    DynamicTest.dynamicTest("256", () -> new MARS256Test().testTheCipher())
                ))
        )));
    }

    /**
     * Test the Cipher against the results.
     * @param pCipher the cipher to test.
     * @param pKey the key to test
     * @param pData the data to test
     * @param pExpected the expected results
     * @throws OceanusException on error
     */
    static void testCipher(final BlockCipher pCipher,
                           final String pKey,
                           final String pData,
                           final String pExpected) throws OceanusException {
        /* Create the output buffer */
        final byte[] myOutput = new byte[pCipher.getBlockSize()];
        final byte[] myFinal = new byte[pCipher.getBlockSize()];

        /* Access the key and the data */
        final KeyParameter myKey = new KeyParameter(TethysDataConverter.hexStringToBytes(pKey));
        final byte[] myData = TethysDataConverter.hexStringToBytes(pData);

        /* Initialise the cipher */
        pCipher.init(true, myKey);
        pCipher.processBlock(myData, 0, myOutput, 0);

        /* Check the encryption */
        final byte[] myExpected = TethysDataConverter.hexStringToBytes(pExpected);
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            testCipher(new AnubisEngine(), KEY128, TESTDATA, EXPECTED);
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            testCipher(new AnubisEngine(), KEY256, TESTDATA, EXPECTED);
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            testCipher(new MARSEngine(), KEY128, TESTDATA, EXPECTED);
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
         * @throws OceanusException on error
         */
        void testTheCipher() throws OceanusException {
            testCipher(new MARSEngine(), KEY256, TESTDATA, EXPECTED);
        }
    }
}
