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
public class PGPSignEncrypt {
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
            final List<BcPGPPublicKeyRing> myRings = loadRings(PGPBase.PUBRSA, PGPBase.PUBDSA, PGPBase.PUBED, PGPBase.PUBEC);

            /* Access the target file */
            final OutputStream myOutput = new FileOutputStream(PGPBase.FILEDIR + "PGPTest.new.asc");
            final BufferedOutputStream myBufferedOut = new BufferedOutputStream(myOutput);
            final ArmoredOutputStream myArmoredOut = new ArmoredOutputStream(myBufferedOut);

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
        final InputStream myInput = new FileInputStream(PGPBase.FILEDIR + pName);
        final BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
        final ArmoredInputStream myArmoredIn = new ArmoredInputStream(myBufferedIn);
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
        final PGPFeatures myAlgs = PGPFeatures.determinePreferences(pKeyRings);

        /* Init encrypted data generator */
        final PGPDataEncryptorBuilder myEncBuilder = new BcPGPDataEncryptorBuilder(myAlgs.getSymAlgorithm())
                .setSecureRandom(RANDOM).setWithIntegrityPacket(myAlgs.withIntegrity());
        final PGPEncryptedDataGenerator encryptedDataGenerator = new PGPEncryptedDataGenerator(myEncBuilder);
        for (BcPGPPublicKeyRing myRing : pKeyRings) {
            final PGPPublicKey myEncKey = obtainEncryptionKey(myRing);
            encryptedDataGenerator.addMethod(new BcPublicKeyKeyEncryptionMethodGenerator(myEncKey));
        }
        final OutputStream encryptedOut = encryptedDataGenerator.open(pOutput, new byte[PGPBase.BUFFER_SIZE]);

        /* start compression */
        final PGPCompressedDataGenerator compressedDataGenerator = new PGPCompressedDataGenerator(myAlgs.getCompAlgorithm());
        final OutputStream compressedOut = compressedDataGenerator.open(encryptedOut);

        /* Create the signature builders */
        final List<PGPSignatureGenerator> mySigners = createSigners(compressedOut, myAlgs.getHashAlgorithm(),
                PGPBase.SECRSA, PGPBase.SECDSA, PGPBase.SECED, PGPBase.SECEC);

        /* Create the Literal Data generator output stream */
        final PGPLiteralDataGenerator literalDataGenerator = new PGPLiteralDataGenerator();
        final File actualFile = new File(PGPBase.FILEDIR + "PGPTest.docx");
        final OutputStream literalOut = literalDataGenerator.open(compressedOut,
                PGPLiteralData.BINARY, "PGPTest.docx",
                new Date(actualFile.lastModified()), new byte[PGPBase.BUFFER_SIZE]);

        /* read input file and write to target file using a buffer */
        final InputStream myInput = new FileInputStream(PGPBase.FILEDIR + "PGPTest.docx");
        final BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
        final byte[] buf = new byte[PGPBase.BUFFER_SIZE];
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
        final PGPPublicKey myMaster = pRing.getPublicKey();

        /* Loop through keyRing */
        final Iterator<PGPPublicKey> kIt = pRing.getPublicKeys();
        while (kIt.hasNext()) {
            final PGPPublicKey k = kIt.next();

            /* Access the binding signature */
            final PGPSignature mySig = PGPBase.obtainKeyIdSignature(k, myMaster.getKeyID());

            /* Make sure that we can encrypt storage */
            final PGPSignatureSubpacketVector v = mySig.getHashedSubPackets();
            if (PGPBase.checkKeyValidity(mySig)
                    && (v.getKeyFlags() & PGPKeyFlags.CAN_ENCRYPT_STORAGE) != 0) {
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
        for (int i = 0; i < pSecrets.length; i++) {
            final String mySecret = pSecrets[i];
            final PGPSignatureGenerator mySigner = createSigner(mySecret, pHashAlgId);
            mySigner.generateOnePassVersion(i < pSecrets.length - 1).encode(pCompressed);
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
        final InputStream myInput = new FileInputStream(PGPBase.FILEDIR + pSecret);
        final BufferedInputStream myBufferedIn = new BufferedInputStream(myInput);
        final ArmoredInputStream myArmoredIn = new ArmoredInputStream(myBufferedIn);
        final BcPGPSecretKeyRing pgpSec = new BcPGPSecretKeyRing(myArmoredIn);
        final PGPSecretKey mySignSecret = obtainSigningKey(pgpSec);
        final PGPPrivateKey mySignKey = obtainPrivateKey(pSecret, mySignSecret);

        /* Create the signature builder */
        final BcPGPContentSignerBuilder mySignerBuilder = new BcPGPContentSignerBuilder(mySignSecret.getPublicKey().getAlgorithm(),
                pHashAlgId);
        if (mySignSecret.getPublicKey().getAlgorithm() != PGPPublicKey.EDDSA) {
            mySignerBuilder.setSecureRandom(RANDOM);
        }

        /* Create the signer */
        final PGPSignatureGenerator mySigner = new PGPSignatureGenerator(mySignerBuilder);
        mySigner.init(PGPSignature.BINARY_DOCUMENT, mySignKey);

        /* Build signature attributes */
        final PGPSignatureSubpacketGenerator spGen = new PGPSignatureSubpacketGenerator();
        spGen.setIssuerFingerprint(false, mySignSecret);
        final Iterator<String> myUserids = mySignSecret.getUserIDs();
        if (myUserids.hasNext()) {
            final String userId = myUserids.next();
            spGen.setSignerUserID(false, userId.getBytes(StandardCharsets.UTF_8));
        }
        mySigner.setHashedSubpackets(spGen.generate());

        /* Return the signer */
        return mySigner;
    }

    /**
     * Try to find a signing key in the KeyRing
     * @param pRing the keyRing collection
     * @return first signing key
     */
    private static PGPSecretKey obtainSigningKey(final PGPSecretKeyRing pRing) {
        /* Access the master key */
        final PGPPublicKey myMaster = pRing.getPublicKey();

        /* Loop through keyRing */
        final Iterator<PGPSecretKey> kIt = pRing.getSecretKeys();
        while (kIt.hasNext()) {
            final PGPSecretKey k = kIt.next();
            final PGPSignature mySig = PGPBase.obtainKeyIdSignature(k.getPublicKey(), myMaster.getKeyID());

            /* Make sure that we can sign data */
            final PGPSignatureSubpacketVector v = mySig.getHashedSubPackets();
            if (PGPBase.checkKeyValidity(mySig)
                    && (v.getKeyFlags() & PGPKeyFlags.CAN_SIGN) != 0) {
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
        final PBESecretKeyDecryptor myDecryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(myPass.toCharArray());
        return pKey.extractPrivateKey(myDecryptor);
    }
}
