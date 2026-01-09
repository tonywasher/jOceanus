/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.mac;

import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;

/**
 * SipHashSpec.
 */
public enum GordianSipHashSpec {
    /**
     * SipHash-2-4.
     */
    SIPHASH_2_4(GordianLength.LEN_64, 2),

    /**
     * SipHash128-2-4.
     */
    SIPHASH128_2_4(GordianLength.LEN_64, 4),

    /**
     * SipHash-4-8.
     */
    SIPHASH_4_8(GordianLength.LEN_128, 2),

    /**
     * SipHash128-4-8.
     */
    SIPHASH128_4_8(GordianLength.LEN_128, 4);

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
     * @param pOutLen the output length
     * @param pCompression the number of compression rounds
     */
    GordianSipHashSpec(final GordianLength pOutLen,
                       final int pCompression) {
        theOutLen = pOutLen;
        theCompression = pCompression;
    }

    /**
     * Obtain the output length.
     * @return the length
     */
    public GordianLength getOutLength() {
        return theOutLen;
    }

    /**
     * Is this a long output?
     * @return true/false
     */
    public boolean isLong() {
        return theOutLen == GordianLength.LEN_128;
    }

    /**
     * Obtain the number of compression rounds.
     * @return the number of rounds
     */
    public int getCompression() {
        return theCompression;
    }

    /**
     * Obtain the number of finalization rounds.
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
            theName += GordianMacSpec.SEP + getCompression()
                        + GordianMacSpec.SEP + getFinalisation();
        }

        /* return the name */
        return theName;
    }
}
