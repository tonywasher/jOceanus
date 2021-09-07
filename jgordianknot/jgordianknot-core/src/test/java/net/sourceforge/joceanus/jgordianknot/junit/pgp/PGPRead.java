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
import org.bouncycastle.openpgp.PGPOnePassSignature;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
import org.bouncycastle.openpgp.PGPSecretKey;
import org.bouncycastle.openpgp.PGPSignature;
import org.bouncycastle.openpgp.PGPSignatureGenerator;
import org.bouncycastle.openpgp.PGPSignatureList;
import org.bouncycastle.openpgp.bc.BcPGPObjectFactory;
import org.bouncycastle.openpgp.bc.BcPGPPublicKeyRing;
import org.bouncycastle.openpgp.bc.BcPGPSecretKeyRing;
import org.bouncycastle.openpgp.operator.PBESecretKeyDecryptor;
import org.bouncycastle.openpgp.operator.bc.BcPBESecretKeyDecryptorBuilder;
import org.bouncycastle.openpgp.operator.bc.BcPGPContentVerifierBuilderProvider;
import org.bouncycastle.openpgp.operator.bc.BcPGPDigestCalculatorProvider;
import org.bouncycastle.openpgp.operator.bc.BcPublicKeyDataDecryptorFactory;

public class PGPRead {
    /**
     * Main program.
     *
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /* Access the decrypted compressed input stream */
            PGPCompressedData myCompressed = accessCompressed(PGPBase.SECDSA);

            /* Validate the compressed stream */
            validateCompressed(myCompressed);

        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load public keyRings
     *
     * @param pSigners the keyRing names
     * @return the keyRing list
     */
    private static List<PGPPublicKeyRing> loadSigners(final String... pSigners) throws PGPException, IOException {
        final List<PGPPublicKeyRing> mySigners = new ArrayList<>();
        for (final String mySigner : pSigners) {
            BcPGPPublicKeyRing myRing = loadPublicKeyRing(mySigner);
            mySigners.add(0, myRing);
        }
        return mySigners;
    }

    /**
     * Load publicKeyRing.
     *
     * @param pName the ring file name
     */
    private static BcPGPPublicKeyRing loadPublicKeyRing(final String pName) throws IOException {
        InputStream myInput = new FileInputStream(PGPBase.FILEDIR + pName);
        BufferedInputStream myBuffered = new BufferedInputStream(myInput);
        ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered);
        return new BcPGPPublicKeyRing(myArmored);
    }

    /**
     * Load secretKeyRing.
     *
     * @param pName the ring file name
     */
    private static BcPGPSecretKeyRing loadSecretKeyRing(final String pName) throws IOException, PGPException {
        InputStream myInput = new FileInputStream(PGPBase.FILEDIR + pName);
        BufferedInputStream myBuffered = new BufferedInputStream(myInput);
        ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered);
        return new BcPGPSecretKeyRing(myArmored);
    }

    /**
     * Access decrypted compressedData
     *
     * @param pName the name of the secret
     * @return the compressed data
     */
    private static PGPCompressedData accessCompressed(final String pName) throws PGPException, IOException {
        /* Access the decrypting key */
        BcPGPSecretKeyRing pgpSec = loadSecretKeyRing(pName);

        /*
         * Load the encrypted DataList
         * created via
         * gpg --sign --local-user PGPTest1 --local-user PGPTest2 --local-user PGPTest3
         *     --encrypt --recipient PGPTest2 --armor --output PGPTest.docx.asc PGPTest.docx
         */
        InputStream myInput = new FileInputStream(PGPBase.FILEDIR + "PGPTest.new.asc");
        BufferedInputStream myBuffered = new BufferedInputStream(myInput);
        ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered);
        BcPGPObjectFactory myFact = new BcPGPObjectFactory(myArmored);
        PGPEncryptedDataList myEncList = (PGPEncryptedDataList) myFact.nextObject();

        /* Loop looking for a decryption match */
        Iterator<?> it = myEncList.getEncryptedDataObjects();
        while (it.hasNext()) {
            PGPPublicKeyEncryptedData pbe = (PGPPublicKeyEncryptedData) it.next();
            PGPSecretKey mySecret = pgpSec.getSecretKey(pbe.getKeyID());
            if (mySecret != null) {
                String myPass = PGPBase.obtainPassword4Secret(pName);
                PBESecretKeyDecryptor myDecryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build(myPass.toCharArray());
                BcPublicKeyDataDecryptorFactory myDecFactory = new BcPublicKeyDataDecryptorFactory(mySecret.extractPrivateKey(myDecryptor));
                myFact = new BcPGPObjectFactory(pbe.getDataStream(myDecFactory));
                return (PGPCompressedData) myFact.nextObject();
            }
        }

        /* Exception if we failed to decrypt */
        throw new IOException("Unable to find decryption key");
    }

    /**
     * Validate compressedData
     * @param pCompressed the compressed data
     */
    private static void validateCompressed(final PGPCompressedData pCompressed) throws PGPException, IOException {
        /* Access the signing keys */
        List<PGPPublicKeyRing> myPubKeys = loadSigners(PGPBase.PUBRSA, PGPBase.PUBDSA, PGPBase.PUBED, PGPBase.PUBEC);
        PGPPublicKeyRingCollection myPubColl = new PGPPublicKeyRingCollection(myPubKeys);

        /* Access the various data parts */
        BcPGPObjectFactory plainFact = new BcPGPObjectFactory(pCompressed.getDataStream());
        PGPOnePassSignatureList myOPSigs = (PGPOnePassSignatureList) plainFact.nextObject();
        PGPLiteralData myData = (PGPLiteralData) plainFact.nextObject();

        /* Initialise the signatures */
        BcPGPContentVerifierBuilderProvider myProvider = new BcPGPContentVerifierBuilderProvider();
        for (PGPOnePassSignature myOPS : myOPSigs) {
            /* access validating key */
            PGPPublicKey publicKey = myPubColl.getPublicKey(myOPS.getKeyID());
            if (publicKey == null) {
                throw new IOException("Signing key not found");
            }

            /* Update signature */
            myOPS.init(myProvider, publicKey);
        }

        /* read input file and write to target file using a buffer */
        InputStream myInput = myData.getDataStream();
        byte[] buf = new byte[PGPBase.BUFFER_SIZE];
        int len;
        while ((len = myInput.read(buf, 0, buf.length)) > 0) {
            for (PGPOnePassSignature myOPS : myOPSigs) {
                myOPS.update(buf, 0, len);
            }
        }

        /* Loop through the signatures */
        PGPSignatureList mySigs = (PGPSignatureList) plainFact.nextObject();
        int myNumSigs = mySigs.size();
        for (int i = 0; i < myNumSigs; i++) {
            /* Access OPS in ascending order and access validating key */
            PGPOnePassSignature myOPS = myOPSigs.get(i);
            PGPSignature mySig = mySigs.get(myNumSigs - 1 - i);

            /* Access signatures in descending order and validate it */
            if (!myOPS.verify(mySig)) {
                throw new IOException("Validation failed");
            }
        }
    }
}
