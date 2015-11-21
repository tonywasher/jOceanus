/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
import java.util.function.Predicate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipher;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianCipherMode;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianDigestType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKey;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMac;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianMacType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianStreamKeyType;
import net.sourceforge.joceanus.jgordianknot.crypto.GordianSymKeyType;
import net.sourceforge.joceanus.jgordianknot.manager.SecureManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Security Test suite.
 */
public class SecurityTestSuite {
    /**
     * Interface for Security Manager creator.
     */
    public interface SecurityManagerCreator {
        /**
         * Create a new SecureManager with default parameters.
         * @return the new SecureManager
         * @throws JOceanusException on error
         */
        SecureManager newSecureManager() throws JOceanusException;

        /**
         * Create a new SecureManager.
         * @param pParams the security parameters
         * @return the new SecureManager
         * @throws JOceanusException on error
         */
        SecureManager newSecureManager(final GordianParameters pParams) throws JOceanusException;
    }

    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 1024;

    /**
     * The Security Manager creator.
     */
    private final SecurityManagerCreator theCreator;

    /**
     * Constructor.
     * @param pCreator the Secure Manager creator
     */
    public SecurityTestSuite(final SecurityManagerCreator pCreator) {
        theCreator = pCreator;
    }

    /**
     * Create a Zip File of files in a directory.
     * @param pZipFile the name of the zip file to create
     * @param pDirectory the directory to archive
     * @param bSecure encrypt the zip file (true/false)
     * @return the contents of the zip file
     * @throws JOceanusException on error
     */
    public GordianZipFileContents createZipFile(final File pZipFile,
                                                final File pDirectory,
                                                final boolean bSecure) throws JOceanusException {
        GordianZipWriteFile myZipFile;

        try {
            /* If we are creating a secure zip file */
            if (bSecure) {
                /* Create new Password Hash */
                SecureManager myManager = theCreator.newSecureManager();
                GordianKeySetHash myHash = myManager.resolveKeySetHash(null, "New");

                /* Initialise the Zip file */
                myZipFile = new GordianZipWriteFile(myHash, pZipFile);

                /* else */
            } else {
                /* Just create a standard zip file */
                myZipFile = new GordianZipWriteFile(pZipFile);
            }

            /* Create a read buffer */
            int myBufLen = BUFFER_LEN;
            byte[] myBuffer = new byte[myBufLen];
            int myRead;

            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory()) {
                myZipFile.close();
                throw new GordianDataException("Invalid source directory");
            }

            /* Loop through the files in the directory */
            for (File myFile : pDirectory.listFiles()) {
                /* Skip directories */
                if (myFile.isDirectory()) {
                    continue;
                }

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

        } catch (JOceanusException e) {
            throw e;
        } catch (Exception e) {
            throw new GordianIOException("Failed to create Zip File", e);
        }
    }

    /**
     * Extract a Zip File to a directory.
     * @param pZipFile the name of the zip file to extract from
     * @param pDirectory the directory to extract to
     * @throws JOceanusException on error
     */
    public void extractZipFile(final File pZipFile,
                               final File pDirectory) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the file */
            GordianZipReadFile myZipFile = new GordianZipReadFile(pZipFile);

            /* Check for security */
            byte[] myHashBytes = myZipFile.getHashBytes();
            if (myHashBytes != null) {
                /* Resolve security and unlock file */
                SecureManager myManager = theCreator.newSecureManager();
                GordianKeySetHash myHash = myManager.resolveKeySetHash(myHashBytes, pZipFile.getName());
                myZipFile.setKeySetHash(myHash);
            }

            /* Access the contents */
            GordianZipFileContents myContents = myZipFile.getContents();

            /* Create a read buffer */
            int myBufLen = BUFFER_LEN;
            byte[] myBuffer = new byte[myBufLen];
            int myRead;

            /* Make sure that we have a directory */
            if (!pDirectory.isDirectory()) {
                throw new GordianDataException("Invalid target directory");
            }

            /* Loop through the entries */
            Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
            while (myIterator.hasNext()) {
                /* Access next entry */
                GordianZipFileEntry myEntry = myIterator.next();

                /* Open the input stream */
                InputStream myInput = myZipFile.getInputStream(myEntry);

                /* Open the output file for writing */
                OutputStream myOutFile = new FileOutputStream(new File(pDirectory, myEntry.getFileName()));
                OutputStream myOutBuffer = new BufferedOutputStream(myOutFile);

                /* Read the entry */
                while ((myRead = myInput.read(myBuffer, 0, myBufLen)) != -1) {
                    /* Write the data to the new file */
                    myOutBuffer.write(myBuffer, 0, myRead);
                }

                /* Close the streams */
                myInput.close();
                myOutBuffer.close();
            }

        } catch (JOceanusException e) {
            throw e;
        } catch (Exception e) {
            throw new GordianIOException("Failed to extract Zip File", e);
        }
    }

    /**
     * Test security algorithms.
     * @throws JOceanusException on error
     */
    protected void testSecurity() throws JOceanusException {
        testSecurity(true, GordianFactoryType.BC);
        testSecurity(false, GordianFactoryType.BC);
        testSecurity(true, GordianFactoryType.JCA);
        testSecurity(false, GordianFactoryType.JCA);
    }

    /**
     * Test security algorithms.
     * @param pRestricted is the factory restricted
     * @param pType the type of factory
     * @throws JOceanusException on error
     */
    private void testSecurity(final boolean pRestricted,
                              final GordianFactoryType pType) throws JOceanusException {
        /* Create new Password Hash */
        GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        SecureManager myManager = theCreator.newSecureManager(myParams);
        GordianKeySetHash myHash = myManager.resolveKeySetHash(null, "New");
        GordianKeySet myKeySet = myHash.getKeySet();
        GordianFactory myFactory = myKeySet.getFactory();

        /* Create new symmetric key and asymmetric Key */
        GordianKey<GordianSymKeyType> mySym = myFactory.generateRandomSymKey();
        GordianKey<GordianStreamKeyType> myStream = myFactory.generateRandomStreamKey();

        /* Secure the keys */
        byte[] mySymSafe = myKeySet.secureKey(mySym);
        byte[] myStreamSafe = myKeySet.secureKey(myStream);

        /* Encrypt some bytes */
        String myTest = "TestString";
        byte[] myBytes = DataConverter.stringToByteArray(myTest);
        byte[] myEncrypt = myKeySet.encryptBytes(myBytes);

        /* Create a data digest */
        GordianDigest myDigest = myFactory.generateRandomDigest();
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        byte[] myDigestBytes = myDigest.finish();

        /* Create a data MAC */
        GordianMac myMac = myFactory.generateRandomMac();
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        byte[] myMacBytes = myMac.finish();

        /* Secure the keys */
        byte[] myMacSafe = myKeySet.secureKey(myMac.getKey());
        byte[] myIV = myMac.getInitVector();
        int myMacId = myKeySet.deriveExternalIdForType(myMac.getMacSpec());

        /* Start a new session */
        myManager = theCreator.newSecureManager(myParams);
        GordianKeySetHash myNewHash = myManager.resolveKeySetHash(myHash.getHash(), "Test");
        GordianKeySet myKeySet1 = myNewHash.getKeySet();
        myFactory = myKeySet.getFactory();

        /* Check the keySets are the same */
        if (!myKeySet1.equals(myKeySet)) {
            System.out.println("Failed to derive keySet");
        }

        /* Derive the Mac */
        GordianMacSpec myMacSpec = myKeySet1.deriveTypeFromExternalId(myMacId, GordianMacSpec.class);
        GordianKey<GordianMacSpec> myMacKey = myKeySet1.deriveKey(myMacSafe, myMacSpec);
        myMac = myFactory.createMac(myMacSpec);
        myMac.initMac(myMacKey, myIV);
        myMac.update(mySymSafe);
        myMac.update(myStreamSafe);
        byte[] myMac1Bytes = myMac.finish();

        /* Create a message digest */
        myDigest = myFactory.createDigest(myDigest.getDigestType());
        myDigest.update(mySymSafe);
        myDigest.update(myStreamSafe);
        byte[] myNewBytes = myDigest.finish();

        /* Check the digests are the same */
        if (!Arrays.areEqual(myDigestBytes, myNewBytes)) {
            System.out.println("Failed to recalculate digest");
        }
        if (!Arrays.areEqual(myMacBytes, myMac1Bytes)) {
            System.out.println("Failed to recalculate mac");
        }

        /* Derive the keys */
        GordianKey<GordianSymKeyType> mySym1 = myKeySet1.deriveKey(mySymSafe, mySym.getKeyType());
        GordianKey<GordianStreamKeyType> myStm1 = myKeySet1.deriveKey(myStreamSafe, myStream.getKeyType());

        /* Check the keys are the same */
        if (!mySym1.equals(mySym)) {
            System.out.println("Failed to decrypt SymmetricKey");
        }
        if (!myStm1.equals(myStream)) {
            System.out.println("Failed to decrypt StreamKey");
        }

        /* Decrypt the bytes */
        byte[] myResult = myKeySet1.decryptBytes(myEncrypt);
        String myAnswer = DataConverter.byteArrayToString(myResult);
        if (!myAnswer.equals(myTest)) {
            System.out.println("Failed to decrypt test string");
        }
    }

    /**
     * List the supported algorithms.
     * @param pProvider the provider
     */
    protected static void listAlgorithms() {
        Set<String> ciphers = new HashSet<String>();
        Set<String> secretKeyFactories = new HashSet<String>();
        Set<String> keyFactories = new HashSet<String>();
        Set<String> keyAgreements = new HashSet<String>();
        Set<String> keyGenerators = new HashSet<String>();
        Set<String> keyPairGenerators = new HashSet<String>();
        Set<String> messageDigests = new HashSet<String>();
        Set<String> macs = new HashSet<String>();
        Set<String> signatures = new HashSet<String>();
        Set<String> randoms = new HashSet<String>();
        Set<String> remaining = new HashSet<String>();

        Security.addProvider(new BouncyCastleProvider());
        Provider[] providers = Security.getProviders();

        for (int i = 0; i != providers.length; i++) {
            if (!providers[i].getName().equals("BC")) {
                continue;
            }
            Iterator<Object> it = providers[i].keySet().iterator();
            while (it.hasNext()) {
                String entry = (String) it.next();
                if (entry.startsWith("Alg.Alias.")) {
                    entry = entry.substring("Alg.Alias.".length());
                }
                if (entry.startsWith("Cipher.")) {
                    ciphers.add(entry.substring("Cipher.".length()));
                } else if (entry.startsWith("SecretKeyFactory.")) {
                    secretKeyFactories.add(entry.substring("SecretKeyFactory.".length()));
                } else if (entry.startsWith("KeyFactory.")) {
                    keyFactories.add(entry.substring("KeyFactory.".length()));
                } else if (entry.startsWith("KeyAgreement.")) {
                    keyAgreements.add(entry.substring("KeyAgreement.".length()));
                } else if (entry.startsWith("KeyGenerator.")) {
                    keyGenerators.add(entry.substring("KeyGenerator.".length()));
                } else if (entry.startsWith("KeyPairGenerator.")) {
                    keyPairGenerators.add(entry.substring("KeyPairGenerator.".length()));
                } else if (entry.startsWith("MessageDigest.")) {
                    messageDigests.add(entry.substring("MessageDigest.".length()));
                } else if (entry.startsWith("Mac.")) {
                    macs.add(entry.substring("Mac.".length()));
                } else if (entry.startsWith("Signature.")) {
                    signatures.add(entry.substring("Signature.".length()));
                } else if (entry.startsWith("SecureRandom.")) {
                    randoms.add(entry.substring("SecureRandom.".length()));
                } else {
                    remaining.add(entry);
                }
            }
        }

        printSet("Ciphers", ciphers);
        printSet("SecretKeyFactories", secretKeyFactories);
        printSet("KeyFactories", keyFactories);
        printSet("KeyAgreements", keyAgreements);
        printSet("KeyGenerators", keyGenerators);
        printSet("KeyPairGenerators", keyPairGenerators);
        printSet("MessageDigests", messageDigests);
        printSet("Macs", macs);
        printSet("Signatures", signatures);
        printSet("Randoms", randoms);
        printSet("Remaining", remaining);
    }

    /**
     * Print out a set of algorithms.
     * @param setName the name of the set
     * @param algorithms the set of algorithms
     */
    private static void printSet(final String setName,
                                 final Set<String> algorithms) {
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
     * Check the supported algorithms.
     * @throws JOceanusException on error
     */
    protected void checkAlgorithms() throws JOceanusException {
        checkAlgorithms(true, GordianFactoryType.BC);
        checkAlgorithms(false, GordianFactoryType.BC);
        checkAlgorithms(true, GordianFactoryType.JCA);
        checkAlgorithms(false, GordianFactoryType.JCA);
    }

    /**
     * Check the supported algorithms.
     * @param pRestricted is the factory restricted
     * @param pType the type of factory
     * @throws JOceanusException on error
     */
    private void checkAlgorithms(final boolean pRestricted,
                                 final GordianFactoryType pType) throws JOceanusException {
        /* Create new Security Generator */
        GordianParameters myParams = new GordianParameters(pRestricted);
        myParams.setFactoryType(pType);
        SecureManager myManager = theCreator.newSecureManager(myParams);
        GordianFactory myFactory = myManager.getSecurityFactory();

        /* Access predicates */
        Predicate<GordianDigestType> myDigestPredicate = myFactory.supportedDigests();
        Predicate<GordianSymKeyType> mySymKeyPredicate = myFactory.supportedSymKeys();
        Predicate<GordianSymKeyType> myMacSymKeyPredicate = myFactory.standardSymKeys();
        Predicate<GordianStreamKeyType> myStreamKeyPredicate = myFactory.supportedStreamKeys();

        /* Create instance of each digest and associated hMac */
        for (GordianDigestType myDigest : GordianDigestType.values()) {
            if (myDigestPredicate.test(myDigest)) {
                myFactory.createDigest(myDigest);
                GordianMacSpec myMacSpec = new GordianMacSpec(GordianMacType.HMAC, myDigest);
                GordianMac myMac = myFactory.createMac(myMacSpec);
                GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(myMacSpec);
                GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();
                myMac.initMac(myKey);
            }
        }

        /* Create instance of each cipher Mac */
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (myMacSymKeyPredicate.test(myType)) {
                GordianMacSpec myMacSpec = new GordianMacSpec(GordianMacType.GMAC, myType);
                GordianMac myMac = myFactory.createMac(myMacSpec);
                GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(myMacSpec);
                GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();
                myMac.initMac(myKey);
                myMacSpec = new GordianMacSpec(GordianMacType.POLY1305, myType);
                myMac = myFactory.createMac(new GordianMacSpec(GordianMacType.POLY1305, myType));
                myGenerator = myFactory.getKeyGenerator(myMacSpec);
                myKey = myGenerator.generateKey();
                myMac.initMac(myKey);
            }
        }

        /* Create remaining MACs */
        GordianMacSpec myMacSpec = new GordianMacSpec(GordianMacType.SKEIN);
        GordianMac myMac = myFactory.createMac(myMacSpec);
        GordianKeyGenerator<GordianMacSpec> myGenerator = myFactory.getKeyGenerator(myMacSpec);
        GordianKey<GordianMacSpec> myKey = myGenerator.generateKey();
        myMac.initMac(myKey);
        myMacSpec = new GordianMacSpec(GordianMacType.VMPC);
        myMac = myFactory.createMac(myMacSpec);
        myGenerator = myFactory.getKeyGenerator(myMacSpec);
        myKey = myGenerator.generateKey();
        myMac.initMac(myKey);

        /* Create instance of each symmetric key */
        for (GordianSymKeyType myType : GordianSymKeyType.values()) {
            if (mySymKeyPredicate.test(myType)) {
                GordianKeyGenerator<GordianSymKeyType> mySymGenerator = myFactory.getKeyGenerator(myType);
                GordianKey<GordianSymKeyType> mySymKey = mySymGenerator.generateKey();
                GordianCipher<GordianSymKeyType> myCipher = myFactory.createSymKeyCipher(myType, GordianCipherMode.SIC, false);
                myCipher.initCipher(mySymKey);
            }
        }

        /* Create instance of each stream key */
        for (GordianStreamKeyType myType : GordianStreamKeyType.values()) {
            if (myStreamKeyPredicate.test(myType)) {
                GordianKeyGenerator<GordianStreamKeyType> myStreamGenerator = myFactory.getKeyGenerator(myType);
                GordianKey<GordianStreamKeyType> myStreamKey = myStreamGenerator.generateKey();
                GordianCipher<GordianStreamKeyType> myCipher = myFactory.createStreamKeyCipher(myType);
                myCipher.initCipher(myStreamKey);
            }
        }
    }
}
