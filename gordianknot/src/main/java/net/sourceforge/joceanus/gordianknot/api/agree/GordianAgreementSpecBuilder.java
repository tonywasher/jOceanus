/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.agree;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;

/**
 * KeyPair Agreement Specification Builder.
 */
public final class GordianAgreementSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianAgreementSpecBuilder() {
    }
    /**
     * Create the KEM agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec kem(final GordianKeyPairSpec pKeyPairSpec,
                                           final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.KEM, pKDFType);
    }

    /**
     * Create the ANON agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec anon(final GordianKeyPairSpec pKeyPairSpec,
                                            final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the Basic agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec basic(final GordianKeyPairSpec pKeyPairSpec,
                                             final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the signed agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec signed(final GordianKeyPairSpec pKeyPairSpec,
                                              final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the MQV agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec mqv(final GordianKeyPairSpec pKeyPairSpec,
                                           final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.MQV, pKDFType);
    }

    /**
     * Create the MQVConfirm agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec mqvConfirm(final GordianKeyPairSpec pKeyPairSpec,
                                                  final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.MQV, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the Unified agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec unified(final GordianKeyPairSpec pKeyPairSpec,
                                               final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.UNIFIED, pKDFType);
    }

    /**
     * Create the unifiedConfirm agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec unifiedConfirm(final GordianKeyPairSpec pKeyPairSpec,
                                                      final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.MQV, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the sm2 agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2(final GordianKeyPairSpec pKeyPairSpec,
                                           final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.SM2, pKDFType);
    }

    /**
     * Create the sm2Confirm agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2Confirm(final GordianKeyPairSpec pKeyPairSpec,
                                                  final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.SM2, pKDFType,  Boolean.TRUE);
    }
}
