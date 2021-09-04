package net.sourceforge.joceanus.jgordianknot.junit.pgp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
import org.bouncycastle.openpgp.PGPSecretKeyRingCollection;
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
    /* Source files location */
    private static final String HOME = System.getProperty("user.home");
    private static final String FILEDIR = HOME + "/PGPTest";

    /**
     * Main program.
     * @param pArgs arguments
     */
    public static void main(final String[] pArgs) {
        /* Protect against exceptions */
        try {
            /*
             * Access the signing keys
             * PGPTest1.pub - RSA/RSA
             * PGPTest2.pub - DSA/ElGamal
             * PGPTest3.pub - EdDSA/XDH
             */
            InputStream myInput = new FileInputStream(FILEDIR + "/PGPTest1.pub.txt");
            BufferedInputStream myBuffered = new BufferedInputStream(myInput);
            ArmoredInputStream myArmored = new ArmoredInputStream(myBuffered);
            BcPGPPublicKeyRing pgpPub = new BcPGPPublicKeyRing(myArmored);
            List<PGPPublicKeyRing> myPubKeys = new ArrayList<>();
            myPubKeys.add(pgpPub);
            myInput = new FileInputStream(FILEDIR + "/PGPTest2.pub.txt");
            myBuffered = new BufferedInputStream(myInput);
            myArmored = new ArmoredInputStream(myBuffered);
            pgpPub = new BcPGPPublicKeyRing(myArmored);
            myPubKeys.add(pgpPub);
            myInput = new FileInputStream(FILEDIR + "/PGPTest3.pub.txt");
            myBuffered = new BufferedInputStream(myInput);
            myArmored = new ArmoredInputStream(myBuffered);
            pgpPub = new BcPGPPublicKeyRing(myArmored);
            myPubKeys.add(pgpPub);
            PGPPublicKeyRingCollection myPubColl = new PGPPublicKeyRingCollection(myPubKeys);

            /*
             * Access the decrypting key
             * password pgptestx where x is key#
             */
            myInput = new FileInputStream(FILEDIR + "/PGPTest2.sec.txt");
            myBuffered = new BufferedInputStream(myInput);
            myArmored = new ArmoredInputStream(myBuffered);
            BcPGPSecretKeyRing pgpSec = new BcPGPSecretKeyRing(myArmored);
            PGPSecretKeyRingCollection mySecColl = new PGPSecretKeyRingCollection(Collections.singletonList(pgpSec));

            /*
             * Access the encrypted file
             * created via
             * gpg --sign --local-user PGPTest1 --local-user PGPTest2 --local-user PGPTest3
             *     --encrypt --recipient PGPTest2 --armor --output PGPTest.docx.asc PGPTest.docx
             */
            myInput = new FileInputStream(FILEDIR + "/PGPTest.new.asc");
            myBuffered = new BufferedInputStream(myInput);
            myArmored = new ArmoredInputStream(myBuffered);
            BcPGPObjectFactory myFact = new BcPGPObjectFactory(myArmored);
            PGPEncryptedDataList myEncList = (PGPEncryptedDataList) myFact.nextObject();
            Iterator<?> it = myEncList.getEncryptedDataObjects();
            PBESecretKeyDecryptor myDecryptor = new BcPBESecretKeyDecryptorBuilder(new BcPGPDigestCalculatorProvider()).build("pgptest2".toCharArray());
            myFact = null;
            while (it.hasNext()) {
                PGPPublicKeyEncryptedData pbe = (PGPPublicKeyEncryptedData) it.next();
                PGPSecretKey mySecret = mySecColl.getSecretKey(pbe.getKeyID());
                if (mySecret != null) {
                    BcPublicKeyDataDecryptorFactory myDecFactory = new BcPublicKeyDataDecryptorFactory(mySecret.extractPrivateKey(myDecryptor));
                    myFact = new BcPGPObjectFactory(pbe.getDataStream(myDecFactory));
                    break;
                }
            }
            if (myFact == null) {
                throw new IOException("Unable to find decryption key");
            }

            /* Access the various data parts */
            PGPCompressedData myCompressed = (PGPCompressedData) myFact.nextObject();
            BcPGPObjectFactory plainFact = new BcPGPObjectFactory(myCompressed.getDataStream());
            PGPOnePassSignatureList mySigs = (PGPOnePassSignatureList) plainFact.nextObject();
            PGPLiteralData myData = (PGPLiteralData) plainFact.nextObject();
            byte[] myBytes = myData.getDataStream().readAllBytes();
            PGPSignatureList mySigList = (PGPSignatureList) plainFact.nextObject();

            /* Process signatures */
            BcPGPContentVerifierBuilderProvider myProvider = new BcPGPContentVerifierBuilderProvider();
            int myNumSigs = mySigs.size();

            /* Loop through the signatures */
            for (int i = 0; i < myNumSigs; i++) {
                /* Access OPS in ascending order and access validating key */
                PGPOnePassSignature mySig = mySigs.get(i);
                PGPPublicKey publicKey = myPubColl.getPublicKey(mySig.getKeyID());
                if (publicKey == null) {
                    throw new IOException("Signing key not found");
                }

                /* Update signature */
                mySig.init(myProvider, publicKey);
                mySig.update(myBytes);

                /* Access signatures in descending order and validate it */
                boolean signOK = mySig.verify(mySigList.get(myNumSigs - 1 - i));
                if (!signOK) {
                    throw new IOException("Validation failed");
                }
            }
        } catch (IOException | PGPException e) {
            e.printStackTrace();
        }
    }
}
