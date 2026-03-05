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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewRSASpec;

import java.math.BigInteger;
import java.util.EnumMap;
import java.util.Map;

/**
 * Modulus Key lengths.
 */
public final class GordianCoreRSASpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewRSASpec, GordianCoreRSASpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreRSASpec[] VALUES = SPECMAP.values().toArray(new GordianCoreRSASpec[0]);

    /**
     * The Spec.
     */
    private final GordianNewRSASpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreRSASpec(final GordianNewRSASpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewRSASpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the length for a spec.
     *
     * @return the length
     */
    public int getLength() {
        switch (theSpec) {
            case MOD1024:
                return GordianLength.LEN_1024.getLength();
            case MOD1536:
                return GordianLength.LEN_1536.getLength();
            case MOD2048:
                return GordianLength.LEN_2048.getLength();
            case MOD3072:
                return GordianLength.LEN_3072.getLength();
            case MOD4096:
                return GordianLength.LEN_4096.getLength();
            case MOD6144:
                return GordianLength.LEN_6144.getLength();
            case MOD8192:
                return GordianLength.LEN_8192.getLength();
            default:
                throw new IllegalArgumentException("Unknown GordianNewRSASpec");
        }
    }

    /**
     * Obtain the matching spec for a BigInteger.
     *
     * @param pValue the integer
     * @return the modulus
     */
    public static GordianCoreRSASpec getRSASpecForInteger(final BigInteger pValue) {
        /* Loop through the values */
        final int myLen = pValue.bitLength();
        for (GordianCoreRSASpec mySpec : values()) {
            if (mySpec.getLength() == myLen) {
                return mySpec;
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
        return pThat instanceof GordianCoreRSASpec myThat
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
    public static GordianCoreRSASpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewRSASpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewRSASpec, GordianCoreRSASpec> newSpecMap() {
        final Map<GordianNewRSASpec, GordianCoreRSASpec> myMap = new EnumMap<>(GordianNewRSASpec.class);
        for (GordianNewRSASpec mySpec : GordianNewRSASpec.values()) {
            myMap.put(mySpec, new GordianCoreRSASpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreRSASpec[] values() {
        return VALUES;
    }
}
