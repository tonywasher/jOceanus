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

package io.github.tonywasher.joceanus.gordianknot.api.agree.spec;

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;

/**
 * KeyPair Agreement Specification Builder.
 */
public interface GordianNewAgreementSpecBuilder {
    /**
     * Define keyPairSpec.
     *
     * @param pSpec the spec
     * @return the Builder
     */
    GordianNewAgreementSpecBuilder withKeyPairSpec(GordianNewKeyPairSpec pSpec);

    /**
     * Define agreementType.
     *
     * @param pType the agreementType
     * @return the Builder
     */
    GordianNewAgreementSpecBuilder withAgreementType(GordianNewAgreementType pType);

    /**
     * Define kdf.
     *
     * @param pKDF the kdf
     * @return the Builder
     */
    GordianNewAgreementSpecBuilder withKDF(GordianNewAgreementKDF pKDF);

    /**
     * Request confirm.
     *
     * @return the builder
     */
    GordianNewAgreementSpecBuilder withConfirm();

    /**
     * Build signatureSpec.
     *
     * @return the agreementSpec
     */
    GordianNewAgreementSpec build();

    /**
     * Create the KEM agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec kem(final GordianNewKeyPairSpec pKeyPairSpec,
                                        final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.KEM).withKDF(pKDFType).build();
    }

    /**
     * Create the ANON agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec anon(final GordianNewKeyPairSpec pKeyPairSpec,
                                         final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.ANON).withKDF(pKDFType).build();
    }

    /**
     * Create the Basic agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec basic(final GordianNewKeyPairSpec pKeyPairSpec,
                                          final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.BASIC).withKDF(pKDFType).build();
    }

    /**
     * Create the signed agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec signed(final GordianNewKeyPairSpec pKeyPairSpec,
                                           final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.SIGNED).withKDF(pKDFType).build();
    }

    /**
     * Create the MQV agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec mqv(final GordianNewKeyPairSpec pKeyPairSpec,
                                        final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.MQV).withKDF(pKDFType).build();
    }

    /**
     * Create the MQVConfirm agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec mqvConfirm(final GordianNewKeyPairSpec pKeyPairSpec,
                                               final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.MQV).withKDF(pKDFType).withConfirm().build();
    }

    /**
     * Create the Unified agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec unified(final GordianNewKeyPairSpec pKeyPairSpec,
                                            final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.UNIFIED).withKDF(pKDFType).build();
    }

    /**
     * Create the unifiedConfirm agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec unifiedConfirm(final GordianNewKeyPairSpec pKeyPairSpec,
                                                   final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.UNIFIED).withKDF(pKDFType).withConfirm().build();
    }

    /**
     * Create the sm2 agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec sm2(final GordianNewKeyPairSpec pKeyPairSpec,
                                        final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.SM2).withKDF(pKDFType).build();
    }

    /**
     * Create the sm2Confirm agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianNewAgreementSpec sm2Confirm(final GordianNewKeyPairSpec pKeyPairSpec,
                                               final GordianNewAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianNewAgreementType.SM2).withKDF(pKDFType).withConfirm().build();
    }
}
