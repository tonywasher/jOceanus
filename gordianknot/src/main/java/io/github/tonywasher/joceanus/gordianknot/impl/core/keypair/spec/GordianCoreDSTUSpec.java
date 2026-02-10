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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDSTUSpec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Named DSTU4145 Elliptic Curves.
 */
public final class GordianCoreDSTUSpec
        implements GordianCoreElliptic {
    /**
     * Curve Base.
     */
    private static final String BASE = "1.2.804.2.1.1.1.1.3.1.1.2.";

    /**
     * The specMap.
     */
    private static final Map<GordianNewDSTUSpec, GordianCoreDSTUSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewDSTUSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreDSTUSpec(final GordianNewDSTUSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewDSTUSpec getSpec() {
        return theSpec;
    }

    @Override
    public String getCurveName() {
        switch (theSpec) {
            case DSTU7:
                return BASE + "7";
            case DSTU8:
                return BASE + "8";
            case DSTU9:
                return BASE + "9";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public GordianLength getKeySize() {
        switch (theSpec) {
            case DSTU9:
                return GordianLength.LEN_431;
            case DSTU8:
                return GordianLength.LEN_366;
            case DSTU7:
                return GordianLength.LEN_306;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the curve for a Name.
     *
     * @param pName the name
     * @return the curve
     */
    public static GordianCoreDSTUSpec getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianCoreDSTUSpec myCurve : values()) {
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
        return pThat instanceof GordianCoreDSTUSpec myThat
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
    public static GordianCoreDSTUSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewDSTUSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewDSTUSpec, GordianCoreDSTUSpec> newSpecMap() {
        final Map<GordianNewDSTUSpec, GordianCoreDSTUSpec> myMap = new EnumMap<>(GordianNewDSTUSpec.class);
        for (GordianNewDSTUSpec mySpec : GordianNewDSTUSpec.values()) {
            myMap.put(mySpec, new GordianCoreDSTUSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreDSTUSpec> values() {
        return SPECMAP.values();
    }
}
