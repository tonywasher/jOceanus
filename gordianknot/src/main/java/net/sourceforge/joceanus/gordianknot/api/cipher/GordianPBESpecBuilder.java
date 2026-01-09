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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBEArgon2Spec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBEDigestAndCountSpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianPBESpec.GordianPBESCryptSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;

/**
 * PBE Specification.
 */
public final class GordianPBESpecBuilder {
    /**
     * Private constructor.
     */
    private GordianPBESpecBuilder() {
    }

    /**
     * Create a pbkdf2Spec.
     * @param pDigestSpec the digestSpec.
     * @param pCount the iteration count
     * @return the new spec
     */
    public static GordianPBEDigestAndCountSpec pbKDF2(final GordianDigestSpec pDigestSpec,
                                                      final int pCount) {
        return new GordianPBEDigestAndCountSpec(GordianPBEType.PBKDF2, pDigestSpec, pCount);
    }

    /**
     * Create a pkcs12Spec.
     * @param pDigestSpec the digestSpec.
     * @param pCount the iteration count
     * @return the new spec
     */
    public static GordianPBEDigestAndCountSpec pkcs12(final GordianDigestSpec pDigestSpec,
                                                      final int pCount) {
        return new GordianPBEDigestAndCountSpec(GordianPBEType.PKCS12, pDigestSpec, pCount);
    }

    /**
     * Create a scryptSpec.
     * @param pCost the cost
     * @param pBlockSize the blockSize
     * @param pParallel the parallelisation
     * @return the new spec
     */
    public static GordianPBESCryptSpec scrypt(final int pCost,
                                              final int pBlockSize,
                                              final int pParallel) {
        return new GordianPBESCryptSpec(pCost, pBlockSize, pParallel);
    }

    /**
     * Create an argonSpec.
     * @param pLanes the Lanes
     * @param pMemory the Memory
     * @param pIterations the iterations
     * @return the new spec
     */
    public static GordianPBEArgon2Spec argon2(final int pLanes,
                                              final int pMemory,
                                              final int pIterations) {
        return new GordianPBEArgon2Spec(pLanes, pMemory, pIterations);
    }
}
