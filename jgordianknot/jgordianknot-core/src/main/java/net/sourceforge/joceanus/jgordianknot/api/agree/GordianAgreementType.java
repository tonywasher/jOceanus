/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;

/**
 * Signature Type.
 */
public enum GordianAgreementType {
    /**
     * KEM.
     */
    KEM,

    /**
     * Basic.
     */
    BASIC,

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
     * Is this Agreement supported for this AsymKeyType?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public boolean isSupported(final GordianAsymKeyType pKeyType) {
        switch (this) {
            case KEM:
                return hasKEM(pKeyType);
            case BASIC:
                return hasBasic(pKeyType);
            case SM2:
                return hasSM2(pKeyType);
            case MQV:
                return hasMQV(pKeyType);
            case UNIFIED:
                return hasUnified(pKeyType);
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have an KEM agreement?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasKEM(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
            case RSA:
            case NEWHOPE:
                return true;
            case X25519:
            case X448:
                return false;
            default:
                return isECorDH(pKeyType);
        }
    }

    /**
     * Does the AsymKeyType have an SM2 agreement?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasSM2(final GordianAsymKeyType pKeyType) {
        return pKeyType == GordianAsymKeyType.SM2;
    }

    /**
     * Does the AsymKeyType have a Basic agreement?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasBasic(final GordianAsymKeyType pKeyType) {
        return isECorDH(pKeyType);
    }

    /**
     * Does the AsymKeyType have a MQV agreement?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasMQV(final GordianAsymKeyType pKeyType) {
        return  pKeyType == GordianAsymKeyType.DIFFIEHELLMAN || isEC(pKeyType);
    }

    /**
     * Does the AsymKeyType have a Unified agreement?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasUnified(final GordianAsymKeyType pKeyType) {
        return isECorDH(pKeyType);
    }

    /**
     * Is the AsymKeyType EC/DH?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    private static boolean isECorDH(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
            case SM2:
            case EC:
            case GOST2012:
            case DSTU4145:
            case DIFFIEHELLMAN:
            case X25519:
            case X448:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the AsymKeyType EC?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    private static boolean isEC(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
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