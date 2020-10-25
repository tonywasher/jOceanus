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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianStreamKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.cipher.GordianSymKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianGOSTElliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianLMSOtsType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianLMSKeySpec.GordianLMSSigType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianMcElieceKeySpec;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianMcElieceKeySpec.GordianMcElieceDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianQTESLAKeyType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSM2Elliptic;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianSPHINCSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCRMBuilder;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCRMParser;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCRMParser.GordianCRMIssuer;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStore;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject;
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
     * Create the keyStore test suite.
     * @return the test stream
     * @throws OceanusException on error
     */
    @TestFactory
    public Stream<DynamicNode> keyStoreTests() throws OceanusException {
        /* Create tests */
        Stream<DynamicNode> myStream = keyStoreTests(GordianFactoryType.BC);
        return Stream.concat(myStream, keyStoreTests(GordianFactoryType.JCA));
    }

    /**
     * Create the keySet test suite for a factoryType.
     * @param pFactoryType the factoryType
     * @return the test stream
     * @throws OceanusException on error
     */
    private Stream<DynamicNode> keyStoreTests(final GordianFactoryType pFactoryType) throws OceanusException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createFactory(pFactoryType);

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer(pFactoryType.toString(), Stream.of(
                DynamicTest.dynamicTest("symmetric", () -> symmetric(myFactory)),
                DynamicTest.dynamicTest("keyPair", () -> keyPairs(myFactory)),
                DynamicTest.dynamicTest("keyPairSet", () -> keyPairSets(myFactory)),
                DynamicTest.dynamicTest("keyPairRequest", () -> keyPairRequest(myFactory))
        )));
    }

    /**
     * test symmetric.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    private void symmetric(final GordianFactory pFactory) throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = pFactory.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore(myKeySetHashSpec);
        final GordianKeyStoreManager myMgr = myFactory.createKeyStoreManager(myStore);

        /* Create the keySet */
        final GordianKeyStoreSet mySet = myMgr.createKeySet(myKeySetSpec, KeyStoreAlias.KEYSET.getName(), DEF_PASSWORD);
        checkKeySet(myMgr, KeyStoreAlias.KEYSET, mySet);

        /* Create a symKey */
        final GordianKeyStoreKey<?> mySymKey = myMgr.createKey(GordianSymKeySpec.aes(myKeyLen), KeyStoreAlias.SYMKEY.getName(), DEF_PASSWORD);
        checkKey(myMgr, KeyStoreAlias.SYMKEY, mySymKey);

        /* Create a streamKey */
        final GordianKeyStoreKey<?> myStreamKey = myMgr.createKey(GordianStreamKeySpec.hc(myKeyLen), KeyStoreAlias.STREAMKEY.getName(), DEF_PASSWORD);
        checkKey(myMgr, KeyStoreAlias.STREAMKEY, myStreamKey);

        /* Create a macKey */
        final GordianKeyStoreKey<?> myMacKey = myMgr.createKey(GordianMacSpec.vmpcMac(myKeyLen), KeyStoreAlias.MACKEY.getName(), DEF_PASSWORD);
        checkKey(myMgr, KeyStoreAlias.MACKEY, myMacKey);

        /* Create keyStore documents */
        final ByteArrayOutputStream myZipStream = new ByteArrayOutputStream();
        myStore.storeToStream(myZipStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myZipStream.toByteArray());
        final GordianKeyStore myStore2 = myFactory.loadKeyStore(myInputStream, DEF_PASSWORD);
        Assertions.assertEquals(myStore, myStore2);

        /* delete the entries */
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
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    private void keyPairs(final GordianFactory pFactory) throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = pFactory.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore(myKeySetHashSpec);
        final GordianKeyStoreManager myMgr = myFactory.createKeyStoreManager(myStore);

        /* Create root certificates */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1);
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePair myRoot = myMgr.createRootKeyPair(mySpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name(KeyStoreAlias.ROOT2);
        final GordianKeyStorePair myRoot2 = myMgr.createRootKeyPair(mySpec, myRoot2Name, KeyStoreAlias.ROOT2.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.ROOT, myRoot);
        checkKeyPair(myMgr, KeyStoreAlias.ROOT2, myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePair myRootAlt = myMgr.createAlternate(myRoot, myUsage, myRoot2, KeyStoreAlias.ROOTALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePair myRoot2Alt = myMgr.createAlternate(myRoot2, myUsage, myRoot, KeyStoreAlias.ROOTALT2.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.ROOTALT, myRootAlt);
        checkKeyPair(myMgr, KeyStoreAlias.ROOTALT2, myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePair myIntermediate = myMgr.createKeyPair(mySpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name(KeyStoreAlias.INTER2);
        final GordianKeyStorePair myIntermediate2 = myMgr.createKeyPair(mySpec, myInter2Name, myUsage, myRoot2, KeyStoreAlias.INTER2.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.INTER, myIntermediate);
        checkKeyPair(myMgr, KeyStoreAlias.INTER2, myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair myIntermediateAlt = myMgr.createAlternate(myIntermediate, myUsage, myRoot2, KeyStoreAlias.INTERALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePair myIntermediate2Alt = myMgr.createAlternate(myIntermediate2, myUsage, myRoot, KeyStoreAlias.INTERALT2.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.INTERALT, myIntermediateAlt);
        checkKeyPair(myMgr, KeyStoreAlias.INTERALT2, myIntermediate2Alt);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair mySigner = myMgr.createKeyPair(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.SIGNER, mySigner);

        /* Create an agreement keyPair */
        final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePair myAgree = myMgr.createKeyPair(mySpec, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.AGREE, myAgree);

        /* Create an encryption keyPair */
        final X500Name myEncryptName = buildX500Name(KeyStoreAlias.ENCRYPT);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePair myEncrypt = myMgr.createKeyPair(mySpec, myEncryptName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
        checkKeyPair(myMgr, KeyStoreAlias.ENCRYPT, myEncrypt);

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
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    private void keyPairSets(final GordianFactory pFactory) throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = pFactory.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myFactory.createKeyStore(myKeySetHashSpec);
        final GordianKeyStoreManager myMgr = myFactory.createKeyStoreManager(myStore);

        /* Create root certificates */
        final GordianKeyPairSetSpec mySpec = GordianKeyPairSetSpec.SIGNLO;
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePairSet myRoot = myMgr.createRootKeyPairSet(mySpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name(KeyStoreAlias.ROOT2);
        final GordianKeyStorePairSet myRoot2 = myMgr.createRootKeyPairSet(mySpec, myRoot2Name, KeyStoreAlias.ROOT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.ROOT, myRoot);
        checkKeyPairSet(myMgr, KeyStoreAlias.ROOT2, myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePairSet myRootAlt = myMgr.createAlternate(myRoot, myUsage, myRoot2, KeyStoreAlias.ROOTALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePairSet myRoot2Alt = myMgr.createAlternate(myRoot2, myUsage, myRoot, KeyStoreAlias.ROOTALT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.ROOTALT, myRootAlt);
        checkKeyPairSet(myMgr, KeyStoreAlias.ROOTALT2, myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePairSet myIntermediate = myMgr.createKeyPairSet(mySpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name(KeyStoreAlias.INTER2);
        final GordianKeyStorePairSet myIntermediate2 = myMgr.createKeyPairSet(mySpec, myInter2Name, myUsage, myRoot2, KeyStoreAlias.INTER2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.INTER, myIntermediate);
        checkKeyPairSet(myMgr, KeyStoreAlias.INTER2, myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet myIntermediateAlt = myMgr.createAlternate(myIntermediate, myUsage, myRoot2, KeyStoreAlias.INTERALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePairSet myIntermediate2Alt = myMgr.createAlternate(myIntermediate2, myUsage, myRoot, KeyStoreAlias.INTERALT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.INTERALT, myIntermediateAlt);
        checkKeyPairSet(myMgr, KeyStoreAlias.INTERALT2, myIntermediate2Alt);

        /* Create a signature keyPairSet */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet mySigner = myMgr.createKeyPairSet(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.SIGNER, mySigner);

        /* Create an agreement keyPairSet */
        final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePairSet myAgree = myMgr.createKeyPairSet(GordianKeyPairSetSpec.AGREELO, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.AGREE, myAgree);

        /* Create an encryption keyPairSet */
        final X500Name myEncryptName = buildX500Name(KeyStoreAlias.ENCRYPT);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePairSet myEncrypt = myMgr.createKeyPairSet(GordianKeyPairSetSpec.ENCRYPT, myEncryptName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
        checkKeyPairSet(myMgr, KeyStoreAlias.ENCRYPT, myEncrypt);

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
     * test keyPairs.
     * @param pFactory the factory
     * @throws OceanusException on error
     */
    private void keyPairRequest(final GordianFactory pFactory) throws OceanusException {
        /* Set up test parameters */
        final GordianLength myKeyLen = GordianLength.LEN_256;
        final GordianKeySetSpec myKeySetSpec = new GordianKeySetSpec(myKeyLen);
        final GordianKeySetHashSpec myKeySetHashSpec = new GordianKeySetHashSpec(myKeySetSpec);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myFactory = pFactory.getKeyPairFactory().getKeyStoreFactory();
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) myFactory.createKeyStore(myKeySetHashSpec);
        final GordianCoreKeyStoreManager myMgr = (GordianCoreKeyStoreManager) myFactory.createKeyStoreManager(myStore);

        /* Create signer keyPairSpecs */
        final GordianKeyPairSpec myRSASpec = GordianKeyPairSpec.rsa(GordianRSAModulus.MOD2048);
        final GordianKeyPairSpec myECSpec = GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1);
        final List<GordianKeyPairSpec> mySignSpecs = new ArrayList<>();
        mySignSpecs.add(myRSASpec);
        mySignSpecs.add(myECSpec);
        mySignSpecs.add(GordianKeyPairSpec.dsa(GordianDSAKeyType.MOD2048));
        mySignSpecs.add(GordianKeyPairSpec.ed25519());
        mySignSpecs.add(GordianKeyPairSpec.ed448());
        mySignSpecs.add(GordianKeyPairSpec.gost2012(GordianGOSTElliptic.GOST512A));
        mySignSpecs.add(GordianKeyPairSpec.dstu4145(GordianDSTU4145Elliptic.DSTU9));
        mySignSpecs.add(GordianKeyPairSpec.sm2(GordianSM2Elliptic.SM2P256V1));
        mySignSpecs.add(GordianKeyPairSpec.sphincs(GordianSPHINCSDigestType.SHA2));
        mySignSpecs.add(GordianKeyPairSpec.rainbow());
        mySignSpecs.add(GordianKeyPairSpec.qTESLA(GordianQTESLAKeyType.PROVABLY_SECURE_III));
        mySignSpecs.add(GordianKeyPairSpec.xmss(GordianXMSSDigestType.SHA512, GordianXMSSHeight.H10));
        mySignSpecs.add(GordianKeyPairSpec.lms(GordianLMSKeySpec.keySpec(GordianLMSSigType.H5, GordianLMSOtsType.W1)));

        final List<GordianKeyPairSpec> myEncSpecs = new ArrayList<>();
        myEncSpecs.add(GordianKeyPairSpec.mcEliece(GordianMcElieceKeySpec.standard()));
        myEncSpecs.add(GordianKeyPairSpec.mcEliece(GordianMcElieceKeySpec.cca2(GordianMcElieceDigestType.SHA512)));
        myEncSpecs.add(GordianKeyPairSpec.elGamal(GordianDHGroup.FFDHE2048));

        final List<GordianKeyPairSpec> myAgreeSpecs = new ArrayList<>();
        myAgreeSpecs.add(GordianKeyPairSpec.newHope());
        myAgreeSpecs.add(GordianKeyPairSpec.dh(GordianDHGroup.FFDHE2048));
        myAgreeSpecs.add(GordianKeyPairSpec.x25519());
        myAgreeSpecs.add(GordianKeyPairSpec.x448());

        /* Create root certificate */
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePair myRoot = myMgr.createRootKeyPair(myRSASpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);

        /* Create intermediate */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePair myIntermediate = myMgr.createKeyPair(myECSpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);

        /* Create certifier */
        final X500Name myCertifierName = buildX500Name(KeyStoreAlias.CERTIFIER);
        final GordianKeyStorePair myCertifier = myMgr.createKeyPair(myECSpec, myCertifierName, myUsage, myRoot, KeyStoreAlias.CERTIFIER.getName(), DEF_PASSWORD);

        /* Create the issuer callback */
        final GordianCRMIssuer myIssuer = s -> {
            final String myAlias = myStore.findIssuerKeyPairCert(s);
            return (GordianKeyStorePair) myStore.getEntry(myAlias, DEF_PASSWORD);
        };

        /* For each signSpec */
        for (GordianKeyPairSpec mySpec : mySignSpecs) {
            /* Create a signature keyPair */
            final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
            myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
            final GordianKeyStorePair mySigner = myMgr.createKeyPair(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
            final GordianCRMBuilder myBuilder = new GordianCRMBuilder((GordianCoreFactory) pFactory, null);
            final GordianPEMObject myRequest = myBuilder.createCertificateRequest(mySigner);
            final GordianCRMParser myParser = new GordianCRMParser(myMgr, myCertifier, null);
            myParser.decodeCertificateRequest(myRequest);
            myStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
        }

        /* For each encSpec */
        for (GordianKeyPairSpec mySpec : myEncSpecs) {
            /* Create an encryption keyPair */
            final X500Name myEncName = buildX500Name(KeyStoreAlias.ENCRYPT);
            myUsage = new GordianKeyPairUsage(GordianKeyPairUse.KEYENCRYPT, GordianKeyPairUse.DATAENCRYPT);
            final GordianKeyStorePair myEnc = myMgr.createKeyPair(mySpec, myEncName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
            final X500Name myMatchName = buildX500Name(KeyStoreAlias.MATCH);
            final GordianKeyStorePair myMatch = myMgr.createKeyPair(mySpec, myMatchName, myUsage, myIntermediate, KeyStoreAlias.MATCH.getName(), DEF_PASSWORD);
            final GordianCoreKeyPairCertificate myMatchCert = (GordianCoreKeyPairCertificate) myMatch.getCertificateChain().get(0);
            final GordianCRMBuilder myBuilder = new GordianCRMBuilder((GordianCoreFactory) pFactory, myMatchCert);
            final GordianPEMObject myRequest = myBuilder.createCertificateRequest(myEnc);
            final GordianCRMParser myParser = new GordianCRMParser(myMgr, myCertifier, myIssuer);
            myParser.decodeCertificateRequest(myRequest);
            myStore.deleteEntry(KeyStoreAlias.ENCRYPT.getName());
            myStore.deleteEntry(KeyStoreAlias.MATCH.getName());
        }

        /* For each agreementSpec */
        for (GordianKeyPairSpec mySpec : myAgreeSpecs) {
            /* Create an agreement keyPair */
            final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
            myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
            final GordianKeyStorePair myAgree = myMgr.createKeyPair(mySpec, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
            final X500Name myMatchName = buildX500Name(KeyStoreAlias.MATCH);
            final GordianKeyStorePair myMatch = myMgr.createKeyPair(mySpec, myMatchName, myUsage, myIntermediate, KeyStoreAlias.MATCH.getName(), DEF_PASSWORD);
            final GordianCoreKeyPairCertificate myMatchCert = (GordianCoreKeyPairCertificate) myMatch.getCertificateChain().get(0);
            final GordianCRMBuilder myBuilder = new GordianCRMBuilder((GordianCoreFactory) pFactory, myMatchCert);
            final GordianPEMObject myRequest = myBuilder.createCertificateRequest(myAgree);
            final GordianCRMParser myParser = new GordianCRMParser(myMgr, myCertifier, myIssuer);
            myParser.decodeCertificateRequest(myRequest);
            myStore.deleteEntry(KeyStoreAlias.AGREE.getName());
            myStore.deleteEntry(KeyStoreAlias.MATCH.getName());
        }

        /* delete the entries */
        myStore.deleteEntry(KeyStoreAlias.ROOT.getName());
        myStore.deleteEntry(KeyStoreAlias.INTER.getName());
        myStore.deleteEntry(KeyStoreAlias.CERTIFIER.getName());

        /* Check that we have deleted all values */
        int mySize = myStore.size();
        Assertions.assertEquals(0, mySize);
    }

    /**
     * check keySet.
     * @param pManager the keyStoreManager
     * @param pAlias the alias
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private static void checkKeySet(final GordianKeyStoreManager pManager,
                                    final KeyStoreAlias pAlias,
                                    final GordianKeyStoreSet pKeySet) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pManager.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeySetEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStoreSet.class));
        Assertions.assertEquals(pKeySet.getKeySet(), myStore.getKeySet(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeySet, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pManager, pAlias, pKeySet);
    }

    /**
     * check key.
     * @param pManager the keyStoreManager
     * @param pAlias the alias
     * @param pKey the key
     * @throws OceanusException on error
     */
    private static void checkKey(final GordianKeyStoreManager pManager,
                                 final KeyStoreAlias pAlias,
                                 final GordianKeyStoreKey<?> pKey) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pManager.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStoreKey.class));
        Assertions.assertEquals(pKey.getKey(), myStore.getKey(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKey, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pManager, pAlias, pKey);
    }

    /**
     * check keyPair.
     * @param pManager the keyStoreManager
     * @param pAlias the alias
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private static void checkKeyPair(final GordianKeyStoreManager pManager,
                                     final KeyStoreAlias pAlias,
                                     final GordianKeyStorePair pKeyPair) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pManager.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyPairEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStorePair.class));
        Assertions.assertEquals(pKeyPair.getKeyPair(), myStore.getKeyPair(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPair.getCertificateChain(), myStore.getKeyPairCertificateChain(myName));
        Assertions.assertEquals(pKeyPair, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pManager, pAlias, pKeyPair);
    }

    /**
     * check keyPairSet.
     * @param pManager the keyStoreManager
     * @param pAlias the alias
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    private static void checkKeyPairSet(final GordianKeyStoreManager pManager,
                                        final KeyStoreAlias pAlias,
                                        final GordianKeyStorePairSet pKeyPairSet) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pManager.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyPairSetEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStorePairSet.class));
        Assertions.assertEquals(pKeyPairSet.getKeyPairSet(), myStore.getKeyPairSet(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPairSet.getCertificateChain(), myStore.getKeyPairSetCertificateChain(myName));
        Assertions.assertEquals(pKeyPairSet, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pManager, pAlias, pKeyPairSet);
    }

    /**
     * check export.
     * @param pManager the keyStoreManager
     * @param pAlias the alias
     * @param pEntry the entry
     * @throws OceanusException on error
     */
    private static void checkExport(final GordianKeyStoreManager pManager,
                                    final KeyStoreAlias pAlias,
                                    final GordianKeyStoreEntry pEntry) throws OceanusException {
        final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
        pManager.exportEntry(pAlias.getName(), myOutStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        Assertions.assertEquals(pEntry, pManager.importEntry(myInputStream, DEF_PASSWORD));
    }

    /**
     * Build X500Name.
     * @param pAlias the Alias
     */
    private static X500Name buildX500Name(final KeyStoreAlias pAlias) {
        /* Build the name */
        X500NameBuilder myBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        myBuilder.addRDN(BCStyle.CN, pAlias.getName());
        myBuilder.addRDN(BCStyle.OU, "jOceanus development");
        myBuilder.addRDN(BCStyle.O, "jOceanus");
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
         * Certifier Certificate.
         */
        CERTIFIER("Certifier Certificate"),

        /**
         * Match Certificate.
         */
        MATCH("Match Certificate"),

        /**
         * KeySet.
         */
        KEYSET("KeySet"),

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
