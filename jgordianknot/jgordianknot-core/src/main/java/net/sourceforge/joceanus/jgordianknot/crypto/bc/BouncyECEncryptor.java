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
package net.sourceforge.joceanus.jgordianknot.crypto.bc;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.bouncycastle.crypto.ec.ECElGamalEncryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import net.sourceforge.joceanus.jgordianknot.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.GordianLogicException;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeySpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianAsymKeyType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * EC Encryption Methods.
 */
public class BouncyECEncryptor {
    /**
     * CoFactor must be less than or equal to 20. Boundary is actually 10 < b < 76.
     */
    private static final int MAXCOFACTOR = 20;

    /**
     * The max iterations to try for a point.
     */
    private static final int MAXITERATION = 1 << Byte.SIZE;

    /**
     * The ECCurve.
     */
    private final ECCurve theCurve;

    /**
     * The Random.
     */
    private final SecureRandom theRandom;

    /**
     * is encryption available?
     */
    private final boolean isAvailable;

    /**
     * The KeyPair.
     */
    private AsymmetricCipherKeyPair theKeyPair;

    /**
     * Constructor.
     * @param pRandom the secure random
     * @param pKeySpec the keySpec.
     */
    public BouncyECEncryptor(final SecureRandom pRandom,
                             final GordianAsymKeySpec pKeySpec) {
        /* Store the random */
        theRandom = pRandom;

        /* Create the generator */
        final ECKeyPairGenerator myGenerator = new ECKeyPairGenerator();
        final GordianAsymKeyType myType = pKeySpec.getKeyType();
        final String myCurve = pKeySpec.getElliptic().getCurveName();

        /* Lookup the parameters */
        final X9ECParameters x9 = GordianAsymKeyType.SM2.equals(myType)
                                  ? GMNamedCurves.getByName(myCurve)
                                  : ECNamedCurveTable.getByName(myCurve);
        theCurve = x9.getCurve();

        /* Initialise the generator */
        final ECDomainParameters myDomain = new ECDomainParameters(x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
        final ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, theRandom);
        myGenerator.init(myParams);

        /* Create the keyPair */
        theKeyPair = myGenerator.generateKeyPair();
        isAvailable = x9.getH().compareTo(BigInteger.valueOf(MAXCOFACTOR)) <= 0;
    }

    /**
     * Is encryption available?
     * @return true/false
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * Obtain the length of the block.
     * @return the length of the block.
     */
    private int getBlockLength() {
        return (theCurve.getFieldSize() + Byte.SIZE - 1) / Byte.SIZE;
    }

    /**
     * Obtain the length of the unencrypted block.
     * @return the length of the block.
     */
    private int getDecryptedBlockLength() {
        return getBlockLength() - 2;
    }

    /**
     * Obtain the length of the encrypted block.
     * @return the length of the block.
     */
    private int getEncryptedBlockLength() {
        return (getBlockLength() + 1) << 1;
    }

    /**
     * Obtain the number of blocks required for the length in terms of blocks.
     * @param pLength the length of clear data
     * @return the number of blocks.
     */
    private int getDecryptedLength(final int pLength) {
        return getDecryptedBlockLength() * getNumBlocks(pLength, getEncryptedBlockLength());
    }

    /**
     * Obtain the number of blocks required for the encrypted output.
     * @param pLength the length of clear data
     * @return the number of blocks.
     */
    private int getEncryptedLength(final int pLength) {
        return getEncryptedBlockLength() * getNumBlocks(pLength, getDecryptedBlockLength());
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
     * @throws OceanusException on error
     */
    public byte[] encrypt(final byte[] pData) throws OceanusException {
        /* Create the output buffer */
        int myInLen = pData.length;
        final byte[] myOutput = new byte[getEncryptedLength(pData.length)];

        /* Access block lengths */
        final int myInBlockLength = getDecryptedBlockLength();

        /* Loop encrypting the blocks */
        int myInOff = 0;
        int myOutOff = 0;
        while (myInLen > 0) {
            /* Encrypt to an ECPair */
            final int myLen = myInLen >= myInBlockLength
                              ? myInBlockLength
                              : myInLen;
            final ECPair myPair = encryptToPair(pData, myInOff, myLen);

            /* Convert into the output buffer */
            myOutOff += convertFromECPair(myPair, myOutput, myOutOff);

            /* Move to next block */
            myInOff += myInBlockLength;
            myInLen -= myInBlockLength;
        }

        /* Return the data */
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
     * @throws OceanusException on error
     */
    private ECPair encryptToPair(final byte[] pData,
                                 final int pInOff,
                                 final int pInLen) throws OceanusException {
        /* Convert the data to an ECPoint */
        final ECPoint myPoint = convertToECPoint(pData, pInOff, pInLen);
        final ECElGamalEncryptor myEncryptor = new ECElGamalEncryptor();

        /* Initialise the encryptor */
        final ParametersWithRandom myParms = new ParametersWithRandom(theKeyPair.getPublic(), theRandom);
        myEncryptor.init(myParms);

        /* Encrypt the data */
        return myEncryptor.encrypt(myPoint);
    }

    /**
     * Convert to ECPoint.
     * @param pInBuffer the input buffer
     * @param pInOff the input offset
     * @param pInLen the length of data to process
     * @return the ECPair
     * @throws OceanusException on error
     */
    private ECPoint convertToECPoint(final byte[] pInBuffer,
                                     final int pInOff,
                                     final int pInLen) throws OceanusException {
        /* Check length */
        final int myLen = getBlockLength();
        if (pInLen > myLen - 2
                || pInLen <= 0) {
            throw new GordianLogicException("Invalid input length");
        }
        if (pInBuffer.length - pInOff < pInLen) {
            throw new GordianLogicException("Invalid input buffer");
        }
        final int myStart = myLen - pInLen - 1;

        /* Create the work buffer and copy data in */
        final byte[] myX = new byte[myLen];
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
            myX[myLen - 1]++;
        }

        /* No possible value found */
        throw new GordianDataException("Unable to find point on curve");
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
            final byte[] myCompressed = new byte[pX.length + 1];
            System.arraycopy(pX, 0, myCompressed, 1, pX.length);
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
     * @throws OceanusException on error
     */
    private int convertFromECPair(final ECPair pPair,
                                  final byte[] pOutBuffer,
                                  final int pOutOff) throws OceanusException {
        /* Check length */
        final int myLen = getBlockLength() + 1;
        if (pOutBuffer.length - pOutOff < myLen << 1) {
            throw new GordianLogicException("Output buffer too small");
        }

        /* Access the two encoded parameters  */
        final byte[] myX = pPair.getX().getEncoded(true);
        final byte[] myY = pPair.getY().getEncoded(true);
        if (myX.length != myLen || myY.length != myLen) {
            throw new GordianDataException("Bad encoding");
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
     * @throws OceanusException on error
     */
    public byte[] decrypt(final byte[] pData) throws OceanusException {
        /* Create the output buffer */
        int myInLen = pData.length;
        final byte[] myOutput = new byte[getDecryptedLength(pData.length)];

        /* Access block lengths */
        final int myInBlockLength = getEncryptedBlockLength();

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

        /* Return the data */
        return myOutOff == myOutput.length
               ? myOutput
               : Arrays.copyOf(myOutput, myOutOff);
    }

    /**
     * Decrypt a value.
     * @param pPair the pair to decrypt
     * @param pOutBuffer the output buffer
     * @param pOutOff the output offset
     * @return the length of data decoded
     * @throws OceanusException on error
     */
    private int decryptFromECPair(final ECPair pPair,
                                  final byte[] pOutBuffer,
                                  final int pOutOff) throws OceanusException {
        /* Create and initialise the decryptor */
        final ECElGamalDecryptor myDecryptor = new ECElGamalDecryptor();
        myDecryptor.init(theKeyPair.getPrivate());

        /* Decrypt the pair */
        final ECPoint myPoint = myDecryptor.decrypt(pPair);
        return convertFromECPoint(myPoint, pOutBuffer, pOutOff);
    }

    /**
     * Convert from ECPoint.
     * @param pPoint the ECPoint
     * @param pOutBuffer the output buffer
     * @param pOutOff the output offset
     * @return the length of data decoded
     * @throws OceanusException on error
     */
    private int convertFromECPoint(final ECPoint pPoint,
                                   final byte[] pOutBuffer,
                                   final int pOutOff) throws OceanusException {
        /* Check length */
        final int myLen = getBlockLength();
        if (pOutBuffer.length - pOutOff < myLen - 2) {
            throw new GordianLogicException("Output buffer too small");
        }

        /* Obtain the X co-ordinate */
        final BigInteger myX = pPoint.getAffineXCoord().toBigInteger();
        final byte[] myBuf = myX.toByteArray();

        /* Count the padding */
        final int myEnd = myBuf.length - 1;
        int myStart = -1;

        /* Loop through the data in fixed time */
        for (int myIndex = 0; myIndex != myEnd; myIndex++) {
            /* If the value is non-zero and we have not yet found start */
            if (myBuf[myIndex] != 0
                    && myStart == -1) {
                myStart = myIndex;
            }
        }

        /* Check validity */
        if (myStart == myEnd || myBuf[myStart] != 1) {
            throw new GordianDataException("Invalid data");
        }

        /* Copy the data out */
        myStart++;
        final int myOutLen = myEnd - myStart;
        System.arraycopy(myBuf, myStart, pOutBuffer, pOutOff, myOutLen);
        return myOutLen;
    }

    /**
     * Convert to ECPair.
     * @param pInBuffer the input buffer
     * @param pInOff the input offset
     * @return the ECPair
     * @throws OceanusException on error
     */
    private ECPair convertToECPair(final byte[] pInBuffer,
                                   final int pInOff) throws OceanusException {
        /* Check length */
        final int myLen = getBlockLength() + 1;
        if (pInBuffer.length - pInOff < myLen << 1) {
            throw new GordianLogicException("Invalid input buffer");
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
