/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
    ECB(true),

    /**
     * CBC Mode.
     */
    CBC(true),

    /**
     * SIC(CTR) Mode.
     */
    SIC(false),

    /**
     * CFB Mode.
     */
    CFB(false),

    /**
     * OFB Mode.
     */
    OFB(false),

    /**
     * EAX Mode.
     */
    EAX(false, true),

    /**
     * CCM Mode.
     */
    CCM(false, true),

    /**
     * GCM Mode.
     */
    GCM(false, true),

    /**
     * OCB Mode.
     */
    OCB(false, true);

    /**
     * Allows padding.
     */
    private final boolean allowsPadding;

    /**
     * is AAD.
     */
    private final boolean isAAD;

    /**
     * Constructor.
     * @param pPadding is padding allowed?
     */
    GordianCipherMode(final boolean pPadding) {
        this(pPadding, false);
    }

    /**
     * Constructor.
     * @param pPadding is padding allowed?
     * @param pAAD is this an AAD mode?
     */
    GordianCipherMode(final boolean pPadding,
                      final boolean pAAD) {
        allowsPadding = pPadding;
        isAAD = pAAD;
    }

    /**
     * Does the mode allow padding?
     * @return true/false
     */
    public boolean allowsPadding() {
        return allowsPadding;
    }

    /**
     * Is this an AAD mode?
     * @return true/false
     */
    public boolean isAAD() {
        return isAAD;
    }

    /**
     * Does the mode need an IV?
     * @return true/false
     */
    public boolean needsIV() {
        return this != ECB;
    }
}
