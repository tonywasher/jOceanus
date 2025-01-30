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
package net.sourceforge.joceanus.gordianknot.api.random;

import java.util.Objects;

import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;

/**
 * SecureRandom Specification.
 */
public class GordianRandomSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The RandomType.
     */
    private final GordianRandomType theRandomType;

    /**
     * The SubSpec.
     */
    private final Object theSubSpec;

    /**
     * Is the secureRandom predicationResistant?
     */
    private final boolean isPredictionResistant;

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
     * @param pRandomType the randomType
     * @param pSubSpec the subSpec
     * @param pResistant is the secureRandom predicationResistant?
     */
    public GordianRandomSpec(final GordianRandomType pRandomType,
                             final Object pSubSpec,
                             final boolean pResistant) {
        theRandomType = pRandomType;
        theSubSpec = pSubSpec;
        isPredictionResistant = pResistant;
        isValid = checkValidity();
    }

    /**
     * Obtain the randomType.
     * @return the randomType.
     */
    public GordianRandomType getRandomType() {
        return theRandomType;
    }

    /**
     * Obtain the subSpec.
     * @return the subSpec.
     */
    private Object getSubSpec() {
        return theSubSpec;
    }

    /**
     * Is the macSpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the digestSpec.
     * @return the digestSpec.
     */
    public GordianDigestSpec getDigestSpec() {
        return theSubSpec instanceof GordianDigestSpec
               ? (GordianDigestSpec) theSubSpec
               : null;
    }

    /**
     * Obtain the symKeySpec.
     * @return the symKeySpec.
     */
    public GordianSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianSymKeySpec
               ? (GordianSymKeySpec) theSubSpec
               : null;
    }

    /**
     * Obtain the predication resistance.
     * @return the resistance.
     */
    public boolean isPredictionResistant() {
        return isPredictionResistant;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        if (theRandomType == null) {
            return false;
        }
        switch (theRandomType) {
            case HMAC:
            case HASH:
                return theSubSpec instanceof GordianDigestSpec
                        && ((GordianDigestSpec) theSubSpec).isValid()
                        && ((GordianDigestSpec) theSubSpec).getDigestType().supportsLargeData();
            case CTR:
            case X931:
                return theSubSpec instanceof GordianSymKeySpec
                        && ((GordianSymKeySpec) theSubSpec).isValid();
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the randomSpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theRandomType.toString();
                theName += SEP + theSubSpec;
                if (isPredictionResistant) {
                    theName += SEP + "resistant";
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidRandomSpec: " + theRandomType + ":" + theSubSpec + ":" + isPredictionResistant;
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

        /* Make sure that the object is a RandomSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the targetSpec */
        final GordianRandomSpec myThat = (GordianRandomSpec) pThat;

        /* Check KeyType, prediction and subSpec */
        return theRandomType == myThat.getRandomType()
                && isPredictionResistant == myThat.isPredictionResistant()
                && Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theRandomType, theSubSpec, isPredictionResistant);
    }
}
