package net.sourceforge.joceanus.jgordianknot;

import java.security.SecureRandom;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.macs.DSTU7564Mac;
import org.bouncycastle.crypto.macs.DSTU7624Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/**
 * Test for Jca OCB support bugs.
 */
public class GordianDSTUMacTest {
    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        TestDSTU7624Padding();
        TestDSTU7624Reuse();
        TestDSTU7564Reuse();
    }

    /**
     * Test DSTU7624 Mac Padding.
     */
    private static void TestDSTU7624Padding() {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            CipherKeyGenerator myGenerator = new CipherKeyGenerator();
            SecureRandom myRandom = new SecureRandom();
            KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
            myGenerator.init(myParams);
            byte[] myKey = myGenerator.generateKey();

            /* Create a KalynaMac */
            DSTU7624Mac myMac = new DSTU7624Mac(128, 128);
            KeyParameter myParms = new KeyParameter(myKey);
            myMac.init(myParms);

            /* Create short 40-byte input for digest */
            byte[] myInput = "A123456789B123456789C123456789D123456789".getBytes();
            myMac.update(myInput, 0, myInput.length);

            /* Access output */
            byte[] myResult = new byte[myMac.getMacSize()];
            myMac.doFinal(myResult, 0);

            System.out.println("DSTU7624 Padding Bug fixed");

        } catch (DataLengthException e) {
            System.out.println("DSTU7624 Padding Bug still exists");
        }
    }

    /**
     * Test DSTU7624 Mac Reuse.
     */
    private static void TestDSTU7624Reuse() {
        /* Create the generator and generate a key */
        CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        SecureRandom myRandom = new SecureRandom();
        KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
        myGenerator.init(myParams);
        byte[] myKey = myGenerator.generateKey();

        /* Create a KalynaMac */
        DSTU7624Mac myMac = new DSTU7624Mac(128, 128);
        KeyParameter myParms = new KeyParameter(myKey);
        myMac.init(myParms);

        /* Create 32-byte input for digest */
        byte[] myInput = "A123456789B123456789C123456789D1".getBytes();
        myMac.update(myInput, 0, myInput.length);

        /* Access output */
        byte[] myResult = new byte[myMac.getMacSize()];
        myMac.doFinal(myResult, 0);

        /* Access output */
        byte[] myRepeat = new byte[myMac.getMacSize()];
        myMac.update(myInput, 0, myInput.length);
        myMac.doFinal(myRepeat, 0);

        if (Arrays.areEqual(myResult, myRepeat)) {
            System.out.println("DSTU7624 Reuse Bug fixed");
        } else {
            System.out.println("DSTU7624 Reuse Bug still exists");
        }
    }

    /**
     * Test DSTU7564 Mac Reuse.
     */
    private static void TestDSTU7564Reuse() {
        /* Create the generator and generate a key */
        CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        SecureRandom myRandom = new SecureRandom();
        KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 256);
        myGenerator.init(myParams);
        byte[] myKey = myGenerator.generateKey();

        /* Create a KalynaMac */
        DSTU7564Mac myMac = new DSTU7564Mac(256);
        KeyParameter myParms = new KeyParameter(myKey);
        myMac.init(myParms);

        /* Create 40-byte input for digest */
        byte[] myInput = "A123456789B123456789C123456789D123456789".getBytes();
        myMac.update(myInput, 0, myInput.length);

        /* Access output */
        byte[] myResult = new byte[myMac.getMacSize()];
        myMac.doFinal(myResult, 0);

        /* Access output */
        byte[] myRepeat = new byte[myMac.getMacSize()];
        myMac.update(myInput, 0, myInput.length);
        myMac.doFinal(myRepeat, 0);

        if (Arrays.areEqual(myResult, myRepeat)) {
            System.out.println("DSTU7624 Reuse Bug fixed");
        } else {
            System.out.println("DSTU7624 Reuse Bug still exists");
        }
    }
}
