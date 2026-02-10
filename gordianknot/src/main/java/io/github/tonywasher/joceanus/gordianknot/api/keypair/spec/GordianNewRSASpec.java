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
 * Modulus Key lengths.
 */
public enum GordianNewRSASpec {
    /**
     * 2048.
     */
    MOD2048,

    /**
     * 1024.
     */
    MOD1024,

    /**
     * 1536.
     */
    MOD1536,

    /**
     * 3072.
     */
    MOD3072,

    /**
     * 4096.
     */
    MOD4096,

    /**
     * 6144.
     */
    MOD6144,

    /**
     * 8192.
     */
    MOD8192;
}
