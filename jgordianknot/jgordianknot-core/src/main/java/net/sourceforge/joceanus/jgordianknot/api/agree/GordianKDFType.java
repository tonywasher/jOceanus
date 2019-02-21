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
 * KDF types.
 */
public enum GordianKDFType {
    /**
     * None.
     */
    NONE,

    /**
     * SHA256 KDF.
     */
    SHA256KDF,

    /**
     * SHA512 KDF.
     */
    SHA512KDF,

    /**
     * SHA256 CKDF.
     */
    SHA256CKDF,

    /**
     * SHA512 CKDF.
     */
    SHA512CKDF;

    /**
     * Determine whether this is a supported kdfType.
     * @param pKeyType pKeyType
     * @param pAgreeType the agreement type
     * @return true/false
     */
    public boolean isSupported(final GordianAsymKeyType pKeyType,
                               final GordianAgreementType pAgreeType) {
        /* Switch on keyType */
        switch (pKeyType) {
            case RSA:
                return !isCKDF();
            case EC:
            case SM2:
            case DSTU4145:
            case GOST2012:
                return !isCKDF() || pAgreeType != GordianAgreementType.KEM;
            case DIFFIEHELLMAN:
                return isSupported4DH(pAgreeType);
            case X25519:
                return isSupported4X25519(pAgreeType);
            case X448:
                return isSupported4X448(pAgreeType);
            default:
                return true;
        }
    }

    /**
     * Determine whether this is a supported kdfType for RSA.
     * @param pAgreeType the agreement type
     * @return true/false
     */
    private boolean isSupported4DH(final GordianAgreementType pAgreeType) {
        /* Switch on keyType */
        switch (this) {
            case SHA256KDF:
            case SHA512KDF:
                return true;
            case SHA256CKDF:
            case SHA512CKDF:
                return pAgreeType == GordianAgreementType.UNIFIED || pAgreeType == GordianAgreementType.MQV;
            case NONE:
                return pAgreeType == GordianAgreementType.BASIC || pAgreeType == GordianAgreementType.KEM;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a supported kdfType for X25519.
     * @param pAgreeType the agreement type
     * @return true/false
     */
    private boolean isSupported4X25519(final GordianAgreementType pAgreeType) {
        /* Switch on keyType */
        switch (this) {
            case SHA256KDF:
                return true;
            case SHA256CKDF:
            case NONE:
                return pAgreeType == GordianAgreementType.BASIC;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a supported kdfType for X25519.
     * @param pAgreeType the agreement type
     * @return true/false
     */
    private boolean isSupported4X448(final GordianAgreementType pAgreeType) {
        /* Switch on keyType */
        switch (this) {
            case SHA512KDF:
                return true;
            case SHA512CKDF:
            case NONE:
                return pAgreeType == GordianAgreementType.BASIC;
            default:
                return false;
        }
    }

    /**
     * Determine whether this is a CKDF.
     * @return true/false
     */
    public boolean isCKDF() {
        return this == SHA256CKDF || this == SHA512CKDF;
    }
}