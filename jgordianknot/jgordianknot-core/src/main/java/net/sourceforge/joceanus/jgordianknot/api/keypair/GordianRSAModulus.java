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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import java.math.BigInteger;

/**
 * Modulus Key lengths.
 */
public enum GordianRSAModulus {
    /**
     * 2048.
     */
    MOD2048(2048),

    /**
     * 1024.
     */
    MOD1024(1024),

    /**
     * 1536.
     */
    MOD1536(1536),

    /**
     * 3072.
     */
    MOD3072(3072),

    /**
     * 4096.
     */
    MOD4096(4096),

    /**
     * 6144.
     */
    MOD6144(6144),

    /**
     * 8192.
     */
    MOD8192(8192);

    /**
     * The modulus length.
     */
    private final int theLength;

    /**
     * Constructor.
     * @param pLength the length of the modulus
     */
    GordianRSAModulus(final int pLength) {
        theLength = pLength;
    }

    /**
     * Obtain the length of the modulus.
     * @return the length
     */
    public int getLength() {
        return theLength;
    }

    /**
     * Obtain the modulus for a BigInteger.
     * @param pValue the integer
     * @return the modulus
     */
    public static GordianRSAModulus getModulusForInteger(final BigInteger pValue) {
        /* Loop through the values */
        final int myLen = pValue.bitLength();
        for (GordianRSAModulus myModulus: values()) {
            if (myModulus.getLength() == myLen) {
                return myModulus;
            }
        }
        return null;
    }
}
