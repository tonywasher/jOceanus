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
package net.sourceforge.joceanus.gordianknot.api.cipher;

import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import org.bouncycastle.crypto.engines.ElephantEngine.ElephantParameters;
import org.bouncycastle.crypto.engines.ISAPEngine.IsapType;
import org.bouncycastle.crypto.engines.RomulusEngine.RomulusParameters;
import org.bouncycastle.crypto.engines.SparkleEngine.SparkleParameters;

import java.util.Objects;

/**
 * GordianKnot StreamKeySpec.
 */
public class GordianStreamKeySpec
    implements GordianKeySpec {
    /**
     * The Separator.
     */
    private static final String SEP = "-";

    /**
     * The StreamKey Type.
     */
    private final GordianStreamKeyType theStreamKeyType;

    /**
     * The Key Length.
     */
    private final GordianLength theKeyLength;

    /**
     * SubKeyType.
     */
    private final GordianStreamSubKeyType theSubKeyType;

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
     * @param pStreamKeyType the streamKeyType
     * @param pKeyLength the keyLength
     */
    public GordianStreamKeySpec(final GordianStreamKeyType pStreamKeyType,
                                final GordianLength pKeyLength) {
        this(pStreamKeyType, pKeyLength, defaultSubKeyType(pStreamKeyType));
    }

    /**
     * Constructor.
     * @param pStreamKeyType the streamKeyType
     * @param pKeyLength the keyLength
     * @param pSubKeyType the subKeyType
     */
    public GordianStreamKeySpec(final GordianStreamKeyType pStreamKeyType,
                                final GordianLength pKeyLength,
                                final GordianStreamSubKeyType pSubKeyType) {
        /* Store parameters */
        theStreamKeyType = pStreamKeyType;
        theKeyLength = pKeyLength;
        theSubKeyType = pSubKeyType;
        isValid = checkValidity();
    }

    /**
     * Obtain streamKey Type.
     * @return the streamKeyType
     */
    public GordianStreamKeyType getStreamKeyType() {
        return theStreamKeyType;
    }

    @Override
    public GordianLength getKeyLength() {
        return theKeyLength;
    }

    /**
     * Obtain subKey Type.
     * @return the subKeyType
     */
    public GordianStreamSubKeyType getSubKeyType() {
        return theSubKeyType;
    }

    /**
     * Is the keySpec valid?
     * @return true/false.
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * Does this cipher need an IV?
     * @return true/false
     */
    public boolean needsIV() {
        return getIVLength() > 0;
    }

    /**
     * Check spec validity.
     * @return valid true/false
     */
    private boolean checkValidity() {
        /* Stream KeyType and Key length must be non-null */
        if (theStreamKeyType == null
                || theKeyLength == null) {
            return false;
        }

        /* Check subKeyTypes */
        switch (theStreamKeyType) {
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
                        && theStreamKeyType.validForKeyLength(theKeyLength);
            case ISAP:
                return theSubKeyType instanceof GordianISAPKey
                        && theStreamKeyType.validForKeyLength(theKeyLength);
            case ROMULUS:
                return theSubKeyType instanceof GordianRomulusKey
                        && theStreamKeyType.validForKeyLength(theKeyLength);
            case SPARKLE:
                return checkSparkleValidity();
            default:
                return theSubKeyType == null
                        && theStreamKeyType.validForKeyLength(theKeyLength);
        }
    }

    /**
     * Check salsa spec validity.
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
               : theStreamKeyType.validForKeyLength(theKeyLength);
    }

    /**
     * Check chacha spec validity.
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
               : theStreamKeyType.validForKeyLength(theKeyLength);
    }

    /**
     * Check vmpc spec validity.
     * @return valid true/false
     */
    private boolean checkVMPCValidity() {
        /* SubKeyType must be a GordianVMPCKey */
        if (!(theSubKeyType instanceof GordianVMPCKey)) {
            return false;
        }

        /* Check keyLength validity */
        return theStreamKeyType.validForKeyLength(theKeyLength);
    }

    /**
     * Check skein spec validity.
     * @return valid true/false
     */
    private boolean checkSkeinValidity() {
        /* SubKeyType must be a GordianSkeinXofKey */
        if (!(theSubKeyType instanceof GordianSkeinXofKey)) {
            return false;
        }

        /* Check keyLength validity */
        return theStreamKeyType.validForKeyLength(theKeyLength);
    }

    /**
     * Check blake2 spec validity.
     * @return valid true/false
     */
    private boolean checkBlake2Validity() {
        /* SubKeyType must be a GordianBlakeXofKey */
        if (!(theSubKeyType instanceof GordianBlakeXofKey)) {
            return false;
        }

        /* Check keyLength validity */
        final GordianBlakeXofKey myType = (GordianBlakeXofKey) theSubKeyType;
        return  theStreamKeyType.validForKeyLength(theKeyLength)
                && (myType != GordianBlakeXofKey.BLAKE2XS
                    || theKeyLength != GordianLength.LEN_512);
    }

    /**
     * Check sparkle spec validity.
     * @return valid true/false
     */
    private boolean checkSparkleValidity() {
        /* SubKeyType must be a GordianSparkleKey */
        if (!(theSubKeyType instanceof GordianSparkleKey)) {
            return false;
        }

        /* Check keyLength validity */
        return theKeyLength == ((GordianSparkleKey) theSubKeyType).requiredKeyLength();
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* If the keySpec is valid */
            if (isValid) {
                /* Load the name */
                theName = getName();
                theName += SEP + theKeyLength;
            }  else {
                /* Report invalid spec */
                theName = "InvalidStreamKeySpec: " + theStreamKeyType + ":" + theKeyLength;
            }
        }

        /* return the name */
        return theName;
    }

    /**
     * Determine the name for the KeySpec.
     * @return the name
     */
    private String getName() {
        /* Handle VMPC-KSA */
        if (theStreamKeyType == GordianStreamKeyType.VMPC
            && theSubKeyType == GordianVMPCKey.KSA) {
           return theStreamKeyType + "KSA3";
        }

        /* Handle XSalsa20 */
        if (theStreamKeyType == GordianStreamKeyType.SALSA20
                && theSubKeyType == GordianSalsa20Key.XSALSA) {
            return "X" + theStreamKeyType;
        }

        /* Handle XChaCha20 */
        if (theStreamKeyType == GordianStreamKeyType.CHACHA20
                && theSubKeyType == GordianChaCha20Key.XCHACHA) {
            return "X" + theStreamKeyType;
        }

        /* Handle ChaCha7539 */
        if (theStreamKeyType == GordianStreamKeyType.CHACHA20
                && theSubKeyType == GordianChaCha20Key.ISO7539) {
            return "ChaCha7539";
        }

        /* Handle Skein */
        if (theStreamKeyType == GordianStreamKeyType.SKEINXOF) {
            return theStreamKeyType + SEP + theSubKeyType.toString();
        }

        /* Handle Remaining types */
        switch (theStreamKeyType) {
            case BLAKE2XOF:
            case ELEPHANT:
            case ISAP:
            case ROMULUS:
            case SPARKLE:
                return theSubKeyType.toString();
            default:
                return theStreamKeyType.toString();
        }
    }

    /**
     * Obtain the IV Length.
     * @return the IV length.
     */
    public int getIVLength() {
        switch (theStreamKeyType) {
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
                return ((GordianBlakeXofKey) theSubKeyType).requiredIVLength().getByteLength();
            case CHACHA20:
                return ((GordianChaCha20Key) theSubKeyType).requiredIVLength().getByteLength();
            case SALSA20:
                return ((GordianSalsa20Key) theSubKeyType).requiredIVLength().getByteLength();
            case SPARKLE:
                return ((GordianSparkleKey) theSubKeyType).requiredIVLength().getByteLength();
            case ISAAC:
            case RC4:
            default:
                return 0;
        }
    }

    /**
     * Does this keySpec optionally support AEAD?
     * @return true/false
     */
    public boolean supportsAEAD() {
        return theStreamKeyType == GordianStreamKeyType.CHACHA20
                 && theSubKeyType != GordianChaCha20Key.STD;
    }

    /**
     * Is this keySpec an AEAD keySpec?
     * @return true/false
     */
    public boolean isAEAD() {
        switch (theStreamKeyType) {
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
        return pThat instanceof GordianStreamKeySpec myThat
                && theStreamKeyType == myThat.getStreamKeyType()
                && theKeyLength == myThat.getKeyLength()
                && theSubKeyType == myThat.getSubKeyType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(theStreamKeyType, theKeyLength, theSubKeyType);
    }

    /**
     * Default subKeyType.
     * @param pKeyType the keyType
     * @return the default
     */
    private static GordianStreamSubKeyType defaultSubKeyType(final GordianStreamKeyType pKeyType) {
        /* Switch on keyType */
        switch (pKeyType) {
            case SALSA20:
                return GordianSalsa20Key.STD;
            case CHACHA20:
                return GordianChaCha20Key.STD;
            case VMPC:
                return GordianVMPCKey.STD;
            case SKEINXOF:
                return GordianSkeinXofKey.STATE1024;
            case BLAKE2XOF:
                return GordianBlakeXofKey.BLAKE2XB;
            case ELEPHANT:
                return GordianElephantKey.ELEPHANT160;
            case ISAP:
                return GordianISAPKey.ISAPA128;
            case ROMULUS:
                return GordianRomulusKey.ROMULUS_M;
            case SPARKLE:
                return GordianSparkleKey.SPARKLE128_128;
            default:
                return null;
        }
    }

    /**
     * SubKeyType.
     */
    public interface GordianStreamSubKeyType {
    }

    /**
     * VMPC Key styles.
     */
    public enum GordianVMPCKey
            implements GordianStreamSubKeyType {
        /**
         * VMPC.
         */
        STD,

        /**
         * VMPC-KSA.
         */
        KSA;
    }

    /**
     * Salsa20 Key styles.
     */
    public enum GordianSalsa20Key
            implements GordianStreamSubKeyType {
        /**
         * Salsa20.
         */
        STD,

        /**
         * XSalsa20.
         */
        XSALSA;

        /**
         * Obtain required ivLength.
         * @return the ivLength
         */
        GordianLength requiredIVLength() {
            switch (this) {
                case STD:
                    return GordianLength.LEN_64;
                case XSALSA:
                default:
                    return GordianLength.LEN_192;
            }
        }
    }

    /**
     * ChaCha20 Key styles.
     */
    public enum GordianChaCha20Key
            implements GordianStreamSubKeyType {
        /**
         * ChaCha20.
         */
        STD,

        /**
         * ChaCha7539.
         */
        ISO7539,

        /**
         * XChaCha20.
         */
        XCHACHA;

        /**
         * Obtain required ivLength.
         * @return the ivLength
         */
        GordianLength requiredIVLength() {
            switch (this) {
                case STD:
                    return GordianLength.LEN_64;
                case ISO7539:
                    return GordianLength.LEN_96;
                case XCHACHA:
                default:
                    return GordianLength.LEN_192;
            }
        }
    }

    /**
     * SkeinXof Key styles.
     */
    public enum GordianSkeinXofKey
            implements GordianStreamSubKeyType {
        /**
         * 256State.
         */
        STATE256(GordianLength.LEN_256),

        /**
         * 512State.
         */
        STATE512(GordianLength.LEN_512),

        /**
         * 1024State.
         */
        STATE1024(GordianLength.LEN_1024);

        /**
         * The length.
         */
        private final GordianLength theLength;

        /**
         * Constructor.
         * @param pLength the stateLength
         */
        GordianSkeinXofKey(final GordianLength pLength) {
            theLength = pLength;
        }

        /**
         * Obtain length.
         * @return the length.
         */
        public GordianLength getLength() {
            return theLength;
        }

        @Override
        public String toString() {
            return theLength.toString();
        }

        /**
         * Obtain subKeyType for stateLength.
         * @param pLength the length
         * @return the subKeyType
         */
        public static GordianSkeinXofKey getKeyTypeForLength(final GordianLength pLength) {
            for (GordianSkeinXofKey myType : values()) {
                if (pLength == myType.theLength) {
                    return myType;
                }
            }
            throw new IllegalArgumentException("Unsupported state length");
        }
    }

    /**
     * BlakeXof Key styles.
     */
    public enum GordianBlakeXofKey
            implements GordianStreamSubKeyType {
        /**
         * Blake2S.
         */
        BLAKE2XS,

        /**
         * Blake2B.
         */
        BLAKE2XB;

        @Override
        public String toString() {
            return this == BLAKE2XB ? "Blake2Xb" : "Blake2Xs";
        }

        /**
         * Obtain required ivLength.
         * @return the ivLength
         */
        GordianLength requiredIVLength() {
            switch (this) {
                case BLAKE2XS:
                    return GordianLength.LEN_64;
                case BLAKE2XB:
                default:
                    return GordianLength.LEN_128;
            }
        }
    }

    /**
     * Elephant Key styles.
     */
    public enum GordianElephantKey
            implements GordianStreamSubKeyType {
        /**
         * Elephant160.
         */
        ELEPHANT160,

        /**
         * Elephant176.
         */
        ELEPHANT176,

        /**
         * Elephant200.
         */
        ELEPHANT200;

        @Override
        public String toString() {
            final String myBase = GordianStreamKeyType.ELEPHANT.toString();
            switch (this) {
                case ELEPHANT160:
                    return myBase + "160";
                case ELEPHANT176:
                    return myBase + "176";
                case ELEPHANT200:
                default:
                    return myBase + "200";
            }
        }

        /**
         * Obtain the elephant parameters.
         * @return the parameters
         */
        public ElephantParameters getParameters() {
            switch (this) {
                case ELEPHANT160:
                    return ElephantParameters.elephant160;
                case ELEPHANT176:
                    return ElephantParameters.elephant176;
                case ELEPHANT200:
                default:
                    return ElephantParameters.elephant200;
            }
        }
    }

    /**
     * ISAP Key styles.
     */
    public enum GordianISAPKey
            implements GordianStreamSubKeyType {
        /**
         * ISAPA128.
         */
        ISAPA128,

        /**
         * ISAPA128A.
         */
        ISAPA128A,

        /**
         * ISAPK128.
         */
        ISAPK128,

        /**
         * ISAPK128A.
         */
        ISAPK128A;

        @Override
        public String toString() {
            final String myBase = GordianStreamKeyType.ISAP.toString();
            switch (this) {
                case ISAPA128:
                    return myBase + "A128";
                case ISAPA128A:
                    return myBase + "A128A";
                case ISAPK128:
                    return myBase + "K128";
                case ISAPK128A:
                default:
                    return myBase + "K128A";
            }
        }

        /**
         * Obtain the ISAP type.
         * @return the type
         */
        public IsapType getType() {
            switch (this) {
                case ISAPA128:
                    return IsapType.ISAP_A_128;
                case ISAPA128A:
                    return IsapType.ISAP_A_128A;
                case ISAPK128:
                    return IsapType.ISAP_K_128;
                case ISAPK128A:
                default:
                    return IsapType.ISAP_K_128A;
            }
        }
    }

    /**
     * Romulus Key styles.
     */
    public enum GordianRomulusKey
            implements GordianStreamSubKeyType {
        /**
         * Romulus-M.
         */
        ROMULUS_M,

        /**
         * Romulus-N.
         */
        ROMULUS_N,

        /**
         * Romulus-T.
         */
        ROMULUS_T;

        @Override
        public String toString() {
            final String myBase = GordianStreamKeyType.ROMULUS.toString();
            switch (this) {
                case ROMULUS_M:
                    return myBase + "-M";
                case ROMULUS_N:
                    return myBase + "-N";
                case ROMULUS_T:
                default:
                    return myBase + "-T";
            }
        }

        /**
         * Obtain the RomulusParameters.
         * @return the parameters
         */
        public RomulusParameters getParameters() {
            switch (this) {
                case ROMULUS_M:
                    return RomulusParameters.RomulusM;
                case ROMULUS_N:
                    return RomulusParameters.RomulusN;
                case ROMULUS_T:
                default:
                    return RomulusParameters.RomulusT;
            }
        }
    }

    /**
     * Sparkle Key styles.
     */
    public enum GordianSparkleKey
            implements GordianStreamSubKeyType {
        /**
         * Sparkle128_128.
         */
        SPARKLE128_128,

        /**
         * Sparkle256_128.
         */
        SPARKLE256_128,

        /**
         * Sparkle192_192.
         */
        SPARKLE192_192,

        /**
         * Sparkle256_256.
         */
        SPARKLE256_256;

        @Override
        public String toString() {
            final String myBase = GordianStreamKeyType.SPARKLE.toString();
            switch (this) {
                case SPARKLE128_128:
                    return myBase + "128_128";
                case SPARKLE256_128:
                    return myBase + "256_128";
                case SPARKLE192_192:
                    return myBase + "192_192";
                case SPARKLE256_256:
                default:
                    return myBase + "256_256";
            }
        }

        /**
         * Obtain required keyLength.
         * @return the keyLength
         */
        GordianLength requiredKeyLength() {
            switch (this) {
                case SPARKLE128_128:
                case SPARKLE256_128:
                    return GordianLength.LEN_128;
                case SPARKLE192_192:
                    return GordianLength.LEN_192;
                case SPARKLE256_256:
                default:
                    return GordianLength.LEN_256;
            }
        }

        /**
         * Obtain required ivLength.
         * @return the ivLength
         */
        GordianLength requiredIVLength() {
            switch (this) {
                case SPARKLE128_128:
                    return GordianLength.LEN_128;
                case SPARKLE192_192:
                    return GordianLength.LEN_192;
                case SPARKLE256_128:
                case SPARKLE256_256:
                default:
                    return GordianLength.LEN_256;
            }
        }

        /**
         * Obtain the Sparkle parameters.
         * @return the parameters
         */
        public SparkleParameters getParameters() {
            switch (this) {
                case SPARKLE128_128:
                    return SparkleParameters.SCHWAEMM128_128;
                case SPARKLE256_128:
                    return SparkleParameters.SCHWAEMM256_128;
                case SPARKLE192_192:
                    return SparkleParameters.SCHWAEMM192_192;
                case SPARKLE256_256:
                default:
                    return SparkleParameters.SCHWAEMM256_256;
            }
        }
    }
}
