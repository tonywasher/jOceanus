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

package io.github.tonywasher.joceanus.gordianknot.api.cipher.spec;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;

/**
 * PBE Specification.
 */
public interface GordianPBESpec {
    /**
     * Obtain the PBEType.
     *
     * @return the PBEType
     */
    GordianPBEType getPBEType();

    /**
     * Is the Spec valid?
     *
     * @return true/false
     */
    boolean isValid();

    /**
     * DigestAndCountSpec.
     */
    interface GordianPBEDigestAndCountSpec
            extends GordianPBESpec {
        /**
         * Obtain the digestSpec.
         *
         * @return the digestSpec
         */
        GordianDigestSpec getDigestSpec();

        /**
         * Obtain the iteration count.
         *
         * @return the count
         */
        int getIterationCount();
    }

    /**
     * SCryptSpec.
     */
    interface GordianPBESCryptSpec
            extends GordianPBESpec {
        /**
         * Obtain the blockSize.
         *
         * @return the blockSize
         */
        int getBlockSize();

        /**
         * Obtain the cost.
         *
         * @return the cost
         */
        int getCost();

        /**
         * Obtain the parallelism.
         *
         * @return the parallelism
         */
        int getParallel();
    }

    /**
     * Argon2Spec.
     */
    interface GordianPBEArgon2Spec
            extends GordianPBESpec {
        /**
         * Obtain the lanes.
         *
         * @return the lanes
         */
        int getLanes();

        /**
         * Obtain the memory.
         *
         * @return the memory
         */
        int getMemory();

        /**
         * Obtain the iteration count.
         *
         * @return the count
         */
        int getIterationCount();
    }
}
