/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.test;

import java.security.SecureRandom;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.crypto.macs.DSTU7624Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/**
 * Test for Jca OCB support bugs.
 */
public final class GordianDSTUMacTest {
    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        testDSTU7624Padding();
        testDSTU7624Reuse();
        testDSTU7564Reuse();
    }

    /**
     * Private constructor.
     */
    private GordianDSTUMacTest() {
    }

    /**
     * Test DSTU7624 Mac Padding.
     */
    private static void testDSTU7624Padding() {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
            final SecureRandom myRandom = new SecureRandom();
            final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
            myGenerator.init(myParams);
            final byte[] myKey = myGenerator.generateKey();

            /* Create a KalynaMac */
            final DSTU7624Mac myMac = new DSTU7624Mac(128, 128);
            final KeyParameter myParms = new KeyParameter(myKey);
            myMac.init(myParms);

            /* Create short 40-byte input for digest */
            final byte[] myInput = "A123456789B123456789C123456789D123456789".getBytes();
            myMac.update(myInput, 0, myInput.length);

            /* Access output */
            final byte[] myResult = new byte[myMac.getMacSize()];
            myMac.doFinal(myResult, 0);

            System.out.println("DSTU7624 Padding Bug fixed");

        } catch (DataLengthException e) {
            System.out.println("DSTU7624 Padding Bug still exists");
        }
    }

    /**
     * Test DSTU7624 Mac Reuse.
     */
    private static void testDSTU7624Reuse() {
        /* Create the generator and generate a key */
        final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        final SecureRandom myRandom = new SecureRandom();
        final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
        myGenerator.init(myParams);
        final byte[] myKey = myGenerator.generateKey();

        /* Create a KalynaMac */
        final DSTU7624Mac myMac = new DSTU7624Mac(128, 128);
        final KeyParameter myParms = new KeyParameter(myKey);
        myMac.init(myParms);

        /* Create 32-byte input for digest */
        final byte[] myInput = "A123456789B123456789C123456789D1".getBytes();
        myMac.update(myInput, 0, myInput.length);

        /* Access output */
        final byte[] myResult = new byte[myMac.getMacSize()];
        myMac.doFinal(myResult, 0);

        /* Access output */
        final byte[] myRepeat = new byte[myMac.getMacSize()];
        myMac.update(myInput, 0, myInput.length);
        myMac.doFinal(myRepeat, 0);

        if (Arrays.areEqual(myResult, myRepeat)) {
            System.out.println("DSTU7624 Reuse Bug fixed");
        } else {
            System.out.println("DSTU7624 Reuse Bug still exists");
        }
    }

    /**
     * Test DSTU7564 Mac Reuse.
     */
    private static void testDSTU7564Reuse() {
        /* Create the generator and generate a key */
        final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        final SecureRandom myRandom = new SecureRandom();
        final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
        myGenerator.init(myParams);
        final byte[] myKey = myGenerator.generateKey();

        /* Create a KalynaMac */
        final DSTU7564Mac myMac = new DSTU7564Mac(256);
        final KeyParameter myParms = new KeyParameter(myKey);
        myMac.init(myParms);

        /* Create 40-byte input for digest */
        final byte[] myInput = "A123456789B123456789C123456789D123456789".getBytes();
        myMac.update(myInput, 0, myInput.length);

        /* Access output */
        final byte[] myResult = new byte[myMac.getMacSize()];
        myMac.doFinal(myResult, 0);

        /* Access output */
        final byte[] myRepeat = new byte[myMac.getMacSize()];
        myMac.update(myInput, 0, myInput.length);
        myMac.doFinal(myRepeat, 0);

        if (Arrays.areEqual(myResult, myRepeat)) {
            System.out.println("DSTU7624 Reuse Bug fixed");
        } else {
            System.out.println("DSTU7624 Reuse Bug still exists");
        }
    }
}
