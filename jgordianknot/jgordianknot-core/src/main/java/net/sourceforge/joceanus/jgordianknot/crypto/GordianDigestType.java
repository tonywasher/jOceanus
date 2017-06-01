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
     * GOST.
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
     * Skein.
     */
    SKEIN(GordianLength.LEN_512, GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384, GordianLength.LEN_1024),

    /**
     * SM3.
     */
    SM3(GordianLength.LEN_256),

    /**
     * Blake2B.
     */
    BLAKE(GordianLength.LEN_512, GordianLength.LEN_160, GordianLength.LEN_256, GordianLength.LEN_384),

    /**
     * SHA1.
     */
    SHA1(GordianLength.LEN_160),

    /**
     * MD5.
     */
    MD5(GordianLength.LEN_128);

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
            theName = GordianCryptoResource.getKeyForDigest(this).getValue();
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
        for (GordianLength myLength : theLengths) {
            if (myLength.equals(pLength)) {
                return true;
            }
        }
        return false;
    }

    /**
     * is length available?
     * @param pStateLength the length
     * @param pLength the length
     * @return true/false
     */
    public boolean isStateValidForLength(final GordianLength pStateLength,
                                         final GordianLength pLength) {
        switch (this) {
            case SHA2:
                return pStateLength == null
                       || pStateLength.equals(pLength.getSha2ExtendedState());
            case SKEIN:
                return pStateLength != null
                       && (pStateLength.equals(pLength.getSkeinState())
                           || pStateLength.equals(pLength.getSkeinExtendedState()));
            default:
                return pStateLength == null;
        }
    }

    /**
     * Does this digest have an extended state for this length?
     * @param pLength the length
     * @return true/false
     */
    public GordianLength getStateForLength(final GordianLength pLength) {
        return GordianDigestType.SKEIN.equals(this)
                                                    ? pLength.getSkeinState()
                                                    : null;
    }

    /**
     * Does this digest have an extended state for this length?
     * @param pLength the length
     * @return true/false
     */
    public GordianLength getExtendedStateForLength(final GordianLength pLength) {
        switch (this) {
            case SHA2:
                return pLength.getSha2ExtendedState();
            case SKEIN:
                GordianLength myState = pLength.getSkeinExtendedState();
                return myState != pLength
                                          ? myState
                                          : null;
            default:
                return null;
        }
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
        return getDefaultLength().getLength() >= GordianLength.LEN_256.getLength();
    }
}
