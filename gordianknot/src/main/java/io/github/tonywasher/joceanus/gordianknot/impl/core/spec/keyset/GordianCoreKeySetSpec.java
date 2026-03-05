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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keyset;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.key.GordianKeyLengths;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianNewKeySetSpec;

import java.util.Objects;

/**
 * KeySet Specification.
 */
public class GordianCoreKeySetSpec
        implements GordianNewKeySetSpec {
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
    GordianCoreKeySetSpec() {
        this(DEFAULT_KEYLEN);
    }

    /**
     * Constructor.
     *
     * @param pKeyLen the keyLength.
     */
    GordianCoreKeySetSpec(final GordianLength pKeyLen) {
        this(pKeyLen, DEFAULT_CIPHER_STEPS);
    }

    /**
     * Constructor.
     *
     * @param pKeyLen   the keyLength.
     * @param pNumSteps the number of cipherSteps
     */
    GordianCoreKeySetSpec(final GordianLength pKeyLen,
                          final int pNumSteps) {
        theKeyLength = pKeyLen;
        theCipherSteps = pNumSteps;
        isValid = validateKeySetSpec();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    @Override
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
        return pThat instanceof GordianCoreKeySetSpec myThat
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
