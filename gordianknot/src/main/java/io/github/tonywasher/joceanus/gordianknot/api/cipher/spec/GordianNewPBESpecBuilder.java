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

package io.github.tonywasher.joceanus.gordianknot.api.cipher.spec;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEArgon2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBESCryptSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;

/**
 * PBE Specification Builder.
 */
public interface GordianNewPBESpecBuilder {
    /**
     * Create a pbkdf2Spec.
     *
     * @param pDigestSpec the digestSpec.
     * @param pCount      the iteration count
     * @return the new spec
     */
    GordianNewPBEDigestAndCountSpec pbKDF2(GordianNewDigestSpec pDigestSpec,
                                           int pCount);

    /**
     * Create a pkcs12Spec.
     *
     * @param pDigestSpec the digestSpec.
     * @param pCount      the iteration count
     * @return the new spec
     */
    GordianNewPBEDigestAndCountSpec pkcs12(GordianNewDigestSpec pDigestSpec,
                                           int pCount);

    /**
     * Create a scryptSpec.
     *
     * @param pCost      the cost
     * @param pBlockSize the blockSize
     * @param pParallel  the parallelisation
     * @return the new spec
     */
    GordianNewPBESCryptSpec scrypt(int pCost,
                                   int pBlockSize,
                                   int pParallel);

    /**
     * Create an argonSpec.
     *
     * @param pLanes      the Lanes
     * @param pMemory     the Memory
     * @param pIterations the iterations
     * @return the new spec
     */
    GordianNewPBEArgon2Spec argon2(int pLanes,
                                   int pMemory,
                                   int pIterations);
}
