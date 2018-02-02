/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Cipher Modes. Available algorithms.
 */
public enum GordianCipherMode {
    /**
     * ECB Mode.
     */
    ECB,

    /**
     * CBC Mode.
     */
    CBC,

    /**
     * SIC(CTR) Mode.
     */
    SIC,

    /**
     * CFB Mode.
     */
    CFB,

    /**
     * OFB Mode.
     */
    OFB,

    /**
     * EAX Mode.
     */
    EAX,

    /**
     * CCM Mode.
     */
    CCM,

    /**
     * GCM Mode.
     */
    GCM,

    /**
     * OCB Mode.
     */
    OCB,

    /**
     * GCFB Mode.
     */
    GCFB,

    /**
     * GOFB Mode.
     */
    GOFB,

    /**
     * KCTR Mode.
     */
    KCTR,

    /**
     * KCCM Mode.
     */
    KCCM,

    /**
     * KGCM Mode.
     */
    KGCM,

    /**
     * G3413CBC Mode.
     */
    G3413CBC,

    /**
     * G3413CFB Mode.
     */
    G3413CFB,

    /**
     * G3413OFB Mode.
     */
    G3413OFB,

    /**
     * G3413CTR Mode.
     */
    G3413CTR;

    /**
     * Does the mode require padding?
     * @return true/false
     */
    public boolean hasPadding() {
        switch (this) {
            case ECB:
            case CBC:
            case G3413CBC:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this an AAD mode?
     * @return true/false
     */
    public boolean isAAD() {
        switch (this) {
            case CCM:
            case GCM:
            case EAX:
            case OCB:
            case KCCM:
            case KGCM:
                return true;
            default:
                return false;
        }
    }

    /**
     * Can we work on a short block?
     * @return true/false
     */
    public boolean allowShortBlock() {
        switch (this) {
            case CCM:
            case GCM:
            case OCB:
            case SIC:
                return false;
            default:
                return true;
        }
    }

    /**
     * Does the mode require a standard block?
     * @return true/false
     */
    public boolean needsStdBlock() {
        switch (this) {
            case CCM:
            case GCM:
            case OCB:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this mode valid for the symKeyType?
     * @param pKeyType the keyType
     * @return true/false
     */
    public boolean validForSymKey(final GordianSymKeyType pKeyType) {
        switch (this) {
            case G3413OFB:
            case G3413CFB:
            case G3413CBC:
            case G3413CTR:
                return GordianSymKeyType.KUZNYECHIK.equals(pKeyType);
            case GOFB:
            case GCFB:
                return GordianSymKeyType.GOST.equals(pKeyType);
            case KCTR:
            case KGCM:
            case KCCM:
                return GordianSymKeyType.KALYNA.equals(pKeyType);
            default:
                return true;
        }
    }

    /**
     * Does the mode need an IV?
     * @return true/false
     */
    public boolean needsIV() {
        return this != ECB;
    }
}
