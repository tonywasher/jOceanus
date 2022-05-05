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
package net.sourceforge.joceanus.jgordianknot.api.agree;

import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairType;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
     * Create the KEM agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec kem(final GordianKeyPairSpec pKeyPairSpec,
                                           final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.KEM, pKDFType);
    }

    /**
     * Create the ANON agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec anon(final GordianKeyPairSpec pKeyPairSpec,
                                            final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.ANON, pKDFType);
    }

    /**
     * Create the Basic agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec basic(final GordianKeyPairSpec pKeyPairSpec,
                                             final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.BASIC, pKDFType);
    }

    /**
     * Create the signed agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec signed(final GordianKeyPairSpec pKeyPairSpec,
                                              final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.SIGNED, pKDFType);
    }

    /**
     * Create the MQV agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec mqv(final GordianKeyPairSpec pKeyPairSpec,
                                           final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.MQV, pKDFType);
    }

    /**
     * Create the MQVConfirm agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec mqvConfirm(final GordianKeyPairSpec pKeyPairSpec,
                                                  final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.MQV, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the Unified agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec unified(final GordianKeyPairSpec pKeyPairSpec,
                                               final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.UNIFIED, pKDFType);
    }

    /**
     * Create the unifiedConfirm agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec uUnifiedConfirm(final GordianKeyPairSpec pKeyPairSpec,
                                                       final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.MQV, pKDFType, Boolean.TRUE);
    }

    /**
     * Create the sm2 agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2(final GordianKeyPairSpec pKeyPairSpec,
                                           final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.SM2, pKDFType);
    }

    /**
     * Create the sm2Confirm agreementSpec.
     * @param pKeyPairSpec the keyPairSpec
     * @param pKDFType the KDF type
     * @return the Spec
     */
    public static GordianAgreementSpec sm2Confirm(final GordianKeyPairSpec pKeyPairSpec,
                                                  final GordianKDFType pKDFType) {
        return new GordianAgreementSpec(pKeyPairSpec, GordianAgreementType.SM2, pKDFType,  Boolean.TRUE);
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

        /* Make sure that the object is an AgreementSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target AgreementSpec */
        final GordianAgreementSpec myThat = (GordianAgreementSpec) pThat;

        /* Match subfields */
        return Objects.equals(theKeyPairSpec, myThat.getKeyPairSpec())
                && theAgreementType == myThat.getAgreementType()
                && theKDFType == myThat.getKDFType()
                && withConfirm == myThat.withConfirm();
    }

    @Override
    public int hashCode() {
        int hashCode = theKeyPairSpec.hashCode() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theAgreementType.hashCode() + (Boolean.TRUE.equals(withConfirm) ? 1 : 0);
        return (hashCode << TethysDataConverter.BYTE_SHIFT) + theKDFType.hashCode();
    }
}
