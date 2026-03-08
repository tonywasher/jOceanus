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

import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric Encryption Specification Builder.
 */
public interface GordianEncryptorSpecBuilder {
    /**
     * Define keyPairType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianEncryptorSpecBuilder withKeyPairType(GordianKeyPairType pType);

    /**
     * Define digestSpec.
     *
     * @param pDigestSpec the digest spec
     * @return the Builder
     */
    GordianEncryptorSpecBuilder withDigestSpec(GordianDigestSpec pDigestSpec);

    /**
     * Define sm2EncryptionType.
     *
     * @param pType       the SM2 encryptionType
     * @param pDigestSpec the digestSpec
     * @return the Builder
     */
    GordianEncryptorSpecBuilder withSM2EncryptionSpec(GordianSM2EncryptionType pType,
                                                      GordianDigestSpec pDigestSpec);

    /**
     * Define encryptorSpec list.
     *
     * @param pSpecs the specs
     * @return the builder
     */
    GordianEncryptorSpecBuilder withEncryptorSpecs(List<GordianEncryptorSpec> pSpecs);

    /**
     * Build encryptorSpec.
     *
     * @return the encryptorSpec
     */
    GordianEncryptorSpec build();

    /**
     * Create RSA Encryptor.
     *
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec rsa(final GordianDigestSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.RSA).withDigestSpec(pSpec).build();
    }

    /**
     * Create ElGamal Encryptor.
     *
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec elGamal(final GordianDigestSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.ELGAMAL).withDigestSpec(pSpec).build();
    }

    /**
     * Create EC Encryptor.
     *
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec ec() {
        return withKeyPairType(GordianKeyPairType.EC).build();
    }

    /**
     * Create GOST Encryptor.
     *
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec gost2012() {
        return withKeyPairType(GordianKeyPairType.GOST).build();
    }

    /**
     * Create SM2 Encryptor.
     *
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec sm2() {
        return withKeyPairType(GordianKeyPairType.SM2).build();
    }

    /**
     * Create SM2 Encryptor.
     *
     * @param pType the sm2EncryptionType
     * @param pSpec the digestSpec
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec sm2(final GordianSM2EncryptionType pType,
                                     final GordianDigestSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.SM2).withSM2EncryptionSpec(pType, pSpec).build();
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec composite(final GordianEncryptorSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianEncryptorSpec composite(final List<GordianEncryptorSpec> pSpecs) {
        return withKeyPairType(GordianKeyPairType.COMPOSITE).withEncryptorSpecs(pSpecs).build();
    }
}
