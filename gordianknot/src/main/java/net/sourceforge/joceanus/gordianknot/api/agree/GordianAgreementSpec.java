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
package net.sourceforge.joceanus.gordianknot.api.agree;

import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairType;

import java.util.Objects;

/**
 * KeyPair Agreement Specification.
 */
public final class GordianAgreementSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * KeyPairSpec.
     */
    private final GordianKeyPairSpec theKeyPairSpec;

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
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreementType the agreement type
     * @param pKDFType the KDF type
     */
    public GordianAgreementSpec(final GordianKeyPairSpec pKeyPairSpec,
                                final GordianAgreementType pAgreementType,
                                final GordianKDFType pKDFType) {
        this(pKeyPairSpec, pAgreementType, pKDFType, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pKeyPairSpec the keyPairSpec
     * @param pAgreementType the agreement type
     * @param pKDFType the KDF type
     * @param pConfirm with key confirmation
     */
    public GordianAgreementSpec(final GordianKeyPairSpec pKeyPairSpec,
                                final GordianAgreementType pAgreementType,
                                final GordianKDFType pKDFType,
                                final Boolean pConfirm) {
        theKeyPairSpec = pKeyPairSpec;
        theAgreementType = pAgreementType;
        theKDFType = pKDFType;
        withConfirm = pConfirm;
        isValid = checkValidity();
    }

    /**
     * Obtain the keyPairSpec.
     * @return the keyPairSpec
     */
    public GordianKeyPairSpec getKeyPairSpec() {
        return theKeyPairSpec;
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
        final GordianKeyPairType myType = theKeyPairSpec.getKeyPairType();
        return theAgreementType.isSupported(myType) && theKDFType.isSupported(myType, theAgreementType);
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
        if (theKeyPairSpec == null
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
                theName = theKeyPairSpec.toString()
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
                theName = "InvalidAgreementSpec: " + theKeyPairSpec + ":" + theAgreementType + ":" + theKDFType;
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

        /* Match subfields */
        return pThat instanceof GordianAgreementSpec myThat
                && Objects.equals(theKeyPairSpec, myThat.getKeyPairSpec())
                && theAgreementType == myThat.getAgreementType()
                && theKDFType == myThat.getKDFType()
                && withConfirm == myThat.withConfirm();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairSpec, theAgreementType, theKDFType, withConfirm);
    }
}
