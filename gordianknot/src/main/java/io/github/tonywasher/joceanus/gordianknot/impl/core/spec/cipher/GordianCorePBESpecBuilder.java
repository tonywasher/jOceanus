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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEArgon2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpec.GordianNewPBESCryptSpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBESpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewPBEType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpec.GordianCorePBEArgon2Spec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpec.GordianCorePBEDigestAndCountSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCorePBESpec.GordianCorePBESCryptSpec;

/**
 * PBE Specification Builder.
 */
public final class GordianCorePBESpecBuilder
        implements GordianNewPBESpecBuilder {
    /**
     * Private constructor.
     */
    private GordianCorePBESpecBuilder() {
    }

    /**
     * Obtain new instance.
     *
     * @return the new instance
     */
    public static GordianCorePBESpecBuilder newInstance() {
        return new GordianCorePBESpecBuilder();
    }

    @Override
    public GordianNewPBEDigestAndCountSpec pbKDF2(final GordianNewDigestSpec pDigestSpec,
                                                  final int pCount) {
        return new GordianCorePBEDigestAndCountSpec(GordianNewPBEType.PBKDF2, pDigestSpec, pCount);
    }

    @Override
    public GordianNewPBEDigestAndCountSpec pkcs12(final GordianNewDigestSpec pDigestSpec,
                                                  final int pCount) {
        return new GordianCorePBEDigestAndCountSpec(GordianNewPBEType.PKCS12, pDigestSpec, pCount);
    }

    @Override
    public GordianNewPBESCryptSpec scrypt(final int pCost,
                                          final int pBlockSize,
                                          final int pParallel) {
        return new GordianCorePBESCryptSpec(pCost, pBlockSize, pParallel);
    }

    @Override
    public GordianNewPBEArgon2Spec argon2(final int pLanes,
                                          final int pMemory,
                                          final int pIterations) {
        return new GordianCorePBEArgon2Spec(pLanes, pMemory, pIterations);
    }
}
