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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSQIsignSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.sqisign.SQIsignParameters;
import org.bouncycastle.pqc.jcajce.spec.SQIsignParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * SQIsign KeySpecs.
 */
public final class GordianCoreSQIsignSpec
        implements GordianCoreKeyPairIdSpec<GordianSQIsignSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianSQIsignSpec, GordianCoreSQIsignSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreSQIsignSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreSQIsignSpec[0]);

    /**
     * The Spec.
     */
    private final GordianSQIsignSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSQIsignSpec(final GordianSQIsignSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.SQISIGN;
    }

    @Override
    public GordianSQIsignSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain SQIsign Parameters.
     *
     * @return the parameters.
     */
    public SQIsignParameters getParameters() {
        return switch (theSpec) {
            case SQISIGN1 -> SQIsignParameters.sqisign_lvl1;
            case SQISIGN3 -> SQIsignParameters.sqisign_lvl3;
            case SQISIGN5 -> SQIsignParameters.sqisign_lvl5;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain SQIsign ParameterSpec.
     *
     * @return the parameters.
     */
    public SQIsignParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case SQISIGN1 -> SQIsignParameterSpec.sqisign_lvl1;
            case SQISIGN3 -> SQIsignParameterSpec.sqisign_lvl3;
            case SQISIGN5 -> SQIsignParameterSpec.sqisign_lvl5;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case SQISIGN1 -> BCObjectIdentifiers.sqisign_lvl1;
            case SQISIGN3 -> BCObjectIdentifiers.sqisign_lvl3;
            case SQISIGN5 -> BCObjectIdentifiers.sqisign_lvl5;
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
        return pThat instanceof GordianCoreSQIsignSpec myThat
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
    public static GordianCoreSQIsignSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianSQIsignSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianSQIsignSpec, GordianCoreSQIsignSpec> newSpecMap() {
        final Map<GordianSQIsignSpec, GordianCoreSQIsignSpec> myMap = new EnumMap<>(GordianSQIsignSpec.class);
        for (GordianSQIsignSpec mySpec : GordianSQIsignSpec.values()) {
            myMap.put(mySpec, new GordianCoreSQIsignSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreSQIsignSpec[] values() {
        return VALUES;
    }
}
