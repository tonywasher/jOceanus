/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.crypto;

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * MultiKey Cipher.
 */
public final class GordianMultiCipher {
    /**
     * The default buffer size.
     */
    private static final int BUFSIZE = 128;

    /**
     * Multiplier to obtain IV from vector.
     */
    private static final int VECTOR_SHIFT = 7;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The number of steps.
     */
    private final int theNumSteps;

    /**
     * The array of standard ciphers (in invocation order).
     */
    private final GordianCipher<?>[] theCiphers;

    /**
     * Map of KeyType to key.
     */
    private final Map<GordianSymKeyType, GordianKey<GordianSymKeyType>> theKeyMap;

    /**
     * Map of KeyType to keyCiphers.
     */
    private final Map<GordianSymKeyType, KeyCiphers> theCipherMap;

    /**
     * The processing buffer.
     */
    private byte[] theBuffer = new byte[BUFSIZE];

    /**
     * The buffer length.
     */
    private int theBufLen = BUFSIZE;

    /**
     * Constructor.
     * @param pKeySet the keySet
     */
    protected GordianMultiCipher(final GordianKeySet pKeySet) {
        /* Access the factory and determine number of steps */
        theFactory = pKeySet.getFactory();
        theNumSteps = theFactory.getNumCipherSteps();
        theCiphers = new GordianCipher<?>[theNumSteps];

        /* Create map */
        theKeyMap = pKeySet.getKeyMap();
        theCipherMap = new EnumMap<>(GordianSymKeyType.class);
    }

    /**
     * Determine the maximum number of output bytes that will be produced for the given number of
     * input bytes.
     * @param pLength the number of input bytes
     * @return # of output bytes
     */
    protected int getOutputLength(final int pLength) {
        int myLen = pLength;
        for (GordianCipher<?> myCipher : theCiphers) {
            myLen = myCipher.getOutputLength(myLen);
        }
        return myLen;
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @return the intermediate processed data
     * @throws OceanusException on error
     */
    protected byte[] update(final byte[] pBytes) throws OceanusException {
        return update(pBytes, 0, pBytes.length);
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @return the intermediate processed data
     * @throws OceanusException on error
     */
    protected byte[] update(final byte[] pBytes,
                            final int pOffset,
                            final int pLength) throws OceanusException {
        /* Create output buffer */
        int myLen = getOutputLength(pLength);
        byte[] myOutput = new byte[myLen];

        /* Process the data */
        int myOut = update(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full or partial buffer */
        return (myOut == myLen)
                                ? myOutput
                                : Arrays.copyOf(myOutput, myOut);
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    protected int update(final byte[] pBytes,
                         final int pOffset,
                         final int pLength,
                         final byte[] pOutput) throws OceanusException {
        return update(pBytes, pOffset, pLength, pOutput, 0);
    }

    /**
     * Process the passed data and return intermediate results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    protected int update(final byte[] pBytes,
                         final int pOffset,
                         final int pLength,
                         final byte[] pOutput,
                         final int pOutOffset) throws OceanusException {
        /* Create an initial buffer */
        byte[] mySource = pBytes;
        int myOffset = pOffset;
        int myDataLen = pLength;

        /* Loop through the ciphers */
        for (GordianCipher<?> myCipher : theCiphers) {
            /* Determine length of next output */
            int myNextLen = myCipher.getOutputLength(myDataLen);

            /* Adjust buffer if required */
            if (myNextLen > theBufLen) {
                theBufLen = myNextLen;
                theBuffer = new byte[theBufLen];
            }

            /* update via this cipher */
            myDataLen = myCipher.update(mySource, myOffset, myDataLen, theBuffer, 0);

            /* Adjust variables */
            mySource = theBuffer;
            myOffset = 0;
        }

        /* Check bounds of output array */
        if (pOutput.length < pOutOffset + myDataLen) {
            throw new GordianDataException("Buffer too short");
        }

        /* Copy data to final buffer */
        System.arraycopy(theBuffer, 0, pOutput, pOutOffset, myDataLen);
        return myDataLen;
    }

    /**
     * Complete the Cipher operation and return final results.
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    protected byte[] finish() throws OceanusException {
        /* Create output buffer */
        int myLen = getOutputLength(0);
        byte[] myOutput = new byte[myLen];

        /* Process the data */
        int myOut = finish(myOutput, 0);

        /* Return full or partial buffer */
        return (myOut == myLen)
                                ? myOutput
                                : Arrays.copyOf(myOutput, myOut);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    protected byte[] finish(final byte[] pBytes) throws OceanusException {
        return finish(pBytes, 0, pBytes.length);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    protected byte[] finish(final byte[] pBytes,
                            final int pOffset,
                            final int pLength) throws OceanusException {
        /* Create output buffer */
        int myLen = getOutputLength(pLength);
        byte[] myOutput = new byte[myLen];

        /* Process the data */
        int myOut = finish(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full or partial buffer */
        return (myOut == myLen)
                                ? myOutput
                                : Arrays.copyOf(myOutput, myOut);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    protected int finish(final byte[] pBytes,
                         final int pOffset,
                         final int pLength,
                         final byte[] pOutput) throws OceanusException {
        return finish(pBytes, pOffset, pLength, pOutput, 0);
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    protected int finish(final byte[] pBytes,
                         final int pOffset,
                         final int pLength,
                         final byte[] pOutput,
                         final int pOutOffset) throws OceanusException {
        /* Update the data */
        int myLen = update(pBytes, pOffset, pLength, pOutput, pOutOffset);

        /* Complete the operation */
        return myLen + finish(pOutput, myLen);
    }

    /**
     * Complete the Cipher operation and return final results.
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    protected int finish(final byte[] pOutput,
                         final int pOutOffset) throws OceanusException {
        /* Create an initial buffer */
        int myDataLen = 0;

        /* Loop through the ciphers */
        for (GordianCipher<?> myCipher : theCiphers) {
            /* Determine length of next output */
            int myNextLen = myCipher.getOutputLength(myDataLen);

            /* Adjust buffer if required */
            if (myNextLen > theBufLen) {
                theBufLen = myNextLen;
                theBuffer = new byte[theBufLen];
            }

            /* finish via this cipher */
            myDataLen = myCipher.finish(theBuffer, 0, myDataLen, theBuffer, 0);
        }

        /* Check bounds of output array */
        if (pOutput.length < pOutOffset + myDataLen) {
            throw new GordianDataException("Buffer too short");
        }

        /* Copy data to final buffer */
        System.arraycopy(theBuffer, 0, pOutput, pOutOffset, myDataLen);
        return myDataLen;
    }

    /**
     * Initialise the ciphers.
     * @param pKeyTypes the keyTypes
     * @param pIV the initialisation vector
     * @param pEncrypt true/false
     * @throws OceanusException on error
     */
    protected void initCiphers(final GordianSymKeyType[] pKeyTypes,
                               final byte[] pIV,
                               final boolean pEncrypt) throws OceanusException {
        /* Check the keys */
        checkSymKeys(pKeyTypes);

        /* Loop through the keys */
        for (int i = 0; i < theNumSteps; i++) {
            /* Obtain the ciphers */
            GordianSymKeyType myKeyType = pKeyTypes[i];
            GordianCipher<GordianSymKeyType> myCipher = getCipher(myKeyType, i);

            /* Initialise the cipher */
            byte[] myIV = myCipher.getCipherSpec().needsIV()
                                                             ? getShiftedVector(myKeyType, pIV)
                                                             : null;
            myCipher.initCipher(theKeyMap.get(myKeyType), myIV, pEncrypt);

            /* Place into correct location */
            int myLoc = pEncrypt
                                 ? i
                                 : theNumSteps - i - 1;
            theCiphers[myLoc] = myCipher;
        }
    }

    /**
     * Check SymKeys.
     * @param pKeyTypes the keyTypes
     * @throws OceanusException on error
     */
    private void checkSymKeys(final GordianSymKeyType[] pKeyTypes) throws OceanusException {
        /* Check length */
        if (pKeyTypes.length != theNumSteps) {
            throw new GordianDataException("Invalid number of keys");
        }

        /* Loop through the keys */
        int mySeen = 0;
        for (int i = 0; i < theNumSteps; i++) {
            /* Obtain the ciphers */
            GordianSymKeyType myKeyType = pKeyTypes[i];

            /* Check non-null */
            if (!theKeyMap.containsKey(myKeyType)) {
                throw new GordianDataException("Unsupported keyType:- " + myKeyType);
            }

            /* Check non-duplicate */
            int myFlag = 1 << myKeyType.ordinal();
            if ((mySeen & myFlag) != 0) {
                throw new GordianDataException("Duplicate keyType:- " + myKeyType);
            }
            mySeen |= myFlag;
        }
    }

    /**
     * Obtain the required Cipher.
     * @param pKeyType the keyType
     * @param pIndex the index of the cipher
     * @return the Cipher
     * @throws OceanusException on error
     */
    private GordianCipher<GordianSymKeyType> getCipher(final GordianSymKeyType pKeyType,
                                                       final int pIndex) throws OceanusException {
        /* Obtain the ciphers */
        KeyCiphers myCiphers = getKeyCiphers(pKeyType);

        /* Return Final cipher if required */
        if (pIndex == theNumSteps - 1) {
            return myCiphers.getFinalCipher();
        }

        /* Return Initial cipher if required */
        if (pIndex == 0) {
            return myCiphers.getInitCipher();
        }

        /* Return the Middle cipher */
        return myCiphers.getMidCipher();
    }

    /**
     * Obtain KeyCipher from map.
     * @param pKeyType the keyType
     * @return the KeyCipher
     * @throws OceanusException on error
     */
    private KeyCiphers getKeyCiphers(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Look up existing ciphers */
        KeyCiphers myCiphers = theCipherMap.get(pKeyType);
        if (myCiphers == null) {
            /* Create new ciphers */
            GordianKey<GordianSymKeyType> myKey = theKeyMap.get(pKeyType);
            myCiphers = new KeyCiphers(myKey);
            theCipherMap.put(pKeyType, myCiphers);
        }

        /* Return the ciphers */
        return myCiphers;
    }

    /**
     * Obtain shifted Initialisation vector.
     * @param pKeyType the keyType
     * @param pVector the initialisation vector
     * @return the shifted vector
     */
    private static byte[] getShiftedVector(final GordianSymKeyType pKeyType,
                                           final byte[] pVector) {
        /* Determine length of input and output vectors */
        int myVectorLen = pVector.length;
        int myLen = pKeyType.getIVLength();
        byte[] myNew = new byte[myLen];

        /* Determine index into array for Key Type */
        int myIndex = VECTOR_SHIFT
                      * (pKeyType.ordinal() + 1);
        myIndex %= myVectorLen;

        /* Determine remaining data length in vector */
        int myRemainder = myVectorLen
                          - myIndex;

        /* If we need a single copy */
        if (myRemainder >= myLen) {
            /* Copy whole part */
            System.arraycopy(pVector, myIndex, myNew, 0, myLen);
        } else {
            /* Build in two parts */
            System.arraycopy(pVector, myIndex, myNew, 0, myRemainder);
            System.arraycopy(pVector, 0, myNew, myRemainder, myLen
                                                             - myRemainder);
        }

        /* return the shifted vector */
        return myNew;
    }

    /**
     * secure key.
     * @param pKeyTypes the keyTypes
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    protected byte[] secureKey(final GordianSymKeyType[] pKeyTypes,
                               final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Check the keys */
        checkSymKeys(pKeyTypes);

        /* Wrap using first cipher */
        KeyCiphers myCiphers = getKeyCiphers(pKeyTypes[0]);
        GordianWrapCipher myCipher = myCiphers.getWrapCipher();
        byte[] myBytes = myCipher.secureKey(myCiphers.getKey(), pKeyToSecure);

        /* Loop through the remaining keys */
        for (int i = 1; i < theNumSteps; i++) {
            /* Wrap using subsequent cipher */
            myCiphers = getKeyCiphers(pKeyTypes[i]);
            myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.secureBytes(myCiphers.getKey(), myBytes);
        }

        /* return the wrapped key */
        return myBytes;
    }

    /**
     * derive key.
     * @param <T> type of key to be unwrapped
     * @param pKeyTypes the keyTypes
     * @param pSecuredKey the securedKey
     * @param pKeyType the type of key to be derived
     * @return the derived key
     * @throws OceanusException on error
     */
    protected <T> GordianKey<T> deriveKey(final GordianSymKeyType[] pKeyTypes,
                                          final byte[] pSecuredKey,
                                          final T pKeyType) throws OceanusException {
        /* Check the keys */
        checkSymKeys(pKeyTypes);

        /* Loop through the remaining keys */
        byte[] myBytes = pSecuredKey;
        for (int i = theNumSteps - 1; i > 0; i--) {
            KeyCiphers myCiphers = getKeyCiphers(pKeyTypes[i]);
            GordianWrapCipher myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.deriveBytes(myCiphers.getKey(), myBytes);
        }

        /* Finally unwrap the key with the first cipher */
        KeyCiphers myCiphers = getKeyCiphers(pKeyTypes[0]);
        GordianWrapCipher myCipher = myCiphers.getWrapCipher();
        return myCipher.deriveKey(myCiphers.getKey(), myBytes, pKeyType);
    }

    /**
     * Secure privateKey.
     * @param pKeyTypes the keyTypes
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    protected byte[] securePrivateKey(final GordianSymKeyType[] pKeyTypes,
                                      final GordianKeyPair pKeyToSecure) throws OceanusException {
        /* Check the keys */
        checkSymKeys(pKeyTypes);

        /* Wrap using first cipher */
        KeyCiphers myCiphers = getKeyCiphers(pKeyTypes[0]);
        GordianWrapCipher myCipher = myCiphers.getWrapCipher();
        byte[] myBytes = myCipher.securePrivateKey(myCiphers.getKey(), pKeyToSecure);

        /* Loop through the remaining keys */
        for (int i = 1; i < theNumSteps; i++) {
            /* Wrap using subsequent cipher */
            myCiphers = getKeyCiphers(pKeyTypes[i]);
            myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.secureBytes(myCiphers.getKey(), myBytes);
        }

        /* return the wrapped key */
        return myBytes;
    }

    /**
     * derive privateKeySpec.
     * @param pKeyTypes the keyTypes
     * @param pSecuredPrivateKey the securedPrivateKey
     * @return the derived keySpec
     * @throws OceanusException on error
     */
    protected PKCS8EncodedKeySpec derivePrivateKeySpec(final GordianSymKeyType[] pKeyTypes,
                                                       final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Check the keys */
        checkSymKeys(pKeyTypes);

        /* Loop through the remaining keys */
        byte[] myBytes = pSecuredPrivateKey;
        for (int i = theNumSteps - 1; i > 0; i--) {
            KeyCiphers myCiphers = getKeyCiphers(pKeyTypes[i]);
            GordianWrapCipher myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.deriveBytes(myCiphers.getKey(), myBytes);
        }

        /* Finally unwrap the key with the first cipher */
        KeyCiphers myCiphers = getKeyCiphers(pKeyTypes[0]);
        GordianWrapCipher myCipher = myCiphers.getWrapCipher();
        return myCipher.deriveKeySpec(myCiphers.getKey(), myBytes);
    }

    /**
     * Class to contain the key ciphers.
     */
    private final class KeyCiphers {
        /**
         * Key.
         */
        private final GordianKey<GordianSymKeyType> theKey;

        /**
         * ECB Cipher (padding).
         */
        private final GordianCipher<GordianSymKeyType> theInitCipher;

        /**
         * ECB Cipher (noPadding).
         */
        private final GordianCipher<GordianSymKeyType> theMidCipher;

        /**
         * SIC Cipher.
         */
        private final GordianCipher<GordianSymKeyType> theFinalCipher;

        /**
         * Wrap Cipher.
         */
        private final GordianWrapCipher theWrapCipher;

        /**
         * Constructor.
         * @param pKey the key
         * @throws OceanusException on error
         */
        private KeyCiphers(final GordianKey<GordianSymKeyType> pKey) throws OceanusException {
            /* Store parameters */
            theKey = pKey;
            GordianSymKeyType myKeyType = theKey.getKeyType();

            /* Create the standard ciphers */
            theInitCipher = theFactory.createSymKeyCipher(GordianSymCipherSpec.ecb(myKeyType, GordianPadding.ISO7816D4));
            theMidCipher = theFactory.createSymKeyCipher(GordianSymCipherSpec.ecb(myKeyType, GordianPadding.NONE));
            theFinalCipher = theFactory.createSymKeyCipher(GordianSymCipherSpec.sic(myKeyType));

            /* Create the wrap cipher */
            theWrapCipher = theFactory.createWrapCipher(myKeyType);
        }

        /**
         * Obtain the key.
         * @return the Key
         */
        private GordianKey<GordianSymKeyType> getKey() {
            return theKey;
        }

        /**
         * Obtain the Initial cipher.
         * @return the Initial Cipher
         */
        private GordianCipher<GordianSymKeyType> getInitCipher() {
            return theInitCipher;
        }

        /**
         * Obtain the Final cipher.
         * @return the Final Cipher
         */
        private GordianCipher<GordianSymKeyType> getFinalCipher() {
            return theFinalCipher;
        }

        /**
         * Obtain the Mid cipher.
         * @return the Middle Cipher
         */
        private GordianCipher<GordianSymKeyType> getMidCipher() {
            return theMidCipher;
        }

        /**
         * Obtain the Wrap cipher.
         * @return the WrapCipher
         */
        private GordianWrapCipher getWrapCipher() {
            return theWrapCipher;
        }
    }
}
