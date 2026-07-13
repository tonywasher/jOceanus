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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHawkSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.hawk.HawkParameters;
import org.bouncycastle.pqc.jcajce.spec.HawkParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * Hawk KeySpec.
 */
public final class GordianCoreHawkSpec
        implements GordianCoreKeyPairIdSpec<GordianHawkSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianHawkSpec, GordianCoreHawkSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreHawkSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreHawkSpec[0]);

    /**
     * The Spec.
     */
    private final GordianHawkSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreHawkSpec(final GordianHawkSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.HAWK;
    }

    @Override
    public GordianHawkSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain Hawk Parameters.
     *
     * @return the parameters.
     */
    public HawkParameters getParameters() {
        return switch (theSpec) {
            case HAWK256 -> HawkParameters.Hawk_256;
            case HAWK512 -> HawkParameters.Hawk_512;
            case HAWK1024 -> HawkParameters.Hawk_1024;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Hawk ParameterSpec.
     *
     * @return the parameters.
     */
    public HawkParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case HAWK256 -> HawkParameterSpec.hawk_256;
            case HAWK512 -> HawkParameterSpec.hawk_512;
            case HAWK1024 -> HawkParameterSpec.hawk_1024;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case HAWK256 -> BCObjectIdentifiers.hawk256;
            case HAWK512 -> BCObjectIdentifiers.hawk512;
            case HAWK1024 -> BCObjectIdentifiers.hawk1024;
            default -> throw new IllegalArgumentException();
        };
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
        return pThat instanceof GordianCoreHawkSpec myThat
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
    public static GordianCoreHawkSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianHawkSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianHawkSpec, GordianCoreHawkSpec> newSpecMap() {
        final Map<GordianHawkSpec, GordianCoreHawkSpec> myMap = new EnumMap<>(GordianHawkSpec.class);
        for (GordianHawkSpec mySpec : GordianHawkSpec.values()) {
            myMap.put(mySpec, new GordianCoreHawkSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreHawkSpec[] values() {
        return VALUES;
    }
}
