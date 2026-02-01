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

package io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * SymKey specification.
 */
public class GordianCoreSymKeySpec
        implements GordianNewSymKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The symKeyTypeMap.
     */
    private static final Map<GordianNewSymKeyType, GordianCoreSymKeyType> TYPEMAP = newTypeMap();

    /**
     * The symKeyType.
     */
    private final GordianCoreSymKeyType theType;

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
     * @param pType        the digestType
     * @param pBlockLength the blockLength
     * @param pKeyLength   the keyLength
     */
    GordianCoreSymKeySpec(final GordianNewSymKeyType pType,
                          final GordianLength pBlockLength,
                          final GordianLength pKeyLength) {
        theType = TYPEMAP.get(pType);
        theBlockLength = pBlockLength;
        theKeyLength = pKeyLength;
        isValid = checkValidity();
    }

    /**
     * Obtain the core symKeyType.
     *
     * @return the type
     */
    public GordianCoreSymKeyType getCoreSymKeyType() {
        return theType;
    }

    @Override
    public GordianNewSymKeyType getSymKeyType() {
        return theType.getType();
    }

    @Override
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
        if (theType == null
                || theBlockLength == null
                || theKeyLength == null) {
            return false;
        }

        /* Check blockLength and keyLength validity */
        return theType.validBlockAndKeyLengths(theBlockLength, theKeyLength);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                final GordianNewSymKeyType myType = getSymKeyType();
                theName = theType.toString();
                if (theType.hasMultipleBlockLengths()
                        && !GordianNewSymKeyType.THREEFISH.equals(myType)) {
                    int myLen = theBlockLength.getLength();
                    if (GordianNewSymKeyType.RC5.equals(myType)) {
                        myLen >>= 1;
                    }
                    theName += myLen;
                }
                theName += SEP + theKeyLength;
            } else {
                /* Report invalid spec */
                theName = "InvalidSymKeySpec: " + theType + ":" + theBlockLength + ":" + theKeyLength;
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
        return pThat instanceof GordianCoreSymKeySpec myThat
                && Objects.equals(theType, myThat.getCoreSymKeyType())
                && theBlockLength == myThat.getBlockLength()
                && theKeyLength == myThat.getKeyLength();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theBlockLength, theKeyLength);
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewSymKeyType, GordianCoreSymKeyType> newTypeMap() {
        final Map<GordianNewSymKeyType, GordianCoreSymKeyType> myMap = new EnumMap<>(GordianNewSymKeyType.class);
        for (GordianNewSymKeyType myType : GordianNewSymKeyType.values()) {
            myMap.put(myType, new GordianCoreSymKeyType(myType));
        }
        return myMap;
    }
}
