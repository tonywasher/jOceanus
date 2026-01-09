/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.mac;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSubSpec.GordianDigestState;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestType;

import java.util.Objects;

/**
 * Mac Specification.
 */
public final class GordianMacSpec
        implements GordianKeySpec {
    /**
     * The Separator.
     */
    static final String SEP = "-";

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
     * @param pSpec the SipHashSpec
     */
    public GordianMacSpec(final GordianMacType pMacType,
                          final GordianSipHashSpec pSpec) {
        /* Store parameters */
        theMacType = pMacType;
        theKeyLength = GordianLength.LEN_128;
        theSubSpec = pSpec;
        isValid = checkValidity();
    }

    /**
     * vmpcMac/raw poly1305Mac Constructor.
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
        return theSubSpec instanceof GordianDigestSpec mySpec
               ? mySpec
               : null;
    }

    /**
     * Obtain DigestState.
     * @return the State
     */
    private GordianDigestState getDigestState() {
        return theSubSpec instanceof GordianDigestSpec mySpec
               ? mySpec.getDigestState()
               : null;
    }

    /**
     * Obtain DigestLength.
     * @return the Length
     */
    private GordianLength getDigestLength() {
        return theSubSpec instanceof GordianDigestSpec mySpec
               ? mySpec.getDigestLength()
               : null;
    }

    /**
     * Obtain SymKeySpec.
     * @return the SymKeySpec
     */
    public GordianSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianSymKeySpec mySpec
               ? mySpec
               : null;
    }

    /**
     * Obtain SymKeyType.
     * @return the Type
     */
    private GordianSymKeyType getSymKeyType() {
        return theSubSpec instanceof GordianSymKeySpec mySpec
               ? mySpec.getSymKeyType()
               : null;
    }

    /**
     * Obtain SymKeyBlockLength.
     * @return the BlockLength
     */
    private GordianLength getSymKeyBlockLength() {
        return theSubSpec instanceof GordianSymKeySpec mySpec
               ? mySpec.getBlockLength()
               : null;
    }

    /**
     * Obtain SymKeyBlockLength.
     * @return the BlockLength
     */
    private int getSymKeyBlockByteLength() {
        return theSubSpec instanceof GordianSymKeySpec mySpec
               ? Objects.requireNonNull(mySpec.getBlockLength()).getByteLength()
               : 0;
    }

    /**
     * Obtain SymKeyHalfBlockLength.
     * @return the HalfBlockLength
     */
    private GordianLength getSymKeyHalfBlockLength() {
        return theSubSpec instanceof GordianSymKeySpec mySpec
               ? mySpec.getHalfBlockLength()
               : null;
    }

    /**
     * Obtain SipHashSpec.
     * @return the Spec
     */
    public GordianSipHashSpec getSipHashSpec() {
        return theSubSpec instanceof GordianSipHashSpec mySpec
               ? mySpec
               : null;
    }

    /**
     * Obtain MacLength.
     * @return the Length
     */
    public GordianLength getMacLength() {
        switch (theMacType) {
            case HMAC:
            case BLAKE2:
            case BLAKE3:
            case SKEIN:
            case KUPYNA:
            case KMAC:
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
                return (GordianLength) theSubSpec;
            case VMPC:
                return GordianLength.LEN_160;
            case GOST:
                return GordianLength.LEN_32;
            case SIPHASH:
                return ((GordianSipHashSpec) theSubSpec).getOutLength();
            default:
                return GordianLength.LEN_64;
        }
    }

    /**
     * Obtain the IV length.
     * @return the IV Length
     */
    public int getIVLen() {
        switch (theMacType) {
            case VMPC:
            case SKEIN:
                return GordianLength.LEN_128.getByteLength();
            case POLY1305:
                return theSubSpec == null
                       ? 0
                       : GordianLength.LEN_128.getByteLength();
            case BLAKE2:
                return Objects.requireNonNull(getDigestState()).isBlake2bState()
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
                return GordianLength.LEN_128 == theKeyLength
                                     ? GordianLength.LEN_128.getByteLength()
                                     : GordianLength.LEN_200.getByteLength();
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
            case BLAKE2:
                return checkBlake2Validity();
            case BLAKE3:
                return checkDigestValidity(GordianDigestType.BLAKE3);
            case KALYNA:
                return checkSymKeyValidity(GordianSymKeyType.KALYNA);
            case KMAC:
                return checkKMACValidity();
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
                return theSubSpec instanceof GordianSipHashSpec
                        && theKeyLength == GordianLength.LEN_128;
            case GOST:
                return theSubSpec == null
                        && theKeyLength == GordianLength.LEN_256;
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
        final GordianDigestType myType = ((GordianDigestSpec) theSubSpec).getDigestType();
        return  pDigestType == null
                ? myType.supportsLargeData()
                : myType == pDigestType;
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
        /* Check that the subSpec is reasonable */
        if (theSubSpec != null
                && !checkSymKeyValidity(null)) {
            return false;
        }

        /* Restrict keyLengths */
        final GordianSymKeySpec mySpec = (GordianSymKeySpec) theSubSpec;
        return theKeyLength == GordianLength.LEN_256
            && (mySpec == null
                || mySpec.getKeyLength() == GordianLength.LEN_128);
    }

    /**
     * Check blake validity.
     * @return valid true/false
     */
    private boolean checkBlake2Validity() {
        /* Check that the spec is reasonable */
        if  (!checkDigestValidity(GordianDigestType.BLAKE2)) {
            return false;
        }

        /* Check keyLength */
        return checkBlake2KeyLength(theKeyLength, (GordianDigestSpec) theSubSpec);
    }

    /**
     * Check blake2 keyLength validity.
     * @param pKeyLen the keyLength
     * @param pSpec the digestSpec
     * @return valid true/false
     */
    private static boolean checkBlake2KeyLength(final GordianLength pKeyLen,
                                                final GordianDigestSpec pSpec) {
        /* Key length must be less or equal to the stateLength */
        return pKeyLen.getLength() <= pSpec.getDigestState().getLength().getLength();
    }

    /**
     * Check blake validity.
     * @return valid true/false
     */
    private boolean checkKMACValidity() {
        /* Check that the spec is reasonable */
        if  (!checkDigestValidity(GordianDigestType.SHAKE)) {
            return false;
        }

        /* Check keyLength */
        return checkKMACKeyLength(theKeyLength, (GordianDigestSpec) theSubSpec);
    }

    /**
     * Check KMAC keyLength validity.
     * @param pKeyLen the keyLength
     * @param pSpec the digestSpec
     * @return valid true/false
     */
    private static boolean checkKMACKeyLength(final GordianLength pKeyLen,
                                              final GordianDigestSpec pSpec) {
        /* Key length must be greater or equal to the stateLength */
        return pKeyLen.getLength() >= pSpec.getDigestState().getLength().getLength();
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

    /**
     * Is this a Xof Mac?
     * @return true/false
     */
    public boolean isXof() {
        switch (theMacType) {
            case KMAC:
            case BLAKE3:
                return true;
            case BLAKE2:
            case SKEIN:
                return Objects.requireNonNull(getDigestSpec()).isXof();
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
                theName = "InvalidMacSpec: " + theMacType + ":" + theSubSpec + ":" + theKeyLength;
                return theName;
            }

            /* Load the name */
            theName = theMacType.toString();
            switch (theMacType) {
                case SIPHASH:
                    theName = theSubSpec.toString();
                    break;
                case POLY1305:
                    theName += theSubSpec == null ? "" : SEP + getSymKeyType();
                    break;
                case GMAC:
                case CMAC:
                case CFBMAC:
                case CBCMAC:
                    theName += SEP + theSubSpec.toString();
                    break;
                case KALYNA:
                    theName += getSymKeyBlockLength()  + SEP + theKeyLength;
                    break;
                case KUPYNA:
                    theName += SEP + getDigestLength() + SEP + theKeyLength;
                    break;
                case KMAC:
                    theName += getDigestState() + SEP + theKeyLength;
                    break;
                case SKEIN:
                    final boolean isSkeinXof = Objects.requireNonNull(getDigestSpec()).isXofMode();
                    theName = GordianDigestType.SKEIN
                               + (isSkeinXof ? "X" : "")
                               + "Mac"
                               + SEP + getDigestState()
                               + (isSkeinXof ? "" : SEP + getDigestLength())
                               + SEP + theKeyLength;
                    break;
                case HMAC:
                case ZUC:
                    theName += theSubSpec.toString() + SEP + theKeyLength;
                    break;
                case BLAKE2:
                    final boolean isBlakeXof = Objects.requireNonNull(getDigestSpec()).isXofMode();
                    theName = GordianDigestType.BLAKE2
                            + Objects.requireNonNull(getDigestState())
                                     .getBlake2Algorithm(isBlakeXof)
                            + "Mac" + (isBlakeXof ? "" : SEP + getDigestLength())
                            + SEP + theKeyLength;
                    break;
                case BLAKE3:
                    theName += SEP + getDigestLength();
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

        /* Check MacType, keyLength and subSpec */
        return pThat instanceof GordianMacSpec myThat
                && theMacType == myThat.getMacType()
                && theKeyLength == myThat.getKeyLength()
                && Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theMacType, theKeyLength, theSubSpec);
    }
}
