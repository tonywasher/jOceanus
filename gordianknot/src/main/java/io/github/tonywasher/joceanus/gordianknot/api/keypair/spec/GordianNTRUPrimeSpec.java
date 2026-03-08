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

/**
 * NTRUPRIME KeySpec.
 */
public interface GordianNTRUPrimeSpec {
    /**
     * Obtain the type.
     *
     * @return the type
     */
    GordianNTRUPrimeType getType();

    /**
     * Obtain the params.
     *
     * @return the params
     */
    GordianNTRUPrimeParams getParams();

    /**
     * Is the keySpec valid?
     *
     * @return true/false.
     */
    boolean isValid();

    /**
     * NTRUPRIME Type.
     */
    enum GordianNTRUPrimeType {
        /**
         * NTRULPrime.
         */
        NTRUL,

        /**
         * SNTRUPrime.
         */
        SNTRU;
    }

    /**
     * NTRUPRIME Parameters.
     */
    enum GordianNTRUPrimeParams {
        /**
         * PR653.
         */
        PR653,

        /**
         * PR761.
         */
        PR761,

        /**
         * PR857.
         */
        PR857,

        /**
         * PR953.
         */
        PR953,

        /**
         * PR1013.
         */
        PR1013,

        /**
         * PR1277.
         */
        PR1277;
    }
}
