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
package io.github.tonywasher.joceanus.gordianknot.api.keypair;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDHSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewDSTUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewGOSTSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewLMSSpec.GordianNewLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUPrimeSpec.GordianNewNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewPicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewRSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSM2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewSnovaSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewXMSSSpec.GordianNewXMSSMTLayers;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.keypair.GordianCoreKeyPairSpecBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric KeyPair Specification Builder.
 */
public final class GordianKeyPairSpecBuilder {
    /**
     * KeyPairSpecBuilder.
     */
    private static final GordianNewKeyPairSpecBuilder BUILDER = GordianCoreKeyPairSpecBuilder.newInstance();

    /**
     * Private constructor.
     */
    private GordianKeyPairSpecBuilder() {
    }

    /**
     * Create RSAKey.
     *
     * @param pSpec the rsaSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec rsa(final GordianNewRSASpec pSpec) {
        return BUILDER.rsa(pSpec);
    }

    /**
     * Create ECKey.
     *
     * @param pSpec the ecSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec ec(final GordianNewECSpec pSpec) {
        return BUILDER.ec(pSpec);
    }

    /**
     * Create SM2Key.
     *
     * @param pSpec the SM2Spec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec sm2(final GordianNewSM2Spec pSpec) {
        return BUILDER.sm2(pSpec);
    }

    /**
     * Create DSTU4145Key.
     *
     * @param pSpec the DSTUSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec dstu4145(final GordianNewDSTUSpec pSpec) {
        return BUILDER.dstu4145(pSpec);
    }

    /**
     * Create GOST2012Key.
     *
     * @param pSpec the GOSTSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec gost2012(final GordianNewGOSTSpec pSpec) {
        return BUILDER.gost2012(pSpec);
    }

    /**
     * Create DSAKey.
     *
     * @param pSpec the dsaSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec dsa(final GordianNewDSASpec pSpec) {
        return BUILDER.dsa(pSpec);
    }

    /**
     * Create DHKey.
     *
     * @param pSpec the dhSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec dh(final GordianNewDHSpec pSpec) {
        return BUILDER.dh(pSpec);
    }

    /**
     * Create ElGamalKey.
     *
     * @param pSpec the dhSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec elGamal(final GordianNewDHSpec pSpec) {
        return BUILDER.elGamal(pSpec);
    }

    /**
     * Create EdDSA25519 Key.
     *
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec x25519() {
        return BUILDER.x25519();
    }

    /**
     * Create EdX448 Key.
     *
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec x448() {
        return BUILDER.x448();
    }

    /**
     * Create EdDSA25519 Key.
     *
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec ed25519() {
        return BUILDER.ed25519();
    }

    /**
     * Create EdDSA448 Key.
     *
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec ed448() {
        return BUILDER.ed448();
    }

    /**
     * Create xmssKey.
     *
     * @param pDigestType the xmss digestType
     * @param pHeight     the height
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec xmss(final GordianNewXMSSDigestType pDigestType,
                                             final GordianNewXMSSHeight pHeight) {
        return BUILDER.xmss(pDigestType, pHeight);
    }

    /**
     * Create xmssMTKey.
     *
     * @param pDigestType the xmss digestType
     * @param pHeight     the height
     * @param pLayers     the layers
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec xmssmt(final GordianNewXMSSDigestType pDigestType,
                                               final GordianNewXMSSHeight pHeight,
                                               final GordianNewXMSSMTLayers pLayers) {
        return BUILDER.xmssmt(pDigestType, pHeight, pLayers);
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
    public static GordianNewKeyPairSpec lms(final GordianNewLMSHash pHashType,
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
    public static GordianNewKeyPairSpec hss(final GordianNewLMSHash pHashType,
                                            final GordianNewLMSHeight pHeight,
                                            final GordianNewLMSWidth pWidth,
                                            final GordianLength pLength,
                                            final int pDepth) {
        return BUILDER.hss(pHashType, pHeight, pWidth, pLength, pDepth);
    }

    /**
     * Create newHopeKey.
     *
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec newHope() {
        return BUILDER.newHope();
    }

    /**
     * Create SLHDSAKey.
     *
     * @param pSpec the slhdsaSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec slhdsa(final GordianNewSLHDSASpec pSpec) {
        return BUILDER.slhdsa(pSpec);
    }

    /**
     * Create CMCEKey.
     *
     * @param pSpec the cmceSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec cmce(final GordianNewCMCESpec pSpec) {
        return BUILDER.cmce(pSpec);
    }

    /**
     * Create FRODOKey.
     *
     * @param pSpec the frodoSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec frodo(final GordianNewFRODOSpec pSpec) {
        return BUILDER.frodo(pSpec);
    }

    /**
     * Create SABERKey.
     *
     * @param pSpec the saberSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec saber(final GordianNewSABERSpec pSpec) {
        return BUILDER.saber(pSpec);
    }

    /**
     * Create MLKEMKey.
     *
     * @param pSpec the mlkemSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec mlkem(final GordianNewMLKEMSpec pSpec) {
        return BUILDER.mlkem(pSpec);
    }

    /**
     * Create MLDSAKey.
     *
     * @param pSpec the mldsaSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec mldsa(final GordianNewMLDSASpec pSpec) {
        return BUILDER.mldsa(pSpec);
    }

    /**
     * Create HQCKey.
     *
     * @param pSpec the hqcSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec hqc(final GordianNewHQCSpec pSpec) {
        return BUILDER.hqc(pSpec);
    }

    /**
     * Create BIKEKey.
     *
     * @param pSpec the bikeSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec bike(final GordianNewBIKESpec pSpec) {
        return BUILDER.bike(pSpec);
    }

    /**
     * Create NTRUKey.
     *
     * @param pSpec the ntruSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec ntru(final GordianNewNTRUSpec pSpec) {
        return BUILDER.ntru(pSpec);
    }

    /**
     * Create NTRUPRIMEKey.
     *
     * @param pType   the Type
     * @param pParams the params
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec ntruprime(final GordianNewNTRUPrimeType pType,
                                                  final GordianNewNTRUPrimeParams pParams) {
        return BUILDER.ntruprime(pType, pParams);
    }

    /**
     * Create FalconKey.
     *
     * @param pSpec the falconSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec falcon(final GordianNewFalconSpec pSpec) {
        return BUILDER.falcon(pSpec);
    }

    /**
     * Create PicnicKey.
     *
     * @param pSpec the picnicSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec picnic(final GordianNewPicnicSpec pSpec) {
        return BUILDER.picnic(pSpec);
    }

    /**
     * Create MayoKey.
     *
     * @param pSpec the mayoSpec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec mayo(final GordianNewMayoSpec pSpec) {
        return BUILDER.mayo(pSpec);
    }

    /**
     * Create SnovaKey.
     *
     * @param pSpec the Snova Spec
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec snova(final GordianNewSnovaSpec pSpec) {
        return BUILDER.snova(pSpec);
    }

    /**
     * Create CompositeKey.
     *
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec composite(final GordianNewKeyPairSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeKey.
     *
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    public static GordianNewKeyPairSpec composite(final List<GordianNewKeyPairSpec> pSpecs) {
        return BUILDER.composite(pSpecs);
    }
}
