/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.ext.engines;

import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.bouncycastle.crypto.ec.ECElGamalEncryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Elliptic curve encryptor.
 * Based on https://onlinelibrary.wiley.com/doi/pdf/10.1002/sec.1702
 */
public class GordianEllipticEncryptor {
    /**
     * CoFactor must be less than or equal to 20. Boundary is actually 10 < b < 76.
     */
    private static final int MAXCOFACTOR = 20;

    /**
     * The max iterations to try for a point.
     */
    private static final int MAXITERATION = 1 << Byte.SIZE;

    /**
     * The encryptor.
     */
    private final ECElGamalEncryptor theEncryptor;

    /**
     * The decryptor.
     */
    private final ECElGamalDecryptor theDecryptor;

    /**
     * The ECCurve.
     */
    private ECCurve theCurve;

    /**
     * is encryption available?
     */
    private boolean isAvailable;

    /**
     * Are we encrypting or decrypting?
     */
    private boolean encrypting;

    /**
     * Constructor.
     */
    public GordianEllipticEncryptor() {
        theEncryptor = new ECElGamalEncryptor();
        theDecryptor = new ECElGamalDecryptor();
    }

    /**
     * Initialise for encryption.
     * @param pPublicKey the publicKey
     * @param pRandom the secureRandom
     */
    public void initForEncrypt(final ECPublicKeyParameters pPublicKey,
                               final SecureRandom pRandom) {
        /* Access domain parameters */
        final ECDomainParameters myDomain = pPublicKey.getParameters();
        if (isUnsupported(myDomain)) {
            throw new IllegalArgumentException("Unsupported curve");
        }

        /* Record details */
        theCurve = myDomain.getCurve();
        isAvailable = true;
        encrypting = true;

        /* Initialise for encryption */
        final ParametersWithRandom myParms = new ParametersWithRandom(pPublicKey, pRandom);
        theEncryptor.init(myParms);
    }

    /**
     * Initialise for decryption.
     * @param pPrivateKey the privateKey
     */
    public void initForDecrypt(final ECPrivateKeyParameters pPrivateKey) {
        /* Access domain parameters */
        final ECDomainParameters myDomain = pPrivateKey.getParameters();
        if (isUnsupported(myDomain)) {
            throw new IllegalArgumentException("Unsupported curve");
        }

        /* Record details */
        theCurve = myDomain.getCurve();
        isAvailable = true;
        encrypting = false;

        /* Initialise for decryption */
        theDecryptor.init(pPrivateKey);
    }

    /**
     * Check whether encryption is available for this domain.
     * @param pDomain the domain
     * @return true/false
     */
    private boolean isUnsupported(final ECDomainParameters pDomain) {
        return pDomain.getH().compareTo(BigInteger.valueOf(MAXCOFACTOR)) > 0;
    }

    /**
     * Obtain the length of field.
     * @return the length of the field.
     */
    private int getFieldLength() {
        return (theCurve.getFieldSize() + Byte.SIZE - 1) / Byte.SIZE;
    }

    /**
     * Obtain the length of block (1 less than fieldLength).
     * @return the length of the block.
     */
    private int getBlockLength() {
        return getFieldLength() - 1;
    }

    /**
     * Obtain the length of the plain block (2 less than blockLength).
     * @return the length of the block.
     */
    private int getPlainBlockLength() {
        return getBlockLength() - 2;
    }

    /**
     * Obtain the length of the encrypted block.
     * @return the length of the block.
     */
    private int getEncodedBlockLength() {
        return (getFieldLength() + 1) << 1;
    }

    /**
     * Obtain the length of the buffer required to receive the decrypted data.
     * @param pLength the length of encrypted data
     * @return the number of bytes.
     */
    private int getDecryptedLength(final int pLength) {
        return getPlainBlockLength() * getNumBlocks(pLength, getEncodedBlockLength());
    }

    /**
     * Obtain the length of the buffer required for the encrypted output.
     * @param pLength the length of clear data
     * @return the number of bytes.
     */
    private int getEncryptedLength(final int pLength) {
        return getEncodedBlockLength() * getNumBlocks(pLength, getPlainBlockLength());
    }

    /**
     * Obtain the number of blocks required for the length in terms of blocks.
     * @param pLength the length of clear data
     * @param pBlockLength the blockLength
     * @return the number of blocks.
     */
    private static int getNumBlocks(final int pLength, final int pBlockLength) {
        return (pLength + pBlockLength - 1) / pBlockLength;
    }

    /**
     * Encrypt a data buffer.
     * @param pData the buffer to encrypt
     * @return the encrypted keyPair
     * @throws InvalidCipherTextException on error
     */
    public byte[] encrypt(final byte[] pData) throws InvalidCipherTextException {
        /* Check that we are set to encrypt */
        if (!isAvailable || !encrypting) {
            throw new IllegalStateException("Not initialised for encrypting");
        }

        /* Create the output buffer */
        int myInLen = pData.length;
        final byte[] myOutput = new byte[getEncryptedLength(pData.length)];

        /* Access block lengths */
        final int myInBlockLength = getPlainBlockLength();

        /* Loop encrypting the blocks */
        int myInOff = 0;
        int myOutOff = 0;
        while (myInLen > 0) {
            /* Encrypt to an ECPair */
            final int myLen = Math.min(myInLen, myInBlockLength);
            final ECPair myPair = encryptToPair(pData, myInOff, myLen);

            /* Convert into the output buffer */
            myOutOff += convertFromECPair(myPair, myOutput, myOutOff);

            /* Move to next block */
            myInOff += myInBlockLength;
            myInLen -= myInBlockLength;
        }

        /* Return full buffer if possible */
        return myOutOff == myOutput.length
                ? myOutput
                : Arrays.copyOf(myOutput, myOutOff);
    }

    /**
     * Encrypt a value.
     * @param pData the buffer to encrypt
     * @param pInOff the offset in the buffer
     * @param pInLen the length of data to encrypt
     * @return the encrypted keyPair
     * @throws InvalidCipherTextException on error
     */
    private ECPair encryptToPair(final byte[] pData,
                                 final int pInOff,
                                 final int pInLen) throws InvalidCipherTextException {
        /* Convert the data to an ECPoint */
        final ECPoint myPoint = convertToECPoint(pData, pInOff, pInLen);

        /* Encrypt the data */
        return theEncryptor.encrypt(myPoint);
    }

    /**
     * Convert to ECPoint.
     * @param pInBuffer the input buffer
     * @param pInOff the input offset
     * @param pInLen the length of data to process
     * @return the ECPair
     * @throws InvalidCipherTextException on error
     */
    private ECPoint convertToECPoint(final byte[] pInBuffer,
                                     final int pInOff,
                                     final int pInLen) throws InvalidCipherTextException {
        /* Check lengths */
        final int myLen = getBlockLength();
        if (pInLen > myLen - 2
                || pInLen <= 0) {
            throw new IllegalArgumentException("Invalid input length");
        }
        if (pInBuffer.length - pInOff < pInLen) {
            throw new IllegalArgumentException("Invalid input buffer");
        }

        /* Create the work buffer and copy data in */
        final byte[] myX = new byte[myLen + 1];

        /* Calculate the start position and place data and padding */
        final int myStart = myLen - pInLen;
        System.arraycopy(pInBuffer, pInOff, myX, myStart, pInLen);
        myX[myStart - 1] = 1;

        /* Loop to obtain point on curve */
        for (int i = 0; i < MAXITERATION; i++) {
            /* Check to see whether the value is on the curve */
            final ECPoint myPoint = checkOnCurve(myX);

            /* If we have a valid point */
            if (myPoint != null) {
                return myPoint;
            }

            /* Increment the test value */
            myX[myLen]++;
        }

        /* No possible value found */
        throw new InvalidCipherTextException("Unable to find point on curve");
    }

    /**
     * Check whether the point is on the curve.
     * @param pX the byte buffer representing X
     * @return the ECPoint if on curve, else null
     */
    private ECPoint checkOnCurve(final byte[] pX) {
        /* Protect against exceptions */
        try {
            /* Create a compressed point */
            final int myFieldLen = getFieldLength();
            final byte[] myCompressed = new byte[myFieldLen + 1];
            System.arraycopy(pX, 0, myCompressed, 1, myFieldLen);
            myCompressed[0] = 2;
            final ECPoint myPoint = theCurve.decodePoint(myCompressed);

            /* Check the point */
            return myPoint.isValid()
                    ? myPoint
                    : null;

            /* Handle invalid coding */
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Convert from ECPair.
     * @param pPair the ECPoint
     * @param pOutBuffer the output buffer
     * @param pOutOff the output offset
     * @return the length of data decoded
     * @throws InvalidCipherTextException on error
     */
    private int convertFromECPair(final ECPair pPair,
                                  final byte[] pOutBuffer,
                                  final int pOutOff) throws InvalidCipherTextException {
        /* Check length */
        final int myLen = getFieldLength() + 1;
        if (pOutBuffer.length - pOutOff < myLen << 1) {
            throw new IllegalArgumentException("Output buffer too small");
        }

        /* Access the two encoded parameters  */
        final byte[] myX = pPair.getX().getEncoded(true);
        final byte[] myY = pPair.getY().getEncoded(true);
        if (myX.length != myLen || myY.length != myLen) {
            throw new InvalidCipherTextException("Bad encoding");
        }

        /* Copy to the output buffer */
        System.arraycopy(myX, 0, pOutBuffer, pOutOff, myLen);
        System.arraycopy(myY, 0, pOutBuffer, pOutOff + myLen, myLen);
        return myLen << 1;
    }

    /**
     * Decrypt a data buffer.
     * @param pData the buffer to encrypt
     * @return the encrypted keyPair
     * @throws InvalidCipherTextException on error
     */
    public byte[] decrypt(final byte[] pData) throws InvalidCipherTextException {
        /* Check that we are set to encrypt */
        if (!isAvailable || encrypting) {
            throw new IllegalStateException("Not initialised for decrypting");
        }

        /* Create the output buffer */
        int myInLen = pData.length;
        final byte[] myOutput = new byte[getDecryptedLength(pData.length)];

        /* Access block lengths */
        final int myInBlockLength = getEncodedBlockLength();

        /* Loop decrypting the blocks */
        int myInOff = 0;
        int myOutOff = 0;
        while (myInLen > 0) {
            /* Encrypt to an ECPair */
            final ECPair myPair = convertToECPair(pData, myInOff);

            /* Convert into the output buffer */
            myOutOff += decryptFromECPair(myPair, myOutput, myOutOff);

            /* Move to next block */
            myInOff += myInBlockLength;
            myInLen -= myInBlockLength;
        }

        /* Return full buffer if possible */
        if (myOutOff == myOutput.length) {
            return myOutput;
        }

        /* Cut down buffer */
        final byte[] myReturn = Arrays.copyOf(myOutput, myOutOff);
        Arrays.fill(myOutput, (byte) 0);
        return myReturn;
    }

    /**
     * Decrypt a value.
     * @param pPair the pair to decrypt
     * @param pOutBuffer the output buffer
     * @param pOutOff the output offset
     * @return the length of data decoded
     * @throws InvalidCipherTextException on error
     */
    private int decryptFromECPair(final ECPair pPair,
                                  final byte[] pOutBuffer,
                                  final int pOutOff) throws InvalidCipherTextException {
        /* Decrypt the pair */
        final ECPoint myPoint = theDecryptor.decrypt(pPair);
        return convertFromECPoint(myPoint, pOutBuffer, pOutOff);
    }

    /**
     * Convert from ECPoint.
     * @param pPoint the ECPoint
     * @param pOutBuffer the output buffer
     * @param pOutOff the output offset
     * @return the length of data decoded
     * @throws InvalidCipherTextException on error
     */
    private int convertFromECPoint(final ECPoint pPoint,
                                   final byte[] pOutBuffer,
                                   final int pOutOff) throws InvalidCipherTextException {
        /* Obtain the X co-ordinate */
        final BigInteger myX = pPoint.getAffineXCoord().toBigInteger();
        final byte[] myBuf = myX.toByteArray();

        /* Set defaults */
        int myStart = -1;
        final int myEnd = myBuf.length - 1;

        /* Loop through the data in fixed time */
        for (int myIndex = 0; myIndex < myEnd; myIndex++) {
            /* If the value is non-zero and we have not yet found start */
            /* Disable the short-circuit logic!! */
            if (myBuf[myIndex] != 0
                    & myStart == -1) {
                myStart = myIndex;
            }
        }

        /* Check validity, disabling short circuit logic */
        if (myStart == -1 | myBuf[myStart] != 1) {
            throw new InvalidCipherTextException("Invalid data");
        }

        /* Bump past padding */
        myStart++;

        /* Check length */
        final int myOutLen = myEnd - myStart;
        if (pOutBuffer.length - pOutOff < myOutLen) {
            throw new IllegalArgumentException("Output buffer too small");
        }

        /* Copy the data out */
        System.arraycopy(myBuf, myStart, pOutBuffer, pOutOff, myOutLen);
        return myOutLen;
    }

    /**
     * Convert to ECPair.
     * @param pInBuffer the input buffer
     * @param pInOff the input offset
     * @return the ECPair
     */
    private ECPair convertToECPair(final byte[] pInBuffer,
                                   final int pInOff)  {
        /* Check length */
        final int myLen = getFieldLength() + 1;
        if (pInBuffer.length - pInOff < myLen << 1) {
            throw new IllegalArgumentException("Invalid input buffer");
        }

        /* Access the X point */
        final byte[] myXbytes  = new byte[myLen];
        System.arraycopy(pInBuffer, pInOff, myXbytes, 0, myLen);
        final ECPoint myX = theCurve.decodePoint(myXbytes);

        /* Access the Y point */
        final byte[] myYbytes = new byte[myLen];
        System.arraycopy(pInBuffer, pInOff + myLen, myYbytes, 0, myLen);
        final ECPoint myY = theCurve.decodePoint(myYbytes);

        /* Create the ECPair */
        return new ECPair(myX, myY);
    }
}
