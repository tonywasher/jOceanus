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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianAIMerSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.aimer.AIMerParameters;
import org.bouncycastle.pqc.jcajce.spec.AIMerParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * AIMer Core Spec.
 */
public final class GordianCoreAIMerSpec
        implements GordianCoreKeyPairIdSpec<GordianAIMerSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianAIMerSpec, GordianCoreAIMerSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreAIMerSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreAIMerSpec[0]);

    /**
     * The Spec.
     */
    private final GordianAIMerSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreAIMerSpec(final GordianAIMerSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.AIMER;
    }

    @Override
    public GordianAIMerSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain BIKE Parameters.
     *
     * @return the parameters.
     */
    public AIMerParameters getParameters() {
        return switch (theSpec) {
            case AIMER128F -> AIMerParameters.aimer128f;
            case AIMER128S -> AIMerParameters.aimer128s;
            case AIMER192F -> AIMerParameters.aimer192f;
            case AIMER192S -> AIMerParameters.aimer192s;
            case AIMER256F -> AIMerParameters.aimer256f;
            case AIMER256S -> AIMerParameters.aimer256s;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain BIKE ParameterSpec.
     *
     * @return the parameters.
     */
    public AIMerParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case AIMER128F -> AIMerParameterSpec.aimer128f;
            case AIMER128S -> AIMerParameterSpec.aimer128s;
            case AIMER192F -> AIMerParameterSpec.aimer192f;
            case AIMER192S -> AIMerParameterSpec.aimer192s;
            case AIMER256F -> AIMerParameterSpec.aimer256f;
            case AIMER256S -> AIMerParameterSpec.aimer256s;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case AIMER128F -> BCObjectIdentifiers.aimer_128f;
            case AIMER128S -> BCObjectIdentifiers.aimer_128s;
            case AIMER192F -> BCObjectIdentifiers.aimer_192f;
            case AIMER192S -> BCObjectIdentifiers.aimer_192s;
            case AIMER256F -> BCObjectIdentifiers.aimer_256f;
            case AIMER256S -> BCObjectIdentifiers.aimer_256s;
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
        return pThat instanceof GordianCoreAIMerSpec myThat
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
    public static GordianCoreAIMerSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianAIMerSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianAIMerSpec, GordianCoreAIMerSpec> newSpecMap() {
        final Map<GordianAIMerSpec, GordianCoreAIMerSpec> myMap = new EnumMap<>(GordianAIMerSpec.class);
        for (GordianAIMerSpec mySpec : GordianAIMerSpec.values()) {
            myMap.put(mySpec, new GordianCoreAIMerSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreAIMerSpec[] values() {
        return VALUES;
    }
}
