/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyLengths;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

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
     * Create hcKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec hc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.HC, pKeyLength);
    }

    /**
     * Create chachaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec chacha(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA20, pKeyLength, GordianChaCha20Key.STD);
    }

    /**
     * Create chacha7539KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec chacha7539(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA20, pKeyLength, GordianChaCha20Key.ISO7539);
    }

    /**
     * Create xchachaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec xchacha(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.CHACHA20, pKeyLength, GordianChaCha20Key.XCHACHA);
    }

    /**
     * Create salsaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec salsa(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SALSA20, pKeyLength, GordianSalsa20Key.STD);
    }

    /**
     * Create xsalsaKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec xsalsa(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SALSA20, pKeyLength, GordianSalsa20Key.XSALSA);
    }

    /**
     * Create isaacKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec isaac(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ISAAC, pKeyLength);
    }

    /**
     * Create rc4KeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec rc4(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.RC4, pKeyLength);
    }

    /**
     * Create vmpcKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec vmpc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.VMPC, pKeyLength, GordianVMPCKey.STD);
    }

    /**
     * Create vmpcKSAKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec vmpcKSA(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.VMPC, pKeyLength, GordianVMPCKey.KSA);
    }

    /**
     * Create grainKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec grain(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.GRAIN, pKeyLength);
    }

    /**
     * Create rabbitKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec rabbit(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.RABBIT, pKeyLength);
    }

    /**
     * Create sosemanukKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec sosemanuk(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SOSEMANUK, pKeyLength);
    }

    /**
     * Create snow3GKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec snow3G(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SNOW3G, pKeyLength);
    }

    /**
     * Create zucKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec zuc(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.ZUC, pKeyLength);
    }

    /**
     * Create skeinKeySpec.
     * @param pKeyLength the keyLength
     * @param pStateLength the stateLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec skeinXof(final GordianLength pKeyLength,
                                                final GordianLength pStateLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.SKEINXOF, pKeyLength, GordianSkeinXofKey.getKeyTypeForLength(pStateLength));
    }

    /**
     * Create blakeKeySpec.
     * @param pKeyLength the keyLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec blakeXof(final GordianLength pKeyLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.BLAKEXOF, pKeyLength);
    }

    /**
     * Create kmacKeySpec.
     * @param pKeyLength the keyLength
     * @param pStateLength the stateLength
     * @return the keySpec
     */
    public static GordianStreamKeySpec kmacXof(final GordianLength pKeyLength,
                                               final GordianLength pStateLength) {
        return new GordianStreamKeySpec(GordianStreamKeyType.KMACXOF, pKeyLength, GordianKMACXofKey.getKeyTypeForLength(pStateLength));
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
        return getIVLength(false) > 0;
    }

    /**
     * Obtain the IV Length.
     * @param pAAD is this an AAD cipher
     * @return the IV length.
     */
    public int getIVLength(final boolean pAAD) {
        switch (theStreamKeyType) {
            case SALSA20:
                return theSubKeyType != GordianSalsa20Key.STD
                       ? GordianLength.LEN_192.getByteLength()
                       : theStreamKeyType.getIVLength(theKeyLength);
            case CHACHA20:
                if (theSubKeyType == GordianChaCha20Key.XCHACHA) {
                    return GordianLength.LEN_192.getByteLength();
                }
                return theSubKeyType == GordianChaCha20Key.ISO7539
                            ? GordianLength.LEN_96.getByteLength()
                            : theStreamKeyType.getIVLength(theKeyLength);
            case BLAKEXOF:
                return theSubKeyType == GordianBlakeXofKey.BLAKE2XS
                       ? GordianLength.LEN_64.getByteLength()
                       : GordianLength.LEN_128.getByteLength();
            default:
                return theStreamKeyType.getIVLength(theKeyLength);
        }
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
            case BLAKEXOF:
                return checkBlakeValidity();
            case KMACXOF:
                return checkKMACValidity();
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
     * Check skein spec validity.
     * @return valid true/false
     */
    private boolean checkBlakeValidity() {
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
     * Check skein spec validity.
     * @return valid true/false
     */
    private boolean checkKMACValidity() {
        /* SubKeyType must be a GordianKMACXofKey */
        if (!(theSubKeyType instanceof GordianKMACXofKey)) {
            return false;
        }

        /* Check keyLength validity */
        final GordianKMACXofKey myType = (GordianKMACXofKey) theSubKeyType;
        return myType.getLength().getLength() <= theKeyLength.getLength()
               && theStreamKeyType.validForKeyLength(theKeyLength);
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
           return theStreamKeyType.toString() + "KSA3";
        }

        /* Handle XSalsa20 */
        if (theStreamKeyType == GordianStreamKeyType.SALSA20
                && theSubKeyType == GordianSalsa20Key.XSALSA) {
            return "X" + theStreamKeyType.toString();
        }

        /* Handle XChaCha20 */
        if (theStreamKeyType == GordianStreamKeyType.CHACHA20
                && theSubKeyType == GordianChaCha20Key.XCHACHA) {
            return "X" + theStreamKeyType.toString();
        }

        /* Handle ChaCha7539 */
        if (theStreamKeyType == GordianStreamKeyType.CHACHA20
                && theSubKeyType == GordianChaCha20Key.ISO7539) {
            return "ChaCha7539";
        }

        /* Handle Skein */
        if (theStreamKeyType == GordianStreamKeyType.SKEINXOF) {
            return theStreamKeyType.toString() + SEP + theSubKeyType.toString();
        }

        /* Handle Blake */
        if (theStreamKeyType == GordianStreamKeyType.BLAKEXOF) {
            return theSubKeyType.toString();
        }

        /* Handle KMAC */
        if (theStreamKeyType == GordianStreamKeyType.KMACXOF) {
            return theStreamKeyType.toString() + theSubKeyType.toString();
        }

        /* return the name */
        return theStreamKeyType.toString();
    }

    /**
     * Determine the name for the KeySpec.
     * @return the name
     */
    public boolean supportsAAD() {
        return theStreamKeyType == GordianStreamKeyType.CHACHA20
                 && theSubKeyType != GordianChaCha20Key.STD;
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

        /* Make sure that the object is a StreamKeySpec */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target StreamKeySpec */
        final GordianStreamKeySpec myThat = (GordianStreamKeySpec) pThat;

        /* Check subFields */
        return theStreamKeyType == myThat.getStreamKeyType()
                && theKeyLength == myThat.getKeyLength()
                && theSubKeyType == myThat.getSubKeyType();
    }

    @Override
    public int hashCode() {
        int hashCode = theStreamKeyType.ordinal() + 1 << TethysDataConverter.BYTE_SHIFT;
        hashCode += theKeyLength.ordinal() + 1;
        if (theSubKeyType != null) {
            hashCode += theSubKeyType.hashCode();
        }
        return hashCode;
    }

    /**
     * List all possible streamKeySpecs for the keyLength.
     * @param pKeyLen the keyLength
     * @return the list
     */
    public static List<GordianStreamKeySpec> listAll(final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Check that the keyLength is supported */
        if (!GordianKeyLengths.isSupportedLength(pKeyLen)) {
            return myList;
        }

        /* For each streamKey type */
        for (final GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            /* if valid for keyLength */
            if (myType.validForKeyLength(pKeyLen)) {
                /* If we need a subType */
                if (myType.needsSubKeyType()) {
                    /* Add all valid subKeyTypes */
                    myList.addAll(listSubKeys(myType, pKeyLen));

                    /* Else just add the spec */
                } else {
                    myList.add(new GordianStreamKeySpec(myType, pKeyLen));
                }
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible subKeyTypes Specs.
     * @param pKeyType the keyType
     * @param pKeyLen the keyLength
     * @return the list
     */
    private static List<GordianStreamKeySpec> listSubKeys(final GordianStreamKeyType pKeyType,
                                                          final GordianLength pKeyLen) {
        /* Create the array list */
        final List<GordianStreamKeySpec> myList = new ArrayList<>();

        /* Loop through the subKeyTypes */
        for (GordianStreamSubKeyType mySubKeyType : listSubKeys(pKeyType)) {
            /* Add valid subKeySpec */
            final GordianStreamKeySpec mySpec = new GordianStreamKeySpec(pKeyType, pKeyLen, mySubKeyType);
            if (mySpec.isValid()) {
                myList.add(mySpec);
            }
        }

        /* Return the list */
        return myList;
    }

    /**
     * List all possible subKeyTypes.
     * @param pKeyType the keyType
     * @return the list
     */
    private static List<GordianStreamSubKeyType> listSubKeys(final GordianStreamKeyType pKeyType) {
        /* Switch on keyType */
        switch (pKeyType) {
            case SALSA20:
                return Arrays.asList(GordianSalsa20Key.values());
            case CHACHA20:
                return Arrays.asList(GordianChaCha20Key.values());
            case VMPC:
                return Arrays.asList(GordianVMPCKey.values());
            case SKEINXOF:
                return Arrays.asList(GordianSkeinXofKey.values());
            case BLAKEXOF:
                return Arrays.asList(GordianBlakeXofKey.values());
            case KMACXOF:
                return Arrays.asList(GordianKMACXofKey.values());
            default:
                return Collections.emptyList();
        }
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
            case BLAKEXOF:
                return GordianBlakeXofKey.BLAKE2XB;
            case KMACXOF:
                return GordianKMACXofKey.STATE128;
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
         * Obtain subKeyType for length.
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
    }

    /**
     * KMACXof Key styles.
     */
    public enum GordianKMACXofKey
            implements GordianStreamSubKeyType {
        /**
         * 128State.
         */
        STATE128(GordianLength.LEN_128),

        /**
         * 256State.
         */
        STATE256(GordianLength.LEN_256);

        /**
         * The length.
         */
        private final GordianLength theLength;

        /**
         * Constructor.
         * @param pLength the stateLength
         */
        GordianKMACXofKey(final GordianLength pLength) {
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
         * Obtain subKeyType for length.
         * @param pLength the length
         * @return the subKeyType
         */
        public static GordianKMACXofKey getKeyTypeForLength(final GordianLength pLength) {
            for (GordianKMACXofKey myType : values()) {
                if (pLength == myType.theLength) {
                    return myType;
                }
            }
            throw new IllegalArgumentException("Unsupported state length");
        }
    }
}
