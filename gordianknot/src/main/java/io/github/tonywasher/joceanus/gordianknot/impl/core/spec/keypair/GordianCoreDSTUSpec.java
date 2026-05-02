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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDSTUSpec;

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
    private static final Map<GordianDSTUSpec, GordianCoreDSTUSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreDSTUSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreDSTUSpec[0]);

    /**
     * The Spec.
     */
    private final GordianDSTUSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreDSTUSpec(final GordianDSTUSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianDSTUSpec getSpec() {
        return theSpec;
    }

    @Override
    public String getCurveName() {
        return switch (theSpec) {
            case DSTU7 -> BASE + "7";
            case DSTU8 -> BASE + "8";
            case DSTU9 -> BASE + "9";
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public int getKeySize() {
        return switch (theSpec) {
            case DSTU9 -> GordianLength.LEN_431.getLength();
            case DSTU8 -> GordianLength.LEN_366.getLength();
            case DSTU7 -> GordianLength.LEN_306.getLength();
            default -> throw new IllegalArgumentException();
        };
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
        return pSpec instanceof GordianDSTUSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianDSTUSpec, GordianCoreDSTUSpec> newSpecMap() {
        final Map<GordianDSTUSpec, GordianCoreDSTUSpec> myMap = new EnumMap<>(GordianDSTUSpec.class);
        for (GordianDSTUSpec mySpec : GordianDSTUSpec.values()) {
            myMap.put(mySpec, new GordianCoreDSTUSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreDSTUSpec[] values() {
        return VALUES;
    }
}
