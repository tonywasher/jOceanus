/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.factory;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.oceanus.resource.OceanusBundleId;
import net.sourceforge.joceanus.oceanus.resource.OceanusBundleLoader;

/**
 * Resource IDs for Factory package.
 */
public enum GordianFactoryResource implements OceanusBundleId {
    /**
     * Factory BC.
     */
    FACTORY_BC("BC"),

    /**
     * Factory JCA.
     */
    FACTORY_JCA("JCA");

    /**
     * The Factory Map.
     */
    private static final Map<GordianFactoryType, OceanusBundleId> FACTORY_MAP = buildFactoryMap();

    /**
     * The Resource Loader.
     */
    private static final OceanusBundleLoader LOADER = OceanusBundleLoader.getLoader(GordianFactory.class.getCanonicalName(),
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
    GordianFactoryResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "factory";
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
     * Build factory map.
     * @return the map
     */
    private static Map<GordianFactoryType, OceanusBundleId> buildFactoryMap() {
        /* Create the map and return it */
        final Map<GordianFactoryType, OceanusBundleId> myMap = new EnumMap<>(GordianFactoryType.class);
        myMap.put(GordianFactoryType.BC, FACTORY_BC);
        myMap.put(GordianFactoryType.JCA, FACTORY_JCA);
        return myMap;
    }

    /**
     * Obtain key for Factory.
     * @param pFactoryType the factoryType
     * @return the resource key
     */
    protected static OceanusBundleId getKeyForFactoryType(final GordianFactoryType pFactoryType) {
        return OceanusBundleLoader.getKeyForEnum(FACTORY_MAP, pFactoryType);
    }
}