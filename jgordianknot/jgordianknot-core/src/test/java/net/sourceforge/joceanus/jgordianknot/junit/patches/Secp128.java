package net.sourceforge.joceanus.jgordianknot.junit.patches;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x9.ECNamedCurveTable;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.ec.ECElGamalDecryptor;
import org.bouncycastle.crypto.ec.ECElGamalEncryptor;
import org.bouncycastle.crypto.ec.ECPair;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.crypto.params.ECNamedDomainParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class Secp128 {
    /**
     * Max attempts to find a point on the curve.
     */
    private static final int MAXITERATIONS = 1 << Byte.SIZE;

    /**
     * The ECCurve.
     */
    private final ECCurve theCurve;

    /**
     * The Encryptor.
     */
    private final ECElGamalEncryptor theEncryptor;

    /**
     * The Decryptor.
     */
    private final ECElGamalDecryptor theDecryptor;

    /**
     * Main entry.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        try {
            /* Run tests */
            new Secp128(false);
            new Secp128(true);
        } catch (InvalidCipherTextException e) {

        }
    }

    /**
     * Constructor.
     * @params useCustomCurve
     */
    private Secp128(final boolean useCustomCurve) throws InvalidCipherTextException {
        /* Create encryptor/decryptor */
        theEncryptor = new ECElGamalEncryptor();
        theDecryptor = new ECElGamalDecryptor();

        /* Create the generator */
        ECKeyPairGenerator myGenerator = new ECKeyPairGenerator();
        SecureRandom myRandom = new SecureRandom();
        String myCurve = "secp128r1";

        /* Lookup the parameters */
        final X9ECParameters x9 = useCustomCurve
                                  ? ECUtil.getNamedCurveByName(myCurve)
                                  : ECNamedCurveTable.getByName(myCurve);

        /* Initialise the generator */
        final ASN1ObjectIdentifier myOid = ECUtil.getNamedCurveOid(myCurve);
        ECNamedDomainParameters myDomain = new ECNamedDomainParameters(myOid, x9.getCurve(), x9.getG(), x9.getN(), x9.getH(), x9.getSeed());
        ECKeyGenerationParameters myParams = new ECKeyGenerationParameters(myDomain, myRandom);
        myGenerator.init(myParams);

        /* Create the key Pair */
        AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();

        /* Initialise for encryption/decryption */
        final ParametersWithRandom myParms = new ParametersWithRandom(myPair.getPublic(), myRandom);
        theEncryptor.init(myParms);
        theDecryptor.init(myPair.getPrivate());

        /* Store the curve */
        theCurve = myDomain.getCurve();

        /* Build buffer to check */
        final byte[] myData = new byte[] { (byte) 0xFF, (byte) 0xFF };

        /* Create the point */
        final ECPoint myPoint = convertToECPoint(myData, 0, myData.length);

        /* Encrypt and decrypt the point */
        final ECPair myEncryptedPair =  theEncryptor.encrypt(myPoint);
        final ECPoint myResolvedPoint = theDecryptor.decrypt(myEncryptedPair);

        /* Check that the resolved point is correct */
        if (!myPoint.equals(myResolvedPoint)) {
            System.out.println("Failed");
        }
    }

    /**
     * Obtain the length of the block.
     * @return the length of the block.
     */
    private int getBlockLength() {
        return (theCurve.getFieldSize() + Byte.SIZE - 1) / Byte.SIZE;
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
        /* Check length */
        final int myLen = getBlockLength();
        if (pInLen > myLen - 2
                || pInLen <= 0) {
            throw new IllegalArgumentException("Invalid input length");
        }
        if (pInBuffer.length - pInOff < pInLen) {
            throw new IllegalArgumentException("Invalid input buffer");
        }

        /* Create the work buffer and copy data in */
        final byte[] myX = new byte[myLen];

        /* Calculate the start position and place data and padding */
        final int myStart = myLen - pInLen - 1;
        System.arraycopy(pInBuffer, pInOff, myX, myStart, pInLen);
        myX[myStart - 1] = 1;

        /* Loop to obtain point on curve */
        for (int i = 0; i < MAXITERATIONS; i++) {
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
        int myEnd = myBuf.length - 1;

        /* Loop through the data in fixed time */
        for (int myIndex = 0; myIndex < myEnd; myIndex++) {
            /* If the value is non-zero and we have not yet found start */
            if (myBuf[myIndex] != 0
                    && myStart == -1) {
                myStart = myIndex;
            }
        }

        /* Check validity */
        if (myStart == -1 || myBuf[myStart] != 1) {
            throw new InvalidCipherTextException("Invalid data");
        }

        /* Bump past the padding */
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
}
