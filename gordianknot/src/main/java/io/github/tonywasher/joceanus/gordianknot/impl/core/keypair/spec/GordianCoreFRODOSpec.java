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

package io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewFRODOSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * FRODO KeySpecs.
 */
public final class GordianCoreFRODOSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewFRODOSpec, GordianCoreFRODOSpec> SPECMAP = newSpecMap();

    /**
     * The Spec.
     */
    private final GordianNewFRODOSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreFRODOSpec(final GordianNewFRODOSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewFRODOSpec getSpec() {
        return theSpec;
    }


    /**
     * Obtain Frodo Parameters.
     *
     * @return the parameters.
     */
    public FrodoParameters getParameters() {
        switch (theSpec) {
            case AES640:
                return FrodoParameters.frodokem640aes;
            case SHAKE640:
                return FrodoParameters.frodokem640shake;
            case AES976:
                return FrodoParameters.frodokem976aes;
            case SHAKE976:
                return FrodoParameters.frodokem976shake;
            case AES1344:
                return FrodoParameters.frodokem1344aes;
            case SHAKE1344:
                return FrodoParameters.frodokem1344shake;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Frodo ParameterSpec.
     *
     * @return the parameters.
     */
    public FrodoParameterSpec getParameterSpec() {
        switch (theSpec) {
            case AES640:
                return FrodoParameterSpec.frodokem640aes;
            case SHAKE640:
                return FrodoParameterSpec.frodokem640shake;
            case AES976:
                return FrodoParameterSpec.frodokem976aes;
            case SHAKE976:
                return FrodoParameterSpec.frodokem976shake;
            case AES1344:
                return FrodoParameterSpec.frodokem1344aes;
            case SHAKE1344:
                return FrodoParameterSpec.frodokem1344shake;
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain Frodo algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        switch (theSpec) {
            case AES640:
                return BCObjectIdentifiers.frodokem640aes;
            case SHAKE640:
                return BCObjectIdentifiers.frodokem640shake;
            case AES976:
                return BCObjectIdentifiers.frodokem976aes;
            case SHAKE976:
                return BCObjectIdentifiers.frodokem976shake;
            case AES1344:
                return BCObjectIdentifiers.frodokem1344aes;
            case SHAKE1344:
                return BCObjectIdentifiers.frodokem1344shake;
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
        return pThat instanceof GordianCoreFRODOSpec myThat
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
    public static GordianCoreFRODOSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewFRODOSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewFRODOSpec, GordianCoreFRODOSpec> newSpecMap() {
        final Map<GordianNewFRODOSpec, GordianCoreFRODOSpec> myMap = new EnumMap<>(GordianNewFRODOSpec.class);
        for (GordianNewFRODOSpec mySpec : GordianNewFRODOSpec.values()) {
            myMap.put(mySpec, new GordianCoreFRODOSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static Collection<GordianCoreFRODOSpec> values() {
        return SPECMAP.values();
    }
}
