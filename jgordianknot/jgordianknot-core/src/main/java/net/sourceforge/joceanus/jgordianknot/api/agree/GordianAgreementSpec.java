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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Key Agreement Specification.
 */
public final class GordianAgreementSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * AsymKeyType.
     */
    private final GordianAsymKeyType theAsymKeyType;

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
     * @param pAsymKeyType the asymKeyType
     * @param pAgreementType the agreement type
     * @param pKDFType the KDF type
     */
    public GordianAgreementSpec(final GordianAsymKeyType pAsymKeyType,
                                final GordianAgreementType pAgreementType,
                                final GordianKDFType pKDFType) {
        this(pAsymKeyType, pAgreementType, pKDFType, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pAgreementType the agreement type
     * @param pKDFType the KDF type
     * @param pConfirm with key confirmation
     */
    public GordianAgreementSpec(final GordianAsymKeyType pAsymKeyType,
                                final GordianAgreementType pAgreementType,
                                final GordianKDFType pKDFType,
                                final Boolean pConfirm) {
        theAsymKeyType = pAsymKeyType;
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
    public static GordianAgreementSpec rsaKEM(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.RSA, GordianAgreementType.KEM, pKDFType);
    }

    /**
     * Create the dhANON agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhAnon(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the dhBasic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhBasic(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the dhSigned agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhSigned(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the dhMQV agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhMQV(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.MQV, pKDFType);
    }

    /**
     * Create the dhMQVConfirm agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhMQVConfirm(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.MQV, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the dhUnified agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhUnified(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.UNIFIED, pKDFType);
    }

    /**
     * Create the dhUnifiedConfirm agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhUnifiedConfirm(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.UNIFIED, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the ecIES agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecIES(final GordianAsymKeyType pKeyType,
                                             final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.KEM, pKDFType);
    }

    /**
     * Create the ecANON agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecAnon(final GordianAsymKeyType pKeyType,
                                              final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the ecdhBasic agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecdhBasic(final GordianAsymKeyType pKeyType,
                                                 final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the ecdhSigned agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecdhSigned(final GordianAsymKeyType pKeyType,
                                                  final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the ecdhMQV agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecdhMQV(final GordianAsymKeyType pKeyType,
                                               final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.MQV, pKDFType);
    }

    /**
     * Create the ecdhMQVConfirm agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecdhMQVConfirm(final GordianAsymKeyType pKeyType,
                                                      final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.MQV, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the ecdhUnified agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecdhUnified(final GordianAsymKeyType pKeyType,
                                                   final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.UNIFIED, pKDFType);
    }

    /**
     * Create the ecdhUnifiedConfirm agreementSpec.
     * @param pKeyType the asymKeyType
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec ecdhUnifiedConfirm(final GordianAsymKeyType pKeyType,
                                                          final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyType, GordianAgreementType.UNIFIED, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the sm2 agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.SM2, GordianAgreementType.SM2, pKDFType);
    }

    /**
     * Create the sm2 agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2Confirm(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.SM2, GordianAgreementType.SM2, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the xdhAnonymous agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhAnon(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the xdhBasic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhBasic(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the xdhSigned agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhSigned(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the xdhUnified agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhUnified(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.UNIFIED, pKDFType);
    }

    /**
     * Create the xdhUnifiedConfirm agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhUnifiedConfirm(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.UNIFIED, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the newHope agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec newHope(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.NEWHOPE, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Obtain the asymKeyType.
     * @return the asymKeyType
     */
    public GordianAsymKeyType getAsymKeyType() {
        return theAsymKeyType;
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
        return theAgreementType.isSupported(theAsymKeyType) && theKDFType.isSupported(theAsymKeyType, theAgreementType);
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
        if (theAsymKeyType == null
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
                theName = theAsymKeyType.toString()
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
                theName = "InvalidAgreementSpec: " + theAsymKeyType + ":" + theAgreementType + ":" + theKDFType;
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
        final GordianAgreementSpec myThat = (GordianAgreementSpec) pThat;

        /* Match subfields */
        return theAsymKeyType == myThat.getAsymKeyType()
                && theAgreementType == myThat.getAgreementType()
                && theKDFType == myThat.getKDFType()
                && withConfirm == myThat.withConfirm();
    }

    @Override
    public int hashCode() {
        int hashCode = theAsymKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theAgreementType.hashCode() + (Boolean.TRUE.equals(withConfirm) ? 1 : 0);
        return (hashCode << TethysDataConverter.BYTE_SHIFT) + theKDFType.hashCode();
    }

    /**
     * Obtain a list of all possible agreements for the keyType.
     * @param pKeyType the keyType
     * @return the list
     */
    public static List<GordianAgreementSpec> listPossibleAgreements(final GordianAsymKeyType pKeyType) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on AsymKeyType */
        switch (pKeyType) {
            case RSA:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.KEM));
                break;
            case NEWHOPE:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                break;
            case SM2:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.MQV, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.SM2, Boolean.TRUE));
                break;
            case EC:
            case GOST2012:
            case DSTU4145:
            case DH:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED, Boolean.TRUE));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.MQV));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.MQV, Boolean.TRUE));
                break;
            case XDH:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.SIGNED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED, Boolean.TRUE));
                break;
            default:
                break;
        }

        /* Return the list */
        return myAgreements;
    }

    /**
     * Create list of KDF variants.
     * @param pAsymKeyType the keyType
     * @param pAgreementType the agreementType
     * @return the list
     */
    private static List<GordianAgreementSpec> listAllKDFs(final GordianAsymKeyType pAsymKeyType,
                                                          final GordianAgreementType pAgreementType) {
        return listAllKDFs(pAsymKeyType, pAgreementType, Boolean.FALSE);
    }
    /**
     * Create list of KDF variants.
     * @param pAsymKeyType the keyType
     * @param pAgreementType the agreementType
     * @param pConfirm with key confirmation
     * @return the list
     */
    private static List<GordianAgreementSpec> listAllKDFs(final GordianAsymKeyType pAsymKeyType,
                                                          final GordianAgreementType pAgreementType,
                                                          final Boolean pConfirm) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Loop through the KDFs */
        for (final GordianKDFType myKDF : GordianKDFType.values()) {
            myAgreements.add(new GordianAgreementSpec(pAsymKeyType, pAgreementType, myKDF, pConfirm));
        }

        /* Return the list */
        return myAgreements;
    }
}
