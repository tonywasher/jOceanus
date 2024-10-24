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
package net.sourceforge.joceanus.jgordianknot.api.digest;

import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;

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
     * The Digest State Length.
     */
    private final GordianLength theStateLength;

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
        this(pDigestType, pDigestType.getStateForLength(pLength), pLength);
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pStateLength the stateLength
     * @param pLength the length
     */
    public GordianDigestSpec(final GordianDigestType pDigestType,
                             final GordianLength pStateLength,
                             final GordianLength pLength) {
        /* Store parameters */
        theDigestType = pDigestType;
        theStateLength = pStateLength;
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
     * Obtain State Length.
     * @return the Length
     */
    public GordianLength getStateLength() {
        return theStateLength;
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
     * Is this a hybrid state.
     * @return true/false
     */
    public boolean isHybrid() {
        return theStateLength != null && !theStateLength.equals(theLength);
    }

    /**
     * Is this a pureSHAKE digest.
     * @return true/false
     */
    public boolean isPureSHAKE() {
        return GordianDigestType.SHAKE.equals(theDigestType)
                && theStateLength != null
                && theLength.getByteLength() == 2 * theStateLength.getByteLength();
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Handle null keyType */
        if (theDigestType == null || theLength == null) {
            return false;
        }

        /* Switch on keyType */
        switch (theDigestType) {
            case SKEIN:
            case BLAKE2:
            case SHAKE:
            case KANGAROO:
            case HARAKA:
                return theStateLength != null;
            case SHA2:
                return true;
            default:
                return theStateLength == null;
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
                        if (theStateLength != null) {
                            theName += SEP + theStateLength;
                        }
                        theName += SEP + theLength;
                        break;
                    case SHAKE:
                        theName += theStateLength;
                        if (!isPureSHAKE()) {
                            theName += SEP + theLength;
                        }
                        break;
                    case SKEIN:
                        theName += SEP + theStateLength;
                        theName += SEP + theLength;
                        break;
                    case BLAKE2:
                        theName = GordianDigestType.getBlake2AlgorithmForStateLength(theStateLength);
                        theName += SEP + theLength;
                        break;
                    case KANGAROO:
                        theName = GordianDigestType.getKangarooAlgorithmForStateLength(theStateLength);
                        theName += SEP + theLength;
                        break;
                    case HARAKA:
                        theName += SEP + theStateLength;
                        break;
                    default:
                        if (theDigestType.getSupportedLengths().length > 1) {
                            theName += SEP + theLength;
                        }
                        break;
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidDigestSpec: " + theDigestType + ":" + theStateLength + ":" + theLength;
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
                && theStateLength == myThat.getStateLength()
                && theLength == myThat.getDigestLength();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theDigestType, theStateLength, theLength);
    }
}
