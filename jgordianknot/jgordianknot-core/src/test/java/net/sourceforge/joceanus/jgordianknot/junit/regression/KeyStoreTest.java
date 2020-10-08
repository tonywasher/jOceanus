/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.junit.regression;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianCipherFactory;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKey;
import net.sourceforge.joceanus.jgordianknot.api.key.GordianKeyGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreHash;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyStore Tests.
 */
public class KeyStoreTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * A factory.
     */
    private static GordianFactory FACTORY;

    /**
     * Create the factory.
     * @throws OceanusException on error
     */
    @BeforeAll
    public static void createFactory() throws OceanusException {
        /* Create the factory */
        FACTORY = GordianGenerator.createFactory(GordianFactoryType.BC);
    }

    /**
     * test symmetric.
     * @throws OceanusException on error
     */
    @Test
    public void symmetric() throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = FACTORY.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore(myKeySetHashSpec);

        /* Create a keySetHash */
        final GordianKeySetFactory mySetFactory = FACTORY.getKeySetFactory();
        final GordianKeySetHash myHash = mySetFactory.generateKeySetHash(myKeySetHashSpec, DEF_PASSWORD);
        myStore.setKeySetHash("HashDef", myHash);
        final GordianKeyStoreHash myHashRec = myStore.getKeySetHash("HashDef", DEF_PASSWORD);
        Assertions.assertEquals(myHash, myHashRec.getKeySetHash());
        checkKeySetHash(myStore, "HashDef", myHashRec);

        /* Record the keySet */
        final GordianKeySet myKeySet = mySetFactory.generateKeySet(myKeySetSpec);
        myStore.setKeySet("KeySet", myKeySet, DEF_PASSWORD);
        final GordianKeyStoreSet mySetRec = myStore.getKeySet("KeySet", DEF_PASSWORD);
        Assertions.assertEquals(myKeySet, mySetRec.getKeySet());
        checkKeySet(myStore, "KeySet", mySetRec);

        /* Create a symKey */
        final GordianCipherFactory myCipherFactory = FACTORY.getCipherFactory();
        final GordianKeyGenerator<GordianSymKeySpec> mySymGenerator = myCipherFactory.getKeyGenerator(GordianSymKeySpec.aes(myKeyLen));
        final GordianKey<GordianSymKeySpec> mySymKey = mySymGenerator.generateKey();
        myStore.setKey("symKey", mySymKey, DEF_PASSWORD);
        final GordianKeyStoreKey<?> mySymKeyRec = myStore.getKey("symKey", DEF_PASSWORD);
        Assertions.assertEquals(mySymKey, mySymKeyRec.getKey());
        checkKey(myStore, "symKey", mySymKeyRec);

        /* Create a streamKey */
        final GordianKeyGenerator<GordianStreamKeySpec> myStreamGenerator = myCipherFactory.getKeyGenerator(GordianStreamKeySpec.hc(myKeyLen));
        final GordianKey<GordianStreamKeySpec> myStreamKey = myStreamGenerator.generateKey();
        myStore.setKey("streamKey", myStreamKey, DEF_PASSWORD);
        final GordianKeyStoreKey<?> myStreamKeyRec = myStore.getKey("streamKey", DEF_PASSWORD);
        Assertions.assertEquals(myStreamKey, myStreamKeyRec.getKey());
        checkKey(myStore, "streamKey", myStreamKeyRec);

        /* Create a macKey */
        final GordianMacFactory myMacFactory = FACTORY.getMacFactory();
        final GordianKeyGenerator<GordianMacSpec> myMacGenerator = myMacFactory.getKeyGenerator(GordianMacSpec.vmpcMac(myKeyLen));
        final GordianKey<GordianMacSpec> myMacKey = myMacGenerator.generateKey();
        myStore.setKey("macKey", myMacKey, DEF_PASSWORD);
        final GordianKeyStoreKey<?> myMacKeyRec = myStore.getKey("macKey", DEF_PASSWORD);
        Assertions.assertEquals(myMacKey, myMacKeyRec.getKey());
        checkKey(myStore, "macKey", myMacKeyRec);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
        myStore.deleteEntry("HashDef");
        myStore.deleteEntry("KeySet");
        myStore.deleteEntry("symKey");
        myStore.deleteEntry("streamKey");
        myStore.deleteEntry("macKey");

        /* Check that we have deleted all values */
        int mySize = myStore.size();
        Assertions.assertEquals(0, mySize);
    }

    /**
     * test keyPairs.
     * @throws OceanusException on error
     */
    @Test
    public void keyPairs() throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = FACTORY.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore(myKeySetHashSpec);

        /* Create root certificates */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1);
        final X500Name myRootName = buildX500Name("Root Certificate");
        final GordianKeyStorePair myRoot = myStore.createRootKeyPair(mySpec, myRootName, "RootCert", DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name("Root Certificate 2");
        final GordianKeyStorePair myRoot2 = myStore.createRootKeyPair(mySpec, myRoot2Name, "RootCert2", DEF_PASSWORD);
        checkKeyPair(myStore, "RootCert", myRoot);
        checkKeyPair(myStore, "RootCert2", myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePair myRootAlt = myStore.createAlternate(myRoot, myUsage, myRoot2, "RootCertAlt", DEF_PASSWORD);
        final GordianKeyStorePair myRoot2Alt = myStore.createAlternate(myRoot2, myUsage, myRoot, "RootCert2Alt", DEF_PASSWORD);
        checkKeyPair(myStore, "RootCertAlt", myRootAlt);
        checkKeyPair(myStore, "RootCert2Alt", myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name("Intermediate Certificate");
        final GordianKeyStorePair myIntermediate = myStore.createKeyPair(mySpec, myInterName, myUsage, myRoot, "InterCert", DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name("Intermediate Certificate 2");
        final GordianKeyStorePair myIntermediate2 = myStore.createKeyPair(mySpec, myInter2Name, myUsage, myRoot2, "InterCert2", DEF_PASSWORD);
        checkKeyPair(myStore, "InterCert", myIntermediate);
        checkKeyPair(myStore, "InterCert2", myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair myIntermediateAlt = myStore.createAlternate(myIntermediate, myUsage, myRoot2, "InterCertAlt", DEF_PASSWORD);
        final GordianKeyStorePair myIntermediate2Alt = myStore.createAlternate(myIntermediate2, myUsage, myRoot, "InterCert2Alt", DEF_PASSWORD);
        checkKeyPair(myStore, "InterCertAlt", myIntermediateAlt);
        checkKeyPair(myStore, "InterCert2Alt", myIntermediate2Alt);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name("Signing Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair mySigner = myStore.createKeyPair(mySpec, mySignName, myUsage, myIntermediate, "SigningCert", DEF_PASSWORD);
        checkKeyPair(myStore, "SigningCert", mySigner);

        /* Create an agreement keyPair */
        final X500Name myAgreeName = buildX500Name("Agreement Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePair myAgree = myStore.createKeyPair(mySpec, myAgreeName, myUsage, myIntermediate, "AgreementCert", DEF_PASSWORD);
        checkKeyPair(myStore, "AgreementCert", myAgree);

        /* Create an encryption keyPair */
        final X500Name myEncryptName = buildX500Name("Encryption Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePair myEncrypt = myStore.createKeyPair(mySpec, myEncryptName, myUsage, myIntermediate, "EncryptCert", DEF_PASSWORD);
        checkKeyPair(myStore, "EncryptCert", myEncrypt);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
        myStore.deleteEntry("RootCert");
        myStore.deleteEntry("RootCert2");
        myStore.deleteEntry("RootCertAlt");
        myStore.deleteEntry("RootCert2Alt");
        myStore.deleteEntry("InterCert");
        myStore.deleteEntry("InterCert2");
        myStore.deleteEntry("InterCertAlt");
        myStore.deleteEntry("InterCert2Alt");
        myStore.deleteEntry("SigningCert");
        myStore.deleteEntry("AgreementCert");
        myStore.deleteEntry("EncryptCert");

        /* Check that we have deleted all values */
        int mySize = myStore.size();
        Assertions.assertEquals(0, mySize);
    }

    /**
     * test keyPairSet.
     * @throws OceanusException on error
     */
    @Test
    public void keyPairSet() throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = FACTORY.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore(myKeySetHashSpec);

        /* Create root certificates */
        final GordianKeyPairSetSpec mySpec = GordianKeyPairSetSpec.SIGNLO;
        final X500Name myRootName = buildX500Name("Root Certificate");
        final GordianKeyStorePairSet myRoot = myStore.createRootKeyPairSet(mySpec, myRootName, "RootCert", DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name("Root Certificate 2");
        final GordianKeyStorePairSet myRoot2 = myStore.createRootKeyPairSet(mySpec, myRoot2Name, "RootCert2", DEF_PASSWORD);
        checkKeyPairSet(myStore, "RootCert", myRoot);
        checkKeyPairSet(myStore, "RootCert2", myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePairSet myRootAlt = myStore.createAlternate(myRoot, myUsage, myRoot2, "RootCertAlt", DEF_PASSWORD);
        final GordianKeyStorePairSet myRoot2Alt = myStore.createAlternate(myRoot2, myUsage, myRoot, "RootCert2Alt", DEF_PASSWORD);
        checkKeyPairSet(myStore, "RootCertAlt", myRootAlt);
        checkKeyPairSet(myStore, "RootCert2Alt", myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name("Intermediate Certificate");
        final GordianKeyStorePairSet myIntermediate = myStore.createKeyPairSet(mySpec, myInterName, myUsage, myRoot, "InterCert", DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name("Intermediate Certificate 2");
        final GordianKeyStorePairSet myIntermediate2 = myStore.createKeyPairSet(mySpec, myInter2Name, myUsage, myRoot2, "InterCert2", DEF_PASSWORD);
        checkKeyPairSet(myStore, "InterCert", myIntermediate);
        checkKeyPairSet(myStore, "InterCert2", myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet myIntermediateAlt = myStore.createAlternate(myIntermediate, myUsage, myRoot2, "InterCertAlt", DEF_PASSWORD);
        final GordianKeyStorePairSet myIntermediate2Alt = myStore.createAlternate(myIntermediate2, myUsage, myRoot, "InterCert2Alt", DEF_PASSWORD);
        checkKeyPairSet(myStore, "InterCertAlt", myIntermediateAlt);
        checkKeyPairSet(myStore, "InterCert2Alt", myIntermediate2Alt);

        /* Create a signature keyPairSet */
        final X500Name mySignName = buildX500Name("Signing Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet mySigner = myStore.createKeyPairSet(mySpec, mySignName, myUsage, myIntermediate, "SigningCert", DEF_PASSWORD);
        checkKeyPairSet(myStore, "SigningCert", mySigner);

        /* Create an agreement keyPairSet */
        final X500Name myAgreeName = buildX500Name("Agreement Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePairSet myAgree = myStore.createKeyPairSet(GordianKeyPairSetSpec.AGREELO, myAgreeName, myUsage, myIntermediate, "AgreementCert", DEF_PASSWORD);
        checkKeyPairSet(myStore, "AgreementCert", myAgree);

        /* Create an encryption keyPairSet */
        final X500Name myEncryptName = buildX500Name("Encryption Certificate");
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePairSet myEncrypt = myStore.createKeyPairSet(GordianKeyPairSetSpec.ENCRYPT, myEncryptName, myUsage, myIntermediate, "EncryptCert", DEF_PASSWORD);
        checkKeyPairSet(myStore, "EncryptCert", myEncrypt);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
        myStore.deleteEntry("RootCert");
        myStore.deleteEntry("RootCert2");
        myStore.deleteEntry("RootCertAlt");
        myStore.deleteEntry("RootCert2Alt");
        myStore.deleteEntry("InterCert");
        myStore.deleteEntry("InterCert2");
        myStore.deleteEntry("InterCertAlt");
        myStore.deleteEntry("InterCert2Alt");
        myStore.deleteEntry("SigningCert");
        myStore.deleteEntry("AgreementCert");
        myStore.deleteEntry("EncryptCert");

        /* Check that we have deleted all values */
        int mySize = myStore.size();
        Assertions.assertEquals(0, mySize);
    }

    /**
     * check keySetHash.
     * @param pKeyStore the keyStore
     * @param pName the name
     * @param pKeyHash the keyHash
     * @throws OceanusException on error
     */
    public void checkKeySetHash(final GordianKeyStore pKeyStore,
                                final String pName,
                                final GordianKeyStoreHash pKeyHash) throws OceanusException {
        Assertions.assertTrue(pKeyStore.containsAlias(pName));
        Assertions.assertTrue(pKeyStore.isKeySetHashEntry(pName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(pName, GordianKeyStoreHash.class));
        Assertions.assertEquals(pKeyHash, pKeyStore.getKeySetHash(pName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyHash, pKeyStore.getEntry(pName, DEF_PASSWORD));
    }

    /**
     * check keySet.
     * @param pKeyStore the keyStore
     * @param pName the name
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    public void checkKeySet(final GordianKeyStore pKeyStore,
                            final String pName,
                            final GordianKeyStoreSet pKeySet) throws OceanusException {
        Assertions.assertTrue(pKeyStore.containsAlias(pName));
        Assertions.assertTrue(pKeyStore.isKeySetEntry(pName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(pName, GordianKeyStoreSet.class));
        Assertions.assertEquals(pKeySet, pKeyStore.getKeySet(pName, DEF_PASSWORD));
        Assertions.assertEquals(pKeySet, pKeyStore.getEntry(pName, DEF_PASSWORD));
    }

    /**
     * check key.
     * @param pKeyStore the keyStore
     * @param pName the name
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void checkKey(final GordianKeyStore pKeyStore,
                         final String pName,
                         final GordianKeyStoreKey<?> pKey) throws OceanusException {
        Assertions.assertTrue(pKeyStore.containsAlias(pName));
        Assertions.assertTrue(pKeyStore.isKeyEntry(pName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(pName, GordianKeyStoreKey.class));
        Assertions.assertEquals(pKey, pKeyStore.getKey(pName, DEF_PASSWORD));
        Assertions.assertEquals(pKey, pKeyStore.getEntry(pName, DEF_PASSWORD));
    }

    /**
     * check keyPair.
     * @param pKeyStore the keyStore
     * @param pName the name
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    public void checkKeyPair(final GordianKeyStore pKeyStore,
                             final String pName,
                             final GordianKeyStorePair pKeyPair) throws OceanusException {
        Assertions.assertTrue(pKeyStore.containsAlias(pName));
        Assertions.assertTrue(pKeyStore.isKeyPairEntry(pName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(pName, GordianKeyStorePair.class));
        Assertions.assertEquals(pKeyPair, pKeyStore.getKeyPair(pName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPair, pKeyStore.getEntry(pName, DEF_PASSWORD));
    }

    /**
     * check keyPair.
     * @param pKeyStore the keyStore
     * @param pName the name
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    public void checkKeyPairSet(final GordianKeyStore pKeyStore,
                                final String pName,
                                final GordianKeyStorePairSet pKeyPairSet) throws OceanusException {
        Assertions.assertTrue(pKeyStore.containsAlias(pName));
        Assertions.assertTrue(pKeyStore.isKeyPairSetEntry(pName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(pName, GordianKeyStorePairSet.class));
        Assertions.assertEquals(pKeyPairSet, pKeyStore.getKeyPairSet(pName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPairSet, pKeyStore.getEntry(pName, DEF_PASSWORD));
    }

    /**
     * Build X500Name.
     * @param pName the CN value.
     */
    private X500Name buildX500Name(final String pName) throws OceanusException {
        /* Build the name */
        X500NameBuilder myBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        myBuilder.addRDN(BCStyle.CN, pName);
        myBuilder.addRDN(BCStyle.O, "jOceanus development");
        myBuilder.addRDN(BCStyle.L, "Romsey");
        myBuilder.addRDN(BCStyle.ST, "HANTS");
        myBuilder.addRDN(BCStyle.C, "UK");
        return myBuilder.build();
    }
}
