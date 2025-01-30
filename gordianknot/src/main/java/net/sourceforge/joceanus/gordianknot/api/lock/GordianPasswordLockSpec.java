/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.lock;

import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;

/**
 * PasswordLock Specification.
 */
public class GordianPasswordLockSpec {
    /**
     * Minimum iterations.
     */
    public static final Integer MINIMUM_ITERATIONS = 1;

    /**
     * Maximum iterations.
     */
    public static final Integer MAXIMUM_ITERATIONS = 64;

    /**
     * Default iterations.
     */
    public static final Integer DEFAULT_ITERATIONS = 8;

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
    private final GordianKeySetSpec theKeySetSpec;

    /**
     * Is the Spec valid?.
     */
    private final boolean isValid;

    /**
     * Constructor.
     */
    public GordianPasswordLockSpec() {
        this(DEFAULT_ITERATIONS);
    }

    /**
     * Constructor.
     * @param pKIterations the iterations (x 1K).
     */
    public GordianPasswordLockSpec(final int pKIterations) {
        this(pKIterations, new GordianKeySetSpec());
    }

    /**
     * Constructor.
     * @param pKeySetSpec the keySetSpec.
     */
    public GordianPasswordLockSpec(final GordianKeySetSpec pKeySetSpec) {
        this(DEFAULT_ITERATIONS, pKeySetSpec);
    }

    /**
     * Constructor.
     * @param pKIterations the iterations (x 1K).
     * @param pKeySetSpec the keySetSpec
     */
    public GordianPasswordLockSpec(final int pKIterations,
                                   final GordianKeySetSpec pKeySetSpec) {
        theKIterations = pKIterations;
        theKeySetSpec = pKeySetSpec;
        isValid = validateLockSpec();
    }

    /**
     * Access the number of Iterations.
     * @return the number of iterations
     */
    public int getNumIterations() {
        return theKIterations * K_MULTIPLIER;
    }

    /**
     * Access the number of Hash Iterations (x 1K).
     * @return the number of hash iterations
     */
    public int getKIterations() {
        return theKIterations;
    }

    /**
     * Access the keySetSpec.
     * @return the keySetSpec
     */
    public GordianKeySetSpec getKeySetSpec() {
        return theKeySetSpec;
    }

    /**
     * is the hashSpec valid?
     * @return true/false
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Validate the Parameters.
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

        /* Make sure that the object is the same class */
        if (!(pThat instanceof GordianPasswordLockSpec)) {
            return false;
        }

        /* Access the target field */
        final GordianPasswordLockSpec myThat = (GordianPasswordLockSpec) pThat;

        /* Check Differences */
        if (theKIterations != myThat.getKIterations()) {
            return false;
        }

        /* Check keySetSpec */
        return theKeySetSpec.equals(myThat.getKeySetSpec());
    }


    @Override
    public int hashCode() {
        return theKIterations + theKeySetSpec.hashCode();
    }

    @Override
    public String toString() {
        return "PasswordLock" + theKIterations + "-" + theKeySetSpec.getKeyLength() + "-" + theKeySetSpec.getCipherSteps();
    }
}
