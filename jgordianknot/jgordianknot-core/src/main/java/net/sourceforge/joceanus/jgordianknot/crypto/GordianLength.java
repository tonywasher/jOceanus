/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Explicit lengths for digests.
 */
public enum GordianLength {
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
     * 224 bits.
     */
    LEN_224(224),

    /**
     * 256 bits.
     */
    LEN_256(256),

    /**
     * 320 bits.
     */
    LEN_320(320),

    /**
     * 384 bits.
     */
    LEN_384(384),

    /**
     * 512 bits.
     */
    LEN_512(512),

    /**
     * 1024 bits.
     */
    LEN_1024(1024);

    /**
     * The length.
     */
    private final int theLength;

    /**
     * Constructor.
     * @param pLength the digest length.
     */
    GordianLength(final int pLength) {
        theLength = pLength;
    }

    /**
     * Obtain the length (in bits).
     * @return the length
     */
    public int getLength() {
        return theLength;
    }

    /**
     * Obtain the length (in bytes).
     * @return the length
     */
    public int getByteLength() {
        return theLength / Byte.SIZE;
    }

    /**
     * Obtain the standard skeinState length.
     * @return the length (null if not supported)
     */
    public GordianLength getSkeinState() {
        switch (this) {
            case LEN_1024:
                return LEN_1024;
            case LEN_384:
            case LEN_512:
                return LEN_512;
            case LEN_128:
            case LEN_160:
            case LEN_224:
            case LEN_256:
                return LEN_256;
            default:
                return null;
        }
    }

    /**
     * Obtain the extended skeinState length.
     * @return the length (null if not supported)
     */
    public GordianLength getSkeinExtendedState() {
        switch (this) {
            case LEN_384:
            case LEN_512:
            case LEN_1024:
                return LEN_1024;
            case LEN_128:
            case LEN_160:
            case LEN_224:
            case LEN_256:
                return LEN_512;
            default:
                return null;
        }
    }

    /**
     * Obtain the standard shakeState length.
     * @return the length (null if not supported)
     */
    public GordianLength getSHAKEState() {
        switch (this) {
            case LEN_256:
            case LEN_384:
            case LEN_512:
                return LEN_256;
            case LEN_128:
            case LEN_160:
            case LEN_224:
                return LEN_128;
            default:
                return null;
        }
    }

    /**
     * Obtain the extended sha2State length.
     * @return the length
     */
    public GordianLength getSha2ExtendedState() {
        switch (this) {
            case LEN_224:
            case LEN_256:
                return LEN_512;
            default:
                return null;
        }
    }
}
