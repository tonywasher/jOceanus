/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianBIKESpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianCMCESpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDHGroup;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSAElliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSAKeyType;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianDSTU4145Elliptic;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianFRODOSpec;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianFalconSpec;
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
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreGateway;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStore;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreGateway;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCoreKeyStoreManager;
import net.sourceforge.joceanus.gordianknot.junit.regression.KeyStoreUtils.KeyStoreAlias;
import org.bouncycastle.asn1.x500.X500Name;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

import static net.sourceforge.joceanus.gordianknot.junit.regression.KeyStoreUtils.DEF_PASSWORD;

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
        return DynamicContainer.dynamicContainer("keyPairRequest", Stream.of(
                DynamicTest.dynamicTest("Initialise", theState::initialise),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECT571K1)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.dsa(GordianDSAKeyType.MOD2048)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.ed25519()),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.ed448()),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.gost2012(GordianGOSTElliptic.GOST512A)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.dstu4145(GordianDSTU4145Elliptic.DSTU9)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.sm2(GordianSM2Elliptic.SM2P256V1)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.falcon(GordianFalconSpec.FALCON512)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.mayo(GordianMayoSpec.MAYO1)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.mldsa(GordianMLDSASpec.MLDSA44)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.picnic(GordianPicnicSpec.L1FS)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.slhdsa(GordianSLHDSASpec.SHA128F)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.snova(GordianSnovaSpec.SNOVA24A_SSK)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.xmss(GordianXMSSDigestType.SHA512, GordianXMSSHeight.H10)),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.lms(new GordianLMSKeySpec(GordianLMSHash.SHA256, GordianLMSHeight.H5,
                        GordianLMSWidth.W1, GordianLength.LEN_256))),
                signedKeyPairRequestTest(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                        GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECP256R1),
                        GordianKeyPairSpecBuilder.ed25519())),
                encryptedKeyPairRequestTest(GordianKeyPairSpecBuilder.elGamal(GordianDHGroup.FFDHE2048)),
                encryptedKeyPairRequestTest(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048),
                        GordianKeyPairSpecBuilder.elGamal(GordianDHGroup.FFDHE2048),
                        GordianKeyPairSpecBuilder.sm2(GordianSM2Elliptic.SM2P256V1))),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.dh(GordianDHGroup.FFDHE2048)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.x25519()),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.x448()),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.newHope()),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.bike(GordianBIKESpec.BIKE128)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.cmce(GordianCMCESpec.BASE3488)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.frodo(GordianFRODOSpec.AES640)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.hqc(GordianHQCSpec.HQC128)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.mlkem(GordianMLKEMSpec.MLKEM512)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.ntru(GordianNTRUSpec.HPS821)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.ntruprime(new GordianNTRUPrimeSpec(GordianNTRUPrimeType.NTRUL, GordianNTRUPrimeParams.PR653))),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.saber(GordianSABERSpec.BASE128)),
                agreedKeyPairRequestTest(GordianKeyPairSpecBuilder.composite(GordianKeyPairSpecBuilder.cmce(GordianCMCESpec.BASE3488),
                        GordianKeyPairSpecBuilder.frodo(GordianFRODOSpec.AES640),
                        GordianKeyPairSpecBuilder.saber(GordianSABERSpec.BASE128))),
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
            final GordianKeyPairSpec myRSASpec = GordianKeyPairSpecBuilder.rsa(GordianRSAModulus.MOD2048);
            final GordianKeyPairSpec myECSpec = GordianKeyPairSpecBuilder.ec(GordianDSAElliptic.SECT571K1);

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
