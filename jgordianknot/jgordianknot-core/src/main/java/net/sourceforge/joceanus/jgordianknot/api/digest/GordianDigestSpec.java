/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianIdSpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
     * Create Md2DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec md2() {
        return new GordianDigestSpec(GordianDigestType.MD2);
    }

    /**
     * Create Md4DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec md4() {
        return new GordianDigestSpec(GordianDigestType.MD4);
    }

    /**
     * Create Md5DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec md5() {
        return new GordianDigestSpec(GordianDigestType.MD5);
    }

    /**
     * Create Sha1DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha1() {
        return new GordianDigestSpec(GordianDigestType.SHA1);
    }

    /**
     * Create sm3DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec sm3() {
        return new GordianDigestSpec(GordianDigestType.SM3);
    }

    /**
     * Create WhirlpoolDigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec whirlpool() {
        return new GordianDigestSpec(GordianDigestType.WHIRLPOOL);
    }

    /**
     * Create TigerDigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec tiger() {
        return new GordianDigestSpec(GordianDigestType.TIGER);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha2(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA2, null, pLength);
    }

    /**
     * Create sha2 alternate DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha2Alt(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA2, GordianDigestType.SHA2.getAlternateStateForLength(pLength), pLength);
    }

    /**
     * Create sha3DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec sha3(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHA3, pLength);
    }

    /**
     * Create blake2bDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake2(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE2, GordianDigestType.BLAKE2.getStateForLength(pLength), pLength);
    }

    /**
     * Create blake2sDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake2Alt(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE2, GordianDigestType.BLAKE2.getAlternateStateForLength(pLength), pLength);
    }

    /**
     * Create blake2DigestSpec.
     * @param pStateLength the state length
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake2(final GordianLength pStateLength,
                                          final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE2, pStateLength, pLength);
    }

    /**
     * Create blake3DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec blake3(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.BLAKE3, pLength);
    }

    /**
     * Create gostDigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec gost() {
        return new GordianDigestSpec(GordianDigestType.GOST);
    }

    /**
     * Create streebogDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec streebog(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.STREEBOG, pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec skein(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, GordianDigestType.SKEIN.getStateForLength(pLength), pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec skeinAlt(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, GordianDigestType.SKEIN.getAlternateStateForLength(pLength), pLength);
    }

    /**
     * Create skeinDigestSpec.
     * @param pStateLength the state length
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec skein(final GordianLength pStateLength,
                                          final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SKEIN, pStateLength, pLength);
    }

    /**
     * Create sha2DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec ripemd(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.RIPEMD, pLength);
    }

    /**
     * Create shake128DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec shake128(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHAKE, GordianLength.LEN_128, pLength);
    }

    /**
     * Create shake256DigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec shake256(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.SHAKE, GordianLength.LEN_256, pLength);
    }

    /**
     * Create kupynaDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec kupyna(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.KUPYNA, pLength);
    }

    /**
     * Create jhDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec jh(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.JH, pLength);
    }

    /**
     * Create groestlDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec groestl(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.GROESTL, pLength);
    }

    /**
     * Create cubeHashDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec cubeHash(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.CUBEHASH, pLength);
    }

    /**
     * Create kangarooDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec kangaroo(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.KANGAROO, GordianLength.LEN_128, pLength);
    }

    /**
     * Create marsupimalDigestSpec.
     * @param pLength the length
     * @return the DigestSpec
     */
    public static GordianDigestSpec marsupimal(final GordianLength pLength) {
        return new GordianDigestSpec(GordianDigestType.KANGAROO, GordianLength.LEN_256, pLength);
    }

    /**
     * Create haraka256DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec haraka256() {
        return new GordianDigestSpec(GordianDigestType.HARAKA, GordianLength.LEN_256);
    }

    /**
     * Create haraka512DigestSpec.
     * @return the DigestSpec
     */
    public static GordianDigestSpec haraka512() {
        return new GordianDigestSpec(GordianDigestType.HARAKA, GordianLength.LEN_512, GordianLength.LEN_256);
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
        final List<GordianDigestSpec> myList = new ArrayList<>();

        /* For each digest type */
        for (final GordianDigestType myType : GordianDigestType.values()) {
            /* For each length */
            for (final GordianLength myLength : myType.getSupportedLengths()) {
                myList.add(new GordianDigestSpec(myType, myLength));
                final GordianLength myState = myType.getAlternateStateForLength(myLength);
                if (myState != null) {
                    myList.add(new GordianDigestSpec(myType, myState, myLength));
                }
            }
        }

        /* Return the list */
        return myList;
    }
}
