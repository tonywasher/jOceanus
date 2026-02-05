/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.gordianknot.impl.core.mac.spec;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianNewSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.GordianDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.digest.spec.GordianNewDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacSpec;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewMacType;
import io.github.tonywasher.joceanus.gordianknot.api.mac.spec.GordianNewSipHashType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.spec.GordianCoreSymKeySpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cipher.spec.GordianCoreSymKeyType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSpec;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestSubSpec.GordianCoreDigestState;
import io.github.tonywasher.joceanus.gordianknot.impl.core.digest.spec.GordianCoreDigestType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * MacSpec implementation.
 */
public class GordianCoreMacSpec
        implements GordianNewMacSpec {
    /**
     * The Separator.
     */
    static final String SEP = "-";

    /**
     * The macTypeMap.
     */
    private static final Map<GordianNewMacType, GordianCoreMacType> TYPEMAP = newTypeMap();

    /**
     * The sipHashMap.
     */
    private static final Map<GordianNewSipHashType, GordianCoreSipHashType> SIPMAP = newSipHashMap();

    /**
     * The Mac Type.
     */
    private final GordianCoreMacType theMacType;

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
     * Constructor.
     *
     * @param pMacType   the macType
     * @param pKeyLength the keyLength
     * @param pSubSpec   the subSpec
     */
    public GordianCoreMacSpec(final GordianNewMacType pMacType,
                              final GordianLength pKeyLength,
                              final Object pSubSpec) {
        /* Store parameters */
        theMacType = TYPEMAP.get(pMacType);
        theKeyLength = pKeyLength;
        theSubSpec = pSubSpec instanceof GordianNewSipHashType mySip ? SIPMAP.get(mySip) : pSubSpec;
        isValid = checkValidity();
    }

    /**
     * Obtain Core Mac Type.
     *
     * @return the MacType
     */
    public GordianCoreMacType getCoreMacType() {
        return theMacType;
    }

    @Override
    public GordianNewMacType getMacType() {
        return theMacType.getType();
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    @Override
    public Object getSubSpec() {
        return theSubSpec;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Obtain DigestSpec.
     *
     * @return the DigestSpec
     */
    public GordianCoreDigestSpec getDigestSpec() {
        return theSubSpec instanceof GordianCoreDigestSpec mySpec
                ? mySpec
                : null;
    }

    /**
     * Obtain DigestState.
     *
     * @return the State
     */
    private GordianCoreDigestState getDigestState() {
        return theSubSpec instanceof GordianCoreDigestSpec mySpec
                ? mySpec.getCoreDigestState()
                : null;
    }

    /**
     * Obtain DigestLength.
     *
     * @return the Length
     */
    private GordianLength getDigestLength() {
        return theSubSpec instanceof GordianCoreDigestSpec mySpec
                ? mySpec.getDigestLength()
                : null;
    }

    /**
     * Obtain SymKeySpec.
     *
     * @return the SymKeySpec
     */
    public GordianCoreSymKeySpec getSymKeySpec() {
        return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                ? mySpec
                : null;
    }

    /**
     * Obtain SymKeyType.
     *
     * @return the Type
     */
    private GordianCoreSymKeyType getSymKeyType() {
        return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                ? mySpec.getCoreSymKeyType()
                : null;
    }

    /**
     * Obtain SymKeyBlockLength.
     *
     * @return the BlockLength
     */
    private GordianLength getSymKeyBlockLength() {
        return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                ? mySpec.getBlockLength()
                : null;
    }

    /**
     * Obtain SymKeyBlockLength.
     *
     * @return the BlockLength
     */
    private int getSymKeyBlockByteLength() {
        return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                ? Objects.requireNonNull(mySpec.getBlockLength()).getByteLength()
                : 0;
    }

    /**
     * Obtain SymKeyHalfBlockLength.
     *
     * @return the HalfBlockLength
     */
    private GordianLength getSymKeyHalfBlockLength() {
        return theSubSpec instanceof GordianCoreSymKeySpec mySpec
                ? mySpec.getHalfBlockLength()
                : null;
    }

    /**
     * Obtain SipHashSpec.
     *
     * @return the Spec
     */
    public GordianCoreSipHashType getSipHashSpec() {
        return theSubSpec instanceof GordianCoreSipHashType mySpec
                ? mySpec
                : null;
    }

    /**
     * Obtain MacLength.
     *
     * @return the Length
     */
    public GordianLength getMacLength() {
        switch (theMacType.getType()) {
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
                return ((GordianCoreSipHashType) theSubSpec).getOutLength();
            default:
                return GordianLength.LEN_64;
        }
    }

    /**
     * Obtain the IV length.
     *
     * @return the IV Length
     */
    public int getIVLen() {
        switch (theMacType.getType()) {
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
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Make sure that macType and keyLength are non-null */
        if (theMacType == null || theKeyLength == null) {
            return false;
        }

        /* Switch on MacType */
        switch (theMacType.getType()) {
            case HMAC:
                return checkDigestValidity(null);
            case KUPYNA:
                return checkDigestValidity(GordianNewDigestType.KUPYNA);
            case SKEIN:
                return checkDigestValidity(GordianNewDigestType.SKEIN);
            case BLAKE2:
                return checkBlake2Validity();
            case BLAKE3:
                return checkDigestValidity(GordianNewDigestType.BLAKE3);
            case KALYNA:
                return checkSymKeyValidity(GordianNewSymKeyType.KALYNA);
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
                return theSubSpec instanceof GordianCoreSipHashType
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
     *
     * @param pDigestType required digestType (or null)
     * @return valid true/false
     */
    private boolean checkDigestValidity(final GordianNewDigestType pDigestType) {
        /* Check that the digestSpec is valid */
        if (!(theSubSpec instanceof GordianCoreDigestSpec mySpec)
                || !mySpec.isValid()) {
            return false;
        }

        /* Check for digestType restrictions */
        final GordianCoreDigestType myType = mySpec.getCoreDigestType();
        return pDigestType == null
                ? myType.supportsLargeData()
                : myType.getType() == pDigestType;
    }

    /**
     * Check symKey validity.
     *
     * @param pSymKeyType required symKeyType (or null)
     * @return valid true/false
     */
    private boolean checkSymKeyValidity(final GordianNewSymKeyType pSymKeyType) {
        /* Check that the symKeySpec is valid */
        if (!(theSubSpec instanceof GordianNewSymKeySpec mySpec)
                || !mySpec.isValid()) {
            return false;
        }

        /* Check for symKeyType restrictions */
        return pSymKeyType == null
                || mySpec.getSymKeyType() == pSymKeyType;
    }

    /**
     * Check poly1305 validity.
     *
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
     *
     * @return valid true/false
     */
    private boolean checkBlake2Validity() {
        /* Check that the spec is reasonable */
        if (!checkDigestValidity(GordianNewDigestType.BLAKE2)) {
            return false;
        }

        /* Check keyLength */
        return checkBlake2KeyLength(theKeyLength, (GordianCoreDigestSpec) theSubSpec);
    }

    /**
     * Check blake2 keyLength validity.
     *
     * @param pKeyLen the keyLength
     * @param pSpec   the digestSpec
     * @return valid true/false
     */
    private static boolean checkBlake2KeyLength(final GordianLength pKeyLen,
                                                final GordianCoreDigestSpec pSpec) {
        /* Key length must be less or equal to the stateLength */
        return pKeyLen.getLength() <= pSpec.getCoreDigestState().getLength().getLength();
    }

    /**
     * Check blake validity.
     *
     * @return valid true/false
     */
    private boolean checkKMACValidity() {
        /* Check that the spec is reasonable */
        if (!checkDigestValidity(GordianNewDigestType.SHAKE)) {
            return false;
        }

        /* Check keyLength */
        return checkKMACKeyLength(theKeyLength, (GordianCoreDigestSpec) theSubSpec);
    }

    /**
     * Check KMAC keyLength validity.
     *
     * @param pKeyLen the keyLength
     * @param pSpec   the digestSpec
     * @return valid true/false
     */
    private static boolean checkKMACKeyLength(final GordianLength pKeyLen,
                                              final GordianCoreDigestSpec pSpec) {
        /* Key length must be greater or equal to the stateLength */
        return pKeyLen.getLength() >= pSpec.getCoreDigestState().getLength().getLength();
    }

    /**
     * Check zuc validity.
     *
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
     *
     * @return true/false
     */
    public boolean isXof() {
        switch (theMacType.getType()) {
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
            switch (theMacType.getType()) {
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
                    theName += getSymKeyBlockLength() + SEP + theKeyLength;
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
        return pThat instanceof GordianCoreMacSpec myThat
                && Objects.equals(theMacType, myThat.getCoreMacType())
                && theKeyLength == myThat.getKeyLength()
                && Objects.equals(theSubSpec, myThat.getSubSpec());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theMacType, theKeyLength, theSubSpec);
    }

    /**
     * Build the type map.
     *
     * @return the type map
     */
    private static Map<GordianNewMacType, GordianCoreMacType> newTypeMap() {
        final Map<GordianNewMacType, GordianCoreMacType> myMap = new EnumMap<>(GordianNewMacType.class);
        for (GordianNewMacType myType : GordianNewMacType.values()) {
            myMap.put(myType, new GordianCoreMacType(myType));
        }
        return myMap;
    }

    /**
     * Build the sipHash map.
     *
     * @return the sipHash map
     */
    private static Map<GordianNewSipHashType, GordianCoreSipHashType> newSipHashMap() {
        final Map<GordianNewSipHashType, GordianCoreSipHashType> myMap = new EnumMap<>(GordianNewSipHashType.class);
        for (GordianNewSipHashType myType : GordianNewSipHashType.values()) {
            myMap.put(myType, new GordianCoreSipHashType(myType));
        }
        return myMap;
    }
}
