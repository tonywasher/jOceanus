/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.api.base;

/**
 * Explicit lengths for digests.
 */
public enum GordianLength {
    /**
     * 32 bits.
     */
    LEN_32(32),

    /**
     * 64 bits.
     */
    LEN_64(64),

    /**
     * 96 bits.
     */
    LEN_96(96),

    /**
     * 128 bits.
     */
    LEN_128(128),

    /**
     * 160 bits.
     */
    LEN_160(160),

    /**
     * 192 bits.
     */
    LEN_192(192),

    /**
     * 200 bits.
     */
    LEN_200(200),

    /**
     * 224 bits.
     */
    LEN_224(224),

    /**
     * 256 bits.
     */
    LEN_256(256),

    /**
     * 283 bits.
     */
    LEN_283(283),

    /**
     * 306 bits.
     */
    LEN_306(306),

    /**
     * 320 bits.
     */
    LEN_320(320),

    /**
     * 366 bits.
     */
    LEN_366(366),

    /**
     * 384 bits.
     */
    LEN_384(384),

    /**
     * 409 bits.
     */
    LEN_409(409),

    /**
     * 431 bits.
     */
    LEN_431(431),

    /**
     * 512 bits.
     */
    LEN_512(512),

    /**
     * 521 bits.
     */
    LEN_521(521),

    /**
     * 571 bits.
     */
    LEN_571(571),

    /**
     * 1024 bits.
     */
    LEN_1024(1024),

    /**
     * 1536 bits.
     */
    LEN_1536(1536),

    /**
     * 2048 bits.
     */
    LEN_2048(2048),

    /**
     * 3072 bits.
     */
    LEN_3072(3072),

    /**
     * 4096 bits.
     */
    LEN_4096(4096),

    /**
     * 6144 bits.
     */
    LEN_6144(6144),

    /**
     * 8192 bits.
     */
    LEN_8192(8192);

    /**
     * The length.
     */
    private final int theLength;

    /**
     * Constructor.
     *
     * @param pLength the digest length.
     */
    GordianLength(final int pLength) {
        theLength = pLength;
    }

    /**
     * Obtain the length (in bits).
     *
     * @return the length
     */
    public int getLength() {
        return theLength;
    }

    /**
     * Obtain the length (in bytes).
     *
     * @return the length
     */
    public int getByteLength() {
        return theLength / Byte.SIZE;
    }

    @Override
    public String toString() {
        return Integer.toString(theLength);
    }
}
