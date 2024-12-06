/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2024 Tony Washer
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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
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
import org.bouncycastle.asn1.crmf.PKMACValue;
import org.bouncycastle.asn1.x509.Certificate;

import net.sourceforge.joceanus.gordianknot.api.keyset.GordianKeySet;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianCertificate;
import net.sourceforge.joceanus.gordianknot.api.keystore.GordianKeyStoreEntry.GordianKeyStorePair;
import net.sourceforge.joceanus.gordianknot.impl.core.base.GordianASN1Util.GordianASN1Object;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianDataException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianIOException;
import net.sourceforge.joceanus.gordianknot.impl.core.exc.GordianLogicException;
import net.sourceforge.joceanus.gordianknot.impl.core.keystore.GordianCRMEncryptor.GordianCRMResult;
import net.sourceforge.joceanus.oceanus.OceanusException;

/**
 * ASN1 Encoding of CertificateResponse.
 * <pre>
 * GordianCertResponseASN1 ::= SEQUENCE {
 *      certReqId    INTEGER
 *      certRespId   INTEGER
 *      CHOICE {
 *          certificate     [1] Certificate,
 *          encrypted       [2] EnvelopedData
 *      }
 *      signerCerts  SEQUENCE SIZE (1..MAX) OF Certificate
 *      macValue  PKMACValue OPTIONAL
 * }
 * </pre>
 */
public final class GordianCertResponseASN1
        extends GordianASN1Object {
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
     * The signer certificates.
     */
    private final Certificate[] theSignerCerts;

    /**
     * The certificate.
     */
    private Certificate theCertificate;

    /**
     * The encoded certificate.
     */
    private EnvelopedData theEncrypted;

    /**
     * The macValue.
     */
    private PKMACValue theMACValue;

    /**
     * Create the ASN1 sequence.
     * @param pReqId the requestId
     * @param pRespId the responseId
     * @param pCertificate the certificate
     * @param pSignerCerts the signerCertificates
     */
    private GordianCertResponseASN1(final int pReqId,
                                    final int pRespId,
                                    final Certificate pCertificate,
                                    final Certificate[] pSignerCerts) {
        /* Store the Details */
        theReqId = pReqId;
        theRespId = pRespId;
        theCertificate = pCertificate;
        theSignerCerts = pSignerCerts.clone();
    }

    /**
     * Constructor.
     * @param pSequence the Sequence
     * @throws OceanusException on error
     */
    private GordianCertResponseASN1(final ASN1Sequence pSequence) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Extract the request/responseIds from the sequence */
            final Enumeration<?> en = pSequence.getObjects();
            theReqId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();
            theRespId = ASN1Integer.getInstance(en.nextElement()).getValue().intValue();

            /* Extract the certificate from the sequence */
            final ASN1TaggedObject myTagged = ASN1TaggedObject.getInstance(en.nextElement());
            switch (myTagged.getTagNo()) {
                case TAG_STANDARD:
                    theCertificate = Certificate.getInstance(myTagged, false);
                    break;
                case TAG_ENCRYPTED:
                    theEncrypted = EnvelopedData.getInstance(myTagged, false);
                    break;
                default:
                    throw new GordianDataException("Unexpected tag");
            }

            /* Extract the signer Certificates from the sequence */
            final ASN1Sequence mySignerCerts = ASN1Sequence.getInstance(en.nextElement());
            final Enumeration<?> enCert = mySignerCerts.getObjects();
            final int myNumCerts = mySignerCerts.size();
            theSignerCerts = new Certificate[mySignerCerts.size()];
            for (int i = 0; i < myNumCerts; i++) {
                theSignerCerts[i] = Certificate.getInstance(enCert.nextElement());
            }

            /* Extract the Optional MACValue from the sequence */
            if (en.hasMoreElements()) {
                theMACValue = PKMACValue.getInstance(en.nextElement());
            }

            /* handle exceptions */
        } catch (IllegalArgumentException e) {
            throw new GordianIOException("Unable to parse ASN1 sequence", e);
        }
    }

    /**
     * Parse the ASN1 object.
     * @param pObject the object to parse
     * @return the parsed object
     * @throws OceanusException on error
     */
    public static GordianCertResponseASN1 getInstance(final Object pObject) throws OceanusException {
        if (pObject instanceof GordianCertResponseASN1) {
            return (GordianCertResponseASN1) pObject;
        } else if (pObject != null) {
            return new GordianCertResponseASN1(ASN1Sequence.getInstance(pObject));
        }
        throw new GordianDataException("Null sequence");
    }

    /**
     * Create the certificate response.
     * @param pReqId the request id
     * @param pRespId the responseId
     * @param pChain the certificate chain
     * @return the response
     */
    public static GordianCertResponseASN1 createCertResponse(final int pReqId,
                                                             final int pRespId,
                                                             final List<GordianCertificate> pChain) {
        /* Create the chain */
        final Certificate[] myChain = new Certificate[pChain.size() - 1];

        /* Store first element in chain */
        final Iterator<GordianCertificate> myIterator = pChain.iterator();
        final Certificate myCert = ((GordianCoreCertificate) myIterator.next()).getCertificate();

        /* Store subsequent details */
        for (int i = 1; i < pChain.size(); i++) {
            myChain[i - 1] = ((GordianCoreCertificate) myIterator.next()).getCertificate();
        }

        /* Return the chain */
        return new GordianCertResponseASN1(pReqId, pRespId, myCert, myChain);
    }

    /**
     * Obtain the requestId.
     * @return the id
     */
    public int getReqId() {
        return theReqId;
    }

    /**
     * Obtain the responseId.
     * @return the id
     */
    public int getRespId() {
        return theRespId;
    }

    /**
     * Obtain the macValue.
     * @return the macValue
     */
    public PKMACValue getMACValue() {
        return theMACValue;
    }

    /**
     * Set the macValue.
     * @param pMACValue the macValue
     */
    public void setMACValue(final PKMACValue pMACValue) {
        theMACValue = pMACValue;
    }

    /**
     * Obtain the macData.
     * @return the macData
     */
    public ASN1Object getMACData() {
        return theCertificate;
    }

    /**
     * Is the response encrypted?
     * @return true/false
     */
    public boolean isEncrypted() {
        return theEncrypted != null;
    }

    /**
     * Obtain the certificate.
     * @param pEncryptor the encryptor
     * @return the certificate
     * @throws OceanusException on error
     */
    public GordianCoreCertificate getCertificate(final GordianCRMEncryptor pEncryptor) throws OceanusException {
        /* If the certificate is still encrypted */
        if (isEncrypted()) {
            throw new GordianLogicException("Certificate still encrypted");
        }

        /* Convert and return the certificate */
        return pEncryptor.convertCertificate(theCertificate);
    }

    /**
     * Obtain the certificate chain.
     * @param pEncryptor the encryptor
     * @return the chain
     * @throws OceanusException on error
     */
    public GordianCoreCertificate[] getCertificateChain(final GordianCRMEncryptor pEncryptor) throws OceanusException {
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
        if (theCertificate != null) {
            v.add(new DERTaggedObject(false, TAG_STANDARD, theCertificate));
        } else if (theEncrypted != null) {
            v.add(new DERTaggedObject(false, TAG_ENCRYPTED, theEncrypted));
        }
        v.add(new DERSequence(theSignerCerts));
        if (theMACValue != null) {
            v.add(theMACValue);
        }

        return new DERSequence(v);
    }

    /**
     * Encrypt certificate.
     * @param pEncryptor the encryptor
     * @throws OceanusException on error
     */
    public void encryptCertificate(final GordianCRMEncryptor pEncryptor) throws OceanusException {
        /* Only encrypt if not currently encrypted */
        if (theEncrypted == null) {
            /* Prepare for encryption */
            final GordianCoreCertificate myCert = pEncryptor.convertCertificate(theCertificate);
            final GordianCRMResult myResult = pEncryptor.prepareForEncryption(myCert);

            /* Create the encrypted content */
            final EncryptedContentInfo myInfo = GordianCRMEncryptor.buildEncryptedContentInfo(myResult.getKeySet(), myCert);
            theEncrypted = new EnvelopedData(null, new BERSet(myResult.getRecipient()), myInfo, (BERSet) null);
            theCertificate = null;
        }
    }

    /**
     * Decrypt certificate.
     * @param pEncryptor the encryptor
     * @param pKeyPair the keyPair
     * @throws OceanusException on error
     */
    public void decryptCertificate(final GordianCRMEncryptor pEncryptor,
                                   final GordianKeyStorePair pKeyPair) throws OceanusException {
        /* Only decrypt if currently encrypted */
        if (theCertificate == null) {
            /* Derive the KeySet */
            final RecipientInfo myRecipient = RecipientInfo.getInstance(theEncrypted.getRecipientInfos().getObjectAt(0));
            final KeyTransRecipientInfo myRecInfo = (KeyTransRecipientInfo) myRecipient.getInfo();
            final GordianKeySet myKeySet = pEncryptor.deriveKeySetFromRecInfo(myRecInfo, pKeyPair.getCertificateChain().get(0), pKeyPair.getKeyPair());

            /* Decrypt the certificate */
            final EncryptedContentInfo myInfo = theEncrypted.getEncryptedContentInfo();
            final byte[] myEncoded = myKeySet.decryptBytes(myInfo.getEncryptedContent().getOctets());
            theCertificate = Certificate.getInstance(myEncoded);
            theEncrypted = null;
        }
    }
}
