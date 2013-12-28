/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2013 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Provider;
import java.security.Security;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jdatamanager.DataConverter;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipFileContents;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipWriteFile;

/**
 * Security Test suite.
 */
public class SecurityTest {
    /**
     * Logger.
     */
    private static Logger theLogger = Logger.getLogger(SecurityTest.class.getName());

    /**
     * Main entry point.
     * @param args the parameters
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Create and show the GUI.
     */
    public static void createAndShowGUI() {
        try {
            listAlgorithms(SecurityProvider.BC);
            // TestStream();
            // checkAlgorithms();
            // testSecurity();
            /* Test zip file creation */
            // File myZipFile = new File("c:\\Users\\Tony\\TestStdZip.zip");
            // createZipFile(myZipFile, new File("c:\\Users\\Tony\\tester"), true);
            // extractZipFile(myZipFile, new File("c:\\Users\\Tony\\testcomp"));
        } catch (Exception e) {
            System.out.println("Help");
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Create a Zip File of files in a directory
     * @param pZipFile the name of the zip file to create
     * @param pDirectory the directory to archive
     * @param bSecure encrypt the zip file (true/false)
     * @return the contents of the zip file
     * @throws JDataException
     */
    protected static ZipFileContents createZipFile(File pZipFile,
                                                   File pDirectory,
                                                   boolean bSecure) throws JDataException {
        ZipWriteFile myZipFile;

        try {
            /* If we are creating a secure zip file */
            if (bSecure) {
                /* Create new Password Hash */
                SecureManager myManager = new SecureManager(theLogger);
                PasswordHash myHash = myManager.resolvePasswordHash(null, "New");

                /* Initialise the Zip file */
                myZipFile = new ZipWriteFile(myHash, pZipFile);
            }

            /* else */
            else {
                /* Just create a standard zip file */
                myZipFile = new ZipWriteFile(pZipFile);
            }

            /* Create a read buffer */
            int myBufLen = 1024;
            byte[] myBuffer = new byte[myBufLen];
            int myRead;

            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory())
                throw new JDataException(ExceptionClass.LOGIC, "Invalid source directory");

            /* Loop through the files is the directory */
            for (File myFile : pDirectory.listFiles()) {
                /* Skip directories */
                if (myFile.isDirectory())
                    continue;

                /* Open the file for reading */
                InputStream myInFile = new FileInputStream(myFile);
                InputStream myInBuffer = new BufferedInputStream(myInFile);

                /* Open the output stream */
                OutputStream myOutput = myZipFile.getOutputStream(new File(myFile.getName()));

                /* Read the header entry */
                while ((myRead = myInBuffer.read(myBuffer, 0, myBufLen)) != -1) {
                    /* Write the data to the zip file */
                    myOutput.write(myBuffer, 0, myRead);
                }

                /* Close the streams */
                myOutput.close();
                myInBuffer.close();
            }

            /* Close the Zip File */
            myZipFile.close();

            /* Return the zip file entries */
            return myZipFile.getContents();

        } catch (JDataException e) {
            throw e;
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.DATA, "Failed to create Zip File", e);
        }
    }

    /**
     * Extract a Zip File to a directory
     * @param pZipFile the name of the zip file to extract from
     * @param pDirectory the directory to extract to
     * @throws JDataException
     */
    protected static void extractZipFile(File pZipFile,
                                         File pDirectory) throws JDataException {
        ZipReadFile myZipFile;

        try {
            /* Access the Zip file */
            myZipFile = new ZipReadFile(pZipFile);

            /* Check for security */
            byte[] myHashBytes = myZipFile.getHashBytes();
            if (myHashBytes != null) {
                /* Resolve security and unlock file */
                SecureManager myManager = new SecureManager(theLogger);
                PasswordHash myHash = myManager.resolvePasswordHash(myHashBytes, pZipFile.getName());
                myZipFile.setPasswordHash(myHash);
            }

            /* Access the contents */
            ZipFileContents myContents = myZipFile.getContents();

            /* Create a read buffer */
            int myBufLen = 1024;
            byte[] myBuffer = new byte[myBufLen];
            int myRead;

            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory())
                throw new JDataException(ExceptionClass.LOGIC, "Invalid source directory");

            Iterator<ZipFileEntry> myIterator = myContents.iterator();
            while (myIterator.hasNext()) {
                /* Access next entry */
                ZipFileEntry myEntry = myIterator.next();

                /* Open the input stream */
                InputStream myInput = myZipFile.getInputStream(myEntry);

                /* Open the output file for writing */
                OutputStream myOutFile = new FileOutputStream(new File(pDirectory, myEntry.getFileName()));
                OutputStream myOutBuffer = new BufferedOutputStream(myOutFile);

                /* Read the entry */
                while ((myRead = myInput.read(myBuffer, 0, myBufLen)) != -1) {
                    /* Write the data to the zip file */
                    myOutBuffer.write(myBuffer, 0, myRead);
                }

                /* Close the streams */
                myInput.close();
                myOutBuffer.close();
            }

        } catch (JDataException e) {
            throw e;
        } catch (Exception e) {
            throw new JDataException(ExceptionClass.DATA, "Failed to extract Zip File", e);
        }
    }

    /**
     * Test security algorithms
     * @throws JDataException
     */
    protected static void testSecurity() throws JDataException {
        /* Create new Password Hash */
        SecureManager myManager = new SecureManager(theLogger);
        PasswordHash myHash = myManager.resolvePasswordHash(null, "New");
        SecurityGenerator myGen = myHash.getSecurityGenerator();

        /* Create new symmetric key and asymmetric Key */
        SymmetricKey mySym = myGen.generateSymmetricKey(SymKeyType.AES);
        AsymmetricKey myAsym = myGen.generateAsymmetricKey();

        /* Secure the keys */
        byte[] mySymSafe = myHash.secureSymmetricKey(mySym);
        byte[] myAsymSafe = myHash.securePrivateKey(myAsym);
        byte[] myAsymPublic = myAsym.getExternalDef();
        byte[] mySymSafe2 = myAsym.secureSymmetricKey(mySym);

        /* Create a data digest */
        DataDigest myDigest = new DataDigest(myGen);
        myDigest.update(mySymSafe);
        myDigest.update(myAsymSafe);
        myDigest.update(myAsymPublic);
        myDigest.update(mySymSafe2);
        // byte[] myDigestBytes = myDigest.digest();

        /* Start a new session */
        myManager = new SecureManager(theLogger);
        PasswordHash myNewHash = myManager.resolvePasswordHash(myHash.getHashBytes(), "Test");
        myGen = myHash.getSecurityGenerator();

        /* Create a message digest */
        myDigest = new DataDigest(myGen, myDigest.getDigestType());
        myDigest.update(mySymSafe);
        myDigest.update(myAsymSafe);
        myDigest.update(myAsymPublic);
        myDigest.update(mySymSafe2);
        // myDigest.validateDigest();

        /* Derive the keys */
        AsymmetricKey myAsym1 = myNewHash.deriveAsymmetricKey(myAsymSafe, myAsymPublic);
        SymmetricKey mySym1 = myNewHash.deriveSymmetricKey(mySymSafe);
        SymmetricKey mySym2 = myAsym1.deriveSymmetricKey(mySymSafe2);

        /* Check the keys are the same */
        if (!myAsym1.equals(myAsym))
            System.out.println("Failed to decrypt AsymmetricKey");
        if (!mySym1.equals(mySym))
            System.out.println("Failed to decrypt SymmetricKey via Hash");
        if (!mySym2.equals(mySym))
            System.out.println("Failed to decrypt SymmetricKey via Asym Key");
    }

    /**
     * List the supported algorithms
     * @param pProvider the provider
     */
    protected static void listAlgorithms(SecurityProvider pProvider) {
        Set<String> ciphers = new HashSet<String>();
        Set<String> keyFactories = new HashSet<String>();
        Set<String> messageDigests = new HashSet<String>();
        Set<String> macs = new HashSet<String>();
        Set<String> signatures = new HashSet<String>();
        Set<String> randoms = new HashSet<String>();
        Set<String> remaining = new HashSet<String>();

        pProvider.ensureInstalled();
        Provider[] providers = Security.getProviders();

        for (int i = 0; i != providers.length; i++) {
            if (!providers[i].getName().equals(pProvider.getProvider()))
                continue;
            Iterator<Object> it = providers[i].keySet().iterator();
            while (it.hasNext()) {
                String entry = (String) it.next();
                if (entry.startsWith("Alg.Alias.")) {
                    entry = entry.substring("Alg.Alias.".length());
                }
                if (entry.startsWith("Cipher.")) {
                    ciphers.add(entry.substring("Cipher.".length()));
                } else if (entry.startsWith("SecretKeyFactory.")) {
                    keyFactories.add(entry.substring("SecretKeyFactory.".length()));
                } else if (entry.startsWith("MessageDigest.")) {
                    messageDigests.add(entry.substring("MessageDigest.".length()));
                } else if (entry.startsWith("Mac.")) {
                    macs.add(entry.substring("Mac.".length()));
                } else if (entry.startsWith("Signature.")) {
                    signatures.add(entry.substring("Signature.".length()));
                } else if (entry.startsWith("SecureRandom.")) {
                    randoms.add(entry.substring("SecureRandom.".length()));
                } else
                    remaining.add(entry);
            }
        }

        printSet("Ciphers", ciphers);
        printSet("SecretKeyFactories", keyFactories);
        printSet("MessageDigests", messageDigests);
        printSet("Macs", macs);
        printSet("Signatures", signatures);
        printSet("Randoms", randoms);
        printSet("Remaining", remaining);
    }

    /**
     * Print out a set of algorithms
     * @param setName the name of the set
     * @param algorithms the set of algorithms
     */
    private static void printSet(String setName,
                                 Set<String> algorithms) {
        System.out.println(setName
                           + ":");
        if (algorithms.isEmpty()) {
            System.out.println("            None available.");
        } else {
            Iterator<String> it = algorithms.iterator();
            while (it.hasNext()) {
                String name = it.next();
                System.out.println("            "
                                   + name);
            }
        }
    }

    /**
     * Check the supported algorithms
     */
    protected static void checkAlgorithms() throws JDataException {
        /* Create new Password Hash */
        SecureManager myManager = new SecureManager(theLogger);
        SecurityGenerator myGenerator = myManager.getSecurityGenerator();

        /* Create instance of each digest */
        for (DigestType myDigest : DigestType.values()) {
            myGenerator.generateDigest(myDigest);
        }

        /* Create instance of each hMac */
        for (DigestType myDigest : DigestType.values()) {
            myGenerator.accessMac(myDigest);
        }

        /* Create instance of each symmetric key */
        for (SymKeyType myType : SymKeyType.values()) {
            myGenerator.generateSymmetricKey(myType);
        }

        /* Create instance of each asymmetric key */
        // for (AsymKeyType myType : AsymKeyType.values()) {
        // AsymmetricKey myKey = myGenerator.generateAsymmetricKey(myType);
        // myKey..accessSignature(myType.getSignature());
        // }
    }

    /**
     * Test stream ciphers.
     * @throws JDataException
     */
    protected static void TestStream() throws JDataException {
        try {
            /* Create new Password Hash */
            SecureManager myManager = new SecureManager(theLogger);
            SecurityGenerator myGenerator = myManager.getSecurityGenerator();

            /* Access the Stream Cipher */
            StreamKeyType myType = StreamKeyType.VMPC;
            Cipher myCipher = Cipher.getInstance(myType.toString(), "BC");
            KeyGenerator myFactory = KeyGenerator.getInstance(myType.toString(), "BC");
            myFactory.init(256, myGenerator.getRandom());
            SecretKey myKey = myFactory.generateKey();
            myCipher.init(Cipher.ENCRYPT_MODE, myKey, myGenerator.getRandom());
            byte[] myIV = myCipher.getIV();

            /* Access test string */
            byte[] myInput = DataConverter.stringToByteArray("MyTesterString");
            byte[] myOutput = myCipher.doFinal(myInput);

            /* Reinitialise for decryption */
            myCipher.init(Cipher.DECRYPT_MODE, myKey, new IvParameterSpec(myIV));
            byte[] myResult = myCipher.doFinal(myOutput);
            String myStr = DataConverter.byteArrayToString(myResult);
            myStr = null;

        } catch (Exception e) {
            throw new JDataException(ExceptionClass.CRYPTO, "FF", e);
        }
    }
}
