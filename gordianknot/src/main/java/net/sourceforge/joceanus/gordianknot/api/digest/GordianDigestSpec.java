/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.digest;

import net.sourceforge.joceanus.gordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;

import java.util.Objects;

/**
 * Digest Specification.
 */
public class GordianDigestSpec
    implements GordianIdSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The Digest Type.
     */
    private final GordianDigestType theDigestType;

    /**
     * The Digest SubSpec.
     */
    private final GordianDigestSubSpec theSubSpec;

    /**
     * The Digest Length.
     */
    private final GordianLength theLength;

    /**
     * Is this a Xof mode?
     */
    private final Boolean isXofMode;

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
     * @param pDigestType the digestType
     */
    public GordianDigestSpec(final GordianDigestType pDigestType) {
        this(pDigestType, pDigestType.getDefaultLength());
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pLength the length
     */
    public GordianDigestSpec(final GordianDigestType pDigestType,
                             final GordianLength pLength) {
        /* Store parameters */
        this(pDigestType, pLength, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pLength the length
     * @param pXofMode is this an explicit Xof?
     */
    public GordianDigestSpec(final GordianDigestType pDigestType,
                             final GordianLength pLength,
                             final Boolean pXofMode) {
        this(pDigestType, GordianDigestSubSpec.getDefaultSubSpecForTypeAndLength(pDigestType, pLength), pLength, pXofMode);
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pState the digestState
     * @param pLength the length
     */
    public GordianDigestSpec(final GordianDigestType pDigestType,
                             final GordianDigestSubSpec pState,
                             final GordianLength pLength) {
        this(pDigestType, pState, pLength, Boolean.FALSE);
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pState the digestState
     * @param pLength the length
     * @param pXofMode is this an explicit Xof?
     */
    public GordianDigestSpec(final GordianDigestType pDigestType,
                             final GordianDigestSubSpec pState,
                             final GordianLength pLength,
                             final Boolean pXofMode) {
        /* Store parameters */
        theDigestType = pDigestType;
        theSubSpec = pState;
        theLength = pLength;
        isXofMode = pXofMode;
        isValid = checkValidity();
    }

    /**
     * Obtain Digest Type.
     * @return the DigestType
     */
    public GordianDigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Obtain DigestSubSpec.
     * @return the SubSpec
     */
    public GordianDigestSubSpec getSubSpec() {
        return theSubSpec;
    }

    /**
     * Obtain DigestState.
     * @return the State
     */
    public GordianDigestState getDigestState() {
        return theSubSpec instanceof GordianDigestState
                ? (GordianDigestState) theSubSpec
                :  null;
    }

    /**
     * Obtain Digest Length.
     * @return the Length
     */
    public GordianLength getDigestLength() {
        return theLength;
    }

    /**
     * Is the digestSpec a Xof mode?
     * @return true/false.
     */
    public Boolean isXofMode() {
        return isXofMode;
    }

    /**
     * Is the digestSpec a Xof?
     * @return true/false.
     */
    public Boolean isXof() {
        return isXofMode || theDigestType.isXof();
    }

    /**
     * Is the digestSpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Is this a sha2 hybrid state.
     * @return true/false
     */
    public boolean isSha2Hybrid() {
        return GordianDigestType.SHA2.equals(theDigestType)
                && GordianDigestState.STATE512.equals(theSubSpec)
                && (GordianLength.LEN_224.equals(theLength)
                    || GordianLength.LEN_256.equals(theLength));
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Handle null type */
        if (theDigestType == null || theLength == null) {
            return false;
        }

        /* Switch on keyType */
        switch (theDigestType) {
            case SHA2:
            case SHAKE:
            case KANGAROO:
            case HARAKA:
                return theSubSpec instanceof GordianDigestState
                        && !isXofMode
                        && getDigestState().validForTypeAndLength(theDigestType, theLength);
            case SKEIN:
            case BLAKE2:
                return theSubSpec instanceof GordianDigestState
                        && (isXofMode ? getDigestState().lengthForXofType(theDigestType) == theLength
                                      : getDigestState().validForTypeAndLength(theDigestType, theLength));
            case ASCON:
                return theSubSpec == null
                        && theDigestType.isLengthValid(theLength);
            default:
                return theSubSpec == null
                        && !isXofMode
                        && theDigestType.isLengthValid(theLength);
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theDigestType.toString();
                switch (theDigestType) {
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
                        theName += getDigestState().getBlake2Algorithm(isXofMode);
                        if (!isXofMode) {
                            theName += SEP + theLength;
                        }
                        break;
                    case KANGAROO:
                        theName = getDigestState().getKangarooAlgorithm();
                        break;
                    case HARAKA:
                        theName += SEP + theSubSpec;
                        break;
                    case ASCON:
                        theName += isXofMode ? "X" : "";
                        break;
                    default:
                        if (theDigestType.getSupportedLengths().length > 1) {
                            theName += SEP + theLength;
                        }
                        break;
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidDigestSpec: " + theDigestType + ":" + theSubSpec + ":" + theLength + ":" + isXofMode;
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

        /* Make sure that the object is a DigestSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target DigestSpec */
        final GordianDigestSpec myThat = (GordianDigestSpec) pThat;

        /* Check subFields */
        return theDigestType == myThat.getDigestType()
                && theSubSpec == myThat.getSubSpec()
                && theLength == myThat.getDigestLength()
                && isXofMode == myThat.isXofMode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theDigestType, theSubSpec, theLength, isXofMode);
    }
}
