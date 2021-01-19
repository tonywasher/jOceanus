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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;

/**
 * Mac types. Available algorithms.
 */
public enum GordianMacType {
    /**
     * HMAC.
     */
    HMAC,

    /**
     * GMAC.
     */
    GMAC,

    /**
     * CMAC.
     */
    CMAC,

    /**
     * Poly1305.
     */
    POLY1305,

    /**
     * Skein.
     */
    SKEIN,

    /**
     * Blake2.
     */
    BLAKE2,

    /**
     * Kalyna.
     */
    KALYNA,

    /**
     * Kupyna.
     */
    KUPYNA,

    /**
     * VMPC.
     */
    VMPC,

    /**
     * ZUC.
     */
    ZUC,

    /**
     * CBCMac.
     */
    CBCMAC,

    /**
     * CFBMac.
     */
    CFBMAC,

    /**
     * SipHash.
     */
    SIPHASH,

    /**
     * KMAC.
     */
    KMAC,

    /**
     * GOST.
     */
    GOST,

    /**
     * Blake3.
     */
    BLAKE3;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = GordianMacResource.getKeyForMac(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Needs re-initialisation after final.
     * @return true/false
     */
    public boolean needsReInitialisation() {
        return this == GMAC;
    }

    /**
     * Is this MacType valid for largeData?
     * @return true/false
     */
    public boolean supportsLargeData() {
        return this != ZUC;
    }

    /**
     * Is this MacType valid for keyLength?
     * @param pKeyLen the keyLength
     * @return true/false
     */
    public boolean validForKeyLength(final GordianLength pKeyLen) {
        switch (this) {
            case POLY1305:
            case GOST:
            case BLAKE3:
                return GordianLength.LEN_256 == pKeyLen;
            case ZUC:
                return GordianLength.LEN_128 == pKeyLen
                        || GordianLength.LEN_256 == pKeyLen;
            case KALYNA:
                return GordianSymKeyType.KALYNA.validForKeyLength(pKeyLen);
            case SIPHASH:
                return GordianLength.LEN_128 == pKeyLen;
            default:
                return true;
        }
    }
}
