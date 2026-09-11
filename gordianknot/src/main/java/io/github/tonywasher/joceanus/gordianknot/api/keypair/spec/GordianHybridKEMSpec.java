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
 * KEM Hybrid Spec.
 */
public enum GordianHybridKEMSpec {
    /**
     * MLKEM768_RSA2048.
     */
    MLKEM768_RSA2048,

    /**
     * MLKEM768_RSA3072.
     */
    MLKEM768_RSA3072,

    /**
     * MLKEM768_RSA4096.
     */
    MLKEM768_RSA4096,

    /**
     * MLKEM768_X25519.
     */
    MLKEM768_X25519,

    /**
     * MLKEM768_ECDH_P256.
     */
    MLKEM768_ECDH_P256,

    /**
     * MLKEM768_ECDH_P384.
     */
    MLKEM768_ECDH_P384,

    /**
     * MLKEM768_ECDH_BP256.
     */
    MLKEM768_ECDH_BP256,

    /**
     * MLKEM1024_RSA3072.
     */
    MLKEM1024_RSA3072,

    /**
     * MLKEM1024_ECDH_P384.
     */
    MLKEM1024_ECDH_P384,

    /**
     * MLKEM1024_ECDH_BP384.
     */
    MLKEM1024_ECDH_BP384,

    /**
     * MLKEM1024_ECDH_P521.
     */
    MLKEM1024_ECDH_P521,

    /**
     * MLKEM1024_X448.
     */
    MLKEM1024_X448;
}
