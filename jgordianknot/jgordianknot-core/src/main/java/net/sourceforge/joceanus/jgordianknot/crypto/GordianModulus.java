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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.math.BigInteger;

import org.bouncycastle.asn1.pkcs.DHParameter;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

/**
 * Modulus Key lengths.
 */
public enum GordianModulus {
    /**
     * 1024.
     */
    MOD1024(1024),

    /**
     * 1536.
     */
    MOD1536(1536),

    /**
     * 2048.
     */
    MOD2048(2048),

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
    GordianModulus(final int pLength) {
        theLength = pLength;
    }

    /**
     * Obtain the length of the modulus.
     * @return the length
     */
    public int getModulus() {
        return theLength;
    }

    /**
     * Obtain the DiffieHellman Prime.
     * @return the prime
     */
    public DHParameters getDHParameters() {
        switch (this) {
            case MOD1024:
                return DHStandardGroups.rfc2409_1024;
            case MOD1536:
                return DHStandardGroups.rfc3526_1536;
            case MOD2048:
                return DHStandardGroups.rfc3526_2048;
            case MOD3072:
                return DHStandardGroups.rfc3526_3072;
            case MOD4096:
                return DHStandardGroups.rfc3526_4096;
            case MOD6144:
                return DHStandardGroups.rfc3526_6144;
            case MOD8192:
            default:
                return DHStandardGroups.rfc3526_8192;
        }
    }

    /**
     * Is the modulus valid for DSA?
     * @return true/false
     */
    public boolean isValidDSA() {
        switch (this) {
            case MOD1024:
            case MOD2048:
            case MOD3072:
                return true;
            default:
                return false;
        }
    }

    /**
     * Obtain the minimum hash length for DSA?
     * @return the minimum length
     */
    public GordianLength getMinDSAHashLength() {
        switch (this) {
            case MOD1024:
                return GordianLength.LEN_160;
            case MOD2048:
                return GordianLength.LEN_224;
            case MOD3072:
            default:
                return GordianLength.LEN_256;
        }
    }

    /**
     * Obtain the modulus for a BigInteger.
     * @param pValue the integer
     * @return the modulus
     */
    public static GordianModulus getModulusForInteger(final BigInteger pValue) {
        /* Loop through the values */
        final int myLen = pValue.bitLength();
        for(GordianModulus myModulus: values()) {
            if (myModulus.getModulus() == myLen) {
                return myModulus;
            }
        }
        return null;
    }

    /**
     * Obtain the modulus for DHParameter.
     * @param pParams the parameters
     * @return the modulus
     */
    public static GordianModulus getModulusForDHParams(final DHParameter pParams) {
        /* Determine the modulus  */
        GordianModulus myModulus = getModulusForInteger(pParams.getP());

        /* Check that the parameters match */
        if (myModulus != null) {
            final DHParameters myDHParms = myModulus.getDHParameters();
            if (!myDHParms.getG().equals(pParams.getG())) {
                myModulus = null;
            }
        }
        return myModulus;
    }

    @Override
    public String toString() {
        return name();
    }
}
