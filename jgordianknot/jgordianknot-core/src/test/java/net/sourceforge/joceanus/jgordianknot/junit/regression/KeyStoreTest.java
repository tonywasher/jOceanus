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
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreGateway;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCRMBuilder;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStore;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreEntry.GordianCoreKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCoreKeyStoreManager;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyPairCRMParser;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianKeyPairSetCRMParser;
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
     * The testKey length.
     */
    private static final GordianLength KEYLEN = GordianLength.LEN_256;

    /**
     * The KeySetSpec.
     */
    private static final GordianKeySetSpec KEYSETSPEC = new GordianKeySetSpec(KEYLEN);

    /**
     * The KeySetHashSpec.
     */
    private static final GordianKeySetHashSpec KEYSETHASHSPEC = new GordianKeySetHashSpec(KEYSETSPEC);

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

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myKSFactory = myFactory.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myKSFactory.createKeyStore(KEYSETHASHSPEC);
        final GordianKeyStoreManager myMgr = myKSFactory.createKeyStoreManager(myStore);

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer(pFactoryType.toString(), Stream.of(
                DynamicTest.dynamicTest("symmetric", () -> symmetric(myMgr)),
                DynamicTest.dynamicTest("keyPair", () -> keyPairs(myMgr)),
                DynamicTest.dynamicTest("keyPairSet", () -> keyPairSets(myMgr)),
                keyPairRequestTest(myMgr),
                keyPairSetRequestTest(myMgr)
        )));
    }

    /**
     * create keyPairRequests test stream.
     * @param pManager the keyStoreManager
     * @return the test stream
     * @throws OceanusException on error
     */
    private DynamicNode keyPairRequestTest(final GordianKeyStoreManager pManager) throws OceanusException {
        /* Create state */
        final KeyPairCertReqState myState = new KeyPairCertReqState(pManager);

        return DynamicContainer.dynamicContainer("keyPairRequest", Stream.of(
            DynamicTest.dynamicTest("Initialise", myState::initialise),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.rsa(GordianRSAModulus.MOD2048)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.dsa(GordianDSAKeyType.MOD2048)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.ed25519()),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.ed448()),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.gost2012(GordianGOSTElliptic.GOST512A)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.dstu4145(GordianDSTU4145Elliptic.DSTU9)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.sm2(GordianSM2Elliptic.SM2P256V1)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.sphincs(GordianSPHINCSDigestType.SHA2)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.rainbow()),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.qTESLA(GordianQTESLAKeyType.PROVABLY_SECURE_III)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.xmss(GordianXMSSDigestType.SHA512, GordianXMSSHeight.H10)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpec.lms(GordianLMSKeySpec.keySpec(GordianLMSSigType.H5, GordianLMSOtsType.W1))),
            encryptedKeyPairRequestTest(myState, GordianKeyPairSpec.mcEliece(GordianMcElieceKeySpec.standard())),
            encryptedKeyPairRequestTest(myState, GordianKeyPairSpec.mcEliece(GordianMcElieceKeySpec.cca2(GordianMcElieceDigestType.SHA512))),
            encryptedKeyPairRequestTest(myState, GordianKeyPairSpec.elGamal(GordianDHGroup.FFDHE2048)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpec.newHope()),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpec.dh(GordianDHGroup.FFDHE2048)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpec.x25519()),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpec.x448()),
            DynamicTest.dynamicTest("Cleanup", myState::cleanUp)
        ));
    }

    /**
     * create signed keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     * @throws OceanusException on error
     */
    private static DynamicNode signedKeyPairRequestTest(final KeyPairCertReqState pState,
                                                        final GordianKeyPairSpec pSpec) throws OceanusException {
        /* Create test */
        return DynamicTest.dynamicTest(pSpec.toString(), () -> signedKeyPairRequest(pState, pSpec));
    }

    /**
     * create encrypted keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     * @throws OceanusException on error
     */
    private static DynamicNode encryptedKeyPairRequestTest(final KeyPairCertReqState pState,
                                                           final GordianKeyPairSpec pSpec) throws OceanusException {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.KEYENCRYPT, GordianKeyPairUse.DATAENCRYPT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairRequest(pState, pSpec, myUsage));
    }

    /**
     * create agreed keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     * @throws OceanusException on error
     */
    private static DynamicNode agreedKeyPairRequestTest(final KeyPairCertReqState pState,
                                                        final GordianKeyPairSpec pSpec) throws OceanusException {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairRequest(pState, pSpec, myUsage));
    }

    /**
     * create keyPairSetRequests test stream.
     * @param pManager the keyStoreManager
     * @return the test stream
     * @throws OceanusException on error
     */
    private DynamicNode keyPairSetRequestTest(final GordianKeyStoreManager pManager) throws OceanusException {
        /* Create state */
        final KeyPairSetCertReqState myState = new KeyPairSetCertReqState(pManager);

        return DynamicContainer.dynamicContainer("keyPairSetRequest", Stream.of(
                DynamicTest.dynamicTest("Initialise", myState::initialise),
                signedKeyPairSetRequestTest(myState, GordianKeyPairSetSpec.SIGNLO),
                signedKeyPairSetRequestTest(myState, GordianKeyPairSetSpec.SIGNHI),
                encryptedKeyPairSetRequestTest(myState, GordianKeyPairSetSpec.ENCRYPT),
                agreedKeyPairSetRequestTest(myState, GordianKeyPairSetSpec.AGREELO),
                agreedKeyPairSetRequestTest(myState, GordianKeyPairSetSpec.AGREEHI),
                DynamicTest.dynamicTest("Cleanup", myState::cleanUp)
        ));
    }

    /**
     * create signed keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     * @throws OceanusException on error
     */
    private static DynamicNode signedKeyPairSetRequestTest(final KeyPairSetCertReqState pState,
                                                           final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create test */
        return DynamicTest.dynamicTest(pSpec.toString(), () -> signedKeyPairSetRequest(pState, pSpec));
    }

    /**
     * create encrypted keyPairSetRequest test for a keyPairSetSpec.
     * @param pState the state
     * @param pSpec the keyPairSetSpec
     * @return the test
     * @throws OceanusException on error
     */
    private static DynamicNode encryptedKeyPairSetRequestTest(final KeyPairSetCertReqState pState,
                                                              final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.KEYENCRYPT, GordianKeyPairUse.DATAENCRYPT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairSetRequest(pState, pSpec, myUsage));
    }

    /**
     * create agreed keyPairSetRequest test for a keyPairSetSpec.
     * @param pState the state
     * @param pSpec the keyPairSetSpec
     * @return the test
     * @throws OceanusException on error
     */
    private static DynamicNode agreedKeyPairSetRequestTest(final KeyPairSetCertReqState pState,
                                                           final GordianKeyPairSetSpec pSpec) throws OceanusException {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairSetRequest(pState, pSpec, myUsage));
    }

    /**
     * test symmetric.
     * @param pManager the keyStoreManager
     * @throws OceanusException on error
     */
    private void symmetric(final GordianKeyStoreManager pManager) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pManager.getKeyStore();
        final GordianKeyStoreFactory myFactory = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStoreGateway myGateway = myFactory.createKeyStoreGateway(pManager);

        /* Create the keySet */
        final GordianKeyStoreSet mySet = pManager.createKeySet(KEYSETSPEC, KeyStoreAlias.KEYSET.getName(), DEF_PASSWORD);
        checkKeySet(myGateway, KeyStoreAlias.KEYSET, mySet);

        /* Create a symKey */
        final GordianKeyStoreKey<?> mySymKey = pManager.createKey(GordianSymKeySpec.aes(KEYLEN), KeyStoreAlias.SYMKEY.getName(), DEF_PASSWORD);
        checkKey(myGateway, KeyStoreAlias.SYMKEY, mySymKey);

        /* Create a streamKey */
        final GordianKeyStoreKey<?> myStreamKey = pManager.createKey(GordianStreamKeySpec.hc(KEYLEN), KeyStoreAlias.STREAMKEY.getName(), DEF_PASSWORD);
        checkKey(myGateway, KeyStoreAlias.STREAMKEY, myStreamKey);

        /* Create a macKey */
        final GordianKeyStoreKey<?> myMacKey = pManager.createKey(GordianMacSpec.vmpcMac(KEYLEN), KeyStoreAlias.MACKEY.getName(), DEF_PASSWORD);
        checkKey(myGateway, KeyStoreAlias.MACKEY, myMacKey);

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
     * @param pManager the keyStoreManager
     * @throws OceanusException on error
     */
    private void keyPairs(final GordianKeyStoreManager pManager) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pManager.getKeyStore();
        final GordianKeyStoreFactory myFactory = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStoreGateway myGateway = myFactory.createKeyStoreGateway(pManager);

        /* Create root certificates */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1);
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePair myRoot = pManager.createRootKeyPair(mySpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name(KeyStoreAlias.ROOT2);
        final GordianKeyStorePair myRoot2 = pManager.createRootKeyPair(mySpec, myRoot2Name, KeyStoreAlias.ROOT2.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.ROOT, myRoot);
        checkKeyPair(myGateway, KeyStoreAlias.ROOT2, myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePair myRootAlt = pManager.createAlternate(myRoot, myUsage, myRoot2, KeyStoreAlias.ROOTALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePair myRoot2Alt = pManager.createAlternate(myRoot2, myUsage, myRoot, KeyStoreAlias.ROOTALT2.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.ROOTALT, myRootAlt);
        checkKeyPair(myGateway, KeyStoreAlias.ROOTALT2, myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePair myIntermediate = pManager.createKeyPair(mySpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name(KeyStoreAlias.INTER2);
        final GordianKeyStorePair myIntermediate2 = pManager.createKeyPair(mySpec, myInter2Name, myUsage, myRoot2, KeyStoreAlias.INTER2.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.INTER, myIntermediate);
        checkKeyPair(myGateway, KeyStoreAlias.INTER2, myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair myIntermediateAlt = pManager.createAlternate(myIntermediate, myUsage, myRoot2, KeyStoreAlias.INTERALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePair myIntermediate2Alt = pManager.createAlternate(myIntermediate2, myUsage, myRoot, KeyStoreAlias.INTERALT2.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.INTERALT, myIntermediateAlt);
        checkKeyPair(myGateway, KeyStoreAlias.INTERALT2, myIntermediate2Alt);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePair mySigner = pManager.createKeyPair(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.SIGNER, mySigner);

        /* Create an agreement keyPair */
        final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePair myAgree = pManager.createKeyPair(mySpec, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.AGREE, myAgree);

        /* Create an encryption keyPair */
        final X500Name myEncryptName = buildX500Name(KeyStoreAlias.ENCRYPT);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePair myEncrypt = pManager.createKeyPair(mySpec, myEncryptName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
        checkKeyPair(myGateway, KeyStoreAlias.ENCRYPT, myEncrypt);

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
     * @param pManager the keyStoreManager
     * @throws OceanusException on error
     */
    private void keyPairSets(final GordianKeyStoreManager pManager) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pManager.getKeyStore();
        final GordianKeyStoreFactory myFactory = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStoreGateway myGateway = myFactory.createKeyStoreGateway(pManager);

        /* Create root certificates */
        final GordianKeyPairSetSpec mySpec = GordianKeyPairSetSpec.SIGNLO;
        final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
        final GordianKeyStorePairSet myRoot = pManager.createRootKeyPairSet(mySpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);
        final X500Name myRoot2Name = buildX500Name(KeyStoreAlias.ROOT2);
        final GordianKeyStorePairSet myRoot2 = pManager.createRootKeyPairSet(mySpec, myRoot2Name, KeyStoreAlias.ROOT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.ROOT, myRoot);
        checkKeyPairSet(myGateway, KeyStoreAlias.ROOT2, myRoot2);

        /* Cross-sign theRoots */
        GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
        final GordianKeyStorePairSet myRootAlt = pManager.createAlternate(myRoot, myUsage, myRoot2, KeyStoreAlias.ROOTALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePairSet myRoot2Alt = pManager.createAlternate(myRoot2, myUsage, myRoot, KeyStoreAlias.ROOTALT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.ROOTALT, myRootAlt);
        checkKeyPairSet(myGateway, KeyStoreAlias.ROOTALT2, myRoot2Alt);

        /* Create intermediates */
        final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
        final GordianKeyStorePairSet myIntermediate = pManager.createKeyPairSet(mySpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);
        final X500Name myInter2Name = buildX500Name(KeyStoreAlias.INTER2);
        final GordianKeyStorePairSet myIntermediate2 = pManager.createKeyPairSet(mySpec, myInter2Name, myUsage, myRoot2, KeyStoreAlias.INTER2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.INTER, myIntermediate);
        checkKeyPairSet(myGateway, KeyStoreAlias.INTER2, myIntermediate2);

        /* Cross-sign the intermediates */
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE, GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet myIntermediateAlt = pManager.createAlternate(myIntermediate, myUsage, myRoot2, KeyStoreAlias.INTERALT.getName(), DEF_PASSWORD);
        final GordianKeyStorePairSet myIntermediate2Alt = pManager.createAlternate(myIntermediate2, myUsage, myRoot, KeyStoreAlias.INTERALT2.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.INTERALT, myIntermediateAlt);
        checkKeyPairSet(myGateway, KeyStoreAlias.INTERALT2, myIntermediate2Alt);

        /* Create a signature keyPairSet */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianKeyStorePairSet mySigner = pManager.createKeyPairSet(mySpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.SIGNER, mySigner);

        /* Create an agreement keyPairSet */
        final X500Name myAgreeName = buildX500Name(KeyStoreAlias.AGREE);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        final GordianKeyStorePairSet myAgree = pManager.createKeyPairSet(GordianKeyPairSetSpec.AGREELO, myAgreeName, myUsage, myIntermediate, KeyStoreAlias.AGREE.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.AGREE, myAgree);

        /* Create an encryption keyPairSet */
        final X500Name myEncryptName = buildX500Name(KeyStoreAlias.ENCRYPT);
        myUsage = new GordianKeyPairUsage(GordianKeyPairUse.DATAENCRYPT);
        final GordianKeyStorePairSet myEncrypt = pManager.createKeyPairSet(GordianKeyPairSetSpec.ENCRYPT, myEncryptName, myUsage, myIntermediate, KeyStoreAlias.ENCRYPT.getName(), DEF_PASSWORD);
        checkKeyPairSet(myGateway, KeyStoreAlias.ENCRYPT, myEncrypt);

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
     * test signed keyPairRequest.
     * @param pState the state
     * @param pKeyPairSpec the keyPairSpec
     * @throws OceanusException on error
     */
    private static void signedKeyPairRequest(final KeyPairCertReqState pState,
                                             final GordianKeyPairSpec pKeyPairSpec) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = pState.getManager();
        final GordianCoreKeyStore myStore = pState.getStore();
        final GordianKeyStorePair myIntermediate = pState.getIntermediate();
        final GordianKeyStorePair myCertifier = pState.getCertifier();

        final GordianKeyStoreGateway myGateway = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory().createKeyStoreGateway(myMgr);
        myGateway.setKeyPairCertifier(KeyStoreAlias.CERTIFIER.getName(), DEF_PASSWORD);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianCoreKeyStorePair mySigner = myMgr.createKeyPair(pKeyPairSpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);

        /* Build the CertificateRequest */
        final GordianCRMBuilder myBuilder = new GordianCRMBuilder(myStore.getFactory());
        final GordianPEMObject myRequest = myBuilder.createCertificateRequest(mySigner);

        /* Parse the certificate Object */
        final GordianKeyPairCRMParser myParser = new GordianKeyPairCRMParser(myMgr, myCertifier, null);
        myParser.decodeCertificateRequest(myRequest);

        /* Cleanup */
        myStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
    }

    /**
     * test encrypted keyPairRequest.
     * @param pState the state
     * @param pKeyPairSpec the keyPairSpec
     * @param pUsage the keyUsage
     * @throws OceanusException on error
     */
    private static void encryptedKeyPairRequest(final KeyPairCertReqState pState,
                                                final GordianKeyPairSpec pKeyPairSpec,
                                                final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = pState.getManager();
        final GordianCoreKeyStore myStore = pState.getStore();
        final GordianKeyStorePair myIntermediate = pState.getIntermediate();
        final GordianKeyStorePair myCertifier = pState.getCertifier();

        /* Create the keyPair */
        final KeyStoreAlias myAlias = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? KeyStoreAlias.ENCRYPT
                : KeyStoreAlias.AGREE;
        final X500Name myCertName = buildX500Name(myAlias);
        final GordianCoreKeyStorePair myCert = myMgr.createKeyPair(pKeyPairSpec, myCertName, pUsage, myIntermediate, myAlias.getName(), DEF_PASSWORD);

        /* Create the target keyPair */
        final X500Name myTargetName = buildX500Name(KeyStoreAlias.TARGET);
        final GordianKeyStorePair myTarget = myMgr.createKeyPair(pKeyPairSpec, myTargetName, pUsage, myIntermediate, KeyStoreAlias.TARGET.getName(), DEF_PASSWORD);
        final GordianCoreKeyPairCertificate myTargetCert = (GordianCoreKeyPairCertificate) myTarget.getCertificateChain().get(0);

        /* Build the CertificateRequest */
        final GordianCRMBuilder myBuilder = new GordianCRMBuilder(myStore.getFactory(), myTargetCert);
        final GordianPEMObject myRequest = myBuilder.createCertificateRequest(myCert);

        /* Parse the certificate Object */
        final GordianKeyPairCRMParser myParser = new GordianKeyPairCRMParser(myMgr, myCertifier, pState::passwordResolver);
        myParser.decodeCertificateRequest(myRequest);

        /* Cleanup */
        myStore.deleteEntry(myAlias.getName());
        myStore.deleteEntry(KeyStoreAlias.TARGET.getName());
    }

    /**
     * test signed keyPairSetRequest.
     * @param pState the state
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @throws OceanusException on error
     */
    private static void signedKeyPairSetRequest(final KeyPairSetCertReqState pState,
                                                final GordianKeyPairSetSpec pKeyPairSetSpec) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = pState.getManager();
        final GordianCoreKeyStore myStore = pState.getStore();
        final GordianKeyStorePairSet myIntermediate = pState.getIntermediate();
        final GordianKeyStorePairSet myCertifier = pState.getCertifier();

        /* Create a signature keyPairSet */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        final GordianCoreKeyStorePairSet mySigner = myMgr.createKeyPairSet(pKeyPairSetSpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);

        /* Build the CertificateRequest */
        final GordianCRMBuilder myBuilder = new GordianCRMBuilder(myStore.getFactory());
        final GordianPEMObject myRequest = myBuilder.createCertificateRequest(mySigner);

        /* Parse the certificate Object */
        final GordianKeyPairSetCRMParser myParser = new GordianKeyPairSetCRMParser(myMgr, myCertifier, null);
        myParser.decodeCertificateRequest(myRequest);

        /* Cleanup */
        myStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
    }

    /**
     * test encrypted keyPairSetRequest.
     * @param pState the state
     * @param pKeyPairSetSpec the keyPairSetSpec
     * @param pUsage the keyUsage
     * @throws OceanusException on error
     */
    private static void encryptedKeyPairSetRequest(final KeyPairSetCertReqState pState,
                                                   final GordianKeyPairSetSpec pKeyPairSetSpec,
                                                   final GordianKeyPairUsage pUsage) throws OceanusException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = pState.getManager();
        final GordianCoreKeyStore myStore = pState.getStore();
        final GordianKeyStorePairSet myIntermediate = pState.getIntermediate();
        final GordianKeyStorePairSet myCertifier = pState.getCertifier();

        /* Create the keyPair */
        final KeyStoreAlias myAlias = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? KeyStoreAlias.ENCRYPT
                : KeyStoreAlias.AGREE;
        final X500Name myCertName = buildX500Name(myAlias);
        final GordianCoreKeyStorePairSet myCert = myMgr.createKeyPairSet(pKeyPairSetSpec, myCertName, pUsage, myIntermediate, myAlias.getName(), DEF_PASSWORD);

        /* Create the target keyPair */
        final X500Name myTargetName = buildX500Name(KeyStoreAlias.TARGET);
        final GordianKeyStorePairSet myTarget = myMgr.createKeyPairSet(pKeyPairSetSpec, myTargetName, pUsage, myIntermediate, KeyStoreAlias.TARGET.getName(), DEF_PASSWORD);
        final GordianCoreKeyPairSetCertificate myTargetCert = (GordianCoreKeyPairSetCertificate) myTarget.getCertificateChain().get(0);

        /* Build the CertificateRequest */
        final GordianCRMBuilder myBuilder = new GordianCRMBuilder(myStore.getFactory(), myTargetCert);
        final GordianPEMObject myRequest = myBuilder.createCertificateRequest(myCert);

        /* Parse the certificate Object */
        final GordianKeyPairSetCRMParser myParser = new GordianKeyPairSetCRMParser(myMgr, myCertifier, pState::passwordResolver);
        myParser.decodeCertificateRequest(myRequest);

        /* Cleanup */
        myStore.deleteEntry(myAlias.getName());
        myStore.deleteEntry(KeyStoreAlias.TARGET.getName());
    }

    /**
     * check keySet.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pKeySet the keySet
     * @throws OceanusException on error
     */
    private static void checkKeySet(final GordianKeyStoreGateway pGateway,
                                    final KeyStoreAlias pAlias,
                                    final GordianKeyStoreSet pKeySet) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pGateway.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeySetEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStoreSet.class));
        Assertions.assertEquals(pKeySet.getKeySet(), myStore.getKeySet(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeySet, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pGateway, pAlias, pKeySet);
    }

    /**
     * check key.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pKey the key
     * @throws OceanusException on error
     */
    private static void checkKey(final GordianKeyStoreGateway pGateway,
                                 final KeyStoreAlias pAlias,
                                 final GordianKeyStoreKey<?> pKey) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pGateway.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStoreKey.class));
        Assertions.assertEquals(pKey.getKey(), myStore.getKey(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKey, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pGateway, pAlias, pKey);
    }

    /**
     * check keyPair.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private static void checkKeyPair(final GordianKeyStoreGateway pGateway,
                                     final KeyStoreAlias pAlias,
                                     final GordianKeyStorePair pKeyPair) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pGateway.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyPairEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStorePair.class));
        Assertions.assertEquals(pKeyPair.getKeyPair(), myStore.getKeyPair(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPair.getCertificateChain(), myStore.getKeyPairCertificateChain(myName));
        Assertions.assertEquals(pKeyPair, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pGateway, pAlias, pKeyPair);
    }

    /**
     * check keyPairSet.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pKeyPairSet the keyPairSet
     * @throws OceanusException on error
     */
    private static void checkKeyPairSet(final GordianKeyStoreGateway pGateway,
                                        final KeyStoreAlias pAlias,
                                        final GordianKeyStorePairSet pKeyPairSet) throws OceanusException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pGateway.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyPairSetEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStorePairSet.class));
        Assertions.assertEquals(pKeyPairSet.getKeyPairSet(), myStore.getKeyPairSet(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPairSet.getCertificateChain(), myStore.getKeyPairSetCertificateChain(myName));
        Assertions.assertEquals(pKeyPairSet, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pGateway, pAlias, pKeyPairSet);
    }

    /**
     * check export.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pEntry the entry
     * @throws OceanusException on error
     */
    private static void checkExport(final GordianKeyStoreGateway pGateway,
                                    final KeyStoreAlias pAlias,
                                    final GordianKeyStoreEntry pEntry) throws OceanusException {
        final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
        pGateway.exportEntry(pAlias.getName(), myOutStream, DEF_PASSWORD);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        Assertions.assertEquals(pEntry, pGateway.importEntry(myInputStream, DEF_PASSWORD));
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
     * KeyPairCertReqState.
     */
    private static class KeyPairCertReqState {
        /**
         * The keyStoreManager.
         */
        private final GordianCoreKeyStoreManager theManager;

        /**
         * The intermediate signer.
         */
        private GordianKeyStorePair theIntermediate;

        /**
         * The certifier signer.
         */
        private GordianKeyStorePair theCertifier;

        /**
         * Constructor.
         * @param pManager the keyStore Manager
         * @throws OceanusException on error
          */
        KeyPairCertReqState(final GordianKeyStoreManager pManager) throws OceanusException {
            /* Store the manager */
            theManager = (GordianCoreKeyStoreManager) pManager;
        }

        /**
         * Obtain the keyStoreManager.
         * @return the keyStoreManager
         */
        GordianCoreKeyStoreManager getManager() {
            return theManager;
        }

        /**
         * Obtain the keyStore.
         * @return the keyStore
         */
        GordianCoreKeyStore getStore() {
            return theManager.getKeyStore();
        }

        /**
         * Obtain the intermediate.
         * @return the intermediate keyPair
         */
        GordianKeyStorePair getIntermediate() {
            return theIntermediate;
        }

        /**
         * Obtain the certifier.
         * @return the certifier keyPair
         */
        GordianKeyStorePair getCertifier() {
            return theCertifier;
        }

        /**
         * Initialise.
         * @throws OceanusException on error
         */
        void initialise() throws OceanusException {
            /* Create specs */
            final GordianKeyPairSpec myRSASpec = GordianKeyPairSpec.rsa(GordianRSAModulus.MOD2048);
            final GordianKeyPairSpec myECSpec = GordianKeyPairSpec.ec(GordianDSAElliptic.SECT571K1);

            /* Create root certificate */
            final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
            final GordianKeyStorePair myRoot = theManager.createRootKeyPair(myRSASpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);

            /* Create intermediate */
            GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
            final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
            theIntermediate = theManager.createKeyPair(myECSpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);

            /* Create certifier */
            final X500Name myCertifierName = buildX500Name(KeyStoreAlias.CERTIFIER);
            theCertifier = theManager.createKeyPair(myRSASpec, myCertifierName, myUsage, myRoot, KeyStoreAlias.CERTIFIER.getName(), DEF_PASSWORD);
        }

        /**
         * Cleanup.
         */
        void cleanUp() {
            /* Access the store */
            final GordianCoreKeyStore myStore = getStore();

            /* delete the entries */
            myStore.deleteEntry(KeyStoreAlias.ROOT.getName());
            myStore.deleteEntry(KeyStoreAlias.INTER.getName());
            myStore.deleteEntry(KeyStoreAlias.CERTIFIER.getName());

            /* Check that we have deleted all values */
            int mySize = myStore.size();
            Assertions.assertEquals(0, mySize);
        }

        /**
         * Password resolver.
         * @param pAlias the alias
         * @return the password
         */
        public char[] passwordResolver(final String pAlias) {
            return DEF_PASSWORD.clone();
        }
    }

    /**
     * KeyPairSetCertReqState.
     */
    private static class KeyPairSetCertReqState {
        /**
         * The keyStoreManager.
         */
        private final GordianCoreKeyStoreManager theManager;

        /**
         * The intermediate signer.
         */
        private GordianKeyStorePairSet theIntermediate;

        /**
         * The certifier signer.
         */
        private GordianKeyStorePairSet theCertifier;

        /**
         * Constructor.
         * @param pManager the keyStore Manager
         * @throws OceanusException on error
         */
        KeyPairSetCertReqState(final GordianKeyStoreManager pManager) throws OceanusException {
            /* Store the manager */
            theManager = (GordianCoreKeyStoreManager) pManager;
        }

        /**
         * Obtain the keyStoreManager.
         * @return the keyStoreManager
         */
        GordianCoreKeyStoreManager getManager() {
            return theManager;
        }

        /**
         * Obtain the keyStore.
         * @return the keyStore
         */
        GordianCoreKeyStore getStore() {
            return theManager.getKeyStore();
        }

        /**
         * Obtain the intermediate.
         * @return the intermediate keyPairSet
         */
        GordianKeyStorePairSet getIntermediate() {
            return theIntermediate;
        }

        /**
         * Obtain the certifier.
         * @return the certifier keyPairSet
         */
        GordianKeyStorePairSet getCertifier() {
            return theCertifier;
        }

        /**
         * Initialise.
         * @throws OceanusException on error
         */
        void initialise() throws OceanusException {
            /* Create specs */
            final GordianKeyPairSetSpec myBaseSpec = GordianKeyPairSetSpec.SIGNHI;

            /* Create root certificate */
            final X500Name myRootName = buildX500Name(KeyStoreAlias.ROOT);
            final GordianKeyStorePairSet myRoot = theManager.createRootKeyPairSet(myBaseSpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);

            /* Create intermediate */
            GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
            final X500Name myInterName = buildX500Name(KeyStoreAlias.INTER);
            theIntermediate = theManager.createKeyPairSet(myBaseSpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);

            /* Create certifier */
            final X500Name myCertifierName = buildX500Name(KeyStoreAlias.CERTIFIER);
            theCertifier = theManager.createKeyPairSet(myBaseSpec, myCertifierName, myUsage, myRoot, KeyStoreAlias.CERTIFIER.getName(), DEF_PASSWORD);
        }

        /**
         * Cleanup.
         */
        void cleanUp() {
            /* Access the store */
            final GordianCoreKeyStore myStore = getStore();

            /* delete the entries */
            myStore.deleteEntry(KeyStoreAlias.ROOT.getName());
            myStore.deleteEntry(KeyStoreAlias.INTER.getName());
            myStore.deleteEntry(KeyStoreAlias.CERTIFIER.getName());

            /* Check that we have deleted all values */
            int mySize = myStore.size();
            Assertions.assertEquals(0, mySize);
        }


        /**
         * Password resolver.
         * @param pAlias the alias
         * @return the password
         */
        public char[] passwordResolver(final String pAlias) {
            return DEF_PASSWORD.clone();
        }
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
         * Target Certificate.
         */
        TARGET("Target Certificate"),

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
