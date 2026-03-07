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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;

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
    GordianNewSignatureSpecBuilder withKeyPairType(GordianNewKeyPairType pType);

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
        return withKeyPairType(GordianNewKeyPairType.RSA).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
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
        return withKeyPairType(GordianNewKeyPairType.DSA).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
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
        return withKeyPairType(GordianNewKeyPairType.EC).withSignatureType(pSignatureType).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create SM2Spec.
     *
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec sm2(final GordianNewDigestSpec pDigestSpec) {
        return withKeyPairType(GordianNewKeyPairType.SM2).withDigestSpec(pDigestSpec).build();
    }

    /**
     * Create DSTU4145Spec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec dstu4145() {
        return withKeyPairType(GordianNewKeyPairType.DSTU).withDigestSpec(usingDigestSpecBuilder().gost()).build();
    }

    /**
     * Create GOST2012Spec.
     *
     * @param pLength the length
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec gost2012(final GordianLength pLength) {
        return withKeyPairType(GordianNewKeyPairType.GOST).withDigestSpec(usingDigestSpecBuilder().streebog(pLength)).build();
    }

    /**
     * Create EdDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec edDSA() {
        return withKeyPairType(GordianNewKeyPairType.EDDSA).build();
    }

    /**
     * Create SLHDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec slhdsa() {
        return withKeyPairType(GordianNewKeyPairType.SLHDSA).build();
    }

    /**
     * Create MLDSASpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec mldsa() {
        return withKeyPairType(GordianNewKeyPairType.MLDSA).build();
    }

    /**
     * Create falconSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec falcon() {
        return withKeyPairType(GordianNewKeyPairType.FALCON).build();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec mayo() {
        return withKeyPairType(GordianNewKeyPairType.MAYO).build();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec snova() {
        return withKeyPairType(GordianNewKeyPairType.SNOVA).build();
    }

    /**
     * Create picnicSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec picnic() {
        return withKeyPairType(GordianNewKeyPairType.PICNIC).build();
    }

    /**
     * Create picnicSpec.
     *
     * @param pDigest the digestSpec
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec picnic(final GordianNewDigestSpec pDigest) {
        return withKeyPairType(GordianNewKeyPairType.PICNIC).withDigestSpec(pDigest).build();
    }

    /**
     * Create xmssSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec xmss() {
        return withKeyPairType(GordianNewKeyPairType.XMSS).build();
    }

    /**
     * Create xmssPHSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec xmssph() {
        return withKeyPairType(GordianNewKeyPairType.XMSS).withSignatureType(GordianNewSignatureType.PREHASH).build();
    }

    /**
     * Create lmsSpec.
     *
     * @return the SignatureSpec
     */
    default GordianNewSignatureSpec lms() {
        return withKeyPairType(GordianNewKeyPairType.LMS).build();
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
        return withKeyPairType(GordianNewKeyPairType.COMPOSITE).withSignatureSpecs(pSpecs).build();
    }
}
