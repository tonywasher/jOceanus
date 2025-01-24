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
package net.sourceforge.joceanus.gordianknot.api.keypair;

/**
 * Elliptic framework.
 */
public interface GordianElliptic {
    /**
     * Obtain the name of the curve.
     * @return the name
     */
    String getCurveName();

    /**
     * Obtain the bitSize of the curve.
     * @return the size
     */
    int getKeySize();

    /**
     * Can the curve encrypt?
     * @return true/false
     */
    default boolean canEncrypt() {
        return true;
    }

    /**
     * Does the curve have a custom implementation?
     * @return true/false
     */
    default boolean hasCustomCurve() {
        return false;
    }
}
