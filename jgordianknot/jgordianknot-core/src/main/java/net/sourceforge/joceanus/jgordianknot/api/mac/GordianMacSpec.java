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
     * The KeyLength.
     */
    private final GordianLength theKeyLength;

    /**
     * The SubSpec.
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
     * @param pKeyLength the keyLength
     * @param pDigestSpec the digestSpec
     */
    public GordianMacSpec(final GordianMacType pMacType,
                          final GordianLength pKeyLength,
                          final GordianDigestSpec pDigestSpec) {
        /* Store parameters */
        theMacType = pMacType;
        theKeyLength = pKeyLength;
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
        /* Store macType */
        theMacType = pMacType;

        /* Special handling for Poly1305 */
        theKeyLength = GordianMacType.POLY1305 == pMacType
                            ? GordianLength.LEN_256
                            : pKeySpec.getKeyLength();
        theSubSpec = pKeySpec;
        isValid = checkValidity();
    }

    /**
     * zucMac Constructor.
     * @param pMacType the macType
     * @param pKeyLength the keyLength
     * @param pLength the length
     */
    public GordianMacSpec(final GordianMacType pMacType,
                          final GordianLength pKeyLength,
                          final GordianLength pLength) {
        /* Store parameters */
        theMacType = pMacType;
        theKeyLength = pKeyLength;
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
        theKeyLength = GordianLength.LEN_128;
        theSubSpec = pFast;
        isValid = checkValidity();
    }

    /**
     * vmpcMac Constructor.
     * @param pKeyLength the keyLength
     * @param pMacType the macType
     */
    public GordianMacSpec(final GordianMacType pMacType,
                          final GordianLength pKeyLength) {
        theMacType = pMacType;
        theKeyLength = pKeyLength;
        theSubSpec = null;
        isValid = checkValidity();
    }

    /**
     * Create hMacSpec.
     * @param pDigestType the digestType
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestType pDigestType) {
        return hMac(new GordianDigestSpec(pDigestType), GordianLength.LEN_128);
    }

    /**
     * Create hMacSpec.
     * @param pDigestType the digestType
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestType pDigestType,
                                      final GordianLength pKeyLength) {
        return hMac(new GordianDigestSpec(pDigestType), pKeyLength);
    }

    /**
     * Create hMacSpec.
     * @param pDigestSpec the digestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestSpec pDigestSpec) {
        return new GordianMacSpec(GordianMacType.HMAC, pDigestSpec.getDigestLength(), pDigestSpec);
    }

    /**
     * Create hMacSpec.
     * @param pDigestSpec the digestSpec
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec hMac(final GordianDigestSpec pDigestSpec,
                                      final GordianLength pKeyLength) {
        return new GordianMacSpec(GordianMacType.HMAC, pKeyLength, pDigestSpec);
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
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength) {
        return skeinMac(pKeyLength, GordianDigestType.SKEIN.getDefaultLength());
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                          final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.skein(pLength);
        return new GordianMacSpec(GordianMacType.SKEIN, pKeyLength, mySpec);
    }

    /**
     * Create skeinMacSpec.
     * @param pKeyLength the keyLength
     * @param pSpec the skeinDigestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec skeinMac(final GordianLength pKeyLength,
                                          final GordianDigestSpec pSpec) {
        return new GordianMacSpec(GordianMacType.SKEIN, pKeyLength, pSpec);
    }

    /**
     * Create blakeMacSpec.
     * @param pKeyLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac(final GordianLength pKeyLength) {
        return GordianMacSpec.blakeMac(pKeyLength, GordianDigestType.BLAKE.getDefaultLength());
    }

    /**
     * Create blakeMacSpec.
     * @param pKeyLength the length
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac(final GordianLength pKeyLength,
                                          final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.blake(pLength);
        return GordianMacSpec.blakeMac(pKeyLength, mySpec);
    }

    /**
     * Create blakeMacSpec.
     * @param pKeyLength the keyLength
     * @param pSpec the blake digestSpec
     * @return the MacSpec
     */
    public static GordianMacSpec blakeMac(final GordianLength pKeyLength,
                                          final GordianDigestSpec pSpec) {
        return new GordianMacSpec(GordianMacType.BLAKE, pKeyLength, pSpec);
    }

    /**
     * Create kalynaMacSpec.
     * @param pKeySpec the keySpec
     * @return the MacSpec
     */
    public static GordianMacSpec kalynaMac(final GordianSymKeySpec pKeySpec) {
        return new GordianMacSpec(GordianMacType.KALYNA, pKeySpec);
    }

    /**
     * Create kupynaMacSpec.
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec kupynaMac(final GordianLength pKeyLength) {
        return GordianMacSpec.kupynaMac(pKeyLength, GordianDigestType.KUPYNA.getDefaultLength());
    }

    /**
     * Create kupynaMacSpec.
     * @param pKeyLength the keyLength
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec kupynaMac(final GordianLength pKeyLength,
                                           final GordianLength pLength) {
        final GordianDigestSpec mySpec = GordianDigestSpec.kupyna(pLength);
        return new GordianMacSpec(GordianMacType.KUPYNA, pKeyLength, mySpec);
    }

    /**
     * Create vmpcMacSpec.
     * @param pKeyLength the keyLength
     * @return the MacSpec
     */
    public static GordianMacSpec vmpcMac(final GordianLength pKeyLength) {
        return new GordianMacSpec(GordianMacType.VMPC, pKeyLength);
    }

    /**
     * Create gostMacSpec.
     * @return the MacSpec
     */
    public static GordianMacSpec gostMac() {
        return new GordianMacSpec(GordianMacType.GOST, GordianLength.LEN_256);
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
     * @param pKeyLength the keyLength
     * @param pLength the length
     * @return the MacSpec
     */
    public static GordianMacSpec zucMac(final GordianLength pKeyLength,
                                        final GordianLength pLength) {
        return new GordianMacSpec(GordianMacType.ZUC, pKeyLength, pLength);
    }

    /**
     * Obtain Mac Type.
     * @return the MacType
     */
    public GordianMacType getMacType() {
        return theMacType;
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
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
               : null;
    }

    /**
     * Obtain DigestStateLength.
     * @return the StateLength
     */
    private GordianLength getDigestStateLength() {
        return theSubSpec instanceof GordianDigestSpec
               ? ((GordianDigestSpec) theSubSpec).getStateLength()
               : null;
    }

    /**
     * Obtain DigestLength.
     * @return the Length
     */
    private GordianLength getDigestLength() {
        return theSubSpec instanceof GordianDigestSpec
               ? ((GordianDigestSpec) theSubSpec).getDigestLength()
               : null;
    }

    /**
     * Obtain SymKeySpec.
     * @return the SymKeySpec
     */
    public GordianSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianSymKeySpec
               ? (GordianSymKeySpec) theSubSpec
               : null;
    }

    /**
     * Obtain SymKeyType.
     * @return the Type
     */
    private GordianSymKeyType getSymKeyType() {
        return theSubSpec instanceof GordianSymKeySpec
               ? ((GordianSymKeySpec) theSubSpec).getSymKeyType()
               : null;
    }

    /**
     * Obtain SymKeyBlockLength.
     * @return the BlockLength
     */
    private GordianLength getSymKeyBlockLength() {
        return theSubSpec instanceof GordianSymKeySpec
               ? ((GordianSymKeySpec) theSubSpec).getBlockLength()
               : null;
    }

    /**
     * Obtain SymKeyBlockLength.
     * @return the BlockLength
     */
    private int getSymKeyBlockByteLength() {
        return theSubSpec instanceof GordianSymKeySpec
               ? ((GordianSymKeySpec) theSubSpec).getBlockLength().getByteLength()
               : null;
    }

    /**
     * Obtain SymKeyHalfBlockLength.
     * @return the HalfBlockLength
     */
    private GordianLength getSymKeyHalfBlockLength() {
        return theSubSpec instanceof GordianSymKeySpec
               ? ((GordianSymKeySpec) theSubSpec).getHalfBlockLength()
               : null;
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
                return getDigestLength();
            case GMAC:
            case POLY1305:
                return GordianLength.LEN_128;
            case CMAC:
            case KALYNA:
                return getSymKeyBlockLength();
            case CBCMAC:
            case CFBMAC:
                return getSymKeyHalfBlockLength();
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
                return GordianDigestType.isBlake2bState(getDigestStateLength())
                       ? GordianLength.LEN_128.getByteLength()
                       : GordianLength.LEN_64.getByteLength();
            case GMAC:
                return GordianLength.LEN_96.getByteLength();
            case CBCMAC:
            case CFBMAC:
                return getSymKeyBlockByteLength();
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
        /* Make sure that macType and keyLength are non-null */
        if (theMacType == null || theKeyLength == null) {
            return false;
        }

        /* Switch on MacType */
        switch (theMacType) {
            case HMAC:
                return checkDigestValidity(null);
            case KUPYNA:
                return checkDigestValidity(GordianDigestType.KUPYNA);
            case SKEIN:
                return checkDigestValidity(GordianDigestType.SKEIN);
            case BLAKE:
                return checkBlakeValidity();
            case KALYNA:
                return checkSymKeyValidity(GordianSymKeyType.KALYNA);
            case CMAC:
            case GMAC:
            case CBCMAC:
            case CFBMAC:
                return checkSymKeyValidity(null);
            case POLY1305:
                return checkPoly1305Validity();
            case ZUC:
                return checkZucValidity();
            case SIPHASH:
                return theSubSpec instanceof Boolean && theKeyLength == GordianLength.LEN_128;
            case GOST:
                return theSubSpec == null && theKeyLength == GordianLength.LEN_256;
            case VMPC:
                 return theSubSpec == null;
            default:
                return false;
        }
    }

    /**
     * Check digest validity.
     * @param pDigestType required digestType (or null)
     * @return valid true/false
     */
    private boolean checkDigestValidity(final GordianDigestType pDigestType) {
        /* Check that the digestSpec is valid */
        if (!(theSubSpec instanceof GordianDigestSpec)
                || !((GordianDigestSpec) theSubSpec).isValid()) {
            return false;
        }

        /* Check for digestType restrictions */
        return  pDigestType == null
                || ((GordianDigestSpec) theSubSpec).getDigestType() == pDigestType;
    }

    /**
     * Check symKey validity.
     * @param pSymKeyType required symKeyType (or null)
     * @return valid true/false
     */
    private boolean checkSymKeyValidity(final GordianSymKeyType pSymKeyType) {
        /* Check that the symKeySpec is valid */
        if (!(theSubSpec instanceof GordianSymKeySpec)
                || !((GordianSymKeySpec) theSubSpec).isValid()) {
            return false;
        }

        /* Check for symKeyType restrictions */
        return  pSymKeyType == null
                || ((GordianSymKeySpec) theSubSpec).getSymKeyType() == pSymKeyType;
    }

    /**
     * Check poly1305 validity.
     * @return valid true/false
     */
    private boolean checkPoly1305Validity() {
        /* Check that the spec is reasonable */
        if (!checkSymKeyValidity(null)) {
            return false;
        }

        /* Restrict keyLengths */
        final GordianSymKeySpec mySpec = (GordianSymKeySpec) theSubSpec;
        return mySpec.getKeyLength() == GordianLength.LEN_128
               && theKeyLength == GordianLength.LEN_256;
    }

    /**
     * Check blake validity.
     * @return valid true/false
     */
    private boolean checkBlakeValidity() {
        /* Check that the spec is reasonable */
        if  (!checkDigestValidity(GordianDigestType.BLAKE)) {
            return false;
        }

        /* Check keyLength */
        return checkBlakeKeyLength(theKeyLength, (GordianDigestSpec) theSubSpec);
    }

    /**
     * Check blake keyLength validity.
     * @param pKeyLen the keyLength
     * @param pSpec the digestSpec
     * @return valid true/false
     */
    private static boolean checkBlakeKeyLength(final GordianLength pKeyLen,
                                               final GordianDigestSpec pSpec) {
        /* Key length must be less or equal to the stateLength */
        return pKeyLen.getLength() <= pSpec.getStateLength().getLength();
    }

    /**
     * Check zuc validity.
     * @return valid true/false
     */
    private boolean checkZucValidity() {
        switch (theKeyLength) {
            case LEN_128:
                return GordianLength.LEN_32 == theSubSpec;
            case LEN_256:
                return GordianLength.LEN_32 == theSubSpec
                        || GordianLength.LEN_64 == theSubSpec
                        || GordianLength.LEN_128 == theSubSpec;
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
                theName = "InvalidMacSpec: " + theMacType + ":" + theKeyLength + ":" + theSubSpec;
                return theName;
            }

            /* Load the name */
            theName = theMacType.toString();
            switch (theMacType) {
                case SIPHASH:
                    theName += SEP + (getBoolean()
                                      ? "2-4"
                                      : "4-8");
                    break;
                case POLY1305:
                    theName += SEP + getSymKeyType();
                    break;
                case GMAC:
                case CMAC:
                case CFBMAC:
                case CBCMAC:
                    theName += SEP + theSubSpec.toString();
                    break;
                case KALYNA:
                    theName += theKeyLength + SEP + getSymKeyBlockLength();
                    break;
                case KUPYNA:
                    theName += theKeyLength + SEP + getDigestLength();
                    break;
                case SKEIN:
                    theName += theKeyLength + SEP + getDigestStateLength() + SEP + getDigestLength();
                    break;
                case HMAC:
                case ZUC:
                    theName += theKeyLength + SEP + theSubSpec.toString();
                    break;
                case BLAKE:
                    theName = GordianDigestType.getBlakeAlgorithmForStateLength(getDigestStateLength())
                                 + "Mac" + theKeyLength + SEP + getDigestLength();
                    break;
                case VMPC:
                    theName += theKeyLength;
                    break;
                case GOST:
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

        /* Check MacType and keyLength */
        if (theMacType != myThat.getMacType()) {
            return false;
        }
        if (theKeyLength != myThat.getKeyLength()) {
            return false;
        }

        /* Match subSpec */
        return Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        int hashCode = theMacType.ordinal() << TethysDataConverter.BYTE_SHIFT;
        hashCode += theKeyLength.ordinal() << TethysDataConverter.BYTE_SHIFT;
        if (theSubSpec != null) {
            hashCode += theSubSpec.hashCode();
        }
        return hashCode;
    }

    /**
     * List all possible macSpecs for a keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianMacSpec> listAll(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianMacSpec> myList = new ArrayList<>();

        /* For each digestSpec */
        for (final GordianDigestSpec mySpec : GordianDigestSpec.listAll()) {
            /* Add the hMacSpec */
            myList.add(GordianMacSpec.hMac(mySpec, pKeyLen));
        }

        /* For each SymKey */
        for (final GordianSymKeySpec mySymKeySpec : GordianSymKeySpec.listAll(pKeyLen)) {
            /* Add gMac/cMac/cfbMac/cbcMac */
            myList.add(GordianMacSpec.gMac(mySymKeySpec));
            myList.add(GordianMacSpec.cMac(mySymKeySpec));
            myList.add(GordianMacSpec.cbcMac(mySymKeySpec));
            myList.add(GordianMacSpec.cfbMac(mySymKeySpec));

            /* Add kalynaMac for keyType of Kalyna */
            if (GordianSymKeyType.KALYNA == mySymKeySpec.getSymKeyType()) {
                myList.add(GordianMacSpec.kalynaMac(mySymKeySpec));
            }
        }

        /* Only add poly1305 for 256bit keyLengths */
        if (GordianLength.LEN_256 == pKeyLen) {
            /* For each SymKey at 128 bits*/
            for (final GordianSymKeySpec mySymKeySpec : GordianSymKeySpec.listAll(GordianLength.LEN_128)) {
                myList.add(GordianMacSpec.poly1305Mac(mySymKeySpec));
            }
        }

        /* Add kupynaMac */
        for (final GordianLength myLength : GordianDigestType.KUPYNA.getSupportedLengths()) {
            myList.add(GordianMacSpec.kupynaMac(pKeyLen, myLength));
        }

        /* Add SkeinMacs */
        for (final GordianLength myLength : GordianDigestType.SKEIN.getSupportedLengths()) {
            myList.add(GordianMacSpec.skeinMac(pKeyLen, myLength));
            if (GordianDigestType.SKEIN.getAlternateStateForLength(myLength) != null) {
                myList.add(GordianMacSpec.skeinMac(pKeyLen, GordianDigestSpec.skeinAlt(myLength)));
            }
        }

        /* Add blakeMacs */
        for (final GordianLength myLength : GordianDigestType.BLAKE.getSupportedLengths()) {
            GordianMacSpec mySpec = GordianMacSpec.blakeMac(pKeyLen, myLength);
            if (mySpec.isValid()) {
                myList.add(mySpec);
            }
            if (GordianDigestType.BLAKE.getAlternateStateForLength(myLength) != null) {
                mySpec = GordianMacSpec.blakeMac(pKeyLen, GordianDigestSpec.blakeAlt(myLength));
                if (mySpec.isValid()) {
                    myList.add(mySpec);
                }
            }
        }

        /* Add vmpcMac */
        myList.add(GordianMacSpec.vmpcMac(pKeyLen));

        /* Add sipHash for 128bit keys */
        if (GordianLength.LEN_128 == pKeyLen) {
            myList.add(GordianMacSpec.sipHashFast());
            myList.add(GordianMacSpec.sipHash());
        }

        /* Add gostHash for 256bit keys */
        if (GordianLength.LEN_256 == pKeyLen) {
            myList.add(GordianMacSpec.gostMac());
        }

        /* Add zucMac */
        if (GordianLength.LEN_128 == pKeyLen) {
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_32));
        } else if (GordianLength.LEN_256 == pKeyLen) {
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_32));
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_64));
            myList.add(GordianMacSpec.zucMac(pKeyLen, GordianLength.LEN_128));
        }

        /* Return the list */
        return myList;
    }
}
