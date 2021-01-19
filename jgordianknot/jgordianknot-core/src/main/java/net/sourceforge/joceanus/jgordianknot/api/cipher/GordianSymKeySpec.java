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
package net.sourceforge.joceanus.jgordianknot.api.cipher;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
     * @param pSymKeyType the symKeyType
     * @param pKeyLength the keyLength
     */
    public GordianSymKeySpec(final GordianSymKeyType pSymKeyType,
                             final GordianLength pKeyLength) {
        this(pSymKeyType, pSymKeyType.getDefaultBlockLength(), pKeyLength);
    }

    /**
     * Constructor.
     * @param pSymKeyType the symKeyType
     * @param pBlockLength the stateLength
     * @param pKeyLength the keyLength
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
     * Create aesKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec aes(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.AES, pKeyLength);
    }

    /**
     * Create serpentKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec serpent(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SERPENT, pKeyLength);
    }

    /**
     * Create twoFishKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec twoFish(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.TWOFISH, pKeyLength);
    }

    /**
     * Create threeFishKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec threeFish(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.THREEFISH, pKeyLength);
    }

    /**
     * Create camelliaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec camellia(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.CAMELLIA, pKeyLength);
    }

    /**
     * Create rc2KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec rc2(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.RC2, pKeyLength);
    }

    /**
     * Create rc5KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec rc5() {
        return new GordianSymKeySpec(GordianSymKeyType.RC5, GordianLength.LEN_128);
    }

    /**
     * Create rc5KeySpec.
     * @param pBlockLength the blockLength
     * @return the keySpec
     */
    public static GordianSymKeySpec rc5(final GordianLength pBlockLength) {
        return new GordianSymKeySpec(GordianSymKeyType.RC5, pBlockLength, GordianLength.LEN_128);
    }

    /**
     * Create rc6KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec rc6(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.RC6, pKeyLength);
    }

    /**
     * Create cast5KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec cast5() {
        return new GordianSymKeySpec(GordianSymKeyType.CAST5, GordianLength.LEN_128);
    }

    /**
     * Create cast6KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec cast6(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.CAST6, pKeyLength);
    }

    /**
     * Create ariaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec aria(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.ARIA, pKeyLength);
    }

    /**
     * Create sm4KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec sm4() {
        return new GordianSymKeySpec(GordianSymKeyType.SM4, GordianLength.LEN_128);
    }

    /**
     * Create noeKeonKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec noekeon() {
        return new GordianSymKeySpec(GordianSymKeyType.NOEKEON, GordianLength.LEN_128);
    }

    /**
     * Create seedKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec seed() {
        return new GordianSymKeySpec(GordianSymKeyType.SEED, GordianLength.LEN_128);
    }

    /**
     * Create teaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec tea() {
        return new GordianSymKeySpec(GordianSymKeyType.TEA, GordianLength.LEN_128);
    }

    /**
     * Create xteaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec xtea() {
        return new GordianSymKeySpec(GordianSymKeyType.XTEA, GordianLength.LEN_128);
    }

    /**
     * Create ideaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec idea() {
        return new GordianSymKeySpec(GordianSymKeyType.IDEA, GordianLength.LEN_128);
    }

    /**
     * Create skipjackKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec skipjack() {
        return new GordianSymKeySpec(GordianSymKeyType.SKIPJACK, GordianLength.LEN_128);
    }

    /**
     * Create desedeKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec desede(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.DESEDE, pKeyLength);
    }

    /**
     * Create blowfishKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec blowfish(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.BLOWFISH, pKeyLength);
    }

    /**
     * Create kalynaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec kalyna(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.KALYNA, pKeyLength);
    }

    /**
     * Create kalynaKeySpec.
     * @param pBlockLength the block length
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec kalyna(final GordianLength pBlockLength,
                                           final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.KALYNA, pBlockLength, pKeyLength);
    }

    /**
     * Create speckKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec speck(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SPECK, pKeyLength);
    }

    /**
     * Create simonKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec simon(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SIMON, pKeyLength);
    }

    /**
     * Create marsKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec mars(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.MARS, pKeyLength);
    }

    /**
     * Create anubisKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec anubis(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.ANUBIS, pKeyLength);
    }

    /**
     * Create leaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianSymKeySpec lea(final GordianLength pKeyLength) {
        return new GordianSymKeySpec(GordianSymKeyType.LEA, pKeyLength);
    }

    /**
     * Obtain symKey Type.
     * @return the symKeyType
     */
    public GordianSymKeyType getSymKeyType() {
        return theSymKeyType;
    }

    /**
     * Obtain Block Length.
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
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Check spec validity.
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
            }  else {
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

        /* Make sure that the object is a SymKeySpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target SymKeySpec */
        final GordianSymKeySpec myThat = (GordianSymKeySpec) pThat;

        /* Check subFields */
        return theSymKeyType == myThat.getSymKeyType()
                && theBlockLength == myThat.getBlockLength()
                && theKeyLength == myThat.getKeyLength();
    }

    @Override
    public int hashCode() {
        int hashCode = theSymKeyType.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        hashCode += theBlockLength.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        hashCode += theKeyLength.ordinal() + 1;
        return hashCode;
    }

    /**
     * List all possible symKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianSymKeySpec> listAll(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianSymKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each symKey type */
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* For each supported block length */
            for (final GordianLength myBlkLen : myType.getSupportedBlockLengths()) {
                /* Add spec if valid for blkLen and keyLen */
                if (myType.validBlockAndKeyLengths(myBlkLen, pKeyLen)) {
                    myList.add(new GordianSymKeySpec(myType, myBlkLen, pKeyLen));
                }
            }
        }

        /* Return the list */
        return myList;
    }
}
