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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import org.bouncycastle.jcajce.provider.asymmetric.GOST;

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
     * Blake2B.
     */
    BLAKE,

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
     * GOST.
     */
    GOST;

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
     * Is this MacType valid for restriction?
     * @param pRestricted true/false
     * @return true/false
     */
    public boolean validForRestriction(final boolean pRestricted) {
        switch (this) {
            case POLY1305:
            case GOST:
                return !pRestricted;
            case SIPHASH:
                return pRestricted;
            default:
                return true;
        }
    }
}
