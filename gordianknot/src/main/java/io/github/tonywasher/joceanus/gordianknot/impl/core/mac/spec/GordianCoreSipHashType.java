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

package io.github.tonywasher.joceanus.gordianknot.impl.core.mac.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewSipHashType;

/**
 * SipHashSpec.
 */
public final class GordianCoreSipHashType {
    /**
     * Small compression.
     */
    private static final int SMALLCOMPRESSION = 2;

    /**
     * Large compression.
     */
    private static final int LARGECOMPRESSION = 4;

    /**
     * The type.
     */
    private final GordianNewSipHashType theType;

    /**
     * The output length.
     */
    private final GordianLength theOutLen;

    /**
     * The number of compression rounds.
     */
    private final int theCompression;

    /**
     * The name of the hash.
     */
    private String theName;

    /**
     * Constructor.
     *
     * @param pType the SipHashType
     */
    GordianCoreSipHashType(final GordianNewSipHashType pType) {
        theType = pType;
        theOutLen = determineOutLength();
        theCompression = determineCompression();
    }

    /**
     * Obtain the type.
     *
     * @return the type
     */
    public GordianNewSipHashType getType() {
        return theType;
    }

    /**
     * Obtain the output length.
     *
     * @return the length
     */
    public GordianLength getOutLength() {
        return theOutLen;
    }

    /**
     * Is this a long output?
     *
     * @return true/false
     */
    public boolean isLong() {
        return theOutLen == GordianLength.LEN_128;
    }

    /**
     * Obtain the number of compression rounds.
     *
     * @return the number of rounds
     */
    public int getCompression() {
        return theCompression;
    }

    /**
     * Obtain the number of finalization rounds.
     *
     * @return the number of rounds
     */
    public int getFinalisation() {
        return theCompression << 1;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Build the name */
            theName = "SipHash";
            if (GordianLength.LEN_128 == theOutLen) {
                theName += theOutLen;
            }
            theName += GordianCoreMacSpec.SEP + getCompression()
                    + GordianCoreMacSpec.SEP + getFinalisation();
        }

        /* return the name */
        return theName;
    }


    /**
     * Determine outputLength.
     *
     * @return the outputLength
     */
    private GordianLength determineOutLength() {
        switch (theType) {
            case SIPHASH_2_4:
            case SIPHASH_4_8:
                return GordianLength.LEN_64;
            case SIPHASH128_2_4:
            case SIPHASH128_4_8:
            default:
                return GordianLength.LEN_128;
        }
    }

    /**
     * Determine compression.
     *
     * @return the compression
     */
    private int determineCompression() {
        switch (theType) {
            case SIPHASH_2_4:
            case SIPHASH128_2_4:
                return SMALLCOMPRESSION;
            case SIPHASH_4_8:
            case SIPHASH128_4_8:
            default:
                return LARGECOMPRESSION;
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check subFields */
        return pThat instanceof GordianCoreSipHashType myThat
                && theType == myThat.getType();
    }

    @Override
    public int hashCode() {
        return theType.hashCode();
    }
}
