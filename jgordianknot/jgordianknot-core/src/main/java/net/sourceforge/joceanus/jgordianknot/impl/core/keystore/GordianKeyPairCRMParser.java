/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.util.Arrays;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPair Certificate Request Message Parser.
 */
public class GordianKeyPairCRMParser
        extends GordianCRMParser {
    /**
     * The testData length.
     */
    private static final int TESTLEN = 1024;

    /**
     * The signer.
     */
    private final GordianKeyStorePair theSigner;

    /**
     * The secret MAC key value.
     */
    private final byte[] theMACSecret;

    /**
     * Constructor.
     * @param pKeyStoreMgr the keyStoreManager
     * @param pSigner the signer
     * @param pEncryptor the encryptor
     * @param pMACSecret the MAC secret value
     * @param pResolver the password resolver
     * @throws OceanusException on error
     */
    public GordianKeyPairCRMParser(final GordianCoreKeyStoreManager pKeyStoreMgr,
                                   final GordianKeyStorePair pSigner,
                                   final GordianCRMEncryptor pEncryptor,
                                   final byte[] pMACSecret,
                                   final Function<String, char[]> pResolver) throws OceanusException {
        /* Store parameters */
        super(pKeyStoreMgr, pEncryptor, pResolver);
        theSigner = pSigner;
        theMACSecret = pMACSecret;

        /* Reject null signer */
        if (theSigner == null) {
            throw new GordianDataException("Null keyPairSigner");
        }
    }

    @Override
    public List<GordianPEMObject> decodeCertificateRequest(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not CertReq */
        GordianPEMCoder.checkObjectType(pObject, GordianPEMObjectType.CERTREQ);

        /* Derive the certificate request message */
        final CertReqMsg myReq = CertReqMsg.getInstance(pObject.getEncoded());
        final CertRequest myCertReq = myReq.getCertReq();
        final ProofOfPossession myProof = myReq.getPopo();
        final AttributeTypeAndValue[] myAttrs = myReq.getRegInfo();
        final CertTemplate myTemplate = myCertReq.getCertTemplate();
        final X500Name mySubject = myTemplate.getSubject();
        final SubjectPublicKeyInfo myPublic = myTemplate.getPublicKey();
        final GordianKeyPairUsage myUsage = GordianCoreCertificate.determineUsage(myTemplate.getExtensions());

        /* Check the PKMacValue */
        checkPKMACValue(myAttrs, myPublic);

        /* Derive keyPair and create certificate chain */
        final GordianKeyPair myPair = deriveKeyPair(myProof, myCertReq, mySubject, myPublic);
        final List<GordianCertificate> myChain = getKeyStoreMgr().signKeyPair(myPair, mySubject, myUsage, theSigner);

        /* Create and return the object list */
        final List<GordianPEMObject> myObjects = new ArrayList<>();
        for (GordianCertificate myCert : myChain) {
            myObjects.add(GordianPEMCoder.encodeCertificate(myCert));
        }
        return myObjects;
    }

    /**
     * decode the certificate response.
     * @param pObject the encoded response
     * @return the decoded response
     */
    public GordianCertResponseASN1 decodeCertificateResponse(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not CertResponse */
        GordianPEMCoder.checkObjectType(pObject, GordianPEMObjectType.CERTRESP);

        /* Derive the certificate request message */
        return GordianCertResponseASN1.getInstance(pObject.getEncoded());
    }

    /**
     * Derive and check the keyPair.
     * @param pProof the proof of possession
     * @param pCertReq the certificate request
     * @param pSubject the subject name
     * @param pPublicKey the publicKey
     * @return the keyPair
     * @throws OceanusException on error
     */
    private GordianKeyPair deriveKeyPair(final ProofOfPossession pProof,
                                         final CertRequest pCertReq,
                                         final X500Name pSubject,
                                         final SubjectPublicKeyInfo pPublicKey) throws OceanusException {
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
     * @param pProof the proof of possession
     * @param pCertReq the certificate request
     * @param pPublicKey the publicKey
     * @return the keyPair
     * @throws OceanusException on error
     */
    private GordianKeyPair deriveSignedKeyPair(final ProofOfPossession pProof,
                                               final CertRequest pCertReq,
                                               final SubjectPublicKeyInfo pPublicKey) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Derive the public Key */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509Spec);
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
            final GordianKeyPair myKeyPair = myGenerator.derivePublicOnlyKeyPair(myX509Spec);

            /* Access the verifier */
            final POPOSigningKey mySigning = (POPOSigningKey) pProof.getObject();
            final AlgorithmIdentifier myAlgId = mySigning.getAlgorithmIdentifier();
            final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
            final GordianSignatureSpec mySignSpec = mySignFactory.getSpecForIdentifier(myAlgId);
            final GordianSignature myVerifier = mySignFactory.createSigner(mySignSpec);

            /* Verify the signature */
            final byte[] mySignature = mySigning.getSignature().getBytes();
            myVerifier.initForVerify(myKeyPair);
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
     * @param pProof the proof of possession
     * @param pSubject the subject name
     * @param pPublicKey the publicKey
     * @return the keyPair
     * @throws OceanusException on error
     */
    private GordianKeyPair deriveEncryptedKeyPair(final ProofOfPossession pProof,
                                                  final X500Name pSubject,
                                                  final SubjectPublicKeyInfo pPublicKey) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the generator */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509Spec);
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);

            /* derive the privateKey and full keyPair */
            final PKCS8EncodedKeySpec myPKCS8Spec = derivePrivateKey(pProof, pSubject);
            final GordianKeyPair myKeyPair = myGenerator.deriveKeyPair(myX509Spec, myPKCS8Spec);

            /* Check that the privateKey matches the publicKey */
            checkPrivateKey(myKeyPair);

            /* Return the public only value */
            return myGenerator.derivePublicOnlyKeyPair(myX509Spec);

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted keyPair", e);
        }
    }

    /**
     * Check PrivateKey.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private void checkPrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Access details */
        final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();

        /* Check for encryption private key */
        final GordianEncryptorSpec myEncSpec = myFactory.getEncryptorFactory().defaultForKeyPair(mySpec);
        if (myEncSpec != null) {
            checkEncryptionPrivateKey(pKeyPair);
            return;
        }

        /* Check for agreement private key */
        final GordianAgreementSpec myAgreeSpec = myFactory.getAgreementFactory().defaultForKeyPair(mySpec);
        if (myAgreeSpec != null) {
            checkAgreementPrivateKey(pKeyPair);
            return;
        }

        /* Reject the request */
        throw new GordianDataException("Unable to verify privateKey");
    }

    /**
     * Check Encryption PrivateKey.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private void checkEncryptionPrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Create the data to encrypt */
        final byte[] mySrc = new byte[TESTLEN];
        getFactory().getRandomSource().getRandom().nextBytes(mySrc);

        /* Access details */
        final GordianEncryptorFactory myFactory = getFactory().getKeyPairFactory().getEncryptorFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        final GordianEncryptorSpec myEncSpec = myFactory.defaultForKeyPair(mySpec);

        /* Create and initialise encryptors */
        final GordianEncryptor mySender = myFactory.createEncryptor(myEncSpec);
        final GordianEncryptor myReceiver = myFactory.createEncryptor(myEncSpec);

        /* Handle Initialisation */
        mySender.initForEncrypt(pKeyPair);
        myReceiver.initForDecrypt(pKeyPair);

        /* Perform the encryption and decryption for all zeros */
        byte[] myEncrypted = mySender.encrypt(mySrc);
        byte[] myResult = myReceiver.decrypt(myEncrypted);

        /* Check the decryption */
        if (!Arrays.areEqual(mySrc, myResult)) {
            throw new GordianDataException("Private key failed validation");
        }
    }

    /**
     * Check Agreement PrivateKey.
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    private void checkAgreementPrivateKey(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Access details */
        final GordianAgreementFactory myFactory = getFactory().getKeyPairFactory().getAgreementFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        final GordianAgreementSpec myAgreeSpec = myFactory.defaultForKeyPair(mySpec);

        /* Create agreement */
        final GordianAnonymousAgreement mySender = (GordianAnonymousAgreement) myFactory.createAgreement(myAgreeSpec);
        mySender.setResultType(null);
        final GordianAnonymousAgreement myResponder = (GordianAnonymousAgreement) myFactory.createAgreement(myAgreeSpec);
        final byte[] myClientHello = mySender.createClientHello(pKeyPair);
        myResponder.acceptClientHello(pKeyPair, myClientHello);

        /* Check the agreements */
        final byte[] myFirst = (byte[]) mySender.getResult();
        final byte[] mySecond = (byte[]) myResponder.getResult();
        if (!Arrays.areEqual(myFirst, mySecond)) {
            throw new GordianDataException("Private key failed validation");
        }
    }

    /**
     * Check PKMacValue.
     * @param pAttrs the attributes
     * @param pPublicKey the public key
     * @throws OceanusException on error
     */
    private void checkPKMACValue(final AttributeTypeAndValue[] pAttrs,
                                 final SubjectPublicKeyInfo pPublicKey) throws OceanusException {
        /* If we have no secret then this is a no-op */
        if (theMACSecret == null) {
            return;
        }

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

        /* We must have a PKMACValue */
        if (myAttr == null) {
            throw new GordianDataException("Missing PKMacValue");
        }

        /* Calculate the PKMACValue and compare with the value that was sent */
        final PKMACValue mySent = PKMACValue.getInstance(myAttr.getValue());
        final PBMParameter myParams = PBMParameter.getInstance(mySent.getAlgId().getParameters());
        final PKMACValue myMACValue = GordianCRMBuilder.calculatePKMacValue(getFactory(), theMACSecret, pPublicKey, myParams);
        if (!mySent.equals(myMACValue)) {
            throw new GordianDataException("Invalid PKMacValue");
        }
    }
}
