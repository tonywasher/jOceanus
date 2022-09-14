/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
import java.util.Arrays;
import java.util.Iterator;
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
public class GordianKeyPairSpec {
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
     * Create SPHINCSPlusKey.
     * @param pSpec the SPHINCSPlus Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec sphincsPlus(final GordianSPHINCSPlusSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.SPHINCSPLUS, pSpec);
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
     * Create KYBERKey.
     * @param pSpec the KYBER Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec kyber(final GordianKYBERSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.KYBER, pSpec);
    }

    /**
     * Create DILITHIUMKey.
     * @param pSpec the DILITHIUM Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec dilithium(final GordianDILITHIUMSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.DILITHIUM, pSpec);
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
     * Create NTRULPRIMEKey.
     * @param pSpec the NTRULPRIME Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec ntrulprime(final GordianNTRULPrimeSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.NTRULPRIME, pSpec);
    }

    /**
     * Create SNTRUPRIMEKey.
     * @param pSpec the SNTRUPRIME Spec
     * @return the KeySpec
     */
    public static GordianKeyPairSpec sntruprime(final GordianSNTRUPrimeSpec pSpec) {
        return new GordianKeyPairSpec(GordianKeyPairType.SNTRUPRIME, pSpec);
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
        if (!(theSubKeyType instanceof GordianRSAModulus)) {
            throw new IllegalArgumentException();
        }
        return (GordianRSAModulus) theSubKeyType;
    }

    /**
     * Obtain the DSA keyType.
     * @return the keyType.
     */
    public GordianDSAKeyType getDSAKeyType() {
        if (!(theSubKeyType instanceof GordianDSAKeyType)) {
            throw new IllegalArgumentException();
        }
        return (GordianDSAKeyType) theSubKeyType;
    }

    /**
     * Obtain the DH Group.
     * @return the dhGroup.
     */
    public GordianDHGroup getDHGroup() {
        if (!(theSubKeyType instanceof GordianDHGroup)) {
            throw new IllegalArgumentException();
        }
        return (GordianDHGroup) theSubKeyType;
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianElliptic getElliptic() {
        if (!(theSubKeyType instanceof GordianElliptic)) {
            throw new IllegalArgumentException();
        }
        return (GordianElliptic) theSubKeyType;
    }

    /**
     * Obtain the elliptic curve.
     * @return the curve.
     */
    public GordianEdwardsElliptic getEdwardsElliptic() {
        if (!(theSubKeyType instanceof GordianEdwardsElliptic)) {
            throw new IllegalArgumentException();
        }
        return (GordianEdwardsElliptic) theSubKeyType;
    }

    /**
     * Obtain the SPHINCS digestType.
     * @return the digestType.
     */
    public GordianSPHINCSDigestType getSPHINCSDigestType() {
        if (!(theSubKeyType instanceof GordianSPHINCSDigestType)) {
            throw new IllegalArgumentException();
        }
        return (GordianSPHINCSDigestType) theSubKeyType;
    }

    /**
     * Obtain the mcEliece keySpec.
     * @return the keySpec.
     */
    public GordianMcElieceKeySpec getMcElieceKeySpec() {
        if (!(theSubKeyType instanceof GordianMcElieceKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianMcElieceKeySpec) theSubKeyType;
    }

    /**
     * Obtain the lms keySpec.
     * @return the keySpec.
     */
    public GordianLMSKeySpec getLMSKeySpec() {
        if (!(theSubKeyType instanceof GordianLMSKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianLMSKeySpec) theSubKeyType;
    }

    /**
     * Obtain the hss keySpec.
     * @return the keySpec.
     */
    public GordianHSSKeySpec getHSSKeySpec() {
        if (!(theSubKeyType instanceof GordianHSSKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianHSSKeySpec) theSubKeyType;
    }

    /**
     * Obtain the XMSS keySpec.
     * @return the keySpec.
     */
    public GordianXMSSKeySpec getXMSSKeySpec() {
        if (!(theSubKeyType instanceof GordianXMSSKeySpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianXMSSKeySpec) theSubKeyType;
    }

    /**
     * Obtain the XMSS digestType.
     * @return the digestType.
     */
    public GordianXMSSDigestType getXMSSDigestType() {
        return getXMSSKeySpec().getDigestType();
    }

    /**
     * Obtain the SPHINCSPlus keySpec.
     * @return the keySpec.
     */
    public GordianSPHINCSPlusSpec getSPHINCSPlusKeySpec() {
        if (!(theSubKeyType instanceof GordianSPHINCSPlusSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianSPHINCSPlusSpec) theSubKeyType;
    }

    /**
     * Obtain the CMCE keySpec.
     * @return the keySpec.
     */
    public GordianCMCESpec getCMCEKeySpec() {
        if (!(theSubKeyType instanceof GordianCMCESpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianCMCESpec) theSubKeyType;
    }

    /**
     * Obtain the FRODO keySpec.
     * @return the keySpec.
     */
    public GordianFRODOSpec getFRODOKeySpec() {
        if (!(theSubKeyType instanceof GordianFRODOSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianFRODOSpec) theSubKeyType;
    }

    /**
     * Obtain the Saber keySpec.
     * @return the keySpec.
     */
    public GordianSABERSpec getSABERKeySpec() {
        if (!(theSubKeyType instanceof GordianSABERSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianSABERSpec) theSubKeyType;
    }

    /**
     * Obtain the Kyber keySpec.
     * @return the keySpec.
     */
    public GordianKYBERSpec getKyberKeySpec() {
        if (!(theSubKeyType instanceof GordianKYBERSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianKYBERSpec) theSubKeyType;
    }

    /**
     * Obtain the Bike keySpec.
     * @return the keySpec.
     */
    public GordianDILITHIUMSpec getDilithiumKeySpec() {
        if (!(theSubKeyType instanceof GordianDILITHIUMSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianDILITHIUMSpec) theSubKeyType;
    }

    /**
     * Obtain the Bike keySpec.
     * @return the keySpec.
     */
    public GordianBIKESpec getBIKEKeySpec() {
        if (!(theSubKeyType instanceof GordianBIKESpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianBIKESpec) theSubKeyType;
    }

    /**
     * Obtain the NTRU keySpec.
     * @return the keySpec.
     */
    public GordianNTRUSpec getNTRUKeySpec() {
        if (!(theSubKeyType instanceof GordianNTRUSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianNTRUSpec) theSubKeyType;
    }

    /**
     * Obtain the NTRUPRIME keySpec.
     * @return the keySpec.
     */
    public GordianNTRULPrimeSpec getNTRULPrimeKeySpec() {
        if (!(theSubKeyType instanceof GordianNTRULPrimeSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianNTRULPrimeSpec) theSubKeyType;
    }

    /**
     * Obtain the NTRUPRIME keySpec.
     * @return the keySpec.
     */
    public GordianSNTRUPrimeSpec getSNTRUPrimeKeySpec() {
        if (!(theSubKeyType instanceof GordianSNTRUPrimeSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianSNTRUPrimeSpec) theSubKeyType;
    }

    /**
     * Obtain the Falcon keySpec.
     * @return the keySpec.
     */
    public GordianFALCONSpec getFalconKeySpec() {
        if (!(theSubKeyType instanceof GordianFALCONSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianFALCONSpec) theSubKeyType;
    }

    /**
     * Obtain the Picnic keySpec.
     * @return the keySpec.
     */
    public GordianPICNICSpec getPicnicKeySpec() {
        if (!(theSubKeyType instanceof GordianPICNICSpec)) {
            throw new IllegalArgumentException();
        }
        return (GordianPICNICSpec) theSubKeyType;
    }

    /**
     * Obtain the composite keySpec iterator.
     * @return the keySpec iterator.
     */
    @SuppressWarnings("unchecked")
    public Iterator<GordianKeyPairSpec> keySpecIterator() {
        if (!(theSubKeyType instanceof List)) {
            throw new IllegalArgumentException();
        }
        return ((List<GordianKeyPairSpec>) theSubKeyType).iterator();
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
                case COMPOSITE:
                    final Iterator<GordianKeyPairSpec> myIterator = keySpecIterator();
                    final StringBuilder myBuilder = new StringBuilder(theName);
                    while (myIterator.hasNext()) {
                        myBuilder.append(SEP).append(myIterator.next().toString());
                    }
                    theName = myBuilder.toString();
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
            case SPHINCSPLUS:
                return theSubKeyType instanceof GordianSPHINCSPlusSpec;
            case CMCE:
                return theSubKeyType instanceof GordianCMCESpec;
            case FRODO:
                return theSubKeyType instanceof GordianFRODOSpec;
            case SABER:
                return theSubKeyType instanceof GordianSABERSpec;
            case KYBER:
                return theSubKeyType instanceof GordianKYBERSpec;
            case DILITHIUM:
                return theSubKeyType instanceof GordianDILITHIUMSpec;
            case BIKE:
                return theSubKeyType instanceof GordianBIKESpec;
            case NTRU:
                return theSubKeyType instanceof GordianNTRUSpec;
            case NTRULPRIME:
                return theSubKeyType instanceof GordianNTRULPrimeSpec;
            case SNTRUPRIME:
                return theSubKeyType instanceof GordianSNTRUPrimeSpec;
            case FALCON:
                return theSubKeyType instanceof GordianFALCONSpec;
            case PICNIC:
                return theSubKeyType instanceof GordianPICNICSpec;
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
            case COMPOSITE:
                return theSubKeyType instanceof List && checkComposite();
            default:
                return false;
        }
    }

    /**
     * Check composite spec validity.
     * @return valid true/false
     */
    private boolean checkComposite() {
        Boolean stateAware = null;
        final List<GordianKeyPairType> myExisting = new ArrayList<>();
        final Iterator<GordianKeyPairSpec> myIterator = keySpecIterator();
        while (myIterator.hasNext()) {
            /* Check that we have not got a null */
            final GordianKeyPairSpec mySpec = myIterator.next();
            if (mySpec == null) {
                return false;
            }

            /* Check that we have not got a duplicate or COMPOSITE */
            final GordianKeyPairType myType = mySpec.getKeyPairType();
            if (myExisting.contains(myType) || myType == GordianKeyPairType.COMPOSITE) {
                return false;
            }

            /* Check that stateAwareness is identical */
            if (stateAware == null) {
                stateAware = mySpec.isStateAware();
            } else if (mySpec.isStateAware() != stateAware) {
                return false;
            }

            /* Add to list */
            myExisting.add(myType);
        }

        /* Make sure there are at least two */
        return myExisting.size() > 1;
    }

    /**
     * is the use subType for signatures?
     * @return true/false
     */
    public boolean isStateAware() {
        switch (theKeyPairType) {
            case XMSS:
            case LMS:
                return true;
            case COMPOSITE:
                return keySpecIterator().next().isStateAware();
            default:
                return false;
        }
    }
}
