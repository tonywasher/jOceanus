/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.random.spec;

import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomType;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * SP800 Random Generator types.
 */
public final class GordianCoreRandomType {
    /**
     * The randomTypeMap.
     */
    private static final Map<GordianNewRandomType, GordianCoreRandomType> TYPEMAP = newTypeMap();

    /**
     * The Random type.
     */
    private final GordianNewRandomType theType;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreRandomType(final GordianNewRandomType pType) {
        theType = pType;
    }

    /**
     * Does the randomType have a symKeySpec?
     *
     * @return true/false
     */
    public GordianNewRandomType getType() {
        return theType;
    }

    /**
     * Does the randomType have a symKeySpec?
     *
     * @return true/false
     */
    public boolean hasSymKeySpec() {
        switch (theType) {
            case CTR:
            case X931:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check subFields */
        return pThat instanceof GordianCoreRandomType myThat
                && theType == myThat.getType();
    }

    @Override
    public int hashCode() {
        return theType.hashCode();
    }

    /**
     * Obtain the core type.
     *
     * @param pType the base type
     * @return the core type
     */
    public static GordianCoreRandomType mapCoreType(final GordianNewRandomType pType) {
        return TYPEMAP.get(pType);
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewRandomType, GordianCoreRandomType> newTypeMap() {
        final Map<GordianNewRandomType, GordianCoreRandomType> myMap = new EnumMap<>(GordianNewRandomType.class);
        for (GordianNewRandomType myType : GordianNewRandomType.values()) {
            myMap.put(myType, new GordianCoreRandomType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreRandomType> values() {
        return TYPEMAP.values();
    }
}
