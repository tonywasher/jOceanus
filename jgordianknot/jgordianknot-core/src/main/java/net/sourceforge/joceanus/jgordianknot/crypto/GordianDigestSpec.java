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
 * Digest Specification.
 */
public class GordianDigestSpec {
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
     * The String name.
     */
    private String theName;

    /**
     * Constructor.
     * @param pDigestType the digestType
     */
    protected GordianDigestSpec(final GordianDigestType pDigestType) {
        this(pDigestType, pDigestType.getDefaultLength());
    }

    /**
     * Constructor.
     * @param pDigestType the digestType
     * @param pLength the length
     */
    protected GordianDigestSpec(final GordianDigestType pDigestType,
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
    protected GordianDigestSpec(final GordianDigestType pDigestType,
                                final GordianLength pStateLength,
                                final GordianLength pLength) {
        /* Store parameters */
        theDigestType = pDigestType;
        theStateLength = pStateLength;
        theLength = pLength;
    }

    /**
     * Create Md5DigestSpec.
     * @return the MacSpec
     */
    public static GordianDigestSpec md5() {
        return new GordianDigestSpec(GordianDigestType.MD5);
    }

    /**
     * Create Sha1DigestSpec.
     * @return the MacSpec
     */
    public static GordianDigestSpec sha1() {
        return new GordianDigestSpec(GordianDigestType.SHA1);
    }

    /**
     * Create sm3DigestSpec.
     * @return the MacSpec
     */
    public static GordianDigestSpec sm3() {
        return new GordianDigestSpec(GordianDigestType.SM3);
    }

    /**
     * Create WhirlpoolDigestSpec.
     * @return the MacSpec
     */
    public static GordianDigestSpec whirlpool() {
        return new GordianDigestSpec(GordianDigestType.WHIRLPOOL);
    }

    /**
     * Create TigerDigestSpec.
     * @return the MacSpec
     */
    public static GordianDigestSpec tiger() {
        return new GordianDigestSpec(GordianDigestType.TIGER);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianDigestSpec sha2(final GordianLength pLength) {
        return sha2(pLength, false);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @param useExtended use extended state
     * @return the MacSpec
     */
    public static GordianDigestSpec sha2(final GordianLength pLength,
                                         final boolean useExtended) {
        return useExtended
                           ? new GordianDigestSpec(GordianDigestType.SHA2, pLength.getSha2ExtendedState(), pLength)
                           : new GordianDigestSpec(GordianDigestType.SHA2, pLength);
    }

    /**
     * Create sha3DigestSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianDigestSpec sha3(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA3, pLength);
    }

    /**
     * Create blakeDigestSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianDigestSpec blake(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE, pLength);
    }

    /**
     * Create gostDigestSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianDigestSpec gost(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.GOST, pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianDigestSpec skein(final GordianLength pLength) {
        return skein(pLength, false);
    }

    /**
     * Create skeinDigestSpec.
     * @param pLength the length
     * @param useExtended use extended state
     * @return the MacSpec
     */
    public static GordianDigestSpec skein(final GordianLength pLength,
                                          final boolean useExtended) {
        return useExtended
                           ? new GordianDigestSpec(GordianDigestType.SKEIN, pLength.getSkeinExtendedState(), pLength)
                           : new GordianDigestSpec(GordianDigestType.SKEIN, pLength.getSkeinState(), pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pStateLength the state length
     * @param pLength the length
     * @return the MacSpec
     */
    protected static GordianDigestSpec skein(final GordianLength pStateLength,
                                             final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, pStateLength, pLength);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianDigestSpec ripemd(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.RIPEMD, pLength);
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

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = theDigestType.toString();
            if (theStateLength != null) {
                theName += SEP + Integer.toString(theStateLength.getLength());
            }
            theName += SEP + Integer.toString(theLength.getLength());
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
        GordianDigestSpec myThat = (GordianDigestSpec) pThat;

        /* Check subFields */
        return theDigestType == myThat.getDigestType()
               && theStateLength == myThat.getStateLength()
               && theLength == myThat.getDigestLength();
    }

    @Override
    public int hashCode() {
        int hashCode = theDigestType.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        if (theStateLength != null) {
            hashCode += theStateLength.ordinal() + 1;
            hashCode <<= TethysDataConverter.BYTE_SHIFT;
        }
        hashCode += theLength.ordinal() + 1;
        return hashCode;
    }

    /**
     * List all possible digestSpecs.
     * @return the list
     */
    public static List<GordianDigestSpec> listAll() {
        /* Create the array list */
        List<GordianDigestSpec> myList = new ArrayList<>();

        /* For each digest type */
        for (GordianDigestType myType : GordianDigestType.values()) {
            /* For each length */
            for (GordianLength myLength : myType.getSupportedLengths()) {
                myList.add(new GordianDigestSpec(myType, myLength));
                GordianLength myState = myType.getExtendedStateForLength(myLength);
                if (myState != null) {
                    myList.add(new GordianDigestSpec(myType, myState, myLength));
                }
            }
        }

        /* Return the list */
        return myList;
    }
}
