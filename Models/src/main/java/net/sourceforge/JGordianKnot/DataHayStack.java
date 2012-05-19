/*******************************************************************************
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
package net.sourceforge.JGordianKnot;

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;

public class DataHayStack {
    /**
     * HayStack signature
     */
    private static final byte theSignature = 0x5b;

    /**
     * Hide a needle in a hayStack
     * @param pNeedle the data to hide
     * @param pHayStack the data to hide within
     * @return the hayStack now containing the needle
     */
    private static byte[] hideNeedle(byte[] pNeedle,
                                     byte[] pHayStack) {
        /* Determine length of needle and hayStack */
        int iNeedleLen = pNeedle.length;
        int iHayStackLen = pHayStack.length;

        /* Needle must be less that 255 bytes */
        if (iNeedleLen > 0xFF) {
            throw new IllegalArgumentException("Needle too Large");
        }

        /* HayStackLen must be at least 16 bytes */
        if (iHayStackLen < 16) {
            throw new IllegalArgumentException("HayStack too Small");
        }

        /* Allocate the new HayStack */
        byte[] myHayStack = new byte[iHayStackLen + iNeedleLen + 2];

        /* Determine position of needle */
        int iMask = pHayStack[0];
        int iPos = 1 + ((iMask >> 4) & 0xF);

        /* Store the signature */
        myHayStack[0] = (byte) (theSignature ^ iMask);

        /* Split the hayStack into two parts */
        System.arraycopy(pHayStack, 0, myHayStack, 1, iPos);
        if (iPos < iHayStackLen)
            System.arraycopy(pHayStack, iPos, myHayStack, iPos + iNeedleLen + 2, iHayStackLen - iPos);

        /* Store the needle length */
        myHayStack[iPos + 1] = (byte) (iNeedleLen ^ iMask);

        /* Loop to store the needle */
        for (int i = 0; i < iNeedleLen; i++) {
            /* Store the needle */
            myHayStack[iPos + i + 2] = (byte) (pNeedle[i] ^ iMask);
        }

        /*
         * Check that we can find the needle again NeedleResult myResult = findNeedle(myHayStack); if
         * ((myResult == null) || (!Arrays.equals(pNeedle, myResult.getNeedle())) ||
         * (!Arrays.equals(pHayStack, myResult.getHayStack()))) { findNeedle(myHayStack); }
         */

        /* Return the new hayStack */
        return myHayStack;
    }

    /**
     * Hide a needle in a hayStack
     * @param pHayStack the data to hide within
     * @return the results
     */
    private static NeedleResult findNeedle(byte[] pHayStack) {
        /* HayStackLen must be at least 16+2 bytes */
        if (pHayStack.length < 18)
            return null;

        /* Determine position of needle */
        int iMask = pHayStack[1];
        int iPos = 1 + ((iMask >> 4) & 0xF);

        /* Access signature */
        byte mySign = (byte) (pHayStack[0] ^ iMask);

        /* Check the signature */
        if (mySign != theSignature)
            return null;

        /* Access the needle length and the hayStack length */
        int iNeedleLen = (byte) (pHayStack[iPos + 1] ^ iMask);
        int iHayStackLen = pHayStack.length - iNeedleLen - 2;

        /* Check that we are within range */
        if ((iNeedleLen + iPos + 2) > pHayStack.length)
            return null;

        /* Allocate buffers */
        byte[] myNeedle = new byte[iNeedleLen];
        byte[] myHayStack = new byte[iHayStackLen];

        /* Loop to recover the needle */
        for (int i = 0; i < iNeedleLen; i++) {
            /* Store the needle */
            myNeedle[i] = (byte) (pHayStack[iPos + i + 2] ^ iMask);
        }

        /* Rebuild the hayStack from the two parts */
        System.arraycopy(pHayStack, 1, myHayStack, 0, iPos);
        System.arraycopy(pHayStack, iPos + iNeedleLen + 2, myHayStack, iPos, iHayStackLen - iPos);

        /* Return the results */
        return new NeedleResult(myNeedle, myHayStack);
    }

    /**
     * Results class for finding needle
     */
    private static class NeedleResult {
        /**
         * The Needle
         */
        private final byte[] theNeedle;

        /**
         * The HayStack
         */
        private final byte[] theHayStack;

        /**
         * Obtain the Needle
         * @return the Needle
         */
        private byte[] getNeedle() {
            return theNeedle;
        }

        /**
         * Obtain the HayStack
         * @return the HayStack
         */
        private byte[] getHayStack() {
            return theHayStack;
        }

        /**
         * Constructor
         * @param pNeedle the needle
         * @param pHayStack the hayStack
         */
        private NeedleResult(byte[] pNeedle,
                             byte[] pHayStack) {
            /* Store data */
            theNeedle = pNeedle;
            theHayStack = pHayStack;
        }
    }

    /**
     * Digest Needle
     */
    public static class DigestNeedle {
        /**
         * DigestType
         */
        private final DigestType theType;

        /**
         * Digest
         */
        private final byte[] theDigest;

        /**
         * External format
         */
        private final byte[] theExternal;

        /**
         * Obtain the Digest Type
         * @return the Digest Type
         */
        public DigestType getDigestType() {
            return theType;
        }

        /**
         * Obtain the Digest
         * @return the Digest
         */
        public byte[] getDigest() {
            return theDigest;
        }

        /**
         * Obtain the External format
         * @return the External format
         */
        public byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format
         * @param pType the digest type
         * @param pDigest the digest value
         */
        public DigestNeedle(DigestType pType,
                            byte[] pDigest) {
            /* Store the parameters */
            theType = pType;
            theDigest = pDigest;

            /* Create the byte array to hide */
            byte[] myDigest = new byte[1];
            myDigest[0] = (byte) (pType.getId() * 0x11);

            /* Hide the digest type into the digest */
            theExternal = hideNeedle(myDigest, pDigest);
        }

        /**
         * Constructor to parse External format
         * @param pExternal the external format
         * @throws ModelException
         */
        public DigestNeedle(byte[] pExternal) throws ModelException {
            /* Store the parameters */
            theExternal = pExternal;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(theExternal);

            /* Check that we found the needle */
            if (myResult == null)
                throw new ModelException(ExceptionClass.DATA, "Invalid Digest");

            /* Store the digest */
            theDigest = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Check that needle is correct length */
            if (myNeedle.length != 1)
                throw new ModelException(ExceptionClass.DATA, "Invalid Digest");

            /* Access Digest */
            theType = DigestType.fromId(myNeedle[0] & 0xF);
        }
    }

    /**
     * SymKey Needle
     */
    public static class SymKeyNeedle {
        /**
         * SymKeyType
         */
        private final SymKeyType theType;

        /**
         * EncodedKey
         */
        private final byte[] theEncodedKey;

        /**
         * External format
         */
        private final byte[] theExternal;

        /**
         * Obtain the SymKey Type
         * @return the SymKey Type
         */
        public SymKeyType getSymKeyType() {
            return theType;
        }

        /**
         * Obtain the Encoded Key
         * @return the Encoded Key
         */
        public byte[] getEncodedKey() {
            return theEncodedKey;
        }

        /**
         * Obtain the External format
         * @return the External format
         */
        public byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format
         * @param pType the symKey type
         * @param pEncodedKey the encodedKey
         */
        public SymKeyNeedle(SymKeyType pType,
                            byte[] pEncodedKey) {
            /* Store the parameters */
            theType = pType;
            theEncodedKey = pEncodedKey;

            /* Create the byte array to hide */
            byte[] myType = new byte[1];
            myType[0] = (byte) (pType.getId() * 0x11);

            /* Hide the symKey type into the encodedKey */
            theExternal = hideNeedle(myType, pEncodedKey);
        }

        /**
         * Constructor to parse External format
         * @param pExternal the external format
         * @throws ModelException
         */
        public SymKeyNeedle(byte[] pExternal) throws ModelException {
            /* Store the parameters */
            theExternal = pExternal;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(theExternal);

            /* Check that we found the needle */
            if (myResult == null)
                throw new ModelException(ExceptionClass.DATA, "Invalid SymKey");

            /* Store the encodedKey */
            theEncodedKey = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Check that needle is correct length */
            if (myNeedle.length != 1)
                throw new ModelException(ExceptionClass.DATA, "Invalid SymKey");

            /* Access SymKeyType */
            theType = SymKeyType.fromId(myNeedle[0] & 0xF);
        }
    }

    /**
     * AsymMode Needle
     */
    public static class AsymModeNeedle {
        /**
         * AsymKeyMode
         */
        private final AsymKeyMode theMode;

        /**
         * PublicKey
         */
        private final byte[] thePublicKey;

        /**
         * External format
         */
        private final byte[] theExternal;

        /**
         * Obtain the AsymKeyMode
         * @return the AsymKeyMode
         */
        public AsymKeyMode getAsymKeyMode() {
            return theMode;
        }

        /**
         * Obtain the Public Key
         * @return the Public Key
         */
        public byte[] getPublicKey() {
            return thePublicKey;
        }

        /**
         * Obtain the External format
         * @return the External format
         */
        public byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format
         * @param pMode the AsmKeyMode
         * @param pPublicKey the publicKey
         */
        public AsymModeNeedle(AsymKeyMode pMode,
                              byte[] pPublicKey) {
            /* Store the parameters */
            theMode = pMode;
            thePublicKey = pPublicKey;

            /* Create the byte array to hide */
            byte[] myMode = pMode.getEncoded();

            /* Hide the AymKeyMode into the publicKey */
            theExternal = hideNeedle(myMode, pPublicKey);
        }

        /**
         * Constructor to parse External format
         * @param pExternal the external format
         * @throws ModelException
         */
        public AsymModeNeedle(byte[] pExternal) throws ModelException {
            /* Store the parameters */
            theExternal = pExternal;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(theExternal);

            /* Check that we found the needle */
            if (myResult == null)
                throw new ModelException(ExceptionClass.DATA, "Invalid ASymKeyMode");

            /* Store the publicKey */
            thePublicKey = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Access Mode */
            theMode = new AsymKeyMode(myNeedle);
        }
    }

    /**
     * HashModeNeedle
     */
    public static class HashModeNeedle {
        /**
         * HashMode
         */
        private final HashMode theMode;

        /**
         * Salt
         */
        private final byte[] theSalt;

        /**
         * Calculated hash
         */
        private final byte[] theHash;

        /**
         * External format
         */
        private final byte[] theExternal;

        /**
         * Obtain the HashMode
         * @return the HashMode
         */
        public HashMode getHashMode() {
            return theMode;
        }

        /**
         * Obtain the Salt
         * @return the Salt
         */
        public byte[] getSalt() {
            return theSalt;
        }

        /**
         * Obtain the Hash
         * @return the Hash
         */
        public byte[] getHash() {
            return theHash;
        }

        /**
         * Obtain the External format
         * @return the External format
         */
        public byte[] getExternal() {
            return theExternal;
        }

        /**
         * Constructor to form External format
         * @param pMode the HashMode
         * @param pSalt the salt
         * @param pHash the hash
         */
        public HashModeNeedle(HashMode pMode,
                              byte[] pSalt,
                              byte[] pHash) {
            /* Store the parameters */
            theMode = pMode;
            theSalt = pSalt;
            theHash = pHash;

            /* Create the byte array to hide */
            byte[] myMode = pMode.getEncoded();

            /* Hide the HashMode into the Salt and thence into hash */
            byte[] myInternal = hideNeedle(myMode, pSalt);
            theExternal = hideNeedle(myInternal, pHash);
        }

        /**
         * Constructor to parse External format
         * @param pExternal the external format
         * @throws ModelException
         */
        public HashModeNeedle(byte[] pExternal) throws ModelException {
            /* Store the parameters */
            theExternal = pExternal;

            /* Find the needle in the hayStack */
            NeedleResult myResult = findNeedle(theExternal);

            /* Check that we found the needle */
            if (myResult == null)
                throw new ModelException(ExceptionClass.DATA, "Invalid HashMode");

            /* Store the hash */
            theHash = myResult.getHayStack();

            /* Find the needle in the hayStack again */
            myResult = findNeedle(myResult.getNeedle());

            /* Check that we found the needle */
            if (myResult == null)
                throw new ModelException(ExceptionClass.DATA, "Invalid HashMode");

            /* Store the salt */
            theSalt = myResult.getHayStack();

            /* Access the needle */
            byte[] myNeedle = myResult.getNeedle();

            /* Access Mode */
            theMode = new HashMode(myNeedle);
        }
    }
}
