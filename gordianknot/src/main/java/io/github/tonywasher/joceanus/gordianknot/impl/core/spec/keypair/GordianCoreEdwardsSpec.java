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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianEdwardsSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * Edwards Elliptic Curves.
 */
public final class GordianCoreEdwardsSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianEdwardsSpec, GordianCoreEdwardsSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreEdwardsSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreEdwardsSpec[0]);

    /**
     * The Spec.
     */
    private final GordianEdwardsSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreEdwardsSpec(final GordianEdwardsSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianEdwardsSpec getSpec() {
        return theSpec;
    }

    /**
     * Is this curve25519?.
     *
     * @return true/false
     */
    public boolean is25519() {
        return theSpec == GordianEdwardsSpec.CURVE25519;
    }

    /**
     * Obtain suffix.
     *
     * @return the suffix
     */
    public String getSuffix() {
        return theSpec == GordianEdwardsSpec.CURVE25519 ? "25519" : "448";
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
        return pThat instanceof GordianCoreEdwardsSpec myThat
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
    public static GordianCoreEdwardsSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianEdwardsSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianEdwardsSpec, GordianCoreEdwardsSpec> newSpecMap() {
        final Map<GordianEdwardsSpec, GordianCoreEdwardsSpec> myMap = new EnumMap<>(GordianEdwardsSpec.class);
        for (GordianEdwardsSpec mySpec : GordianEdwardsSpec.values()) {
            myMap.put(mySpec, new GordianCoreEdwardsSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreEdwardsSpec[] values() {
        return VALUES;
    }
}
