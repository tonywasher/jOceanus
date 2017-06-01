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
    OCB;

    /**
     * Does the mode require padding?
     * @return true/false
     */
    public boolean hasPadding() {
        switch (this) {
            case ECB:
            case CBC:
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
                return true;
            default:
                return false;
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
