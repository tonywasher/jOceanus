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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestSubSpec.GordianNewDigestState;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.digest.GordianCoreDigestSubSpec.GordianCoreDigestState;

import java.util.Objects;

/**
 * DataDigestSpec implementation.
 */
public class GordianCoreDigestSpec
        implements GordianNewDigestSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The digestType.
     */
    private final GordianCoreDigestType theType;

    /**
     * The digestSubSpec.
     */
    private final GordianCoreDigestSubSpec theSubSpec;

    /**
     * The length.
     */
    private final GordianLength theLength;

    /**
     * Is this a Xof mode?
     */
    private final boolean isXofMode;

    /**
     * is the digestSpec valid?.
     */
    private final boolean isValid;

    /**
     * The name of the spec.
     */
    private String theName;

    /**
     * Constructor.
     *
     * @param pType    the digestType
     * @param pSubSpec the subSpec
     * @param pLength  the length
     * @param pXofMode is this a XofMode?
     */
    GordianCoreDigestSpec(final GordianNewDigestType pType,
                          final GordianNewDigestSubSpec pSubSpec,
                          final GordianLength pLength,
                          final boolean pXofMode) {
        theType = GordianCoreDigestType.mapCoreType(pType);
        theSubSpec = GordianCoreDigestState.mapCoreState(pSubSpec);
        theLength = pLength;
        isXofMode = pXofMode;
        isValid = checkValidity();
    }

    /**
     * Obtain the core digestType.
     *
     * @return the type
     */
    public GordianCoreDigestType getCoreDigestType() {
        return theType;
    }

    @Override
    public GordianNewDigestType getDigestType() {
        return theType.getType();
    }

    @Override
    public GordianNewDigestSubSpec getSubSpec() {
        return theSubSpec instanceof GordianCoreDigestSubSpec myState ? myState.getSubSpec() : null;
    }

    /**
     * Obtain the core digestState.
     *
     * @return the type
     */
    public GordianCoreDigestState getCoreDigestState() {
        return theSubSpec instanceof GordianCoreDigestState myState ? myState : null;
    }

    @Override
    public GordianLength getDigestLength() {
        return theLength;
    }

    /**
     * Is the digestSpec a Xof mode?
     *
     * @return true/false.
     */
    public Boolean isXofMode() {
        return isXofMode;
    }

    /**
     * Is the digestSpec a Xof?
     *
     * @return true/false.
     */
    public boolean isXof() {
        return isXofMode || theType.isXof();
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Is this a sha2 hybrid state.
     *
     * @return true/false
     */
    public boolean isSha2Hybrid() {
        return GordianNewDigestType.SHA2.equals(theType.getType())
                && GordianNewDigestState.STATE512.equals(getSubSpec())
                && (GordianLength.LEN_224.equals(theLength)
                || GordianLength.LEN_256.equals(theLength));
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Handle null type */
        if (theType == null || theLength == null) {
            return false;
        }

        /* Switch on keyType */
        switch (theType.getType()) {
            case SHA2:
            case SHAKE:
            case KANGAROO:
            case HARAKA:
                return theSubSpec instanceof GordianCoreDigestState
                        && !isXofMode
                        && getCoreDigestState().validForTypeAndLength(theType, theLength);
            case SKEIN:
            case BLAKE2:
                return theSubSpec instanceof GordianCoreDigestState
                        && (isXofMode ? getCoreDigestState().lengthForXofType(theType) == theLength
                        : getCoreDigestState().validForTypeAndLength(theType, theLength));
            case ASCON:
                return theSubSpec == null
                        && theType.isLengthValid(theLength);
            default:
                return theSubSpec == null
                        && !isXofMode
                        && theType.isLengthValid(theLength);
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theType.toString();
                switch (theType.getType()) {
                    case SHA2:
                        if (isSha2Hybrid()) {
                            theName += SEP + theSubSpec;
                        }
                        theName += SEP + theLength;
                        break;
                    case SHAKE:
                        theName += theSubSpec;
                        break;
                    case SKEIN:
                        if (isXofMode) {
                            theName += "X" + SEP + theSubSpec;
                        } else {
                            theName += SEP + theSubSpec + SEP + theLength;
                        }
                        break;
                    case BLAKE2:
                        theName += getCoreDigestState().getBlake2Algorithm(isXofMode);
                        if (!isXofMode) {
                            theName += SEP + theLength;
                        }
                        break;
                    case KANGAROO:
                        theName = getCoreDigestState().getKangarooAlgorithm();
                        break;
                    case HARAKA:
                        theName += SEP + theSubSpec;
                        break;
                    case ASCON:
                        theName += isXofMode ? "X" : "";
                        break;
                    default:
                        if (theType.getSupportedLengths().length > 1) {
                            theName += SEP + theLength;
                        }
                        break;
                }
            } else {
                /* Report invalid spec */
                theName = "InvalidDigestSpec: " + theType + ":" + theSubSpec + ":" + theLength + ":" + isXofMode;
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

        /* Check subFields */
        return pThat instanceof GordianCoreDigestSpec myThat
                && Objects.equals(theType, myThat.getCoreDigestType())
                && Objects.equals(theSubSpec, myThat.getCoreDigestState())
                && theLength == myThat.getDigestLength()
                && isXofMode == myThat.isXofMode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theSubSpec, theLength, isXofMode);
    }
}
