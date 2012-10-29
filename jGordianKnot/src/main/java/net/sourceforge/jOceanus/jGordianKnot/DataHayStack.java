/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.jOceanus.jGordianKnot;

import java.util.Arrays;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;

/**
 * Class to hide a small piece of data into a larger piece of data.
 * @author Tony Washer
 */
public class DataHayStack {
    /**
     * HayStack signature.
     */
    private static final byte SIGN_HAYSTACK = 0x5b;

    /**
     * Maximum needle length.
     */
    private static final int MAX_NEEDLE_LEN = 255;

    /**
     * Minimum hayStack length.
     */
    private static final int MIN_HAYSTACK_LEN = 16;

    /**
     * Mask shift.
     */
    private static final int MASK_SHIFT = 4;

    /**
     * Shift mask.
     */
    private static final int SHIFT_MASK = 0xF;

    /**
     * Id shift.
     */
    private static final int ID_SHIFT = 0x11;

    /**
     * Id mask.
     */
    private static final int ID_MASK = 0xF;

    /**
     * Hide a needle in a hayStack.
     * @param pNeedle the data to hide
     * @param pHayStack the data to hide within
     * @return the hayStack now containing the needle
     */
    private static byte[] hideNeedle(final byte[] pNeedle,
                                     final byte[] pHayStack) {
        /* Determine length of needle and hayStack */
        int iNeedleLen = pNeedle.length;
        int iHayStackLen = pHayStack.length;

        /* Needle must be less that 255 bytes */
        if (iNeedleLen > MAX_NEEDLE_LEN) {
            throw new IllegalArgumentException("Needle too Large");
        }

        /* HayStackLen must be at least 16 bytes */
        if (iHayStackLen < MIN_HAYSTACK_LEN) {
            throw new IllegalArgumentException("HayStack too Small");
        }

        /* Allocate the new HayStack */
        byte[] myHayStack = new byte[iHayStackLen + iNeedleLen + 2];

        /* Determine position of needle */
        int iMask = pHayStack[0];
        int iPos = 1 + ((iMask >> MASK_SHIFT) & SHIFT_MASK);

        /* Store the signature */
        myHayStack[0] = (byte) (SIGN_HAYSTACK ^ iMask);

        /* Split the hayStack into two parts */
        System.arraycopy(pHayStack, 0, myHayStack, 1, iPos);
        if (iPos < iHayStackLen) {
            System.arraycopy(pHayStack, iPos, myHayStack, iPos + iNeedleLen + 2, iHayStackLen - iPos);
        }

        /* Store the needle length */
        myHayStack[iPos + 1] = (byte) (iNeedleLen ^ iMask);

        /* Loop to store the needle */
        for (int i = 0; i < iNeedleLen; i++) {
            /* Store the needle */
            myHayStack[iPos + i + 2] = (byte) (pNeedle[i] ^ iMask);
        }

        /* Return the new hayStack */
        return myHayStack;
    }

    /**
     * Hide a needle in a hayStack.
     * @param pHayStack the data to hide within
     * @return the results
     */
    private static NeedleResult findNeedle(final byte[] pHayStack) {
        /* HayStackLen must be at least 16+2 bytes */
        if (pHayStack.length < MIN_HAYSTACK_LEN + 2) {
            return null;
        }

        /* Determine position of needle */
        int iMask = pHayStack[1];
        int iPos = 1 + ((iMask >> MASK_SHIFT) & SHIFT_MASK);

        /* Access signature */
        byte mySign = (byte) (pHayStack[0] ^ iMask);

        /* Check the signature */
        if (mySign != SIGN_HAYSTACK) {
            return null;
        }

        /* Access the needle length and the hayStack length */
        int iNeedleLen = (byte) (pHayStack[iPos + 1] ^ iMask);
        int iHayStackLen = pHayStack.length - iNeedleLen - 2;

        /* Check that we are within range */
        if ((iNeedleLen + iPos + 2) > pHayStack.length) {
            return null;
        }

        /* Allocate buffers */
        NeedleResult myResult = new NeedleResult(iNeedleLen, iHayStackLen);
        byte[] myNeedle = myResult.getNeedle();
        byte[] myHayStack = myResult.getHayStack();

        /* Loop to recover the needle */
        for (int i = 0; i < iNeedleLen; i++) {
            /* Store the needle */
            myNeedle[i] = (byte) (pHayStack[iPos + i + 2] ^ iMask);
        }

        /* Rebuild the hayStack from the two parts */
        System.arraycopy(pHayStack, 1, myHayStack, 0, iPos);
        System.arraycopy(pHayStack, iPos + iNeedleLen + 2, myHayStack, iPos, iHayStackLen - iPos);

        /* Return the results */
        return myResult;
    }

    /**
     * Results class for finding needle.
     */
    private static final class NeedleResult {
        /**
         * The Needle.
         */
        private final byte[] theNeedle;

        /**
         * The HayStack.
         */
        private final byte[] theHayStack;

        /**
         * Obtain the Needle.
         * @return the Needle
         */
        private byte[] getNeedle() {
            return theNeedle;
        }

        /**
         * Obtain the HayStack.
         * @return the HayStack
         */
        private byte[] getHayStack() {
            return theHayStack;
        }

        /**
         * Constructor.
         * @param pNeedleLen the needle
         * @param pHayStackLen the hayStack
         */
        private NeedleResult(final int pNeedleLen,
                             final int pHayStackLen) {
            /* Store data */
            theNeedle = new byte[pNeedleLen];
            theHayStack = new byte[pHayStackLen];
        }
    }

    /**
     * Digest Needle.
     */
    protected static class DigestNeedle {
        /**
         * DigestType.
         */
        private final DigestType theType;

        /**
         * Digest.
         */
        private final byte[] theDigest;

        /**
         * External format.
         */
        private final byte[] theExternal;

        /**
         * Obtain the Digest Type.
         * @return the Digest Type
         */
        protected DigestType getDigestType() {
            return theType;
        }

        /**
         * Obtain the Digest.
         * @return the Digest
         */
        protected byte[] getDigest() {
            return theDigest;
        }

        /**
         * Obtain the External format.
         * @return the External format
         */
        protected byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format.
         * @param pType the digest type
         * @param pDigest the digest value
         */
        protected DigestNeedle(final DigestType pType,
                               final byte[] pDigest) {
            /* Don't store the parameters */
            theType = null;
            theDigest = null;

            /* Create the byte array to hide */
            byte[] myDigest = new byte[1];
            myDigest[0] = (byte) (pType.getId() * ID_SHIFT);

            /* Hide the digest type into the digest */
            theExternal = hideNeedle(myDigest, pDigest);
        }

        /**
         * Constructor to parse External format.
         * @param pExternal the external format
         * @throws JDataException on error
         */
        protected DigestNeedle(final byte[] pExternal) throws JDataException {
            /* Don't store the parameters */
            theExternal = null;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(pExternal);

            /* Check that we found the needle */
            if (myResult == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Digest");
            }

            /* Store the digest */
            theDigest = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Check that needle is correct length */
            if (myNeedle.length != 1) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Digest");
            }

            /* Access Digest */
            theType = DigestType.fromId(myNeedle[0] & ID_MASK);
        }
    }

    /**
     * SymKey Needle.
     */
    protected static class SymKeyNeedle {
        /**
         * SymKeyType.
         */
        private final SymKeyType theType;

        /**
         * EncodedKey.
         */
        private final byte[] theEncodedKey;

        /**
         * External format.
         */
        private final byte[] theExternal;

        /**
         * Obtain the SymKey Type.
         * @return the SymKey Type
         */
        protected SymKeyType getSymKeyType() {
            return theType;
        }

        /**
         * Obtain the Encoded Key.
         * @return the Encoded Key
         */
        protected byte[] getEncodedKey() {
            return theEncodedKey;
        }

        /**
         * Obtain the External format.
         * @return the External format
         */
        protected byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format.
         * @param pType the symKey type
         * @param pEncodedKey the encodedKey
         */
        protected SymKeyNeedle(final SymKeyType pType,
                               final byte[] pEncodedKey) {
            /* Don't store the parameters */
            theType = null;
            theEncodedKey = null;

            /* Create the byte array to hide */
            byte[] myType = new byte[1];
            myType[0] = (byte) (pType.getId() * ID_SHIFT);

            /* Hide the symKey type into the encodedKey */
            theExternal = hideNeedle(myType, pEncodedKey);
        }

        /**
         * Constructor to parse External format.
         * @param pExternal the external format
         * @throws JDataException on error
         */
        protected SymKeyNeedle(final byte[] pExternal) throws JDataException {
            /* Don't store the parameters */
            theExternal = null;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(pExternal);

            /* Check that we found the needle */
            if (myResult == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid SymKey");
            }

            /* Store the encodedKey */
            theEncodedKey = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Check that needle is correct length */
            if (myNeedle.length != 1) {
                throw new JDataException(ExceptionClass.DATA, "Invalid SymKey");
            }

            /* Access SymKeyType */
            theType = SymKeyType.fromId(myNeedle[0] & ID_MASK);
        }
    }

    /**
     * AsymMode Needle.
     */
    protected static class AsymModeNeedle {
        /**
         * AsymKeyMode.
         */
        private final AsymKeyMode theMode;

        /**
         * PublicKey.
         */
        private final byte[] thePublicKey;

        /**
         * External format.
         */
        private final byte[] theExternal;

        /**
         * Obtain the AsymKeyMode.
         * @return the AsymKeyMode
         */
        protected AsymKeyMode getAsymKeyMode() {
            return theMode;
        }

        /**
         * Obtain the Public Key.
         * @return the Public Key
         */
        protected byte[] getPublicKey() {
            return thePublicKey;
        }

        /**
         * Obtain the External format.
         * @return the External format
         */
        protected byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format.
         * @param pMode the AsmKeyMode
         * @param pPublicKey the publicKey
         */
        protected AsymModeNeedle(final AsymKeyMode pMode,
                                 final byte[] pPublicKey) {
            /* Don't store the parameters */
            theMode = null;
            thePublicKey = null;

            /* Create the byte array to hide */
            byte[] myMode = pMode.getEncoded();

            /* Hide the AymKeyMode into the publicKey */
            theExternal = hideNeedle(myMode, pPublicKey);
        }

        /**
         * Constructor to parse External format.
         * @param pExternal the external format
         * @throws JDataException on error
         */
        protected AsymModeNeedle(final byte[] pExternal) throws JDataException {
            /* Don't store the parameters */
            theExternal = null;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(pExternal);

            /* Check that we found the needle */
            if (myResult == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid ASymKeyMode");
            }

            /* Store the publicKey */
            thePublicKey = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Access Mode */
            theMode = new AsymKeyMode(myNeedle);
        }
    }

    /**
     * HashModeNeedle.
     */
    protected static class HashModeNeedle {
        /**
         * HashMode.
         */
        private final HashMode theMode;

        /**
         * Salt.
         */
        private final byte[] theSalt;

        /**
         * Calculated hash.
         */
        private final byte[] theHash;

        /**
         * External format.
         */
        private final byte[] theExternal;

        /**
         * Obtain the HashMode.
         * @return the HashMode
         */
        protected HashMode getHashMode() {
            return theMode;
        }

        /**
         * Obtain the Salt.
         * @return the Salt
         */
        protected byte[] getSalt() {
            return theSalt;
        }

        /**
         * Obtain the Hash.
         * @return the Hash
         */
        protected byte[] getHash() {
            return theHash;
        }

        /**
         * Obtain the External format.
         * @return the External format
         */
        protected byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format.
         * @param pMode the HashMode
         * @param pSalt the salt
         * @param pHash the hash
         */
        protected HashModeNeedle(final HashMode pMode,
                                 final byte[] pSalt,
                                 final byte[] pHash) {
            /* Don't store the parameters */
            theMode = null;
            theSalt = null;
            theHash = null;

            /* Create the byte array to hide */
            byte[] myMode = pMode.getEncoded();

            /* Hide the HashMode into the Salt and thence into hash */
            byte[] myInternal = hideNeedle(myMode, pSalt);
            theExternal = hideNeedle(myInternal, pHash);
        }

        /**
         * Constructor to parse External format.
         * @param pExternal the external format
         * @throws JDataException on error
         */
        protected HashModeNeedle(final byte[] pExternal) throws JDataException {
            /* Don't store the parameters */
            theExternal = null;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(pExternal);

            /* Check that we found the needle */
            if (myResult == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid HashMode");
            }

            /* Store the hash */
            theHash = myResult.getHayStack();

            /* Find the needle in the hayStack again */
            myResult = findNeedle(myResult.getNeedle());

            /* Check that we found the needle */
            if (myResult == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid HashMode");
            }

            /* Store the salt */
            theSalt = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Access Mode */
            theMode = new HashMode(myNeedle);
        }
    }

    /**
     * EncryptionModeNeedle.
     */
    protected static class EncryptModeNeedle {
        /**
         * EncryptionMode.
         */
        private final EncryptionMode theMode;

        /**
         * InitVector.
         */
        private final byte[] theInitVector;

        /**
         * Encrypted bytes.
         */
        private final byte[] theBytes;

        /**
         * External format.
         */
        private final byte[] theExternal;

        /**
         * Obtain the EncryptionMode.
         * @return the EncryptionMode
         */
        protected EncryptionMode getEncryptionMode() {
            return theMode;
        }

        /**
         * Obtain the InitVector.
         * @return the InitVector
         */
        protected byte[] getInitVector() {
            return theInitVector;
        }

        /**
         * Obtain the encrypted bytes.
         * @return the encrypted bytes
         */
        protected byte[] getEncryptedBytes() {
            return theBytes;
        }

        /**
         * Obtain the External format.
         * @return the External format
         */
        protected byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format.
         * @param pMode the HashMode
         * @param pInitVector the initVector
         * @param pBytes the encrypted bytes
         */
        protected EncryptModeNeedle(final EncryptionMode pMode,
                                    final byte[] pInitVector,
                                    final byte[] pBytes) {
            /* Don't store the parameters */
            theMode = null;
            theInitVector = null;
            theBytes = null;

            /* Create the byte array to hide */
            byte[] myMode = pMode.getEncoded();

            /* Allocate the hayStack */
            byte[] myHayStack = new byte[SymmetricKey.IVSIZE + pBytes.length];
            System.arraycopy(pInitVector, 0, myHayStack, 0, SymmetricKey.IVSIZE);
            System.arraycopy(pBytes, 0, myHayStack, SymmetricKey.IVSIZE, pBytes.length);

            /* Hide the EncryptionMode into the HayStack */
            theExternal = hideNeedle(myMode, myHayStack);
        }

        /**
         * Constructor to parse External format.
         * @param pExternal the external format
         * @throws JDataException on error
         */
        protected EncryptModeNeedle(final byte[] pExternal) throws JDataException {
            /* Don't store the parameters */
            theExternal = null;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(pExternal);

            /* Check that we found the needle */
            if (myResult == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid EncryptMode");
            }

            /* Store the bytes */
            byte[] myHayStack = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Access Mode */
            theMode = new EncryptionMode(myNeedle);

            /* Split the HayStack into the separate parts */
            theInitVector = Arrays.copyOf(myHayStack, SymmetricKey.IVSIZE);
            theBytes = Arrays.copyOfRange(myHayStack, SymmetricKey.IVSIZE, myHayStack.length);
        }
    }
}
