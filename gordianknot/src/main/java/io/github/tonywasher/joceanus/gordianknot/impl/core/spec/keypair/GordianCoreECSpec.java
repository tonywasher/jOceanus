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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewECSpec;

import java.util.Collection;
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
    private static final Map<GordianNewECSpec, GordianCoreECSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewECSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreECSpec(final GordianNewECSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewECSpec getSpec() {
        return theSpec;
    }

    @Override
    public String getCurveName() {
        switch (theSpec) {
            case SECT571K1:
                return "sect571k1";
            case SECT571R1:
                return "sect571r1";
            case SECP521R1:
                return "secp521r1";
            case SECT409K1:
                return "sect409k1";
            case SECT409R1:
                return "sect409r1";
            case SECP384R1:
                return "secp384r1";
            case SECT283K1:
                return "sect283k1";
            case SECT283R1:
                return "sect283r1";
            case SECP256K1:
                return "secp256k1";
            case SECP256R1:
                return "secp256r1";
            case BRAINPOOLP512R1:
                return "brainpoolP512r1";
            case BRAINPOOLP512T1:
                return "brainpoolP512t1";
            case BRAINPOOLP384R1:
                return "brainpoolP384r1";
            case BRAINPOOLP384T1:
                return "brainpoolP384t1";
            case BRAINPOOLP320R1:
                return "brainpoolP320r1";
            case BRAINPOOLP320T1:
                return "brainpoolP320t1";
            case BRAINPOOLP256R1:
                return "brainpoolP256r1";
            case BRAINPOOLP256T1:
                return "brainpoolP256r1";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public GordianLength getKeySize() {
        switch (theSpec) {
            case SECT571K1:
            case SECT571R1:
                return GordianLength.LEN_571;
            case SECP521R1:
                return GordianLength.LEN_521;
            case BRAINPOOLP512R1:
            case BRAINPOOLP512T1:
                return GordianLength.LEN_512;
            case SECT409K1:
            case SECT409R1:
                return GordianLength.LEN_409;
            case SECP384R1:
            case BRAINPOOLP384R1:
            case BRAINPOOLP384T1:
                return GordianLength.LEN_384;
            case BRAINPOOLP320R1:
            case BRAINPOOLP320T1:
                return GordianLength.LEN_320;
            case SECT283K1:
            case SECT283R1:
                return GordianLength.LEN_283;
            case SECP256K1:
            case SECP256R1:
            case BRAINPOOLP256R1:
            case BRAINPOOLP256T1:
                return GordianLength.LEN_256;
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
        return pSpec instanceof GordianNewECSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewECSpec, GordianCoreECSpec> newSpecMap() {
        final Map<GordianNewECSpec, GordianCoreECSpec> myMap = new EnumMap<>(GordianNewECSpec.class);
        for (GordianNewECSpec mySpec : GordianNewECSpec.values()) {
            myMap.put(mySpec, new GordianCoreECSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreECSpec> values() {
        return SPECMAP.values();
    }
}
