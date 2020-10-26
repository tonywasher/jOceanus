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
import java.util.Arrays;

import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.EncKeyWithID;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairSetAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairSetEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSetSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keypairset.GordianKeyPairSetAlgId;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet Certificate Request Message Builder.
 */
public class GordianKeyPairSetCRMBuilder {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The target certificate.
     */
    private final GordianCoreKeyPairSetCertificate theTarget;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pTarget the target certificate
     */
    public GordianKeyPairSetCRMBuilder(final GordianCoreFactory pFactory,
                                       final GordianCoreKeyPairSetCertificate pTarget) {
        theFactory = pFactory;
        theTarget = pTarget;
    }

    /**
     * Create a Certificate request.
     * @param pKeyPairSet the keyStore entry
     * @return the encoded PEM object
     * @throws OceanusException on error
     */
    public GordianPEMObject createCertificateRequest(final GordianKeyStorePairSet pKeyPairSet) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the certificate */
            final GordianCoreKeyPairSetCertificate myCert = (GordianCoreKeyPairSetCertificate) pKeyPairSet.getCertificateChain().get(0);

            /* Create the Certificate request */
            final CertRequest myCertReq = GordianKeyPairCRMBuilder.createCertRequest(myCert);

            /* Create the ProofOfPossession */
            final ProofOfPossession myProof = createProofOfPossession(pKeyPairSet, myCert, myCertReq);

            /* Create a CertificateRequestMsg */
            final CertReqMsg myReqMsg = new CertReqMsg(myCertReq, myProof, null);
            return new GordianPEMObject(GordianPEMObjectType.KEYPAIRSETCERTREQ, myReqMsg.getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to create CRMF", e);
        }
    }

    /**
     * Create Proof of Possession.
     * @param pKeyPairSet the keyStore entry
     * @param pCertificate the local certificate
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    private ProofOfPossession createProofOfPossession(final GordianKeyStorePairSet pKeyPairSet,
                                                      final GordianCoreKeyPairSetCertificate pCertificate,
                                                      final CertRequest pCertRequest) throws OceanusException {
        /* Try to send a signed proof */
        final GordianKeyPairSet myKeyPairSet = pKeyPairSet.getKeyPairSet();
        final GordianKeyPairSetSpec mySpec = myKeyPairSet.getKeyPairSetSpec();
        if (mySpec.canSign()) {
            return createSignedProof(myKeyPairSet, mySpec, pCertRequest);
        }

        /* Send encrypted key via Encryption or Agreement */
        final GordianKeyPairUsage myUsage = theTarget.getUsage();
        return myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? createEncryptedProof(myKeyPairSet, pCertificate)
                : createAgreedProof(myKeyPairSet, pCertificate);
    }

    /**
     * Create a Signed Proof of Possession.
     * @param pKeyPairSet the keyPairSet
     * @param pKeyPairSetSpec the signatureSpec
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createSignedProof(final GordianKeyPairSet pKeyPairSet,
                                        final GordianKeyPairSetSpec pKeyPairSetSpec,
                                        final CertRequest pCertRequest) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the signer */
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
            final GordianKeyPairSetSignature mySigner = mySignFactory.createKeyPairSetSigner(pKeyPairSetSpec);

            /* Create the signature */
            mySigner.initForSigning(pKeyPairSet);
            mySigner.update(pCertRequest.getEncoded());
            final byte[] mySignature = mySigner.sign();

            /* Build the signing key */
            final AlgorithmIdentifier myAlgId = GordianKeyPairSetAlgId.determineAlgorithmId(pKeyPairSetSpec);
            final POPOSigningKey myKey = new POPOSigningKey(null, myAlgId, new DERBitString(mySignature));
            return new ProofOfPossession(myKey);

        } catch (IOException e) {
            throw new GordianIOException("Failed to create Signed Proof of Possession", e);
        }
    }

    /**
     * Create an Encrypted Proof of Possession.
     * @param pKeyPairSet the keyPairSet
     * @param pCertificate the local certificate
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createEncryptedProof(final GordianKeyPairSet pKeyPairSet,
                                           final GordianCoreKeyPairSetCertificate pCertificate) throws OceanusException {
        /* Create the random key */
        final byte[] myKey = new byte[GordianLength.LEN_1024.getByteLength()];
        theFactory.getRandomSource().getRandom().nextBytes(myKey);

        /* Derive the keySet from the key */
        final GordianKeySet myKeySet = GordianKeyPairCRMBuilder.deriveKeySetFromKey(theFactory, myKey);

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = buildEncryptedContentInfo(myKeySet, pKeyPairSet, pCertificate);

        /* Create the encrypted key */
        final GordianKeyPairSetSpec mySpec = theTarget.getKeyPair().getKeyPairSetSpec();
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianKeyPairSetEncryptor myEncryptor = myEncFactory.createKeyPairSetEncryptor(mySpec);
        myEncryptor.initForEncrypt(theTarget.getKeyPair());
        final byte[] myEncryptedKey = myEncryptor.encrypt(myKey);
        Arrays.fill(myKey, (byte) 0);

        /* Create the proof of Possession */
        return createProofOfPossession(GordianKeyPairSetAlgId.determineAlgorithmId(mySpec), myEncryptedKey, myInfo);
    }

    /**
     * Create an Agreed Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pCertificate the local certificate
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createAgreedProof(final GordianKeyPairSet pKeyPair,
                                        final GordianCoreKeyPairSetCertificate pCertificate) throws OceanusException {
        /* Create the agreement */
        final GordianKeyPairSetAgreementSpec mySpec = GordianKeyPairSetAgreementSpec.anon(theTarget.getKeyPair().getKeyPairSetSpec());
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myFactory.getAgreementFactory();
        final GordianKeyPairSetAnonymousAgreement myAgree = (GordianKeyPairSetAnonymousAgreement) myAgreeFactory.createKeyPairSetAgreement(mySpec);
        myAgree.setResultType(new GordianKeySetSpec());
        final byte[] myHello = myAgree.createClientHello(theTarget.getKeyPair());
        final GordianKeySet myKeySet = (GordianKeySet) myAgree.getResult();

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = buildEncryptedContentInfo(myKeySet, pKeyPair, pCertificate);

        /* Create the proof of Possession */
        return createProofOfPossession(GordianKeyPairSetAlgId.determineAlgorithmId(mySpec), myHello, myInfo);
    }

    /**
     * Build the encryptedContentInfo.
     * @param pKeySet the keySet to encrypt with
     * @param pKeyPairSet the keyPairSet
     * @param pCertificate the local certificate
     * @return the encryptedContentInfo
     * @throws OceanusException on error
     */
    private EncryptedContentInfo buildEncryptedContentInfo(final GordianKeySet pKeySet,
                                                           final GordianKeyPairSet pKeyPairSet,
                                                           final GordianCoreKeyPairSetCertificate pCertificate) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Obtain the PrivateKeyInfo */
            final GordianKeyPairSetFactory myFactory = theFactory.getKeyPairFactory().getKeyPairSetFactory();
            final GordianKeyPairSetGenerator myGenerator = myFactory.getKeyPairSetGenerator(pKeyPairSet.getKeyPairSetSpec());
            final PKCS8EncodedKeySpec mySpec = myGenerator.getPKCS8Encoding(pKeyPairSet);
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(mySpec.getEncoded());
            final EncKeyWithID myKeyWithId = new EncKeyWithID(myInfo, new GeneralName(pCertificate.getSubject().getName()));
            final byte[] myData = pKeySet.encryptBytes(myKeyWithId.getEncoded());
            final GordianKeySetSpecASN1 myASN1 = new GordianKeySetSpecASN1(pKeySet.getKeySetSpec());
            final AlgorithmIdentifier myAlgId = myASN1.getAlgorithmId();
            return new EncryptedContentInfo(CRMFObjectIdentifiers.id_ct_encKeyWithID, myAlgId, new BEROctetString(myData));

        } catch (IOException e) {
            throw new GordianIOException("Failed to create EncryptedContentInfo", e);
        }
    }

    /**
     * Create Proof of Possession.
     * @param pAlgId the algorithmId
     * @param pEncodedKey the encoded key
     * @param pContent the encryptedContent
     * @return the proof of possession
     */
    ProofOfPossession createProofOfPossession(final AlgorithmIdentifier pAlgId,
                                              final byte[] pEncodedKey,
                                              final EncryptedContentInfo pContent) {
        /* Create the recipient info */
        final IssuerAndSerialNumber myId = new IssuerAndSerialNumber(theTarget.getIssuer().getName(), theTarget.getSerialNo());
        final KeyTransRecipientInfo myKTInfo = new KeyTransRecipientInfo(new RecipientIdentifier(myId), pAlgId, new BEROctetString(pEncodedKey));
        final RecipientInfo myRecInfo = new RecipientInfo(myKTInfo);
        final EnvelopedData myEnvData = new EnvelopedData(null, new BERSet(myRecInfo), pContent, (BERSet) null);
        return new ProofOfPossession(ProofOfPossession.TYPE_KEY_ENCIPHERMENT, new GordianKeyPairCRMBuilder.XPOPOPrivKey(myEnvData));
    }
}
