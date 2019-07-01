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
public class G3413CTR {
    /**
     * Note the provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        TestG3413CTRInit("GOST3412-2015", 8);
        TestG3413CTRInit("GOST3412-2015", 16);
    }

    /**
     * Test G3413CTR Init.
     * @param pAlgorithm the algorithm
     */
    private static void TestG3413CTRInit(final String pAlgorithm,
                                         final int pIVLen) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            KeyGenerator myGenerator = KeyGenerator.getInstance(pAlgorithm, BCPROV);

            /* Initialise the generator */
            SecureRandom myRandom = new SecureRandom();
            myGenerator.init(256, myRandom);
            SecretKey myKey = myGenerator.generateKey();

            /* Create IV */
            byte[] myIV = new byte[pIVLen];
            myRandom.nextBytes(myIV);

            /* Create a G3413CTR Cipher */
            Cipher myCipher = Cipher.getInstance(pAlgorithm + "/CTR/NoPadding", BCPROV);
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, new IvParameterSpec(myIV));

            System.out.println(pAlgorithm + " G3413CTR JCA init Bug fixed");

            /* Catch general exceptions */
        } catch (NoSuchPaddingException
                | NoSuchAlgorithmException e) {
            System.out.println("General exception");
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            System.out.println(pAlgorithm + " G3413CTR IV " + pIVLen + " rejected by JCA Wrapper");
        } catch (InvalidKeyException e) {
            System.out.println(pAlgorithm + " G3413CTR IV " + pIVLen + " rejected by CipherMode");
        }
    }
}
