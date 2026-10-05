/*
 * GordianKnot: Security Suite
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway;

import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayResponse;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus.GordianCertFailure;
import io.github.tonywasher.joceanus.gordianknot.api.certgateway.GordianCertGatewayStatus.GordianCertStatus;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianDataException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianIOException;
import io.github.tonywasher.joceanus.gordianknot.api.exc.GordianLogicException;
import io.github.tonywasher.joceanus.gordianknot.api.keyset.GordianKeySet;
import io.github.tonywasher.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import io.github.tonywasher.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import io.github.tonywasher.joceanus.gordianknot.impl.core.cert.GordianCoreCertificate;
import io.github.tonywasher.joceanus.gordianknot.impl.core.certgateway.GordianCertGatewayEncryptor.GordianCertGatewayResult;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.BERSet;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cms.EncryptedContentInfo;
import org.bouncycastle.asn1.cms.EnvelopedData;
import org.bouncycastle.asn1.cms.KeyTransRecipientInfo;
import org.bouncycastle.asn1.cms.RecipientInfo;
import org.bouncycastle.asn1.x509.Certificate;

import java.util.Enumeration;
import java.util.List;

/**
 * Certificate Gateway Response ASN1.
 * <pre>
 * GordianCertResponseASN1 ::= SEQUENCE {
 *      certReqId    INTEGER
 *      certRespId   INTEGER
 *      status       GordianCertStatusASN1
 *      CHOICE {
 *          certificate     [1] Certificate,
 *          encrypted       [2] EnvelopedData
 *      } OPTIONAL
 *      signerCerts  SEQUENCE SIZE (1..MAX) OF Certificate OPTIONAL
 * }
 * </pre>
 */
public final class GordianCertGatewayResponseASN1
        extends GordianASN1Object
        implements GordianCertGatewayResponse {
    /**
     * The standard certificate tag.
     */
    private static final int TAG_STANDARD = 1;

    /**
     * The encrypted certificate tag.
     */
    private static final int TAG_ENCRYPTED = 2;

    /**
     * The requestId.
     */
    private final int theReqId;

    /**
     * The responseId.
     */
    private final int theRespId;

    /**
     * The status.
     */
    private final GordianCertGatewayStatusASN1 theStatus;

    /**
     * The signer certificates.
     */
    private Certificate[] theSignerCerts;

    /**
     * The certificate.
     */
    private Certificate theCertificate;

    /**
     * The encoded certificate.
     */
    private EnvelopedData theEncrypted;

    /**
     * Create the ASN1 sequence.
     *
     * @param pReqId   the requestId
     * @param pFailure the failure Code
     */
    private GordianCertGatewayResponseASN1(final int pReqId,
                                           final GordianCertFailure pFailure) {
        /* Store the Details */
        theReqId = pReqId;
        theRespId = -1;
        theStatus = GordianCertGatewayStatusASN1.createCertStatus(pFailure);
        theCertificate = null;
        theEncrypted = null;
        theSignerCerts = null;
    }

    /**
     * Create the ASN1 sequence.
     *
     * @param pReqId       the requestId
     * @param pRespId      the responseId
     * @param pStatus      the status
     * @param pCertificate the certificate
     * @param pSignerCerts the signerCertificates
     */
    private GordianCertGatewayResponseASN1(final int pReqId,
                                           final int pRespId,
                                           final GordianCertStatus pStatus,
                                           final Object pCertificate,
                                           final Certificate[] pSignerCerts) {
        /* Store the Details */
        theReqId = pReqId;
        theRespId = pRespId;
        theStatus = GordianCertGatewayStatusASN1.createCertStatus(pStatus);
        theCertificate = pCertificate instanceof Certificate myCert ? myCert : null;
        theEncrypted = pCertificate instanceof EnvelopedData myEnveloped ? myEnveloped : null;
        theSignerCerts = pSignerCerts.clone();
    }

    /**
     * Constructor.
     *
     * @param pSequence the Sequence
     * @throws GordianException on error
     */
    private GordianCertGatewayResponseASN1(final ASN1Sequence pSequence) throws GordianException {
        /* Protect against exceptions */
        try {
            /* Extract the request/responseIds from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theReqId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            theRespId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            theStatus = GordianCertGatewayStatusASN1.getInstance(en.nextElement());

            /* Extract the certificate from the sequence */
            if (en.hasMoreElements()) {
                final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
                switch (myTagged.getTagNo()) {
                    case TAG_STANDARD:
                        theCertificate = Certificate.getInstance(myTagged, false);
                        theEncrypted = null;
                        break;
                    case TAG_ENCRYPTED:
                        theEncrypted = EnvelopedData.getInstance(myTagged, false);
                        theCertificate = null;
                        break;
                    default:
                        throw new GordianDataException("Unexpected tag");
                }
            }

            /* Extract the signer Certificates from the sequence */
            if (en.hasMoreElements()) {
                final ASN1Sequence mySignerCerts = ASN1Sequence.getInstance(en.nextElement());
                final Enumeration<?> enCert = mySignerCerts.getObjects();
                final int myNumCerts = mySignerCerts.size();
                theSignerCerts = new Certificate[mySignerCerts.size()];
                for (int i = 0; i < myNumCerts; i++) {
                    theSignerCerts[i] = Certificate.getInstance(enCert.nextElement());
                }
            }

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     *
     * @param pObject the object to parse
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCertGatewayResponseASN1 getInstance(final Object pObject) throws GordianException {
        if (pObject instanceof GordianCertGatewayResponseASN1 myASN1) {
            return myASN1;
        } else if (pObject != null) {
            return new GordianCertGatewayResponseASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Parse the ASN1 Tagged object.
     *
     * @param pObject   the object to parse
     * @param pExplicit is the tag explicit?
     * @return the parsed object
     * @throws GordianException on error
     */
    public static GordianCertGatewayResponseASN1 getInstance(final ASN1TaggedObject pObject,
                                                             final boolean pExplicit) throws GordianException {
        return getInstance(ASN1Sequence.getInstance(pObject, pExplicit));
    }

    /**
     * Create the certificate response.
     *
     * @param pReqId   the request id
     * @param pFailure the failure code
     * @return the response
     */
    public static GordianCertGatewayResponseASN1 createResponse(final int pReqId,
                                                                final GordianCertFailure pFailure) {
        /* Create the response */
        return new GordianCertGatewayResponseASN1(pReqId, pFailure);
    }

    /**
     * Create the certificate response.
     *
     * @param pReqId       the request id
     * @param pRespId      the responseId
     * @param pStatus      the status
     * @param pCertificate the certificate
     * @param pChain       the certificate chain
     * @return the response
     */
    public static GordianCertGatewayResponseASN1 createResponse(final int pReqId,
                                                                final int pRespId,
                                                                final GordianCertStatus pStatus,
                                                                final Object pCertificate,
                                                                final List<Certificate> pChain) {
        /* Convert the chain to an array */
        final Certificate[] myChain = new Certificate[pChain.size()];
        pChain.toArray(myChain);

        /* Return the ASN1 */
        return new GordianCertGatewayResponseASN1(pReqId, pRespId, pStatus, pCertificate, myChain);
    }

    /**
     * Create the certificate response.
     *
     * @param pReqId     the request id
     * @param pRespId    the responseId
     * @param pStatus    the status
     * @param pEnveloped the encrypted certificate
     * @param pChain     the certificate chain
     * @return the response
     */
    public static GordianCertGatewayResponseASN1 createResponse(final int pReqId,
                                                                final int pRespId,
                                                                final GordianCertStatus pStatus,
                                                                final EnvelopedData pEnveloped,
                                                                final List<Certificate> pChain) {
        /* Convert the chain to an array */
        final Certificate[] myChain = new Certificate[pChain.size()];
        pChain.toArray(myChain);

        /* Return the ASN1 */
        return new GordianCertGatewayResponseASN1(pReqId, pRespId, pStatus, pEnveloped, myChain);
    }

    @Override
    public int getRequestId() {
        return theReqId;
    }

    @Override
    public int getResponseId() {
        return theRespId;
    }

    @Override
    public GordianCertGatewayStatusASN1 getStatus() {
        return theStatus;
    }

    /**
     * Obtain the encrypted certificate.
     *
     * @return the encrypted certificate
     */
    public EnvelopedData getEncrypted() {
        return theEncrypted;
    }

    /**
     * Obtain the certificate.
     *
     * @return the certificate
     */
    public Certificate getCertificate() {
        return theCertificate;
    }

    /**
     * Obtain the certificate chain.
     *
     * @return the chain
     */
    public List<Certificate> getTheCertificateChain() {
        return List.of(theSignerCerts);
    }

    /**
     * Is the response encrypted?
     *
     * @return true/false
     */
    public boolean isEncrypted() {
        return theEncrypted != null;
    }

    /**
     * Obtain the certificate.
     *
     * @param pEncryptor the encryptor
     * @return the certificate
     * @throws GordianException on error
     */
    public GordianCoreCertificate getCertificate(final GordianCertGatewayEncryptor pEncryptor) throws GordianException {
        /* If the certificate is still encrypted */
        if (isEncrypted()) {
            throw new GordianLogicException("Certificate still encrypted");
        }

        /* Convert and return the certificate */
        return pEncryptor.convertCertificate(theCertificate);
    }

    /**
     * Obtain the certificate chain.
     *
     * @param pEncryptor the encryptor
     * @return the chain
     * @throws GordianException on error
     */
    public GordianCoreCertificate[] getCertificateChain(final GordianCertGatewayEncryptor pEncryptor) throws GordianException {
        /* If the certificate is still encrypted */
        if (theSignerCerts == null) {
            throw new GordianLogicException("Certificate chain not available");
        }

        /* If the certificate is still encrypted */
        if (isEncrypted()) {
            throw new GordianLogicException("Certificate still encrypted");
        }

        /* Create the chain */
        final GordianCoreCertificate[] myChain = new GordianCoreCertificate[theSignerCerts.length + 1];

        /* Store first element in chain */
        myChain[0] = pEncryptor.convertCertificate(theCertificate);

        /* Store subsequent details */
        for (int i = 0; i < theSignerCerts.length; i++) {
            myChain[i + 1] = pEncryptor.convertCertificate(theSignerCerts[i]);
        }

        /* Return the chain */
        return myChain;
    }

    @Override
    public ASN1Primitive toASN1Primitive() {
        final ASN1EncodableVector v = new ASN1EncodableVector();
        v.add(new ASN1Integer(theReqId));
        v.add(new ASN1Integer(theRespId));
        v.add(theStatus);
        if (theEncrypted != null) {
            v.add(new DERTaggedObject(false, TAG_ENCRYPTED, theEncrypted));
        } else if (theCertificate != null) {
            v.add(new DERTaggedObject(false, TAG_STANDARD, theCertificate));
        }
        if (theSignerCerts != null) {
            v.add(new DERSequence(theSignerCerts));
        }

        return new DERSequence(v);
    }

    /**
     * Encrypt certificate.
     *
     * @param pEncryptor the encryptor
     * @throws GordianException on error
     */
    public void encryptCertificate(final GordianCertGatewayEncryptor pEncryptor) throws GordianException {
        /* Only encrypt if not currently encrypted */
        if (theEncrypted == null) {
            /* Prepare for encryption */
            final GordianCoreCertificate myCert = pEncryptor.convertCertificate(theCertificate);
            final GordianCertGatewayResult myResult = pEncryptor.prepareForEncryption(myCert);

            /* Create the encrypted content */
            final EncryptedContentInfo myInfo = GordianCertGatewayEncryptor.buildEncryptedContentInfo(myResult.keySet(), myCert);
            theEncrypted = new EnvelopedData(null, new BERSet(myResult.recipient()), myInfo, (BERSet) null);
            theCertificate = null;
        }
    }

    /**
     * Decrypt certificate.
     *
     * @param pEncryptor the encryptor
     * @param pKeyPair   the keyPair
     * @throws GordianException on error
     */
    public void decryptCertificate(final GordianCertGatewayEncryptor pEncryptor,
                                   final GordianKeyStorePair pKeyPair) throws GordianException {
        /* Only decrypt if currently encrypted */
        if (theCertificate == null) {
            /* If the certificate is still encrypted */
            if (theEncrypted == null) {
                throw new GordianLogicException("No Certificate available");
            }

            /* Derive the KeySet */
            final RecipientInfo myRecipient = RecipientInfo.getInstance(theEncrypted.getRecipientInfos().getObjectAt(0));
            final KeyTransRecipientInfo myRecInfo = (KeyTransRecipientInfo) myRecipient.getInfo();
            final GordianKeySet myKeySet = pEncryptor.deriveKeySetFromRecInfo(myRecInfo,
                    pKeyPair.getCertificateChain().getFirst(), pKeyPair.getKeyPair());

            /* Decrypt the certificate */
            final EncryptedContentInfo myInfo = theEncrypted.getEncryptedContentInfo();
            final byte[] myEncoded = myKeySet.decryptBytes(myInfo.getEncryptedContent().getOctets());
            theCertificate = Certificate.getInstance(myEncoded);
            theEncrypted = null;
        }
    }
}
