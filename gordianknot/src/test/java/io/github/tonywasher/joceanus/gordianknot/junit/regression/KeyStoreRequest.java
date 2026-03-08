/*
 * GordianKnot: Security Suite
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.gordianknot.junit.regression;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.base.GordianLength;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import io.github.tonywasher.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianBIKESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianCMCESpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDHSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianDSTUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianECSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFRODOSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianFalconSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianGOSTSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianHQCSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianKeyPairSpecBuilder;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSHash;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianLMSSpec.GordianLMSWidth;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMLKEMSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianMayoSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeParams;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUPrimeSpec.GordianNTRUPrimeType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianNTRUSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianPicnicSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianRSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSABERSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSLHDSASpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSM2Spec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianSnovaSpec;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSDigestType;
import io.github.tonywasher.joceanus.gordianknot.api.keypair.spec.GordianXMSSSpec.GordianXMSSHeight;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreGateway;
import io.github.tonywasher.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreManager;
import io.github.tonywasher.joceanus.gordianknot.junit.regression.KeyStoreUtils.KeyStoreAlias;
import io.github.tonywasher.joceanus.gordianknot.util.GordianUtilities;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

import static io.github.tonywasher.joceanus.gordianknot.junit.regression.KeyStoreUtils.DEF_PASSWORD;

/**
 * KeyStore Request Tests.
 */
public final class KeyStoreRequest {
    /**
     * Default secret.
     */
    private static final String DEF_MACSECRET = "A Simple MAC Secret";

    /**
     * The KeyStore State.
     */
    private final KeyPairCertReqState theState;

    /**
     * Private constructor.
     *
     * @param pManager the manager
     */
    KeyStoreRequest(final GordianKeyStoreManager pManager) {
        theState = new KeyPairCertReqState(pManager);
    }

    /**
     * create keyPairRequests test stream.
     *
     * @return the test stream
     */
    DynamicNode keyPairRequestTest() {
        final GordianKeyPairSpecBuilder myBuilder = GordianUtilities.newKeyPairSpecBuilder();
        return DynamicContainer.dynamicContainer("keyPairRequest", Stream.of(
                DynamicTest.dynamicTest("Initialise", theState::initialise),
                signedKeyPairRequestTest(myBuilder.rsa(GordianRSASpec.MOD2048)),
                signedKeyPairRequestTest(myBuilder.ec(GordianECSpec.SECT571K1)),
                signedKeyPairRequestTest(myBuilder.dsa(GordianDSASpec.MOD2048)),
                signedKeyPairRequestTest(myBuilder.ed25519()),
                signedKeyPairRequestTest(myBuilder.ed448()),
                signedKeyPairRequestTest(myBuilder.gost2012(GordianGOSTSpec.GOST512A)),
                signedKeyPairRequestTest(myBuilder.dstu4145(GordianDSTUSpec.DSTU9)),
                signedKeyPairRequestTest(myBuilder.sm2(GordianSM2Spec.SM2P256V1)),
                signedKeyPairRequestTest(myBuilder.falcon(GordianFalconSpec.FALCON512)),
                signedKeyPairRequestTest(myBuilder.mayo(GordianMayoSpec.MAYO1)),
                signedKeyPairRequestTest(myBuilder.mldsa(GordianMLDSASpec.MLDSA44)),
                signedKeyPairRequestTest(myBuilder.picnic(GordianPicnicSpec.L1FS)),
                signedKeyPairRequestTest(myBuilder.slhdsa(GordianSLHDSASpec.SHA128F)),
                signedKeyPairRequestTest(myBuilder.snova(GordianSnovaSpec.SNOVA24A_SSK)),
                signedKeyPairRequestTest(myBuilder.xmss(GordianXMSSDigestType.SHA512, GordianXMSSHeight.H10)),
                signedKeyPairRequestTest(myBuilder.lms(GordianLMSHash.SHA256, GordianLMSHeight.H5,
                        GordianLMSWidth.W1, GordianLength.LEN_256)),
                signedKeyPairRequestTest(myBuilder.composite(myBuilder.rsa(GordianRSASpec.MOD2048),
                        myBuilder.ec(GordianECSpec.SECP256R1),
                        myBuilder.ed25519())),
                encryptedKeyPairRequestTest(myBuilder.elGamal(GordianDHSpec.FFDHE2048)),
                encryptedKeyPairRequestTest(myBuilder.composite(myBuilder.rsa(GordianRSASpec.MOD2048),
                        myBuilder.elGamal(GordianDHSpec.FFDHE2048),
                        myBuilder.sm2(GordianSM2Spec.SM2P256V1))),
                agreedKeyPairRequestTest(myBuilder.dh(GordianDHSpec.FFDHE2048)),
                agreedKeyPairRequestTest(myBuilder.x25519()),
                agreedKeyPairRequestTest(myBuilder.x448()),
                agreedKeyPairRequestTest(myBuilder.newHope()),
                agreedKeyPairRequestTest(myBuilder.bike(GordianBIKESpec.BIKE128)),
                agreedKeyPairRequestTest(myBuilder.cmce(GordianCMCESpec.BASE3488)),
                agreedKeyPairRequestTest(myBuilder.frodo(GordianFRODOSpec.AES640)),
                agreedKeyPairRequestTest(myBuilder.hqc(GordianHQCSpec.HQC128)),
                agreedKeyPairRequestTest(myBuilder.mlkem(GordianMLKEMSpec.MLKEM512)),
                agreedKeyPairRequestTest(myBuilder.ntru(GordianNTRUSpec.HPS821)),
                agreedKeyPairRequestTest(myBuilder.ntruprime(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR653)),
                agreedKeyPairRequestTest(myBuilder.saber(GordianSABERSpec.BASE128)),
                agreedKeyPairRequestTest(myBuilder.composite(myBuilder.cmce(GordianCMCESpec.BASE3488),
                        myBuilder.frodo(GordianFRODOSpec.AES640),
                        myBuilder.saber(GordianSABERSpec.BASE128))),
                DynamicTest.dynamicTest("Cleanup", theState::cleanUp)
        ));
    }

    /**
     * create signed keyPairRequest test for a keyPairSpec.
     *
     * @param pSpec the keyPairSpec
     * @return the test
     */
    private DynamicNode signedKeyPairRequestTest(final GordianKeyPairSpec pSpec) {
        /* Create test */
        return DynamicTest.dynamicTest(pSpec.toString(), () -> signedKeyPairRequest(pSpec));
    }

    /**
     * create encrypted keyPairRequest test for a keyPairSpec.
     *
     * @param pSpec the keyPairSpec
     * @return the test
     */
    private DynamicNode encryptedKeyPairRequestTest(final GordianKeyPairSpec pSpec) {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.KEYENCRYPT, GordianKeyPairUse.DATAENCRYPT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairRequest(pSpec, myUsage));
    }

    /**
     * create agreed keyPairRequest test for a keyPairSpec.
     *
     * @param pSpec the keyPairSpec
     * @return the test
     */
    private DynamicNode agreedKeyPairRequestTest(final GordianKeyPairSpec pSpec) {
        /* Create test */
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT);
        return DynamicTest.dynamicTest(pSpec.toString(), () -> encryptedKeyPairRequest(pSpec, myUsage));
    }

    /**
     * test signed keyPairRequest.
     *
     * @param pKeyPairSpec the keyPairSpec
     * @throws GordianException on error
     */
    private void signedKeyPairRequest(final GordianKeyPairSpec pKeyPairSpec) throws GordianException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = theState.getManager();
        final GordianCoreKeyStore myStore = theState.getStore();
        final GordianKeyStorePair myIntermediate = theState.getIntermediate();

        /* Handle a disabled keyPairSpec */
        if (!myMgr.getKeyStore().getFactory().getAsyncFactory().getKeyPairFactory().supportedKeyPairSpecs().test(pKeyPairSpec)) {
            return;
        }

        /* Create and configure gateway */
        final GordianKeyStoreGateway myGateway = myStore.getFactory().getAsyncFactory().getKeyStoreFactory().createKeyStoreGateway(myMgr);
        myGateway.setPasswordResolver(theState::passwordResolver);
        myGateway.setCertifier(KeyStoreAlias.CERTIFIER.getName());
        myGateway.setMACSecretResolver(n -> DEF_MACSECRET);

        /* Create a signature keyPair */
        final X500Name mySignName = KeyStoreUtils.buildX500Name(KeyStoreAlias.SIGNER);
        final GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.SIGNATURE);
        myMgr.createKeyPair(pKeyPairSpec, mySignName, myUsage, myIntermediate, KeyStoreAlias.SIGNER.getName(), KeyStoreUtils.DEF_PASSWORD);

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
     *
     * @param pKeyPairSpec the keyPairSpec
     * @param pUsage       the keyUsage
     * @throws GordianException on error
     */
    private void encryptedKeyPairRequest(final GordianKeyPairSpec pKeyPairSpec,
                                         final GordianKeyPairUsage pUsage) throws GordianException {
        /* Access details */
        final GordianCoreKeyStoreManager myMgr = theState.getManager();
        final GordianCoreKeyStore myStore = theState.getStore();
        final GordianKeyStorePair myIntermediate = theState.getIntermediate();

        /* Create the keyPair */
        final KeyStoreAlias myAlias = pUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? KeyStoreAlias.ENCRYPT
                : KeyStoreAlias.AGREE;
        final X500Name myCertName = KeyStoreUtils.buildX500Name(myAlias);
        myMgr.createKeyPair(pKeyPairSpec, myCertName, pUsage, myIntermediate, myAlias.getName(), KeyStoreUtils.DEF_PASSWORD);

        /* Create and configure gateway */
        final GordianKeyStoreGateway myGateway = myStore.getFactory().getAsyncFactory().getKeyStoreFactory().createKeyStoreGateway(myMgr);
        myGateway.setPasswordResolver(theState::passwordResolver);
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
         *
         * @param pManager the keyStore Manager
         */
        KeyPairCertReqState(final GordianKeyStoreManager pManager) {
            /* Store the manager */
            theManager = (GordianCoreKeyStoreManager) pManager;
        }

        /**
         * Obtain the keyStoreManager.
         *
         * @return the keyStoreManager
         */
        GordianCoreKeyStoreManager getManager() {
            return theManager;
        }

        /**
         * Obtain the keyStore.
         *
         * @return the keyStore
         */
        GordianCoreKeyStore getStore() {
            return theManager.getKeyStore();
        }

        /**
         * Obtain the intermediate.
         *
         * @return the intermediate keyPair
         */
        GordianKeyStorePair getIntermediate() {
            return theIntermediate;
        }

        /**
         * Obtain the certifier.
         *
         * @return the certifier keyPair
         */
        GordianKeyStorePair getCertifier() {
            return theCertifier;
        }

        /**
         * Initialise.
         *
         * @throws GordianException on error
         */
        void initialise() throws GordianException {
            /* Reset the keyStore */
            theManager.getKeyStore().reset();

            /* Create specs */
            final GordianKeyPairSpecBuilder myBuilder = GordianUtilities.newKeyPairSpecBuilder();
            final GordianKeyPairSpec myRSASpec = myBuilder.rsa(GordianRSASpec.MOD2048);
            final GordianKeyPairSpec myECSpec = myBuilder.ec(GordianECSpec.SECT571K1);

            /* Create root certificate */
            final X500Name myRootName = KeyStoreUtils.buildX500Name(KeyStoreAlias.ROOT);
            final GordianKeyStorePair myRoot = theManager.createRootKeyPair(myRSASpec, myRootName, KeyStoreAlias.ROOT.getName(), DEF_PASSWORD);

            /* Create intermediate */
            GordianKeyPairUsage myUsage = new GordianKeyPairUsage(GordianKeyPairUse.CERTIFICATE);
            final X500Name myInterName = KeyStoreUtils.buildX500Name(KeyStoreAlias.INTER);
            theIntermediate = theManager.createKeyPair(myECSpec, myInterName, myUsage, myRoot, KeyStoreAlias.INTER.getName(), DEF_PASSWORD);

            /* Create certifier */
            final X500Name myCertifierName = KeyStoreUtils.buildX500Name(KeyStoreAlias.CERTIFIER);
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
         *
         * @param pAlias the alias
         * @return the password
         */
        public char[] passwordResolver(final String pAlias) {
            return DEF_PASSWORD.clone();
        }
    }
}
