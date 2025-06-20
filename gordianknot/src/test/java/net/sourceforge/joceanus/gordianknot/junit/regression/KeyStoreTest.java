/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.junit.regression;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianStreamKeySpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.cipher.GordianSymKeySpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianBIKESpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianCMCESpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianFalconSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianFRODOSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianGOSTElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianHQCSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianLMSHash;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianLMSHeight;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianLMSKeySpec.GordianLMSWidth;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianMLDSASpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianMLKEMSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianMayoSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeParams;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianNTRUSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianPicnicSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianRSAModulus;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSABERSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSLHDSASpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSM2Elliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianSnovaSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSDigestType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianXMSSKeySpec.GordianXMSSHeight;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStore;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreKey;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStoreSet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreFactory;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreGateway;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.util.GordianGenerator;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

/**
 * KeyStore Tests.
 */
class KeyStoreTest {
    /**
     * Default password.
     */
    private static final char[] DEF_PASSWORD = "SimplePassword".toCharArray();

    /**
     * Default secret.
     */
    private static final String DEF_MACSECRET = "A Simple MAC Secret";

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
    private static final GordianPasswordLockSpec KEYSETLOCKSPEC = new GordianPasswordLockSpec(KEYSETSPEC);

    /**
     * Create the keyStore test suite.
     * @return the test stream
     * @throws GordianException on error
     */
    @TestFactory
    Stream<DynamicNode> keyStoreTests() throws GordianException {
        /* Create tests */
        Stream<DynamicNode> myStream = keyStoreTests(GordianFactoryType.BC);
        return Stream.concat(myStream, keyStoreTests(GordianFactoryType.JCA));
    }

    /**
     * Create the keySet test suite for a factoryType.
     * @param pFactoryType the factoryType
     * @return the test stream
     * @throws GordianException on error
     */
    private Stream<DynamicNode> keyStoreTests(final GordianFactoryType pFactoryType) throws GordianException {
        /* Create the factory */
        final GordianFactory myFactory = GordianGenerator.createRandomFactory(pFactoryType);

        /* Access keyStoreFactory and create a keyStore */
        final GordianKeyStoreFactory myKSFactory = myFactory.getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStore myStore = myKSFactory.createKeyStore(KEYSETLOCKSPEC);
        final GordianKeyStoreManager myMgr = myKSFactory.createKeyStoreManager(myStore);

        /* Return the stream */
        return Stream.of(DynamicContainer.dynamicContainer(pFactoryType.toString(), Stream.of(
                DynamicTest.dynamicTest("symmetric", () -> symmetric(myMgr)),
                DynamicTest.dynamicTest("keyPair", () -> keyPairs(myMgr)),
                keyPairRequestTest(myMgr)
        )));
    }

    /**
     * create keyPairRequests test stream.
     * @param pManager the keyStoreManager
     * @return the test stream
     */
    private DynamicNode keyPairRequestTest(final GordianKeyStoreManager pManager) {
        /* Create state */
        final KeyPairCertReqState myState = new KeyPairCertReqState(pManager);

        return DynamicContainer.dynamicContainer("keyPairRequest", Stream.of(
            DynamicTest.dynamicTest("Initialise", myState::initialise),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECT571K1)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.dsa(GordianDSAKeyType.MOD2048)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.ed25519()),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.ed448()),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.gost2012(GordianGOSTElliptic.GOST512A)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.dstu4145(GordianDSTU4145Elliptic.DSTU9)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.sm2(GordianSM2Elliptic.SM2P256V1)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.slhdsa(GordianSLHDSASpec.SHA128F)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.mldsa(GordianMLDSASpec.MLDSA44)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.falcon(GordianFalconSpec.FALCON512)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.picnic(GordianPicnicSpec.L1FS)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.mayo(GordianMayoSpec.MAYO1)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.snova(GordianSnovaSpec.SNOVA24A_SSK)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.xmss(GordianXMSSDigestType.SHA512, GordianXMSSHeight.H10)),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.lms(new GordianLMSKeySpec(GordianLMSHash.SHA256, GordianLMSHeight.H5,
                                                                                           GordianLMSWidth.W1, GordianLength.LEN_256))),
            signedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                                                                                  GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECP256R1),
                                                                                  GordianKeyPairSpecBuilder.ed25519())),
            encryptedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.elGamal(GordianDHGroup.FFDHE2048)),
            encryptedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                                                                                     GordianKeyPairSpecBuilder.elGamal(GordianDHGroup.FFDHE2048),
                                                                                     GordianKeyPairSpecBuilder.sm2(GordianSM2Elliptic.SM2P256V1))),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.dh(GordianDHGroup.FFDHE2048)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.x25519()),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.x448()),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.newHope()),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.cmce(GordianCMCESpec.BASE3488)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.frodo(GordianFRODOSpec.AES640)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.saber(GordianSABERSpec.BASE128)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.mlkem(GordianMLKEMSpec.MLKEM512)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.hqc(GordianHQCSpec.HQC128)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.bike(GordianBIKESpec.BIKE128)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.ntru(GordianNTRUSpec.HPS821)),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR653))),
            agreedKeyPairRequestTest(myState, GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.cmce(GordianCMCESpec.BASE3488),
                                                                                  GordianKeyPairSpecBuilder.frodo(GordianFRODOSpec.AES640),
                                                                                  GordianKeyPairSpecBuilder.saber(GordianSABERSpec.BASE128))),
            DynamicTest.dynamicTest("Cleanup", myState::cleanUp)
        ));
    }

    /**
     * create signed keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     */
    private static DynamicNode signedKeyPairRequestTest(final KeyPairCertReqState pState,
                                                        final GordianKeyPairSpec pSpec) {
        /* Create test */
        return DynamicTest.dynamicTest(pSpec.toString(), () -> signedKeyPairRequest(pState, pSpec));
    }

    /**
     * create encrypted keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     */
    private static DynamicNode encryptedKeyPairRequestTest(final KeyPairCertReqState pState,
                                                           final GordianKeyPairSpec pSpec) {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.KEYENCRYPT, GordianKeyPairUse.DATAENCRYPT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairRequest(pState, pSpec, myUsage));
    }

    /**
     * create agreed keyPairRequest test for a keyPairSpec.
     * @param pState the state
     * @param pSpec the keyPairSpec
     * @return the test
     */
    private static DynamicNode agreedKeyPairRequestTest(final KeyPairCertReqState pState,
                                                        final GordianKeyPairSpec pSpec) {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairRequest(pState, pSpec, myUsage));
    }

    /**
     * test symmetric.
     * @param pManager the keyStoreManager
     * @throws GordianException on error
     */
    private void symmetric(final GordianKeyStoreManager pManager) throws GordianException {
        /* Access details */
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pManager.getKeyStore();
        final GordianKeyStoreFactory myFactory = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStoreGateway myGateway = myFactory.createKeyStoreGateway(pManager);

        /* Create the keySet */
        final GordianKeyStoreSet mySet = pManager.createKeySet(KEYSETSPEC, KeyStoreAlias.KEYSET.getName(), DEF_PASSWORD);
        checkKeySet(myGateway, KeyStoreAlias.KEYSET, mySet);

        /* Create a symKey */
        final GordianKeyStoreKey<?> mySymKey = pManager.createKey(GordianSymKeySpecBuilder.aes(KEYLEN), KeyStoreAlias.SYMKEY.getName(), DEF_PASSWORD);
        checkKey(myGateway, KeyStoreAlias.SYMKEY, mySymKey);

        /* Create a streamKey */
        final GordianKeyStoreKey<?> myStreamKey = pManager.createKey(GordianStreamKeySpecBuilder.hc(KEYLEN), KeyStoreAlias.STREAMKEY.getName(), DEF_PASSWORD);
        checkKey(myGateway, KeyStoreAlias.STREAMKEY, myStreamKey);

        /* Create a macKey */
        final GordianKeyStoreKey<?> myMacKey = pManager.createKey(GordianMacSpecBuilder.vmpcMac(KEYLEN), KeyStoreAlias.MACKEY.getName(), DEF_PASSWORD);
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
     * @throws GordianException on error
     */
    private void keyPairs(final GordianKeyStoreManager pManager) throws GordianException {
        /* Access details */
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pManager.getKeyStore();
        final GordianKeyStoreFactory myFactory = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory();
        final GordianKeyStoreGateway myGateway = myFactory.createKeyStoreGateway(pManager);

        /* Create root certificates */
        final GordianKeyPairSpec mySpec = GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECT571K1);
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
     * test signed keyPairRequest.
     * @param pState the state
     * @param pKeyPairSpec the keyPairSpec
     * @throws GordianException on error
     */
    private static void signedKeyPairRequest(final KeyPairCertReqState pState,
                                             final GordianKeyPairSpec pKeyPairSpec) throws GordianException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = pState.getManager();
        final GordianCoreKeyStore myStore = pState.getStore();
        final GordianKeyStorePair myIntermediate = pState.getIntermediate();

        /* Handle a disabled keyPairSpec */
        if (!myMgr.getKeyStore().getFactory().getKeyPairFactory().supportedKeyPairSpecs().test(pKeyPairSpec)) {
            return;
        }

        /* Create and configure gateway */
        final GordianKeyStoreGateway myGateway = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory().createKeyStoreGateway(myMgr);
        myGateway.setPasswordResolver(pState::passwordResolver);
        myGateway.setCertifier(KeyStoreAlias.CERTIFIER.getName());
        myGateway.setMACSecretResolver(n -> DEF_MACSECRET);

        /* Create a signature keyPair */
        final X500Name mySignName = buildX500Name(KeyStoreAlias.SIGNER);
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        myMgr.createKeyPair(pKeyPairSpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), DEF_PASSWORD);
        /* Build the CertificateRequest */
        final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
        myGateway.createCertificateRequest(KeyStoreAlias.SIGNER.getName(), myOutStream);

        /* Process the certificateRequest */
        ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        myOutStream.reset();
        myGateway.processCertificateRequest(myInputStream, myOutStream);

        /* Process the certificateResponse */
        myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        myOutStream.reset();
        final Integer myRespId = myGateway.processCertificateResponse(myInputStream, myOutStream);

        /* Cleanup */
        myStore.deleteEntry(KeyStoreAlias.SIGNER.getName());
        myStore.deleteEntry(((GordianCoreKeyStoreGateway) myGateway).getCertificateAlias(myRespId));
    }

    /**
     * test encrypted keyPairRequest.
     * @param pState the state
     * @param pKeyPairSpec the keyPairSpec
     * @param pUsage the keyUsage
     * @throws GordianException on error
     */
    private static void encryptedKeyPairRequest(final KeyPairCertReqState pState,
                                                final GordianKeyPairSpec pKeyPairSpec,
                                                final GordianKeyPairUsage pUsage) throws GordianException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = pState.getManager();
        final GordianCoreKeyStore myStore = pState.getStore();
        final GordianKeyStorePair myIntermediate = pState.getIntermediate();

        /* Create the keyPair */
        final KeyStoreAlias myAlias = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? KeyStoreAlias.ENCRYPT
                : KeyStoreAlias.AGREE;
        final X500Name myCertName = buildX500Name(myAlias);
        myMgr.createKeyPair(pKeyPairSpec, myCertName, pUsage, myIntermediate, myAlias.getName(), DEF_PASSWORD);

        /* Create and configure gateway */
        final GordianKeyStoreGateway myGateway = myStore.getFactory().getKeyPairFactory().getKeyStoreFactory().createKeyStoreGateway(myMgr);
        myGateway.setPasswordResolver(pState::passwordResolver);
        myGateway.setCertifier(KeyStoreAlias.CERTIFIER.getName());
        myGateway.setMACSecretResolver(n -> DEF_MACSECRET);

        /* Build the CertificateRequest */
        final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
        myGateway.createCertificateRequest(myAlias.getName(), myOutStream);

        /* Process the certificateRequest */
        ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        myOutStream.reset();
        myGateway.processCertificateRequest(myInputStream, myOutStream);

        /* Process the certificateResponse */
        myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        myOutStream.reset();
        final Integer myRespId = myGateway.processCertificateResponse(myInputStream, myOutStream);

        /* Process the certificateAck */
        myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        myOutStream.reset();
        myGateway.processCertificateAck(myInputStream);

        /* Cleanup */
        myStore.deleteEntry(myAlias.getName());
        myStore.deleteEntry(((GordianCoreKeyStoreGateway) myGateway).getCertificateAlias(myRespId));
    }

    /**
     * check keySet.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pKeySet the keySet
     * @throws GordianException on error
     */
    private static void checkKeySet(final GordianKeyStoreGateway pGateway,
                                    final KeyStoreAlias pAlias,
                                    final GordianKeyStoreSet pKeySet) throws GordianException {
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
     * @throws GordianException on error
     */
    private static void checkKey(final GordianKeyStoreGateway pGateway,
                                 final KeyStoreAlias pAlias,
                                 final GordianKeyStoreKey<?> pKey) throws GordianException {
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
     * @throws GordianException on error
     */
    private static void checkKeyPair(final GordianKeyStoreGateway pGateway,
                                     final KeyStoreAlias pAlias,
                                     final GordianKeyStorePair pKeyPair) throws GordianException {
        final String myName = pAlias.getName();
        final GordianKeyStore myStore = pGateway.getKeyStore();
        Assertions.assertTrue(myStore.containsAlias(myName));
        Assertions.assertTrue(myStore.isKeyPairEntry(myName));
        Assertions.assertTrue(myStore.entryInstanceOf(myName, GordianKeyStorePair.class));
        Assertions.assertEquals(pKeyPair.getKeyPair(), myStore.getKeyPair(myName, DEF_PASSWORD));
        Assertions.assertEquals(pKeyPair.getCertificateChain(), myStore.getCertificateChain(myName));
        Assertions.assertEquals(pKeyPair, myStore.getEntry(myName, DEF_PASSWORD));
        checkExport(pGateway, pAlias, pKeyPair);
    }

    /**
     * check export.
     * @param pGateway the keyStoreGateway
     * @param pAlias the alias
     * @param pEntry the entry
     * @throws GordianException on error
     */
    private static void checkExport(final GordianKeyStoreGateway pGateway,
                                    final KeyStoreAlias pAlias,
                                    final GordianKeyStoreEntry pEntry) throws GordianException {
        final GordianCoreKeyStore myStore = (GordianCoreKeyStore) pGateway.getKeyStore();
        final GordianZipLock myLock = myStore.getFactory().getZipFactory().keySetZipLock(DEF_PASSWORD);
        final ByteArrayOutputStream myOutStream = new ByteArrayOutputStream();
        pGateway.setPasswordResolver(a -> DEF_PASSWORD.clone());
        pGateway.exportEntry(pAlias.getName(), myOutStream, myLock);
        final ByteArrayInputStream myInputStream = new ByteArrayInputStream(myOutStream.toByteArray());
        pGateway.setLockResolver(l -> l.unlock(DEF_PASSWORD));
        Assertions.assertEquals(pEntry, pGateway.importEntry(myInputStream));
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
          */
        KeyPairCertReqState(final GordianKeyStoreManager pManager) {
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
         * @throws GordianException on error
         */
        void initialise() throws GordianException {
            /* Create specs */
            final GordianKeyPairSpec myRSASpec = GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048);
            final GordianKeyPairSpec myECSpec = GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECT571K1);

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
