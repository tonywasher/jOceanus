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
 * Sign Hybrid Spec.
 */
public enum GordianHybridSignSpec {
    /**
     * MLDSA44_RSA2048_PSS_SHA256.
     */
    MLDSA44_RSA2048_PSS_SHA256,

    /**
     * MLDSA44_RSA2048_PKCS15_SHA256.
     */
    MLDSA44_RSA2048_PKCS15_SHA256,

    /**
     * MLDSA44_Ed25519_SHA512.
     */
    MLDSA44_ED25519_SHA512,

    /**
     * MLDSA44_ECDSA_P256_SHA256.
     */
    MLDSA44_ECDSA_P256_SHA256,

    /**
     * MLDSA65_RSA3072_PSS_SHA512.
     */
    MLDSA65_RSA3072_PSS_SHA512,

    /**
     * MLDSA65_RSA3072_PKCS15_SHA512.
     */
    MLDSA65_RSA3072_PKCS15_SHA512,

    /**
     * MLDSA65_RSA4096_PSS_SHA512.
     */
    MLDSA65_RSA4096_PSS_SHA512,

    /**
     * MLDSA65_RSA4096_PKCS15_SHA512.
     */
    MLDSA65_RSA4096_PKCS15_SHA512,

    /**
     * MLDSA65_ECDSA_P256_SHA512.
     */
    MLDSA65_ECDSA_P256_SHA512,

    /**
     * MLDSA65_ECDSA_P384_SHA512.
     */
    MLDSA65_ECDSA_P384_SHA512,

    /**
     * MLDSA65_ECDSA_brainpoolP256r1_SHA512.
     */
    MLDSA65_ECDSA_BP256_SHA512,

    /**
     * MLDSA65_Ed25519_SHA512.
     */
    MLDSA65_ED25519_SHA512,

    /**
     * MLDSA87_ECDSA_P384_SHA512.
     */
    MLDSA87_ECDSA_P384_SHA512,

    /**
     * MLDSA87_ECDSA_brainpoolP384r1_SHA512.
     */
    MLDSA87_ECDSA_BP384_SHA512,

    /**
     * MLDSA87_Ed448_SHAKE256.
     */
    MLDSA87_ED448_SHAKE256,

    /**
     * MLDSA87_RSA3072_PSS_SHA512.
     */
    MLDSA87_RSA3072_PSS_SHA512,

    /**
     * MLDSA87_RSA4096_PSS_SHA512.
     */
    MLDSA87_RSA4096_PSS_SHA512,

    /**
     * MLDSA87_ECDSA_P521_SHA512.
     */
    MLDSA87_ECDSA_P521_SHA512;
}
