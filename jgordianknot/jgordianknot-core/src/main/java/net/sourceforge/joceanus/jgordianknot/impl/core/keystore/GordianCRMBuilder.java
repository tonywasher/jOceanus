/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2021 Tony Washer
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
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BEROctetString;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientIdentifier;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.crmf.CRMFObjectIdentifiers;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.EncKeyWithID;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.agree.GordianCoreAgreementFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.encrypt.GordianCoreEncryptorFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianKeySetSpecASN1;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * CRM Builder.
 */
public class GordianCRMBuilder {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The target certificate.
     */
    private final GordianCoreCertificate theTarget;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public GordianCRMBuilder(final GordianCoreFactory pFactory) {
        this(pFactory, null);
    }

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pTarget the target certificate
     */
    public GordianCRMBuilder(final GordianCoreFactory pFactory,
                             final GordianCoreCertificate pTarget) {
        theFactory = pFactory;
        theTarget = pTarget;
    }

    /**
     * Create a Certificate request.
     * @param pKeyPair the keyStore entry
     * @return the encoded PEM object
     * @throws OceanusException on error
     */
    public GordianPEMObject createCertificateRequest(final GordianKeyStorePair pKeyPair) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the certificate */
            final GordianCoreCertificate myCert = (GordianCoreCertificate) pKeyPair.getCertificateChain().get(0);

            /* Create the Certificate request */
            final CertRequest myCertReq = createCertRequest(myCert);

            /* Create the ProofOfPossession */
            final ProofOfPossession myProof = createKeyPairProofOfPossession(pKeyPair, myCert, myCertReq);

            /* Create a CRMF */
            final CertReqMsg myReqMsg = new CertReqMsg(myCertReq, myProof, null);
            final GordianPEMObjectType myObjectType = GordianPEMObjectType.CERTREQ;
            return new GordianPEMObject(myObjectType, myReqMsg.getEncoded());

        } catch (IOException e) {
            throw new GordianIOException("Failed to create CRMF", e);
        }
    }

    /**
     * Create a Certificate request.
     * @param pCertificate the local certificate
     * @return the certificate request
     * @throws OceanusException on error
     */
    private static CertRequest createCertRequest(final GordianCoreCertificate pCertificate) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the Certificate template Builder */
            final CertTemplateBuilder myBuilder = new CertTemplateBuilder();

            /* Record certificate name */
            myBuilder.setSubject(pCertificate.getSubject().getName());

            /* Record certificate publicKey */
            final X509EncodedKeySpec myX509 = pCertificate.getX509KeySpec();
            final SubjectPublicKeyInfo myPublicInfo = SubjectPublicKeyInfo.getInstance(myX509.getEncoded());
            myBuilder.setPublicKey(myPublicInfo);

            /* record extensions */
            final ExtensionsGenerator myGenerator = new ExtensionsGenerator();
            myGenerator.addExtension(Extension.keyUsage, true, pCertificate.getUsage().getKeyUsage());
            myGenerator.addExtension(Extension.basicConstraints, false, new BasicConstraints(false));
            myBuilder.setExtensions(myGenerator.generate());

            /* Using the current timestamp as the certificate serial number */
            final int myNow = (int) System.currentTimeMillis();

            /* Create the Certificate request */
            return new CertRequest(myNow, myBuilder.build(), null);

        } catch (IOException e) {
            throw new GordianIOException("Failed to create Certificate request", e);
        }
    }

    /**
     * Create KeyPair Proof of Possession.
     * @param pKeyPair the keyStore entry
     * @param pCertificate the local certificate
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    private ProofOfPossession createKeyPairProofOfPossession(final GordianKeyStorePair pKeyPair,
                                                             final GordianCoreCertificate pCertificate,
                                                             final CertRequest pCertRequest) throws OceanusException {
        /* Try to send a signed proof */
        final GordianKeyPair myKeyPair = pKeyPair.getKeyPair();
        final GordianKeyPairSpec mySpec = myKeyPair.getKeyPairSpec();
        final GordianSignatureSpec mySignSpec = GordianSignatureSpec.defaultForKey(mySpec);
        if (mySignSpec != null) {
            return createKeyPairSignedProof(myKeyPair, mySignSpec, pCertRequest);
        }

        /* Obtain the PKCS8Encoding of the private key */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(mySpec);
        final PKCS8EncodedKeySpec myPKCS8Encoding = myGenerator.getPKCS8Encoding(myKeyPair);

        /* Send encrypted key via Encryption or Agreement */
        return createTargetedProofOfPossession(myPKCS8Encoding, pCertificate);
    }

    /**
     * Create Targeted Proof of Possession.
     * @param pPKCS8Encoding the PKCS8Encoding
     * @param pCertificate the local certificate
     * @return the proof of possession
     * @throws OceanusException on error
     */
    private ProofOfPossession createTargetedProofOfPossession(final PKCS8EncodedKeySpec pPKCS8Encoding,
                                                              final GordianCoreCertificate pCertificate) throws OceanusException {
        /* Try to send an encrypted proof */
        final GordianKeyPair myKeyPair = theTarget.getKeyPair();
        final GordianKeyPairSpec mySpec = myKeyPair.getKeyPairSpec();
        final GordianEncryptorSpec myEncSpec = GordianEncryptorSpec.defaultForKey(mySpec);
        if (myEncSpec != null) {
            return createKeyPairEncryptedProof(pPKCS8Encoding, myEncSpec, pCertificate, theTarget);
        }

        /* Try to send an agreed proof */
        final GordianAgreementSpec myAgreeSpec = GordianAgreementSpec.defaultForKey(mySpec);
        if (myAgreeSpec != null) {
            return createKeyPairAgreedProof(pPKCS8Encoding, myAgreeSpec, pCertificate, myKeyPair);
        }

        /* Reject the request */
        throw new GordianDataException("Unable to create ProofOfPossession");
    }

    /**
     * Create a Signed KeyPair Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pSignSpec the signatureSpec
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createKeyPairSignedProof(final GordianKeyPair pKeyPair,
                                               final GordianSignatureSpec pSignSpec,
                                               final CertRequest pCertRequest) throws OceanusException {
        /* Create the signer */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
        final GordianSignature mySigner = mySignFactory.createSigner(pSignSpec);
        final AlgorithmIdentifier myAlgId = mySignFactory.getIdentifierForSpecAndKeyPair(pSignSpec, pKeyPair);

        /* Build the signed proof */
        return createSignedProof(pKeyPair, myAlgId, mySigner, pCertRequest);
    }

    /**
     * Create a Signed Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pAlgId the algorithmId
     * @param pSigner the signer
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    private static ProofOfPossession createSignedProof(final GordianKeyPair pKeyPair,
                                                       final AlgorithmIdentifier pAlgId,
                                                       final GordianSignature pSigner,
                                                       final CertRequest pCertRequest) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the signature */
            pSigner.initForSigning(pKeyPair);
            pSigner.update(pCertRequest.getEncoded());
            final byte[] mySignature = pSigner.sign();

            /* Build the signing key */
            final POPOSigningKey myKey = new POPOSigningKey(null, pAlgId, new DERBitString(mySignature));
            return new ProofOfPossession(myKey);

        } catch (IOException e) {
            throw new GordianIOException("Failed to create Signed Proof of Possession", e);
        }
    }

    /**
     * Create a KeyPair Encrypted Proof of Possession.
     * @param pPKCS8Encoding the PKCS8Encoded privateKey
     * @param pEncryptorSpec the encryptorSpec
     * @param pCertificate the local certificate
     * @param pTargetCertificate the target certificate
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createKeyPairEncryptedProof(final PKCS8EncodedKeySpec pPKCS8Encoding,
                                                  final GordianEncryptorSpec pEncryptorSpec,
                                                  final GordianCertificate pCertificate,
                                                  final GordianCoreCertificate pTargetCertificate) throws OceanusException {
        /* Create the random key */
        final byte[] myKey = createKeyForKeySet();

        /* Derive the keySet from the key */
        final GordianKeySet myKeySet = deriveKeySetFromKey(theFactory, myKey);

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = buildEncryptedContentInfo(myKeySet, pPKCS8Encoding, pCertificate);

        /* Create the proof of Possession */
        return encryptKeyWithKeyPair(myKey, myInfo, pTargetCertificate, pEncryptorSpec);
    }

    /**
     * Create a KeyPair Agreed Proof of Possession.
     * @param pPKCS8Encoding the PKCS8Encoded privateKey
     * @param pAgreeSpec the agreementSpec
     * @param pCertificate the local certificate
     * @param pKeyPair the target keyPair
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createKeyPairAgreedProof(final PKCS8EncodedKeySpec pPKCS8Encoding,
                                               final GordianAgreementSpec pAgreeSpec,
                                               final GordianCertificate pCertificate,
                                               final GordianKeyPair pKeyPair) throws OceanusException {
        /* Create the agreement */
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myFactory.getAgreementFactory();
        final GordianAnonymousAgreement myAgree = (GordianAnonymousAgreement) myAgreeFactory.createAgreement(pAgreeSpec);
        myAgree.setResultType(new GordianKeySetSpec());
        final byte[] myHello = myAgree.createClientHello(pKeyPair);
        final GordianKeySet myKeySet = (GordianKeySet) myAgree.getResult();

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = buildEncryptedContentInfo(myKeySet, pPKCS8Encoding, pCertificate);

        /* Create the proof of Possession */
        return createProofOfPossession(myAgreeFactory.getIdentifierForSpec(pAgreeSpec), myHello, myInfo);
    }

    /**
     * Create a random key for KeySet.
     * @return the new key
     */
    private byte[] createKeyForKeySet() {
        final byte[] myKey = new byte[GordianLength.LEN_1024.getByteLength()];
        theFactory.getRandomSource().getRandom().nextBytes(myKey);
        return myKey;
    }

    /**
     * Derive a keySet from a key.
     * @param pFactory the factory
     * @param pKey the key
     * @return the keySet
     * @throws OceanusException on error
     */
    static GordianKeySet deriveKeySetFromKey(final GordianCoreFactory pFactory,
                                             final byte[] pKey) throws OceanusException {
        /* Create a new Factory using the key */
        final byte[] myPhrase = Arrays.copyOf(pKey, GordianLength.LEN_256.getByteLength());
        final GordianParameters myParams = new GordianParameters(GordianFactoryType.BC);
        myParams.setSecurityPhrase(myPhrase);
        myParams.setInternal();
        final GordianFactory myFactory = pFactory.newFactory(myParams);
        Arrays.fill(myPhrase, (byte) 0);

        /* Create keySet from key */
        final byte[] mySecret = Arrays.copyOfRange(pKey, GordianLength.LEN_512.getByteLength(), GordianLength.LEN_1024.getByteLength());
        final byte[] myIV = Arrays.copyOfRange(pKey, GordianLength.LEN_256.getByteLength(), GordianLength.LEN_512.getByteLength());
        final GordianCoreKeySetFactory myKeySets = (GordianCoreKeySetFactory) myFactory.getKeySetFactory();
        final GordianCoreKeySet myKeySet = myKeySets.createKeySet(new GordianKeySetSpec());
        myKeySet.buildFromSecret(mySecret, myIV);
        Arrays.fill(mySecret, (byte) 0);
        Arrays.fill(myIV, (byte) 0);
        return myKeySet;
    }

    /**
     * Encrypt the key with a keyPair.
     * @param pKey the key to encrypt
     * @param pInfo the encrypted Info
     * @param pCertificate the target certificate
     * @param pSpec the encryptorSpec
     * @return the encrypted key
     * @throws OceanusException on error
     */
    private ProofOfPossession encryptKeyWithKeyPair(final byte[] pKey,
                                                    final EncryptedContentInfo pInfo,
                                                    final GordianCoreCertificate pCertificate,
                                                    final GordianEncryptorSpec pSpec) throws OceanusException {
        /* Create the encrypted key */
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianEncryptor myEncryptor = myEncFactory.createEncryptor(pSpec);

        /* Create the encrypted key */
        myEncryptor.initForEncrypt(pCertificate.getKeyPair());
        final byte[] myEncryptedKey = myEncryptor.encrypt(pKey);
        Arrays.fill(pKey, (byte) 0);

        /* Create the proof of Possession */
        return createProofOfPossession(myEncFactory.getIdentifierForSpec(pSpec), myEncryptedKey, pInfo);
    }

    /**
     * Build the encryptedContentInfo.
     * @param pKeySet the keySet to encrypt with
     * @param pPKCS8Encoding the PKCS8Encoded privateKey
     * @param pCertificate the local certificate
     * @return the encryptedContentInfo
     * @throws OceanusException on error
     */
    private static EncryptedContentInfo buildEncryptedContentInfo(final GordianKeySet pKeySet,
                                                                  final PKCS8EncodedKeySpec pPKCS8Encoding,
                                                                  final GordianCertificate pCertificate) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Obtain the PrivateKeyInfo */
            final PrivateKeyInfo myInfo = PrivateKeyInfo.getInstance(pPKCS8Encoding.getEncoded());
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
    private ProofOfPossession createProofOfPossession(final AlgorithmIdentifier pAlgId,
                                                      final byte[] pEncodedKey,
                                                      final EncryptedContentInfo pContent) {
        /* Create the recipient info */
        final IssuerAndSerialNumber myId = new IssuerAndSerialNumber(theTarget.getIssuer().getName(), theTarget.getSerialNo());
        final KeyTransRecipientInfo myKTInfo = new KeyTransRecipientInfo(new RecipientIdentifier(myId), pAlgId, new BEROctetString(pEncodedKey));
        final RecipientInfo myRecInfo = new RecipientInfo(myKTInfo);
        final EnvelopedData myEnvData = new EnvelopedData(null, new BERSet(myRecInfo), pContent, (BERSet) null);
        return new ProofOfPossession(ProofOfPossession.TYPE_KEY_ENCIPHERMENT, new XPOPOPrivKey(myEnvData));
    }

    /**
     * Extended POPOPrivKey to allow encryptedKey.
     */
    static class XPOPOPrivKey extends POPOPrivKey {
        /**
         * The encrypted key.
         */
        private final EnvelopedData theKey;

        /**
         * Constructor.
         * @param pEncryptedKey the encryptedKey.
         */
        XPOPOPrivKey(final EnvelopedData pEncryptedKey) {
            super((PKMACValue) null);
            theKey = pEncryptedKey;
        }

        @Override
        public ASN1Primitive toASN1Primitive() {
            return new DERTaggedObject(false, encryptedKey, theKey);
        }
    }
}
