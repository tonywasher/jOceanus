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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.crypto.params.FrodoKEMParameters;
import org.bouncycastle.jcajce.spec.FrodoKEMParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * FRODO KeySpecs.
 */
public final class GordianCoreFRODOSpec
        implements GordianCoreKeyPairIdSpec<GordianFRODOSpec> {
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

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.FRODO;
    }

    @Override
    public GordianFRODOSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain Frodo Parameters.
     *
     * @return the parameters.
     */
    public FrodoKEMParameters getParameters() {
        return switch (theSpec) {
            case AES976 -> FrodoKEMParameters.frodokem976aes;
            case SHAKE976 -> FrodoKEMParameters.frodokem976shake;
            case AES976E -> FrodoKEMParameters.efrodokem976aes;
            case SHAKE976E -> FrodoKEMParameters.efrodokem976shake;
            case AES1344 -> FrodoKEMParameters.frodokem1344aes;
            case SHAKE1344 -> FrodoKEMParameters.frodokem1344shake;
            case AES1344E -> FrodoKEMParameters.efrodokem1344aes;
            case SHAKE1344E -> FrodoKEMParameters.efrodokem1344shake;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Frodo ParameterSpec.
     *
     * @return the parameters.
     */
    public FrodoKEMParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case AES976 -> FrodoKEMParameterSpec.frodokem976aes;
            case SHAKE976 -> FrodoKEMParameterSpec.frodokem976shake;
            case AES976E -> FrodoKEMParameterSpec.efrodokem976aes;
            case SHAKE976E -> FrodoKEMParameterSpec.efrodokem976shake;
            case AES1344 -> FrodoKEMParameterSpec.frodokem1344aes;
            case SHAKE1344 -> FrodoKEMParameterSpec.frodokem1344shake;
            case AES1344E -> FrodoKEMParameterSpec.efrodokem1344aes;
            case SHAKE1344E -> FrodoKEMParameterSpec.efrodokem1344shake;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case AES976 -> ISOIECObjectIdentifiers.frodokem976_aes;
            case SHAKE976 -> ISOIECObjectIdentifiers.frodokem976_shake;
            case AES976E -> ISOIECObjectIdentifiers.efrodokem976_aes;
            case SHAKE976E -> ISOIECObjectIdentifiers.efrodokem976_shake;
            case AES1344 -> ISOIECObjectIdentifiers.frodokem1344_aes;
            case SHAKE1344 -> ISOIECObjectIdentifiers.frodokem1344_shake;
            case AES1344E -> ISOIECObjectIdentifiers.efrodokem1344_aes;
            case SHAKE1344E -> ISOIECObjectIdentifiers.efrodokem1344_shake;
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
