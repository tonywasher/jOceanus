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
import java.util.EnumSet;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * KeyPairSet Agreement Specification.
 */
public final class GordianKeyPairSetAgreementSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * KeyPairSetSpec.
     */
    private final GordianKeyPairSetSpec theKeyPairSetSpec;

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
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @param pAgreementType the agreement type
     */
    public GordianKeyPairSetAgreementSpec(final GordianKeyPairSetSpec pKeyPairSetSpec,
                                          final GordianAgreementType pAgreementType) {
        this(pKeyPairSetSpec, pAgreementType, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @param pAgreementType the agreement type
     * @param pConfirm with key confirmation
     */
    public GordianKeyPairSetAgreementSpec(final GordianKeyPairSetSpec pKeyPairSetSpec,
                                          final GordianAgreementType pAgreementType,
                                          final Boolean pConfirm) {
        theKeyPairSetSpec = pKeyPairSetSpec;
        theAgreementType = pAgreementType;
        theKDFType = getKDFTypeForKeyPairSetSpec(theKeyPairSetSpec);
        withConfirm = pConfirm;
        isValid = checkValidity();
    }

    /**
     * Create the anonymous agreementSpec.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the Spec
     */
    public static GordianKeyPairSetAgreementSpec anon(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        return new GordianKeyPairSetAgreementSpec(pKeyPairSetSpec, GordianAgreementType.ANON);
    }

    /**
     * Create the signed agreementSpec.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the Spec
     */
    public static GordianKeyPairSetAgreementSpec signed(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        return new GordianKeyPairSetAgreementSpec(pKeyPairSetSpec, GordianAgreementType.SIGNED);
    }

    /**
     * Create the handshake agreementSpec.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the Spec
     */
    public static GordianKeyPairSetAgreementSpec handshake(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        return new GordianKeyPairSetAgreementSpec(pKeyPairSetSpec, GordianAgreementType.UNIFIED);
    }

    /**
     * Create the anonymous agreementSpec.
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @return the Spec
     */
    public static GordianKeyPairSetAgreementSpec confirm(final GordianKeyPairSetSpec pKeyPairSetSpec) {
        return new GordianKeyPairSetAgreementSpec(pKeyPairSetSpec, GordianAgreementType.UNIFIED, Boolean.TRUE);
    }

    /**
     * Obtain the keyPairSetSpec.
     * @return the keyPairSetSpec
     */
    public GordianKeyPairSetSpec getKeyPairSetSpec() {
        return theKeyPairSetSpec;
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
     * Is the agreementSpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the KDF for the KeyPairSetSpec.
     * @param pSpec the keyPairSetSpec
     * @return the KDFType.
     */
    private static GordianKDFType getKDFTypeForKeyPairSetSpec(final GordianKeyPairSetSpec pSpec) {
        return GordianKeyPairSetSpec.AGREEHI.equals(pSpec)
                ? GordianKDFType.SHA512KDF
                : GordianKDFType.SHA256KDF;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* All components must be non-null */
        if (theKeyPairSetSpec == null
                || theAgreementType == null
                || theKDFType == null
                || withConfirm == null) {
            return false;
        }

        /* KeyPairSet must be capable of agreement */
        if (!theKeyPairSetSpec.canAgree()) {
            return false;
        }

        /* Only a restricted set of agreementTypes are allowed */
        switch (theAgreementType) {
            case ANON:
            case SIGNED:
                /* Confirm not allowed */
                return Boolean.FALSE.equals(withConfirm);
            case UNIFIED:
                return true;
            case KEM:
            case BASIC:
            case MQV:
            case SM2:
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the agreementSpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theKeyPairSetSpec.toString()
                        + SEP + theAgreementType;

                /* Add Confirm if present */
                if (Boolean.TRUE.equals(withConfirm)) {
                    theName += SEP + "CONFIRM";
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidAgreementSpec: " + theKeyPairSetSpec + ":" + theAgreementType;
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
        final GordianKeyPairSetAgreementSpec myThat = (GordianKeyPairSetAgreementSpec) pThat;

        /* Match subfields */
        return theKeyPairSetSpec == myThat.getKeyPairSetSpec()
                && theAgreementType == myThat.getAgreementType()
                && withConfirm == myThat.withConfirm();
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyPairSetSpec.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theAgreementType.hashCode();
        return (hashCode << TethysDataConverter.BYTE_SHIFT) + (Boolean.TRUE.equals(withConfirm) ? 1 : 0);
    }

    /**
     * Obtain a list of all possible agreements for a keyPairSetSpec.
     * @return the list
     */
    public static List<GordianKeyPairSetAgreementSpec> listPossibleAgreements() {
        /* Create list */
        final List<GordianKeyPairSetAgreementSpec> myAgreements = new ArrayList<>();

        /* Add possible agreements for each spec */
        EnumSet.allOf(GordianKeyPairSetSpec.class)
                .forEach(s -> myAgreements.addAll(listPossibleAgreements(s)));

        /* Return the list */
        return myAgreements;
    }

    /**
     * Obtain a list of all possible agreements for a keyPairSetSpec.
     * @param pSpec the keyPairSetSpec
     * @return the list
     */
    public static List<GordianKeyPairSetAgreementSpec> listPossibleAgreements(final GordianKeyPairSetSpec pSpec) {
        /* Create list */
        final List<GordianKeyPairSetAgreementSpec> myAgreements = new ArrayList<>();

        /* If the Spec can form agreements */
        if (pSpec.canAgree()) {
            /* Add the agreements */
            myAgreements.add(new GordianKeyPairSetAgreementSpec(pSpec, GordianAgreementType.ANON));
            myAgreements.add(new GordianKeyPairSetAgreementSpec(pSpec, GordianAgreementType.SIGNED));
            myAgreements.add(new GordianKeyPairSetAgreementSpec(pSpec, GordianAgreementType.UNIFIED));
            myAgreements.add(new GordianKeyPairSetAgreementSpec(pSpec, GordianAgreementType.UNIFIED, Boolean.TRUE));
        }

        /* Return the list */
        return myAgreements;
    }
}
