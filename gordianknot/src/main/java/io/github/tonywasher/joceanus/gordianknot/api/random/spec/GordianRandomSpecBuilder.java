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

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianSymKeySpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;

/**
 * SecureRandom Specification Builder.
 */
public interface GordianRandomSpecBuilder {
    /**
     * Define RandomType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianRandomSpecBuilder withType(GordianRandomType pType);

    /**
     * Define digestSubSpec.
     *
     * @param pDigestSpec the digest subSpec
     * @return the Builder
     */
    GordianRandomSpecBuilder withDigestSubSpec(GordianDigestSpec pDigestSpec);

    /**
     * Define symKeySubSpec.
     *
     * @param pSymKeySpec the symKeySpec subSpec
     * @return the Builder
     */
    GordianRandomSpecBuilder withSymKeySubSpec(GordianSymKeySpec pSymKeySpec);

    /**
     * Use resistance.
     *
     * @return the Builder
     */
    GordianRandomSpecBuilder withResistance();

    /**
     * Access digestSpecBuilder.
     *
     * @return the digestSpec builder
     */
    GordianDigestSpecBuilder usingDigestSpecBuilder();

    /**
     * Access symKeySpecBuilder.
     *
     * @return the symKeySpec builder
     */
    GordianSymKeySpecBuilder usingSymKeySpecBuilder();

    /**
     * Build randomSpec.
     *
     * @return the randomSpec
     */
    GordianRandomSpec build();

    /**
     * Create hashSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianRandomSpec hash(final GordianDigestSpec pDigest) {
        return withType(GordianRandomType.HASH).withDigestSubSpec(pDigest).build();
    }

    /**
     * Create prediction resistant hashSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianRandomSpec hashResist(final GordianDigestSpec pDigest) {
        return withType(GordianRandomType.HASH).withDigestSubSpec(pDigest).withResistance().build();
    }

    /**
     * Create hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianRandomSpec hMac(final GordianDigestSpec pDigest) {
        return withType(GordianRandomType.HMAC).withDigestSubSpec(pDigest).build();
    }

    /**
     * Create prediction resistant hMacSpec.
     *
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    default GordianRandomSpec hMacResist(final GordianDigestSpec pDigest) {
        return withType(GordianRandomType.HMAC).withDigestSubSpec(pDigest).withResistance().build();
    }

    /**
     * Create ctrSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianRandomSpec ctr(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianRandomType.CTR).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create prediction resistant ctrSpec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianRandomSpec ctrResist(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianRandomType.CTR).withSymKeySubSpec(pSymKeySpec).withResistance().build();
    }

    /**
     * Create x931Spec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianRandomSpec x931(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianRandomType.X931).withSymKeySubSpec(pSymKeySpec).build();
    }

    /**
     * Create prediction resistant x931Spec.
     *
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    default GordianRandomSpec x931Resist(final GordianSymKeySpec pSymKeySpec) {
        return withType(GordianRandomType.X931).withSymKeySubSpec(pSymKeySpec).withResistance().build();
    }
}
