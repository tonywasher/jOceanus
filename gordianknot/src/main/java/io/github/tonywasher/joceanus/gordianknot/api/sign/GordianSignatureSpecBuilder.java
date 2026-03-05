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
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;

import java.util.Arrays;
import java.util.List;

/**
 * Signature Specification Builder.
 */
public final class GordianSignatureSpecBuilder {
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
    public static GordianSignatureSpec rsa(final GordianSignatureType pSignatureType,
                                           final GordianNewDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianNewKeyPairType.RSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create DSASpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dsa(final GordianSignatureType pSignatureType,
                                           final GordianNewDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianNewKeyPairType.DSA, pSignatureType, pDigestSpec);
    }

    /**
     * Create ECSpec.
     *
     * @param pSignatureType the signatureType
     * @param pDigestSpec    the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec ec(final GordianSignatureType pSignatureType,
                                          final GordianNewDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianNewKeyPairType.EC, pSignatureType, pDigestSpec);
    }

    /**
     * Create SM2Spec.
     *
     * @param pDigestSpec the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec sm2(final GordianNewDigestSpec pDigestSpec) {
        return new GordianSignatureSpec(GordianNewKeyPairType.SM2, pDigestSpec);
    }

    /**
     * Create DSTU4145Spec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec dstu4145() {
        return new GordianSignatureSpec(GordianNewKeyPairType.DSTU4145, GordianDigestSpecBuilder.gost());
    }

    /**
     * Create GOST2012Spec.
     *
     * @param pLength the length
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec gost2012(final GordianLength pLength) {
        return new GordianSignatureSpec(GordianNewKeyPairType.GOST2012, GordianDigestSpecBuilder.streebog(pLength));
    }

    /**
     * Create EdDSASpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec edDSA() {
        return new GordianSignatureSpec(GordianNewKeyPairType.EDDSA, GordianSignatureType.NATIVE);
    }

    /**
     * Create SLHDSASpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec slhdsa() {
        return new GordianSignatureSpec(GordianNewKeyPairType.SLHDSA, GordianSignatureType.NATIVE);
    }

    /**
     * Create MLDSASpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec mldsa() {
        return new GordianSignatureSpec(GordianNewKeyPairType.MLDSA, GordianSignatureType.NATIVE);
    }

    /**
     * Create falconSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec falcon() {
        return new GordianSignatureSpec(GordianNewKeyPairType.FALCON, GordianSignatureType.NATIVE);
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec mayo() {
        return new GordianSignatureSpec(GordianNewKeyPairType.MAYO, GordianSignatureType.NATIVE);
    }

    /**
     * Create mayoSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec snova() {
        return new GordianSignatureSpec(GordianNewKeyPairType.SNOVA, GordianSignatureType.NATIVE);
    }

    /**
     * Create picnicSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec picnic() {
        return new GordianSignatureSpec(GordianNewKeyPairType.PICNIC, GordianSignatureType.NATIVE);
    }

    /**
     * Create picnicSpec.
     *
     * @param pDigest the digestSpec
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec picnic(final GordianNewDigestSpec pDigest) {
        return new GordianSignatureSpec(GordianNewKeyPairType.PICNIC, GordianSignatureType.NATIVE, pDigest);
    }

    /**
     * Create xmssSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec xmss() {
        return new GordianSignatureSpec(GordianNewKeyPairType.XMSS, GordianSignatureType.NATIVE);
    }

    /**
     * Create xmssPHSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec xmssph() {
        return new GordianSignatureSpec(GordianNewKeyPairType.XMSS, GordianSignatureType.PREHASH);
    }

    /**
     * Create lmsSpec.
     *
     * @return the SignatureSpec
     */
    public static GordianSignatureSpec lms() {
        return new GordianSignatureSpec(GordianNewKeyPairType.LMS, GordianSignatureType.NATIVE);
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianSignatureSpec composite(final GordianSignatureSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeSpec.
     *
     * @param pSpecs the list of encryptorSpecs
     * @return the encryptorSpec
     */
    public static GordianSignatureSpec composite(final List<GordianSignatureSpec> pSpecs) {
        return new GordianSignatureSpec(GordianNewKeyPairType.COMPOSITE, pSpecs);
    }
}
