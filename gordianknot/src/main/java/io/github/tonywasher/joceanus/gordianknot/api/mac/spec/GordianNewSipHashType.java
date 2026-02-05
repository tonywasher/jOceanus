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

package io.github.tonywasher.joceanus.gordianknot.api.mac.spec;

/**
 * SipHashType.
 */
public enum GordianNewSipHashType {
    /**
     * SipHash-2-4.
     */
    SIPHASH_2_4,

    /**
     * SipHash128-2-4.
     */
    SIPHASH128_2_4,

    /**
     * SipHash-4-8.
     */
    SIPHASH_4_8,

    /**
     * SipHash128-4-8.
     */
    SIPHASH128_4_8;
}
