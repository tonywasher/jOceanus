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
 * StreamKey SubSpec.
 */
public interface GordianStreamKeySubType {
    /**
     * VMPC Key styles.
     */
    enum GordianVMPCKey
            implements GordianStreamKeySubType {
        /**
         * VMPC.
         */
        STD,

        /**
         * VMPC-KSA.
         */
        KSA;
    }

    /**
     * Salsa20 Key styles.
     */
    enum GordianSalsa20Key
            implements GordianStreamKeySubType {
        /**
         * Salsa20.
         */
        STD,

        /**
         * XSalsa20.
         */
        XSALSA;
    }

    /**
     * ChaCha20 Key styles.
     */
    enum GordianChaCha20Key
            implements GordianStreamKeySubType {
        /**
         * ChaCha20.
         */
        STD,

        /**
         * ChaCha7539.
         */
        ISO7539,

        /**
         * XChaCha20.
         */
        XCHACHA;
    }

    /**
     * SkeinXof Key styles.
     */
    enum GordianSkeinXofKey
            implements GordianStreamKeySubType {
        /**
         * 256State.
         */
        STATE256,

        /**
         * 512State.
         */
        STATE512,

        /**
         * 1024State.
         */
        STATE1024;
    }

    /**
     * BlakeXof Key styles.
     */
    enum GordianBlakeXofKey
            implements GordianStreamKeySubType {
        /**
         * Blake2S.
         */
        BLAKE2XS,

        /**
         * Blake2B.
         */
        BLAKE2XB;
    }

    /**
     * Elephant Key styles.
     */
    enum GordianElephantKey
            implements GordianStreamKeySubType {
        /**
         * Elephant160.
         */
        ELEPHANT160,

        /**
         * Elephant176.
         */
        ELEPHANT176,

        /**
         * Elephant200.
         */
        ELEPHANT200;
    }

    /**
     * ISAP Key styles.
     */
    enum GordianISAPKey
            implements GordianStreamKeySubType {
        /**
         * ISAPA128.
         */
        ISAPA128,

        /**
         * ISAPA128A.
         */
        ISAPA128A,

        /**
         * ISAPK128.
         */
        ISAPK128,

        /**
         * ISAPK128A.
         */
        ISAPK128A;
    }

    /**
     * Romulus Key styles.
     */
    enum GordianRomulusKey
            implements GordianStreamKeySubType {
        /**
         * Romulus-M.
         */
        ROMULUS_M,

        /**
         * Romulus-N.
         */
        ROMULUS_N,

        /**
         * Romulus-T.
         */
        ROMULUS_T;
    }

    /**
     * Sparkle Key styles.
     */
    enum GordianSparkleKey
            implements GordianStreamKeySubType {
        /**
         * Sparkle128_128.
         */
        SPARKLE128_128,

        /**
         * Sparkle256_128.
         */
        SPARKLE256_128,

        /**
         * Sparkle192_192.
         */
        SPARKLE192_192,

        /**
         * Sparkle256_256.
         */
        SPARKLE256_256;
    }
}
