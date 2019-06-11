package net.sourceforge.joceanus.jgordianknot.junit.patches;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Test for Jca OCB support bugs.
 */
public class OCB {
    /**
     * Note the provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        TestOCBInit("DSTU7624", "DSTU7624-128");
        TestOCBInit("GOST3412-2015", "GOST3412-2015");
    }

    /**
     * Test Jca OCB Init.
     * @param pKeyGenAlgorithm the algorithm
     * @param pCipherAlgorithm the algorithm
     */
    private static void TestOCBInit(final String pKeyGenAlgorithm,
                                    final String pCipherAlgorithm) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            KeyGenerator myGenerator = KeyGenerator.getInstance(pKeyGenAlgorithm, BCPROV);

            /* Initialise the generator */
            SecureRandom myRandom = new SecureRandom();
            myGenerator.init(256, myRandom);
            SecretKey myKey = myGenerator.generateKey();

            /* Create IV */
            byte[] myIV = new byte[16];
            myRandom.nextBytes(myIV);

            /* Create a OCB Cipher */
            Cipher myCipher = Cipher.getInstance(pCipherAlgorithm + "/OCB/NoPadding", BCPROV);
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, new IvParameterSpec(myIV));

            System.out.println(pCipherAlgorithm + " OCB JCA init Bug fixed");

            /* Catch general exceptions */
        } catch (NoSuchPaddingException
                | InvalidKeyException
                | InvalidAlgorithmParameterException e) {
            System.out.println("Failed to create generator");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(pCipherAlgorithm + " OCB JCA init Bug still exists");
        }
    }
}

