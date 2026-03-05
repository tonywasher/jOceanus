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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSABERSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.jcajce.spec.SABERParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * SABER KeySpec.
 */
public final class GordianCoreSABERSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewSABERSpec, GordianCoreSABERSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreSABERSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreSABERSpec[0]);

    /**
     * The Spec.
     */
    private final GordianNewSABERSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSABERSpec(final GordianNewSABERSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewSABERSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain SABER Parameters.
     *
     * @return the parameters.
     */
    public SABERParameters getParameters() {
        switch (theSpec) {
            case LIGHT128:
                return SABERParameters.lightsaberkem128r3;
            case BASE128:
                return SABERParameters.saberkem128r3;
            case FIRE128:
                return SABERParameters.firesaberkem128r3;
            case LIGHT192:
                return SABERParameters.lightsaberkem192r3;
            case BASE192:
                return SABERParameters.saberkem192r3;
            case FIRE192:
                return SABERParameters.firesaberkem192r3;
            case LIGHT256:
                return SABERParameters.lightsaberkem256r3;
            case BASE256:
                return SABERParameters.saberkem256r3;
            case FIRE256:
                return SABERParameters.firesaberkem256r3;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain SABER ParameterSpec.
     *
     * @return the parameters.
     */
    public SABERParameterSpec getParameterSpec() {
        switch (theSpec) {
            case LIGHT128:
                return SABERParameterSpec.lightsaberkem128r3;
            case BASE128:
                return SABERParameterSpec.saberkem128r3;
            case FIRE128:
                return SABERParameterSpec.firesaberkem128r3;
            case LIGHT192:
                return SABERParameterSpec.lightsaberkem192r3;
            case BASE192:
                return SABERParameterSpec.saberkem192r3;
            case FIRE192:
                return SABERParameterSpec.firesaberkem192r3;
            case LIGHT256:
                return SABERParameterSpec.lightsaberkem256r3;
            case BASE256:
                return SABERParameterSpec.saberkem256r3;
            case FIRE256:
                return SABERParameterSpec.firesaberkem256r3;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Saber algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case LIGHT128:
                return BCObjectIdentifiers.lightsaberkem128r3;
            case BASE128:
                return BCObjectIdentifiers.saberkem128r3;
            case FIRE128:
                return BCObjectIdentifiers.firesaberkem128r3;
            case LIGHT192:
                return BCObjectIdentifiers.lightsaberkem192r3;
            case BASE192:
                return BCObjectIdentifiers.saberkem192r3;
            case FIRE192:
                return BCObjectIdentifiers.firesaberkem192r3;
            case LIGHT256:
                return BCObjectIdentifiers.lightsaberkem256r3;
            case BASE256:
                return BCObjectIdentifiers.saberkem256r3;
            case FIRE256:
                return BCObjectIdentifiers.firesaberkem256r3;
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
        return pThat instanceof GordianCoreSABERSpec myThat
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
    public static GordianCoreSABERSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewSABERSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewSABERSpec, GordianCoreSABERSpec> newSpecMap() {
        final Map<GordianNewSABERSpec, GordianCoreSABERSpec> myMap = new EnumMap<>(GordianNewSABERSpec.class);
        for (GordianNewSABERSpec mySpec : GordianNewSABERSpec.values()) {
            myMap.put(mySpec, new GordianCoreSABERSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreSABERSpec[] values() {
        return VALUES;
    }
}
