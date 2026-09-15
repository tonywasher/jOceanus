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
package io.github.tonywasher.joceanus.gordianknot.api.cert;

import java.util.Set;

/**
 * Certificate Usage.
 */
public interface GordianCertUsage {
    /**
     * Add a use.
     *
     * @param pUse the use to add
     * @return the usage
     */
    GordianCertUsage withUse(GordianCertUse pUse);

    /**
     * Add uses.
     *
     * @param pUse the uses to add
     * @return the usage
     */
    GordianCertUsage withUses(GordianCertUse... pUse);

    /**
     * Remove a use.
     *
     * @param pUse the use to remove
     */
    void removeUse(GordianCertUse pUse);

    /**
     * Does the keyPair have the specified use?
     *
     * @param pUse the use to test for
     * @return true/false
     */
    boolean hasUse(GordianCertUse pUse);

    /**
     * Obtain the usageSet.
     *
     * @return the UseSet
     */
    Set<GordianCertUse> getUsageSet();

    /**
     * Add a purpose.
     *
     * @param pPurpose the purpose to add
     * @return the usage
     */
    GordianCertUsage withPurpose(GordianCertPurpose pPurpose);

    /**
     * Add purposes.
     *
     * @param pPurposes the purposes to add
     * @return the usage
     */
    GordianCertUsage withPurposes(GordianCertPurpose... pPurposes);

    /**
     * Remove a purpose.
     *
     * @param pPurpose the use to remove
     */
    void removePurpose(GordianCertPurpose pPurpose);

    /**
     * Does the keyPair have the specified purpose?
     *
     * @param pPurpose the purpose to test for
     * @return true/false
     */
    boolean hasPurpose(GordianCertPurpose pPurpose);

    /**
     * Obtain the purposeSet.
     *
     * @return the purposeSet
     */
    Set<GordianCertPurpose> getPurposeSet();
}
