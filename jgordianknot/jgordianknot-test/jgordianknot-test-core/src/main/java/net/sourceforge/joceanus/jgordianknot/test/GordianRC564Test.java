package net.sourceforge.joceanus.jgordianknot.test;

import org.bouncycastle.crypto.CipherKeyGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.engines.RC564Engine;
import org.bouncycastle.crypto.modes.AEADBlockCipher;
import org.bouncycastle.crypto.modes.CCMBlockCipher;
import org.bouncycastle.crypto.modes.EAXBlockCipher;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.modes.OCBBlockCipher;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.RC5Parameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.RC5ParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.SecureRandom;

/**
 * Test for RC5-64 JCE bugs.
 */
public class GordianRC564Test {
    /**
     * RC5-64 algorithm.
     */
    private static final String RC564_ALGO = "RC5-64";

    /**
     * Note the provider.
     */
    private static final Provider BCPROV = new BouncyCastleProvider();

    /**
     * Run Tests.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        TestRC564Init("EAX");
        TestRC564Init("CCM");
        TestRC564Init("GCM");
        TestRC564Init();
    }

    /**
     * Test Jca RC5-64 Init.
     * @param pMode the mode
     */
    private static void TestRC564Init(final String pMode) {
        /* Catch Exceptions */
        try {
            /* Create the generator and generate a key */
            KeyGenerator myGenerator = KeyGenerator.getInstance(RC564_ALGO, BCPROV);

            /* Initialise the generator */
            SecureRandom myRandom = new SecureRandom();
            myGenerator.init(128, myRandom);
            SecretKey myKey = myGenerator.generateKey();

            /* Create a CBC Cipher */
            Cipher myCipher = Cipher.getInstance(RC564_ALGO + "/" + pMode + "/NoPadding", BCPROV);

            /* Create IV */
            byte[] myIV = new byte[16];
            myRandom.nextBytes(myIV);

            /* Create a parameterSpec */
            RC5ParameterSpec mySpec = new RC5ParameterSpec(1, 12, 64, myIV);
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, mySpec);

            System.out.println("RC5-64 " + pMode + " JCA init Bug fixed");

            /* Catch general exceptions */
        } catch (NoSuchAlgorithmException
                | NoSuchPaddingException
                | InvalidAlgorithmParameterException e) {
            System.out.println("Failed to create generator");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            System.out.println("RC5-64 " + pMode + " JCA init Bug still exists");
        }
    }

    /**
     * Test lightweight RC5-64 Init.
     */
    private static void TestRC564Init() {
        /* Create the generator and generate a key */
        CipherKeyGenerator myGenerator = new CipherKeyGenerator();
        SecureRandom myRandom = new SecureRandom();
        KeyGenerationParameters myParams = new KeyGenerationParameters(myRandom, 128);
        myGenerator.init(myParams);
        byte[] myKey = myGenerator.generateKey();

        /* Create base engine */
        RC564Engine myEngine = new RC564Engine();

        /* Create IV */
        byte[] myIV = new byte[12];
        myRandom.nextBytes(myIV);

        /* Create a parameterSpec */
        RC5Parameters myParms = new RC5Parameters(myKey, 12);
        ParametersWithIV myIVParms = new ParametersWithIV(myParms, myIV);

        /* Catch Exceptions */
        try {
            /* Create cipher */
            AEADBlockCipher myCipher = new EAXBlockCipher(myEngine);
            myCipher.init(true, myIVParms);

            System.out.println("RC5-64 EAX init Bug fixed");

            /* Catch general exceptions */
        } catch (IllegalArgumentException e) {
            System.out.println("RC5-64 EAX init Bug still exists");
        }

        /* Catch Exceptions */
        try {
            /* Create cipher */
            AEADBlockCipher myCipher = new CCMBlockCipher(myEngine);
            myCipher.init(true, myIVParms);

            System.out.println("RC5-64 CCM init Bug fixed");

            /* Catch general exceptions */
        } catch (IllegalArgumentException e) {
            System.out.println("RC5-64 CCM init Bug still exists");
        }

        /* Catch Exceptions */
        try {
            /* Create cipher */
            AEADBlockCipher myCipher = new GCMBlockCipher(myEngine);
            myCipher.init(true, myIVParms);

            System.out.println("RC5-64 GCM init Bug fixed");

            /* Catch general exceptions */
        } catch (ClassCastException e) {
            System.out.println("RC5-64 GCM init Bug still exists");
        }

        /* Catch Exceptions */
        try {
            /* Create cipher */
            AEADBlockCipher myCipher = new OCBBlockCipher(myEngine, new RC564Engine());
            myCipher.init(true, myIVParms);

            System.out.println("RC5-64 OCB init Bug fixed");

            /* Catch general exceptions */
        } catch (ClassCastException e) {
            System.out.println("RC5-64 OCB init Bug still exists");
        }
    }
}
