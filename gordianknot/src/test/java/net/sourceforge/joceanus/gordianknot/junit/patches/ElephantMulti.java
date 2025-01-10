package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.modes.AEADCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.patch.engines.ElephantEngine;
import org.bouncycastle.crypto.patch.engines.ElephantEngine.ElephantParameters;
import org.bouncycastle.crypto.patch.engines.ISAPEngine;
import org.bouncycastle.crypto.patch.engines.ISAPEngine.IsapType;
import org.bouncycastle.crypto.patch.engines.PhotonBeetleEngine;
import org.bouncycastle.crypto.patch.engines.PhotonBeetleEngine.PhotonBeetleParameters;
import org.bouncycastle.crypto.patch.engines.XoodyakEngine;

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
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        //checkCipher(new GordianElephantEngine(ElephantParameters.elephant160), 12);
        checkCipher(new ElephantEngine(ElephantParameters.elephant160), 12);
        checkCipher(new ElephantEngine(ElephantParameters.elephant176), 12);
        checkCipher(new ElephantEngine(ElephantParameters.elephant200), 12);
        checkCipher(new ISAPEngine(IsapType.ISAP_A_128), 16);
        checkCipher(new PhotonBeetleEngine(PhotonBeetleParameters.pb128), 16);
        checkCipher(new XoodyakEngine(), 16);
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
            final int myExpectedOutLen = pCipher.getOutputSize(DATALEN);
            final byte[] myEncrypted = new byte[myExpectedOutLen];
            pCipher.processAADBytes(myAEAD, 0, AEADLEN);

            /* Loop processing partial data */
            int myOutLen = 0;
            for (int myPos = 0; myPos < DATALEN; myPos += PARTLEN) {
                final int myLen = Math.min(PARTLEN, DATALEN - myPos);
                myOutLen += pCipher.processBytes(myData, myPos, myLen, myEncrypted, myOutLen);
            }

            /* Finish the encryption */
            myOutLen += pCipher.doFinal(myEncrypted, myOutLen);

            /* Initialise the cipher for decryption */
            pCipher.init(false, myParams);
            final int myExpectedClearLen = pCipher.getOutputSize(myOutLen);
            final byte[] myDecrypted = new byte[myExpectedClearLen];
            pCipher.processAADBytes(myAEAD, 0, AEADLEN);
            int myClearLen = 0;
            for (int myPos = 0; myPos < myOutLen; myPos += PARTLEN) {
                final int myLen = Math.min(PARTLEN, myOutLen - myPos);
                myClearLen += pCipher.processBytes(myEncrypted, myPos, myLen, myDecrypted, myClearLen);
            }
            myClearLen += pCipher.doFinal(myDecrypted, myClearLen);
            final byte[] myResult = Arrays.copyOf(myDecrypted, myClearLen);

            /* Check that we have the same result */
            if (!Arrays.equals(myData, myResult)) {
                System.out.println("Cipher " + pCipher.getAlgorithmName() + " failed");
            }
        } catch (InvalidCipherTextException e) {
            throw new RuntimeException(e);
        }
    }
}
