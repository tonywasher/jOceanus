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
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Key Agreement Specification.
 */
public class GordianAgreementSpec {
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
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pAsymKeyType the asymKeyType
     * @param pAgreementType the agreement type
     */
    public GordianAgreementSpec(final GordianAsymKeyType pAsymKeyType,
                                final GordianAgreementType pAgreementType) {
        theAsymKeyType = pAsymKeyType;
        theAgreementType = pAgreementType;
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
     * Is this Agreement supported?
     * @return true/false
     */
    public boolean isSupported() {
        return theAgreementType.isSupported(theAsymKeyType);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theAsymKeyType.toString()
                        + SEP + theAgreementType.toString();
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
               && theAgreementType == myThat.getAgreementType();
    }

    @Override
    public int hashCode() {
        final int hashCode = theAsymKeyType.hashCode() << TethysDataConverter.BYTE_SHIFT;
        return hashCode + theAgreementType.hashCode();
    }
}
