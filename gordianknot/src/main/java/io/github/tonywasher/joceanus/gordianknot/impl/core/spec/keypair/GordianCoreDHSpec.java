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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDHSpec;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * DH Groups.
 */
public final class GordianCoreDHSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewDHSpec, GordianCoreDHSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewDHSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreDHSpec(final GordianNewDHSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewDHSpec getSpec() {
        return theSpec;
    }


    /**
     * Obtain the parameters.
     *
     * @return the parameters
     */
    public DHParameters getParameters() {
        switch (theSpec) {
            case STD1024:
                return DHStandardGroups.rfc2409_1024;
            case STD1536:
                return DHStandardGroups.rfc3526_1536;
            case STD2048:
                return DHStandardGroups.rfc3526_2048;
            case STD3072:
                return DHStandardGroups.rfc3526_3072;
            case STD4096:
                return DHStandardGroups.rfc3526_4096;
            case STD6144:
                return DHStandardGroups.rfc3526_6144;
            case STD8192:
                return DHStandardGroups.rfc3526_8192;
            case FFDHE2048:
                return DHStandardGroups.rfc7919_ffdhe2048;
            case FFDHE3072:
                return DHStandardGroups.rfc7919_ffdhe3072;
            case FFDHE4096:
                return DHStandardGroups.rfc7919_ffdhe4096;
            case FFDHE6144:
                return DHStandardGroups.rfc7919_ffdhe6144;
            case FFDHE8192:
                return DHStandardGroups.rfc7919_ffdhe8192;
            default:
                throw new IllegalArgumentException("Unknown GordianNewDHSpec");
        }
    }

    /**
     * Obtain the group for parameters.
     *
     * @param pParams the parameters
     * @return the group
     */
    public static GordianCoreDHSpec getSpecForParams(final DHParameters pParams) {
        /* Loop through the values */
        for (GordianCoreDHSpec myGroup : values()) {
            if (myGroup.getParameters().equals(pParams)) {
                return myGroup;
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
        return pThat instanceof GordianCoreDHSpec myThat
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
    public static GordianCoreDHSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewDHSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewDHSpec, GordianCoreDHSpec> newSpecMap() {
        final Map<GordianNewDHSpec, GordianCoreDHSpec> myMap = new EnumMap<>(GordianNewDHSpec.class);
        for (GordianNewDHSpec mySpec : GordianNewDHSpec.values()) {
            myMap.put(mySpec, new GordianCoreDHSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreDHSpec> values() {
        return SPECMAP.values();
    }
}
