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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.lock;

import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianNewKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.api.lock.spec.GordianNewPasswordLockSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keyset.GordianCoreKeySetSpecBuilder;

import java.util.Objects;

/**
 * PasswordLock Specification.
 */
public class GordianCorePasswordLockSpec
        implements GordianNewPasswordLockSpec {
    /**
     * 1K Multiplier.
     */
    private static final int K_MULTIPLIER = 1024;

    /**
     * The Number of iterations (x 1K).
     */
    private final int theKIterations;

    /**
     * The KeySetSpec.
     */
    private final GordianNewKeySetSpec theKeySetSpec;

    /**
     * Is the Spec valid?.
     */
    private final boolean isValid;

    /**
     * Constructor.
     */
    GordianCorePasswordLockSpec() {
        this(DEFAULT_ITERATIONS);
    }

    /**
     * Constructor.
     *
     * @param pKIterations the iterations (x 1K).
     */
    GordianCorePasswordLockSpec(final int pKIterations) {
        this(pKIterations, GordianCoreKeySetSpecBuilder.newInstance().keySet());
    }

    /**
     * Constructor.
     *
     * @param pKeySetSpec the keySetSpec.
     */
    GordianCorePasswordLockSpec(final GordianNewKeySetSpec pKeySetSpec) {
        this(DEFAULT_ITERATIONS, pKeySetSpec);
    }

    /**
     * Constructor.
     *
     * @param pKIterations the iterations (x 1K).
     * @param pKeySetSpec  the keySetSpec
     */
    GordianCorePasswordLockSpec(final int pKIterations,
                                final GordianNewKeySetSpec pKeySetSpec) {
        theKIterations = pKIterations;
        theKeySetSpec = pKeySetSpec;
        isValid = validateLockSpec();
    }

    @Override
    public int getNumIterations() {
        return theKIterations * K_MULTIPLIER;
    }

    @Override
    public int getKIterations() {
        return theKIterations;
    }

    @Override
    public GordianNewKeySetSpec getKeySetSpec() {
        return theKeySetSpec;
    }

    /**
     * is the hashSpec valid?
     *
     * @return true/false
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Validate the Parameters.
     *
     * @return valid true/false
     */
    private boolean validateLockSpec() {
        /* Check keySetSpec */
        if (theKeySetSpec == null || !theKeySetSpec.isValid()) {
            return false;
        }

        /* Check iterations is in range */
        return theKIterations >= MINIMUM_ITERATIONS
                && theKIterations <= MAXIMUM_ITERATIONS;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (pThat == this) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check keySetSpec */
        return pThat instanceof GordianCorePasswordLockSpec myThat
                && theKIterations == myThat.getKIterations()
                && theKeySetSpec.equals(myThat.getKeySetSpec());
    }


    @Override
    public int hashCode() {
        return Objects.hash(theKIterations, theKeySetSpec);
    }

    @Override
    public String toString() {
        return "PasswordLock" + theKIterations + "-" + theKeySetSpec.getKeyLength() + "-" + theKeySetSpec.getCipherSteps();
    }
}
