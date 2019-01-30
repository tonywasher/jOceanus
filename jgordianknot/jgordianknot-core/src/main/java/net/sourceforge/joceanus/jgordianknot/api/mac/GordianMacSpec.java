/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Mac Specification.
 */
public final class GordianMacSpec implements GordianKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * Initialisation Vector size (128/8).
     */
    private static final int IVSIZE = 16;

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
    public GordianMacSpec(final GordianMacType pMacType,
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
    public GordianMacSpec(final GordianMacType pMacType,
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
    public GordianMacSpec(final GordianMacType pMacType) {
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
        final GordianDigestSpec mySpec = GordianDigestSpec.skein(pLength);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMacAlt(final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.skeinAlt(pLength);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pState the state length
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pState,
                                          final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.skein(pState, pLength);
        return new GordianMacSpec(GordianMacType.SKEIN, mySpec);
    }

    /**
     * Create blakeMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac() {
        return GordianMacSpec.blakeMac(GordianDigestType.BLAKE.getDefaultLength());
    }

    /**
     * Create blakeMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac(final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.blake(pLength);
        return new GordianMacSpec(GordianMacType.BLAKE, mySpec);
    }

    /**
     * Create blakeMacSpec.
     * @param pState the state length
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac(final GordianLength pState,
                                          final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.blake(pState, pLength);
        return new GordianMacSpec(GordianMacType.BLAKE, mySpec);
    }

    /**
     * Create blakeMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMacAlt(final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.blakeAlt(pLength);
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

    /**
     * Obtain the IV length.
     * @return the IV Length
     */
    public int getIVLen() {
        switch (theMacType) {
            case VMPC:
            case GMAC:
            case POLY1305:
            case SKEIN:
                return IVSIZE;
            case BLAKE:
                return GordianDigestType.isBlake2bState(theDigestSpec.getStateLength())
                       ? IVSIZE
                       : IVSIZE >> 1;
            default:
                return 0;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theMacType.toString();
            switch (theMacType) {
                case HMAC:
                    theName += SEP + theDigestSpec;
                    break;
                case SKEIN:
                    if (theDigestSpec != null) {
                        theName += SEP + theDigestSpec.getStateLength()
                                + SEP + theDigestSpec.getDigestLength();
                    }
                    break;
                case GMAC:
                case CMAC:
                case POLY1305:
                    theName += SEP + theKeySpec;
                    break;
                case KUPYNA:
                    if (theDigestSpec != null) {
                        theName += SEP + theDigestSpec.getDigestLength();
                    }
                    break;
                case BLAKE:
                    if (theDigestSpec != null) {
                        theName = GordianDigestType.getBlakeAlgorithmForStateLength(theDigestSpec.getStateLength());
                        theName += "Mac" + SEP + theDigestSpec.getDigestLength();
                    }
                    break;
                case KALYNA:
                    if (theKeySpec != null) {
                        theName += SEP + theKeySpec.getBlockLength();
                    }
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

        /* Add SkeinMacs */
        for (final GordianLength myLength : GordianDigestType.SKEIN.getSupportedLengths()) {
            myList.add(GordianMacSpec.skeinMac(myLength));
            if (GordianDigestType.SKEIN.getAlternateStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.skeinMacAlt(myLength));
            }
        }

        /* Add vmpcMac */
        myList.add(GordianMacSpec.vmpcMac());

        /* Add blakeMac */
        for (final GordianLength myLength : GordianDigestType.BLAKE.getSupportedLengths()) {
            myList.add(GordianMacSpec.blakeMac(myLength));
            if (GordianDigestType.BLAKE.getAlternateStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.blakeMacAlt(myLength));
            }
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
