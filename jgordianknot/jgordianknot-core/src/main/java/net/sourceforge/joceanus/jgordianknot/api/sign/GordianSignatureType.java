/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;

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
     * PSS-MGF1.
     */
    PSSMGF1,

    /**
     * PSS-SHAKE128.
     */
    PSS128,

    /**
     * PSS-SHAKE256.
     */
    PSS256,

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
    PREHASH;

    /**
     * Is this Signature supported for this AsymKeyType?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public boolean isSupported(final GordianKeyPairType pKeyType) {
        switch (this) {
            case ISO9796D2:
            case PSSMGF1:
            case PSS128:
            case PSS256:
            case X931:
                return GordianKeyPairType.RSA == pKeyType;
            case NR:
                return GordianKeyPairType.EC == pKeyType;
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
     * Does the AsymKeyType have a PreHash signature?
     * @param pKeyType the asymKeyType
     * @return true/false
     */
    public static boolean hasPreHash(final GordianKeyPairType pKeyType) {
        switch (pKeyType) {
            case XMSS:
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
    public static boolean hasDSA(final GordianKeyPairType pKeyType) {
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
    public static boolean hasNative(final GordianKeyPairType pKeyType) {
        switch (pKeyType) {
            case SM2:
            case EDDSA:
            case DSTU4145:
            case GOST2012:
            case SPHINCSPLUS:
            case DILITHIUM:
            case FALCON:
            case PICNIC:
            case RAINBOW:
            case XMSS:
            case LMS:
            case COMPOSITE:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this signature type PSS?
     * @return true/false
     */
    public boolean isPSS() {
        switch (this) {
            case PSSMGF1:
            case PSS128:
            case PSS256:
                return true;
            default:
                return false;
        }
    }
}
