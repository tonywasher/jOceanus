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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.random;

import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomSpec;
import io.github.tonywasher.joceanus.gordianknot.api.random.spec.GordianNewRandomType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSpec;

import java.util.Objects;

/**
 * SecureRandom Specification.
 */
public class GordianCoreRandomSpec
        implements GordianNewRandomSpec {
    /**
     * The Separator.
     */
    static final String SEP = "-";

    /**
     * The RandomType.
     */
    private final GordianCoreRandomType theType;

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
     *
     * @param pRandomType the randomType
     * @param pSubSpec    the subSpec
     * @param pResistant  is the secureRandom predicationResistant?
     */
    public GordianCoreRandomSpec(final GordianNewRandomType pRandomType,
                                 final Object pSubSpec,
                                 final boolean pResistant) {
        theType = GordianCoreRandomType.mapCoreType(pRandomType);
        theSubSpec = pSubSpec;
        isPredictionResistant = pResistant;
        isValid = checkValidity();
    }

    @Override
    public GordianNewRandomType getRandomType() {
        return theType.getType();
    }

    /**
     * Obtain the randomType.
     *
     * @return the randomType.
     */
    public GordianCoreRandomType getCoreType() {
        return theType;
    }

    @Override
    public Object getSubSpec() {
        return theSubSpec;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain the digestSpec.
     *
     * @return the digestSpec.
     */
    public GordianCoreDigestSpec getDigestSpec() {
        return theSubSpec instanceof GordianCoreDigestSpec mySpec
                ? mySpec
                : null;
    }

    /**
     * Obtain the symKeySpec.
     *
     * @return the symKeySpec.
     */
    public GordianCoreSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                ? mySpec
                : null;
    }

    /**
     * Obtain the predication resistance.
     *
     * @return the resistance.
     */
    public boolean isPredictionResistant() {
        return isPredictionResistant;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        if (theType == null) {
            return false;
        }
        switch (theType.getType()) {
            case HMAC:
            case HASH:
                return theSubSpec instanceof GordianCoreDigestSpec mySpec
                        && mySpec.isValid()
                        && mySpec.getCoreDigestType().supportsLargeData();
            case CTR:
            case X931:
                return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                        && mySpec.isValid();
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
                theName = theType.toString();
                theName += SEP + theSubSpec;
                if (isPredictionResistant) {
                    theName += SEP + "resistant";
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidRandomSpec: " + theType + ":" + theSubSpec + ":" + isPredictionResistant;
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

        /* Check KeyType, prediction and subSpec */
        return pThat instanceof GordianCoreRandomSpec myThat
                && Objects.equals(theType, myThat.getCoreType())
                && isPredictionResistant == myThat.isPredictionResistant()
                && Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theSubSpec, isPredictionResistant);
    }
}
