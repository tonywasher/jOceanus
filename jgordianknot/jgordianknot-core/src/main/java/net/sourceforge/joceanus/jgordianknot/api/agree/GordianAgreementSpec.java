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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
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
     * Obtain a list of all possible agreements for the keyPair.
     * @param pKeyPair the keyPair
     * @return the list
     */
    public static List<GordianAgreementSpec> listPossibleAgreements(final GordianKeyPair pKeyPair) {
        /* Create list */
        final List<GordianAgreementSpec> myAgreements = new ArrayList<>();

        /* Switch on AsymKeyType */
        final GordianAsymKeyType myType = pKeyPair.getKeySpec().getKeyType();
        switch (myType) {
            case RSA:
            case NEWHOPE:
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.KEM));
                break;
            case SM2:
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.SM2));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.MQV));
                break;
            case EC:
            case GOST2012:
            case DSTU4145:
            case DIFFIEHELLMAN:
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.KEM));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.UNIFIED));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.MQV));
                break;
            case X25519:
            case X448:
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.BASIC));
                myAgreements.addAll(listAllKDFs(myType, GordianAgreementType.UNIFIED));
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

