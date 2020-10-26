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

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSetSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet Certificate Request Message Parser.
 */
public class GordianKeyPairSetCRMParser {
    /**
     * The Issuer Callback.
     */
    public interface GordianKeyPairSetCRMIssuer {
        /**
         * Obtain KeyPair entry for issuer.
         * @param pIssuerId the issuerId
         * @return the keyPair entry
         * @throws OceanusException on error
         */
        GordianKeyStorePairSet getIssuerKeyPairSet(IssuerAndSerialNumber pIssuerId) throws OceanusException;
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
    private final GordianKeyStorePairSet theSigner;

    /**
     * The issuer lookup.
     */
    private final GordianKeyPairSetCRMIssuer theIssuer;

    /**
     * Constructor.
     * @param pKeyStoreMgr the keyStoreManager
     * @param pSigner the signer
     * @param pIssuer the issuer lookup
     */
    public GordianKeyPairSetCRMParser(final GordianCoreKeyStoreManager pKeyStoreMgr,
                                      final GordianKeyStorePairSet pSigner,
                                      final GordianKeyPairSetCRMIssuer pIssuer) {
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
        final GordianKeyPairSet myPairSet = deriveKeyPairSet(myProof, myCertReq, mySubject, myPublic);
        final List<GordianKeyPairSetCertificate> myChain = theKeyStoreMgr.signKeyPairSet(myPairSet, mySubject, myUsage, theSigner);

        /* Create and return the object list */
        final List<GordianPEMObject> myObjects = new ArrayList<>();
        for (GordianKeyPairSetCertificate myCert : myChain) {
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
    private GordianKeyPairSet deriveKeyPairSet(final ProofOfPossession pProof,
                                               final CertRequest pCertReq,
                                               final X500Name pSubject,
                                               final SubjectPublicKeyInfo pPublicKey) throws OceanusException {
        /* Handle signed keyPair */
        if (pProof.getType() == ProofOfPossession.TYPE_SIGNING_KEY) {
            return deriveSignedKeyPairSet(pProof, pCertReq, pPublicKey);
        }

        /* Handle signed keyPair */
        if (pProof.getType() == ProofOfPossession.TYPE_KEY_ENCIPHERMENT) {
            return deriveEncryptedKeyPairSet(pProof, pSubject, pPublicKey);
        }

        /* Not supported */
        throw new GordianDataException("Unsupported proof type");
    }

    /**
     * Derive a signed keyPairSet.
     * @param pProof the proof of possession
     * @param pCertReq the certificate request
     * @param pPublicKey the publicKey
     * @return the keyPairSet
     * @throws OceanusException on error
     */
    private GordianKeyPairSet deriveSignedKeyPairSet(final ProofOfPossession pProof,
                                                     final CertRequest pCertReq,
                                                     final SubjectPublicKeyInfo pPublicKey) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Derive the public Key */
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final GordianKeyPairSetFactory mySetFactory = myFactory.getKeyPairSetFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSetSpec myKeySpec = mySetFactory.determineKeyPairSetSpec(myX509Spec);
            final GordianKeyPairSetGenerator myGenerator = mySetFactory.getKeyPairSetGenerator(myKeySpec);
            final GordianKeyPairSet myKeyPairSet = myGenerator.derivePublicOnlyKeyPairSet(myX509Spec);

            /* Access the verifier */
            final POPOSigningKey mySigning = (POPOSigningKey) pProof.getObject();
            final AlgorithmIdentifier myAlgId = mySigning.getAlgorithmIdentifier();
            final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
            final GordianKeyPairSetSignature myVerifier = mySignFactory.createKeyPairSetSigner(myKeySpec);

            /* Verify the signature */
            final byte[] mySignature = mySigning.getSignature().getBytes();
            myVerifier.initForVerify(myKeyPairSet);
            myVerifier.update(pCertReq.getEncoded());
            if (!myVerifier.verify(mySignature)) {
                throw new GordianDataException("Verification of keyPairSet failed");
            }
            return myKeyPairSet;

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive signed keyPair", e);
        }
    }

    /**
     * Derive an encrypted keyPairSet.
     * @param pProof the proof of possession
     * @param pSubject the subject name
     * @param pPublicKey the publicKey
     * @return the keyPairSet
     * @throws OceanusException on error
     */
    private GordianKeyPairSet deriveEncryptedKeyPairSet(final ProofOfPossession pProof,
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
            final GordianKeyStorePairSet myIssuer = theIssuer.getIssuerKeyPairSet(myIssId);
            final GordianKeyPairSetCertificate myCert = myIssuer.getCertificateChain().get(0);
            final GordianKeyPairSet myKeyPairSet = myIssuer.getKeyPairSet();

            /* Derive the keySet appropriately */
            final GordianKeyPairUsage myUsage = myCert.getUsage();
            final GordianKeySet myKeySet = myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                    ? deriveEncryptedKeySet(myKeyPairSet, myAId, myEncryptedKey)
                    : deriveAgreedKeySet(myKeyPairSet, myEncryptedKey);

            /* Access the generator */
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final GordianKeyPairSetFactory mySetFactory = myFactory.getKeyPairSetFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSetSpec myKeySpec = mySetFactory.determineKeyPairSetSpec(myX509Spec);
            final GordianKeyPairSetGenerator myGenerator = mySetFactory.getKeyPairSetGenerator(myKeySpec);

            /* Decrypt the privateKey/ID */
            final EncKeyWithID myKeyWithId = EncKeyWithID.getInstance(myKeySet.decryptBytes(myEncryptedPrivKey));

            /* Check that the ID matches */
            final X500Name myName = X500Name.getInstance(GeneralName.getInstance(myKeyWithId.getIdentifier()).getName());
            if (!myName.equals(pSubject)) {
                throw new GordianDataException("Mismatch on subjectID");
            }

            /* myName and Subject should be identical */
            final PKCS8EncodedKeySpec myPKCS8Spec = new PKCS8EncodedKeySpec(myKeyWithId.getPrivateKey().getEncoded());
            return myGenerator.deriveKeyPairSet(myX509Spec, myPKCS8Spec);

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted keyPair", e);
        }
    }

    /**
     * Derive an encrypted keySet.
     * @param pKeyPairSet the keyPairSet
     * @param pAlgId the algorithm Identifier
     * @param pEncryptedKey the encrypted key
     * @return the derived keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveEncryptedKeySet(final GordianKeyPairSet pKeyPairSet,
                                                final AlgorithmIdentifier pAlgId,
                                                final byte[] pEncryptedKey) throws OceanusException {
        /* Handle decryption */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianKeyPairSetSpec myKeySpec = pKeyPairSet.getKeyPairSetSpec();
        final GordianKeyPairSetEncryptor myEncryptor = myEncFactory.createKeyPairSetEncryptor(myKeySpec);
        myEncryptor.initForDecrypt(pKeyPairSet);
        final byte[] myKey = myEncryptor.decrypt(pEncryptedKey);
        return GordianKeyPairCRMBuilder.deriveKeySetFromKey(theFactory, myKey);
    }

    /**
     * Derive an agreed keySet.
     * @param pKeyPairSet the keyPairSet
     * @param pHello the clientHello
     * @return the derived keySet
     * @throws OceanusException on error
     */
    private GordianKeySet deriveAgreedKeySet(final GordianKeyPairSet pKeyPairSet,
                                             final byte[] pHello) throws OceanusException {
        /* Handle agreement */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myKPFactory.getAgreementFactory();
        final GordianKeyPairSetAnonymousAgreement myAgree = (GordianKeyPairSetAnonymousAgreement) myAgreeFactory.createKeyPairSetAgreement(pHello);
        myAgree.acceptClientHello(pKeyPairSet, pHello);
        return (GordianKeySet) myAgree.getResult();
    }
}
