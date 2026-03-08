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

package io.github.tonywasher.joceanus.gordianknot.api.sign.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairType;

import java.util.Arrays;
import java.util.List;

/**
 * Signature Specification Builder.
 */
public interface GordianSignatureSpecBuilder {
    /**
     * Define keyPairType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianSignatureSpecBuilder withKeyPairType(GordianKeyPairType pType);

    /**
     * Define signatureType.
     *
     * @param pSignatureType the signatureType
     * @return the Builder
     */
    GordianSignatureSpecBuilder withSignatureType(GordianSignatureType pSignatureType);

    /**
     * Define digestSpec.
     *
     * @param pDigestSpec the digest spec
     * @return the Builder
     */
    GordianSignatureSpecBuilder withDigestSpec(GordianDigestSpec pDigestSpec);

    /**
     * Define signatureSpec list.
     *
     * @param pSpecs the specs
     * @return the builder
     */
    GordianSignatureSpecBuilder withSignatureSpecs(List<GordianSignatureSpec> pSpecs);

    /**
     * Access digestSpecBuilder.
     *
     * @return the digestSpec builder
     */
    GordianDigestSpecBuilder usingDigestSpecBuilder();

    /**
     * Build signatureSpec.
     *
     * @return the signatureSpec
     */
    GordianSignatureSpec build();

    /**
     * Create RSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    default GordianSignatureSpec rsa(final GordianSignatureType pSignatureType,
                                     final GordianDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.RSA).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create DSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    default GordianSignatureSpec dsa(final GordianSignatureType pSignatureType,
                                     final GordianDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.DSA).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create ECSpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    default GordianSignatureSpec ec(final GordianSignatureType pSignatureType,
                                    final GordianDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.EC).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create SM2Spec.
     *
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    default GordianSignatureSpec sm2(final GordianDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.SM2).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create DSTU4145Spec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec dstu4145() {
        return withKeyPairType(GordianKeyPairType.DSTU).withDigestSpec(usingDigestSpecBuilder().gost()).build();
    }

    /**
     * Create GOST2012Spec.
     *
     * @param pLength the length
     * @return the SignatureSpec
     */
    default GordianSignatureSpec gost2012(final GordianLength pLength) {
        return withKeyPairType(GordianKeyPairType.GOST).withDigestSpec(usingDigestSpecBuilder().streebog(pLength)).build();
    }

    /**
     * Create EdDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec edDSA() {
        return withKeyPairType(GordianKeyPairType.EDDSA).build();
    }

    /**
     * Create SLHDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec slhdsa() {
        return withKeyPairType(GordianKeyPairType.SLHDSA).build();
    }

    /**
     * Create MLDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec mldsa() {
        return withKeyPairType(GordianKeyPairType.MLDSA).build();
    }

    /**
     * Create falconSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec falcon() {
        return withKeyPairType(GordianKeyPairType.FALCON).build();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec mayo() {
        return withKeyPairType(GordianKeyPairType.MAYO).build();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec snova() {
        return withKeyPairType(GordianKeyPairType.SNOVA).build();
    }

    /**
     * Create picnicSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec picnic() {
        return withKeyPairType(GordianKeyPairType.PICNIC).build();
    }

    /**
     * Create picnicSpec.
     *
     * @param pDigest the digestSpec
     * @return the SignatureSpec
     */
    default GordianSignatureSpec picnic(final GordianDigestSpec pDigest) {
        return withKeyPairType(GordianKeyPairType.PICNIC).withDigestSpec(pDigest).build();
    }

    /**
     * Create xmssSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec xmss() {
        return withKeyPairType(GordianKeyPairType.XMSS).build();
    }

    /**
     * Create xmssPHSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec xmssph() {
        return withKeyPairType(GordianKeyPairType.XMSS).withSignatureType(GordianSignatureType.PREHASH).build();
    }

    /**
     * Create lmsSpec.
     *
     * @return the SignatureSpec
     */
    default GordianSignatureSpec lms() {
        return withKeyPairType(GordianKeyPairType.LMS).build();
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianSignatureSpec composite(final GordianSignatureSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianSignatureSpec composite(final List<GordianSignatureSpec> pSpecs) {
        return withKeyPairType(GordianKeyPairType.COMPOSITE).withSignatureSpecs(pSpecs).build();
    }
}
