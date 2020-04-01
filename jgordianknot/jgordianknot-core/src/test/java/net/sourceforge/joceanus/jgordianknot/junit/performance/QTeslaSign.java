package net.sourceforge.joceanus.jgordianknot.junit.performance;

import java.security.SecureRandom;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASecurityCategory;
import org.bouncycastle.pqc.crypto.qtesla.QTESLASigner;

/**
 * QTesla signature tests.
 */
public class QTeslaSign {
    /**
     * The testMessage length.
     */
    private static final int MSGLEN = 1024;

    /**
     * The number of signatures.
     */
    private static final int NUMSIGNS = 10000;

    /**
     * The secureRandom.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * The testMessage.
     */
    private static final byte[] MESSAGE = new byte[MSGLEN];

    /**
     * main.
     * @param pArgs the program arguments
     */
    public static void main(final String[] pArgs) {
        /* Initialise the message */
        RANDOM.nextBytes(MESSAGE);

        /* Create and initialise the generator */
        final QTESLAKeyPairGenerator myGenerator = new QTESLAKeyPairGenerator();
        final KeyGenerationParameters myParams
                = new QTESLAKeyGenerationParameters(QTESLASecurityCategory.PROVABLY_SECURE_I, RANDOM);
        myGenerator.init(myParams);

        /* Generate the keyPair */
        final AsymmetricCipherKeyPair myPair = myGenerator.generateKeyPair();
        final QTESLAPublicKeyParameters myPublicKey = (QTESLAPublicKeyParameters) myPair.getPublic();
        final QTESLAPrivateKeyParameters myPrivateKey = (QTESLAPrivateKeyParameters) myPair.getPrivate();

        /* Initialise signer and verifier */
        final QTESLASigner mySigner = new QTESLASigner();
        mySigner.init(true, new ParametersWithRandom(myPrivateKey, RANDOM));
        final QTESLASigner myVerifier = new QTESLASigner();
        myVerifier.init(false, myPublicKey);

        /* Loop NUMSIGNS times */
        for (int i = 0; i < NUMSIGNS; i++) {
            /* Sign the message */
            final byte[] mySignature =  mySigner.generateSignature(MESSAGE);

            /* Verify the signature */
            if (!myVerifier.verifySignature(MESSAGE, mySignature)) {
                /* Handle failure */
                System.out.println("\rFailed on signature " + (i + 1) + " of " + NUMSIGNS);
                System.exit(1);
            }

            /* Display progress */
            System.out.print("\r" + (i + 1));
        }

        /* Succeeded */
        System.out.println("\rSucceeded");
    }
}
