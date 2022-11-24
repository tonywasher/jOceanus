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
import java.util.Arrays;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.crmf.AttributeTypeAndValue;
import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplateBuilder;
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.crmf.POPOPrivKey;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.jgordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactory;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.jgordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySet;
import net.sourceforge.joceanus.jgordianknot.impl.core.keyset.GordianCoreKeySetFactory;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianCRMEncryptor.GordianCRMResult;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.mac.GordianCoreMac;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * CRM Builder.
 */
public class GordianCRMBuilder {
    /**
     * AttrOID branch.
     */
    public static final ASN1ObjectIdentifier ATTROID = GordianASN1Util.EXTOID.branch("4");

    /**
     * AttrOID branch.
     */
    public static final ASN1ObjectIdentifier MACVALUEATTROID = ATTROID.branch("1");

    /**
     * The # of hash iterations .
     */
    private static final int PBM_ITERATIONS = 10000;

    /**
     * The factory.
     */
    private final GordianCoreFactory theFactory;

    /**
     * The encryptor.
     */
    private final GordianCRMEncryptor theEncryptor;

    /**
     * The target certificate.
     */
    private final GordianCoreCertificate theTarget;

    /**
     * The secret MAC key value.
     */
    private final byte[] theMACSecret;

    /**
     * Constructor.
     * @param pFactory the factory
     * @param pEncryptor the encryptor
     * @param pMACSecret the MAC secret value
     * @param pTarget the target certificate
     */
    public GordianCRMBuilder(final GordianCoreFactory pFactory,
                             final GordianCRMEncryptor pEncryptor,
                             final byte[] pMACSecret,
                             final GordianCoreCertificate pTarget) {
        theFactory = pFactory;
        theEncryptor = pEncryptor;
        theMACSecret = pMACSecret;
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

            /* Create control if necessary */
            AttributeTypeAndValue[] myAttrs = null;
            if (theMACSecret != null) {
                /* Create the PKMACValue Control */
                final PBMParameter myParams = generatePBMParameters();
                final PKMACValue myMACValue = calculatePKMacValue(theFactory, theMACSecret, myCertReq.getCertTemplate().getPublicKey(), myParams);
                myAttrs = new AttributeTypeAndValue[] { new AttributeTypeAndValue(MACVALUEATTROID, myMACValue) };
            }

            /* Create a CRMF */
            final CertReqMsg myReqMsg = new CertReqMsg(myCertReq, myProof, myAttrs);
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
        final GordianSignatureSpec mySignSpec = theFactory.getKeyPairFactory().getSignatureFactory().defaultForKeyPair(mySpec);
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
        /* Prepare for encryption */
        final GordianCRMResult myResult = theEncryptor.prepareForEncryption(theTarget);

        /* Derive the keySet from the key */
        final GordianKeySet myKeySet = myResult.getKeySet();

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = GordianCRMEncryptor.buildEncryptedContentInfo(myKeySet, pPKCS8Encoding, pCertificate);

        /* Create the Proof of possession */
        final EnvelopedData myEnvData = new EnvelopedData(null, new BERSet(myResult.getRecipient()), myInfo, (BERSet) null);
        return new ProofOfPossession(ProofOfPossession.TYPE_KEY_ENCIPHERMENT, new XPOPOPrivKey(myEnvData));
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

    /**
     * Create PBMParameters.
     * @return the PBMParameters
     */
    private PBMParameter generatePBMParameters() {
        /* Create the salt value */
        final byte[] mySalt = new byte[GordianLength.LEN_256.getByteLength()];
        final GordianRandomSource mySource = theFactory.getRandomSource();
        mySource.getRandom().nextBytes(mySalt);

        /* Access algorithm ids */
        final AlgorithmIdentifier myHashId = new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE);
        final AlgorithmIdentifier myMacId = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);

        /* Create the PBMParameter */
        return new PBMParameter(mySalt, myHashId, PBM_ITERATIONS, myMacId);
    }

    /**
     * Calculate PKMacValue.
     * @param pFactory the factory
     * @param pSecret the secret value
     * @param pPublicKey the publicKey
     * @param pParams the PBM Parameters
     * @return the PKMacValue
     * @throws OceanusException on error
     */
    static PKMACValue calculatePKMacValue(final GordianCoreFactory pFactory,
                                          final byte[] pSecret,
                                          final SubjectPublicKeyInfo pPublicKey,
                                          final PBMParameter pParams) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Create the digest */
            final GordianDigestFactory myDigests = pFactory.getDigestFactory();
            final GordianDigestSpec myDigestSpec = pFactory.getDigestSpecForIdentifier(pParams.getOwf());
            final GordianDigest myDigest = myDigests.createDigest(myDigestSpec);

            /* Run through the first iteration */
            myDigest.update(pSecret);
            myDigest.update(pParams.getSalt().getOctets());
            byte[] myKey = new byte[myDigest.getDigestSize()];
            myKey = myDigest.finish(myKey);

            /* Loop through the remaining iterations */
            final int numIterations = pParams.getIterationCount().intValueExact() - 1;
            for (int i = 0; i < numIterations; i++) {
                myKey = myDigest.finish(myKey);
            }

            /* Create the mac */
            final GordianMacFactory myMacs = pFactory.getMacFactory();
            final GordianMacSpec myMacSpec = (GordianMacSpec) pFactory.getKeySpecForIdentifier(pParams.getMac());
            final GordianCoreMac myMac = (GordianCoreMac) myMacs.createMac(myMacSpec);
            myMac.initKeyBytes(myKey);

            /* Create the result */
            myMac.update(pPublicKey.toASN1Primitive().getEncoded());
            final byte[] myResult = myMac.finish();
            return new PKMACValue(pParams, new DERBitString(myResult));

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to parse SubjectPublicKeyInfo", e);
        }
    }
}
