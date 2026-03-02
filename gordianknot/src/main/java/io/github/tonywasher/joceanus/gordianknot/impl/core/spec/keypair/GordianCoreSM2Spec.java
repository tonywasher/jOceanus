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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSM2Spec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * Named SM2 Elliptic Curves.
 */
public final class GordianCoreSM2Spec
        implements GordianCoreElliptic {
    /**
     * The specMap.
     */
    private static final Map<GordianNewSM2Spec, GordianCoreSM2Spec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewSM2Spec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSM2Spec(final GordianNewSM2Spec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewSM2Spec getSpec() {
        return theSpec;
    }

    @Override
    public String getCurveName() {
        switch (theSpec) {
            case SM2P256V1:
                return "sm2p256v1";
            case WAPIP192V1:
                return "wapip192v1";
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public GordianLength getKeySize() {
        switch (theSpec) {
            case SM2P256V1:
                return GordianLength.LEN_256;
            case WAPIP192V1:
                return GordianLength.LEN_192;
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
    public static GordianCoreSM2Spec getCurveForName(final String pName) {
        /* Loop through the curves */
        for (GordianCoreSM2Spec myCurve : values()) {
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
        return pThat instanceof GordianCoreSM2Spec myThat
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
    public static GordianCoreSM2Spec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewSM2Spec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewSM2Spec, GordianCoreSM2Spec> newSpecMap() {
        final Map<GordianNewSM2Spec, GordianCoreSM2Spec> myMap = new EnumMap<>(GordianNewSM2Spec.class);
        for (GordianNewSM2Spec mySpec : GordianNewSM2Spec.values()) {
            myMap.put(mySpec, new GordianCoreSM2Spec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreSM2Spec> values() {
        return SPECMAP.values();
    }
}
