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
package io.github.tonywasher.joceanus.gordianknot.api.encrypt;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpec;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewEncryptorSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec.GordianNewSM2EncryptionType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.encrypt.GordianCoreEncryptorSpecBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric Encryption Specification Builder.
 */
public final class GordianEncryptorSpecBuilder {
    /**
     * EncryptorSpecBuilder.
     */
    private static final GordianNewEncryptorSpecBuilder BUILDER = GordianCoreEncryptorSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianEncryptorSpecBuilder() {
    }

    /**
     * Create RSA Encryptor.
     *
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec rsa(final GordianNewDigestSpec pSpec) {
        return BUILDER.rsa(pSpec);
    }

    /**
     * Create ElGamal Encryptor.
     *
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec elGamal(final GordianNewDigestSpec pSpec) {
        return BUILDER.elGamal(pSpec);
    }

    /**
     * Create EC Encryptor.
     *
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec ec() {
        return BUILDER.ec();
    }

    /**
     * Create GOST Encryptor.
     *
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec gost2012() {
        return BUILDER.gost2012();
    }

    /**
     * Create SM2 Encryptor.
     *
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec sm2() {
        return BUILDER.sm2();
    }

    /**
     * Create SM2 Encryptor.
     *
     * @param pType the sm2EncryptionType
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec sm2(final GordianNewSM2EncryptionType pType,
                                              final GordianNewDigestSpec pSpec) {
        return BUILDER.sm2(pType, pSpec);
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec composite(final GordianNewEncryptorSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianNewEncryptorSpec composite(final List<GordianNewEncryptorSpec> pSpecs) {
        return BUILDER.composite(pSpecs);
    }
}
