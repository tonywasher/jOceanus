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
        theAsymKeyType = pAsymKeyType;
        theAgreementType = pAgreementType;
        theKDFType = pKDFType;
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
     * Create the dhMQV agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec dhMQV(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.DH, GordianAgreementType.MQV, pKDFType);
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
     * Create the sm2 agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.SM2, GordianAgreementType.SM2, pKDFType);
    }

    /**
     * Create the x25519Basic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhAnon(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the x25519Basic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhBasic(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the x25519Basic agreementSpec.
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec xdhUnified(final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(GordianAsymKeyType.XDH, GordianAgreementType.UNIFIED, pKDFType);
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
     * @return the kdfype
     */
    public GordianKDFType getKDFType() {
        return theKDFType;
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
        return theAsymKeyType != null && theAgreementType != null && theKDFType != null;
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
                && theKDFType == myThat.getKDFType();
    }

    @Override
    public int hashCode() {
        int hashCode = theAsymKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theAgreementType.hashCode();
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
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.MQV));
                break;
            case EC:
            case GOST2012:
            case DSTU4145:
            case DH:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.MQV));
                break;
            case XDH:
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.ANON));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(pKeyType, GordianAgreementType.UNIFIED));
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
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Loop through the KDFs */
        for (final GordianKDFType myKDF : GordianKDFType.values()) {
            myAgreements.add(new GordianAgreementSpec(pAsymKeyType, pAgreementType, myKDF));
        }

        /* Return the list */
        return myAgreements;
    }
}

