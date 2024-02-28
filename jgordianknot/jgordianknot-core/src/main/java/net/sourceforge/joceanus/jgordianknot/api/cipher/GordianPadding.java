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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

/**
 * Padding Modes. Available algorithms.
 */
public enum GordianPadding {
    /**
     * CTS Padding.
     */
    CTS,

    /**
     * ISO7816-4 Padding.
     */
    ISO7816D4,

    /**
     * X9.23 Padding.
     */
    X923,

    /**
     * PKCS7 Padding.
     */
    PKCS7,

    /**
     * TBC Padding.
     */
    TBC,

    /**
     * No Padding.
     */
    NONE;
}
