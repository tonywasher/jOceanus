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

package io.github.tonywasher.joceanus.gordianknot.api.keypair.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;

/**
 * LMS KeyTypes.
 */
public interface GordianLMSSpec {
    /**
     * Obtain the hash.
     *
     * @return the hash
     */
    GordianLMSHash getHash();

    /**
     * Obtain the width.
     *
     * @return the width
     */
    GordianLMSHeight getHeight();

    /**
     * Obtain the width.
     *
     * @return the width
     */
    GordianLMSWidth getWidth();

    /**
     * Obtain the length.
     *
     * @return the length
     */
    GordianLength getLength();

    /**
     * Obtain the treeDepth.
     *
     * @return the treeDepth.
     */
    int getTreeDepth();

    /**
     * Is the keySpec valid?
     *
     * @return true/false.
     */
    boolean isValid();

    /**
     * LMS hash.
     */
    enum GordianLMSHash {
        /**
         * Sha256.
         */
        SHA256,

        /**
         * Shake256.
         */
        SHAKE256;
    }

    /**
     * LMS height.
     */
    enum GordianLMSHeight {
        /**
         * H5.
         */
        H5,

        /**
         * H10.
         */
        H10,

        /**
         * H15.
         */
        H15,

        /**
         * H20.
         */
        H20,

        /**
         * H25.
         */
        H25;
    }

    /**
     * LMS Width.
     */
    enum GordianLMSWidth {
        /**
         * W1.
         */
        W1,

        /**
         * W2.
         */
        W2,

        /**
         * W4.
         */
        W4,

        /**
         * W8.
         */
        W8;
    }
}
