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
package io.github.tonywasher.joceanus.gordianknot.api.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEArgon2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBESCryptSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpecBuilder;

/**
 * PBE Specification.
 */
public final class GordianPBESpecBuilder {
    /**
     * StreamKeySpecBuilder.
     */
    private static final GordianNewPBESpecBuilder BUILDER = GordianCorePBESpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianPBESpecBuilder() {
    }

    /**
     * Create a pbkdf2Spec.
     *
     * @param pDigestSpec the digestSpec.
     * @param pCount      the iteration count
     * @return the new spec
     */
    public static GordianNewPBEDigestAndCountSpec pbKDF2(final GordianNewDigestSpec pDigestSpec,
                                                         final int pCount) {
        return BUILDER.pbKDF2(pDigestSpec, pCount);
    }

    /**
     * Create a pkcs12Spec.
     *
     * @param pDigestSpec the digestSpec.
     * @param pCount      the iteration count
     * @return the new spec
     */
    public static GordianNewPBEDigestAndCountSpec pkcs12(final GordianNewDigestSpec pDigestSpec,
                                                         final int pCount) {
        return BUILDER.pkcs12(pDigestSpec, pCount);
    }

    /**
     * Create a scryptSpec.
     *
     * @param pCost      the cost
     * @param pBlockSize the blockSize
     * @param pParallel  the parallelisation
     * @return the new spec
     */
    public static GordianNewPBESCryptSpec scrypt(final int pCost,
                                                 final int pBlockSize,
                                                 final int pParallel) {
        return BUILDER.scrypt(pCost, pBlockSize, pParallel);
    }

    /**
     * Create an argonSpec.
     *
     * @param pLanes      the Lanes
     * @param pMemory     the Memory
     * @param pIterations the iterations
     * @return the new spec
     */
    public static GordianNewPBEArgon2Spec argon2(final int pLanes,
                                                 final int pMemory,
                                                 final int pIterations) {
        return BUILDER.argon2(pLanes, pMemory, pIterations);
    }
}
