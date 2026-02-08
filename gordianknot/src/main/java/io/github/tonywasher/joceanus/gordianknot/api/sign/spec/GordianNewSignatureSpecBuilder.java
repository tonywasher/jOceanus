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
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.GordianKeyPairType;

import java.util.Arrays;
import java.util.List;

/**
 * Signature Specification Builder.
 */
public interface GordianNewSignatureSpecBuilder {
    /**
     * Define keyPairType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewSignatureSpecBuilder withKeyPairType(GordianKeyPairType pType);

    /**
     * Define signatureType.
     *
     * @param pSignatureType the signatureType
     * @return the Builder
     */
    GordianNewSignatureSpecBuilder withSignatureType(GordianNewSignatureType pSignatureType);

    /**
     * Define digestSpec.
     *
     * @param pDigestSpec the digest spec
     * @return the Builder
     */
    GordianNewSignatureSpecBuilder withDigestSpec(GordianNewDigestSpec pDigestSpec);

    /**
     * Define signatureSpec list.
     *
     * @param pSpecs the specs
     * @return the builder
     */
    GordianNewSignatureSpecBuilder withSignatureSpecs(List<GordianNewSignatureSpec> pSpecs);

    /**
     * Access digestSpecBuilder.
     *
     * @return the digestSpec builder
     */
    GordianNewDigestSpecBuilder usingDigestSpecBuilder();

    /**
     * Build signatureSpec.
     *
     * @return the signatureSpec
     */
    GordianNewSignatureSpec build();

    /**
     * Create RSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec rsa(final GordianNewSignatureType pSignatureType,
                                        final GordianNewDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.RSA).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create DSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec dsa(final GordianNewSignatureType pSignatureType,
                                        final GordianNewDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.DSA).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create ECSpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec ec(final GordianNewSignatureType pSignatureType,
                                       final GordianNewDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.EC).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create SM2Spec.
     *
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec sm2(final GordianNewDigestSpec pDigestSpec) {
        return withKeyPairType(GordianKeyPairType.SM2).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create DSTU4145Spec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec dstu4145() {
        return withKeyPairType(GordianKeyPairType.DSTU4145).withDigestSpec(usingDigestSpecBuilder().gost()).build();
    }

    /**
     * Create GOST2012Spec.
     *
     * @param pLength the length
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec gost2012(final GordianLength pLength) {
        return withKeyPairType(GordianKeyPairType.GOST2012).withDigestSpec(usingDigestSpecBuilder().streebog(pLength)).build();
    }

    /**
     * Create EdDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec edDSA() {
        return withKeyPairType(GordianKeyPairType.EDDSA).build();
    }

    /**
     * Create SLHDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec slhdsa() {
        return withKeyPairType(GordianKeyPairType.RSA).build();
    }

    /**
     * Create MLDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec mldsa() {
        return withKeyPairType(GordianKeyPairType.MLDSA).build();
    }

    /**
     * Create falconSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec falcon() {
        return withKeyPairType(GordianKeyPairType.FALCON).build();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec mayo() {
        return withKeyPairType(GordianKeyPairType.MAYO).build();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec snova() {
        return withKeyPairType(GordianKeyPairType.SNOVA).build();
    }

    /**
     * Create picnicSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec picnic() {
        return withKeyPairType(GordianKeyPairType.PICNIC).build();
    }

    /**
     * Create picnicSpec.
     *
     * @param pDigest the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec picnic(final GordianNewDigestSpec pDigest) {
        return withKeyPairType(GordianKeyPairType.PICNIC).withDigestSpec(pDigest).build();
    }

    /**
     * Create xmssSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec xmss() {
        return withKeyPairType(GordianKeyPairType.XMSS).build();
    }

    /**
     * Create xmssPHSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec xmssph() {
        return withKeyPairType(GordianKeyPairType.RSA).withSignatureType(GordianNewSignatureType.PREHASH).build();
    }

    /**
     * Create lmsSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec lms() {
        return withKeyPairType(GordianKeyPairType.LMS).build();
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianNewSignatureSpec composite(final GordianNewSignatureSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    default GordianNewSignatureSpec composite(final List<GordianNewSignatureSpec> pSpecs) {
        return withKeyPairType(GordianKeyPairType.COMPOSITE).withSignatureSpecs(pSpecs).build();
    }
}
