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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPlusSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.ntruplus.NTRUPlusParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRUPlusParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * NTRUPlus KeySpec.
 */
public final class GordianCoreNTRUPlusSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNTRUPlusSpec, GordianCoreNTRUPlusSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreNTRUPlusSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreNTRUPlusSpec[0]);

    /**
     * The Spec.
     */
    private final GordianNTRUPlusSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreNTRUPlusSpec(final GordianNTRUPlusSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNTRUPlusSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain NTRU Parameters.
     *
     * @return the parameters.
     */
    public NTRUPlusParameters getParameters() {
        return switch (theSpec) {
            case KEM768 -> NTRUPlusParameters.ntruplus_kem_768;
            case KEM864 -> NTRUPlusParameters.ntruplus_kem_864;
            case KEM1152 -> NTRUPlusParameters.ntruplus_kem_1152;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain NTRU ParameterSpec.
     *
     * @return the parameters.
     */
    public NTRUPlusParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case KEM768 -> NTRUPlusParameterSpec.ntruplus_768;
            case KEM864 -> NTRUPlusParameterSpec.ntruplus_864;
            case KEM1152 -> NTRUPlusParameterSpec.ntruplus_1152;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain NTRU algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case KEM768 -> BCObjectIdentifiers.ntruplus768;
            case KEM864 -> BCObjectIdentifiers.ntruplus864;
            case KEM1152 -> BCObjectIdentifiers.ntruplus1152;
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
        return pThat instanceof GordianCoreNTRUPlusSpec myThat
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
    public static GordianCoreNTRUPlusSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNTRUPlusSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNTRUPlusSpec, GordianCoreNTRUPlusSpec> newSpecMap() {
        final Map<GordianNTRUPlusSpec, GordianCoreNTRUPlusSpec> myMap = new EnumMap<>(GordianNTRUPlusSpec.class);
        for (GordianNTRUPlusSpec mySpec : GordianNTRUPlusSpec.values()) {
            myMap.put(mySpec, new GordianCoreNTRUPlusSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreNTRUPlusSpec[] values() {
        return VALUES;
    }
}
