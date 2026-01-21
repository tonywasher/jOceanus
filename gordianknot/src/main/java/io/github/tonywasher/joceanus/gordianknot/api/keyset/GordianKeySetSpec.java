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
package io.github.tonywasher.joceanus.gordianknot.api.keyset;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;

import java.util.Objects;

/**
 * KeySet Specification.
 */
public class GordianKeySetSpec {
    /**
     * Minimum Cipher Steps.
     */
    public static final Integer MINIMUM_CIPHER_STEPS = 3;

    /**
     * Maximum Cipher Steps.
     */
    public static final Integer MAXIMUM_CIPHER_STEPS = 6;

    /**
     * Default Cipher Steps.
     */
    public static final Integer DEFAULT_CIPHER_STEPS = 4;

    /**
     * Default KeyLength.
     */
    public static final GordianLength DEFAULT_KEYLEN = GordianLength.LEN_256;

    /**
     * KeyLength.
     */
    private final GordianLength theKeyLength;

    /**
     * Number of CipherSteps.
     */
    private final int theCipherSteps;

    /**
     * Is the Spec valid?.
     */
    private final boolean isValid;

    /**
     * Constructor.
     */
    public GordianKeySetSpec() {
        this(DEFAULT_KEYLEN);
    }

    /**
     * Constructor.
     *
     * @param pKeyLen the keyLength.
     */
    public GordianKeySetSpec(final GordianLength pKeyLen) {
        this(pKeyLen, DEFAULT_CIPHER_STEPS);
    }

    /**
     * Constructor.
     *
     * @param pKeyLen   the keyLength.
     * @param pNumSteps the number of cipherSteps
     */
    public GordianKeySetSpec(final GordianLength pKeyLen,
                             final int pNumSteps) {
        theKeyLength = pKeyLen;
        theCipherSteps = pNumSteps;
        isValid = validateKeySetSpec();
    }

    /**
     * Is the Spec valid?
     *
     * @return true/false
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the keyLength.
     *
     * @return the keyLength
     */
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    /**
     * Obtain the number of Cipher Steps.
     *
     * @return the # of cipher steps
     */
    public int getCipherSteps() {
        return theCipherSteps;
    }

    /**
     * Validate the Parameters.
     *
     * @return valid true/false
     */
    private boolean validateKeySetSpec() {
        /* Check keyLength */
        if (theKeyLength == null
                || !GordianKeyLengths.isSupportedLength(theKeyLength)) {
            return false;
        }

        /* Check cipher steps is in range */
        return !(theCipherSteps < MINIMUM_CIPHER_STEPS
                || theCipherSteps > MAXIMUM_CIPHER_STEPS);
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

        /* Check Length and cipherSteps */
        return pThat instanceof GordianKeySetSpec myThat
                && theKeyLength == myThat.getKeyLength()
                && theCipherSteps == myThat.getCipherSteps();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyLength, theCipherSteps);
    }

    @Override
    public String toString() {
        return "KeySet" + theKeyLength + "-" + theCipherSteps;
    }
}
