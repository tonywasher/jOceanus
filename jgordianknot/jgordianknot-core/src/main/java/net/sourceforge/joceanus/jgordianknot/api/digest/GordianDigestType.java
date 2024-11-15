/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.api.digest;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;

/**
 * DataDigest types. Available algorithms.
 */
public enum GordianDigestType {
    /**
     * SHA2.
     */
    SHA2(GordianLength.LEN_512, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * Tiger.
     */
    TIGER(GordianLength.LEN_192),

    /**
     * WhirlPool.
     */
    WHIRLPOOL(GordianLength.LEN_512),

    /**
     * RIPEMD.
     */
    RIPEMD(GordianLength.LEN_320, GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_256),

    /**
     * GOST2012.
     */
    STREEBOG(GordianLength.LEN_512, GordianLength.LEN_256),

    /**
     * GOST.
     */
    GOST(GordianLength.LEN_256),

    /**
     * SHA3.
     */
    SHA3(GordianLength.LEN_512, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * SHAKE.
     */
    SHAKE(GordianLength.LEN_256, GordianLength.LEN_512),

    /**
     * Skein.
     */
    SKEIN(GordianLength.LEN_512, GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_1024),

    /**
     * Kupyna.
     */
    KUPYNA(GordianLength.LEN_512, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * SM3.
     */
    SM3(GordianLength.LEN_256),

    /**
     * Blake2.
     */
    BLAKE2(GordianLength.LEN_512, GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * SHA1.
     */
    SHA1(GordianLength.LEN_160),

    /**
     * MD5.
     */
    MD5(GordianLength.LEN_128),

    /**
     * MD4.
     */
    MD4(GordianLength.LEN_128),

    /**
     * MD2.
     */
    MD2(GordianLength.LEN_128),

    /**
     * JH.
     */
    JH(GordianLength.LEN_512, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * GROESTL.
     */
    GROESTL(GordianLength.LEN_512, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * CubeHash.
     */
    CUBEHASH(GordianLength.LEN_512, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * Kangaroo.
     */
    KANGAROO(GordianLength.LEN_256, GordianLength.LEN_512),

    /**
     * Haraka.
     */
    HARAKA(GordianLength.LEN_256),

    /**
     * Blake3.
     */
    BLAKE3(GordianLength.LEN_256, GordianLength.LEN_512);

    /**
     * The Supported lengths.
     */
    private final GordianLength[] theLengths;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pLengths the supported lengths
     */
    GordianDigestType(final GordianLength... pLengths) {
        theLengths = pLengths;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = GordianDigestResource.getKeyForDigest(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain default length.
     * @return the default length
     */
    public GordianLength getDefaultLength() {
        return theLengths[0];
    }

    /**
     * Obtain supported lengths.
     * @return the supported lengths (first is default)
     */
    public GordianLength[] getSupportedLengths() {
        return theLengths;
    }

    /**
     * is length valid?
     * @param pLength the length
     * @return true/false
     */
    public boolean isLengthValid(final GordianLength pLength) {
        for (final GordianLength myLength : theLengths) {
            if (myLength.equals(pLength)) {
                return true;
            }
        }
        return false;
    }

    /**
     * does this digest support large amounts of data?
     * @return true/false
     */
    public boolean supportsLargeData() {
        return this != HARAKA;
    }

    /**
     * is this available as an external hashDigest?
     * @return true/false
     */
    public boolean isExternalHashDigest() {
        return isLengthValid(GordianLength.LEN_512);
    }

    /**
     * is this available as a combined hashDigest?
     * @return true/false
     */
    public boolean isCombinedHashDigest() {
        return getDefaultLength().getLength() >= GordianLength.LEN_256.getLength()
                && supportsLargeData();
    }
}
