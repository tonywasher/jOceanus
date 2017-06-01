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
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Symmetric Key Types. Available algorithms.
 */
public enum GordianSymKeyType {
    /**
     * AES.
     */
    AES(128),

    /**
     * TwoFish.
     */
    TWOFISH(128),

    /**
     * Serpent.
     */
    SERPENT(128),

    /**
     * CAMELLIA.
     */
    CAMELLIA(128),

    /**
     * RC6.
     */
    RC6(128),

    /**
     * CAST6.
     */
    CAST6(128),

    /**
     * ThreeFish.
     */
    THREEFISH(256),

    /**
     * ARIA.
     */
    ARIA(128),

    /**
     * SM4.
     */
    SM4(128),

    /**
     * NoeKeon.
     */
    NOEKEON(128),

    /**
     * SEED.
     */
    SEED(128);

    /**
     * The IV Length.
     */
    private final int theIVLen;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pIVLen the IV length
     */
    GordianSymKeyType(final int pIVLen) {
        theIVLen = pIVLen;
    }

    /**
     * Obtain the IV Length.
     * @return the IV length.
     */
    public int getIVLength() {
        return theIVLen / Byte.SIZE;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = GordianCryptoResource.getKeyForSym(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Does this KeyType use a standard block size?
     * @return true/false
     */
    public boolean isStdBlock() {
        return !this.equals(THREEFISH);
    }

    /**
     * Is this KeyType valid for restriction?
     * @param pRestricted true/false
     * @return true/false
     */
    public boolean validForRestriction(final boolean pRestricted) {
        switch (this) {
            case THREEFISH:
                return !pRestricted;
            case SM4:
            case SEED:
            case NOEKEON:
                return pRestricted;
            default:
                return true;
        }
    }
}
