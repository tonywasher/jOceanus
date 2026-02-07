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

package io.github.tonywasher.joceanus.gordianknot.api.encrypt.spec;

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric Encryption Specification Builder.
 */
public interface GordianNewEncryptorSpecBuilder {
    /**
     * Define keyPairType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewEncryptorSpecBuilder withKeyPairType(GordianKeyPairType pType);

    /**
     * Define digestSpec.
     *
     * @param pDigestSpec the digest spec
     * @return the Builder
     */
    GordianNewEncryptorSpecBuilder withDigestSpec(GordianNewDigestSpec pDigestSpec);

    /**
     * Define sm2EncryptionType.
     *
     * @param pType the SM2 encryptionType
     * @return the Builder
     */
    GordianNewEncryptorSpecBuilder withSM2EncryptionType(GordianNewSM2EncryptionType pType);

    /**
     * Define encryptorSpec list.
     *
     * @param pSpecs the specs
     * @return the digestSpec builder
     */
    GordianNewEncryptorSpecBuilder withEncryptorSpecs(List<GordianNewEncryptorSpec> pSpecs);

    /**
     * Build signatureSpec.
     *
     * @return the encryptorSpec
     */
    GordianNewEncryptorSpec build();

    /**
     * Create RSA Encryptor.
     *
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec rsa(final GordianNewDigestSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.RSA).withDigestSpec(pSpec).build();
    }

    /**
     * Create ElGamal Encryptor.
     *
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec elGamal(final GordianNewDigestSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.ELGAMAL).withDigestSpec(pSpec).build();
    }

    /**
     * Create EC Encryptor.
     *
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec ec() {
        return withKeyPairType(GordianKeyPairType.EC).build();
    }

    /**
     * Create GOST Encryptor.
     *
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec gost2012() {
        return withKeyPairType(GordianKeyPairType.GOST2012).build();
    }

    /**
     * Create SM2 Encryptor.
     *
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec sm2() {
        return withKeyPairType(GordianKeyPairType.SM2).build();
    }

    /**
     * Create SM2 Encryptor.
     *
     * @param pType the sm2EncryptionType
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec sm2(final GordianNewSM2EncryptionType pType,
                                        final GordianNewDigestSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.SM2).withSM2EncryptionType(pType).withDigestSpec(pSpec).build();
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec composite(final GordianNewEncryptorSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianNewEncryptorSpec composite(final List<GordianNewEncryptorSpec> pSpecs) {
        return withKeyPairType(GordianKeyPairType.COMPOSITE).withEncryptorSpecs(pSpecs).build();
    }
}
