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
import java.util.function.Function;

import org.bouncycastle.asn1.crmf.CertReqMsg;
import org.bouncycastle.asn1.crmf.CertRequest;
import org.bouncycastle.asn1.crmf.CertTemplate;
import org.bouncycastle.asn1.crmf.POPOSigningKey;
import org.bouncycastle.asn1.crmf.ProofOfPossession;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianKeyPairFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSet;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetFactory;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetGenerator;
import net.sourceforge.joceanus.jgordianknot.api.keypairset.GordianKeyPairSetSpec;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairSetCertificate;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyPairUsage;
import net.sourceforge.joceanus.jgordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePairSet;
import net.sourceforge.joceanus.jgordianknot.api.sign.GordianKeyPairSetSignature;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianDataException;
import net.sourceforge.joceanus.jgordianknot.impl.core.base.GordianIOException;
import net.sourceforge.joceanus.jgordianknot.impl.core.keystore.GordianPEMObject.GordianPEMObjectType;
import net.sourceforge.joceanus.jgordianknot.impl.core.sign.GordianCoreSignatureFactory;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * KeyPairSet Certificate Request Message Parser.
 */
public class GordianKeyPairSetCRMParser
        extends GordianCRMParser {
    /**
     * The signer.
     */
    private final GordianKeyStorePairSet theSigner;

    /**
     * Constructor.
     * @param pKeyStoreMgr the keyStoreManager
     * @param pSigner the signer
     * @param pResolver the password resolver
     * @throws OceanusException on error
     */
    public GordianKeyPairSetCRMParser(final GordianCoreKeyStoreManager pKeyStoreMgr,
                                      final GordianKeyStorePairSet pSigner,
                                      final Function<String, char[]> pResolver) throws OceanusException {
        /* Store parameters */
        super(pKeyStoreMgr, pResolver);
        theSigner = pSigner;

        /* Reject null signer */
        if (theSigner == null) {
            throw new GordianDataException("Null keyPairSigner");
        }
    }

    @Override
    public List<GordianPEMObject> decodeCertificateRequest(final GordianPEMObject pObject) throws OceanusException {
        /* Reject if not KeyPairCertReq */
        GordianPEMCoder.checkObjectType(pObject, GordianPEMObjectType.KEYPAIRSETCERTREQ);

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
        final List<GordianKeyPairSetCertificate> myChain = getKeyStoreMgr().signKeyPairSet(myPairSet, mySubject, myUsage, theSigner);

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

        /* Handle encrypted keyPair */
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
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairSetFactory mySetFactory = myFactory.getKeyPairSetFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSetSpec myKeySpec = mySetFactory.determineKeyPairSetSpec(myX509Spec);
            final GordianKeyPairSetGenerator myGenerator = mySetFactory.getKeyPairSetGenerator(myKeySpec);
            final GordianKeyPairSet myKeyPairSet = myGenerator.derivePublicOnlyKeyPairSet(myX509Spec);

            /* Access the verifier */
            final POPOSigningKey mySigning = (POPOSigningKey) pProof.getObject();
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
            throw new GordianIOException("Failed to derive signed keyPairSet", e);
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
            /* Access the generator */
            final GordianKeyPairFactory myFactory = getFactory().getKeyPairFactory();
            final GordianKeyPairSetFactory mySetFactory = myFactory.getKeyPairSetFactory();
            final X509EncodedKeySpec myX509Spec = new X509EncodedKeySpec(pPublicKey.getEncoded());
            final GordianKeyPairSetSpec myKeySpec = mySetFactory.determineKeyPairSetSpec(myX509Spec);
            final GordianKeyPairSetGenerator myGenerator = mySetFactory.getKeyPairSetGenerator(myKeySpec);

            /* derive the privateKey and keyPairSet */
            final PKCS8EncodedKeySpec myPKCS8Spec = derivePrivateKey(pProof, pSubject);
            return myGenerator.deriveKeyPairSet(myX509Spec, myPKCS8Spec);

        } catch (IOException e) {
            throw new GordianIOException("Failed to derive encrypted keyPairSet", e);
        }
    }
}
