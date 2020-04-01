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
package net.sourceforge.joceanus.jgordianknot.api.asym;

/**
 * Edwards Elliptic Curves.
 */
public enum GordianEdwardsElliptic {
    /**
     * Curve25519.
     */
    CURVE25519,

    /**
     * Curve448.
     */
    CURVE448;

    /**
     * Is this curve25519?.
     * @return true/false
     */
    public boolean is25519() {
        return this == CURVE25519;
    }

    /**
     * Obtain suffix.
     * @return the suffix
     */
    public String getSuffix() {
        return this == CURVE25519 ? "25519" : "448";
    }
}
