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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFRODOSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * FRODO KeySpecs.
 */
public final class GordianCoreFRODOSpec {
    /**
     * The specMap.
     */
    private static final Map<GordianFRODOSpec, GordianCoreFRODOSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreFRODOSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreFRODOSpec[0]);

    /**
     * The Spec.
     */
    private final GordianFRODOSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreFRODOSpec(final GordianFRODOSpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianFRODOSpec getSpec() {
        return theSpec;
    }


    /**
     * Obtain Frodo Parameters.
     *
     * @return the parameters.
     */
    public FrodoParameters getParameters() {
        return switch (theSpec) {
            case AES640 -> FrodoParameters.frodokem640aes;
            case SHAKE640 -> FrodoParameters.frodokem640shake;
            case AES976 -> FrodoParameters.frodokem976aes;
            case SHAKE976 -> FrodoParameters.frodokem976shake;
            case AES1344 -> FrodoParameters.frodokem1344aes;
            case SHAKE1344 -> FrodoParameters.frodokem1344shake;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Frodo ParameterSpec.
     *
     * @return the parameters.
     */
    public FrodoParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case AES640 -> FrodoParameterSpec.frodokem640aes;
            case SHAKE640 -> FrodoParameterSpec.frodokem640shake;
            case AES976 -> FrodoParameterSpec.frodokem976aes;
            case SHAKE976 -> FrodoParameterSpec.frodokem976shake;
            case AES1344 -> FrodoParameterSpec.frodokem1344aes;
            case SHAKE1344 -> FrodoParameterSpec.frodokem1344shake;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Frodo algorithm Identifier.
     *
     * @return the identifier.
     */
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case AES640 -> BCObjectIdentifiers.frodokem640aes;
            case SHAKE640 -> BCObjectIdentifiers.frodokem640shake;
            case AES976 -> BCObjectIdentifiers.frodokem976aes;
            case SHAKE976 -> BCObjectIdentifiers.frodokem976shake;
            case AES1344 -> BCObjectIdentifiers.frodokem1344aes;
            case SHAKE1344 -> BCObjectIdentifiers.frodokem1344shake;
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
        return pSpec instanceof GordianFRODOSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianFRODOSpec, GordianCoreFRODOSpec> newSpecMap() {
        final Map<GordianFRODOSpec, GordianCoreFRODOSpec> myMap = new EnumMap<>(GordianFRODOSpec.class);
        for (GordianFRODOSpec mySpec : GordianFRODOSpec.values()) {
            myMap.put(mySpec, new GordianCoreFRODOSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreFRODOSpec[] values() {
        return VALUES;
    }
}
