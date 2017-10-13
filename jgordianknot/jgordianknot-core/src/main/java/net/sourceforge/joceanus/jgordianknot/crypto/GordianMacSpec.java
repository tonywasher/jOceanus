/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
    private final GordianSymKeySpec theKeySpec;

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
        theKeySpec = null;
    }

    /**
     * gMac/Poly1305 Constructor.
     * @param pMacType the macType
     * @param pKeySpec the keySpec
     */
    protected GordianMacSpec(final GordianMacType pMacType,
                             final GordianSymKeySpec pKeySpec) {
        /* Store parameters */
        theMacType = pMacType;
        theDigestSpec = null;
        theKeySpec = pKeySpec;
    }

    /**
     * vmpcMac Constructor.
     * @param pMacType the macType
     */
    protected GordianMacSpec(final GordianMacType pMacType) {
        theMacType = pMacType;
        theDigestSpec = null;
        theKeySpec = null;
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
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec gMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.GMAC, pSymKeySpec);
    }

    /**
     * Create cMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec cMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.CMAC, pSymKeySpec);
    }

    /**
     * Create poly1305MacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec poly1305Mac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.POLY1305, pSymKeySpec);
    }

    /**
     * Create skeinMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac() {
        return GordianMacSpec.skeinMac(GordianDigestType.SKEIN.getDefaultLength());
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
        final GordianDigestSpec mySpec = GordianDigestSpec.skein(pLength, useExtended);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pState the state length
     * @param pLength the length
     * @return the MacSpec
     */
    static GordianMacSpec skeinMac(final GordianLength pState,
                                   final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.skein(pState, pLength);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac() {
        return GordianMacSpec.blakeMac(GordianDigestType.BLAKE.getDefaultLength());
    }

    /**
     * Create blake2bMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac(final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.blake(pLength);
        return new GordianMacSpec(GordianMacType.BLAKE, mySpec);
    }

    /**
     * Create kalynaMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec kalynaMac() {
        return GordianMacSpec.kalynaMac(GordianSymKeyType.KALYNA.getDefaultLength());
    }

    /**
     * Create kalynaMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec kalynaMac(final GordianLength pLength) {
        final GordianSymKeySpec mySpec = GordianSymKeySpec.kalyna(pLength);
        return new GordianMacSpec(GordianMacType.KALYNA, mySpec);
    }

    /**
     * Create kupynaMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec kupynaMac() {
        return GordianMacSpec.skeinMac(GordianDigestType.KUPYNA.getDefaultLength());
    }

    /**
     * Create kupynaMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec kupynaMac(final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.kupyna(pLength);
        return new GordianMacSpec(GordianMacType.KUPYNA, mySpec);
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
    public GordianSymKeySpec getKeySpec() {
        return theKeySpec;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theMacType.toString();
            switch (theMacType) {
                case HMAC:
                    theName += SEP + theDigestSpec.toString();
                    break;
                case SKEIN:
                    theName += SEP + theDigestSpec.getStateLength().getLength()
                               + SEP + theDigestSpec.getDigestLength().getLength();
                    break;
                case GMAC:
                case CMAC:
                case POLY1305:
                    theName += SEP + theKeySpec.toString();
                    break;
                case KUPYNA:
                case BLAKE:
                    theName += SEP + theDigestSpec.getDigestLength().getLength();
                    break;
                case KALYNA:
                    theName += SEP + theKeySpec.getBlockLength().getLength();
                    break;
                case VMPC:
                default:
                    break;
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
        final GordianMacSpec myThat = (GordianMacSpec) pThat;

        /* Check MacType */
        if (theMacType != myThat.getMacType()) {
            return false;
        }

        /* Match subfields */
        if (theDigestSpec != null) {
            return theDigestSpec.equals(myThat.getDigestSpec());
        }
        if (theKeySpec != null) {
            return theKeySpec.equals(myThat.getKeySpec());
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hashCode = theMacType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        if (theDigestSpec != null) {
            hashCode += theDigestSpec.hashCode();
        }
        if (theKeySpec != null) {
            hashCode += theKeySpec.hashCode();
        }
        return hashCode;
    }

    /**
     * BlockSize for padding.
     * @return the blockSize (or null)
     */
    public int paddingBlock() {
        return GordianMacType.KALYNA.equals(theMacType)
               && theKeySpec != null
                                     ? theKeySpec.getBlockLength().getByteLength()
                                     : 0;
    }

    /**
     * List all possible macSpecs.
     * @return the list
     */
    public static List<GordianMacSpec> listAll() {
        /* Create the array list */
        final List<GordianMacSpec> myList = new ArrayList<>();

        /* For each digestSpec */
        for (final GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            myList.add(GordianMacSpec.hMac(mySpec));
        }

        /* For each SymKey */
        for (final GordianSymKeySpec mySymKeySpec : GordianSymKeySpec.listAll()) {
            myList.add(GordianMacSpec.gMac(mySymKeySpec));
            myList.add(GordianMacSpec.cMac(mySymKeySpec));
            myList.add(GordianMacSpec.poly1305Mac(mySymKeySpec));
        }

        /* For each length */
        for (final GordianLength myLength : GordianLength.values()) {
            myList.add(GordianMacSpec.skeinMac(myLength));
            if (GordianDigestType.SKEIN.getExtendedStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.skeinMac(myLength, true));
            }
        }

        /* Add vmpcMac */
        myList.add(GordianMacSpec.vmpcMac());

        /* Add blakeMac */
        for (final GordianLength myLength : GordianDigestType.BLAKE.getSupportedLengths()) {
            myList.add(GordianMacSpec.blakeMac(myLength));
        }

        /* Add kalynaMac */
        for (final GordianLength myLength : GordianSymKeyType.KALYNA.getSupportedLengths()) {
            myList.add(GordianMacSpec.kalynaMac(myLength));
        }

        /* Add kupynaMac */
        for (final GordianLength myLength : GordianDigestType.KUPYNA.getSupportedLengths()) {
            myList.add(GordianMacSpec.kupynaMac(myLength));
        }

        /* Return the list */
        return myList;
    }
}
