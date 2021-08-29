/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.crypto.ext.digests.Blake2;
import org.bouncycastle.crypto.ext.digests.Blake2X;
import org.bouncycastle.crypto.ext.digests.Blake2b;
import org.bouncycastle.crypto.ext.digests.Blake2s;
import org.bouncycastle.crypto.ext.digests.SkeinBase;
import org.bouncycastle.crypto.ext.digests.SkeinXof;
import org.bouncycastle.crypto.ext.engines.Blake2XEngine;
import org.bouncycastle.crypto.ext.engines.SkeinXofEngine;
import org.bouncycastle.crypto.ext.params.Blake2Parameters;
import org.bouncycastle.crypto.ext.params.SkeinXParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Test Cases for Xof-based Stream Ciphers.
 */
public class XofStreamCipher {
    /**
     * KeyStream length.
     */
    private static final int STREAM_LEN = 1024;

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

    /**
     * Create the xofStreamCipher test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> streamCipherTests() throws OceanusException {
        /* Create tests */
        return Stream.of(DynamicContainer.dynamicContainer("XofStreams", Stream.of(
                DynamicContainer.dynamicContainer("Blake2X", Stream.of(
                        DynamicTest.dynamicTest("Blake2s", () -> new Blake2XTest().runTests(false)),
                        DynamicTest.dynamicTest("Blake2b", () -> new Blake2XTest().runTests(true))
                )),
                DynamicContainer.dynamicContainer("SkeinXof", Stream.of(
                        DynamicTest.dynamicTest("Skein-256", () -> new SkeinXofTest().runTests(256)),
                        DynamicTest.dynamicTest("Skein-512", () -> new SkeinXofTest().runTests(512)),
                        DynamicTest.dynamicTest("Skein-1024", () -> new SkeinXofTest().runTests(1024))
                ))
        )));
    }

    /**
     * Test a stream cipher against a XofDigest.
     * @param pCipher the stream Cipher
     * @param pKey the key
     * @param pIV the IV
     * @param pXof the xof.
     */
    private static void testXofStream(final StreamCipher pCipher,
                                      final String pKey,
                                      final String pIV,
                                      final Xof pXof) {
        /* Translate key and IV */
        final byte[] myKeyBytes = Hex.decode(pKey);
        final byte[] myIVBytes = pIV == null ? null : Hex.decode(pIV);

        /* Initialise the Cipher */
        final KeyParameter myKey = new KeyParameter(myKeyBytes);
        final CipherParameters myParms = pIV == null ? myKey : new ParametersWithIV(myKey, myIVBytes);
        pCipher.init(true, myParms);

        /* Init the Xof */
        initXof(pXof, myKeyBytes, myIVBytes);

        /* Obtain the Xof stream */
        final byte[] myXof = new byte[STREAM_LEN];
        pXof.doOutput(myXof, 0, STREAM_LEN);

        /* Test using the initial stream */
        final byte[] myInput = new byte[STREAM_LEN];
        final byte[] myOutput = new byte[STREAM_LEN];
        pCipher.processBytes(myInput, 0, STREAM_LEN, myOutput, 0);
        Assertions.assertArrayEquals(myXof, myOutput, "Mismatch on output");

        /* Test using the reset stream */
        pCipher.reset();
        pCipher.processBytes(myInput, 0, STREAM_LEN, myOutput, 0);
        Assertions.assertArrayEquals(myXof, myOutput, "Mismatch after reset");
    }

    /**
     * Initialise the Xof.
     * @param pXof the xof.
     * @param pKey the key
     * @param pIV the IV
     */
    private static void initXof(final Xof pXof,
                                final byte[] pKey,
                                final byte[] pIV) {
        /* Handle Blake2X */
        if (pXof instanceof Blake2X) {
            final Blake2X myXof = (Blake2X) pXof;
            final Blake2Parameters.Builder myBuilder = new Blake2Parameters.Builder()
                    .setKey(pKey)
                    .setMaxOutputLen(-1);
            if (pIV != null) {
                myBuilder.setSalt(pIV);
            }
            myXof.init(myBuilder.build());
        }

        /* Handle SkeinXof */
        if (pXof instanceof SkeinXof) {
            final SkeinXof myXof = (SkeinXof) pXof;
            final SkeinXParameters.Builder myBuilder = new SkeinXParameters.Builder()
                    .setKey(pKey)
                    .setMaxOutputLen(-1);
            if (pIV != null) {
                myBuilder.setNonce(pIV);
            }
            myXof.init(myBuilder.build());
        }
    }

    /**
     * The Blake2X cipher.
     */
    private static class Blake2XTest {
        /**
         * Run the tests.
         * @param pBlake2b use blake2b?
         */
        void runTests(final boolean pBlake2b) {
            /* Create the cipher and Xof */
            final Blake2 myDigest = pBlake2b ? new Blake2b(512) : new Blake2s(256);
            final Blake2XEngine myCipher = new Blake2XEngine(myDigest);
            final Blake2X myXof = new Blake2X((Blake2) myDigest.copy());

            /* Select IVs */
            final String IV1 = pBlake2b ? IV128_1 : IV64_1;
            final String IV2 = pBlake2b ? IV128_2 : IV64_2;

            /* Run tests */
            testXofStream(myCipher, KEY128_1, null, myXof);
            testXofStream(myCipher, KEY128_1, IV1, myXof);
            testXofStream(myCipher, KEY128_2, IV2, myXof);
            testXofStream(myCipher, KEY256_1, IV1, myXof);
            testXofStream(myCipher, KEY256_2, IV2, myXof);
        }
    }

    /**
     * The SkeinXof cipher.
     */
    private static class SkeinXofTest {
        /**
         * Run the tests.
         * @param pState the statelength
         */
        void runTests(final int pState) {
            /* Create the cipher and Xof */
            final SkeinXofEngine myCipher = new SkeinXofEngine(pState);
            final SkeinXof myXof = new SkeinXof(new SkeinBase(pState, pState));

            /* Run tests */
            testXofStream(myCipher, KEY128_1, null, myXof);
            testXofStream(myCipher, KEY128_1, IV64_1, myXof);
            testXofStream(myCipher, KEY128_2, IV64_2, myXof);
            testXofStream(myCipher, KEY256_1, IV128_1, myXof);
            testXofStream(myCipher, KEY256_2, IV128_2, myXof);
        }
    }
}
