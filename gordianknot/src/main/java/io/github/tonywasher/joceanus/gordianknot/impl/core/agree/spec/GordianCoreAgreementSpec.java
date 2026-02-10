/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.agree.spec;

import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementKDF;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementSpec;
import io.github.tonywasher.joceanus.gordianknot.api.agree.spec.GordianNewAgreementType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNewKeyPairType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keypair.spec.GordianCoreKeyPairSpec;

import java.util.Objects;

/**
 * KeyPair Agreement Specification.
 */
public class GordianCoreAgreementSpec
        implements GordianNewAgreementSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * KeyPairSpec.
     */
    private final GordianCoreKeyPairSpec theKeyPairSpec;

    /**
     * AgreementType.
     */
    private final GordianCoreAgreementType theAgreementType;

    /**
     * KDFType.
     */
    private final GordianCoreAgreementKDF theKDFType;

    /**
     * With Confirmation?.
     */
    private final boolean withConfirm;

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
     *
     * @param pKeyPairSpec   the keyPairSpec
     * @param pAgreementType the agreement type
     * @param pKDFType       the KDF type
     */
    public GordianCoreAgreementSpec(final GordianNewKeyPairSpec pKeyPairSpec,
                                    final GordianNewAgreementType pAgreementType,
                                    final GordianNewAgreementKDF pKDFType) {
        this(pKeyPairSpec, pAgreementType, pKDFType, false);
    }

    /**
     * Constructor.
     *
     * @param pKeyPairSpec   the keyPairSpec
     * @param pAgreementType the agreement type
     * @param pKDFType       the KDF type
     * @param pConfirm       with key confirmation
     */
    public GordianCoreAgreementSpec(final GordianNewKeyPairSpec pKeyPairSpec,
                                    final GordianNewAgreementType pAgreementType,
                                    final GordianNewAgreementKDF pKDFType,
                                    final boolean pConfirm) {
        theKeyPairSpec = (GordianCoreKeyPairSpec) pKeyPairSpec;
        theAgreementType = GordianCoreAgreementType.mapCoreType(pAgreementType);
        theKDFType = GordianCoreAgreementKDF.mapCoreKDF(pKDFType);
        withConfirm = pConfirm;
        isValid = checkValidity();
    }

    @Override
    public GordianNewKeyPairSpec getKeyPairSpec() {
        return theKeyPairSpec;
    }

    /**
     * Obtain the core keyPairSpec.
     *
     * @return the core Spec
     */
    public GordianCoreKeyPairSpec getCoreKeyPairSpec() {
        return theKeyPairSpec;
    }

    @Override
    public GordianNewAgreementType getAgreementType() {
        return theAgreementType.getType();
    }

    /**
     * Obtain the agreementType.
     *
     * @return the agreementType
     */
    public GordianCoreAgreementType getCoreAgreementType() {
        return theAgreementType;
    }

    @Override
    public GordianNewAgreementKDF getKDFType() {
        return theKDFType.getKDF();
    }

    /**
     * Obtain the kdfType.
     *
     * @return the kdfType
     */
    public GordianCoreAgreementKDF getCoreKDFType() {
        return theKDFType;
    }

    /**
     * Is this agreement with key confirmation?
     *
     * @return true/false
     */
    public boolean withConfirm() {
        return withConfirm;
    }

    /**
     * Is this Agreement supported?
     *
     * @return true/false
     */
    public boolean isSupported() {
        final GordianNewKeyPairType myType = theKeyPairSpec.getKeyPairType();
        return theAgreementType.isSupported(myType)
                && theKDFType.isSupported(myType, theAgreementType.getType());
    }

    /**
     * Is the agreementSpec valid?
     *
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* All components must be non-null */
        if (theKeyPairSpec == null
                || theAgreementType == null
                || theKDFType == null) {
            return false;
        }

        /* Confirmation is restricted to certain agreement types */
        if (withConfirm) {
            switch (theAgreementType.getType()) {
                case UNIFIED:
                case MQV:
                case SM2:
                    return true;
                default:
                    return false;
            }
        }

        /* Valid if supported */
        return isSupported();
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
                if (GordianNewAgreementKDF.NONE != theKDFType.getKDF()) {
                    theName += SEP + theKDFType;
                }

                /* Add Confirm if present */
                if (withConfirm) {
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
        return pThat instanceof GordianCoreAgreementSpec myThat
                && Objects.equals(theKeyPairSpec, myThat.getKeyPairSpec())
                && Objects.equals(theAgreementType, myThat.getCoreAgreementType())
                && Objects.equals(theKDFType, myThat.getCoreKDFType())
                && withConfirm == myThat.withConfirm();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theKeyPairSpec, theAgreementType, theKDFType, withConfirm);
    }
}
