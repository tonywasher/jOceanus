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
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPKeyFlags;
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
import org.bouncycastle.openpgp.PGPSignatureSubpacketVector;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
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
    /* Buffer Size */
    private static final int BUFFER_SIZE = 1 << 16;

    /* Secure Random */
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Main program.
     *
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Access the encryption keys */
            List<BcPGPPublicKeyRing> myRings = loadRings(PGPBase.PUBRSA, PGPBase.PUBDSA);

            /* Access the target file */
            OutputStream myOutput = new FileOutputStream(PGPBase.FILEDIR + "PGPTest.new.asc");
            BufferedOutputStream myBufferedOut = new BufferedOutputStream(myOutput);
            ArmoredOutputStream myArmoredOut = new ArmoredOutputStream(myBufferedOut);

            /* Encrypt the file */
            encryptFile(myArmoredOut, myRings);

            /* Close armored output */
            myArmoredOut.close();

        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the list of target rings
     * @param pRings the keyRing names
     * @return the ring list
     */
    private static List<BcPGPPublicKeyRing> loadRings(final String... pRings) throws PGPException, IOException {
        final List<BcPGPPublicKeyRing> myRings = new ArrayList<>();
        for (final String myRing : pRings) {
            myRings.add(loadRing(myRing));
        }
        return myRings;
    }

    /**
     * Load a publicKeyRing.
     *
     * @param pName the name of the ring
     * @return the loaded ring
     */
    private static BcPGPPublicKeyRing loadRing(final String pName) throws IOException {
        /* Load the ring */
        InputStream myInput = new FileInputStream(PGPBase.FILEDIR + pName);
        BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
        ArmoredInputStream myArmoredIn = new ArmoredInputStream(myBufferedIn);
        return new BcPGPPublicKeyRing(myArmoredIn);
    }

    /**
     * Encrypt a file.
     * @param pOutput the output stream
     * @param pKeyRings the keyRings to encrypt to
     */
    public static void encryptFile(final OutputStream pOutput,
                                   final List<BcPGPPublicKeyRing> pKeyRings) throws PGPException, IOException {
        /* Determine algorithms */
        AvailableAlgs myAlgs = AvailableAlgs.determinePreferences(pKeyRings);

        /* Init encrypted data generator */
        PGPDataEncryptorBuilder myEncBuilder = new BcPGPDataEncryptorBuilder(myAlgs.getSymAlgorithm())
                .setSecureRandom(RANDOM);
        PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(myEncBuilder);
        for (BcPGPPublicKeyRing myRing : pKeyRings) {
            PGPPublicKey myEncKey = obtainEncryptionKey(myRing);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(myEncKey));
        }
        OutputStream encryptedOut = encryptedDataGenerator.open(pOutput, new byte[BUFFER_SIZE]);

        /* start compression */
        PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(myAlgs.getCompAlgorithm());
        OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);

        /* Create the signature builders */
        List<PGPSignatureGenerator> mySigners = createSigners(compressedOut, myAlgs.getHashAlgorithm(),
                PGPBase.SECRSA, PGPBase.SECDSA, PGPBase.SECEC);

        /* Create the Literal Data generator output stream */
        PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        File actualFile = new File(PGPBase.FILEDIR + "PGPTest.docx");
        OutputStream literalOut = literalDataGenerator.open(compressedOut,
                PGPLiteralData.BINARY, "PGPTest.docx",
                new Date(actualFile.lastModified()), new byte[BUFFER_SIZE]);

        /* read input file and write to target file using a buffer */
        InputStream myInput = new FileInputStream(PGPBase.FILEDIR + "PGPTest.docx");
        BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
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
    }

    /**
     * Try to find an encryption key in the keyRing
     * @param pRing the keyRing
     * @return first encryption key
     */
    private static PGPPublicKey obtainEncryptionKey(final PGPPublicKeyRing pRing) {
        /* Access the master key */
        PGPPublicKey myMaster = pRing.getPublicKey();

        /* Loop through keyRing */
        Iterator<PGPPublicKey> kIt = pRing.getPublicKeys();
        while (kIt.hasNext()) {
            PGPPublicKey k = kIt.next();

            /* Access the binding signature */
            PGPSignature mySig = PGPBase.obtainKeyIdSignature(k, myMaster.getKeyID());

            /* Make sure that we can encrypt storage */
            PGPSignatureSubpacketVector v = mySig.getHashedSubPackets();
            if ((v.getKeyFlags() & PGPKeyFlags.CAN_ENCRYPT_STORAGE) != 0) {
                return k;
            }
        }

        /* No valid encryption key */
        throw new IllegalArgumentException("Can't find encryption key in key ring.");
    }

    /**
     * Create signers for secret keys
     * @param pCompressed the compressed stream
     * @param pHashAlgId the hashAlgId
     * @param pSecrets the keyRing names
     * @return the signer list
     */
    private static List<PGPSignatureGenerator> createSigners(final OutputStream pCompressed,
                                                             final int pHashAlgId,
                                                             final String... pSecrets) throws PGPException, IOException {
        final List<PGPSignatureGenerator> mySigners = new ArrayList<>();
        for (final String mySecret : pSecrets) {
            PGPSignatureGenerator mySigner = createSigner(mySecret, pHashAlgId);
            mySigner.generateOnePassVersion(false).encode(pCompressed);
            mySigners.add(0, mySigner);
        }
        return mySigners;
    }

    /**
     * Create signer for secret key
     * @param pSecret the keyRing name
     * @param pHashAlgId the hashAlgId
     * @return the signer
     */
    private static PGPSignatureGenerator createSigner(final String pSecret,
                                                      final int pHashAlgId) throws PGPException, IOException {
        /* Access the signing key */
        InputStream myInput = new FileInputStream(PGPBase.FILEDIR + pSecret);
        BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
        ArmoredInputStream myArmoredIn = new ArmoredInputStream(myBufferedIn);
        BcPGPSecretKeyRing pgpSec = new BcPGPSecretKeyRing(myArmoredIn);
        PGPSecretKey mySignSecret = obtainSigningKey(pgpSec);
        PGPPrivateKey mySignKey = obtainPrivateKey(pSecret, mySignSecret);

        /* Create the signature builder */
        BcPGPContentSignerBuilder mySignerBuilder = new BcPGPContentSignerBuilder(mySignSecret.getPublicKey().getAlgorithm(),
                pHashAlgId);
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
     * @param pRing the keyRing collection
     * @return first signing key
     */
    private static PGPSecretKey obtainSigningKey(final PGPSecretKeyRing pRing) {
        /* Access the master key */
        PGPPublicKey myMaster = pRing.getPublicKey();

        /* Loop through keyRing */
        Iterator<PGPSecretKey> kIt = pRing.getSecretKeys();
        while (kIt.hasNext()) {
            PGPSecretKey k = kIt.next();
            PGPSignature mySig = PGPBase.obtainKeyIdSignature(k.getPublicKey(), myMaster.getKeyID());

            /* Make sure that we can encrypt storage */
            PGPSignatureSubpacketVector v = mySig.getHashedSubPackets();
            if ((v.getKeyFlags() & PGPKeyFlags.CAN_SIGN) != 0) {
                return k;
            }
        }

        /* No valid signing key */
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
        final String myPass = PGPBase.obtainPassword4Secret(pSecret);
        PBESecretKeyDecryptor myDecryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(myPass.toCharArray());
        return pKey.extractPrivateKey(myDecryptor);
    }
}
