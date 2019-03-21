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
package net.sourceforge.joceanus.jgordianknot.api.mac;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeyType;
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
     * The Mac Type.
     */
    private final GordianMacType theMacType;

    /**
     * The Subpec.
     */
    private final Object theSubSpec;

    /**
     * The Validity.
     */
    private final boolean isValid;

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
        theSubSpec = pDigestSpec;
        isValid = checkValidity();
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
        theSubSpec = pKeySpec;
        isValid = checkValidity();
    }

    /**
     * zucMac Constructor.
     * @param pMacType the macType
     * @param pLength the length
     */
    public GordianMacSpec(final GordianMacType pMacType,
                          final GordianLength pLength) {
        /* Store parameters */
        theMacType = pMacType;
        theSubSpec = pLength;
        isValid = checkValidity();
    }

    /**
     * sipHash Constructor.
     * @param pMacType the macType
     * @param pFast use fast hash true/false
     */
    public GordianMacSpec(final GordianMacType pMacType,
                          final Boolean pFast) {
        /* Store parameters */
        theMacType = pMacType;
        theSubSpec = pFast;
        isValid = checkValidity();
    }

    /**
     * vmpcMac Constructor.
     * @param pMacType the macType
     */
    public GordianMacSpec(final GordianMacType pMacType) {
        theMacType = pMacType;
        theSubSpec = null;
        isValid = checkValidity();
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
        return GordianMacSpec.kalynaMac(GordianSymKeyType.KALYNA.getDefaultBlockLength());
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
     * Create gostMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec gostMac() {
        return new GordianMacSpec(GordianMacType.GOST);
    }

    /**
     * Create sipHashSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec sipHash() {
        return new GordianMacSpec(GordianMacType.SIPHASH, Boolean.FALSE);
    }

    /**
     * Create sipHashFastSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec sipHashFast() {
        return new GordianMacSpec(GordianMacType.SIPHASH, Boolean.TRUE);
    }

    /**
     * Create cbcMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec cbcMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.CBCMAC, pSymKeySpec);
    }

    /**
     * Create cfbMacSpec.
     * @param pSymKeySpec the symKeySpec
     * @return the MacSpec
     */
    public static GordianMacSpec cfbMac(final GordianSymKeySpec pSymKeySpec) {
        return new GordianMacSpec(GordianMacType.CFBMAC, pSymKeySpec);
    }

    /**
     * Create zucMacSpec.
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec zucMac(final GordianLength pLength) {
        return new GordianMacSpec(GordianMacType.ZUC, pLength);
    }

    /**
     * Obtain Mac Type.
     * @return the MacType
     */
    public GordianMacType getMacType() {
        return theMacType;
    }

    /**
     * Obtain SubSpec.
     * @return the SubSpec
     */
    public Object getSubSpec() {
        return theSubSpec;
    }

    /**
     * Is the macSpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain DigestSpec.
     * @return the DigestSpec
     */
    public GordianDigestSpec getDigestSpec() {
        return theSubSpec instanceof GordianDigestSpec
               ? (GordianDigestSpec) theSubSpec
               : GordianDigestSpec.sha1();
    }

    /**
     * Obtain SymKeySpec.
     * @return the SymKeySpec
     */
    public GordianSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianSymKeySpec
               ? (GordianSymKeySpec) theSubSpec
               : GordianSymKeySpec.aes();
    }

    /**
     * Obtain Boolean.
     * @return the Boolean
     */
    public Boolean getBoolean() {
        return theSubSpec instanceof Boolean
               ? (Boolean) theSubSpec
               : Boolean.FALSE;
    }

    /**
     * Obtain MacLength.
     * @return the Length
     */
    public GordianLength getMacLength() {
        switch (theMacType) {
            case HMAC:
            case BLAKE:
            case SKEIN:
            case KUPYNA:
                return getDigestSpec().getDigestLength();
            case GMAC:
            case POLY1305:
                return GordianLength.LEN_128;
            case CMAC:
            case KALYNA:
                return getSymKeySpec().getBlockLength();
            case CBCMAC:
            case CFBMAC:
                return getSymKeySpec().getHalfBlockLength();
            case ZUC:
                return ((GordianLength) theSubSpec);
            case VMPC:
                return GordianLength.LEN_160;
            case GOST:
                return GordianLength.LEN_32;
            case SIPHASH:
            default:
                return GordianLength.LEN_64;
        }
    }

    /**
     * Obtain the IV length.
     * @param pKeyLen the keyLength
     * @return the IV Length
     */
    public int getIVLen(final GordianLength pKeyLen) {
        switch (theMacType) {
            case VMPC:
            case POLY1305:
            case SKEIN:
                return GordianLength.LEN_128.getByteLength();
            case BLAKE:
                return GordianDigestType.isBlake2bState(getDigestSpec().getStateLength())
                       ? GordianLength.LEN_128.getByteLength()
                       : GordianLength.LEN_64.getByteLength();
            case GMAC:
                return GordianLength.LEN_96.getByteLength();
            case CBCMAC:
            case CFBMAC:
                return getSymKeySpec().getBlockLength().getByteLength();
            case GOST:
                return GordianLength.LEN_64.getByteLength();
            case ZUC:
                return GordianStreamKeyType.ZUC.getIVLength(pKeyLen);
            default:
                return 0;
        }
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        if (theMacType == null) {
            return false;
        }
        switch (theMacType) {
            case HMAC:
            case KUPYNA:
            case BLAKE:
            case SKEIN:
                return theSubSpec instanceof GordianDigestSpec
                        && ((GordianDigestSpec) theSubSpec).isValid();
            case CMAC:
            case GMAC:
            case POLY1305:
            case KALYNA:
            case CBCMAC:
            case CFBMAC:
                return theSubSpec instanceof GordianSymKeySpec
                        && ((GordianSymKeySpec) theSubSpec).isValid();
            case ZUC:
                return theSubSpec instanceof GordianLength;
            case SIPHASH:
                return theSubSpec instanceof Boolean;
            case GOST:
            case VMPC:
                 return theSubSpec == null;
            default:
                return false;
        }
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the macSpec is invalid */
            if (!isValid) {
                /* Report invalid spec */
                theName = "InvalidMacSpec: " + theMacType + ":" + theSubSpec;
                return theName;
            }

            /* Load the name */
            theName = theMacType.toString();
            if (theSubSpec != null) {
                if (GordianMacType.SIPHASH.equals(theMacType)) {
                    theName += SEP + (getBoolean() ? "2-4" : "4-8");
                } else {
                    theName += SEP + theSubSpec.toString();
                }
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

        /* Match subSpec */
        return Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        int hashCode = theMacType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        if (theSubSpec != null) {
            hashCode += theSubSpec.hashCode();
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
            myList.add(GordianMacSpec.cbcMac(mySymKeySpec));
            myList.add(GordianMacSpec.cfbMac(mySymKeySpec));
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

        /* Add spiHash */
        myList.add(GordianMacSpec.sipHashFast());
        myList.add(GordianMacSpec.sipHash());

        /* Add gostHash */
        myList.add(GordianMacSpec.gostMac());

        /* Add blakeMac */
        for (final GordianLength myLength : GordianDigestType.BLAKE.getSupportedLengths()) {
            myList.add(GordianMacSpec.blakeMac(myLength));
            if (GordianDigestType.BLAKE.getAlternateStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.blakeMacAlt(myLength));
            }
        }

        /* Add kalynaMac */
        for (final GordianLength myLength : GordianSymKeyType.KALYNA.getSupportedBlockLengths()) {
            myList.add(GordianMacSpec.kalynaMac(myLength));
        }

        /* Add kupynaMac */
        for (final GordianLength myLength : GordianDigestType.KUPYNA.getSupportedLengths()) {
            myList.add(GordianMacSpec.kupynaMac(myLength));
        }

        /* Add zucMac */
        myList.add(GordianMacSpec.zucMac(GordianLength.LEN_32));
        myList.add(GordianMacSpec.zucMac(GordianLength.LEN_64));
        myList.add(GordianMacSpec.zucMac(GordianLength.LEN_128));

        /* Return the list */
        return myList;
    }
}
