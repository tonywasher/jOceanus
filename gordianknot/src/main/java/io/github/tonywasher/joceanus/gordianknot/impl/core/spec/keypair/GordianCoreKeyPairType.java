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

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianRequired;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Asymmetric KeyPairTypes.
 */
public final class GordianCoreKeyPairType {
    /**
     * The keyPairTypeMap.
     */
    private static final Map<GordianKeyPairType, GordianCoreKeyPairType> TYPEMAP = newTypeMap();

    /**
     * The keyPairTypeArray.
     */
    private static final GordianCoreKeyPairType[] VALUES = TYPEMAP.values().toArray(new GordianCoreKeyPairType[0]);

    /**
     * The keyPairType.
     */
    private final GordianKeyPairType theType;

    /**
     * Constructor.
     *
     * @param pType the type
     */
    private GordianCoreKeyPairType(final GordianKeyPairType pType) {
        theType = pType;
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianKeyPairType getType() {
        return theType;
    }

    /**
     * use random for signatures?
     *
     * @return true/false
     */
    public boolean useRandomForSignatures() {
        return switch (theType) {
            case PICNIC, LMS, XMSS, EDDSA -> false;
            default -> true;
        };
    }

    /**
     * Do we need a digest for signatures?
     *
     * @return ALWAYS/POSSIBLE/NEVER
     */
    public GordianRequired useDigestForSignatures() {
        return switch (theType) {
            case SLHDSA, MLDSA, FALCON, MAYO, SNOVA, XMSS, EDDSA, LMS -> GordianRequired.NEVER;
            case PICNIC -> GordianRequired.POSSIBLE;
            default -> GordianRequired.ALWAYS;
        };
    }

    /**
     * use subType for signatures?
     *
     * @return true/false
     */
    public boolean subTypeForSignatures() {
        return switch (theType) {
            case MLDSA, SLHDSA, FALCON, MAYO, SNOVA, XMSS -> true;
            default -> false;
        };
    }

    /**
     * Is the keyPair in the standard jcaProvider?
     *
     * @return true/false
     */
    public boolean isStandardJca() {
        return switch (theType) {
            case RSA, DSA, EC, ELGAMAL, DH, SM2, GOST, DSTU, XDH, EDDSA, MLKEM, MLDSA, SLHDSA -> true;
            default -> false;
        };
    }

    @Override
    public String toString() {
        return theType.toString();
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
        return pThat instanceof GordianCoreKeyPairType myThat
                && theType == myThat.getType();
    }

    @Override
    public int hashCode() {
        return theType.hashCode();
    }

    /**
     * Obtain the core type.
     *
     * @param pType the base type
     * @return the core type
     */
    public static GordianCoreKeyPairType mapCoreType(final Object pType) {
        return pType instanceof GordianKeyPairType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianKeyPairType, GordianCoreKeyPairType> newTypeMap() {
        final Map<GordianKeyPairType, GordianCoreKeyPairType> myMap = new EnumMap<>(GordianKeyPairType.class);
        for (GordianKeyPairType myType : GordianKeyPairType.values()) {
            myMap.put(myType, new GordianCoreKeyPairType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreKeyPairType[] values() {
        return VALUES;
    }
}
