/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot;

/**
 * Cipher Modes. Available algorithms.
 */
public enum CipherMode {
    /**
     * CBC Mode.
     */
    CBC,

    /**
     * OFB Mode.
     */
    OFB,

    /**
     * CFB Mode.
     */
    CFB,

    /**
     * SIC(CTR) Mode.
     */
    SIC,

    /**
     * GCM Mode.
     */
    GCM,

    /**
     * EAX Mode.
     */
    EAX;

    /**
     * Obtain cipher mode.
     * @return the cipher mode
     */
    public String getCipherMode() {
        switch (this) {
            case CFB:
            case OFB:
                return name()
                       + "8";
            default:
                return name();
        }
    }
}
