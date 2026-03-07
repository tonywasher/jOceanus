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
package io.github.tonywasher.joceanus.gordianknot.api.sign;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpec;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.sign.spec.GordianNewSignatureType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.sign.GordianCoreSignatureSpecBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Signature Specification Builder.
 */
public final class GordianSignatureSpecBuilder {
    /**
     * SignatureSpecBuilder.
     */
    private static final GordianNewSignatureSpecBuilder BUILDER = GordianCoreSignatureSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianSignatureSpecBuilder() {
    }

    /**
     * Create RSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec rsa(final GordianNewSignatureType pSignatureType,
                                              final GordianNewDigestSpec pDigestSpec) {
        return BUILDER.rsa(pSignatureType, pDigestSpec);
    }

    /**
     * Create DSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec dsa(final GordianNewSignatureType pSignatureType,
                                              final GordianNewDigestSpec pDigestSpec) {
        return BUILDER.dsa(pSignatureType, pDigestSpec);
    }

    /**
     * Create ECSpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec ec(final GordianNewSignatureType pSignatureType,
                                             final GordianNewDigestSpec pDigestSpec) {
        return BUILDER.ec(pSignatureType, pDigestSpec);
    }

    /**
     * Create SM2Spec.
     *
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec sm2(final GordianNewDigestSpec pDigestSpec) {
        return BUILDER.sm2(pDigestSpec);
    }

    /**
     * Create DSTU4145Spec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec dstu4145() {
        return BUILDER.dstu4145();
    }

    /**
     * Create GOST2012Spec.
     *
     * @param pLength the length
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec gost2012(final GordianLength pLength) {
        return BUILDER.gost2012(pLength);
    }

    /**
     * Create EdDSASpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec edDSA() {
        return BUILDER.edDSA();
    }

    /**
     * Create SLHDSASpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec slhdsa() {
        return BUILDER.slhdsa();
    }

    /**
     * Create MLDSASpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec mldsa() {
        return BUILDER.mldsa();
    }

    /**
     * Create falconSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec falcon() {
        return BUILDER.falcon();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec mayo() {
        return BUILDER.mayo();
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec snova() {
        return BUILDER.snova();
    }

    /**
     * Create picnicSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec picnic() {
        return BUILDER.picnic();
    }

    /**
     * Create picnicSpec.
     *
     * @param pDigest the digestSpec
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec picnic(final GordianNewDigestSpec pDigest) {
        return BUILDER.picnic(pDigest);
    }

    /**
     * Create xmssSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec xmss() {
        return BUILDER.xmss();
    }

    /**
     * Create xmssPHSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec xmssph() {
        return BUILDER.xmssph();
    }

    /**
     * Create lmsSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianNewSignatureSpec lms() {
        return BUILDER.lms();
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianNewSignatureSpec composite(final GordianNewSignatureSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianNewSignatureSpec composite(final List<GordianNewSignatureSpec> pSpecs) {
        return BUILDER.composite(pSpecs);
    }
}
