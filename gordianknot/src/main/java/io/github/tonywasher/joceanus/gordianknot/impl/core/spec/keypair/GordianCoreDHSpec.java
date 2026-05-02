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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDHSpec;
import org.bouncycastle.crypto.agreement.DHStandardGroups;
import org.bouncycastle.crypto.params.DHParameters;

import java.util.EnumMap;
import java.util.Map;

/**
 * DH Groups.
 */
public final class GordianCoreDHSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianDHSpec, GordianCoreDHSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreDHSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreDHSpec[0]);

    /**
     * The Spec.
     */
    private final GordianDHSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreDHSpec(final GordianDHSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianDHSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the length for a spec.
     *
     * @return the length
     */
    public int getLength() {
        return switch (theSpec) {
            case STD1024 -> GordianLength.LEN_1024.getLength();
            case STD1536 -> GordianLength.LEN_1536.getLength();
            case STD2048, FFDHE2048 -> GordianLength.LEN_2048.getLength();
            case STD3072, FFDHE3072 -> GordianLength.LEN_3072.getLength();
            case STD4096, FFDHE4096 -> GordianLength.LEN_4096.getLength();
            case STD6144, FFDHE6144 -> GordianLength.LEN_6144.getLength();
            case STD8192, FFDHE8192 -> GordianLength.LEN_8192.getLength();
            default -> throw new IllegalArgumentException("Unknown GordianNewRSASpec");
        };
    }

    /**
     * Obtain the parameters.
     *
     * @return the parameters
     */
    public DHParameters getParameters() {
        return switch (theSpec) {
            case STD1024 -> DHStandardGroups.rfc2409_1024;
            case STD1536 -> DHStandardGroups.rfc3526_1536;
            case STD2048 -> DHStandardGroups.rfc3526_2048;
            case STD3072 -> DHStandardGroups.rfc3526_3072;
            case STD4096 -> DHStandardGroups.rfc3526_4096;
            case STD6144 -> DHStandardGroups.rfc3526_6144;
            case STD8192 -> DHStandardGroups.rfc3526_8192;
            case FFDHE2048 -> DHStandardGroups.rfc7919_ffdhe2048;
            case FFDHE3072 -> DHStandardGroups.rfc7919_ffdhe3072;
            case FFDHE4096 -> DHStandardGroups.rfc7919_ffdhe4096;
            case FFDHE6144 -> DHStandardGroups.rfc7919_ffdhe6144;
            case FFDHE8192 -> DHStandardGroups.rfc7919_ffdhe8192;
            default -> throw new IllegalArgumentException("Unknown GordianDHSpec");
        };
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
        return pSpec instanceof GordianDHSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianDHSpec, GordianCoreDHSpec> newSpecMap() {
        final Map<GordianDHSpec, GordianCoreDHSpec> myMap = new EnumMap<>(GordianDHSpec.class);
        for (GordianDHSpec mySpec : GordianDHSpec.values()) {
            myMap.put(mySpec, new GordianCoreDHSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreDHSpec[] values() {
        return VALUES;
    }
}
