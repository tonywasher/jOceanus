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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDSASpec;
import org.bouncycastle.asn1.x509.DSAParameter;

import java.util.EnumMap;
import java.util.Map;

/**
 * DSA KeyTypes.
 */
public final class GordianCoreDSASpec {
    /**
     * The specMap.
     */
    private static final Map<GordianNewDSASpec, GordianCoreDSASpec> SPECMAP = newSpecMap();

    /**
     * The specArray.
     */
    private static final GordianCoreDSASpec[] VALUES = SPECMAP.values().toArray(new GordianCoreDSASpec[0]);

    /**
     * The Spec.
     */
    private final GordianNewDSASpec theSpec;

    /**
     * Constructor.
     *
     * @param pSpec the spec
     */
    private GordianCoreDSASpec(final GordianNewDSASpec pSpec) {
        theSpec = pSpec;
    }

    /**
     * Obtain the spec.
     *
     * @return the spec
     */
    public GordianNewDSASpec getSpec() {
        return theSpec;
    }

    /**
     * Obtain the keySize of the keyType.
     *
     * @return the keySize
     */
    public int getKeySize() {
        switch (theSpec) {
            case MOD1024:
                return GordianLength.LEN_1024.getLength();
            case MOD2048:
                return GordianLength.LEN_2048.getLength();
            case MOD3072:
                return GordianLength.LEN_3072.getLength();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the hashSize of the keyType.
     *
     * @return the hashSize
     */
    public int getHashSize() {
        switch (theSpec) {
            case MOD1024:
                return GordianLength.LEN_160.getLength();
            case MOD2048:
            case MOD3072:
                return GordianLength.LEN_256.getLength();
            default:
                throw new IllegalArgumentException();
        }
    }

    /**
     * Obtain the DSAKeyType for DSAParameters.
     *
     * @param pParams the parameters
     * @return the DSAKeyType
     */
    public static GordianCoreDSASpec getDSASpecForParms(final DSAParameter pParams) {
        /* Loop through the values */
        final int myLen = pParams.getP().bitLength();
        final int myHashSize = pParams.getQ().bitLength();
        for (GordianCoreDSASpec mySpec : values()) {
            if (mySpec.getKeySize() == myLen
                    && mySpec.getHashSize() == myHashSize) {
                return mySpec;
            }
        }
        return null;
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
        return pThat instanceof GordianCoreDSASpec myThat
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
    public static GordianCoreDSASpec mapCoreSpec(final Object pSpec) {
        return pSpec instanceof GordianNewDSASpec mySpec ? SPECMAP.get(mySpec) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewDSASpec, GordianCoreDSASpec> newSpecMap() {
        final Map<GordianNewDSASpec, GordianCoreDSASpec> myMap = new EnumMap<>(GordianNewDSASpec.class);
        for (GordianNewDSASpec mySpec : GordianNewDSASpec.values()) {
            myMap.put(mySpec, new GordianCoreDSASpec(mySpec));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreDSASpec[] values() {
        return VALUES;
    }
}
