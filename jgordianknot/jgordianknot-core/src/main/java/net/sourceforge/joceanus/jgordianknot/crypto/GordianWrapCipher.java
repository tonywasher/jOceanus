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

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * GordianKnot base for WrapCipher.
 * <p>
 * This class uses a variant of RFC5649 to wrap keyData. In particular it uses the cipher in CBC
 * mode with no padding, and hence use an initVector that is derived via the key. It has also been
 * modified so that it does not require a 128-block cipher.
 */
public class GordianWrapCipher {
    /**
     * Wrap repeat count.
     */
    private static final int WRAP_COUNT = 6;

    /**
     * Integrity value.
     */
    private static final byte INTEGRITY_VALUE1 = (byte) 0xA6;

    /**
     * Integrity value.
     */
    private static final byte INTEGRITY_VALUE2 = (byte) 0x59;

    /**
     * Integrity modulo.
     */
    private static final int INTEGRITY_MODULO = 4;

    /**
     * The Security Factory.
     */
    private final GordianFactory theFactory;

    /**
     * Underlying cipher.
     */
    private final GordianCipher<GordianSymKeySpec> theCipher;

    /**
     * The block size.
     */
    private final int theBlockLen;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipher the underlying cipher
     */
    protected GordianWrapCipher(final GordianFactory pFactory,
                                final GordianCipher<GordianSymKeySpec> pCipher) {
        theFactory = pFactory;
        theCipher = pCipher;
        theBlockLen = getKeySpec().getBlockLength().getByteLength() >> 1;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    protected GordianSymKeySpec getKeySpec() {
        return theCipher.getKeyType();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianFactory getFactory() {
        return theFactory;
    }

    /**
     * Secure key.
     * @param pKey the key to use to secure the key
     * @param pKeyToSecure the key to secure
     * @return the securedKey
     * @throws OceanusException on error
     */
    protected byte[] secureKey(final GordianKey<GordianSymKeySpec> pKey,
                               final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Secure the bytes */
        return secureBytes(pKey, pKeyToSecure.getKeyBytes());
    }

    /**
     * Derive key from bytes.
     * @param <T> type of key to be derived
     * @param pKey the key to use to derive the key
     * @param pSecuredKey the securedKey
     * @param pKeyType the type of key to be derived
     * @return the derived key
     * @throws OceanusException on error
     */
    protected <T> GordianKey<T> deriveKey(final GordianKey<GordianSymKeySpec> pKey,
                                          final byte[] pSecuredKey,
                                          final T pKeyType) throws OceanusException {
        /* Unwrap the bytes */
        final byte[] myBytes = deriveBytes(pKey, pSecuredKey);

        /* Generate the key */
        final GordianKeyGenerator<T> myGenerator = theFactory.getKeyGenerator(pKeyType);
        return myGenerator.buildKeyFromBytes(myBytes);
    }

    /**
     * Secure private key.
     * @param pKey the key to use to secure the key
     * @param pKeyPairToSecure the key to secure
     * @return the wrapped bytes
     * @throws OceanusException on error
     */
    protected byte[] securePrivateKey(final GordianKey<GordianSymKeySpec> pKey,
                                      final GordianKeyPair pKeyPairToSecure) throws OceanusException {
        /* Access the KeyPair Generator */
        final GordianKeyPairGenerator myGenerator = theFactory.getKeyPairGenerator(pKeyPairToSecure.getKeySpec());
        final PKCS8EncodedKeySpec myPKCS8Key = myGenerator.getPKCS8Encoding(pKeyPairToSecure);
        return secureBytes(pKey, myPKCS8Key.getEncoded());
    }

    /**
     * derive private key.
     * @param pKey the key to use to derive the key
     * @param pSecuredPrivateKey the secured privateKey
     * @return the derived key
     * @throws OceanusException on error
     */
    protected PKCS8EncodedKeySpec deriveKeySpec(final GordianKey<GordianSymKeySpec> pKey,
                                                final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Derive the keySpec */
        final byte[] myBytes = deriveBytes(pKey, pSecuredPrivateKey);
        return new PKCS8EncodedKeySpec(myBytes);
    }

    /**
     * secure bytes (based on RFC 5649).
     * @param pKey the key to use to secure the bytes
     * @param pBytesToSecure the bytes to secure
     * @return the secured bytes
     * @throws OceanusException on error
     */
    protected byte[] secureBytes(final GordianKey<GordianSymKeySpec> pKey,
                                 final byte[] pBytesToSecure) throws OceanusException {
        /* Check validity of key */
        theCipher.checkValidKey(pKey);

        /* Determine number of blocks */
        final int myDataLen = pBytesToSecure.length;
        int myNumBlocks = myDataLen
                          / theBlockLen;

        /* Calculate padding length */
        final int myTrueLen;
        if (myDataLen % theBlockLen == 0) {
            myTrueLen = myDataLen;
        } else {
            myNumBlocks++;
            myTrueLen = myNumBlocks
                        * theBlockLen;
        }

        /* Allocate buffer for data and encryption */
        final int myBufferLen = theBlockLen << 1;
        final byte[] myData = new byte[myTrueLen
                                       + theBlockLen];
        final byte[] myBuffer = new byte[myBufferLen];
        final byte[] myResult = new byte[myBufferLen];

        /* Determine semantics of the initial block */
        final byte[] myByteLen = TethysDataConverter.integerToByteArray(myDataLen);
        final int myCheckLen = theBlockLen - Integer.BYTES;

        /* Build the initial block */
        for (int i = 0; i < myCheckLen; i++) {
            myData[i] = getIntegrityValue(i);
        }
        System.arraycopy(myByteLen, 0, myData, myCheckLen, Integer.BYTES);
        System.arraycopy(pBytesToSecure, 0, myData, theBlockLen, myDataLen);

        /* Initialise the cipher */
        theCipher.initCipher(pKey, null, true);

        /* Loop WRAP_COUNT times */
        int myCount = 1;
        for (int myCycle = 0; myCycle < WRAP_COUNT; myCycle++) {
            /* Loop through the data blocks */
            for (int myBlock = 1, myOffset = theBlockLen; myBlock <= myNumBlocks; myBlock++, myOffset += theBlockLen) {
                /* Build the data to be encrypted */
                System.arraycopy(myData, 0, myBuffer, 0, theBlockLen);
                System.arraycopy(myData, myOffset, myBuffer, theBlockLen, theBlockLen);

                /* Encrypt the byte array */
                theCipher.finish(myBuffer, 0, myBufferLen, myResult, 0);

                /* Adjust the result using the count as a mask */
                for (int myMask = myCount++, myIndex = myBufferLen - 1; myMask != 0; myMask >>>= Byte.SIZE, myIndex--) {
                    myResult[myIndex] ^= (byte) myMask;
                }

                /* Restore encrypted data */
                System.arraycopy(myResult, 0, myData, 0, theBlockLen);
                System.arraycopy(myResult, theBlockLen, myData, myOffset, theBlockLen);
            }
        }

        /* Return the wrapped data */
        return myData;
    }

    /**
     * derive bytes (based on RFC 5649).
     * @param pKey the key to use to derive the bytes
     * @param pSecuredBytes the bytes to derive
     * @return the derived bytes
     * @throws OceanusException on error
     */
    protected byte[] deriveBytes(final GordianKey<GordianSymKeySpec> pKey,
                                 final byte[] pSecuredBytes) throws OceanusException {
        /* Check validity of key */
        theCipher.checkValidKey(pKey);

        /* Determine number of blocks */
        final int myDataLen = pSecuredBytes.length
                              - theBlockLen;
        final int myNumBlocks = myDataLen
                                / theBlockLen;

        /* Data must be a multiple of BlockLength */
        if (myNumBlocks * theBlockLen != myDataLen) {
            throw new GordianDataException("Invalid data length");
        }

        /* Allocate buffers for data and encryption */
        final int myBufferLen = theBlockLen << 1;
        final byte[] myData = Arrays.copyOf(pSecuredBytes, pSecuredBytes.length);
        final byte[] myBuffer = new byte[myBufferLen];
        final byte[] myResult = new byte[myBufferLen];

        /* Initialise the cipher */
        theCipher.initCipher(pKey, null, false);

        /* Loop WRAP_COUNT times */
        int myCount = myNumBlocks * WRAP_COUNT;
        for (int myCycle = WRAP_COUNT; myCycle > 0; myCycle--) {
            /* Loop through the data blocks */
            for (int myBlock = myNumBlocks, myOffset = theBlockLen
                                                       * myBlock; myBlock >= 1; myBlock--, myOffset -= theBlockLen) {
                /* Build the data to be decrypted */
                System.arraycopy(myData, 0, myBuffer, 0, theBlockLen);
                System.arraycopy(myData, myOffset, myBuffer, theBlockLen, theBlockLen);

                /* Adjust the buffer using the count as a mask */
                for (int myMask = myCount--, myIndex = myBufferLen - 1; myMask != 0; myMask >>>= Byte.SIZE, myIndex--) {
                    myBuffer[myIndex] ^= (byte) myMask;
                }

                /* Decrypt the byte array */
                theCipher.finish(myBuffer, 0, myBufferLen, myResult, 0);

                /* Restore decrypted data */
                System.arraycopy(myResult, 0, myData, 0, theBlockLen);
                System.arraycopy(myResult, theBlockLen, myData, myOffset, theBlockLen);
            }
        }

        /* Check initialisation value */
        final int myCheckLen = theBlockLen - Integer.BYTES;
        boolean isCheckOK = true;
        for (int myInit = 0; isCheckOK && myInit < myCheckLen; myInit++) {
            isCheckOK = myData[myInit] == getIntegrityValue(myInit);
        }

        /* If we are OK */
        if (isCheckOK) {
            /* Obtain encoded length */
            final byte[] myByteLen = Arrays.copyOfRange(myData, myCheckLen, myCheckLen + Integer.BYTES);
            final int myEncodedLen = TethysDataConverter.byteArrayToInteger(myByteLen);

            /* Obtain zeroLen and check that it is valid */
            final int myZeroLen = myDataLen - myEncodedLen;
            isCheckOK = myZeroLen >= 0 && myZeroLen < theBlockLen;

            /* Check trailing bytes */
            for (int myZeros = myZeroLen, myLoc = myData.length - 1; isCheckOK
                                                                     && myZeros > 0; myZeros--, myLoc--) {
                /* Check that byte is zero */
                isCheckOK = myData[myLoc] == 0;
            }

            /* Return unwrapped data */
            if (isCheckOK) {
                return Arrays.copyOfRange(myData, theBlockLen, myData.length
                                                               - myZeroLen);
            }
        }

        /* Reject if checks fail */
        throw new GordianDataException("Integrity checks failed");
    }

    /**
     * Determine integrity value for position.
     * @param pIndex the index
     * @return the integrity value
     */
    private static byte getIntegrityValue(final int pIndex) {
        return (pIndex + 1) % INTEGRITY_MODULO < 2
                                                   ? INTEGRITY_VALUE1
                                                   : INTEGRITY_VALUE2;
    }
}
