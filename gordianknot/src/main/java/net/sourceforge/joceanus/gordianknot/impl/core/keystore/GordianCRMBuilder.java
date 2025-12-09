/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.base.GordianLength;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigest;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestFactory;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpec;
import net.sourceforge.joceanus.gordianknot.api.digest.GordianDigestSpecBuilder;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPair;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairGenerator;
import net.sourceforge.joceanus.gordianknot.api.keypair.GordianKeyPairSpec;
import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMac;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacFactory;
import net.sourceforge.joceanus.gordianknot.api.mac.GordianMacSpec;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignParams;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignature;
import net.sourceforge.joceanus.gordianknot.api.sign.GordianSignatureSpec;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianCoreFactory;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianRandomSource;
import net.sourceforge.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCRMEncryptor.GordianCRMResult;
import net.sourceforge.joceanus.gordianknot.impl.core.sign.GordianCoreSignatureFactory;
import org.bouncycastle.asn1.ASN1Object;
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
import org.bouncycastle.asn1.crmf.SubsequentMessage;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.ExtensionsGenerator;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import java.io.IOException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

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
     * The gateway.
     */
    private final GordianCoreKeyStoreGateway theGateway;

    /**
     * Constructor.
     * @param pGateway the gateway
     */
    GordianCRMBuilder(final GordianCoreKeyStoreGateway pGateway) {
        theGateway = pGateway;
    }

    /**
     * Create a Certificate request.
     * @param pKeyPair the keyStore entry
     * @param pRequestId the reqId
     * @return the certificate request message
     * @throws GordianException on error
     */
    public CertReqMsg createCertificateRequest(final GordianKeyStorePair pKeyPair,
                                               final int pRequestId) throws GordianException {
        /* Access the certificate */
        final GordianCoreCertificate myCert = (GordianCoreCertificate) pKeyPair.getCertificateChain().get(0);

        /* Create the Certificate request */
        final CertRequest myCertReq = createCertRequest(myCert, pRequestId);

        /* Create the ProofOfPossession */
        final ProofOfPossession myProof = createKeyPairProofOfPossession(pKeyPair, myCert, myCertReq);

        /* Create control if necessary */
        AttributeTypeAndValue[] myAttrs = null;
        final X500Name myName = myCert.getSubjectName();
        final byte[] myMACSecret = theGateway.getMACSecret(myName);
        if (myMACSecret != null) {
            /* Create the PKMACValue Control */
            final PKMACValue myMACValue = createPKMACValue(myMACSecret, myCertReq.getCertTemplate().getPublicKey());
            myAttrs = new AttributeTypeAndValue[] { new AttributeTypeAndValue(MACVALUEATTROID, myMACValue) };
        }

        /* Create a CRMF */
        return new CertReqMsg(myCertReq, myProof, myAttrs);
    }

    /**
     * Create a Certificate request.
     * @param pCertificate the local certificate
     * @param pRequestId the reqId
     * @return the certificate request
     * @throws GordianException on error
     */
    private static CertRequest createCertRequest(final GordianCoreCertificate pCertificate,
                                                 final int pRequestId) throws GordianException {
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
            return new CertRequest(pRequestId, myBuilder.build(), null);

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
     * @throws GordianException on error
     */
    private ProofOfPossession createKeyPairProofOfPossession(final GordianKeyStorePair pKeyPair,
                                                             final GordianCoreCertificate pCertificate,
                                                             final CertRequest pCertRequest) throws GordianException {
        /* Try to send a signed proof */
        final GordianKeyPair myKeyPair = pKeyPair.getKeyPair();
        final GordianKeyPairSpec mySpec = myKeyPair.getKeyPairSpec();
        final GordianSignatureSpec mySignSpec = theGateway.getFactory().getKeyPairFactory().getSignatureFactory().defaultForKeyPair(mySpec);
        if (mySignSpec != null) {
            return createKeyPairSignedProof(myKeyPair, mySignSpec, pCertRequest);
        }

        /* Send encrypted key via targeted encryption or request encrypted certificate */
        final GordianCoreCertificate myTarget = theGateway.getTarget();
        return myTarget != null
                ? createTargetedProofOfPossession(myKeyPair, pCertificate)
                : new ProofOfPossession(ProofOfPossession.TYPE_KEY_ENCIPHERMENT, new POPOPrivKey(SubsequentMessage.encrCert));
    }

    /**
     * Create Targeted Proof of Possession.
     * @param pKeyPair the keyPair
     * @param pCertificate the local certificate
     * @return the proof of possession
     * @throws GordianException on error
     */
    private ProofOfPossession createTargetedProofOfPossession(final GordianKeyPair pKeyPair,
                                                              final GordianCoreCertificate pCertificate) throws GordianException {
        /* Obtain the PKCS8Encoding of the private key */
        final GordianKeyPairFactory myFactory = theGateway.getFactory().getKeyPairFactory();
        final GordianKeyPairSpec mySpec = pKeyPair.getKeyPairSpec();
        final GordianKeyPairGenerator myGenerator = myFactory.getKeyPairGenerator(mySpec);
        final PKCS8EncodedKeySpec myPKCS8Encoding = myGenerator.getPKCS8Encoding(pKeyPair);

        /* Prepare for encryption */
        final GordianCRMEncryptor myEncryptor = theGateway.getEncryptor();
        final GordianCoreCertificate myTarget = theGateway.getTarget();
        final GordianCRMResult myResult = myEncryptor.prepareForEncryption(myTarget);

        /* Derive the keySet from the key */
        final GordianKeySet myKeySet = myResult.getKeySet();

        /* Create the encrypted data */
        final EncryptedContentInfo myInfo = GordianCRMEncryptor.buildEncryptedContentInfo(myKeySet, myPKCS8Encoding, pCertificate);

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
     * @throws GordianException on error
     */
    ProofOfPossession createKeyPairSignedProof(final GordianKeyPair pKeyPair,
                                               final GordianSignatureSpec pSignSpec,
                                               final CertRequest pCertRequest) throws GordianException {
        /* Create the signer */
        final GordianKeyPairFactory myFactory = theGateway.getFactory().getKeyPairFactory();
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
     * @throws GordianException on error
     */
    private static ProofOfPossession createSignedProof(final GordianKeyPair pKeyPair,
                                                       final AlgorithmIdentifier pAlgId,
                                                       final GordianSignature pSigner,
                                                       final CertRequest pCertRequest) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Create the signature */
            pSigner.initForSigning(GordianSignParams.keyPair(pKeyPair));
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
     * Create MACValue.
     * @param pSecret the secret
     * @param pData the data to calculate over
     * @return the MACValue
     * @throws GordianException on error
     */
    public PKMACValue createPKMACValue(final byte[] pSecret,
                                       final ASN1Object pData) throws GordianException {
        final PBMParameter myParams = generatePBMParameters();
        return calculatePKMacValue(theGateway.getFactory(), pSecret, pData, myParams);
    }

    /**
     * Check PKMACValue.
     * @param pSecret the secret
     * @param pData the data to calculate over
     * @param pMACValue the supplied MACValue
     * @throws GordianException on error
     */
    public void checkPKMACValue(final byte[] pSecret,
                                final ASN1Object pData,
                                final PKMACValue pMACValue) throws GordianException {
        final PBMParameter myParams = PBMParameter.getInstance(pMACValue.getAlgId().getParameters());
        final PKMACValue myMACValue = calculatePKMacValue(theGateway.getFactory(), pSecret, pData, myParams);
        if (!pMACValue.equals(myMACValue)) {
            throw new GordianDataException("Invalid PKMacValue");
        }
    }

    /**
     * Create PBMParameters.
     * @return the PBMParameters
     */
    private PBMParameter generatePBMParameters() {
        /* Create the salt value */
        final byte[] mySalt = new byte[GordianLength.LEN_256.getByteLength()];
        final GordianRandomSource mySource = theGateway.getFactory().getRandomSource();
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
     * @param pObject the object
     * @param pParams the PBM Parameters
     * @return the PKMacValue
     * @throws GordianException on error
     */
    private static PKMACValue calculatePKMacValue(final GordianCoreFactory pFactory,
                                                  final byte[] pSecret,
                                                  final ASN1Object pObject,
                                                  final PBMParameter pParams) throws GordianException {
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
            final GordianMac myMac = myMacs.createMac(myMacSpec);
            myMac.initKeyBytes(myKey);

            /* Create the result */
            myMac.update(pObject.toASN1Primitive().getEncoded());
            final byte[] myResult = myMac.finish();
            return new PKMACValue(pParams, new DERBitString(myResult));

            /* Handle exceptions */
        } catch (IOException e) {
            throw new GordianIOException("Failed to calculate PKMACValue", e);
        }
    }

    /**
     * Calculate AckValue.
     * @param pCertificate the certificate
     * @return the AckValue
     * @throws GordianException on error
     */
    public byte[] calculateAckValue(final GordianCoreCertificate pCertificate) throws GordianException {
        /* Access the MACSecret */
        final X500Name mySubject = pCertificate.getSubjectName();
        final byte[] myMACSecret = theGateway.getMACSecret(mySubject);

        /* Create the digest */
        final GordianCoreFactory myFactory = theGateway.getFactory();
        final GordianDigestFactory myDigests = myFactory.getDigestFactory();
        final GordianDigest myDigest = myDigests.createDigest(GordianDigestSpecBuilder.sha2(GordianLength.LEN_256));

        /* Calculate the digest */
        if (myMACSecret != null) {
            myDigest.update(myMACSecret);
        }
        myDigest.update(pCertificate.getEncoded());
        return myDigest.finish();
    }
}
