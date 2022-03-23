/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * KeyPair Agreement Specification.
 */
public final class GordianKeyPairAgreementSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * KeyPairType.
     */
    private final GordianKeyPairType theKeyPairType;

    /**
     * AgreementType.
     */
    private final GordianAgreementType theAgreementType;

    /**
     * KDFType.
     */
    private final GordianKDFType theKDFType;

    /**
     * With Confirmation?.
     */
    private final Boolean withConfirm;

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
     * @param pKeyPairType the keyPairType
     * @param pAgreementType the agreement type
     * @param pKDFType the KDF type
     */
    public GordianKeyPairAgreementSpec(final GordianKeyPairType pKeyPairType,
                                       final GordianAgreementType pAgreementType,
                                       final GordianKDFType pKDFType) {
        this(pKeyPairType, pAgreementType, pKDFType, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pKeyPairType the keyPairType
     * @param pAgreementType the agreement type
     * @param pKDFType the KDF type
     * @param pConfirm with key confirmation
     */
    public GordianKeyPairAgreementSpec(final GordianKeyPairType pKeyPairType,
                                       final GordianAgreementType pAgreementType,
                                       final GordianKDFType pKDFType,
                                       final Boolean pConfirm) {
        theKeyPairType = pKeyPairType;
        theAgreementType = pAgreementType;
        theKDFType = pKDFType;
        withConfirm = pConfirm;
        isValid = checkValidity();
    }

    /**
     * Create the rsa agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec rsaKEM(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.RSA, GordianAgreementType.KEM, pKDFType);
    }

    /**
     * Create the dhANON agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhAnon(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.DH, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the dhBasic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhBasic(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.DH, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the dhSigned agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhSigned(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.DH, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the dhMQV agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhMQV(final GordianKDFType pKDFType) {
        return dhMQVConfirm(pKDFType, Boolean.FALSE);
    }

    /**
     * Create the dhMQVConfirm agreementSpec.
     * @param pKDFType the KDF type
     * @param pConfirm confirm message needed?
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhMQVConfirm(final GordianKDFType pKDFType,
                                                           final Boolean pConfirm) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.DH, GordianAgreementType.MQV, pKDFType, pConfirm);
    }

    /**
     * Create the dhUnified agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhUnified(final GordianKDFType pKDFType) {
        return dhUnifiedConfirm(pKDFType, Boolean.FALSE);
    }

    /**
     * Create the dhUnifiedConfirm agreementSpec.
     * @param pKDFType the KDF type
     * @param pConfirm confirm message needed?
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec dhUnifiedConfirm(final GordianKDFType pKDFType,
                                                               final Boolean pConfirm) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.DH, GordianAgreementType.UNIFIED, pKDFType, pConfirm);
    }

    /**
     * Create the ecIES agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecIES(final GordianKeyPairType pKeyPairType,
                                                    final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(pKeyPairType, GordianAgreementType.KEM, pKDFType);
    }

    /**
     * Create the ecdhANON agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhAnon(final GordianKeyPairType pKeyPairType,
                                                       final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(pKeyPairType, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the ecdhBasic agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhBasic(final GordianKeyPairType pKeyPairType,
                                                        final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(pKeyPairType, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the ecdhSigned agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhSigned(final GordianKeyPairType pKeyPairType,
                                                         final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(pKeyPairType, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the ecdhMQV agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhMQV(final GordianKeyPairType pKeyPairType,
                                                      final GordianKDFType pKDFType) {
        return ecdhMQVConfirm(pKeyPairType, pKDFType, Boolean.FALSE);
    }

    /**
     * Create the ecdhMQVConfirm agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @param pConfirm confirm message needed?
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhMQVConfirm(final GordianKeyPairType pKeyPairType,
                                                             final GordianKDFType pKDFType,
                                                             final Boolean pConfirm) {
        return new GordianKeyPairAgreementSpec(pKeyPairType, GordianAgreementType.MQV, pKDFType, pConfirm);
    }

    /**
     * Create the ecdhUnified agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhUnified(final GordianKeyPairType pKeyPairType,
                                                          final GordianKDFType pKDFType) {
        return ecdhUnifiedConfirm(pKeyPairType, pKDFType, Boolean.FALSE);
    }

    /**
     * Create the ecdhUnifiedConfirm agreementSpec.
     * @param pKeyPairType the keyPairType
     * @param pKDFType the KDF type
     * @param pConfirm confirm message needed?
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec ecdhUnifiedConfirm(final GordianKeyPairType pKeyPairType,
                                                                 final GordianKDFType pKDFType,
                                                                 final Boolean pConfirm) {
        return new GordianKeyPairAgreementSpec(pKeyPairType, GordianAgreementType.UNIFIED, pKDFType, pConfirm);
    }

    /**
     * Create the sm2 agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec sm2(final GordianKDFType pKDFType) {
        return sm2Confirm(pKDFType, Boolean.FALSE);
    }

    /**
     * Create the sm2 agreementSpec.
     * @param pKDFType the KDF type
     * @param pConfirm confirm message needed?
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec sm2Confirm(final GordianKDFType pKDFType,
                                                         final Boolean pConfirm) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.SM2, GordianAgreementType.SM2, pKDFType, pConfirm);
    }

    /**
     * Create the xdhAnonymous agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec xdhAnon(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.XDH, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the xdhBasic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec xdhBasic(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.XDH, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the xdhSigned agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec xdhSigned(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.XDH, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the xdhUnified agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec xdhUnified(final GordianKDFType pKDFType) {
        return xdhUnifiedConfirm(pKDFType, Boolean.FALSE);
    }

    /**
     * Create the xdhUnifiedConfirm agreementSpec.
     * @param pKDFType the KDF type
     * @param pConfirm confirm message needed?
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec xdhUnifiedConfirm(final GordianKDFType pKDFType,
                                                                final Boolean pConfirm) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.XDH, GordianAgreementType.UNIFIED, pKDFType, pConfirm);
    }

    /**
     * Create the newHope agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec newHope(final GordianKDFType pKDFType) {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.NEWHOPE, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the cmce agreementSpec.
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec cmce() {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.CMCE, GordianAgreementType.KEM, GordianKDFType.NONE);
    }

    /**
     * Create the frodo agreementSpec.
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec frodo() {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.FRODO, GordianAgreementType.KEM, GordianKDFType.NONE);
    }

    /**
     * Create the saber agreementSpec.
     * @return the Spec
     */
    public static GordianKeyPairAgreementSpec saber() {
        return new GordianKeyPairAgreementSpec(GordianKeyPairType.SABER, GordianAgreementType.KEM, GordianKDFType.NONE);
    }

    /**
     * Create default signatureSpec for key.
     * @param pKeySpec the keySpec
     * @return the SignatureSpec
     */
    public static GordianKeyPairAgreementSpec defaultForKey(final GordianKeyPairSpec pKeySpec) {
        final GordianKeyPairType myType = pKeySpec.getKeyPairType();
        switch (myType) {
            case DH:
                return GordianKeyPairAgreementSpec.dhAnon(GordianKDFType.SHA256KDF);
            case XDH:
                return pKeySpec.getEdwardsElliptic().is25519()
                        ? GordianKeyPairAgreementSpec.xdhAnon(GordianKDFType.SHA256KDF)
                        : GordianKeyPairAgreementSpec.xdhAnon(GordianKDFType.SHA512KDF);
            case NEWHOPE:
                return GordianKeyPairAgreementSpec.newHope(GordianKDFType.SHA256KDF);
            case CMCE:
                return GordianKeyPairAgreementSpec.cmce();
            case FRODO:
                return GordianKeyPairAgreementSpec.frodo();
            case SABER:
                return GordianKeyPairAgreementSpec.saber();
            case EC:
            case SM2:
            case GOST2012:
            case DSTU4145:
                return GordianKeyPairAgreementSpec.ecdhAnon(myType, GordianKDFType.SHA256KDF);
            default:
                return null;
        }
    }

    /**
     * Obtain the keyPairType.
     * @return the keyPairType
     */
    public GordianKeyPairType getKeyPairType() {
        return theKeyPairType;
    }

    /**
     * Obtain the agreementType.
     * @return the agreementType
     */
    public GordianAgreementType getAgreementType() {
        return theAgreementType;
    }

    /**
     * Obtain the kdfType.
     * @return the kdfType
     */
    public GordianKDFType getKDFType() {
        return theKDFType;
    }

    /**
     * Is this agreement with key confirmation?
     * @return true/false
     */
    public Boolean withConfirm() {
        return withConfirm;
    }

    /**
     * Is this Agreement supported?
     * @return true/false
     */
    public boolean isSupported() {
        return theAgreementType.isSupported(theKeyPairType) && theKDFType.isSupported(theKeyPairType, theAgreementType);
    }

    /**
     * Is the agreementSpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* All components must be non-null */
        if (theKeyPairType == null
                || theAgreementType == null
                || theKDFType == null
                || withConfirm == null) {
            return false;
        }

        /* Confirmation is restricted to certain agreement types */
        if (Boolean.TRUE.equals(withConfirm)) {
            switch (theAgreementType) {
                case UNIFIED:
                case MQV:
                case SM2:
                    return true;
                default:
                    return false;
            }
        }

        /* Valid */
        return true;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the agreementSpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyPairType.toString()
                        + SEP + theAgreementType;

                /* Add KDF type if present */
                if (GordianKDFType.NONE != theKDFType) {
                    theName += SEP + theKDFType;
                }

                /* Add Confirm if present */
                if (Boolean.TRUE.equals(withConfirm)) {
                    theName += SEP + "CONFIRM";
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidAgreementSpec: " + theKeyPairType + ":" + theAgreementType + ":" + theKDFType;
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

        /* Make sure that the object is an AgreementSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target AgreementSpec */
        final GordianKeyPairAgreementSpec myThat = (GordianKeyPairAgreementSpec) pThat;

        /* Match subfields */
        return theKeyPairType == myThat.getKeyPairType()
                && theAgreementType == myThat.getAgreementType()
                && theKDFType == myThat.getKDFType()
                && withConfirm == myThat.withConfirm();
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyPairType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theAgreementType.hashCode() + (Boolean.TRUE.equals(withConfirm) ? 1 : 0);
        return (hashCode << TethysDataConverter.BYTE_SHIFT) + theKDFType.hashCode();
    }

    /**
     * Obtain a list of all possible agreements for the keyPairType.
     * @param pKeyPairType the keyPairType
     * @return the list
     */
    public static List<GordianKeyPairAgreementSpec> listPossibleAgreements(final GordianKeyPairType pKeyPairType) {
        /* Create list */
        final List<GordianKeyPairAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on keyPairType */
        switch (pKeyPairType) {
            case RSA:
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.KEM));
                break;
            case NEWHOPE:
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.ANON));
                break;
            case CMCE:
                myAgreements.add(cmce());
                break;
            case FRODO:
                myAgreements.add(frodo());
                break;
            case SABER:
                myAgreements.add(saber());
                break;
            case EC:
            case SM2:
            case GOST2012:
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.UNIFIED, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.MQV, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.SM2, Boolean.TRUE));
                break;
            case DH:
            case DSTU4145:
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.UNIFIED, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.MQV, Boolean.TRUE));
                break;
            case XDH:
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyPairType, GordianAgreementType.UNIFIED, Boolean.TRUE));
                break;
            default:
                break;
        }

        /* Return the list */
        return myAgreements;
    }

    /**
     * Create list of KDF variants.
     * @param pKeyPairType the keyPairType
     * @param pAgreementType the agreementType
     * @return the list
     */
    public static List<GordianKeyPairAgreementSpec> listAllKDFs(final GordianKeyPairType pKeyPairType,
                                                                final GordianAgreementType pAgreementType) {
        return listAllKDFs(pKeyPairType, pAgreementType, Boolean.FALSE);
    }

    /**
     * Create list of KDF variants.
     * @param pKeyPairType the keyPairType
     * @param pAgreementType the agreementType
     * @param pConfirm with key confirmation
     * @return the list
     */
    public static List<GordianKeyPairAgreementSpec> listAllKDFs(final GordianKeyPairType pKeyPairType,
                                                                final GordianAgreementType pAgreementType,
                                                                final Boolean pConfirm) {
        /* Create list */
        final List<GordianKeyPairAgreementSpec> myAgreements = new ArrayList<>();

        /* Loop through the KDFs */
        for (final GordianKDFType myKDF : GordianKDFType.values()) {
            myAgreements.add(new GordianKeyPairAgreementSpec(pKeyPairType, pAgreementType, myKDF, pConfirm));
        }

        /* Return the list */
        return myAgreements;
    }
}
