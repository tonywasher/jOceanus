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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianECSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * Named Elliptic Curves.
 */
public final class GordianCoreECSpec
        implements GordianCoreElliptic {
    /**
     * The specMap.
     */
    private static final Map<GordianECSpec, GordianCoreECSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreECSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreECSpec[0]);

    /**
     * The Spec.
     */
    private final GordianECSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreECSpec(final GordianECSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianECSpec getSpec() {
        return theSpec;
    }

    @Override
    public String getCurveName() {
        return switch (theSpec) {
            case SECT571K1 -> "sect571k1";
            case SECT571R1 -> "sect571r1";
            case SECP521R1 -> "secp521r1";
            case SECT409K1 -> "sect409k1";
            case SECT409R1 -> "sect409r1";
            case SECP384R1 -> "secp384r1";
            case SECT283K1 -> "sect283k1";
            case SECT283R1 -> "sect283r1";
            case SECP256K1 -> "secp256k1";
            case SECP256R1 -> "secp256r1";
            case BRAINPOOLP512R1 -> "brainpoolP512r1";
            case BRAINPOOLP512T1 -> "brainpoolP512t1";
            case BRAINPOOLP384R1 -> "brainpoolP384r1";
            case BRAINPOOLP384T1 -> "brainpoolP384t1";
            case BRAINPOOLP320R1 -> "brainpoolP320r1";
            case BRAINPOOLP320T1 -> "brainpoolP320t1";
            case BRAINPOOLP256R1 -> "brainpoolP256r1";
            case BRAINPOOLP256T1 -> "brainpoolP256t1";
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public int getKeySize() {
        return switch (theSpec) {
            case SECT571K1, SECT571R1 -> GordianLength.LEN_571.getLength();
            case SECP521R1 -> GordianLength.LEN_521.getLength();
            case BRAINPOOLP512R1, BRAINPOOLP512T1 -> GordianLength.LEN_512.getLength();
            case SECT409K1, SECT409R1 -> GordianLength.LEN_409.getLength();
            case SECP384R1, BRAINPOOLP384R1, BRAINPOOLP384T1 -> GordianLength.LEN_384.getLength();
            case BRAINPOOLP320R1, BRAINPOOLP320T1 -> GordianLength.LEN_320.getLength();
            case SECT283K1, SECT283R1 -> GordianLength.LEN_283.getLength();
            case SECP256K1, SECP256R1, BRAINPOOLP256R1, BRAINPOOLP256T1 -> GordianLength.LEN_256.getLength();
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain the curve for a Name.
     *
     * @param pName the name
     * @return the curve
     */
    public static GordianCoreECSpec getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianCoreECSpec myCurve : values()) {
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
    public boolean hasCustomCurve() {
        return switch (theSpec) {
            case SECP256K1, SECP256R1, SECP384R1, SECP521R1, SECT283K1, SECT283R1, SECT409K1, SECT409R1, SECT571K1,
                 SECT571R1 -> true;
            default -> false;
        };
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
        return pThat instanceof GordianCoreECSpec myThat
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
    public static GordianCoreECSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianECSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianECSpec, GordianCoreECSpec> newSpecMap() {
        final Map<GordianECSpec, GordianCoreECSpec> myMap = new EnumMap<>(GordianECSpec.class);
        for (GordianECSpec mySpec : GordianECSpec.values()) {
            myMap.put(mySpec, new GordianCoreECSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreECSpec[] values() {
        return VALUES;
    }
}
