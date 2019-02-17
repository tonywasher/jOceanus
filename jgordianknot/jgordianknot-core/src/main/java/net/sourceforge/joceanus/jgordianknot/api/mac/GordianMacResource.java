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

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for Mac package.
 */
public enum GordianMacResource implements TethysBundleId {
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
     * MAC BLAKE.
     */
    MAC_BLAKE("BLAKE"),

    /**
     * MAC VMPC.
     */
    MAC_VMPC("VMPC");

    /**
     * The MAC Map.
     */
    private static final Map<GordianMacType, TethysBundleId> MAC_MAP = buildMacMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getLoader(GordianMac.class.getCanonicalName(),
            ResourceBundle::getBundle);

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
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
    /**
     * Build MAC map.
     * @return the map
     */
    private static Map<GordianMacType, TethysBundleId> buildMacMap() {
        /* Create the map and return it */
        final Map<GordianMacType, TethysBundleId> myMap = new EnumMap<>(GordianMacType.class);
        myMap.put(GordianMacType.HMAC, MAC_HMAC);
        myMap.put(GordianMacType.GMAC, MAC_GMAC);
        myMap.put(GordianMacType.CMAC, MAC_CMAC);
        myMap.put(GordianMacType.POLY1305, MAC_POLY);
        myMap.put(GordianMacType.SKEIN, MAC_SKEIN);
        myMap.put(GordianMacType.KALYNA, MAC_KALYNA);
        myMap.put(GordianMacType.KUPYNA, MAC_KUPYNA);
        myMap.put(GordianMacType.BLAKE, MAC_BLAKE);
        myMap.put(GordianMacType.VMPC, MAC_VMPC);
        return myMap;
    }

    /**
     * Obtain key for MAC.
     * @param pMac the MacType
     * @return the resource key
     */
    protected static TethysBundleId getKeyForMac(final GordianMacType pMac) {
        return TethysBundleLoader.getKeyForEnum(MAC_MAP, pMac);
    }
}

