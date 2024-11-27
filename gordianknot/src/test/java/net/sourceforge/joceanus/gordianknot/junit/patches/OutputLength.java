package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.AsconEngine;
import org.bouncycastle.crypto.engines.AsconEngine.AsconParameters;
import org.bouncycastle.crypto.engines.SparkleEngine;
import org.bouncycastle.crypto.engines.SparkleEngine.SparkleParameters;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.patch.engines.GordianElephantEngine;
import org.bouncycastle.crypto.patch.engines.GordianElephantEngine.ElephantParameters;
import org.bouncycastle.crypto.patch.engines.GordianISAPEngine;
import org.bouncycastle.crypto.patch.engines.GordianISAPEngine.IsapType;
import org.bouncycastle.crypto.patch.engines.GordianPhotonBeetleEngine;
import org.bouncycastle.crypto.patch.engines.GordianPhotonBeetleEngine.PhotonBeetleParameters;
import org.bouncycastle.crypto.patch.engines.GordianXoodyakEngine;

import java.security.SecureRandom;
import java.util.Arrays;

public class OutputLength {
    /**
     * Data length.
     */
    private static final int DATALEN = 1025;

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
        checkCipher(new AsconEngine(AsconParameters.ascon128a), 16);
        checkCipher(new GordianPhotonBeetleEngine(PhotonBeetleParameters.pb128), 16);
        checkCipher(new SparkleEngine(SparkleParameters.SCHWAEMM128_128), 16);
        checkCipher(new GordianXoodyakEngine(), 16);
        checkCipher(new GordianISAPEngine(IsapType.ISAP_A_128), 16);
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
            for (int i = 0; i < DATALEN; i++) {
                myOutLen += pCipher.processBytes(myData, i, 1, myEncrypted, myOutLen);
            }
            int myRemaining = pCipher.getOutputSize(0);

            /* Check that the predicted data output length is reasonable */
            if (myRemaining + myOutLen < myFinalOutLen) {
                System.out.println("Bad outputLength on encryption for " + pCipher.getAlgorithmName());
            }
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
