/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.EnumMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import net.sourceforge.joceanus.jgordianknot.JGordianCryptoException;
import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jtethys.JOceanusException;

import org.bouncycastle.util.Arrays;

/**
 * Wrapper class for Cipher used for encryption data objects.
 * <p>
 * This class also includes a KeyWrapper algorithm that is a modified form of RFC3394. The differences are as follows
 * <ol>
 * <li>CBC/NoPadding is used as the encryption mode rather than ECB. This requires an initialisation vector.
 * <li>The initialisation vector is used as the integrity header check value as well as for initialising the cipher.
 * <li>Encryption etc. is performed using the doFinal() method so that the cipher is reinitialised on each step.
 * <li>With the inclusion of ThreeFish (which has a cipher block size of 256-bits), the algorithm will always use a wrap block size of half the cipher block
 * size - normally 64-bits, but 128-bits in the case of ThreeFish. This requires the integrity check value to be expanded to 128-bits on ThreeFish so that is
 * always fills a whole number of blocks.
 * <li>The algorithm supports byte arrays that are not strict multiples of the wrap block size. Such arrays are padded with null bytes up to the block size and
 * the number of padding bytes is stored as the last byte of the integrity check vector.
 * </ol>
 */
public class DataCipher {
    /**
     * Cipher creation failure error text.
     */
    private static final String ERROR_CREATE = "Failed to create Cipher";

    /**
     * Data length error text.
     */
    private static final String ERROR_DATALEN = "Invalid data length";

    /**
     * Wrap failure error text.
     */
    private static final String ERROR_WRAP = "Failed to wrap bytes";

    /**
     * UnWrap failure error text.
     */
    private static final String ERROR_UNWRAP = "Failed to unwrap bytes";

    /**
     * UnWrap integrity error text.
     */
    private static final String ERROR_INTEGRITY = "Integrity checks failed";

    /**
     * Wrap repeat count.
     */
    private static final int WRAP_COUNT = 6;

    /**
     * Multiplier to obtain IV from vector.
     */
    private static final int VECTOR_SHIFT = 7;

    /**
     * The cipher.
     */
    private final Map<CipherMode, Cipher> theCipherMap;

    /**
     * The Provider Name.
     */
    private final String theProviderName;

    /**
     * The Block Size.
     */
    private final int theBlockSize;

    /**
     * The SymmetricKey.
     */
    private final SymmetricKey theSymKey;

    /**
     * The SecretKey.
     */
    private final SecretKey theSecretKey;

    /**
     * Get Symmetric Key Type.
     * @return the key type
     */
    protected SymKeyType getSymKeyType() {
        return theSymKey.getKeyType();
    }

    /**
     * Get Block Size.
     * @return the block size
     */
    protected int getBlockSize() {
        return theBlockSize;
    }

    /**
     * Constructor.
     * @param pKey the Symmetric Key
     * @throws JOceanusException on error
     */
    protected DataCipher(final SymmetricKey pKey) throws JOceanusException {
        /* Record keys */
        theSymKey = pKey;
        theSecretKey = theSymKey.getSecretKey();

        /* Create the Cipher Map */
        theCipherMap = new EnumMap<CipherMode, Cipher>(CipherMode.class);

        /* Allocate an initial Cipher */
        SecurityGenerator myGenerator = theSymKey.getGenerator();
        theProviderName = myGenerator.getProviderName();
        Cipher myCipher = getCipher(CipherMode.CBC);
        theBlockSize = myCipher.getBlockSize();
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

        /* Make sure that the object is a DataCipher */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the target Cipher */
        DataCipher myThat = (DataCipher) pThat;

        /* Check the symmetric key */
        return theSymKey.equals(myThat.theSymKey);
    }

    @Override
    public int hashCode() {
        return theSymKey.hashCode();
    }

    /**
     * Encrypt bytes.
     * @param pBytes bytes to encrypt
     * @param pMode the cipher mode
     * @param pVector initialisation vector
     * @return Encrypted bytes
     * @throws JOceanusException on error
     */
    public byte[] encryptBytes(final byte[] pBytes,
                               final CipherMode pMode,
                               final byte[] pVector) throws JOceanusException {
        /* Obtain the Cipher */
        Cipher myCipher = getCipher(pMode);

        /* Access the shifted vector */
        byte[] myShift = getShiftedVector(pVector);

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the shifted vector */
            AlgorithmParameterSpec myParms = new IvParameterSpec(myShift);
            myCipher.init(Cipher.ENCRYPT_MODE, theSecretKey, myParms);

            /* Encrypt the byte array */
            return myCipher.doFinal(pBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new JGordianCryptoException("Failed to encrypt bytes", e);
        }
    }

    /**
     * Decrypt bytes.
     * @param pBytes bytes to decrypt
     * @param pMode the cipher mode
     * @param pVector initialisation vector
     * @return Decrypted bytes
     * @throws JOceanusException on error
     */
    public byte[] decryptBytes(final byte[] pBytes,
                               final CipherMode pMode,
                               final byte[] pVector) throws JOceanusException {
        /* Obtain the Cipher */
        Cipher myCipher = getCipher(pMode);

        /* Access the shifted vector */
        byte[] myShift = getShiftedVector(pVector);

        /* Protect against exceptions */
        try {
            /* Initialise the cipher using the vector */
            AlgorithmParameterSpec myParms = new IvParameterSpec(myShift);
            myCipher.init(Cipher.DECRYPT_MODE, theSecretKey, myParms);

            /* Encrypt the byte array */
            return myCipher.doFinal(pBytes);
        } catch (IllegalBlockSizeException | BadPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new JGordianCryptoException("Failed to decrypt bytes", e);
        }
    }

    /**
     * Wrap bytes.
     * @param pBytes the bytes to wrap
     * @param pVector initialisation vector
     * @return the wrapped bytes
     * @throws JOceanusException on error
     */
    protected byte[] wrapBytes(final byte[] pBytes,
                               final byte[] pVector) throws JOceanusException {
        /* Determine the block length */
        int myBlockLen = getBlockSize() >> 1;

        /* Determine number of blocks */
        int myDataLen = pBytes.length;
        int myNumBlocks = myDataLen
                          / myBlockLen;

        /* Calculate padding length */
        int myTrueLen;
        if ((myDataLen % myBlockLen) == 0) {
            myTrueLen = myDataLen;
        } else {
            myNumBlocks++;
            myTrueLen = myNumBlocks
                        * myBlockLen;
        }
        int myZeroLen = myTrueLen
                        - myDataLen;

        /* Allocate buffer for data and encryption */
        byte[] myData = new byte[myTrueLen
                                 + myBlockLen];
        byte[] myBuffer = new byte[myBlockLen << 1];

        /* Access the shifted vector */
        byte[] myShift = getShiftedVector(pVector);

        /* Build the basic block */
        int myCheckLen = myBlockLen - 1;
        System.arraycopy(myShift, 0, myData, 0, myCheckLen);
        myData[myCheckLen] = (byte) myZeroLen;
        System.arraycopy(pBytes, 0, myData, myBlockLen, myDataLen);

        /* Access Cipher */
        Cipher myCipher = getCipher(CipherMode.OFB);

        /* Initialise the cipher */
        try {
            /* Initialise the cipher */
            myCipher.init(Cipher.ENCRYPT_MODE, theSecretKey, new IvParameterSpec(myShift));

            /* Loop WRAP_COUNT times */
            for (int myCycle = 0, myCount = 1; myCycle < WRAP_COUNT; myCycle++) {
                /* Loop through the data blocks */
                for (int myBlock = 1, myOffset = myBlockLen; myBlock <= myNumBlocks; myBlock++, myOffset += myBlockLen) {
                    /* Build the data to be encrypted */
                    System.arraycopy(myData, 0, myBuffer, 0, myBlockLen);
                    System.arraycopy(myData, myOffset, myBuffer, myBlockLen, myBlockLen);

                    /* Encrypt the byte array */
                    byte[] myResult = myCipher.doFinal(myBuffer);

                    /* Adjust the result using the count as a mask */
                    for (int myMask = myCount++, myIndex = myBlockLen - 1; myMask != 0; myMask >>>= Byte.SIZE, myIndex--) {
                        myResult[myIndex] ^= (byte) myMask;
                    }

                    /* Restore encrypted data */
                    System.arraycopy(myResult, 0, myData, 0, myBlockLen);
                    System.arraycopy(myResult, myBlockLen, myData, myOffset, myBlockLen);
                }
            }
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new JGordianCryptoException(ERROR_WRAP, e);
        }

        /* Return the wrapped data */
        return myData;
    }

    /**
     * unWrap bytes.
     * @param pBytes the bytes to unwrap
     * @param pVector initialisation vector
     * @return the unwrapped bytes
     * @throws JOceanusException on error
     */
    protected byte[] unwrapBytes(final byte[] pBytes,
                                 final byte[] pVector) throws JOceanusException {
        /* Determine the block length */
        int myBlockLen = getBlockSize() >> 1;

        /* Determine number of blocks */
        int myDataLen = pBytes.length
                        - myBlockLen;
        int myNumBlocks = myDataLen
                          / myBlockLen;

        /* Data must be a multiple of BlockLength */
        if ((myNumBlocks * myBlockLen) != myDataLen) {
            throw new IllegalArgumentException(ERROR_DATALEN);
        }

        /* Access the shifted vector */
        byte[] myShift = getShiftedVector(pVector);

        /* Allocate buffers for data and encryption */
        byte[] myData = Arrays.copyOf(pBytes, pBytes.length);
        byte[] myBuffer = new byte[myBlockLen << 1];

        /* Access Cipher */
        Cipher myCipher = getCipher(CipherMode.OFB);

        /* Protect against exceptions */
        try {
            /* Initialise the cipher */
            myCipher.init(Cipher.DECRYPT_MODE, theSecretKey, new IvParameterSpec(myShift));

            /* Loop WRAP_COUNT times */
            for (int myCycle = WRAP_COUNT - 1, myCount = myNumBlocks
                                                         * (myCycle + 1); myCycle >= 0; myCycle--) {
                /* Loop through the data blocks */
                for (int myBlock = myNumBlocks, myOffset = myBlockLen
                                                           * myBlock; myBlock >= 1; myBlock--, myOffset -= myBlockLen) {
                    /* Build the data to be decrypted */
                    System.arraycopy(myData, 0, myBuffer, 0, myBlockLen);
                    System.arraycopy(myData, myOffset, myBuffer, myBlockLen, myBlockLen);

                    /* Adjust the buffer using the count as a mask */
                    for (int myMask = myCount--, myIndex = myBlockLen - 1; myMask != 0; myMask >>>= Byte.SIZE, myIndex--) {
                        myBuffer[myIndex] ^= (byte) myMask;
                    }

                    /* Decrypt the byte array */
                    byte[] myResult = myCipher.doFinal(myBuffer);

                    /* Restore decrypted data */
                    System.arraycopy(myResult, 0, myData, 0, myBlockLen);
                    System.arraycopy(myResult, myBlockLen, myData, myOffset, myBlockLen);
                }
            }
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            throw new JGordianCryptoException(ERROR_UNWRAP, e);
        }

        /* Determine check values */
        int myCheckLen = myBlockLen - 1;
        int myZeroLen = myData[myCheckLen];

        /* Check initialisation value */
        boolean isCheckOK = Arrays.areEqual(Arrays.copyOf(myData, myCheckLen), Arrays.copyOf(myShift, myCheckLen));

        /* Check valid ZeroLen */
        isCheckOK &= (myZeroLen >= 0)
                     && (myZeroLen < myBlockLen);

        /* Check trailing bytes */
        for (int myZeros = myZeroLen, myLoc = myData.length - 1; isCheckOK
                                                                 && myZeros > 0; myZeros--, myLoc--) {
            /* Check that byte is zero */
            isCheckOK = myData[myLoc] == 0;
        }

        /* Reject if checks fail */
        if (!isCheckOK) {
            throw new JGordianDataException(ERROR_INTEGRITY);
        }

        /* Return unwrapped data */
        return Arrays.copyOfRange(myData, myBlockLen, myData.length
                                                      - myZeroLen);
    }

    /**
     * Obtain shifted Initialisation vector.
     * @param pVector the initialisation vector
     * @return the shifted vector
     */
    private byte[] getShiftedVector(final byte[] pVector) {
        /* Determine length of input and output vectors */
        int myVectorLen = pVector.length;
        int myLen = getBlockSize();
        SymKeyType myType = theSymKey.getKeyType();
        byte[] myNew = new byte[myLen];

        /* Determine index into array for Key Type */
        int myIndex = VECTOR_SHIFT
                      * myType.getId();
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
     * Obtain Cipher for mode.
     * @param pMode the Cipher Mode
     * @return the Cipher
     * @throws JOceanusException on error
     */
    private Cipher getCipher(final CipherMode pMode) throws JOceanusException {
        /* Look up Cipher in map */
        Cipher myCipher = theCipherMap.get(pMode);

        /* If we have not used this cipher before */
        if (myCipher == null) {
            try {
                /* Allocate the Cipher */
                SymKeyType myKeyType = theSymKey.getKeyType();
                myCipher = Cipher.getInstance(myKeyType.getDataCipher(pMode), theProviderName);

                /* Store the Cipher in the map */
                theCipherMap.put(pMode, myCipher);

            } catch (NoSuchAlgorithmException | NoSuchProviderException | NoSuchPaddingException e) {
                throw new JGordianCryptoException(ERROR_CREATE, e);
            }
        }

        /* Return the cipher */
        return myCipher;
    }
}
