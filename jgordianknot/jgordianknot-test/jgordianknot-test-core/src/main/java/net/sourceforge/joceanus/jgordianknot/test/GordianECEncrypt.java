/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.test;

import java.security.SecureRandom;
import java.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.crypto.bc.BouncyOldECEncryptor;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * EC Encryption Scratchpad.
 */
public final class GordianECEncrypt {
    /**
     * Test buffer size.
     */
    private static final int TESTLEN = 1024;

    /**
     *  Create the random.
     */
    private static SecureRandom theRandom = new SecureRandom();

    /**
     * Private constructor.
     */
    private GordianECEncrypt() {
    }

    /**
     * Test.
     * @param pSpec the keySpec.
     */
    private static void runTest(final GordianAsymKeySpec pSpec) {
        /* Create the encryptor */
        final BouncyOldECEncryptor myEncryptor = new BouncyOldECEncryptor(theRandom, pSpec);
        if (!myEncryptor.isAvailable()) {
            return;
        }

        /* Create the data to encrypt */
        final int myLen = TESTLEN;
        final byte[] mySrc = new byte[myLen];
        theRandom.nextBytes(mySrc);

        /* Protect against exceptions */
        try {
            /* Encrypt the bytes */
            final byte[] mySecret = myEncryptor.encrypt(mySrc);
            final byte[] myResult = myEncryptor.decrypt(mySecret);

            /* Check success */
            if (!Arrays.equals(myResult, mySrc)) {
                System.out.println("Failed - " + pSpec);
            } else {
                System.out.println("Succeeded - " + pSpec);
            }

        } catch (OceanusException e) {
            System.out.println(e.getMessage() + " - " + pSpec);
        }
    }

    /**
     * Main.
     * @param pArgs the program arguments
     */
    public static void main(final String[] pArgs) {
        /* Loop through all Elliptic */
        for (GordianDSAElliptic myCurve : GordianDSAElliptic.values()) {
            runTest(GordianAsymKeySpec.ec(myCurve));
        }
        for (GordianSM2Elliptic myCurve : GordianSM2Elliptic.values()) {
            runTest(GordianAsymKeySpec.sm2(myCurve));
        }
        for (GordianGOSTElliptic myCurve : GordianGOSTElliptic.values()) {
            runTest(GordianAsymKeySpec.gost2012(myCurve));
        }
    }
}
