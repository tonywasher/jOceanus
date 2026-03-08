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
 * DH Groups.
 */
public enum GordianDHSpec {
    /**
     * ffdhe 2048.
     */
    FFDHE2048,

    /**
     * std 2048.
     */
    STD2048,

    /**
     * std 1024.
     */
    STD1024,

    /**
     * std 2048.
     */
    STD1536,

    /**
     * std 3072.
     */
    STD3072,

    /**
     * ffdhe 3072.
     */
    FFDHE3072,

    /**
     * std 4096.
     */
    STD4096,

    /**
     * ffdhe 4096.
     */
    FFDHE4096,

    /**
     * std 6144.
     */
    STD6144,

    /**
     * ffdhe 6144.
     */
    FFDHE6144,

    /**
     * std 8192.
     */
    STD8192,

    /**
     * ffdhe 8192.
     */
    FFDHE8192;
}
