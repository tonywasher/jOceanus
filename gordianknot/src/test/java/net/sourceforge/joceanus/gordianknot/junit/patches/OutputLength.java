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
import org.bouncycastle.crypto.patch.engines.ElephantXEngine;
import org.bouncycastle.crypto.patch.engines.ElephantXEngine.ElephantParameters;
import org.bouncycastle.crypto.patch.engines.ISAPXEngine;
import org.bouncycastle.crypto.patch.engines.ISAPXEngine.IsapType;
import org.bouncycastle.crypto.patch.engines.PhotonXEngine;
import org.bouncycastle.crypto.patch.engines.PhotonXEngine.PhotonBeetleParameters;
import org.bouncycastle.crypto.patch.engines.XoodyakXEngine;

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
        checkCipher(new ElephantXEngine(ElephantParameters.elephant160), 12);
        checkCipher(new AsconEngine(AsconParameters.ascon128a), 16);
        checkCipher(new PhotonXEngine(PhotonBeetleParameters.pb128), 16);
        checkCipher(new SparkleEngine(SparkleParameters.SCHWAEMM128_128), 16);
        checkCipher(new XoodyakXEngine(), 16);
        checkCipher(new ISAPXEngine(IsapType.ISAP_A_128), 16);
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
            final int myMaxOutLen = pCipher.getOutputSize(DATALEN);
            final byte[] myEncrypted = new byte[myMaxOutLen];
            //pCipher.processAADBytes(myAEAD, 0, AEADLEN);
            int myOutLen = pCipher.processBytes(myData, 0, DATALEN, myEncrypted, 0);
            int myRemaining = pCipher.getOutputSize(0);
            if (myRemaining + myOutLen < myMaxOutLen) {
                System.out.println("Bad outputLength on encryption for " + pCipher.getAlgorithmName());
            }
            int myProcessed = pCipher.doFinal(myEncrypted, myOutLen);
            if (myOutLen + myProcessed != myMaxOutLen) {
                System.out.println("Bad total on encryption for " + pCipher.getAlgorithmName());
            }

            /* Note that myOutLen is too large by DATALEN  */

            /* Initialise the cipher for decryption */
            pCipher.init(false, myParams);
            final int myMaxClearLen = pCipher.getOutputSize(myMaxOutLen);
            final byte[] myDecrypted = new byte[myMaxClearLen];
            //pCipher.processAADBytes(myAEAD, 0, AEADLEN);
            int myClearLen = pCipher.processBytes(myEncrypted, 0, myEncrypted.length, myDecrypted, 0);
            myRemaining = pCipher.getOutputSize(0);
            if (myRemaining + myClearLen < myMaxClearLen) {
                System.out.println("Bad outputLength on decryption for " + pCipher.getAlgorithmName());
            }
            myProcessed = pCipher.doFinal(myDecrypted, myClearLen);
            if (myClearLen + myProcessed != myMaxClearLen) {
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
