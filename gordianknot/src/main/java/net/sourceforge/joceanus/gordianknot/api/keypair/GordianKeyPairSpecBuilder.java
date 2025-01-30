/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.keypair;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSMTLayers;

import java.util.Arrays;
import java.util.List;

/**
 * Asymmetric KeyPair Specification Builder.
 */
public final class GordianKeyPairSpecBuilder {
    /**
     * Private constructor.
     */
    private GordianKeyPairSpecBuilder() {
    }

    /**
     * Create RSAKey.
     * @param pModulus the modulus
     * @return the KeySpec
     */
    public static GordianKeyPairSpec rsa(final GordianRSAModulus pModulus) {
        return new GordianKeyPairSpec(GordianKeyPairType.RSA, pModulus);
    }

    /**
     * Create ECKey.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianKeyPairSpec ec(final GordianDSAElliptic pCurve) {
        return new GordianKeyPairSpec(GordianKeyPairType.EC, pCurve);
    }

    /**
     * Create SM2Key.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianKeyPairSpec sm2(final GordianSM2Elliptic pCurve) {
        return new GordianKeyPairSpec(GordianKeyPairType.SM2, pCurve);
    }

    /**
     * Create DSTU4145Key.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianKeyPairSpec dstu4145(final GordianDSTU4145Elliptic pCurve) {
        return new GordianKeyPairSpec(GordianKeyPairType.DSTU4145, pCurve);
    }

    /**
     * Create GOST2012Key.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianKeyPairSpec gost2012(final GordianGOSTElliptic pCurve) {
        return new GordianKeyPairSpec(GordianKeyPairType.GOST2012, pCurve);
    }

    /**
     * Create DSAKey.
     * @param pKeyType the keyType
     * @return the KeySpec
     */
    public static GordianKeyPairSpec dsa(final GordianDSAKeyType pKeyType) {
        return new GordianKeyPairSpec(GordianKeyPairType.DSA, pKeyType);
    }

    /**
     * Create DHKey.
     * @param pGroup the group
     * @return the KeySpec
     */
    public static GordianKeyPairSpec dh(final GordianDHGroup pGroup) {
        return new GordianKeyPairSpec(GordianKeyPairType.DH, pGroup);
    }

    /**
     * Create ElGamalKey.
     * @param pGroup the group
     * @return the KeySpec
     */
    public static GordianKeyPairSpec elGamal(final GordianDHGroup pGroup) {
        return new GordianKeyPairSpec(GordianKeyPairType.ELGAMAL, pGroup);
    }

    /**
     * Create EdDSA25519 Key.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec x25519() {
        return new GordianKeyPairSpec(GordianKeyPairType.XDH, GordianEdwardsElliptic.CURVE25519);
    }

    /**
     * Create EdX448 Key.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec x448() {
        return new GordianKeyPairSpec(GordianKeyPairType.XDH, GordianEdwardsElliptic.CURVE448);
    }

    /**
     * Create EdDSA25519 Key.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec ed25519() {
        return new GordianKeyPairSpec(GordianKeyPairType.EDDSA, GordianEdwardsElliptic.CURVE25519);
    }

    /**
     * Create EdDSA448 Key.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec ed448() {
        return new GordianKeyPairSpec(GordianKeyPairType.EDDSA, GordianEdwardsElliptic.CURVE448);
    }

    /**
     * Create xmssKey.
     * @param pDigestType the xmss digestType
     * @param pHeight the height
     * @return the KeySpec
     */
    public static GordianKeyPairSpec xmss(final GordianXMSSDigestType pDigestType,
                                          final GordianXMSSHeight pHeight) {
        return new GordianKeyPairSpec(GordianKeyPairType.XMSS, GordianXMSSKeySpec.xmss(pDigestType, pHeight));
    }

    /**
     * Create xmssMTKey.
     * @param pDigestType the xmss digestType
     * @param pHeight the height
     * @param pLayers the layers
     * @return the KeySpec
     */
    public static GordianKeyPairSpec xmssmt(final GordianXMSSDigestType pDigestType,
                                            final GordianXMSSHeight pHeight,
                                            final GordianXMSSMTLayers pLayers) {
        return new GordianKeyPairSpec(GordianKeyPairType.XMSS, GordianXMSSKeySpec.xmssmt(pDigestType, pHeight, pLayers));
    }

    /**
     * Create lmsKey.
     * @param pKeySpec the keySpec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec lms(final GordianLMSKeySpec pKeySpec) {
        return hss(pKeySpec, 1);
    }

    /**
     * Create hssKey.
     * @param pKeySpec the keySpec
     * @param pDepth the treeDepth
     * @return the KeySpec
     */
    public static GordianKeyPairSpec hss(final GordianLMSKeySpec pKeySpec,
                                         final int pDepth) {
        return new GordianKeyPairSpec(GordianKeyPairType.LMS, new GordianHSSKeySpec(pKeySpec, pDepth));
    }

    /**
     * Create newHopeKey.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec newHope() {
        return new GordianKeyPairSpec(GordianKeyPairType.NEWHOPE, null);
    }

    /**
     * Create SLHDSAKey.
     * @param pSpec the SLHDSA Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec slhdsa(final GordianSLHDSASpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.SLHDSA, pSpec);
    }

    /**
     * Create CMCEKey.
     * @param pSpec the CMCE Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec cmce(final GordianCMCESpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.CMCE, pSpec);
    }

    /**
     * Create FRODOKey.
     * @param pSpec the FRODO Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec frodo(final GordianFRODOSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.FRODO, pSpec);
    }

    /**
     * Create SABERKey.
     * @param pSpec the SABER Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec saber(final GordianSABERSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.SABER, pSpec);
    }

    /**
     * Create MLKEMKey.
     * @param pSpec the MLKEM Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec mlkem(final GordianMLKEMSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.MLKEM, pSpec);
    }

    /**
     * Create MLDSAKey.
     * @param pSpec the MLDSA Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec mldsa(final GordianMLDSASpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.MLDSA, pSpec);
    }

    /**
     * Create HQCKey.
     * @param pSpec the HQC Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec hqc(final GordianHQCSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.HQC, pSpec);
    }

    /**
     * Create BIKEKey.
     * @param pSpec the BIKE Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec bike(final GordianBIKESpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.BIKE, pSpec);
    }

    /**
     * Create NTRUKey.
     * @param pSpec the NTRU Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec ntru(final GordianNTRUSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.NTRU, pSpec);
    }

    /**
     * Create NTRUPRIMEKey.
     * @param pSpec the NTRUPRIME Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec ntruprime(final GordianNTRUPrimeSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.NTRUPRIME, pSpec);
    }

    /**
     * Create FalconKey.
     * @param pSpec the FALCON Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec falcon(final GordianFALCONSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.FALCON, pSpec);
    }

    /**
     * Create PicnicKey.
     * @param pSpec the Picnic Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec picnic(final GordianPICNICSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.PICNIC, pSpec);
    }

    /**
     * Create RainbowKey.
     * @param pSpec the Rainbow Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec rainbow(final GordianRainbowSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.RAINBOW, pSpec);
    }

    /**
     * Create CompositeKey.
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    public static GordianKeyPairSpec composite(final GordianKeyPairSpec... pSpecs) {
        return composite(Arrays.asList(pSpecs));
    }

    /**
     * Create CompositeKey.
     * @param pSpecs the list of keySpecs
     * @return the KeySpec
     */
    public static GordianKeyPairSpec composite(final List<GordianKeyPairSpec> pSpecs) {
        return new GordianKeyPairSpec(GordianKeyPairType.COMPOSITE, pSpecs);
    }
}
