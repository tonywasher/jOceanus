/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.asym;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.asym.GordianXMSSKeySpec.GordianXMSSDigestType;
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
     * The Validity.
     */
    private final boolean isValid;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pKeyType the keyType
     * @param pSubKeyType the subKeyType
     */
    public GordianAsymKeySpec(final GordianAsymKeyType pKeyType,
                              final Object pSubKeyType) {
        theKeyType = pKeyType;
        theSubKeyType = pSubKeyType;
        isValid = checkValidity();
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
        return new GordianAsymKeySpec(GordianAsymKeyType.DH, pGroup);
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
     * @param pDigestType the SPHINCS digestType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec sphincs(final GordianSPHINCSDigestType pDigestType) {
        return new GordianAsymKeySpec(GordianAsymKeyType.SPHINCS, pDigestType);
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
     * @param pDigestType the xmss digestType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec xmss(final GordianXMSSDigestType pDigestType) {
        return new GordianAsymKeySpec(GordianAsymKeyType.XMSS, GordianXMSSKeySpec.xmss(pDigestType));
    }

    /**
     * Create xmssMTKey.
     * @param pDigestType the xmss digestType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec xmssmt(final GordianXMSSDigestType pDigestType) {
        return new GordianAsymKeySpec(GordianAsymKeyType.XMSS, GordianXMSSKeySpec.xmssmt(pDigestType));
    }

    /**
     * Create qTESLAKey.
     * @param pKeyType the keyType
     * @return the KeySpec
     */
    public static GordianAsymKeySpec qTESLA(final GordianQTESLAKeyType pKeyType) {
        return new GordianAsymKeySpec(GordianAsymKeyType.QTESLA, pKeyType);
    }

    /**
     * Create lmsKey.
     * @param pKeySpec the keySpec
     * @return the KeySpec
     */
    public static GordianAsymKeySpec lms(final GordianLMSKeySpec pKeySpec) {
        return new GordianAsymKeySpec(GordianAsymKeyType.LMS, pKeySpec);
    }

    /**
     * Create hssKey.
     * @param pKeySpec the keySpec
     * @return the KeySpec
     */
    public static GordianAsymKeySpec hss(final GordianLMSKeySpec... pKeySpec) {
        return new GordianAsymKeySpec(GordianAsymKeyType.LMS, new GordianHSSKeySpec(pKeySpec));
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
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the RSAmodulus.
     * @return the modulus.
     */
    public GordianRSAModulus getRSAModulus() {
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
     * Obtain the DH Group.
     * @return the dhGroup.
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
     * Obtain the SPHINCS digestType.
     * @return the digestType.
     */
    public GordianSPHINCSDigestType getSPHINCSDigestType() {
        return theSubKeyType instanceof GordianSPHINCSDigestType
               ? (GordianSPHINCSDigestType) theSubKeyType
               : null;
    }

    /**
     * Obtain the mcEliece keySpec.
     * @return the keySpec.
     */
    public GordianMcElieceKeySpec getMcElieceKeySpec() {
        return theSubKeyType instanceof GordianMcElieceKeySpec
               ? (GordianMcElieceKeySpec) theSubKeyType
               : null;
    }

    /**
     * Obtain the lms keySpec.
     * @return the keySpec.
     */
    public GordianLMSKeySpec getLMSKeySpec() {
        return theSubKeyType instanceof GordianLMSKeySpec
               ? (GordianLMSKeySpec) theSubKeyType
               : null;
    }

    /**
     * Obtain the hss keySpec.
     * @return the keySpec.
     */
    public GordianHSSKeySpec getHSSKeySpec() {
        return theSubKeyType instanceof GordianHSSKeySpec
               ? (GordianHSSKeySpec) theSubKeyType
               : null;
    }

    /**
     * Obtain the XMSS keySpec.
     * @return the keySpec.
     */
    public GordianXMSSKeySpec getXMSSKeySpec() {
        return theSubKeyType instanceof GordianXMSSKeySpec
               ? (GordianXMSSKeySpec) theSubKeyType
               : null;
    }

    /**
     * Obtain the XMSS digestType.
     * @return the digestType.
     */
    public GordianXMSSDigestType getXMSSDigestType() {
        return theSubKeyType instanceof GordianXMSSKeySpec
               ? ((GordianXMSSKeySpec) theSubKeyType).getDigestType()
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
        return theSubKeyType instanceof GordianQTESLAKeyType
               ? ((GordianQTESLAKeyType) theSubKeyType).getCategory()
               : -1;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyType.toString();
                if (theSubKeyType != null) {
                    if (theKeyType == GordianAsymKeyType.XMSS) {
                        theName = theSubKeyType.toString();
                    } else {
                        theName += SEP + theSubKeyType.toString();
                    }
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidAsymKeySpec: " + theKeyType + ":" + theSubKeyType;
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
        return Objects.equals(theSubKeyType, myThat.theSubKeyType);
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
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Handle null keyType */
        if (theKeyType == null) {
            return false;
        }

        /* Switch on keyType */
        switch (theKeyType) {
            case RSA:
                return theSubKeyType instanceof GordianRSAModulus;
            case DSA:
                return theSubKeyType instanceof GordianDSAKeyType;
            case DH:
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
                return theSubKeyType instanceof GordianSPHINCSDigestType;
            case MCELIECE:
                return theSubKeyType instanceof GordianMcElieceKeySpec
                        && ((GordianMcElieceKeySpec) theSubKeyType).isValid();
            case XMSS:
                return theSubKeyType instanceof GordianXMSSKeySpec
                        && ((GordianXMSSKeySpec) theSubKeyType).isValid();
            case QTESLA:
                return theSubKeyType instanceof GordianQTESLAKeyType;
            case LMS:
                return (theSubKeyType instanceof GordianLMSKeySpec
                         && ((GordianLMSKeySpec) theSubKeyType).isValid());
            case ED25519:
            case ED448:
            case X25519:
            case X448:
            case RAINBOW:
            case NEWHOPE:
                return theSubKeyType == null;
            default:
                return false;
        }
    }

    /**
     * Obtain a list of all possible asymKeySpecs.
     * @return the list
     */
    public static List<GordianAsymKeySpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianAsymKeySpec> mySpecs = new ArrayList<>();

        /* Add RSA */
        EnumSet.allOf(GordianRSAModulus.class).forEach(m -> mySpecs.add(GordianAsymKeySpec.rsa(m)));

        /* Add DSA */
        EnumSet.allOf(GordianDSAKeyType.class).forEach(t -> mySpecs.add(GordianAsymKeySpec.dsa(t)));

        /* Add DH  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianAsymKeySpec.dh(g)));

        /* Add EC */
        EnumSet.allOf(GordianDSAElliptic.class).forEach(c -> mySpecs.add(GordianAsymKeySpec.ec(c)));

        /* Add SM2 */
        EnumSet.allOf(GordianSM2Elliptic.class).forEach(c -> mySpecs.add(GordianAsymKeySpec.sm2(c)));

        /* Add GOST2012 */
        EnumSet.allOf(GordianGOSTElliptic.class).forEach(c -> mySpecs.add(GordianAsymKeySpec.gost2012(c)));

        /* Add DSTU4145 */
        EnumSet.allOf(GordianDSTU4145Elliptic.class).forEach(c -> mySpecs.add(GordianAsymKeySpec.dstu4145(c)));

        /* Add Ed25519/Ed448 */
        mySpecs.add(GordianAsymKeySpec.ed25519());
        mySpecs.add(GordianAsymKeySpec.ed448());

        /* Add X25519/X448 */
        mySpecs.add(GordianAsymKeySpec.x25519());
        mySpecs.add(GordianAsymKeySpec.x448());

        /* Add Rainbow */
        mySpecs.add(GordianAsymKeySpec.rainbow());

        /* Add NewHope */
        mySpecs.add(GordianAsymKeySpec.newHope());

        /* Add qTESLA */
        EnumSet.allOf(GordianQTESLAKeyType.class).forEach(t -> mySpecs.add(GordianAsymKeySpec.qTESLA(t)));

        /* Add SPHINCS */
        EnumSet.allOf(GordianSPHINCSDigestType.class).forEach(t -> mySpecs.add(GordianAsymKeySpec.sphincs(t)));

        /* Add XMSS/XMSSMT */
        EnumSet.allOf(GordianXMSSDigestType.class).forEach(t -> mySpecs.add(GordianAsymKeySpec.xmss(t)));
        EnumSet.allOf(GordianXMSSDigestType.class).forEach(t -> mySpecs.add(GordianAsymKeySpec.xmssmt(t)));

        /* Add McEliece */
        GordianMcElieceKeySpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(GordianAsymKeySpec.mcEliece(t)));

        /* Add LMS */
        GordianLMSKeySpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(GordianAsymKeySpec.lms(t)));

        /* Return the list */
        return mySpecs;
    }
}
