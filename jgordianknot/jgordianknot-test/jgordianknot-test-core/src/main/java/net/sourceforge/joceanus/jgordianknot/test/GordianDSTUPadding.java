/*******************************************************************************
 * GordianKnot: Security Suite Copyright 2012,2018 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.test;

import java.security.SecureRandom;
import java.util.Arrays;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.DSTU7624Engine;
import org.bouncycastle.crypto.macs.KGMac;
import org.bouncycastle.crypto.modes.KCCMBlockCipher;
import org.bouncycastle.crypto.modes.KGCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

/**
 * Test for DSTU7624 Padding problems in modes.
 */
public final class GordianDSTUPadding {
    /**
     * BlockSize.
     */
    private static final int BLOCKSIZE = 128;

    /**
     * Create standard 64-byte input for encryption.
     */
    private static final byte[] BYTES_64 = "A123456789B123456789C123456789D123456789E123456789F123456789G123".getBytes();

    /**
     * Create longer 65-byte input for encryption.
     */
    private static final byte[] BYTES_67 = "A123456789B123456789C123456789D123456789E123456789F123456789G123456".getBytes();

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        testDSTU7624CCM(BLOCKSIZE, BYTES_64, true, false);
        testDSTU7624CCM(BLOCKSIZE, BYTES_64, false, true);
        testDSTU7624CCM(BLOCKSIZE, BYTES_64, true, true);
        testDSTU7624CCM(BLOCKSIZE, BYTES_67, true, false);
        testDSTU7624CCM(BLOCKSIZE, BYTES_67, false, true);
        testDSTU7624CCM(BLOCKSIZE, BYTES_67, true, true);

        testDSTU7624GCM(BLOCKSIZE, BYTES_64, true, false);
        testDSTU7624GCM(BLOCKSIZE, BYTES_64, false, true);
        testDSTU7624GCM(BLOCKSIZE, BYTES_64, true, true);
        testDSTU7624GCM(BLOCKSIZE, BYTES_67, true, false);
        testDSTU7624GCM(BLOCKSIZE, BYTES_67, false, true);
        testDSTU7624GCM(BLOCKSIZE, BYTES_67, true, true);

        testKGMac(BLOCKSIZE, BYTES_64);
        testKGMac(BLOCKSIZE, BYTES_67);
    }

    /**
     * Private constructor.
     */
    private GordianDSTUPadding() {
    }

    /**
     * Test lightweight Kalyna KCCM Mode.
     * @param pBlockSize the blockSize
     * @param pData the data to encrypt
     * @param useAAD pass data as AAD to cipher
     * @param useData pass data as data to cipher
     */
    private static void testDSTU7624CCM(final int pBlockSize,
                                        final byte[] pData,
                                        final boolean useAAD,
                                        final boolean useData) {
        /* Create the generator and generate a key */
        final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        final SecureRandom myRandom = new SecureRandom();
        final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, pBlockSize);
        myGenerator.init(myParams);
        final byte[] myKey = myGenerator.generateKey();

        /* Create an base engine */
        final DSTU7624Engine myEngine = new DSTU7624Engine(pBlockSize);

        /* Create IV */
        final byte[] myIV = new byte[myEngine.getBlockSize()];
        myRandom.nextBytes(myIV);

        /* Create a parameterSpec */
        final KeyParameter myParms = new KeyParameter(myKey);
        final ParametersWithIV myIVParms = new ParametersWithIV(myParms, myIV);

        /* Create cipher */
        final KCCMBlockCipher myCipher = new KCCMBlockCipher(myEngine);

        /* Catch Exceptions */
        try {
            /* Try with standard data only */
            myCipher.init(true, myIVParms);
            if (useAAD) {
                myCipher.processAADBytes(pData, 0, pData.length);
            }
            byte[] myEncrypted = new byte[myCipher.getUpdateOutputSize(useData
                                                                               ? pData.length
                                                                               : 0)];
            int myLen = useData
                                ? myCipher.processBytes(pData, 0, pData.length, myEncrypted, 0)
                                : 0;
            int myXtra = myCipher.getOutputSize(0);
            myEncrypted = Arrays.copyOf(myEncrypted, myLen + myXtra);
            myLen += myCipher.doFinal(myEncrypted, myLen);
            if (myLen != myEncrypted.length) {
                myEncrypted = Arrays.copyOf(myEncrypted, myLen);
            }

            myCipher.init(false, myIVParms);
            if (useAAD) {
                myCipher.processAADBytes(pData, 0, pData.length);
            }
            byte[] myClearText = new byte[myCipher.getUpdateOutputSize(myEncrypted.length)];
            myLen = myCipher.processBytes(myEncrypted, 0, myEncrypted.length, myClearText, 0);
            myXtra = myCipher.getOutputSize(0);
            myClearText = Arrays.copyOf(myClearText, myLen + myXtra);
            myLen += myCipher.doFinal(myClearText, myLen);
            if (myLen != myClearText.length) {
                myClearText = Arrays.copyOf(myClearText, myLen);
            }

            final boolean bSuccess = useData
                                             ? Arrays.equals(pData, myClearText)
                                             : myLen == 0;
            if (bSuccess) {
                System.out.println("DSTU7624-" + pBlockSize + " Bug fixed");
            } else {
                System.out.println("DSTU7624-" + pBlockSize + " Bug still exists");
            }

            /* Catch general exceptions */
        } catch (IllegalArgumentException | IllegalStateException | InvalidCipherTextException e) {
            System.out.println("DSTU7624-" + pBlockSize + " Bug still exists");
        }
    }

    /**
     * Test lightweight Kalyna KGCM Mode.
     * @param pBlockSize the blockSize
     * @param pData the data to encrypt
     * @param useAAD pass data as AAD to cipher
     * @param useData pass data as data to cipher
     */
    private static void testDSTU7624GCM(final int pBlockSize,
                                        final byte[] pData,
                                        final boolean useAAD,
                                        final boolean useData) {
        /* Create the generator and generate a key */
        final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        final SecureRandom myRandom = new SecureRandom();
        final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, pBlockSize);
        myGenerator.init(myParams);
        final byte[] myKey = myGenerator.generateKey();

        /* Create an base engine */
        final DSTU7624Engine myEngine = new DSTU7624Engine(pBlockSize);

        /* Create IV */
        final byte[] myIV = new byte[myEngine.getBlockSize()];
        myRandom.nextBytes(myIV);

        /* Create a parameterSpec */
        final KeyParameter myParms = new KeyParameter(myKey);
        final ParametersWithIV myIVParms = new ParametersWithIV(myParms, myIV);

        /* Create cipher */
        final KGCMBlockCipher myCipher = new KGCMBlockCipher(myEngine);

        /* Catch Exceptions */
        try {
            /* Try with standard data only */
            myCipher.init(true, myIVParms);
            if (useAAD) {
                myCipher.processAADBytes(pData, 0, pData.length);
            }
            byte[] myEncrypted = new byte[myCipher.getUpdateOutputSize(useData
                                                                               ? pData.length
                                                                               : 0)];
            int myLen = useData
                                ? myCipher.processBytes(pData, 0, pData.length, myEncrypted, 0)
                                : 0;
            int myXtra = myCipher.getOutputSize(0);
            myEncrypted = Arrays.copyOf(myEncrypted, myLen + myXtra);
            myLen += myCipher.doFinal(myEncrypted, myLen);
            if (myLen != myEncrypted.length) {
                myEncrypted = Arrays.copyOf(myEncrypted, myLen);
            }

            myCipher.init(false, myIVParms);
            if (useAAD) {
                myCipher.processAADBytes(pData, 0, pData.length);
            }
            byte[] myClearText = new byte[myCipher.getUpdateOutputSize(myEncrypted.length)];
            myLen = myCipher.processBytes(myEncrypted, 0, myEncrypted.length, myClearText, 0);
            myXtra = myCipher.getOutputSize(0);
            myClearText = Arrays.copyOf(myClearText, myLen + myXtra);
            myLen += myCipher.doFinal(myClearText, myLen);
            if (myLen != myClearText.length) {
                myClearText = Arrays.copyOf(myClearText, myLen);
            }

            final boolean bSuccess = useData
                                             ? Arrays.equals(pData, myClearText)
                                             : myLen == 0;
            if (bSuccess) {
                System.out.println("DSTU7624-" + pBlockSize + " Bug fixed");
            } else {
                System.out.println("DSTU7624-" + pBlockSize + " Bug still exists");
            }

            /* Catch general exceptions */
        } catch (InvalidCipherTextException e) {
            System.out.println("DSTU7624-" + pBlockSize + " Bug still exists");
        }
    }

    /**
     * Test KGMac.
     * @param pBlockSize the blockSize
     * @param pData the data to digest
     */
    private static void testKGMac(final int pBlockSize,
                                  final byte[] pData) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
            final SecureRandom myRandom = new SecureRandom();
            final KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
            myGenerator.init(myParams);
            final byte[] myKey = myGenerator.generateKey();

            /* Create an base engine */
            final DSTU7624Engine myEngine = new DSTU7624Engine(pBlockSize);

            /* Create IV */
            final byte[] myIV = new byte[myEngine.getBlockSize()];
            myRandom.nextBytes(myIV);

            /* Create a parameterSpec */
            final KeyParameter myParms = new KeyParameter(myKey);
            final ParametersWithIV myIVParms = new ParametersWithIV(myParms, myIV);

            /* Create cipher */
            final KGCMBlockCipher myCipher = new KGCMBlockCipher(myEngine);

            /* Create a KGMac */
            final KGMac myMac = new KGMac(myCipher);
            myMac.init(myIVParms);

            /* Update the Mac */
            myMac.update(pData, 0, pData.length);

            /* Access output */
            final byte[] myResult = new byte[myMac.getMacSize()];
            myMac.doFinal(myResult, 0);

            System.out.println("DSTU7624 Padding Bug fixed");

        } catch (DataLengthException e) {
            System.out.println("DSTU7624 Padding Bug still exists");
        }
    }
}
