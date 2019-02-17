/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * SymKey specification.
 */
public class GordianSymKeySpec implements GordianKeySpec {
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
     */
    public GordianSymKeySpec(final GordianSymKeyType pSymKeyType) {
        this(pSymKeyType, pSymKeyType.getDefaultLength());
    }

    /**
     * Constructor.
     * @param pSymKeyType the symKeyType
     * @param pBlockLength the stateLength
     */
    public GordianSymKeySpec(final GordianSymKeyType pSymKeyType,
                             final GordianLength pBlockLength) {
        /* Store parameters */
        theSymKeyType = pSymKeyType;
        theBlockLength = pBlockLength;
        isValid = checkValidity();
    }

    /**
     * Create AesKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec aes() {
        return new GordianSymKeySpec(GordianSymKeyType.AES);
    }

    /**
     * Create SerpentKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec serpent() {
        return new GordianSymKeySpec(GordianSymKeyType.SERPENT);
    }

    /**
     * Create TwoFishKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec twofish() {
        return new GordianSymKeySpec(GordianSymKeyType.TWOFISH);
    }

    /**
     * Create ThreeFishKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec threefish() {
        return new GordianSymKeySpec(GordianSymKeyType.THREEFISH);
    }

    /**
     * Create CamelliaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec camellia() {
        return new GordianSymKeySpec(GordianSymKeyType.CAMELLIA);
    }

    /**
     * Create rc2KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec rc2() {
        return new GordianSymKeySpec(GordianSymKeyType.RC2);
    }

    /**
     * Create rc5KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec rc5() {
        return new GordianSymKeySpec(GordianSymKeyType.RC5);
    }

    /**
     * Create rc6KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec rc6() {
        return new GordianSymKeySpec(GordianSymKeyType.RC6);
    }

    /**
     * Create cast5KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec cast5() {
        return new GordianSymKeySpec(GordianSymKeyType.CAST5);
    }

    /**
     * Create cast6KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec cast6() {
        return new GordianSymKeySpec(GordianSymKeyType.CAST6);
    }

    /**
     * Create ariaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec aria() {
        return new GordianSymKeySpec(GordianSymKeyType.ARIA);
    }

    /**
     * Create sm4KeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec sm4() {
        return new GordianSymKeySpec(GordianSymKeyType.SM4);
    }

    /**
     * Create noekeonKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec noekeon() {
        return new GordianSymKeySpec(GordianSymKeyType.NOEKEON);
    }

    /**
     * Create seedKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec seed() {
        return new GordianSymKeySpec(GordianSymKeyType.SEED);
    }

    /**
     * Create teaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec tea() {
        return new GordianSymKeySpec(GordianSymKeyType.TEA);
    }

    /**
     * Create xteaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec xtea() {
        return new GordianSymKeySpec(GordianSymKeyType.XTEA);
    }

    /**
     * Create ideaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec idea() {
        return new GordianSymKeySpec(GordianSymKeyType.IDEA);
    }

    /**
     * Create skipjackKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec skipjack() {
        return new GordianSymKeySpec(GordianSymKeyType.SKIPJACK);
    }

    /**
     * Create desedeKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec desede() {
        return new GordianSymKeySpec(GordianSymKeyType.DESEDE);
    }

    /**
     * Create blowfishKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec blowfish() {
        return new GordianSymKeySpec(GordianSymKeyType.BLOWFISH);
    }

    /**
     * Create kalynaKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec kalyna() {
        return new GordianSymKeySpec(GordianSymKeyType.KALYNA);
    }

    /**
     * Create kalynaKeySpec.
     * @param pBlockLength the block length
     * @return the keySpec
     */
    public static GordianSymKeySpec kalyna(final GordianLength pBlockLength) {
        return new GordianSymKeySpec(GordianSymKeyType.KALYNA, pBlockLength);
    }

    /**
     * Create speckKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec speck() {
        return new GordianSymKeySpec(GordianSymKeyType.SPECK);
    }

    /**
     * Create speckKeySpec.
     * @param pBlockLength the block length
     * @return the keySpec
     */
    public static GordianSymKeySpec speck(final GordianLength pBlockLength) {
        return new GordianSymKeySpec(GordianSymKeyType.SPECK, pBlockLength);
    }

    /**
     * Create anubisKeySpec.
     * @return the keySpec
     */
    public static GordianSymKeySpec anubis() {
        return new GordianSymKeySpec(GordianSymKeyType.ANUBIS);
    }

    /**
     * Obtain symKey Type.
     * @return the symKeyType
     */
    public GordianSymKeyType getSymKeyType() {
        return theSymKeyType;
    }

    /**
     * Obtain State Length.
     * @return the Length
     */
    public GordianLength getBlockLength() {
        return theBlockLength;
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
        return theSymKeyType != null && theBlockLength != null;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = theSymKeyType.toString();
                if (theSymKeyType.hasMultipleLengths()) {
                    int myLen = theBlockLength.getLength();
                    if (GordianSymKeyType.RC5.equals(theSymKeyType)) {
                        myLen >>= 1;
                    }
                    theName += SEP + myLen;
                }
            }  else {
                /* Report invalid spec */
                theName = "InvalidSymKeySpec: " + theSymKeyType + ":" + theBlockLength;
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
                && theBlockLength == myThat.getBlockLength();
    }

    @Override
    public int hashCode() {
        int hashCode = theSymKeyType.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        hashCode += theBlockLength.ordinal() + 1;
        return hashCode;
    }

    /**
     * List all possible symKeySpecs.
     * @return the list
     */
    public static List<GordianSymKeySpec> listAll() {
        /* Create the array list */
        final List<GordianSymKeySpec> myList = new ArrayList<>();

        /* For each symKey type */
        for (final GordianSymKeyType myType : GordianSymKeyType.values()) {
            /* For each length */
            for (final GordianLength myLength : myType.getSupportedLengths()) {
                myList.add(new GordianSymKeySpec(myType, myLength));
            }
        }

        /* Return the list */
        return myList;
    }
}
