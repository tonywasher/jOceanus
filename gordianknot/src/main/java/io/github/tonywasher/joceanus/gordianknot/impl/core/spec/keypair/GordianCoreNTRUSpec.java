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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.ntru.NTRUParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRUParameterSpec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * NTRU KeySpec.
 */
public final class GordianCoreNTRUSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewNTRUSpec, GordianCoreNTRUSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewNTRUSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreNTRUSpec(final GordianNewNTRUSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewNTRUSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain NTRU Parameters.
     *
     * @return the parameters.
     */
    public NTRUParameters getParameters() {
        switch (theSpec) {
            case HPS509:
                return NTRUParameters.ntruhps2048509;
            case HPS677:
                return NTRUParameters.ntruhps2048677;
            case HPS821:
                return NTRUParameters.ntruhps4096821;
            case HPS1229:
                return NTRUParameters.ntruhps40961229;
            case HRSS701:
                return NTRUParameters.ntruhrss701;
            case HRSS1373:
                return NTRUParameters.ntruhrss1373;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain NTRU ParameterSpec.
     *
     * @return the parameters.
     */
    public NTRUParameterSpec getParameterSpec() {
        switch (theSpec) {
            case HPS509:
                return NTRUParameterSpec.ntruhps2048509;
            case HPS677:
                return NTRUParameterSpec.ntruhps2048677;
            case HPS821:
                return NTRUParameterSpec.ntruhps4096821;
            case HPS1229:
                return NTRUParameterSpec.ntruhps40961229;
            case HRSS701:
                return NTRUParameterSpec.ntruhrss701;
            case HRSS1373:
                return NTRUParameterSpec.ntruhrss1373;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain NTRU algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case HPS509:
                return BCObjectIdentifiers.ntruhps2048509;
            case HPS677:
                return BCObjectIdentifiers.ntruhps2048677;
            case HPS821:
                return BCObjectIdentifiers.ntruhps4096821;
            case HPS1229:
                return BCObjectIdentifiers.ntruhps40961229;
            case HRSS701:
                return BCObjectIdentifiers.ntruhrss701;
            case HRSS1373:
                return BCObjectIdentifiers.ntruhrss1373;
            default:
                throw new IllegalArgumentException();
        }
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
        return pThat instanceof GordianCoreNTRUSpec myThat
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
    public static GordianCoreNTRUSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewNTRUSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewNTRUSpec, GordianCoreNTRUSpec> newSpecMap() {
        final Map<GordianNewNTRUSpec, GordianCoreNTRUSpec> myMap = new EnumMap<>(GordianNewNTRUSpec.class);
        for (GordianNewNTRUSpec mySpec : GordianNewNTRUSpec.values()) {
            myMap.put(mySpec, new GordianCoreNTRUSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreNTRUSpec> values() {
        return SPECMAP.values();
    }
}
