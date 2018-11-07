/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Asymmetric Key Specification.
 */
public final class GordianAsymKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The AsymKeyType.
     */
    private final GordianAsymKeyType theKeyType;

    /**
     * The SubKeyType.
     */
    private final Object theSubKeyType;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pSubKeyType the subKeyType
     */
    private GordianAsymKeySpec(final GordianAsymKeyType pKeyType,
                               final Object pSubKeyType) {
        theKeyType = pKeyType;
        theSubKeyType = pSubKeyType;
    }

    /**
     * Create RSAKey.
     * @param pModulus the modulus
     * @return the KeySpec
     */
    public static GordianAsymKeySpec rsa(final GordianRSAModulus pModulus) {
        return new GordianAsymKeySpec(GordianAsymKeyType.RSA, pModulus);
    }

    /**
     * Create ECKey.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianAsymKeySpec ec(final GordianDSAElliptic pCurve) {
        return new GordianAsymKeySpec(GordianAsymKeyType.EC, pCurve);
    }

    /**
     * Create SM2Key.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianAsymKeySpec sm2(final GordianSM2Elliptic pCurve) {
        return new GordianAsymKeySpec(GordianAsymKeyType.SM2, pCurve);
    }

    /**
     * Create DSTU4145Key.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianAsymKeySpec dstu4145(final GordianDSTU4145Elliptic pCurve) {
        return new GordianAsymKeySpec(GordianAsymKeyType.DSTU4145, pCurve);
    }

    /**
     * Create GOST2012Key.
     * @param pCurve the curve
     * @return the KeySpec
     */
    public static GordianAsymKeySpec gost2012(final GordianGOSTElliptic pCurve) {
        return new GordianAsymKeySpec(GordianAsymKeyType.GOST2012, pCurve);
    }

    /**
     * Create DSAKey.
     * @param pKeyType the keyType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec dsa(final GordianDSAKeyType pKeyType) {
        return new GordianAsymKeySpec(GordianAsymKeyType.DSA, pKeyType);
    }

    /**
     * Create DHKey.
     * @param pGroup the group
     * @return the KeySpec
     */
    public static GordianAsymKeySpec dh(final GordianDHGroup pGroup) {
        return new GordianAsymKeySpec(GordianAsymKeyType.DIFFIEHELLMAN, pGroup);
    }

    /**
     * Create EdDSA25519 Key.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec x25519() {
        return new GordianAsymKeySpec(GordianAsymKeyType.X25519, null);
    }

    /**
     * Create EdX448 Key.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec x448() {
        return new GordianAsymKeySpec(GordianAsymKeyType.X448, null);
    }

    /**
     * Create EdDSA25519 Key.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec ed25519() {
        return new GordianAsymKeySpec(GordianAsymKeyType.ED25519, null);
    }

    /**
     * Create EdDSA448 Key.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec ed448() {
        return new GordianAsymKeySpec(GordianAsymKeyType.ED448, null);
    }

    /**
     * Create SPHINCSKey.
     * @param pKeyType the SPHINCS keyType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec sphincs(final GordianSPHINCSKeyType pKeyType) {
        return new GordianAsymKeySpec(GordianAsymKeyType.SPHINCS, pKeyType);
    }

    /**
     * Create McElieceKey.
     * @param pKeySpec the McEliece keySpec
     * @return the KeySpec
     */
    public static GordianAsymKeySpec mcEliece(final GordianMcElieceKeySpec pKeySpec) {
        return new GordianAsymKeySpec(GordianAsymKeyType.MCELIECE, pKeySpec);
    }

    /**
     * Create RainbowKey.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec rainbow() {
        return new GordianAsymKeySpec(GordianAsymKeyType.RAINBOW, null);
    }

    /**
     * Create NewHopeKey.
     * @return the KeySpec
     */
    public static GordianAsymKeySpec newHope() {
        return new GordianAsymKeySpec(GordianAsymKeyType.NEWHOPE, null);
    }

    /**
     * Create xmssKey.
     * @param pKeySpec the xmss keyType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec xmss(final GordianXMSSKeyType pKeySpec) {
        return new GordianAsymKeySpec(GordianAsymKeyType.XMSS, pKeySpec);
    }

    /**
     * Create xmssMTKey.
     * @param pKeySpec the xmssMT keyType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec xmssmt(final GordianXMSSKeyType pKeySpec) {
        return new GordianAsymKeySpec(GordianAsymKeyType.XMSSMT, pKeySpec);
    }

    /**
     * Create qTESLAKey.
     * @param pKeySpec the keySpec
     * @return the KeySpec
     */
    public static GordianAsymKeySpec qTESLA(final GordianQTESLAKeyType pKeySpec) {
        return new GordianAsymKeySpec(GordianAsymKeyType.QTESLA, pKeySpec);
    }

    /**
     * Obtain the keyType.
     * @return the keyType.
     */
    public GordianAsymKeyType getKeyType() {
        return theKeyType;
    }

    /**
     * Obtain the subKeyType.
     * @return the keyType.
     */
    public Object getSubKeyType() {
        return theSubKeyType;
    }

    /**
     * Obtain the RSAmodulus.
     * @return the modulus.
     */
    public GordianRSAModulus getModulus() {
        return theSubKeyType instanceof GordianRSAModulus
                                                       ? (GordianRSAModulus) theSubKeyType
                                                       : null;
    }

    /**
     * Obtain the DSA keyType.
     * @return the keyType.
     */
    public GordianDSAKeyType getDSAKeyType() {
        return theSubKeyType instanceof GordianDSAKeyType
                                                          ? (GordianDSAKeyType) theSubKeyType
                                                          : null;
    }

    /**
     * Obtain the DSA keyType.
     * @return the keyType.
     */
    public GordianDHGroup getDHGroup() {
        return theSubKeyType instanceof GordianDHGroup
               ? (GordianDHGroup) theSubKeyType
               : null;
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianElliptic getElliptic() {
        return theSubKeyType instanceof GordianElliptic
                                                        ? (GordianElliptic) theSubKeyType
                                                        : null;
    }

    /**
     * Obtain the SPHINCS keyType.
     * @return the keyType.
     */
    public GordianSPHINCSKeyType getSPHINCSType() {
        return theSubKeyType instanceof GordianSPHINCSKeyType
                                                              ? (GordianSPHINCSKeyType) theSubKeyType
                                                              : null;
    }

    /**
     * Obtain the mcEliece keySpec.
     * @return the keySpec.
     */
    public GordianMcElieceKeySpec getMcElieceSpec() {
        return theSubKeyType instanceof GordianMcElieceKeySpec
                                                               ? (GordianMcElieceKeySpec) theSubKeyType
                                                               : null;
    }

    /**
     * Obtain the XMSS keyType.
     * @return the keyType.
     */
    public GordianXMSSKeyType getXMSSKeyType() {
        return theSubKeyType instanceof GordianXMSSKeyType
                                                           ? (GordianXMSSKeyType) theSubKeyType
                                                           : null;
    }

    /**
     * Obtain the qTESLA keyType.
     * @return the keyType.
     */
    public GordianQTESLAKeyType getQTESLAKeyType() {
        return theSubKeyType instanceof GordianQTESLAKeyType
               ? (GordianQTESLAKeyType) theSubKeyType
               : null;
    }

    /**
     * Obtain the qTESLA category.
     * @return the category.
     */
    public int getQTESLACategory() {
        final GordianQTESLAKeyType myKeyType = getQTESLAKeyType();
        return myKeyType != null
               ? myKeyType.getCategory()
               : null;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theKeyType.toString();
            if (theSubKeyType != null) {
                theName += SEP + theSubKeyType.toString();
            }
        }

        /* return the name */
        return theName;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is an AsymSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target KeySpec */
        final GordianAsymKeySpec myThat = (GordianAsymKeySpec) pThat;

        /* Check KeyType */
        if (theKeyType != myThat.getKeyType()) {
            return false;
        }

        /* Match subfields */
        return theSubKeyType == null
               ? myThat.theSubKeyType == null
               : theSubKeyType.equals(myThat.theSubKeyType);
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        if (theSubKeyType != null) {
            hashCode += theSubKeyType.hashCode();
        }
        return hashCode;
    }

    /**
     * Check subKeyType validity.
     * @return valid true/false
     */
    public boolean checkSubKeyType() {
        switch (theKeyType) {
            case RSA:
                return theSubKeyType instanceof GordianRSAModulus;
            case DSA:
                return theSubKeyType instanceof GordianDSAKeyType;
            case DIFFIEHELLMAN:
                return theSubKeyType instanceof GordianDHGroup;
            case EC:
                return theSubKeyType instanceof GordianDSAElliptic;
            case SM2:
                return theSubKeyType instanceof GordianSM2Elliptic;
            case GOST2012:
                return theSubKeyType instanceof GordianGOSTElliptic;
            case DSTU4145:
                return theSubKeyType instanceof GordianDSTU4145Elliptic;
            case SPHINCS:
                return theSubKeyType instanceof GordianSPHINCSKeyType;
            case MCELIECE:
                return theSubKeyType instanceof GordianMcElieceKeySpec;
            case XMSS:
            case XMSSMT:
                return theSubKeyType instanceof GordianXMSSKeyType;
            case QTESLA:
                return theSubKeyType instanceof GordianQTESLAKeyType;
            case ED25519:
            case ED448:
            case X25519:
            case X448:
            case RAINBOW:
            case NEWHOPE:
                return theSubKeyType == null;
            default:
                throw new IllegalArgumentException();
        }
    }
}
