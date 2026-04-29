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

package io.github.tonywasher.joceanus.gordianknot.impl.core.spec.cipher;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySpec;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianBlakeXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianChaCha20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianElephantKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianISAPKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianRomulusKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSalsa20Key;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSkeinXofKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianSparkleKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeySubType.GordianVMPCKey;
import io.github.tonywasher.joceanus.gordianknot.api.cipher.spec.GordianStreamKeyType;
import io.github.tonywasher.joceanus.gordianknot.impl.core.spec.base.GordianSpecConstants;

import java.util.Objects;

/**
 * GordianKnot StreamKeySpec.
 */
public class GordianCoreStreamKeySpec
        implements GordianStreamKeySpec {
    /**
     * The StreamKey Type.
     */
    private final GordianCoreStreamKeyType theType;

    /**
     * The Key Length.
     */
    private final GordianLength theKeyLength;

    /**
     * SubKeyType.
     */
    private final GordianStreamKeySubType theSubKeyType;

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
     * @param pType       the streamKeyType
     * @param pSubKeyType the subKeyTypeType
     * @param pKeyLength  the keyLength
     */
    GordianCoreStreamKeySpec(final GordianStreamKeyType pType,
                             final GordianStreamKeySubType pSubKeyType,
                             final GordianLength pKeyLength) {
        theType = GordianCoreStreamKeyType.mapCoreType(pType);
        theSubKeyType = pSubKeyType;
        theKeyLength = pKeyLength;
        isValid = checkValidity();
    }

    /**
     * Obtain the Core streamKeyType.
     *
     * @return the keyType
     */
    public GordianCoreStreamKeyType getCoreStreamKeyType() {
        return theType;
    }

    @Override
    public GordianStreamKeyType getStreamKeyType() {
        return theType.getType();
    }

    @Override
    public GordianStreamKeySubType getSubKeyType() {
        return theSubKeyType;
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    /**
     * Does this cipher need an IV?
     *
     * @return true/false
     */
    public boolean needsIV() {
        return getIVLength() > 0;
    }

    /**
     * Check spec validity.
     *
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Stream KeyType and Key length must be non-null */
        if (theType == null
                || theKeyLength == null) {
            return false;
        }

        /* Check subKeyTypes */
        switch (theType.getType()) {
            case SALSA20:
                return checkSalsaValidity();
            case CHACHA20:
                return checkChaChaValidity();
            case VMPC:
                return checkVMPCValidity();
            case SKEINXOF:
                return checkSkeinValidity();
            case BLAKE2XOF:
                return checkBlake2Validity();
            case ELEPHANT:
                return theSubKeyType instanceof GordianElephantKey
                        && theType.validForKeyLength(theKeyLength);
            case ISAP:
                return theSubKeyType instanceof GordianISAPKey
                        && theType.validForKeyLength(theKeyLength);
            case ROMULUS:
                return theSubKeyType instanceof GordianRomulusKey
                        && theType.validForKeyLength(theKeyLength);
            case SPARKLE:
                return checkSparkleValidity();
            default:
                return theSubKeyType == null
                        && theType.validForKeyLength(theKeyLength);
        }
    }

    /**
     * Check salsa spec validity.
     *
     * @return valid true/false
     */
    private boolean checkSalsaValidity() {
        /* SubKeyType must be a SalsaKey */
        if (!(theSubKeyType instanceof GordianSalsa20Key)) {
            return false;
        }

        /* Check keyLength validity */
        return theSubKeyType != GordianSalsa20Key.STD
                ? theKeyLength == GordianLength.LEN_256
                : theType.validForKeyLength(theKeyLength);
    }

    /**
     * Check chacha spec validity.
     *
     * @return valid true/false
     */
    private boolean checkChaChaValidity() {
        /* SubKeyType must be a ChaChaKey */
        if (!(theSubKeyType instanceof GordianChaCha20Key)) {
            return false;
        }

        /* Check keyLength validity */
        return theSubKeyType != GordianChaCha20Key.STD
                ? theKeyLength == GordianLength.LEN_256
                : theType.validForKeyLength(theKeyLength);
    }

    /**
     * Check vmpc spec validity.
     *
     * @return valid true/false
     */
    private boolean checkVMPCValidity() {
        /* SubKeyType must be a GordianVMPCKey */
        if (!(theSubKeyType instanceof GordianVMPCKey)) {
            return false;
        }

        /* Check keyLength validity */
        return theType.validForKeyLength(theKeyLength);
    }

    /**
     * Check skein spec validity.
     *
     * @return valid true/false
     */
    private boolean checkSkeinValidity() {
        /* SubKeyType must be a GordianSkeinXofKey */
        if (!(theSubKeyType instanceof GordianSkeinXofKey)) {
            return false;
        }

        /* Check keyLength validity */
        return theType.validForKeyLength(theKeyLength);
    }

    /**
     * Check blake2 spec validity.
     *
     * @return valid true/false
     */
    private boolean checkBlake2Validity() {
        /* SubKeyType must be a GordianBlakeXofKey */
        if (!(theSubKeyType instanceof GordianBlakeXofKey myType)) {
            return false;
        }

        /* Check keyLength validity */
        return theType.validForKeyLength(theKeyLength)
                && (myType != GordianBlakeXofKey.BLAKE2XS
                || theKeyLength != GordianLength.LEN_512);
    }

    /**
     * Check sparkle spec validity.
     *
     * @return valid true/false
     */
    private boolean checkSparkleValidity() {
        /* SubKeyType must be a GordianSparkleKey */
        if (!(theSubKeyType instanceof GordianSparkleKey)) {
            return false;
        }

        /* Check keyLength validity */
        return theKeyLength == GordianCoreStreamKeySubType.requiredSparkleKeyLength((GordianSparkleKey) theSubKeyType);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = getName();
                theName += GordianSpecConstants.SEP + theKeyLength;
            } else {
                /* Report invalid spec */
                theName = "InvalidStreamKeySpec: " + theType + ":" + theKeyLength;
            }
        }

        /* return the name */
        return theName;
    }

    /**
     * Determine the name for the KeySpec.
     *
     * @return the name
     */
    private String getName() {
        switch (theType.getType()) {
            case VMPC:
                return theSubKeyType == GordianVMPCKey.KSA ? theType + "KSA3" : theType.toString();
            case SALSA20:
                return theSubKeyType == GordianSalsa20Key.XSALSA ? "X" + theType : theType.toString();
            case CHACHA20:
                switch ((GordianChaCha20Key) theSubKeyType) {
                    case XCHACHA:
                        return "X" + theType;
                    case ISO7539:
                        return "ChaCha7539";
                    default:
                        return theType.toString();
                }
            case SKEINXOF:
                return theType + GordianSpecConstants.SEP
                        + GordianCoreStreamKeySubType.getLengthForSkeinXofKey((GordianSkeinXofKey) theSubKeyType);
            case BLAKE2XOF:
            case ELEPHANT:
            case ISAP:
            case ROMULUS:
            case SPARKLE:
                return GordianCoreStreamKeySubType.toSubTypeString(theType.getType(), theSubKeyType);
            default:
                return theType.toString();
        }
    }

    /**
     * Obtain the IV Length.
     *
     * @return the IV length.
     */
    public int getIVLength() {
        switch (theType.getType()) {
            case RABBIT:
                return GordianLength.LEN_64.getByteLength();
            case GRAIN:
            case ELEPHANT:
                return GordianLength.LEN_96.getByteLength();
            case SOSEMANUK:
            case SNOW3G:
            case BLAKE3XOF:
            case SKEINXOF:
            case ASCON:
            case ISAP:
            case PHOTONBEETLE:
            case ROMULUS:
            case XOODYAK:
                return GordianLength.LEN_128.getByteLength();
            case HC:
                return GordianLength.LEN_128 == theKeyLength
                        ? GordianLength.LEN_128.getByteLength()
                        : GordianLength.LEN_256.getByteLength();
            case ZUC:
                return GordianLength.LEN_128 == theKeyLength
                        ? GordianLength.LEN_128.getByteLength()
                        : GordianLength.LEN_200.getByteLength();
            case VMPC:
                return theKeyLength.getByteLength();
            case BLAKE2XOF:
                return GordianCoreStreamKeySubType.requiredBlakeIVLength((GordianBlakeXofKey) theSubKeyType).getByteLength();
            case CHACHA20:
                return GordianCoreStreamKeySubType.requiredChaChaIVLength((GordianChaCha20Key) theSubKeyType).getByteLength();
            case SALSA20:
                return GordianCoreStreamKeySubType.requiredSalsaIVLength((GordianSalsa20Key) theSubKeyType).getByteLength();
            case SPARKLE:
                return GordianCoreStreamKeySubType.requiredSparkleIVLength((GordianSparkleKey) theSubKeyType).getByteLength();
            case ISAAC:
            case RC4:
            default:
                return 0;
        }
    }

    /**
     * Does this keySpec optionally support AEAD?
     *
     * @return true/false
     */
    public boolean supportsAEAD() {
        return GordianStreamKeyType.CHACHA20.equals(theType.getType())
                && theSubKeyType != GordianChaCha20Key.STD;
    }

    /**
     * Is this keySpec an AEAD keySpec?
     *
     * @return true/false
     */
    public boolean isAEAD() {
        switch (theType.getType()) {
            case ASCON:
            case ELEPHANT:
            case ISAP:
            case PHOTONBEETLE:
            case ROMULUS:
            case SPARKLE:
            case XOODYAK:
                return true;
            default:
                return false;
        }
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

        /* Check subFields */
        return pThat instanceof GordianCoreStreamKeySpec myThat
                && Objects.equals(theType, myThat.getCoreStreamKeyType())
                && theKeyLength == myThat.getKeyLength()
                && theSubKeyType == myThat.getSubKeyType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theType, theKeyLength, theSubKeyType);
    }
}
