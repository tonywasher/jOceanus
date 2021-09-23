package net.sourceforge.joceanus.jgordianknot.junit.pgp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.bcpg.ArmoredInputStream;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPMarker;
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

/**
 * PGP XDecrypt and verify.
 */
public class PGPXDecryptVerify {
    /* Target Style/Decrypt */
    private static final PGPXKeyRingStyle STYLE = PGPXKeyRingStyle.DUO;
    private static final PGPXKeyRing DECRYPT = PGPXKeyRing.DSAELGAMALDUO;

    /**
     * Main program.
     *
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Access the decrypted compressed input stream */
            final BcPGPSecretKeyRing pgpSec = PGPXKeyRingUtil.loadSecretKeyRing(DECRYPT);
            final PGPPublicKeyEncryptedData myEncrypted = accessEncryptedData(pgpSec);
            final PGPCompressedData myCompressed = accessCompressed(myEncrypted, pgpSec);

            /* Validate the compressed stream */
            validateCompressed(myCompressed);

            /* Perform integrity checks if required */
            if (myEncrypted.isIntegrityProtected()
                && !myEncrypted.verify()) {
                throw new IOException("Integrity checks failed");
            }

        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load public keyRings
     *
     * @return the keyRing list
     */
    private static List<PGPPublicKeyRing> loadSigners() throws PGPException, IOException {
        final List<PGPPublicKeyRing> mySigners = new ArrayList<>();
        for (final PGPXKeyRing mySigner : PGPXKeyRing.values()) {
            if (mySigner.getKeyPairStyle() == STYLE) {
                final BcPGPPublicKeyRing myRing = PGPXKeyRingUtil.loadPublicKeyRing(mySigner);
                mySigners.add(myRing);
            }
        }
        return mySigners;
    }

    /**
     * Access encryptedData
     *
     * @param pSecret the secret keyRing
     * @return the encrypted data
     */
    private static PGPPublicKeyEncryptedData accessEncryptedData(final BcPGPSecretKeyRing pSecret) throws PGPException, IOException {
        /*Load the encrypted DataList */
        final InputStream myInput = new FileInputStream(PGPXKeyRingUtil.FILEDIR + "PGPTest.new.asc");
        final BufferedInputStream myBuffered = new BufferedInputStream(myInput);
        final ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered);
        final BcPGPObjectFactory myFact = new BcPGPObjectFactory(myArmored);
        Object myObject = myFact.nextObject();
        while (myObject instanceof PGPMarker) {
            myObject = myFact.nextObject();
        }
        final PGPEncryptedDataList myEncList = (PGPEncryptedDataList) myObject;

        /* Loop looking for a decryption match */
        final Iterator<?> it = myEncList.getEncryptedDataObjects();
        while (it.hasNext()) {
            final PGPPublicKeyEncryptedData pbe = (PGPPublicKeyEncryptedData) it.next();
            final PGPSecretKey mySecret = pSecret.getSecretKey(pbe.getKeyID());
            if (mySecret != null) {
                return pbe;
            }
        }

        /* Exception if we failed to decrypt */
        throw new IOException("Unable to find decryption key");
    }

    /**
     * Access decrypted compressedData
     *
     * @param pEncrypted the selected encryptedData
     * @param pSecret the secret keyRing
     * @return the compressed data
     */
    private static PGPCompressedData accessCompressed(final PGPPublicKeyEncryptedData pEncrypted,
                                                      final BcPGPSecretKeyRing pSecret) throws PGPException, IOException {

        final String myPass = DECRYPT.obtainPassword4Secret();
        final PGPSecretKey mySecret = pSecret.getSecretKey(pEncrypted.getKeyID());
        final PBESecretKeyDecryptor myDecryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(myPass.toCharArray());
        final BcPublicKeyDataDecryptorFactory myDecFactory = new BcPublicKeyDataDecryptorFactory(mySecret.extractPrivateKey(myDecryptor));
        final BcPGPObjectFactory myFact = new BcPGPObjectFactory(pEncrypted.getDataStream(myDecFactory));
        return (PGPCompressedData) myFact.nextObject();
    }

    /**
     * Validate compressedData
     * @param pCompressed the compressed data
     */
    private static void validateCompressed(final PGPCompressedData pCompressed) throws PGPException, IOException {
        /* Access the signing keys */
        final List<PGPPublicKeyRing> myPubKeys = loadSigners();
        final PGPPublicKeyRingCollection myPubColl = new PGPPublicKeyRingCollection(myPubKeys);

        /* Access the various data parts */
        final BcPGPObjectFactory plainFact = new BcPGPObjectFactory(pCompressed.getDataStream());
        final PGPOnePassSignatureList myOPSigs = (PGPOnePassSignatureList) plainFact.nextObject();
        final PGPLiteralData myData = (PGPLiteralData) plainFact.nextObject();

        /* Initialise the signatures */
        final BcPGPContentVerifierBuilderProvider myProvider = new BcPGPContentVerifierBuilderProvider();
        for (final PGPOnePassSignature myOPS : myOPSigs) {
            /* access validating key */
            final PGPPublicKey publicKey = myPubColl.getPublicKey(myOPS.getKeyID());
            if (publicKey == null) {
                throw new IOException("Signing key not found");
            }

            /* Update signature */
            myOPS.init(myProvider, publicKey);
        }

        /* read input file and write to target file using a buffer */
        final InputStream myInput = myData.getDataStream();
        final byte[] buf = new byte[PGPXKeyRingUtil.BUFFER_SIZE];
        int len;
        while ((len = myInput.read(buf, 0, buf.length)) > 0) {
            for (final PGPOnePassSignature myOPS : myOPSigs) {
                myOPS.update(buf, 0, len);
            }
        }

        /* Loop through the signatures */
        final PGPSignatureList mySigs = (PGPSignatureList) plainFact.nextObject();
        final int myNumSigs = mySigs.size();
        for (int i = 0; i < myNumSigs; i++) {
            /* Access OPS in ascending order and access validating key */
            final PGPOnePassSignature myOPS = myOPSigs.get(i);
            final PGPSignature mySig = mySigs.get(myNumSigs - 1 - i);

            /* Access signatures in descending order and validate it */
            if (!myOPS.verify(mySig)) {
                throw new IOException("Validation failed");
            }
        }
    }
}
