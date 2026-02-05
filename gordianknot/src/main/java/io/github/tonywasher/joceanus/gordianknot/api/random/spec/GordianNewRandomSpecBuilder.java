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

package io.github.tonywasher.joceanus.gordianknot.api.random.spec;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;

/**
 * SecureRandom Specification Builder.
 */
public interface GordianNewRandomSpecBuilder {
    /**
     * Define RandomType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewRandomSpecBuilder withType(GordianNewRandomType pType);

    /**
     * Define digestSubSpec.
     *
     * @param pDigestSpec the digest subSpec
     * @return the Builder
     */
    GordianNewRandomSpecBuilder withDigestSubSpec(GordianNewDigestSpec pDigestSpec);

    /**
     * Define symKeySubSpec.
     *
     * @param pSymKeySpec the symKeySpec subSpec
     * @return the Builder
     */
    GordianNewRandomSpecBuilder withSymKeySubSpec(GordianNewSymKeySpec pSymKeySpec);

    /**
     * Use resistance.
     *
     * @return the Builder
     */
    GordianNewRandomSpecBuilder withResistance();

    /**
     * Build randomSpec.
     *
     * @return the randomSpec
     */
    GordianNewRandomSpec build();

    /**
     * Create hashSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec hash(final GordianNewDigestSpec pDigest) {
        return withType(GordianNewRandomType.HASH).withDigestSubSpec(pDigest).build();
    }

    /**
     * Create prediction resistant hashSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec hashResist(final GordianNewDigestSpec pDigest) {
        return withType(GordianNewRandomType.HASH).withDigestSubSpec(pDigest).withResistance().build();
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec hMac(final GordianNewDigestSpec pDigest) {
        return withType(GordianNewRandomType.HMAC).withDigestSubSpec(pDigest).build();
    }

    /**
     * Create prediction resistant hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec hMacResist(final GordianNewDigestSpec pDigest) {
        return withType(GordianNewRandomType.HMAC).withDigestSubSpec(pDigest).withResistance().build();
    }

    /**
     * Create ctrSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec ctr(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewRandomType.CTR).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create prediction resistant ctrSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec ctrResist(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewRandomType.CTR).withSymKeySubSpec(pSymKeySpec).withResistance().build();
    }

    /**
     * Create x931Spec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec x931(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewRandomType.X931).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create prediction resistant x931Spec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianNewRandomSpec x931Resist(final GordianNewSymKeySpec pSymKeySpec) {
        return withType(GordianNewRandomType.X931).withSymKeySubSpec(pSymKeySpec).withResistance().build();
    }
}
