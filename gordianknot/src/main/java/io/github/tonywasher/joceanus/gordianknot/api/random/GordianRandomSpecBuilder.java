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
package io.github.tonywasher.joceanus.gordianknot.api.random;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.random.GordianCoreRandomSpecBuilder;

/**
 * SecureRandom Specification Builder.
 */
public final class GordianRandomSpecBuilder {
    /**
     * RandomSpecBuilder.
     */
    private static final GordianNewRandomSpecBuilder BUILDER = GordianCoreRandomSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianRandomSpecBuilder() {
    }

    /**
     * Create hashSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec hash(final GordianNewDigestSpec pDigest) {
        return BUILDER.hash(pDigest);
    }

    /**
     * Create prediction resistant hashSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec hashResist(final GordianNewDigestSpec pDigest) {
        return BUILDER.hashResist(pDigest);
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec hMac(final GordianNewDigestSpec pDigest) {
        return BUILDER.hMac(pDigest);
    }

    /**
     * Create prediction resistant hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec hMacResist(final GordianNewDigestSpec pDigest) {
        return BUILDER.hMacResist(pDigest);
    }

    /**
     * Create ctrSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec ctr(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.ctr(pSymKeySpec);
    }

    /**
     * Create prediction resistant ctrSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec ctrResist(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.ctrResist(pSymKeySpec);
    }

    /**
     * Create x931Spec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec x931(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.x931(pSymKeySpec);
    }

    /**
     * Create prediction resistant x931Spec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianNewRandomSpec x931Resist(final GordianNewSymKeySpec pSymKeySpec) {
        return BUILDER.x931Resist(pSymKeySpec);
    }
}
