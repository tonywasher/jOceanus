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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSmaugTSpec;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.pqc.crypto.smaugt.SmaugTParameters;
import org.bouncycastle.pqc.jcajce.spec.SmaugTParameterSpec;

import java.util.EnumMap;
import java.util.Map;

/**
 * SmaugT KeySpec.
 */
public final class GordianCoreSmaugTSpec
        implements GordianCoreKeyPairIdSpec<GordianSmaugTSpec> {
    /**
     * The specMap.
     */
    private static final Map<GordianSmaugTSpec, GordianCoreSmaugTSpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreSmaugTSpec[] VALUES = SPECMAP.values().toArray(new GordianCoreSmaugTSpec[0]);

    /**
     * The Spec.
     */
    private final GordianSmaugTSpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreSmaugTSpec(final GordianSmaugTSpec pSpec) {
        theSpec = pSpec;
    }

    @Override
    public GordianKeyPairType getKeyPairType() {
        return GordianKeyPairType.SMAUGT;
    }

    @Override
    public GordianSmaugTSpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain NTRU Parameters.
     *
     * @return the parameters.
     */
    public SmaugTParameters getParameters() {
        return switch (theSpec) {
            case MODE1 -> SmaugTParameters.smaugt_mode1;
            case MODE3 -> SmaugTParameters.smaugt_mode3;
            case MODE5 -> SmaugTParameters.smaugt_mode5;
            case MODET -> SmaugTParameters.smaugt_modet;
            default -> throw new IllegalArgumentException();
        };
    }

    /**
     * Obtain NTRU ParameterSpec.
     *
     * @return the parameters.
     */
    public SmaugTParameterSpec getParameterSpec() {
        return switch (theSpec) {
            case MODE1 -> SmaugTParameterSpec.smaugt_mode1;
            case MODE3 -> SmaugTParameterSpec.smaugt_mode3;
            case MODE5 -> SmaugTParameterSpec.smaugt_mode5;
            case MODET -> SmaugTParameterSpec.smaugt_modet;
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public ASN1ObjectIdentifier getIdentifier() {
        return switch (theSpec) {
            case MODE1 -> BCObjectIdentifiers.smaugt_mode1;
            case MODE3 -> BCObjectIdentifiers.smaugt_mode3;
            case MODE5 -> BCObjectIdentifiers.smaugt_mode5;
            case MODET -> BCObjectIdentifiers.smaugt_modet;
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
        return pThat instanceof GordianCoreSmaugTSpec myThat
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
    public static GordianCoreSmaugTSpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianSmaugTSpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianSmaugTSpec, GordianCoreSmaugTSpec> newSpecMap() {
        final Map<GordianSmaugTSpec, GordianCoreSmaugTSpec> myMap = new EnumMap<>(GordianSmaugTSpec.class);
        for (GordianSmaugTSpec mySpec : GordianSmaugTSpec.values()) {
            myMap.put(mySpec, new GordianCoreSmaugTSpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreSmaugTSpec[] values() {
        return VALUES;
    }
}
