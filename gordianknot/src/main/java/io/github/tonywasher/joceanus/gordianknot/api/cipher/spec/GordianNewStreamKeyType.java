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

/**
 * Stream Key Types. Available algorithms.
 */
public enum GordianNewStreamKeyType {
    /**
     * Salsa20.
     */
    SALSA20,

    /**
     * HC.
     */
    HC,

    /**
     * ChaCha20.
     */
    CHACHA20,

    /**
     * VMPC.
     */
    VMPC,

    /**
     * ISAAC.
     */
    ISAAC,

    /**
     * RC4.
     */
    RC4,

    /**
     * Grain.
     */
    GRAIN,

    /**
     * Sosemanuk.
     */
    SOSEMANUK,

    /**
     * Rabbit.
     */
    RABBIT,

    /**
     * Snow3G.
     */
    SNOW3G,

    /**
     * Zuc.
     */
    ZUC,

    /**
     * SkeinXof.
     */
    SKEINXOF,

    /**
     * Blake2Xof.
     */
    BLAKE2XOF,

    /**
     * Blake3Xof.
     */
    BLAKE3XOF,

    /**
     * Ascon.
     */
    ASCON,

    /**
     * Elephant.
     */
    ELEPHANT,

    /**
     * ISAP.
     */
    ISAP,

    /**
     * PhotonBeetle.
     */
    PHOTONBEETLE,

    /**
     * Romulus.
     */
    ROMULUS,

    /**
     * Sparkle.
     */
    SPARKLE,

    /**
     * Xoodyak.
     */
    XOODYAK;
}
