/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2017 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianStreamCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherSpec.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * MultiKey Cipher.
 */
public final class GordianMultiCipher {
    /**
     * The default buffer size.
     */
    private static final int BUFSIZE = 128;

    /**
     * The factory.
     */
    private final GordianFactory theFactory;

    /**
     * The number of steps.
     */
    private final int theNumSteps;

    /**
     * The array of ciphers (in invocation order).
     */
    private final GordianCipher<?>[] theCiphers;

    /**
     * Map of KeyType to symKey.
     */
    private final Map<GordianSymKeyType, GordianKey<GordianSymKeyType>> theSymKeyMap;

    /**
     * Map of KeyType to streamKey.
     */
    private final Map<GordianStreamKeyType, GordianKey<GordianStreamKeyType>> theStreamKeyMap;

    /**
     * Map of KeyType to SymKeyCiphers.
     */
    private final Map<GordianSymKeyType, SymKeyCiphers> theSymCipherMap;

    /**
     * Map of KeyType to StreamKeyCiphers.
     */
    private final Map<GordianStreamKeyType, GordianCipher<GordianStreamKeyType>> theStreamCipherMap;

    /**
     * Map of DigestType to hMac.
     */
    private final Map<GordianDigestType, GordianMac> theHMacMap;

    /**
     * The processing buffers.
     */
    private byte[][] theBuffers = new byte[2][BUFSIZE];

    /**
     * Constructor.
     * @param pKeySet the keySet
     */
    protected GordianMultiCipher(final GordianKeySet pKeySet) {
        /* Access the factory and determine number of steps */
        theFactory = pKeySet.getFactory();
        theNumSteps = theFactory.getNumCipherSteps();
        theCiphers = new GordianCipher<?>[theNumSteps + 1];

        /* Create symmetric maps */
        theSymKeyMap = pKeySet.getSymKeyMap();
        theSymCipherMap = new EnumMap<>(GordianSymKeyType.class);

        /* Create stream maps */
        theStreamKeyMap = pKeySet.getStreamKeyMap();
        theStreamCipherMap = new EnumMap<>(GordianStreamKeyType.class);

        /* Create hMac map */
        theHMacMap = new EnumMap<>(GordianDigestType.class);
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
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = update(pBytes, pOffset, pLength, myOutput, 0);

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
        /* Access initial buffer */
        byte[] mySource = pBytes;
        int myBufIndex = 0;
        byte[] myOutput = theBuffers[myBufIndex];
        int myOffset = pOffset;
        int myDataLen = pLength;

        /* Loop through the ciphers */
        for (GordianCipher<?> myCipher : theCiphers) {
            /* If we have no data to update, we have finished */
            if (myDataLen == 0) {
                break;
            }

            /* Determine length of next output */
            final int myNextLen = myCipher.getOutputLength(myDataLen);

            /* Expand buffer if required */
            if (myNextLen > myOutput.length) {
                myOutput = new byte[myNextLen];
                theBuffers[myBufIndex] = myOutput;
            }

            /* update via this cipher */
            myDataLen = myCipher.update(mySource, myOffset, myDataLen, myOutput, 0);

            /* Adjust variables */
            mySource = myOutput;
            myOffset = 0;

            /* Determine new output */
            myBufIndex = (myBufIndex + 1) % 2;
            myOutput = theBuffers[myBufIndex];
        }

        /* Check bounds of output array */
        if (pOutput.length < pOutOffset + myDataLen) {
            throw new GordianDataException("Buffer too short");
        }

        /* Copy data to final buffer */
        if (myDataLen > 0) {
            System.arraycopy(mySource, 0, pOutput, pOutOffset, myDataLen);
        }

        /* Return the number of bytes that were output */
        return myDataLen;
    }

    /**
     * Complete the Cipher operation and return final results.
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    protected byte[] finish() throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(0);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(myOutput, 0);

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
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(pBytes, pOffset, pLength, myOutput, 0);

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
        final int myLen = update(pBytes, pOffset, pLength, pOutput, pOutOffset);

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
        /* Access initial buffers */
        int myDataLen = 0;
        int myBufIndex = 0;
        byte[] myOutput = theBuffers[myBufIndex];
        byte[] mySource = myOutput;

        /* Loop through the ciphers */
        for (GordianCipher<?> myCipher : theCiphers) {
            /* Determine length of next output */
            final int myNextLen = myCipher.getOutputLength(myDataLen);

            /* If there is no possible output then skip to next cipher */
            if (myNextLen == 0) {
                myDataLen = 0;
                continue;
            }

            /* Expand buffer if required */
            if (myNextLen > myOutput.length) {
                myOutput = new byte[myNextLen];
                theBuffers[myBufIndex] = myOutput;
            }

            /* finish via this cipher */
            myDataLen = myCipher.finish(mySource, 0, myDataLen, myOutput, 0);

            /* Adjust variables */
            mySource = myOutput;
            myBufIndex = (myBufIndex + 1) % 2;
            myOutput = theBuffers[myBufIndex];
        }

        /* Check bounds of output array */
        if (pOutput.length < pOutOffset + myDataLen) {
            throw new GordianDataException("Buffer too short");
        }

        /* Copy data to final buffer */
        if (myDataLen > 0) {
            System.arraycopy(mySource, 0, pOutput, pOutOffset, myDataLen);
        }

        /* Return the number of bytes that were output */
        return myDataLen;
    }

    /**
     * Initialise the ciphers.
     * @param pParams the parameters
     * @param pEncrypt true/false
     * @throws OceanusException on error
     */
    protected void initCiphers(final GordianKeySetParameters pParams,
                               final boolean pEncrypt) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Access parameter details */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final GordianStreamKeyType myStreamKeyType = pParams.getStreamKeyType();
        final GordianDigestType myMacType = pParams.getHMacType();
        final byte[] myInitVector = pParams.getInitVector();

        /* Loop through the keys */
        for (int i = 0; i < theNumSteps; i++) {
            /* Obtain the ciphers */
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            final GordianCipher<GordianSymKeyType> myCipher = getCipher(myKeyType, i);

            /* Initialise the cipher */
            final GordianKey<GordianSymKeyType> mySymKey = theSymKeyMap.get(myKeyType);
            final byte[] myIV = myCipher.getCipherSpec().needsIV()
                                                                   ? calculateInitVector(myMacType, mySymKey, myInitVector, myKeyType.getIVLength())
                                                                   : null;
            myCipher.initCipher(mySymKey, myIV, pEncrypt);

            /* Place into correct location */
            final int myLoc = pEncrypt
                                       ? i + 1
                                       : theNumSteps - i - 1;
            theCiphers[myLoc] = myCipher;
        }

        /* initialise the streamCipher */
        final GordianCipher<GordianStreamKeyType> myCipher = getStreamKeyCipher(myStreamKeyType);
        final GordianKey<GordianStreamKeyType> myStreamKey = theStreamKeyMap.get(myStreamKeyType);
        final byte[] myIV = myCipher.getCipherSpec().needsIV()
                                                               ? calculateInitVector(myMacType, myStreamKey, myInitVector, myStreamKeyType.getIVLength())
                                                               : null;
        myCipher.initCipher(myStreamKey, myIV, pEncrypt);

        /* Place into correct location */
        final int myLoc = pEncrypt
                                   ? 0
                                   : theNumSteps;
        theCiphers[myLoc] = myCipher;
    }

    /**
     * Check SymKeys.
     * @param pParams the parameters
     * @throws OceanusException on error
     */
    private void checkParameters(final GordianKeySetParameters pParams) throws OceanusException {
        /* Check length */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        if (mySymKeyTypes.length != theNumSteps) {
            throw new GordianDataException("Invalid number of symKeys");
        }

        /* Loop through the symKeys */
        int mySeen = 0;
        for (int i = 0; i < theNumSteps; i++) {
            /* Obtain the keyType */
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];

            /* Check non-null */
            if (!theSymKeyMap.containsKey(myKeyType)) {
                throw new GordianDataException("Unsupported keyType:- " + myKeyType);
            }

            /* Check non-duplicate */
            final int myFlag = 1 << myKeyType.ordinal();
            if ((mySeen & myFlag) != 0) {
                throw new GordianDataException("Duplicate keyType:- " + myKeyType);
            }
            mySeen |= myFlag;
        }

        /* Check streamKey */
        final GordianStreamKeyType myStreamKeyType = pParams.getStreamKeyType();
        if (!theStreamKeyMap.containsKey(myStreamKeyType)) {
            throw new GordianDataException("Unsupported keyType:- " + myStreamKeyType);
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
        final SymKeyCiphers myCiphers = getKeyCiphers(pKeyType);

        /* Return Final cipher if required */
        if (pIndex == theNumSteps - 1) {
            return myCiphers.getFinalCipher();
        }

        /* Return Initial/Middle cipher */
        return pIndex == 0
                           ? myCiphers.getInitCipher()
                           : myCiphers.getMidCipher();
    }

    /**
     * Obtain KeyCipher from map.
     * @param pKeyType the keyType
     * @return the KeyCipher
     * @throws OceanusException on error
     */
    private SymKeyCiphers getKeyCiphers(final GordianSymKeyType pKeyType) throws OceanusException {
        /* Look up existing ciphers */
        SymKeyCiphers myCiphers = theSymCipherMap.get(pKeyType);
        if (myCiphers == null) {
            /* Create new ciphers */
            final GordianKey<GordianSymKeyType> myKey = theSymKeyMap.get(pKeyType);
            myCiphers = new SymKeyCiphers(myKey);
            theSymCipherMap.put(pKeyType, myCiphers);
        }

        /* Return the ciphers */
        return myCiphers;
    }

    /**
     * Obtain StreamKeyCipher from map.
     * @param pKeyType the keyType
     * @return the KeyCipher
     * @throws OceanusException on error
     */
    private GordianCipher<GordianStreamKeyType> getStreamKeyCipher(final GordianStreamKeyType pKeyType) throws OceanusException {
        /* Look up existing ciphers */
        GordianCipher<GordianStreamKeyType> myCipher = theStreamCipherMap.get(pKeyType);
        if (myCipher == null) {
            /* Create new cipher */
            myCipher = theFactory.createStreamKeyCipher(GordianStreamCipherSpec.stream(pKeyType));
            theStreamCipherMap.put(pKeyType, myCipher);
        }

        /* Return the cipher */
        return myCipher;
    }

    /**
     * Obtain hashed initialisation vector.
     * @param pDigestType the digest type
     * @param pKey the key
     * @param pVector the initialisation vector
     * @param pIVLen the length of the required IV
     * @return the shifted vector
     * @throws OceanusException on error
     */
    private byte[] calculateInitVector(final GordianDigestType pDigestType,
                                       final GordianKey<?> pKey,
                                       final byte[] pVector,
                                       final int pIVLen) throws OceanusException {
        /* Access the hMac */
        GordianMac myMac = theHMacMap.get(pDigestType);
        if (myMac == null) {
            final GordianMacSpec myMacSpec = GordianMacSpec.hMac(pDigestType);
            myMac = theFactory.createMac(myMacSpec);
            theHMacMap.put(pDigestType, myMac);
        }

        /* Initialise the hMac using the key */
        final GordianKey<GordianMacSpec> myMacKey = pKey.convertToKeyType(myMac.getMacSpec());
        myMac.initMac(myMacKey);

        /* Update using IV and then personalisation */
        myMac.update(TethysDataConverter.stringToByteArray(pKey.getKeyType().toString()));
        myMac.update(pVector);
        final byte[] myIV = myMac.finish(theFactory.getPersonalisation());

        /* Return appropriate length of data */
        return myIV.length > pIVLen
                                    ? Arrays.copyOf(myIV, pIVLen)
                                    : myIV;
    }

    /**
     * secure key.
     * @param pParams the parameters
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    protected byte[] secureKey(final GordianKeySetParameters pParams,
                               final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Secure the key */
        return secureBytes(pParams, pKeyToSecure.getKeyBytes());
    }

    /**
     * derive key.
     * @param <T> type of key to be unwrapped
     * @param pParams the parameters
     * @param pSecuredKey the securedKey
     * @param pKeyType the type of key to be derived
     * @return the derived key
     * @throws OceanusException on error
     */
    protected <T> GordianKey<T> deriveKey(final GordianKeySetParameters pParams,
                                          final byte[] pSecuredKey,
                                          final T pKeyType) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Derive the bytes */
        final byte[] myBytes = deriveBytes(pParams, pSecuredKey);

        /* Generate the key */
        final GordianKeyGenerator<T> myGenerator = theFactory.getKeyGenerator(pKeyType);
        return myGenerator.buildKeyFromBytes(myBytes);
    }

    /**
     * Secure privateKey.
     * @param pParams the parameters
     * @param pKeyPairToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    protected byte[] securePrivateKey(final GordianKeySetParameters pParams,
                                      final GordianKeyPair pKeyPairToSecure) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Secure the key */
        final GordianKeyPairGenerator myGenerator = theFactory.getKeyPairGenerator(pKeyPairToSecure.getKeySpec());
        final PKCS8EncodedKeySpec myPKCS8Key = myGenerator.getPKCS8Encoding(pKeyPairToSecure);
        return secureBytes(pParams, myPKCS8Key.getEncoded());
    }

    /**
     * derive privateKeySpec.
     * @param pParams the parameters
     * @param pSecuredPrivateKey the securedPrivateKey
     * @return the derived keySpec
     * @throws OceanusException on error
     */
    protected PKCS8EncodedKeySpec derivePrivateKeySpec(final GordianKeySetParameters pParams,
                                                       final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Derive the keySpec */
        final byte[] myBytes = deriveBytes(pParams, pSecuredPrivateKey);
        return new PKCS8EncodedKeySpec(myBytes);
    }

    /**
     * secure bytes.
     * @param pParams the parameters
     * @param pBytesToSecure the key to secure
     * @return the securedBytes
     * @throws OceanusException on error
     */
    private byte[] secureBytes(final GordianKeySetParameters pParams,
                               final byte[] pBytesToSecure) throws OceanusException {
        /* Access the parameters */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final GordianStreamKeyType myStreamKeyType = pParams.getStreamKeyType();
        final GordianDigestType myMacType = pParams.getHMacType();
        final byte[] myInitVector = pParams.getInitVector();

        /* Access and initialise the streamCipher */
        final GordianCipher<GordianStreamKeyType> myStreamCipher = getStreamKeyCipher(myStreamKeyType);
        final GordianKey<GordianStreamKeyType> myStreamKey = theStreamKeyMap.get(myStreamKeyType);
        final byte[] myIV = calculateInitVector(myMacType, myStreamKey, myInitVector, myStreamKeyType.getIVLength());
        myStreamCipher.initCipher(myStreamKey, myIV, true);

        /* Process via the stream Cipher */
        byte[] myBytes = myStreamCipher.finish(pBytesToSecure);

        /* Loop through the keys */
        for (int i = 0; i < theNumSteps; i++) {
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            final SymKeyCiphers myCiphers = getKeyCiphers(myKeyType);
            final GordianWrapCipher myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.secureBytes(myCiphers.getKey(), myBytes);
        }

        /* return the secured bytes */
        return myBytes;
    }

    /**
     * derive Bytes.
     * @param pParams the parameters
     * @param pSecuredBytes the securedBytes
     * @return the derived bytes
     * @throws OceanusException on error
     */
    private byte[] deriveBytes(final GordianKeySetParameters pParams,
                               final byte[] pSecuredBytes) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Access the parameters */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final GordianStreamKeyType myStreamKeyType = pParams.getStreamKeyType();
        final GordianDigestType myMacType = pParams.getHMacType();
        final byte[] myInitVector = pParams.getInitVector();

        /* Loop through the symmetric keys */
        byte[] myBytes = pSecuredBytes;
        for (int i = theNumSteps - 1; i >= 0; i--) {
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            final SymKeyCiphers myCiphers = getKeyCiphers(myKeyType);
            final GordianWrapCipher myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.deriveBytes(myCiphers.getKey(), myBytes);
        }

        /* Access and initialise the streamCipher */
        final GordianCipher<GordianStreamKeyType> myStreamCipher = getStreamKeyCipher(myStreamKeyType);
        final GordianKey<GordianStreamKeyType> myStreamKey = theStreamKeyMap.get(myStreamKeyType);
        final byte[] myIV = calculateInitVector(myMacType, myStreamKey, myInitVector, myStreamKeyType.getIVLength());
        myStreamCipher.initCipher(myStreamKey, myIV, false);

        /* Process via the stream Cipher */
        myBytes = myStreamCipher.finish(myBytes);

        /* Return the derived bytes */
        return myBytes;
    }

    /**
     * Class to contain the symmetric key ciphers.
     */
    private final class SymKeyCiphers {
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
        private SymKeyCiphers(final GordianKey<GordianSymKeyType> pKey) throws OceanusException {
            /* Store parameters */
            theKey = pKey;
            final GordianSymKeyType myKeyType = theKey.getKeyType();

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
