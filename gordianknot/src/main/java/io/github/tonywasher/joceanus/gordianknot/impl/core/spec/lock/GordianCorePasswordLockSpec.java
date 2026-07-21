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

import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpec;
import io.github.tonywasher.joceanus.gordianknot.api.lock.spec.GordianPasswordLockSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keyset.GordianCoreKeySetSpecBuilder;

import java.util.Objects;

/**
 * PasswordLock Specification.
 */
public class GordianCorePasswordLockSpec
        implements GordianPasswordLockSpec {
    /**
     * 1K Multiplier.
     */
    private static final int K_MULTIPLIER = 1024;

    /**
     * The Number of iterations (2<sup>x</sup> x 1K).
     */
    private final int thePowerIterations;

    /**
     * The KeySetSpec.
     */
    private final GordianKeySetSpec theKeySetSpec;

    /**
     * Is the Spec valid?.
     */
    private final boolean isValid;

    /**
     * Constructor.
     */
    GordianCorePasswordLockSpec() {
        this(DEFAULT_POWER_ITERATIONS);
    }

    /**
     * Constructor.
     *
     * @param pPowerIterations the iterations (x 1K).
     */
    GordianCorePasswordLockSpec(final int pPowerIterations) {
        this(pPowerIterations, GordianCoreKeySetSpecBuilder.newInstance().keySet());
    }

    /**
     * Constructor.
     *
     * @param pKeySetSpec the keySetSpec.
     */
    GordianCorePasswordLockSpec(final GordianKeySetSpec pKeySetSpec) {
        this(DEFAULT_POWER_ITERATIONS, pKeySetSpec);
    }

    /**
     * Constructor.
     *
     * @param pPowerIterations the iterations (x 1K).
     * @param pKeySetSpec      the keySetSpec
     */
    GordianCorePasswordLockSpec(final int pPowerIterations,
                                final GordianKeySetSpec pKeySetSpec) {
        thePowerIterations = pPowerIterations;
        theKeySetSpec = pKeySetSpec;
        isValid = validateLockSpec();
    }

    @Override
    public int getNumIterations() {
        return (1 << thePowerIterations) * K_MULTIPLIER;
    }

    @Override
    public int getPowerIterations() {
        return thePowerIterations;
    }

    @Override
    public GordianKeySetSpec getKeySetSpec() {
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
        return thePowerIterations >= MINIMUM_POWER_ITERATIONS
                && thePowerIterations <= MAXIMUM_POWER_ITERATIONS;
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
                && thePowerIterations == myThat.getPowerIterations()
                && theKeySetSpec.equals(myThat.getKeySetSpec());
    }


    @Override
    public int hashCode() {
        return Objects.hash(thePowerIterations, theKeySetSpec);
    }

    @Override
    public String toString() {
        return "PasswordLock" + thePowerIterations + "-" + theKeySetSpec.getKeyLength() + "-" + theKeySetSpec.getCipherSteps();
    }
}
