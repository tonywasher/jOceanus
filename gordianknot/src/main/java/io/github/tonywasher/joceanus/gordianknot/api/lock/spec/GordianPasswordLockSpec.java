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

package io.github.tonywasher.joceanus.gordianknot.api.lock.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keyset.spec.GordianKeySetSpec;

/**
 * PasswordLock Specification.
 */
public interface GordianPasswordLockSpec {
    /**
     * Minimum iterations (2<sup>3</sup>K).
     */
    Integer MINIMUM_POWER_ITERATIONS = 3;

    /**
     * Maximum iterations (2<sup>20</sup>K).
     */
    Integer MAXIMUM_POWER_ITERATIONS = 20;

    /**
     * Default iterations. (2<sup>6</sup>K).
     */
    Integer DEFAULT_POWER_ITERATIONS = 6;

    /**
     * Access the number of Iterations.
     *
     * @return the number of iterations
     */
    int getNumIterations();

    /**
     * Access the power of Hash Iterations (2<sup>x</sup>K).
     *
     * @return the power of hash iterations
     */
    int getPowerIterations();

    /**
     * Access the keySetSpec.
     *
     * @return the keySetSpec
     */
    GordianKeySetSpec getKeySetSpec();

    /**
     * is the hashSpec valid?
     *
     * @return true/false
     */
    boolean isValid();
}
