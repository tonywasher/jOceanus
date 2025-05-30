/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;

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
     * CFB8 Mode.
     */
    CFB8,

    /**
     * OFB Mode.
     */
    OFB,

    /**
     * OFB8 Mode.
     */
    OFB8,

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
    G3413CTR,

    /**
     * GCMSIV Mode.
     */
    GCMSIV;

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
            case GCMSIV:
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
            case GCMSIV:
            case OCB:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this mode valid for the symKeySpec?
     * @param pKeySpec the keySpec
     * @return true/false
     */
    public boolean validForSymKey(final GordianSymKeySpec pKeySpec) {
        final GordianSymKeyType myKeyType = pKeySpec.getSymKeyType();
        final GordianLength myKeyLen = pKeySpec.getKeyLength();
        switch (this) {
            case G3413OFB:
            case G3413CFB:
            case G3413CBC:
            case G3413CTR:
                return GordianSymKeyType.KUZNYECHIK.equals(myKeyType);
            case GOFB:
            case GCFB:
                return GordianSymKeyType.GOST.equals(myKeyType);
            case KCTR:
            case KGCM:
            case KCCM:
                return GordianSymKeyType.KALYNA.equals(myKeyType);
            case GCMSIV:
                return GordianLength.LEN_128.equals(myKeyLen)
                         || GordianLength.LEN_256.equals(myKeyLen);
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

    /**
     * Needs re-initialisation after final.
     * @return true/false
     */
    public boolean needsReInitialisation() {
        return this == GCM;
    }
}
