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

package io.github.tonywasher.joceanus.gordianknot.api.digest.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianIdSpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;

/**
 * DataDigestSpec definition.
 */
public interface GordianNewDigestSpec
        extends GordianIdSpec {
    /**
     * Obtain Digest Type.
     *
     * @return the DigestType
     */
    GordianNewDigestType getDigestType();

    /**
     * Obtain DigestSubSpec.
     *
     * @return the SubSpec
     */
    GordianNewDigestSubSpec getSubSpec();

    /**
     * Obtain DigestState.
     *
     * @return the State
     */
    GordianNewDigestState getDigestState();

    /**
     * Obtain Digest Length.
     *
     * @return the Length
     */
    GordianLength getDigestLength();

    /**
     * Is the digestSpec a Xof mode?
     *
     * @return true/false.
     */
    boolean isXofMode();

    /**
     * Is the digestSpec valid?
     *
     * @return true/false.
     */
    boolean isValid();
}
