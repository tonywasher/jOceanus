/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.api.agree;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;

/**
 * Signature Type.
 */
public enum GordianAgreementType {
    /**
     * KEM.
     */
    KEM,

    /**
     * Anonymous.
     */
    ANON,

    /**
     * Basic.
     */
    BASIC,

    /**
     * Signed.
     */
    SIGNED,

    /**
     * SM2.
     */
    SM2,

    /**
     * MQV.
     */
    MQV,

    /**
     * Unified.
     */
    UNIFIED;

    /**
     * Is this an anonymous agreement?
     * @return true/false
     */
    public boolean isAnonymous() {
        switch (this) {
            case KEM:
            case ANON:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this a signed agreement?
     * @return true/false
     */
    public boolean isSigned() {
        return this == SIGNED;
    }

    /**
     * Is this an confirmable agreement?
     * @return true/false
     */
    public boolean canConfirm() {
        switch (this) {
            case UNIFIED:
            case MQV:
            case SM2:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this Agreement supported for this KeyPairType?
     * @param pKeyPairType the keyPair
     * @return true/false
     */
    public boolean isSupported(final GordianKeyPairType pKeyPairType) {
        if (pKeyPairType == GordianKeyPairType.COMPOSITE) {
            return true;
        }
        switch (this) {
            case KEM:
                return hasKEM(pKeyPairType);
            case ANON:
                return hasAnon(pKeyPairType);
            case BASIC:
            case SIGNED:
                return hasBasic(pKeyPairType);
            case SM2:
                return hasSM2(pKeyPairType);
            case MQV:
                return hasMQV(pKeyPairType);
            case UNIFIED:
                return hasUnified(pKeyPairType);
            default:
                return false;
        }
    }

    /**
     * Does the keyPairType have an KEM agreement?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasKEM(final GordianKeyPairType pKeyPairType) {
        switch (pKeyPairType) {
            case RSA:
            case EC:
            case GOST2012:
            case DSTU4145:
            case SM2:
            case CMCE:
            case FRODO:
            case SABER:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the keyPairType have an ANON agreement?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasAnon(final GordianKeyPairType pKeyPairType) {
        switch (pKeyPairType) {
            case NEWHOPE:
            case DH:
            case EC:
            case SM2:
            case GOST2012:
            case DSTU4145:
            case XDH:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the kKeyPairType have an SM2 agreement?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasSM2(final GordianKeyPairType pKeyPairType) {
        switch (pKeyPairType) {
            case EC:
            case SM2:
            case GOST2012:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the keyPairType have a Basic agreement?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasBasic(final GordianKeyPairType pKeyPairType) {
        return isECorDH(pKeyPairType);
    }

    /**
     * Does the keyPairType have a MQV agreement?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasMQV(final GordianKeyPairType pKeyPairType) {
        return  pKeyPairType == GordianKeyPairType.DH || isEC(pKeyPairType);
    }

    /**
     * Does the keyPairType have a Unified agreement?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    public static boolean hasUnified(final GordianKeyPairType pKeyPairType) {
        return isECorDH(pKeyPairType);
    }

    /**
     * Is the keyPairType EC/DH?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    private static boolean isECorDH(final GordianKeyPairType pKeyPairType) {
        switch (pKeyPairType) {
            case SM2:
            case EC:
            case GOST2012:
            case DSTU4145:
            case DH:
            case XDH:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the keyPairType EC?
     * @param pKeyPairType the keyPairType
     * @return true/false
     */
    private static boolean isEC(final GordianKeyPairType pKeyPairType) {
        switch (pKeyPairType) {
            case SM2:
            case EC:
            case GOST2012:
            case DSTU4145:
                return true;
            default:
                return false;
        }
    }
}
