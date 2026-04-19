/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.api.mac;

import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKey;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianMacSpec;

/**
 * Mac Parameters.
 */
public interface GordianMacParams {
    /**
     * Obtain the key.
     *
     * @return the key
     */
    GordianKey<GordianMacSpec> getKey();

    /**
     * Obtain the Nonce.
     *
     * @return the nonce
     */
    byte[] getNonce();

    /**
     * Is the nonce randomly generated?
     *
     * @return true/false
     */
    boolean randomNonce();

    /**
     * Obtain the Personalisation.
     *
     * @return the personalisation
     */
    byte[] getPersonal();

    /**
     * Obtain the Output length.
     *
     * @return the outLength
     */
    long getOutputLength();

    /**
     * Obtain the treeLeafLength.
     *
     * @return the leafLength
     */
    int getTreeLeafLen();

    /**
     * Obtain the treeFanOut.
     *
     * @return the fanOut
     */
    short getTreeFanOut();

    /**
     * Obtain the treeMaxDepth.
     *
     * @return the maxDepth
     */
    short getTreeMaxDepth();
}
