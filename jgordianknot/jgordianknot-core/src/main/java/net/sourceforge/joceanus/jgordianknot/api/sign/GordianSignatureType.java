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
package net.sourceforge.joceanus.jgordianknot.api.sign;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;

/**
 * Signature Type.
 */
public enum GordianSignatureType {
    /**
     * Native.
     */
    NATIVE,

    /**
     * DSA.
     */
    DSA,

    /**
     * DetDSA.
     */
    DDSA,

    /**
     * NR.
     */
    NR,

    /**
     * PSS.
     */
    PSS,

    /**
     * X9.31.
     */
    X931,

    /**
     * ISO9796d2.
     */
    ISO9796D2,

    /**
     * PreHash.
     */
    PREHASH,

    /**
     * Pure.
     */
    PURE;

    /**
     * Is this Signature supported for this AsymKeyType?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public boolean isSupported(final GordianAsymKeyType pKeyType) {
        switch (this) {
            case ISO9796D2:
            case PSS:
            case X931:
                return GordianAsymKeyType.RSA == pKeyType;
            case NR:
                return GordianAsymKeyType.EC == pKeyType;
            case PURE:
                return hasPure(pKeyType);
            case PREHASH:
                return hasPreHash(pKeyType);
            case NATIVE:
                return hasNative(pKeyType);
            case DSA:
            case DDSA:
                return hasDSA(pKeyType);
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a Pure signature?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasPure(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
            case ED25519:
            case ED448:
            case XMSS:
            case XMSSMT:
            case QTESLA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a PreHash signature?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasPreHash(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
            case ED25519:
            case ED448:
            case XMSS:
            case XMSSMT:
            case SPHINCS:
            case RSA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a DSA signature?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasDSA(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
            case EC:
            case DSA:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does the AsymKeyType have a Native signature?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasNative(final GordianAsymKeyType pKeyType) {
        switch (pKeyType) {
            case SM2:
            case ED25519:
            case DSTU4145:
            case GOST2012:
            case RAINBOW:
                return true;
            default:
                return false;
        }
    }
}
