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

package io.github.tonywasher.joceanus.gordianknot.api.keypair.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSMTLayers;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric KeyPair Specification Builder.
 */
public interface GordianKeyPairSpecBuilder {
    /**
     * Define keyPairType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianKeyPairSpecBuilder withKeyPairType(GordianKeyPairType pType);

    /**
     * Define enum subSpec.
     *
     * @param pSubSpec the subSpec
     * @return the Builder
     */
    GordianKeyPairSpecBuilder withEnumSubSpec(Enum<?> pSubSpec);

    /**
     * Define xmss subSpec.
     *
     * @param pDigestType the digestType
     * @param pHeight     the height
     * @return the Builder
     */
    GordianKeyPairSpecBuilder withXMSSSubSpec(GordianXMSSDigestType pDigestType,
                                              GordianXMSSHeight pHeight);

    /**
     * Define xmssMT subSpec.
     *
     * @param pDigestType the digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     * @return the Builder
     */
    GordianKeyPairSpecBuilder withXMSSMTSubSpec(GordianXMSSDigestType pDigestType,
                                                GordianXMSSHeight pHeight,
                                                GordianXMSSMTLayers pLayers);

    /**
     * Define hss subSpec.
     *
     * @param pHashType the hashType
     * @param pHeight   the height
     * @param pWidth    the width
     * @param pLength   the length
     * @param pDepth    the treeDepth
     * @return the Builder
     */
    GordianKeyPairSpecBuilder withLMSSubSpec(GordianLMSHash pHashType,
                                             GordianLMSHeight pHeight,
                                             GordianLMSWidth pWidth,
                                             GordianLength pLength,
                                             int pDepth);

    /**
     * Define ntruPrime subSpec.
     *
     * @param pType   the Type
     * @param pParams the params
     * @return the Builder
     */
    GordianKeyPairSpecBuilder withNTRUPrimeSubSpec(GordianNTRUPrimeType pType,
                                                   GordianNTRUPrimeParams pParams);

    /**
     * Define keyPairSpec list.
     *
     * @param pSpecs the specs
     * @return the builder
     */
    GordianKeyPairSpecBuilder withKeyPairSpecs(List<GordianKeyPairSpec> pSpecs);

    /**
     * Build the keyPairSpec.
     *
     * @return the keyPairSpec
     */
    GordianKeyPairSpec build();

    /**
     * Create RSAKey.
     *
     * @param pSpec the subSpec
     * @return the KeySpec
     */
    default GordianKeyPairSpec rsa(final GordianRSASpec pSpec) {
        return withKeyPairType(GordianKeyPairType.RSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create ECKey.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianKeyPairSpec ec(final GordianECSpec pCurve) {
        return withKeyPairType(GordianKeyPairType.EC).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create SM2Key.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianKeyPairSpec sm2(final GordianSM2Spec pCurve) {
        return withKeyPairType(GordianKeyPairType.SM2).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create DSTU4145Key.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianKeyPairSpec dstu4145(final GordianDSTUSpec pCurve) {
        return withKeyPairType(GordianKeyPairType.DSTU).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create GOST2012Key.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianKeyPairSpec gost2012(final GordianGOSTSpec pCurve) {
        return withKeyPairType(GordianKeyPairType.GOST).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create DSAKey.
     *
     * @param pSpec the subSpec
     * @return the KeySpec
     */
    default GordianKeyPairSpec dsa(final GordianDSASpec pSpec) {
        return withKeyPairType(GordianKeyPairType.DSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create DHKey.
     *
     * @param pSpec the subSpec
     * @return the KeySpec
     */
    default GordianKeyPairSpec dh(final GordianDHSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.DH).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create ElGamalKey.
     *
     * @param pSpec the spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec elGamal(final GordianDHSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.ELGAMAL).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create EdDSA25519 Key.
     *
     * @return the KeySpec
     */
    default GordianKeyPairSpec x25519() {
        return withKeyPairType(GordianKeyPairType.XDH).withEnumSubSpec(GordianEdwardsSpec.CURVE25519).build();
    }

    /**
     * Create EdX448 Key.
     *
     * @return the KeySpec
     */
    default GordianKeyPairSpec x448() {
        return withKeyPairType(GordianKeyPairType.XDH).withEnumSubSpec(GordianEdwardsSpec.CURVE448).build();
    }

    /**
     * Create EdDSA25519 Key.
     *
     * @return the KeySpec
     */
    default GordianKeyPairSpec ed25519() {
        return withKeyPairType(GordianKeyPairType.EDDSA).withEnumSubSpec(GordianEdwardsSpec.CURVE25519).build();
    }

    /**
     * Create EdDSA448 Key.
     *
     * @return the KeySpec
     */
    default GordianKeyPairSpec ed448() {
        return withKeyPairType(GordianKeyPairType.EDDSA).withEnumSubSpec(GordianEdwardsSpec.CURVE448).build();
    }

    /**
     * Create xmssKey.
     *
     * @param pDigestType the xmss digestType
     * @param pHeight     the height
     * @return the KeySpec
     */
    default GordianKeyPairSpec xmss(final GordianXMSSDigestType pDigestType,
                                    final GordianXMSSHeight pHeight) {
        return withKeyPairType(GordianKeyPairType.XMSS).withXMSSSubSpec(pDigestType, pHeight).build();
    }

    /**
     * Create xmssMTKey.
     *
     * @param pDigestType the xmss digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     * @return the KeySpec
     */
    default GordianKeyPairSpec xmssmt(final GordianXMSSDigestType pDigestType,
                                      final GordianXMSSHeight pHeight,
                                      final GordianXMSSMTLayers pLayers) {
        return withKeyPairType(GordianKeyPairType.XMSS).withXMSSMTSubSpec(pDigestType, pHeight, pLayers).build();
    }

    /**
     * Create lmsKey.
     *
     * @param pHashType the hashType
     * @param pHeight   the height
     * @param pWidth    the width
     * @param pLength   the length
     * @return the KeySpec
     */
    default GordianKeyPairSpec lms(final GordianLMSHash pHashType,
                                   final GordianLMSHeight pHeight,
                                   final GordianLMSWidth pWidth,
                                   final GordianLength pLength) {
        return hss(pHashType, pHeight, pWidth, pLength, 1);
    }

    /**
     * Create hssKey.
     *
     * @param pHashType the hashType
     * @param pHeight   the height
     * @param pWidth    the width
     * @param pLength   the length
     * @param pDepth    the treeDepth
     * @return the KeySpec
     */
    default GordianKeyPairSpec hss(final GordianLMSHash pHashType,
                                   final GordianLMSHeight pHeight,
                                   final GordianLMSWidth pWidth,
                                   final GordianLength pLength,
                                   final int pDepth) {
        return withKeyPairType(GordianKeyPairType.LMS).withLMSSubSpec(pHashType, pHeight, pWidth, pLength, pDepth).build();
    }

    /**
     * Create newHopeKey.
     *
     * @return the KeySpec
     */
    default GordianKeyPairSpec newHope() {
        return withKeyPairType(GordianKeyPairType.NEWHOPE).build();
    }

    /**
     * Create SLHDSAKey.
     *
     * @param pSpec the SLHDSA Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec slhdsa(final GordianSLHDSASpec pSpec) {
        return withKeyPairType(GordianKeyPairType.SLHDSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create CMCEKey.
     *
     * @param pSpec the CMCE Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec cmce(final GordianCMCESpec pSpec) {
        return withKeyPairType(GordianKeyPairType.CMCE).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create FRODOKey.
     *
     * @param pSpec the FRODO Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec frodo(final GordianFRODOSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.FRODO).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create SABERKey.
     *
     * @param pSpec the SABER Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec saber(final GordianSABERSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.SABER).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create MLKEMKey.
     *
     * @param pSpec the MLKEM Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec mlkem(final GordianMLKEMSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.MLKEM).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create MLDSAKey.
     *
     * @param pSpec the MLDSA Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec mldsa(final GordianMLDSASpec pSpec) {
        return withKeyPairType(GordianKeyPairType.MLDSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create HQCKey.
     *
     * @param pSpec the HQC Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec hqc(final GordianHQCSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.HQC).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create BIKEKey.
     *
     * @param pSpec the BIKE Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec bike(final GordianBIKESpec pSpec) {
        return withKeyPairType(GordianKeyPairType.BIKE).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create NTRUKey.
     *
     * @param pSpec the NTRU Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec ntru(final GordianNTRUSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.NTRU).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create NTRUPRIMEKey.
     *
     * @param pType   the Type
     * @param pParams the params
     * @return the KeySpec
     */
    default GordianKeyPairSpec ntruprime(final GordianNTRUPrimeType pType,
                                         final GordianNTRUPrimeParams pParams) {
        return withKeyPairType(GordianKeyPairType.NTRUPRIME).withNTRUPrimeSubSpec(pType, pParams).build();
    }

    /**
     * Create FalconKey.
     *
     * @param pSpec the FALCON Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec falcon(final GordianFalconSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.FALCON).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create PicnicKey.
     *
     * @param pSpec the Picnic Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec picnic(final GordianPicnicSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.PICNIC).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create MayoKey.
     *
     * @param pSpec the Mayo Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec mayo(final GordianMayoSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.MAYO).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create SnovaKey.
     *
     * @param pSpec the Snova Spec
     * @return the KeySpec
     */
    default GordianKeyPairSpec snova(final GordianSnovaSpec pSpec) {
        return withKeyPairType(GordianKeyPairType.SNOVA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create CompositeKey.
     *
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    default GordianKeyPairSpec composite(final GordianKeyPairSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeKey.
     *
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    default GordianKeyPairSpec composite(final List<GordianKeyPairSpec> pSpecs) {
        return withKeyPairType(GordianKeyPairType.COMPOSITE).withKeyPairSpecs(pSpecs).build();
    }
}
