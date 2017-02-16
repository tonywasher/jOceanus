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

import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * GordianKnot base for WrapCipher.
 * <p>
 * This class uses a variant of AESKW to wrap keyData. In particular it uses the cipher in CBC/CTR
 * mode with no padding, and hence use an initVector that is derive via the key.
 */
public abstract class GordianWrapCipher {
    /**
     * Wrap repeat count.
     */
    private static final int WRAP_COUNT = 6;

    /**
     * Integrity value.
     */
    private static final byte INTEGRITY_VALUE = (byte) 0xA6;

    /**
     * The Security Factory.
     */
    private final GordianFactory theFactory;

    /**
     * Underlying cipher.
     */
    private final GordianCipher<GordianSymKeyType> theCipher;

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
                                final GordianCipher<GordianSymKeyType> pCipher) {
        theFactory = pFactory;
        theCipher = pCipher;
        theBlockLen = getKeyType().getIVLength() >> 1;
    }

    /**
     * Obtain the keyType.
     * @return the keyType
     */
    public GordianSymKeyType getKeyType() {
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
     * Wrap key.
     * @param pKey the key to use to wrap the key
     * @param pKeyToWrap the key to wrap
     * @return the wrapped bytes
     * @throws OceanusException on error
     */
    public abstract byte[] wrapKey(GordianKey<GordianSymKeyType> pKey,
                                   GordianKey<?> pKeyToWrap) throws OceanusException;

    /**
     * unWrap key.
     * @param <T> type of key to be unwrapped
     * @param pKey the key to use to unwrap the key
     * @param pBytes the bytes to unwrap
     * @param pKeyType the type of key to be unwrapped
     * @return the unwrapped key
     * @throws OceanusException on error
     */
    public abstract <T> GordianKey<T> unwrapKey(GordianKey<GordianSymKeyType> pKey,
                                                byte[] pBytes,
                                                T pKeyType) throws OceanusException;

    /**
     * Wrap private key.
     * @param pKey the key to use to wrap the key
     * @param pKeyToWrap the key to wrap
     * @return the wrapped bytes
     * @throws OceanusException on error
     */
    public byte[] wrapKey(final GordianKey<GordianSymKeyType> pKey,
                          final GordianKeyPair pKeyToWrap) throws OceanusException {
        /* Access the KeyPair Generator */
        GordianKeyPairGenerator myGenerator = theFactory.getKeyPairGenerator(pKeyToWrap.getKeySpec());
        PKCS8EncodedKeySpec myPKCS8Key = myGenerator.getPKCS8Encoding(pKeyToWrap);
        return wrapBytes(pKey, myPKCS8Key.getEncoded());
    }

    /**
     * unWrap private key.
     * @param pKey the key to use to unwrap the key
     * @param pBytes the bytes to unwrap
     * @return the unwrapped key
     * @throws OceanusException on error
     */
    public PKCS8EncodedKeySpec unwrapKey(final GordianKey<GordianSymKeyType> pKey,
                                         final byte[] pBytes) throws OceanusException {
        /* Access the KeyPair Generator */
        byte[] myBytes = unwrapBytes(pKey, pBytes);
        return new PKCS8EncodedKeySpec(myBytes);
    }

    /**
     * Wrap bytes (based on AESKW).
     * @param pKey the key to use to wrap the bytes
     * @param pBytes the bytes to wrap
     * @return the wrapped bytes
     * @throws OceanusException on error
     */
    public byte[] wrapBytes(final GordianKey<GordianSymKeyType> pKey,
                            final byte[] pBytes) throws OceanusException {
        /* Check validity of key */
        theCipher.checkValidKey(pKey);

        /* Determine number of blocks */
        int myDataLen = pBytes.length;
        int myNumBlocks = myDataLen
                          / theBlockLen;

        /* Calculate padding length */
        int myTrueLen;
        if ((myDataLen % theBlockLen) == 0) {
            myTrueLen = myDataLen;
        } else {
            myNumBlocks++;
            myTrueLen = myNumBlocks
                        * theBlockLen;
        }
        int myZeroLen = myTrueLen
                        - myDataLen;

        /* Allocate buffer for data and encryption */
        int myBufferLen = theBlockLen << 1;
        byte[] myData = new byte[myTrueLen
                                 + theBlockLen];
        byte[] myBuffer = new byte[myBufferLen];
        byte[] myResult = new byte[myBufferLen];

        /* Access the IV */
        byte[] myIV = getWrapIV(pKey);

        /* Build the basic block */
        int myCheckLen = theBlockLen - 1;
        for (int i = 0; i < myCheckLen; i++) {
            myData[i] = INTEGRITY_VALUE;
        }
        myData[myCheckLen] = (byte) myZeroLen;
        System.arraycopy(pBytes, 0, myData, theBlockLen, myDataLen);

        /* Initialise the cipher */
        theCipher.initCipher(pKey, myIV, true);

        /* Loop WRAP_COUNT times */
        for (long myCycle = 0; myCycle < WRAP_COUNT; myCycle++) {
            /* Loop through the data blocks */
            for (int myBlock = 1, myOffset = theBlockLen; myBlock <= myNumBlocks; myBlock++, myOffset += theBlockLen) {
                /* Build the data to be encrypted */
                System.arraycopy(myData, 0, myBuffer, 0, theBlockLen);
                System.arraycopy(myData, myOffset, myBuffer, theBlockLen, theBlockLen);

                /* Encrypt the byte array */
                theCipher.finish(myBuffer, 0, myBufferLen, myResult, 0);

                /* Restore encrypted data */
                System.arraycopy(myResult, 0, myData, 0, theBlockLen);
                System.arraycopy(myResult, theBlockLen, myData, myOffset, theBlockLen);
            }
        }

        /* Return the wrapped data */
        return myData;
    }

    /**
     * unWrap bytes (based on AESKW).
     * @param pKey the key to use to unwrap the bytes
     * @param pBytes the bytes to unwrap
     * @return the unwrapped bytes
     * @throws OceanusException on error
     */
    public byte[] unwrapBytes(final GordianKey<GordianSymKeyType> pKey,
                              final byte[] pBytes) throws OceanusException {
        /* Check validity of key */
        theCipher.checkValidKey(pKey);

        /* Determine number of blocks */
        int myDataLen = pBytes.length
                        - theBlockLen;
        int myNumBlocks = myDataLen
                          / theBlockLen;

        /* Data must be a multiple of BlockLength */
        if ((myNumBlocks * theBlockLen) != myDataLen) {
            throw new GordianDataException("Invalid data length");
        }

        /* Access the IV */
        byte[] myIV = getWrapIV(pKey);

        /* Allocate buffers for data and encryption */
        int myBufferLen = theBlockLen << 1;
        byte[] myData = Arrays.copyOf(pBytes, pBytes.length);
        byte[] myBuffer = new byte[myBufferLen];
        byte[] myResult = new byte[myBufferLen];

        /* Initialise the cipher */
        theCipher.initCipher(pKey, myIV, false);

        /* Loop WRAP_COUNT times */
        for (long myCycle = WRAP_COUNT; myCycle > 0; myCycle--) {
            /* Loop through the data blocks */
            for (int myBlock = myNumBlocks, myOffset = theBlockLen
                                                       * myBlock; myBlock >= 1; myBlock--, myOffset -= theBlockLen) {
                /* Build the data to be decrypted */
                System.arraycopy(myData, 0, myBuffer, 0, theBlockLen);
                System.arraycopy(myData, myOffset, myBuffer, theBlockLen, theBlockLen);

                /* Decrypt the byte array */
                theCipher.finish(myBuffer, 0, myBufferLen, myResult, 0);

                /* Restore decrypted data */
                System.arraycopy(myResult, 0, myData, 0, theBlockLen);
                System.arraycopy(myResult, theBlockLen, myData, myOffset, theBlockLen);
            }
        }

        /* Determine check values */
        int myCheckLen = theBlockLen - 1;
        int myZeroLen = myData[myCheckLen];

        /* Check initialisation value */
        boolean isCheckOK = true;
        for (int myInit = 0; isCheckOK && myInit < myCheckLen; myInit++) {
            isCheckOK = myData[myInit] == INTEGRITY_VALUE;
        }

        /* Check valid ZeroLen */
        isCheckOK &= (myZeroLen >= 0)
                     && (myZeroLen < theBlockLen);

        /* Check trailing bytes */
        for (int myZeros = myZeroLen, myLoc = myData.length - 1; isCheckOK
                                                                 && myZeros > 0; myZeros--, myLoc--) {
            /* Check that byte is zero */
            isCheckOK = myData[myLoc] == 0;
        }

        /* Reject if checks fail */
        if (!isCheckOK) {
            throw new GordianDataException("Integrity checks failed");
        }

        /* Return unwrapped data */
        return Arrays.copyOfRange(myData, theBlockLen, myData.length
                                                       - myZeroLen);
    }

    /**
     * Determine the IV for a key.
     * @param pKey the key
     * @return the IV
     * @throws OceanusException on error
     */
    private byte[] getWrapIV(final GordianKey<GordianSymKeyType> pKey) throws OceanusException {
        /* Create the MAC and standard data */
        GordianMacSpec myMacSpec = GordianMacSpec.hMac(theFactory.getDefaultDigest());
        GordianMac myMac = theFactory.createMac(myMacSpec);
        myMac.initMac(pKey.convertToKeyType(myMacSpec));

        /* Update using personalisation */
        byte[] myIV = myMac.finish(theFactory.getPersonalisation());

        /* Return appropriate length of data */
        int myLen = getKeyType().getIVLength();
        return myIV.length > myLen
                                   ? Arrays.copyOf(myIV, myLen)
                                   : myIV;
    }
}
