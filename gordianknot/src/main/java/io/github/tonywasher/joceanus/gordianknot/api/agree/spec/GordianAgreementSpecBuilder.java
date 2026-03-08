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

import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;

/**
 * KeyPair Agreement Specification Builder.
 */
public interface GordianAgreementSpecBuilder {
    /**
     * Define keyPairSpec.
     *
     * @param pSpec the spec
     * @return the Builder
     */
    GordianAgreementSpecBuilder withKeyPairSpec(GordianKeyPairSpec pSpec);

    /**
     * Define agreementType.
     *
     * @param pType the agreementType
     * @return the Builder
     */
    GordianAgreementSpecBuilder withAgreementType(GordianAgreementType pType);

    /**
     * Define kdf.
     *
     * @param pKDF the kdf
     * @return the Builder
     */
    GordianAgreementSpecBuilder withKDF(GordianAgreementKDF pKDF);

    /**
     * Request confirm.
     *
     * @return the builder
     */
    GordianAgreementSpecBuilder withConfirm();

    /**
     * Build signatureSpec.
     *
     * @return the agreementSpec
     */
    GordianAgreementSpec build();

    /**
     * Create generic agreement.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreeType   the agreementType
     * @param pKDFType     the KDF type
     * @return the agreementSpec
     */
    default GordianAgreementSpec agree(final GordianKeyPairSpec pKeyPairSpec,
                                       final GordianAgreementType pAgreeType,
                                       final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(pAgreeType).withKDF(pKDFType).build();
    }

    /**
     * Create generic agreement.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreeType   the agreementType
     * @param pKDFType     the KDF type
     * @param pConfirm     withConfirm(true/false)
     * @return the agreementSpec
     */
    default GordianAgreementSpec agree(final GordianKeyPairSpec pKeyPairSpec,
                                       final GordianAgreementType pAgreeType,
                                       final GordianAgreementKDF pKDFType,
                                       final boolean pConfirm) {
        withKeyPairSpec(pKeyPairSpec).withAgreementType(pAgreeType).withKDF(pKDFType);
        if (pConfirm) {
            withConfirm();
        }
        return build();
    }

    /**
     * Create the KEM agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec kem(final GordianKeyPairSpec pKeyPairSpec,
                                     final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.KEM).withKDF(pKDFType).build();
    }

    /**
     * Create the ANON agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec anon(final GordianKeyPairSpec pKeyPairSpec,
                                      final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.ANON).withKDF(pKDFType).build();
    }

    /**
     * Create the Basic agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec basic(final GordianKeyPairSpec pKeyPairSpec,
                                       final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.BASIC).withKDF(pKDFType).build();
    }

    /**
     * Create the signed agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec signed(final GordianKeyPairSpec pKeyPairSpec,
                                        final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.SIGNED).withKDF(pKDFType).build();
    }

    /**
     * Create the MQV agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec mqv(final GordianKeyPairSpec pKeyPairSpec,
                                     final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.MQV).withKDF(pKDFType).build();
    }

    /**
     * Create the MQVConfirm agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec mqvConfirm(final GordianKeyPairSpec pKeyPairSpec,
                                            final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.MQV).withKDF(pKDFType).withConfirm().build();
    }

    /**
     * Create the Unified agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec unified(final GordianKeyPairSpec pKeyPairSpec,
                                         final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.UNIFIED).withKDF(pKDFType).build();
    }

    /**
     * Create the unifiedConfirm agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec unifiedConfirm(final GordianKeyPairSpec pKeyPairSpec,
                                                final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.UNIFIED).withKDF(pKDFType).withConfirm().build();
    }

    /**
     * Create the sm2 agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec sm2(final GordianKeyPairSpec pKeyPairSpec,
                                     final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.SM2).withKDF(pKDFType).build();
    }

    /**
     * Create the sm2Confirm agreementSpec.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType     the KDF type
     * @return the Spec
     */
    default GordianAgreementSpec sm2Confirm(final GordianKeyPairSpec pKeyPairSpec,
                                            final GordianAgreementKDF pKDFType) {
        return withKeyPairSpec(pKeyPairSpec).withAgreementType(GordianAgreementType.SM2).withKDF(pKDFType).withConfirm().build();
    }
}
