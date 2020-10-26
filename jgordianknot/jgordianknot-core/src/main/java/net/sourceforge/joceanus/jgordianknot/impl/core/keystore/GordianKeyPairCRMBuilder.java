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

import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKDFType;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAgreementSpec;
import net.sourceforge.joceanus.jgordianknot.api.agree.GordianKeyPairAnonymousAgreement;
import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianEncryptorSpec;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianKeyPairEncryptor;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianMcElieceEncryptionType;
import net.sourceforge.joceanus.jgordianknot.api.encrypt.GordianSM2EncryptionSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUse;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSignature;
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
 * KeyPair Certificate Request Message Builder.
 */
public class GordianKeyPairCRMBuilder {
    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The target certificate.
     */
    private final GordianCoreKeyPairCertificate theTarget;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pTarget the target certificate
     */
    public GordianKeyPairCRMBuilder(final GordianCoreFactory pFactory,
                                    final GordianCoreKeyPairCertificate pTarget) {
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
            final GordianCoreKeyPairCertificate myCert = (GordianCoreKeyPairCertificate) pKeyPair.getCertificateChain().get(0);

            /* Create the Certificate request */
            final CertRequest myCertReq = createCertRequest(myCert);

            /* Create the ProofOfPossession */
            final ProofOfPossession myProof = createProofOfPossession(pKeyPair, myCert, myCertReq);

            /* Create a CRMF */
            final CertReqMsg myReqMsg = new CertReqMsg(myCertReq, myProof, null);
            return new GordianPEMObject(GordianPEMObjectType.KEYPAIRCERTREQ, myReqMsg.getEncoded());

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
    static CertRequest createCertRequest(final GordianCoreCertificate<?, ?> pCertificate) throws OceanusException {
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

             /* Create the Certificate request */
            return new CertRequest(1, myBuilder.build(), null);

        } catch (IOException e) {
            throw new GordianIOException("Failed to create Certificate request", e);
        }
    }

    /**
     * Create Proof of Possession.
     * @param pKeyPair the keyStore entry
     * @param pCertificate the local certificate
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    private ProofOfPossession createProofOfPossession(final GordianKeyStorePair pKeyPair,
                                                      final GordianCoreKeyPairCertificate pCertificate,
                                                      final CertRequest pCertRequest) throws OceanusException {
        /* Try to send a signed proof */
        final GordianKeyPair myKeyPair = pKeyPair.getKeyPair();
        final GordianSignatureSpec mySpec = GordianSignatureSpec.defaultForKey(myKeyPair.getKeyPairSpec());
        if (mySpec != null) {
            return createSignedProof(myKeyPair, mySpec, pCertRequest);
        }

        /* Send encrypted key via Encryption or Agreement */
        final GordianKeyPairUsage myUsage = theTarget.getUsage();
        return myUsage.hasUse(GordianKeyPairUse.KEYENCRYPT)
                ? createEncryptedProof(myKeyPair, pCertificate)
                : createAgreedProof(myKeyPair, pCertificate);
    }

    /**
     * Create a Signed Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pSignSpec the signatureSpec
     * @param pCertRequest the certificate request
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createSignedProof(final GordianKeyPair pKeyPair,
                                        final GordianSignatureSpec pSignSpec,
                                        final CertRequest pCertRequest) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the signer */
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final GordianCoreSignatureFactory mySignFactory = (GordianCoreSignatureFactory) myFactory.getSignatureFactory();
            final GordianKeyPairSignature mySigner = mySignFactory.createKeyPairSigner(pSignSpec);

            /* Create the signature */
            mySigner.initForSigning(pKeyPair);
            mySigner.update(pCertRequest.getEncoded());
            final byte[] mySignature = mySigner.sign();

            /* Build the signing key */
            final AlgorithmIdentifier myAlgId = mySignFactory.getIdentifierForSpecAndKeyPair(pSignSpec, pKeyPair);
            final POPOSigningKey myKey = new POPOSigningKey(null, myAlgId, new DERBitString(mySignature));
            return new ProofOfPossession(myKey);

        } catch (IOException e) {
            throw new GordianIOException("Failed to create Signed Proof of Possession", e);
        }
    }

    /**
     * Create an Encrypted Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pCertificate the local certificate
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createEncryptedProof(final GordianKeyPair pKeyPair,
                                           final GordianCoreKeyPairCertificate pCertificate) throws OceanusException {
        /* Create the random key */
        final byte[] myKey = new byte[GordianLength.LEN_1024.getByteLength()];
        theFactory.getRandomSource().getRandom().nextBytes(myKey);

        /* Derive the keySet from the key */
        final GordianKeySet myKeySet = deriveKeySetFromKey(theFactory, myKey);

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = buildEncryptedContentInfo(myKeySet, pKeyPair, pCertificate);

        /* Create the encrypted key */
        final GordianEncryptorSpec mySpec = getEncryptionSpec(theTarget.getKeyPair());
        final GordianKeyPairFactory myKPFactory = theFactory.getKeyPairFactory();
        final GordianCoreEncryptorFactory myEncFactory = (GordianCoreEncryptorFactory) myKPFactory.getEncryptorFactory();
        final GordianKeyPairEncryptor myEncryptor = myEncFactory.createKeyPairEncryptor(mySpec);
        myEncryptor.initForEncrypt(theTarget.getKeyPair());
        final byte[] myEncryptedKey = myEncryptor.encrypt(myKey);
        Arrays.fill(myKey, (byte) 0);

        /* Create the proof of Possession */
        return createProofOfPossession(myEncFactory.getIdentifierForSpec(mySpec), myEncryptedKey, myInfo);
    }

    /**
     * Create an Agreed Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pCertificate the local certificate
     * @return the proof of possession
     * @throws OceanusException on error
     */
    ProofOfPossession createAgreedProof(final GordianKeyPair pKeyPair,
                                        final GordianCoreKeyPairCertificate pCertificate) throws OceanusException {
        /* Create the agreement */
        final GordianKeyPairAgreementSpec mySpec = getAgreementSpec(theTarget.getKeyPair());
        final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
        final GordianCoreAgreementFactory myAgreeFactory = (GordianCoreAgreementFactory) myFactory.getAgreementFactory();
        final GordianKeyPairAnonymousAgreement myAgree = (GordianKeyPairAnonymousAgreement) myAgreeFactory.createKeyPairAgreement(mySpec);
        myAgree.setResultType(new GordianKeySetSpec());
        final byte[] myHello = myAgree.createClientHello(theTarget.getKeyPair());
        final GordianKeySet myKeySet = (GordianKeySet) myAgree.getResult();

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = buildEncryptedContentInfo(myKeySet, pKeyPair, pCertificate);

        /* Create the proof of Possession */
        return createProofOfPossession(myAgreeFactory.getIdentifierForSpec(mySpec), myHello, myInfo);
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
     * Build the encryptedContentInfo.
     * @param pKeySet the keySet to encrypt with
     * @param pKeyPair the keyPair
     * @param pCertificate the local certificate
     * @return the encryptedContentInfo
     * @throws OceanusException on error
     */
    private EncryptedContentInfo buildEncryptedContentInfo(final GordianKeySet pKeySet,
                                                           final GordianKeyPair pKeyPair,
                                                           final GordianCoreKeyPairCertificate pCertificate) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Obtain the PrivateKeyInfo */
            final GordianKeyPairFactory myFactory = theFactory.getKeyPairFactory();
            final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(pKeyPair.getKeyPairSpec());
            final PKCS8EncodedKeySpec mySpec = myGenerator.getPKCS8Encoding(pKeyPair);
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
        return new ProofOfPossession(ProofOfPossession.TYPE_KEY_ENCIPHERMENT, new XPOPOPrivKey(myEnvData));
    }

    /**
     * Obtain encryptionSpec check for keyPair.
     * @param pKeyPair the keyPair
     * @return the encryptionSpec
     * @throws OceanusException on error
     */
    private static GordianEncryptorSpec getEncryptionSpec(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Switch on keyType */
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        switch (mySpec.getKeyPairType()) {
            case RSA:
                return GordianEncryptorSpec.rsa(GordianDigestSpec.sha2(GordianLength.LEN_512));
            case EC:
            case SM2:
            case GOST2012:
                return GordianEncryptorSpec.sm2(GordianSM2EncryptionSpec.c1c2c3(GordianDigestSpec.sm3()));
            case ELGAMAL:
                return GordianEncryptorSpec.elGamal(GordianDigestSpec.sha2(GordianLength.LEN_512));
            case MCELIECE:
                return mySpec.getMcElieceKeySpec().isCCA2()
                        ? GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.FUJISAKI)
                        : GordianEncryptorSpec.mcEliece(GordianMcElieceEncryptionType.STANDARD);
            case DH:
            case XDH:
            case NEWHOPE:
            case DSA:
            case EDDSA:
            case DSTU4145:
            case RAINBOW:
            case SPHINCS:
            case QTESLA:
            case XMSS:
            case LMS:
            default:
                throw new GordianDataException("Unexpected keyPair type");
        }
    }

    /**
     * Obtain encryptionSpec check for keyPair.
     * @param pKeyPair the keyPair
     * @return the encryptionSpec
     * @throws OceanusException on error
     */
    private static GordianKeyPairAgreementSpec getAgreementSpec(final GordianKeyPair pKeyPair) throws OceanusException {
        /* Switch on keyType */
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        switch (mySpec.getKeyPairType()) {
            case DH:
                return GordianKeyPairAgreementSpec.dhAnon(GordianKDFType.SHA256KDF);
            case XDH:
                return mySpec.getEdwardsElliptic().is25519()
                        ? GordianKeyPairAgreementSpec.xdhAnon(GordianKDFType.SHA256KDF)
                        : GordianKeyPairAgreementSpec.xdhAnon(GordianKDFType.SHA512KDF);
            case NEWHOPE:
                return GordianKeyPairAgreementSpec.newHope(GordianKDFType.SHA256KDF);
            case RSA:
            case EC:
            case SM2:
            case GOST2012:
            case ELGAMAL:
            case MCELIECE:
            case DSA:
            case EDDSA:
            case DSTU4145:
            case RAINBOW:
            case SPHINCS:
            case QTESLA:
            case XMSS:
            case LMS:
            default:
                throw new GordianDataException("Unexpected keyPair type");
        }
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
