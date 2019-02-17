/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
    SHAKE(GordianLength.LEN_256, GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_384, GordianLength.LEN_512),

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
     * Blake2B.
     */
    BLAKE(GordianLength.LEN_512, GordianLength.LEN_128, GordianLength.LEN_160, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384),

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
    GROESTL(GordianLength.LEN_512, GordianLength.LEN_224, GordianLength.LEN_256, GordianLength.LEN_384);

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
                        || pStateLength.equals(getAlternateSha2StateForLength(pLength));
            case SHAKE:
                return pStateLength != null
                        && (pStateLength.equals(getSHAKEStateForLength(pLength))
                        || pStateLength.equals(getAlternateSHAKEStateForLength(pLength)));
            case SKEIN:
                return pStateLength != null
                        && (pStateLength.equals(getSkeinStateForLength(pLength))
                        || pStateLength.equals(getAlternateSkeinStateForLength(pLength)));
            case BLAKE:
                return pStateLength != null
                        && (pStateLength.equals(getBLAKEStateForLength(pLength))
                        || pStateLength.equals(getAlternateBLAKEStateForLength(pLength)));
            default:
                return pStateLength == null;
        }
    }

    /**
     * Does this digest have a state for this length?
     * @param pLength the length
     * @return true/false
     */
    public GordianLength getStateForLength(final GordianLength pLength) {
        switch (this) {
            case SKEIN:
                return getSkeinStateForLength(pLength);
            case SHAKE:
                return getSHAKEStateForLength(pLength);
            case BLAKE:
                return getBLAKEStateForLength(pLength);
            default:
                return null;
        }
    }

    /**
     * Obtain the standard skeinState length.
     * @param pLength the length
     * @return the length (null if not supported)
     */
    private static GordianLength getSkeinStateForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_1024:
                return GordianLength.LEN_1024;
            case LEN_384:
            case LEN_512:
                return GordianLength.LEN_512;
            case LEN_128:
            case LEN_160:
            case LEN_224:
            case LEN_256:
                return GordianLength.LEN_256;
            default:
                return null;
        }
    }

    /**
     * Obtain the standard shakeState length.
     * @param pLength the length
     * @return the length (null if not supported)
     */
    private static GordianLength getSHAKEStateForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_256:
            case LEN_384:
            case LEN_512:
                return GordianLength.LEN_256;
            case LEN_128:
            case LEN_160:
            case LEN_224:
                return GordianLength.LEN_128;
            default:
                return null;
        }
    }

    /**
     * Obtain the standard blakeState length.
     * @param pLength the length
     * @return the length (null if not supported)
     */
    private static GordianLength getBLAKEStateForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_160:
            case LEN_256:
            case LEN_384:
            case LEN_512:
                return GordianLength.LEN_512;
            case LEN_128:
            case LEN_224:
                return GordianLength.LEN_256;
            default:
                return null;
        }
    }

    /**
     * Obtain the alternate stateLength for this length?
     * @param pLength the length
     * @return the length (null if not supported)
     */
    public GordianLength getAlternateStateForLength(final GordianLength pLength) {
        switch (this) {
            case SHA2:
                return getAlternateSha2StateForLength(pLength);
            case SHAKE:
                return getAlternateSHAKEStateForLength(pLength);
            case SKEIN:
                return getAlternateSkeinStateForLength(pLength);
            case BLAKE:
                return getAlternateBLAKEStateForLength(pLength);
            default:
                return null;
        }
    }

    /**
     * Obtain the alternate skeinState length.
     * @param pLength the length
     * @return the length (null if not supported)
     */
    private static GordianLength getAlternateSkeinStateForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_384:
            case LEN_512:
                return GordianLength.LEN_1024;
            case LEN_128:
            case LEN_160:
            case LEN_224:
            case LEN_256:
                return GordianLength.LEN_512;
            default:
                return null;
        }
    }

    /**
     * Obtain the alternate shakeState length.
     * @param pLength the length
     * @return the length (null if not supported)
     */
    private static GordianLength getAlternateSHAKEStateForLength(final GordianLength pLength) {
        return GordianLength.LEN_256.equals(pLength)
               ?  GordianLength.LEN_128
               : null;
    }

    /**
     * Obtain the alternate blakeState length.
     * @param pLength the length
     * @return the length (null if not supported)
     */
    private static GordianLength getAlternateBLAKEStateForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_160:
            case LEN_256:
                return GordianLength.LEN_256;
            default:
                return null;
        }
    }

    /**
     * Obtain the extended sha2State length.
     * @param pLength the length
     * @return the length
     */
    private static GordianLength getAlternateSha2StateForLength(final GordianLength pLength) {
        switch (pLength) {
            case LEN_224:
            case LEN_256:
                return GordianLength.LEN_512;
            default:
                return null;
        }
    }

    /**
     * Is this the Blake2b algorithm?
     * @param pLength the length
     * @return true/false
     */
    public static boolean isBlake2bState(final GordianLength pLength) {
        return GordianLength.LEN_512.equals(pLength);
    }

    /**
     * Obtain the blakeAlgorithm name for State.
     * @param pLength the length
     * @return the length
     */
    public static String getBlakeAlgorithmForStateLength(final GordianLength pLength) {
        return BLAKE.toString() + "2" + (isBlake2bState(pLength)
                                         ? "b"
                                         : "s");
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
