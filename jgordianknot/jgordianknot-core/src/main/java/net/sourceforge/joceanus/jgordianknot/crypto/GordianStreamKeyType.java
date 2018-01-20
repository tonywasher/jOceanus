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
 * Stream Key Type.
 */
public enum GordianStreamKeyType {
    /**
     * XSalsa20.
     */
    XSALSA20(GordianLength.LEN_192),

    /**
     * Salsa20.
     */
    SALSA20(GordianLength.LEN_64),

    /**
     * HC.
     */
    HC(GordianLength.LEN_128),

    /**
     * ChaCha.
     */
    CHACHA(GordianLength.LEN_64),

    /**
     * ChaCha7539.
     */
    CHACHA7539(GordianLength.LEN_96),

    /**
     * VMPC.
     */
    VMPC(GordianLength.LEN_128),

    /**
     * ISAAC.
     */
    ISAAC(null),

    /**
     * RC4.
     */
    RC4(null),

    /**
     * Grain.
     */
    GRAIN(GordianLength.LEN_96),

    /**
     * Sosemanuk.
     */
    SOSEMANUK(GordianLength.LEN_128);

    /**
     * The IV Length.
     */
    private final GordianLength theIVLen;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pIVLen the IV length
     */
    GordianStreamKeyType(final GordianLength pIVLen) {
        theIVLen = pIVLen;
    }

    /**
     * Obtain the IV Length.
     * @return the IV length.
     */
    public int getIVLength() {
        return theIVLen == null
                                ? 0
                                : theIVLen.getByteLength();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = GordianCryptoResource.getKeyForStream(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Is this KeyType valid for restriction?
     * @param pRestricted true/false
     * @return true/false
     */
    public boolean validForRestriction(final boolean pRestricted) {
        switch (this) {
            case XSALSA20:
            case CHACHA7539:
                return !pRestricted;
            case GRAIN:
                return pRestricted;
            default:
                return true;
        }
    }
}
