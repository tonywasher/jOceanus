/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keypair;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianHSSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSMTLayers;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Asymmetric KeyPair Specification.
 */
public final class GordianKeyPairSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The keyPairType.
     */
    private final GordianKeyPairType theKeyPairType;

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
    public GordianKeyPairSpec(final GordianKeyPairType pKeyType,
                              final Object pSubKeyType) {
        theKeyPairType = pKeyType;
        theSubKeyType = pSubKeyType;
        isValid = checkValidity();
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
     * Create SPHINCSKey.
     * @param pDigestType the SPHINCS digestType
     * @return the KeySpec
     */
    public static GordianKeyPairSpec sphincs(final GordianSPHINCSDigestType pDigestType) {
        return new GordianKeyPairSpec(GordianKeyPairType.SPHINCS, pDigestType);
    }

    /**
     * Create McElieceKey.
     * @param pKeySpec the McEliece keySpec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec mcEliece(final GordianMcElieceKeySpec pKeySpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.MCELIECE, pKeySpec);
    }

    /**
     * Create RainbowKey.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec rainbow() {
        return new GordianKeyPairSpec(GordianKeyPairType.RAINBOW, null);
    }

    /**
     * Create NewHopeKey.
     * @return the KeySpec
     */
    public static GordianKeyPairSpec newHope() {
        return new GordianKeyPairSpec(GordianKeyPairType.NEWHOPE, null);
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
     * Create qTESLAKey.
     * @param pKeyType the keyType
     * @return the KeySpec
     */
    public static GordianKeyPairSpec qTESLA(final GordianQTESLAKeyType pKeyType) {
        return new GordianKeyPairSpec(GordianKeyPairType.QTESLA, pKeyType);
    }

    /**
     * Create lmsKey.
     * @param pKeySpec the keySpec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec lms(final GordianLMSKeySpec pKeySpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.LMS, pKeySpec);
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
     * Obtain the keyPairType.
     * @return the keyPairType.
     */
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType;
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
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianEdwardsElliptic getEdwardsElliptic() {
        return theSubKeyType instanceof GordianEdwardsElliptic
               ? (GordianEdwardsElliptic) theSubKeyType
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
                /* Derive the name */
                deriveName();
            }  else {
                /* Report invalid spec */
                theName = "InvalidKeyPairSpec: " + theKeyPairType + ":" + theSubKeyType;
            }
        }

        /* return the name */
        return theName;
    }

    /**
     * Derive name.
     */
    private void deriveName() {
        /* Load the name */
        theName = theKeyPairType.toString();
        if (theSubKeyType != null) {
            switch (theKeyPairType) {
                case XMSS:
                    theName = theSubKeyType.toString();
                    break;
                case EDDSA:
                    theName = "Ed" + ((GordianEdwardsElliptic) theSubKeyType).getSuffix();
                    break;
                case XDH:
                    theName = "X" + ((GordianEdwardsElliptic) theSubKeyType).getSuffix();
                    break;
                default:
                    theName += SEP + theSubKeyType.toString();
                    break;
            }
        }
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

        /* Make sure that the object is a keyPairSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target KeySpec */
        final GordianKeyPairSpec myThat = (GordianKeyPairSpec) pThat;

        /* Check KeyPairType */
        if (theKeyPairType != myThat.getKeyPairType()) {
            return false;
        }

        /* Match subfields */
        return Objects.equals(theSubKeyType, myThat.theSubKeyType);
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyPairType.hashCode() << TethysDataConverter.BYTE_SHIFT;
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
        /* Handle null keyPairType */
        if (theKeyPairType == null) {
            return false;
        }

        /* Switch on keyPairType */
        switch (theKeyPairType) {
            case RSA:
                return theSubKeyType instanceof GordianRSAModulus;
            case DSA:
                return theSubKeyType instanceof GordianDSAKeyType;
            case DH:
            case ELGAMAL:
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
                         && ((GordianLMSKeySpec) theSubKeyType).isValid())
                        || (theSubKeyType instanceof GordianHSSKeySpec
                            && ((GordianHSSKeySpec) theSubKeyType).isValid());
            case EDDSA:
            case XDH:
                return theSubKeyType instanceof GordianEdwardsElliptic;
            case RAINBOW:
            case NEWHOPE:
                return theSubKeyType == null;
            default:
                return false;
        }
    }

    /**
     * Obtain a list of all possible keyPairSpecs.
     * @return the list
     */
    public static List<GordianKeyPairSpec> listPossibleKeySpecs() {
        /* Create the list */
        final List<GordianKeyPairSpec> mySpecs = new ArrayList<>();

        /* Add RSA */
        EnumSet.allOf(GordianRSAModulus.class).forEach(m -> mySpecs.add(GordianKeyPairSpec.rsa(m)));

        /* Add DSA */
        EnumSet.allOf(GordianDSAKeyType.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.dsa(t)));

        /* Add DH  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianKeyPairSpec.dh(g)));

        /* Add ElGamal  */
        EnumSet.allOf(GordianDHGroup.class).forEach(g -> mySpecs.add(GordianKeyPairSpec.elGamal(g)));

        /* Add EC */
        EnumSet.allOf(GordianDSAElliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.ec(c)));

        /* Add SM2 */
        EnumSet.allOf(GordianSM2Elliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.sm2(c)));

        /* Add GOST2012 */
        EnumSet.allOf(GordianGOSTElliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.gost2012(c)));

        /* Add DSTU4145 */
        EnumSet.allOf(GordianDSTU4145Elliptic.class).forEach(c -> mySpecs.add(GordianKeyPairSpec.dstu4145(c)));

        /* Add Ed25519/Ed448 */
        mySpecs.add(GordianKeyPairSpec.ed448());
        mySpecs.add(GordianKeyPairSpec.ed25519());

        /* Add X25519/X448 */
        mySpecs.add(GordianKeyPairSpec.x448());
        mySpecs.add(GordianKeyPairSpec.x25519());

        /* Add Rainbow */
        mySpecs.add(GordianKeyPairSpec.rainbow());

        /* Add NewHope */
        mySpecs.add(GordianKeyPairSpec.newHope());

        /* Add qTESLA */
        EnumSet.allOf(GordianQTESLAKeyType.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.qTESLA(t)));

        /* Add SPHINCS */
        EnumSet.allOf(GordianSPHINCSDigestType.class).forEach(t -> mySpecs.add(GordianKeyPairSpec.sphincs(t)));

        /* Add XMSS/XMSSMT */
        GordianXMSSKeySpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(new GordianKeyPairSpec(GordianKeyPairType.XMSS, t)));

        /* Add McEliece */
        GordianMcElieceKeySpec.listPossibleKeySpecs().forEach(t -> mySpecs.add(GordianKeyPairSpec.mcEliece(t)));

        /* Add LMS */
        GordianLMSKeySpec.listPossibleKeySpecs().forEach(t -> {
            mySpecs.add(GordianKeyPairSpec.lms(t));
            for (int i = 2; i < GordianHSSKeySpec.MAX_DEPTH; i++) {
                mySpecs.add(GordianKeyPairSpec.hss(t, i));
            }
        });

        /* Return the list */
        return mySpecs;
    }
}
