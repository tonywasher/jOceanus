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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianGOSTSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * Named GOST-2012 Elliptic Curves.
 */

public final class GordianCoreGOSTSpec
        implements GordianCoreElliptic {
    /**
     * The specMap.
     */
    private static final Map<GordianGOSTSpec, GordianCoreGOSTSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreGOSTSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreGOSTSpec[0]);

    /**
     * The Spec.
     */
    private final GordianGOSTSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreGOSTSpec(final GordianGOSTSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianGOSTSpec getSpec() {
        return theSpec;
    }

    @Override
    public String getCurveName() {
        switch (theSpec) {
            case GOST256A:
                return "Tc26-Gost-3410-12-256-paramSetA";
            case GOST512A:
                return "Tc26-Gost-3410-12-512-paramSetA";
            case GOST512B:
                return "Tc26-Gost-3410-12-512-paramSetB";
            case GOST512C:
                return "Tc26-Gost-3410-12-512-paramSetC";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public int getKeySize() {
        return theSpec == GordianGOSTSpec.GOST256A
                ? GordianLength.LEN_256.getLength() : GordianLength.LEN_512.getLength();
    }

    /**
     * Obtain the curve for a Name.
     *
     * @param pName the name
     * @return the curve
     */
    public static GordianCoreGOSTSpec getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianCoreGOSTSpec myCurve : values()) {
            if (pName.equals(myCurve.getCurveName())) {
                return myCurve;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return theSpec.toString();
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
        return pThat instanceof GordianCoreGOSTSpec myThat
                && theSpec == myThat.getSpec();
    }

    @Override
    public int hashCode() {
        return theSpec.hashCode();
    }

    /**
     * Obtain the core spec.
     *
     * @param pSpec the base spec
     * @return the core spec
     */
    public static GordianCoreGOSTSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianGOSTSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianGOSTSpec, GordianCoreGOSTSpec> newSpecMap() {
        final Map<GordianGOSTSpec, GordianCoreGOSTSpec> myMap = new EnumMap<>(GordianGOSTSpec.class);
        for (GordianGOSTSpec mySpec : GordianGOSTSpec.values()) {
            myMap.put(mySpec, new GordianCoreGOSTSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreGOSTSpec[] values() {
        return VALUES;
    }
}
