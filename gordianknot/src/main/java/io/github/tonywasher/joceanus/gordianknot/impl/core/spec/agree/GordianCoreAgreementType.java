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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.agree;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM9Spec.GordianSM9EncryptType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Agreement Type.
 */
public final class GordianCoreAgreementType {
    /**
     * The agreementTypeMap.
     */
    private static final Map<GordianAgreementType, GordianCoreAgreementType> TYPEMAP = newTypeMap();

    /**
     * The agreementTypeArray.
     */
    private static final GordianCoreAgreementType[] VALUES = TYPEMAP.values().toArray(new GordianCoreAgreementType[0]);

    /**
     * The type.
     */
    private final GordianAgreementType theType;

    /**
     * Constructor.
     *
     * @param pType the agreementType
     */
    private GordianCoreAgreementType(final GordianAgreementType pType) {
        theType = pType;
    }

    /**
     * Obtain the Type.
     *
     * @return the Type
     */
    public GordianAgreementType getType() {
        return theType;
    }

    /**
     * Is this an anonymous agreement?
     *
     * @return true/false
     */
    public boolean isAnonymous() {
        return switch (theType) {
            case KEM, ANON -> true;
            default -> false;
        };
    }

    /**
     * Is this a signed agreement?
     *
     * @return true/false
     */
    public boolean isSigned() {
        return theType == GordianAgreementType.SIGNED;
    }

    /**
     * Is this an confirmable agreement?
     *
     * @return true/false
     */
    public boolean canConfirm() {
        return switch (theType) {
            case UNIFIED, MQV, SM2 -> true;
            default -> false;
        };
    }

    /**
     * Is this Agreement supported for this KeyPairSpec?
     *
     * @param pKeyPairSpec the keyPairSpec
     * @return true/false
     */
    public boolean isSupported(final GordianKeyPairSpec pKeyPairSpec) {
        final GordianKeyPairType myType = pKeyPairSpec.getKeyPairType();
        if (myType == GordianKeyPairType.COMPOSITE) {
            return true;
        }
        return switch (theType) {
            case KEM -> hasKEM(pKeyPairSpec);
            case ANON -> hasAnon(myType);
            case BASIC, SIGNED -> hasBasic(myType);
            case SM2 -> hasSM2(myType);
            case MQV -> hasMQV(myType);
            case UNIFIED -> hasUnified(myType);
            case SM9 -> hasSM9(pKeyPairSpec);
            default -> false;
        };
    }

    /**
     * Does the keyPairSpec have an KEM agreement?
     *
     * @param pKeyPairSpec the keyPairSpec
     * @return true/false
     */
    public static boolean hasKEM(final GordianKeyPairSpec pKeyPairSpec) {
        return switch (pKeyPairSpec.getKeyPairType()) {
            case RSA, EC, GOST, DSTU, SM2, CMCE, FRODO, SABER, MLKEM, HQC,
                 BIKE, NTRU, NTRUPLUS, NTRUPRIME, NEWHOPE, SMAUGT -> true;
            case SM9 -> switch (pKeyPairSpec.getSubSpec()) {
                case GordianSM9EncryptType.ENCMASTER, GordianSM9EncryptType.ENCRYPT -> true;
                default -> false;
            };
            default -> false;
        };
    }

    /**
     * Does the keyPairType have an ANON agreement?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasAnon(final GordianKeyPairType pKeyPairType) {
        return switch (pKeyPairType) {
            case DH, EC, SM2, GOST, DSTU, XDH -> true;
            default -> false;
        };
    }

    /**
     * Does the keyPairType have an SM2 agreement?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasSM2(final GordianKeyPairType pKeyPairType) {
        return switch (pKeyPairType) {
            case EC, SM2, GOST -> true;
            default -> false;
        };
    }

    /**
     * Does the keyPairType have a Basic agreement?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasBasic(final GordianKeyPairType pKeyPairType) {
        return isECorDH(pKeyPairType);
    }

    /**
     * Does the keyPairType have a MQV agreement?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasMQV(final GordianKeyPairType pKeyPairType) {
        return pKeyPairType == GordianKeyPairType.DH || isEC(pKeyPairType);
    }

    /**
     * Does the keyPairType have a Unified agreement?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasUnified(final GordianKeyPairType pKeyPairType) {
        return isECorDH(pKeyPairType);
    }

    /**
     * Does the keyPairSpec have an SM9 agreement?
     *
     * @param pKeyPairSpec the keyPairSpec
     * @return true/false
     */
    public static boolean hasSM9(final GordianKeyPairSpec pKeyPairSpec) {
        return pKeyPairSpec.getKeyPairType() == GordianKeyPairType.SM9
                && switch (pKeyPairSpec.getSubSpec()) {
            case GordianSM9EncryptType.ENCMASTER, GordianSM9EncryptType.EXCHANGE -> true;
            default -> false;
        };
    }

    /**
     * Is the keyPairType EC/DH?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    private static boolean isECorDH(final GordianKeyPairType pKeyPairType) {
        return switch (pKeyPairType) {
            case SM2, EC, GOST, DSTU, DH, XDH -> true;
            default -> false;
        };
    }

    /**
     * Is the keyPairType EC?
     *
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    private static boolean isEC(final GordianKeyPairType pKeyPairType) {
        return switch (pKeyPairType) {
            case SM2, EC, GOST, DSTU -> true;
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
        return pThat instanceof GordianCoreAgreementType myThat
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
    public static GordianCoreAgreementType mapCoreType(final Object pType) {
        return pType instanceof GordianAgreementType myType ? TYPEMAP.get(myType) : null;
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianAgreementType, GordianCoreAgreementType> newTypeMap() {
        final Map<GordianAgreementType, GordianCoreAgreementType> myMap = new EnumMap<>(GordianAgreementType.class);
        for (GordianAgreementType myType : GordianAgreementType.values()) {
            myMap.put(myType, new GordianCoreAgreementType(myType));
        }
        return myMap;
    }

    /**
     * Obtain the values.
     *
     * @return the values
     */
    public static GordianCoreAgreementType[] values() {
        return VALUES;
    }
}
