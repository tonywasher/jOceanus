/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.cipher;

import net.sourceforge.joceanus.jgordianknot.api.asym.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianWrapper;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianAsymFactory;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKey;
import net.sourceforge.joceanus.jgordianknot.impl.core.key.GordianCoreKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypair.GordianCoreKeyPairGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysDataConverter;
import org.bouncycastle.util.Arrays;

import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * GordianKnot base for WrapCipher.
 * <p>
 * This class uses a variant of RFC5649 to wrap keyData. It has been modified so that it does not
 * require a 128-block cipher.
 */
public class GordianCoreWrapper
        implements GordianWrapper {
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
    private final GordianCoreFactory theFactory;

    /**
     * Underlying cipher.
     */
    private final GordianCoreCipher<GordianSymKeySpec> theCipher;

    /**
     * The block size.
     */
    private final int theBlockLen;

    /**
     * Constructor.
     * @param pFactory the Security Factory
     * @param pCipher the underlying cipher
     */
    GordianCoreWrapper(final GordianCoreFactory pFactory,
                       final GordianCoreCipher<GordianSymKeySpec> pCipher) {
        theFactory = pFactory;
        theCipher = pCipher;
        theBlockLen = getKeySpec().getBlockLength().getByteLength() >> 1;
    }

   @Override
    public GordianSymKeySpec getKeySpec() {
        return theCipher.getKeyType();
    }

    /**
     * Obtain the factory.
     * @return the factory
     */
    protected GordianCoreFactory getFactory() {
        return theFactory;
    }

    @Override
    public byte[] secureKey(final GordianKey<GordianSymKeySpec> pKey,
                            final GordianKey<?> pKeyToSecure) throws OceanusException {
        /* Secure the bytes */
        return secureBytes(pKey, ((GordianCoreKey<?>) pKeyToSecure).getKeyBytes());
    }

    @Override
    public <T extends GordianKeySpec> GordianKey<T> deriveKey(final GordianKey<GordianSymKeySpec> pKey,
                                                              final byte[] pSecuredKey,
                                                              final T pKeyType) throws OceanusException {
        /* Unwrap the bytes */
        final byte[] myBytes = deriveBytes(pKey, pSecuredKey);

        /* Generate the key */
        final GordianCipherFactory myCipherFactory = theFactory.getCipherFactory();
        final GordianCoreKeyGenerator<T> myGenerator = (GordianCoreKeyGenerator<T>) myCipherFactory.getKeyGenerator(pKeyType);
        return myGenerator.buildKeyFromBytes(myBytes);
    }

    @Override
    public byte[] securePrivateKey(final GordianKey<GordianSymKeySpec> pKey,
                                   final GordianKeyPair pKeyPairToSecure) throws OceanusException {
        /* Access the KeyPair Generator */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(pKeyPairToSecure.getKeySpec());
        final PKCS8EncodedKeySpec myPKCS8Key = myGenerator.getPKCS8Encoding(pKeyPairToSecure);
        return secureBytes(pKey, myPKCS8Key.getEncoded());
    }

    @Override
    public GordianKeyPair deriveKeyPair(final GordianKey<GordianSymKeySpec> pKey,
                                        final X509EncodedKeySpec pPublicKeySpec,
                                        final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Access the PKCS8Encoding */
        final PKCS8EncodedKeySpec myPrivate = derivePrivateKeySpec(pKey, pSecuredPrivateKey);

        /* Determine and check the keySpec */
        final GordianAsymFactory myAsym = theFactory.getAsymmetricFactory();
        final GordianAsymKeySpec myKeySpec = myAsym.determineKeySpec(pPublicKeySpec);
        if (!myKeySpec.equals(myAsym.determineKeySpec(myPrivate))) {
            throw new GordianLogicException("Mismatch on keySpecs");
        }

        /* Derive the keyPair */
        final GordianCoreKeyPairGenerator myGenerator = (GordianCoreKeyPairGenerator) myAsym.getKeyPairGenerator(myKeySpec);
        return myGenerator.deriveKeyPair(pPublicKeySpec, myPrivate);
    }

    /**
     * derive private key.
     * @param pKey the key to use to derive the key
     * @param pSecuredPrivateKey the secured privateKey
     * @return the derived key
     * @throws OceanusException on error
     */
    private PKCS8EncodedKeySpec derivePrivateKeySpec(final GordianKey<GordianSymKeySpec> pKey,
                                                     final byte[] pSecuredPrivateKey) throws OceanusException {
        /* Derive the keySpec */
        final byte[] myBytes = deriveBytes(pKey, pSecuredPrivateKey);
        return new PKCS8EncodedKeySpec(myBytes);
    }

    @Override
    public byte[] secureBytes(final GordianKey<GordianSymKeySpec> pKey,
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

        /* Determine semantics of the initial block */
        final byte[] myByteLen = TethysDataConverter.integerToByteArray(myDataLen);
        int myCheckLen = theBlockLen - Integer.BYTES;
        int myHdrLen = theBlockLen;

        /* Handle 64-bit ciphers */
        if (myCheckLen == 0) {
            myCheckLen = theBlockLen;
            myHdrLen += Integer.BYTES;
            myNumBlocks++;
        }

        /* Add a block for random data */
        myNumBlocks++;
        myHdrLen += theBlockLen;

        /* Allocate buffer for data and encryption */
        final int myBufferLen = theBlockLen << 1;
        final byte[] myData = new byte[myTrueLen
                + myHdrLen];
        final byte[] myBuffer = new byte[myBufferLen];
        final byte[] myResult = new byte[myBufferLen];
        final byte[] myRandom = new byte[theBlockLen];

        /* Build the initial block */
        for (int i = 0; i < myCheckLen; i++) {
            myData[i] = getIntegrityValue(i);
        }
        System.arraycopy(myByteLen, 0, myData, myCheckLen, Integer.BYTES);
        theFactory.getRandomSource().getRandom().nextBytes(myRandom);
        System.arraycopy(myRandom, 0, myData, myCheckLen + Integer.BYTES, theBlockLen);
        System.arraycopy(pBytesToSecure, 0, myData, myHdrLen, myDataLen);

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

    @Override
    public byte[] deriveBytes(final GordianKey<GordianSymKeySpec> pKey,
                              final byte[] pSecuredBytes) throws OceanusException {
        /* Check validity of key */
        theCipher.checkValidKey(pKey);

        /* Determine number of blocks */
        int myDataLen = pSecuredBytes.length
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

        /* Handle 64-bit ciphers */
        int myCheckLen = theBlockLen - Integer.BYTES;
        if (myCheckLen == 0) {
            myCheckLen = theBlockLen;
            myDataLen -= theBlockLen;
        }

        /* Adjust for random data */
        myDataLen -= theBlockLen;

        /* Check initialisation value */
        boolean isCheckOK = true;
        for (int myInit = 0; myInit < myCheckLen; myInit++) {
            if (myData[myInit] != getIntegrityValue(myInit)) {
                isCheckOK = false;
            }
        }

        /* Obtain encoded length */
        final byte[] myByteLen = Arrays.copyOfRange(myData, myCheckLen, myCheckLen + Integer.BYTES);
        final int myEncodedLen = TethysDataConverter.byteArrayToInteger(myByteLen);

        /* Obtain zeroLen and check that it is valid */
        final int myZeroLen = myDataLen - myEncodedLen;
        if (myZeroLen < 0) {
            isCheckOK = false;
        }
        if (myZeroLen >= theBlockLen) {
            isCheckOK = false;
        }

        /* Check trailing bytes */
        for (int myZeros = myZeroLen, myLoc = myData.length - 1; myZeros > 0; myZeros--, myLoc--) {
            /* Check that byte is zero */
            if (myData[myLoc] != 0) {
                isCheckOK = false;
            }
        }

        /* Return unwrapped data */
        if (isCheckOK) {
            return Arrays.copyOfRange(myData, myCheckLen + Integer.BYTES + theBlockLen, myData.length
                    - myZeroLen);
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

    @Override
    public int getKeyWrapExpansion() {
        return getKeyWrapExpansion(getKeySpec().getBlockLength());
    }

    /**
     * Obtain keyWrapExpansion for a blockLen.
     * @param pBlockLen the number of bits in the blockLen
     * @return the keyWrap expansion
     */
    public static int getKeyWrapExpansion(final GordianLength pBlockLen) {
        final int myBlockLen = pBlockLen.getByteLength() >> 1;
        final int myNumBlocks = 1 + myBlockLen <= Integer.BYTES
                                ? 2
                                : 1;
        return myNumBlocks * myBlockLen;
    }
}
