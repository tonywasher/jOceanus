/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.mac;

import net.sourceforge.joceanus.gordianknot.api.base.GordianBundleLoader;
import net.sourceforge.joceanus.gordianknot.api.base.GordianBundleLoader.GordianBundleId;

/**
 * Resource IDs for Mac package.
 */
public enum GordianMacResource
        implements GordianBundleId {
    /**
     * MAC HMAC.
     */
    MAC_HMAC("HMAC"),

    /**
     * MAC GMAC.
     */
    MAC_GMAC("GMAC"),

    /**
     * MAC CMAC.
     */
    MAC_CMAC("CMAC"),

    /**
     * MAC KMAC.
     */
    MAC_KMAC("KMAC"),

    /**
     * MAC POLY1305.
     */
    MAC_POLY("POLY1305"),

    /**
     * MAC SKEIN.
     */
    MAC_SKEIN("SKEIN"),

    /**
     * MAC KALYNA.
     */
    MAC_KALYNA("KALYNA"),

    /**
     * MAC KUPYNA.
     */
    MAC_KUPYNA("KUPYNA"),

    /**
     * MAC BLAKE2.
     */
    MAC_BLAKE2("BLAKE2"),

    /**
     * MAC BLAKE2.
     */
    MAC_BLAKE3("BLAKE3"),

    /**
     * MAC CBC.
     */
    MAC_CBC("CBC"),

    /**
     * MAC CFB.
     */
    MAC_CFB("CFB"),

    /**
     * MAC SipHash.
     */
    MAC_SIPHASH("SipHash"),

    /**
     * MAC GOST.
     */
    MAC_GOST("GOST"),

    /**
     * MAC VMPC.
     */
    MAC_VMPC("VMPC");

    /**
     * The Resource Loader.
     */
    private static final GordianBundleLoader LOADER = GordianBundleLoader.getLoader(GordianMacResource.class.getCanonicalName());

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    GordianMacResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "mac";
    }

    @Override
    public String getValue() {
        /* If we have not initialized the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
