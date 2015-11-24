/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.crypto;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Mac Specification.
 */
public final class GordianMacSpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The Mac Type.
     */
    private final GordianMacType theMacType;

    /**
     * The Digest Type.
     */
    private final GordianDigestType theDigestType;

    /**
     * The SymKey Type.
     */
    private final GordianSymKeyType theKeyType;

    /**
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pMacType the macType
     */
    public GordianMacSpec(final GordianMacType pMacType) {
        theMacType = pMacType;
        theDigestType = null;
        theKeyType = null;
    }

    /**
     * Constructor.
     * @param pMacType the macType
     * @param pDigestType the digestType
     */
    public GordianMacSpec(final GordianMacType pMacType,
                   final GordianDigestType pDigestType) {
        /* Store parameters */
        theMacType = pMacType;
        theDigestType = pDigestType;
        theKeyType = null;
    }

    /**
     * Constructor.
     * @param pMacType the macType
     * @param pKeyType the keyType
     */
    public GordianMacSpec(final GordianMacType pMacType,
                   final GordianSymKeyType pKeyType) {
        /* Store parameters */
        theMacType = pMacType;
        theDigestType = null;
        theKeyType = pKeyType;
    }

    /**
     * Obtain Mac Type.
     * @return the MacType
     */
    public GordianMacType getMacType() {
        return theMacType;
    }

    /**
     * Obtain Digest Type.
     * @return the DigestType
     */
    public GordianDigestType getDigestType() {
        return theDigestType;
    }

    /**
     * Obtain SymKey Type.
     * @return the KeyType
     */
    public GordianSymKeyType getKeyType() {
        return theKeyType;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theMacType.toString();
            if (theDigestType != null) {
                theName += SEP + theDigestType.toString();
            } else if (theKeyType != null) {
                theName += SEP + theKeyType.toString();
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

        /* Make sure that the object is a MacSpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Key */
        GordianMacSpec myThat = (GordianMacSpec) pThat;

        /* Check MacType */
        if (theMacType != myThat.getMacType()) {
            return false;
        }

        /* Match subfields */
        if (theDigestType != null) {
            return theDigestType.equals(myThat.getDigestType());
        }
        if (theKeyType != null) {
            return theKeyType.equals(myThat.getKeyType());
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = theMacType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        if (theDigestType != null) {
            hashCode += theDigestType.ordinal();
        }
        if (theKeyType != null) {
            hashCode += theKeyType.ordinal();
        }
        return hashCode;
    }
}
