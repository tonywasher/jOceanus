package net.sourceforge.joceanus.gordianknot.junit.patches;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.DefaultBufferedBlockCipher;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.GOST28147Engine;
import org.bouncycastle.crypto.modes.GCFBBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import java.security.SecureRandom;
import java.util.Arrays;

public class GCFBLargeData {
    /**
     * Data Length. (Anything above 1024).
     */
    private static final int DATALEN = 1025;

    /**
     * SecureRandom.
     */
    private static SecureRandom RANDOM = new SecureRandom();

    /**
     * Main.
     */
    public static void main(final String[] args) {
        try {
            checkCipher(new GCFBBlockCipher(new GOST28147Engine()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check Cipher.
     * @param pCipher the cipher
     */
    private static void checkCipher(final BlockCipher pCipher) throws Exception {
        /* Create the data */
        final byte[] myData = new byte[DATALEN];
        RANDOM.nextBytes(myData);

        /* Create the Key parameters */
        final CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        final KeyGenerationParameters myGenParams = new KeyGenerationParameters(RANDOM, 256);
        myGenerator.init(myGenParams);
        final byte[] myKey = myGenerator.generateKey();
        final KeyParameter myKeyParams = new KeyParameter(myKey);

        /* Create the IV */
        final byte[] myIV = new byte[16];
        RANDOM.nextBytes(myIV);

        /* Create the initParams */
        final ParametersWithIV myParams = new ParametersWithIV(myKeyParams, myIV);

        /* Wrap Block Cipher with buffered BlockCipher */
        final BufferedBlockCipher myCipher = new DefaultBufferedBlockCipher(pCipher);

        /* Initialise the cipher for encryption */
        myCipher.init(true, myParams);

        /* Encipher the text */
        final byte[] myOutput = new byte[myCipher.getOutputSize(DATALEN)];
        int myOutLen = myCipher.processBytes(myData, 0, DATALEN, myOutput, 0);
        myCipher.doFinal(myOutput, myOutLen);

        /* Re-Encipher the text */
        final byte[] myOutput2 = new byte[myCipher.getOutputSize(DATALEN)];
        myOutLen = myCipher.processBytes(myData, 0, DATALEN, myOutput2, 0);
        myCipher.doFinal(myOutput2, myOutLen);

        /* Check that the cipherTexts are identical */
        if (!Arrays.equals(myOutput, myOutput2)) {
            int i = 0;
        }
    }
}
