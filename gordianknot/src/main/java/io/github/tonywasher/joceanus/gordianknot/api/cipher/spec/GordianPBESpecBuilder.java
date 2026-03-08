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

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec.GordianPBEArgon2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec.GordianPBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianPBESpec.GordianPBESCryptSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;

/**
 * PBE Specification Builder.
 */
public interface GordianPBESpecBuilder {
    /**
     * Access digestSpecBuilder.
     *
     * @return the digestSpec builder
     */
    GordianDigestSpecBuilder usingDigestSpecBuilder();

    /**
     * Create a pbkdf2Spec.
     *
     * @param pDigestSpec the digestSpec.
     * @param pCount      the iteration count
     * @return the new spec
     */
    GordianPBEDigestAndCountSpec pbKDF2(GordianDigestSpec pDigestSpec,
                                        int pCount);

    /**
     * Create a pkcs12Spec.
     *
     * @param pDigestSpec the digestSpec.
     * @param pCount      the iteration count
     * @return the new spec
     */
    GordianPBEDigestAndCountSpec pkcs12(GordianDigestSpec pDigestSpec,
                                        int pCount);

    /**
     * Create a scryptSpec.
     *
     * @param pCost      the cost
     * @param pBlockSize the blockSize
     * @param pParallel  the parallelisation
     * @return the new spec
     */
    GordianPBESCryptSpec scrypt(int pCost,
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
    GordianPBEArgon2Spec argon2(int pLanes,
                                int pMemory,
                                int pIterations);
}
