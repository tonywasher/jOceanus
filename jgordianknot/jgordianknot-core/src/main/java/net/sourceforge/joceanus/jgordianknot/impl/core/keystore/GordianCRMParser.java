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
package net.sourceforge.joceanus.jgordianknot.impl.core.keystore;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;

import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.EncKeyWithID;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Certificate Request Message Parser.
 */
public class GordianCRMParser {
    /**
     * The Issuer Callback.
     */
    public interface GordianCRMIssuer {
        /**
         * Obtain KeyPair entry for issuer.
         * @param pIssuerId the issuerId
         * @return the keyPair entry
         * @throws OceanusException on error
         */
        GordianKeyStorePair getIssuerKeyPair(IssuerAndSerialNumber pIssuerId) throws OceanusException;
    }

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The keyStore.
     */
    private final GordianCoreKeyStore theKeyStore;

    /**
     * The keyStoreManager.
     */
    private final GordianCoreKeyStoreManager theKeyStoreMgr;

    /**
     * The signer.
     */
    private final GordianKeyStorePair theSigner;

    /**
     * The issuer lookup.
     */
    private final GordianCRMIssuer theIssuer;

    /**
     * Constructor.
     * @param pKeyStoreMgr the keyStoreManager
     * @param pSigner the signer
     * @param pIssuer the issuer lookup
     */
    public GordianCRMParser(final GordianCoreKeyStoreManager pKeyStoreMgr,
                            final GordianKeyStorePair pSigner,
                            final GordianCRMIssuer pIssuer) {
        /* Store parameters */
        theKeyStoreMgr = pKeyStoreMgr;
        theKeyStore = pKeyStoreMgr.getKeyStore();
        theSigner = pSigner;
        theIssuer = pIssuer;
        theFactory = theKeyStore.getFactory();
    }

    /**
     * Decode a certificate request.
     * @param pObject the PEM object
     * @return the PEM certificate chain
     * @throws OceanusException on error
     */
    public List<GordianPEMObject> decodeCertificateRequest(final GordianPEMObject pObject) throws OceanusException {
        /* Derive the certificate request message */
        final CertReqMsg myReq = CertReqMsg.getInstance(pObject.getEncoded());
        final CertRequest myCertReq = myReq.getCertReq();
        final ProofOfPossession myProof = myReq.getPopo();
        final CertTemplate myTemplate = myCertReq.getCertTemplate();
        final X500Name mySubject = myTemplate.getSubject();
        final SubjectPublicKeyInfo myPublic = myTemplate.getPublicKey();
        final GordianKeyPairUsage myUsage = GordianCoreCertificate.determineUsage(myTemplate.getExtensions());

        /* Derive keyPair and access certificate chain */
        final GordianKeyPair myPair = deriveKeyPair(myProof, myCertReq, mySubject, myPublic);
        final List<GordianKeyPairCertificate> myChain = theKeyStoreMgr.signKeyPair(myPair, mySubject, myUsage, theSigner);

        /* Create and return the object list */
        final List<GordianPEMObject> myObjects = new ArrayList<>();
        for (GordianKeyPairCertificate myCert : myChain) {
            myObjects.add(GordianPEMCoder.encodeCertificate(myCert));
        }
        return myObjects;
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
        if (pProof.getType() == ProofOfPossession.TYPE_SIGNING_KEY) {
            return deriveSignedKeyPair(pProof, pCertReq, pPublicKey);
        }

        /* Handle signed keyPair */
        if (pProof.getType() == ProofOfPossession.TYPE_KEY_ENCIPHERMENT) {
            return deriveEncryptedKeyPair(pProof, pSubject, pPublicKey);
        }

        /* Not supported */
        throw new GordianDataException("Unsupported proof type");
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
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509Spec);
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);
            final GordianKeyPair myKeyPair = myGenerator.derivePublicOnlyKeyPair(myX509Spec);

            /* Access the verifier */
            final POPOSigningKey mySigning = (POPOSigningKey) pProof.getObject();
            final AlgorithmIdentifier myAlgId = mySigning.getAlgorithmIdentifier();
            final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
            final GordianSignatureSpec mySignSpec = mySignFactory.getSpecForIdentifier(myAlgId);
            final GordianKeyPairSignature myVerifier = mySignFactory.createKeyPairSigner(mySignSpec);

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
            /* Extract details */
            final POPOPrivKey myEncrypt = (POPOPrivKey) pProof.getObject();
            final EnvelopedData myData = (EnvelopedData) myEncrypt.getValue();
            final RecipientInfo myRecipient = RecipientInfo.getInstance(myData.getRecipientInfos().getObjectAt(0));
            final EncryptedContentInfo myContent = myData.getEncryptedContentInfo();
            final byte[] myEncryptedPrivKey = myContent.getEncryptedContent().getOctets();
            final KeyTransRecipientInfo myRecInfo = (KeyTransRecipientInfo) myRecipient.getInfo();
            final AlgorithmIdentifier myAId = myRecInfo.getKeyEncryptionAlgorithm();
            final byte[] myEncryptedKey = myRecInfo.getEncryptedKey().getOctets();

            /* Locate the alias for the matching keyCertificate */
            final IssuerAndSerialNumber myIssId = (IssuerAndSerialNumber) myRecInfo.getRecipientIdentifier().getId();
            final GordianKeyStorePair myIssuer = theIssuer.getIssuerKeyPair(myIssId);
            final GordianKeyPairCertificate myCert = myIssuer.getCertificateChain().get(0);
            final GordianKeyPair myKeyPair = myIssuer.getKeyPair();

            /* Derive the keySet appropriately */
            final GordianKeyPairUsage myUsage = myCert.getUsage();
            final GordianKeySet myKeySet = myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                    ? deriveEncryptedKeySet(myKeyPair, myAId, myEncryptedKey)
                    : deriveAgreedKeySet(myKeyPair, myEncryptedKey);

            /* Access the generator */
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSpec myKeySpec = myFactory.determineKeyPairSpec(myX509Spec);
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(myKeySpec);

            /* Decrypt the privateKey/ID */
            final EncKeyWithID myKeyWithId = EncKeyWithID.getInstance(myKeySet.decryptBytes(myEncryptedPrivKey));

            /* Check that the ID matches */
            final X500Name myName = X500Name.getInstance(GeneralName.getInstance(myKeyWithId.getIdentifier()).getName());
            if (!myName.equals(pSubject)) {
                throw new GordianDataException("Mismatch on subjectID");
            }

            /* myName and Subject should be identical */
            final PKCS8EncodedKeySpec myPKCS8Spec = new PKCS8EncodedKeySpec(myKeyWithId.getPrivateKey().getEncoded());
            return myGenerator.deriveKeyPair(myX509Spec, myPKCS8Spec);

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted keyPair", e);
        }
    }

    /**
     * Derive an encrypted keySet.
     * @param pKeyPair the keyPair
     * @param pAlgId the algorithm Identifier
     * @param pEncryptedKey the encrypted key
     * @return the derived keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveEncryptedKeySet(final GordianKeyPair pKeyPair,
                                                final AlgorithmIdentifier pAlgId,
                                                final byte[] pEncryptedKey) throws OceanusException {
        /* Handle decryption */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianEncryptorSpec myEncSpec = myEncFactory.getSpecForIdentifier(pAlgId);
        final GordianKeyPairEncryptor myEncryptor = myEncFactory.createKeyPairEncryptor(myEncSpec);
        myEncryptor.initForDecrypt(pKeyPair);
        final byte[] myKey = myEncryptor.decrypt(pEncryptedKey);
        return GordianCRMBuilder.deriveKeySetFromKey(theFactory, myKey);
    }

    /**
     * Derive an agreed keySet.
     * @param pKeyPair the keyPair
     * @param pHello the clientHello
     * @return the derived keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveAgreedKeySet(final GordianKeyPair pKeyPair,
                                             final byte[] pHello) throws OceanusException {
        /* Handle agreement */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myKPFactory.getAgreementFactory();
        final GordianKeyPairAnonymousAgreement myAgree = (GordianKeyPairAnonymousAgreement) myAgreeFactory.createKeyPairAgreement(pHello);
        myAgree.acceptClientHello(pKeyPair, pHello);
        return (GordianKeySet) myAgree.getResult();
    }
}
