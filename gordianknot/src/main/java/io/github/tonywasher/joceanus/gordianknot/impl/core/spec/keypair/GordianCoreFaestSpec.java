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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFaestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.faest.FaestParameters;
import org.bouncycastle.pqc.jcajce.spec.FaestParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * Faest KeySpec.
 */
public final class GordianCoreFaestSpec
        implements GordianCoreKeyPairIdSpec<GordianFaestSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianFaestSpec, GordianCoreFaestSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreFaestSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreFaestSpec[0]);

    /**
     * The Spec.
     */
    private final GordianFaestSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreFaestSpec(final GordianFaestSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.FAEST;
    }

    @Override
    public GordianFaestSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain Faest Parameters.
     *
     * @return the parameters.
     */
    public FaestParameters getParameters() {
        return switch (theSpec) {
            case FAEST128S -> FaestParameters.faest_128s;
            case FAEST128F -> FaestParameters.faest_128f;
            case FAEST192S -> FaestParameters.faest_192s;
            case FAEST192F -> FaestParameters.faest_192f;
            case FAEST256S -> FaestParameters.faest_256s;
            case FAEST256F -> FaestParameters.faest_256f;
            case FAESTEM128S -> FaestParameters.faest_em_128s;
            case FAESTEM128F -> FaestParameters.faest_em_128f;
            case FAESTEM192S -> FaestParameters.faest_em_192s;
            case FAESTEM192F -> FaestParameters.faest_em_192f;
            case FAESTEM256S -> FaestParameters.faest_em_256s;
            case FAESTEM256F -> FaestParameters.faest_em_256f;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain Faest ParameterSpec.
     *
     * @return the parameters.
     */
    public FaestParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case FAEST128S -> FaestParameterSpec.faest_128s;
            case FAEST128F -> FaestParameterSpec.faest_128f;
            case FAEST192S -> FaestParameterSpec.faest_192s;
            case FAEST192F -> FaestParameterSpec.faest_192f;
            case FAEST256S -> FaestParameterSpec.faest_256s;
            case FAEST256F -> FaestParameterSpec.faest_256f;
            case FAESTEM128S -> FaestParameterSpec.faest_em_128s;
            case FAESTEM128F -> FaestParameterSpec.faest_em_128f;
            case FAESTEM192S -> FaestParameterSpec.faest_em_192s;
            case FAESTEM192F -> FaestParameterSpec.faest_em_192f;
            case FAESTEM256S -> FaestParameterSpec.faest_em_256s;
            case FAESTEM256F -> FaestParameterSpec.faest_em_256f;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case FAEST128S -> BCObjectIdentifiers.faest_128s;
            case FAEST128F -> BCObjectIdentifiers.faest_128f;
            case FAEST192S -> BCObjectIdentifiers.faest_192s;
            case FAEST192F -> BCObjectIdentifiers.faest_192f;
            case FAEST256S -> BCObjectIdentifiers.faest_256s;
            case FAEST256F -> BCObjectIdentifiers.faest_256f;
            case FAESTEM128S -> BCObjectIdentifiers.faest_em_128s;
            case FAESTEM128F -> BCObjectIdentifiers.faest_em_128f;
            case FAESTEM192S -> BCObjectIdentifiers.faest_em_192s;
            case FAESTEM192F -> BCObjectIdentifiers.faest_em_192f;
            case FAESTEM256S -> BCObjectIdentifiers.faest_em_256s;
            case FAESTEM256F -> BCObjectIdentifiers.faest_em_256f;
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
        return pThat instanceof GordianCoreFaestSpec myThat
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
    public static GordianCoreFaestSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianFaestSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianFaestSpec, GordianCoreFaestSpec> newSpecMap() {
        final Map<GordianFaestSpec, GordianCoreFaestSpec> myMap = new EnumMap<>(GordianFaestSpec.class);
        for (GordianFaestSpec mySpec : GordianFaestSpec.values()) {
            myMap.put(mySpec, new GordianCoreFaestSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreFaestSpec[] values() {
        return VALUES;
    }
}
