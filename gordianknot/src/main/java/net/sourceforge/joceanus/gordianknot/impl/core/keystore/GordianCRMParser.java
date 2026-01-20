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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUsage;
import net.sourceforge.joceanus.gordianknot.api.cert.GordianKeyPairUse;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.gordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianAsyncFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreement;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementFactory;
import net.sourceforge.joceanus.gordianknot.api.xagree.GordianXAgreementParams;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianBaseFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCertUtils;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.EncKeyWithID;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * Certificate Request Message Parser.
 */
public class GordianCRMParser {
    /**
     * The testData length.
     */
    private static final int TESTLEN = 1024;

    /**
     * The gateway.
     */
    private final GordianBaseKeyStoreGateway theGateway;

    /**
     * The builder.
     */
    private final GordianCRMBuilder theBuilder;

    /**
     * Constructor.
     *
     * @param pGateway the gateway
     * @param pBuilder the builder
     */
    GordianCRMParser(final GordianBaseKeyStoreGateway pGateway,
                     final GordianCRMBuilder pBuilder) {
        /* Store parameters */
        theGateway = pGateway;
        theBuilder = pBuilder;
    }

    /**
     * Does a certificate request require encryption?
     *
     * @param pCertReq the certificate request
     * @return true/false
     */
    static boolean requiresEncryption(final CertReqMsg pCertReq) {
        /* Only encipherment keys may be encrypted */
        final ProofOfPossession myProof = pCertReq.getPop();
        if (myProof.getType() != ProofOfPossession.TYPE_KEY_ENCIPHERMENT) {
            return false;
        }

        /* Check that we are responding with an encrypted certificate */
        final POPOPrivKey myPOP = POPOPrivKey.getInstance(myProof.getObject());
        final int myProofType = myPOP.getType();
        return myProofType == POPOPrivKey.subsequentMessage
                && ASN1Integer.getInstance(myPOP.getValue()).intValueExact() == SubsequentMessage.encrCert.intValueExact();
    }

    /**
     * process a certificate request.
     *
     * @param pRequest the request
     * @return the signed certificate chain
     * @throws GordianException on error
     */
    List<GordianCertificate> processCertificateRequest(final CertReqMsg pRequest) throws GordianException {
        /* Derive the certificate request message */
        final CertRequest myCertReq = pRequest.getCertReq();
        final ProofOfPossession myProof = pRequest.getPop();
        final AttributeTypeAndValue[] myAttrs = pRequest.getRegInfo();
        final CertTemplate myTemplate = myCertReq.getCertTemplate();
        final X500Name mySubject = myTemplate.getSubject();
        final SubjectPublicKeyInfo myPublic = myTemplate.getPublicKey();
        final GordianKeyPairUsage myUsage = GordianCertUtils.determineUsage(myTemplate.getExtensions());

        /* Check the PKMacValue */
        checkPKMACValue(mySubject, myAttrs, myPublic);

        /* Derive keyPair and create certificate chain */
        final GordianKeyPair myPair = deriveKeyPair(myProof, myCertReq, mySubject, myPublic);
        final GordianBaseKeyStoreManager myKeyStoreMgr = (GordianBaseKeyStoreManager) theGateway.getKeyStoreManager();
        final GordianKeyStorePair mySigner = theGateway.getSigner();
        if (mySigner == null) {
            throw new GordianLogicException("Null keyPairSigner");
        }
        return myKeyStoreMgr.signKeyPair(myPair, mySubject, myUsage, mySigner);
    }

    /**
     * process a certificate response.
     *
     * @param pResponse the certificate response
     * @param pKeyPair  the keyPair
     */
    public void processCertificateResponse(final GordianCertResponseASN1 pResponse,
                                           final GordianKeyStorePair pKeyPair) throws GordianException {
        /* Decrypt if necessary */
        if (pResponse.isEncrypted()) {
            final GordianCRMEncryptor myEncryptor = theGateway.getEncryptor();
            pResponse.decryptCertificate(myEncryptor, pKeyPair);
        }

        /* Check the MACValue */
        final GordianCoreCertificate myCert = (GordianCoreCertificate) pKeyPair.getCertificateChain().get(0);
        final X500Name myName = myCert.getSubjectName();
        final byte[] myMACSecret = theGateway.getMACSecret(myName);
        final ASN1Object myMACData = pResponse.getMACData();
        final PKMACValue mySent = pResponse.getMACValue();

        /* If we have a mismatch on security */
        if ((myMACSecret == null) != (mySent == null)) {
            throw new GordianDataException("Mismatch on PKMAC Security");
        }

        /* If we have a MACValue */
        if (mySent != null) {
            /* Calculate the PKMACValue and compare with the value that was sent */
            theBuilder.checkPKMACValue(myMACSecret, myMACData, mySent);
        }

        /* Check that the publicKey matches */
        final GordianCoreCertificate myNewCert = pResponse.getCertificate(theGateway.getEncryptor());
        if (!myNewCert.checkMatchingPublicKey(pKeyPair.getKeyPair())) {
            throw new GordianDataException("Mismatch on publicKey");
        }

        /* Check that subjectName matches */
        final GordianCoreCertificate myOldCert = (GordianCoreCertificate) pKeyPair.getCertificateChain().get(0);
        if (!myNewCert.getSubjectName().equals(myOldCert.getSubjectName())) {
            throw new GordianDataException("Mismatch on subjectName");
        }
    }

    /**
     * Derive the privateKey.
     *
     * @param pProof   the proof of possession
     * @param pSubject the subject name
     * @return the PKCS8Encoded privateKey
     * @throws GordianException on error
     */
    PKCS8EncodedKeySpec derivePrivateKey(final ProofOfPossession pProof,
                                         final X500Name pSubject) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract details */
            final POPOPrivKey myEncrypt = (POPOPrivKey) pProof.getObject();
            final EnvelopedData myData = (EnvelopedData) myEncrypt.getValue();
            final RecipientInfo myRecipient = RecipientInfo.getInstance(myData.getRecipientInfos().getObjectAt(0));
            final EncryptedContentInfo myContent = myData.getEncryptedContentInfo();
            final byte[] myEncryptedPrivKey = myContent.getEncryptedContent().getOctets();
            final KeyTransRecipientInfo myRecInfo = (KeyTransRecipientInfo) myRecipient.getInfo();

            /* Derive the keySet */
            final GordianKeySet myKeySet = deriveKeySetFromRecInfo(myRecInfo);

            /* Decrypt the privateKey/ID */
            final EncKeyWithID myKeyWithId = EncKeyWithID.getInstance(myKeySet.decryptBytes(myEncryptedPrivKey));

            /* Check that the ID matches */
            final X500Name myName = X500Name.getInstance(GeneralName.getInstance(myKeyWithId.getIdentifier()).getName());
            if (!myName.equals(pSubject)) {
                throw new GordianDataException("Mismatch on subjectID");
            }

            /* myName and Subject should be identical */
            return new PKCS8EncodedKeySpec(myKeyWithId.getPrivateKey().getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted privateKey", e);
        }
    }

    /**
     * Derive the keySet via a keyPairSet issuer.
     *
     * @param pRecInfo the recipient info
     * @return the keySet
     * @throws GordianException on error
     */
    private GordianKeySet deriveKeySetFromRecInfo(final KeyTransRecipientInfo pRecInfo) throws GordianException {
        /* Access issuer details */
        final IssuerAndSerialNumber myIssId = (IssuerAndSerialNumber) pRecInfo.getRecipientIdentifier().getId();

        /* Locate issuer */
        final GordianBaseKeyStore myKeyStore = (GordianBaseKeyStore) theGateway.getKeyStore();
        final String myAlias = myKeyStore.findIssuerCert(myIssId);
        final Function<String, char[]> myResolver = theGateway.getPasswordResolver();
        final char[] myPassword = myResolver.apply(myAlias);
        if (myPassword == null) {
            throw new GordianDataException("No password available for issuer");
        }
        final GordianKeyStoreEntry myIssuerEntry = myKeyStore.getEntry(myAlias, myPassword);
        Arrays.fill(myPassword, (char) 0);

        /* Access details */
        final GordianKeyStorePair myIssuer = (GordianKeyStorePair) myIssuerEntry;
        final GordianCertificate myCert = myIssuer.getCertificateChain().get(0);
        final GordianCRMEncryptor myEncryptor = theGateway.getEncryptor();
        return myEncryptor.deriveKeySetFromRecInfo(pRecInfo, myCert, myIssuer.getKeyPair());
    }

    /**
     * Derive and check the keyPair.
     *
     * @param pProof     the proof of possession
     * @param pCertReq   the certificate request
     * @param pSubject   the subject name
     * @param pPublicKey the publicKey
     * @return the keyPair
     * @throws GordianException on error
     */
    private GordianKeyPair deriveKeyPair(final ProofOfPossession pProof,
                                         final CertRequest pCertReq,
                                         final X500Name pSubject,
                                         final SubjectPublicKeyInfo pPublicKey) throws GordianException {
        /* Handle signed keyPair */
        switch (pProof.getType()) {
            case ProofOfPossession.TYPE_SIGNING_KEY:
                return deriveSignedKeyPair(pProof, pCertReq, pPublicKey);
            case ProofOfPossession.TYPE_KEY_ENCIPHERMENT:
                return deriveEncryptedKeyPair(pProof, pSubject, pPublicKey);
            default:
                throw new GordianDataException("Unsupported proof type");
        }
    }

    /**
     * Derive a signed keyPair.
     *
     * @param pProof     the proof of possession
     * @param pCertReq   the certificate request
     * @param pPublicKey the publicKey
     * @return the keyPair
     * @throws GordianException on error
     */
    private GordianKeyPair deriveSignedKeyPair(final ProofOfPossession pProof,
                                               final CertRequest pCertReq,
                                               final SubjectPublicKeyInfo pPublicKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Derive the public Key */
            final GordianAsyncFactory myFactory = theGateway.getFactory().getAsyncFactory();
            final GordianKeyPairFactory myKPFactory = myFactory.getKeyPairFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSpec myKeySpec = myKPFactory.determineKeyPairSpec(myX509Spec);
            final GordianKeyPairGenerator myGenerator = myKPFactory.getKeyPairGenerator(myKeySpec);
            final GordianKeyPair myKeyPair = myGenerator.derivePublicOnlyKeyPair(myX509Spec);

            /* Access the verifier */
            final POPOSigningKey mySigning = (POPOSigningKey) pProof.getObject();
            final AlgorithmIdentifier myAlgId = mySigning.getAlgorithmIdentifier();
            final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
            final GordianSignatureSpec mySignSpec = mySignFactory.getSpecForIdentifier(myAlgId);
            final GordianSignature myVerifier = mySignFactory.createSigner(mySignSpec);

            /* Verify the signature */
            final byte[] mySignature = mySigning.getSignature().getBytes();
            myVerifier.initForVerify(GordianSignParams.keyPair(myKeyPair));
            myVerifier.update(pCertReq.getEncoded());
            if (!myVerifier.verify(mySignature)) {
                throw new GordianDataException("Verification of keyPair failed");
            }
            return myKeyPair;

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive signed keyPair", e);
        }
    }

    /**
     * Derive an encrypted keyPair.
     *
     * @param pProof     the proof of possession
     * @param pSubject   the subject name
     * @param pPublicKey the publicKey
     * @return the keyPair
     * @throws GordianException on error
     */
    private GordianKeyPair deriveEncryptedKeyPair(final ProofOfPossession pProof,
                                                  final X500Name pSubject,
                                                  final SubjectPublicKeyInfo pPublicKey) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Access the generator */
            final GordianKeyPairFactory myFactory = theGateway.getFactory().getAsyncFactory().getKeyPairFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509Spec);
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);

            /* Determine type of proof of Possession */
            final POPOPrivKey myPOP = POPOPrivKey.getInstance(pProof.getObject());
            final int myProofType = myPOP.getType();

            /* Handle encryptedKey */
            if (myProofType == POPOPrivKey.encryptedKey) {
                /* derive the privateKey and full keyPair */
                final PKCS8EncodedKeySpec myPKCS8Spec = derivePrivateKey(pProof, pSubject);
                final GordianKeyPair myKeyPair = myGenerator.deriveKeyPair(myX509Spec, myPKCS8Spec);

                /* Check that the privateKey matches the publicKey */
                checkPrivateKey(myKeyPair);
            } else if (myProofType != POPOPrivKey.subsequentMessage
                    || ASN1Integer.getInstance(myPOP.getValue()).intValueExact() != SubsequentMessage.encrCert.intValueExact()) {
                throw new GordianDataException("Unsupported ProofType");
            }

            /* Return the public only value */
            return myGenerator.derivePublicOnlyKeyPair(myX509Spec);

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted keyPair", e);
        }
    }

    /**
     * Check PrivateKey.
     *
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    private void checkPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Access details */
        final GordianAsyncFactory myFactory = theGateway.getFactory().getAsyncFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();

        /* Check for encryption private key */
        final GordianEncryptorSpec myEncSpec = myFactory.getEncryptorFactory().defaultForKeyPair(mySpec);
        if (myEncSpec != null) {
            checkEncryptionPrivateKey(pKeyPair);
            return;
        }

        /* Check for agreement private key */
        final GordianAgreementSpec myAgreeSpec = myFactory.getXAgreementFactory().defaultForKeyPair(mySpec);
        if (myAgreeSpec != null) {
            checkAgreementPrivateKey(pKeyPair);
            return;
        }

        /* Reject the request */
        throw new GordianDataException("Unable to verify privateKey");
    }

    /**
     * Check Encryption PrivateKey.
     *
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    private void checkEncryptionPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];
        final GordianBaseFactory myFactory = theGateway.getFactory();
        myFactory.getRandomSource().getRandom().nextBytes(mySrc);

        /* Access details */
        final GordianEncryptorFactory myEncFactory = myFactory.getAsyncFactory().getEncryptorFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        final GordianEncryptorSpec myEncSpec = myEncFactory.defaultForKeyPair(mySpec);

        /* Create and initialise encryptors */
        final GordianEncryptor mySender = myEncFactory.createEncryptor(myEncSpec);
        final GordianEncryptor myReceiver = myEncFactory.createEncryptor(myEncSpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(pKeyPair);
        myReceiver.initForDecrypt(pKeyPair);

        /* Perform the encryption and decryption for all zeros */
        final byte[] myEncrypted = mySender.encrypt(mySrc);
        final byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check the decryption */
        if (!org.bouncycastle.util.Arrays.areEqual(mySrc, myResult)) {
            throw new GordianDataException("Private key failed validation");
        }
    }

    /**
     * Check Agreement PrivateKey.
     *
     * @param pKeyPair the keyPair
     * @throws GordianException on error
     */
    private void checkAgreementPrivateKey(final GordianKeyPair pKeyPair) throws GordianException {
        /* Access details */
        final GordianBaseFactory myFactory = theGateway.getFactory();
        final GordianXAgreementFactory myAgreeFactory = myFactory.getAsyncFactory().getXAgreementFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        final GordianAgreementSpec myAgreeSpec = myAgreeFactory.defaultForKeyPair(mySpec);

        /* Create agreement */
        final GordianCertificate myCert = myAgreeFactory.newMiniCertificate(GordianCRMEncryptor.SERVER, pKeyPair,
                new GordianKeyPairUsage(GordianKeyPairUse.AGREEMENT));
        GordianXAgreementParams myParams = myAgreeFactory.newAgreementParams(myAgreeSpec, GordianLength.LEN_256.getByteLength())
                .setServerCertificate(myCert);
        final GordianXAgreement mySender = myAgreeFactory.createAgreement(myParams);
        final byte[] myClientHello = mySender.nextMessage();
        final GordianXAgreement myResponder = myAgreeFactory.parseAgreementMessage(myClientHello);
        myParams = myResponder.getAgreementParams().setServerCertificate(myCert);
        myResponder.updateParams(myParams);

        /* Check the agreements */
        final byte[] myFirst = (byte[]) mySender.getResult();
        final byte[] mySecond = (byte[]) myResponder.getResult();
        if (!Arrays.equals(myFirst, mySecond)) {
            throw new GordianDataException("Private key failed validation");
        }
    }

    /**
     * Check PKMacValue.
     *
     * @param pSubject   the subject name
     * @param pAttrs     the attributes
     * @param pPublicKey the public key
     * @throws GordianException on error
     */
    private void checkPKMACValue(final X500Name pSubject,
                                 final AttributeTypeAndValue[] pAttrs,
                                 final SubjectPublicKeyInfo pPublicKey) throws GordianException {
        /* Loop through the Attrs */
        AttributeTypeAndValue myAttr = null;
        if (pAttrs != null) {
            for (AttributeTypeAndValue myCurr : pAttrs) {
                if (GordianCRMBuilder.MACVALUEATTROID.equals(myCurr.getType())) {
                    myAttr = myCurr;
                    break;
                }
            }
        }

        /* If we have a mismatch on security */
        final byte[] myMACSecret = theGateway.getMACSecret(pSubject);
        if ((myMACSecret == null) != (myAttr == null)) {
            throw new GordianDataException("Mismatch on PKMAC Security");
        }

        /* If we have a MACValue */
        if (myAttr != null) {
            /* Calculate the PKMACValue and compare with the value that was sent */
            final PKMACValue mySent = PKMACValue.getInstance(myAttr.getValue());
            theBuilder.checkPKMACValue(myMACSecret, pPublicKey, mySent);
        }
    }
}
