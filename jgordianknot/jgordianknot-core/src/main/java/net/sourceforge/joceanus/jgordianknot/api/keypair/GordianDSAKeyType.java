/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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

import org.bouncycastle.asn1.x509.DSAParameter;

/**
 * DSA KeyTypes.
 */
public enum GordianDSAKeyType {
    /**
     * 2048.
     */
    MOD2048(2048, 256),

    /**
     * 1024, 160.
     */
    MOD1024(1024, 160),

    /**
     * 3072.
     */
    MOD3072(3072, 256);

    /**
     * The keySize.
     */
    private final int theKeySize;

    /**
     * The hashSize.
     */
    private final int theHashSize;

    /**
     * Constructor.
     * @param pKeySize the keySize
     * @param pHashSize the hashSize
     */
    GordianDSAKeyType(final int pKeySize,
                      final int pHashSize) {
        theKeySize = pKeySize;
        theHashSize = pHashSize;
    }

    /**
     * Obtain the keySize of the keyType.
     * @return the keySize
     */
    public int getKeySize() {
        return theKeySize;
    }

    /**
     * Obtain the hashSize of the keyType.
     * @return the hashSize
     */
    public int getHashSize() {
        return theHashSize;
    }

    /**
     * Obtain the DSAKeyType for DSAParameters.
     * @param pParams the parameters
     * @return the DSAKeyType
     */
    public static GordianDSAKeyType getDSATypeForParms(final DSAParameter pParams) {
        /* Loop through the values */
        final int myLen = pParams.getP().bitLength();
        final int myHashSize = pParams.getQ().bitLength();
        for (GordianDSAKeyType myType: values()) {
            if (myType.getKeySize() == myLen
                    && myType.getHashSize() == myHashSize) {
                return myType;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return name();
    }
}
