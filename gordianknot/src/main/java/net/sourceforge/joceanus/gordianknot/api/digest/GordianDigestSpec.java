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
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianAsconSubSpec;
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
        this(pDigestType, GordianDigestSubSpec.getDefaultSubSpecForTypeAndLength(pDigestType, pLength), pLength);
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
        /* Store parameters */
        theDigestType = pDigestType;
        theSubSpec = pState;
        theLength = pLength;
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
     * Obtain AsconSubSpec.
     * @return the SubSpec
     */
    public GordianAsconSubSpec getAsconSubSpec() {
        return theSubSpec instanceof GordianAsconSubSpec
                ? (GordianAsconSubSpec) theSubSpec
                : null;
    }

    /**
     * Obtain Digest Length.
     * @return the Length
     */
    public GordianLength getDigestLength() {
        return theLength;
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
        /* Handle null spec/length */
        if (theDigestType == null || theLength == null) {
            return false;
        }

        /* Switch on keyType */
        switch (theDigestType) {
            case SHA2:
            case SKEIN:
            case BLAKE2:
            case SHAKE:
            case KANGAROO:
            case HARAKA:
                return theSubSpec instanceof GordianDigestState
                        && getDigestState().validForTypeAndLength(theDigestType, theLength);
            case ASCON:
                return theSubSpec instanceof GordianAsconSubSpec
                        && theDigestType.isLengthValid(theLength);
            default:
                return theSubSpec == null
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
                        theName += SEP + theSubSpec;
                        theName += SEP + theLength;
                        break;
                    case BLAKE2:
                        theName = getDigestState().getBlake2Algorithm();
                        theName += SEP + theLength;
                        break;
                    case KANGAROO:
                        theName = getDigestState().getKangarooAlgorithm();
                        break;
                    case HARAKA:
                        theName += SEP + theSubSpec;
                        break;
                    case ASCON:
                        theName = theSubSpec.toString();
                        break;
                    default:
                        if (theDigestType.getSupportedLengths().length > 1) {
                            theName += SEP + theLength;
                        }
                        break;
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidDigestSpec: " + theDigestType + ":" + theSubSpec + ":" + theLength;
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
                && theLength == myThat.getDigestLength();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theDigestType, theSubSpec, theLength);
    }
}
