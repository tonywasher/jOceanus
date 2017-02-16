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
        theDigestType = pDigestType;
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
        return new GordianDigestSpec(GordianDigestType.SHA2, pLength);
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
        return new GordianDigestSpec(GordianDigestType.SKEIN, pLength);
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
            theName += SEP + theLength.toString();
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

        /* Check DigestType and length */
        return theDigestType.equals(myThat.getDigestType())
               && theLength.equals(myThat.getDigestLength());
    }

    @Override
    public int hashCode() {
        int hashCode = theDigestType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theLength.ordinal();
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
            }
        }

        /* Return the list */
        return myList;
    }
}
