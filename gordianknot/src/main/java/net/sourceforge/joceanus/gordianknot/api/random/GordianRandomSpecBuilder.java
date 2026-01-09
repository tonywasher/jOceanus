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
package net.sourceforge.joceanus.gordianknot.api.random;

import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;

/**
 * SecureRandom Specification Builder.
 */
public final class GordianRandomSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianRandomSpecBuilder() {
    }

    /**
     * Create hashSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hash(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HASH, pDigest, false);
    }

    /**
     * Create prediction resistant hashSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hashResist(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HASH, pDigest, true);
    }

    /**
     * Create hMacSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hMac(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HMAC, pDigest, false);
    }

    /**
     * Create prediction resistant hMacSpec.
     * @param pDigest the digestSpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec hMacResist(final GordianDigestSpec pDigest) {
        return new GordianRandomSpec(GordianRandomType.HMAC, pDigest, true);
    }

    /**
     * Create ctrSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec ctr(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.CTR, pSymKeySpec, false);
    }

    /**
     * Create prediction resistant ctrSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec ctrResist(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.CTR, pSymKeySpec, true);
    }

    /**
     * Create x931Spec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec x931(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.X931, pSymKeySpec, false);
    }

    /**
     * Create prediction resistant x931Spec.
     * @param pSymKeySpec the symKeySpec
     * @return the RandomSpec
     */
    public static GordianRandomSpec x931Resist(final GordianSymKeySpec pSymKeySpec) {
        return new GordianRandomSpec(GordianRandomType.X931, pSymKeySpec, true);
    }
}
