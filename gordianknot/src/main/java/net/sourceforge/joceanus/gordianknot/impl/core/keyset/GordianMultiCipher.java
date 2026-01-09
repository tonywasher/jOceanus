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
package net.sourceforge.joceanus.gordianknot.impl.core.keyset;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianCipherParameters.GordianKeyCipherParameters;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymCipher;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeyType;
import net.sourceforge.joceanus.gordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKey;
import net.sourceforge.joceanus.gordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.gordianknot.impl.core.keyset.GordianKeySetRecipe.GordianKeySetParameters;
import org.bouncycastle.util.Arrays;

import java.security.spec.PKCS8EncodedKeySpec;
import java.util.EnumMap;
import java.util.Map;

/**
 * MultiKey Cipher.
 */
public final class GordianMultiCipher
    implements GordianCipher {
    /**
     * The default buffer size.
     */
    private static final int BUFSIZE = GordianLength.LEN_128.getByteLength();

    /**
     * The factory.
     */
    private final GordianBaseFactory theFactory;

    /**
     * The number of steps.
     */
    private final int theNumSteps;

    /**
     * The array of ciphers (in invocation order).
     */
    private final GordianSymCipher[] theCiphers;

    /**
     * Map of KeyType to SymKeyCiphers.
     */
    private final Map<GordianSymKeyType, GordianSymKeyCipherSet> theCipherMap;

    /**
     * The processing buffers.
     */
    private final byte[][] theBuffers = new byte[2][BUFSIZE << 1];

    /**
     * Constructor.
     * @param pKeySet the keySet
     * @throws GordianException on error
     */
    GordianMultiCipher(final GordianBaseKeySet pKeySet) throws GordianException {
        /* Access the factory and determine number of steps */
        theFactory = pKeySet.getFactory();
        theNumSteps = pKeySet.getKeySetSpec().getCipherSteps();
        theCiphers = new GordianSymCipher[theNumSteps];

        /* Create symmetric map */
        theCipherMap = new EnumMap<>(GordianSymKeyType.class);

        /* Loop copying any existing symKeys */
        for (GordianKey<GordianSymKeySpec> myKey : pKeySet.getSymKeyMap().values()) {
            declareSymKey(myKey);
        }
    }

    @Override
    public int getOutputLength(final int pLength) {
        int myLen = pLength;
        for (final GordianSymCipher myCipher : theCiphers) {
            myLen = myCipher.getOutputLength(myLen);
        }
        return myLen;
    }

    @Override
    public int update(final byte[] pBytes,
                      final int pOffset,
                      final int pLength,
                      final byte[] pOutput,
                      final int pOutOffset) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Initialise counters */
            int myRemaining = pLength;
            int myOffset = pOffset;
            int myOutOffset = pOutOffset;
            int myProcessed = 0;

            /* While we have more data to process */
            while (myRemaining > 0) {
                /* Determine how many bytes to process */
                final int myInputLen = Math.min(myRemaining, BUFSIZE);

                /* update the next block */
                final int myDataLen = processBlock(pBytes, myOffset, myInputLen, pOutput, myOutOffset);

                /* Update counters */
                myOffset += myInputLen;
                myRemaining -= myInputLen;
                myOutOffset += myDataLen;
                myProcessed += myDataLen;
            }

            /* Return the number of bytes that were output */
            return myProcessed;

        } finally {
            /* Clear the work buffers */
            Arrays.fill(theBuffers[0], (byte) 0);
            Arrays.fill(theBuffers[1], (byte) 0);
        }
    }

    /**
     * Process a block of data through the ciphers.
     * @param pBytes Bytes to update cipher with
     * @param pOffset offset within pBytes to read bytes from
     * @param pLength length of data to update with
     * @param pOutput the output buffer to receive processed data
     * @param pOutOffset offset within pOutput to write bytes to
     * @return the length of data written to the output buffer
     * @throws GordianException on error
     */
    private int processBlock(final byte[] pBytes,
                             final int pOffset,
                             final int pLength,
                             final byte[] pOutput,
                             final int pOutOffset) throws GordianException {
        /* Access initial buffer */
        byte[] mySource = pBytes;
        int myBufIndex = 0;
        byte[] myOutput = theBuffers[myBufIndex];
        int myOffset = pOffset;
        int myDataLen = pLength;

        /* Loop through the ciphers */
        for (final GordianSymCipher myCipher : theCiphers) {
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

        /* If we have data */
        if (myDataLen > 0) {
            /* Copy data to final buffer */
            System.arraycopy(mySource, 0, pOutput, pOutOffset, myDataLen);
        }

        /* Return the number of bytes that were output */
        return myDataLen;
    }

    @Override
    public int finish(final byte[] pOutput,
                      final int pOutOffset) throws GordianException {
            /* Access initial buffers */
        int myDataLen = 0;
        int myBufIndex = 0;
        byte[] myOutput = theBuffers[myBufIndex];
        byte[] mySource = myOutput;

        /* Protect against exceptions */
        try {
            /* Loop through the ciphers */
            for (final GordianSymCipher myCipher : theCiphers) {
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

            /* If we have data  */
            if (myDataLen > 0) {
                /* Copy data to final buffer */
                System.arraycopy(mySource, 0, pOutput, pOutOffset, myDataLen);
            }

            /* Return the number of bytes that were output */
            return myDataLen;

        } finally {
            /* Clear the work buffers */
            Arrays.fill(theBuffers[0], (byte) 0);
            Arrays.fill(theBuffers[1], (byte) 0);
        }
    }

    /**
     * Initialise the ciphers.
     * @param pParams the parameters
     * @param pEncrypt true/false
     * @throws GordianException on error
     */
    void initCiphers(final GordianKeySetParameters pParams,
                     final boolean pEncrypt) throws GordianException {
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
            final GordianSymCipher myCipher = getCipher(myKeyType, i);

            /* Initialise the cipher */
            final GordianKey<GordianSymKeySpec> mySymKey = theCipherMap.get(myKeyType).getKey();
            final byte[] myIV = myCipher.getCipherSpec().needsIV()
                                ? calculateInitVector(myInitVector, mySection++)
                                : null;
            final GordianKeyCipherParameters<GordianSymKeySpec> myParms = myIV == null
                      ? GordianCipherParameters.key(mySymKey)
                      : GordianCipherParameters.keyAndNonce(mySymKey, myIV);
            if (pEncrypt) {
                myCipher.initForEncrypt(myParms);
            } else {
                myCipher.initForDecrypt(myParms);
            }

            /* Place into correct location */
            final int myLoc = pEncrypt
                              ? i
                              : theNumSteps - i - 1;
            theCiphers[myLoc] = myCipher;
        }
    }

    /**
     * Declare symmetricKey.
     * @param pKey the key
     * @throws GordianException on error
     */
    void declareSymKey(final GordianKey<GordianSymKeySpec> pKey) throws GordianException {
        final GordianSymKeyCipherSet myCiphers = new GordianSymKeyCipherSet(theFactory, pKey);
        theCipherMap.put(pKey.getKeyType().getSymKeyType(), myCiphers);
    }

    /**
     * Check SymKeys.
     * @param pParams the parameters
     * @throws GordianException on error
     */
    private void checkParameters(final GordianKeySetParameters pParams) throws GordianException {
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
            if (!theCipherMap.containsKey(myKeyType)) {
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
     */
    private GordianSymCipher getCipher(final GordianSymKeyType pKeyType,
                                       final int pIndex) {
        /* Obtain the ciphers */
        final GordianSymKeyCipherSet myCiphers = theCipherMap.get(pKeyType);

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
     * @throws GordianException on error
     */
    byte[] secureKey(final GordianKeySetParameters pParams,
                     final GordianKey<?> pKeyToSecure) throws GordianException {
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
     * @param pOffset the offset within the secured key buffer
     * @param pKeyType the type of key to be derived
     * @return the derived key
     * @throws GordianException on error
     */
    <T extends GordianKeySpec> GordianKey<T> deriveKey(final GordianKeySetParameters pParams,
                                                       final byte[] pSecuredKey,
                                                       final int pOffset,
                                                       final T pKeyType) throws GordianException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Derive the bytes */
        final byte[] myBytes = deriveBytes(pParams, pSecuredKey, pOffset);

        /* Access the relevant generator */
        final GordianCoreKeyGenerator<T> myGenerator = pKeyType instanceof GordianMacSpec
                    ? (GordianCoreKeyGenerator<T>) theFactory.getMacFactory().getKeyGenerator(pKeyType)
                    : (GordianCoreKeyGenerator<T>) theFactory.getCipherFactory().getKeyGenerator(pKeyType);

        /* Generate the key */
        return myGenerator.buildKeyFromBytes(myBytes);
    }

    /**
     * Secure privateKey.
     * @param pParams the parameters
     * @param pKeyPairToSecure the key to secure
     * @return the securedKey
     * @throws GordianException on error
     */
    byte[] securePrivateKey(final GordianKeySetParameters pParams,
                            final GordianKeyPair pKeyPairToSecure) throws GordianException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Secure the key */
        final GordianKeyPairFactory myFactory = theFactory.getAsyncFactory().getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeyPairToSecure.getKeyPairSpec());
        final PKCS8EncodedKeySpec myPKCS8Key = myGenerator.getPKCS8Encoding(pKeyPairToSecure);
        return secureBytes(pParams, myPKCS8Key.getEncoded());
    }

    /**
     * derive privateKeySpec.
     * @param pParams the parameters
     * @param pSecuredPrivateKey the securedPrivateKey
     * @param pOffset the offset within the secured key
     * @return the derived keySpec
     * @throws GordianException on error
     */
    PKCS8EncodedKeySpec derivePrivateKeySpec(final GordianKeySetParameters pParams,
                                             final byte[] pSecuredPrivateKey,
                                             final int pOffset) throws GordianException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Derive the keySpec */
        final byte[] myBytes = deriveBytes(pParams, pSecuredPrivateKey, pOffset);
        return new PKCS8EncodedKeySpec(myBytes);
    }

    /**
     * secure bytes.
     * @param pParams the parameters
     * @param pBytesToSecure the key to secure
     * @return the securedBytes
     * @throws GordianException on error
     */
    byte[] secureBytes(final GordianKeySetParameters pParams,
                       final byte[] pBytesToSecure) throws GordianException {
        /* Access the parameters */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final byte[] myInitVector = pParams.getInitVector();

        /* Access and initialise the streamCipher */
        final GordianSymKeyType myStreamKeyType = mySymKeyTypes[0];
        final GordianKey<GordianSymKeySpec> myStreamKey = theCipherMap.get(myStreamKeyType).getKey();
        final GordianSymCipher myStreamCipher = getCipher(myStreamKeyType, 0);
        final byte[] myIV = calculateInitVector(myInitVector, 0);
        final GordianKeyCipherParameters<GordianSymKeySpec> myParms = GordianCipherParameters.keyAndNonce(myStreamKey, myIV);
        myStreamCipher.initForEncrypt(myParms);

        /* Process via the stream Cipher */
        final byte[] myBytes = myStreamCipher.finish(pBytesToSecure);

        /* Create the keySetWrapper */
        final GordianSymKeyCipherSet[] mySymCiphers = new GordianSymKeyCipherSet[theNumSteps - 1];
        for (int i = 1; i < theNumSteps; i++) {
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            mySymCiphers[i - 1] = theCipherMap.get(myKeyType);
        }
        final GordianKeySetWrapper myWrapper = new GordianKeySetWrapper(theFactory, mySymCiphers);
        return myWrapper.secureBytes(myBytes);
    }

    /**
     * derive Bytes.
     * @param pParams the parameters
     * @param pSecuredBytes the securedBytes
     * @param pOffset the offset within the secured key
     * @return the derived bytes
     * @throws GordianException on error
     */
    byte[] deriveBytes(final GordianKeySetParameters pParams,
                       final byte[] pSecuredBytes,
                       final int pOffset) throws GordianException {
        /* Check the parameters */
        checkParameters(pParams);

        /* Access the parameters */
        final GordianSymKeyType[] mySymKeyTypes = pParams.getSymKeyTypes();
        final byte[] myInitVector = pParams.getInitVector();

        /* Create the keySetWrapper */
        final GordianSymKeyCipherSet[] mySymCiphers = new GordianSymKeyCipherSet[theNumSteps - 1];
        for (int i = theNumSteps - 1; i >= 1; i--) {
            final GordianSymKeyType myKeyType = mySymKeyTypes[i];
            mySymCiphers[theNumSteps - i - 1] = theCipherMap.get(myKeyType);
        }
        final GordianKeySetWrapper myWrapper = new GordianKeySetWrapper(theFactory, mySymCiphers);
        final byte[] myBytes = myWrapper.deriveBytes(pSecuredBytes, pOffset);

        /* Access and initialise the streamCipher */
        final GordianSymKeyType myStreamKeyType = mySymKeyTypes[0];
        final GordianKey<GordianSymKeySpec> myStreamKey = theCipherMap.get(myStreamKeyType).getKey();
        final GordianSymCipher myStreamCipher = getCipher(myStreamKeyType, 0);
        final byte[] myIV = calculateInitVector(myInitVector, 0);
        final GordianKeyCipherParameters<GordianSymKeySpec> myParms = GordianCipherParameters.keyAndNonce(myStreamKey, myIV);
        myStreamCipher.initForDecrypt(myParms);

        /* Process via the stream Cipher */
        return myStreamCipher.finish(myBytes);
    }

    /**
     * Derive PolyMac key.
     * @param pParams the parameters
     * @return the key
     * @throws GordianException on error
     */
    public GordianKey<GordianMacSpec> derivePoly1305Key(final GordianKeySetParameters pParams) throws GordianException {
        /* Access keyType from parameters */
        final GordianSymKeyType myKeyType = pParams.getPoly1305SymKeyType();

        /* Access the required cipher */
        final GordianSymKeyCipherSet myCiphers = theCipherMap.get(myKeyType);
        final GordianSymCipher myCipher = myCiphers.getStandardCipher();
        myCipher.initForEncrypt(GordianCipherParameters.key(myCiphers.getKey()));

        /* First part is from IV section 2 */
        final byte[] myIV = pParams.getInitVector();
        final byte[] myIV1 = calculateInitVector(myIV, 2);
        final byte[] myIV2 = calculateInitVector(myIV, 3);
        final byte[] myKeyBytes = Arrays.concatenate(myIV1, myIV2);

        /* Obtain the keyGenerator */
        final GordianMacFactory myMacs = theFactory.getMacFactory();
        final GordianMacSpec mySpec = GordianMacSpecBuilder.poly1305Mac();
        final GordianCoreKeyGenerator<GordianMacSpec> myGenerator = (GordianCoreKeyGenerator<GordianMacSpec>) myMacs.getKeyGenerator(mySpec);
        return myGenerator.buildKeyFromBytes(myKeyBytes);
    }

    /**
     * Encrypt Mac.
     * @param pSymKeyType teh symKeyType to use
     * @param pMac the mac to encrypt
     * @return the encrypted Mac
     * @throws GordianException on error
     */
    public byte[] encryptMac(final GordianSymKeyType pSymKeyType,
                             final byte[] pMac) throws GordianException {
        /* Access the required cipher */
        final GordianSymKeyCipherSet myCiphers = theCipherMap.get(pSymKeyType);
        final GordianSymCipher myCipher = myCiphers.getStandardCipher();
        myCipher.initForEncrypt(GordianCipherParameters.key(myCiphers.getKey()));

        /* encrypt the mac*/
        return myCipher.finish(pMac);
    }
}
