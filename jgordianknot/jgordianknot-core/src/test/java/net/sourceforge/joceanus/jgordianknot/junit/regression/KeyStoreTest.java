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
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetFactory;
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
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreManager;
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
        final GordianKeyStoreManager myMgr = myFactory.createKeyStoreManager(myStore);

        /* Create a keySetHash */
        final GordianKeySetFactory mySetFactory = FACTORY.getKeySetFactory();
        final GordianKeyStoreHash myHash = myMgr.createKeySetHash(myKeySetHashSpec, DEF_PASSWORD, KeyStoreAlias.KEYSETHASH.getName());
        checkKeySetHash(myStore, KeyStoreAlias.KEYSETHASH, myHash);

        /* Create the keySet */
        final GordianKeyStoreSet mySet = myMgr.createKeySet(myKeySetSpec, KeyStoreAlias.KEYSET.getName(), DEF_PASSWORD);
        checkKeySet(myStore, KeyStoreAlias.KEYSET, mySet);

        /* Create a symKey */
        final GordianKeyStoreKey<?> mySymKey = myMgr.createKey(GordianSymKeySpec.aes(myKeyLen), KeyStoreAlias.SYMKEY.getName(), DEF_PASSWORD);
        checkKey(myStore, KeyStoreAlias.SYMKEY, mySymKey);

        /* Create a streamKey */
        final GordianKeyStoreKey<?> myStreamKey = myMgr.createKey(GordianStreamKeySpec.hc(myKeyLen), KeyStoreAlias.STREAMKEY.getName(), DEF_PASSWORD);
        checkKey(myStore, KeyStoreAlias.STREAMKEY, myStreamKey);

        /* Create a macKey */
        final GordianKeyStoreKey<?> myMacKey = myMgr.createKey(GordianMacSpec.vmpcMac(myKeyLen), KeyStoreAlias.MACKEY.getName(), DEF_PASSWORD);
        checkKey(myStore, KeyStoreAlias.MACKEY, myMacKey);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
        myStore.deleteEntry(KeyStoreAlias.KEYSETHASH.getName());
        myStore.deleteEntry(KeyStoreAlias.KEYSET.getName());
        myStore.deleteEntry(KeyStoreAlias.SYMKEY.getName());
        myStore.deleteEntry(KeyStoreAlias.STREAMKEY.getName());
        myStore.deleteEntry(KeyStoreAlias.MACKEY.getName());

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
        final GordianKeyStoreManager myMgr = myFactory.createKeyStoreManager(myStore);

        /* Create root certificates */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1);
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePair myRoot = myMgr.createRootKeyPair(mySpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name(KeyStoreAlias.ROOT2);
        final GordianKeyStorePair myRoot2 = myMgr.createRootKeyPair(mySpec, myRoot2Name, KeyStoreAlias.ROOT2.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.ROOT, myRoot);
        checkKeyPair(myStore, KeyStoreAlias.ROOT2, myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePair myRootAlt = myMgr.createAlternate(myRoot, myUsage, myRoot2, KeyStoreAlias.ROOTALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePair myRoot2Alt = myMgr.createAlternate(myRoot2, myUsage, myRoot, KeyStoreAlias.ROOTALT2.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.ROOTALT, myRootAlt);
        checkKeyPair(myStore, KeyStoreAlias.ROOTALT2, myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePair myIntermediate = myMgr.createKeyPair(mySpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name(KeyStoreAlias.INTER2);
        final GordianKeyStorePair myIntermediate2 = myMgr.createKeyPair(mySpec, myInter2Name, myUsage, myRoot2, KeyStoreAlias.INTER2.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.INTER, myIntermediate);
        checkKeyPair(myStore, KeyStoreAlias.INTER2, myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair myIntermediateAlt = myMgr.createAlternate(myIntermediate, myUsage, myRoot2, KeyStoreAlias.INTERALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePair myIntermediate2Alt = myMgr.createAlternate(myIntermediate2, myUsage, myRoot, KeyStoreAlias.INTERALT2.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.INTERALT, myIntermediateAlt);
        checkKeyPair(myStore, KeyStoreAlias.INTERALT2, myIntermediate2Alt);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair mySigner = myMgr.createKeyPair(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.SIGNER, mySigner);

        /* Create an agreement keyPair */
        final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePair myAgree = myMgr.createKeyPair(mySpec, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.AGREE, myAgree);

        /* Create an encryption keyPair */
        final X500Name myEncryptName = buildX500Name(KeyStoreAlias.ENCRYPT);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePair myEncrypt = myMgr.createKeyPair(mySpec, myEncryptName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
        checkKeyPair(myStore, KeyStoreAlias.ENCRYPT, myEncrypt);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
        myStore.deleteEntry(KeyStoreAlias.ROOT.getName());
        myStore.deleteEntry(KeyStoreAlias.ROOT2.getName());
        myStore.deleteEntry(KeyStoreAlias.ROOTALT.getName());
        myStore.deleteEntry(KeyStoreAlias.ROOTALT2.getName());
        myStore.deleteEntry(KeyStoreAlias.INTER.getName());
        myStore.deleteEntry(KeyStoreAlias.INTER2.getName());
        myStore.deleteEntry(KeyStoreAlias.INTERALT.getName());
        myStore.deleteEntry(KeyStoreAlias.INTERALT2.getName());
        myStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
        myStore.deleteEntry(KeyStoreAlias.AGREE.getName());
        myStore.deleteEntry(KeyStoreAlias.ENCRYPT.getName());

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
        final GordianKeyStoreManager myMgr = myFactory.createKeyStoreManager(myStore);

        /* Create root certificates */
        final GordianKeyPairSetSpec mySpec = GordianKeyPairSetSpec.SIGNLO;
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePairSet myRoot = myMgr.createRootKeyPairSet(mySpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name(KeyStoreAlias.ROOT2);
        final GordianKeyStorePairSet myRoot2 = myMgr.createRootKeyPairSet(mySpec, myRoot2Name, KeyStoreAlias.ROOT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.ROOT, myRoot);
        checkKeyPairSet(myStore, KeyStoreAlias.ROOT2, myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePairSet myRootAlt = myMgr.createAlternate(myRoot, myUsage, myRoot2, KeyStoreAlias.ROOTALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePairSet myRoot2Alt = myMgr.createAlternate(myRoot2, myUsage, myRoot, KeyStoreAlias.ROOTALT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.ROOTALT, myRootAlt);
        checkKeyPairSet(myStore, KeyStoreAlias.ROOTALT2, myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePairSet myIntermediate = myMgr.createKeyPairSet(mySpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name(KeyStoreAlias.INTER2);
        final GordianKeyStorePairSet myIntermediate2 = myMgr.createKeyPairSet(mySpec, myInter2Name, myUsage, myRoot2, KeyStoreAlias.INTER2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.INTER, myIntermediate);
        checkKeyPairSet(myStore, KeyStoreAlias.INTER2, myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet myIntermediateAlt = myMgr.createAlternate(myIntermediate, myUsage, myRoot2, KeyStoreAlias.INTERALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePairSet myIntermediate2Alt = myMgr.createAlternate(myIntermediate2, myUsage, myRoot, KeyStoreAlias.INTERALT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.INTERALT, myIntermediateAlt);
        checkKeyPairSet(myStore, KeyStoreAlias.INTERALT2, myIntermediate2Alt);

        /* Create a signature keyPairSet */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet mySigner = myMgr.createKeyPairSet(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.SIGNER, mySigner);

        /* Create an agreement keyPairSet */
        final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePairSet myAgree = myMgr.createKeyPairSet(GordianKeyPairSetSpec.AGREELO, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.AGREE, myAgree);

        /* Create an encryption keyPairSet */
        final X500Name myEncryptName = buildX500Name(KeyStoreAlias.ENCRYPT);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePairSet myEncrypt = myMgr.createKeyPairSet(GordianKeyPairSetSpec.ENCRYPT, myEncryptName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
        checkKeyPairSet(myStore, KeyStoreAlias.ENCRYPT, myEncrypt);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
        myStore.deleteEntry(KeyStoreAlias.ROOT.getName());
        myStore.deleteEntry(KeyStoreAlias.ROOT2.getName());
        myStore.deleteEntry(KeyStoreAlias.ROOTALT.getName());
        myStore.deleteEntry(KeyStoreAlias.ROOTALT2.getName());
        myStore.deleteEntry(KeyStoreAlias.INTER.getName());
        myStore.deleteEntry(KeyStoreAlias.INTER2.getName());
        myStore.deleteEntry(KeyStoreAlias.INTERALT.getName());
        myStore.deleteEntry(KeyStoreAlias.INTERALT2.getName());
        myStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
        myStore.deleteEntry(KeyStoreAlias.AGREE.getName());
        myStore.deleteEntry(KeyStoreAlias.ENCRYPT.getName());

        /* Check that we have deleted all values */
        int mySize = myStore.size();
        Assertions.assertEquals(0, mySize);
    }

    /**
     * check keySetHash.
     * @param pKeyStore the keyStore
     * @param pAlias the alias
     * @param pKeyHash the keyHash
     * @throws OceanusException on error
     */
    public void checkKeySetHash(final GordianKeyStore pKeyStore,
                                final KeyStoreAlias pAlias,
                                final GordianKeyStoreHash pKeyHash) throws OceanusException {
        final String myName = pAlias.getName();
        Assertions.assertTrue(pKeyStore.containsAlias(myName));
        Assertions.assertTrue(pKeyStore.isKeySetHashEntry(myName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(myName, GordianKeyStoreHash.class));
        Assertions.assertEquals(pKeyHash.getKeySetHash(), pKeyStore.getKeySetHash(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyHash, pKeyStore.getEntry(myName, DEF_PASSWORD));
    }

    /**
     * check keySet.
     * @param pKeyStore the keyStore
     * @param pAlias the alias
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    public void checkKeySet(final GordianKeyStore pKeyStore,
                            final KeyStoreAlias pAlias,
                            final GordianKeyStoreSet pKeySet) throws OceanusException {
        final String myName = pAlias.getName();
        Assertions.assertTrue(pKeyStore.containsAlias(myName));
        Assertions.assertTrue(pKeyStore.isKeySetEntry(myName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(myName, GordianKeyStoreSet.class));
        Assertions.assertEquals(pKeySet.getKeySet(), pKeyStore.getKeySet(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeySet, pKeyStore.getEntry(myName, DEF_PASSWORD));
    }

    /**
     * check key.
     * @param pKeyStore the keyStore
     * @param pAlias the alias
     * @param pKey the key
     * @throws OceanusException on error
     */
    public void checkKey(final GordianKeyStore pKeyStore,
                         final KeyStoreAlias pAlias,
                         final GordianKeyStoreKey<?> pKey) throws OceanusException {
        final String myName = pAlias.getName();
        Assertions.assertTrue(pKeyStore.containsAlias(myName));
        Assertions.assertTrue(pKeyStore.isKeyEntry(myName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(myName, GordianKeyStoreKey.class));
        Assertions.assertEquals(pKey.getKey(), pKeyStore.getKey(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKey, pKeyStore.getEntry(myName, DEF_PASSWORD));
    }

    /**
     * check keyPair.
     * @param pKeyStore the keyStore
     * @param pAlias the alias
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    public void checkKeyPair(final GordianKeyStore pKeyStore,
                             final KeyStoreAlias pAlias,
                             final GordianKeyStorePair pKeyPair) throws OceanusException {
        final String myName = pAlias.getName();
        Assertions.assertTrue(pKeyStore.containsAlias(myName));
        Assertions.assertTrue(pKeyStore.isKeyPairEntry(myName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(myName, GordianKeyStorePair.class));
        Assertions.assertEquals(pKeyPair.getKeyPair(), pKeyStore.getKeyPair(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPair.getCertificateChain(), pKeyStore.getKeyPairCertificateChain(myName));
        Assertions.assertEquals(pKeyPair, pKeyStore.getEntry(myName, DEF_PASSWORD));
    }

    /**
     * check keyPair.
     * @param pKeyStore the keyStore
     * @param pAlias the alias
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    public void checkKeyPairSet(final GordianKeyStore pKeyStore,
                                final KeyStoreAlias pAlias,
                                final GordianKeyStorePairSet pKeyPairSet) throws OceanusException {
        final String myName = pAlias.getName();
        Assertions.assertTrue(pKeyStore.containsAlias(myName));
        Assertions.assertTrue(pKeyStore.isKeyPairSetEntry(myName));
        Assertions.assertTrue(pKeyStore.entryInstanceOf(myName, GordianKeyStorePairSet.class));
        Assertions.assertEquals(pKeyPairSet.getKeyPairSet(), pKeyStore.getKeyPairSet(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPairSet.getCertificateChain(), pKeyStore.getKeyPairSetCertificateChain(myName));
        Assertions.assertEquals(pKeyPairSet, pKeyStore.getEntry(myName, DEF_PASSWORD));
    }

    /**
     * Build X500Name.
     * @param pAlias the Alias
     */
    private X500Name buildX500Name(final KeyStoreAlias pAlias) {
        /* Build the name */
        X500NameBuilder myBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        myBuilder.addRDN(BCStyle.CN, pAlias.getName());
        myBuilder.addRDN(BCStyle.O, "jOceanus development");
        myBuilder.addRDN(BCStyle.L, "Romsey");
        myBuilder.addRDN(BCStyle.ST, "HANTS");
        myBuilder.addRDN(BCStyle.C, "UK");
        return myBuilder.build();
    }

    /**
     * Aliases.
     */
    enum KeyStoreAlias {
        /**
         * Root Certificate.
         */
        ROOT("Root Certificate"),

        /**
         * Root Certificate 2.
         */
        ROOT2("Root Certificate 2"),

        /**
         * Root Certificate Alt.
         */
        ROOTALT("Root Certificate Alt"),

        /**
         * Root Certificate 2 Alt.
         */
        ROOTALT2("Root Certificate 2 Alt"),

        /**
         * Intermediate Certificate.
         */
        INTER("Intermediate Certificate"),

        /**
         * Intermediate Certificate 2.
         */
        INTER2("Intermediate Certificate 2"),

        /**
         * Intermediate Certificate Alt.
         */
        INTERALT("Intermediate Certificate Alt"),

        /**
         * Root Certificate.
         */
        INTERALT2("Intermediate Certificate 2 Alt"),

        /**
         * Signing Certificate.
         */
        SIGNER("Signing Certificate"),

        /**
         * Agreement Certificate.
         */
        AGREE("Agreement Certificate"),

        /**
         * Encrypt Certificate.
         */
        ENCRYPT("Encrypt Certificate"),

        /**
         * KeySet.
         */
        KEYSET("KeySet"),

        /**
         * KeySetHash.
         */
        KEYSETHASH("KeySetHash"),

        /**
         * SymKey.
         */
        SYMKEY("SymKey"),

        /**
         * StreamKey.
         */
        STREAMKEY("StreamKey"),

        /**
         * MacKey.
         */
        MACKEY("MacKey");

        /**
         * Name.
         */
        private final String theName;

        /**
         * Constructor.
         * @param pName the name
         */
        KeyStoreAlias(final String pName) {
            theName = pName;
        }

        /**
         * Obtain the name.
         * @return the name
         */
        String getName() {
            return theName;
        }
    }
}
