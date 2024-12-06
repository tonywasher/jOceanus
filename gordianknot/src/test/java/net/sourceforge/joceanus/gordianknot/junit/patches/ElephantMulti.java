package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.ElephantEngine;
import org.bouncycastle.crypto.engines.ElephantEngine.ElephantParameters;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.security.SecureRandom;

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
        //checkCipher(new GordianElephantEngine(ElephantParameters.elephant160), 12);
        checkCipher(new ElephantEngine(ElephantParameters.elephant160), 12, 20);
        checkCipher(new ElephantEngine(ElephantParameters.elephant176), 12, 22);
        checkCipher(new ElephantEngine(ElephantParameters.elephant200), 12, 25);
    }

    /**
     * Check cipher.
     * @param pCipher the cipher
     */
    private static void checkCipher(final AEADCipher pCipher,
                                    final int pNonceLen,
                                    final int pBufferLen) {
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
            final int myExpectedOutLen = pCipher.getOutputSize(DATALEN);
            final byte[] myEncrypted = new byte[myExpectedOutLen];
            pCipher.processAADBytes(myAEAD, 0, AEADLEN);

            /* Process some initial data */
            int myOutLen = pCipher.processBytes(myData, 0, PARTLEN, myEncrypted, 0);

            /* Note that myOutLen is incorrect so calculate what it should have been */
            myOutLen = pBufferLen * (PARTLEN / pBufferLen) ;

            /* FAILS on this call */
            myOutLen += pCipher.processBytes(myData, PARTLEN, DATALEN - PARTLEN, myEncrypted, myOutLen);

            /* If it succeeded myOutLen is again incorrect, so recalculate it  */
            myOutLen = pBufferLen * (DATALEN / pBufferLen);

            /* Finish the encryption */
            pCipher.doFinal(myEncrypted, myOutLen);

        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }
}
