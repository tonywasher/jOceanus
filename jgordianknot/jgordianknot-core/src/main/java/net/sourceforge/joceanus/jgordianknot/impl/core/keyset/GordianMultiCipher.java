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
package net.sourceforge.joceanus.jgordianknot.impl.core.keyset;

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.EnumMap;
import java.util.Map;

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianConsumer;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianPadding;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymCipherSpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreCipherFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.cipher.GordianCoreWrapper;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMacFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * MultiKey Cipher.
 */
final class GordianMultiCipher {
    /**
     * The default buffer size.
     */
    private static final int BUFSIZE = 128;

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

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
    private final Map<GordianSymKeyType, GordianKey<GordianSymKeySpec>> theSymKeyMap;

    /**
     * Map of KeyType to SymKeyCiphers.
     */
    private final Map<GordianSymKeyType, SymKeyCiphers> theSymCipherMap;

    /**
     * The processing buffers.
     */
    private final byte[][] theBuffers = new byte[2][BUFSIZE];

    /**
     * The keySet.
     */
    private final GordianCoreKeySet theKeySet;

    /**
     * The digest.
     */
    private GordianDigest theDigest;

    /**
     * The Mac.
     */
    private GordianMac theMac;

    /**
     * The initial Consumer.
     */
    private GordianConsumer theInitial;

    /**
     * The final Consumer.
     */
    private GordianConsumer theFinal;

    /**
     * Have we initialised the map yet?
     */
    private boolean initKeys;

    /**
     * Constructor.
     * @param pKeySet the keySet
     */
    GordianMultiCipher(final GordianCoreKeySet pKeySet) {
        /* Access the factory and determine number of steps */
        theKeySet = pKeySet;
        theFactory = pKeySet.getFactory();
        theNumSteps = theFactory.getNumCipherSteps();
        theCiphers = new GordianCipher<?>[theNumSteps];

        /* Create symmetric maps */
        theSymKeyMap = new EnumMap<>(GordianSymKeyType.class);
        theSymCipherMap = new EnumMap<>(GordianSymKeyType.class);
    }

    /**
     * Determine the maximum number of output bytes that will be produced for the given number of
     * input bytes.
     * @param pLength the number of input bytes
     * @return # of output bytes
     */
    int getOutputLength(final int pLength) {
        int myLen = pLength;
        for (final GordianCipher<?> myCipher : theCiphers) {
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
    byte[] update(final byte[] pBytes) throws OceanusException {
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
    byte[] update(final byte[] pBytes,
                  final int pOffset,
                  final int pLength) throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = update(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
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
    int update(final byte[] pBytes,
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
    int update(final byte[] pBytes,
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

        /* Update the initial consumer */
        theInitial.update(pBytes, pOffset, pLength);

        /* Loop through the ciphers */
        for (final GordianCipher<?> myCipher : theCiphers) {
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
            Arrays.fill(mySource, (byte) 0);
            throw new GordianDataException("Buffer too short");
        }

        /* If we have data */
        if (myDataLen > 0) {
            /* Update the final consumer */
            theFinal.update(mySource, 0, myDataLen);

            /* Copy data to final buffer */
            System.arraycopy(mySource, 0, pOutput, pOutOffset, myDataLen);
            Arrays.fill(mySource, (byte) 0);
        }

        /* Return the number of bytes that were output */
        return myDataLen;
    }

    /**
     * Complete the Cipher operation and return final results.
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    byte[] finish() throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(0);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
    }

    /**
     * Process the passed data and return final results.
     * @param pBytes Bytes to update cipher with
     * @return the remaining processed data
     * @throws OceanusException on error
     */
    byte[] finish(final byte[] pBytes) throws OceanusException {
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
    byte[] finish(final byte[] pBytes,
                  final int pOffset,
                  final int pLength) throws OceanusException {
        /* Create output buffer */
        final int myLen = getOutputLength(pLength);
        final byte[] myOutput = new byte[myLen];

        /* Process the data */
        final int myOut = finish(pBytes, pOffset, pLength, myOutput, 0);

        /* Return full buffer if possible */
        if (myOut == myLen) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOut);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
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
    int finish(final byte[] pBytes,
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
    int finish(final byte[] pBytes,
               final int pOffset,
               final int pLength,
               final byte[] pOutput,
               final int pOutOffset) throws OceanusException {
        /* Update the data */
        final int myLen = update(pBytes, pOffset, pLength, pOutput, pOutOffset);

        /* Complete the operation */
        return myLen + finish(pOutput, pOutOffset + myLen);
    }

    /**
     * Complete the Cipher operation and return final results.
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the number of bytes transferred to the output buffer
     * @throws OceanusException on error
     */
    int finish(final byte[] pOutput,
               final int pOutOffset) throws OceanusException {
        /* Access initial buffers */
        int myDataLen = 0;
        int myBufIndex = 0;
        byte[] myOutput = theBuffers[myBufIndex];
        byte[] mySource = myOutput;

        /* Loop through the ciphers */
        for (final GordianCipher<?> myCipher : theCiphers) {
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
            Arrays.fill(mySource, (byte) 0);
            throw new GordianDataException("Buffer too short");
        }

        /* If we have data  */
        if (myDataLen > 0) {
            /* Update the final consumer */
            theFinal.update(mySource, 0, myDataLen);

            /* Copy data to final buffer */
            System.arraycopy(mySource, 0, pOutput, pOutOffset, myDataLen);
            Arrays.fill(mySource, (byte) 0);
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
    void initCiphers(final GordianKeySetParameters pParams,
                     final boolean pEncrypt) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Access parameter details */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final byte[] myInitVector = pParams.getInitVector();

        /* Loop through the keys */
        int mySection = 0;
        for (int i = 0; i < theNumSteps; i++) {
            /* Obtain the ciphers */
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            final GordianCipher<GordianSymKeySpec> myCipher = getCipher(myKeyType, i);

            /* Initialise the cipher */
            final GordianKey<GordianSymKeySpec> mySymKey = theSymKeyMap.get(myKeyType);
            final byte[] myIV = myCipher.getCipherSpec().needsIV()
                                ? calculateInitVector(myInitVector, mySection++)
                                : null;
            myCipher.initCipher(mySymKey, myIV, pEncrypt);

            /* Place into correct location */
            final int myLoc = pEncrypt
                              ? i
                              : theNumSteps - i - 1;
            theCiphers[myLoc] = myCipher;
        }

        /* Create the the digest and the Mac */
        final GordianDigestType myDigest = pParams.getDigestType();
        final GordianDigestFactory myDigests = theFactory.getDigestFactory();
        theDigest = myDigests.createDigest(new GordianDigestSpec(myDigest));
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        theMac = myMacs.createMac(GordianMacSpec.hMac(myDigest));

        /* Initialise the digest and the Mac */
        theDigest.update(calculateInitVector(myInitVector, mySection++));
        theMac.initMac(calculateInitVector(myInitVector, mySection));

        /* Set the consumers inthe correct order */
        theInitial = pEncrypt ? theDigest : theMac;
        theFinal = pEncrypt ? theMac : theDigest;
    }

    /**
     * Calculate Mac.
     * @param pParams the parameters.
     */
    void calculateMac(final GordianKeySetParameters pParams) {
        /* Calculate the digest */
        theMac.update(theDigest.finish());

        /* Calculate the mac and store the required bytes */
        final byte[] myResult = theMac.finish();
        System.arraycopy(myResult, 0, pParams.getMac(), 0, GordianKeySetRecipe.MACLEN);
    }

    /**
     * validate Mac.
     * @param pParams the parameters.
     * @throws OceanusException on error
     */
    void validateMac(final GordianKeySetParameters pParams) throws OceanusException {
        /* Calculate the digest */
        theMac.update(theDigest.finish());

        /* Calculate the mac */
        final byte[] myResult = theMac.finish();
        final byte[] myExpected = pParams.getMac();

        /* Validate the mac */
        boolean bFailed = false;
        for (int i = 0; i < GordianKeySetRecipe.MACLEN; i++) {
            if (myResult[i] != myExpected[i]) {
                bFailed = true;
            }
        }

        /* Throw exception on error */
        if (bFailed) {
            throw new GordianDataException("Invalid Mac");
        }
    }

    /**
     * Check SymKeys.
     * @param pParams the parameters
     * @throws OceanusException on error
     */
    private void checkParameters(final GordianKeySetParameters pParams) throws OceanusException {
        /* If we have not yet initialised the keys */
        if (!initKeys) {
            /* Initialise the keyMap */
            for (final GordianKey<GordianSymKeySpec> myKey : theKeySet.getSymKeyMap().values()) {
                theSymKeyMap.put(myKey.getKeyType().getSymKeyType(), myKey);
            }

            /* Set the flag */
            initKeys = true;
        }

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
    }

    /**
     * Obtain the required Cipher.
     * @param pKeyType the keyType
     * @param pIndex the index of the cipher
     * @return the Cipher
     * @throws OceanusException on error
     */
    private GordianCipher<GordianSymKeySpec> getCipher(final GordianSymKeyType pKeyType,
                                                       final int pIndex) throws OceanusException {
        /* Obtain the ciphers */
        final SymKeyCiphers myCiphers = getKeyCiphers(pKeyType);

        /* Return Stream cipher if required */
        if (pIndex == 0 || pIndex == theNumSteps - 1) {
            return myCiphers.getStreamCipher();
        }

        /* Return Initial/Middle cipher */
        return pIndex == 1
               ? myCiphers.getPaddingCipher()
               : myCiphers.getStandardCipher();
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
            final GordianKey<GordianSymKeySpec> myKey = theSymKeyMap.get(pKeyType);
            myCiphers = new SymKeyCiphers(theFactory, myKey);
            theSymCipherMap.put(pKeyType, myCiphers);
        }

        /* Return the ciphers */
        return myCiphers;
    }

    /**
     * Obtain relevant initialisation vector.
     * @param pVector the initialisation vector
     * @param pSection the requested section
     * @return the shifted vector
     */
    private static byte[] calculateInitVector(final byte[] pVector,
                                              final int pSection) {
        /* Determine the index of the section */
        final int myIndex = pSection * GordianLength.LEN_128.getByteLength();

        /* Return appropriate section length of data */
        return Arrays.copyOfRange(pVector, myIndex, myIndex + GordianLength.LEN_128.getByteLength());
    }

    /**
     * secure key.
     * @param pParams the parameters
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    byte[] secureKey(final GordianKeySetParameters pParams,
                     final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Secure the key */
        return secureBytes(pParams, ((GordianCoreKey<?>) pKeyToSecure).getKeyBytes());
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
    @SuppressWarnings("unchecked")
    <T extends GordianKeySpec> GordianKey<T> deriveKey(final GordianKeySetParameters pParams,
                                                       final byte[] pSecuredKey,
                                                       final T pKeyType) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Derive the bytes */
        final byte[] myBytes = deriveBytes(pParams, pSecuredKey);

        /* Handle the macSpec separately */
        if (pKeyType instanceof GordianMacSpec) {
            final GordianCoreMacFactory myFactory = (GordianCoreMacFactory) theFactory.getMacFactory();
            final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myFactory.getKeyGenerator((GordianMacSpec) pKeyType);
            return myGenerator.buildKeyFromBytes(myBytes);
        }

        /* Generate the key */
        final GordianCoreCipherFactory myFactory = (GordianCoreCipherFactory) theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myFactory.getKeyGenerator(pKeyType);
        return myGenerator.buildKeyFromBytes(myBytes);
    }

    /**
     * Secure privateKey.
     * @param pParams the parameters
     * @param pKeyPairToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    byte[] securePrivateKey(final GordianKeySetParameters pParams,
                            final GordianKeyPair pKeyPairToSecure) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Secure the key */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(pKeyPairToSecure.getKeySpec());
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
    PKCS8EncodedKeySpec derivePrivateKeySpec(final GordianKeySetParameters pParams,
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
    byte[] secureBytes(final GordianKeySetParameters pParams,
                       final byte[] pBytesToSecure) throws OceanusException {
        /* Access the parameters */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final byte[] myInitVector = pParams.getInitVector();

        /* Access and initialise the streamCipher */
        final GordianSymKeyType myStreamKeyType = mySymKeyTypes[0];
        final GordianKey<GordianSymKeySpec> myStreamKey = theSymKeyMap.get(myStreamKeyType);
        final GordianCipher<GordianSymKeySpec> myStreamCipher = getCipher(myStreamKeyType, 0);
        final byte[] myIV = calculateInitVector(myInitVector, 0);
        myStreamCipher.initCipher(myStreamKey, myIV, true);

        /* Process via the stream Cipher */
        byte[] myBytes = myStreamCipher.finish(pBytesToSecure);

        /* Loop through the keys */
        for (int i = 1; i < theNumSteps; i++) {
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            final SymKeyCiphers myCiphers = getKeyCiphers(myKeyType);
            final GordianCoreWrapper myCipher = myCiphers.getWrapCipher();
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
    byte[] deriveBytes(final GordianKeySetParameters pParams,
                       final byte[] pSecuredBytes) throws OceanusException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Access the parameters */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final byte[] myInitVector = pParams.getInitVector();

        /* Loop through the keys */
        byte[] myBytes = pSecuredBytes;
        for (int i = theNumSteps - 1; i >= 1; i--) {
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            final SymKeyCiphers myCiphers = getKeyCiphers(myKeyType);
            final GordianCoreWrapper myCipher = myCiphers.getWrapCipher();
            myBytes = myCipher.deriveBytes(myCiphers.getKey(), myBytes);
        }

        /* Access and initialise the streamCipher */
        final GordianSymKeyType myStreamKeyType = mySymKeyTypes[0];
        final GordianKey<GordianSymKeySpec> myStreamKey = theSymKeyMap.get(myStreamKeyType);
        final GordianCipher<GordianSymKeySpec> myStreamCipher = getCipher(myStreamKeyType, 0);
        final byte[] myIV = calculateInitVector(myInitVector, 0);
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
        private final GordianKey<GordianSymKeySpec> theKey;

        /**
         * ECB Cipher (padding).
         */
        private final GordianCipher<GordianSymKeySpec> thePaddingCipher;

        /**
         * ECB Cipher (noPadding).
         */
        private final GordianCipher<GordianSymKeySpec> theStandardCipher;

        /**
         * Stream Cipher.
         */
        private final GordianCipher<GordianSymKeySpec> theStreamCipher;

        /**
         * Wrap Cipher.
         */
        private final GordianCoreWrapper theWrapCipher;

        /**
         * Constructor.
         * @param pFactory the factory
         * @param pKey the key
         * @throws OceanusException on error
         */
        SymKeyCiphers(final GordianCoreFactory pFactory,
                      final GordianKey<GordianSymKeySpec> pKey) throws OceanusException {
            /* Store parameters */
            theKey = pKey;
            final GordianSymKeySpec myKeySpec = theKey.getKeyType();
            final GordianCipherFactory myFactory = pFactory.getCipherFactory();

            /* Create the standard ciphers */
            thePaddingCipher = myFactory.createSymKeyCipher(GordianSymCipherSpec.ecb(myKeySpec, GordianPadding.ISO7816D4));
            theStandardCipher = myFactory.createSymKeyCipher(GordianSymCipherSpec.ecb(myKeySpec, GordianPadding.NONE));
            theStreamCipher = myFactory.createSymKeyCipher(GordianSymCipherSpec.sic(myKeySpec));

            /* Create the wrap cipher */
            theWrapCipher = (GordianCoreWrapper) myFactory.createKeyWrapper(myKeySpec);
        }

        /**
         * Obtain the key.
         * @return the Key
         */
        GordianKey<GordianSymKeySpec> getKey() {
            return theKey;
        }

        /**
         * Obtain the Padding cipher.
         * @return the Padding Cipher
         */
        GordianCipher<GordianSymKeySpec> getPaddingCipher() {
            return thePaddingCipher;
        }

        /**
         * Obtain the Stream cipher.
         * @return the Stream Cipher
         */
        GordianCipher<GordianSymKeySpec> getStreamCipher() {
            return theStreamCipher;
        }

        /**
         * Obtain the Standard cipher.
         * @return the Standard Cipher
         */
        GordianCipher<GordianSymKeySpec> getStandardCipher() {
            return theStandardCipher;
        }

        /**
         * Obtain the Wrap cipher.
         * @return the WrapCipher
         */
        GordianCoreWrapper getWrapCipher() {
            return theWrapCipher;
        }
    }
}
