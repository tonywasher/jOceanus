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
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSMTLayers;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric KeyPair Specification Builder.
 */
public interface GordianNewKeyPairSpecBuilder {
    /**
     * Define keyPairType.
     *
     * @param pType the type
     * @return the Builder
     */
    GordianNewKeyPairSpecBuilder withKeyPairType(GordianNewKeyPairType pType);

    /**
     * Define enum subSpec.
     *
     * @param pSubSpec the subSpec
     * @return the Builder
     */
    GordianNewKeyPairSpecBuilder withEnumSubSpec(Enum<?> pSubSpec);

    /**
     * Define xmss subSpec.
     *
     * @param pDigestType the digestType
     * @param pHeight     the height
     * @return the Builder
     */
    GordianNewKeyPairSpecBuilder withXMSSSubSpec(GordianNewXMSSDigestType pDigestType,
                                                 GordianNewXMSSHeight pHeight);

    /**
     * Define xmssMT subSpec.
     *
     * @param pDigestType the digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     * @return the Builder
     */
    GordianNewKeyPairSpecBuilder withXMSSMTSubSpec(GordianNewXMSSDigestType pDigestType,
                                                   GordianNewXMSSHeight pHeight,
                                                   GordianNewXMSSMTLayers pLayers);

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
    GordianNewKeyPairSpecBuilder withHSSSubSpec(GordianNewLMSHash pHashType,
                                                GordianNewLMSHeight pHeight,
                                                GordianNewLMSWidth pWidth,
                                                GordianLength pLength,
                                                int pDepth);

    /**
     * Define ntruPrime subSpec.
     *
     * @param pType   the Type
     * @param pParams the params
     * @return the Builder
     */
    GordianNewKeyPairSpecBuilder withNTRUPrimeSubSpec(GordianNewNTRUPrimeType pType,
                                                      GordianNewNTRUPrimeParams pParams);

    /**
     * Define keyPairSpec list.
     *
     * @param pSpecs the specs
     * @return the builder
     */
    GordianNewKeyPairSpecBuilder withKeyPairSpecs(List<GordianNewKeyPairSpec> pSpecs);

    /**
     * Build the keyPairSpec.
     *
     * @return the keyPairSpec
     */
    GordianNewKeyPairSpec build();

    /**
     * Create RSAKey.
     *
     * @param pSpec the subSpec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec rsa(final GordianNewRSASpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.RSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create ECKey.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec ec(final GordianNewECSpec pCurve) {
        return withKeyPairType(GordianNewKeyPairType.EC).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create SM2Key.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec sm2(final GordianNewSM2Spec pCurve) {
        return withKeyPairType(GordianNewKeyPairType.SM2).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create DSTU4145Key.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec dstu4145(final GordianNewDSTUSpec pCurve) {
        return withKeyPairType(GordianNewKeyPairType.DSTU4145).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create GOST2012Key.
     *
     * @param pCurve the curve
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec gost2012(final GordianNewGOSTSpec pCurve) {
        return withKeyPairType(GordianNewKeyPairType.GOST2012).withEnumSubSpec(pCurve).build();
    }

    /**
     * Create DSAKey.
     *
     * @param pSpec the subSpec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec dsa(final GordianNewDSASpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.DSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create DHKey.
     *
     * @param pSpec the subSpec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec dh(final GordianNewDHSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.DH).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create ElGamalKey.
     *
     * @param pSpec the spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec elGamal(final GordianNewDHSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.ELGAMAL).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create EdDSA25519 Key.
     *
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec x25519() {
        return withKeyPairType(GordianNewKeyPairType.XDH).withEnumSubSpec(GordianNewEdwardsSpec.CURVE25519).build();
    }

    /**
     * Create EdX448 Key.
     *
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec x448() {
        return withKeyPairType(GordianNewKeyPairType.XDH).withEnumSubSpec(GordianNewEdwardsSpec.CURVE448).build();
    }

    /**
     * Create EdDSA25519 Key.
     *
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec ed25519() {
        return withKeyPairType(GordianNewKeyPairType.EDDSA).withEnumSubSpec(GordianNewEdwardsSpec.CURVE25519).build();
    }

    /**
     * Create EdDSA448 Key.
     *
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec ed448() {
        return withKeyPairType(GordianNewKeyPairType.EDDSA).withEnumSubSpec(GordianNewEdwardsSpec.CURVE448).build();
    }

    /**
     * Create xmssKey.
     *
     * @param pDigestType the xmss digestType
     * @param pHeight     the height
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec xmss(final GordianNewXMSSDigestType pDigestType,
                                       final GordianNewXMSSHeight pHeight) {
        return withKeyPairType(GordianNewKeyPairType.XMSS).withXMSSSubSpec(pDigestType, pHeight).build();
    }

    /**
     * Create xmssMTKey.
     *
     * @param pDigestType the xmss digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec xmssmt(final GordianNewXMSSDigestType pDigestType,
                                         final GordianNewXMSSHeight pHeight,
                                         final GordianNewXMSSMTLayers pLayers) {
        return withKeyPairType(GordianNewKeyPairType.XMSS).withXMSSMTSubSpec(pDigestType, pHeight, pLayers).build();
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
    default GordianNewKeyPairSpec lms(final GordianNewLMSHash pHashType,
                                      final GordianNewLMSHeight pHeight,
                                      final GordianNewLMSWidth pWidth,
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
    default GordianNewKeyPairSpec hss(final GordianNewLMSHash pHashType,
                                      final GordianNewLMSHeight pHeight,
                                      final GordianNewLMSWidth pWidth,
                                      final GordianLength pLength,
                                      final int pDepth) {
        return withKeyPairType(GordianNewKeyPairType.LMS).withHSSSubSpec(pHashType, pHeight, pWidth, pLength, pDepth).build();
    }

    /**
     * Create newHopeKey.
     *
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec newHope() {
        return withKeyPairType(GordianNewKeyPairType.NEWHOPE).build();
    }

    /**
     * Create SLHDSAKey.
     *
     * @param pSpec the SLHDSA Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec slhdsa(final GordianNewSLHDSASpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.SLHDSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create CMCEKey.
     *
     * @param pSpec the CMCE Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec cmce(final GordianNewCMCESpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.CMCE).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create FRODOKey.
     *
     * @param pSpec the FRODO Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec frodo(final GordianNewFRODOSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.FRODO).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create SABERKey.
     *
     * @param pSpec the SABER Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec saber(final GordianNewSABERSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.SABER).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create MLKEMKey.
     *
     * @param pSpec the MLKEM Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec mlkem(final GordianNewMLKEMSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.MLKEM).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create MLDSAKey.
     *
     * @param pSpec the MLDSA Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec mldsa(final GordianNewMLDSASpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.MLDSA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create HQCKey.
     *
     * @param pSpec the HQC Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec hqc(final GordianNewHQCSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.HQC).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create BIKEKey.
     *
     * @param pSpec the BIKE Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec bike(final GordianNewBIKESpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.BIKE).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create NTRUKey.
     *
     * @param pSpec the NTRU Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec ntru(final GordianNewNTRUSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.NTRU).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create NTRUPRIMEKey.
     *
     * @param pType   the Type
     * @param pParams the params
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec ntruprime(final GordianNewNTRUPrimeType pType,
                                            final GordianNewNTRUPrimeParams pParams) {
        return withKeyPairType(GordianNewKeyPairType.NTRUPRIME).withNTRUPrimeSubSpec(pType, pParams).build();
    }

    /**
     * Create FalconKey.
     *
     * @param pSpec the FALCON Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec falcon(final GordianNewFalconSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.FALCON).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create PicnicKey.
     *
     * @param pSpec the Picnic Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec picnic(final GordianNewPicnicSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.PICNIC).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create MayoKey.
     *
     * @param pSpec the Mayo Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec mayo(final GordianNewMayoSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.MAYO).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create SnovaKey.
     *
     * @param pSpec the Snova Spec
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec snova(final GordianNewSnovaSpec pSpec) {
        return withKeyPairType(GordianNewKeyPairType.SNOVA).withEnumSubSpec(pSpec).build();
    }

    /**
     * Create CompositeKey.
     *
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec composite(final GordianNewKeyPairSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeKey.
     *
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    default GordianNewKeyPairSpec composite(final List<GordianNewKeyPairSpec> pSpecs) {
        return withKeyPairType(GordianNewKeyPairType.COMPOSITE).withKeyPairSpecs(pSpecs).build();
    }
}
