/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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

import java.util.ArrayList;
import java.util.List;

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
     * The DigestSpec.
     */
    private final GordianDigestSpec theDigestSpec;

    /**
     * The SymKey Type.
     */
    private final GordianSymKeyType theKeyType;

    /**
     * The String name.
     */
    private String theName;

    /**
     * hMac/skeinMac Constructor.
     * @param pMacType the macType
     * @param pDigestSpec the digestSpec
     */
    protected GordianMacSpec(final GordianMacType pMacType,
                             final GordianDigestSpec pDigestSpec) {
        /* Store parameters */
        theMacType = pMacType;
        theDigestSpec = pDigestSpec;
        theKeyType = null;
    }

    /**
     * gMac/Poly1305 Constructor.
     * @param pMacType the macType
     * @param pKeyType the keyType
     */
    protected GordianMacSpec(final GordianMacType pMacType,
                             final GordianSymKeyType pKeyType) {
        /* Store parameters */
        theMacType = pMacType;
        theDigestSpec = null;
        theKeyType = pKeyType;
    }

    /**
     * vmpcMac Constructor.
     * @param pMacType the macType
     */
    protected GordianMacSpec(final GordianMacType pMacType) {
        theMacType = pMacType;
        theDigestSpec = null;
        theKeyType = null;
    }

    /**
     * Create hMacSpec.
     * @param pDigestType the digestType
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestType pDigestType) {
        return hMac(new GordianDigestSpec(pDigestType));
    }

    /**
     * Create hMacSpec.
     * @param pDigestSpec the digestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestSpec pDigestSpec) {
        return new GordianMacSpec(GordianMacType.HMAC, pDigestSpec);
    }

    /**
     * Create gMacSpec.
     * @param pSymKeyType the symKeyType
     * @return the MacSpec
     */
    public static GordianMacSpec gMac(final GordianSymKeyType pSymKeyType) {
        return new GordianMacSpec(GordianMacType.GMAC, pSymKeyType);
    }

    /**
     * Create cMacSpec.
     * @param pSymKeyType the symKeyType
     * @return the MacSpec
     */
    public static GordianMacSpec cMac(final GordianSymKeyType pSymKeyType) {
        return new GordianMacSpec(GordianMacType.CMAC, pSymKeyType);
    }

    /**
     * Create poly1305MacSpec.
     * @param pSymKeyType the symKeyType
     * @return the MacSpec
     */
    public static GordianMacSpec poly1305Mac(final GordianSymKeyType pSymKeyType) {
        return new GordianMacSpec(GordianMacType.POLY1305, pSymKeyType);
    }

    /**
     * Create skeinMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pLength) {
        return skeinMac(pLength, false);
    }

    /**
     * Create skeinMacSpec.
     * @param pLength the length
     * @param useExtended use extended state
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pLength,
                                          final boolean useExtended) {
        GordianDigestSpec mySpec = GordianDigestSpec.skein(pLength, useExtended);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pState the state length
     * @param pLength the length
     * @return the MacSpec
     */
    protected static GordianMacSpec skeinMac(final GordianLength pState,
                                             final GordianLength pLength) {
        GordianDigestSpec mySpec = GordianDigestSpec.skein(pState, pLength);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create vmpcMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec vmpcMac() {
        return new GordianMacSpec(GordianMacType.VMPC);
    }

    /**
     * Obtain Mac Type.
     * @return the MacType
     */
    public GordianMacType getMacType() {
        return theMacType;
    }

    /**
     * Obtain DigestSpec.
     * @return the DigestSpec
     */
    public GordianDigestSpec getDigestSpec() {
        return theDigestSpec;
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
            if (theDigestSpec != null) {
                theName += SEP + (GordianMacType.SKEIN.equals(theMacType)
                                                                          ? theDigestSpec.getStateLength().getLength()
                                                                            + SEP + theDigestSpec.getDigestLength().getLength()
                                                                          : theDigestSpec.toString());
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

        /* Access the target MacSpec */
        GordianMacSpec myThat = (GordianMacSpec) pThat;

        /* Check MacType */
        if (theMacType != myThat.getMacType()) {
            return false;
        }

        /* Match subfields */
        if (theDigestSpec != null) {
            return theDigestSpec.equals(myThat.getDigestSpec());
        }
        if (theKeyType != null) {
            return theKeyType.equals(myThat.getKeyType());
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = theMacType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        if (theDigestSpec != null) {
            hashCode += theDigestSpec.hashCode();
        }
        if (theKeyType != null) {
            hashCode += theKeyType.ordinal();
        }
        return hashCode;
    }

    /**
     * List all possible macSpecs.
     * @return the list
     */
    public static List<GordianMacSpec> listAll() {
        /* Create the array list */
        List<GordianMacSpec> myList = new ArrayList<>();

        /* For each digestSpec */
        for (GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            myList.add(GordianMacSpec.hMac(mySpec));
        }

        /* For each SymKey */
        for (GordianSymKeyType mySymKeyType : GordianSymKeyType.values()) {
            myList.add(GordianMacSpec.gMac(mySymKeyType));
            myList.add(GordianMacSpec.cMac(mySymKeyType));
            myList.add(GordianMacSpec.poly1305Mac(mySymKeyType));
        }

        /* For each length */
        for (GordianLength myLength : GordianLength.values()) {
            myList.add(GordianMacSpec.skeinMac(myLength));
            if (GordianDigestType.SKEIN.getExtendedStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.skeinMac(myLength, true));
            }
        }

        /* Add vmpcMac */
        myList.add(GordianMacSpec.vmpcMac());

        /* Return the list */
        return myList;
    }
}
