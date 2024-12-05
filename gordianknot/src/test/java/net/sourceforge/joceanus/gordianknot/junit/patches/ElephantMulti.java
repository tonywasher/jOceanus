package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.patch.engines.GordianElephantEngine;
import org.bouncycastle.crypto.patch.engines.GordianElephantEngine.ElephantParameters;

import java.security.SecureRandom;
import java.util.Arrays;

public class ElephantMulti {
    /**
     * Data length.
     */
    private static final int DATALEN = 1025;

    /**
     * Partial Data length.
     * Must be greater than or equal to internal buffer length (20, 22 or 25) to exhibit problem.
     */
    private static final int PARTLEN = 41;

    /**
     * AEAD length.
     */
    private static final int AEADLEN = 10;

    /**
     * Main.
     * @params pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        checkCipher(new GordianElephantEngine(ElephantParameters.elephant160), 12);
    }

    /**
     * Check cipher.
     * @param pCipher the cipher
     */
    private static void checkCipher(final AEADCipher pCipher,
                                    final int pNonceLen) {
        try {
            /* Obtain some random data */
            final byte[] myData = new byte[DATALEN];
            final SecureRandom myRandom = new SecureRandom();
            myRandom.nextBytes(myData);

            /* Obtain some random AEAD */
            final byte[] myAEAD = new byte[AEADLEN];
            myRandom.nextBytes(myAEAD);

            /* Create the Key parameters */
            final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
            final KeyGenerationParameters myGenParams = new KeyGenerationParameters(myRandom, 128);
            myGenerator.init(myGenParams);
            final byte[] myKey = myGenerator.generateKey();
            final KeyParameter myKeyParams = new KeyParameter(myKey);

            /* Create the nonce */
            final byte[] myNonce = new byte[pNonceLen];
            myRandom.nextBytes(myNonce);
            final ParametersWithIV myParams = new ParametersWithIV(myKeyParams, myNonce);

            /* Initialise the cipher for encryption */
            pCipher.init(true, myParams);
            final int myFinalOutLen = pCipher.getOutputSize(DATALEN);
            final byte[] myEncrypted = new byte[myFinalOutLen];
            pCipher.processAADBytes(myAEAD, 0, AEADLEN);
            int myOutLen = 0;
            myOutLen += pCipher.processBytes(myData, 0, PARTLEN, myEncrypted, myOutLen);
            int myRemaining = pCipher.getOutputSize(DATALEN - PARTLEN);
            myOutLen += pCipher.processBytes(myData, PARTLEN, DATALEN - PARTLEN, myEncrypted, myOutLen);
            int myProcessed = pCipher.doFinal(myEncrypted, myOutLen);

            /* Check that the total data output is as predicted */
            if (myOutLen + myProcessed != myFinalOutLen) {
                System.out.println("Bad total on encryption for " + pCipher.getAlgorithmName());
            }

            /* Initialise the cipher for decryption */
            pCipher.init(false, myParams);
            final int myFinalClearLen = pCipher.getOutputSize(myFinalOutLen);
            final byte[] myDecrypted = new byte[myFinalClearLen];
            pCipher.processAADBytes(myAEAD, 0, AEADLEN);
            int myClearLen = pCipher.processBytes(myEncrypted, 0, myEncrypted.length, myDecrypted, 0);
            myRemaining = pCipher.getOutputSize(0);

            /* Check that the predicted data output length is reasonable */
            if (myRemaining + myClearLen < myFinalClearLen) {
                System.out.println("Bad outputLength on decryption for " + pCipher.getAlgorithmName());
            }
            myProcessed = pCipher.doFinal(myDecrypted, myClearLen);

            /* Check that the total data output is as predicted */
            if (myClearLen + myProcessed != myFinalClearLen) {
                System.out.println("Bad total on decryption for " + pCipher.getAlgorithmName());
            }
            final byte[] myResult = Arrays.copyOf(myDecrypted, DATALEN);

            /* Check that we have the same result */
            if (!Arrays.equals(myData, myResult)) {
                System.out.println("Cipher " + pCipher.getAlgorithmName() + " failed");
            }
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }
}
