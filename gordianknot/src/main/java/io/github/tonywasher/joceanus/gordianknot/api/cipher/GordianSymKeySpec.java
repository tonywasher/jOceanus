/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.gordianknot.api.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;

import java.util.Objects;

/**
 * SymKey specification.
 */
public class GordianSymKeySpec
        implements GordianKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The SymKey Type.
     */
    private final GordianSymKeyType theSymKeyType;

    /**
     * The Engine Block Length.
     */
    private final GordianLength theBlockLength;

    /**
     * The Key Length.
     */
    private final GordianLength theKeyLength;

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
     * @param pSymKeyType the symKeyType
     * @param pKeyLength  the keyLength
     */
    public GordianSymKeySpec(final GordianSymKeyType pSymKeyType,
                             final GordianLength pKeyLength) {
        this(pSymKeyType, pSymKeyType.getDefaultBlockLength(), pKeyLength);
    }

    /**
     * Constructor.
     *
     * @param pSymKeyType  the symKeyType
     * @param pBlockLength the stateLength
     * @param pKeyLength   the keyLength
     */
    public GordianSymKeySpec(final GordianSymKeyType pSymKeyType,
                             final GordianLength pBlockLength,
                             final GordianLength pKeyLength) {
        /* Store parameters */
        theSymKeyType = pSymKeyType;
        theBlockLength = pBlockLength;
        theKeyLength = pKeyLength;
        isValid = checkValidity();
    }

    /**
     * Obtain symKey Type.
     *
     * @return the symKeyType
     */
    public GordianSymKeyType getSymKeyType() {
        return theSymKeyType;
    }

    /**
     * Obtain Block Length.
     *
     * @return the blockLength
     */
    public GordianLength getBlockLength() {
        return theBlockLength;
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    /**
     * Obtain HalfBlock length.
     *
     * @return the Length
     */
    public GordianLength getHalfBlockLength() {
        switch (theBlockLength) {
            case LEN_64:
                return GordianLength.LEN_32;
            case LEN_128:
                return GordianLength.LEN_64;
            case LEN_256:
                return GordianLength.LEN_128;
            case LEN_512:
                return GordianLength.LEN_256;
            case LEN_1024:
                return GordianLength.LEN_512;
            default:
                return theBlockLength;
        }
    }

    /**
     * Is the keySpec valid?
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
        /* Everything must be non-null */
        if (theSymKeyType == null
                || theBlockLength == null
                || theKeyLength == null) {
            return false;
        }

        /* Check blockLength and keyLength validity */
        return theSymKeyType.validBlockAndKeyLengths(theBlockLength, theKeyLength);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theSymKeyType.toString();
                if (theSymKeyType.hasMultipleBlockLengths()
                        && !GordianSymKeyType.THREEFISH.equals(theSymKeyType)) {
                    int myLen = theBlockLength.getLength();
                    if (GordianSymKeyType.RC5.equals(theSymKeyType)) {
                        myLen >>= 1;
                    }
                    theName += myLen;
                }
                theName += SEP + theKeyLength;
            } else {
                /* Report invalid spec */
                theName = "InvalidSymKeySpec: " + theSymKeyType + ":" + theBlockLength + ":" + theKeyLength;
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
        return pThat instanceof GordianSymKeySpec myThat
                && theSymKeyType == myThat.getSymKeyType()
                && theBlockLength == myThat.getBlockLength()
                && theKeyLength == myThat.getKeyLength();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theSymKeyType, theBlockLength, theKeyLength);
    }
}
