package net.sourceforge.joceanus.jgordianknot.junit.pgp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.bcpg.CompressionAlgorithmTags;
import org.bouncycastle.bcpg.HashAlgorithmTags;
import org.bouncycastle.bcpg.SymmetricKeyAlgorithmTags;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPLiteralDataGenerator;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureSubpacketGenerator;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.PGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.PGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentSignerBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyKeyEncryptionMethodGenerator;

/**
 * PGP File encryption utilities.
 */
public class PGPCreate {
    /* Source files location */
    private static final String HOME = System.getProperty("user.home");
    private static final String FILEDIR = HOME + "/PGPTest";

    /* Public Key Names */
    private static final String PUBRSA = "/PGPTest1.pub.txt";
    private static final String PUBDSA = "/PGPTest2.pub.txt";
    private static final String PUBEC = "/PGPTest3.pub.txt";

    /* Secret Key Names */
    private static final String SECRSA = "/PGPTest1.sec.txt";
    private static final String SECDSA = "/PGPTest2.sec.txt";
    private static final String SECEC = "/PGPTest3.sec.txt";

    /* Buffer Size */
    private static final int BUFFER_SIZE = 1 << 20;

    /* Secure Random */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Main program.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /*
             * Access the encryption key
             * PGPTest1.pub - RSA/RSA
             * PGPTest2.pub - DSA/ElGamal
             * PGPTest3.pub - EdDSA/ECDH
             */
            InputStream myInput = new FileInputStream(FILEDIR + PUBDSA);
            BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
            ArmoredInputStream myArmoredIn = new ArmoredInputStream(myBufferedIn);
            BcPGPPublicKeyRing pgpPub = new BcPGPPublicKeyRing(myArmoredIn);
            PGPPublicKey myEncKey = obtainEncryptionKey(pgpPub);

            /*
             * Access the target file
             */
            OutputStream myOutput = new FileOutputStream(FILEDIR + "/PGPTest.new.asc");
            BufferedOutputStream myBufferedOut = new BufferedOutputStream(myOutput);
            ArmoredOutputStream myArmoredOut = new ArmoredOutputStream(myBufferedOut);

            /* Init encrypted data generator */
            PGPDataEncryptorBuilder myEncBuilder = new BcPGPDataEncryptorBuilder(SymmetricKeyAlgorithmTags.AES_128)
                    .setSecureRandom(RANDOM);
            PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(myEncBuilder);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(myEncKey));
            OutputStream encryptedOut = encryptedDataGenerator.open(myArmoredOut, new byte[BUFFER_SIZE]);

            /* start compression */
            PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(
                    CompressionAlgorithmTags.ZIP);
            OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);

            /* Create the signature builders */
            List<PGPSignatureGenerator> mySigners = createSigners(compressedOut, SECRSA, SECDSA, SECEC);

            /* Create the Literal Data generator output stream */
            PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
            File actualFile = new File(FILEDIR + "/PGPTest.docx");
            OutputStream literalOut = literalDataGenerator.open(compressedOut,
                    PGPLiteralData.BINARY, "PGPTest.data",
                    new Date(actualFile.lastModified()), new byte[BUFFER_SIZE]);

            /* read input file and write to target file using a buffer */
            myInput = new FileInputStream(FILEDIR + "/PGPTest.docx");
            myBufferedIn = new BufferedInputStream(myInput);
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while ((len = myBufferedIn.read(buf, 0, buf.length)) > 0) {
                literalOut.write(buf, 0, len);
                for (final PGPSignatureGenerator mySigner : mySigners) {
                    mySigner.update(buf, 0, len);
                }
            }

            /* Close input/output files */
            myBufferedIn.close();
            literalOut.close();
            literalDataGenerator.close();

            /* Generate signatures */
            for (final PGPSignatureGenerator mySigner : mySigners) {
                mySigner.generate().encode(compressedOut);
            }

            /* Close compressed output */
            compressedOut.close();
            compressedDataGenerator.close();

            /* Close encrypted output */
            encryptedOut.close();
            encryptedDataGenerator.close();

            /* Close armored output */
            myArmoredOut.close();

        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Try to find an encryption key in the keyRing
     * We will use the first one for now
     * @param pRing the keyRing
     * @return first encryption key
     */
    private static PGPPublicKey obtainEncryptionKey(final PGPPublicKeyRing pRing) {
        /* Loop through keyRing */
        Iterator<PGPPublicKey> kIt = pRing.getPublicKeys();
        while (kIt.hasNext()) {
            PGPPublicKey k = kIt.next();
            if (k.isEncryptionKey()) {
                return k;
            }
        }
        throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }

    /**
     * Create signers for secret keys
     * @param pCompressed the compressed stream
     * @param pSecrets the keyRing names
     * @return the signer list
     */
    private static List<PGPSignatureGenerator> createSigners(final OutputStream pCompressed,
                                                             final String... pSecrets) throws PGPException, IOException {
        final List<PGPSignatureGenerator> mySigners = new ArrayList<>();
        for (final String mySecret : pSecrets) {
            PGPSignatureGenerator mySigner = createSigner(mySecret);
            mySigner.generateOnePassVersion(false).encode(pCompressed);
            mySigners.add(0, mySigner);
        }
        return mySigners;
    }

    /**
     * Create signer for secret key
     * @param pSecret the keyRing name
     * @return the signer
     */
    private static PGPSignatureGenerator createSigner(final String pSecret) throws PGPException, IOException {
        /* Access the signing key */
        InputStream myInput = new FileInputStream(FILEDIR + pSecret);
        BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
        ArmoredInputStream myArmoredIn = new ArmoredInputStream(myBufferedIn);
        BcPGPSecretKeyRing pgpSec = new BcPGPSecretKeyRing(myArmoredIn);
        PGPSecretKey mySignSecret = obtainSigningKey(pgpSec);
        PGPPrivateKey mySignKey = obtainPrivateKey(pSecret, mySignSecret);

        /* Create the signature builder */
        BcPGPContentSignerBuilder mySignerBuilder = new BcPGPContentSignerBuilder(mySignSecret.getPublicKey().getAlgorithm(),
                HashAlgorithmTags.SHA1);
        if (mySignSecret.getPublicKey().getAlgorithm() != PGPPublicKey.EDDSA) {
            mySignerBuilder.setSecureRandom(RANDOM);
        }
        PGPSignatureGenerator mySigner = new PGPSignatureGenerator(mySignerBuilder);
        mySigner.init(PGPSignature.BINARY_DOCUMENT, mySignKey);
        Iterator<String> myUserids = mySignSecret.getUserIDs();
        if (myUserids.hasNext()) {
            String userId = myUserids.next();
            PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
            spGen.setSignerUserID(false, userId.getBytes(StandardCharsets.UTF_8));
            mySigner.setHashedSubpackets(spGen.generate());
        }
        return mySigner;
    }

    /**
     * Try to find a signing key in the KeyRing
     * We will use the first one for now.
     * @param pRing the keyRing collection
     * @return first signing key
     */
    private static PGPSecretKey obtainSigningKey(final PGPSecretKeyRing pRing) {
        /* Loop through keyRing */
        Iterator<PGPSecretKey> kIt = pRing.getSecretKeys();
        while (kIt.hasNext()) {
            PGPSecretKey k = kIt.next();
            if (k.isSigningKey()) {
                return k;
            }
        }
        throw new IllegalArgumentException("Can't find signing key in key ring.");
    }

    /**
     * Obtain private key for secret key
     * @param pSecret the keyRing name
     * @param pKey the secretKey
     * @return password
     */
    private static PGPPrivateKey obtainPrivateKey(final String pSecret,
                                                  final PGPSecretKey pKey) throws PGPException {
        final String myPass = obtainPassword4Secret(pSecret);
        PBESecretKeyDecryptor myDecryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(myPass.toCharArray());
        return pKey.extractPrivateKey(myDecryptor);
    }

    /**
     * Obtain password for secret key
     * @param pSecret the keyRing name
     * @return password
     */
    private static String obtainPassword4Secret(final String pSecret) {
        switch (pSecret) {
            case SECRSA: return "pgptest1";
            case SECDSA: return "pgptest2";
            case SECEC:  return "pgptest3";
            default:
                throw new IllegalArgumentException("Unexpected secret key.");
        }
    }
}
